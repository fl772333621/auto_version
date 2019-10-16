echo "同步source的文件到backup"
echo "按任意键开始操作，取消操作请关闭该窗口！"
pause
echo "再次按任意键确认开始操作，取消操作请关闭该窗口！"
pause
set h=%time:~0,2%
set h=%h: =0%
set filename=%date:~0,4%%date:~5,2%%date:~8,2%_%h%%time:~3,2%%time:~6,2%.log
java -cp .\auto_version.jar com.mfanw.auto_version.AutoVersion > %filename%
echo "操作完成"
pause