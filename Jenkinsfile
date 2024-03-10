pipeline {
    agent any
    tools {
        maven 'Maven 3.6.3'
        jdk 'jdk11'
    }
    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                    git submodule update --init --recursive
                '''
            }
        }

        stage ('Build') {
            steps {
                sh 'mvn -Dmaven.test.failure.ignore=true clean install'
                archiveArtifacts artifacts: '**/target/ResourcePackConverter-*.jar', excludes: '**/target/original-*.jar', fingerprint: true, followSymlinks: false, onlyIfSuccessful: true
            }
        }
    }
}
