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
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import com.esferalia.aon.entity.hibernate.tools.AonExporter;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @author ecastellano
 */
@Mojo(name = "generate-hibernate-cfg", defaultPhase = LifecyclePhase.GENERATE_RESOURCES,
		requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
		requiresProject = true, threadSafe = true)
public class AonHibernateCFGMojo extends AbstractMojo {


	private static final Object LOCK = new Object();

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

	@Parameter(defaultValue = "${project.build.directory}/generated-resources")
	private String outputDir;

	@Parameter(defaultValue = "${project.basedir}/templates/hibernate.cfg.xml.ftl")
	private String template;
	
	private String buildNumber;

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
		synchronized (LOCK) {
			try {
				Collection<String> classes = AonExporter.map.values();
				Map<String, Object> additionalContext = new HashMap<String, Object>();
				additionalContext.put("pojos", classes);
				additionalContext.put("buildNumber", buildNumber);
				Configuration cfg = new Configuration();
				File templateFile = new File(getTemplate()); 
				cfg.setTemplateLoader(new FileTemplateLoader(templateFile.getParentFile()));
				Template tpl = cfg.getTemplate(templateFile.getName());
				File dir = new File(getOutputDir());
				dir.mkdirs();
				File file  = new File(dir, "hibernate.cfg.xml");
				FileWriter output = new FileWriter(file);
				tpl.process(additionalContext, output);
				output.flush();
				output.close();
			}
			catch(Exception e) {
				throw new MojoExecutionException(e.getMessage(),e);
			}		
		}
	}

}
