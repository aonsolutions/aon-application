package com.code.aon.campaign.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.campaign.ActivityProcess;
import com.code.aon.campaign.Campaign;
import com.code.aon.campaign.CampaignDossier;
import com.code.aon.campaign.Process;
import com.code.aon.campaign.ProcessDetail;
import com.code.aon.campaign.ProcessDetailTransition;
import com.code.aon.campaign.ProcessTransitionType;
import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;

public class CampaignAliasWriter {

	public static void main(String[] args) throws IOException {

		File file = new File("/AON-PROJECT/aon-campaign/src/main/java/com/code/aon/campaign/dao/ICampaignAlias.java");
		String[] classes = new String[7]; 
		classes[0] = ActivityProcess.class.getName();
		classes[1] = Campaign.class.getName();
		classes[2] = CampaignDossier.class.getName();
		classes[3] = Process.class.getName();
		classes[4] = ProcessDetail.class.getName();
		classes[5] = ProcessDetailTransition.class.getName();
		classes[6] = ProcessTransitionType.class.getName();
		HibernateUtil.getSessionFactory(null);
		AliasWriter writer = new AliasWriter("com.code.aon.campaign.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}
