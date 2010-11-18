package com.code.aon.ui.employee.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.SingleCollectionProvider;
import com.code.aon.company.Enterprise;
import com.code.aon.employee.Salary;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.AonFile;

public class SalaryController extends BasicController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryController.class);
	
	private static final String SALARY_PATTERN = "Nomina {0} ({1,date,dd.MM.yyyy}-{2,date,dd.MM.yyyy})";

	private String getSubject( Salary salary ) {
		String name = salary.getContract().getPerson().getFullName();
		return MessageFormat.format(SALARY_PATTERN, name, salary.getStartDate(), salary.getEndDate());
	}

	private void setRecipients( MessageController messageController, Salary salary ) throws ManagerBeanException {
		Enterprise enterprise = salary.getContract().getWorkPlace().getEnterprise();
		String[] emails = CompanyEmailUtil.getEmails(enterprise.getRegistry());			
		CompanyEmailUtil.initMessageController(messageController, emails);
	}
	
	private void writeReport( Salary salary, OutputStream out ) throws ReportException {
		ReportManager reportManager = new ReportManager();
		reportManager.setOutputFormat(OutputFormat.PDF);
		reportManager.setCollectionProvider( new SingleCollectionProvider(salary) );
		reportManager.execute( out, IEmployeeConstants.CURRENT_SALARY_REPORT );
	}	
	
	public AonFile getSalaryFile( Salary salary ) throws IOException, ReportException {
		String fileName = getSubject(salary);
		File file = File.createTempFile( fileName, ".pdf" );
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		writeReport(salary, out);
		IOUtils.closeQuietly(out);
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);	
		aonFile.setFileName( fileName + ".pdf" );
		return aonFile;
	}	
	
	public void onSendByEmail( ActionEvent event ) {
		Salary salary = (Salary) getTo();
		MessageController messageController = (MessageController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MESSAGE);
		messageController.initNewMessage();
		messageController.setSubject( getSubject(salary) );
		try {
			setRecipients(messageController, salary);
			messageController.addAttachment( getSalaryFile(salary) );
		} catch (Throwable e) {
			LOGGER.error(">>>> onSendByEmail ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		messageController.setShowNewMessageWindow(true);
	}

}
