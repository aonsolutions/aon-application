package com.code.aon.project.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.project.ActivityType;
import com.code.aon.project.Project;
import com.code.aon.project.ProjectActivity;
import com.code.aon.project.ProjectType;

public class ProjectAliasWriter {

	public static void main(String[] args) throws IOException {
		File file = new File("/AON-TRUNK/aon-project/src/main/java/com/code/aon/project/dao/IProjectAlias.java");
		String[] classes = new String[] { 
			ActivityType.class.getName(),
	        Project.class.getName(),
	        ProjectActivity.class.getName(),
	        ProjectType.class.getName()
		};
		HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName());
		AliasWriter writer = new AliasWriter("com.code.aon.project.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}

