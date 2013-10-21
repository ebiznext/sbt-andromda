package com.ebiznext.sbt.plugins

import sbt._
import Keys._

import sbt.classpath.ClasspathUtilities

/**
 * @author stephane.manciot@ebiznext.com
 *
 */
class AndroMDA(val classpath : Seq[File], val configurationFile : File){

  lazy val configurationContents = IO.read(configurationFile)

  lazy val oldContextClassLoader = Thread.currentThread.getContextClassLoader

  lazy val classLoader = ClasspathUtilities.toLoader(classpath)

  lazy val configurationClass = classLoader.loadClass("org.andromda.core.configuration.Configuration")
  lazy val androMDAClass = classLoader.loadClass("org.andromda.core.AndroMDA")
  lazy val androMDANewInstanceMethod = androMDAClass.getMethod("newInstance")
  lazy val runMethod = androMDAClass.getMethod("run", configurationClass)
  lazy val shutdownMethod = androMDAClass.getMethod("shutdown")
  lazy val configurationGetInstanceMethod = configurationClass.getMethod("getInstance", classOf[java.lang.String])
  lazy val mappingsUrl : java.net.URL = classLoader.getResource("META-INF/andromda/mappings")
  lazy val addMappingsSearchLocationMethod = configurationClass.getMethod("addMappingsSearchLocation", classOf[java.lang.String])

  def generate() : Unit = {
  	try{
        Thread.currentThread.setContextClassLoader(classLoader)
        val configuration = configurationGetInstanceMethod.invoke(null, configurationContents)
        if (mappingsUrl != null)
        {
          addMappingsSearchLocationMethod.invoke(configuration, mappingsUrl.toString)
        }
        val androMDA = androMDANewInstanceMethod.invoke(null)
        runMethod.invoke(androMDA, configuration)
        shutdownMethod.invoke(androMDA)
  	}
  	finally{
        Thread.currentThread.setContextClassLoader(oldContextClassLoader)    		
  	}
  } 

}

