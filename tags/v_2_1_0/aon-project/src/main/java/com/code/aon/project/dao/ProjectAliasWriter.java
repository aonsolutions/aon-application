package com.code.aon.project.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.project.Activity;
import com.code.aon.project.ActivityType;
import com.code.aon.project.DailyTracking;
import com.code.aon.project.Dossier;
import com.code.aon.project.DossierType;
import com.code.aon.project.JobType;
import com.code.aon.project.PeriodicalTask;
import com.code.aon.project.Task;

public class ProjectAliasWriter {

	public static void main(String[] args) throws IOException {

		File file = new File("/AON-PROJECT/aon-project/src/main/java/com/code/aon/project/dao/IProjectAlias.java");
		String[] classes = new String[8]; 
		classes[0] = Activity.class.getName();
		classes[1] = ActivityType.class.getName();
        classes[2] = DailyTracking.class.getName();
		classes[3] = Dossier.class.getName();
		classes[4] = DossierType.class.getName();
        classes[5] = JobType.class.getName();
        classes[6] = Task.class.getName();
        classes[7] = PeriodicalTask.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.project.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}
