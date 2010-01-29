package com.code.aon.webmail.bean;

public interface IMimeType {

	String IMAGE_ANY = "image/*";
	
	String APPLICATION_ANY = "application/*";
	
	String APPLICATION_APPLEFILE = "application/applefile";

	String MULTIPART_ANY = "multipart/*";
	
	String RELATED = "related";
	
	String MULTIPART_RELATED = "multipart/" + RELATED;

	String MULTIPART_MIXED = "multipart/mixed";

	String MULTIPART_ALTERNATIVE = "multipart/alternative";

	String TEXT_ANY = "text/*";
	
	String TEXT_PLAIN = "text/plain";

	String TEXT_HTML = "text/html";
	
	String MESSAGE_ANY = "message/*";
	
	String MESSAGE_RFC822 = "message/rfc822";
	
}
