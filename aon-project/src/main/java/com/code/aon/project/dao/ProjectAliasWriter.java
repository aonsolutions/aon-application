package com.code.aon.project.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.project.Activity;
import com.code.aon.project.ActivityType;
import com.code.aon.project.DailyTracking;
import com.code.aon.project.Dossier;
import com.code.aon.project.DossierType;
import com.code.aon.project.JobType;
import com.code.aon.project.Task;

public class ProjectAliasWriter {

	public static void main(String[] args) throws IOException {
		File file = new File("/AON-TRUNK/aon-project/src/main/java/com/code/aon/project/dao/IProjectAlias.java");
		String[] classes = new String[] { 
			Activity.class.getName(),
			ActivityType.class.getName(),
	        DailyTracking.class.getName(),
			Dossier.class.getName(),
			DossierType.class.getName(),
	        JobType.class.getName(),
	        Task.class.getName()
		};
		HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName());
		AliasWriter writer = new AliasWriter("com.code.aon.project.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}
