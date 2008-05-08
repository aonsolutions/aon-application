package com.code.aon.groupware.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.groupware.Alarm;
import com.code.aon.groupware.Notice;
import com.code.aon.groupware.Note;
import com.code.aon.groupware.Contact;
import com.code.aon.groupware.FavoriteCategory;
import com.code.aon.groupware.Favorite;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IGroupWareAlias {



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
	* DAOConstantsEntry for Contact entity.
	*/ 
	DAOConstantsEntry CONTACT_ENTRY = DAOConstants.getDAOConstant(Contact.class);

	/** 
	* Alias value: Contact_address
	* Hibernate value: Contact.address
	*/
	String  CONTACT_ADDRESS = CONTACT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Contact_cellularPhone
	* Hibernate value: Contact.cellularPhone
	*/
	String  CONTACT_CELLULAR_PHONE = CONTACT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Contact_email
	* Hibernate value: Contact.email
	*/
	String  CONTACT_EMAIL = CONTACT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Contact_fax
	* Hibernate value: Contact.fax
	*/
	String  CONTACT_FAX = CONTACT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Contact_id
	* Hibernate value: Contact.id
	*/
	String  CONTACT_ID = CONTACT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Contact_name
	* Hibernate value: Contact.name
	*/
	String  CONTACT_NAME = CONTACT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Contact_note
	* Hibernate value: Contact.note
	*/
	String  CONTACT_NOTE = CONTACT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Contact_organization
	* Hibernate value: Contact.organization
	*/
	String  CONTACT_ORGANIZATION = CONTACT_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Contact_phone
	* Hibernate value: Contact.phone
	*/
	String  CONTACT_PHONE = CONTACT_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Contact_user_id
	* Hibernate value: Contact.user.id
	*/
	String  CONTACT_USER_ID = CONTACT_ENTRY.getAliasNames()[9];



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


}