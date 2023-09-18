FROM 083580179390.dkr.ecr.eu-west-1.amazonaws.com/aonsolutions/aon-gate:core

ADD dist/aon-solutions2 /var/www/html/web
RUN chown -R apache:apache /var/www/html
EXPOSE $PORT
CMD ["/usr/sbin/httpd", "-D", "FOREGROUND"]
