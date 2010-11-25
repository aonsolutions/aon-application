package com.code.aon.ui.employee.controller;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.employee.Agreement;
import com.code.aon.employee.AgreementLevelCategory;
import com.code.aon.employee.Contract;
import com.code.aon.employee.ContractData;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.employee.enumeration.ContractCode;
import com.code.aon.employee.enumeration.ContractModel;
import com.code.aon.employee.enumeration.ContractOption;
import com.code.aon.employee.enumeration.ContractStatus;
import com.code.aon.employee.enumeration.ContractType;
import com.code.aon.employee.enumeration.ContractWorkingDay;
import com.code.aon.employee.enumeration.QuoteGroup;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.employee.utils.ContractBuilder;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.util.AonUtil;
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
	private Enterprise enterprise;
	private boolean enterpriseListEnabled;
	private boolean personListEnabled;
	private boolean showNewPersonWindow;
	private boolean showNewEnterpriseWindow;
	private ContractBuilder contractBuilder;
	private ContractOption contractOption;
	private ContractType contractType;
	private ContractModel model;
	private ContractCode code;
	private String imageUrl;
	private ContractWorkingDay workingDay;
	private List<SelectItem> workplaces;
	private boolean agreementSalary;
	private Double salary;
	private QuoteGroup quoteGroup;
	private String category;
	private Agreement agreement;
	
	
	public Agreement getAgreement() {
		return agreement;
	}

	public void setAgreement(Agreement agreement) {
		this.agreement = agreement;
	}

	public QuoteGroup getQuoteGroup() {
		return quoteGroup;
	}

	public void setQuoteGroup(QuoteGroup quoteGroup) {
		this.quoteGroup = quoteGroup;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public boolean isAgreementSalary() {
		return agreementSalary;
	}

	public void setAgreementSalary(boolean agreementSalary) {
		this.agreementSalary = agreementSalary;
	}

	public Double getSalary() {
		if(isAgreementSalary()){
			// a la espera deque se cree el agreement en enterprise 
			// para recoger el salario del convenio
			salary = new Double(10000);
		} 
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}
	
	public List<SelectItem> getWorkplaces() {
		return workplaces;
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

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public Contract getContract() {
		return contract;
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
		List<SelectItem> agreements = new LinkedList<SelectItem>();
		if(getEnterprise().getAgreement()!=null){
			Criteria criteria = new Criteria();
			try {
				IManagerBean bean = BeanManager.getManagerBean(Agreement.class);
				String identifier = bean.getFieldName(IEmployeeAlias.AGREEMENT_ID);
				Integer data = getContract().getWorkPlace().getEnterprise().getAgreement();
				criteria.addEqualExpression(identifier, data);
				for( ITransferObject to : bean.getList(criteria) ) {
					Agreement a = (Agreement)to;
					String name = a.getDescription();
					SelectItem item = new SelectItem(a, name);
					agreements.add(item);			
				}
			} catch (ManagerBeanException e) {
				
			}
		}
		return agreements;
	}
	public List<SelectItem> getCategories(){
		List<SelectItem> categories = new LinkedList<SelectItem>();
		if(getEnterprise().getAgreement()!=null){
			Criteria criteria = new Criteria();
			try {
				IManagerBean bean = BeanManager.getManagerBean(AgreementLevelCategory.class);
				String identifier = bean.getFieldName(IEmployeeAlias.AGREEMENT_LEVEL_CATEGORY_LEVEL_AGREEMENT_ID);
				Integer data = getContract().getWorkPlace().getEnterprise().getAgreement();
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
		setCurrentStep(0);
	}
	
	public void onSelect(ActionEvent event) {
		setCurrentStep(1);
	}
	public void onSelectEnterprise(ActionEvent event) {
		EnterpriseController enterpriseC = (EnterpriseController)AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER);
		enterpriseC.onSelect(event);
		setEnterprise((Enterprise) enterpriseC.getTo());
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
			criteria.addEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_PERSON_ID), getContract().getPerson().getId());
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				setContract((Contract)list.get(0));
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
		if(getContractType().getCodes().length>0 && getCode()==null){
			String msg = "Debe seleccionar el tiempo de la jornada";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException();
		}
	}
	
	private void onFinish(ActionEvent event) {
		// remesar el contrato
		getContract().setStatus(ContractStatus.PROCESSED);
		accept();
		acceptContractData();
	}
	public void onSave(ActionEvent event) {
		onContractGenerate(event);
		getContract().setStatus(ContractStatus.PENDING);
		accept();
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
//	public void onChangeZoom( ActionEvent event ) {
//		getContractBuilder().setZoomFactor(zoomFactor);
//	}
	
	private void accept(){
		try {
			setContract((Contract)BeanManager.getManagerBean(Contract.class).insertOrUpdate(getContract()));
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private void acceptContractData(){
		ContractData data = new ContractData();
		data.setCode(getCode());
		data.setConditions("");
		data.setContract(getContract());
		data.setDescription("");
		data.setStartDate(getContract().getStartDate());
		data.setEndDate(getContract().getEndDate());
		data.setQuoteGroup(getQuoteGroup());
		data.setCategory(getCategory());
		try {
			BeanManager.getManagerBean(ContractData.class).insert(data);
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
