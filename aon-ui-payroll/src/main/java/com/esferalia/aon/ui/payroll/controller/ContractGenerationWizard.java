package com.esferalia.aon.ui.payroll.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.Classpath;
import com.code.aon.company.WorkPlace;
import com.code.aon.employee.Contract;
import com.code.aon.employee.ContractType;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.employee.enumeration.ContractModel;
import com.code.aon.employee.enumeration.ContractStatus;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.registry.controller.RegistryDirStaffLinesController;
import com.code.aon.ui.util.AonUtil;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

public class ContractGenerationWizard implements Serializable, ICollectionProvider{

	private static final long serialVersionUID = 3733409240562499848L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractGenerationWizard.class.getName());

	private final String ENTERPRISE_CONTROLLER="enterprise";
	private final String PERSON_CONTROLLER="person";
	private final String ENTERPRISE_DIR_STAFF_CONTROLLER="enterpriseDirStaff";
	private final String MODEL_PATH = "com/esferalia/aon/ui/payroll/contractModel/"; 
	private int currentStep;
	private static final String[] STEPS = {
			"contractGenerationWizard_step0",
			"contractGenerationWizard_step1",
			"contractGenerationWizard_step2",
			"contractGenerationWizard_step3",
			"contractGenerationWizard_step4" };
	private Contract contract;
	private boolean enterpriseListEnabled;
	private boolean personListEnabled;
	private boolean showContractDetailWindow;
	private boolean showNewPersonWindow;
	private List<ContractField> contractFields;
	private Integer contractPage;
	private ContractModel model;
	private int numberOfContractPages;

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
	public List<ContractField> getContractFields() {
		if(contractFields==null){
			contractFields = new LinkedList<ContractField>();
		}
		return contractFields;
	}
	public void setContractFields(List<ContractField> contractFields) {
		this.contractFields = contractFields;
	}
	public Integer getContractPage() {
		return contractPage;
	}
	public void setContractPage(Integer contractPage) {
		this.contractPage = contractPage;
	}
	public ContractModel getModel() {
		return model;
	}
	public void setModel(ContractModel model) {
		this.model = model;
	}

	public boolean isShowContractDetailWindow() {
		return showContractDetailWindow;
	}

	public void setShowContractDetailWindow(boolean showContractDetailWindow) {
		this.showContractDetailWindow = showContractDetailWindow;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public Contract getContract() {
		return contract;
	}

	public int getCurrentStep() {
		return this.currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}
	
	// Action Listeners
	public void onNext(ActionEvent event) {
		if (getCurrentStep() == 0) {
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 1) {
			// buscar si existe algun contrato con la empresa y la persona seleccionadas
			onSearchContract(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 2) {
			onValidate(event);
			setContractFields(null);
			onContractDetailShow(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 3) {
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 4) {
			onFinish(event);
		} 
//		else if (getCurrentStep() == 5) {
//			onFinish(event);
//		}
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
		getContract().setContractType(new ContractType());
		getContract().setStartDate(new Date());
		setCurrentStep(0);
	}
	
	public void onSelect(ActionEvent event) {
		setCurrentStep(1);
	}
	public void onSelectEnterprise(ActionEvent event) {
		EnterpriseController enterpriseC = (EnterpriseController)AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER);
		LinesController workplaceC = (LinesController)AonUtil.getRegisteredBean("enterpriseWorkplace");
		LinesController activityC = (LinesController)AonUtil.getRegisteredBean("enterpriseActivity");
		LinesController cccC = (LinesController)AonUtil.getRegisteredBean("enterpriseCCC");

		workplaceC.onSearch(null);
		workplaceC.onSelectFirst(null);
		WorkPlace w = (WorkPlace)workplaceC.getTo();
		getContract().setWorkPlace(w);
//		try {
//			
////			EnterpriseActivity ea;
////			EnterpriseCCC ccc;
//			
////			getContract().setCcc(ccc);
//			getContract().setWorkPlace(w);
////			getContract().getWorkPlace().setEnterprise((Enterprise)enterpriseC.getWrappedList().get(enterpriseC.getModel().getRowIndex()));
//		} catch (ManagerBeanException e) {
//			LOGGER.error("Error obtaining enterprise", e);
//		}
		setEnterpriseListEnabled(false);
		setCurrentStep(1);
	}
	public void onSelectPerson(ActionEvent event) {
		RegistryController person = (RegistryController)AonUtil.getRegisteredBean(PERSON_CONTROLLER);
		try {
			getContract().setPerson((Person)person.getWrappedList().get(person.getModel().getRowIndex()));
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining person", e);
		}
		setPersonListEnabled(false);
//		setContractFields(null);
		setCurrentStep(2);
	}

	public void onSearchEnterprise(ActionEvent event) {
		((EnterpriseController)AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER)).onSearch(event);
		try {
			((EnterpriseController)AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER)).clearCriteria();
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining enterprise", e);
		}
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
			criteria.addEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID), getContract().getWorkPlace().getEnterprise().getId());
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				setContract((Contract)list.get(0));
			}
			
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private void onValidate(ActionEvent event) {
		
	}
	
	private void onFinish(ActionEvent event) {
		// remesar el contrato
		getContract().setStatus(ContractStatus.PROCESSED);
		accept();
	}
	
	public void onSaveProcess(ActionEvent event) {
		getContract().setStatus(ContractStatus.PENDING);
		accept();
	}

	public void onNewPersonShow( ActionEvent event ) {
		((RegistryController)AonUtil.getRegisteredBean(PERSON_CONTROLLER)).onReset(event);
	}
	public void onAcceptPerson( ActionEvent event ) {
		((RegistryController)AonUtil.getRegisteredBean(PERSON_CONTROLLER)).onAccept(event);
		setPersonListEnabled(true);
	}
	
	public void onContractDetailShow( ActionEvent event ) {
		try {
			if(getContractFields()==null || getContractFields().size()==0 ){
				readPdfFields();
				loadDefaultFields();
				setContractPage(1);
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	public void onContractSave( ActionEvent event ) {
		try {
			buildPdf();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AbortProcessingException(e);
		} catch (DocumentException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AbortProcessingException(e);
		}
	}
	
	public void onDownloadContract( ActionEvent event ) {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		try {
			byte[] buffer = getContract().getPdf();
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
	
	public void onChangeModalPage( ActionEvent event ) {
		setContractPage(getContractPage()+1);
	}
	public void onFirstModalPage( ActionEvent event ) {
		setContractPage(1);
	}
	public boolean isLastModalPage() {
		return getContractPage().equals(numberOfContractPages);
	}
	
//	private void pdfDigester() {
//		
//	}
	
	@SuppressWarnings("unchecked")
	private void readPdfFields() throws IOException{
		String file = model+".pdf"; 
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = Classpath.search(cl, MODEL_PATH, file);
		PdfReader reader = new PdfReader(urls[0]);
		numberOfContractPages = reader.getNumberOfPages();
		AcroFields form = reader.getAcroFields();
		HashMap fields = form.getFields();

//		form.getAppearanceStates("Verif1");
		
		String key;
		ContractField field;
		for (Iterator it = fields.keySet().iterator(); it.hasNext();) {
			key = (String) it.next();
			field = new ContractField();
			if(form.getFieldType(key)==AcroFields.FIELD_TYPE_CHECKBOX){
					field.setType(AcroFields.FIELD_TYPE_CHECKBOX);
			} else if(form.getFieldType(key)==AcroFields.FIELD_TYPE_TEXT){
					field.setType(AcroFields.FIELD_TYPE_TEXT);;
			} else {
				field.setType(AcroFields.FIELD_TYPE_NONE);;
			}
			Float f = form.getFieldPositions(key)[0];
			field.setPage(f.intValue());
			field.setLabel(key);
			field.setBottomCoordinates(getBottomCoordinates(form, key));
			field.setLeftCoordinates(getLeftCoordinates(form, key));
			field.setWidth(getInputTextWidth(form, key));
			field.setHeight(getInputTextHeight(form, key));
			if(field.getType()!=null){
				getContractFields().add(field);
			}
		}
		reader.close();
	}
	
	private void loadDefaultFields() throws ManagerBeanException {
		RegistryDirStaffLinesController rDirStaff = (RegistryDirStaffLinesController)AonUtil.getRegisteredBean(ENTERPRISE_DIR_STAFF_CONTROLLER);
		if(getContract().getStartDate()!=null){
			Criteria criteria = rDirStaff.getCriteria();
			criteria.addLessThanOrEqualExpression(rDirStaff.getFieldName(IRegistryAlias.REGISTRY_DIR_STAFF_DUE_DATE), getContract().getStartDate());
			rDirStaff.setCriteria(criteria);
		}
		rDirStaff.onSearch(null);
		rDirStaff.onSelectFirst(null);
		RegistryDirStaff dir = (RegistryDirStaff)rDirStaff.getTo();
		for(ContractField field: getContractFields()){
			try{
			if(field.getLabel().equals("Texto1")){
				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			// nombre representante
			if(field.getLabel().equals("Texto2")){
				field.setValue(dir.getName());
			}
			// nif representante
			if(field.getLabel().equals("Texto3")){
				field.setValue(dir.getDocument());
			}
			// en concepto de que
			if(field.getLabel().equals("Texto4")){
				if(dir.isShareHolder()){
					field.setValue("Socio");
				}
				if(dir.isDirector()){
					field.setValue("Apoderado");
				}
				if(dir.isRepresentative()){
					field.setValue("Representante");
				}
				
			}
			// nombre empresa
			if(field.getLabel().equals("Texto5")){
				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getFullName());
			}
			//direccion empresa
			if(field.getLabel().equals("Texto6")){
				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getFullAddress());
			}
			// pais empresa
			if(field.getLabel().equals("Texto7")){
				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getGeozone().getName());
			}
			if(field.getLabel().equals("Cifra1")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra2")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra3")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			// municipio empresa
			if(field.getLabel().equals("Texto8")){
				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getCity());
			}
			if(field.getLabel().equals("Cifra4")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra5")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra6")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra7")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra8")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			// CP
			if(field.getLabel().equals("Cifra9")){
				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(0, 1));
			}
			if(field.getLabel().equals("Cifra10")){
				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(1, 2));
			}
			if(field.getLabel().equals("Cifra11")){
				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(2, 3));
			}
			if(field.getLabel().equals("Cifra12")){
				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(3, 4));
			}
			if(field.getLabel().equals("Cifra13")){
				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(4, 5));
			}
			// numero ccc
			if(field.getLabel().equals("Cifra14")){
//				field.setValue(getContract().getCcc().getCCC());
			}
			if(field.getLabel().equals("Cifra15")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			} 
			if(field.getLabel().equals("Cifra16")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			} 
			if(field.getLabel().equals("Cifra17")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			} 
			if(field.getLabel().equals("Cifra18")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			} 
			if(field.getLabel().equals("Cifra19")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			} 
			if(field.getLabel().equals("Texto9")){
				field.setValue(getContract().getCcc().getCCC());
			} 
			if(field.getLabel().equals("Cifra20")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra21")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			// actividad economica
			if(field.getLabel().equals("Texto10")){
				field.setValue(getContract().getCcc().getActivity().getDescription());
			}
			if(field.getLabel().equals("Cifra22")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra23")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			// pais centro trabajo
			if(field.getLabel().equals("Texto11")){
				field.setValue(getContract().getWorkPlace().getAddress().getGeozone().getName());
			}
			if(field.getLabel().equals("Cifra24")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra25")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra26")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			// municipio centro trabajo
			if(field.getLabel().equals("Texto12")){
				field.setValue(getContract().getWorkPlace().getAddress().getCity());
			}
			if(field.getLabel().equals("Cifra27")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra28")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra29")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra30")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra31")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			// nombre empleado
			if(field.getLabel().equals("Texto13")){
				field.setValue(getContract().getPerson().getRegistry().getFullName());
			}
			// cif empleado
			if(field.getLabel().equals("Texto14")){
				field.setValue(getContract().getPerson().getRegistry().getDocument());
			}
			// fecha nacimiento empleado
			if(field.getLabel().equals("Texto15")){
				field.setValue(getContract().getPerson().getBirthDate().toString());
			}
			//numero ss empleado
			if(field.getLabel().equals("Texto16")){
				field.setValue(getContract().getPerson().getSocialSecurityNumber());
			}
			// nivel formativo empleado
			if(field.getLabel().equals("Texto17")){
//				field.setValue(getContract().getPerson());
			}
			if(field.getLabel().equals("Cifra32")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra33")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			// nacionalidad empleado
			if(field.getLabel().equals("Texto18")){
				field.setValue(getContract().getPerson().getRegistry().getDefaultAddress().getGeozone().getName());
			}
			if(field.getLabel().equals("Cifra34")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra35")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra36")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			//  municipio domicilio empleado
			if(field.getLabel().equals("Texto19")){
				field.setValue(getContract().getPerson().getRegistry().getDefaultAddress().getCity());
			}
			if(field.getLabel().equals("Cifra37")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra38")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra39")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra40")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra41")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			// pais domicilio empleado
			if(field.getLabel().equals("Texto20")){
				field.setValue(getContract().getPerson().getRegistry().getDefaultAddress().getGeozone().getName());
			}
			if(field.getLabel().equals("Cifra42")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra43")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			if(field.getLabel().equals("Cifra44")){
//				field.setValue(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			}
			} catch(NullPointerException e){
				
			}
		}
	}

	private void buildPdf() throws IOException, DocumentException {
		String file = model+".pdf"; 
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = Classpath.search(cl, MODEL_PATH, file);
		PdfReader reader = new PdfReader(urls[0]);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		PdfStamper stamp = new PdfStamper(reader, baos);
	    AcroFields form = stamp.getAcroFields();
	    String checkValue = null;
	    for(ContractField field: getContractFields()){
			if(field.getType()==AcroFields.FIELD_TYPE_CHECKBOX){
				if(checkValue==null){
					checkValue = form.getAppearanceStates(field.getLabel())[0];
				}
				form.setField(field.getLabel(), field.getValue().equals("true")?checkValue:"");
			} else {
				form.setField(field.getLabel(), field.getValue());
			}
		}
	    stamp.setFormFlattening(true);
	    stamp.close();
	    reader.close();
	    getContract().setPdf(baos.toByteArray());
	}
	
	private void accept(){
		try {
			BeanManager.getManagerBean(Contract.class).insertOrUpdate(getContract());
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/*
	 * [page, llx, lly, urx, ury]
	 */
	private static String getBottomCoordinates(AcroFields form, String key){
		return Float.toString(form.getFieldPositions(key)[2]-5);
	}
	private static String getLeftCoordinates(AcroFields form, String key){
		return Float.toString(form.getFieldPositions(key)[1]-5);
	}
	private static String getInputTextWidth(AcroFields form, String key){
//		form.getFieldItem(key).
//		return Integer.toString(form.getFieldItem(key).size());
		Float f = form.getFieldPositions(key)[3]-form.getFieldPositions(key)[1];
		return String.valueOf(f.intValue());
		
//		return Float.toString();
	}
	private static String getInputTextHeight(AcroFields form, String key){
		return Float.toString(form.getFieldPositions(key)[4]-form.getFieldPositions(key)[2]);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////
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
