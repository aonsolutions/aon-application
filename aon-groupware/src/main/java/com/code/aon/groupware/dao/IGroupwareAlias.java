package com.code.aon.groupware.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.groupware.Alarm;
import com.code.aon.groupware.Campaign;
import com.code.aon.groupware.CampaignProject;
import com.code.aon.groupware.CampaignType;
import com.code.aon.groupware.CostProfile;
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

/** 
* Interface for holding entity properties constants.
*/ 
public interface IGroupwareAlias {



	/** 
	* DAOConstantsEntry for Alarm entity.
	*/ 
	DAOConstantsEntry ALARM_ENTRY = DAOConstants.getDAOConstant(Alarm.class);

	/** 
	* Alias value: Alarm_alarmDate
	* Hibernate value: Alarm.alarmDate
	*/
	String  ALARM_ALARM_DATE = ALARM_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Alarm_description
	* Hibernate value: Alarm.description
	*/
	String  ALARM_DESCRIPTION = ALARM_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Alarm_id
	* Hibernate value: Alarm.id
	*/
	String  ALARM_ID = ALARM_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Alarm_priority
	* Hibernate value: Alarm.priority
	*/
	String  ALARM_PRIORITY = ALARM_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Alarm_source
	* Hibernate value: Alarm.source
	*/
	String  ALARM_SOURCE = ALARM_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Alarm_sourceId
	* Hibernate value: Alarm.sourceId
	*/
	String  ALARM_SOURCE_ID = ALARM_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Alarm_status
	* Hibernate value: Alarm.status
	*/
	String  ALARM_STATUS = ALARM_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Alarm_user_id
	* Hibernate value: Alarm.user.id
	*/
	String  ALARM_USER_ID = ALARM_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for Campaign entity.
	*/ 
	DAOConstantsEntry CAMPAIGN_ENTRY = DAOConstants.getDAOConstant(Campaign.class);

	/** 
	* Alias value: Campaign_campaignType_id
	* Hibernate value: Campaign.campaignType.id
	*/
	String  CAMPAIGN_CAMPAIGN_TYPE_ID = CAMPAIGN_ENTRY.getAliasNames()[0];

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
	* Alias value: Campaign_enterprise_id
	* Hibernate value: Campaign.enterprise.id
	*/
	String  CAMPAIGN_ENTERPRISE_ID = CAMPAIGN_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Campaign_id
	* Hibernate value: Campaign.id
	*/
	String  CAMPAIGN_ID = CAMPAIGN_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Campaign_manual
	* Hibernate value: Campaign.manual
	*/
	String  CAMPAIGN_MANUAL = CAMPAIGN_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Campaign_process_id
	* Hibernate value: Campaign.process.id
	*/
	String  CAMPAIGN_PROCESS_ID = CAMPAIGN_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Campaign_startDate
	* Hibernate value: Campaign.startDate
	*/
	String  CAMPAIGN_START_DATE = CAMPAIGN_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Campaign_status
	* Hibernate value: Campaign.status
	*/
	String  CAMPAIGN_STATUS = CAMPAIGN_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Campaign_workGroup_id
	* Hibernate value: Campaign.workGroup.id
	*/
	String  CAMPAIGN_WORK_GROUP_ID = CAMPAIGN_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for CampaignProject entity.
	*/ 
	DAOConstantsEntry CAMPAIGN_PROJECT_ENTRY = DAOConstants.getDAOConstant(CampaignProject.class);

	/** 
	* Alias value: CampaignProject_id
	* Hibernate value: CampaignProject.id
	*/
	String  CAMPAIGN_PROJECT_ID = CAMPAIGN_PROJECT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CampaignProject_campaign_id
	* Hibernate value: CampaignProject.campaign.id
	*/
	String  CAMPAIGN_PROJECT_CAMPAIGN_ID = CAMPAIGN_PROJECT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CampaignProject_project_id
	* Hibernate value: CampaignProject.project.id
	*/
	String  CAMPAIGN_PROJECT_PROJECT_ID = CAMPAIGN_PROJECT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CampaignProject_project_name
	* Hibernate value: CampaignProject.project.name
	*/
	String  CAMPAIGN_PROJECT_PROJECT_NAME = CAMPAIGN_PROJECT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CampaignProject_project_registry_document
	* Hibernate value: CampaignProject.project.registry.document
	*/
	String  CAMPAIGN_PROJECT_PROJECT_REGISTRY_DOCUMENT = CAMPAIGN_PROJECT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: CampaignProject_project_registry_name
	* Hibernate value: CampaignProject.project.registry.name
	*/
	String  CAMPAIGN_PROJECT_PROJECT_REGISTRY_NAME = CAMPAIGN_PROJECT_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for CampaignType entity.
	*/ 
	DAOConstantsEntry CAMPAIGN_TYPE_ENTRY = DAOConstants.getDAOConstant(CampaignType.class);

