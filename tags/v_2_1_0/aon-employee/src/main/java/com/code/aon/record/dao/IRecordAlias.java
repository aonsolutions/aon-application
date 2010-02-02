package com.code.aon.record.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.record.Work;
import com.code.aon.record.Course;
import com.code.aon.record.Position;
import com.code.aon.record.Contract;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IRecordAlias {



	/** 
	* DAOConstantsEntry for Work entity.
	*/ 
	DAOConstantsEntry WORK_ENTRY = DAOConstants.getDAOConstant(Work.class);

	/** 
	* Alias value: Work_description
	* Hibernate value: Work.description
	*/
	String  WORK_DESCRIPTION = WORK_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Work_employee_id
	* Hibernate value: Work.employee.id
	*/
	String  WORK_EMPLOYEE_ID = WORK_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Work_endingDate
	* Hibernate value: Work.endingDate
	*/
	String  WORK_ENDING_DATE = WORK_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Work_id
	* Hibernate value: Work.id
	*/
	String  WORK_ID = WORK_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Work_startingDate
	* Hibernate value: Work.startingDate
	*/
	String  WORK_STARTING_DATE = WORK_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for Course entity.
	*/ 
	DAOConstantsEntry COURSE_ENTRY = DAOConstants.getDAOConstant(Course.class);

	/** 
	* Alias value: Course_description
	* Hibernate value: Course.description
	*/
	String  COURSE_DESCRIPTION = COURSE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Course_employee_id
	* Hibernate value: Course.employee.id
	*/
	String  COURSE_EMPLOYEE_ID = COURSE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Course_endingDate
	* Hibernate value: Course.endingDate
	*/
	String  COURSE_ENDING_DATE = COURSE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Course_id
	* Hibernate value: Course.id
	*/
	String  COURSE_ID = COURSE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Course_startingDate
	* Hibernate value: Course.startingDate
	*/
	String  COURSE_STARTING_DATE = COURSE_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for Position entity.
	*/ 
	DAOConstantsEntry POSITION_ENTRY = DAOConstants.getDAOConstant(Position.class);

	/** 
	* Alias value: Position_calendar
	* Hibernate value: Position.calendar
	*/
	String  POSITION_CALENDAR = POSITION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Position_description
	* Hibernate value: Position.description
	*/
	String  POSITION_DESCRIPTION = POSITION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Position_employee_id
	* Hibernate value: Position.employee.id
	*/
	String  POSITION_EMPLOYEE_ID = POSITION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Position_endingDate
	* Hibernate value: Position.endingDate
	*/
	String  POSITION_ENDING_DATE = POSITION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Position_id
	* Hibernate value: Position.id
	*/
	String  POSITION_ID = POSITION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Position_startingDate
	* Hibernate value: Position.startingDate
	*/
	String  POSITION_STARTING_DATE = POSITION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Position_workActivity_id
	* Hibernate value: Position.workActivity.id
	*/
	String  POSITION_WORK_ACTIVITY_ID = POSITION_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Position_workPlace_id
	* Hibernate value: Position.workPlace.id
	*/
	String  POSITION_WORK_PLACE_ID = POSITION_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for Contract entity.
	*/ 
	DAOConstantsEntry CONTRACT_ENTRY = DAOConstants.getDAOConstant(Contract.class);

	/** 
	* Alias value: Contract_contractType
	* Hibernate value: Contract.contractType
	*/
	String  CONTRACT_CONTRACT_TYPE = CONTRACT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Contract_employee_id
	* Hibernate value: Contract.employee.id
	*/
	String  CONTRACT_EMPLOYEE_ID = CONTRACT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Contract_endingDate
	* Hibernate value: Contract.endingDate
	*/
	String  CONTRACT_ENDING_DATE = CONTRACT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Contract_grossSalary
	* Hibernate value: Contract.grossSalary
	*/
	String  CONTRACT_GROSS_SALARY = CONTRACT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Contract_id
	* Hibernate value: Contract.id
	*/
	String  CONTRACT_ID = CONTRACT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Contract_startingDate
	* Hibernate value: Contract.startingDate
	*/
	String  CONTRACT_STARTING_DATE = CONTRACT_ENTRY.getAliasNames()[5];


}