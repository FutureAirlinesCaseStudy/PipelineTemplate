import org.fa.*
import static groovy.json.JsonOutput.*

def call(Map config) {
    println prettyPrint(toJson(config))
    def checkout = new gitCheckout()
    def mvn = new mvnHelpers()
    def sonar = new sonarScanner()
    buildNumber = "${BUILD_NUMBER}"   

    pipeline {
        agent {
	label "docker"
	}
        options {
            timeout(time: config.timeout ?: 90 , unit: 'MINUTES')
            buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '5'))
        }
        
        stages {
            stage('Checkout') {
                steps {
                    script {
  		            def gitDetails = checkout.standard(
                            "${config.git_repo}",
                            "${config.branch}",
                            "${config.git_checkout_credentials}"
                        )
                        currentBuild.description = gitDetails.description
                        commit = gitDetails.commitHash
                    }
                }
            }
			

            stage('Build & UnitTest') {
                options {
                    timeout(time: 60, unit: 'MINUTES')
                }
                    steps{
                        script{
                            mvn.standardBuildUnitTest( 
				"${config.pom_file}",
                                "${config.settings_file}"
				)
                        }
                    }                   
            }
                stage('Sonar') {                    
                    stages {
                        stage ('Sonar Analysis') {
                        when {
                            expression { return !config.skip_sonar_analysis }
                             }                              
                            options {
                                timeout(time: 30, unit: 'MINUTES')
                            }
                                steps{
                                    script{
                                        withSonarQubeEnv("${config.sonar_inst}"){
                                            sonar.javaLongLivedBranch(
                                            "${config.pom_file}",
                                            "${config.settings_file}",											
                                            "${config.sonar_url}",
                                            "${config.branch}",
                                              commit
                                            )
                                        }
                                    }
                                }
                            }   
                        stage('Quality Check') {
                            when {
                                expression { return !config.skip_quality_gate }
                                }                             
                            options {
                                timeout(time: 30, unit: 'MINUTES')
                            }
                                steps{
                                    sleep(60)
                                    waitForQualityGate abortPipeline: true
                                }
                            }
                        }
                    }

                stage('Docker Image Build & Push') {
                    steps{
                        script{
				sh 'mvn dockerfile:build' 
                            }
                        }
                    }
                }
            }                    
        }
