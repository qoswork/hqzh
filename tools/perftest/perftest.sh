#!/bin/sh

HQDIR=../..
LIBDIR=$HQDIR/thirdparty/lib
LIBS=$LIBDIR/groovy-all-1.0.jar
LIBS="$LIBS:$LIBDIR/commons-cli-1.1.jar"
LIBS="$LIBS:$LIBDIR/commons-logging-1.0.4.jar"
LIBS="$LIBS:$LIBDIR/commons-lang-2.3.jar"
LIBS="$LIBS:$LIBDIR/commons-codec-1.3.jar"
LIBS="$LIBS:$LIBDIR/xslt/xercesImpl-2.6.2.jar"
LIBS="$LIBS:$LIBDIR/backport-util-concurrent.jar"
LIBS="$LIBS:lib/commons-httpclient-3.0.1.jar"
LIBS="$LIBS:lib/htmlunit-1.11.jar"
LIBS="$LIBS:lib/commons-io-1.3.2.jar"
LIBS="$LIBS:lib/commons-collections.jar"
LIBS="$LIBS:lib/commons-cli-1.1.jar"
LIBS="$LIBS:lib/jaxen-1.1.1.jar"
LIBS="$LIBS:lib/js.jar"
LIBS="$LIBS:lib/nekohtml.jar"
LIBS="$LIBS:$HQDIR/build/classes"
LIBS="$LIBS:src"

JAVA="${JAVA_HOME}/bin/java"
JAVA_OPTS=-Xmx1024m
CLASS=groovy.lang.GroovyShell

if [ $# != 2 ] ; then
   echo "Syntax: $0 <testFile.groovy> <config.props>"
   exit 1
fi

java $JAVA_OPTS -classpath $LIBS \
     -Dcom.gargoylesoftware.htmlunit=DEBUG \
     -Dperftest.script="$1" \
     -Dperftest.propfile="$2" \
     $CLASS \
     src/org/hyperic/perftest/PerfTest.groovy 2>&1 \
 | grep -v isRedirectNeeded | grep -v followRedirects
     
