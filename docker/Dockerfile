FROM registry.hr:5000/jre-alpine:3.7
MAINTAINER zhanghr <say_hello_plz@qq.com>

ADD @project.build.finalName@.jar /usr/nmghr/@project.build.finalName@.jar
ADD run.sh /usr/nmghr/run.sh
RUN chmod +x /usr/nmghr/run.sh
CMD ["/usr/nmghr/run.sh"]