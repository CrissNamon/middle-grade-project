pipeline {
    agent any
    tools {
      gradle '8.11.1'
    }
    def dockerImage
    stages {
		    stage ("build") {
        	  steps {
                sh 'gradle test jar'
            }
        }
        stage ("build docker image") {
            steps {
                dockerImage = docker.build("kpekepsalt/middle-grade-project")
            }
        }
        stage ("publish") {
            steps {
                docker.withRegistry('https://registry.hub.docker.com', 'docker-hub') {
                     dockerImage.push("latest")
                }
            }
        }
    }
}