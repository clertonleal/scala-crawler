name := "crawler"

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.jsoup" % "jsoup" % "1.8.1",
  "com.github.tminglei" %% "slick-pg" % "0.8.2",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
)