package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_SDD_MANDATE;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_SDD_MANDATE_EMAIL_BODY;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_SDD_MANDATE_EMAIL_SENDED_TO;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_SDD_MANDATE_LIST_NAME;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_SDD_MANDATE_SEND_EMAIL_FINISH;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_SDD_MANDATE_SEND_EMAIL_KO;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_SDD_MANDATE_SEND_EMAIL_OK;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_SDD_MANDATE_SEND_EMAIL_PROCESS_INIT;
import static com.code.aon.ui.common.ICommonMessages.NOT_MAIL_ACCOUNT;
import static com.code.aon.ui.common.ICommonMessages.NOT_MAIL_ACCOUNTS;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import jakarta.mail.Address;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.customer.Customer;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.customer.controller.CustomerListController;
import com.code.aon.ui.customer.controller.ICustomerConstants;
import com.code.aon.ui.finance.SddMandateObject;
import com.code.aon.ui.finance.util.FinanceEmailUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.bean.AonMessage;

public class SddMandateController extends CustomerListController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SddMandateController.class);
	
	private Map<Integer, Boolean> recurrentPayments;
	private Map<Integer, Boolean> oneOffPayments;
	private Map<Integer, String> references;
	
	private FinanceEmailUtil emailUtil; 
	private Date signDate; 
	private String sameReferenceValue; 
	private Boolean recurrentPayment; 
	
	public Map<Integer, Boolean> getRecurrentPayments() {
		return recurrentPayments;
	}

	public void setRecurrentPayments(Map<Integer, Boolean> recurrentPayments) {
		this.recurrentPayments = recurrentPayments;
	}

	public Map<Integer, Boolean> getOneOffPayments() {
		return oneOffPayments;
	}

	public void setOneOffPayments(Map<Integer, Boolean> oneOffPayments) {
		this.oneOffPayments = oneOffPayments;
	}
	
	public String getSameReferenceValue() {
		return sameReferenceValue;
	}

	public void setSameReferenceValue(String sameReferenceValue) {
		this.sameReferenceValue = sameReferenceValue;
	}

	public Boolean getRecurrentPayment() {
		return recurrentPayment;
	}

	public void setRecurrentPayment(Boolean recurrentPayment) {
		this.recurrentPayment = recurrentPayment;
	}

	public FinanceEmailUtil getEmailUtil() {
		if(emailUtil==null){
			emailUtil = new FinanceEmailUtil();
		}
		return emailUtil;
	}

	public void setEmailUtil(FinanceEmailUtil emailUtil) {
		this.emailUtil = emailUtil;
	}

	public Date getSignDate() {
		return signDate;
	}

	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}

	public Boolean getCustomerEmailAvailable() throws ManagerBeanException{
		Customer customer = (Customer) model.getRowData();
		return StringUtils.isNotBlank(customer.getRegistry().getEmails());
	}
	
	public String getReferenceChanged() {
		if(model.isRowAvailable()){
			Customer to = (Customer) model.getRowData();
			return references.get(to.getId());
		}
		return null;
	}
	public void setReferenceChanged(String reference) {
		Customer to = (Customer) model.getRowData();
		if (StringUtils.isNotBlank(reference)) {
			references.put(to.getId(), reference);
		} else {
			references.remove(to.getId());
		}
	}
	public boolean getRecurrentPaymentChecked() {
		Customer to = (Customer) model.getRowData();
		return recurrentPayments.containsKey(to.getId());
	}
	public void setRecurrentPaymentChecked(boolean rowChecked) {
		if (rowChecked) {
			selectPaymentType(recurrentPayments, oneOffPayments);
		} else {
			removeMapValue(recurrentPayments);
		}
	}
	public boolean getOneOffPaymentChecked() {
		Customer to = (Customer) model.getRowData();
		return oneOffPayments.containsKey(to.getId());
	}
	public void setOneOffPaymentChecked(boolean rowChecked) {
		if (rowChecked) {
			selectPaymentType(oneOffPayments, recurrentPayments);
		} else {
			removeMapValue(oneOffPayments);
		}
	}

	private void selectPaymentType(Map<Integer, Boolean> mapToAdd, Map<Integer, Boolean> mapToRemove){
		Customer customer;
		try {
			customer = (Customer) getModel().getRowData();
			selectPaymentType(customer, mapToAdd, mapToRemove);
		} catch (ManagerBeanException e) {
			LOGGER.error("No se ha podido seleccionar el tipo de pago");
		}
	}

	private void selectPaymentType(Customer customer, Map<Integer, Boolean> mapToAdd, Map<Integer, Boolean> mapToRemove){
		mapToAdd.put(customer.getId(), true);
		if(mapToRemove.containsKey(customer.getId())){
			mapToRemove.remove(customer.getId());
		}
	}
	
	private void removeMapValue(Map<Integer, ?> map){
		Customer customer;
		try {
			customer = (Customer) getModel().getRowData();
			if(map.containsKey(customer.getId())){
				map.remove(customer.getId());
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("No se ha podido seleccionar el tipo de pago");
		}
	}
	
	@Override
	public boolean getRowChecked() {
		if(model.isRowAvailable()){
			return super.getRowChecked();
		}
		return false;
	}
	
	@Override
	public void setRowChecked(boolean rowChecked) {
		if (!rowChecked) {
			removeMapValue(oneOffPayments);
			removeMapValue(recurrentPayments);
			removeMapValue(references);
		}
		super.setRowChecked(rowChecked);
	}
	
	@Override
	public void checkNone(ActionEvent event) {
		recurrentPayments = new HashMap<Integer, Boolean>();
		oneOffPayments = new HashMap<Integer, Boolean>();
		references = new HashMap<Integer, String>();
		super.checkNone(event);
	}
	
	public void onInit(ActionEvent event){
		super.clearCheckedCustomers();
		checkNone(event);
		setSignDate(new Date());
		setSameReferenceValue(null);
		setRecurrentPayment(null);
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		onInit(event);
		super.onSearch(event);
	}
	
	public void onLoadCustomer(ActionEvent event) throws ManagerBeanException {
		BasicController sourceController = (BasicController)AonUtil.getRegisteredBean(ICustomerConstants.CUSTOMER_CONTROLLER_NAME);
		Customer customer = (Customer) this.getModel().getRowData();
		sourceController.onLoad(event, customer.getId(), FINANCE_SDD_MANDATE_LIST_NAME, null);	
	}
	
	public void checkAllRecurrentPayments(ActionEvent event) throws ManagerBeanException {
		for (ITransferObject ito : this.getCheckedCustomers()) {
			Customer customer = (Customer)ito;
			selectPaymentType(customer, recurrentPayments, oneOffPayments);
		}
	}
	
	public void checkAllOneOffPayments(ActionEvent event) throws ManagerBeanException {
		for (ITransferObject ito : this.getCheckedCustomers()) {
			Customer customer = (Customer)ito;
			selectPaymentType(customer, oneOffPayments, recurrentPayments);
		}
	}
	
	public void completeAllReferences(ActionEvent event) {
		for (ITransferObject ito : this.getCheckedCustomers()) {
			Customer customer = (Customer)ito;
			references.put(customer.getId(), sameReferenceValue);
		}
	}

	public void completeAllValues(ActionEvent event)  throws ManagerBeanException {
		completeAllReferences(event);
		if(getRecurrentPayment()!=null){
			if(getRecurrentPayment()){
				checkAllRecurrentPayments(event);
			} else {
				checkAllOneOffPayments(event);
			}
		}
	}
	
	///////////////////////////////////////
	// SENDMAIL METHODS
	///////////////////////////////////////
	public void onInitSendEmail(ActionEvent event){
		MessageController messageController = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		messageController.onPrepareEmailWindow(event);
		if ( messageController.isShowNewMessageWindow() ) {			
			messageController.setAppendSignature(false);
			messageController.setSaveSent(false);
			try {		
				messageController.onNewMessage(event);
				messageController.initNewMessage();
				messageController.setSubject( AonUtil.getMessage(FINANCE_SDD_MANDATE) );
				messageController.updateMessageBody(getEmailUtil().getEmailContent(AonUtil.getMessage(FINANCE_SDD_MANDATE_EMAIL_BODY)));
			} catch (Throwable th) {
				LOGGER.error(th.getMessage(), th);
				AonUtil.addErrorMessage(th.getMessage());
				throw new AbortProcessingException(th.getMessage(), th);
			}
		}
	}
	
	public void onSendByEmail(ActionEvent event) throws ManagerBeanException{
		Customer customer = (Customer) this.getModel().getRowData();
		if(customer != null){
			try {
				MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
				if (mailConfig.getMailAccountCount() > 0) {
					MessageController messageController = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
					messageController.initNewMessage();
					messageController.setSubject( AonUtil.getMessage(FINANCE_SDD_MANDATE) );
					messageController.updateMessageBody(getEmailUtil().getEmailContent(AonUtil.getMessage(FINANCE_SDD_MANDATE_EMAIL_BODY)));
					CompanyEmailUtil.initMessageController(messageController, obtainEmails(customer));
					messageController.addAttachment(getEmailUtil().getSddMandateReport(createCustomerSddMandate(customer)));
					messageController.setShowNewMessageWindow(true);
				} else {
					AonUtil.addErrorMessageFromBundle(NOT_MAIL_ACCOUNTS);
				}
			} catch (Throwable e) {
				LOGGER.error(">>>> onSendByEmail ",e);
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);
			}
		}
	}
		
	public void sendAllByEmail(ActionEvent event){
		CompanyEmailUtil emailUtil = new CompanyEmailUtil();
		emailUtil.setNumberOfMessagesPerTransport(10);
		LogPanelController logger = LogPanelController.getInstance();
		MessageController messageController = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		messageController.setShowNewMessageWindow(false);
		try {
			logger.info( AonUtil.getMessage(FINANCE_SDD_MANDATE_SEND_EMAIL_PROCESS_INIT, this.getCheckedCustomers().size()) );
			int unknownError=0;
			for( Customer customer : this.getCheckedCustomers() ) {
				if ( logger.isActivePoll() ) {
						AonFile file = null;
						try {
							String[] emails = obtainEmails(customer);
							if ( ArrayUtils.isEmpty(emails) ) {
								String text = AonUtil.getMessage(NOT_MAIL_ACCOUNT);
								String message = MessageFormat.format(text, customer.getRegistry().getFullName());				
								logger.error( message );
							} else {
								Address[] recipients = getEmailAddresses(emails, customer.getRegistry().getFullName() );
								String subject = messageController.getSubject();
								String content = messageController.getContent();
								
								file = getEmailUtil().getSddMandateReport(createCustomerSddMandate(customer));
								
								AonMessage aonMessage = emailUtil.getEmailSender().createMessage(recipients, subject);
								emailUtil.getEmailSender().addMessageContent(aonMessage, content, MimeType.MIME_HTML, file);
								emailUtil.getEmailSender().sendMessage(aonMessage);
								
								String text = AonUtil.getMessage(FINANCE_SDD_MANDATE_EMAIL_SENDED_TO);
								String message = MessageFormat.format(text, customer.getRegistry().getFullName(), ArrayUtils.toString(emails) );
								logger.info( message );
							}
						} catch (Throwable th) {
							LOGGER.error(th.getMessage(), th);
							String text = "ERROR. No se ha podido enviar el mensaje a " + customer.getRegistry().getFullName() ;
							text += "<br />" + "MOTIVO -> " + th.getMessage() + "<br />" + th.getCause();
							logger.error( text );
							unknownError++;
						} finally {
							if ( file != null ) {
								file.clean();
							}
						}
				} else {
					break;
				}
			}
			String text_finish_ok = AonUtil.getMessage(FINANCE_SDD_MANDATE_SEND_EMAIL_OK);
			String message_finish_ok = MessageFormat.format(text_finish_ok, (this.getCheckedCustomers().size()-(logger.getErrors().size()-unknownError)) );
			logger.info( message_finish_ok );
			String text_finish_ko = AonUtil.getMessage(FINANCE_SDD_MANDATE_SEND_EMAIL_KO);
			String message_finish_ko = MessageFormat.format(text_finish_ko, (logger.getErrors().size()-unknownError));
			logger.info( message_finish_ko );
		} finally {
			emailUtil.close();
			logger.info( AonUtil.getMessage(FINANCE_SDD_MANDATE_SEND_EMAIL_FINISH) );
			logger.finish();
		}
	}
	private Address[] getEmailAddresses( String[] emails, String name ) throws UnsupportedEncodingException, AddressException {
		Address[] addresses = new Address[emails.length];
		for( int i = 0; i < emails.length; i++ ) {
			if ( i == 0 ) {
				addresses[i] = new InternetAddress( emails[i], name );
			} else {
				addresses[i] = new InternetAddress( emails[i] );	
			}
		}
		return addresses;
	}
	private String[] obtainEmails(Customer customer) throws ManagerBeanException {
		String emailList = customer.getRegistry().getEmails();
		String[] emails = null;
		if(StringUtils.isNotBlank(emailList)){
			emails = emailList.split(",");
		}
		return emails;
	}
	
	////////////////////////////////////////////////////////////
	// REPORT
	////////////////////////////////////////////////////////////
	private SddMandateObject createCustomerSddMandate(Customer customer){
		SddMandateObject sddMandate = new SddMandateObject();
		sddMandate.setRegistry(customer.getRegistry());
		sddMandate.setRecurrentPayment(recurrentPayments.containsKey(customer.getId()) && recurrentPayments.get(customer.getId()));
		sddMandate.setOneOffPayment(oneOffPayments.containsKey(customer.getId()) && oneOffPayments.get(customer.getId()));
		sddMandate.setReference(references.get(customer.getId()));
		sddMandate.setSignDate(this.getSignDate());
		return sddMandate;
	}

	@Override
	public Collection<ITransferObject> getCollection() {
		try {
			if(this.getModel().isRowAvailable()){
				List<ITransferObject> list = new LinkedList<ITransferObject>();
				list.add(createCustomerSddMandate((Customer) this.getModel().getRowData()));
				return list;
			} else {
				List<ITransferObject> list = new LinkedList<ITransferObject>();
				for(Customer customer: this.getCheckedCustomers()){
					list.add(createCustomerSddMandate(customer));
				}
				return list;
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> getCollection ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
}
