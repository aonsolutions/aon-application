package com.esferalia.aon.ui.payroll.controller.wizard;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.company.EnterpriseCCC;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.registry.controller.IRegistryConstants;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.AgreementLevel;
import com.esferalia.aon.payroll.AgreementLevelCategory;
import com.esferalia.aon.payroll.AgreementLevelPayment;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.payroll.enumeration.ContractOption;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.payroll.enumeration.ContractType;
import com.esferalia.aon.payroll.enumeration.ContractWorkingDay;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;
import com.esferalia.aon.ui.payroll.utils.ContractBuilder;
import com.esferalia.aon.ui.payroll.utils.ContractXmlReader;
import com.esferalia.aon.ui.payroll.utils.ContractXmlWriter;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATOS;
import com.esferalia.aon.ui.payroll.utils.contractMojo.ObjectFactory;
import com.lowagie.text.DocumentException;

public class ContractGenerationWizard extends BasicController{

	private static final long serialVersionUID = 3733409240562499848L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractGenerationWizard.class.getName());
	private static final String IMAGE_URL_PREFIX0 = ".contractImage";
	private static final String IMAGE_URL_PREFIX1 = "?model=";
	private static final String IMAGE_URL_PREFIX2 = "&width=";
	private static final String IMAGE_URL_PREFIX3 = "&height=";
	private static final int MAX_FILE_SIZE_MB = 3;
	private static final int MAX_FILE_SIZE = MAX_FILE_SIZE_MB*1024*1024;
	private static final String CONTRACT_XML_CONTEXT_PATH = "com.esferalia.aon.ui.payroll.utils.contractMojo";
	
	private int currentStep;
	private static final String[] STEPS = {
			"contractGenerationWizard_step0",
			"contractGenerationWizard_step1",
			"contractGenerationWizard_step2",
			"contractGenerationWizard_step3",
			"contractGenerationWizard_step4" };

	private Contract contract;
	private ContractData contractData;
	private Enterprise enterprise;
	private boolean enterpriseListEnabled;
	private boolean personListEnabled;
	private boolean showNewPersonWindow;
	private boolean showNewEnterpriseWindow;
	private ContractBuilder contractBuilder;
	private String imageUrl;
	private List<SelectItem> workPlaces;

	
	private ContractOption contractOption;
	private ContractType contractType;
	private ContractModel contractModel;
	private ContractCode code;
	private ContractWorkingDay workingDay;
	private Double salary;
	private QuoteGroup quoteGroup;
	private Agreement agreement;
	private AgreementLevel agreementLevel;
	private AgreementLevelCategory category;
	private AgreementLevelPayment payment;
	private AonFile aonFile;
	private boolean agreementSalary;
	private ContractXmlWriter xmlWriter;
	
	private ContrataParams params;
	
	
	public ContractXmlWriter getXmlWriter() {
		return xmlWriter;
	}
	public void setXmlWriter(ContractXmlWriter xmlWriter) {
		this.xmlWriter = xmlWriter;
	}
	public ContrataParams getParams() {
		return params;
	}
	public void setParams(ContrataParams params) {
		this.params = params;
	}
	public AonFile getAonFile() {
		return this.aonFile;
	}
	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}
	
	public Agreement getAgreement() {
		return agreement;
	}

	public void setAgreement(Agreement agreement) {
		this.agreement = agreement;
	}
	
	public AgreementLevel getAgreementLevel() {
		return agreementLevel;
	}

	public void setAgreementLevel(AgreementLevel agreementLevel) {
		this.agreementLevel = agreementLevel;
		if(agreementLevel==null){
			setPayment(null);
		} else {
			loadPayment();
		}
	}

	private void loadPayment() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(AgreementLevelPayment.class);
			String identifier = bean.getFieldName(IPayrollAlias.AGREEMENT_LEVEL_PAYMENT_LEVEL_ID);
			Integer data = getAgreementLevel().getId();
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(identifier, data);
			for( ITransferObject to : bean.getList(criteria) ) {
				AgreementLevelPayment a = (AgreementLevelPayment)to;
				if(a.getType() == PaymentType.BASE_SALARY){
					setPayment(a);
				}
			}
		} catch (ManagerBeanException e) {
			//NADA. no se carga ningun payment
		}
	}

	public AgreementLevelPayment getPayment() {
		return payment;
	}

	public void setPayment(AgreementLevelPayment payment) {
		this.payment = payment;
	}

	public QuoteGroup getQuoteGroup() {
		return quoteGroup;
	}

	public void setQuoteGroup(QuoteGroup quoteGroup) {
		this.quoteGroup = quoteGroup;
	}

	public AgreementLevelCategory getCategory() {
		return category;
	}

	public void setCategory(AgreementLevelCategory category) {
		this.category = category;
		//TODO REASIGNAR
//		if(category!=null){
//			getContractData().setCategory(category.getDescription());
//		}
	}

	public boolean isAgreementSalary() {
		return agreementSalary;
	}

	public void setAgreementSalary(boolean agreementSalary) {
		this.agreementSalary = agreementSalary;
	}

	public Double getSalary() {
		if(isAgreementSalary()){
			salary = NumberUtils.toDouble(getPayment().getExpression());
		} else if(salary==null){
			salary = 0.0;
		}
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}
	
	public List<SelectItem> getWorkPlaces() {
		if(workPlaces==null){
			loadWorkPlaces();
		}
		return workPlaces;
	}
	public void setWorkPlaces(List<SelectItem> workPlaces) {
		this.workPlaces = workPlaces;
	}

	public ContractWorkingDay getWorkingDay() {
		return workingDay;
	}
	public void setWorkingDay(ContractWorkingDay workingDay) {
		this.workingDay = workingDay;
	}
	
	public String getImageUrl() {
		StringBuilder builder = new StringBuilder(getContractBuilder().getContractPage().toString());
		builder.append(IMAGE_URL_PREFIX0);
		builder.append(IMAGE_URL_PREFIX1);
		builder.append(getContractModel());
		builder.append(IMAGE_URL_PREFIX2);
		builder.append(getContractBuilder().getContractWidth());
		builder.append(IMAGE_URL_PREFIX3);
		builder.append(getContractBuilder().getContractHeight());
		imageUrl = builder.toString();
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public ContractBuilder getContractBuilder() {
		if(contractBuilder==null){
			contractBuilder = ContractBuilder.getInstance();
		}
		return contractBuilder;
	}
	public void setContractBuilder(ContractBuilder contractBuilder) {
		this.contractBuilder = contractBuilder;
	}
	public boolean isEnterpriseListEnabled() {
		return enterpriseListEnabled;
	}
	public void setEnterpriseListEnabled(boolean enterpriseListEnabled) {
		this.enterpriseListEnabled = enterpriseListEnabled;
	}
	public boolean isPersonListEnabled() {
		return personListEnabled;
	}
	public void setPersonListEnabled(boolean personListEnabled) {
		this.personListEnabled = personListEnabled;
	}
	public boolean isShowNewPersonWindow() {
		return showNewPersonWindow;
	}
	public void setShowNewPersonWindow(boolean showNewPersonWindow) {
		this.showNewPersonWindow = showNewPersonWindow;
	}
	public boolean isShowNewEnterpriseWindow() {
		return showNewEnterpriseWindow;
	}
	public void setShowNewEnterpriseWindow(boolean showNewEnterpriseWindow) {
		this.showNewEnterpriseWindow = showNewEnterpriseWindow;
	}
	public ContractModel getContractModel() {
		return contractModel;
	}
	public void setContractModel(ContractModel contractModel) {
		this.contractModel = contractModel;
	}
	public ContractCode getCode() {
		return code;
	}
	public void setCode(ContractCode code) {
		this.code = code;
	}
	public List<SelectItem> getContractCodes() {
		List<SelectItem> list=null;
		list = new LinkedList<SelectItem>();
		if(getContractType()!=null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			for (ContractCode c : getContractType().getCodes()) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				list.add(item);
			}
		}
		return list;
	}
	public ContractOption getContractOption() {
		return contractOption;
	}
	public void setContractOption(ContractOption contractOption) {
		this.contractOption = contractOption;
	}
	public ContractType getContractType() {
		return contractType;
	}
	public void setContractType(ContractType contractType) {
		this.contractType = contractType;
		if(contractType!=null){
			setContractModel(contractType.getModel());
		}
	}
	public List<SelectItem> getContractTypes() {
		List<SelectItem> list;
		list = new LinkedList<SelectItem>();
		if(getContractOption()!=null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			for (ContractType t : getContractOption().getTypes()) {
				String name = t.getName(locale);
				SelectItem item = new SelectItem(t, name);
				list.add(item);
			}
		}
		return list;
	}

	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public ContractData getContractData() {
		return contractData;
	}
	public void setContractData(ContractData contractData) {
		this.contractData = contractData;
	}
	
	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
		loadWorkPlaces();
	}

	public int getCurrentStep() {
		return this.currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}
	
	public List<SelectItem> getAgreements(){
		List<SelectItem> agreements = null;
		// TODO a la espera del company-payroll-bridge
//		if(getEnterprise().getAgreement()!=null && getEnterprise().getAgreement().getId()!=null){
//			agreements = new LinkedList<SelectItem>();
//			String name = getEnterprise().getAgreement().getDescription();
//			SelectItem item = new SelectItem(getEnterprise().getAgreement(), name);
//			agreements.add(item);			
//		}
		return agreements;
	}
	public List<SelectItem> getAgreementLevels(){
		List<SelectItem> levels = new LinkedList<SelectItem>();
		if(getAgreement()!=null && getAgreement().getId()!=null){
			Criteria criteria = new Criteria();
			try {
				IManagerBean bean = BeanManager.getManagerBean(AgreementLevel.class);
				String identifier = bean.getFieldName(IPayrollAlias.AGREEMENT_LEVEL_AGREEMENT_ID);
				Integer data = getAgreement().getId();
				criteria.addEqualExpression(identifier, data);
				for( ITransferObject to : bean.getList(criteria) ) {
					AgreementLevel l = (AgreementLevel)to;
					String name = l.getDescription();
					SelectItem item = new SelectItem(l, name);
					levels.add(item);			
				}
			} catch (ManagerBeanException e) {
				
			}
		}
		return levels;
	}
	public List<SelectItem> getCategories(){
		List<SelectItem> categories = new LinkedList<SelectItem>();
		if(getAgreementLevel()!=null){
			Criteria criteria = new Criteria();
			try {
				IManagerBean bean = BeanManager.getManagerBean(AgreementLevelCategory.class);
				String identifier = bean.getFieldName(IPayrollAlias.AGREEMENT_LEVEL_CATEGORY_LEVEL_AGREEMENT_ID);
				Integer data = getAgreementLevel().getId();
				criteria.addEqualExpression(identifier, data);
				for( ITransferObject to : bean.getList(criteria) ) {
					AgreementLevelCategory c = (AgreementLevelCategory)to;
					String name = c.getDescription();
					SelectItem item = new SelectItem(c, name);
					categories.add(item);			
				}
			} catch (ManagerBeanException e) {
				
			}
		}
		return categories;
	}
	
	// Action Listeners
	public void onNext(ActionEvent event) {
		if (getCurrentStep() == 0) {
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 1) {
			// buscar si existe algun contrato con la empresa y la persona seleccionadas
//			onSearchContract(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 2) {
			onValidate(event);
			getContractBuilder().setContractModelUrl(null);
			getContractBuilder().setContractFields(null);
			onContractDetailShow(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 3) {
			onContractGenerate(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 4) {
			onFinish(event);
		}
	}

	public void onPrevious(ActionEvent event) {
		if (getCurrentStep() == 4) {
			setCurrentStep(getCurrentStep() - 1);
		} else if (getCurrentStep() == 3) {
			setCurrentStep(getCurrentStep() - 1);
		} else if (getCurrentStep() == 2) {
			setCurrentStep(getCurrentStep() - 1);
		} else if (getCurrentStep() == 1) {
			setPersonListEnabled(false);
			setCurrentStep(getCurrentStep() - 1);
		}
	}

	public String previous() {
		return STEPS[getCurrentStep()];
	}

	public String next() {
		return STEPS[getCurrentStep()];
	}

	public boolean isPreviousAvailable() {
		return (getCurrentStep() > 0);
	}

	public boolean isNextAvailable() {
		return (getCurrentStep() < 4)&&(getCurrentStep() > 1);
	}
	
	public boolean isLast() {
		return (getCurrentStep() == STEPS.length-1);
	}
	
	/*
	 * ATTACH
	 */
	public void onFileUploaded( ActionEvent event ) {
		if(getAonFile().getSize()>MAX_FILE_SIZE){
			setAonFile(null);
			String msg = "El tamaño del archivo excede de lo permitido ("+MAX_FILE_SIZE_MB+" Mb)";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void fileUploaded(UploadEvent event) {
		try {
			UploadItem item = event.getUploadItem();
			AonFile f = new AonFile();
			File file = item.getFile();
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				f.setData(data);
			}
			f.setFileName( item.getFileName() );
			f.setMimeType( MimeType.get(item.getContentType()) );
			setAonFile(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	/*
	 * ActionListeners
	 */
	public void onStart(ActionEvent event) {
		setEnterpriseListEnabled(false);
		setPersonListEnabled(false);
		setContract(new Contract());
		getContract().setStartDate(new Date());
		setContractData(new ContractData());
		setParams(new ContrataParams());
		
		
		setWorkPlaces(null);
		setContractOption(null);
		setContractType(null);
		setCode(null);
		setAgreement(null);
		setCategory(null);
		setQuoteGroup(null);
		setAgreementSalary(false);
		setSalary(null);
		
		
		setCurrentStep(0);
	}
	
	public void onEnterpriseChanged( LookupChangeEvent event ) {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			setEnterprise((Enterprise)event.getNewValue());
		} else {
			setEnterprise(null);
		}
		loadWorkPlaces();
		setWorkPlaces(null);
//		setActivities(null);
	}
	
//	public void onSelect(ActionEvent event) {
//		setCurrentStep(1);
//	}
		
	private void onValidate(ActionEvent event) {
		if(getContractOption()==null){
			String msg = "Debe seleccionar la modalidad de contrato";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException();
		}
		if(getContractType()==null){
			String msg = "Debe seleccionar el tipo de contrato";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException();
		}
		// TODO REASIGNAR
//		if(getContractType().getCodes().length>0 && getContractData().getCode()==null){
//			String msg = "Debe seleccionar el tiempo de la jornada";
//			AonUtil.addErrorMessage(msg);
//			throw new AbortProcessingException();
//		}
	}
	
	private void onFinish(ActionEvent event) {
		// remesar el contrato
		getContract().setStatus(ContractStatus.PROCESSED);
		accept();
		acceptContractData();
		if(!isAgreementSalary()){
			acceptContractPayment();
		}
	}
	public void onDocumentSave(ActionEvent event) {
		onContractGenerate(event);
		getContract().setStatus(ContractStatus.PENDING);
//		accept();
//		acceptContractData();
//		if(!isAgreementSalary()){
//			acceptContractPayment();
//		}
		saveDocument();
	}
	public void onContractGenerate( ActionEvent event ) {
		try {
			getContract().setDocument(getContractBuilder().buildPdf(getContractModel()));
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AbortProcessingException(e);
		} catch (DocumentException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AbortProcessingException(e);
		}
	}
	private void saveDocument() {
//		try {
//			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
//			getContractBuilder().buildPdf(getContractModel());
//			
//		} catch (IOException e) {
//			LOGGER.error(e.getMessage(), e);
//			throw new AbortProcessingException(e);
//		} catch (DocumentException e) {
//			LOGGER.error(e.getMessage(), e);
//			throw new AbortProcessingException(e);
//		} catch (ManagerBeanException e) {
//			LOGGER.error(e.getMessage(), e);
//			throw new AbortProcessingException(e);
//		}
	}

//	public void onNewPersonShow( ActionEvent event ) {
//		((RegistryController)AonUtil.getRegisteredBean(IRegistryConstants.PERSON_CONTROLLER_NAME)).onReset(event);
//	}
//	public void onAcceptPerson( ActionEvent event ) {
//		((RegistryController)AonUtil.getRegisteredBean(IRegistryConstants.PERSON_CONTROLLER_NAME)).accept(event);
//		setPersonListEnabled(true);
//	}
	
	public void onContractDetailShow( ActionEvent event ) {
		try {
			if(getContractBuilder().getContractFields()==null || getContractBuilder().getContractFields().size()==0 ){
				getContractBuilder().setZoomFactor(2);
				getContractBuilder().readPdfFields(getContract().getDocument(),getContractModel());
				getContractBuilder().loadDefaultFields(getContract());
				getContractBuilder().setContractPage(1);
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public void onDownloadContract( ActionEvent event ) {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		try {
			byte[] buffer = getContract().getDocument();
			InputStream in = new ByteArrayInputStream(buffer);
			int bytes = in.read(buffer);
			while (bytes != -1) {
				response.getOutputStream().write(buffer, 0, bytes);
				bytes = in.read(buffer);
			}
			in.close();
			response.setContentType(MimeType.MIME_PDF.getName()); 
			response.flushBuffer();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		context.responseComplete();
	}
	
	public void onChangeContractPage( ActionEvent event ) {
		getContractBuilder().setContractPage(getContractBuilder().getContractPage()+1);
	}
	public void onFirstContractPage( ActionEvent event ) {
		getContractBuilder().setContractPage(1);
	}
	public boolean isLastContractPage() {
		return getContractBuilder().getContractPage().equals(getContractBuilder().getNumberOfContractPages());
	}
	
//	private void accept(){
//		try {
//			setContract((Contract)BeanManager.getManagerBean(Contract.class).insertOrUpdate(getContract()));
//		} catch (ManagerBeanException e) {
//			LOGGER.error(e.getMessage(), e);
//		}
//	}
	
	private void acceptContractData(){
		//TODO REASIGNAR
//		getContractData().setContract(getContract());
//		getContractData().setCode(getCode());
//		getContractData().setStartDate(getContract().getStartDate());
//		getContractData().setEndDate(getContract().getEndDate());
//		getContractData().setConditions("");
//		getContractData().setDescription("");
//		if(getQuoteGroup()!=null){
//			getContractData().setQuoteGroup(getQuoteGroup());
//		}
//		if(getCategory()!=null && StringUtils.isEmpty(getCategory().getDescription())){
//			getContractData().setCategory(getCategory().getDescription());
//		}
//		try {
//			setContractData((ContractData) BeanManager.getManagerBean(ContractData.class).insertOrUpdate(getContractData()));
//		} catch (ManagerBeanException e) {
//			LOGGER.error(e.getMessage(), e);
//		}
	}
	
	private void acceptContractPayment(){
		ContractPayment payment = new ContractPayment();
		payment.setContract(getContract());
		payment.setType(PaymentType.BASE_SALARY);
		payment.setDescription("");
		payment.setStartDate(getContract().getStartDate());
		payment.setEndDate(null);
		payment.setExpression(Double.toString(CommonUtil.round(getSalary())));
		try {
			BeanManager.getManagerBean(ContractPayment.class).insert(payment);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 *  collection para el jasperReport
	 */
	private List<Contract> list;
	public List<Contract> getList() {
		return list;
	}
	
	public void setList(List<Contract> list) {
		this.list = list;
	}
	
	public void loadList() throws ManagerBeanException {
		setList(new ArrayList<Contract>());
//		IManagerBean bean = BeanManager.getManagerBean(Contract.class);
//		getList().add((Contract)bean.getList(null).get(0));
		getList().add(getContract());
	}

//	@Override
//	public Collection<?> getCollection() {
//		try {
//			loadList();
//		} catch (ManagerBeanException e) {
//			e.printStackTrace();
//		}
//		return getList();
//	}
//
//	@Override
//	public Collection<?> getCollection(boolean forceRefresh)
//			throws ManagerBeanException {
//		return getCollection();
//	}
	
//	public String getBeanName(){
//		return "contractGenerationWizard";
//	}

//	public List<SelectItem> loadWorkPlaces() throws ManagerBeanException{
//		IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
//		Criteria criteria = new Criteria();
//		criteria.addEqualExpression(bean.getFieldName(ICompanyAlias.WORK_PLACE_ACTIVE), true);
//		criteria.addEqualExpression(bean.getFieldName(ICompanyAlias.WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());
//		criteria.addOrder(bean.getFieldName(ICompanyAlias.WORK_PLACE_DESCRIPTION));
//		List<SelectItem> list = new LinkedList<SelectItem>();
//		for(ITransferObject to: bean.getList(criteria)){
//			WorkPlace wp = (WorkPlace)to;
//			list.add(new SelectItem(wp, wp.getDescription()));	
//		}
//		setWorkPlaces(list);
//		return list;
//	}
	private void loadWorkPlaces() {
		setWorkPlaces(new LinkedList<SelectItem>());
		if (getEnterprise() != null) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICompanyAlias.WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());
				List<ITransferObject> list = bean.getList(criteria);
				for (ITransferObject to : list) {
					WorkPlace w = (WorkPlace)to; 
					String name = w.getDescription();
					SelectItem item = new SelectItem(w, name);
					getWorkPlaces().add(item);
				}
			} catch (ManagerBeanException e) {
				String msg = "Imposible cargar los Centros de Trabajo de la empresa. (" + e.getMessage() +")";
				LOGGER.error(msg);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg,e);
			}						
		}
	}
	
	
	
	
	public List<SelectItem> getCCCs() throws ManagerBeanException {
		LinkedList<SelectItem> cccs = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(EnterpriseCCC.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICompanyAlias.ENTERPRISE_CCC_ACTIVITY_ENTERPRISE_ID), getContract().getWorkPlace().getEnterprise().getId());
		criteria.addOrder(bean.getFieldName(ICompanyAlias.ENTERPRISE_CCC_CCC));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			EnterpriseCCC ccc = (EnterpriseCCC)to;
			cccs.add(new SelectItem(ccc, ccc.getCcc()));
		}
    	return cccs;
    }
	
	
	public void onShowExtraTab(ActionEvent event){
		
	}

	public void onShowContrataTab(ActionEvent event){
		
	}
	
	private CONTRATOS contratos;
	
	public CONTRATOS getContratos() {
		return contratos;
	}
	public void setContratos(CONTRATOS contratos) {
		this.contratos = contratos;
	}
	
	public void readXml() throws JAXBException, IOException {
		searchContrataAttach();
		if(getContrataAttach()!=null){
			byte[] f = getContrataAttach().getData();
			if(f!=null && f.length>0){
				File file = File.createTempFile("aon-temp", ".XML");
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(f);
				fos.close();
				JAXBContext jaxbContext = JAXBContext.newInstance(CONTRACT_XML_CONTEXT_PATH);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				setContratos((CONTRATOS) unmarshaller.unmarshal(file));
				ContractXmlReader reader = new ContractXmlReader();
				reader.completeContrataParams(getContratos(), getParams());
				unmarshaller.setEventHandler(new ContractValidationEventHandler());
			}
		}
	}
	
	private ContractAttachment contrataAttach;
	
	public void setContrataAttach(ContractAttachment contrataAttach) {
		this.contrataAttach = contrataAttach;
	}
	public ContractAttachment getContrataAttach(){
		return contrataAttach;
	}
	
	private void searchContrataAttach(){
		setContrataAttach(null);
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), ((Contract)getTo()).getId());
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.SPEE_CONTRATA);
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				setContrataAttach((ContractAttachment) list.get(0));
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
	}
	
	public void generateXml() throws JAXBException, ManagerBeanException, IOException{
		JAXBContext jaxbContext = JAXBContext.newInstance(CONTRACT_XML_CONTEXT_PATH);
		
		ObjectFactory factory = new ObjectFactory();
		setContratos(factory.createCONTRATOS());
		setXmlWriter(new ContractXmlWriter());
		getXmlWriter().setContract(getContract());
		getXmlWriter().setParams(getParams());
		
		getContratos().getCONTRATO100AndCONTRATO130AndCONTRATO150().add(getXmlWriter().execute());
		
		Marshaller marshaller = jaxbContext.createMarshaller();
//		JAXBElement<CONTRATOS> element = (new ObjectFactory()).createBooking(booking);
//		JAXBElement<CONTRATOS> element = new JAXBElement<CONTRATOS>(); 
		
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		File file = File.createTempFile("aon-temp", ".XML"); 
//		marshaller.marshal( getContratos(), System.out );
		marshaller.marshal( getContratos(), file );
		
		FileInputStream fin = new FileInputStream(file);
		byte fileContent[] = new byte[(int)file.length()];
		fin.read(fileContent);
		if(getContrataAttach()==null){
			setContrataAttach(new ContractAttachment());
		}
		getContrataAttach().setContract((Contract) getTo());
		getContrataAttach().setData(fileContent);
		getContrataAttach().setAttachmentType(ContractAttachmentType.SPEE_CONTRATA);
		getContrataAttach().setMimeType(MimeType.MIME_XML);
		getContrataAttach().setDescription("fichero_contrata");
		fin.close();
	}
	
	public class ContractValidationEventHandler implements
			ValidationEventHandler {
		public boolean handleEvent(ValidationEvent ve) {
			if (ve.getSeverity() == ValidationEvent.FATAL_ERROR
					|| ve.getSeverity() == ValidationEvent.ERROR) {
				ValidationEventLocator locator = ve.getLocator();
				// Print message from valdation event
				System.out.println("Invalid booking document: "
						+ locator.getURL());
				System.out.println("Error: " + ve.getMessage());
				// Output line and column number
				System.out.println("Error at column "
						+ locator.getColumnNumber() + ", line "
						+ locator.getLineNumber());
			}
			return true;
		}
	}
	
//	private File file;
//	
//	public File getFile() {
//		return file;
//	}
//	public void setFile(File file) {
//		this.file = file;
//	}
//	public void onGenerateXml(ActionEvent event){
//		try {
//			generateXml();
//			FacesContext faces = FacesContext.getCurrentInstance();
//			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
//			String fileName = "aon-out";
//			response.setContentType(MimeType.MIME_XML.getName());
//			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xml\";");
//
//			ServletOutputStream output = response.getOutputStream();
//			InputStream input = new FileInputStream(file);
//			int size = IOUtils.copy(input, output);
//			if (size > 0) {
//				response.setHeader("Content-Length", String.valueOf(size));
//			}
//			output.close();
//			input.close();
//			response.flushBuffer();
//			faces.responseComplete();
//		} catch (IOException e) {
//			AonUtil.addErrorMessage(e.getMessage());
//			throw new AbortProcessingException(e);
//		} catch (ManagerBeanException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JAXBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
}