	/** 
	* Alias value: CampaignType_active
	* Hibernate value: CampaignType.active
	*/
	String  CAMPAIGN_TYPE_ACTIVE = CAMPAIGN_TYPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CampaignType_description
	* Hibernate value: CampaignType.description
	*/
	String  CAMPAIGN_TYPE_DESCRIPTION = CAMPAIGN_TYPE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CampaignType_enterprise_id
	* Hibernate value: CampaignType.enterprise.id
	*/
	String  CAMPAIGN_TYPE_ENTERPRISE_ID = CAMPAIGN_TYPE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CampaignType_id
	* Hibernate value: CampaignType.id
	*/
	String  CAMPAIGN_TYPE_ID = CAMPAIGN_TYPE_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for CostProfile entity.
	*/ 
	DAOConstantsEntry COST_PROFILE_ENTRY = DAOConstants.getDAOConstant(CostProfile.class);

	/** 
	* Alias value: CostProfile_cost
	* Hibernate value: CostProfile.cost
	*/
	String  COST_PROFILE_COST = COST_PROFILE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CostProfile_description
	* Hibernate value: CostProfile.description
	*/
	String  COST_PROFILE_DESCRIPTION = COST_PROFILE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CostProfile_enterprise_id
	* Hibernate value: CostProfile.enterprise.id
	*/
	String  COST_PROFILE_ENTERPRISE_ID = COST_PROFILE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CostProfile_id
	* Hibernate value: CostProfile.id
	*/
	String  COST_PROFILE_ID = COST_PROFILE_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for DailyTracking entity.
	*/ 
	DAOConstantsEntry DAILY_TRACKING_ENTRY = DAOConstants.getDAOConstant(DailyTracking.class);

	/** 
	* Alias value: DailyTracking_activityType_id
	* Hibernate value: DailyTracking.activityType.id
	*/
	String  DAILY_TRACKING_ACTIVITY_TYPE_ID = DAILY_TRACKING_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: DailyTracking_comments
	* Hibernate value: DailyTracking.comments
	*/
	String  DAILY_TRACKING_COMMENTS = DAILY_TRACKING_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: DailyTracking_cost
	* Hibernate value: DailyTracking.cost
	*/
	String  DAILY_TRACKING_COST = DAILY_TRACKING_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: DailyTracking_enterprise_id
	* Hibernate value: DailyTracking.enterprise.id
	*/
	String  DAILY_TRACKING_ENTERPRISE_ID = DAILY_TRACKING_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: DailyTracking_id
	* Hibernate value: DailyTracking.id
	*/
	String  DAILY_TRACKING_ID = DAILY_TRACKING_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: DailyTracking_jobType_id
	* Hibernate value: DailyTracking.jobType.id
	*/
	String  DAILY_TRACKING_JOB_TYPE_ID = DAILY_TRACKING_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: DailyTracking_project_id
	* Hibernate value: DailyTracking.project.id
	*/
	String  DAILY_TRACKING_PROJECT_ID = DAILY_TRACKING_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: DailyTracking_registry_id
	* Hibernate value: DailyTracking.registry.id
	*/
	String  DAILY_TRACKING_REGISTRY_ID = DAILY_TRACKING_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: DailyTracking_taskHolder_id
	* Hibernate value: DailyTracking.taskHolder.id
	*/
	String  DAILY_TRACKING_TASK_HOLDER_ID = DAILY_TRACKING_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: DailyTracking_trackingDate
	* Hibernate value: DailyTracking.trackingDate
	*/
	String  DAILY_TRACKING_TRACKING_DATE = DAILY_TRACKING_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: DailyTracking_trackingDuration
	* Hibernate value: DailyTracking.trackingDuration
	*/
	String  DAILY_TRACKING_TRACKING_DURATION = DAILY_TRACKING_ENTRY.getAliasNames()[10];



