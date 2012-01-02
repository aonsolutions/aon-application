package com.code.aon.ui.webmail.controller;

public interface IWebMailConstants {

	String BUNDLE_NAME = "webmailBundle";	
	
	String WEBMAIL_HEADER_NAME = "OfficeWeb - AonWebMail 2.0";
	
	// ************************************************************
	// NAVIGATION
	// ************************************************************
	String NAVIGATION_LOGIN = "login"; 
	String NAVIGATION_FOLDER = "home"; 
	String NAVIGATION_MESSAGE = "message";
	String NAVIGATION_MESSAGE_NEW = "messageNew";
	String NAVIGATION_SEARCH = "messageSearch"; 
	String NAVIGATION_SIGNATURE_FORM = "signature_form";
	String NAVIGATION_MAILACCOUNT_FORM = "mailAccount_form";
	String NAVIGATION_MAILACCOUNT_LIST = "mailAccount_list";

	// ************************************************************
	// BEAN
	// ************************************************************
	String BEAN_LOGIN = "webmailLogin";
	String BEAN_USER = "user";
	String BEAN_MAIL_ACCOUNT = "mailAccount";
	String BEAN_WEBMAIL = "webmail";
	String BEAN_TREE = "webmailTree";
	String BEAN_FOLDER = "webmailFolder";
	String BEAN_MESSAGE = "webmailMessage";
	String BEAN_ATTACH = "webmailAttach";
	String BEAN_SEARCH = "webmailSearch";
	String BEAN_INPUTFILE = "inputFile";
	String BEAN_CONTACT = "contact";
	String BEAN_SIGNATURE = "signature";
	String BEAN_AUTOCOMPLETEEMAILDICC = "autoCompleteEmailDictionary";
	String BEAN_AUTOCOMPLETEEMAIL = "autoCompleteEmailBean";
	String BEAN_MULTISELECTIONEMAIL = "multiSelectionEmailBean";
	String BEAN_SPAM = "spam";

	// ************************************************************
	// CONFIG
	// ************************************************************
	String CONNECT_PROPERTY = "connect";
	String CONNECT_DOMAIN_MAIL_ACCOUNTS_PROPERTY = "connectDomainMailAccounts";
	String SHOW_DOMAIN_MAIL_ACCOUNTS_PROPERTY = "showDomainMailAccounts";
	
	// ************************************************************
	// LDAP
	// ************************************************************

	String WEBMAIL_CONFIG = "aonWebmailConfig";
	String REJECTED_EXTENSIONS = "webmailRejectedExtension";
	String MAX_ATTACHMENT_SIZE = "webmailMaxAttachmentSize";

	// ************************************************************
	// MESSAGE ID
	// ************************************************************	
	
	String ID_DUPLICATED = "ldap_id_duplicated";
	String INVALID_NAME = "ldap_invalid_name";
	String ID_USED = "ldap_id_used";
	
	String CONTACT_DUPLICATED = "webmail_contact_duplicated";
	String CONTACT_USED = "webmail_contact_used";
	String NOT_SERVER_CONNECTED = "webmail_not_server_conected";
	String NOT_MAIL_ACCOUNT = "webmail_not_mail_account";
	String NOT_MAIL_ACCOUNTS = "webmail_not_mail_accounts";
	String SIGNATURE_DUPLICATED = "webmail_signature_duplicated";
	String SIGNATURE_USED = "webmail_signature_used";	
	String MAIL_ACCOUNT_DUPLICATED = "webmail_mailAccount_duplicated";
	String SEND_EMAIL_FINISH = "webmail_send_email_finish";

}
