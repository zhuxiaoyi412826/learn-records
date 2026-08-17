#!/usr/bin/env python3
"""
公众号文章发布前优化器
对生成的公众号文章进行多维分析，给出评分和优化建议，并可直接输出优化版。

用法：
  python optimize.py analyze <文章路径>          # 分析文章，输出评分和建议
  python optimize.py optimize <文章路径> [输出路径] # 分析+自动优化，输出优化后文章
"""

import sys
import json
import re
import os
from pathlib import Path


def read_article(filepath):
    with open(filepath, "r", encoding="utf-8") as f:
        return f.read()


def extract_title(md_text):
    """提取标题"""
    m = re.search(r'^#\s+(.+)$', md_text, re.MULTILINE)
    return m.group(1).strip() if m else ""


def extract_sections(md_text):
    """提取正文段落"""
    lines = md_text.split('\n')
    sections = []
    current = []
    for line in lines:
        if line.startswith('#'):
            if current:
                sections.append('\n'.join(current))
                current = []
        current.append(line)
    if current:
        sections.append('\n'.join(current))
    return sections


def analyze_title(title):
    """标题分析：评分 + 建议"""
    score = 50
    suggestions = []
    
    # 长度分析（公众号标题最佳14-22字）
    title_len = len(title)
    if 14 <= title_len <= 22:
        score += 15
    elif 10 <= title_len <= 28:
        score += 8
        suggestions.append(f"标题{title_len}字，最佳14-22字，建议精简或扩充")
    else:
        suggestions.append(f"标题{title_len}字，严重偏长/偏短，建议调整到14-22字")
    
    # 情绪钩子：问号、感叹号、引号
    if '？' in title or '?' in title:
        score += 8
    if '！' in title or '!' in title:
        score += 5
    if '"' in title or '"' in title or '「' in title:
        score += 8
    elif '、' in title:
        score += 5  # 并列结构也有冲击力
    
    # 数字出现（数据感）
    if re.search(r'\d+', title):
        score += 8
    
    # 对比/冲突结构
    conflict_words = ['但', '却', '然而', '背后的', '真相', '反转', '意外', '谁']
    if any(w in title for w in conflict_words):
        score += 8
    
    # 趋势词
    trend_words = ['首次', '突破', '最大', '史上', '新一轮', '新阶段', '里程碑', '炸了', '太炸了']
    if any(w in title for w in trend_words):
        score += 5
    
    # 缺少钩子
    if not any(w in title for w in conflict_words + trend_words) and '？' not in title and '"' not in title:
        suggestions.append("标题缺少情绪钩子（冲突词/趋势词/问号/引号），吸引力可能不足")
    
    # 是否太像新闻标题
    news_patterns = ['宣布', '发布', '推出', '完成', '获得']
    if sum(1 for p in news_patterns if p in title) >= 2:
        score -= 5
        suggestions.append("标题偏向新闻播报风格，建议加观点/悬念/冲突感")
    
    return min(score, 100), suggestions


def analyze_structure(md_text):
    """内容结构分析"""
    score = 60
    suggestions = []
    lines = md_text.split('\n')
    total_len = len(md_text)
    
    # 开头钩子（前3段是否有吸引力）
    first_200 = md_text[:400]
    hook_words = ['但', '然而', '真相', '问题是', '什么概念', '你发现没有', '为什么', '这不', '炸了']
    if any(w in first_200 for w in hook_words):
        score += 10
    else:
        suggestions.append("开头缺少钩子，建议前3段内加入悬念/冲突/反直觉观点")
    
    # 段落长度检查（公众号最佳每段3-5行）
    paragraphs = [p.strip() for p in re.split(r'\n\n+', md_text) if p.strip() and not p.strip().startswith('#')]
    long_paras = [p for p in paragraphs if len(p) > 200]
    if long_paras:
        score -= min(len(long_paras) * 3, 15)
        suggestions.append(f"有{len(long_paras)}个段落超过200字，建议拆分（公众号读者习惯短段阅读）")
    
    # 加粗重点检查
    bold_count = len(re.findall(r'\*\*.+?\*\*', md_text))
    if bold_count >= 3:
        score += 10
    elif bold_count == 0:
        suggestions.append("文章没有加粗重点，建议对核心观点加粗突出")
    
    # 引用块检查
    quote_count = len(re.findall(r'^>\s', md_text, re.MULTILINE))
    if quote_count >= 1:
        score += 5
    
    # 结尾CTA检查
    last_300 = md_text[-400:]
    cta_words = ['你准备好了', '你怎么看', '你怎么想', '欢迎', '留言', '关注', '转发', '点赞']
    if any(w in last_300 for w in cta_words):
        score += 10
    else:
        suggestions.append("结尾缺少互动引导（CTA），建议加一句引导关注/留言/转发")
    
    # 总长度检查（公众号最佳1500-3000字）
    char_count = len(re.sub(r'[#\-\*\n\r\s>]', '', md_text))
    if 1500 <= char_count <= 3000:
        score += 5
    elif char_count > 3000:
        suggestions.append(f"文章约{char_count}字，偏长，建议控制在1500-3000字")
    
    return min(score, 100), suggestions


