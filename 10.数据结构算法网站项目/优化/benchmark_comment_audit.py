# -*- coding: utf-8 -*-
"""
题解评论审核接口基准测试脚本

流程：登录 → 访问评论列表接口 → 退出登录，循环 N 次，打印统计信息。
统计：总耗时 min/max/avg + 各环节耗时 + 分布图。
"""

import requests
import time
import sys
import statistics

# ==================== 公共变量 ====================
BASE_URL   = "http://localhost:80"
USERNAME   = "algovize"
PASSWORD   = "algovize123"
RUN_COUNT  = 10                  # 执行次数，可命令行覆盖
PAGE       = 1
PAGE_SIZE  = 1
AUDIT_STATUS = "pending"

LOGIN_URL    = f"{BASE_URL}/api/admin/login"
COMMENT_URL  = f"{BASE_URL}/api/admin/solutions/comments"
LOGOUT_URL   = f"{BASE_URL}/api/admin/logout"


def do_one_round():
    """执行一轮：登录 → 评论列表 → 退出，返回各环节毫秒耗时 dict。"""
    session = requests.Session()
    timings = {}

    # ① 登录
    t0 = time.perf_counter()
    resp = session.post(LOGIN_URL, json={"username": USERNAME, "password": PASSWORD}, timeout=10)
    timings["login"] = (time.perf_counter() - t0) * 1000
    if resp.status_code != 200:
        raise RuntimeError(f"登录失败: HTTP {resp.status_code} {resp.text[:200]}")
    token = resp.json().get("data", {}).get("token")
    if not token:
        raise RuntimeError(f"登录响应无 token: {resp.text[:200]}")
    # 设置 cookie
    session.cookies.set("satoken", token)

    # ② 访问评论列表接口
    t0 = time.perf_counter()
    resp = session.get(COMMENT_URL, params={
        "auditStatus": AUDIT_STATUS,
        "page": PAGE,
        "pageSize": PAGE_SIZE,
    }, timeout=10)
    timings["comment_list"] = (time.perf_counter() - t0) * 1000
    if resp.status_code != 200:
        raise RuntimeError(f"评论列表失败: HTTP {resp.status_code} {resp.text[:200]}")

    # ③ 退出登录
    t0 = time.perf_counter()
    resp = session.post(LOGOUT_URL, timeout=10)
    timings["logout"] = (time.perf_counter() - t0) * 1000

    timings["total"] = timings["login"] + timings["comment_list"] + timings["logout"]
    return timings


def print_bar(value_ms, max_ms, width=30):
    """生成简易条形图。"""
    filled = int(value_ms / max_ms * width) if max_ms > 0 else 0
    return "█" * filled + "░" * (width - filled)


def main():
    run_count = int(sys.argv[1]) if len(sys.argv) > 1 else RUN_COUNT
    print(f"\n{'=' * 70}")
    print(f"  题解评论审核接口基准测试")
    print(f"  目标: {BASE_URL}")
    print(f"  账号: {USERNAME}")
    print(f"  执行轮数: {run_count}")
    print(f"  流程: 登录 → 评论列表(auditStatus={AUDIT_STATUS},page={PAGE},size={PAGE_SIZE}) → 退出")
    print(f"{'=' * 70}\n")

    results = []
    for i in range(1, run_count + 1):
        try:
            t = do_one_round()
            results.append(t)
            print(f"  第 {i:>3d} 轮  总计 {t['total']:7.2f}ms  "
                  f"(登录 {t['login']:6.2f} | 列表 {t['comment_list']:6.2f} | 退出 {t['logout']:5.2f})")
        except Exception as e:
            print(f"  第 {i:>3d} 轮  失败: {e}")
            continue

    if not results:
        print("\n  所有轮次均失败，无统计数据。")
        return

    # ==================== 统计信息 ====================
    totals     = [r["total"] for r in results]
    login_ts   = [r["login"] for r in results]
    list_ts    = [r["comment_list"] for r in results]
    logout_ts  = [r["logout"] for r in results]

    max_total = max(totals)

    print(f"\n{'=' * 70}")
    print(f"  统计信息（{len(results)} 次成功）")
    print(f"{'=' * 70}")

    # --- 各环节统计 ---
    for label, data in [("登录", login_ts), ("评论列表", list_ts), ("退出", logout_ts), ("总计", totals)]:
        mn = min(data)
        mx = max(data)
        avg = statistics.mean(data)
        med = statistics.median(data)
        bar = print_bar(avg, max_total)
        print(f"\n  【{label}】")
        print(f"    最快: {mn:8.2f}ms")
        print(f"    最慢: {mx:8.2f}ms")
        print(f"    平均: {avg:8.2f}ms")
        print(f"    中位: {med:8.2f}ms")
        print(f"    分布: {bar} {avg:.1f}ms")

    # --- 耗时分布饼图（按平均值）---
    avg_login  = statistics.mean(login_ts)
    avg_list   = statistics.mean(list_ts)
    avg_logout = statistics.mean(logout_ts)
    avg_total  = statistics.mean(totals)

    print(f"\n  {'=' * 50}")
    print(f"  平均耗时分布（总计 {avg_total:.2f}ms）")
    print(f"  {'=' * 50}")
    for label, val in [("登录    (Redis写+DB查用户)", avg_login),
                        ("评论列表 (Redis读token+权限+COUNT+SELECT)", avg_list),
                        ("退出    (Redis删token)", avg_logout)]:
        pct = val / avg_total * 100 if avg_total > 0 else 0
        bar_w = int(pct / 100 * 40)
        print(f"  {label:42s} {val:7.2f}ms  {pct:5.1f}%  {'█' * bar_w}")

    # --- 逐轮明细 ---
    print(f"\n  逐轮明细（ms）")
    print(f"  {'轮次':>4s}  {'登录':>8s}  {'评论列表':>8s}  {'退出':>8s}  {'总计':>8s}")
    print(f"  {'-' * 4}  {'-' * 8}  {'-' * 8}  {'-' * 8}  {'-' * 8}")
    for i, r in enumerate(results, 1):
        print(f"  {i:>4d}  {r['login']:8.2f}  {r['comment_list']:8.2f}  {r['logout']:8.2f}  {r['total']:8.2f}")

    print(f"\n{'=' * 70}\n")


if __name__ == "__main__":
    main()
