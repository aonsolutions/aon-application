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
import com.code.aon.cms.Link;
import com.code.aon.cms.LinkDetail;
import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.LinkCategoryDetail;
import com.code.aon.cms.Faq;
import com.code.aon.cms.FaqDetail;
import com.code.aon.cms.FaqCategory;
import com.code.aon.cms.FaqCategoryDetail;
import com.code.aon.cms.Brand;
import com.code.aon.cms.BrandDetail;
import com.code.aon.cms.ProductCategory;
import com.code.aon.cms.ProductCategoryDetail;
import com.code.aon.cms.Product;
import com.code.aon.cms.ProductDetail;
import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleDetail;
import com.code.aon.cms.ArticleCategory;
import com.code.aon.cms.ArticleCategoryDetail;
import com.code.aon.cms.Banner;
import com.code.aon.cms.BannerDetail;
import com.code.aon.cms.BannerCategory;
import com.code.aon.cms.BannerCategoryDetail;
import com.code.aon.cms.Download;
import com.code.aon.cms.DownloadDetail;
import com.code.aon.cms.DownloadCategory;
import com.code.aon.cms.DownloadCategoryDetail;
import com.code.aon.cms.AlbumCategory;
import com.code.aon.cms.AlbumCategoryDetail;
import com.code.aon.cms.Album;
import com.code.aon.cms.AlbumDetail;
import com.code.aon.cms.AlbumImage;
import com.code.aon.cms.AlbumImageDetail;
import com.code.aon.cms.Sidebar;
import com.code.aon.cms.SidebarOption;
import com.code.aon.cms.SidebarOptionDetail;
import com.code.aon.cms.Section;
import com.code.aon.cms.ModularPage;
import com.code.aon.cms.ModularPageOption;
import com.code.aon.cms.ModularPageOptionDetail;
import com.code.aon.cms.DirectAccessGroup;
import com.code.aon.cms.DirectAccessGroupDetail;
import com.code.aon.cms.DirectAccess;
import com.code.aon.cms.DirectAccessDetail;
import com.code.aon.cms.FaqConfig;
import com.code.aon.cms.LinkConfig;
import com.code.aon.cms.AlbumConfig;
import com.code.aon.cms.ArticleConfig;
import com.code.aon.cms.ModularPageDetail;
import com.code.aon.cms.ArticleRelated;
import com.code.aon.cms.ArticleDocument;
import com.code.aon.cms.ArticleDocumentDetail;
import com.code.aon.cms.DownloadConfig;
import com.code.aon.cms.Company;
import com.code.aon.cms.Activity;
import com.code.aon.cms.ActivityDetail;
import com.code.aon.cms.CompanyActivity;
import com.code.aon.cms.Bulletin;
import com.code.aon.cms.BulletinDetail;
import com.code.aon.cms.BulletinArticle;
import com.code.aon.cms.BulletinEmail;
import com.code.aon.cms.FooterBannerCategory;
import com.code.aon.cms.Diary;
import com.code.aon.cms.DiaryDetail;
import com.code.aon.cms.HiruOrganizerCentre;
import com.code.aon.cms.HiruCourse;
import com.code.aon.cms.HiruCourseDetail;
import com.code.aon.cms.HiruConfig;
import com.code.aon.cms.ProductCategoryConfig;
import com.code.aon.cms.SportCategory;
import com.code.aon.cms.SportCategoryDetail;
import com.code.aon.cms.SportClub;
import com.code.aon.cms.SportPosition;
import com.code.aon.cms.SportPositionDetail;
import com.code.aon.cms.SportNationality;
import com.code.aon.cms.SportNationalityDetail;
import com.code.aon.cms.SportSeason;
import com.code.aon.cms.SportPlayer;
import com.code.aon.cms.SportCareerPath;
import com.code.aon.cms.SportCoach;
import com.code.aon.cms.SportConfig;

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
	* Alias value: Config_ftp_path
	* Hibernate value: Config.ftp_path
	*/
	String  CONFIG_FTP_PATH = CONFIG_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Config_ftp_server
	* Hibernate value: Config.ftp_server
	*/
	String  CONFIG_FTP_SERVER = CONFIG_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Config_ftp_user
	* Hibernate value: Config.ftp_user
	*/
	String  CONFIG_FTP_USER = CONFIG_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Config_host
	* Hibernate value: Config.host
	*/
	String  CONFIG_HOST = CONFIG_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Config_id
	* Hibernate value: Config.id
	*/
	String  CONFIG_ID = CONFIG_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Config_online
	* Hibernate value: Config.online
	*/
	String  CONFIG_ONLINE = CONFIG_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Config_preview_ftp_password
	* Hibernate value: Config.preview_ftp_password
	*/
	String  CONFIG_PREVIEW_FTP_PASSWORD = CONFIG_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Config_preview_ftp_path
	* Hibernate value: Config.preview_ftp_path
	*/
	String  CONFIG_PREVIEW_FTP_PATH = CONFIG_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Config_preview_ftp_server
	* Hibernate value: Config.preview_ftp_server
	*/
	String  CONFIG_PREVIEW_FTP_SERVER = CONFIG_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Config_preview_ftp_user
	* Hibernate value: Config.preview_ftp_user
	*/
	String  CONFIG_PREVIEW_FTP_USER = CONFIG_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Config_preview_host
	* Hibernate value: Config.preview_host
	*/
	String  CONFIG_PREVIEW_HOST = CONFIG_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Config_smtp_auth
	* Hibernate value: Config.smtp_auth
	*/
	String  CONFIG_SMTP_AUTH = CONFIG_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Config_smtp_password
	* Hibernate value: Config.smtp_password
	*/
	String  CONFIG_SMTP_PASSWORD = CONFIG_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Config_smtp_server
	* Hibernate value: Config.smtp_server
	*/
	String  CONFIG_SMTP_SERVER = CONFIG_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Config_smtp_user
	* Hibernate value: Config.smtp_user
	*/
	String  CONFIG_SMTP_USER = CONFIG_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Config_template
	* Hibernate value: Config.template
	*/
	String  CONFIG_TEMPLATE = CONFIG_ENTRY.getAliasNames()[19];



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
	* Alias value: GenericPage_section_id
	* Hibernate value: GenericPage.section.id
	*/
	String  GENERIC_PAGE_SECTION_ID = GENERIC_PAGE_ENTRY.getAliasNames()[5];



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
	* Alias value: Header_bannerCategory_id
	* Hibernate value: Header.bannerCategory.id
	*/
	String  HEADER_BANNER_CATEGORY_ID = HEADER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Header_css
	* Hibernate value: Header.css
	*/
	String  HEADER_CSS = HEADER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Header_default_
	* Hibernate value: Header.default_
	*/
	String  HEADER_DEFAULT_ = HEADER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Header_id
	* Hibernate value: Header.id
	*/
	String  HEADER_ID = HEADER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Header_javascript
	* Hibernate value: Header.javascript
	*/
	String  HEADER_JAVASCRIPT = HEADER_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Header_language_menu
	* Hibernate value: Header.language_menu
	*/
	String  HEADER_LANGUAGE_MENU = HEADER_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Header_language_menu_type
	* Hibernate value: Header.language_menu_type
	*/
	String  HEADER_LANGUAGE_MENU_TYPE = HEADER_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Header_menu_id
	* Hibernate value: Header.menu.id
	*/
	String  HEADER_MENU_ID = HEADER_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for HeaderDetail entity.
	*/ 
	DAOConstantsEntry HEADER_DETAIL_ENTRY = DAOConstants.getDAOConstant(HeaderDetail.class);

	/** 
	* Alias value: HeaderDetail_alt
	* Hibernate value: HeaderDetail.alt
	*/
	String  HEADER_DETAIL_ALT = HEADER_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: HeaderDetail_content
	* Hibernate value: HeaderDetail.content
	*/
	String  HEADER_DETAIL_CONTENT = HEADER_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: HeaderDetail_header_id
	* Hibernate value: HeaderDetail.header.id
	*/
	String  HEADER_DETAIL_HEADER_ID = HEADER_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: HeaderDetail_id
	* Hibernate value: HeaderDetail.id
	*/
	String  HEADER_DETAIL_ID = HEADER_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: HeaderDetail_image
	* Hibernate value: HeaderDetail.image
	*/
	String  HEADER_DETAIL_IMAGE = HEADER_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: HeaderDetail_language_id
	* Hibernate value: HeaderDetail.language.id
	*/
	String  HEADER_DETAIL_LANGUAGE_ID = HEADER_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: HeaderDetail_sitename
	* Hibernate value: HeaderDetail.sitename
	*/
	String  HEADER_DETAIL_SITENAME = HEADER_DETAIL_ENTRY.getAliasNames()[6];



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
	* Alias value: Footer_default_
	* Hibernate value: Footer.default_
	*/
	String  FOOTER_DEFAULT_ = FOOTER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Footer_id
	* Hibernate value: Footer.id
	*/
	String  FOOTER_ID = FOOTER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Footer_menu_id
	* Hibernate value: Footer.menu.id
	*/
	String  FOOTER_MENU_ID = FOOTER_ENTRY.getAliasNames()[3];



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



	/** 
	* DAOConstantsEntry for Link entity.
	*/ 
	DAOConstantsEntry LINK_ENTRY = DAOConstants.getDAOConstant(Link.class);

	/** 
	* Alias value: Link_active
	* Hibernate value: Link.active
	*/
	String  LINK_ACTIVE = LINK_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Link_alias
	* Hibernate value: Link.alias
	*/
	String  LINK_ALIAS = LINK_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Link_id
	* Hibernate value: Link.id
	*/
	String  LINK_ID = LINK_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Link_linkCategory_id
	* Hibernate value: Link.linkCategory.id
	*/
	String  LINK_LINK_CATEGORY_ID = LINK_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Link_position
	* Hibernate value: Link.position
	*/
	String  LINK_POSITION = LINK_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Link_url
	* Hibernate value: Link.url
	*/
	String  LINK_URL = LINK_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for LinkDetail entity.
	*/ 
	DAOConstantsEntry LINK_DETAIL_ENTRY = DAOConstants.getDAOConstant(LinkDetail.class);

	/** 
	* Alias value: LinkDetail_description
	* Hibernate value: LinkDetail.description
	*/
	String  LINK_DETAIL_DESCRIPTION = LINK_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: LinkDetail_id
	* Hibernate value: LinkDetail.id
	*/
	String  LINK_DETAIL_ID = LINK_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: LinkDetail_label
	* Hibernate value: LinkDetail.label
	*/
	String  LINK_DETAIL_LABEL = LINK_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: LinkDetail_language_id
	* Hibernate value: LinkDetail.language.id
	*/
	String  LINK_DETAIL_LANGUAGE_ID = LINK_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: LinkDetail_link_id
	* Hibernate value: LinkDetail.link.id
	*/
	String  LINK_DETAIL_LINK_ID = LINK_DETAIL_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for LinkCategory entity.
	*/ 
	DAOConstantsEntry LINK_CATEGORY_ENTRY = DAOConstants.getDAOConstant(LinkCategory.class);

	/** 
	* Alias value: LinkCategory_active
	* Hibernate value: LinkCategory.active
	*/
	String  LINK_CATEGORY_ACTIVE = LINK_CATEGORY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: LinkCategory_alias
	* Hibernate value: LinkCategory.alias
	*/
	String  LINK_CATEGORY_ALIAS = LINK_CATEGORY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: LinkCategory_id
	* Hibernate value: LinkCategory.id
	*/
	String  LINK_CATEGORY_ID = LINK_CATEGORY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: LinkCategory_position
	* Hibernate value: LinkCategory.position
	*/
	String  LINK_CATEGORY_POSITION = LINK_CATEGORY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: LinkCategory_section_id
	* Hibernate value: LinkCategory.section.id
	*/
	String  LINK_CATEGORY_SECTION_ID = LINK_CATEGORY_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for LinkCategoryDetail entity.
	*/ 
	DAOConstantsEntry LINK_CATEGORY_DETAIL_ENTRY = DAOConstants.getDAOConstant(LinkCategoryDetail.class);

	/** 
	* Alias value: LinkCategoryDetail_id
	* Hibernate value: LinkCategoryDetail.id
	*/
	String  LINK_CATEGORY_DETAIL_ID = LINK_CATEGORY_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: LinkCategoryDetail_label
	* Hibernate value: LinkCategoryDetail.label
	*/
	String  LINK_CATEGORY_DETAIL_LABEL = LINK_CATEGORY_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: LinkCategoryDetail_language_id
	* Hibernate value: LinkCategoryDetail.language.id
	*/
	String  LINK_CATEGORY_DETAIL_LANGUAGE_ID = LINK_CATEGORY_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: LinkCategoryDetail_linkCategory_id
	* Hibernate value: LinkCategoryDetail.linkCategory.id
	*/
	String  LINK_CATEGORY_DETAIL_LINK_CATEGORY_ID = LINK_CATEGORY_DETAIL_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Faq entity.
	*/ 
	DAOConstantsEntry FAQ_ENTRY = DAOConstants.getDAOConstant(Faq.class);

	/** 
	* Alias value: Faq_active
	* Hibernate value: Faq.active
	*/
	String  FAQ_ACTIVE = FAQ_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Faq_alias
	* Hibernate value: Faq.alias
	*/
	String  FAQ_ALIAS = FAQ_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Faq_faqCategory_id
	* Hibernate value: Faq.faqCategory.id
	*/
	String  FAQ_FAQ_CATEGORY_ID = FAQ_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Faq_id
	* Hibernate value: Faq.id
	*/
	String  FAQ_ID = FAQ_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Faq_position
	* Hibernate value: Faq.position
	*/
	String  FAQ_POSITION = FAQ_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for FaqDetail entity.
	*/ 
	DAOConstantsEntry FAQ_DETAIL_ENTRY = DAOConstants.getDAOConstant(FaqDetail.class);

	/** 
	* Alias value: FaqDetail_answer
	* Hibernate value: FaqDetail.answer
	*/
	String  FAQ_DETAIL_ANSWER = FAQ_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: FaqDetail_faq_id
	* Hibernate value: FaqDetail.faq.id
	*/
	String  FAQ_DETAIL_FAQ_ID = FAQ_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: FaqDetail_id
	* Hibernate value: FaqDetail.id
	*/
	String  FAQ_DETAIL_ID = FAQ_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: FaqDetail_language_id
	* Hibernate value: FaqDetail.language.id
	*/
	String  FAQ_DETAIL_LANGUAGE_ID = FAQ_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: FaqDetail_question
	* Hibernate value: FaqDetail.question
	*/
	String  FAQ_DETAIL_QUESTION = FAQ_DETAIL_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for FaqCategory entity.
	*/ 
	DAOConstantsEntry FAQ_CATEGORY_ENTRY = DAOConstants.getDAOConstant(FaqCategory.class);

	/** 
	* Alias value: FaqCategory_active
	* Hibernate value: FaqCategory.active
	*/
	String  FAQ_CATEGORY_ACTIVE = FAQ_CATEGORY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: FaqCategory_alias
	* Hibernate value: FaqCategory.alias
	*/
	String  FAQ_CATEGORY_ALIAS = FAQ_CATEGORY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: FaqCategory_id
	* Hibernate value: FaqCategory.id
	*/
	String  FAQ_CATEGORY_ID = FAQ_CATEGORY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: FaqCategory_position
	* Hibernate value: FaqCategory.position
	*/
	String  FAQ_CATEGORY_POSITION = FAQ_CATEGORY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: FaqCategory_section_id
	* Hibernate value: FaqCategory.section.id
	*/
	String  FAQ_CATEGORY_SECTION_ID = FAQ_CATEGORY_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for FaqCategoryDetail entity.
	*/ 
	DAOConstantsEntry FAQ_CATEGORY_DETAIL_ENTRY = DAOConstants.getDAOConstant(FaqCategoryDetail.class);

	/** 
	* Alias value: FaqCategoryDetail_faqCategory_id
	* Hibernate value: FaqCategoryDetail.faqCategory.id
	*/
	String  FAQ_CATEGORY_DETAIL_FAQ_CATEGORY_ID = FAQ_CATEGORY_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: FaqCategoryDetail_id
	* Hibernate value: FaqCategoryDetail.id
	*/
	String  FAQ_CATEGORY_DETAIL_ID = FAQ_CATEGORY_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: FaqCategoryDetail_label
	* Hibernate value: FaqCategoryDetail.label
	*/
	String  FAQ_CATEGORY_DETAIL_LABEL = FAQ_CATEGORY_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: FaqCategoryDetail_language_id
	* Hibernate value: FaqCategoryDetail.language.id
	*/
	String  FAQ_CATEGORY_DETAIL_LANGUAGE_ID = FAQ_CATEGORY_DETAIL_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Brand entity.
	*/ 
	DAOConstantsEntry BRAND_ENTRY = DAOConstants.getDAOConstant(Brand.class);

	/** 
	* Alias value: Brand_active
	* Hibernate value: Brand.active
	*/
	String  BRAND_ACTIVE = BRAND_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Brand_alias
	* Hibernate value: Brand.alias
	*/
	String  BRAND_ALIAS = BRAND_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Brand_id
	* Hibernate value: Brand.id
	*/
	String  BRAND_ID = BRAND_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for BrandDetail entity.
	*/ 
	DAOConstantsEntry BRAND_DETAIL_ENTRY = DAOConstants.getDAOConstant(BrandDetail.class);

	/** 
	* Alias value: BrandDetail_brand_id
	* Hibernate value: BrandDetail.brand.id
	*/
	String  BRAND_DETAIL_BRAND_ID = BRAND_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: BrandDetail_id
	* Hibernate value: BrandDetail.id
	*/
	String  BRAND_DETAIL_ID = BRAND_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: BrandDetail_label
	* Hibernate value: BrandDetail.label
	*/
	String  BRAND_DETAIL_LABEL = BRAND_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: BrandDetail_language_id
	* Hibernate value: BrandDetail.language.id
	*/
	String  BRAND_DETAIL_LANGUAGE_ID = BRAND_DETAIL_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for ProductCategory entity.
	*/ 
	DAOConstantsEntry PRODUCT_CATEGORY_ENTRY = DAOConstants.getDAOConstant(ProductCategory.class);

	/** 
	* Alias value: ProductCategory_id
	* Hibernate value: ProductCategory.id
	*/
	String  PRODUCT_CATEGORY_ID = PRODUCT_CATEGORY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProductCategory_alias
	* Hibernate value: ProductCategory.alias
	*/
	String  PRODUCT_CATEGORY_ALIAS = PRODUCT_CATEGORY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProductCategory_active
	* Hibernate value: ProductCategory.active
	*/
	String  PRODUCT_CATEGORY_ACTIVE = PRODUCT_CATEGORY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ProductCategory_parent_id
	* Hibernate value: ProductCategory.parent<id
	*/
	String  PRODUCT_CATEGORY_PARENT_ID = PRODUCT_CATEGORY_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for ProductCategoryDetail entity.
	*/ 
	DAOConstantsEntry PRODUCT_CATEGORY_DETAIL_ENTRY = DAOConstants.getDAOConstant(ProductCategoryDetail.class);

	/** 
	* Alias value: ProductCategoryDetail_id
	* Hibernate value: ProductCategoryDetail.id
	*/
	String  PRODUCT_CATEGORY_DETAIL_ID = PRODUCT_CATEGORY_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProductCategoryDetail_label
	* Hibernate value: ProductCategoryDetail.label
	*/
	String  PRODUCT_CATEGORY_DETAIL_LABEL = PRODUCT_CATEGORY_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProductCategoryDetail_language_id
	* Hibernate value: ProductCategoryDetail.language.id
	*/
	String  PRODUCT_CATEGORY_DETAIL_LANGUAGE_ID = PRODUCT_CATEGORY_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ProductCategoryDetail_productCategory_id
	* Hibernate value: ProductCategoryDetail.productCategory.id
	*/
	String  PRODUCT_CATEGORY_DETAIL_PRODUCT_CATEGORY_ID = PRODUCT_CATEGORY_DETAIL_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Product entity.
	*/ 
	DAOConstantsEntry PRODUCT_ENTRY = DAOConstants.getDAOConstant(Product.class);

	/** 
	* Alias value: Product_active
	* Hibernate value: Product.active
	*/
	String  PRODUCT_ACTIVE = PRODUCT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Product_alias
	* Hibernate value: Product.alias
	*/
	String  PRODUCT_ALIAS = PRODUCT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Product_brand_id
	* Hibernate value: Product.brand.id
	*/
	String  PRODUCT_BRAND_ID = PRODUCT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Product_id
	* Hibernate value: Product.id
	*/
	String  PRODUCT_ID = PRODUCT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Product_image
	* Hibernate value: Product.image
	*/
	String  PRODUCT_IMAGE = PRODUCT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Product_offerPrice
	* Hibernate value: Product.offerPrice
	*/
	String  PRODUCT_OFFER_PRICE = PRODUCT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Product_price
	* Hibernate value: Product.price
	*/
	String  PRODUCT_PRICE = PRODUCT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Product_productCategory_id
	* Hibernate value: Product.productCategory.id
	*/
	String  PRODUCT_PRODUCT_CATEGORY_ID = PRODUCT_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for ProductDetail entity.
	*/ 
	DAOConstantsEntry PRODUCT_DETAIL_ENTRY = DAOConstants.getDAOConstant(ProductDetail.class);

	/** 
	* Alias value: ProductDetail_alt
	* Hibernate value: ProductDetail.alt
	*/
	String  PRODUCT_DETAIL_ALT = PRODUCT_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProductDetail_id
	* Hibernate value: ProductDetail.id
	*/
	String  PRODUCT_DETAIL_ID = PRODUCT_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProductDetail_label
	* Hibernate value: ProductDetail.label
	*/
	String  PRODUCT_DETAIL_LABEL = PRODUCT_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ProductDetail_language_id
	* Hibernate value: ProductDetail.language.id
	*/
	String  PRODUCT_DETAIL_LANGUAGE_ID = PRODUCT_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ProductDetail_product_id
	* Hibernate value: ProductDetail.product.id
	*/
	String  PRODUCT_DETAIL_PRODUCT_ID = PRODUCT_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ProductDetail_shortLabel
	* Hibernate value: ProductDetail.shortLabel
	*/
	String  PRODUCT_DETAIL_SHORT_LABEL = PRODUCT_DETAIL_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Article entity.
	*/ 
	DAOConstantsEntry ARTICLE_ENTRY = DAOConstants.getDAOConstant(Article.class);

	/** 
	* Alias value: Article_active
	* Hibernate value: Article.active
	*/
	String  ARTICLE_ACTIVE = ARTICLE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Article_alias
	* Hibernate value: Article.alias
	*/
	String  ARTICLE_ALIAS = ARTICLE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Article_articleCategory_id
	* Hibernate value: Article.articleCategory.id
	*/
	String  ARTICLE_ARTICLE_CATEGORY_ID = ARTICLE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Article_articleType
	* Hibernate value: Article.articleType
	*/
	String  ARTICLE_ARTICLE_TYPE = ARTICLE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Article_endDate
	* Hibernate value: Article.endDate
	*/
	String  ARTICLE_END_DATE = ARTICLE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Article_expireDate
	* Hibernate value: Article.expireDate
	*/
	String  ARTICLE_EXPIRE_DATE = ARTICLE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Article_id
	* Hibernate value: Article.id
	*/
	String  ARTICLE_ID = ARTICLE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Article_image
	* Hibernate value: Article.image
	*/
	String  ARTICLE_IMAGE = ARTICLE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Article_initDate
	* Hibernate value: Article.initDate
	*/
	String  ARTICLE_INIT_DATE = ARTICLE_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Article_position
	* Hibernate value: Article.position
	*/
	String  ARTICLE_POSITION = ARTICLE_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Article_publishDate
	* Hibernate value: Article.publishDate
	*/
	String  ARTICLE_PUBLISH_DATE = ARTICLE_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Article_thumbnail
	* Hibernate value: Article.thumbnail
	*/
	String  ARTICLE_THUMBNAIL = ARTICLE_ENTRY.getAliasNames()[11];



	/** 
	* DAOConstantsEntry for ArticleDetail entity.
	*/ 
	DAOConstantsEntry ARTICLE_DETAIL_ENTRY = DAOConstants.getDAOConstant(ArticleDetail.class);

	/** 
	* Alias value: ArticleDetail_alt
	* Hibernate value: ArticleDetail.alt
	*/
	String  ARTICLE_DETAIL_ALT = ARTICLE_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ArticleDetail_alt_thumbnail
	* Hibernate value: ArticleDetail.alt_thumbnail
	*/
	String  ARTICLE_DETAIL_ALT_THUMBNAIL = ARTICLE_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ArticleDetail_article_id
	* Hibernate value: ArticleDetail.article.id
	*/
	String  ARTICLE_DETAIL_ARTICLE_ID = ARTICLE_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ArticleDetail_content
	* Hibernate value: ArticleDetail.content
	*/
	String  ARTICLE_DETAIL_CONTENT = ARTICLE_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ArticleDetail_id
	* Hibernate value: ArticleDetail.id
	*/
	String  ARTICLE_DETAIL_ID = ARTICLE_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ArticleDetail_image_info
	* Hibernate value: ArticleDetail.image_info
	*/
	String  ARTICLE_DETAIL_IMAGE_INFO = ARTICLE_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ArticleDetail_language_id
	* Hibernate value: ArticleDetail.language.id
	*/
	String  ARTICLE_DETAIL_LANGUAGE_ID = ARTICLE_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ArticleDetail_subtitle
	* Hibernate value: ArticleDetail.subtitle
	*/
	String  ARTICLE_DETAIL_SUBTITLE = ARTICLE_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ArticleDetail_title
	* Hibernate value: ArticleDetail.title
	*/
	String  ARTICLE_DETAIL_TITLE = ARTICLE_DETAIL_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for ArticleCategory entity.
	*/ 
	DAOConstantsEntry ARTICLE_CATEGORY_ENTRY = DAOConstants.getDAOConstant(ArticleCategory.class);

	/** 
	* Alias value: ArticleCategory_active
	* Hibernate value: ArticleCategory.active
	*/
	String  ARTICLE_CATEGORY_ACTIVE = ARTICLE_CATEGORY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ArticleCategory_alias
	* Hibernate value: ArticleCategory.alias
	*/
	String  ARTICLE_CATEGORY_ALIAS = ARTICLE_CATEGORY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ArticleCategory_elementSection_id
	* Hibernate value: ArticleCategory.elementSection.id
	*/
	String  ARTICLE_CATEGORY_ELEMENT_SECTION_ID = ARTICLE_CATEGORY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ArticleCategory_id
	* Hibernate value: ArticleCategory.id
	*/
	String  ARTICLE_CATEGORY_ID = ARTICLE_CATEGORY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ArticleCategory_position
	* Hibernate value: ArticleCategory.position
	*/
	String  ARTICLE_CATEGORY_POSITION = ARTICLE_CATEGORY_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ArticleCategory_section_id
	* Hibernate value: ArticleCategory.section.id
	*/
	String  ARTICLE_CATEGORY_SECTION_ID = ARTICLE_CATEGORY_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for ArticleCategoryDetail entity.
	*/ 
	DAOConstantsEntry ARTICLE_CATEGORY_DETAIL_ENTRY = DAOConstants.getDAOConstant(ArticleCategoryDetail.class);

	/** 
	* Alias value: ArticleCategoryDetail_articleCategory_id
	* Hibernate value: ArticleCategoryDetail.articleCategory.id
	*/
	String  ARTICLE_CATEGORY_DETAIL_ARTICLE_CATEGORY_ID = ARTICLE_CATEGORY_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ArticleCategoryDetail_id
	* Hibernate value: ArticleCategoryDetail.id
	*/
	String  ARTICLE_CATEGORY_DETAIL_ID = ARTICLE_CATEGORY_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ArticleCategoryDetail_label
	* Hibernate value: ArticleCategoryDetail.label
	*/
	String  ARTICLE_CATEGORY_DETAIL_LABEL = ARTICLE_CATEGORY_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ArticleCategoryDetail_language_id
	* Hibernate value: ArticleCategoryDetail.language.id
	*/
	String  ARTICLE_CATEGORY_DETAIL_LANGUAGE_ID = ARTICLE_CATEGORY_DETAIL_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Banner entity.
	*/ 
	DAOConstantsEntry BANNER_ENTRY = DAOConstants.getDAOConstant(Banner.class);

	/** 
	* Alias value: Banner_active
	* Hibernate value: Banner.active
	*/
	String  BANNER_ACTIVE = BANNER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Banner_alias
	* Hibernate value: Banner.alias
	*/
	String  BANNER_ALIAS = BANNER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Banner_bannerCategory_id
	* Hibernate value: Banner.bannerCategory.id
	*/
	String  BANNER_BANNER_CATEGORY_ID = BANNER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Banner_id
	* Hibernate value: Banner.id
	*/
	String  BANNER_ID = BANNER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Banner_position
	* Hibernate value: Banner.position
	*/
	String  BANNER_POSITION = BANNER_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for BannerDetail entity.
	*/ 
	DAOConstantsEntry BANNER_DETAIL_ENTRY = DAOConstants.getDAOConstant(BannerDetail.class);

	/** 
	* Alias value: BannerDetail_banner_id
	* Hibernate value: BannerDetail.banner.id
	*/
	String  BANNER_DETAIL_BANNER_ID = BANNER_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: BannerDetail_description
	* Hibernate value: BannerDetail.description
	*/
	String  BANNER_DETAIL_DESCRIPTION = BANNER_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: BannerDetail_id
	* Hibernate value: BannerDetail.id
	*/
	String  BANNER_DETAIL_ID = BANNER_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: BannerDetail_image
	* Hibernate value: BannerDetail.image
	*/
	String  BANNER_DETAIL_IMAGE = BANNER_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: BannerDetail_label
	* Hibernate value: BannerDetail.label
	*/
	String  BANNER_DETAIL_LABEL = BANNER_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: BannerDetail_language_id
	* Hibernate value: BannerDetail.language.id
	*/
	String  BANNER_DETAIL_LANGUAGE_ID = BANNER_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: BannerDetail_url
	* Hibernate value: BannerDetail.url
	*/
	String  BANNER_DETAIL_URL = BANNER_DETAIL_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for BannerCategory entity.
	*/ 
	DAOConstantsEntry BANNER_CATEGORY_ENTRY = DAOConstants.getDAOConstant(BannerCategory.class);

	/** 
	* Alias value: BannerCategory_active
	* Hibernate value: BannerCategory.active
	*/
	String  BANNER_CATEGORY_ACTIVE = BANNER_CATEGORY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: BannerCategory_alias
	* Hibernate value: BannerCategory.alias
	*/
	String  BANNER_CATEGORY_ALIAS = BANNER_CATEGORY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: BannerCategory_id
	* Hibernate value: BannerCategory.id
	*/
	String  BANNER_CATEGORY_ID = BANNER_CATEGORY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: BannerCategory_position
	* Hibernate value: BannerCategory.position
	*/
	String  BANNER_CATEGORY_POSITION = BANNER_CATEGORY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: BannerCategory_section_id
	* Hibernate value: BannerCategory.section.id
	*/
	String  BANNER_CATEGORY_SECTION_ID = BANNER_CATEGORY_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for BannerCategoryDetail entity.
	*/ 
	DAOConstantsEntry BANNER_CATEGORY_DETAIL_ENTRY = DAOConstants.getDAOConstant(BannerCategoryDetail.class);

	/** 
	* Alias value: BannerCategoryDetail_bannerCategory_id
	* Hibernate value: BannerCategoryDetail.bannerCategory.id
	*/
	String  BANNER_CATEGORY_DETAIL_BANNER_CATEGORY_ID = BANNER_CATEGORY_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: BannerCategoryDetail_id
	* Hibernate value: BannerCategoryDetail.id
	*/
	String  BANNER_CATEGORY_DETAIL_ID = BANNER_CATEGORY_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: BannerCategoryDetail_label
	* Hibernate value: BannerCategoryDetail.label
	*/
	String  BANNER_CATEGORY_DETAIL_LABEL = BANNER_CATEGORY_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: BannerCategoryDetail_language_id
	* Hibernate value: BannerCategoryDetail.language.id
	*/
	String  BANNER_CATEGORY_DETAIL_LANGUAGE_ID = BANNER_CATEGORY_DETAIL_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Download entity.
	*/ 
	DAOConstantsEntry DOWNLOAD_ENTRY = DAOConstants.getDAOConstant(Download.class);

	/** 
	* Alias value: Download_active
	* Hibernate value: Download.active
	*/
	String  DOWNLOAD_ACTIVE = DOWNLOAD_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Download_alias
	* Hibernate value: Download.alias
	*/
	String  DOWNLOAD_ALIAS = DOWNLOAD_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Download_downloadCategory_id
	* Hibernate value: Download.downloadCategory.id
	*/
	String  DOWNLOAD_DOWNLOAD_CATEGORY_ID = DOWNLOAD_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Download_id
	* Hibernate value: Download.id
	*/
	String  DOWNLOAD_ID = DOWNLOAD_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Download_position
	* Hibernate value: Download.position
	*/
	String  DOWNLOAD_POSITION = DOWNLOAD_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Download_publishDate
	* Hibernate value: Download.publishDate
	*/
	String  DOWNLOAD_PUBLISH_DATE = DOWNLOAD_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Download_size
	* Hibernate value: Download.size
	*/
	String  DOWNLOAD_SIZE = DOWNLOAD_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Download_type
	* Hibernate value: Download.type
	*/
	String  DOWNLOAD_TYPE = DOWNLOAD_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for DownloadDetail entity.
	*/ 
	DAOConstantsEntry DOWNLOAD_DETAIL_ENTRY = DAOConstants.getDAOConstant(DownloadDetail.class);

	/** 
	* Alias value: DownloadDetail_description
	* Hibernate value: DownloadDetail.description
	*/
	String  DOWNLOAD_DETAIL_DESCRIPTION = DOWNLOAD_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: DownloadDetail_download_id
	* Hibernate value: DownloadDetail.download.id
	*/
	String  DOWNLOAD_DETAIL_DOWNLOAD_ID = DOWNLOAD_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: DownloadDetail_file
	* Hibernate value: DownloadDetail.file
	*/
	String  DOWNLOAD_DETAIL_FILE = DOWNLOAD_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: DownloadDetail_id
	* Hibernate value: DownloadDetail.id
	*/
	String  DOWNLOAD_DETAIL_ID = DOWNLOAD_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: DownloadDetail_language_id
	* Hibernate value: DownloadDetail.language.id
	*/
	String  DOWNLOAD_DETAIL_LANGUAGE_ID = DOWNLOAD_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: DownloadDetail_title
	* Hibernate value: DownloadDetail.title
	*/
	String  DOWNLOAD_DETAIL_TITLE = DOWNLOAD_DETAIL_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for DownloadCategory entity.
	*/ 
	DAOConstantsEntry DOWNLOAD_CATEGORY_ENTRY = DAOConstants.getDAOConstant(DownloadCategory.class);

	/** 
	* Alias value: DownloadCategory_active
	* Hibernate value: DownloadCategory.active
	*/
	String  DOWNLOAD_CATEGORY_ACTIVE = DOWNLOAD_CATEGORY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: DownloadCategory_alias
	* Hibernate value: DownloadCategory.alias
	*/
	String  DOWNLOAD_CATEGORY_ALIAS = DOWNLOAD_CATEGORY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: DownloadCategory_id
	* Hibernate value: DownloadCategory.id
	*/
	String  DOWNLOAD_CATEGORY_ID = DOWNLOAD_CATEGORY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: DownloadCategory_image
	* Hibernate value: DownloadCategory.image
	*/
	String  DOWNLOAD_CATEGORY_IMAGE = DOWNLOAD_CATEGORY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: DownloadCategory_itemsPerPage
	* Hibernate value: DownloadCategory.itemsPerPage
	*/
	String  DOWNLOAD_CATEGORY_ITEMS_PER_PAGE = DOWNLOAD_CATEGORY_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: DownloadCategory_position
	* Hibernate value: DownloadCategory.position
	*/
	String  DOWNLOAD_CATEGORY_POSITION = DOWNLOAD_CATEGORY_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: DownloadCategory_section_id
	* Hibernate value: DownloadCategory.section.id
	*/
	String  DOWNLOAD_CATEGORY_SECTION_ID = DOWNLOAD_CATEGORY_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for DownloadCategoryDetail entity.
	*/ 
	DAOConstantsEntry DOWNLOAD_CATEGORY_DETAIL_ENTRY = DAOConstants.getDAOConstant(DownloadCategoryDetail.class);

	/** 
	* Alias value: DownloadCategoryDetail_downloadCategory_id
	* Hibernate value: DownloadCategoryDetail.downloadCategory.id
	*/
	String  DOWNLOAD_CATEGORY_DETAIL_DOWNLOAD_CATEGORY_ID = DOWNLOAD_CATEGORY_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: DownloadCategoryDetail_id
	* Hibernate value: DownloadCategoryDetail.id
	*/
	String  DOWNLOAD_CATEGORY_DETAIL_ID = DOWNLOAD_CATEGORY_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: DownloadCategoryDetail_label
	* Hibernate value: DownloadCategoryDetail.label
	*/
	String  DOWNLOAD_CATEGORY_DETAIL_LABEL = DOWNLOAD_CATEGORY_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: DownloadCategoryDetail_language_id
	* Hibernate value: DownloadCategoryDetail.language.id
	*/
	String  DOWNLOAD_CATEGORY_DETAIL_LANGUAGE_ID = DOWNLOAD_CATEGORY_DETAIL_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for AlbumCategory entity.
	*/ 
	DAOConstantsEntry ALBUM_CATEGORY_ENTRY = DAOConstants.getDAOConstant(AlbumCategory.class);

	/** 
	* Alias value: AlbumCategory_active
	* Hibernate value: AlbumCategory.active
	*/
	String  ALBUM_CATEGORY_ACTIVE = ALBUM_CATEGORY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AlbumCategory_alias
	* Hibernate value: AlbumCategory.alias
	*/
	String  ALBUM_CATEGORY_ALIAS = ALBUM_CATEGORY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AlbumCategory_id
	* Hibernate value: AlbumCategory.id
	*/
	String  ALBUM_CATEGORY_ID = ALBUM_CATEGORY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AlbumCategory_position
	* Hibernate value: AlbumCategory.position
	*/
	String  ALBUM_CATEGORY_POSITION = ALBUM_CATEGORY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: AlbumCategory_section_id
	* Hibernate value: AlbumCategory.section.id
	*/
	String  ALBUM_CATEGORY_SECTION_ID = ALBUM_CATEGORY_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for AlbumCategoryDetail entity.
	*/ 
	DAOConstantsEntry ALBUM_CATEGORY_DETAIL_ENTRY = DAOConstants.getDAOConstant(AlbumCategoryDetail.class);

	/** 
	* Alias value: AlbumCategoryDetail_albumCategory_id
	* Hibernate value: AlbumCategoryDetail.albumCategory.id
	*/
	String  ALBUM_CATEGORY_DETAIL_ALBUM_CATEGORY_ID = ALBUM_CATEGORY_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AlbumCategoryDetail_id
	* Hibernate value: AlbumCategoryDetail.id
	*/
	String  ALBUM_CATEGORY_DETAIL_ID = ALBUM_CATEGORY_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AlbumCategoryDetail_label
	* Hibernate value: AlbumCategoryDetail.label
	*/
	String  ALBUM_CATEGORY_DETAIL_LABEL = ALBUM_CATEGORY_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AlbumCategoryDetail_language_id
	* Hibernate value: AlbumCategoryDetail.language.id
	*/
	String  ALBUM_CATEGORY_DETAIL_LANGUAGE_ID = ALBUM_CATEGORY_DETAIL_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Album entity.
	*/ 
	DAOConstantsEntry ALBUM_ENTRY = DAOConstants.getDAOConstant(Album.class);

	/** 
	* Alias value: Album_active
	* Hibernate value: Album.active
	*/
	String  ALBUM_ACTIVE = ALBUM_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Album_albumCategory_id
	* Hibernate value: Album.albumCategory.id
	*/
	String  ALBUM_ALBUM_CATEGORY_ID = ALBUM_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Album_alias
	* Hibernate value: Album.alias
	*/
	String  ALBUM_ALIAS = ALBUM_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Album_id
	* Hibernate value: Album.id
	*/
	String  ALBUM_ID = ALBUM_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Album_image
	* Hibernate value: Album.image
	*/
	String  ALBUM_IMAGE = ALBUM_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Album_itemsPerPage
	* Hibernate value: Album.itemsPerPage
	*/
	String  ALBUM_ITEMS_PER_PAGE = ALBUM_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Album_position
	* Hibernate value: Album.position
	*/
	String  ALBUM_POSITION = ALBUM_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Album_publishDate
	* Hibernate value: Album.publishDate
	*/
	String  ALBUM_PUBLISH_DATE = ALBUM_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for AlbumDetail entity.
	*/ 
	DAOConstantsEntry ALBUM_DETAIL_ENTRY = DAOConstants.getDAOConstant(AlbumDetail.class);

	/** 
	* Alias value: AlbumDetail_album_id
	* Hibernate value: AlbumDetail.album.id
	*/
	String  ALBUM_DETAIL_ALBUM_ID = ALBUM_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AlbumDetail_alt
	* Hibernate value: AlbumDetail.alt
	*/
	String  ALBUM_DETAIL_ALT = ALBUM_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AlbumDetail_description
	* Hibernate value: AlbumDetail.description
	*/
	String  ALBUM_DETAIL_DESCRIPTION = ALBUM_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AlbumDetail_id
	* Hibernate value: AlbumDetail.id
	*/
	String  ALBUM_DETAIL_ID = ALBUM_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: AlbumDetail_language_id
	* Hibernate value: AlbumDetail.language.id
	*/
	String  ALBUM_DETAIL_LANGUAGE_ID = ALBUM_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: AlbumDetail_title
	* Hibernate value: AlbumDetail.title
	*/
	String  ALBUM_DETAIL_TITLE = ALBUM_DETAIL_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for AlbumImage entity.
	*/ 
	DAOConstantsEntry ALBUM_IMAGE_ENTRY = DAOConstants.getDAOConstant(AlbumImage.class);

	/** 
	* Alias value: AlbumImage_active
	* Hibernate value: AlbumImage.active
	*/
	String  ALBUM_IMAGE_ACTIVE = ALBUM_IMAGE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AlbumImage_album_id
	* Hibernate value: AlbumImage.album.id
	*/
	String  ALBUM_IMAGE_ALBUM_ID = ALBUM_IMAGE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AlbumImage_id
	* Hibernate value: AlbumImage.id
	*/
	String  ALBUM_IMAGE_ID = ALBUM_IMAGE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AlbumImage_image
	* Hibernate value: AlbumImage.image
	*/
	String  ALBUM_IMAGE_IMAGE = ALBUM_IMAGE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: AlbumImage_position
	* Hibernate value: AlbumImage.position
	*/
	String  ALBUM_IMAGE_POSITION = ALBUM_IMAGE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: AlbumImage_thumbnail
	* Hibernate value: AlbumImage.thumbnail
	*/
	String  ALBUM_IMAGE_THUMBNAIL = ALBUM_IMAGE_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for AlbumImageDetail entity.
	*/ 
	DAOConstantsEntry ALBUM_IMAGE_DETAIL_ENTRY = DAOConstants.getDAOConstant(AlbumImageDetail.class);

	/** 
	* Alias value: AlbumImageDetail_albumImage_id
	* Hibernate value: AlbumImageDetail.albumImage.id
	*/
	String  ALBUM_IMAGE_DETAIL_ALBUM_IMAGE_ID = ALBUM_IMAGE_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AlbumImageDetail_alt
	* Hibernate value: AlbumImageDetail.alt
	*/
	String  ALBUM_IMAGE_DETAIL_ALT = ALBUM_IMAGE_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AlbumImageDetail_alt_thumbnail
	* Hibernate value: AlbumImageDetail.alt_thumbnail
	*/
	String  ALBUM_IMAGE_DETAIL_ALT_THUMBNAIL = ALBUM_IMAGE_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AlbumImageDetail_description
	* Hibernate value: AlbumImageDetail.description
	*/
	String  ALBUM_IMAGE_DETAIL_DESCRIPTION = ALBUM_IMAGE_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: AlbumImageDetail_id
	* Hibernate value: AlbumImageDetail.id
	*/
	String  ALBUM_IMAGE_DETAIL_ID = ALBUM_IMAGE_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: AlbumImageDetail_language_id
	* Hibernate value: AlbumImageDetail.language.id
	*/
	String  ALBUM_IMAGE_DETAIL_LANGUAGE_ID = ALBUM_IMAGE_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: AlbumImageDetail_title
	* Hibernate value: AlbumImageDetail.title
	*/
	String  ALBUM_IMAGE_DETAIL_TITLE = ALBUM_IMAGE_DETAIL_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for Sidebar entity.
	*/ 
	DAOConstantsEntry SIDEBAR_ENTRY = DAOConstants.getDAOConstant(Sidebar.class);

	/** 
	* Alias value: Sidebar_alias
	* Hibernate value: Sidebar.alias
	*/
	String  SIDEBAR_ALIAS = SIDEBAR_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Sidebar_default_
	* Hibernate value: Sidebar.default_
	*/
	String  SIDEBAR_DEFAULT_ = SIDEBAR_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Sidebar_id
	* Hibernate value: Sidebar.id
	*/
	String  SIDEBAR_ID = SIDEBAR_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for SidebarOption entity.
	*/ 
	DAOConstantsEntry SIDEBAR_OPTION_ENTRY = DAOConstants.getDAOConstant(SidebarOption.class);

	/** 
	* Alias value: SidebarOption_active
	* Hibernate value: SidebarOption.active
	*/
	String  SIDEBAR_OPTION_ACTIVE = SIDEBAR_OPTION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SidebarOption_alias
	* Hibernate value: SidebarOption.alias
	*/
	String  SIDEBAR_OPTION_ALIAS = SIDEBAR_OPTION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SidebarOption_id
	* Hibernate value: SidebarOption.id
	*/
	String  SIDEBAR_OPTION_ID = SIDEBAR_OPTION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SidebarOption_ident
	* Hibernate value: SidebarOption.ident
	*/
	String  SIDEBAR_OPTION_IDENT = SIDEBAR_OPTION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SidebarOption_level
	* Hibernate value: SidebarOption.level
	*/
	String  SIDEBAR_OPTION_LEVEL = SIDEBAR_OPTION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: SidebarOption_position
	* Hibernate value: SidebarOption.position
	*/
	String  SIDEBAR_OPTION_POSITION = SIDEBAR_OPTION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: SidebarOption_side
	* Hibernate value: SidebarOption.side
	*/
	String  SIDEBAR_OPTION_SIDE = SIDEBAR_OPTION_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: SidebarOption_sidebar_id
	* Hibernate value: SidebarOption.sidebar.id
	*/
	String  SIDEBAR_OPTION_SIDEBAR_ID = SIDEBAR_OPTION_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: SidebarOption_type
	* Hibernate value: SidebarOption.type
	*/
	String  SIDEBAR_OPTION_TYPE = SIDEBAR_OPTION_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for SidebarOptionDetail entity.
	*/ 
	DAOConstantsEntry SIDEBAR_OPTION_DETAIL_ENTRY = DAOConstants.getDAOConstant(SidebarOptionDetail.class);

	/** 
	* Alias value: SidebarOptionDetail_id
	* Hibernate value: SidebarOptionDetail.id
	*/
	String  SIDEBAR_OPTION_DETAIL_ID = SIDEBAR_OPTION_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SidebarOptionDetail_label
	* Hibernate value: SidebarOptionDetail.label
	*/
	String  SIDEBAR_OPTION_DETAIL_LABEL = SIDEBAR_OPTION_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SidebarOptionDetail_language_id
	* Hibernate value: SidebarOptionDetail.language.id
	*/
	String  SIDEBAR_OPTION_DETAIL_LANGUAGE_ID = SIDEBAR_OPTION_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SidebarOptionDetail_sidebar_option_id
	* Hibernate value: SidebarOptionDetail.sidebar_option.id
	*/
	String  SIDEBAR_OPTION_DETAIL_SIDEBAR_OPTION_ID = SIDEBAR_OPTION_DETAIL_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Section entity.
	*/ 
	DAOConstantsEntry SECTION_ENTRY = DAOConstants.getDAOConstant(Section.class);

	/** 
	* Alias value: Section_alias
	* Hibernate value: Section.alias
	*/
	String  SECTION_ALIAS = SECTION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Section_default_
	* Hibernate value: Section.default_
	*/
	String  SECTION_DEFAULT_ = SECTION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Section_footer_id
	* Hibernate value: Section.footer.id
	*/
	String  SECTION_FOOTER_ID = SECTION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Section_header_id
	* Hibernate value: Section.header.id
	*/
	String  SECTION_HEADER_ID = SECTION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Section_id
	* Hibernate value: Section.id
	*/
	String  SECTION_ID = SECTION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Section_menu_alt_id
	* Hibernate value: Section.menu_alt.id
	*/
	String  SECTION_MENU_ALT_ID = SECTION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Section_menu_id
	* Hibernate value: Section.menu.id
	*/
	String  SECTION_MENU_ID = SECTION_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Section_parent__id
	* Hibernate value: Section.parent_.id
	*/
	String  SECTION_PARENT__ID = SECTION_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Section_parent_sidebar_left
	* Hibernate value: Section.parent_sidebar_left
	*/
	String  SECTION_PARENT_SIDEBAR_LEFT = SECTION_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Section_parent_sidebar_right
	* Hibernate value: Section.parent_sidebar_right
	*/
	String  SECTION_PARENT_SIDEBAR_RIGHT = SECTION_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Section_show_footer
	* Hibernate value: Section.show_footer
	*/
	String  SECTION_SHOW_FOOTER = SECTION_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Section_show_header
	* Hibernate value: Section.show_header
	*/
	String  SECTION_SHOW_HEADER = SECTION_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Section_show_menu
	* Hibernate value: Section.show_menu
	*/
	String  SECTION_SHOW_MENU = SECTION_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Section_show_menu_alt
	* Hibernate value: Section.show_menu_alt
	*/
	String  SECTION_SHOW_MENU_ALT = SECTION_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Section_show_sidebar_left
	* Hibernate value: Section.show_sidebar_left
	*/
	String  SECTION_SHOW_SIDEBAR_LEFT = SECTION_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Section_show_sidebar_right
	* Hibernate value: Section.show_sidebar_right
	*/
	String  SECTION_SHOW_SIDEBAR_RIGHT = SECTION_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Section_sidebar_id
	* Hibernate value: Section.sidebar.id
	*/
	String  SECTION_SIDEBAR_ID = SECTION_ENTRY.getAliasNames()[16];



	/** 
	* DAOConstantsEntry for ModularPage entity.
	*/ 
	DAOConstantsEntry MODULAR_PAGE_ENTRY = DAOConstants.getDAOConstant(ModularPage.class);

	/** 
	* Alias value: ModularPage_active
	* Hibernate value: ModularPage.active
	*/
	String  MODULAR_PAGE_ACTIVE = MODULAR_PAGE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ModularPage_alias
	* Hibernate value: ModularPage.alias
	*/
	String  MODULAR_PAGE_ALIAS = MODULAR_PAGE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ModularPage_homepage
	* Hibernate value: ModularPage.homepage
	*/
	String  MODULAR_PAGE_HOMEPAGE = MODULAR_PAGE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ModularPage_id
	* Hibernate value: ModularPage.id
	*/
	String  MODULAR_PAGE_ID = MODULAR_PAGE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ModularPage_modularType
	* Hibernate value: ModularPage.modularType
	*/
	String  MODULAR_PAGE_MODULAR_TYPE = MODULAR_PAGE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ModularPage_section_id
	* Hibernate value: ModularPage.section.id
	*/
	String  MODULAR_PAGE_SECTION_ID = MODULAR_PAGE_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for ModularPageOption entity.
	*/ 
	DAOConstantsEntry MODULAR_PAGE_OPTION_ENTRY = DAOConstants.getDAOConstant(ModularPageOption.class);

	/** 
	* Alias value: ModularPageOption_active
	* Hibernate value: ModularPageOption.active
	*/
	String  MODULAR_PAGE_OPTION_ACTIVE = MODULAR_PAGE_OPTION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ModularPageOption_alias
	* Hibernate value: ModularPageOption.alias
	*/
	String  MODULAR_PAGE_OPTION_ALIAS = MODULAR_PAGE_OPTION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ModularPageOption_id
	* Hibernate value: ModularPageOption.id
	*/
	String  MODULAR_PAGE_OPTION_ID = MODULAR_PAGE_OPTION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ModularPageOption_ident
	* Hibernate value: ModularPageOption.ident
	*/
	String  MODULAR_PAGE_OPTION_IDENT = MODULAR_PAGE_OPTION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ModularPageOption_modular_page_id
	* Hibernate value: ModularPageOption.modular_page.id
	*/
	String  MODULAR_PAGE_OPTION_MODULAR_PAGE_ID = MODULAR_PAGE_OPTION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ModularPageOption_position
	* Hibernate value: ModularPageOption.position
	*/
	String  MODULAR_PAGE_OPTION_POSITION = MODULAR_PAGE_OPTION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ModularPageOption_type
	* Hibernate value: ModularPageOption.type
	*/
	String  MODULAR_PAGE_OPTION_TYPE = MODULAR_PAGE_OPTION_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for ModularPageOptionDetail entity.
	*/ 
	DAOConstantsEntry MODULAR_PAGE_OPTION_DETAIL_ENTRY = DAOConstants.getDAOConstant(ModularPageOptionDetail.class);

	/** 
	* Alias value: ModularPageOptionDetail_id
	* Hibernate value: ModularPageOptionDetail.id
	*/
	String  MODULAR_PAGE_OPTION_DETAIL_ID = MODULAR_PAGE_OPTION_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ModularPageOptionDetail_label
	* Hibernate value: ModularPageOptionDetail.label
	*/
	String  MODULAR_PAGE_OPTION_DETAIL_LABEL = MODULAR_PAGE_OPTION_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ModularPageOptionDetail_language_id
	* Hibernate value: ModularPageOptionDetail.language.id
	*/
	String  MODULAR_PAGE_OPTION_DETAIL_LANGUAGE_ID = MODULAR_PAGE_OPTION_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ModularPageOptionDetail_modular_page_option_id
	* Hibernate value: ModularPageOptionDetail.modular_page_option.id
	*/
	String  MODULAR_PAGE_OPTION_DETAIL_MODULAR_PAGE_OPTION_ID = MODULAR_PAGE_OPTION_DETAIL_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for DirectAccessGroup entity.
	*/ 
	DAOConstantsEntry DIRECT_ACCESS_GROUP_ENTRY = DAOConstants.getDAOConstant(DirectAccessGroup.class);

	/** 
	* Alias value: DirectAccessGroup_active
	* Hibernate value: DirectAccessGroup.active
	*/
	String  DIRECT_ACCESS_GROUP_ACTIVE = DIRECT_ACCESS_GROUP_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: DirectAccessGroup_alias
	* Hibernate value: DirectAccessGroup.alias
	*/
	String  DIRECT_ACCESS_GROUP_ALIAS = DIRECT_ACCESS_GROUP_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: DirectAccessGroup_id
	* Hibernate value: DirectAccessGroup.id
	*/
	String  DIRECT_ACCESS_GROUP_ID = DIRECT_ACCESS_GROUP_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: DirectAccessGroup_section_id
	* Hibernate value: DirectAccessGroup.section.id
	*/
	String  DIRECT_ACCESS_GROUP_SECTION_ID = DIRECT_ACCESS_GROUP_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for DirectAccessGroupDetail entity.
	*/ 
	DAOConstantsEntry DIRECT_ACCESS_GROUP_DETAIL_ENTRY = DAOConstants.getDAOConstant(DirectAccessGroupDetail.class);

	/** 
	* Alias value: DirectAccessGroupDetail_directAccessGroup_id
	* Hibernate value: DirectAccessGroupDetail.directAccessGroup.id
	*/
	String  DIRECT_ACCESS_GROUP_DETAIL_DIRECT_ACCESS_GROUP_ID = DIRECT_ACCESS_GROUP_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: DirectAccessGroupDetail_id
	* Hibernate value: DirectAccessGroupDetail.id
	*/
	String  DIRECT_ACCESS_GROUP_DETAIL_ID = DIRECT_ACCESS_GROUP_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: DirectAccessGroupDetail_label
	* Hibernate value: DirectAccessGroupDetail.label
	*/
	String  DIRECT_ACCESS_GROUP_DETAIL_LABEL = DIRECT_ACCESS_GROUP_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: DirectAccessGroupDetail_language_id
	* Hibernate value: DirectAccessGroupDetail.language.id
	*/
	String  DIRECT_ACCESS_GROUP_DETAIL_LANGUAGE_ID = DIRECT_ACCESS_GROUP_DETAIL_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for DirectAccess entity.
	*/ 
	DAOConstantsEntry DIRECT_ACCESS_ENTRY = DAOConstants.getDAOConstant(DirectAccess.class);

	/** 
	* Alias value: DirectAccess_active
	* Hibernate value: DirectAccess.active
	*/
	String  DIRECT_ACCESS_ACTIVE = DIRECT_ACCESS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: DirectAccess_alias
	* Hibernate value: DirectAccess.alias
	*/
	String  DIRECT_ACCESS_ALIAS = DIRECT_ACCESS_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: DirectAccess_directAccessGroup_id
	* Hibernate value: DirectAccess.directAccessGroup.id
	*/
	String  DIRECT_ACCESS_DIRECT_ACCESS_GROUP_ID = DIRECT_ACCESS_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: DirectAccess_id
	* Hibernate value: DirectAccess.id
	*/
	String  DIRECT_ACCESS_ID = DIRECT_ACCESS_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: DirectAccess_ident
	* Hibernate value: DirectAccess.ident
	*/
	String  DIRECT_ACCESS_IDENT = DIRECT_ACCESS_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: DirectAccess_image
	* Hibernate value: DirectAccess.image
	*/
	String  DIRECT_ACCESS_IMAGE = DIRECT_ACCESS_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: DirectAccess_level
	* Hibernate value: DirectAccess.level
	*/
	String  DIRECT_ACCESS_LEVEL = DIRECT_ACCESS_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: DirectAccess_position
	* Hibernate value: DirectAccess.position
	*/
	String  DIRECT_ACCESS_POSITION = DIRECT_ACCESS_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: DirectAccess_type
	* Hibernate value: DirectAccess.type
	*/
	String  DIRECT_ACCESS_TYPE = DIRECT_ACCESS_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for DirectAccessDetail entity.
	*/ 
	DAOConstantsEntry DIRECT_ACCESS_DETAIL_ENTRY = DAOConstants.getDAOConstant(DirectAccessDetail.class);

	/** 
	* Alias value: DirectAccessDetail_description
	* Hibernate value: DirectAccessDetail.description
	*/
	String  DIRECT_ACCESS_DETAIL_DESCRIPTION = DIRECT_ACCESS_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: DirectAccessDetail_directAccess_id
	* Hibernate value: DirectAccessDetail.directAccess.id
	*/
	String  DIRECT_ACCESS_DETAIL_DIRECT_ACCESS_ID = DIRECT_ACCESS_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: DirectAccessDetail_id
	* Hibernate value: DirectAccessDetail.id
	*/
	String  DIRECT_ACCESS_DETAIL_ID = DIRECT_ACCESS_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: DirectAccessDetail_label
	* Hibernate value: DirectAccessDetail.label
	*/
	String  DIRECT_ACCESS_DETAIL_LABEL = DIRECT_ACCESS_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: DirectAccessDetail_language_id
	* Hibernate value: DirectAccessDetail.language.id
	*/
	String  DIRECT_ACCESS_DETAIL_LANGUAGE_ID = DIRECT_ACCESS_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: DirectAccessDetail_url
	* Hibernate value: DirectAccessDetail.url
	*/
	String  DIRECT_ACCESS_DETAIL_URL = DIRECT_ACCESS_DETAIL_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for FaqConfig entity.
	*/ 
	DAOConstantsEntry FAQ_CONFIG_ENTRY = DAOConstants.getDAOConstant(FaqConfig.class);

	/** 
	* Alias value: FaqConfig_id
	* Hibernate value: FaqConfig.id
	*/
	String  FAQ_CONFIG_ID = FAQ_CONFIG_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: FaqConfig_section_id
	* Hibernate value: FaqConfig.section.id
	*/
	String  FAQ_CONFIG_SECTION_ID = FAQ_CONFIG_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for LinkConfig entity.
	*/ 
	DAOConstantsEntry LINK_CONFIG_ENTRY = DAOConstants.getDAOConstant(LinkConfig.class);

	/** 
	* Alias value: LinkConfig_id
	* Hibernate value: LinkConfig.id
	*/
	String  LINK_CONFIG_ID = LINK_CONFIG_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: LinkConfig_section_id
	* Hibernate value: LinkConfig.section.id
	*/
	String  LINK_CONFIG_SECTION_ID = LINK_CONFIG_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for AlbumConfig entity.
	*/ 
	DAOConstantsEntry ALBUM_CONFIG_ENTRY = DAOConstants.getDAOConstant(AlbumConfig.class);

	/** 
	* Alias value: AlbumConfig_id
	* Hibernate value: AlbumConfig.id
	*/
	String  ALBUM_CONFIG_ID = ALBUM_CONFIG_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AlbumConfig_section_id
	* Hibernate value: AlbumConfig.section.id
	*/
	String  ALBUM_CONFIG_SECTION_ID = ALBUM_CONFIG_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for ArticleConfig entity.
	*/ 
	DAOConstantsEntry ARTICLE_CONFIG_ENTRY = DAOConstants.getDAOConstant(ArticleConfig.class);

	/** 
	* Alias value: ArticleConfig_id
	* Hibernate value: ArticleConfig.id
	*/
	String  ARTICLE_CONFIG_ID = ARTICLE_CONFIG_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ArticleConfig_section_id
	* Hibernate value: ArticleConfig.section.id
	*/
	String  ARTICLE_CONFIG_SECTION_ID = ARTICLE_CONFIG_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for ModularPageDetail entity.
	*/ 
	DAOConstantsEntry MODULAR_PAGE_DETAIL_ENTRY = DAOConstants.getDAOConstant(ModularPageDetail.class);

	/** 
	* Alias value: ModularPageDetail_id
	* Hibernate value: ModularPageDetail.id
	*/
	String  MODULAR_PAGE_DETAIL_ID = MODULAR_PAGE_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ModularPageDetail_label
	* Hibernate value: ModularPageDetail.label
	*/
	String  MODULAR_PAGE_DETAIL_LABEL = MODULAR_PAGE_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ModularPageDetail_language_id
	* Hibernate value: ModularPageDetail.language.id
	*/
	String  MODULAR_PAGE_DETAIL_LANGUAGE_ID = MODULAR_PAGE_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ModularPageDetail_modular_page_id
	* Hibernate value: ModularPageDetail.modular_page.id
	*/
	String  MODULAR_PAGE_DETAIL_MODULAR_PAGE_ID = MODULAR_PAGE_DETAIL_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for ArticleRelated entity.
	*/ 
	DAOConstantsEntry ARTICLE_RELATED_ENTRY = DAOConstants.getDAOConstant(ArticleRelated.class);

	/** 
	* Alias value: ArticleRelated_articleParent_id
	* Hibernate value: ArticleRelated.articleParent.id
	*/
	String  ARTICLE_RELATED_ARTICLE_PARENT_ID = ARTICLE_RELATED_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ArticleRelated_articleRelated_id
	* Hibernate value: ArticleRelated.articleRelated.id
	*/
	String  ARTICLE_RELATED_ARTICLE_RELATED_ID = ARTICLE_RELATED_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ArticleRelated_id
	* Hibernate value: ArticleRelated.id
	*/
	String  ARTICLE_RELATED_ID = ARTICLE_RELATED_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for ArticleDocument entity.
	*/ 
	DAOConstantsEntry ARTICLE_DOCUMENT_ENTRY = DAOConstants.getDAOConstant(ArticleDocument.class);

	/** 
	* Alias value: ArticleDocument_alias
	* Hibernate value: ArticleDocument.alias
	*/
	String  ARTICLE_DOCUMENT_ALIAS = ARTICLE_DOCUMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ArticleDocument_article_id
	* Hibernate value: ArticleDocument.article.id
	*/
	String  ARTICLE_DOCUMENT_ARTICLE_ID = ARTICLE_DOCUMENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ArticleDocument_id
	* Hibernate value: ArticleDocument.id
	*/
	String  ARTICLE_DOCUMENT_ID = ARTICLE_DOCUMENT_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for ArticleDocumentDetail entity.
	*/ 
	DAOConstantsEntry ARTICLE_DOCUMENT_DETAIL_ENTRY = DAOConstants.getDAOConstant(ArticleDocumentDetail.class);

	/** 
	* Alias value: ArticleDocumentDetail_articleDocument_id
	* Hibernate value: ArticleDocumentDetail.articleDocument.id
	*/
	String  ARTICLE_DOCUMENT_DETAIL_ARTICLE_DOCUMENT_ID = ARTICLE_DOCUMENT_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ArticleDocumentDetail_description
	* Hibernate value: ArticleDocumentDetail.description
	*/
	String  ARTICLE_DOCUMENT_DETAIL_DESCRIPTION = ARTICLE_DOCUMENT_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ArticleDocumentDetail_file
	* Hibernate value: ArticleDocumentDetail.file
	*/
	String  ARTICLE_DOCUMENT_DETAIL_FILE = ARTICLE_DOCUMENT_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ArticleDocumentDetail_id
	* Hibernate value: ArticleDocumentDetail.id
	*/
	String  ARTICLE_DOCUMENT_DETAIL_ID = ARTICLE_DOCUMENT_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ArticleDocumentDetail_language_id
	* Hibernate value: ArticleDocumentDetail.language.id
	*/
	String  ARTICLE_DOCUMENT_DETAIL_LANGUAGE_ID = ARTICLE_DOCUMENT_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ArticleDocumentDetail_title
	* Hibernate value: ArticleDocumentDetail.title
	*/
	String  ARTICLE_DOCUMENT_DETAIL_TITLE = ARTICLE_DOCUMENT_DETAIL_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for DownloadConfig entity.
	*/ 
	DAOConstantsEntry DOWNLOAD_CONFIG_ENTRY = DAOConstants.getDAOConstant(DownloadConfig.class);

	/** 
	* Alias value: DownloadConfig_id
	* Hibernate value: DownloadConfig.id
	*/
	String  DOWNLOAD_CONFIG_ID = DOWNLOAD_CONFIG_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: DownloadConfig_section_id
	* Hibernate value: DownloadConfig.section.id
	*/
	String  DOWNLOAD_CONFIG_SECTION_ID = DOWNLOAD_CONFIG_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Company entity.
	*/ 
	DAOConstantsEntry COMPANY_ENTRY = DAOConstants.getDAOConstant(Company.class);

	/** 
	* Alias value: Company_address
	* Hibernate value: Company.address
	*/
	String  COMPANY_ADDRESS = COMPANY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Company_email
	* Hibernate value: Company.email
	*/
	String  COMPANY_EMAIL = COMPANY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Company_fax
	* Hibernate value: Company.fax
	*/
	String  COMPANY_FAX = COMPANY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Company_id
	* Hibernate value: Company.id
	*/
	String  COMPANY_ID = COMPANY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Company_locality
	* Hibernate value: Company.locality
	*/
	String  COMPANY_LOCALITY = COMPANY_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Company_logo
	* Hibernate value: Company.logo
	*/
	String  COMPANY_LOGO = COMPANY_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Company_name
	* Hibernate value: Company.name
	*/
	String  COMPANY_NAME = COMPANY_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Company_postal_code
	* Hibernate value: Company.postal_code
	*/
	String  COMPANY_POSTAL_CODE = COMPANY_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Company_province
	* Hibernate value: Company.province
	*/
	String  COMPANY_PROVINCE = COMPANY_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Company_telephone
	* Hibernate value: Company.telephone
	*/
	String  COMPANY_TELEPHONE = COMPANY_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Company_web
	* Hibernate value: Company.web
	*/
	String  COMPANY_WEB = COMPANY_ENTRY.getAliasNames()[10];



	/** 
	* DAOConstantsEntry for Activity entity.
	*/ 
	DAOConstantsEntry ACTIVITY_ENTRY = DAOConstants.getDAOConstant(Activity.class);

	/** 
	* Alias value: Activity_alias
	* Hibernate value: Activity.alias
	*/
	String  ACTIVITY_ALIAS = ACTIVITY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Activity_id
	* Hibernate value: Activity.id
	*/
	String  ACTIVITY_ID = ACTIVITY_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for ActivityDetail entity.
	*/ 
	DAOConstantsEntry ACTIVITY_DETAIL_ENTRY = DAOConstants.getDAOConstant(ActivityDetail.class);

	/** 
	* Alias value: ActivityDetail_activity_id
	* Hibernate value: ActivityDetail.activity.id
	*/
	String  ACTIVITY_DETAIL_ACTIVITY_ID = ACTIVITY_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ActivityDetail_description
	* Hibernate value: ActivityDetail.description
	*/
	String  ACTIVITY_DETAIL_DESCRIPTION = ACTIVITY_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ActivityDetail_id
	* Hibernate value: ActivityDetail.id
	*/
	String  ACTIVITY_DETAIL_ID = ACTIVITY_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ActivityDetail_language_id
	* Hibernate value: ActivityDetail.language.id
	*/
	String  ACTIVITY_DETAIL_LANGUAGE_ID = ACTIVITY_DETAIL_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for CompanyActivity entity.
	*/ 
	DAOConstantsEntry COMPANY_ACTIVITY_ENTRY = DAOConstants.getDAOConstant(CompanyActivity.class);

	/** 
	* Alias value: CompanyActivity_activity_id
	* Hibernate value: CompanyActivity.activity.id
	*/
	String  COMPANY_ACTIVITY_ACTIVITY_ID = COMPANY_ACTIVITY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CompanyActivity_company_id
	* Hibernate value: CompanyActivity.company.id
	*/
	String  COMPANY_ACTIVITY_COMPANY_ID = COMPANY_ACTIVITY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CompanyActivity_id
	* Hibernate value: CompanyActivity.id
	*/
	String  COMPANY_ACTIVITY_ID = COMPANY_ACTIVITY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CompanyActivity_company_name
	* Hibernate value: CompanyActivity.company.name
	*/
	String  COMPANY_ACTIVITY_COMPANY_NAME = COMPANY_ACTIVITY_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Bulletin entity.
	*/ 
	DAOConstantsEntry BULLETIN_ENTRY = DAOConstants.getDAOConstant(Bulletin.class);

	/** 
	* Alias value: Bulletin_alias
	* Hibernate value: Bulletin.alias
	*/
	String  BULLETIN_ALIAS = BULLETIN_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Bulletin_id
	* Hibernate value: Bulletin.id
	*/
	String  BULLETIN_ID = BULLETIN_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Bulletin_publish_date
	* Hibernate value: Bulletin.publish_date
	*/
	String  BULLETIN_PUBLISH_DATE = BULLETIN_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Bulletin_template
	* Hibernate value: Bulletin.template
	*/
	String  BULLETIN_TEMPLATE = BULLETIN_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for BulletinDetail entity.
	*/ 
	DAOConstantsEntry BULLETIN_DETAIL_ENTRY = DAOConstants.getDAOConstant(BulletinDetail.class);

	/** 
	* Alias value: BulletinDetail_bulletin_id
	* Hibernate value: BulletinDetail.bulletin.id
	*/
	String  BULLETIN_DETAIL_BULLETIN_ID = BULLETIN_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: BulletinDetail_content
	* Hibernate value: BulletinDetail.content
	*/
	String  BULLETIN_DETAIL_CONTENT = BULLETIN_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: BulletinDetail_id
	* Hibernate value: BulletinDetail.id
	*/
	String  BULLETIN_DETAIL_ID = BULLETIN_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: BulletinDetail_language_id
	* Hibernate value: BulletinDetail.language.id
	*/
	String  BULLETIN_DETAIL_LANGUAGE_ID = BULLETIN_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: BulletinDetail_title
	* Hibernate value: BulletinDetail.title
	*/
	String  BULLETIN_DETAIL_TITLE = BULLETIN_DETAIL_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for BulletinArticle entity.
	*/ 
	DAOConstantsEntry BULLETIN_ARTICLE_ENTRY = DAOConstants.getDAOConstant(BulletinArticle.class);

	/** 
	* Alias value: BulletinArticle_article_id
	* Hibernate value: BulletinArticle.article.id
	*/
	String  BULLETIN_ARTICLE_ARTICLE_ID = BULLETIN_ARTICLE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: BulletinArticle_bulletin_id
	* Hibernate value: BulletinArticle.bulletin.id
	*/
	String  BULLETIN_ARTICLE_BULLETIN_ID = BULLETIN_ARTICLE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: BulletinArticle_id
	* Hibernate value: BulletinArticle.id
	*/
	String  BULLETIN_ARTICLE_ID = BULLETIN_ARTICLE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: BulletinArticle_position
	* Hibernate value: BulletinArticle.position
	*/
	String  BULLETIN_ARTICLE_POSITION = BULLETIN_ARTICLE_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for BulletinEmail entity.
	*/ 
	DAOConstantsEntry BULLETIN_EMAIL_ENTRY = DAOConstants.getDAOConstant(BulletinEmail.class);

	/** 
	* Alias value: BulletinEmail_email
	* Hibernate value: BulletinEmail.email
	*/
	String  BULLETIN_EMAIL_EMAIL = BULLETIN_EMAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: BulletinEmail_id
	* Hibernate value: BulletinEmail.id
	*/
	String  BULLETIN_EMAIL_ID = BULLETIN_EMAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: BulletinEmail_language_id
	* Hibernate value: BulletinEmail.language.id
	*/
	String  BULLETIN_EMAIL_LANGUAGE_ID = BULLETIN_EMAIL_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for FooterBannerCategory entity.
	*/ 
	DAOConstantsEntry FOOTER_BANNER_CATEGORY_ENTRY = DAOConstants.getDAOConstant(FooterBannerCategory.class);

	/** 
	* Alias value: FooterBannerCategory_bannerCategory_id
	* Hibernate value: FooterBannerCategory.bannerCategory.id
	*/
	String  FOOTER_BANNER_CATEGORY_BANNER_CATEGORY_ID = FOOTER_BANNER_CATEGORY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: FooterBannerCategory_footer_id
	* Hibernate value: FooterBannerCategory.footer.id
	*/
	String  FOOTER_BANNER_CATEGORY_FOOTER_ID = FOOTER_BANNER_CATEGORY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: FooterBannerCategory_id
	* Hibernate value: FooterBannerCategory.id
	*/
	String  FOOTER_BANNER_CATEGORY_ID = FOOTER_BANNER_CATEGORY_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for Diary entity.
	*/ 
	DAOConstantsEntry DIARY_ENTRY = DAOConstants.getDAOConstant(Diary.class);

	/** 
	* Alias value: Diary_categories
	* Hibernate value: Diary.categories
	*/
	String  DIARY_CATEGORIES = DIARY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Diary_elementSection_id
	* Hibernate value: Diary.elementSection.id
	*/
	String  DIARY_ELEMENT_SECTION_ID = DIARY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Diary_id
	* Hibernate value: Diary.id
	*/
	String  DIARY_ID = DIARY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Diary_pastEvents
	* Hibernate value: Diary.pastEvents
	*/
	String  DIARY_PAST_EVENTS = DIARY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Diary_section_id
	* Hibernate value: Diary.section.id
	*/
	String  DIARY_SECTION_ID = DIARY_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for DiaryDetail entity.
	*/ 
	DAOConstantsEntry DIARY_DETAIL_ENTRY = DAOConstants.getDAOConstant(DiaryDetail.class);

	/** 
	* Alias value: DiaryDetail_content
	* Hibernate value: DiaryDetail.content
	*/
	String  DIARY_DETAIL_CONTENT = DIARY_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: DiaryDetail_diary_id
	* Hibernate value: DiaryDetail.diary.id
	*/
	String  DIARY_DETAIL_DIARY_ID = DIARY_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: DiaryDetail_id
	* Hibernate value: DiaryDetail.id
	*/
	String  DIARY_DETAIL_ID = DIARY_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: DiaryDetail_language_id
	* Hibernate value: DiaryDetail.language.id
	*/
	String  DIARY_DETAIL_LANGUAGE_ID = DIARY_DETAIL_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for HiruOrganizerCentre entity.
	*/ 
	DAOConstantsEntry HIRU_ORGANIZER_CENTRE_ENTRY = DAOConstants.getDAOConstant(HiruOrganizerCentre.class);

	/** 
	* Alias value: HiruOrganizerCentre_active
	* Hibernate value: HiruOrganizerCentre.active
	*/
	String  HIRU_ORGANIZER_CENTRE_ACTIVE = HIRU_ORGANIZER_CENTRE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: HiruOrganizerCentre_address
	* Hibernate value: HiruOrganizerCentre.address
	*/
	String  HIRU_ORGANIZER_CENTRE_ADDRESS = HIRU_ORGANIZER_CENTRE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: HiruOrganizerCentre_email
	* Hibernate value: HiruOrganizerCentre.email
	*/
	String  HIRU_ORGANIZER_CENTRE_EMAIL = HIRU_ORGANIZER_CENTRE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: HiruOrganizerCentre_fax
	* Hibernate value: HiruOrganizerCentre.fax
	*/
	String  HIRU_ORGANIZER_CENTRE_FAX = HIRU_ORGANIZER_CENTRE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: HiruOrganizerCentre_id
	* Hibernate value: HiruOrganizerCentre.id
	*/
	String  HIRU_ORGANIZER_CENTRE_ID = HIRU_ORGANIZER_CENTRE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: HiruOrganizerCentre_locality
	* Hibernate value: HiruOrganizerCentre.locality
	*/
	String  HIRU_ORGANIZER_CENTRE_LOCALITY = HIRU_ORGANIZER_CENTRE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: HiruOrganizerCentre_name
	* Hibernate value: HiruOrganizerCentre.name
	*/
	String  HIRU_ORGANIZER_CENTRE_NAME = HIRU_ORGANIZER_CENTRE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: HiruOrganizerCentre_postal_code
	* Hibernate value: HiruOrganizerCentre.postal_code
	*/
	String  HIRU_ORGANIZER_CENTRE_POSTAL_CODE = HIRU_ORGANIZER_CENTRE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: HiruOrganizerCentre_telephone
	* Hibernate value: HiruOrganizerCentre.telephone
	*/
	String  HIRU_ORGANIZER_CENTRE_TELEPHONE = HIRU_ORGANIZER_CENTRE_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: HiruOrganizerCentre_web
	* Hibernate value: HiruOrganizerCentre.web
	*/
	String  HIRU_ORGANIZER_CENTRE_WEB = HIRU_ORGANIZER_CENTRE_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for HiruCourse entity.
	*/ 
	DAOConstantsEntry HIRU_COURSE_ENTRY = DAOConstants.getDAOConstant(HiruCourse.class);

	/** 
	* Alias value: HiruCourse_active
	* Hibernate value: HiruCourse.active
	*/
	String  HIRU_COURSE_ACTIVE = HIRU_COURSE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: HiruCourse_alias
	* Hibernate value: HiruCourse.alias
	*/
	String  HIRU_COURSE_ALIAS = HIRU_COURSE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: HiruCourse_endDate
	* Hibernate value: HiruCourse.endDate
	*/
	String  HIRU_COURSE_END_DATE = HIRU_COURSE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: HiruCourse_hiruOrganizerCentre_id
	* Hibernate value: HiruCourse.hiruOrganizerCentre.id
	*/
	String  HIRU_COURSE_HIRU_ORGANIZER_CENTRE_ID = HIRU_COURSE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: HiruCourse_hiruPlace
	* Hibernate value: HiruCourse.hiruPlace
	*/
	String  HIRU_COURSE_HIRU_PLACE = HIRU_COURSE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: HiruCourse_id
	* Hibernate value: HiruCourse.id
	*/
	String  HIRU_COURSE_ID = HIRU_COURSE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: HiruCourse_initDate
	* Hibernate value: HiruCourse.initDate
	*/
	String  HIRU_COURSE_INIT_DATE = HIRU_COURSE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: HiruCourse_subject
	* Hibernate value: HiruCourse.subject
	*/
	String  HIRU_COURSE_SUBJECT = HIRU_COURSE_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for HiruCourseDetail entity.
	*/ 
	DAOConstantsEntry HIRU_COURSE_DETAIL_ENTRY = DAOConstants.getDAOConstant(HiruCourseDetail.class);

	/** 
	* Alias value: HiruCourseDetail_contents
	* Hibernate value: HiruCourseDetail.contents
	*/
	String  HIRU_COURSE_DETAIL_CONTENTS = HIRU_COURSE_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: HiruCourseDetail_employeeRegistration
	* Hibernate value: HiruCourseDetail.employeeRegistration
	*/
	String  HIRU_COURSE_DETAIL_EMPLOYEE_REGISTRATION = HIRU_COURSE_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: HiruCourseDetail_genericInfo
	* Hibernate value: HiruCourseDetail.genericInfo
	*/
	String  HIRU_COURSE_DETAIL_GENERIC_INFO = HIRU_COURSE_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: HiruCourseDetail_giverEntity
	* Hibernate value: HiruCourseDetail.giverEntity
	*/
	String  HIRU_COURSE_DETAIL_GIVER_ENTITY = HIRU_COURSE_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: HiruCourseDetail_hiruCourse_id
	* Hibernate value: HiruCourseDetail.hiruCourse.id
	*/
	String  HIRU_COURSE_DETAIL_HIRU_COURSE_ID = HIRU_COURSE_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: HiruCourseDetail_id
	* Hibernate value: HiruCourseDetail.id
	*/
	String  HIRU_COURSE_DETAIL_ID = HIRU_COURSE_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: HiruCourseDetail_info
	* Hibernate value: HiruCourseDetail.info
	*/
	String  HIRU_COURSE_DETAIL_INFO = HIRU_COURSE_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: HiruCourseDetail_language_id
	* Hibernate value: HiruCourseDetail.language.id
	*/
	String  HIRU_COURSE_DETAIL_LANGUAGE_ID = HIRU_COURSE_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: HiruCourseDetail_length
	* Hibernate value: HiruCourseDetail.length
	*/
	String  HIRU_COURSE_DETAIL_LENGTH = HIRU_COURSE_DETAIL_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: HiruCourseDetail_name
	* Hibernate value: HiruCourseDetail.name
	*/
	String  HIRU_COURSE_DETAIL_NAME = HIRU_COURSE_DETAIL_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: HiruCourseDetail_notEmployeeRegistration
	* Hibernate value: HiruCourseDetail.notEmployeeRegistration
	*/
	String  HIRU_COURSE_DETAIL_NOT_EMPLOYEE_REGISTRATION = HIRU_COURSE_DETAIL_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: HiruCourseDetail_numberParticipant
	* Hibernate value: HiruCourseDetail.numberParticipant
	*/
	String  HIRU_COURSE_DETAIL_NUMBER_PARTICIPANT = HIRU_COURSE_DETAIL_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: HiruCourseDetail_objetives
	* Hibernate value: HiruCourseDetail.objetives
	*/
	String  HIRU_COURSE_DETAIL_OBJETIVES = HIRU_COURSE_DETAIL_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: HiruCourseDetail_url
	* Hibernate value: HiruCourseDetail.url
	*/
	String  HIRU_COURSE_DETAIL_URL = HIRU_COURSE_DETAIL_ENTRY.getAliasNames()[13];



	/** 
	* DAOConstantsEntry for HiruConfig entity.
	*/ 
	DAOConstantsEntry HIRU_CONFIG_ENTRY = DAOConstants.getDAOConstant(HiruConfig.class);

	/** 
	* Alias value: HiruConfig_id
	* Hibernate value: HiruConfig.id
	*/
	String  HIRU_CONFIG_ID = HIRU_CONFIG_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: HiruConfig_section_id
	* Hibernate value: HiruConfig.section.id
	*/
	String  HIRU_CONFIG_SECTION_ID = HIRU_CONFIG_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for ProductCategoryConfig entity.
	*/ 
	DAOConstantsEntry PRODUCT_CATEGORY_CONFIG_ENTRY = DAOConstants.getDAOConstant(ProductCategoryConfig.class);

	/** 
	* Alias value: ProductCategoryConfig_id
	* Hibernate value: ProductCategoryConfig.id
	*/
	String  PRODUCT_CATEGORY_CONFIG_ID = PRODUCT_CATEGORY_CONFIG_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProductCategoryConfig_section_id
	* Hibernate value: ProductCategoryConfig.section.id
	*/
	String  PRODUCT_CATEGORY_CONFIG_SECTION_ID = PRODUCT_CATEGORY_CONFIG_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for SportCategory entity.
	*/ 
	DAOConstantsEntry SPORT_CATEGORY_ENTRY = DAOConstants.getDAOConstant(SportCategory.class);

	/** 
	* Alias value: SportCategory_alias
	* Hibernate value: SportCategory.alias
	*/
	String  SPORT_CATEGORY_ALIAS = SPORT_CATEGORY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SportCategory_default_
	* Hibernate value: SportCategory.default_
	*/
	String  SPORT_CATEGORY_DEFAULT_ = SPORT_CATEGORY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SportCategory_id
	* Hibernate value: SportCategory.id
	*/
	String  SPORT_CATEGORY_ID = SPORT_CATEGORY_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for SportCategoryDetail entity.
	*/ 
	DAOConstantsEntry SPORT_CATEGORY_DETAIL_ENTRY = DAOConstants.getDAOConstant(SportCategoryDetail.class);

	/** 
	* Alias value: SportCategoryDetail_description
	* Hibernate value: SportCategoryDetail.description
	*/
	String  SPORT_CATEGORY_DETAIL_DESCRIPTION = SPORT_CATEGORY_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SportCategoryDetail_id
	* Hibernate value: SportCategoryDetail.id
	*/
	String  SPORT_CATEGORY_DETAIL_ID = SPORT_CATEGORY_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SportCategoryDetail_language_id
	* Hibernate value: SportCategoryDetail.language.id
	*/
	String  SPORT_CATEGORY_DETAIL_LANGUAGE_ID = SPORT_CATEGORY_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SportCategoryDetail_sportCategory_id
	* Hibernate value: SportCategoryDetail.sportCategory.id
	*/
	String  SPORT_CATEGORY_DETAIL_SPORT_CATEGORY_ID = SPORT_CATEGORY_DETAIL_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for SportClub entity.
	*/ 
	DAOConstantsEntry SPORT_CLUB_ENTRY = DAOConstants.getDAOConstant(SportClub.class);

	/** 
	* Alias value: SportClub_default_
	* Hibernate value: SportClub.default_
	*/
	String  SPORT_CLUB_DEFAULT_ = SPORT_CLUB_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SportClub_description
	* Hibernate value: SportClub.description
	*/
	String  SPORT_CLUB_DESCRIPTION = SPORT_CLUB_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SportClub_id
	* Hibernate value: SportClub.id
	*/
	String  SPORT_CLUB_ID = SPORT_CLUB_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SportClub_image
	* Hibernate value: SportClub.image
	*/
	String  SPORT_CLUB_IMAGE = SPORT_CLUB_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SportClub_logo
	* Hibernate value: SportClub.logo
	*/
	String  SPORT_CLUB_LOGO = SPORT_CLUB_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: SportClub_sportCategory_id
	* Hibernate value: SportClub.sportCategory.id
	*/
	String  SPORT_CLUB_SPORT_CATEGORY_ID = SPORT_CLUB_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: SportClub_stadium
	* Hibernate value: SportClub.stadium
	*/
	String  SPORT_CLUB_STADIUM = SPORT_CLUB_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for SportPosition entity.
	*/ 
	DAOConstantsEntry SPORT_POSITION_ENTRY = DAOConstants.getDAOConstant(SportPosition.class);

	/** 
	* Alias value: SportPosition_alias
	* Hibernate value: SportPosition.alias
	*/
	String  SPORT_POSITION_ALIAS = SPORT_POSITION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SportPosition_id
	* Hibernate value: SportPosition.id
	*/
	String  SPORT_POSITION_ID = SPORT_POSITION_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for SportPositionDetail entity.
	*/ 
	DAOConstantsEntry SPORT_POSITION_DETAIL_ENTRY = DAOConstants.getDAOConstant(SportPositionDetail.class);

	/** 
	* Alias value: SportPositionDetail_description
	* Hibernate value: SportPositionDetail.description
	*/
	String  SPORT_POSITION_DETAIL_DESCRIPTION = SPORT_POSITION_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SportPositionDetail_id
	* Hibernate value: SportPositionDetail.id
	*/
	String  SPORT_POSITION_DETAIL_ID = SPORT_POSITION_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SportPositionDetail_language_id
	* Hibernate value: SportPositionDetail.language.id
	*/
	String  SPORT_POSITION_DETAIL_LANGUAGE_ID = SPORT_POSITION_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SportPositionDetail_sportPosition_id
	* Hibernate value: SportPositionDetail.sportPosition.id
	*/
	String  SPORT_POSITION_DETAIL_SPORT_POSITION_ID = SPORT_POSITION_DETAIL_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for SportNationality entity.
	*/ 
	DAOConstantsEntry SPORT_NATIONALITY_ENTRY = DAOConstants.getDAOConstant(SportNationality.class);

	/** 
	* Alias value: SportNationality_alias
	* Hibernate value: SportNationality.alias
	*/
	String  SPORT_NATIONALITY_ALIAS = SPORT_NATIONALITY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SportNationality_id
	* Hibernate value: SportNationality.id
	*/
	String  SPORT_NATIONALITY_ID = SPORT_NATIONALITY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SportNationality_image
	* Hibernate value: SportNationality.image
	*/
	String  SPORT_NATIONALITY_IMAGE = SPORT_NATIONALITY_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for SportNationalityDetail entity.
	*/ 
	DAOConstantsEntry SPORT_NATIONALITY_DETAIL_ENTRY = DAOConstants.getDAOConstant(SportNationalityDetail.class);

	/** 
	* Alias value: SportNationalityDetail_description
	* Hibernate value: SportNationalityDetail.description
	*/
	String  SPORT_NATIONALITY_DETAIL_DESCRIPTION = SPORT_NATIONALITY_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SportNationalityDetail_id
	* Hibernate value: SportNationalityDetail.id
	*/
	String  SPORT_NATIONALITY_DETAIL_ID = SPORT_NATIONALITY_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SportNationalityDetail_language_id
	* Hibernate value: SportNationalityDetail.language.id
	*/
	String  SPORT_NATIONALITY_DETAIL_LANGUAGE_ID = SPORT_NATIONALITY_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SportNationalityDetail_sportNationality_id
	* Hibernate value: SportNationalityDetail.sportNationality.id
	*/
	String  SPORT_NATIONALITY_DETAIL_SPORT_NATIONALITY_ID = SPORT_NATIONALITY_DETAIL_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for SportSeason entity.
	*/ 
	DAOConstantsEntry SPORT_SEASON_ENTRY = DAOConstants.getDAOConstant(SportSeason.class);

	/** 
	* Alias value: SportSeason_default_
	* Hibernate value: SportSeason.default_
	*/
	String  SPORT_SEASON_DEFAULT_ = SPORT_SEASON_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SportSeason_description
	* Hibernate value: SportSeason.description
	*/
	String  SPORT_SEASON_DESCRIPTION = SPORT_SEASON_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SportSeason_id
	* Hibernate value: SportSeason.id
	*/
	String  SPORT_SEASON_ID = SPORT_SEASON_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for SportPlayer entity.
	*/ 
	DAOConstantsEntry SPORT_PLAYER_ENTRY = DAOConstants.getDAOConstant(SportPlayer.class);

	/** 
	* Alias value: SportPlayer_active
	* Hibernate value: SportPlayer.active
	*/
	String  SPORT_PLAYER_ACTIVE = SPORT_PLAYER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SportPlayer_bornDate
	* Hibernate value: SportPlayer.bornDate
	*/
	String  SPORT_PLAYER_BORN_DATE = SPORT_PLAYER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SportPlayer_bornPlace
	* Hibernate value: SportPlayer.bornPlace
	*/
	String  SPORT_PLAYER_BORN_PLACE = SPORT_PLAYER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SportPlayer_comunitary
	* Hibernate value: SportPlayer.comunitary
	*/
	String  SPORT_PLAYER_COMUNITARY = SPORT_PLAYER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SportPlayer_id
	* Hibernate value: SportPlayer.id
	*/
	String  SPORT_PLAYER_ID = SPORT_PLAYER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: SportPlayer_lenght
	* Hibernate value: SportPlayer.lenght
	*/
	String  SPORT_PLAYER_LENGHT = SPORT_PLAYER_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: SportPlayer_name
	* Hibernate value: SportPlayer.name
	*/
	String  SPORT_PLAYER_NAME = SPORT_PLAYER_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: SportPlayer_number
	* Hibernate value: SportPlayer.number
	*/
	String  SPORT_PLAYER_NUMBER = SPORT_PLAYER_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: SportPlayer_photo
	* Hibernate value: SportPlayer.photo
	*/
	String  SPORT_PLAYER_PHOTO = SPORT_PLAYER_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: SportPlayer_sportClub_id
	* Hibernate value: SportPlayer.sportClub.id
	*/
	String  SPORT_PLAYER_SPORT_CLUB_ID = SPORT_PLAYER_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: SportPlayer_sportNationality_id
	* Hibernate value: SportPlayer.sportNationality.id
	*/
	String  SPORT_PLAYER_SPORT_NATIONALITY_ID = SPORT_PLAYER_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: SportPlayer_sportPosition_id
	* Hibernate value: SportPlayer.sportPosition.id
	*/
	String  SPORT_PLAYER_SPORT_POSITION_ID = SPORT_PLAYER_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: SportPlayer_weight
	* Hibernate value: SportPlayer.weight
	*/
	String  SPORT_PLAYER_WEIGHT = SPORT_PLAYER_ENTRY.getAliasNames()[12];



	/** 
	* DAOConstantsEntry for SportCareerPath entity.
	*/ 
	DAOConstantsEntry SPORT_CAREER_PATH_ENTRY = DAOConstants.getDAOConstant(SportCareerPath.class);

	/** 
	* Alias value: SportCareerPath_club
	* Hibernate value: SportCareerPath.club
	*/
	String  SPORT_CAREER_PATH_CLUB = SPORT_CAREER_PATH_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SportCareerPath_endDate
	* Hibernate value: SportCareerPath.endDate
	*/
	String  SPORT_CAREER_PATH_END_DATE = SPORT_CAREER_PATH_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SportCareerPath_id
	* Hibernate value: SportCareerPath.id
	*/
	String  SPORT_CAREER_PATH_ID = SPORT_CAREER_PATH_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SportCareerPath_initDate
	* Hibernate value: SportCareerPath.initDate
	*/
	String  SPORT_CAREER_PATH_INIT_DATE = SPORT_CAREER_PATH_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SportCareerPath_sportPlayer_id
	* Hibernate value: SportCareerPath.sportPlayer.id
	*/
	String  SPORT_CAREER_PATH_SPORT_PLAYER_ID = SPORT_CAREER_PATH_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for SportCoach entity.
	*/ 
	DAOConstantsEntry SPORT_COACH_ENTRY = DAOConstants.getDAOConstant(SportCoach.class);

	/** 
	* Alias value: SportCoach_id
	* Hibernate value: SportCoach.id
	*/
	String  SPORT_COACH_ID = SPORT_COACH_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SportCoach_job
	* Hibernate value: SportCoach.job
	*/
	String  SPORT_COACH_JOB = SPORT_COACH_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SportCoach_name
	* Hibernate value: SportCoach.name
	*/
	String  SPORT_COACH_NAME = SPORT_COACH_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SportCoach_sportClub_id
	* Hibernate value: SportCoach.sportClub.id
	*/
	String  SPORT_COACH_SPORT_CLUB_ID = SPORT_COACH_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for SportConfig entity.
	*/ 
	DAOConstantsEntry SPORT_CONFIG_ENTRY = DAOConstants.getDAOConstant(SportConfig.class);

	/** 
	* Alias value: SportConfig_id
	* Hibernate value: SportConfig.id
	*/
	String  SPORT_CONFIG_ID = SPORT_CONFIG_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SportConfig_section_id
	* Hibernate value: SportConfig.section.id
	*/
	String  SPORT_CONFIG_SECTION_ID = SPORT_CONFIG_ENTRY.getAliasNames()[1];


}