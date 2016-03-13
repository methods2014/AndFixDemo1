cmd build
F:\hotfix\apkpatch-1.0.3>apkpatch.bat -f new-release.apk -t old-release.apk -o output1 -k methodTest.jks -p methodTest -a methodTest -e methodTest
apkpatch.bat -f new.apk -t old.apk -o fix -k methodTest.jks -p methodTest -a methodTest -e methodTest



1.先打包apk(old.apk) ：用签名方式， 安装到手机中，展示bug
2.build out.apatch：找到原来old.apk 然后将bug修改掉 用签名方式打包new.apk. 用上面的命令生成apkpatch.bat out.apatch差异文件
3.传到文件系统中：将第一步的应用进程杀死，然后用device monitor将 out.apatch文件传到 /storage/sdcard0/out.apatch 这个地址
4.重启应用，将bug修复掉了。

只支持方法修改，不支持增加类和属性

usage: apkpatch -f <new> -t <old> -o <output> -k <keystore> -p <***> -a <alias> -e <***>
 -a,--alias <alias>     keystore entry alias.
 -e,--epassword <***>   keystore entry password.
 -f,--from <loc>        new Apk file path.
 -k,--keystore <loc>    keystore path.
 -n,--name <name>       patch name.
 -o,--out <dir>         output dir.
 -p,--kpassword <***>   keystore password.
 -t,--to <loc>          old Apk file path.


1.接口形如：{"code":"0","version":"5.8","fixUrl":"https://raw.githubusercontent.com/methods2014/m1/master/out.apatch"}
2.要求下载文件的后缀是*.apatch，在程序里做了匹配。
3.在更新补丁文件时，要同时将版本号改变了version