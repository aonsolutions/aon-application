package com.code.aon.consultant.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.consultant.RecordData;
import com.code.aon.consultant.RegistryDirStaff;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IConsultantAlias {



	/** 
	* DAOConstantsEntry for RecordData entity.
	*/ 
	DAOConstantsEntry RECORD_DATA_ENTRY = DAOConstants.getDAOConstant(RecordData.class);

	/** 
	* Alias value: RecordData_attach_id
	* Hibernate value: RecordData.attach.id
	*/
	String  RECORD_DATA_ATTACH_ID = RECORD_DATA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: RecordData_creationDate
	* Hibernate value: RecordData.creationDate
	*/
	String  RECORD_DATA_CREATION_DATE = RECORD_DATA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: RecordData_description
	* Hibernate value: RecordData.description
	*/
	String  RECORD_DATA_DESCRIPTION = RECORD_DATA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: RecordData_id
	* Hibernate value: RecordData.id
	*/
	String  RECORD_DATA_ID = RECORD_DATA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: RecordData_notary
	* Hibernate value: RecordData.notary
	*/
	String  RECORD_DATA_NOTARY = RECORD_DATA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: RecordData_number
	* Hibernate value: RecordData.number
	*/
	String  RECORD_DATA_NUMBER = RECORD_DATA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: RecordData_page
	* Hibernate value: RecordData.page
	*/
	String  RECORD_DATA_PAGE = RECORD_DATA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: RecordData_recordDate
	* Hibernate value: RecordData.recordDate
	*/
	String  RECORD_DATA_RECORD_DATE = RECORD_DATA_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: RecordData_registration
	* Hibernate value: RecordData.registration
	*/
	String  RECORD_DATA_REGISTRATION = RECORD_DATA_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: RecordData_registry_id
	* Hibernate value: RecordData.registry.id
	*/
	String  RECORD_DATA_REGISTRY_ID = RECORD_DATA_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: RecordData_section
	* Hibernate value: RecordData.section
	*/
	String  RECORD_DATA_SECTION = RECORD_DATA_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: RecordData_sheet
	* Hibernate value: RecordData.sheet
	*/
	String  RECORD_DATA_SHEET = RECORD_DATA_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: RecordData_volume
	* Hibernate value: RecordData.volume
	*/
	String  RECORD_DATA_VOLUME = RECORD_DATA_ENTRY.getAliasNames()[12];



	/** 
	* DAOConstantsEntry for RegistryDirStaff entity.
	*/ 
	DAOConstantsEntry REGISTRY_DIR_STAFF_ENTRY = DAOConstants.getDAOConstant(RegistryDirStaff.class);

	/** 
	* Alias value: RegistryDirStaff_director
	* Hibernate value: RegistryDirStaff.director
	*/
	String  REGISTRY_DIR_STAFF_DIRECTOR = REGISTRY_DIR_STAFF_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: RegistryDirStaff_document
	* Hibernate value: RegistryDirStaff.document
	*/
	String  REGISTRY_DIR_STAFF_DOCUMENT = REGISTRY_DIR_STAFF_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: RegistryDirStaff_dueDate
	* Hibernate value: RegistryDirStaff.dueDate
	*/
	String  REGISTRY_DIR_STAFF_DUE_DATE = REGISTRY_DIR_STAFF_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: RegistryDirStaff_id
	* Hibernate value: RegistryDirStaff.id
	*/
	String  REGISTRY_DIR_STAFF_ID = REGISTRY_DIR_STAFF_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: RegistryDirStaff_name
	* Hibernate value: RegistryDirStaff.name
	*/
	String  REGISTRY_DIR_STAFF_NAME = REGISTRY_DIR_STAFF_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: RegistryDirStaff_nominalValue
	* Hibernate value: RegistryDirStaff.nominalValue
	*/
	String  REGISTRY_DIR_STAFF_NOMINAL_VALUE = REGISTRY_DIR_STAFF_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: RegistryDirStaff_percentShare
	* Hibernate value: RegistryDirStaff.percentShare
	*/
	String  REGISTRY_DIR_STAFF_PERCENT_SHARE = REGISTRY_DIR_STAFF_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: RegistryDirStaff_registry_id
	* Hibernate value: RegistryDirStaff.registry.id
	*/
	String  REGISTRY_DIR_STAFF_REGISTRY_ID = REGISTRY_DIR_STAFF_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: RegistryDirStaff_representative
	* Hibernate value: RegistryDirStaff.representative
	*/
	String  REGISTRY_DIR_STAFF_REPRESENTATIVE = REGISTRY_DIR_STAFF_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: RegistryDirStaff_shareHolder
	* Hibernate value: RegistryDirStaff.shareHolder
	*/
	String  REGISTRY_DIR_STAFF_SHARE_HOLDER = REGISTRY_DIR_STAFF_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: RegistryDirStaff_shareNumber
	* Hibernate value: RegistryDirStaff.shareNumber
	*/
	String  REGISTRY_DIR_STAFF_SHARE_NUMBER = REGISTRY_DIR_STAFF_ENTRY.getAliasNames()[10];


}