	/** 
	* DAOConstantsEntry for Favorite entity.
	*/ 
	DAOConstantsEntry FAVORITE_ENTRY = DAOConstants.getDAOConstant(Favorite.class);

	/** 
	* Alias value: Favorite_description
	* Hibernate value: Favorite.description
	*/
	String  FAVORITE_DESCRIPTION = FAVORITE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Favorite_favoriteCategory_id
	* Hibernate value: Favorite.favoriteCategory.id
	*/
	String  FAVORITE_FAVORITE_CATEGORY_ID = FAVORITE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Favorite_id
	* Hibernate value: Favorite.id
	*/
	String  FAVORITE_ID = FAVORITE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Favorite_url
	* Hibernate value: Favorite.url
	*/
	String  FAVORITE_URL = FAVORITE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Favorite_user_id
	* Hibernate value: Favorite.user.id
	*/
	String  FAVORITE_USER_ID = FAVORITE_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for FavoriteCategory entity.
	*/ 
	DAOConstantsEntry FAVORITE_CATEGORY_ENTRY = DAOConstants.getDAOConstant(FavoriteCategory.class);

	/** 
	* Alias value: FavoriteCategory_description
	* Hibernate value: FavoriteCategory.description
	*/
	String  FAVORITE_CATEGORY_DESCRIPTION = FAVORITE_CATEGORY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: FavoriteCategory_id
	* Hibernate value: FavoriteCategory.id
	*/
	String  FAVORITE_CATEGORY_ID = FAVORITE_CATEGORY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: FavoriteCategory_user_id
	* Hibernate value: FavoriteCategory.user.id
	*/
	String  FAVORITE_CATEGORY_USER_ID = FAVORITE_CATEGORY_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for JobType entity.
	*/ 
	DAOConstantsEntry JOB_TYPE_ENTRY = DAOConstants.getDAOConstant(JobType.class);

	/** 
	* Alias value: JobType_description
	* Hibernate value: JobType.description
	*/
	String  JOB_TYPE_DESCRIPTION = JOB_TYPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: JobType_id
	* Hibernate value: JobType.id
	*/
	String  JOB_TYPE_ID = JOB_TYPE_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Note entity.
	*/ 
	DAOConstantsEntry NOTE_ENTRY = DAOConstants.getDAOConstant(Note.class);

	/** 
	* Alias value: Note_date
	* Hibernate value: Note.date
	*/
	String  NOTE_DATE = NOTE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Note_id
	* Hibernate value: Note.id
	*/
	String  NOTE_ID = NOTE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Note_note
	* Hibernate value: Note.note
	*/
	String  NOTE_NOTE = NOTE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Note_owner_id
	* Hibernate value: Note.owner.id
	*/
	String  NOTE_OWNER_ID = NOTE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Note_subject
	* Hibernate value: Note.subject
	*/
	String  NOTE_SUBJECT = NOTE_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for Notice entity.
	*/ 
	DAOConstantsEntry NOTICE_ENTRY = DAOConstants.getDAOConstant(Notice.class);

	/** 
	* Alias value: Notice_company
	* Hibernate value: Notice.company
	*/
	String  NOTICE_COMPANY = NOTICE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Notice_date
	* Hibernate value: Notice.date
	*/
	String  NOTICE_DATE = NOTICE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Notice_id
	* Hibernate value: Notice.id
	*/
	String  NOTICE_ID = NOTICE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Notice_phone
	* Hibernate value: Notice.phone
	*/
	String  NOTICE_PHONE = NOTICE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Notice_priority
	* Hibernate value: Notice.priority
	*/
	String  NOTICE_PRIORITY = NOTICE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Notice_recipient_id
	* Hibernate value: Notice.recipient.id
	*/
	String  NOTICE_RECIPIENT_ID = NOTICE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Notice_sender_id
	* Hibernate value: Notice.sender.id
	*/
	String  NOTICE_SENDER_ID = NOTICE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Notice_source
	* Hibernate value: Notice.source
	*/
	String  NOTICE_SOURCE = NOTICE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Notice_status
	* Hibernate value: Notice.status
	*/
	String  NOTICE_STATUS = NOTICE_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Notice_subject
	* Hibernate value: Notice.subject
	*/
	String  NOTICE_SUBJECT = NOTICE_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Notice_type
	* Hibernate value: Notice.type
	*/
	String  NOTICE_TYPE = NOTICE_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Notice_workGroup_id
	* Hibernate value: Notice.workGroup.id
	*/
	String  NOTICE_WORK_GROUP_ID = NOTICE_ENTRY.getAliasNames()[11];



