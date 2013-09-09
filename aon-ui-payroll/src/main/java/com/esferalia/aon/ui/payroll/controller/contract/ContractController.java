package com.esferalia.aon.ui.payroll.controller.contract;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.SingleCollectionProvider;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.report.OutputFormat;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.AgreementLevelCategory;
import com.esferalia.aon.payroll.AgreementLevelData;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.EnterpriseActivity;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.PayrollWorkPlace;
import com.esferalia.aon.payroll.TrainingCenter;
import com.esferalia.aon.payroll.TrainingCourse;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractDuration;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.payroll.enumeration.ContractModelCode;
import com.esferalia.aon.payroll.enumeration.ContractOption;
import com.esferalia.aon.payroll.enumeration.ContractType;
import com.esferalia.aon.payroll.enumeration.ContractWorkingDay;
import com.esferalia.aon.payroll.enumeration.OccupationType;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.PayrollAppParamsController;
import com.esferalia.aon.ui.payroll.controller.TrainingCenterController;
import com.esferalia.aon.ui.payroll.utils.ContractUtils;
import com.esferalia.aon.ui.sepe.controller.ContrataController;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;

public class ContractController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContractController.class.getName());
	
	private Enterprise enterprise;
	private List<SelectItem> workPlaces;
	private List<SelectItem> enterpriseCCCs;
	private List<SelectItem> activities;
	private Agreement agreement;
	
	private ContractParams params;

	private boolean showNewContractModal;
	private boolean skipPayrollData;
	
	public ContractParams getParams() {
		if(params==null){
			params = new ContractParams();
		}
		return params;
	}
	public void setParams(ContractParams params) {
		this.params = params;
	}
	
	public boolean isSkipPayrollData() {
		return skipPayrollData;
	}
	public void setSkipPayrollData(boolean skipPayrollData) {
		this.skipPayrollData = skipPayrollData;
	}
	public boolean isShowNewContractModal() {
		return showNewContractModal;
	}
	public void setShowNewContractModal(boolean showNewContractModal) {
		this.showNewContractModal = showNewContractModal;
	}
	public Agreement getAgreement() {
		return agreement;
	}
	public void setAgreement(Agreement agreement) {
		this.agreement = agreement;
	}
	
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
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
	
	public List<SelectItem> getEnterpriseCCCs() {
		if(enterpriseCCCs==null){
			loadEnterpriseCCCs();
		}
		return enterpriseCCCs;
	}
	public void setEnterpriseCCCs(List<SelectItem> enterpriseCCCs) {
		this.enterpriseCCCs = enterpriseCCCs;
	}

	public List<SelectItem> getActivities() {
		if(activities==null){
			loadActivities();
		}
		return activities;
	}
	public void setActivities(List<SelectItem> activities) {
		this.activities = activities;
	}
	
	public boolean isEndDateRequired(){
		String contractCode = null;
		if(this.isNew() && this.getParams()!=null && this.getParams().getContractCode()!=null){
			contractCode = this.getParams().getContractCode().getValue();
		} else {
			contractCode = ContractUtils.getInstance().getContractDataMap((Contract) this.getTo()).get(ContextVariable.TC2.getName());
		}
		String[] codes = {"402", "420", "421", "430", "441", "452", "502", "520", "530", "541", "552", "970"};
		return ArrayUtils.contains(codes, contractCode) ;
	}
	
	public boolean isEndDateOptional(){
		String contractCode = null;
		if(this.isNew() && this.getParams()!=null && this.getParams().getContractCode()!=null){
			contractCode = this.getParams().getContractCode().getValue();
		} else {
			contractCode = ContractUtils.getInstance().getContractDataMap((Contract) this.getTo()).get(ContextVariable.TC2.getName());
		}
		String[] codes = {"401", "403", "410", "501", "503", "510", "540", "980", "990"};
		return ArrayUtils.contains(codes, contractCode) ;
	}
	
	public boolean isExtensibleContract(){
		// TODO: what contracts are extensible?
		String contractCode = ContractUtils.getInstance().getContractDataMap((Contract) this.getTo()).get(ContextVariable.TC2.getName());
		String[] codes = {"421"};
		return ArrayUtils.contains(codes, contractCode) ;
	}

	public boolean isTransformableContract(){
		// TODO: what contracts are transformable?
		String contractCode = ContractUtils.getInstance().getContractDataMap((Contract) this.getTo()).get(ContextVariable.TC2.getName());
		String[] codes = {};
		return ArrayUtils.contains(codes, contractCode) ;
	}
	
	public List<SelectItem> getContractModel() {
		ContractModel[] availableModels = {ContractModel.PE151, ContractModel.PE170, 
				ContractModel.PE176, ContractModel.PE177, ContractModel.PE179, 
				ContractModel.PE183, ContractModel.PE187, ContractModel.PE226};
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> list = new LinkedList<SelectItem>();
		if(getParams().getContractCode()!=null){
			for( ContractType type : ContractType.values() ) {
				if( ArrayUtils.contains(availableModels, type.getModel()) ){
					for( ContractCode code : type.getCodes() ) {
						if ( code ==  getParams().getContractCode()) {
							SelectItem item = new SelectItem(type.getModel(), type.getName(locale));
							list.add(item);
						}
					}
				}
			}
		}
		return list;
	}
	
	public String getSepeCommunicationId(){
		if(this.getTo()!=null){
			ContractUtils utils = ContractUtils.getInstance();
			return utils.getContractDataMap((Contract) getTo()).get(ContextVariable.SEPE_CONTRACT_ID.getName());
		}
		return null;
	}

	public String getContractCode(){
		try {
			if(this.getModel().isRowAvailable()){
				ContractUtils utils = ContractUtils.getInstance();
				String code = utils.getContractDataMap((Contract) getModel().getRowData()).get(ContextVariable.TC2.getName());
				ContractCode contractCode = ContractCode.getContractCodeByValue(code);
				return contractCode!=null?code + " - " + contractCode.getName(FacesContext.getCurrentInstance().getViewRoot().getLocale()):"";
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> getContractCode exception: ",e);
			AonUtil.addErrorMessage("Se ha producido un error al obtener el código de contrato. ");
			throw new AbortProcessingException(e.getMessage(), e);
		}
		return null;
	}
		
	public void onShowNewContractModal(ActionEvent event) {
		setShowNewContractModal(true);
	}
	
	public void onPersonBack(ActionEvent event){
		try {
			this.refresh(event);
			this.getManagerBean().restoreNullSubPOJOs(getTo());
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onPersonBack exception: ",e);
			AonUtil.addErrorMessage("Se ha producido un error al recargar los datos de persona. ");
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void onEnterpriseChanged( LookupChangeEvent event ) {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			setEnterprise((Enterprise)event.getNewValue());
		} else {
			setEnterprise(null);
		}
		
		setActivities(null);
		setWorkPlaces(null);
		setEnterpriseCCCs(null);
	}
	
	public void onActivityChanged( ActionEvent event ) {
		setEnterpriseCCCs(null);
	}
	
	public void onWorkPlaceChanged( ActionEvent event ) {
		Contract contract = (Contract) getTo();
		contract.setActivity(null);
		contract.setEnterpriseCCC(null);
		loadActivities();
		loadEnterpriseCCCs();
		
		loadWorkplaceAgreement(event);
	}

	private void loadWorkPlaces() {
		setWorkPlaces(new LinkedList<SelectItem>());
		if (getEnterprise() != null) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());
				List<ITransferObject> list = bean.getList(criteria);
				for (ITransferObject to : list) {
					WorkPlace w = (WorkPlace)to; 
					String name = w.getDescription();
					SelectItem item = new SelectItem(w, name);
					getWorkPlaces().add(item);
				}
				Contract contract = (Contract) getTo();
				if( !getWorkPlaces().isEmpty() && (contract.getWorkPlace()==null || contract.getWorkPlace().getId()==null) ){
					contract.setWorkPlace((WorkPlace) getWorkPlaces().get(0).getValue());
				}
			} catch (ManagerBeanException e) {
				String msg = "Imposible cargar los Centros de Trabajo de la empresa. (" + e.getMessage() +")";
				LOGGER.error(msg);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg,e);
			}						
			loadWorkplaceAgreement(null);
		}
	}

	private void loadActivities() {
		setActivities( new LinkedList<SelectItem>());
		if (getEnterprise() != null) {
			try {
				Contract contract = (Contract) getTo();
				IManagerBean pwBean = BeanManager.getManagerBean(PayrollWorkPlace.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(pwBean.getFieldName(IEntityAlias.PAYROLL_WORK_PLACE_WORK_PLACE_ID), contract.getWorkPlace().getId());
				List<ITransferObject> pwList = pwBean.getList(criteria);
				for(ITransferObject to: pwList){
					PayrollWorkPlace pw = (PayrollWorkPlace) to;
					if(pw.getEnterpriseActivity()!=null && pw.getEnterpriseActivity().getCnae2009()!=null){
						String name = pw.getEnterpriseActivity().getDescription() + " - (" + pw.getEnterpriseActivity().getCnae2009().getCode() + ") " + pw.getEnterpriseActivity().getCnae2009().getTitle();
						SelectItem item = new SelectItem(pw.getEnterpriseActivity(), name);
						getActivities().add(item);
					}
				}
				if( !getActivities().isEmpty() && (contract.getActivity()==null || contract.getActivity().getId()==null)){
					contract.setActivity((EnterpriseActivity)getActivities().get(0).getValue());
					BasicController controller = (BasicController) AonUtil.getRegisteredBean(IPayrollConstants.ENTERPRISE_CCC_CONTROLLER);
					controller.onSelectFirst(null);
					contract.setEnterpriseCCC((EnterpriseCCC) controller.getTo());
				}
			} catch (ManagerBeanException e) {
				String msg = "Imposible cargar las Actividades de la empresa. (" + e.getMessage() +")";
				LOGGER.error(msg);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg,e);
			}						
		}
	}

	private void loadEnterpriseCCCs() {
		setEnterpriseCCCs( new LinkedList<SelectItem>());
		Contract contract = (Contract) getTo();
		if (contract.getActivity() != null && contract.getActivity().getId() != null) {
			try {
				IManagerBean ecBean = BeanManager.getManagerBean(EnterpriseCCC.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(ecBean.getFieldName(IEntityAlias.ENTERPRISE_CCC_ACTIVITY_ID), contract.getActivity().getId());
				List<ITransferObject> ecList = ecBean.getList(criteria);
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				for(ITransferObject to: ecList){
					EnterpriseCCC ccc = (EnterpriseCCC) to;
					String name = ccc.getType().getName(locale) +" ("+ ccc.getCcc()+")";
					SelectItem item = new SelectItem(ccc, name);
					getEnterpriseCCCs().add(item);
				}
			} catch (ManagerBeanException e) {
				String msg = "Imposible cargar los CCC de la empresa. (" + e.getMessage() +")";
				LOGGER.error(msg);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg,e);
			}						
		}
	}
	
	public void loadWorkplaceAgreement(ActionEvent event){
		Contract contract = (Contract) getTo(); 
		try {
			if ( contract.getWorkPlace()!=null && contract.getWorkPlace().getId()!=null ) {
				IManagerBean bean = BeanManager.getManagerBean(PayrollWorkPlace.class);
				Criteria  criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAYROLL_WORK_PLACE_WORK_PLACE_ID), contract.getWorkPlace().getId());
				List<ITransferObject> list = bean.getList(criteria);
				if(!list.isEmpty()){
					PayrollWorkPlace pw = (PayrollWorkPlace) list.get(0);
					if(pw.getAgreement()!=null){
						setAgreement(pw.getAgreement());
					}
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "error loading workplace agreement";
			LOGGER.error(msg);
		}
	}
	
	@Deprecated
	public List<SelectItem> getAgreementLevelCategories(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		try {
			Criteria criteria = new Criteria();
			if(getAgreement()!=null && getAgreement().getId()!=null){
				IManagerBean cBean = BeanManager.getManagerBean(AgreementLevelCategory.class);
				criteria = new Criteria();
				criteria.addEqualExpression(cBean.getFieldName(IEntityAlias.AGREEMENT_LEVEL_CATEGORY_LEVEL_AGREEMENT_ID), getAgreement().getId());
				for (ITransferObject to : cBean.getList(criteria)) {
					AgreementLevelCategory alc = (AgreementLevelCategory) to;
					String name = alc.getLevel().getDescription() + (alc.getDescription()!=null?" - "+alc.getDescription():"");
					SelectItem item = new SelectItem(alc, name);
					list.add(item);
				}
			}
		} catch (ManagerBeanException e) {
			// NADA, no se cargan datos del convenio
		}
		return list;
	}
	
	@Deprecated
	public void onChangeAgreement(LookupChangeEvent event){
		if (event.getNewValue() == null || event.getNewValue().equals("")) {
			Contract contract = (Contract) getTo();
			contract.setAgreementLevelCategory(null);
		}
	}
	
	@Deprecated
	public void onChangeAgreementLevelCategory(ActionEvent event){
		// TODO if is new contract then ask for the salary. how obtain the salary from de agreement?
		Contract contract = (Contract) getTo();
		try {
			if(contract.getAgreementLevelCategory()!=null){
				IManagerBean bean = BeanManager.getManagerBean(AgreementLevelData.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_LEVEL_DATA_LEVEL_ID), contract.getAgreementLevelCategory().getLevel().getId());
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_LEVEL_DATA_NAME), "P05_IMPORTE");
				List<ITransferObject> list = bean.getList(criteria);
				if(!list.isEmpty()){
					AgreementLevelData d = (AgreementLevelData) list.get(0);
					getParams().setAgreementSalaryCheck(true);
					getParams().setGrossSalary(d.getDoubleExpression());
				} else {
					getParams().setAgreementSalaryCheck(false);
					getParams().setGrossSalary(null);
				}
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
	}
	
	public void onChangeCno(LookupChangeEvent event){
		Contract contract = (Contract) this.getTo();
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			String desc = ((CNO)event.getNewValue()).getTitle();
			if(StringUtils.isNotBlank(desc) && desc.length()>64){
				desc = desc.substring(0, 63);
			}
			contract.setCategoryDescription(desc);
		}
	}
	
	public boolean isTrainingContract(){
		ContractUtils utils = ContractUtils.getInstance();
		return utils.isTrainingContract((Contract) this.getTo());
	}

	public boolean isTrainingCenterDefined(){
		ContractUtils utils = ContractUtils.getInstance();
		Map<String, String> map = utils.getContractDataMap((Contract) this.getTo());
		return map.get(ContextVariable.TRAINING_CENTER.getName())!=null;
	}

	public boolean isTrainingCourseDefined(){
		ContractUtils utils = ContractUtils.getInstance();
		Map<String, String> map = utils.getContractDataMap((Contract) this.getTo());
		return map.get(ContextVariable.TRAINING_COURSE.getName())!=null;
	}
	
	public List<SelectItem> getTrainingCenters(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		CNO cno = getParams().getCno();
		if(cno!=null && cno.getId()!=null){
			List<TrainingCenter> centerList = new LinkedList<TrainingCenter>();
			try {
				IManagerBean bean = BeanManager.getManagerBean(TrainingCourse.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TRAINING_COURSE_CNO_ID), cno.getId());
				for(ITransferObject to: bean.getList(criteria)){
					TrainingCourse course = (TrainingCourse) to;
					if(!centerList.contains(course.getTrainingCenter())){
						centerList.add(course.getTrainingCenter());
					}
				}
				if(centerList.size()==1){
					getParams().setTrainingCenter(centerList.get(0));
				} else if(centerList.size()>1){
					PayrollAppParamsController params = (PayrollAppParamsController) AonUtil.getRegisteredBean(IPayrollConstants.PAYROLL_APP_PARAMS_CONTROLLER_NAME);
					if( getParams().getContractModelCode() != null
							&& getParams().getContractModelCode().getCode() == ContractCode.C421 
							&& params.getDefaultTrainingCenter()!=null && params.getDefaultTrainingCenter().getId()!=null){
						if(centerList.contains(params.getDefaultTrainingCenter())){
							getParams().setTrainingCenter(params.getDefaultTrainingCenter());
						}
					}
				}
			} catch (ManagerBeanException e) {
				String msg = "No se han podido cargar los centros formativos."; 
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
			}
			
			for(TrainingCenter center: centerList){
				SelectItem item = new SelectItem(center, center.getRegistry().getFullName());
				list.add(item);
			}
		}
		return list;
	}

	public List<SelectItem> getTrainingCourses(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		CNO cno = getParams().getCno();
		TrainingCenter center = getParams().getTrainingCenter();
		if( cno!=null && cno.getId()!=null
				&& center!=null && center.getId()!=null ){
			try {
				IManagerBean bean = BeanManager.getManagerBean(TrainingCourse.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TRAINING_COURSE_CNO_ID), cno.getId());
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TRAINING_COURSE_TRAINING_CENTER_ID), center.getId());
				for(ITransferObject to: bean.getList(criteria)){
					TrainingCourse course = (TrainingCourse) to;
					SelectItem item = new SelectItem(course, "("+course.getCode()+") "+course.getOccupationName());
					list.add(item);
				}
			} catch (ManagerBeanException e) {
				String msg = "No se han podido cargar los cursos formativos."; 
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
			}
		}
		return list;
	}
	
	public TrainingCenter getTrainingCenter(){
		return getParams().getTrainingCourse().getTrainingCenter();
	}
	
	public RegistryDirStaff getTrainingCenterDirStaff() throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID);
		criteria.addEqualExpression(alias, getTrainingCenter().getId());
		alias = bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE);
		Expression exp1 = ExpressionUtilities.getGreaterThanOrEqualExpression(alias, new Date());
		Expression exp2 = ExpressionUtilities.getNullExpression(alias);
		criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));
		Iterator<ITransferObject> iter = bean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryDirStaff)iter.next();
		}
		return null;
	}
	
	public InputStream getTrainingCenterLogo() throws IOException, ManagerBeanException{
		RegistryAttachment attach = obtainTrainingCenterLogo();
		if(attach != null){
			return new ByteArrayInputStream(attach.getData());
		}
		return null;
	}
	
	public RegistryAttachment obtainTrainingCenterLogo() throws ManagerBeanException {
		IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String alias = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
		criteria.addEqualExpression(alias, getTrainingCenter().getId());
		String type = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
		criteria.addEqualExpression(type, RegistryAttachmentType.LOGO);
		Iterator<ITransferObject> iter = registryAttachBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAttachment)iter.next();
		}
		return null;
	}
	
	private String selectedTab;
	
	
	public String getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	public void onGenerateDocument(ActionEvent event){
		generateDocument();
	}
	public void generateDocument(){
		
		ContractPdfController pdfDocument = (ContractPdfController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_PDF_CONTROLLER_NAME);
		// Documento del contrato
		try {
			pdfDocument.setDocumentType(ContractAttachmentType.CONTRACT_DOC_DRAFT);
			pdfDocument.loadDocument(true);
			pdfDocument.saveDocument();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido generar el documento del contrato");
			AonUtil.addErrorMessage(e.getMessage());
		} catch (UnsupportedContractDocumentException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido generar el documento del contrato");
			AonUtil.addErrorMessage(e.getMessage());
		} catch (Exception e){
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido generar el documento del contrato");
			AonUtil.addErrorMessage(e.getMessage());
		}
		
		// Documento de la copia basica
		try {
			pdfDocument.setDocumentType(ContractAttachmentType.BASIC_COPY_DRAFT);
			pdfDocument.loadDocument(true);
			pdfDocument.saveDocument();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido generar el documento de la copia basica");
			AonUtil.addErrorMessage(e.getMessage());
		} catch (UnsupportedContractDocumentException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido generar el documento de la copia basica");
			AonUtil.addErrorMessage(e.getMessage());
		} catch (Exception e){
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido generar el documento de la copia basica");
			AonUtil.addErrorMessage(e.getMessage());
		}
		
		// Documento del anexxo ii de contrato de formacion (421)
		try {
			if( isTrainingContract() && isTrainingCourseDefined() ){
				pdfDocument.setDocumentType(ContractAttachmentType.TRAINING_ANNEX_II);
				pdfDocument.loadDocument(true);
				pdfDocument.saveDocument();
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido generar el documento del anexo II");
			AonUtil.addErrorMessage(e.getMessage());
		} catch (UnsupportedContractDocumentException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido generar el documento del anexo II");
			AonUtil.addErrorMessage(e.getMessage());
		} catch (Exception e){
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido generar el documento del anexo II");
			AonUtil.addErrorMessage(e.getMessage());
		}
		
		generateDirectDebitDocument();
		
		setSelectedTab("attachData");
	}
	
	public void generateDirectDebitDocument(){
		try {
			if( isTrainingCourseDefined() ){
				ContractAttachment attach = obtainDirectDebitReport();
				BeanManager.getManagerBean(ContractAttachment.class).insertOrUpdate(attach);
				ContractAttachController attachController = (ContractAttachController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_ATTACH_CONTROLLER);
				attachController.initializeModel();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido generar el documento de la domiciliacion bancaria");
			AonUtil.addErrorMessage(e.getMessage());
		}
	}

	private ContractAttachment obtainDirectDebitReport() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), ((Contract)this.getTo()).getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.TRAINING_CENTER_DIRECT_DEBIT);
		List<ITransferObject> list = bean.getList(criteria);
		ContractAttachment attach = null;
		if(!list.isEmpty()){
			attach = (ContractAttachment) list.get(0);
		} else {
			attach = new ContractAttachment();
		}
		attach.setContract((Contract) this.getTo());
		attach.setData(getReport(IPayrollConstants.TRAINING_DIRECT_DEBIT_REPORT_KEY));
		attach.setMimeType(MimeType.MIME_PDF);
		attach.setAttachmentType(ContractAttachmentType.TRAINING_CENTER_DIRECT_DEBIT);
		attach.setAttachDate(new Date());
		attach.setDescription( AonUtil.getMessage(IPayrollConstants.BUNDLE_NAME, IPayrollConstants.PAYROLL_TRAINING_CENTER_DIRECT_DEBIT));
		return attach;
	}
	
	@SuppressWarnings("unchecked")
	private byte[] getReport( String report ) {
		try {
			ReportManager reportManager = new ReportManager();
			reportManager.setCollectionProvider( new SingleCollectionProvider(this.getTo()) );
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			reportManager.execute( out, report);
			return out.toByteArray();
		} catch (Throwable e) {
			LOGGER.error(">>>> onReport " + e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}			
	}	
	
	public void onContrataExtensionShow(ActionEvent event){
		ContrataController contrataController = (ContrataController) AonUtil.getRegisteredBean(ISepeConstants.EXTENSION_CONTRATA_CONTROLLER_NAME);
		contrataController.initialize((Contract) this.getTo());
		contrataController.onContrataDataShow(event);
	}
	

//	 * ************************************
//	 * 			DOWNLOAD & UPLOAD METHODS		
//	 * ************************************
	
	public void onDownloadContract( ActionEvent event ) {
		try {
			Contract c = (Contract)this.getModel().getRowData();
			download(c, ContractAttachmentType.CONTRACT_DOC);
		} catch (IOException e) {
			String msg = "Imposible descargar el Contrato. (" +e.getMessage() + ")"; 
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} catch (ManagerBeanException e) {
			String msg = "Imposible descargar el Contrato. (" +e.getMessage() + ")"; 
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	private void download(Contract c, ContractAttachmentType type) throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		ContractAttachment attach = obtainContractAttachDocument(c, type);
		byte[] buffer = attach.getData();
		InputStream in = new ByteArrayInputStream(buffer);
		int bytes = in.read(buffer);
		while (bytes != -1) {
			response.getOutputStream().write(buffer, 0, bytes);
			bytes = in.read(buffer);
		}
		in.close();
		response.setContentType(MimeType.MIME_PDF.getName()); 
		response.flushBuffer();
		context.responseComplete();
	}
	
	public ContractAttachment obtainContractAttachDocument(Contract contract, ContractAttachmentType type) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), contract.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), type);
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				return (ContractAttachment) list.get(0);
			} 
		} catch (ManagerBeanException e) {
			// NADA, el documento se queda vacio
		}
		return null;
	}
	
	public boolean getExistSignedContractDocument(){
		try {
			if(this.getModel().isRowAvailable()){
				Contract c = (Contract)this.getModel().getRowData();
				return obtainContractAttachDocument(c, ContractAttachmentType.CONTRACT_DOC) != null;
			}
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido obtener el documento del contrato. (" +e.getMessage() + ")"; 
			LOGGER.error(msg, e);
		}
		return false;
	}
	
	public boolean getExistSignedBasicCopyDocument(){
		try {
			if(this.getModel().isRowAvailable()){
				Contract c = (Contract)this.getModel().getRowData();
				return obtainContractAttachDocument(c, ContractAttachmentType.BASIC_COPY) != null;
			}
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido obtener el documento del contrato. (" +e.getMessage() + ")"; 
			LOGGER.error(msg, e);
		}
		return false;
	}
	
	
	public String onTrainingCenterDirectDebitReport() throws ManagerBeanException{
		TrainingCenterController tcController = (TrainingCenterController) AonUtil.getRegisteredBean(IPayrollConstants.TRAINING_CENTER_CONTROLLER_NAME);
		try {
			ContractUtils utils = ContractUtils.getInstance();
			Map<String, String> map = utils.getContractDataMap((Contract) this.getTo());
			if(map.get(ContextVariable.TRAINING_COURSE.getName())!=null){
				String courseId = map.get(ContextVariable.TRAINING_COURSE.getName());
				TrainingCourse course = (TrainingCourse) BeanManager.getManagerBean(TrainingCourse.class).get(Integer.parseInt(courseId));
				tcController.select(null, course.getTrainingCenter().getId());
			}
			ReportManager report = (ReportManager) AonUtil.getRegisteredBean("report");
			report.setReportKey("trainingDirectDebit");
			report.setOutputFormat(OutputFormat.PDF);
			return report.onExecute();
		} finally {
			tcController.clearCriteria();
			tcController.initializeModel();
		}
	}
	
	
