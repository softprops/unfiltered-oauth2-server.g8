organization := "com.example"

name := "$name$"

version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "net.databinder" %% "unfiltered-oauth2" % "$unfiltered_version$",
  "net.databinder" %% "unfiltered-json" % "$unfiltered_version$"
)