	/** 
	* DAOConstantsEntry for Process entity.
	*/ 
	DAOConstantsEntry PROCESS_ENTRY = DAOConstants.getDAOConstant(Process.class);

	/** 
	* Alias value: Process_active
	* Hibernate value: Process.active
	*/
	String  PROCESS_ACTIVE = PROCESS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Process_description
	* Hibernate value: Process.description
	*/
	String  PROCESS_DESCRIPTION = PROCESS_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Process_enterprise_id
	* Hibernate value: Process.enterprise.id
	*/
	String  PROCESS_ENTERPRISE_ID = PROCESS_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Process_id
	* Hibernate value: Process.id
	*/
	String  PROCESS_ID = PROCESS_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for ProcessDetail entity.
	*/ 
	DAOConstantsEntry PROCESS_DETAIL_ENTRY = DAOConstants.getDAOConstant(ProcessDetail.class);

	/** 
	* Alias value: ProcessDetail_active
	* Hibernate value: ProcessDetail.active
	*/
	String  PROCESS_DETAIL_ACTIVE = PROCESS_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProcessDetail_alertDays
	* Hibernate value: ProcessDetail.alertDays
	*/
	String  PROCESS_DETAIL_ALERT_DAYS = PROCESS_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProcessDetail_comments
	* Hibernate value: ProcessDetail.comments
	*/
	String  PROCESS_DETAIL_COMMENTS = PROCESS_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ProcessDetail_dateReference
	* Hibernate value: ProcessDetail.dateReference
	*/
	String  PROCESS_DETAIL_DATE_REFERENCE = PROCESS_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ProcessDetail_days
	* Hibernate value: ProcessDetail.days
	*/
	String  PROCESS_DETAIL_DAYS = PROCESS_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ProcessDetail_description
	* Hibernate value: ProcessDetail.description
	*/
	String  PROCESS_DETAIL_DESCRIPTION = PROCESS_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ProcessDetail_id
	* Hibernate value: ProcessDetail.id
	*/
	String  PROCESS_DETAIL_ID = PROCESS_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ProcessDetail_position
	* Hibernate value: ProcessDetail.position
	*/
	String  PROCESS_DETAIL_POSITION = PROCESS_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ProcessDetail_priority
	* Hibernate value: ProcessDetail.priority
	*/
	String  PROCESS_DETAIL_PRIORITY = PROCESS_DETAIL_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: ProcessDetail_process_id
	* Hibernate value: ProcessDetail.process.id
	*/
	String  PROCESS_DETAIL_PROCESS_ID = PROCESS_DETAIL_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: ProcessDetail_workgroup_id
	* Hibernate value: ProcessDetail.workgroup.id
	*/
	String  PROCESS_DETAIL_WORKGROUP_ID = PROCESS_DETAIL_ENTRY.getAliasNames()[10];



	/** 
	* DAOConstantsEntry for ProcessDetailTransition entity.
	*/ 
	DAOConstantsEntry PROCESS_DETAIL_TRANSITION_ENTRY = DAOConstants.getDAOConstant(ProcessDetailTransition.class);

	/** 
	* Alias value: ProcessDetailTransition_id
	* Hibernate value: ProcessDetailTransition.id
	*/
	String  PROCESS_DETAIL_TRANSITION_ID = PROCESS_DETAIL_TRANSITION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProcessDetailTransition_nextProcessDetail_id
	* Hibernate value: ProcessDetailTransition.nextProcessDetail.id
	*/
	String  PROCESS_DETAIL_TRANSITION_NEXT_PROCESS_DETAIL_ID = PROCESS_DETAIL_TRANSITION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProcessDetailTransition_processDetail_id
	* Hibernate value: ProcessDetailTransition.processDetail.id
	*/
	String  PROCESS_DETAIL_TRANSITION_PROCESS_DETAIL_ID = PROCESS_DETAIL_TRANSITION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ProcessDetailTransition_processTransitionType_id
	* Hibernate value: ProcessDetailTransition.processTransitionType.id
	*/
	String  PROCESS_DETAIL_TRANSITION_PROCESS_TRANSITION_TYPE_ID = PROCESS_DETAIL_TRANSITION_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for ProcessTask entity.
	*/ 
	DAOConstantsEntry PROCESS_TASK_ENTRY = DAOConstants.getDAOConstant(ProcessTask.class);

