package com.code.aon.marketing.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.Campaign;
import com.code.aon.marketing.MarketingAction;
import com.code.aon.marketing.MarketingTarget;
import com.code.aon.marketing.Question;
import com.code.aon.marketing.QuestionValue;
import com.code.aon.marketing.Survey;
import com.code.aon.marketing.SurveyQuestion;
import com.code.aon.marketing.SurveyResponse;
import com.code.aon.marketing.SurveyResponseDetail;
import com.code.aon.marketing.SurveyWorkflow;
import com.code.aon.marketing.TargetProfile;

/**
 * @author Consulting & Development. Aimar Tellitu - 19-sep-2008
 *
 */
public class MarketingAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/AON-PROJECT/aon-marketing/src/main/java/com/code/aon/marketing/dao/IMarketingAlias.java");
		String[] classes = new String[] { 
				MarketingAction.class.getName(),
				ActionTarget.class.getName(),
				Campaign.class.getName(),
				Question.class.getName(),
				QuestionValue.class.getName(),
				Survey.class.getName(),
				SurveyQuestion.class.getName(),
				SurveyResponse.class.getName(),
				SurveyResponseDetail.class.getName(),
				SurveyWorkflow.class.getName(),
				TargetProfile.class.getName(),
				MarketingTarget.class.getName()};
		HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName());
		AliasWriter writer = new AliasWriter("com.code.aon.marketing.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}