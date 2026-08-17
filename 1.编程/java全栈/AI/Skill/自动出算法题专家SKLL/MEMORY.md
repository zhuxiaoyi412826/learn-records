Windows环境要点：
- 插件安装：install_from_path+execute不可用（全平台）；变通：复制插件到plugins-custom/，再qw_action enable启用qoderwork.settings.plugins.{folderName}（Windows用PowerShell Copy-Item）
- Bash工具实际运行/usr/bin/bash非cmd.exe；文件复制用cp非copy
- 无Python/poppler；PDF文本提取用Node.js: npm i pdfjs-dist, require('pdfjs-dist/legacy/build/pdf.mjs'), getDocument({data:Uint8Array})→getPage→getTextContent→items.map(i=>i.str)
§
算法资源（D盘）：
- 题库：D:\文件\算法\ 算法题目.json（详细格式：{version,generatedAt,totalProblems,problemRange,problems:[{id,title,difficulty,category,problem,solutions,comparison,testCases,testResult,summary}]}）
- 书籍：D:\书籍\算法（4本PDF：BAT霜神/Java实现/LeetCode101/Go1470题解）
- 导入规则：跳过重复题目（按标题去重），ID从JSON中max+1递增，追加到文件末尾
- JSON生成方式：用Node.js解析MD文件（parse_md_to_json.js模式），不要在JS中硬编码中文数据（引号转义必崩）
§
出题时总结章节必须严格按五步结构化格式撰写：
1. **题目概述：** 一句话概括考什么
2. **解题方法：** 用什么方法、核心思路
3. **时间复杂度：** 最优解法的时间复杂度
4. **空间复杂度：** 最优解法的空间复杂度
5. **改进方向：** 局限性或可优化之处

每步用加粗标签开头，各占一段。不要用一段话笼统概括。