	/** 
	* Alias value: ProcessTask_campaign_id
	* Hibernate value: ProcessTask.campaign.id
	*/
	String  PROCESS_TASK_CAMPAIGN_ID = PROCESS_TASK_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProcessTask_id
	* Hibernate value: ProcessTask.id
	*/
	String  PROCESS_TASK_ID = PROCESS_TASK_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProcessTask_processDetail_id
	* Hibernate value: ProcessTask.processDetail.id
	*/
	String  PROCESS_TASK_PROCESS_DETAIL_ID = PROCESS_TASK_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ProcessTask_task_id
	* Hibernate value: ProcessTask.task.id
	*/
	String  PROCESS_TASK_TASK_ID = PROCESS_TASK_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ProcessTask_task_status
	* Hibernate value: ProcessTask.task.status
	*/
	String  PROCESS_TASK_TASK_STATUS = PROCESS_TASK_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ProcessTask_task_project_id
	* Hibernate value: ProcessTask.task.project.id
	*/
	String  PROCESS_TASK_TASK_PROJECT_ID = PROCESS_TASK_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for ProcessTransitionType entity.
	*/ 
	DAOConstantsEntry PROCESS_TRANSITION_TYPE_ENTRY = DAOConstants.getDAOConstant(ProcessTransitionType.class);

	/** 
	* Alias value: ProcessTransitionType_description
	* Hibernate value: ProcessTransitionType.description
	*/
	String  PROCESS_TRANSITION_TYPE_DESCRIPTION = PROCESS_TRANSITION_TYPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProcessTransitionType_id
	* Hibernate value: ProcessTransitionType.id
	*/
	String  PROCESS_TRANSITION_TYPE_ID = PROCESS_TRANSITION_TYPE_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Task entity.
	*/ 
	DAOConstantsEntry TASK_ENTRY = DAOConstants.getDAOConstant(Task.class);

	/** 
	* Alias value: Task_activityType_id
	* Hibernate value: Task.activityType.id
	*/
	String  TASK_ACTIVITY_TYPE_ID = TASK_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Task_comments
	* Hibernate value: Task.comments
	*/
	String  TASK_COMMENTS = TASK_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Task_description
	* Hibernate value: Task.description
	*/
	String  TASK_DESCRIPTION = TASK_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Task_dueDate
	* Hibernate value: Task.dueDate
	*/
	String  TASK_DUE_DATE = TASK_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Task_endDate
	* Hibernate value: Task.endDate
	*/
	String  TASK_END_DATE = TASK_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Task_enterprise_id
	* Hibernate value: Task.enterprise.id
	*/
	String  TASK_ENTERPRISE_ID = TASK_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Task_id
	* Hibernate value: Task.id
	*/
	String  TASK_ID = TASK_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Task_percent
	* Hibernate value: Task.percent
	*/
	String  TASK_PERCENT = TASK_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Task_priority
	* Hibernate value: Task.priority
	*/
	String  TASK_PRIORITY = TASK_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Task_project_id
	* Hibernate value: Task.project<id
	*/
	String  TASK_PROJECT_ID = TASK_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Task_project_name
	* Hibernate value: Task.project<name
	*/
	String  TASK_PROJECT_NAME = TASK_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Task_project_projectType_id
	* Hibernate value: Task.project.projectType<id
	*/
	String  TASK_PROJECT_PROJECT_TYPE_ID = TASK_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Task_registry_id
	* Hibernate value: Task.registry<id
	*/
	String  TASK_REGISTRY_ID = TASK_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Task_registry_name
	* Hibernate value: Task.registry<name
	*/
	String  TASK_REGISTRY_NAME = TASK_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Task_repeatPeriod
	* Hibernate value: Task.repeatPeriod
	*/
	String  TASK_REPEAT_PERIOD = TASK_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Task_sender_id
	* Hibernate value: Task.sender.id
	*/
	String  TASK_SENDER_ID = TASK_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Task_source
	* Hibernate value: Task.source
	*/
	String  TASK_SOURCE = TASK_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Task_startDate
	* Hibernate value: Task.startDate
	*/
	String  TASK_START_DATE = TASK_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Task_status
	* Hibernate value: Task.status
	*/
	String  TASK_STATUS = TASK_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Task_taskHolder_id
	* Hibernate value: Task.taskHolder<id
	*/
	String  TASK_TASK_HOLDER_ID = TASK_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Task_workGroup_id
	* Hibernate value: Task.workGroup.id
	*/
	String  TASK_WORK_GROUP_ID = TASK_ENTRY.getAliasNames()[20];



