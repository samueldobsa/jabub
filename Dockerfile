FROM jabub-jdk-python-js-base

WORKDIR /jabub
COPY ./target/migration-0.0.1-SNAPSHOT.jar /jabub

#ENTRYPOINT ["/etc/nginx/docker-entrypoint.sh"]

CMD ["java", "-jar" ,"/jabub/migration-0.0.1-SNAPSHOT.jar"]
