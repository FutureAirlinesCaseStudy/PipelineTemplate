package org.fa

def javaShortLivedBranch(pomFile, settingsFile, sonarUrl, sourceBranch, targetBranch, version) {
    sh """
    mvn sonar:sonar -f ${pomFile} -s ${settingsFile} \
    -Dsonar.host.url=${sonarUrl} \
    -Dsonar.branch.name=${sourceBranch} \
    -Dsonar.branch.target=${targetBranch} \
    -Dsonar.projectVersion=${version}
    """
}

def javaLongLivedBranch(pomFile, settingsFile, sonarUrl, sourceBranch, version) {
    sh """
    mvn sonar:sonar -f ${pomFile} -s ${settingsFile} \
    -Dsonar.host.url=${sonarUrl} \
    -Dsonar.branch.name=${sourceBranch} \
	-Dsonar.projectVersion=${version}    
    """
}

def javaAutoBranchScan(pomFile, settingsFile, sonarUrl, sourceBranch, targetBranch, version){
    try{
        javaShortLivedBranch(pomFile, settingsFile, sonarUrl, sourceBranch, targetBranch, version)
    } catch(Exception ex) {
        println "WARNING: Falling back to single branch analysis. Source to target branch analysis failed."
        javaLongLivedBranch(pomFile, settingsFile, sonarUrl, sourceBranch, version)
    }
}


return this
