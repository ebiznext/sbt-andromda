sbt-andromda
============

an sbt plugin for andromda (an open source code generation framework that follows Model Driven Architecture paradigm)

## Requirements

* [SBT 0.13+](http://www.scala-sbt.org/)


## Quick start

Add plugin to *project/plugins.sbt*:

```scala

resolvers += "Sonatype Repository" at "https://oss.sonatype.org/content/groups/public"

addSbtPlugin("com.ebiznext.sbt.plugins" % "sbt-andromda" % "0.1.2")
```

For *.sbt* build definitions, inject the plugin settings in *build.sbt*:

```scala
seq(androMDA.settings :_*)
```

For *.scala* build definitions, inject the plugin settings in *Build.scala*:

```scala
Project(..., settings = Project.defaultSettings ++ com.ebiznext.sbt.plugins.AndroMDAPlugin.androMDA.settings)
```

## Configuration

Plugin keys are located in `com.ebiznext.sbt.plugins.AndroMDAPlugin.Keys`

