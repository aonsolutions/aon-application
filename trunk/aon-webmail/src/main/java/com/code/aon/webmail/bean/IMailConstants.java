package com.code.aon.webmail.bean;

import java.io.File;

public interface IMailConstants {
	
	String PROPERTIES_PATH = "/com/code/aon/webmail/bean/";
	
	String DEFAULT_PROPERTIES = PROPERTIES_PATH + "default.mail.properties";
	
	File WEBMAIL_PROPERTIES = new File( "/home/COMMON-RESOURCES/aon-webmail/mail.properties" );	
	
	String MAIL_MIME_DECODETEXT_STRICT = "mail.mime.decodetext.strict";

	String MAIL_IMAP_SOCKET_FACTORY_PORT = "mail.imap.socketFactory.port";

	String MAIL_IMAP_PORT = "mail.imap.port";

	String MAIL_IMAP_SOCKET_FACTORY_FALLBACK = "mail.imap.socketFactory.fallback";

	String MAIL_IMAP_SOCKET_FACTORY_CLASS = "mail.imap.socketFactory.class";
	
	String MAIL_SMTP_AUTH = "mail.smtp.auth";

	String MAIL_HOST = "mail.host";

	String MAIL_STORE_PROTOCOL = "mail.store.protocol";

	String IMAP = "imap";
	
	String SMTPS = "smtps";

	String SMTP = "smtp";
	
	String X_MAILER = "X-Mailer";
	
	String WEBMAIL_MAILER = "OfficeWeb - AonWebMail 4.11.0";	
	
    String OTHER_FOLDER_NAME = "other";

    String DRAFT_FOLDER_NAME = "Borrador";

    String TRASH_FOLDER_NAME = "Papelera";

    String SENT_FOLDER_NAME = "Enviados";

    String INBOX_FOLDER_NAME = "INBOX";

    String SPAM_FOLDER_NAME = "spam";	
	
}
