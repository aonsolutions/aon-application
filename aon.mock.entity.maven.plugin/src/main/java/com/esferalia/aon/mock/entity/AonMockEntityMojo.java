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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import jakarta.persistence.Table;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
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

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @author ecastellano
 */
@Mojo(name = "generate-mock-entities", defaultPhase = LifecyclePhase.GENERATE_SOURCES,
		requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
		requiresProject = true, threadSafe = true)
public class AonMockEntityMojo extends AbstractMojo {

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

	@Parameter(defaultValue = "${project.build.directory}/generated-sources")
	private String outputDir;

	@Parameter(defaultValue = "true")
	private boolean generateHibernateCfg;

	@Parameter
	private String buildNumber;

	@Parameter(defaultValue = "${project.build.directory}/classes")
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
		synchronized( LOCK) {
			try {
				Collection<String> classes = AonExporter.map.values();
				for (String clazz:classes) {
					Map<String, Object> additionalContext = new HashMap<String, Object>();
					String aonPackage = ClassUtils.getPackageName(clazz);
					String aonEntity = ClassUtils.getShortClassName(clazz);
					String generatedEntity = aonEntity + AonExporter.CLASS_SUFFIX;
					additionalContext.put("aonPackage", aonPackage);
					additionalContext.put("aonEntity", aonEntity);
					additionalContext.put("buildNumber", buildNumber);
					additionalContext.put("generatedPackage", AonExporter.ENTITY_PACKAGE);
					if ("EnterpriseUser".equals(aonEntity)) {
						generatedEntity = "UserDB";
					}
					additionalContext.put("generatedEntity", generatedEntity);	
					additionalContext.put("date", new Date().toString());
					String tableAnnotation =  getTableAnnotation(generatedEntity);
					boolean hasUniqueConstraint = StringUtils.contains(tableAnnotation,"UniqueConstraint");
					additionalContext.put("table",StringUtils.defaultIfEmpty(tableAnnotation, ""));
					additionalContext.put("hasUniqueConstraint",hasUniqueConstraint);
					Configuration cfg = new Configuration();
					cfg.setTemplateLoader(new ClassTemplateLoader(this.getClass(),"/"));
					Template tpl = cfg.getTemplate("aon/Pojo.ftl");
					File packageDir = new File(getOutputDir(), aonPackage.replace('.', '/'));
					packageDir.mkdirs();
					File file  = new File(packageDir, aonEntity + ".java");
					FileWriter output = new FileWriter(file);
					getLog().info(" Processing mock entity ..: " + aonPackage + "." + aonEntity + ".java");
					tpl.process(additionalContext, output);
					output.flush();
					output.close();
				}
				if (isGenerateHibernateCfg()) {
					Map<String, Object> additionalContext = new HashMap<String, Object>();
					additionalContext.put("buildNumber", buildNumber);
					additionalContext.put("pojos", classes);
					Configuration cfg = new Configuration();
					cfg.setTemplateLoader(new ClassTemplateLoader(this.getClass(),"/"));
					Template tpl = cfg.getTemplate("aon/hibernate.cfg.xml.ftl");
					File dir = new File(getResourcesOutputDir());
					dir.mkdirs();
					File file  = new File(dir, "hibernate.cfg.xml");
					FileWriter output = new FileWriter(file);
					tpl.process(additionalContext, output);			
					output.flush();
					output.close();
				}
			}
			catch(Exception e) {
				throw new MojoExecutionException(e.getMessage(),e);
			} finally {
				System.gc();
			}
			
		}		
	}

	private String getTableAnnotation(String generatedEntity) throws IOException {
		File entityFile = new File (getOutputDir() + "/" 
					+ StringUtils.replace(AonExporter.ENTITY_PACKAGE, ".", "/") 
					+ "/" + generatedEntity + ".java");
		String table = null;
		if (entityFile.canRead()) {
			LineNumberReader reader = new LineNumberReader(new FileReader(entityFile));
			while (reader.ready()) {
				String line = reader.readLine();
				if (StringUtils.isNotBlank(line) && StringUtils.contains(line, "@Table")) {
					table = line;
					break;
				}
			}
			reader.close();
		} else {
			ClassPool pool = ClassPool.getDefault();
			try {
				CtClass clazz = pool.get(AonExporter.ENTITY_PACKAGE + "." + generatedEntity);
				Object ann = clazz.getAnnotation(Table.class);
				table = ann.toString();
			} catch (ClassNotFoundException e) {
				getLog().warn(e.getMessage());
			} catch (NotFoundException e) {
				getLog().warn(e.getMessage());
			}
			
		}
		return table;
	}

}
