package org.fa

def standard(def repository, def branch, def sshCredentials, timeout=120) {
    checkout([
        $class: 'GitSCM',
        branches: [[name: "${branch}"]],
        doGenerateSubmoduleConfigurations: false,
        extensions: [
            [$class: 'CheckoutOption', timeout: timeout],
            [$class: 'CloneOption', depth: 0, noTags: true, reference: '', shallow: true, timeout: timeout],
            [$class: 'SubmoduleOption', parentCredentials: true],
            [$class: 'LocalBranch', localBranch: "${branch}"],
            [$class: 'CleanBeforeCheckout']
        ],
        submoduleCfg: [],
        userRemoteConfigs: [[
            credentialsId: "${sshCredentials}",
            url: "${repository}"
        ]]
    ])

    def commit_hash = sh(
        returnStdout: true,
        script: 'git rev-parse HEAD'
    )
    return [
        commitHash: commit_hash
    ]
}


return this