package com.esferalia.aon.ui.payroll.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.SingleCollectionProvider;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.company.Enterprise;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.registry.controller.IRegistryConstants;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MessageController;
import com.esferalia.aon.payroll.Salary;

public class SalaryController extends BasicController implements IPayrollConstants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryController.class);
	
	private static final String SALARY_PATTERN = "Nomina {0} ({1,date,dd.MM.yyyy}-{2,date,dd.MM.yyyy})";

	public String getFileName( Salary salary ) {
		String name = salary.getContract().getPerson().getFullName();
		return MessageFormat.format(SALARY_PATTERN, name, salary.getStartDate(), salary.getEndDate());
	}

	private void setRecipients( MessageController messageController, Enterprise enterprise ) throws ManagerBeanException {
		String[] emails = CompanyEmailUtil.getEmails(enterprise.getRegistry());			
		CompanyEmailUtil.initMessageController(messageController, emails);
	}
	
	public void writeReport( Salary salary, OutputStream out ) throws ReportException {
		ReportManager reportManager = new ReportManager();
		reportManager.setOutputFormat(OutputFormat.PDF);
		reportManager.setCollectionProvider( new SingleCollectionProvider(salary) );
		reportManager.execute( out, CURRENT_SALARY_REPORT );
	}	
	
	public AonFile getSalaryFile( Salary salary ) throws IOException, ReportException {
		String fileName = getFileName(salary);
		File file = File.createTempFile( fileName, "." + MimeType.MIME_PDF.getExtension() );
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		writeReport(salary, out);
		IOUtils.closeQuietly(out);
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);	
		aonFile.setFileName( fileName  + "." + MimeType.MIME_PDF.getExtension() );
		return aonFile;
	}	
		
	private String getEmailSubject( Enterprise enterprise ) {
		String message = AonUtil.getMessage(BUNDLE_NAME, SALARY_EMAIL_SUBJECT);
		return MessageFormat.format(message, enterprise.getRegistry().getFullName() );
	}
	
	private RegistryMedia getRegistryMedia( Enterprise enterprise, MediaType type ) {
		RegistryMedia media = null;
		Criteria criteria = new Criteria();
		try {
			IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
			String registryId = bean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID);
			criteria.addEqualExpression(registryId, enterprise.getRegistry().getId());
			String typeAlias = bean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_MEDIA_TYPE);
			criteria.addEqualExpression(typeAlias, type);
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				media = (RegistryMedia) list.get(0);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return media;
	}
	
	private String getEmailContent( Enterprise enterprise, Collection<Salary> salaries ) {
		StringBuffer body = new StringBuffer();
		body.append( "<html><head>" );
		body.append( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" );
		body.append( "</head><body>" );
		
		body.append(AonUtil.getMessage(BUNDLE_NAME, SALARY_EMAIL_BODY_HEADER) );
		for( Salary salary : salaries ) {
			String message = AonUtil.getMessage(BUNDLE_NAME, SALARY_EMAIL_BODY_LINE);
			String line = MessageFormat.format(message, salary.getContract().getPerson().getFullName(), salary.getIssueDate() );
			body.append( line );
		}
		body.append(AonUtil.getMessage(BUNDLE_NAME, SALARY_EMAIL_BODY_FOOTER) );		

		body.append( enterprise.getRegistry().getFullName() ).append( "<br/>" );
		RegistryMedia phone = getRegistryMedia(enterprise, MediaType.FIXED_PHONE);
		if ( phone != null ) {
			String phoneLabel = AonUtil.getMessage(IRegistryConstants.BUNDLE_NAME, IRegistryConstants.REGISTRY_PHONE);
			body.append( StringEscapeUtils.escapeHtml(phoneLabel));
			body.append( ": " ).append( phone.getValue()).append( "<br/>" );			
		}
		RegistryMedia fax = getRegistryMedia(enterprise, MediaType.FAX);
		if ( fax != null ) {
			String faxLabel = AonUtil.getMessage(IRegistryConstants.BUNDLE_NAME, IRegistryConstants.REGISTRY_FAX);
			body.append(faxLabel).append( ": " ).append( fax.getValue() ).append( "<br/>" );
		}
		RegistryMedia web = getRegistryMedia(enterprise, MediaType.WEB);
		if ( web != null ) {
			body.append( "<a href=\"" ).append( web.getValue() ).append( "\">").append( web.getValue() ).append("</a>" );
		}
		return body.toString();
	}	
	
	public MessageController initMail( Enterprise enterprise, Collection<Salary> salaries ) throws ManagerBeanException {
		MessageController messageController = (MessageController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MESSAGE);
		messageController.initNewMessage();
		messageController.setSubject( getEmailSubject(enterprise) );
		messageController.setContent( getEmailContent(enterprise, salaries) );
		setRecipients(messageController, enterprise);
		return messageController;
	}
	
	public void onSendByEmail( ActionEvent event ) {
		Salary salary = (Salary) getTo();
		Enterprise enterprise = salary.getContract().getWorkPlace().getEnterprise();
		try {
			Collection<Salary> salaries = new LinkedList<Salary>();
			salaries.add(salary);
			MessageController messageController = initMail( enterprise, salaries );
			messageController.addAttachment( getSalaryFile(salary) );
			messageController.setShowNewMessageWindow(true);
		} catch (Throwable e) {
			LOGGER.error(">>>> onSendByEmail ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

}