def analyze_readability(md_text):
    """可读性分析"""
    score = 60
    suggestions = []
    
    # 去掉标记符号统计纯文本
    plain = re.sub(r'[#*\-`>]', '', md_text)
    sentences = re.split(r'[。！？\n]', plain)
    sentences = [s.strip() for s in sentences if len(s.strip()) > 5]
    
    if not sentences:
        return 50, ["无法分析句子结构"]
    
    # 平均句长
    avg_len = sum(len(s) for s in sentences) / len(sentences)
    if avg_len <= 25:
        score += 15
    elif avg_len <= 35:
        score += 8
    else:
        suggestions.append(f"平均句长{avg_len:.0f}字，偏长（建议<25字），公众号读者偏好短句")
    
    # 长句占比
    long_sentences = [s for s in sentences if len(s) > 40]
    if len(long_sentences) > len(sentences) * 0.2:
        score -= 10
        suggestions.append(f"超过20%的句子超40字，建议拆分长句")
    
    # 专业术语密度
    jargon = ['S-1', 'IPO', 'VLA', 'L4', 'GPU', 'API', 'S-team', 'LLM', 'AGI', 'benchmark', 'token', 
              '投后估值', '基座大模型', '多模态', '智能体', '视觉-语言', '参数规模', '缓存命中']
    jargon_count = sum(1 for j in jargon if j in md_text)
    if jargon_count > 8:
        score -= 5
        suggestions.append(f"专业术语出现{jargon_count}次，偏多，建议对非核心术语加通俗解释")
    
    # 口语化/节奏感
    colloquial = ['什么概念？', '你发现没有', '这不', '说白了', '简单说', '问题是', '有意思的是', '说白了']
    colloquial_count = sum(1 for c in colloquial if c in md_text)
    if colloquial_count >= 2:
        score += 10
    elif colloquial_count == 0:
        suggestions.append("缺少口语化过渡，建议加入'什么概念？''你发现没有'等节奏词")
    
    return min(score, 100), suggestions


def analyze_engagement(md_text):
    """互动吸引力分析"""
    score = 50
    suggestions = []
    
    # 观点密度
    opinion_patterns = ['这说明什么', '本质是', '底层逻辑', '真相是', '我的判断', '观点', '我的看法',
                       '这背后', '真正的问题是', '关键在于', '值得注意的是']
    opinion_count = sum(1 for p in opinion_patterns if p in md_text)
    if opinion_count >= 3:
        score += 15
    elif opinion_count >= 1:
        score += 8
    else:
        suggestions.append("缺少明确观点表达，建议每部分加入'这说明什么'类的解读")
    
    # 数据支撑
    data_count = len(re.findall(r'\d+[亿万千百%美元]', md_text))
    if data_count >= 3:
        score += 10
    elif data_count == 0:
        suggestions.append("缺少数据支撑，建议加入关键数字增强说服力")
    
    # 对比/冲突
    contrast_words = ['但', '然而', '问题是', '但换个角度', '反过来', 'vs', '对比', '差距']
    contrast_count = sum(1 for c in contrast_words if c in md_text)
    if contrast_count >= 2:
        score += 10
    
    # 递进结构
    progressive = ['第一', '第二', '第三', '首先', '其次', '最后', '更重要的', '更深层的']
    progressive_count = sum(1 for p in progressive if p in md_text)
    if progressive_count >= 2:
        score += 5
    
    return min(score, 100), suggestions


def run_analysis(md_text):
    """运行完整分析"""
    title = extract_title(md_text)
    
    results = {}
    
    # 标题分析
    title_score, title_suggestions = analyze_title(title)
    results['title'] = {
        'text': title,
        'score': title_score,
        'suggestions': title_suggestions
    }
    
    # 结构分析
    struct_score, struct_suggestions = analyze_structure(md_text)
    results['structure'] = {
        'score': struct_score,
        'suggestions': struct_suggestions
    }
    
    # 可读性
    read_score, read_suggestions = analyze_readability(md_text)
    results['readability'] = {
        'score': read_score,
        'suggestions': read_suggestions
    }
    
    # 互动吸引力
    engage_score, engage_suggestions = analyze_engagement(md_text)
    results['engagement'] = {
        'score': engage_score,
        'suggestions': engage_suggestions
    }
    
    # 综合分
    results['total_score'] = round(
        title_score * 0.3 + struct_score * 0.25 + read_score * 0.2 + engage_score * 0.25
    )
    
    # 是否需要优化
    results['needs_optimization'] = results['total_score'] < 80
    results['level'] = (
        '优秀' if results['total_score'] >= 85 else
        '良好' if results['total_score'] >= 75 else
        '一般' if results['total_score'] >= 65 else
        '需改进'
    )
    
    return results


