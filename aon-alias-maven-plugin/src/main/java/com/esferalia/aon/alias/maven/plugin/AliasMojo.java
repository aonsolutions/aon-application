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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.hibernate.MappingException;
import org.hibernate.metadata.ClassMetadata;

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
	 * @parameter default-value="false"
	 * 
	 */
	private Boolean includeGeneratedEntities;

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
	
	public Boolean getIncludeGeneratedEntities() {
		return includeGeneratedEntities;
	}
	public void setIncludeGeneratedEntities(Boolean includeGeneratedEntities) {
		this.includeGeneratedEntities = includeGeneratedEntities;
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		try {
			getLog().info("Source Folder Directory ..: " + getOutputDir());
			File packageDir = new File(getOutputDir(), getTargetPackage().replace('.', '/'));
			packageDir.mkdirs();
			
			getLog().info("Source Package Directory ..: " + packageDir);
			File file  = new File(packageDir, getTargetName() + ".java");
			getLog().info("Alias Class file ..: " + file);
			
			List<String> sourceFolders = project.getCompileSourceRoots();
			
			List<String> aonClasses = new LinkedList<String>();
			List<String> entityClasses = new LinkedList<String>();
			for (int i=0;i<sourceFolders.size(); i++) {
				List<String> sourceFolderAonClasses = getAonEntityClasses(sourceFolders.get(i));  
				aonClasses.addAll(sourceFolderAonClasses);
				for (String aonClass : sourceFolderAonClasses) {
					String[] tokens = StringUtils.split(aonClass,".");
					String entityClass = ENTITY_PACKAGE + tokens[tokens.length - 1];
					entityClasses.add(entityClass);
					getLog().info(" Entity Class Found ..: " + entityClass + " from "+ aonClass);
				}
			}
			
			HibernateUtil.getSessionFactory(sessionFactoryName);
			if (getIncludeGeneratedEntities()) {
				Map<?,ClassMetadata> map = HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName()).getAllClassMetadata();
				Iterator<ClassMetadata> i = map.values().iterator();
				String[] packages = StringUtils.split(getSourcePackages(), ',');
				while ( i.hasNext() ) {
					ClassMetadata cmd = i.next();
					String className = cmd.getEntityName();
					for (String packageName : packages ) {
						if (className.startsWith(packageName) && !entityClasses.contains(className)) {
							entityClasses.add(cmd.getEntityName());
							aonClasses.add(cmd.getEntityName());
							getLog().info(" Entity Class Found ..: " + cmd.getEntityName());
						}
					}
				}
			}
			Collections.sort(entityClasses);
			AliasWriter writer = new AliasWriter(getTargetPackage());
			String[] entityClassesToArray= entityClasses.toArray (new String [entityClasses.size ()]);
			String[] aonClassesToArray = aonClasses.toArray (new String [aonClasses.size ()]);
			writer.write(entityClassesToArray,aonClassesToArray, file);
			getLog().info("---------------");
			getLog().info("---------------");
			getLog().info("Si has creado un @Entity nuevo, añádelo al hibernate.cfg del proyecto aon-aliss-maven-plugin, porque si no, NO ME ENTERO!.");
			getLog().info("---------------");
			getLog().info("---------------");
			getLog().info("Alias generados");

		} catch (IOException e) {
			getLog().error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (MappingException e) {
			getLog().error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			HibernateUtil.closeSession(sessionFactoryName);
		}
	}

	private List<String> getAonEntityClasses(String sourceDir) throws IOException {
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
								classes.add(pack+ "." + filename);
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
/*
	private static ArrayList<Class<?>> getClassesForPackage(Package pkg) {
	    String pkgname = pkg.getName();
	    ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
	    // Get a File object for the package
	    File directory = null;
	    String fullPath;
	    String relPath = pkgname.replace('.', '/');
	    System.out.println("ClassDiscovery: Package: " + pkgname + " becomes Path:" + relPath);
	    URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
	    System.out.println("ClassDiscovery: Resource = " + resource);
	    if (resource == null) {
	        throw new RuntimeException("No resource for " + relPath);
	    }
	    fullPath = resource.getFile();
	    System.out.println("ClassDiscovery: FullPath = " + resource);

	    try {
	        directory = new File(resource.toURI());
	    } catch (URISyntaxException e) {
	        throw new RuntimeException(pkgname + " (" + resource + ") does not appear to be a valid URL / URI.  Strange, since we got it from the system...", e);
	    } catch (IllegalArgumentException e) {
	        directory = null;
	    }
	    System.out.println("ClassDiscovery: Directory = " + directory);

	    if (directory != null && directory.exists()) {
	        // Get the list of the files contained in the package
	        String[] files = directory.list();
	        for (int i = 0; i < files.length; i++) {
	            // we are only interested in .class files
	            if (files[i].endsWith(".class")) {
	                // removes the .class extension
	                String className = pkgname + '.' + files[i].substring(0, files[i].length() - 6);
	                System.out.println("ClassDiscovery: className = " + className);
	                try {
	                    classes.add(Class.forName(className));
	                } 
	                catch (ClassNotFoundException e) {
	                    throw new RuntimeException("ClassNotFoundException loading " + className);
	                }
	            }
	        }
	    }
	    else {
	        try {
	            String jarPath = fullPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
	            JarFile jarFile = new JarFile(jarPath);         
	            Enumeration<JarEntry> entries = jarFile.entries();
	            while(entries.hasMoreElements()) {
	                JarEntry entry = entries.nextElement();
	                String entryName = entry.getName();
	                if(entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
	                    System.out.println("ClassDiscovery: JarEntry: " + entryName);
	                    String className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
	                    System.out.println("ClassDiscovery: className = " + className);
	                    try {
	                        classes.add(Class.forName(className));
	                    } 
	                    catch (ClassNotFoundException e) {
	                        throw new RuntimeException("ClassNotFoundException loading " + className);
	                    }
	                }
	            }
	        } catch (IOException e) {
	            throw new RuntimeException(pkgname + " (" + directory + ") does not appear to be a valid package", e);
	        }
	    }
	    return classes;
	}

	public static void main(String[] args) {
		
		
		
		String[] packages = StringUtils.split("com.code.aon.common,com.code.aon.config", ',');
		List<String> classes = new LinkedList<String>();
		for (String packageName : packages ) {
			System.out.println( packageName );
			Package p = Package.getPackage(packageName);
			System.out.println( p );
		}
	}
*/

}
