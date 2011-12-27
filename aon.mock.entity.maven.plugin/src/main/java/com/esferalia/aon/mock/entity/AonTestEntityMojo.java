/**
 * 
 */
package com.esferalia.aon.mock.entity;

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
import java.io.FileWriter;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import com.esferalia.aon.entity.hibernate.tools.AonExporter;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @author ecastellano
 * 
 * @goal generate-test-entities
 * @phase generate-test-sources
 * @requiresDependencyResolution compile+runtime
 * @requiresProject
 * 
 */
public class AonTestEntityMojo extends AbstractMojo {
	
	/**
	 * The Maven Project Object
	 * 
	 * @parameter expression="${project}"
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * The maven project's helper.
	 * 
	 * @component role="org.apache.maven.project.MavenProjectHelper"
	 * @readonly
	 */
	private MavenProjectHelper projectHelper;

	/**
	 * @parameter default-value="${project.build.directory}/generated-sources"
	 */
	private String outputDir;
	
	/**
	 * @parameter default-value="true"
	 */
	private boolean generateHibernateCfg;

	/**
	 * @parameter default-value="${project.build.directory}/classes"
	 */
	private String resourcesOutputDir;

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

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}
	
	public String getResourcesOutputDir() {
		return resourcesOutputDir;
	}

	public void setResourcesOutputDir(String resourcesOutputDir) {
		this.resourcesOutputDir = resourcesOutputDir;
	}

	public boolean isGenerateHibernateCfg() {
		return generateHibernateCfg;
	}

	public void setGenerateHibernateCfg(boolean generateHibernateCfg) {
		this.generateHibernateCfg = generateHibernateCfg;
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			Collection<String> classes = AonExporter.map.values();
			for (String clazz:classes) {
				Map<String, Object> additionalContext = new HashMap<String, Object>();
				String aonPackage = ClassUtils.getPackageName(clazz);
				String aonEntity = ClassUtils.getShortClassName(clazz);
				additionalContext.put("aonPackage", aonPackage);
				additionalContext.put("aonEntity", aonEntity);
				additionalContext.put("date", new Date().toString());
				Configuration cfg = new Configuration();
				cfg.setTemplateLoader(new ClassTemplateLoader(this.getClass(),"/"));
				Template tpl = cfg.getTemplate("aon/TestEntity.ftl");
				File packageDir = new File(getOutputDir(), "com/esferalia/aon/test");
				packageDir.mkdirs();
				File file  = new File(packageDir, "TestEntity" + aonEntity + ".java");
				FileWriter output = new FileWriter(file);
				tpl.process(additionalContext, output);			
			}
			if (isGenerateHibernateCfg()) {
				Map<String, Object> additionalContext = new HashMap<String, Object>();
				additionalContext.put("pojos", classes);
				Configuration cfg = new Configuration();
				cfg.setTemplateLoader(new ClassTemplateLoader(this.getClass(),"/"));
				Template tpl = cfg.getTemplate("aon/hibernate.cfg.xml.ftl");
				File dir = new File(getResourcesOutputDir());
				dir.mkdirs();
				File file  = new File(dir, "hibernate.cfg.xml");
				FileWriter output = new FileWriter(file);
				tpl.process(additionalContext, output);			
			}
			
		}
		catch(Exception e) {
			throw new MojoExecutionException(e.getMessage(),e);
		}		
	}

}
