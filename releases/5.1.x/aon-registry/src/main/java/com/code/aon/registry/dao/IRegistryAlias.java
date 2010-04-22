package com.code.aon.registry.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.registry.Category;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddInfo;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.RegistryNote;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.RegistryRelationship;
import com.code.aon.registry.RegistrySegment;
import com.code.aon.registry.Relationship;
import com.code.aon.registry.Segment;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IRegistryAlias {



	/** 
	* DAOConstantsEntry for Category entity.
	*/ 
	DAOConstantsEntry CATEGORY_ENTRY = DAOConstants.getDAOConstant(Category.class);

	/** 
	* Alias value: Category_id
	* Hibernate value: Category.id
	*/
	String  CATEGORY_ID = CATEGORY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Category_name
	* Hibernate value: Category.name
	*/
	String  CATEGORY_NAME = CATEGORY_ENTRY.getAliasNames()[1];



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
	* DAOConstantsEntry for Registry entity.
	*/ 
	DAOConstantsEntry REGISTRY_ENTRY = DAOConstants.getDAOConstant(Registry.class);

	/** 
	* Alias value: Registry_alias
	* Hibernate value: Registry.alias
	*/
	String  REGISTRY_ALIAS = REGISTRY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Registry_document
	* Hibernate value: Registry.document
	*/
	String  REGISTRY_DOCUMENT = REGISTRY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Registry_id
	* Hibernate value: Registry.id
	*/
	String  REGISTRY_ID = REGISTRY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Registry_name
	* Hibernate value: Registry.name
	*/
	String  REGISTRY_NAME = REGISTRY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Registry_surname
	* Hibernate value: Registry.surname
	*/
	String  REGISTRY_SURNAME = REGISTRY_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Registry_type
	* Hibernate value: Registry.type
	*/
	String  REGISTRY_TYPE = REGISTRY_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for RegistryAddInfo entity.
	*/ 
	DAOConstantsEntry REGISTRY_ADD_INFO_ENTRY = DAOConstants.getDAOConstant(RegistryAddInfo.class);

	/** 
	* Alias value: RegistryAddInfo_attribute
	* Hibernate value: RegistryAddInfo.attribute
	*/
	String  REGISTRY_ADD_INFO_ATTRIBUTE = REGISTRY_ADD_INFO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: RegistryAddInfo_id
	* Hibernate value: RegistryAddInfo.id
	*/
	String  REGISTRY_ADD_INFO_ID = REGISTRY_ADD_INFO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: RegistryAddInfo_registry_id
	* Hibernate value: RegistryAddInfo.registry.id
	*/
	String  REGISTRY_ADD_INFO_REGISTRY_ID = REGISTRY_ADD_INFO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: RegistryAddInfo_value
	* Hibernate value: RegistryAddInfo.value
	*/
	String  REGISTRY_ADD_INFO_VALUE = REGISTRY_ADD_INFO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: RegistryAddInfo_valueDate
	* Hibernate value: RegistryAddInfo.valueDate
	*/
	String  REGISTRY_ADD_INFO_VALUE_DATE = REGISTRY_ADD_INFO_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for RegistryAddress entity.
	*/ 
	DAOConstantsEntry REGISTRY_ADDRESS_ENTRY = DAOConstants.getDAOConstant(RegistryAddress.class);

	/** 
	* Alias value: RegistryAddress_address
	* Hibernate value: RegistryAddress.address
	*/
	String  REGISTRY_ADDRESS_ADDRESS = REGISTRY_ADDRESS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: RegistryAddress_address2
	* Hibernate value: RegistryAddress.address2
	*/
	String  REGISTRY_ADDRESS_ADDRESS2 = REGISTRY_ADDRESS_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: RegistryAddress_address3
	* Hibernate value: RegistryAddress.address3
	*/
	String  REGISTRY_ADDRESS_ADDRESS3 = REGISTRY_ADDRESS_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: RegistryAddress_addressType
	* Hibernate value: RegistryAddress.addressType
	*/
	String  REGISTRY_ADDRESS_ADDRESS_TYPE = REGISTRY_ADDRESS_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: RegistryAddress_city
	* Hibernate value: RegistryAddress.city
	*/
	String  REGISTRY_ADDRESS_CITY = REGISTRY_ADDRESS_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: RegistryAddress_geozone_id
	* Hibernate value: RegistryAddress.geozone.id
	*/
	String  REGISTRY_ADDRESS_GEOZONE_ID = REGISTRY_ADDRESS_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: RegistryAddress_id
	* Hibernate value: RegistryAddress.id
	*/
	String  REGISTRY_ADDRESS_ID = REGISTRY_ADDRESS_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: RegistryAddress_recipient
	* Hibernate value: RegistryAddress.recipient
	*/
	String  REGISTRY_ADDRESS_RECIPIENT = REGISTRY_ADDRESS_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: RegistryAddress_registry_id
	* Hibernate value: RegistryAddress.registry.id
	*/
	String  REGISTRY_ADDRESS_REGISTRY_ID = REGISTRY_ADDRESS_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: RegistryAddress_streetType
	* Hibernate value: RegistryAddress.streetType
	*/
	String  REGISTRY_ADDRESS_STREET_TYPE = REGISTRY_ADDRESS_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: RegistryAddress_zip
	* Hibernate value: RegistryAddress.zip
	*/
	String  REGISTRY_ADDRESS_ZIP = REGISTRY_ADDRESS_ENTRY.getAliasNames()[10];



	/** 
	* DAOConstantsEntry for RegistryAttachment entity.
	*/ 
	DAOConstantsEntry REGISTRY_ATTACHMENT_ENTRY = DAOConstants.getDAOConstant(RegistryAttachment.class);

	/** 
	* Alias value: RegistryAttachment_category_id
	* Hibernate value: RegistryAttachment.category.id
	*/
	String  REGISTRY_ATTACHMENT_CATEGORY_ID = REGISTRY_ATTACHMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: RegistryAttachment_data
	* Hibernate value: RegistryAttachment.data
	*/
	String  REGISTRY_ATTACHMENT_DATA = REGISTRY_ATTACHMENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: RegistryAttachment_description
	* Hibernate value: RegistryAttachment.description
	*/
	String  REGISTRY_ATTACHMENT_DESCRIPTION = REGISTRY_ATTACHMENT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: RegistryAttachment_id
	* Hibernate value: RegistryAttachment.id
	*/
	String  REGISTRY_ATTACHMENT_ID = REGISTRY_ATTACHMENT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: RegistryAttachment_mimeType
	* Hibernate value: RegistryAttachment.mimeType
	*/
	String  REGISTRY_ATTACHMENT_MIME_TYPE = REGISTRY_ATTACHMENT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: RegistryAttachment_registryAttachmentType
	* Hibernate value: RegistryAttachment.registryAttachmentType
	*/
	String  REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE = REGISTRY_ATTACHMENT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: RegistryAttachment_registry_id
	* Hibernate value: RegistryAttachment.registry.id
	*/
	String  REGISTRY_ATTACHMENT_REGISTRY_ID = REGISTRY_ATTACHMENT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: RegistryAttachment_scope_id
	* Hibernate value: RegistryAttachment.scope.id
	*/
	String  REGISTRY_ATTACHMENT_SCOPE_ID = REGISTRY_ATTACHMENT_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: RegistryAttachment_size
	* Hibernate value: RegistryAttachment.size
	*/
	String  REGISTRY_ATTACHMENT_SIZE = REGISTRY_ATTACHMENT_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for RegistryBank entity.
	*/ 
	DAOConstantsEntry REGISTRY_BANK_ENTRY = DAOConstants.getDAOConstant(RegistryBank.class);

	/** 
	* Alias value: RegistryBank_bankAccount
	* Hibernate value: RegistryBank.bankAccount
	*/
	String  REGISTRY_BANK_BANK_ACCOUNT = REGISTRY_BANK_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: RegistryBank_bank_id
	* Hibernate value: RegistryBank.bank.id
	*/
	String  REGISTRY_BANK_BANK_ID = REGISTRY_BANK_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: RegistryBank_id
	* Hibernate value: RegistryBank.id
	*/
	String  REGISTRY_BANK_ID = REGISTRY_BANK_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: RegistryBank_registry_id
	* Hibernate value: RegistryBank.registry.id
	*/
	String  REGISTRY_BANK_REGISTRY_ID = REGISTRY_BANK_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: RegistryBank_sufix
	* Hibernate value: RegistryBank.sufix
	*/
	String  REGISTRY_BANK_SUFIX = REGISTRY_BANK_ENTRY.getAliasNames()[4];



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



	/** 
	* DAOConstantsEntry for RegistryMedia entity.
	*/ 
	DAOConstantsEntry REGISTRY_MEDIA_ENTRY = DAOConstants.getDAOConstant(RegistryMedia.class);

	/** 
	* Alias value: RegistryMedia_administrative
	* Hibernate value: RegistryMedia.administrative
	*/
	String  REGISTRY_MEDIA_ADMINISTRATIVE = REGISTRY_MEDIA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: RegistryMedia_comment
	* Hibernate value: RegistryMedia.comment
	*/
	String  REGISTRY_MEDIA_COMMENT = REGISTRY_MEDIA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: RegistryMedia_commercial
	* Hibernate value: RegistryMedia.commercial
	*/
	String  REGISTRY_MEDIA_COMMERCIAL = REGISTRY_MEDIA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: RegistryMedia_id
	* Hibernate value: RegistryMedia.id
	*/
	String  REGISTRY_MEDIA_ID = REGISTRY_MEDIA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: RegistryMedia_mediaType
	* Hibernate value: RegistryMedia.mediaType
	*/
	String  REGISTRY_MEDIA_MEDIA_TYPE = REGISTRY_MEDIA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: RegistryMedia_registry_id
	* Hibernate value: RegistryMedia.registry.id
	*/
	String  REGISTRY_MEDIA_REGISTRY_ID = REGISTRY_MEDIA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: RegistryMedia_technical
	* Hibernate value: RegistryMedia.technical
	*/
	String  REGISTRY_MEDIA_TECHNICAL = REGISTRY_MEDIA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: RegistryMedia_value
	* Hibernate value: RegistryMedia.value
	*/
	String  REGISTRY_MEDIA_VALUE = REGISTRY_MEDIA_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for RegistryNote entity.
	*/ 
	DAOConstantsEntry REGISTRY_NOTE_ENTRY = DAOConstants.getDAOConstant(RegistryNote.class);

	/** 
	* Alias value: RegistryNote_comments
	* Hibernate value: RegistryNote.comments
	*/
	String  REGISTRY_NOTE_COMMENTS = REGISTRY_NOTE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: RegistryNote_description
	* Hibernate value: RegistryNote.description
	*/
	String  REGISTRY_NOTE_DESCRIPTION = REGISTRY_NOTE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: RegistryNote_id
	* Hibernate value: RegistryNote.id
	*/
	String  REGISTRY_NOTE_ID = REGISTRY_NOTE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: RegistryNote_noteDate
	* Hibernate value: RegistryNote.noteDate
	*/
	String  REGISTRY_NOTE_NOTE_DATE = REGISTRY_NOTE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: RegistryNote_notetype
	* Hibernate value: RegistryNote.notetype
	*/
	String  REGISTRY_NOTE_NOTETYPE = REGISTRY_NOTE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: RegistryNote_registry_id
	* Hibernate value: RegistryNote.registry.id
	*/
	String  REGISTRY_NOTE_REGISTRY_ID = REGISTRY_NOTE_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for RegistryPayMethod entity.
	*/ 
	DAOConstantsEntry REGISTRY_PAY_METHOD_ENTRY = DAOConstants.getDAOConstant(RegistryPayMethod.class);

	/** 
	* Alias value: RegistryPayMethod_daysBetweenPayments
	* Hibernate value: RegistryPayMethod.daysBetweenPayments
	*/
	String  REGISTRY_PAY_METHOD_DAYS_BETWEEN_PAYMENTS = REGISTRY_PAY_METHOD_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: RegistryPayMethod_daysToFirstPayment
	* Hibernate value: RegistryPayMethod.daysToFirstPayment
	*/
	String  REGISTRY_PAY_METHOD_DAYS_TO_FIRST_PAYMENT = REGISTRY_PAY_METHOD_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: RegistryPayMethod_id
	* Hibernate value: RegistryPayMethod.id
	*/
	String  REGISTRY_PAY_METHOD_ID = REGISTRY_PAY_METHOD_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: RegistryPayMethod_numberOfPayments
	* Hibernate value: RegistryPayMethod.numberOfPayments
	*/
	String  REGISTRY_PAY_METHOD_NUMBER_OF_PAYMENTS = REGISTRY_PAY_METHOD_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: RegistryPayMethod_paymentDays
	* Hibernate value: RegistryPayMethod.paymentDays
	*/
	String  REGISTRY_PAY_METHOD_PAYMENT_DAYS = REGISTRY_PAY_METHOD_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: RegistryPayMethod_payment_id
	* Hibernate value: RegistryPayMethod.payment.id
	*/
	String  REGISTRY_PAY_METHOD_PAYMENT_ID = REGISTRY_PAY_METHOD_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: RegistryPayMethod_registryBank_id
	* Hibernate value: RegistryPayMethod.registryBank.id
	*/
	String  REGISTRY_PAY_METHOD_REGISTRY_BANK_ID = REGISTRY_PAY_METHOD_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: RegistryPayMethod_registry_id
	* Hibernate value: RegistryPayMethod.registry.id
	*/
	String  REGISTRY_PAY_METHOD_REGISTRY_ID = REGISTRY_PAY_METHOD_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for RegistryRelationship entity.
	*/ 
	DAOConstantsEntry REGISTRY_RELATIONSHIP_ENTRY = DAOConstants.getDAOConstant(RegistryRelationship.class);

	/** 
	* Alias value: RegistryRelationship_comments
	* Hibernate value: RegistryRelationship.comments
	*/
	String  REGISTRY_RELATIONSHIP_COMMENTS = REGISTRY_RELATIONSHIP_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: RegistryRelationship_id
	* Hibernate value: RegistryRelationship.id
	*/
	String  REGISTRY_RELATIONSHIP_ID = REGISTRY_RELATIONSHIP_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: RegistryRelationship_registry_id
	* Hibernate value: RegistryRelationship.registry.id
	*/
	String  REGISTRY_RELATIONSHIP_REGISTRY_ID = REGISTRY_RELATIONSHIP_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: RegistryRelationship_relatedRegistry_id
	* Hibernate value: RegistryRelationship.relatedRegistry.id
	*/
	String  REGISTRY_RELATIONSHIP_RELATED_REGISTRY_ID = REGISTRY_RELATIONSHIP_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: RegistryRelationship_relationship_id
	* Hibernate value: RegistryRelationship.relationship.id
	*/
	String  REGISTRY_RELATIONSHIP_RELATIONSHIP_ID = REGISTRY_RELATIONSHIP_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for RegistrySegment entity.
	*/ 
	DAOConstantsEntry REGISTRY_SEGMENT_ENTRY = DAOConstants.getDAOConstant(RegistrySegment.class);

	/** 
	* Alias value: RegistrySegment_id
	* Hibernate value: RegistrySegment.id
	*/
	String  REGISTRY_SEGMENT_ID = REGISTRY_SEGMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: RegistrySegment_registry_id
	* Hibernate value: RegistrySegment.registry.id
	*/
	String  REGISTRY_SEGMENT_REGISTRY_ID = REGISTRY_SEGMENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: RegistrySegment_segment_id
	* Hibernate value: RegistrySegment.segment.id
	*/
	String  REGISTRY_SEGMENT_SEGMENT_ID = REGISTRY_SEGMENT_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for Relationship entity.
	*/ 
	DAOConstantsEntry RELATIONSHIP_ENTRY = DAOConstants.getDAOConstant(Relationship.class);

	/** 
	* Alias value: Relationship_description
	* Hibernate value: Relationship.description
	*/
	String  RELATIONSHIP_DESCRIPTION = RELATIONSHIP_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Relationship_id
	* Hibernate value: Relationship.id
	*/
	String  RELATIONSHIP_ID = RELATIONSHIP_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Segment entity.
	*/ 
	DAOConstantsEntry SEGMENT_ENTRY = DAOConstants.getDAOConstant(Segment.class);

	/** 
	* Alias value: Segment_id
	* Hibernate value: Segment.id
	*/
	String  SEGMENT_ID = SEGMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Segment_name
	* Hibernate value: Segment.name
	*/
	String  SEGMENT_NAME = SEGMENT_ENTRY.getAliasNames()[1];


}