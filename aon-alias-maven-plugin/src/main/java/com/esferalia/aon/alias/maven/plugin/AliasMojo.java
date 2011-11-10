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
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.hibernate.MappingException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;

/**
 * @author ecastellano
 * 
 * @goal generate-alias
 * @phase process-sources
 * @requiresDependencyResolution
 * @requiresProject
 * 
 */
public class AliasMojo extends AbstractMojo {
	
	private static final String ENTITY_PACKAGE = "com.code.aon.entity.master."; 

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
	 * @parameter
	 */
	private String targetPackage;

	/**
	 * @parameter
	 */
	private String targetName;

	/**
	 * @parameter
	 */
	private String sourcePackages;
	
	/**
	 * @parameter
	 */
	private String excludes;

	/**
	 * @parameter default-value="${project.build.directory}/generated-sources"
	 */
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
		try {
			getLog().info("Source Folder Directory ..: " + getOutputDir());
			File packageDir = new File(getOutputDir(), getTargetPackage().replace('.', '/'));
			packageDir.mkdirs();
			getLog().info("Source Package Directory ..: " + packageDir);
			File file  = new File(packageDir, getTargetName() + ".java");
			getLog().info("Alias Class file ..: " + file);

			String sessionFactoryName = HibernateUtil.getSessionFactoryName();
			HibernateUtil.getSessionFactory(sessionFactoryName);

			List sourceFolders = project.getCompileSourceRoots();
			List<String> classes = new LinkedList<String>();
			for (int i=0;i<sourceFolders.size(); i++) {
				classes.addAll(getEntityClasses((String) sourceFolders.get(i)));
			}
			Collections.sort(classes);
			for (int x = 0; x < classes.size(); x++) {
				classes.set(x, ENTITY_PACKAGE + classes.get(x));
				getLog().info(" Entity Class Found ..: " + classes.get(x));
			}
			AliasWriter writer = new AliasWriter(getTargetPackage());
			String[] toAlias = classes.toArray (new String [classes.size ()]);
			writer.write(toAlias, file);
			getLog().info("Alias generados");

		} catch (IOException e) {
			getLog().error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (MappingException e) {
			getLog().error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private List<String> getEntityClasses(String sourceDir) throws IOException {
		String[] packages = StringUtils.split(getSourcePackages(), ',');
		List<String> classes = new LinkedList<String>();
		for (String pack : packages ) {
			pack = StringUtils.trim(pack);
			String packageFolder = sourceDir + "/" + StringUtils.replace(pack,".", "/");
			File folder = new File(packageFolder);
			File[] files = folder.listFiles( new JavaFileFilter()); 
			if (files != null) {
				for (File classFile :  folder.listFiles( new JavaFileFilter())) {
					LineNumberReader reader = new LineNumberReader(new FileReader(classFile));
					while (reader.ready()) {
						String line = reader.readLine();
						if (StringUtils.contains(line, "@Entity")) {
							String filename = FilenameUtils.removeExtension(classFile.getName());
							if (!isExclude(filename)) {
								classes.add(filename);
								break;
							}
						}
					}
				}
			}
		}
		return classes;
	}

	private boolean isExclude(String filename) {
		if (StringUtils.isNotBlank(getExcludes())) {
			String[] excludePojos = StringUtils.split(getExcludes(),',');
			for (String excludePojo: excludePojos) {
				if (StringUtils.equals(filename, excludePojo)) {
					return true;
				}
			}
		}
		return false;
	}

	public class JavaFileFilter implements FileFilter {
		@Override
		public boolean accept(File file) {
			return (file.getName().toLowerCase().endsWith("java"));
		}
	}

	public static void main(String[] args) throws IOException {
		AliasMojo mojo = new AliasMojo();
		mojo.setSourcePackages("com.code.aon.company");
		for (String pack : mojo.getEntityClasses("/home/ecastellano/AON-7/aon.parent/aon-company/src/main/java") ) {
			System.out.println(pack);
		}
	}
}
