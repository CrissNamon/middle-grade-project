pipeline {
    agent any
    tools {
      gradle '8.11.1'
    }
    stages {
		    stage ("build") {
        	  steps {
                sh 'gradle test build'
            }
        }
    }
}