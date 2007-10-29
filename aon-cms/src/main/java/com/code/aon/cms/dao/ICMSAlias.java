package com.code.aon.cms.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.cms.Language;
import com.code.aon.cms.Config;
import com.code.aon.cms.ConfigDetail;
import com.code.aon.cms.GenericPage;
import com.code.aon.cms.GenericPageDetail;
import com.code.aon.cms.Menu;
import com.code.aon.cms.MenuOption;
import com.code.aon.cms.MenuOptionDetail;
import com.code.aon.cms.Header;
import com.code.aon.cms.HeaderDetail;
import com.code.aon.cms.Footer;
import com.code.aon.cms.FooterDetail;

/** 
* Interface for holding entity properties constants.
*/ 
public interface ICMSAlias {



	/** 
	* DAOConstantsEntry for Language entity.
	*/ 
	DAOConstantsEntry LANGUAGE_ENTRY = DAOConstants.getDAOConstant(Language.class);

	/** 
	* Alias value: Language_defaultLanguage
	* Hibernate value: Language.defaultLanguage
	*/
	String  LANGUAGE_DEFAULT_LANGUAGE = LANGUAGE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Language_description
	* Hibernate value: Language.description
	*/
	String  LANGUAGE_DESCRIPTION = LANGUAGE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Language_id
	* Hibernate value: Language.id
	*/
	String  LANGUAGE_ID = LANGUAGE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Language_language
	* Hibernate value: Language.language
	*/
	String  LANGUAGE_LANGUAGE = LANGUAGE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Language_position
	* Hibernate value: Language.position
	*/
	String  LANGUAGE_POSITION = LANGUAGE_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for Config entity.
	*/ 
	DAOConstantsEntry CONFIG_ENTRY = DAOConstants.getDAOConstant(Config.class);

	/** 
	* Alias value: Config_domain
	* Hibernate value: Config.domain
	*/
	String  CONFIG_DOMAIN = CONFIG_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Config_from_email
	* Hibernate value: Config.from_email
	*/
	String  CONFIG_FROM_EMAIL = CONFIG_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Config_from_name
	* Hibernate value: Config.from_name
	*/
	String  CONFIG_FROM_NAME = CONFIG_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Config_ftp_password
	* Hibernate value: Config.ftp_password
	*/
	String  CONFIG_FTP_PASSWORD = CONFIG_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Config_ftp_server
	* Hibernate value: Config.ftp_server
	*/
	String  CONFIG_FTP_SERVER = CONFIG_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Config_ftp_user
	* Hibernate value: Config.ftp_user
	*/
	String  CONFIG_FTP_USER = CONFIG_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Config_host
	* Hibernate value: Config.host
	*/
	String  CONFIG_HOST = CONFIG_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Config_id
	* Hibernate value: Config.id
	*/
	String  CONFIG_ID = CONFIG_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Config_online
	* Hibernate value: Config.online
	*/
	String  CONFIG_ONLINE = CONFIG_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Config_preview_host
	* Hibernate value: Config.preview_host
	*/
	String  CONFIG_PREVIEW_HOST = CONFIG_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Config_smtp_auth
	* Hibernate value: Config.smtp_auth
	*/
	String  CONFIG_SMTP_AUTH = CONFIG_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Config_smtp_password
	* Hibernate value: Config.smtp_password
	*/
	String  CONFIG_SMTP_PASSWORD = CONFIG_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Config_smtp_server
	* Hibernate value: Config.smtp_server
	*/
	String  CONFIG_SMTP_SERVER = CONFIG_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Config_smtp_user
	* Hibernate value: Config.smtp_user
	*/
	String  CONFIG_SMTP_USER = CONFIG_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Config_template
	* Hibernate value: Config.template
	*/
	String  CONFIG_TEMPLATE = CONFIG_ENTRY.getAliasNames()[14];



