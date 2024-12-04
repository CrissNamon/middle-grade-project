FROM jenkins/jenkins:lts-jdk21
USER root
RUN apt update && curl -fsSL https://get.docker.com | sh
RUN usermod -aG docker jenkins
RUN chown jenkins:docker /var/run/docker.sock
USER jenkins