// ************************************
// ************************************
	public class ContractParams {
		private ContractOption contractOption;
		private ContractType contractType;
		private ContractModelCode contractModelCode;
		private ContractCode contractCode;
		private ContractCode contractTransformCode;
		private QuoteGroup quoteGroup;
		private OccupationType occupationType;
		private Double irpf;
		private ContractCode tc2Code;
		private ContractDuration contractDuration;
		private ContractWorkingDay contractWorkingDay;
		private CNO cno;
		
		private boolean agreementSalaryCheck;
		private boolean agreementSalary;
		private Double grossSalary;
		private Boolean subsidized;
		private TrainingCenter trainingCenter;
		private TrainingCourse trainingCourse;
		private Date trainingStartDate;
		private Date trainingEndDate;
		private String workSchedule;
		private String trainingSchedule;
		
		public boolean isAgreementSalaryCheck() {
			return agreementSalaryCheck;
		}
		public void setAgreementSalaryCheck(boolean agreementSalaryCheck) {
			this.agreementSalaryCheck = agreementSalaryCheck;
		}
		public boolean isAgreementSalary() {
			return agreementSalary;
		}
		public void setAgreementSalary(boolean agreementSalary) {
			this.agreementSalary = agreementSalary;
		}
		public Double getGrossSalary() {
			return grossSalary;
		}
		public void setGrossSalary(Double grossSalary) {
			this.grossSalary = grossSalary;
		}
		public Boolean getSubsidized() {
			return subsidized;
		}
		public void setSubsidized(Boolean subsidized) {
			this.subsidized = subsidized;
		}
		public TrainingCenter getTrainingCenter() {
			return trainingCenter;
		}
		public void setTrainingCenter(TrainingCenter trainingCenter) {
			this.trainingCenter = trainingCenter;
		}
		public TrainingCourse getTrainingCourse() {
			return trainingCourse;
		}
		public void setTrainingCourse(TrainingCourse trainingCourse) {
			this.trainingCourse = trainingCourse;
			setCno(trainingCourse.getCNO());
		}
		public Date getTrainingStartDate() {
			return trainingStartDate;
		}
		public void setTrainingStartDate(Date trainingStartDate) {
			this.trainingStartDate = trainingStartDate;
		}
		public Date getTrainingEndDate() {
			return trainingEndDate;
		}
		public void setTrainingEndDate(Date trainingEndDate) {
			this.trainingEndDate = trainingEndDate;
		}
		public String getWorkSchedule() {
			return workSchedule;
		}
		public void setWorkSchedule(String workSchedule) {
			this.workSchedule = workSchedule;
		}
		public String getTrainingSchedule() {
			return trainingSchedule;
		}
		public void setTrainingSchedule(String trainingSchedule) {
			this.trainingSchedule = trainingSchedule;
		}
		public Double getIrpf() {
			return irpf;
		}
		public void setIrpf(Double irpf) {
			this.irpf = irpf;
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
		}
		public ContractModelCode getContractModelCode() {
			return contractModelCode;
		}
		public void setContractModelCode(ContractModelCode contractModelCode) {
			this.contractModelCode = contractModelCode;
		}
		public ContractCode getContractCode() {
			return contractCode;
		}
		public void setContractCode(ContractCode contractCode) {
			this.contractCode = contractCode;
		}
		public ContractCode getContractTransformCode() {
			return contractTransformCode;
		}
		public void setContractTransformCode(ContractCode contractTransformCode) {
			this.contractTransformCode = contractTransformCode;
		}
		public QuoteGroup getQuoteGroup() {
			return quoteGroup;
		}
		public void setQuoteGroup(QuoteGroup quoteGroup) {
			this.quoteGroup = quoteGroup;
		}
		public OccupationType getOccupationType() {
			return occupationType;
		}
		public void setOccupationType(OccupationType occupationType) {
			this.occupationType = occupationType;
		}
		public ContractCode getTc2Code() {
			return tc2Code;
		}
		public void setTc2Code(ContractCode tc2Code) {
			this.tc2Code = tc2Code;
		}
		public ContractDuration getContractDuration() {
			return contractDuration;
		}
		public void setContractDuration(ContractDuration contractDuration) {
			this.contractDuration = contractDuration;
		}
		public ContractWorkingDay getContractWorkingDay() {
			return contractWorkingDay;
		}
		public void setContractWorkingDay(ContractWorkingDay contractWorkingDay) {
			this.contractWorkingDay = contractWorkingDay;
		}
		public CNO getCno() {
			return cno;
		}
		public void setCno(CNO cno) {
			this.cno = cno;
		}
		
	}
	
}
