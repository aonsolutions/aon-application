package com.code.aon.infoweb.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.infoweb.WebInfo;
import com.code.aon.infoweb.WebInfoPage;
import com.code.aon.infoweb.WebInfoPageDetail;
import com.code.aon.infoweb.WebInfoPageResource;
import com.code.aon.infoweb.WebInfoStyle;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IWebInfoAlias {



	/** 
	* DAOConstantsEntry for WebInfo entity.
	*/ 
	DAOConstantsEntry WEB_INFO_ENTRY = DAOConstants.getDAOConstant(WebInfo.class);

	/** 
	* Alias value: WebInfo_commercialDescription
	* Hibernate value: WebInfo.commercialDescription
	*/
	String  WEB_INFO_COMMERCIAL_DESCRIPTION = WEB_INFO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: WebInfo_company_id
	* Hibernate value: WebInfo.company.id
	*/
	String  WEB_INFO_COMPANY_ID = WEB_INFO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: WebInfo_id
	* Hibernate value: WebInfo.id
	*/
	String  WEB_INFO_ID = WEB_INFO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: WebInfo_schedule
	* Hibernate value: WebInfo.schedule
	*/
	String  WEB_INFO_SCHEDULE = WEB_INFO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: WebInfo_slogan
	* Hibernate value: WebInfo.slogan
	*/
	String  WEB_INFO_SLOGAN = WEB_INFO_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for WebInfoPage entity.
	*/ 
	DAOConstantsEntry WEB_INFO_PAGE_ENTRY = DAOConstants.getDAOConstant(WebInfoPage.class);

	/** 
	* Alias value: WebInfoPage_active
	* Hibernate value: WebInfoPage.active
	*/
	String  WEB_INFO_PAGE_ACTIVE = WEB_INFO_PAGE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: WebInfoPage_id
	* Hibernate value: WebInfoPage.id
	*/
	String  WEB_INFO_PAGE_ID = WEB_INFO_PAGE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: WebInfoPage_name
	* Hibernate value: WebInfoPage.name
	*/
	String  WEB_INFO_PAGE_NAME = WEB_INFO_PAGE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: WebInfoPage_position
	* Hibernate value: WebInfoPage.position
	*/
	String  WEB_INFO_PAGE_POSITION = WEB_INFO_PAGE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: WebInfoPage_type
	* Hibernate value: WebInfoPage.type
	*/
	String  WEB_INFO_PAGE_TYPE = WEB_INFO_PAGE_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for WebInfoPageDetail entity.
	*/ 
	DAOConstantsEntry WEB_INFO_PAGE_DETAIL_ENTRY = DAOConstants.getDAOConstant(WebInfoPageDetail.class);

	/** 
	* Alias value: WebInfoPageDetail_content
	* Hibernate value: WebInfoPageDetail.content
	*/
	String  WEB_INFO_PAGE_DETAIL_CONTENT = WEB_INFO_PAGE_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: WebInfoPageDetail_extra
	* Hibernate value: WebInfoPageDetail.extra
	*/
	String  WEB_INFO_PAGE_DETAIL_EXTRA = WEB_INFO_PAGE_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: WebInfoPageDetail_id
	* Hibernate value: WebInfoPageDetail.id
	*/
	String  WEB_INFO_PAGE_DETAIL_ID = WEB_INFO_PAGE_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: WebInfoPageDetail_layout
	* Hibernate value: WebInfoPageDetail.layout
	*/
	String  WEB_INFO_PAGE_DETAIL_LAYOUT = WEB_INFO_PAGE_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: WebInfoPageDetail_title
	* Hibernate value: WebInfoPageDetail.title
	*/
	String  WEB_INFO_PAGE_DETAIL_TITLE = WEB_INFO_PAGE_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: WebInfoPageDetail_webInfoPage_id
	* Hibernate value: WebInfoPageDetail.webInfoPage.id
	*/
	String  WEB_INFO_PAGE_DETAIL_WEB_INFO_PAGE_ID = WEB_INFO_PAGE_DETAIL_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for WebInfoPageResource entity.
	*/ 
	DAOConstantsEntry WEB_INFO_PAGE_RESOURCE_ENTRY = DAOConstants.getDAOConstant(WebInfoPageResource.class);

	/** 
	* Alias value: WebInfoPageResource_content
	* Hibernate value: WebInfoPageResource.content
	*/
	String  WEB_INFO_PAGE_RESOURCE_CONTENT = WEB_INFO_PAGE_RESOURCE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: WebInfoPageResource_id
	* Hibernate value: WebInfoPageResource.id
	*/
	String  WEB_INFO_PAGE_RESOURCE_ID = WEB_INFO_PAGE_RESOURCE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: WebInfoPageResource_rattach_id
	* Hibernate value: WebInfoPageResource.rattach.id
	*/
	String  WEB_INFO_PAGE_RESOURCE_RATTACH_ID = WEB_INFO_PAGE_RESOURCE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: WebInfoPageResource_webInfoPage_id
	* Hibernate value: WebInfoPageResource.webInfoPage.id
	*/
	String  WEB_INFO_PAGE_RESOURCE_WEB_INFO_PAGE_ID = WEB_INFO_PAGE_RESOURCE_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for WebInfoStyle entity.
	*/ 
	DAOConstantsEntry WEB_INFO_STYLE_ENTRY = DAOConstants.getDAOConstant(WebInfoStyle.class);

	/** 
	* Alias value: WebInfoStyle_id
	* Hibernate value: WebInfoStyle.id
	*/
	String  WEB_INFO_STYLE_ID = WEB_INFO_STYLE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: WebInfoStyle_value
	* Hibernate value: WebInfoStyle.value
	*/
	String  WEB_INFO_STYLE_VALUE = WEB_INFO_STYLE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: WebInfoStyle_variable
	* Hibernate value: WebInfoStyle.variable
	*/
	String  WEB_INFO_STYLE_VARIABLE = WEB_INFO_STYLE_ENTRY.getAliasNames()[2];


}