	/** 
	* DAOConstantsEntry for ConfigDetail entity.
	*/ 
	DAOConstantsEntry CONFIG_DETAIL_ENTRY = DAOConstants.getDAOConstant(ConfigDetail.class);

	/** 
	* Alias value: ConfigDetail_config_id
	* Hibernate value: ConfigDetail.config.id
	*/
	String  CONFIG_DETAIL_CONFIG_ID = CONFIG_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ConfigDetail_css
	* Hibernate value: ConfigDetail.css
	*/
	String  CONFIG_DETAIL_CSS = CONFIG_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ConfigDetail_description
	* Hibernate value: ConfigDetail.description
	*/
	String  CONFIG_DETAIL_DESCRIPTION = CONFIG_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ConfigDetail_id
	* Hibernate value: ConfigDetail.id
	*/
	String  CONFIG_DETAIL_ID = CONFIG_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ConfigDetail_javascript
	* Hibernate value: ConfigDetail.javascript
	*/
	String  CONFIG_DETAIL_JAVASCRIPT = CONFIG_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ConfigDetail_keywords
	* Hibernate value: ConfigDetail.keywords
	*/
	String  CONFIG_DETAIL_KEYWORDS = CONFIG_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ConfigDetail_language_id
	* Hibernate value: ConfigDetail.language.id
	*/
	String  CONFIG_DETAIL_LANGUAGE_ID = CONFIG_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ConfigDetail_offline_message
	* Hibernate value: ConfigDetail.offline_message
	*/
	String  CONFIG_DETAIL_OFFLINE_MESSAGE = CONFIG_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ConfigDetail_sitename
	* Hibernate value: ConfigDetail.sitename
	*/
	String  CONFIG_DETAIL_SITENAME = CONFIG_DETAIL_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for GenericPage entity.
	*/ 
	DAOConstantsEntry GENERIC_PAGE_ENTRY = DAOConstants.getDAOConstant(GenericPage.class);

	/** 
	* Alias value: GenericPage_active
	* Hibernate value: GenericPage.active
	*/
	String  GENERIC_PAGE_ACTIVE = GENERIC_PAGE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: GenericPage_alias
	* Hibernate value: GenericPage.alias
	*/
	String  GENERIC_PAGE_ALIAS = GENERIC_PAGE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: GenericPage_create_date
	* Hibernate value: GenericPage.create_date
	*/
	String  GENERIC_PAGE_CREATE_DATE = GENERIC_PAGE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: GenericPage_id
	* Hibernate value: GenericPage.id
	*/
	String  GENERIC_PAGE_ID = GENERIC_PAGE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: GenericPage_menu
	* Hibernate value: GenericPage.menu
	*/
	String  GENERIC_PAGE_MENU = GENERIC_PAGE_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for GenericPageDetail entity.
	*/ 
	DAOConstantsEntry GENERIC_PAGE_DETAIL_ENTRY = DAOConstants.getDAOConstant(GenericPageDetail.class);

	/** 
	* Alias value: GenericPageDetail_content
	* Hibernate value: GenericPageDetail.content
	*/
	String  GENERIC_PAGE_DETAIL_CONTENT = GENERIC_PAGE_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: GenericPageDetail_description
	* Hibernate value: GenericPageDetail.description
	*/
	String  GENERIC_PAGE_DETAIL_DESCRIPTION = GENERIC_PAGE_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: GenericPageDetail_generic_page_id
	* Hibernate value: GenericPageDetail.generic_page.id
	*/
	String  GENERIC_PAGE_DETAIL_GENERIC_PAGE_ID = GENERIC_PAGE_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: GenericPageDetail_id
	* Hibernate value: GenericPageDetail.id
	*/
	String  GENERIC_PAGE_DETAIL_ID = GENERIC_PAGE_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: GenericPageDetail_keywords
	* Hibernate value: GenericPageDetail.keywords
	*/
	String  GENERIC_PAGE_DETAIL_KEYWORDS = GENERIC_PAGE_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: GenericPageDetail_language_id
	* Hibernate value: GenericPageDetail.language.id
	*/
	String  GENERIC_PAGE_DETAIL_LANGUAGE_ID = GENERIC_PAGE_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: GenericPageDetail_title
	* Hibernate value: GenericPageDetail.title
	*/
	String  GENERIC_PAGE_DETAIL_TITLE = GENERIC_PAGE_DETAIL_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for Menu entity.
	*/ 
	DAOConstantsEntry MENU_ENTRY = DAOConstants.getDAOConstant(Menu.class);

