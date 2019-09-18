#!/bin/sh
# docker memory restriction(byte)
LIMIT_IN_BYTES=$(cat /sys/fs/cgroup/memory/memory.limit_in_bytes)
# reserved memory size(m)
RESERVED_MEGABYTES=64
# default heap memory size(m)
HEAP_SIE=256

# If not default limit_in_bytes in cgroup
if [ "$LIMIT_IN_BYTES" -ne "9223372036854771712" ]
then
    limit_in_megabytes=$(expr $LIMIT_IN_BYTES \/ 1048576)
    HEAP_SIE=$(expr $limit_in_megabytes - $RESERVED_MEGABYTES)
fi

# MetaspaceSize = $HEAP_SIE/8
METASPACESIZE=$(expr $HEAP_SIE \/ 8)
# MaxMetaspaceSize = $HEAP_SIE/4
MAXMETASPACESIZE=$(expr $HEAP_SIE \/ 4)

#===========================================================================================
# JVM Configuration
#===========================================================================================
JAVA_OPTS="-server -Xms${HEAP_SIE}m -Xmx${HEAP_SIE}m"
JAVA_OPTS="${JAVA_OPTS} -XX:MetaspaceSize=${METASPACESIZE}m -XX:MaxMetaspaceSize=${MAXMETASPACESIZE}m"
JAVA_OPTS="${JAVA_OPTS} -XX:+UseParallelGC -XX:SurvivorRatio=8  -XX:-UseParNewGC"
JAVA_OPTS="${JAVA_OPTS} -verbose:gc -Xloggc:/usr/on36/gc.log -XX:+PrintGCDetails"
JAVA_OPTS="${JAVA_OPTS} -XX:-OmitStackTraceInFastThrow"
JAVA_OPTS="${JAVA_OPTS} -XX:-UseLargePages"
#JAVA_OPTS="${JAVA_OPTS} -Xdebug -Xrunjdwp:transport=dt_socket,address=9555,server=y,suspend=n"

echo JAVA_OPTS=$JAVA_OPTS
java $JAVA_OPTS -jar /usr/nmghr/@project.build.finalName@.jar