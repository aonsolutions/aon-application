package com.esferalia.aon.in.payroll.aws.lambda;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class SESInspector {

    private static Optional<MimeMessage> handleMessage(String bucket, String key, Consumer<BodyPart>... handlers) {
	MimeMessage mimeMessage = null;
	AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();
	S3Object s3Object = s3.getObject(new GetObjectRequest(bucket, key));
	try (InputStream is = s3Object.getObjectContent()) {
	    mimeMessage = handleMIME(is, handlers);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return Optional.ofNullable(mimeMessage);
    }

    private static MimeMessage handleMIME(InputStream is, Consumer<BodyPart>... handlers) throws Exception {
	Session session = Session.getInstance(System.getProperties());
	MimeMessage mimeMessage = new MimeMessage(session, is);
	Multipart multipart = (Multipart) mimeMessage.getContent();
	for (int i = 0; i < multipart.getCount(); i++) {
	    BodyPart bodyPart = multipart.getBodyPart(i);
	    try {
		for (Consumer<BodyPart> handler : handlers) {
		    handler.accept(bodyPart);
		}
	    } catch (Exception e) {
	    }
	}
	return mimeMessage;
    }

    private static Consumer<BodyPart> extract(String dir) {
	return (BodyPart bodyPart) -> {
	    try {
		File file = new File(dir, bodyPart.getFileName());
		FileOutputStream os = new FileOutputStream(file);
		InputStream is = bodyPart.getInputStream();
		byte buffer [] = new byte [1024];
		while ( is.read(buffer) >= 0 ) 
		    os.write(buffer);
		is.close();
		os.flush();
		os.close();
		System.out.println("Extract : '" + file.getAbsolutePath() + "', " + bodyPart.getContentType());
	    } catch (MessagingException | IOException e) {

	    }
	};
    }

    public static void main(String[] args) throws Exception {

	String bucket = "aon-ses-inbox";

	for (String messageId : args) {
	    System.out.println("messageId: " + messageId);
	    String key = String.format("laboral@aon.solutions/%s", messageId);
	    handleMessage(bucket, key, extract("/home/rtrepiana/Downloads"));
	}

	System.out.println("That's all Folks!");
    }
}
