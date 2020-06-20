jenkins.model.Jenkins.theInstance.getProjects().each { job ->
    if (!job.name.contains('bootstrap') && !job.name.contains('Jenkins')) {
        job.delete()
    }
}

job('job-dsl-checkout') {
    
    scm {
        github('smiles7v/hello-world-war', 'master')
    }
      
   publishers {
        downstream 'job-dsl-compile', 'SUCCESS'
    }
    
}

mavenJob('job-dsl-compile'){
   
  customWorkspace('/var/lib/jenkins/workspace/job-dsl-checkout')
  mavenInstallation('Maven 3.3.9')
  goals('compile')
    
  publishers {
        downstream 'job-dsl-package', 'SUCCESS'
   }
}

mavenJob('job-dsl-package'){
    customWorkspace('/var/lib/jenkins/workspace/job-dsl-checkout')
    mavenInstallation('Maven 3.3.9')
    goals('package')
    
  publishers {
        downstream 'job-dsl-deploy', 'SUCCESS'
  }
}

job('job-dsl-deploy') {
    description 'Deploy app to the demo server'
    
    steps{
             shell 'sshpass -p "123456" scp /var/lib/jenkins/workspace/job-dsl-checkout/target/hello-world-war-1.0.0.war /temp'
      }
}

listView('List View DSLs') {
    jobs {
        regex('job-dsl-.+')
    }
    columns {
        status()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
}

