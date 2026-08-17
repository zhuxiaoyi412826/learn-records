#!/usr/bin/env python3
"""
Markdown转公众号HTML工具
按照公众号排版规范将Markdown文章转换为HTML格式

用法：
  python md_to_html.py convert <输入md文件> <输出html文件>
  python md_to_html.py preview <输入md文件>  # 输出HTML到控制台
"""

import re
import sys
import os


def md_to_wechat_html(md_text):
    """将Markdown转换为公众号HTML格式"""
    
    lines = md_text.split('\n')
    html_parts = []
    in_list = False
    list_type = None
    
    for line in lines:
        line = line.rstrip()
        
        # 空行 - 段落间距
        if not line:
            if in_list:
                html_parts.append('</ul>' if list_type == 'ul' else '</ol>')
                in_list = False
                list_type = None
            html_parts.append('<p style="height:4px;"></p>')
            continue
        
        # 一级标题（文章主标题）
        if line.startswith('# '):
            title = line[2:].strip()
            html_parts.append(f'<h1 style="color:#2563EB; font-size:22px; font-weight:bold; text-align:center; margin:20px 0;">{inline_format(title)}</h1>')
            continue
        
        # 二级标题（小节标题）
        if line.startswith('## '):
            title = line[3:].strip()
            html_parts.append(f'<h2 style="color:#2563EB; font-size:18px; font-weight:bold; margin-top:30px; margin-bottom:10px;">{inline_format(title)}</h2>')
            continue
        
        # 三级标题
        if line.startswith('### '):
            title = line[4:].strip()
            html_parts.append(f'<h3 style="color:#333; font-size:16px; font-weight:bold; margin-top:20px; margin-bottom:8px;">{inline_format(title)}</h3>')
            continue
        
        # 引用块
        if line.startswith('> '):
            quote_text = line[2:].strip()
            html_parts.append(f'<blockquote style="border-left:3px solid #2563EB; padding-left:15px; color:#666; font-size:14px; line-height:1.8; margin:10px 0;">{inline_format(quote_text)}</blockquote>')
            continue
        
        # 无序列表
        if line.startswith('- ') or line.startswith('* '):
            item_text = line[2:].strip()
            if not in_list or list_type != 'ul':
                if in_list:
                    html_parts.append('</ul>' if list_type == 'ul' else '</ol>')
                html_parts.append('<ul style="margin:10px 0; padding-left:20px;">')
                in_list = True
                list_type = 'ul'
            html_parts.append(f'<li style="color:#333; font-size:16px; line-height:2.0; margin:5px 0; text-indent:0;">{inline_format(item_text)}</li>')
            continue
        
        # 有序列表
        if re.match(r'^\d+\. ', line):
            item_text = re.sub(r'^\d+\. ', '', line).strip()
            if not in_list or list_type != 'ol':
                if in_list:
                    html_parts.append('</ul>' if list_type == 'ul' else '</ol>')
                html_parts.append('<ol style="margin:10px 0; padding-left:20px;">')
                in_list = True
                list_type = 'ol'
            html_parts.append(f'<li style="color:#333; font-size:16px; line-height:2.0; margin:5px 0; text-indent:0;">{inline_format(item_text)}</li>')
            continue
        
        # 分隔线
        if line == '---' or line == '***':
            html_parts.append('<hr style="border:none; border-top:1px solid #eee; margin:20px 0;">')
            continue
        
        # 代码块（简单处理）
        if line.startswith('```'):
            # 这里简化处理，实际可能需要更复杂的状态管理
            continue
        
        # 普通段落
        if in_list:
            html_parts.append('</ul>' if list_type == 'ul' else '</ol>')
            in_list = False
            list_type = None
        
        # 检查是否是参考资料部分（特殊样式）
        if '参考资料' in line or '来源' in line:
            html_parts.append(f'<p style="color:#999; font-size:13px; line-height:1.8; margin:8px 0; text-indent:0;">{inline_format(line)}</p>')
        else:
            # 正文段落：首行缩进2em，行高2.0
            formatted = inline_format(line)
            html_parts.append(f'<p style="color:#333; font-size:16px; line-height:2.0; text-indent:2em; margin:6px 0;">{formatted}</p>')
    
    # 结束未闭合的列表
    if in_list:
        html_parts.append('</ul>' if list_type == 'ul' else '</ol>')
    
    # 组装完整HTML
    html = '\n'.join(html_parts)
    
    # 添加整体包装
    full_html = f'''<div style="max-width:100%; background:#fff; padding:10px; font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,sans-serif;">
{html}
</div>'''
    
    return full_html


def inline_format(text):
    """处理行内格式：加粗、链接、代码等"""
    
    # 加粗 **text**
    text = re.sub(r'\*\*(.+?)\*\*', r'<strong style="color:#333;">\1</strong>', text)
    
    # 斜体 *text*
    text = re.sub(r'(?<!\*)\*([^*\n]+)\*(?!\*)', r'<em>\1</em>', text)
    
    # 行内代码 `code`
    text = re.sub(r'`(.+?)`', r'<code style="background:#f5f5f5; padding:2px 5px; border-radius:3px; font-size:14px; color:#c7254e;">\1</code>', text)
    
    # 链接 [text](url)
    text = re.sub(r'\[(.+?)\]\((.+?)\)', r'<a href="\2" style="color:#2563EB; text-decoration:none;">\1</a>', text)
    
    return text


def add_cta(html_text, cta_text="看完了？欢迎关注我，一起探讨AI的无限可能～"):
    """在结尾添加关注引导CTA"""
    cta_html = f'''<p style="height:20px;"></p>
<p style="color:#2563EB; font-size:15px; font-weight:bold; text-align:center; line-height:1.8; text-indent:0;">{cta_text}</p>
<p style="height:10px;"></p>'''
    return html_text + cta_html


def main():
    if len(sys.argv) < 3:
        print(__doc__)
        sys.exit(1)

    command = sys.argv[1]
    input_file = sys.argv[2]

    if not os.path.exists(input_file):
        print(f"文件不存在: {input_file}")
        sys.exit(1)

    with open(input_file, 'r', encoding='utf-8') as f:
        md_text = f.read()

    html = md_to_wechat_html(md_text)
    html_with_cta = add_cta(html)

    if command == 'convert':
        if len(sys.argv) < 4:
            print("用法: python md_to_html.py convert <输入md文件> <输出html文件>")
            sys.exit(1)
        output_file = sys.argv[3]
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write(html_with_cta)
        print(f"转换完成，已保存到: {output_file}")

    elif command == 'preview':
        print(html_with_cta)

    else:
        print(f"未知命令: {command}")
        print(__doc__)
        sys.exit(1)


if __name__ == '__main__':
    main()
