package com.code.aon.entity.test;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

public class EntityDiscovery {
	
	private EntityCompare mojo;
	
	public EntityDiscovery( EntityCompare mojo ) {
		this.mojo = mojo;	
	}
	
	public List<String> getAonEntityClasses() throws IOException {
		List<String> entityClasses = new LinkedList<String>();
		String[] sourceFolders = new String[]{"../aon-config/src/main/java"};
		for (int i = 0; i < sourceFolders.length; i++) {
			List<String> sourceFolderAonClasses = getAonEntityClasses(sourceFolders[i]);
			entityClasses.addAll(sourceFolderAonClasses);
		}
		return entityClasses;
	}
	
	
	private List<String> getAonEntityClasses(String sourceDir) throws IOException {
		String[] packages = new String[]{"com.code.aon.account","com.code.aon.config"};
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
							classes.add(pack+ "." + filename);
							break;
						}
					}
					reader.close();
				}
			}
		}
		return classes;
	}

	public class JavaFileFilter implements FileFilter {
		@Override
		public boolean accept(File file) {
			return (file.getName().toLowerCase().endsWith("java"));
		}
	}

}
