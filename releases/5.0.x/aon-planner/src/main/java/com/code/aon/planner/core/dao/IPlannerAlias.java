package com.code.aon.planner.core.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.planner.core.Calendar;
import com.code.aon.planner.core.IncidenceType;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IPlannerAlias {

	/** 
	* DAOConstantsEntry for Calendar entity.
	*/ 
	DAOConstantsEntry CALENDAR_ENTRY = DAOConstants.getDAOConstant(Calendar.class);

	/** 
	* Alias value: Calendar_addSpreadEventAllowed
	* Hibernate value: Calendar.addSpreadEventAllowed
	*/
	String  CALENDAR_ADDSPREADEVENTALLOWED = CALENDAR_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Calendar_data
	* Hibernate value: Calendar.data
	*/
	String  CALENDAR_DATA = CALENDAR_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Calendar_description
	* Hibernate value: Calendar.description
	*/
	String  CALENDAR_DESCRIPTION = CALENDAR_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Calendar_id
	* Hibernate value: Calendar.id
	*/
	String  CALENDAR_ID = CALENDAR_ENTRY.getAliasNames()[3];


	/** 
	* DAOConstantsEntry for IncidenceType entity.
	*/ 
	DAOConstantsEntry INCIDENCE_TYPE_ENTRY = DAOConstants.getDAOConstant(IncidenceType.class);

	/** 
	* Alias value: IncidenceType_alias
	* Hibernate value: IncidenceType.alias
	*/
	String  INCIDENCE_TYPE_ALIAS = INCIDENCE_TYPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: IncidenceType_compute
	* Hibernate value: IncidenceType.compute
	*/
	String  INCIDENCE_TYPE_COMPUTE = INCIDENCE_TYPE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: IncidenceType_description
	* Hibernate value: IncidenceType.description
	*/
	String  INCIDENCE_TYPE_DESCRIPTION = INCIDENCE_TYPE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: IncidenceType_id
	* Hibernate value: IncidenceType.id
	*/
	String  INCIDENCE_TYPE_ID = INCIDENCE_TYPE_ENTRY.getAliasNames()[3];


}