package org.cris
/**
 * Maven Build
 */

def standardBuildUnitTest(pomFile, settingsFile) {
    sh(
        label: "Standard Build and Unit Test",
        script: """
            mvn clean install -f ${pomFile} -s ${settingsFile} -Dmaven.test.failure.ignore -Dmaven.test.skip=false            
        """ 
    )
}

return this