	/** 
	* Alias value: Menu_alias
	* Hibernate value: Menu.alias
	*/
	String  MENU_ALIAS = MENU_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Menu_defaultMenu
	* Hibernate value: Menu.defaultMenu
	*/
	String  MENU_DEFAULT_MENU = MENU_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Menu_id
	* Hibernate value: Menu.id
	*/
	String  MENU_ID = MENU_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Menu_type
	* Hibernate value: Menu.type
	*/
	String  MENU_TYPE = MENU_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for MenuOption entity.
	*/ 
	DAOConstantsEntry MENU_OPTION_ENTRY = DAOConstants.getDAOConstant(MenuOption.class);

	/** 
	* Alias value: MenuOption_active
	* Hibernate value: MenuOption.active
	*/
	String  MENU_OPTION_ACTIVE = MENU_OPTION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: MenuOption_alias
	* Hibernate value: MenuOption.alias
	*/
	String  MENU_OPTION_ALIAS = MENU_OPTION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: MenuOption_id
	* Hibernate value: MenuOption.id
	*/
	String  MENU_OPTION_ID = MENU_OPTION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: MenuOption_ident
	* Hibernate value: MenuOption.ident
	*/
	String  MENU_OPTION_IDENT = MENU_OPTION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: MenuOption_level
	* Hibernate value: MenuOption.level
	*/
	String  MENU_OPTION_LEVEL = MENU_OPTION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: MenuOption_menu_id
	* Hibernate value: MenuOption.menu.id
	*/
	String  MENU_OPTION_MENU_ID = MENU_OPTION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: MenuOption_position
	* Hibernate value: MenuOption.position
	*/
	String  MENU_OPTION_POSITION = MENU_OPTION_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: MenuOption_separator
	* Hibernate value: MenuOption.separator
	*/
	String  MENU_OPTION_SEPARATOR = MENU_OPTION_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: MenuOption_type
	* Hibernate value: MenuOption.type
	*/
	String  MENU_OPTION_TYPE = MENU_OPTION_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for MenuOptionDetail entity.
	*/ 
	DAOConstantsEntry MENU_OPTION_DETAIL_ENTRY = DAOConstants.getDAOConstant(MenuOptionDetail.class);

	/** 
	* Alias value: MenuOptionDetail_id
	* Hibernate value: MenuOptionDetail.id
	*/
	String  MENU_OPTION_DETAIL_ID = MENU_OPTION_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: MenuOptionDetail_label
	* Hibernate value: MenuOptionDetail.label
	*/
	String  MENU_OPTION_DETAIL_LABEL = MENU_OPTION_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: MenuOptionDetail_language_id
	* Hibernate value: MenuOptionDetail.language.id
	*/
	String  MENU_OPTION_DETAIL_LANGUAGE_ID = MENU_OPTION_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: MenuOptionDetail_menu_option_id
	* Hibernate value: MenuOptionDetail.menu_option.id
	*/
	String  MENU_OPTION_DETAIL_MENU_OPTION_ID = MENU_OPTION_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: MenuOptionDetail_url
	* Hibernate value: MenuOptionDetail.url
	*/
	String  MENU_OPTION_DETAIL_URL = MENU_OPTION_DETAIL_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for Header entity.
	*/ 
	DAOConstantsEntry HEADER_ENTRY = DAOConstants.getDAOConstant(Header.class);

