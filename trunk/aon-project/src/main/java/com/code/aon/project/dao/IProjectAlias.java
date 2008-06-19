package com.code.aon.project.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.project.Activity;
import com.code.aon.project.ActivityType;
import com.code.aon.project.DailyTracking;
import com.code.aon.project.Dossier;
import com.code.aon.project.DossierType;
import com.code.aon.project.JobType;
import com.code.aon.project.Task;
import com.code.aon.project.PeriodicalTask;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IProjectAlias {



	/** 
	* DAOConstantsEntry for Activity entity.
	*/ 
	DAOConstantsEntry ACTIVITY_ENTRY = DAOConstants.getDAOConstant(Activity.class);

	/** 
	* Alias value: Activity_id
	* Hibernate value: Activity.id
	*/
	String  ACTIVITY_ID = ACTIVITY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Activity_dossier_id
	* Hibernate value: Activity.dossier.id
	*/
	String  ACTIVITY_DOSSIER_ID = ACTIVITY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Activity_activityType_id
	* Hibernate value: Activity.activityType.id
	*/
	String  ACTIVITY_ACTIVITY_TYPE_ID = ACTIVITY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Activity_workgroup_id
	* Hibernate value: Activity.workgroup.id
	*/
	String  ACTIVITY_WORKGROUP_ID = ACTIVITY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Activity_dossier_number
	* Hibernate value: Activity.dossier.number
	*/
	String  ACTIVITY_DOSSIER_NUMBER = ACTIVITY_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Activity_customer_document
	* Hibernate value: Activity.dossier.customer.registry.document
	*/
	String  ACTIVITY_CUSTOMER_DOCUMENT = ACTIVITY_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Activity_customer_name
	* Hibernate value: Activity.dossier.customer.registry.name
	*/
	String  ACTIVITY_CUSTOMER_NAME = ACTIVITY_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Activity_customer_surname
	* Hibernate value: Activity.dossier.customer.registry.surname
	*/
	String  ACTIVITY_CUSTOMER_SURNAME = ACTIVITY_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Activity_dossier_status
	* Hibernate value: Activity.dossier.status
	*/
	String  ACTIVITY_DOSSIER_STATUS = ACTIVITY_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for ActivityType entity.
	*/ 
	DAOConstantsEntry ACTIVITY_TYPE_ENTRY = DAOConstants.getDAOConstant(ActivityType.class);

	/** 
	* Alias value: ActivityType_id
	* Hibernate value: ActivityType.id
	*/
	String  ACTIVITY_TYPE_ID = ACTIVITY_TYPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ActivityType_description
	* Hibernate value: ActivityType.description
	*/
	String  ACTIVITY_TYPE_DESCRIPTION = ACTIVITY_TYPE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ActivityType_dossierType_id
	* Hibernate value: ActivityType.dossierType<id
	*/
	String  ACTIVITY_TYPE_DOSSIER_TYPE_ID = ACTIVITY_TYPE_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for DailyTracking entity.
	*/ 
	DAOConstantsEntry DAILY_TRACKING_ENTRY = DAOConstants.getDAOConstant(DailyTracking.class);

	/** 
	* Alias value: DailyTracking_id
	* Hibernate value: DailyTracking.id
	*/
	String  DAILY_TRACKING_ID = DAILY_TRACKING_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: DailyTracking_user_id
	* Hibernate value: DailyTracking.user.id
	*/
	String  DAILY_TRACKING_USER_ID = DAILY_TRACKING_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: DailyTracking_trackingDate
	* Hibernate value: DailyTracking.trackingDate
	*/
	String  DAILY_TRACKING_TRACKING_DATE = DAILY_TRACKING_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: DailyTracking_trackingDuration
	* Hibernate value: DailyTracking.trackingDuration
	*/
	String  DAILY_TRACKING_TRACKING_DURATION = DAILY_TRACKING_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: DailyTracking_jobType_id
	* Hibernate value: DailyTracking.jobType.id
	*/
	String  DAILY_TRACKING_JOB_TYPE_ID = DAILY_TRACKING_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: DailyTracking_customer_id
	* Hibernate value: DailyTracking.customer.id
	*/
	String  DAILY_TRACKING_CUSTOMER_ID = DAILY_TRACKING_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: DailyTracking_customer_registry_name
	* Hibernate value: DailyTracking.customer.registry.name
	*/
	String  DAILY_TRACKING_CUSTOMER_REGISTRY_NAME = DAILY_TRACKING_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: DailyTracking_customer_registry_surname
	* Hibernate value: DailyTracking.customer.registry.surname
	*/
	String  DAILY_TRACKING_CUSTOMER_REGISTRY_SURNAME = DAILY_TRACKING_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: DailyTracking_customer_status
	* Hibernate value: DailyTracking.customer.status
	*/
	String  DAILY_TRACKING_CUSTOMER_STATUS = DAILY_TRACKING_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: DailyTracking_dossier_id
	* Hibernate value: DailyTracking.dossier.id
	*/
	String  DAILY_TRACKING_DOSSIER_ID = DAILY_TRACKING_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: DailyTracking_dossier_number
	* Hibernate value: DailyTracking.dossier.number
	*/
	String  DAILY_TRACKING_DOSSIER_NUMBER = DAILY_TRACKING_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: DailyTracking_activity_id
	* Hibernate value: DailyTracking.activity.id
	*/
	String  DAILY_TRACKING_ACTIVITY_ID = DAILY_TRACKING_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: DailyTracking_comments
	* Hibernate value: DailyTracking.comments
	*/
	String  DAILY_TRACKING_COMMENTS = DAILY_TRACKING_ENTRY.getAliasNames()[12];



	/** 
	* DAOConstantsEntry for Dossier entity.
	*/ 
	DAOConstantsEntry DOSSIER_ENTRY = DAOConstants.getDAOConstant(Dossier.class);

	/** 
	* Alias value: Dossier_customer_id
	* Hibernate value: Dossier.customer.id
	*/
	String  DOSSIER_CUSTOMER_ID = DOSSIER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Dossier_dossierType_id
	* Hibernate value: Dossier.dossierType.id
	*/
	String  DOSSIER_DOSSIER_TYPE_ID = DOSSIER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Dossier_id
	* Hibernate value: Dossier.id
	*/
	String  DOSSIER_ID = DOSSIER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Dossier_location
	* Hibernate value: Dossier.location
	*/
	String  DOSSIER_LOCATION = DOSSIER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Dossier_number
	* Hibernate value: Dossier.number
	*/
	String  DOSSIER_NUMBER = DOSSIER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Dossier_status
	* Hibernate value: Dossier.status
	*/
	String  DOSSIER_STATUS = DOSSIER_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for DossierType entity.
	*/ 
	DAOConstantsEntry DOSSIER_TYPE_ENTRY = DAOConstants.getDAOConstant(DossierType.class);

	/** 
	* Alias value: DossierType_description
	* Hibernate value: DossierType.description
	*/
	String  DOSSIER_TYPE_DESCRIPTION = DOSSIER_TYPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: DossierType_id
	* Hibernate value: DossierType.id
	*/
	String  DOSSIER_TYPE_ID = DOSSIER_TYPE_ENTRY.getAliasNames()[1];



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
	* DAOConstantsEntry for Task entity.
	*/ 
	DAOConstantsEntry TASK_ENTRY = DAOConstants.getDAOConstant(Task.class);

	/** 
	* Alias value: Task_id
	* Hibernate value: Task.id
	*/
	String  TASK_ID = TASK_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Task_description
	* Hibernate value: Task.description
	*/
	String  TASK_DESCRIPTION = TASK_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Task_startDate
	* Hibernate value: Task.startDate
	*/
	String  TASK_START_DATE = TASK_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Task_endDate
	* Hibernate value: Task.endDate
	*/
	String  TASK_END_DATE = TASK_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Task_dueDate
	* Hibernate value: Task.dueDate
	*/
	String  TASK_DUE_DATE = TASK_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Task_priority
	* Hibernate value: Task.priority
	*/
	String  TASK_PRIORITY = TASK_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Task_status
	* Hibernate value: Task.status
	*/
	String  TASK_STATUS = TASK_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Task_percent
	* Hibernate value: Task.percent
	*/
	String  TASK_PERCENT = TASK_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Task_user_id
	* Hibernate value: Task.user<id
	*/
	String  TASK_USER_ID = TASK_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Task_user_name
	* Hibernate value: Task.user<name
	*/
	String  TASK_USER_NAME = TASK_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Task_workGroup_id
	* Hibernate value: Task.workGroup.id
	*/
	String  TASK_WORK_GROUP_ID = TASK_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Task_source
	* Hibernate value: Task.source
	*/
	String  TASK_SOURCE = TASK_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Task_dossier_id
	* Hibernate value: Task.dossier<id
	*/
	String  TASK_DOSSIER_ID = TASK_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Task_dossier_number
	* Hibernate value: Task.dossier<number
	*/
	String  TASK_DOSSIER_NUMBER = TASK_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Task_activity_id
	* Hibernate value: Task.activity.id
	*/
	String  TASK_ACTIVITY_ID = TASK_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Task_sender_id
	* Hibernate value: Task.sender.id
	*/
	String  TASK_SENDER_ID = TASK_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Task_comments
	* Hibernate value: Task.comments
	*/
	String  TASK_COMMENTS = TASK_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Task_customer_id
	* Hibernate value: Task.dossier.customer.id
	*/
	String  TASK_CUSTOMER_ID = TASK_ENTRY.getAliasNames()[17];



	/** 
	* DAOConstantsEntry for PeriodicalTask entity.
	*/ 
	DAOConstantsEntry PERIODICAL_TASK_ENTRY = DAOConstants.getDAOConstant(PeriodicalTask.class);

	/** 
	* Alias value: PeriodicalTask_endDate
	* Hibernate value: PeriodicalTask.endDate
	*/
	String  PERIODICAL_TASK_END_DATE = PERIODICAL_TASK_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: PeriodicalTask_id
	* Hibernate value: PeriodicalTask.id
	*/
	String  PERIODICAL_TASK_ID = PERIODICAL_TASK_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: PeriodicalTask_nextDate
	* Hibernate value: PeriodicalTask.nextDate
	*/
	String  PERIODICAL_TASK_NEXT_DATE = PERIODICAL_TASK_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: PeriodicalTask_owner_id
	* Hibernate value: PeriodicalTask.owner.id
	*/
	String  PERIODICAL_TASK_OWNER_ID = PERIODICAL_TASK_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: PeriodicalTask_period
	* Hibernate value: PeriodicalTask.period
	*/
	String  PERIODICAL_TASK_PERIOD = PERIODICAL_TASK_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: PeriodicalTask_quantity
	* Hibernate value: PeriodicalTask.quantity
	*/
	String  PERIODICAL_TASK_QUANTITY = PERIODICAL_TASK_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: PeriodicalTask_startDate
	* Hibernate value: PeriodicalTask.startDate
	*/
	String  PERIODICAL_TASK_START_DATE = PERIODICAL_TASK_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: PeriodicalTask_task_id
	* Hibernate value: PeriodicalTask.task.id
	*/
	String  PERIODICAL_TASK_TASK_ID = PERIODICAL_TASK_ENTRY.getAliasNames()[7];


}