package com.code.aon.marketing.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.marketing.MarketingAction;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.Campaign;
import com.code.aon.marketing.Question;
import com.code.aon.marketing.QuestionValue;
import com.code.aon.marketing.Survey;
import com.code.aon.marketing.SurveyQuestion;
import com.code.aon.marketing.SurveyResponse;
import com.code.aon.marketing.SurveyResponseDetail;
import com.code.aon.marketing.SurveyWorkflow;
import com.code.aon.marketing.TargetProfile;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IMarketingAlias {



	/** 
	* DAOConstantsEntry for MarketingAction entity.
	*/ 
	DAOConstantsEntry MARKETING_ACTION_ENTRY = DAOConstants.getDAOConstant(MarketingAction.class);

	/** 
	* Alias value: MarketingAction_campaign_id
	* Hibernate value: MarketingAction.campaign.id
	*/
	String  MARKETING_ACTION_CAMPAIGN_ID = MARKETING_ACTION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: MarketingAction_endDate
	* Hibernate value: MarketingAction.endDate
	*/
	String  MARKETING_ACTION_END_DATE = MARKETING_ACTION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: MarketingAction_id
	* Hibernate value: MarketingAction.id
	*/
	String  MARKETING_ACTION_ID = MARKETING_ACTION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: MarketingAction_mediaType
	* Hibernate value: MarketingAction.mediaType
	*/
	String  MARKETING_ACTION_MEDIA_TYPE = MARKETING_ACTION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: MarketingAction_startDate
	* Hibernate value: MarketingAction.startDate
	*/
	String  MARKETING_ACTION_START_DATE = MARKETING_ACTION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: MarketingAction_survey_id
	* Hibernate value: MarketingAction.survey.id
	*/
	String  MARKETING_ACTION_SURVEY_ID = MARKETING_ACTION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: MarketingAction_campaign_active
	* Hibernate value: MarketingAction.campaign.active
	*/
	String  MARKETING_ACTION_CAMPAIGN_ACTIVE = MARKETING_ACTION_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for ActionTarget entity.
	*/ 
	DAOConstantsEntry ACTION_TARGET_ENTRY = DAOConstants.getDAOConstant(ActionTarget.class);

	/** 
	* Alias value: ActionTarget_action_id
	* Hibernate value: ActionTarget.action.id
	*/
	String  ACTION_TARGET_ACTION_ID = ACTION_TARGET_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ActionTarget_id
	* Hibernate value: ActionTarget.id
	*/
	String  ACTION_TARGET_ID = ACTION_TARGET_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ActionTarget_status
	* Hibernate value: ActionTarget.status
	*/
	String  ACTION_TARGET_STATUS = ACTION_TARGET_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ActionTarget_surveyResponse_id
	* Hibernate value: ActionTarget.surveyResponse.id
	*/
	String  ACTION_TARGET_SURVEY_RESPONSE_ID = ACTION_TARGET_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ActionTarget_target_id
	* Hibernate value: ActionTarget.target.id
	*/
	String  ACTION_TARGET_TARGET_ID = ACTION_TARGET_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for Campaign entity.
	*/ 
	DAOConstantsEntry CAMPAIGN_ENTRY = DAOConstants.getDAOConstant(Campaign.class);

	/** 
	* Alias value: Campaign_active
	* Hibernate value: Campaign.active
	*/
	String  CAMPAIGN_ACTIVE = CAMPAIGN_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Campaign_description
	* Hibernate value: Campaign.description
	*/
	String  CAMPAIGN_DESCRIPTION = CAMPAIGN_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Campaign_id
	* Hibernate value: Campaign.id
	*/
	String  CAMPAIGN_ID = CAMPAIGN_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for Question entity.
	*/ 
	DAOConstantsEntry QUESTION_ENTRY = DAOConstants.getDAOConstant(Question.class);

	/** 
	* Alias value: Question_active
	* Hibernate value: Question.active
	*/
	String  QUESTION_ACTIVE = QUESTION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Question_argument
	* Hibernate value: Question.argument
	*/
	String  QUESTION_ARGUMENT = QUESTION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Question_id
	* Hibernate value: Question.id
	*/
	String  QUESTION_ID = QUESTION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Question_text
	* Hibernate value: Question.text
	*/
	String  QUESTION_TEXT = QUESTION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Question_type
	* Hibernate value: Question.type
	*/
	String  QUESTION_TYPE = QUESTION_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for QuestionValue entity.
	*/ 
	DAOConstantsEntry QUESTION_VALUE_ENTRY = DAOConstants.getDAOConstant(QuestionValue.class);

	/** 
	* Alias value: QuestionValue_date
	* Hibernate value: QuestionValue.date
	*/
	String  QUESTION_VALUE_DATE = QUESTION_VALUE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: QuestionValue_id
	* Hibernate value: QuestionValue.id
	*/
	String  QUESTION_VALUE_ID = QUESTION_VALUE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: QuestionValue_number
	* Hibernate value: QuestionValue.number
	*/
	String  QUESTION_VALUE_NUMBER = QUESTION_VALUE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: QuestionValue_question_id
	* Hibernate value: QuestionValue.question.id
	*/
	String  QUESTION_VALUE_QUESTION_ID = QUESTION_VALUE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: QuestionValue_text
	* Hibernate value: QuestionValue.text
	*/
	String  QUESTION_VALUE_TEXT = QUESTION_VALUE_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for Survey entity.
	*/ 
	DAOConstantsEntry SURVEY_ENTRY = DAOConstants.getDAOConstant(Survey.class);

	/** 
	* Alias value: Survey_active
	* Hibernate value: Survey.active
	*/
	String  SURVEY_ACTIVE = SURVEY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Survey_creationDate
	* Hibernate value: Survey.creationDate
	*/
	String  SURVEY_CREATION_DATE = SURVEY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Survey_description
	* Hibernate value: Survey.description
	*/
	String  SURVEY_DESCRIPTION = SURVEY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Survey_id
	* Hibernate value: Survey.id
	*/
	String  SURVEY_ID = SURVEY_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for SurveyQuestion entity.
	*/ 
	DAOConstantsEntry SURVEY_QUESTION_ENTRY = DAOConstants.getDAOConstant(SurveyQuestion.class);

	/** 
	* Alias value: SurveyQuestion_id
	* Hibernate value: SurveyQuestion.id
	*/
	String  SURVEY_QUESTION_ID = SURVEY_QUESTION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SurveyQuestion_position
	* Hibernate value: SurveyQuestion.position
	*/
	String  SURVEY_QUESTION_POSITION = SURVEY_QUESTION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SurveyQuestion_question_id
	* Hibernate value: SurveyQuestion.question.id
	*/
	String  SURVEY_QUESTION_QUESTION_ID = SURVEY_QUESTION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SurveyQuestion_survey_id
	* Hibernate value: SurveyQuestion.survey.id
	*/
	String  SURVEY_QUESTION_SURVEY_ID = SURVEY_QUESTION_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for SurveyResponse entity.
	*/ 
	DAOConstantsEntry SURVEY_RESPONSE_ENTRY = DAOConstants.getDAOConstant(SurveyResponse.class);

	/** 
	* Alias value: SurveyResponse_action_id
	* Hibernate value: SurveyResponse.action.id
	*/
	String  SURVEY_RESPONSE_ACTION_ID = SURVEY_RESPONSE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SurveyResponse_creationDate
	* Hibernate value: SurveyResponse.creationDate
	*/
	String  SURVEY_RESPONSE_CREATION_DATE = SURVEY_RESPONSE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SurveyResponse_date
	* Hibernate value: SurveyResponse.date
	*/
	String  SURVEY_RESPONSE_DATE = SURVEY_RESPONSE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SurveyResponse_id
	* Hibernate value: SurveyResponse.id
	*/
	String  SURVEY_RESPONSE_ID = SURVEY_RESPONSE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SurveyResponse_survey_id
	* Hibernate value: SurveyResponse.survey.id
	*/
	String  SURVEY_RESPONSE_SURVEY_ID = SURVEY_RESPONSE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: SurveyResponse_target_id
	* Hibernate value: SurveyResponse.target.id
	*/
	String  SURVEY_RESPONSE_TARGET_ID = SURVEY_RESPONSE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: SurveyResponse_user_id
	* Hibernate value: SurveyResponse.user.id
	*/
	String  SURVEY_RESPONSE_USER_ID = SURVEY_RESPONSE_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for SurveyResponseDetail entity.
	*/ 
	DAOConstantsEntry SURVEY_RESPONSE_DETAIL_ENTRY = DAOConstants.getDAOConstant(SurveyResponseDetail.class);

	/** 
	* Alias value: SurveyResponseDetail_date
	* Hibernate value: SurveyResponseDetail.date
	*/
	String  SURVEY_RESPONSE_DETAIL_DATE = SURVEY_RESPONSE_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SurveyResponseDetail_id
	* Hibernate value: SurveyResponseDetail.id
	*/
	String  SURVEY_RESPONSE_DETAIL_ID = SURVEY_RESPONSE_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SurveyResponseDetail_number
	* Hibernate value: SurveyResponseDetail.number
	*/
	String  SURVEY_RESPONSE_DETAIL_NUMBER = SURVEY_RESPONSE_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SurveyResponseDetail_question_id
	* Hibernate value: SurveyResponseDetail.question.id
	*/
	String  SURVEY_RESPONSE_DETAIL_QUESTION_ID = SURVEY_RESPONSE_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SurveyResponseDetail_surveyResponse_id
	* Hibernate value: SurveyResponseDetail.surveyResponse.id
	*/
	String  SURVEY_RESPONSE_DETAIL_SURVEY_RESPONSE_ID = SURVEY_RESPONSE_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: SurveyResponseDetail_text
	* Hibernate value: SurveyResponseDetail.text
	*/
	String  SURVEY_RESPONSE_DETAIL_TEXT = SURVEY_RESPONSE_DETAIL_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for SurveyWorkflow entity.
	*/ 
	DAOConstantsEntry SURVEY_WORKFLOW_ENTRY = DAOConstants.getDAOConstant(SurveyWorkflow.class);

	/** 
	* Alias value: SurveyWorkflow_date
	* Hibernate value: SurveyWorkflow.date
	*/
	String  SURVEY_WORKFLOW_DATE = SURVEY_WORKFLOW_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SurveyWorkflow_id
	* Hibernate value: SurveyWorkflow.id
	*/
	String  SURVEY_WORKFLOW_ID = SURVEY_WORKFLOW_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SurveyWorkflow_nextSurveyQuestion_id
	* Hibernate value: SurveyWorkflow.nextSurveyQuestion.id
	*/
	String  SURVEY_WORKFLOW_NEXT_SURVEY_QUESTION_ID = SURVEY_WORKFLOW_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SurveyWorkflow_number
	* Hibernate value: SurveyWorkflow.number
	*/
	String  SURVEY_WORKFLOW_NUMBER = SURVEY_WORKFLOW_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SurveyWorkflow_operator
	* Hibernate value: SurveyWorkflow.operator
	*/
	String  SURVEY_WORKFLOW_OPERATOR = SURVEY_WORKFLOW_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: SurveyWorkflow_questionValue_id
	* Hibernate value: SurveyWorkflow.questionValue.id
	*/
	String  SURVEY_WORKFLOW_QUESTION_VALUE_ID = SURVEY_WORKFLOW_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: SurveyWorkflow_surveyQuestion_id
	* Hibernate value: SurveyWorkflow.surveyQuestion.id
	*/
	String  SURVEY_WORKFLOW_SURVEY_QUESTION_ID = SURVEY_WORKFLOW_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: SurveyWorkflow_text
	* Hibernate value: SurveyWorkflow.text
	*/
	String  SURVEY_WORKFLOW_TEXT = SURVEY_WORKFLOW_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for TargetProfile entity.
	*/ 
	DAOConstantsEntry TARGET_PROFILE_ENTRY = DAOConstants.getDAOConstant(TargetProfile.class);

	/** 
	* Alias value: TargetProfile_date
	* Hibernate value: TargetProfile.date
	*/
	String  TARGET_PROFILE_DATE = TARGET_PROFILE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: TargetProfile_id
	* Hibernate value: TargetProfile.id
	*/
	String  TARGET_PROFILE_ID = TARGET_PROFILE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: TargetProfile_lastUpdate
	* Hibernate value: TargetProfile.lastUpdate
	*/
	String  TARGET_PROFILE_LAST_UPDATE = TARGET_PROFILE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: TargetProfile_number
	* Hibernate value: TargetProfile.number
	*/
	String  TARGET_PROFILE_NUMBER = TARGET_PROFILE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: TargetProfile_question_id
	* Hibernate value: TargetProfile.question.id
	*/
	String  TARGET_PROFILE_QUESTION_ID = TARGET_PROFILE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: TargetProfile_target_id
	* Hibernate value: TargetProfile.target.id
	*/
	String  TARGET_PROFILE_TARGET_ID = TARGET_PROFILE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: TargetProfile_text
	* Hibernate value: TargetProfile.text
	*/
	String  TARGET_PROFILE_TEXT = TARGET_PROFILE_ENTRY.getAliasNames()[6];


}