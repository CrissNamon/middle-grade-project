pipeline {
    agent any
    tools {
      gradle '8.11.1'
    }
    stages {
		    stage ("build") {
        	  steps {
                sh 'gradle test jar'
            }
        }
        stage ("build docker image") {
            steps {
                sh 'docker build -t kpekepsalt:middle-grade-project .'
            }
        }
        stage ("publish") {
            steps {
                withDockerRegistry(credentialsId: 'docker-hub') {
                     sh 'docker push kpekepsalt:middle-grade-project:latest'
                }
            }
        }
    }
}