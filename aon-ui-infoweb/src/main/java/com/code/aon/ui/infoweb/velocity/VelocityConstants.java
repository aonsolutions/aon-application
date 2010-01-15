package com.code.aon.ui.infoweb.velocity;

public interface VelocityConstants {

	String

	//APLICATION CONSTANTS
	TEMPLATE_NAME_PARAM			= "WEBINFO_TEMPLATE_NAME",
	HOMEPAGE_NAME_PARAM			= "WEBINFO_HOMEPAGE_ID",
	DOMAINS_PATH				= "/home/DOMAINS",
	WEBSITE_PATH				= "WEBSITES",
	RESOURCE_PATH				= "DOMAIN-RESOURCES",
	PREVIEW_PREFIX				= "preview.",
	WWW_PREFIX					= "www.",
	IMAGE_PAGE_PREFFIX 			= "aonInfoWeb_",
	DEFAULT_TEMPLATE			= "Default",
	
	CSS_PATH					= "css",
	CSS_STYLE_DEFAULTS			= "default.properties",
	CSSIMG_PATH					= "img",
	IMAGES_PATH					= "images",
	INDEX_TITLE					= "Inicio",

	TEMPORAL_PATH				= "/home/COMMON-RESOURCES/aon-web-info/temporal",

	//TEMPLATES CONSTANTS
	TEMPLATE_PATH				= "/home/COMMON-RESOURCES/aon-web-info/templates",
		
	//VELOCITY PROPERTIES
	VELOCITY_FILE_ENCODING		= "iso-8859-1",
	VELOCITY_LOG_FILE			= "velocity.log",
		
	//VELOCTIY VALUES
	IMAGES_KEY					= "images",
	IMAGE_KEY					= "image",
	MENU_KEY					= "menu",
	ADDRESS_KEY					= "address",
	ADDRESSES_KEY				= "addresses",
	ALL_IMAGES_KEY 				= "all_images",
	FAX_KEY 					= "fax",
	PHONE_KEY					= "phone",
	EMAIL_KEY					= "email",
	DESCRIPTION_KEY				= "description",
	SLOGAN_KEY					= "slogan",
	SCHEDULE_KEY				= "schedule",
	LOGO_KEY					= "logo",
	PAGENAME_KEY				= "pagename",
	GALLERY_KEY					= "gallery",
	RETURN_KEY					= "return",
	NEXT_KEY					= "next",
	PREVIOUS_KEY				= "previous",
	COORDS_KEY					= "coords",
	TEXT_KEY					= "text",
	TITLE_KEY					= "title",
	CONTENT_KEY					= "content",
	COMPANY_KEY					= "company",
	CURRENT_YEAR_KEY			= "currentYear";

	//TEMPLATES
	String INDEX_TEMPLATE		= "index.vm";	
	String GALLERY_TEMPLATE		= "gallery.vm";
	String IMAGE_VIEW_TEMPLATE	= "imageview.vm";
	String LOCATION_TEMPLATE	= "location.vm";
	String CONTACT_TEMPLATE		= "contact.vm";
	String MAIL_TEMPLATE		= "mail.vm";
	String HOME_TEMPLATE		= "home.vm";
	String STYLE_TEMPLATE		= "style.vm";
	String ERROR_TEMPLATE		= "404error.vm";
	
	//FILE
	String ERROR_HTML			= "404error.html";
	String INDEX_HTML			= "index.html";
	String MAIL_PHP				= "mail.php";

}
