import java.io.PrintWriter
import play.sbt.PlayRunHook
import sbt._
import scala.io.Source
import scala.sys.process.Process

object YarnHooks {

  object Commands {
    val install = "yarn install"
    val buildWatch = "yarn build --watch"
  }

  object Shell {
    def execute(cmd: String, cwd: File, envs: (String, String)*): Int = {
      Process(cmd, cwd, envs: _*).!
    }

    def invoke(cmd: String, cwd: File, envs: (String, String)*): Process = {
      Process(cmd, cwd, envs: _*).run
    }
  }

  def apply(base: File): PlayRunHook = {

    val frontendBase = base / "frontend"
    val packageJsonPath = frontendBase / "package.json"

    val frontEndTarget = base / "target" / "frontend"
    val packageJsonHashPath = frontEndTarget / "package.json.hash"

    object YarnBuildProcess extends PlayRunHook {
      var process: Option[Process] = None

      override def beforeStarted(): Unit = {
        val currPackageJsonHash = Source.fromFile(packageJsonPath).getLines().mkString.hashCode().toString()
        val oldPackageJsonHash = getStoredPackageJsonHash()

        if (!oldPackageJsonHash.exists(_ == currPackageJsonHash)) {
          println(s"Found new/changed package.json. Run '${Commands.install}'...")

          Shell.execute(Commands.install, frontendBase)

          updateStoredPackageJsonHash(currPackageJsonHash)
        }
      }

      override def afterStarted(): Unit = {
        println("> Watching frontend changes")
        process = Option(Shell.invoke(Commands.buildWatch, frontendBase))
      }

      override def afterStopped(): Unit = {
        process.foreach(_.destroy)
        process = None
      }

      private def getStoredPackageJsonHash(): Option[String] = {
        if (packageJsonHashPath.exists()) {
          val hash = Source.fromFile(packageJsonHashPath).getLines().mkString
          Some(hash)
        } else {
          None
        }
      }

      private def updateStoredPackageJsonHash(hash: String): Unit = {
        val dir = frontEndTarget

        if (!dir.exists)
          dir.mkdirs

        val pw = new PrintWriter(packageJsonHashPath)

        try {
          pw.write(hash)
        } finally {
          pw.close()
        }
      }
    }

    YarnBuildProcess
  }
}
