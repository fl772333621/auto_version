# 一、配置方法

## 1、需要备份的磁盘设置：
（必须）将source.auto_version文件放到需要备份的磁盘根目录下。
（可选）指定source.auto_version文件内容为sourcePath=dev4\bi-experiment-wrapper表示仅备份该目录；如果该文件内容为空表示整个磁盘都备份

## 2、备份到的磁盘设置：
（必须）将backup.auto_version文件放到备份到的磁盘根目录下

## 3、开始备份：
（必须）双击执行<开始同步.bat>文件，自动开始同步，同步日志会在该目录下生成。




# 二、说明

## 1、source.auto_version表示的是从哪里备份，backup.auto_version表示的是备份到哪里
建议从U盘备份，备份到电脑的硬盘

## 2、备份过程中的文件会遇到四种情况：
```
source无，backup有：表示删除了文件，该种情况backup内文件被重命名添加时间戳后缀
    同步前：source无pom.xml，backup有pom.xml
    同步后：source无pom.xml，backup有pom.xml.20191015_161706.auto_version
source有，backup无：表示新增了文件，该种情况backup内会新增文件
    同步前：source有pom.xml，source无pom.xml
    同步后：source有pom.xml，source有pom.xml
source有，backup有且文件完全相同：表示文件无任何修改，不做操作
    同步前后没有区别
source有，backup有且文件不相同：表示修改了文件，该种情况backup内的文件被重命名添加时间戳后缀，并拷贝source内该文件到backup内
    同步前：source有pom.xml，source有pom.xml，但是两个文件不一样
    同步后：source有pom.xml，source有pom.xml(该文件跟source的pom.xml完全一样)和pom.xml.20191015_161706.auto_version(该文件跟backup的原始pom.xml一样)
    ```
