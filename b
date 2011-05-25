#!/bin/sh

TOMCAT=/usr/share/tomcat7
WEBAPP=${TOMCAT}/webapps
WEB=WebContent
ROOT=${WEBAPP}/ROOT

if [ ! -d /tmp/bin ];then
    echo [ MD ] /tmp/bin
    mkdir -p /tmp/bin
fi

if [ ! -d bin ];then
    echo [ LN ] bin
    ln -s /tmp/bin bin
fi

if [ ! -d ${WEB}/WEB-INF/lib ];then
    echo [ LN ] lib
    ln -s `pwd`/lib ${WEB}/WEB-INF/lib
fi

if [ ! -d ${WEB}/WEB-INF/classes ];then
    echo [ LN ] classes
    ln -s /tmp/bin ${WEB}/WEB-INF/classes
fi 

if [ ! -d ${ROOT} ];then
    echo [ LN ] ${ROOT}
    ln -s `pwd`/${WEB} ${ROOT}
fi

#if [ ! -f ${ROOT}/index.html ];then
#    echo [ CP ] WebContent
#    cp -r ${WEB}/* ${ROOT}/
#fi

if [ ! -f ./bin/paoding-dic-home.properties ];then
    echo [ CP ] paoding ...
    cp ./config/* ./bin/
fi

if [ ! -d /tmp/weibogrep ];then
    echo [ MD ] /tmp/weibogrep
    mkdir -p /tmp/weibogrep
fi

echo [ ANT ] build
ant || exit

echo [ TOMCAT ] restart
/etc/rc.d/tomcat7 restart
