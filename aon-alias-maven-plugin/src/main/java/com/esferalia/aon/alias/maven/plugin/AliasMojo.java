/**
 * 
 */
package com.esferalia.aon.alias.maven.plugin;

/********************************************************************
 * Copyright (c) 2011, esferalia NETWORKS S.A
 *
 * The copyright of the computer program herein is the property 
 * of esferalia NETWORKS.
 *********************************************************************
 * The program may be used and/or copied only with the written 
 * permission of esferalia NETWORKS, or in accordance with the 
 * terms and conditions stipulated in the agreement contract 
 * under which the program has been supplied.
 *********************************************************************
 */


import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.hibernate.MappingException;
import org.hibernate.metadata.ClassMetadata;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;

/**
 * @author ecastellano
 */
@Mojo(name = "generate-alias", defaultPhase = LifecyclePhase.PROCESS_SOURCES,
		requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
		requiresProject = true, threadSafe = true)
public class AliasMojo extends AbstractMojo {

	private static final Object LOCK = new Object();

	private static final String ENTITY_PACKAGE = "com.esferalia.aon.entity.master.";
	private static final String MESSAGING_PACKAGE = "com.code.aon.messaging.";

	/**
	 * The Maven Project Object
	 */
	@Parameter(defaultValue = "${project}", readonly = true)
	protected MavenProject project;

	/**
	 * The maven project's helper.
	 */
	@Component
	private MavenProjectHelper projectHelper;

	@Parameter
	private String targetPackage;

	@Parameter
	private String targetName;

	@Parameter
	private String sourcePackages;

	@Parameter
	private String excludes;

	@Parameter(defaultValue = "${project.build.directory}/generated-sources")
	private String outputDir;

	public MavenProject getProject() {
		return project;
	}

	public void setProject(MavenProject project) {
		this.project = project;
	}

	public MavenProjectHelper getProjectHelper() {
		return projectHelper;
	}

	public void setProjectHelper(MavenProjectHelper projectHelper) {
		this.projectHelper = projectHelper;
	}

	public String getTargetPackage() {
		return targetPackage;
	}

	public void setTargetPackage(String targetPackage) {
		this.targetPackage = targetPackage;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getSourcePackages() {
		return sourcePackages;
	}

	public void setSourcePackages(String sourcePackages) {
		this.sourcePackages = sourcePackages;
	}

	public String getExcludes() {
		return excludes;
	}

	public void setExcludes(String excludes) {
		this.excludes = excludes;
	}
	
	public String getOutputDir() {
		return outputDir;
	}
	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		synchronized (LOCK) {
			String sessionFactoryName = HibernateUtil.getSessionFactoryName();
			try {
				getLog().info("Source Folder Directory ..: " + getOutputDir());
				File packageDir = new File(getOutputDir(), getTargetPackage().replace('.', '/'));
				packageDir.mkdirs();
				
				getLog().info("Source Package Directory ..: " + packageDir);
				File file  = new File(packageDir, getTargetName() + ".java");
				getLog().info("Alias Class file ..: " + file);
				
				// Lista de Entidades que todavia est�n en los proyectos aon.
				// Sirve basicamente para mostrar los imports en la clase generada.
				List<String> aonClasses = new LinkedList<String>();
	
				// Lista de Entidades que alimentar�n a  todavia est�n en los proyectos aon.			
				List<String> entityClasses = new LinkedList<String>();
				
				HibernateUtil.getSessionFactory(sessionFactoryName);
				Map<?,ClassMetadata> map = HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName()).getAllClassMetadata();
				for (ClassMetadata cmd : map.values()) {
					String entityName = cmd.getEntityName();
					if (!entityName.startsWith(ENTITY_PACKAGE) &&
						!entityName.startsWith(MESSAGING_PACKAGE) &&
						!entityClasses.contains(entityName)) {
						entityClasses.add(cmd.getEntityName());
						aonClasses.add(cmd.getEntityName());
						getLog().info(" Entity Class Found ..: " + cmd.getEntityName());
					}
				}
				Collections.sort(entityClasses);
				AliasWriter writer = new AliasWriter(getTargetPackage());
				String[] entityClassesToArray= entityClasses.toArray (new String [entityClasses.size ()]);
				writer.write(entityClassesToArray, file);
				getLog().info("---------------");
				getLog().info("Alias generados");
				getLog().info("---------------");
	
			} catch (IOException e) {
				getLog().error(e);
				throw new MojoExecutionException(e.getMessage(), e);
			} catch (MappingException e) {
				getLog().error(e);
				throw new MojoExecutionException(e.getMessage(), e);
			} finally {
				HibernateUtil.closeSession(sessionFactoryName);
				System.gc();
			}
		}
	}
}
