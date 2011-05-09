import sbt._

class MyProject(info: ProjectInfo) extends DefaultProject(info) {
  val ufversion = "0.3.3-SNAPSHOT"
  val ufoa2 = "net.databinder" %% "unfiltered-oauth2" % ufversion
  val ufjson = "net.databinder" %% "unfiltered-json" % ufversion
}
