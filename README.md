
# PM Stats Exporter

The PM Stats Exporter Service is periodically exporting the
calculated KPIs to Kafka which are produced by the PM Stats Calculator.

## Contact Information

Team KODIAK are currently the acting development team working on the PM Stats
Exporter service. For support please contact
[Zsengeller Szabolcs](mailto:zsengeller.szabolcs@ericsson.com) (PO),
[István Albert Oláh](mailto:istvan.albert.olah@ericsson.com) (SM) and
[Gergő Molnár](mailto:gergo.molnar@ericsson.com) (Service Owner).

### Email

Guardians for this project can be reached at
[Team KODIAK](mailto:PDLEEASWAT@pdl.internal.ericsson.com)

#### Logging Functionality
The PM Stats Exporter Service is using EO Common Logging Library for Logging Functionality.
Following files has been modified to setup this library: pom.xml,values.yaml,deployment.yaml and _helpers.tpl.
Contact Team Green Pass for any support regarding EO Common Logging Library.

Maintainer: <PDLEAMGREE@pdl.internal.ericsson.com>

EO Common Logging Library documentation: https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/ESO/EO+Common+Logging+Library

## Maven Dependencies

The application dependencies are listed in the relevant `pom.xml` file.

## Build related artifacts

The main build tool is BOB provided by ADP. For convenience, maven wrapper is
provided to allow the developer to build in an isolated workstation that does
not have access to ADP.

  - [ruleset2.0.yaml](ruleset2.0.yaml) - for more details on BOB please see
[Bob 2.0 User Guide][3]
.
     You can also see an example of Bob usage in a Maven project in
[BOB][2]
.
  - [precoderview.Jenkinsfile](precodereview.Jenkinsfile) - for pre code review
Jenkins pipeline that runs when patch set is pushed.
  - [publish.Jenkinsfile](publish.Jenkinsfile) - for publish Jenkins pipeline
that runs after patch set is merged to master.
  - [.bob.env](.bob.env) - if you are running Bob for the first time this file
will not be available on your machine.
    For more details on how to set it up please see [Bob 2.0 User Guide][3].

If the developer wishes to manually build the application in the local
workstation, the `bob clean init-dev build image package-local`
command can be used once BOB is configured in the workstation.
Note: The `mvn clean install` command will be required before running the
bob command above.
See the "Containerization and Deployment to Kubernetes cluster" section for more
details on deploying the built application.

Stub jar files are necessary to allow contract tests to run. The stub jars are
stored in JFrog (Artifactory). To allow the contract test to access and retrieve
the stub jars, the .bob.env file must be configured as follows.

```bash
SELI_ARTIFACTORY_REPO_USER=<LAN user id>
SELI_ARTIFACTORY_REPO_PASS=<JFrog encripted LAN PWD or API key>
HOME=<path containing .m2, e.g. /c/Users/<user>/>
```

To retrieve an encrypted LAN password or API key, login to
[JFrog][9] and select "Edit Profile".
For info in setting the .bob.env file see
[Bob 2.0 User Guide][3].

## Containerization and Deployment to Kubernetes cluster

Following artifacts contains information related to building a container and
enabling deployment to a Kubernetes cluster:

- [charts](charts/) folder - used by BOB to lint, package and upload helm chart
to helm repository.
  -  Once the project is built in the local workstation using the
`bob clean init-dev build image package-local`
command, a packaged helm chart is available in the folder
`.bob/eric-oss-pm-stats-exporter-internal/`folder. This chart can be
manually installed in Kubernetes using `bash helm install`
command. [P.S. required only for Manual deployment from local workstation]

## Setting up CI Pipeline

-  Docker Registry is used to store and pull Docker images. At Ericsson official
chart repository is maintained at the org-level JFrog Artifactory.
   Follow the link to set up a
   [Docker registry][4].
-  Helm repo is a location where packaged charts can be stored and shared. The
official chart repository is maintained at the org-level JFrog Artifactory.
   Follow the link to set up a [Helm repo][5].
-  Follow instructions at [Jenkins Pipeline setup][8] to use out-of-box
Jenkinsfiles which comes along with eric-oss-pm-stats-exporter.
-  Jenkins Setup involves master and agent machines. If there is not any Jenkins
master setup, follow instructions at [Jenkins Master][6] -- 2.89.2 (FEM
Jenkins).
-  Request a node from the GIC (Note: RHEL 7 GridEngine Nodes have been
successfully tested).
   [Request Node][10].
-  To setup [Jenkins Agent][7] for Jenkins, jobs execution follow the
instructions at Jenkins Agent Setup.
-  The provided ruleset is designed to work in standard environments, but in
case you need, you can fine tune the automatically generated ruleset to adapt
to your project needs. Take a look at [Bob 2.0 User Guide][3] for details about
ruleset configuration.

## Using the Helm Repo API Token

