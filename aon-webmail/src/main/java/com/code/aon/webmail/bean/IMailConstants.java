package com.code.aon.webmail.bean;

import java.io.File;

public interface IMailConstants {
	
	String PROPERTIES_PATH = "/com/code/aon/webmail/bean/";
	
	String DEFAULT_PROPERTIES = PROPERTIES_PATH + "default.mail.properties";
	
	File WEBMAIL_PROPERTIES = new File( "/home/COMMON-RESOURCES/aon-webmail/mail.properties" );
	
	int DEFAULT_IMAP_PORT = 143;
	
	int DEFAULT_POP3_PORT = 110;
	
	int DEFAULT_SMTP_PORT = 25;	
	
	String IMAP = "imap";
	
	String SMTPS = "smtps";

	String SMTP = "smtp";
		
	String MAIL_PREFIX = "mail.";
	
	String PORT = ".port";

	String SOCKET_FACTORY_PORT = ".socketFactory.port";
	
	String SOCKET_FACTORY_FALLBACK = ".socketFactory.fallback";

	String SOCKET_FACTORY_CLASS = ".socketFactory.class";
	
	String STARTTLS_ENABLE = ".starttls.enable";
	
	String TIMEOUT = ".timeout";
	
	String CONNECTION_TIMEOUT = ".connectiontimeout";
	
	String DEFAULT_TIMEOUT = "30000";
	
	String AUTH = ".auth";

	String LOCALHOST = ".localhost";

	String MAIL_HOST = MAIL_PREFIX + "host";
	
	String MAIL_STORE_PROTOCOL = MAIL_PREFIX + "store.protocol";
	
	String MAIL_TRANSPORT_PROTOCOL = MAIL_PREFIX + "transport.protocol";

	String X_MAILER = "X-Mailer";
	
	String WEBMAIL_MAILER = "OfficeWeb - AonWebMail 4.11.0";	
	
    String OTHER_FOLDER_NAME = "other";

    String DRAFT_FOLDER_NAME = "Borrador";

    String TRASH_FOLDER_NAME = "Papelera";

    String SENT_FOLDER_NAME = "Enviados";

    String INBOX_FOLDER_NAME = "INBOX";

    String SPAM_FOLDER_NAME = "spam";	
	
}
