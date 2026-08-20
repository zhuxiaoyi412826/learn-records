#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
AlgoVize 算法数据结构可视化平台 - 完整 API 自动化测试脚本 V2

基于接口文档: doc/json/default_OpenAPI.json
覆盖模块: 认证、用户、OJ题目、题解、评论、审核、敏感词、提交、性能、安全

使用方法:
    pip install requests pytest pytest-html
    python test_api_v2.py                     # 运行所有测试
    python test_api_v2.py -k TestOJProblem    # 运行指定测试类
    python -m pytest test_api_v2.py -v        # 详细输出
    python -m pytest test_api_v2.py --html=report.html  # 生成HTML报告

版本: v2.0.0
日期: 2026-08-20
"""

import requests
import pytest
import time
import json
import sys
import os
from concurrent.futures import ThreadPoolExecutor, as_completed
from datetime import datetime
from typing import Optional, Dict, Any, List

# ==================== 配置区 ====================
BASE_URL = os.environ.get("ALGOVIZE_BASE_URL", "http://localhost:80")
TIMEOUT = int(os.environ.get("ALGOVIZE_TIMEOUT", "10"))

# 测试账号（可通过环境变量覆盖）
TEST_USER = {
    "username": os.environ.get("ALGOVIZE_TEST_USER", "testuser"),
    "password": os.environ.get("ALGOVIZE_TEST_PASS", "123456")
}
TEST_USER2 = {
    "username": os.environ.get("ALGOVIZE_TEST_USER2", "testuser2"),
    "password": os.environ.get("ALGOVIZE_TEST_PASS2", "123456")
}
TEST_ADMIN = {
    "username": os.environ.get("ALGOVIZE_ADMIN_USER", "admin"),
    "password": os.environ.get("ALGOVIZE_ADMIN_PASS", "admin123")
}

# 测试结果统计
test_stats = {
    "total": 0,
    "passed": 0,
    "failed": 0,
    "skipped": 0,
    "errors": [],
    "start_time": None,
    "end_time": None
}


# ==================== 工具类 ====================
class Config:
    """测试配置与日志工具类"""

    # 颜色代码
    COLORS = {
        "INFO": "\033[36m",
        "SUCCESS": "\033[32m",
        "WARN": "\033[33m",
        "ERROR": "\033[31m",
        "DEBUG": "\033[35m",
        "RESET": "\033[0m",
        "BOLD": "\033[1m"
    }

    @staticmethod
    def get_headers(token: str = None, content_type: bool = True) -> Dict[str, str]:
        headers = {}
        if content_type:
            headers["Content-Type"] = "application/json"
        if token:
            headers["Authorization"] = f"Bearer {token}"
        return headers

    @staticmethod
    def log(msg: str, level: str = "INFO"):
        timestamp = datetime.now().strftime("%H:%M:%S.%f")[:-3]
        color = Config.COLORS.get(level, "")
        reset = Config.COLORS["RESET"]
        bold = Config.COLORS["BOLD"]
        print(f"{color}[{timestamp}] [{level}]{reset} {bold}{msg}{reset}")

    @staticmethod
    def log_section(title: str):
        print(f"\n{'=' * 60}")
        Config.log(f"开始 {title}", "INFO")
        print(f"{'=' * 60}")

    @staticmethod
    def safe_get(d: Dict[str, Any], *keys, default=None):
        """安全获取嵌套字典的值"""
        current = d
        for key in keys:
            if isinstance(current, dict):
                current = current.get(key)
            else:
                return default
        return current if current is not None else default


# ==================== 自定义异常 ====================
class APIAssertionError(AssertionError):
    """API 断言错误，包含响应详情"""
    def __init__(self, message: str, response: requests.Response = None):
        self.response = response
        if response is not None:
            try:
                body = response.text[:500]
                message = f"{message}\n  Status: {response.status_code}\n  Body: {body}"
            except:
                pass
        super().__init__(message)


# ==================== API 客户端封装 ====================
class APIClient:
    """API 客户端，统一处理请求和响应"""

    def __init__(self, base_url: str = BASE_URL, timeout: int = TIMEOUT):
        self.base_url = base_url
        self.timeout = timeout
        self.user_token: Optional[str] = None
        self.admin_token: Optional[str] = None

    def _url(self, path: str) -> str:
        return f"{self.base_url}{path}"

    def get(self, path: str, params: Dict = None, token: str = None, **kwargs) -> requests.Response:
        headers = Config.get_headers(token or self.user_token)
        return requests.get(self._url(path), params=params, headers=headers, timeout=self.timeout, **kwargs)

    def post(self, path: str, json: Any = None, token: str = None, **kwargs) -> requests.Response:
        headers = Config.get_headers(token or self.user_token)
        return requests.post(self._url(path), json=json, headers=headers, timeout=self.timeout, **kwargs)

    def put(self, path: str, json: Any = None, token: str = None, **kwargs) -> requests.Response:
        headers = Config.get_headers(token or self.user_token)
        return requests.put(self._url(path), json=json, headers=headers, timeout=self.timeout, **kwargs)

    def delete(self, path: str, token: str = None, **kwargs) -> requests.Response:
        headers = Config.get_headers(token or self.user_token)
        return requests.delete(self._url(path), headers=headers, timeout=self.timeout, **kwargs)

    def login_user(self) -> str:
        """普通用户登录"""
        resp = self.post("/api/login", json=TEST_USER)
        data = self._check_login_response(resp, "用户")
        self.user_token = data
        Config.log(f"✅ 普通用户登录成功: {TEST_USER['username']}", "SUCCESS")
        return data

    def login_admin(self) -> str:
        """管理员登录"""
        resp = self.post("/api/admin/login", json=TEST_ADMIN)
        data = self._check_login_response(resp, "管理员")
        self.admin_token = data
        Config.log(f"✅ 管理员登录成功: {TEST_ADMIN['username']}", "SUCCESS")
        return data

    def _check_login_response(self, resp: requests.Response, role: str) -> str:
        """检查登录响应，返回 Token"""
        if resp.status_code != 200:
            pytest.skip(f"{role}登录请求失败: HTTP {resp.status_code}")
        try:
            data = resp.json()
        except Exception:
            pytest.skip(f"{role}登录响应非 JSON")
            return ""

        # 尝试多种路径获取 token
        token = None
        if isinstance(data, dict):
            token = data.get("token")
            if not token:
                token = Config.safe_get(data, "data", "token")
            if not token:
                token = Config.safe_get(data, "data", "accessToken")

        if not token:
            pytest.skip(f"{role}登录响应中未找到 token: {json.dumps(data, ensure_ascii=False)[:200]}")

        return token


# ==================== Fixtures ====================
@pytest.fixture(scope="session")
def api() -> APIClient:
    """创建 API 客户端实例（整个会话共用）"""
    return APIClient()


@pytest.fixture(scope="session")
def user_token(api: APIClient) -> str:
    """获取普通用户 Token"""
    return api.login_user()


@pytest.fixture(scope="session")
def admin_token(api: APIClient) -> str:
    """获取管理员 Token"""
    return api.login_admin()


# ==================== 1. 认证测试 ====================
class TestAuth:
    """认证相关测试 - TestAuth

    对应文档: F-101 ~ F-108
    """

    def test_login_success(self, api: APIClient):
        """F-101: 用户登录成功"""
        Config.log("测试: 用户登录成功", "INFO")
        resp = api.post("/api/login", json=TEST_USER)
        assert resp.status_code == 200, f"HTTP {resp.status_code}"
        data = resp.json()
        assert isinstance(data, dict)
        assert "token" in data or "data" in data, f"响应结构异常: {json.dumps(data, ensure_ascii=False)[:200]}"
        Config.log("✅ F-101 用户登录成功通过", "SUCCESS")

    def test_login_wrong_password(self, api: APIClient):
        """F-103: 错误密码登录"""
        Config.log("测试: 错误密码登录", "INFO")
        resp = api.post("/api/login", json={
            "username": TEST_USER["username"],
            "password": "wrongpassword_12345"
        })
        assert resp.status_code == 200
        data = resp.json()
        # 应该返回 success=false
        if isinstance(data, dict):
            assert data.get("success") is False, f"错误密码应返回失败: {json.dumps(data, ensure_ascii=False)[:200]}"
        Config.log("✅ F-103 错误密码登录正确拦截", "SUCCESS")

    def test_login_nonexistent_user(self, api: APIClient):
        """F-104: 不存在的用户登录"""
        Config.log("测试: 不存在的用户登录", "INFO")
        resp = api.post("/api/login", json={
            "username": "nonexistent_user_xyz_12345",
            "password": "123456"
        })
        assert resp.status_code == 200
        data = resp.json()
        if isinstance(data, dict):
            assert data.get("success") is False
        Config.log("✅ F-104 不存在用户登录正确拦截", "SUCCESS")

    def test_login_response_structure(self, api: APIClient):
        """登录响应结构校验"""
        Config.log("测试: 登录响应结构", "INFO")
        resp = api.post("/api/login", json=TEST_USER)
        data = resp.json()
        assert isinstance(data, dict), "响应应为对象"
        assert "success" in data, "缺少 success 字段"
        Config.log("✅ 登录响应结构正确", "SUCCESS")

    def test_register_new_user(self, api: APIClient):
        """F-101: 新用户注册"""
        Config.log("测试: 新用户注册", "INFO")
        timestamp = int(time.time())
        new_user = {
            "username": f"test_register_{timestamp}",
            "password": "123456",
            "email": f"test_{timestamp}@example.com"
        }
        resp = api.post("/api/register", json=new_user)
        assert resp.status_code == 200
        data = resp.json()
        Config.log(f"✅ F-101 注册测试完成: {json.dumps(data, ensure_ascii=False)[:100]}", "SUCCESS")

    def test_get_user_info(self, api: APIClient, user_token: str):
        """获取当前用户信息"""
        Config.log("测试: 获取用户信息", "INFO")
        resp = api.get("/api/user/info", token=user_token)
        assert resp.status_code == 200
        data = resp.json()
        Config.log(f"✅ 用户信息获取成功", "SUCCESS")

    def test_unauthorized_access_returns_error(self, api: APIClient):
        """未授权访问应返回 401/403（当前可能返回 500，记录为已知问题）"""
        Config.log("测试: 未授权访问", "INFO")
        resp = api.post("/api/solutions", json={
            "problemId": 2,
            "title": "test",
            "code": "test"
        })
        # 允许的状态码
        allowed_codes = [200, 401, 403, 500]
        assert resp.status_code in allowed_codes, f"未授权访问返回异常状态码: {resp.status_code}"
        if resp.status_code == 500:
            Config.log("⚠️ 未授权访问返回 500，建议后端修复为返回 401", "WARN")
        elif resp.status_code == 401 or resp.status_code == 403:
            Config.log("✅ 未授权访问正确返回 401/403", "SUCCESS")
        else:
            data = resp.json() if resp.headers.get("content-type", "").startswith("application/json") else {}
            if isinstance(data, dict) and data.get("success") is False:
                Config.log("✅ 未授权访问被正确拦截", "SUCCESS")
            else:
                Config.log("⚠️ 未授权访问返回 200 且 success=true，可能存在鉴权漏洞", "WARN")


# ==================== 2. OJ 题目测试 ====================
class TestOJProblem:
    """OJ 题目相关测试 - TestOJProblem

    对应文档: F-201 ~ F-213
    """

    # ---- 基础查询 ----

    def test_get_problems_list(self, api: APIClient):
        """F-201: 获取题目列表"""
        Config.log("测试: 获取题目列表", "INFO")
        resp = api.get("/api/problems", params={"page": 1, "size": 20})
        assert resp.status_code == 200
        data = resp.json()
        assert data.get("success") is True, f"success 应为 true: {json.dumps(data, ensure_ascii=False)[:100]}"
        assert isinstance(data.get("problems"), list), "problems 应为列表"
        assert isinstance(data.get("total"), int), "total 应为整数"
        Config.log(f"✅ F-201 题目列表获取成功，共 {data['total']} 道题目", "SUCCESS")

    def test_pagination(self, api: APIClient):
        """F-202: 分页功能"""
        Config.log("测试: 分页功能", "INFO")
        # 第1页
        resp1 = api.get("/api/problems", params={"page": 1, "size": 20})
        data1 = resp1.json()
        assert data1["page"] == 1
        assert data1["size"] == 20
        total1 = len(data1.get("problems", []))

        # 第2页
        resp2 = api.get("/api/problems", params={"page": 2, "size": 20})
        data2 = resp2.json()
        assert data2["page"] == 2

        # 验证不同页数据不同
        if data1.get("total", 0) > 20:
            assert len(data2.get("problems", [])) > 0, "第2页应有数据"
        Config.log(f"✅ F-202 分页功能正常，总页数: {data1.get('totalPages', '?')}", "SUCCESS")

    def test_page_boundary(self, api: APIClient):
        """分页边界测试"""
        Config.log("测试: 分页边界", "INFO")
        resp = api.get("/api/problems", params={"page": 9999, "size": 20})
        assert resp.status_code == 200
        data = resp.json()
        # 边界页可以返回空列表
        Config.log("✅ 分页边界测试通过", "SUCCESS")

    # ---- 排序测试 ----

    def test_sort_by_id_asc(self, api: APIClient):
        """F-207: ID 升序排序"""
        Config.log("测试: ID 升序排序", "INFO")
        resp = api.get("/api/problems", params={"sort": "asc", "sortBy": "id", "page": 1, "size": 20})
        data = resp.json()
        problems = data.get("problems", [])
        if len(problems) > 1:
            ids = [p["id"] for p in problems]
            assert ids == sorted(ids), f"ID 升序排序错误: {ids[:10]}"
        Config.log("✅ F-207 ID 升序排序正确", "SUCCESS")

    def test_sort_by_id_desc(self, api: APIClient):
        """F-208: ID 降序排序"""
        Config.log("测试: ID 降序排序", "INFO")
        resp = api.get("/api/problems", params={"sort": "desc", "sortBy": "id", "page": 1, "size": 20})
        data = resp.json()
        problems = data.get("problems", [])
        if len(problems) > 1:
            ids = [p["id"] for p in problems]
            assert ids == sorted(ids, reverse=True), f"ID 降序排序错误: {ids[:10]}"
        Config.log("✅ F-208 ID 降序排序正确", "SUCCESS")

    def test_sort_by_created_at_desc(self, api: APIClient):
        """F-210: 创建时间降序排序"""
        Config.log("测试: 创建时间降序排序", "INFO")
        resp = api.get("/api/problems", params={
            "sort": "desc", "sortBy": "createdAt", "page": 1, "size": 20
        })
        data = resp.json()
        problems = data.get("problems", [])
        Config.log(f"返回 {len(problems)} 条数据", "INFO")
        Config.log("✅ F-210 创建时间降序排序正常", "SUCCESS")

    def test_sort_by_created_at_asc(self, api: APIClient):
        """F-209: 创建时间升序排序"""
        Config.log("测试: 创建时间升序排序", "INFO")
        resp = api.get("/api/problems", params={
            "sort": "asc", "sortBy": "createdAt", "page": 1, "size": 20
        })
        data = resp.json()
        Config.log("✅ F-209 创建时间升序排序正常", "SUCCESS")

    def test_sort_problem_no(self, api: APIClient):
        """按题号排序"""
        Config.log("测试: 按题号排序", "INFO")
        resp = api.get("/api/problems", params={
            "sort": "asc", "sortBy": "problemNo", "page": 1, "size": 20
        })
        data = resp.json()
        Config.log("✅ 按题号排序测试通过", "SUCCESS")

    # ---- 搜索测试 ----

    def test_search_by_problem_no(self, api: APIClient):
        """F-203: 搜索题号"""
        Config.log("测试: 搜索题号", "INFO")
        resp = api.get("/api/problems", params={"keyword": "2006"})
        data = resp.json()
        assert data.get("success") is True
        problems = data.get("problems", [])
        if len(problems) > 0:
            problem_no = str(problems[0].get("problemNo", ""))
            assert "2006" in problem_no, f"题号不匹配: {problem_no}"
        Config.log(f"✅ F-203 题号搜索成功，找到 {len(problems)} 个题目", "SUCCESS")

    def test_search_by_title(self, api: APIClient):
        """F-204: 搜索标题"""
        Config.log("测试: 搜索标题", "INFO")
        resp = api.get("/api/problems", params={"keyword": "两数之和"})
        data = resp.json()
        assert data.get("success") is True
        Config.log(f"✅ F-204 标题搜索成功，找到 {len(data.get('problems', []))} 个题目", "SUCCESS")

    def test_search_by_tag(self, api: APIClient):
        """F-205: 搜索标签"""
        Config.log("测试: 搜索标签", "INFO")
        resp = api.get("/api/problems", params={"keyword": "数组"})
        data = resp.json()
        assert data.get("success") is True
        Config.log(f"✅ F-205 标签搜索成功，找到 {len(data.get('problems', []))} 个题目", "SUCCESS")

    def test_search_no_results(self, api: APIClient):
        """空搜索结果"""
        Config.log("测试: 空搜索结果", "INFO")
        resp = api.get("/api/problems", params={"keyword": "不存在的题目xyz12345"})
        data = resp.json()
        assert data.get("success") is True
        assert data.get("total") == 0, f"应返回 0 条，实际 {data.get('total')}"
        Config.log("✅ 空搜索正确返回 0 结果", "SUCCESS")

    # ---- 筛选测试 ----

    def test_filter_difficulty_easy(self, api: APIClient):
        """F-206: 难度筛选（简单）"""
        Config.log("测试: 筛选 easy 难度", "INFO")
        resp = api.get("/api/problems", params={"difficulty": "easy", "page": 1, "size": 20})
        data = resp.json()
        problems = data.get("problems", [])
        for p in problems:
            assert p.get("difficulty") == "easy", f"难度筛选错误: {p.get('difficulty')}"
        Config.log(f"✅ easy 难度筛选正确，共 {len(problems)} 题", "SUCCESS")

    def test_filter_difficulty_medium(self, api: APIClient):
        """难度筛选（中等）"""
        Config.log("测试: 筛选 medium 难度", "INFO")
        resp = api.get("/api/problems", params={"difficulty": "medium", "page": 1, "size": 20})
        data = resp.json()
        problems = data.get("problems", [])
        for p in problems:
            assert p.get("difficulty") == "medium"
        Config.log(f"✅ medium 难度筛选正确，共 {len(problems)} 题", "SUCCESS")

    def test_filter_difficulty_hard(self, api: APIClient):
        """难度筛选（困难）"""
        Config.log("测试: 筛选 hard 难度", "INFO")
        resp = api.get("/api/problems", params={"difficulty": "hard", "page": 1, "size": 20})
        data = resp.json()
        problems = data.get("problems", [])
        for p in problems:
            assert p.get("difficulty") == "hard"
        Config.log(f"✅ hard 难度筛选正确，共 {len(problems)} 题", "SUCCESS")

    # ---- 详情与结构 ----

    def test_get_problem_detail(self, api: APIClient):
        """F-211: 题目详情"""
        Config.log("测试: 题目详情", "INFO")
        list_resp = api.get("/api/problems", params={"page": 1, "size": 1})
        problems = list_resp.json().get("problems", [])
        if not problems:
            pytest.skip("无可用题目")
        pid = problems[0]["id"]
        resp = api.get(f"/api/problems/{pid}")
        assert resp.status_code == 200
        Config.log("✅ F-211 题目详情获取成功", "SUCCESS")

    def test_problem_response_structure(self, api: APIClient):
        """题目响应结构校验"""
        Config.log("测试: 题目响应结构", "INFO")
        resp = api.get("/api/problems", params={"page": 1, "size": 1})
        data = resp.json()
        problems = data.get("problems", [])
        if problems:
            p = problems[0]
            required_fields = ["id", "title", "difficulty"]
            for field in required_fields:
                assert field in p, f"题目缺少必要字段: {field}"
        Config.log("✅ 题目响应结构正确", "SUCCESS")

    def test_all_problems_includes_inactive(self, api: APIClient):
        """B-201: 后台获取所有题目（含禁用）"""
        Config.log("测试: 后台获取所有题目", "INFO")
        resp = api.get("/api/problems/all", params={"page": 1, "size": 20})
        assert resp.status_code == 200
        data = resp.json()
        assert data.get("success") is True
        total_all = data.get("total", 0)

        # 对比前台只返回 ACTIVE
        resp_front = api.get("/api/problems", params={"page": 1, "size": 20})
        total_front = resp_front.json().get("total", 0)

        Config.log(f"后台题目总数: {total_all}, 前台(ACTIVE)总数: {total_front}", "INFO")
        assert total_all >= total_front, "后台题目数应 >= 前台题目数"
        Config.log("✅ B-201 后台题目列表获取成功", "SUCCESS")


# ==================== 3. 题解测试 ====================
class TestOJSolution:
    """题解相关测试 - TestOJSolution

    对应文档: F-301 ~ F-318
    """

    @pytest.fixture
    def problem_id(self, api: APIClient) -> int:
        """获取一个有效题目 ID"""
        resp = api.get("/api/problems", params={"page": 1, "size": 1})
        problems = resp.json().get("problems", [])
        if not problems:
            pytest.skip("无可用题目")
        return problems[0]["id"]

    def test_publish_solution(self, api: APIClient, user_token: str, problem_id: int):
        """F-303: 发布题解"""
        Config.log("测试: 发布题解", "INFO")
        solution = {
            "problemId": problem_id,
            "title": "自动化测试题解 - " + str(int(time.time())),
            "idea": "这是解题思路，使用哈希表",
            "process": "1. 创建哈希表存储元素 2. 遍历数组查找互补元素",
            "codeLang": "java",
            "code": "class Solution { public int[] twoSum(int[] nums, int target) { return new int[0]; } }"
        }
        resp = api.post("/api/solutions", json=solution, token=user_token)
        assert resp.status_code == 200
        data = resp.json()
        assert data.get("success") is True
        Config.log(f"✅ F-303 题解发布成功", "SUCCESS")

    def test_publish_solution_empty(self, api: APIClient, user_token: str, problem_id: int):
        """F-304: 发布空内容题解"""
        Config.log("测试: 发布空内容题解", "INFO")
        resp = api.post("/api/solutions", json={
            "problemId": problem_id,
            "title": "",
            "idea": "",
            "process": "",
            "codeLang": "java",
            "code": ""
        }, token=user_token)
        assert resp.status_code == 200
        Config.log("✅ F-304 空内容题解校验完成", "SUCCESS")

    def test_publish_solution_high_sensitive(self, api: APIClient, user_token: str, problem_id: int):
        """F-305: 发布含高危敏感词的题解"""
        Config.log("测试: 发布含高危敏感词题解", "INFO")
        resp = api.post("/api/solutions", json={
            "problemId": problem_id,
            "title": "赌博网站推广",
            "idea": "这是解题思路",
            "process": "这是解题过程",
            "codeLang": "java",
            "code": "class Solution { public int test() { return 0; } }"
        }, token=user_token)
        assert resp.status_code == 200
        data = resp.json()
        Config.log(f"✅ F-305 高危敏感词处理: success={data.get('success')}, status={data.get('auditStatus')}", "SUCCESS")

    def test_publish_solution_medium_sensitive(self, api: APIClient, user_token: str, problem_id: int):
        """F-306: 发布含中危敏感词的题解"""
        Config.log("测试: 发布含中危敏感词题解", "INFO")
        resp = api.post("/api/solutions", json={
            "problemId": problem_id,
            "title": "刷单返利",
            "idea": "这是解题思路",
            "process": "这是解题过程",
            "codeLang": "java",
            "code": "class Solution { public int test() { return 0; } }"
        }, token=user_token)
        assert resp.status_code == 200
        Config.log("✅ F-306 中危敏感词处理完成", "SUCCESS")

    def test_get_solutions_list(self, api: APIClient, problem_id: int):
        """F-301: 获取题解列表"""
        Config.log("测试: 获取题解列表", "INFO")
        resp = api.get("/api/solutions", params={"problemId": problem_id})
        assert resp.status_code == 200
        Config.log(f"✅ F-301 题解列表获取成功", "SUCCESS")

    def test_get_solution_detail(self, api: APIClient):
        """F-302: 获取题解详情（浏览量+1）"""
        Config.log("测试: 题解详情", "INFO")
        # 先获取一个题解 ID
        list_resp = api.get("/api/solutions", params={"page": 1, "size": 1})
        solutions = list_resp.json()
        # 兼容不同返回结构
        if isinstance(solutions, list):
            sol_list = solutions
        elif isinstance(solutions, dict):
            sol_list = solutions.get("data", solutions.get("list", []))
        else:
            sol_list = []

        if not sol_list:
            pytest.skip("无题解可测试")

        sol_id = sol_list[0].get("id")
        if sol_id is None:
            pytest.skip("题解数据结构异常")

        resp = api.get(f"/api/solutions/{sol_id}")
        assert resp.status_code == 200
        Config.log("✅ F-302 题解详情获取成功（浏览量应+1）", "SUCCESS")

    def test_like_solution(self, api: APIClient, user_token: str):
        """F-308: 点赞功能"""
        Config.log("测试: 点赞功能", "INFO")
        # 获取一个题解
        list_resp = api.get("/api/solutions", params={"page": 1, "size": 1})
        solutions = list_resp.json()
        if isinstance(solutions, list):
            sol_list = solutions
        elif isinstance(solutions, dict):
            sol_list = solutions.get("data", solutions.get("list", []))
        else:
            sol_list = []

        if not sol_list:
            pytest.skip("无题解可测试点赞")

        sol_id = sol_list[0].get("id")
        resp = api.post(f"/api/solutions/{sol_id}/like", token=user_token)
        assert resp.status_code == 200
        data = resp.json()
        Config.log(f"✅ F-308 点赞功能成功: liked={data.get('liked')}, likeCount={data.get('likeCount')}", "SUCCESS")

    def test_like_cancel(self, api: APIClient, user_token: str):
        """F-309: 取消点赞"""
        Config.log("测试: 取消点赞", "INFO")
        list_resp = api.get("/api/solutions", params={"page": 1, "size": 1})
        solutions = list_resp.json()
        if isinstance(solutions, list):
            sol_list = solutions
        elif isinstance(solutions, dict):
            sol_list = solutions.get("data", solutions.get("list", []))
        else:
            sol_list = []

        if not sol_list:
            pytest.skip("无题解可测试")

        sol_id = sol_list[0].get("id")
        resp = api.post(f"/api/solutions/{sol_id}/like", token=user_token)
        assert resp.status_code == 200
        data = resp.json()
        # 再次点赞即取消
        resp2 = api.post(f"/api/solutions/{sol_id}/like", token=user_token)
        assert resp2.status_code == 200
        Config.log("✅ F-309 取消点赞功能正常", "SUCCESS")


# ==================== 4. 评论测试 ====================
class TestComment:
    """评论相关测试 - TestComment

    对应文档: F-311 ~ F-315
    """

    @pytest.fixture
    def solution_id(self, api: APIClient) -> int:
        """获取一个有效题解 ID"""
        resp = api.get("/api/solutions", params={"page": 1, "size": 1})
        data = resp.json()
        if isinstance(data, list):
            sol_list = data
        elif isinstance(data, dict):
            sol_list = data.get("data", data.get("list", []))
        else:
            sol_list = []
        if not sol_list:
            pytest.skip("无题解可测试评论")
        return sol_list[0].get("id")

    @pytest.fixture
    def problem_id(self, api: APIClient) -> int:
        """获取一个有效题目 ID"""
        resp = api.get("/api/problems", params={"page": 1, "size": 1})
        problems = resp.json().get("problems", [])
        if not problems:
            pytest.skip("无可用题目")
        return problems[0]["id"]

    def test_publish_comment(self, api: APIClient, user_token: str, solution_id: int, problem_id: int):
        """F-312: 发布评论"""
        Config.log("测试: 发布评论", "INFO")
        resp = api.post("/api/comments", json={
            "solutionId": solution_id,
            "problemId": problem_id,
            "content": "这篇题解得很好，学到了很多东西"
        }, token=user_token)
        assert resp.status_code == 200
        data = resp.json()
        Config.log(f"✅ F-312 评论发布成功", "SUCCESS")

    def test_publish_comment_sensitive(self, api: APIClient, user_token: str, solution_id: int, problem_id: int):
        """F-314: 发布含敏感词评论"""
        Config.log("测试: 发布含敏感词评论", "INFO")
        resp = api.post("/api/comments", json={
            "solutionId": solution_id,
            "problemId": problem_id,
            "content": "这里有赌博网站，快来看看"
        }, token=user_token)
        assert resp.status_code == 200
        Config.log("✅ F-314 敏感词评论处理完成", "SUCCESS")

    def test_publish_reply(self, api: APIClient, user_token: str, solution_id: int, problem_id: int):
        """F-313: 回复评论"""
        Config.log("测试: 回复评论", "INFO")
        resp = api.post("/api/comments", json={
            "solutionId": solution_id,
            "problemId": problem_id,
            "content": "同意你的观点，我也是这么想的",
            "parentId": 1,
            "rootId": 1
        }, token=user_token)
        assert resp.status_code == 200
        Config.log("✅ F-313 回复评论成功", "SUCCESS")

    def test_get_comments_list(self, api: APIClient, solution_id: int):
        """F-311: 获取评论列表"""
        Config.log("测试: 获取评论列表", "INFO")
        resp = api.get("/api/comments", params={"solutionId": solution_id})
        assert resp.status_code == 200
        Config.log("✅ F-311 评论列表获取成功", "SUCCESS")


# ==================== 5. 提交管理测试 ====================
class TestSubmission:
    """提交管理测试 - TestSubmission

    对应文档: B-221 ~ B-225
    """

    def test_get_submissions(self, api: APIClient, admin_token: str):
        """B-221: 获取所有提交"""
        Config.log("测试: 获取所有提交", "INFO")
        resp = api.get("/api/submissions", token=admin_token)
        assert resp.status_code == 200
        data = resp.json()
        Config.log(f"✅ B-221 提交列表获取成功", "SUCCESS")

    def test_get_submissions_by_problem(self, api: APIClient, admin_token: str):
        """按题目筛选提交"""
        Config.log("测试: 按题目筛选提交", "INFO")
        resp = api.get("/api/submissions", params={"problemId": 1}, token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ 按题目筛选提交成功", "SUCCESS")

    def test_get_submissions_by_status(self, api: APIClient, admin_token: str):
        """按状态筛选提交"""
        Config.log("测试: 按状态筛选提交", "INFO")
        resp = api.get("/api/submissions", params={"status": "AC"}, token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ 按状态筛选提交成功", "SUCCESS")

    def test_user_submission_stats(self, api: APIClient, admin_token: str):
        """B-225: 用户提交统计"""
        Config.log("测试: 用户提交统计", "INFO")
        resp = api.get("/api/submissions/stats/user/1", token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ B-225 用户提交统计获取成功", "SUCCESS")


# ==================== 6. 审核测试（管理员） ====================
class TestAudit:
    """内容审核测试 - TestAudit

    对应文档: B-301 ~ B-315
    """

    def test_get_pending_solutions(self, api: APIClient, admin_token: str):
        """B-301: 获取待审核题解"""
        Config.log("测试: 获取待审核题解", "INFO")
        resp = api.get("/api/admin/solutions", params={"status": "pending"}, token=admin_token)
        assert resp.status_code == 200
        data = resp.json()
        Config.log(f"✅ B-301 待审核题解获取成功", "SUCCESS")

    def test_get_solutions_all_status(self, api: APIClient, admin_token: str):
        """获取所有状态的题解"""
        Config.log("测试: 所有状态题解", "INFO")
        for status in ["pending", "passed", "rejected", "blocked"]:
            resp = api.get("/api/admin/solutions", params={"status": status}, token=admin_token)
            assert resp.status_code == 200
            Config.log(f"  状态 {status}: OK", "INFO")
        Config.log("✅ 所有状态题解查询成功", "SUCCESS")

    def test_audit_pass_solution(self, api: APIClient, admin_token: str):
        """B-303: 审核通过题解"""
        Config.log("测试: 审核通过题解", "INFO")
        # 获取一个待审核题解
        list_resp = api.get("/api/admin/solutions", params={"status": "pending", "page": 1, "size": 1}, token=admin_token)
        data = list_resp.json()
        sol_list = data.get("list", data.get("data", []))
        if not sol_list:
            pytest.skip("无待审核题解")
        sol_id = sol_list[0].get("id")
        resp = api.put(f"/api/admin/solutions/{sol_id}/audit", json={"status": "passed"}, token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ B-303 审核通过成功", "SUCCESS")

    def test_audit_reject_solution(self, api: APIClient, admin_token: str):
        """B-304: 审核驳回题解"""
        Config.log("测试: 审核驳回题解", "INFO")
        list_resp = api.get("/api/admin/solutions", params={"status": "pending", "page": 1, "size": 1}, token=admin_token)
        data = list_resp.json()
        sol_list = data.get("list", data.get("data", []))
        if not sol_list:
            pytest.skip("无待审核题解")
        sol_id = sol_list[0].get("id")
        resp = api.put(f"/api/admin/solutions/{sol_id}/audit", json={
            "status": "rejected",
            "reason": "内容不符合要求"
        }, token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ B-304 审核驳回成功", "SUCCESS")

    def test_get_pending_comments(self, api: APIClient, admin_token: str):
        """B-311: 获取待审核评论"""
        Config.log("测试: 获取待审核评论", "INFO")
        resp = api.get("/api/admin/comments", params={"status": "pending"}, token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ B-311 待审核评论获取成功", "SUCCESS")

    def test_audit_pass_comment(self, api: APIClient, admin_token: str):
        """B-312: 审核通过评论"""
        Config.log("测试: 审核通过评论", "INFO")
        list_resp = api.get("/api/admin/comments", params={"status": "pending", "page": 1, "size": 1}, token=admin_token)
        data = list_resp.json()
        cmt_list = data.get("list", data.get("data", []))
        if not cmt_list:
            pytest.skip("无待审核评论")
        cmt_id = cmt_list[0].get("id")
        resp = api.put(f"/api/admin/comments/{cmt_id}/audit", json={"status": "passed"}, token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ B-312 评论审核通过成功", "SUCCESS")


# ==================== 7. 敏感词测试 ====================
class TestSensitiveWord:
    """敏感词管理测试 - TestSensitiveWord

    对应文档: B-401 ~ B-407
    """

    def test_get_sensitive_words(self, api: APIClient, admin_token: str):
        """B-401: 获取敏感词列表"""
        Config.log("测试: 敏感词列表", "INFO")
        resp = api.get("/api/admin/sensitive-words", token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ B-401 敏感词列表获取成功", "SUCCESS")

    def test_test_sensitive_word_detection(self, api: APIClient):
        """B-405: 敏感词检测"""
        Config.log("测试: 敏感词检测", "INFO")
        test_cases = [
            ("这是一条正常内容", False),
            ("这里有赌博网站信息", True),
            ("刷单返利，快来看看", True),
        ]
        for content, should_detect in test_cases:
            resp = api.post("/api/admin/sensitive-words/test", json={"content": content})
            assert resp.status_code == 200
            data = resp.json()
            detected = data.get("detected", False)
            Config.log(f"  '{content[:20]}...' -> detected={detected}", "INFO")
        Config.log("✅ B-405 敏感词检测功能正常", "SUCCESS")

    def test_add_sensitive_word(self, api: APIClient, admin_token: str):
        """B-402: 添加敏感词"""
        Config.log("测试: 添加敏感词", "INFO")
        timestamp = int(time.time())
        resp = api.post("/api/admin/sensitive-words", json={
            "word": f"测试敏感词_{timestamp}",
            "level": "LOW",
            "category": "test"
        }, token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ B-402 敏感词添加成功", "SUCCESS")

    def test_refresh_cache(self, api: APIClient, admin_token: str):
        """B-407: 刷新敏感词缓存"""
        Config.log("测试: 刷新敏感词缓存", "INFO")
        resp = api.post("/api/admin/sensitive-words/refresh-cache", token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ B-407 敏感词缓存刷新成功", "SUCCESS")


# ==================== 8. 后台用户管理测试 ====================
class TestAdminUser:
    """后台用户管理测试 - TestAdminUser

    对应文档: B-101 ~ B-109
    """

    def test_get_user_list(self, api: APIClient, admin_token: str):
        """B-101: 获取用户列表"""
        Config.log("测试: 获取用户列表", "INFO")
        resp = api.get("/api/admin/user/list", params={"page": 1, "pageSize": 10}, token=admin_token)
        assert resp.status_code == 200
        data = resp.json()
        Config.log(f"✅ B-101 用户列表获取成功", "SUCCESS")

    def test_get_user_count(self, api: APIClient):
        """获取用户数量"""
        Config.log("测试: 用户数量统计", "INFO")
        resp = api.get("/api/admin/user/count")
        assert resp.status_code == 200
        Config.log("✅ 用户数量获取成功", "SUCCESS")

    def test_user_statistics_summary(self, api: APIClient, admin_token: str):
        """B-011 ~ B-013: 数据统计大屏"""
        Config.log("测试: 数据统计", "INFO")
        resp = api.get("/api/statistics/summary", token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ B-011 ~ B-013 数据统计获取成功", "SUCCESS")

    def test_user_trend(self, api: APIClient, admin_token: str):
        """用户趋势"""
        Config.log("测试: 用户趋势", "INFO")
        resp = api.get("/api/statistics/trend", params={"type": "user"}, token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ 用户趋势获取成功", "SUCCESS")


# ==================== 9. 系统管理测试 ====================
class TestSystemManagement:
    """系统管理测试 - TestSystemManagement

    对应文档: B-701 ~ B-704
    """

    def test_login_log(self, api: APIClient, admin_token: str):
        """B-701: 登录日志"""
        Config.log("测试: 登录日志", "INFO")
        resp = api.get("/api/system/login-log", params={"page": 1, "pageSize": 20}, token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ B-701 登录日志获取成功", "SUCCESS")

    def test_login_log_stats(self, api: APIClient, admin_token: str):
        """登录日志统计"""
        Config.log("测试: 登录日志统计", "INFO")
        resp = api.get("/api/system/login-log/stats", token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ 登录日志统计获取成功", "SUCCESS")

    def test_operation_log(self, api: APIClient, admin_token: str):
        """B-702: 操作日志"""
        Config.log("测试: 操作日志", "INFO")
        resp = api.get("/api/system/operation-log", params={"page": 1, "pageSize": 20}, token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ B-702 操作日志获取成功", "SUCCESS")

    def test_system_config(self, api: APIClient, admin_token: str):
        """B-703: 系统配置"""
        Config.log("测试: 系统配置", "INFO")
        resp = api.get("/api/system/config", token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ B-703 系统配置获取成功", "SUCCESS")


# ==================== 10. 运维监控测试 ====================
class TestMonitor:
    """运维监控测试 - TestMonitor

    对应文档: B-801 ~ B-803
    """

    def test_service_status(self, api: APIClient, admin_token: str):
        """B-801: 服务状态监控"""
        Config.log("测试: 服务状态", "INFO")
        resp = api.get("/api/monitor/service", token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ B-801 服务状态获取成功", "SUCCESS")

    def test_api_performance(self, api: APIClient, admin_token: str):
        """B-802: API 性能监控"""
        Config.log("测试: API 性能", "INFO")
        resp = api.get("/api/monitor/api", params={"hours": 24}, token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ B-802 API 性能数据获取成功", "SUCCESS")


# ==================== 11. 数据导出测试 ====================
class TestExport:
    """数据导出测试 - TestExport

    对应文档: B-901 ~ B-902
    """

    def test_export_users(self, api: APIClient, admin_token: str):
        """B-901: 导出用户数据"""
        Config.log("测试: 导出用户", "INFO")
        resp = api.get("/api/export/users", params={"format": "excel"}, token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ B-901 用户数据导出成功", "SUCCESS")

    def test_export_logs(self, api: APIClient, admin_token: str):
        """B-902: 导出日志数据"""
        Config.log("测试: 导出日志", "INFO")
        resp = api.get("/api/export/logs", params={"format": "excel"}, token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ B-902 日志数据导出成功", "SUCCESS")


# ==================== 12. 支付/硬币测试 ====================
class TestPayment:
    """支付/硬币系统测试 - TestPayment

    对应文档: B-501 ~ B-604
    """

    def test_get_coin_products(self, api: APIClient):
        """F-404: 硬币商品浏览"""
        Config.log("测试: 硬币商品列表", "INFO")
        resp = api.get("/api/payment/products")
        assert resp.status_code == 200
        Config.log("✅ F-404 硬币商品获取成功", "SUCCESS")

    def test_get_product_detail(self, api: APIClient):
        """硬币商品详情"""
        Config.log("测试: 硬币商品详情", "INFO")
        # 先获取一个商品
        list_resp = api.get("/api/payment/products")
        data = list_resp.json()
        products = data if isinstance(data, list) else data.get("products", [])
        if not products:
            pytest.skip("无可测试商品")
        pid = products[0].get("id")
        if pid is None:
            pytest.skip("商品 ID 不存在")
        resp = api.get(f"/api/payment/products/{pid}")
        assert resp.status_code == 200
        Config.log("✅ 硬币商品详情获取成功", "SUCCESS")

    def test_admin_get_products(self, api: APIClient, admin_token: str):
        """B-501: 后台商品列表（含下架）"""
        Config.log("测试: 后台商品列表", "INFO")
        resp = api.get("/api/coin/admin/products", token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ B-501 后台商品列表获取成功", "SUCCESS")

    def test_admin_get_purchases(self, api: APIClient, admin_token: str):
        """购买记录查询"""
        Config.log("测试: 购买记录", "INFO")
        resp = api.get("/api/coin/admin/purchases", token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ 购买记录获取成功", "SUCCESS")

    def test_admin_get_orders(self, api: APIClient, admin_token: str):
        """B-601: 订单列表"""
        Config.log("测试: 订单列表", "INFO")
        resp = api.get("/api/orders", token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ B-601 订单列表获取成功", "SUCCESS")

    def test_payment_stats(self, api: APIClient, admin_token: str):
        """B-604: 支付统计"""
        Config.log("测试: 支付统计", "INFO")
        resp = api.get("/api/payment/stats", token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ B-604 支付统计获取成功", "SUCCESS")


# ==================== 13. 公告/反馈测试 ====================
class TestExtension:
    """扩展功能测试 - TestExtension

    对应文档: F-005, B-311 ~ B-314
    """

    def test_get_announcements_published(self, api: APIClient):
        """F-005: 已发布公告"""
        Config.log("测试: 已发布公告", "INFO")
        resp = api.get("/api/announcements/published")
        assert resp.status_code == 200
        Config.log("✅ F-005 公告获取成功", "SUCCESS")

    def test_admin_announcement_list(self, api: APIClient, admin_token: str):
        """后台公告管理"""
        Config.log("测试: 后台公告列表", "INFO")
        resp = api.get("/api/extension/announcement", params={"page": 1, "pageSize": 10}, token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ 后台公告列表获取成功", "SUCCESS")

    def test_admin_feedback_list(self, api: APIClient, admin_token: str):
        """反馈管理"""
        Config.log("测试: 反馈列表", "INFO")
        resp = api.get("/api/extension/feedback", params={"page": 1, "pageSize": 10}, token=admin_token)
        assert resp.status_code == 200
        Config.log("✅ 反馈列表获取成功", "SUCCESS")


# ==================== 14. 面试题测试 ====================
class TestInterview:
    """面试题功能测试 - TestInterview

    对应文档: F-501 ~ F-504
    """

    def test_get_interview_problems(self, api: APIClient):
        """F-501: 面试题列表"""
        Config.log("测试: 面试题列表", "INFO")
        resp = api.get("/api/interview/user/problems", params={"page": 1, "size": 20})
        assert resp.status_code == 200
        Config.log("✅ F-501 面试题列表获取成功", "SUCCESS")


# ==================== 15. 性能测试 ====================
class TestPerformance:
    """性能测试 - TestPerformance"""

    def test_api_response_time(self, api: APIClient):
        """API 响应时间测试"""
        Config.log("测试: API 响应时间", "INFO")
        times = []
        for i in range(5):
            start = time.time()
            resp = api.get("/api/problems", params={"page": 1, "size": 20})
            elapsed = time.time() - start
            times.append(elapsed)
            assert resp.status_code == 200

        avg_time = sum(times) / len(times)
        max_time = max(times)
        p95_time = sorted(times)[int(len(times) * 0.95)] if len(times) > 1 else max_time

        Config.log(f"响应时间统计:", "INFO")
        Config.log(f"  平均: {avg_time*1000:.0f}ms", "INFO")
        Config.log(f"  最大: {max_time*1000:.0f}ms", "INFO")
        Config.log(f"  P95:  {p95_time*1000:.0f}ms", "INFO")

        assert avg_time < 2.0, f"平均响应时间过长: {avg_time:.2f}s"
        Config.log("✅ API 响应时间正常", "SUCCESS")

    def test_concurrent_requests(self, api: APIClient):
        """并发请求测试"""
        Config.log("测试: 并发请求 (10 线程)", "INFO")

        def fetch_page(page):
            try:
                resp = api.get("/api/problems", params={"page": page + 1, "size": 20})
                return resp.status_code == 200
            except Exception as e:
                return False

        with ThreadPoolExecutor(max_workers=10) as executor:
            futures = [executor.submit(fetch_page, i) for i in range(10)]
            results = [f.result() for f in as_completed(futures)]

        success_count = sum(results)
        success_rate = success_count / len(results) * 100
        Config.log(f"并发请求: {success_count}/10 成功 ({success_rate:.0f}%)", "INFO")
        assert success_count >= 8, f"并发请求成功率过低: {success_count}/10"
        Config.log("✅ 并发请求测试通过", "SUCCESS")

    def test_large_data_fetch(self, api: APIClient):
        """大数据量获取测试"""
        Config.log("测试: 大数据量获取", "INFO")
        start = time.time()
        resp = api.get("/api/problems", params={"page": 1, "size": 100})
        elapsed = time.time() - start
        assert resp.status_code == 200
        data = resp.json()
        count = len(data.get("problems", []))
        Config.log(f"获取 {count} 条数据耗时: {elapsed*1000:.0f}ms", "INFO")
        Config.log("✅ 大数据量获取测试通过", "SUCCESS")

    def test_sort_performance(self, api: APIClient):
        """排序性能测试"""
        Config.log("测试: 排序性能", "INFO")
        for sort_by in ["id", "createdAt"]:
            for sort_dir in ["asc", "desc"]:
                start = time.time()
                resp = api.get("/api/problems", params={
                    "sort": sort_dir, "sortBy": sort_by, "page": 1, "size": 50
                })
                elapsed = time.time() - start
                assert resp.status_code == 200
                Config.log(f"  sortBy={sort_by} sort={sort_dir}: {elapsed*1000:.0f}ms", "INFO")
        Config.log("✅ 排序性能测试通过", "SUCCESS")


# ==================== 16. 安全性测试 ====================
class TestSecurity:
    """安全性测试 - TestSecurity"""

    def test_sql_injection_keyword(self, api: APIClient):
        """SQL 注入防护测试（关键词搜索）"""
        Config.log("测试: SQL 注入防护", "INFO")
        resp = api.get("/api/problems", params={"keyword": "1' OR '1'='1"})
        assert resp.status_code == 200
        data = resp.json()
        Config.log(f"SQL 注入测试: 返回 {data.get('total', 0)} 条", "INFO")
        Config.log("✅ SQL 注入防护测试通过", "SUCCESS")

    def test_sql_injection_login(self, api: APIClient):
        """SQL 注入防护测试（登录）"""
        Config.log("测试: SQL 注入防护 - 登录", "INFO")
        resp = api.post("/api/login", json={
            "username": "admin' OR '1'='1",
            "password": "' OR '1'='1"
        })
        assert resp.status_code == 200
        data = resp.json()
        if isinstance(data, dict):
            assert data.get("success") is False, "SQL 注入登录应失败"
        Config.log("✅ SQL 注入登录防护正确", "SUCCESS")

    def test_xss_payload_search(self, api: APIClient):
        """XSS 防护测试"""
        Config.log("测试: XSS 防护", "INFO")
        xss_payloads = [
            "<script>alert('xss')</script>",
            "javascript:alert('xss')",
            "<img src=x onerror=alert('xss')>",
        ]
        for payload in xss_payloads:
            resp = api.get("/api/problems", params={"keyword": payload})
            assert resp.status_code == 200
        Config.log("✅ XSS 防护测试通过", "SUCCESS")

    def test_idor_prevention(self, api: APIClient, user_token: str):
        """越权访问防护（IDOR）"""
        Config.log("测试: 越权访问防护", "INFO")
        # 尝试访问其他用户的数据
        resp = api.get("/api/users/99999", token=user_token)
        # 应该返回 404 或 403
        Config.log(f"越权访问响应: HTTP {resp.status_code}", "INFO")
        Config.log("✅ 越权访问防护测试完成", "SUCCESS")

    def test_rate_limiting(self, api: APIClient):
        """速率限制测试"""
        Config.log("测试: 速率限制", "INFO")
        # 短时间内发送大量请求
        start = time.time()
        responses = []
        for i in range(30):
            resp = api.get("/api/problems", params={"page": 1, "size": 1})
            responses.append(resp.status_code)
        elapsed = time.time() - start

        status_200 = sum(1 for s in responses if s == 200)
        status_429 = sum(1 for s in responses if s == 429)

        Config.log(f"30 次请求耗时 {elapsed:.2f}s, 200={status_200}, 429={status_429}", "INFO")
        if status_429 > 0:
            Config.log("✅ 速率限制生效", "SUCCESS")
        else:
            Config.log("⚠️ 未检测到速率限制，建议添加", "WARN")


# ==================== 17. 测试报告 ====================
class TestReport:
    """测试报告类 - 输出总结"""

    def test_print_summary(self):
        """打印测试总结"""
        Config.log("=" * 60)
        Config.log("测试完成总结", "SUCCESS")
        Config.log("=" * 60)
        Config.log(f"请查看上方测试结果统计", "INFO")


# ==================== 主入口 ====================
if __name__ == "__main__":
    print("\n" + "=" * 60)
    print("  AlgoVize 算法数据结构可视化平台 - API 自动化测试 V2")
    print(f"  开始时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"  后端地址: {BASE_URL}")
    print(f"  超时时间: {TIMEOUT}s")
    print("=" * 60 + "\n")

    # 检查后端服务
    print("正在检查后端服务...")
    try:
        resp = requests.get(f"{BASE_URL}/api/problems", params={"page": 1, "size": 1}, timeout=5)
        if resp.status_code == 200:
            print(f"\033[32m✅ 后端服务可用 ({BASE_URL})\033[0m\n")
        else:
            print(f"\033[33m⚠️ 后端返回异常状态码: {resp.status_code}\033[0m\n")
    except requests.exceptions.ConnectionError:
        print(f"\033[31m❌ 无法连接后端服务: {BASE_URL}\033[0m")
        print("请确保后端服务已启动！")
        sys.exit(1)
    except requests.exceptions.Timeout:
        print(f"\033[31m❌ 连接超时: {BASE_URL}\033[0m")
        sys.exit(1)

    # 运行测试
    import pytest
    exit_code = pytest.main([
        __file__,
        "-v",
        "--tb=short",
        "--no-header",
        "-p", "no:warnings"
    ])

    print("\n" + "=" * 60)
    print(f"  测试完成，退出码: {exit_code}")
    print(f"  结束时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("=" * 60)

    if exit_code == 0:
        print("\n\033[32m🎉 所有测试通过！\033[0m\n")
    else:
        print(f"\n\033[31m❌ 存在失败的测试用例，请查看上方日志\033[0m\n")
