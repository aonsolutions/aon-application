package com.esferalia.aon.ui.payroll.controller.wizard;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.AgreementLevel;
import com.esferalia.aon.payroll.AgreementLevelCategory;
import com.esferalia.aon.payroll.AgreementLevelPayment;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.payroll.enumeration.ContractOption;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.payroll.enumeration.ContractType;
import com.esferalia.aon.payroll.enumeration.ContractWorkingDay;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.ui.payroll.utils.ContractBuilder;
import com.lowagie.text.DocumentException;

public class ContractGenerationWizard implements Serializable, ICollectionProvider{

	private static final long serialVersionUID = 3733409240562499848L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractGenerationWizard.class.getName());

	private final String ENTERPRISE_CONTROLLER="enterprise";
	private final String ENTERPRISE_WORKPLACE_CONTROLLER="enterpriseWorkplace";
	private final String PERSON_CONTROLLER="person";
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
	private boolean agreementSalary;
	private List<SelectItem> workplaces;

	
	private ContractOption contractOption;
	private ContractType contractType;
	private ContractModel model;
	private ContractCode code;
	private ContractWorkingDay workingDay;
	private Double salary;
	private QuoteGroup quoteGroup;
	private Agreement agreement;
	private AgreementLevel agreementLevel;
	private AgreementLevelCategory category;
	private AgreementLevelPayment payment;
	
	
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
	
	public List<SelectItem> getWorkplaces() {
		return workplaces;
	}
	public void setWorkplaces(List<SelectItem> workplaces) {
		this.workplaces = workplaces;
	}

	public ContractWorkingDay getWorkingDay() {
		return workingDay;
	}
	public void setWorkingDay(ContractWorkingDay workingDay) {
		this.workingDay = workingDay;
	}
	
	public String getImageUrl() {
		imageUrl = getContractBuilder().getContractPage().toString();
		imageUrl += ".contractImage";
		imageUrl += "?model="+getModel();
		imageUrl += "&width="+getContractBuilder().getContractWidth();
		imageUrl += "&height="+getContractBuilder().getContractHeight();
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
	public ContractModel getModel() {
		return model;
	}
	public void setModel(ContractModel model) {
		this.model = model;
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
			setModel(contractType.getModel());
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
	 * ActionListeners
	 */
	public void onStart(ActionEvent event) {
		setEnterpriseListEnabled(false);
		setPersonListEnabled(false);
		setContract(new Contract());
		getContract().setStartDate(new Date());
		setContractData(new ContractData());
		
		
		setWorkplaces(null);
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
	
	public void onSelect(ActionEvent event) {
		setCurrentStep(1);
	}
	public void onSelectEnterprise(ActionEvent event) {
		EnterpriseController enterpriseC = (EnterpriseController)AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER);
		enterpriseC.onSelect(event);
		setEnterprise((Enterprise) enterpriseC.getTo());
		// TODO a la espera del company-payroll-bridge
//		setAgreement(getEnterprise().getAgreement());
		if(enterpriseC.getCcc()==null){
			String msg = "La empresa no dispone de ninguna cuenta de cotizacion";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		getContract().setEnterpriseCCC(enterpriseC.getCcc());
		this.workplaces = loadWorkPlaces();
		setEnterpriseListEnabled(false);
		setCurrentStep(1);
	}
	
	public List<SelectItem> loadWorkPlaces(){
		LinesController wpc = (LinesController)AonUtil.getRegisteredBean(ENTERPRISE_WORKPLACE_CONTROLLER);
		wpc.onSearch(null);
		List<SelectItem> list = new LinkedList<SelectItem>();
		for(ITransferObject to: wpc.getWrappedList()){
			WorkPlace wp = (WorkPlace)to;
			SelectItem item = new SelectItem(wp, wp.getDescription());
			list.add(item);	
		}
		return list;
	}
	public void onSelectPerson(ActionEvent event) {
		RegistryController person = (RegistryController)AonUtil.getRegisteredBean(PERSON_CONTROLLER);
		try {
			getContract().setPerson((Person)person.getModel().getRowData());
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining person", e);
		}
		setPersonListEnabled(false);
		onSearchContract(event);
		setCurrentStep(2);
	}

	public void onSearchEnterprise(ActionEvent event) {
		((EnterpriseController)AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER)).onSearch(event);
		setEnterpriseListEnabled(true);
	}
	
	public void onSearchPerson(ActionEvent event) {
		((RegistryController)AonUtil.getRegisteredBean(PERSON_CONTROLLER)).onSearch(event);
		try {
			((RegistryController)AonUtil.getRegisteredBean(PERSON_CONTROLLER)).clearCriteria();
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining person", e);
		}
		setPersonListEnabled(true);
	}

	public void onResetEnterpriseSearch(ActionEvent event) {
		setEnterpriseListEnabled(false);
	}
	public void onResetPersonSearch(ActionEvent event) {
		setPersonListEnabled(false);
	}

	private void onSearchContract(ActionEvent event) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_PERSON_ID), getContract().getPerson().getId());
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				setContract((Contract)list.get(0));
				IManagerBean dataBean = BeanManager.getManagerBean(ContractData.class);
				criteria = new Criteria();
				criteria.addEqualExpression(dataBean.getFieldName(IPayrollAlias.CONTRACT_DATA_CONTRACT_ID), getContract().getId());
				Expression expr1;
		    	Expression expr2;
				expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(dataBean.getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE), new Date());
		    	expr2 = ExpressionUtilities.getNullExpression(dataBean.getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE));
		    	criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));	
				List<ITransferObject> dataList = dataBean.getList(criteria);
				if(dataList.size()>1){
					String msg = "Existen varios contratos activos para esta persona";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				} else if(dataList.size()==1){
					setContractData((ContractData)dataList.get(0));
				} else {
					setContractData(new ContractData());
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
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
	public void onSave(ActionEvent event) {
		onContractGenerate(event);
		getContract().setStatus(ContractStatus.PENDING);
		accept();
		acceptContractData();
		if(!isAgreementSalary()){
			acceptContractPayment();
		}
	}
	public void onContractGenerate( ActionEvent event ) {
		try {
			getContract().setDocument(getContractBuilder().buildPdf(getModel()));
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AbortProcessingException(e);
		} catch (DocumentException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AbortProcessingException(e);
		}
	}

	public void onNewPersonShow( ActionEvent event ) {
		((RegistryController)AonUtil.getRegisteredBean(PERSON_CONTROLLER)).onReset(event);
	}
	public void onAcceptPerson( ActionEvent event ) {
		((RegistryController)AonUtil.getRegisteredBean(PERSON_CONTROLLER)).accept(event);
		setPersonListEnabled(true);
	}
	
	public void onContractDetailShow( ActionEvent event ) {
		try {
			if(getContractBuilder().getContractFields()==null || getContractBuilder().getContractFields().size()==0 ){
				getContractBuilder().setZoomFactor(2);
				getContractBuilder().readPdfFields(getContract().getDocument(),getModel());
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
	
	private void accept(){
		try {
			setContract((Contract)BeanManager.getManagerBean(Contract.class).insertOrUpdate(getContract()));
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
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
	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection() {
		try {
			loadList();
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return getList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		return getCollection();
	}
	
	
	
}