	/** 
	* Alias value: Header_alias
	* Hibernate value: Header.alias
	*/
	String  HEADER_ALIAS = HEADER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Header_css
	* Hibernate value: Header.css
	*/
	String  HEADER_CSS = HEADER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Header_defaultHeader
	* Hibernate value: Header.defaultHeader
	*/
	String  HEADER_DEFAULT_HEADER = HEADER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Header_id
	* Hibernate value: Header.id
	*/
	String  HEADER_ID = HEADER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Header_javascript
	* Hibernate value: Header.javascript
	*/
	String  HEADER_JAVASCRIPT = HEADER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Header_language_menu
	* Hibernate value: Header.language_menu
	*/
	String  HEADER_LANGUAGE_MENU = HEADER_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Header_language_menu_type
	* Hibernate value: Header.language_menu_type
	*/
	String  HEADER_LANGUAGE_MENU_TYPE = HEADER_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Header_menu
	* Hibernate value: Header.menu
	*/
	String  HEADER_MENU = HEADER_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for HeaderDetail entity.
	*/ 
	DAOConstantsEntry HEADER_DETAIL_ENTRY = DAOConstants.getDAOConstant(HeaderDetail.class);

	/** 
	* Alias value: HeaderDetail_content
	* Hibernate value: HeaderDetail.content
	*/
	String  HEADER_DETAIL_CONTENT = HEADER_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: HeaderDetail_header_id
	* Hibernate value: HeaderDetail.header.id
	*/
	String  HEADER_DETAIL_HEADER_ID = HEADER_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: HeaderDetail_id
	* Hibernate value: HeaderDetail.id
	*/
	String  HEADER_DETAIL_ID = HEADER_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: HeaderDetail_image
	* Hibernate value: HeaderDetail.image
	*/
	String  HEADER_DETAIL_IMAGE = HEADER_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: HeaderDetail_language_id
	* Hibernate value: HeaderDetail.language.id
	*/
	String  HEADER_DETAIL_LANGUAGE_ID = HEADER_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: HeaderDetail_sitename
	* Hibernate value: HeaderDetail.sitename
	*/
	String  HEADER_DETAIL_SITENAME = HEADER_DETAIL_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Footer entity.
	*/ 
	DAOConstantsEntry FOOTER_ENTRY = DAOConstants.getDAOConstant(Footer.class);

	/** 
	* Alias value: Footer_alias
	* Hibernate value: Footer.alias
	*/
	String  FOOTER_ALIAS = FOOTER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Footer_defaultFooter
	* Hibernate value: Footer.defaultFooter
	*/
	String  FOOTER_DEFAULT_FOOTER = FOOTER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Footer_id
	* Hibernate value: Footer.id
	*/
	String  FOOTER_ID = FOOTER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Footer_menu
	* Hibernate value: Footer.menu
	*/
	String  FOOTER_MENU = FOOTER_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for FooterDetail entity.
	*/ 
	DAOConstantsEntry FOOTER_DETAIL_ENTRY = DAOConstants.getDAOConstant(FooterDetail.class);

	/** 
	* Alias value: FooterDetail_content
	* Hibernate value: FooterDetail.content
	*/
	String  FOOTER_DETAIL_CONTENT = FOOTER_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: FooterDetail_footer_id
	* Hibernate value: FooterDetail.footer.id
	*/
	String  FOOTER_DETAIL_FOOTER_ID = FOOTER_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: FooterDetail_id
	* Hibernate value: FooterDetail.id
	*/
	String  FOOTER_DETAIL_ID = FOOTER_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: FooterDetail_language_id
	* Hibernate value: FooterDetail.language.id
	*/
	String  FOOTER_DETAIL_LANGUAGE_ID = FOOTER_DETAIL_ENTRY.getAliasNames()[3];


}