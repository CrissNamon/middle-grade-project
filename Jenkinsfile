pipeline {
    agent any
    tools {
      gradle '8.11.1'
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
                dockerImage = docker.build 'kpekepsalt:middle-grade-project'
            }
        }
        stage ("publish") {
            steps {
                docker.withRegistry('', 'docker-hub') {
                     dockerImage.push()
                }
            }
        }
    }
}