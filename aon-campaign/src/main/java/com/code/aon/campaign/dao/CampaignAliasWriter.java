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
		File file = new File("/AON-TRUNK/aon-campaign/src/main/java/com/code/aon/campaign/dao/ICampaignAlias.java");
		String[] classes = new String[] { 
			ActivityProcess.class.getName(),
			Campaign.class.getName(),
			CampaignDossier.class.getName(),
			Process.class.getName(),
			ProcessDetail.class.getName(),
			ProcessDetailTransition.class.getName(),
			ProcessTransitionType.class.getName()
		};
		HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName());
		AliasWriter writer = new AliasWriter("com.code.aon.campaign.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}
