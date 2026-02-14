# 二维码工具

一个功能强大的二维码工具，支持二维码识别和生成功能。

## 功能特性

- **二维码识别**
  - 支持鼠标拖选屏幕区域识别二维码
  - 支持多屏幕选择区域
  - 自动将识别结果复制到剪贴板
  - 支持Windows和Mac系统

- **二维码生成**
  - 输入内容自动实时生成二维码
  - 支持UTF-8编码
  - 实时预览二维码效果
  - 支持保存二维码为PNG图片

## 技术栈

- **开发语言**: Java 8
- **构建工具**: Maven
- **GUI框架**: Swing
- **二维码库**: ZXing 3.5.0
  - core: 二维码核心功能
  - javase: Java SE平台支持

## 项目结构

```
tools/
├── src/main/java/com/tools/
│   └── MainUI.java              # 主界面
├── qrcode-reader/                 # 二维码识别模块
│   └── src/main/java/com/qrtools/
│       ├── QRCodeReader.java       # 屏幕捕获和区域选择
│       └── QRCodeDecoder.java     # 二维码解码
├── pom.xml                      # 根项目POM
├── qrcode-reader/pom.xml         # 二维码识别模块POM
└── target/qrcode-tools.jar       # 可执行JAR包
```

## 构建说明

### 前置要求

- JDK 8 或更高版本
- Maven 3.6 或更高版本

### 构建步骤

1. 克隆或下载项目到本地
2. 在项目根目录执行构建命令：

```bash
mvn clean package
```

3. 构建完成后，可执行JAR包位于 `target/qrcode-tools.jar`

## 使用说明

### 运行程序

```bash
java -jar target/qrcode-tools.jar
```

### 识别二维码

1. 启动程序后，点击"识别二维码"按钮
2. 在屏幕上用鼠标拖选要识别的区域
3. 松开鼠标后自动识别二维码
4. 识别结果会自动复制到剪贴板

### 生成二维码

1. 启动程序后，点击"生成二维码"按钮
2. 在左侧输入框中输入要生成的内容
3. 右侧会实时显示生成的二维码
4. 点击"保存二维码"按钮可将二维码保存为PNG文件

## 依赖说明

### Maven依赖

```xml
<dependencies>
    <!-- 二维码识别模块 -->
    <dependency>
        <groupId>com.tools</groupId>
        <artifactId>qrcode-reader</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    
    <!-- ZXing核心库 -->
    <dependency>
        <groupId>com.google.zxing</groupId>
        <artifactId>core</artifactId>
        <version>3.5.0</version>
    </dependency>
    
    <!-- ZXing Java SE支持 -->
    <dependency>
        <groupId>com.google.zxing</groupId>
        <artifactId>javase</artifactId>
        <version>3.5.0</version>
    </dependency>
</dependencies>
```

## 构建插件

项目使用以下Maven插件：

- `maven-compiler-plugin`: 编译Java代码
- `maven-jar-plugin`: 配置为跳过基础JAR创建
- `maven-assembly-plugin`: 创建包含所有依赖的可执行JAR

## 系统要求

- **操作系统**: Windows 7+, macOS 10.12+
- **Java版本**: JDK 8 或更高版本
- **内存**: 最低256MB RAM
- **磁盘空间**: 约50MB

## 许可证

本项目仅供学习和个人使用。

## 贡献

欢迎提交Issue和Pull Request来改进项目。

## 联系方式

如有问题或建议，请通过Issue联系。