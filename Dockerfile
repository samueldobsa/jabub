FROM jabub-jdk-python-js-base

WORKDIR /jabub

ENTRYPOINT ["/etc/nginx/docker-entrypoint.sh"]