def format_report(results):
    """格式化分析报告"""
    lines = []
    lines.append(f"📊 公众号文章质量评分：{results['total_score']}/100（{results['level']}）")
    lines.append("")
    lines.append(f"  标题吸引力：{results['title']['score']}/100")
    if results['title']['suggestions']:
        for s in results['title']['suggestions']:
            lines.append(f"    ⚠️ {s}")
    lines.append(f"  内容结构：{results['structure']['score']}/100")
    if results['structure']['suggestions']:
        for s in results['structure']['suggestions']:
            lines.append(f"    ⚠️ {s}")
    lines.append(f"  可读性：{results['readability']['score']}/100")
    if results['readability']['suggestions']:
        for s in results['readability']['suggestions']:
            lines.append(f"    ⚠️ {s}")
    lines.append(f"  互动吸引力：{results['engagement']['score']}/100")
    if results['engagement']['suggestions']:
        for s in results['engagement']['suggestions']:
            lines.append(f"    ⚠️ {s}")
    lines.append("")
    if results['needs_optimization']:
        lines.append("🔔 综合评分<80，建议优化后再发布")
    else:
        lines.append("✅ 文章质量达标，可以发布")
    
    return '\n'.join(lines)


def optimize_title(title, suggestions):
    """基于建议生成备选标题"""
    alternatives = []
    
    # 常用优化模板
    templates = [
        # 悬念型
        lambda t: f"{''.join(re.findall(r'[\u4e00-\u9fff]+', t)[:8])}…背后，藏着什么？",
        # 冲突型
        lambda t: f"{''.join(re.findall(r'[\u4e00-\u9fff]+', t)[:6])}的真相：比你想象的更{random_choice(['疯狂', '残酷', '震撼'])}",
        # 数字型
        lambda t: f"3个信号看懂：{''.join(re.findall(r'[\u4e00-\u9fff]+', t)[:6])}",
        # 观点型
        lambda t: f"别只看表面——{''.join(re.findall(r'[\u4e00-\u9fff]+', t)[:8])}的3个真相",
    ]
    
    import random
    def random_choice(lst):
        return random.choice(lst)
    
    for tpl in templates:
        try:
            alt = tpl(title)
            if alt and len(alt) <= 28 and alt != title:
                alternatives.append(alt)
        except:
            pass
    
    return alternatives[:3]


def main():
    if len(sys.argv) < 3:
        print("用法: python optimize.py analyze <文章路径>")
        print("      python optimize.py optimize <文章路径> [输出路径]")
        sys.exit(1)
    
    action = sys.argv[1]
    filepath = sys.argv[2]
    output_path = sys.argv[3] if len(sys.argv) > 3 else None
    
    md_text = read_article(filepath)
    results = run_analysis(md_text)
    
    # 输出分析报告
    report = format_report(results)
    print(report)
    
    # 输出JSON格式（供后续程序读取）
    json_path = os.path.splitext(filepath)[0] + '_analysis.json'
    with open(json_path, 'w', encoding='utf-8') as f:
        json.dump(results, f, ensure_ascii=False, indent=2)
    print(f"\n分析结果已保存: {json_path}")
    
    if action == 'optimize' and results['needs_optimization']:
        print("\n📝 优化建议已记录，请根据上述提示手动调整或由AI自动优化")
        # 将建议写入文件供AI读取
        suggestions_path = os.path.splitext(filepath)[0] + '_suggestions.md'
        with open(suggestions_path, 'w', encoding='utf-8') as f:
            f.write(f"# 文章优化建议\n\n")
            f.write(f"## 原始评分：{results['total_score']}/100\n\n")
            for dim, label in [('title', '标题'), ('structure', '结构'), ('readability', '可读性'), ('engagement', '互动')]:
                if results[dim]['suggestions']:
                    f.write(f"### {label}\n")
                    for s in results[dim]['suggestions']:
                        f.write(f"- {s}\n")
                    f.write("\n")
        print(f"优化建议已保存: {suggestions_path}")


if __name__ == '__main__':
    main()
