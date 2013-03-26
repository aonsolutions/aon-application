package com.code.aon.ui.publisher.controller;

public interface IPublisherConstants {
	
	String FTP_SERVER = "ftp.server";
	String FTP_USER = "ftp.user";
	String FTP_PASSWORD = "ftp.password";
	
	String BUNDLE_NAME = "publisherBundle";

	String PUBLISH_OK = "publisher_publish_ok";
	String PUBLISH_ERROR = "publisher_publish_error";
	String DELETE_OK = "publisher_delete_ok";
	String DELETE_ERROR = "publisher_delete_error";

	String FTP_CONNECTING = "publisher_ftp_connecting";
	String FTP_LOGIN = "publisher_ftp_login";
	String FTP_CONNECTED = "publisher_ftp_connected";
	String FTP_LOGOUT = "publisher_ftp_logout";
	String FTP_DISCONNECTED = "publisher_ftp_disconnected";
	String FTP_UPLOAD_FILE = "publisher_ftp_upload_file";

	String FTP_ERROR_CONNECTION = "publisher_ftp_error_connection";
	String FTP_ERROR_LOGIN = "publisher_ftp_error_login";
	String FTP_ERROR_DELETE_FILE = "publisher_ftp_error_delete_file";
	String FTP_ERROR_DELETE_DIRECTORY = "publisher_ftp_error_delete_directory";
	String FTP_ERROR_CHANGE_DIRECTORY = "publisher_ftp_error_change_directory";
	String FTP_ERROR_CREATE_DIRECTORY = "publisher_ftp_error_create_directory";
	String FTP_ERROR_CREATE_FILE = "publisher_ftp_error_create_file";
	
}
