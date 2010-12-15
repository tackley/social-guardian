import sbt._

class LiftProject(info: ProjectInfo) extends DefaultWebProject(info) {
  val liftVersion = "2.2-RC1"
//  val dispatchVersion = "0.7.8"

  val scalaSnapshots = ScalaToolsSnapshots

  val liftUtil = "net.liftweb" %% "lift-util" % liftVersion  withSources
  val liftCommon ="net.liftweb" %% "lift-common" % liftVersion withSources
  val liftWebkit ="net.liftweb" %% "lift-webkit" % liftVersion withSources
  val liftRecord = "net.liftweb" %% "lift-record" % liftVersion withSources
  val liftMongoRecord = "net.liftweb" %% "lift-mongodb-record" % liftVersion withSources

  val liftTestkit = "net.liftweb" %% "lift-testkit" % liftVersion

//  val dispatchTwitter = "net.databinder" %% "dispatch-twitter" % dispatchVersion withSources

  val jettyLib = "org.mortbay.jetty" % "jetty" % "6.1.22" % "test"
  val junit = "org.scalatest" % "scalatest" % "1.2" % "test"
}
