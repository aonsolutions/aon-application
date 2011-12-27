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
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import com.esferalia.aon.entity.hibernate.tools.AonExporter;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @author ecastellano
 * 
 * @goal generate-hibernate-cfg
 * @phase generate-resources
 * @requiresDependencyResolution compile+runtime
 * @requiresProject
 * 
 */
public class AonHibernateCFGMojo extends AbstractMojo {
	
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
	 * @parameter default-value="${project.build.directory}/generated-resources"
	 */
	private String outputDir;

	/**
	 * @parameter default-value="${project.basedir}/templates/hibernate.cfg.xml.ftl"
	 */
	private String template;

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
	
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			Collection<String> classes = AonExporter.map.values();
			Map<String, Object> additionalContext = new HashMap<String, Object>();
			additionalContext.put("pojos", classes);
			Configuration cfg = new Configuration();
			File templateFile = new File(getTemplate()); 
			cfg.setTemplateLoader(new FileTemplateLoader(templateFile.getParentFile()));
			Template tpl = cfg.getTemplate(templateFile.getName());
			File dir = new File(getOutputDir());
			dir.mkdirs();
			File file  = new File(dir, "hibernate.cfg.xml");
			FileWriter output = new FileWriter(file);
			tpl.process(additionalContext, output);			
		}
		catch(Exception e) {
			throw new MojoExecutionException(e.getMessage(),e);
		}		
	}

}