The Helm Repo API Token is usually set using credentials on a given Jenkins FEM.
If the project you are developing is part of IDUN/Aeonic this will be
pre-configured for you. However, if you are developing an independent project
please refer to the 'Helm Repo' section:
[Chassis CI Pipeline Start Guide](https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/PCNCG/Microservice+Chassis+CI+Pipeline+Start+Guide#MicroserviceChassisCIPipelineStartGuide-HelmRepo)

Once the Helm Repo API Token is made available via the Jenkins job credentials
the precodereview and publish Jenkins jobs will accept the credentials (ex.
HELM_SELI_REPO_API_TOKEN' or 'HELM_SERO_REPO_API_TOKEN) and create a variable
HELM_REPO_API_TOKEN which is then used by the other files.

Credentials refers to a user or a functional user. This user may have access to
multiple Helm repos. In the event where you want to change to a different Helm
repo, that requires a different access rights, you will need to update the set
credentials.

## Artifactory Set-up Explanation

Artifactory repos (dev, ci-internal and drop) are set up following the ADP
principles:
[ADP Repository Principles](https://confluence.lmera.ericsson.se/pages/viewpage.action?spaceKey=AA&title=2+Repositories)

The commands: "bob init-dev build image package" will ensure that you are
pushing a Docker image to: [Docker registry - Dev](https://arm.seli.gic.ericsson.se/artifactory/docker-v2-global-local/proj-eric-oss-dev/)

The Precodereview Jenkins job pushes a Docker image to:
[Docker registry - CI Internal](https://arm.seli.gic.ericsson.se/artifactory/docker-v2-global-local/proj-eric-oss-ci-internal/)

This is intended behaviour which mimics the behavior of the Publish Jenkins
job. This job presents what will happen when the real microservice image is
being pushed to the drop repository. Furthermore, the 'Helm Install' stage
needs a Docker image which has been previously uploaded to a remote repository,
hence why making a push to the CI Internal is necessary.

The Publish job also pushes to the CI-Internal repository, however the Publish
stage promotes the Docker image and Helm chart to the drop repo:
[Docker registry - Drop](https://arm.seli.gic.ericsson.se/artifactory/docker-v2-global-local/proj-eric-oss-drop/)

Similarly, the Helm chart is being pushed to three separate repositories:
[Helm registry - Dev](https://arm.seli.gic.ericsson.se/artifactory/proj-eric-oss-dev-helm/)

The Precodereview Jenkins job pushes the Helm chart to:
[Helm registry - CI Internal](https://arm.seli.gic.ericsson.se/artifactory/proj-eric-oss-ci-internal-helm/)

This is intended behaviour which mimics the behavior of the Publish Jenkins job.
This job presents what will happen when the real Helm chart is being pushed to
the drop repository.
The Publish Jenkins job pushes the Helm chart to:
[Helm registry - Drop](https://arm.seli.gic.ericsson.se/artifactory/proj-eric-oss-drop-helm/)

## Links

[Gerrit Repos][1]
[BOB][2]
[Bob 2.0 User Guide][3]
[Docker registry][4]
[Helm Repo][5]
[Jenkins Master][6]
[Jenkins Agent][7]
[Jenkins Pipeline setup][8]
[JFrog][9]
[Request Node][10]
[EO Common Logging][11]
[SLF4J][12]

[1]: https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/PCNCG/Design+and+Development+Environment (Gerrit Repos)
[2]: https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/PCNCG/Adopting+BOB+Into+the+MVP+Project (BOB)
[3]: https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/adp-cicd/bob/+/refs/heads/master/USER_GUIDE_2.0.md (Bob 2.0 User Guide)
[4]: https://confluence.lmera.ericsson.se/pages/viewpage.action?spaceKey=ACD&title=How+to+create+new+docker+repository+in+ARM+artifactory (Docker registry)
[5]: https://confluence.lmera.ericsson.se/display/ACD/How+to+setup+Helm+repositories+for+ADP+e2e+CICD (Helm Repo)
[6]: https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/PCNCG/Microservice+Chassis+CI+Pipeline+Start+Guide#MicroserviceChassisCIPipelineStartGuide-JenkinsMaster-2.89.2(FEMJenkins) (Jenkins Master)
[7]: https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/PCNCG/Microservice+Chassis+CI+Pipeline+Start+Guide#MicroserviceChassisCIPipelineStartGuide-Prerequisites (Jenkins Agent)
[8]: https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/PCNCG/Microservice+Chassis+CI+Pipeline+Start+Guide#MicroserviceChassisCIPipelineStartGuide-JenkinsPipelinesetup (Jenkins Pipeline setup)
[9]: https://arm.seli.gic.ericsson.se (JFrog)
[10]: https://estart.internal.ericsson.com/ (Request Node)
[11]: https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/ESO/EO+Common+Logging+Library (EO Common Logging)
[12]: https://logging.apache.org/log4j/2.x/log4j-slf4j-impl/index.html (SLF4J)
