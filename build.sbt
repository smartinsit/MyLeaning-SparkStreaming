import AssemblyKeys._

organization := "com.paypal.gps"
version := "1.0"
scalaVersion := "2.10.4"
ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}
resolvers += "PayPal Nexus Release Repository" at "http://nexus.paypal.com/nexus/content/groups/public-all/"
libraryDependencies += "org.apache.spark" %% "spark-core" % "1.3.1"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.3.1"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "1.3.1"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka" % "1.3.1"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.12"
libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.2.4" % "test"

assemblySettings
mergeStrategy in assembly := {
  case x if Assembly.isConfigFile(x) =>
    MergeStrategy.concat
  case PathList(ps@_*) if Assembly.isReadme(ps.last) ||
    Assembly.isLicenseFile(ps.last) =>
    MergeStrategy.rename
  case PathList("META-INF", xs@_*) =>
    (xs map {
      _.toLowerCase
    }) match {
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) |
           ("dependencies" :: Nil) =>
        MergeStrategy.discard
      case ps@(x :: xs) if ps.last.endsWith(".sf") ||
        ps.last.endsWith(".dsa") =>
        MergeStrategy.discard
      case "plexus" :: xs =>
        MergeStrategy.discard
      case "services" :: xs =>
        MergeStrategy.filterDistinctLines
      case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
        MergeStrategy.filterDistinctLines
      case _ => MergeStrategy.first
    }
  case _ => MergeStrategy.first
}