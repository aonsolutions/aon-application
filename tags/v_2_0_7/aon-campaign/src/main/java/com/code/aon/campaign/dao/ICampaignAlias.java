package com.code.aon.campaign.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.campaign.ActivityProcess;
import com.code.aon.campaign.Campaign;
import com.code.aon.campaign.CampaignDossier;
import com.code.aon.campaign.Process;
import com.code.aon.campaign.ProcessDetail;

/** 
* Interface for holding entity properties constants.
*/ 
public interface ICampaignAlias {



	/** 
	* DAOConstantsEntry for ActivityProcess entity.
	*/ 
	DAOConstantsEntry ACTIVITY_PROCESS_ENTRY = DAOConstants.getDAOConstant(ActivityProcess.class);

	/** 
	* Alias value: ActivityProcess_id
	* Hibernate value: ActivityProcess.id
	*/
	String  ACTIVITY_PROCESS_ID = ACTIVITY_PROCESS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ActivityProcess_campaign_id
	* Hibernate value: ActivityProcess.campaign.id
	*/
	String  ACTIVITY_PROCESS_CAMPAIGN_ID = ACTIVITY_PROCESS_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ActivityProcess_activity_id
	* Hibernate value: ActivityProcess.activity.id
	*/
	String  ACTIVITY_PROCESS_ACTIVITY_ID = ACTIVITY_PROCESS_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ActivityProcess_processDetail_id
	* Hibernate value: ActivityProcess.processDetail.id
	*/
	String  ACTIVITY_PROCESS_PROCESS_DETAIL_ID = ACTIVITY_PROCESS_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ActivityProcess_task_id
	* Hibernate value: ActivityProcess.task.id
	*/
	String  ACTIVITY_PROCESS_TASK_ID = ACTIVITY_PROCESS_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ActivityProcess_dossier_id
	* Hibernate value: ActivityProcess.activity.dossier.id
	*/
	String  ACTIVITY_PROCESS_DOSSIER_ID = ACTIVITY_PROCESS_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ActivityProcess_processDetail_position
	* Hibernate value: ActivityProcess.processDetail.position
	*/
	String  ACTIVITY_PROCESS_PROCESS_DETAIL_POSITION = ACTIVITY_PROCESS_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ActivityProcess_task_endDate
	* Hibernate value: ActivityProcess.task.endDate
	*/
	String  ACTIVITY_PROCESS_TASK_END_DATE = ACTIVITY_PROCESS_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ActivityProcess_task_dueDate
	* Hibernate value: ActivityProcess.task.dueDate
	*/
	String  ACTIVITY_PROCESS_TASK_DUE_DATE = ACTIVITY_PROCESS_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for Campaign entity.
	*/ 
	DAOConstantsEntry CAMPAIGN_ENTRY = DAOConstants.getDAOConstant(Campaign.class);

	/** 
	* Alias value: Campaign_activityType_id
	* Hibernate value: Campaign.activityType.id
	*/
	String  CAMPAIGN_ACTIVITY_TYPE_ID = CAMPAIGN_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Campaign_description
	* Hibernate value: Campaign.description
	*/
	String  CAMPAIGN_DESCRIPTION = CAMPAIGN_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Campaign_endDate
	* Hibernate value: Campaign.endDate
	*/
	String  CAMPAIGN_END_DATE = CAMPAIGN_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Campaign_id
	* Hibernate value: Campaign.id
	*/
	String  CAMPAIGN_ID = CAMPAIGN_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Campaign_process_id
	* Hibernate value: Campaign.process.id
	*/
	String  CAMPAIGN_PROCESS_ID = CAMPAIGN_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Campaign_startDate
	* Hibernate value: Campaign.startDate
	*/
	String  CAMPAIGN_START_DATE = CAMPAIGN_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Campaign_status
	* Hibernate value: Campaign.status
	*/
	String  CAMPAIGN_STATUS = CAMPAIGN_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Campaign_type
	* Hibernate value: Campaign.type
	*/
	String  CAMPAIGN_TYPE = CAMPAIGN_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Campaign_workGroup_id
	* Hibernate value: Campaign.workGroup.id
	*/
	String  CAMPAIGN_WORK_GROUP_ID = CAMPAIGN_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for CampaignDossier entity.
	*/ 
	DAOConstantsEntry CAMPAIGN_DOSSIER_ENTRY = DAOConstants.getDAOConstant(CampaignDossier.class);

