# PasteImageToMarkdown

## 版本更新记录
### v1.6.1
1. 支持对当前文档的相对路径
2. 本地存储文件夹的名称支持${filename}占位符

### v1.5.8
1. 添加对腾讯云对象存储的支持

### v1.5.6
1. 修复windows下复制磁盘上图片的bug
2. 支持复制网页上的图片

### v1.5.5
1. 修复windows下无法正常工作的bug
2. 添加对阿里云对象存储的支持


### v1.5.4
1. 修复粘贴磁盘gif图片的bug
2. 支持一次性粘贴多张磁盘图片


### v1.5.3
1. 支持把图片保存到七牛云
2. 支持图片保存到工程中

## 介绍
- 把粘贴板的图片插入到markdown文件中。
- 可以选择保存图片到：<br>
    1. 本地保存<br>
    2. 七牛云<br>
    3. 阿里云<br>
    4. 腾讯云(正在开发中)<br>
    
- 可以支持：<br>
    1. 一次性粘贴多张磁盘图片（会自动过滤非图片文件）<br>
    2. 支持粘贴gif图片<br>
    3. 其他方式保存到粘贴板的图片<br><br>  
          
- 下载安装
    1. 可以直接在jetbrains插件仓库搜索 PasteImageIntoMarkdown 安装。
    2. 由于jetbrains对插件发布有延迟，通过jetbrains插件仓库下载到的未必是最新版，
    最新版可以在这里下载：[最新版下载][https://github.com/rocky-peng/PasteImageToMarkdown/releases/latest]
    
    安装完成后，请前往Setting->Other Settings->PasteImageIntoMarkdown填写七牛相关信息。<br><br>
    如若使用出现问题，欢迎发送邮件到 rocky.peng@qq.com 或者前往github提交issue。<br><br>
    github: <a href="https://github.com/rocky-peng/PasteImageToMarkdown">https://github.com/rocky-peng/PasteImageToMarkdown</a>
  
### 本地存储
填写的路径是相对于工程目录
![](http://cdn.justdopay.com/pasteimageintomarkdown/2022-11-16/96496789716700.png)


### 七牛云存储
![](http://cdn.justdopay.com/pasteimageintomarkdown/2022-11-16/96532398067000.png)

### 阿里云存储
![](http://cdn.justdopay.com/pasteimageintomarkdown/2022-11-16/96554701270300.png)

### 腾讯云存储
![](http://cdn.justdopay.com/pasteimageintomarkdown/2022-11-16/96615298448400.png)


[https://github.com/rocky-peng/PasteImageToMarkdown/releases/latest]: https://github.com/rocky-peng/PasteImageToMarkdown/releases/latest
