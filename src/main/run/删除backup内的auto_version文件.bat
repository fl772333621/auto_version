echo "ɾ��backup�ļ����ڵ�ȫ����auto_version�ļ�"
echo "���������ʼ������ȡ��������رոô��ڣ�"
pause
echo "�ٴΰ������ȷ�Ͽ�ʼ������ȡ��������رոô��ڣ�"
pause
set h=%time:~0,2%
set h=%h: =0%
set filename=%date:~0,4%%date:~5,2%%date:~8,2%_%h%%time:~3,2%%time:~6,2%.log
java -cp .\auto_version.jar com.mfanw.auto_version.DeleteAutoVersionFile > %filename%
echo "�������"
pause