	/** 
	* Alias value: CampaignDossier_id
	* Hibernate value: CampaignDossier.id
	*/
	String  CAMPAIGN_DOSSIER_ID = CAMPAIGN_DOSSIER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CampaignDossier_campaign_id
	* Hibernate value: CampaignDossier.campaign.id
	*/
	String  CAMPAIGN_DOSSIER_CAMPAIGN_ID = CAMPAIGN_DOSSIER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CampaignDossier_dossier_id
	* Hibernate value: CampaignDossier.dossier.id
	*/
	String  CAMPAIGN_DOSSIER_DOSSIER_ID = CAMPAIGN_DOSSIER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CampaignDossier_dossier_number
	* Hibernate value: CampaignDossier.dossier.number
	*/
	String  CAMPAIGN_DOSSIER_DOSSIER_NUMBER = CAMPAIGN_DOSSIER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CampaignDossier_customer_document
	* Hibernate value: CampaignDossier.dossier.customer.registry.document
	*/
	String  CAMPAIGN_DOSSIER_CUSTOMER_DOCUMENT = CAMPAIGN_DOSSIER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: CampaignDossier_customer_name
	* Hibernate value: CampaignDossier.dossier.customer.registry.name
	*/
	String  CAMPAIGN_DOSSIER_CUSTOMER_NAME = CAMPAIGN_DOSSIER_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: CampaignDossier_customer_surname
	* Hibernate value: CampaignDossier.dossier.customer.registry.surname
	*/
	String  CAMPAIGN_DOSSIER_CUSTOMER_SURNAME = CAMPAIGN_DOSSIER_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for Process entity.
	*/ 
	DAOConstantsEntry PROCESS_ENTRY = DAOConstants.getDAOConstant(Process.class);

	/** 
	* Alias value: Process_description
	* Hibernate value: Process.description
	*/
	String  PROCESS_DESCRIPTION = PROCESS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Process_id
	* Hibernate value: Process.id
	*/
	String  PROCESS_ID = PROCESS_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for ProcessDetail entity.
	*/ 
	DAOConstantsEntry PROCESS_DETAIL_ENTRY = DAOConstants.getDAOConstant(ProcessDetail.class);

	/** 
	* Alias value: ProcessDetail_alertDays
	* Hibernate value: ProcessDetail.alertDays
	*/
	String  PROCESS_DETAIL_ALERT_DAYS = PROCESS_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProcessDetail_dateReference
	* Hibernate value: ProcessDetail.dateReference
	*/
	String  PROCESS_DETAIL_DATE_REFERENCE = PROCESS_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProcessDetail_days
	* Hibernate value: ProcessDetail.days
	*/
	String  PROCESS_DETAIL_DAYS = PROCESS_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ProcessDetail_description
	* Hibernate value: ProcessDetail.description
	*/
	String  PROCESS_DETAIL_DESCRIPTION = PROCESS_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ProcessDetail_id
	* Hibernate value: ProcessDetail.id
	*/
	String  PROCESS_DETAIL_ID = PROCESS_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ProcessDetail_position
	* Hibernate value: ProcessDetail.position
	*/
	String  PROCESS_DETAIL_POSITION = PROCESS_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ProcessDetail_process_id
	* Hibernate value: ProcessDetail.process.id
	*/
	String  PROCESS_DETAIL_PROCESS_ID = PROCESS_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ProcessDetail_workgroup_id
	* Hibernate value: ProcessDetail.workgroup.id
	*/
	String  PROCESS_DETAIL_WORKGROUP_ID = PROCESS_DETAIL_ENTRY.getAliasNames()[7];


}