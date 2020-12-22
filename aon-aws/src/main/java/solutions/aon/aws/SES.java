package solutions.aon.aws;
/*
 * Copyright 2014-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;

public class SES extends AWS{
	
	private static String CONFIGURATION_SET = "ConfigSet";

    public static String sendEmail(String from, String to, String subject, String body) {
        Destination destination = new Destination().withToAddresses(new String[]{to});

        Content subject2 = new Content().withData(subject);
        Content textBody = new Content().withData(body);
        Body body2 = new Body().withHtml(textBody);

        Message message = new Message().withSubject(subject2).withBody(body2);

        SendEmailRequest request = new SendEmailRequest().withSource(from).withDestination(destination).withMessage(message);
        
        try {
            AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
                .withCredentials(getProvider())
                .withRegion("eu-west-1")
                .build();

            client.sendEmail(request);
            System.out.println("Email sent!");
            return "ok";
        } catch (Exception ex) {
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
            return "Error message: " + ex.getMessage();
        }
    }
    
    public static void sendEmailWithAttachment(String from, String to, String subject, String body, LinkedList<File> files) throws AddressException, MessagingException, IOException{	
    	Session session = Session.getDefaultInstance(new Properties());
        
        // Create a new MimeMessage object.
        MimeMessage message = new MimeMessage(session);
        
        // Add subject, from and to lines.
        message.setSubject(subject, "UTF-8");
        message.setFrom(new InternetAddress(from));
        message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(to));

        // Create a multipart/alternative child container.
        MimeMultipart msg_body = new MimeMultipart("alternative");
        
        // Create a wrapper for the HTML and text parts.        
        MimeBodyPart wrap = new MimeBodyPart();
        
        // Define the text part.
//        MimeBodyPart textPart = new MimeBodyPart();
//        textPart.setContent(BODY_TEXT, "text/plain; charset=UTF-8");
                
        // Define the HTML part.
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(body,"text/html; charset=UTF-8");
                
        // Add the text and HTML parts to the child container.
//        msg_body.addBodyPart(textPart);
        msg_body.addBodyPart(htmlPart);
        
        // Add the child container to the wrapper object.
        wrap.setContent(msg_body);
        
        // Create a multipart/mixed parent container.
        MimeMultipart msg = new MimeMultipart("mixed");
        
        // Add the parent container to the message.
        message.setContent(msg);
        
        // Add the multipart/alternative part to the message.
        msg.addBodyPart(wrap);
        
        for (File file : files) {
        	// Define the attachment
        	MimeBodyPart att = new MimeBodyPart(); 
        	DataSource bds = new FileDataSource(file);
        	att.setDataHandler(new DataHandler(bds)); 
        	att.setFileName(bds.getName());             
            // Add the attachment to the message.
            msg.addBodyPart(att);
		}

        // Try to send the email.
        try {
            System.out.println("Attempting to send an email through Amazon SES "
                              +"using the AWS SDK for Java...");

            // Instantiate an Amazon SES client, which will make the service 
            // call with the supplied AWS credentials.
            AmazonSimpleEmailService client = 
                    AmazonSimpleEmailServiceClientBuilder.standard()
                    // Replace US_WEST_2 with the AWS Region you're using for
                    // Amazon SES.
                    .withRegion(Regions.EU_WEST_1).build();
            
            // Print the raw email content on the console
            PrintStream out = System.out;
            message.writeTo(out);

            // Send the email.
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);
            RawMessage rawMessage = 
            		new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

            SendRawEmailRequest rawEmailRequest = 
            		new SendRawEmailRequest(rawMessage)
            		    .withConfigurationSetName(CONFIGURATION_SET);
            
            client.sendRawEmail(rawEmailRequest);
            System.out.println("Email sent!");
        // Display an error if something goes wrong.
        } catch (Exception ex) {
          System.out.println("Email Failed");
            System.err.println("Error message: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
