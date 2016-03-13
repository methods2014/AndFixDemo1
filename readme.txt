andFix demo
根据andfix热补丁，增加了在线下载补丁并更新的功能。
1.每次启动在application中判断是否有补丁以及补丁的版本号。
2.支持多次补丁，每次删除之前的，使用最新的补丁。
3.服务端需要一个接口和一个下载补丁文件的地址.
    接口返回：{"code":"0","version":"5.8","fixUrl":"https://raw.githubusercontent.com/methods2014/m1/master/out.apatch"}
    version为版本号，每次新增补丁都要更新此字段。
    fixUrl为补丁下载地址。
    code为服务端返回的错误码。暂未对此字段处理。
4.github是可以作为测试服务器的，就是有缓存，需要在生成的文件url后面加一个时间戳。
5.里边使用了android本身带的json解析和Http请求。没有导入其他jar包。
6.andfix原始链接和git地址
  http://blog.csdn.net/qxs965266509/article/details/49802429
  原理解析
  http://blog.csdn.net/qxs965266509/article/details/49816007
  git地址
  https://github.com/alibaba/AndFix



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