	/** 
	* DAOConstantsEntry for TaskHolder entity.
	*/ 
	DAOConstantsEntry TASK_HOLDER_ENTRY = DAOConstants.getDAOConstant(TaskHolder.class);

	/** 
	* Alias value: TaskHolder_id
	* Hibernate value: TaskHolder.id
	*/
	String  TASK_HOLDER_ID = TASK_HOLDER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: TaskHolder_active
	* Hibernate value: TaskHolder.active
	*/
	String  TASK_HOLDER_ACTIVE = TASK_HOLDER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: TaskHolder_costProfile_id
	* Hibernate value: TaskHolder.costProfile.id
	*/
	String  TASK_HOLDER_COST_PROFILE_ID = TASK_HOLDER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: TaskHolder_costProfile_description
	* Hibernate value: TaskHolder.costProfile.description
	*/
	String  TASK_HOLDER_COST_PROFILE_DESCRIPTION = TASK_HOLDER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: TaskHolder_registry_id
	* Hibernate value: TaskHolder.registry.id
	*/
	String  TASK_HOLDER_REGISTRY_ID = TASK_HOLDER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: TaskHolder_registry_name
	* Hibernate value: TaskHolder.registry.name
	*/
	String  TASK_HOLDER_REGISTRY_NAME = TASK_HOLDER_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: TaskHolder_registry_alias
	* Hibernate value: TaskHolder.registry.alias
	*/
	String  TASK_HOLDER_REGISTRY_ALIAS = TASK_HOLDER_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: TaskHolder_type
	* Hibernate value: TaskHolder.type
	*/
	String  TASK_HOLDER_TYPE = TASK_HOLDER_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: TaskHolder_user_id
	* Hibernate value: TaskHolder.user<id
	*/
	String  TASK_HOLDER_USER_ID = TASK_HOLDER_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: TaskHolder_user_name
	* Hibernate value: TaskHolder.user<name
	*/
	String  TASK_HOLDER_USER_NAME = TASK_HOLDER_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: TaskHolder_user_alias
	* Hibernate value: TaskHolder.user<alias
	*/
	String  TASK_HOLDER_USER_ALIAS = TASK_HOLDER_ENTRY.getAliasNames()[10];



	/** 
	* DAOConstantsEntry for TaskHolderWorkgroup entity.
	*/ 
	DAOConstantsEntry TASK_HOLDER_WORKGROUP_ENTRY = DAOConstants.getDAOConstant(TaskHolderWorkgroup.class);

	/** 
	* Alias value: TaskHolderWorkgroup_id
	* Hibernate value: TaskHolderWorkgroup.id
	*/
	String  TASK_HOLDER_WORKGROUP_ID = TASK_HOLDER_WORKGROUP_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: TaskHolderWorkgroup_taskHolder_id
	* Hibernate value: TaskHolderWorkgroup.taskHolder.id
	*/
	String  TASK_HOLDER_WORKGROUP_TASK_HOLDER_ID = TASK_HOLDER_WORKGROUP_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: TaskHolderWorkgroup_taskHolder_active
	* Hibernate value: TaskHolderWorkgroup.taskHolder.active
	*/
	String  TASK_HOLDER_WORKGROUP_TASK_HOLDER_ACTIVE = TASK_HOLDER_WORKGROUP_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: TaskHolderWorkgroup_workGroup_id
	* Hibernate value: TaskHolderWorkgroup.workGroup.id
	*/
	String  TASK_HOLDER_WORKGROUP_WORK_GROUP_ID = TASK_HOLDER_WORKGROUP_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: TaskHolderWorkgroup_workGroup_status
	* Hibernate value: TaskHolderWorkgroup.workGroup.status
	*/
	String  TASK_HOLDER_WORKGROUP_WORK_GROUP_STATUS = TASK_HOLDER_WORKGROUP_ENTRY.getAliasNames()[4];


}