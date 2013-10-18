package com.ebiznext.sbt.plugins

import sbt._

/**
 * @author stephane.manciot@ebiznext.com
 *
 */
object AndroMDADependencies {

  def dependencies(androMDAVersion: String, Config:Configuration) : Seq[ModuleID] = Seq[ModuleID](
    "org.andromda" % "andromda-core" % androMDAVersion % Config.name,

    "org.andromda.repositories" % "andromda-repository-mdr-uml14" % androMDAVersion % Config.name,
    "org.andromda.repositories" % "andromda-repository-emf-uml2" % androMDAVersion % Config.name,

    "org.andromda.metafacades" % "andromda-metafacades-uml14" % androMDAVersion % Config.name,
    "org.andromda.metafacades" % "andromda-metafacades-emf-uml2" % androMDAVersion % Config.name,
    ("org.andromda.metafacades" % "andromda-metafacades-uml" % androMDAVersion % Config.name) jar,

    "org.andromda.templateengines" % "andromda-templateengine-velocity" % androMDAVersion % Config.name,

    "org.andromda.profiles.uml2" % "andromda-profile" % androMDAVersion % Config.name,

    // andromda maven plugin -> to add default mappings within classpath
    "org.andromda.maven.plugins" % "andromda-maven-plugin" % androMDAVersion % Config.name,

    // androMDA cartridges
    ("org.andromda.cartridges" % "andromda-meta-cartridge" % androMDAVersion % Config.name) jar,
    ("org.andromda.cartridges" % "andromda-java-cartridge" % androMDAVersion % Config.name) jar,
    ("org.andromda.cartridges" % "andromda-spring-cartridge" % androMDAVersion % Config.name) jar,
    ("org.andromda.cartridges" % "andromda-webservice-cartridge" % androMDAVersion % Config.name) jar,

    // androMDA translationlibraries
    "org.andromda.translationlibraries" % "andromda-ocl-validation-library" % androMDAVersion % Config.name,
    "org.andromda.translationlibraries" % "andromda-ocl-query-library" % androMDAVersion % Config.name,

    "org.codehaus.plexus" % "plexus-utils" % "1.0.4" % Config.name,
    "commons-lang" % "commons-lang" % "2.1" % Config.name
  )

}