package com.code.aon.webmail;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeBodyPart;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.util.AonFile;

public class WebmailUtil {

    public static BodyPart getBodyPart( AonFile af ) throws MessagingException {
    	MimeBodyPart bodyPart = new MimeBodyPart();
    	FileDataSource fds = new AonFileDataSource(af);
		String name = FilenameUtils.getName(af.getFileName());
    	bodyPart.setFileName( name );
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

