第一步：需要备份的磁盘设置：
1、将source.auto_version文件放到需要备份的磁盘根目录下
2、（可选）指定source.auto_version文件内容为sourcePath=dev4\bi-experiment-wrapper表示仅备份该目录；如果该文件内容为空表示整个磁盘都备份


第二步：备份到的磁盘设置：
1、将backup.auto_version文件放到备份到的磁盘根目录下


第三步：双击执行<开始执行.bat>文件，自动开始同步，同步日志会在该目录下生成。