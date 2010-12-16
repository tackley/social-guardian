import sbt._

class LiftProject(info: ProjectInfo) extends DefaultWebProject(info) {
  val liftVersion = "2.2-RC1"

  val scalaSnapshots = ScalaToolsSnapshots
  val guardianGithub = "Guardian Github Releases" at "http://guardian.github.com/maven/repo-releases"
  val guardianGithubSnapshots = "Guardian Github Snapshots" at "http://guardian.github.com/maven/repo-snapshots"
  
  val liftUtil = "net.liftweb" %% "lift-util" % liftVersion  withSources
  val liftCommon ="net.liftweb" %% "lift-common" % liftVersion withSources
  val liftWebkit ="net.liftweb" %% "lift-webkit" % liftVersion withSources
  val liftRecord = "net.liftweb" %% "lift-record" % liftVersion withSources
  val liftMongoRecord = "net.liftweb" %% "lift-mongodb-record" % liftVersion withSources

  val contentApiClient = "com.gu.openplatform" %% "content-api-client" % "1.10-SNAPSHOT" withSources()

  val liftTestkit = "net.liftweb" %% "lift-testkit" % liftVersion

  val jettyLib = "org.mortbay.jetty" % "jetty" % "6.1.22" % "test"
  val junit = "org.scalatest" % "scalatest" % "1.2" % "test"
}
