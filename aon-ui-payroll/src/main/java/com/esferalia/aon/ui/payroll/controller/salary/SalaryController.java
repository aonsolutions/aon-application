package com.esferalia.aon.ui.payroll.controller.salary;

import static com.code.aon.ui.common.ICommonMessages.PAYROLL_BUNDLE;
import static com.code.aon.ui.common.ICommonMessages.PHONE;
import static com.code.aon.ui.common.ICommonMessages.SALARY_EMAIL_BODY_FOOTER;
import static com.code.aon.ui.common.ICommonMessages.SALARY_EMAIL_BODY_HEADER;
import static com.code.aon.ui.common.ICommonMessages.SALARY_EMAIL_BODY_LINE;
import static com.code.aon.ui.common.ICommonMessages.SALARY_EMAIL_SUBJECT;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.SingleCollectionProvider;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.company.Enterprise;
import com.code.aon.company.EnterpriseData;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MessageController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBonus;
import com.esferalia.aon.payroll.SalaryCost;
import com.esferalia.aon.payroll.SalaryDeduction;
import com.esferalia.aon.payroll.SalaryEmbargo;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;



public class SalaryController extends BasicController implements IPayrollConstants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryController.class);
	
	private static final String SALARY_PATTERN = "Nomina {0} ({1,date,dd.MM.yyyy}-{2,date,dd.MM.yyyy})";
	
	private static class CtxSortedSalaryItems<T extends Enum<T> & IResourceable> {
		FacesContext			facesContext;
		SortedSalaryItems<T> 	sortedSalaryItems;
		
		public boolean isValid(){
			return facesContext == FacesContext.getCurrentInstance();
		}
		
		public void setSortedSalaryItems(SortedSalaryItems<T> sortedSalaryItems){
			this.sortedSalaryItems = sortedSalaryItems; 
			this.facesContext = FacesContext.getCurrentInstance();
		}
		
		public SortedSalaryItems<T> getSortedSalaryItems() {
			return isValid() ? sortedSalaryItems: null;
		}
	}
	
	private CtxSortedSalaryItems<BonusType> 		cachedSalaryBonuses
	 	= new CtxSortedSalaryItems<BonusType>();
	private CtxSortedSalaryItems<PaymentType> 		cachedSalaryPayments
		= new CtxSortedSalaryItems<PaymentType>();
	private CtxSortedSalaryItems<DeductionType> 	cachedSalaryDeductions
		= new CtxSortedSalaryItems<DeductionType>();
	private CtxSortedSalaryItems<DeductionType> 	cachedSalaryCosts
		= new CtxSortedSalaryItems<DeductionType>();
	
	
	public String getFileName( Salary salary ) {
		String name = salary.getContract().getPerson().getFullName();
		return MessageFormat.format(SALARY_PATTERN, name, salary.getStartDate(), salary.getEndDate());
	}
	
	public SortedSalaryItems<BonusType> getSortedSalaryBonuses() 
		throws ManagerBeanException, SalaryException{
		
		if ( cachedSalaryBonuses.isValid() ) {
			return cachedSalaryBonuses.getSortedSalaryItems();
		}

		SortedSalaryItems<BonusType> sortedSalaryBonuses =
			new SortedSalaryItems<BonusType>();
		
		Collection<SalaryBonus> salaryBonuses  = 
			((Salary) getTo() ).getBonus();
		Collection<SalaryBonus> oldSalaryBonuses= 
			Collections.emptyList();
		sortedSalaryBonuses.setItems(salaryBonuses, oldSalaryBonuses);
		
		cachedSalaryBonuses.setSortedSalaryItems(sortedSalaryBonuses);
		
		return sortedSalaryBonuses;
	}

	public SortedSalaryItems<PaymentType> getSortedSalaryPayments() 
		throws ManagerBeanException{
		
		if ( cachedSalaryPayments.isValid() ){
			return cachedSalaryPayments.getSortedSalaryItems();
		}
		SortedSalaryItems<PaymentType> sortedSalaryPayments =
			new SortedSalaryItems<PaymentType>(PaymentType.values());
		
		Collection<SalaryPayment> salaryPayments  = 
			getSalaryPayments();
		Collection<SalaryPayment> oldSalaryPayments = 
			Collections.emptyList();
		sortedSalaryPayments.setItems(salaryPayments, oldSalaryPayments);
		
		cachedSalaryPayments.setSortedSalaryItems(sortedSalaryPayments);
		
		return sortedSalaryPayments;
	}
	
	public SortedSalaryItems<DeductionType> getSortedSalaryDeductions() 
	throws ManagerBeanException{
	
		if ( cachedSalaryDeductions.isValid() ){
			return cachedSalaryDeductions.getSortedSalaryItems();
		}
		
		SortedSalaryItems<DeductionType> sortedSalaryDeductions=
			new SortedSalaryItems<DeductionType>(DeductionType.values());
		
		Collection<SalaryDeduction> salaryDeductions =
			getSalaryDeductions();
		
		Collection<SalaryDeduction> oldSalaryDeductions = 
			Collections.emptyList();
		sortedSalaryDeductions.setItems(salaryDeductions, oldSalaryDeductions);

		cachedSalaryDeductions.setSortedSalaryItems(sortedSalaryDeductions);
		
		return sortedSalaryDeductions;
	}

	public SortedSalaryItems<DeductionType> getSortedSalaryCosts() 
	throws ManagerBeanException{
		if ( cachedSalaryCosts.isValid()){
			return cachedSalaryCosts.getSortedSalaryItems();
		}
		
		SortedSalaryItems<DeductionType> sortedSalaryCosts=
			new SortedSalaryItems<DeductionType>(DeductionType.values());
		
		Collection<SalaryCost> salaryCosts =
			getSalaryCosts();
		
		Collection<SalaryCost> oldSalaryCosts = 
			Collections.emptyList();
		sortedSalaryCosts.setItems(salaryCosts, oldSalaryCosts);
		
		cachedSalaryCosts.setSortedSalaryItems(sortedSalaryCosts);
		
		return sortedSalaryCosts;
	}
	
	
	private Collection<SalaryPayment> getSalaryPayments () throws ManagerBeanException {
		Salary salary = (Salary) getTo();
		String sessionName = HibernateUtil.getSessionFactoryName(Salary.class.getName());
		Session session = HibernateUtil.getSession(sessionName);
		// Si el Salary está conectado a la session de Hibernate utilizamos la potencia
		// que nos da la obtención de colecciones tipo LAZY. En caso contrario vamos por 
		// el FrameWork.
		if (  session.contains(salary)  || salary.getId() == null ) {
			return  salary.getSalaryPayments();
		} else {
			IManagerBean bean = BeanManager.getManagerBean(SalaryPayment.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_PAYMENT_SALARY_ID), salary.getId());
			List<?> list = bean.getList(c);
			return (Collection<SalaryPayment>) list;
		}
		
	}

	private Collection<SalaryDeduction> getSalaryDeductions () throws ManagerBeanException {
		Salary salary = (Salary) getTo();
		String sessionName = HibernateUtil.getSessionFactoryName(Salary.class.getName());
		Session session = HibernateUtil.getSession(sessionName);
		// Si el Salary está conectado a la session de Hibernate utilizamos la potencia
		// que nos da la obtención de colecciones tipo LAZY. En caso contrario vamos por 
		// el FrameWork.
		if (  session.contains(salary)  || salary.getId() == null ) {
			return  salary.getSalaryDeductions();
		} else {
			IManagerBean bean = BeanManager.getManagerBean(SalaryDeduction.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_DEDUCTION_SALARY_ID), salary.getId());
			List<SalaryDeduction> list = new LinkedList<SalaryDeduction>();
			for(ITransferObject to: bean.getList(c)){
				SalaryDeduction d = (SalaryDeduction) to;
				list.add( d );
			}
			list.addAll(getSalaryEmbargos());
			return (Collection<SalaryDeduction>) list;
		}
		
	}
	
	private Collection<SalaryDeduction> getSalaryEmbargos () throws ManagerBeanException {
		Salary salary = (Salary) getTo();
		String sessionName = HibernateUtil.getSessionFactoryName(Salary.class.getName());
		Session session = HibernateUtil.getSession(sessionName);
		// Si el Salary está conectado a la session de Hibernate utilizamos la potencia
		// que nos da la obtención de colecciones tipo LAZY. En caso contrario vamos por 
		// el FrameWork.
		List<?> embargosList ;
		if (  session.contains(salary)  || salary.getId() == null ) {
			embargosList = (List<SalaryEmbargo>) salary.getSalaryEmbargos();
		} else {
			IManagerBean bean = BeanManager.getManagerBean(SalaryEmbargo.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_EMBARGO_SALARY_ID), salary.getId());
			embargosList = bean.getList(c);
		}
		List<SalaryDeduction> list = new LinkedList<SalaryDeduction>();
		for(Object o: embargosList){
			SalaryEmbargo e = (SalaryEmbargo) o;
			SalaryDeduction d = new SalaryDeduction();
			d.setDeductionConcept(e.getName());
			d.setDescription(e.getDescription());
			d.setAmount(e.getAmount());
			d.setType(DeductionType.OTHER);
			list.add(d);
		}
		return list;
		
	}

	private Collection<SalaryCost> getSalaryCosts() throws ManagerBeanException {
		Salary salary = (Salary) getTo();
		String sessionName = HibernateUtil.getSessionFactoryName(Salary.class.getName());
		Session session = HibernateUtil.getSession(sessionName);
		// Si el Salary está conectado a la session de Hibernate utilizamos la potencia
		// que nos da la obtención de colecciones tipo LAZY. En caso contrario vamos por 
		// el FrameWork.
		if (  session.contains(salary)  || salary.getId() == null ) {
			return  salary.getSalaryCosts();
		} else {
			IManagerBean bean = BeanManager.getManagerBean(SalaryCost.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_COST_SALARY_ID), salary.getId());
			List<?> list = bean.getList(c);
			return (Collection<SalaryCost>) list;
		}
		
	}

	private void setRecipients( MessageController messageController, Enterprise enterprise ) throws ManagerBeanException {
		String[] emails = CompanyEmailUtil.getAdministrativeEmails(enterprise.getRegistry());			
		CompanyEmailUtil.initMessageController(messageController, emails);
	}
	
	public void writeReport( Salary salary, OutputStream out ) throws ReportException {
		ReportManager reportManager = new ReportManager();
		reportManager.setOutputFormat(OutputFormat.PDF);
		reportManager.setCollectionProvider( new SingleCollectionProvider(salary) );
		reportManager.execute( out, SALARY_REPORT );
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
		String message = AonUtil.getMessage(PAYROLL_BUNDLE, SALARY_EMAIL_SUBJECT);
		return MessageFormat.format(message, enterprise.getRegistry().getFullName() );
	}
	
	private RegistryMedia getRegistryMedia( Enterprise enterprise, MediaType type ) {
		RegistryMedia media = null;
		Criteria criteria = new Criteria();
		try {
			IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
			String registryId = bean.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID);
			criteria.addEqualExpression(registryId, enterprise.getRegistry().getId());
			String typeAlias = bean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE);
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
		
		body.append(AonUtil.getMessage(PAYROLL_BUNDLE, SALARY_EMAIL_BODY_HEADER) );
		for( Salary salary : salaries ) {
			String message = AonUtil.getMessage(PAYROLL_BUNDLE, SALARY_EMAIL_BODY_LINE);
			String line = MessageFormat.format(message, salary.getContract().getPerson().getFullName(), salary.getIssueDate() );
			body.append( line );
		}
		body.append(AonUtil.getMessage(PAYROLL_BUNDLE, SALARY_EMAIL_BODY_FOOTER) );		

		body.append( enterprise.getRegistry().getFullName() ).append( "<br/>" );
		RegistryMedia phone = getRegistryMedia(enterprise, MediaType.FIXED_PHONE);
		if ( phone != null ) {
			String phoneLabel = AonUtil.getMessage(PHONE);
			body.append( StringEscapeUtils.escapeHtml(phoneLabel));
			body.append( ": " ).append( phone.getValue()).append( "<br/>" );			
		}
		RegistryMedia fax = getRegistryMedia(enterprise, MediaType.FAX);
		if ( fax != null ) {
			String faxLabel = AonUtil.getMessage(ICommonMessages.FAX);
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
	
	
	/**
	 * Gets the attach as input stream.
	 * 
	 * @return the attach as input stream
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 * @throws IOException the IO exception
	 */
	public InputStream getAttachAsInputStream() throws IOException, ManagerBeanException{
		RegistryAttachment attach = obtainCompanyLogo();
		if(attach != null){
			return new ByteArrayInputStream(attach.getData());
		}
		return null;
	}
	
	/**
	 * Obtains enterprise logo.
	 * 
	 * @return the registry attachment
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public RegistryAttachment obtainCompanyLogo() throws ManagerBeanException {
		Integer id = ((Salary)getTo()).getContract().getWorkPlace().getEnterprise().getId();
		IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String alias = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
		criteria.addEqualExpression(alias, id);
		String type = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
		criteria.addEqualExpression(type, RegistryAttachmentType.LOGO);
		Iterator<?> iter = registryAttachBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAttachment)iter.next();
		}
		return null;
	}
	
	/*
	 * Plantilla de impresion de la nomina
	 */
	public String getSalaryTemplate(){
		try {
			EnterpriseData enterpriseData = getEnterpriseDataTemplate();
			if(enterpriseData == null || enterpriseData.getExpression() == null){
				ApplicationParameter appParam = getAppDefaultTemplate();
				if(appParam == null || appParam.getValue() == null){
					return DEFAULT_SALARY_TEMPLATE;
				}
				return appParam.getValue();
			}
			return enterpriseData.getExpression();
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> getSalaryTemplate ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private ApplicationParameter getAppDefaultTemplate() throws ManagerBeanException {
		IManagerBean dataBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(dataBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), ICompanyConstants.REPORT_SALARY_PARAM);
		List<ITransferObject> list = dataBean.getList(criteria);
		if(list.isEmpty()){
			return null;
		}
		return (ApplicationParameter) dataBean.getList(criteria).get(0);
	}

	private EnterpriseData getEnterpriseDataTemplate() throws ManagerBeanException {
		IManagerBean dataBean = BeanManager.getManagerBean(EnterpriseData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(dataBean.getFieldName(IEntityAlias.ENTERPRISE_DATA_ENTERPRISE_ID), ((Salary)getTo()).getContract().getWorkPlace().getEnterprise().getId());
		criteria.addEqualExpression(dataBean.getFieldName(IEntityAlias.ENTERPRISE_DATA_NAME), ICompanyConstants.REPORT_SALARY_PARAM);
		List<ITransferObject> list = dataBean.getList(criteria);
		if(list.isEmpty()){
			return null;
		}
		return (EnterpriseData) dataBean.getList(criteria).get(0);
	}

}
