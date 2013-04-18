package com.esferalia.aon.ui.payroll.controller.contract;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.SingleCollectionProvider;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.ql.Criteria;
import com.code.aon.report.OutputFormat;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.registry.controller.IRegistryConstants;
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
import com.esferalia.aon.payroll.enumeration.ContractModelCode;
import com.esferalia.aon.payroll.enumeration.ContractOption;
import com.esferalia.aon.payroll.enumeration.ContractType;
import com.esferalia.aon.payroll.enumeration.ContractWorkingDay;
import com.esferalia.aon.payroll.enumeration.OccupationType;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.ui.calendar.controller.CalendarController;
import com.esferalia.aon.ui.payroll.controller.EnterpriseTree;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.IVariablesHandler;
import com.esferalia.aon.ui.payroll.controller.PayrollAppParamsController;
import com.esferalia.aon.ui.payroll.controller.TrainingCenterController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractPdfController.PdfType;
import com.esferalia.aon.ui.payroll.controller.salary.SettleController;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class ContractController extends BasicController implements IVariablesHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContractController.class.getName());
	
	private Enterprise enterprise;
	private List<SelectItem> workPlaces;
	private List<SelectItem> enterpriseCCCs;
	private List<SelectItem> activities;
	private Agreement agreement;
	
	private ContractVariableHandler variableHandler;
	private ContractParams params;

	private boolean showNewContractModal;
	private boolean skipPayrollData;
	private boolean showDocumentIssueWindow;

	
	public ContractParams getParams() {
		if(params==null){
			params = new ContractParams();
		}
		return params;
	}
	public void setParams(ContractParams params) {
		this.params = params;
	}
	public ContractVariableHandler getHandler() {
		if(variableHandler==null){
			variableHandler = new ContractVariableHandler(this);
		}
		return variableHandler;
	}
	public void setHandler(ContractVariableHandler handler) {
		this.variableHandler = handler;
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
	public boolean isShowDocumentIssueWindow() {
		return showDocumentIssueWindow;
	}
	public void setShowDocumentIssueWindow(boolean showDocumentIssueWindow) {
		this.showDocumentIssueWindow = showDocumentIssueWindow;
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
		
	public void onShowNewContractModal(ActionEvent event) {
		setShowNewContractModal(true);
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
	
	public void onChangeAgreement(LookupChangeEvent event){
		if (event.getNewValue() == null || event.getNewValue().equals("")) {
			Contract contract = (Contract) getTo();
			contract.setAgreementLevelCategory(null);
		}
	}
	
	public void onChangeAgreementLevelCategory(ActionEvent event){
		// TODO if contract.isNew then pedir salario bruto, y si es asi como obtenerlo del convenio?
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
			contract.setCategoryDescription(((CNO)event.getNewValue()).getTitle());
		}
	}
	
	public boolean isTrainingContract(){
		PayrollUtils utils = new PayrollUtils();
		Map<String, String> map = utils.getContractDataMap((Contract) this.getTo());
		return map.get(ContextVariable.TC2.getName())!=null && ContractCode.getContractCodeByValue(map.get(ContextVariable.TC2.getName()))==ContractCode.C421;
	}

	public boolean isTrainingCenterDefined(){
		PayrollUtils utils = new PayrollUtils();
		Map<String, String> map = utils.getContractDataMap((Contract) this.getTo());
		return map.get(ContextVariable.TRAINING_CENTER.getName())!=null;
	}

	public boolean isTrainingCourseDefined(){
		PayrollUtils utils = new PayrollUtils();
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
	
	private String selectedTab;
	
	
	
	public String getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	public void onShowDocumentIssueWindow(ActionEvent event){
		setShowDocumentIssueWindow(false);
		
		ContractPdfController pdfDocument = (ContractPdfController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_PDF_CONTROLLER_NAME);
		try {
			if( !getExistSignedContractDocument() ){
				pdfDocument.setDocumentType(PdfType.CONTRACT);
				pdfDocument.generateDocument();
				pdfDocument.onDocumentSave(event);
			}
			if( !getExistSignedBasicCopyDocument() ){
				pdfDocument.setDocumentType(PdfType.BASIC_COPY);
				pdfDocument.generateDocument();
				pdfDocument.onDocumentSave(event);
			}
			if( isTrainingContract() && isTrainingCourseDefined() ){
				pdfDocument.setDocumentType(PdfType.ANNEX);
				pdfDocument.generateDocument();
				pdfDocument.onDocumentSave(event);
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		} catch (UnsupportedContractDocumentException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		}
		
		try {
			if( isTrainingCourseDefined() ){
				ContractAttachment attach = obtainDirectDebitReport();
				attach.setDescription( AonUtil.getMessage(IPayrollConstants.BUNDLE_NAME, "payroll_trainingCenter_directDebit"));
				try {
					BeanManager.getManagerBean(ContractAttachment.class).insertOrUpdate(attach);
					ContractAttachController attachController = (ContractAttachController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_ATTACH_CONTROLLER);
					attachController.initializeModel();
				} catch (ManagerBeanException e) {
					LOGGER.error(e.getMessage(), e);
					AonUtil.addErrorMessage(e.getMessage());
					throw new AbortProcessingException(e.getMessage());
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		}
		
		setSelectedTab("attachData");
		
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
		attach.setData(getReport("trainingDirectDebit"));
		attach.setMimeType(MimeType.MIME_PDF);
		attach.setAttachmentType(ContractAttachmentType.TRAINING_CENTER_DIRECT_DEBIT);
		attach.setAttachDate(new Date());
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
	

//	 * ************************************
//	 * 			DOWNLOAD & UPLOAD METHODS		
//	 * ************************************
	
	public void onDownloadContract( ActionEvent event ) {
		try {
			Contract c = (Contract)this.getModel().getRowData();
			download(c, ContractAttachmentType.CONTRACT_DOCUMENT);
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
				return obtainContractAttachDocument(c, ContractAttachmentType.CONTRACT_DOCUMENT) != null;
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
	
	public void onContractDocumentShow( ActionEvent event ) {
		ContractPdfController controller = (ContractPdfController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_PDF_CONTROLLER_NAME);
		controller.onContractDocumentShow(event);
	}
	
	
	
	public String onTrainingCenterDirectDebitReport() throws ManagerBeanException{
		TrainingCenterController tcController = (TrainingCenterController) AonUtil.getRegisteredBean(IPayrollConstants.TRAINING_CENTER_CONTROLLER_NAME);
		try {
			PayrollUtils utils = new PayrollUtils();
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
	 
//	 * ************************************
//	 * 			TREE METHODS		
//	 * ************************************
	
	public void onShowVariables( ActionEvent event ) {
		try {
			Contract to = (Contract) getTo();
			IController c = FormUtil.getController(IPayrollConstants.CONTRACT_DATA_CONTROLLER);
			c.onEditSearch(event);
			c.getCriteria().addEqualExpression(c.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), to.getId());
			c.onSearch(event);
			this.initializeVariables(event);
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar las variables del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}						
	}
	
	public void onShowPayments( ActionEvent event ) {
		ContractPaymentController c = (ContractPaymentController) FormUtil.getController(IPayrollConstants.CONTRACT_PAYMENT_CONTROLLER);
		c.reset(false);
		c.initialize();
	}
	
	public void onShowDeductions( ActionEvent event ) {
		try {
			Contract to = (Contract) getTo();
			ContractDeductionController c = (ContractDeductionController) FormUtil.getController(IPayrollConstants.CONTRACT_DEDUCTION_CONTROLLER);
			c.reset(false);
			c.onEditSearch(event);
			c.getCriteria().addEqualExpression(c.getFieldName(IEntityAlias.CONTRACT_DEDUCTION_CONTRACT_ID), to.getId());
			c.onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar las deducciones del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}						
	}
	public void onShowEmbargos( ActionEvent event ) {
		try {
			Contract to = (Contract) getTo();
			ContractEmbargoController c = (ContractEmbargoController) FormUtil.getController(IPayrollConstants.CONTRACT_EMBARGO_CONTROLLER);
			c.onEditSearch(event);
			c.getCriteria().addEqualExpression(c.getFieldName(IEntityAlias.CONTRACT_EMBARGO_CONTRACT_ID), to.getId());
			c.onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar los embargos del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}						
	}
	public void onShowBonus( ActionEvent event ) {
		try {
			Contract to = (Contract) getTo();
			ContractBonusController c = (ContractBonusController) FormUtil.getController(IPayrollConstants.CONTRACT_BONUS_CONTROLLER);
			c.onEditSearch(event);
			c.getCriteria().addEqualExpression(c.getFieldName(IEntityAlias.CONTRACT_BONUS_CONTRACT_ID), to.getId());
			c.onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar las bonificaciones del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}						
	}
	public void onShowDocuments( ActionEvent event ) {
		
	}
	 
	public void onEdit( ActionEvent event ) {
		try {
			EnterpriseTree tree = (EnterpriseTree) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_TREE_CONTROLLER_NAME);
			select( event, tree.getContract() );
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onEdit exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}						
	}    
	
	public void onLoadCalendar( ActionEvent event ) {
		// TODO implementar la busqueda del calendario. si la entidad no tiene calendario, 
		// buscar el calendario en sus entidades superiores: contract -> workplace -> enterprise -> agreement
		Contract c = (Contract) getTo();
		CalendarController controller = (CalendarController) AonUtil.getRegisteredBean(ICompanyConstants.CALENDAR_CONTROLLER_NAME);
		controller.setEnterpriseName(c.getWorkPlace().getEnterprise().getRegistry().getFullName());
		controller.setWorkPlaceName(c.getWorkPlace().getDescription());
		controller.setContractName(c.getPerson().getFullName());
		controller.setCalendarId(c.getCalendar().getId());
		controller.onInitialize(event);
	}	
	
	public void onShowIrpfData( ActionEvent event ) {
		Contract c = (Contract) getTo();
		try {
			IrpfDataController controller = (IrpfDataController) AonUtil.getRegisteredBean(IPayrollConstants.IRPF_DATA_CONTROLLER_NAME);
			controller.onEditSearch(event);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(controller.getManagerBean().getFieldName(IEntityAlias.IRPF_DATA_CONTRACT_ID),c.getId());
			controller.clearCriteria();
			controller.setCriteria(criteria);
			controller.onSearch(event);
			if(controller.getRowCount()<=0){
				controller.onReset(event);
			} else {
				controller.getModel().setRowIndex(0);
				controller.onSelect(event);
			}
			controller.onCalculateIrpf(event);
			controller.setBackAction(IPayrollConstants.CONTRACT_FORM_TREE);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onIrpfCalculate exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void onShowSettle( ActionEvent event ) {
		if(getTo()==null){
			String msg = "Error al obtener los datos de contrato.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		SettleController controller = (SettleController) AonUtil.getRegisteredBean(IPayrollConstants.SETTLE_CONTROLLER_NAME);
		controller.onSelectContract(event);
	}
	
	public void onShowContractLeave( ActionEvent event ) {
		Contract c = (Contract) getTo();
		if(c==null){
			String msg = "Error al obtener los datos de contrato.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		ContractLeaveController controller = (ContractLeaveController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_LEAVE_CONTROLLER_NAME);
		controller.setContract(c);
		controller.onReset(event);
		controller.initialize();
		controller.createLeaveReportSuggest();
	}
	
	public void onEditPerson( ActionEvent event ) {
		try {
			BasicController controller = (BasicController) FormUtil.getController(IRegistryConstants.PERSON_CONTROLLER_NAME);
			controller.onLoad(event, ((Contract)this.getTo()).getPerson().getId(), "contract_formTree", "contract.onPersonBack");
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onEditPerson exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}				
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
	
//	public void onShowContrataData(ActionEvent event){
//		ContractContrataController controller = (ContractContrataController) AonUtil.getRegisteredBean("contractContrata");
//		controller.initialize((Contract) this.getTo());
//		controller.onContractaDataShow(event);
//	}
	

	
	

	
//	 * ************************************
//	 * 			VARIABLES (contractData)		
//	 * ************************************	
	@Override
	public List<?> expressionContext(Object suggest) {
		return getHandler().expressionContext(suggest);
	}
	@Override
	public IManagerBean getVariableManagerBean() throws ManagerBeanException {
		return getHandler().getVariableManagerBean();
	}
	@Override
	public void initializeVariables(ActionEvent event) {
		getHandler().initializeVariables(event);
	}
	@Override
	public void resetVariable() {
		getHandler().resetVariable();
	}
	
	


	public class ContractParams {
		private ContractOption contractOption;
		private ContractType contractType;
		private ContractModelCode contractModelCode;
		private ContractCode contractCode;
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
		
		public boolean isTrainingContract(){
			return getContractModelCode()!=null && getContractModelCode().getCode()==ContractCode.C421;
		}
		
	}
	
}
