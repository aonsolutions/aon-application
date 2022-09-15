package com.esferalia.aon.in.payroll.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import com.esferalia.aon.in.payroll.pdf.SalaryPDFParser;
import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.watson.util.AonStringUtils;

public class MimeEmail {

	public static void main(String[] args) {
		for (String arg : args) {
			try ( InputStream is = new  FileInputStream(arg)) {
				Session session = Session.getInstance(System.getProperties());
				MimeMessage mimeMessage = new MimeMessage(session, is);
				Multipart multipart = (Multipart ) mimeMessage.getContent() ;
				
				for ( int i = 0; i < multipart.getCount() ; i++ ) {
					parse(multipart.getBodyPart(i));
				}
				
				
			} catch ( Exception  e ) {
				System.err.printf("ERROR [%s]: %s \r\n", arg, e.getMessage());
			}
		}
	}
	
	private static void extract( BodyPart bodyPart ) throws IOException, MessagingException{
		if ( AonStringUtils.isBlank(bodyPart.getFileName() ))
			return;
		
		try (InputStream is = bodyPart.getInputStream() ;
			FileOutputStream os = new FileOutputStream(bodyPart.getFileName())) {
			byte read [] = new byte [1024];
			while ( is.read(read, 0, 1024) != -1  ) 
				os.write(read);
			System.out.printf("INFO : %s , %s %d \r\n" , bodyPart.getFileName(), bodyPart.getContentType(), bodyPart.getSize() );
		}
	}

	private static void parse( BodyPart bodyPart ) throws IOException, MessagingException, UnknownPDFException{
		if ( !isMimeType(bodyPart.getContentType(), "application/pdf" ) )
			return;
		SalaryBuilder salaryBuilder = new SalaryBuilder();
		SalaryPDFParser.parse(bodyPart.getInputStream(), salaryBuilder);
		Salary salary = salaryBuilder.getSalary();
		
		System.out.printf("INFO : %s , %s %s[%s,%s]-%s  \r\n" , 
				bodyPart.getFileName(), 
				salary.getStartDate(), 
				salary.getEnterpriseName() , 
				salary.getEnterpriseDocument() , 
				salary.getCcc() , 
				salary.getEmployeeName() );
	}

	private static boolean isMimeType(String contentType, String mimeType) {
		return AonStringUtils.containsIgnoreCase(contentType, mimeType);
	}
	
}
