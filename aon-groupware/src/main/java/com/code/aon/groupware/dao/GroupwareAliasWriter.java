package com.code.aon.groupware.dao;


import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.groupware.Alarm;
import com.code.aon.groupware.Campaign;
import com.code.aon.groupware.CampaignProject;
import com.code.aon.groupware.CampaignType;
import com.code.aon.groupware.DailyTracking;
import com.code.aon.groupware.Favorite;
import com.code.aon.groupware.FavoriteCategory;
import com.code.aon.groupware.JobType;
import com.code.aon.groupware.Note;
import com.code.aon.groupware.Notice;
import com.code.aon.groupware.Process;
import com.code.aon.groupware.ProcessDetail;
import com.code.aon.groupware.ProcessDetailTransition;
import com.code.aon.groupware.ProcessTask;
import com.code.aon.groupware.ProcessTransitionType;
import com.code.aon.groupware.Task;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.groupware.TaskHolderWorkgroup;

public class GroupwareAliasWriter {

	public static void main(String[] args) throws IOException {
		File file = new File("/home/ecastellano/AON-PROJECT/aon-groupware/src/main/java/com/code/aon/groupware/dao/IGroupwareAlias.java");
		String[] classes = new String[] { 
			Alarm.class.getName(),
			Campaign.class.getName(),
			CampaignProject.class.getName(),
			CampaignType.class.getName(),
			DailyTracking.class.getName(),
        	Favorite.class.getName(),
        	FavoriteCategory.class.getName(),
        	JobType.class.getName(),
        	Note.class.getName(),
			Notice.class.getName(),
			Process.class.getName(),
			ProcessDetail.class.getName(),
			ProcessDetailTransition.class.getName(),
			ProcessTask.class.getName(),
			ProcessTransitionType.class.getName(),
			Task.class.getName(),
			TaskHolder.class.getName(),
			TaskHolderWorkgroup.class.getName()
		};
		HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName());
		AliasWriter writer = new AliasWriter("com.code.aon.groupware.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}

}
