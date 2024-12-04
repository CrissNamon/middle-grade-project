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
                sh 'gradle test shadowJar'
            }
        }
        stage ("build docker image") {
            steps {
                script {
                    dockerImage = docker.build("kpekepsalt/middle-grade-project")
                }
            }
        }
        stage ("publish") {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'docker-hub') {
                        dockerImage.push("latest")
                    }
                }
            }
        }
        stage ("deploy") {
            steps {
                script {
                    sh """curl --location --request PATCH 'http://host.docker.internal:9991/apis/apps/v1/namespaces/default/deployments/app' \
                          --header 'Content-Type: application/merge-patch+json' \
                          --data '{
                              "spec": {
                                  "template": {
                                      "metadata": {
                                          "annotations": {
                                              "build.number": "${currentBuild.number}"
                                          }
                                      }
                                  }
                              }
                          }'"""
                }
            }
        }
    }
}
