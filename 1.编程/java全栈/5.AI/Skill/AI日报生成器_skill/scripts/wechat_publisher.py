#!/usr/bin/env python3
"""
微信公众号发布工具
支持：上传永久素材、创建草稿、获取access_token

用法：
  python wechat_publisher.py upload_image <图片路径>    # 上传图片素材，返回media_id
  python wechat_publisher.py create_draft <title> <content_html> <thumb_media_id>  # 创建草稿
  python wechat_publisher.py get_token                    # 获取并打印access_token
"""

import os
import sys
import json
from coze_workload_identity import requests


class WeChatPublisher:
    """微信公众号发布器"""

    def __init__(self):
        # 从环境变量读取凭证（skill凭证系统自动注入）
        # 环境变量名格式: COZE_{CREDENTIAL_NAME}_{SKILL_ID}
        # credential_name = wechat_mp, 全部大写后为 WECHAT_MP
        self.access_token = os.getenv("COZE_WECHAT_MP_7652588211117490186")
        if not self.access_token:
            # 备选：检查是否有本地配置的token文件
            token_file = os.path.join(os.path.dirname(__file__), '..', 'access_token.txt')
            if os.path.exists(token_file):
                with open(token_file, 'r') as f:
                    self.access_token = f.read().strip()
        
        if not self.access_token:
            raise ValueError("未找到微信公众号凭证，请先配置wechat_mp凭证")
        
        self.base_url = "https://api.weixin.qq.com/cgi-bin"

    def _get(self, path, params=None):
        """GET请求"""
        if params is None:
            params = {}
        params['access_token'] = self.access_token
        url = f"{self.base_url}/{path}"
        response = requests.get(url, params=params, timeout=30)
        data = response.json()
        if data.get('errcode', 0) != 0:
            raise Exception(f"微信API错误: {data.get('errcode')} - {data.get('errmsg')}")
        return data

    def _post(self, path, data=None, files=None):
        """POST请求"""
        url = f"{self.base_url}/{path}?access_token={self.access_token}"
        
        if files:
            # 表单上传
            response = requests.post(url, files=files, timeout=60)
        else:
            # JSON提交
            headers = {'Content-Type': 'application/json; charset=utf-8'}
            json_str = json.dumps(data, ensure_ascii=False).encode('utf-8')
            response = requests.post(url, data=json_str, headers=headers, timeout=30)
        
        result = response.json()
        if result.get('errcode', 0) != 0:
            raise Exception(f"微信API错误: {result.get('errcode')} - {result.get('errmsg')}")
        return result

    def upload_image(self, image_path):
        """上传图片到永久素材库
        返回: media_id
        """
        if not os.path.exists(image_path):
            raise FileNotFoundError(f"图片不存在: {image_path}")
        
        # 检查图片大小（微信限制：图片<10MB）
        file_size = os.path.getsize(image_path)
        if file_size > 10 * 1024 * 1024:
            raise ValueError(f"图片过大（{file_size/1024/1024:.1f}MB），微信限制10MB以内")
        
        with open(image_path, 'rb') as f:
            files = {'media': (os.path.basename(image_path), f, 'image/jpeg')}
            result = self._post('material/add_material', files=files)
        
        return result.get('media_id')

    def create_draft(self, title, content_html, thumb_media_id, author="AI日报", digest=""):
        """创建图文草稿
        返回: media_id（草稿ID）
        """
        articles = [{
            "title": title,
            "author": author,
            "digest": digest,
            "content": content_html,
            "thumb_media_id": thumb_media_id,
            "need_open_comment": 1,
            "only_fans_can_comment": 0
        }]
        
        data = {"articles": articles}
        result = self._post('draft/add', data=data)
        
        return result.get('media_id')

    def get_draft_list(self, offset=0, count=10):
        """获取草稿列表"""
        data = {
            "offset": offset,
            "count": count,
            "no_content": 1
        }
        return self._post('draft/batchget', data=data)


def main():
    if len(sys.argv) < 2:
        print(__doc__)
        sys.exit(1)

    command = sys.argv[1]
    publisher = WeChatPublisher()

    if command == 'upload_image':
        if len(sys.argv) < 3:
            print("用法: python wechat_publisher.py upload_image <图片路径>")
            sys.exit(1)
        media_id = publisher.upload_image(sys.argv[2])
        print(f"图片上传成功，media_id: {media_id}")
        print(media_id)  # 输出纯ID供脚本调用

    elif command == 'create_draft':
        if len(sys.argv) < 5:
            print("用法: python wechat_publisher.py create_draft <标题> <HTML内容文件> <封面media_id>")
            sys.exit(1)
        title = sys.argv[2]
        html_file = sys.argv[3]
        thumb_media_id = sys.argv[4]
        
        with open(html_file, 'r', encoding='utf-8') as f:
            content_html = f.read()
        
        # 提取摘要（前120字）
        import re
        plain_text = re.sub(r'<[^>]+>', '', content_html)
        digest = plain_text[:120]
        
        draft_id = publisher.create_draft(title, content_html, thumb_media_id, digest=digest)
        print(f"草稿创建成功，media_id: {draft_id}")
        print(draft_id)

    elif command == 'get_token':
        print(f"access_token: {publisher.access_token[:20]}...")

    elif command == 'list':
        result = publisher.get_draft_list()
        print(json.dumps(result, ensure_ascii=False, indent=2))

    else:
        print(f"未知命令: {command}")
        print(__doc__)
        sys.exit(1)


if __name__ == '__main__':
    main()
