package com.code.aon.webmail;

import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.util.AonFile;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeUtility;

public class WebmailUtil {

    public static BodyPart getBodyPart( AonFile af ) throws MessagingException{
    	MimeBodyPart bodyPart = new MimeBodyPart();
    	FileDataSource fds = new AonFileDataSource(af);
		String name = FilenameUtils.getName(af.getFileName());
    	try {
			bodyPart.setFileName( MimeUtility.encodeText(name, "UTF-8", null) );
		} catch (UnsupportedEncodingException e) {
			bodyPart.setFileName( name );
		}
    	bodyPart.setDataHandler(new DataHandler(fds));
    	return bodyPart;	
    }

    public static String getContentId( Part part ) throws MessagingException {
    	String[] contentId = part.getHeader("Content-ID");
    	if (! ArrayUtils.isEmpty(contentId) ) {
    		return contentId[0];
    	}
    	return null;
    }
    
}

