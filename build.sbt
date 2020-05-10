import scala.sys.process._

organization := "com.example"
name := "play-vue-input-validation"

lazy val root = (project in file("."))
  .settings(
    scalaVersion := "2.13.2"
  )
  .enablePlugins(PlayScala)

libraryDependencies += guice

PlayKeys.playRunHooks += baseDirectory.map(YarnHooks.apply).value

lazy val frontEndBuild = taskKey[Unit]("Execute yarn build command")

val frontendPath = "frontend"
val frontEndFile = file(frontendPath)

frontEndBuild := {
  println(Process("yarn install", frontEndFile).!!)
  println(Process("yarn build", frontEndFile).!!)
}

dist := (dist dependsOn frontEndBuild).value
stage := (stage dependsOn dist).value

// vuejs output dir is removed before build
