# **1.Puppeteer**

 是一个由 Google 开发的 **Node.js 库**，它提供了高级 API 来控制 Chrome 或 Chromium 浏览器，支持自动化执行各种浏览器操作。常被用于 **网页爬虫、自动化测试、生成截图 / PDF、UI 自动化** 等场景。

直接使用 npm install puppeteer 下载会报错 你遇到的 `npm error code 1` 是由 Puppeteer 安装过程中下载 Chrome 浏览器失败导致的

![image-20250707175632689](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20250707175632689.png)

设置下载puppeter不下载谷歌浏览器 

```bash
set PUPPETEER_SKIP_DOWNLOAD=true && npm install puppeteer
```

这样就可以下载成功

在使用时需要知道其Google的安装路径 一般是在这个目录下

C:\Program Files\Google\Chrome\Application

```
const fs = require('fs');
const puppeteer = require('puppeteer');

(async () => {
  try {
    // 指定本地Chrome路径（根据实际情况修改）
    const browser = await puppeteer.launch({
      executablePath: 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe',
      headless: false, // 非无头模式，便于调试
      args: ['--no-sandbox', '--disable-setuid-sandbox']
    });

    const page = await browser.newPage();
    const jsContent = fs.readFileSync('example.js', 'utf8');

    await page.setContent(`
      <html>
        <head>
          <style>
            pre {
              font-family: Consolas, monospace;
              font-size: 14px;
              line-height: 1.5;
              padding: 20px;
              white-space: pre-wrap;
              background-color: #f5f5f5;
              border-radius: 4px;
            }
          </style>
        </head>
        <body>
          <pre>${jsContent}</pre>
        </body>
      </html>
    `);

    await page.pdf({
      path: 'example.pdf',
      format: 'A4',
      printBackground: true,
      margin: {
        top: '20mm',
        right: '20mm',
        bottom: '20mm',
        left: '20mm'
      }
    });

    console.log('PDF生成成功！');
    await browser.close();
  } catch (error) {
    console.error('生成PDF时出错:', error);
  }
})();
```

![image-20250707181400000](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20250707181400000.png)

![image-20250707181420610](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20250707181420610.png)

合并某个文件件下所有的js文件

```
const fs = require('fs');
const path = require('path'); // 用于处理文件路径
const puppeteer = require('puppeteer');

(async () => {
  try {
    const jsFolderPath = './src'; // JS文件所在目录（根据实际情况修改）
    const outputPath = 'combined.js.pdf'; // 输出PDF文件名
    
    // 读取目录中的所有JS文件
    const jsFiles = fs.readdirSync(jsFolderPath)
      .filter(file => file.endsWith('.js'));
    
    if (jsFiles.length === 0) {
      throw new Error(`目录 ${jsFolderPath} 中未找到JS文件`);
    }
    
    // 合并所有JS文件内容
    let combinedContent = '';
    jsFiles.forEach(file => {
      const filePath = path.join(jsFolderPath, file);
      const content = fs.readFileSync(filePath, 'utf8');
      
      // 添加文件分隔标记
      combinedContent += `\n\n/* ===== ${file} ===== */\n\n`;
      combinedContent += content;
    });
    
    // 启动浏览器
    const browser = await puppeteer.launch({
      executablePath: 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe',
      headless: false,
      args: ['--no-sandbox', '--disable-setuid-sandbox']
    });
    
    const page = await browser.newPage();
    
    // 设置页面内容（添加语法高亮支持）
    await page.setContent(`
      <html>
        <head>
          <style>
            body { font-family: Arial, sans-serif; margin: 40px; }
            h1 { color: #333; }
            pre {
              font-family: Consolas, monospace;
              font-size: 14px;
              line-height: 1.5;
              padding: 20px;
              margin: 15px 0;
              white-space: pre-wrap;
              background-color: #f5f5f5;
              border-radius: 4px;
              overflow-x: auto;
            }
            .file-header {
              background-color: #e0e0e0;
              padding: 8px 15px;
              margin-bottom: 0;
              font-weight: bold;
              border-radius: 4px 4px 0 0;
            }
          </style>
        </head>
        <body>
          <h1>合并JS文件 - ${new Date().toLocaleDateString()}</h1>
          <pre>${combinedContent}</pre>
        </body>
      </html>
    `);
    
    // 生成PDF
    await page.pdf({
      path: outputPath,
      format: 'A4',
      printBackground: true,
      margin: {
        top: '20mm',
        right: '20mm',
        bottom: '20mm',
        left: '20mm'
      }
    });
    
    console.log(`成功合并 ${jsFiles.length} 个JS文件到 ${outputPath}`);
    await browser.close();
  } catch (error) {
    console.error('合并JS文件并生成PDF时出错:', error);
  }
})();
```

