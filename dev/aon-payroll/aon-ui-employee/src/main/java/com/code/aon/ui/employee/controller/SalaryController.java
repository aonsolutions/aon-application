package com.code.aon.ui.employee.controller;

import java.text.MessageFormat;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.employee.Salary;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MessageController;

public class SalaryController extends BasicController {
	
	private static final String SUBJECT_PATTERN = "Nomina {0} ({1,date,short}-{2,date,short})";

	private String getSubject( Salary salary ) {
		String name = salary.getContract().getPerson().getFullName();
		return MessageFormat.format(SUBJECT_PATTERN, name, salary.getStartDate(), salary.getEndDate());
	}

	private void setRecipients( MessageController messageController, Salary salary ) throws ManagerBeanException {
		Enterprise enterprise = salary.getContract().getWorkPlace().getEnterprise();
		String[] emails = CompanyEmailUtil.getEmails(enterprise.getRegistry());			
		CompanyEmailUtil.initMessageController(messageController, emails);
	}
	
	public void onSendByEmail( ActionEvent event ) throws ManagerBeanException {
		Salary salary = (Salary) getTo();
		MessageController messageController = (MessageController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MESSAGE);
		messageController.initNewMessage();
		messageController.setSubject( getSubject(salary) );
		setRecipients(messageController, salary);
		messageController.setShowNewMessageWindow(true);
	}

}
