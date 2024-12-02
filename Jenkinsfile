pipeline {
    agent any
    tools {
      gradle '8.11.1'
      'org.jenkinsci.plugins.docker.commons.tools.DockerTool' 'docker'
    }
    environment {
      dockerImage = ''
    }
    stages {
		    stage ("build") {
        	  steps {
                sh 'gradle test jar'
            }
        }
        stage ("build docker image") {
            steps {
                script {
                    docker.withTool('docker') {
                        dockerImage = docker.build("kpekepsalt/middle-grade-project")
                    }
                }
            }
        }
        stage ("publish") {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'docker-hub') {
                        dockerImage.push("latest")
                    }
                }
            }
        }
    }
}