package com.ebiznext.sbt.plugins

import sbt._
import Keys._

/**
 * @author stephane.manciot@ebiznext.com
 *
 */
object AndroMDAPlugin extends Plugin {

  private lazy val androMDAClean = taskKey[Seq[File]]("Clean AndroMDA generated sources")

  trait Keys {

    lazy val Config = config("andromda") extend(Compile) hide

    lazy val androMDAVersion = settingKey[String]("AndroMDA version")
    lazy val lastModifiedCheck = settingKey[Boolean]("Whether or not a last modified check should be performed before running AndroMDA again")
    lazy val androMDAConfiguration = settingKey[File]("AndroMDA configuration file")
    lazy val androMDAProperties = settingKey[Seq[(String, String)]]("Properties to be replaced within AndroMDA configuration file")
    lazy val androMDAGenerate = taskKey[Seq[File]]("Generate AndroMDA sources")
    lazy val androMDAGeneratedSources = taskKey[Seq[File]]("Retrieve AndroMDA generated sources")

  }  

  private object AndroMDADefaults extends Keys {
    val settings = Seq(
      androMDAVersion := "3.2",
      lastModifiedCheck := true,
      androMDAProperties := Seq(),
      libraryDependencies ++= AndroMDADependencies.dependencies(androMDAVersion.value, Config),
      androMDAConfiguration := (sourceDirectory in Compile).value / "config" / "andromda.xml"
    )
  }

  object androMDA extends Keys {
    lazy val settings = Seq(ivyConfigurations += Config) ++ AndroMDADefaults.settings ++ Seq(
      // unmanagedResourceDirectories in Compile += androMDAConfiguration.value.getParentFile,
      androMDAProperties += "lastModifiedCheck" -> ("" + lastModifiedCheck.value),
      managedClasspath in androMDAGenerate <<= (classpathTypes in androMDAGenerate, update) map { (ct, report) =>
          Classpaths.managedJars(Config, ct, report)
      },
      androMDAGenerate := {
        val s: TaskStreams = streams.value
        val classpath : Seq[File] = ((managedClasspath in androMDAGenerate).value).files
        val props = Map.empty[String, String] ++ androMDAProperties.value
        Filter(s.log, androMDAConfiguration.value, (classDirectory in Compile).value, props)
        val configurationFile : File = (classDirectory in Compile).value / androMDAConfiguration.value.name
        if(configurationFile.exists()) {new AndroMDA(classpath, configurationFile).generate()}
        Nil
      },
      androMDAGeneratedSources in Compile := {
        retrieveGeneratedFiles(streams.value, baseDirectory.value)
      },
      sourceGenerators in Compile <+= androMDAGeneratedSources in Compile,
      androMDAClean := {
        val files : Seq[File] = (baseDirectory.value / ".." ** "andromda*.log").get ++ retrieveGeneratedFiles(streams.value, baseDirectory.value)
        IO.delete(files)
        files
      },
      clean <<= (clean) dependsOn (androMDAClean)
    )

    def retrieveGeneratedFiles(s: TaskStreams, baseDirectory : File) : Seq[File] = {
      val f : File = baseDirectory / ".." / "andromda.log"
      if(f.exists()){
        val token : String = "Output:"
        val files : Seq[File] = IO.readLines(f) filter {_ contains token} map { l =>
          file(l.substring(l.indexOf("file:/", l.indexOf(token)) + 5, l.length - 1).trim)
        } filter {f => f.getAbsolutePath.startsWith(baseDirectory.getAbsolutePath)} filter {f => (f.name endsWith (".java")) || (f.name endsWith (".scala")) || (f.name endsWith (".groovy"))}
        files
      }
      else{
        s.log.warn(f.getAbsolutePath() + " does not exist")
        Nil
      }
    }
  }

  /**
   * extracted from sbtfilter.FilterPlugin
   * @see https://github.com/sdb/xsbt-filter
   */
  object Filter {
    import util.matching.Regex._
    import java.io.{ FileReader, BufferedReader, PrintWriter }

    val pattern = """((?:\\?)\$\{.+?\})""".r
    def replacer(props: Map[String, String]) = (m: Match) => {
      m.matched match {
        case s if s.startsWith("\\") => Some("""\$\{%s\}""" format s.substring(3, s.length -1))
        case s => props.get(s.substring(2, s.length -1))
      }
    }
    def filter(line: String, props: Map[String, String]) = pattern.replaceSomeIn(line, replacer(props))

    def apply(log: Logger, file: File, dir: File, props: Map[String, String]) {
      log debug ("Filter properties: %s" format (props.mkString("{", ", ", "}")))
      log debug ("Filtering %s" format file.absolutePath)
      IO.createDirectory(dir)
      val dest = new File(dir, file.getName)
      val out = new PrintWriter(dest)
      val in = new BufferedReader(new FileReader(file))
      IO.foreachLine(in) { line => IO.writeLines(out, Seq(filter(line, props))) }
      in.close()
      out.close()
    }
  }

}