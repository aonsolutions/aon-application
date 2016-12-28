package com.esferalia.aon.ui.payroll.controller.contract;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.SingleCollectionProvider;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.report.ReportException;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.ui.webmail.controller.MessageController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.ContractPdfField;
import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contract.pdf.annex.ModelPE230;
import com.esferalia.aon.file.payroll.contract.pdf.basicCopy.BasicCopy;
import com.esferalia.aon.file.payroll.contract.pdf.enterpriseCertificate.EnterpriseCertificate;
import com.esferalia.aon.file.payroll.contract.pdf.extension.Extension;
import com.esferalia.aon.file.payroll.contract.pdf.model.ClausulasModel;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractClause;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.certificados.TLDCAUSS;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.CertificadoEmpresa;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.file.ContractPdfWriter;
import com.esferalia.aon.ui.payroll.utils.ContractUtils;
import com.esferalia.aon.ui.payroll.utils.PayrollEmailUtil;
import com.esferalia.aon.ui.payroll.utils.PdfUtils;
import com.esferalia.aon.ui.sepe.controller.CertificadosCollectionsController;
import com.esferalia.aon.ui.sepe.controller.ContrataContratosController;
import com.esferalia.aon.ui.sepe.controller.ContrataProrrogasController;
import com.esferalia.aon.ui.sepe.controller.ContrataTransformacionesController;
import com.esferalia.aon.ui.sepe.controller.IContrataController;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.file.CertificadosWriter;

public class ContractPdfController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractPdfController.class.getName());
	private static final String IMAGE_URL_PREFIX0 = ".contractImage";
	private static final String IMAGE_URL_PREFIX1 = "?model=";
	private static final String IMAGE_URL_PREFIX2 = "&width=";
	private static final String IMAGE_URL_PREFIX3 = "&height=";
	private static final double FACTOR_1X = 1.2;
	private static final double FACTOR_2X = 1.4;
	private static final double FACTOR_3X = 1.6;
	private static final double FACTOR_4X = 1.8;

	private Integer documentPage;
	private int zoomFactor;
	private Contract contract;
	private ContractPdfWriter contractPdfWriter;
	private String imageUrl;
	private ModelOption modelOption;
	private ContractCode code;
	private ContractAttachment contractPdfDraft;	
	private String backAction;
	private ContractAttachmentType documentType;
	private List<IContrataParams> contrataParams;
	private TLDCAUSS suspensionCause;
	
	private boolean showDocumentGenerationWindow;
	private boolean readOnly;
	
	public boolean isShowDocumentGenerationWindow() {
		return showDocumentGenerationWindow;
	}
	public void setShowDocumentGenerationWindow(boolean showDocumentGenerationWindow) {
		this.showDocumentGenerationWindow = showDocumentGenerationWindow;
	}
	
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public Integer getDocumentPage() {
		return documentPage;
	}
	public void setDocumentPage(Integer documentPage) {
		this.documentPage = documentPage;
	}
	public int getFactorizedValue(double value){
		if(getZoomFactor()==0){
			return (int)value;
		} else if(getZoomFactor()==1){
			return (int)(value*FACTOR_1X);
		} else if(getZoomFactor()==2){
			return (int)(value*FACTOR_2X);
		} else if(getZoomFactor()==3){
			return (int)(value*FACTOR_3X);
		} else if(getZoomFactor()==4){
			return (int)(value*FACTOR_4X);
		}
		return (int)value;
	}
	public double getFactorizedValue(){
		if(getZoomFactor()==1){
			return 0.86;
		} else if(getZoomFactor()==2){
			return 1.0;
		} else if(getZoomFactor()==3){
			return 1.15;
		} else if(getZoomFactor()==4){
			return 1.3;
		}
		return 1;
	}
	
	public ContractAttachmentType getDocumentType() {
		return documentType;
	}
	public void setDocumentType(ContractAttachmentType documentType) {
		this.documentType = documentType;
	}
	public int getZoomFactor() {
		return zoomFactor;
	}
	public void setZoomFactor(int zoomFactor) {
		this.zoomFactor = zoomFactor;
	}
	public Integer getDocumentWidth() {
		return getFactorizedValue(getContractPdfWriter().getDocumentWidth());
	}
	public Integer getDocumentHeight() {
		return getFactorizedValue(getContractPdfWriter().getDocumentHeight());
	}
	public int getNumberOfDocumentPages() {
		return getContractPdfWriter().getNumberOfDocumentPages();
	}
	public String backAction() {
		return backAction;
	}
	public String getBackAction() {
		return backAction;
	}
	public void setBackAction(String backAction) {
		this.backAction = backAction;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public ContractPdfWriter getContractPdfWriter() {
		if(contractPdfWriter==null){
			contractPdfWriter = new ContractPdfWriter();
		}
		return contractPdfWriter;
	}
	public void setContractPdfWriter(ContractPdfWriter contractPdfWriter) {
		this.contractPdfWriter = contractPdfWriter;
	}
	public ContractCode getCode() {
		return code;
	}
	public void setCode(ContractCode code) {
		this.code = code;
	}
	
	public ModelOption getModelOption() {
		return modelOption;
	}
	public void setModelOption(ModelOption modelOption) {
		this.modelOption = modelOption;
	}
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	private ContractAttachment getContractPdfDraft() {
		return contractPdfDraft;
	}
	public void setContractPdfDraft(ContractAttachment contractPdfDraft) {
		this.contractPdfDraft = contractPdfDraft;
	}
	public boolean isNevv(){
		return getContractPdfDraft()==null||getContractPdfDraft().getId()==null;
	}
	public ContractAttachmentType getContractPdfType(){
		return ContractAttachmentType.CONTRACT_DOC_DRAFT;
	}
	public ContractAttachmentType getBasicCopyPdfType(){
		return ContractAttachmentType.BASIC_COPY_DRAFT;
	}
	public ContractAttachmentType getAnnexIIPdfType(){
		return ContractAttachmentType.TRAINING_ANNEX_II;
	}
	public ContractAttachmentType getExtensionPdfType(){
		return ContractAttachmentType.EXTENSION_DOC_DRAFT;
	}
	public ContractAttachmentType getEnterpriseCertificatePdfType(){
		return ContractAttachmentType.ENTERPRISE_CERTIFICATE_DOC_DRAFT;
	}
	public List<IContrataParams> getContrataParams() {
		return contrataParams;
	}
	public void setContrataParams(List<IContrataParams> contrataParams) {
		this.contrataParams = contrataParams;
	}
	public TLDCAUSS getContractSuspensionCause() {
		return this.suspensionCause;
	}
	public void setContractSuspensionCause(TLDCAUSS suspensionCause) {
		this.suspensionCause = suspensionCause;
	}
	public List<SelectItem> getTLDCAUSSCodeList() {
		CertificadosCollectionsController controller = new CertificadosCollectionsController();
		for(SelectItem item: controller.getTLDCAUSSCodeList()){
			TLDCAUSS e = (TLDCAUSS) item.getValue();
			item.setLabel(e.getCode() + " - " + item.getLabel());
		}
		return controller.getTLDCAUSSCodeList();
	}
	
	private void initialize() {
		initialize(true);
	}
	private void initialize(boolean loadDocumentType) {
		setContract((Contract) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER).getTo());
		String tc2 = ContractUtils.getInstance().getContractDataMap(getContract()).get(ContextVariable.TC2.getName());
		setCode(ContractCode.getContractCodeByValue(tc2));
		if(loadDocumentType){
			setContractPdfDraft( obtainContractPdfDraft() );
		}
		String code = ContractUtils.getInstance().getContractDataMap(getContract()).get(ContextVariable.CONTRACT_END_CODE.getName());
		setContractSuspensionCause(TLDCAUSS.getEnumByValue(code));
	}
	
	private ContractAttachment obtainContractPdfDraft() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), getContract().getId());
			if(getDocumentType()!=null){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), getDocumentType());
			} else {
				criteria.addNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE));
			}
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				return (ContractAttachment) list.get(0);
			}
		} catch (ManagerBeanException e) {
			// NADA, se devuelve una nueva instancia
		}
		return new ContractAttachment();
	}
	
	public String getImageUrl() {
		StringBuilder builder = null;
		if(getDocumentPage()<=3){
			builder = new StringBuilder(getDocumentPage().toString());
		} else {
			builder = new StringBuilder(getModelOption().getPageNumber().toString());
		}
		builder.append(IMAGE_URL_PREFIX0);
		builder.append(IMAGE_URL_PREFIX1);
		if(getDocumentType()==ContractAttachmentType.CONTRACT_DOC_DRAFT){
			builder.append(getModelOption().getPdfModel());
		} else if(getDocumentType()==ContractAttachmentType.BASIC_COPY_DRAFT){
			builder.append(BasicCopy.BASIC_COPY_NAME);
		} else if(getDocumentType()==ContractAttachmentType.TRAINING_ANNEX_II){
			builder.append(ModelPE230.MODEL_NAME);
		} else if(getDocumentType()==ContractAttachmentType.EXTENSION_DOC_DRAFT){
			builder.append(Extension.EXTENSION_NAME);
		} else if(getDocumentType()==ContractAttachmentType.ENTERPRISE_CERTIFICATE_DOC_DRAFT){
			builder.append(EnterpriseCertificate.ENTERPRISE_CERTIFICATE_NAME);
		}
		builder.append(IMAGE_URL_PREFIX2);
		builder.append(getDocumentWidth());
		builder.append(IMAGE_URL_PREFIX3);
		builder.append(getDocumentHeight());
		imageUrl = builder.toString();
		return imageUrl;
	}
	
	public void beforeDocumentShow() {
		ContractUtils utils = ContractUtils.getInstance();
//		String tc2 = utils.getContractDataMap(getContract()).get(ContextVariable.TC2.getName());
//		setCode(ContractCode.getContractCodeByValue(tc2));
		String option = utils.getContractInfoMap(getContract()).get(ContractVariable.CONTRACT_MODEL_OPTION.getValue());
		String indefinite = utils.getContractDataMap(getContract()).get(ContextVariable.INDEFINITE.getName());
		String fullTime = utils.getContractDataMap(getContract()).get(ContextVariable.FULL_TIME.getName());
		if(StringUtils.isNotBlank(option)){
			setModelOption(ModelOption.valueOf(option));
		} else {
			setModelOption(null);
		}	
		if( getDocumentType()==ContractAttachmentType.CONTRACT_DOC_DRAFT && getCode()!=null ){
			if(new Boolean(indefinite)){
				
			}
			if(new Boolean(fullTime)){
				
			}
		} else if( getDocumentType()==ContractAttachmentType.TRAINING_ANNEX_II ){
			
		}
	}
	
	public void onContractDocumentPreview( ActionEvent event ) {
		setReadOnly(true);
		prepareContractDocumentShow();
	}
	public void onChangeContractDocument( ActionEvent event ) {
		setReadOnly(true);
		prepareContractDocumentShow();
	}
	public void onContractDocumentShow( ActionEvent event ) {
		setReadOnly(false);
		prepareContractDocumentShow();
	}
	private void prepareContractDocumentShow() {
		setZoomFactor(2);
		setDocumentPage(1);
		initialize();
		if(getDocumentType()==null){
			setContractPdfWriter(null);
			setImageUrl(null);
			setModelOption(null);
			setContractPdfDraft(null);	
		} else {
			try {
				loadDocument(true);
				createPdfThumbnail();
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage());
			} catch (UnsupportedContractDocumentException e) {
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage());
			}
		}
	}
	
	public void loadDocument(boolean forceRefresh) throws UnsupportedContractDocumentException, IOException{
//		initialize();
		setContractPdfWriter(null);
		if(getContract()==null || getContract().getId()==null){
			String msg = "Error al obtener los datos de contrato";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		beforeDocumentShow();
		if(getDocumentType()==ContractAttachmentType.CONTRACT_DOC_DRAFT && getModelOption()==null){
			String msg = "Modelo de contrato no reconocido.";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		
		setContrataParams(new LinkedList<IContrataParams>());
		IContrataController contrataController = null;
		IContrataParams newParams = null;
		
		ContractController contractController = (ContractController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_CONTROLLER);
		if(!contractController.isContractInternship(getContract())
				&& !contractController.isContractRetaQuote(getContract())){
			
			// always load INITIAL CONTRACT contrata data
			contrataController = (ContrataContratosController) AonUtil.getRegisteredBean(ISepeConstants.CONTRACT_CONTRATA_CONTROLLER_NAME);
			contrataController.initialize(getContract());
			contrataController.onContrataDataShow(null);
			if(contrataController.getGeneratedFile()!=null && (contrataController.getGeneratedFile().getSize()>0)){
				newParams = contrataController.getHandler().getParams();
			} else {
				newParams = new ContrataContratoParams();
			}
			getContrataParams().add(newParams);
			
			// load EXTENSION contrata data
			if(getDocumentType()==ContractAttachmentType.EXTENSION_DOC_DRAFT){
				contrataController = (ContrataProrrogasController) AonUtil.getRegisteredBean(ISepeConstants.EXTENSION_CONTRATA_CONTROLLER_NAME);
				contrataController.initialize(getContract());
				contrataController.onContrataDataShow(null);
				getContrataParams().add(contrataController.getHandler().getParams());
			}
			
			// load TRANSFORM contrata data
			if(code!=null && isTransformedContract(code) && getDocumentType()==getContractPdfType()){
				contrataController = (ContrataTransformacionesController) AonUtil.getRegisteredBean(ISepeConstants.TRANSFORM_CONTRATA_CONTROLLER_NAME);
				contrataController.initialize(getContract());
				contrataController.onContrataDataShow(null);
				getContrataParams().add(contrataController.getHandler().getParams());
			}
		}
		
		loadPdfDocument(forceRefresh);
	}
	
	private boolean isTransformedContract(ContractCode contractCode) {
		return contractCode!=null && ArrayUtils.contains(ISepeConstants.AVAILABLE_TRANSFORM_CODE_COMMUNICATION, contractCode.getValue());
	}
	
	
	private void loadPdfDocument(boolean forceRefresh) throws UnsupportedContractDocumentException, IOException{
		if(getDocumentType()==ContractAttachmentType.CONTRACT_DOC_DRAFT){
			if(forceRefresh || getContractPdfDraft()==null || getContractPdfDraft().getId()==null){
				getContractPdfWriter().loadNewPdf(getModelOption().getPdfModel(), getContract(), getContrataParams());
				completeNewPdfFields(ContractAttachmentType.CONTRACT_DOC_DRAFT);
			} else {
				getContractPdfWriter().loadExistingPdf(getContractPdfDraft(), getContract());
			}
		} else if(getDocumentType()==ContractAttachmentType.BASIC_COPY_DRAFT){
			if(forceRefresh || getContractPdfDraft()==null || getContractPdfDraft().getId()==null){
				getContractPdfWriter().loadNewPdf(BasicCopy.BASIC_COPY_NAME, getContract(), getContrataParams());
			} else {
				getContractPdfWriter().loadExistingPdf(BasicCopy.BASIC_COPY_NAME, getContractPdfDraft());
			}
		} else if(getDocumentType()==ContractAttachmentType.TRAINING_ANNEX_II){
			if(forceRefresh || getContractPdfDraft()==null || getContractPdfDraft().getId()==null){
				getContractPdfWriter().loadNewPdf(ModelPE230.MODEL_NAME, getContract(), getContrataParams());
				completeNewPdfFields(ContractAttachmentType.TRAINING_ANNEX_II);
			} else {
				getContractPdfWriter().loadExistingPdf(ModelPE230.MODEL_NAME, getContractPdfDraft());
			}
		} else if(getDocumentType()==ContractAttachmentType.EXTENSION_DOC_DRAFT){
			if(forceRefresh || getContractPdfDraft()==null || getContractPdfDraft().getId()==null){
				getContractPdfWriter().loadNewPdf(Extension.EXTENSION_NAME, getContract(), getContrataParams());
				completeNewPdfFields(ContractAttachmentType.EXTENSION_DOC_DRAFT);
			} else {
				getContractPdfWriter().loadExistingPdf(Extension.EXTENSION_NAME, getContractPdfDraft());
			}
		} else if(getDocumentType()==ContractAttachmentType.CONTRACT_CLAUSES){
			if(forceRefresh || getContractPdfDraft()==null || getContractPdfDraft().getId()==null){
				getContractPdfWriter().loadNewPdf(ClausulasModel.MODEL_NAME, getContract(), getContrataParams());
				completeNewPdfFields(ContractAttachmentType.CONTRACT_CLAUSES);
			} else {
				getContractPdfWriter().loadExistingPdf(ClausulasModel.MODEL_NAME, getContractPdfDraft());
			}
		} else if(getDocumentType()==ContractAttachmentType.ENTERPRISE_CERTIFICATE_DOC_DRAFT){
			if(forceRefresh || getContractPdfDraft()==null || getContractPdfDraft().getId()==null){
				getContractPdfWriter().loadNewPdf(EnterpriseCertificate.ENTERPRISE_CERTIFICATE_NAME, getContract(), getEnterpriseCertificate());
				completeNewPdfFields(ContractAttachmentType.ENTERPRISE_CERTIFICATE_DOC_DRAFT);
			} else {
				getContractPdfWriter().loadExistingPdf(EnterpriseCertificate.ENTERPRISE_CERTIFICATE_NAME, getContractPdfDraft());
			}
		}
	}
	
	private List<CertificadoEmpresa> getEnterpriseCertificate(){
		CertificadosWriter writer = new CertificadosWriter();
		try {
			List<CertificadoEmpresa> list = new LinkedList<CertificadoEmpresa>();
			list.add(writer.createCertificadoEmpresaType(getContract(), getContractSuspensionCause()!=null?getContractSuspensionCause().getCode():null));
			
			return list;
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	// TODO  
	private void completeNewPdfFields(ContractAttachmentType attachType) {
		ContractUtils utils = ContractUtils.getInstance();
//		ContractClausesController clausesController = (ContractClausesController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_CLAUSES_CONTROLLER);
		if(attachType==ContractAttachmentType.CONTRACT_DOC_DRAFT){
//			if(StringUtils.isNotBlank(clausesController.getCustomClauses()) && getContractPdfWriter().getPdfDocument().getPdfFieldsMap().get(AbstractContractModel.FieldName.ADDITIONAL_CLAUSES.getValue())!=null){
//				if(clausesController.getCustomClauses().length()>50){
//					getContractPdfWriter().getPdfDocument().getPdfFieldsMap().get(AbstractContractModel.FieldName.ADDITIONAL_CLAUSES.getValue()).setValue("Segun anexo adjunto");
//				} else {
//					getContractPdfWriter().getPdfDocument().getPdfFieldsMap().get(AbstractContractModel.FieldName.ADDITIONAL_CLAUSES.getValue()).setValue(clausesController.getCustomClauses());
//				}
//			}
//			if(contractController.getParams().getContractCode()==ContractCode.C421){
//				getContractPdfWriter().getPdfDocument().getPdfFieldsMap().get("jornhoraefec").setValue(contractController.getParams().getWorkSchedule());
//			}
		} else if(attachType==ContractAttachmentType.TRAINING_ANNEX_II) {
			String workSchedule = utils.getContractInfoMap(getContract()).get(ContractVariable.TRAINING_SCHEDULE.getValue());
			((ContractPdfField) getContractPdfWriter().getPdfDocument().getPdfFieldsMap().get(ModelPE230.PE230_TRAINING_COURSE_SCHEDULE)).setValue(workSchedule);
		} else if(attachType==ContractAttachmentType.CONTRACT_CLAUSES) {
//			getContractPdfWriter().getPdfDocument().getPdfFieldsMap().get(Clauses.CLAUSES_CONTENT).setValue(clausesController.getCustomClauses());
		}
	}
	
	private void createPdfThumbnail() throws IOException, UnsupportedContractDocumentException{
		String fileName = "";
		if(getDocumentType()==ContractAttachmentType.CONTRACT_DOC_DRAFT){
			fileName = getModelOption().getPdfModel()+".pdf"; 
		} else if(getDocumentType()==ContractAttachmentType.BASIC_COPY_DRAFT){
			fileName = BasicCopy.BASIC_COPY_NAME+".pdf"; 
		} else if(getDocumentType()==ContractAttachmentType.TRAINING_ANNEX_II){
			fileName = ModelPE230.MODEL_NAME+".pdf"; 
		} else if(getDocumentType()==ContractAttachmentType.EXTENSION_DOC_DRAFT){
			fileName = Extension.EXTENSION_NAME+".pdf"; 
		} else if(getDocumentType()==ContractAttachmentType.ENTERPRISE_CERTIFICATE_DOC_DRAFT){
			fileName = EnterpriseCertificate.ENTERPRISE_CERTIFICATE_NAME+".pdf"; 
		}
		URL url = getContractPdfWriter().getContractDocumentUrl(fileName);
		if(getDocumentPage()<=3){
			PdfUtils.createPdfWallpaper(url, getDocumentPage(), getDocumentWidth().intValue(), getDocumentHeight().intValue());
		} else {
			PdfUtils.createPdfWallpaper(url, getModelOption().getPageNumber(), getDocumentWidth().intValue(), getDocumentHeight().intValue());
		}
	}
	
	public void onChangeZoomFactor( ActionEvent event ) throws IOException, UnsupportedContractDocumentException {
		for(ContractPdfField field: (Collection<ContractPdfField>)getContractPdfWriter().getContractPdfFields()){
			field.setZoomFactor(getZoomFactor());
		}
		createPdfThumbnail();
	}
	public void onNextDocumentPage( ActionEvent event ) throws IOException, UnsupportedContractDocumentException {
		setDocumentPage(getDocumentPage()+1);
		createPdfThumbnail();
	}
	public void onPreviousDocumentPage( ActionEvent event ) throws IOException, UnsupportedContractDocumentException {
		setDocumentPage(getDocumentPage()-1);
		createPdfThumbnail();
	}
	public void onFirstDocumentPage( ActionEvent event ) throws IOException, UnsupportedContractDocumentException {
		setDocumentPage(1);
		createPdfThumbnail();
	}
	public void onLastDocumentPage( ActionEvent event ) throws IOException, UnsupportedContractDocumentException {
		setDocumentPage(getNumberOfDocumentPages());
		createPdfThumbnail();
	}
	public boolean isFirstDocumentPage() {
		return getDocumentPage().equals(1);
	}
	public boolean isLastDocumentPage() {
		return getDocumentPage().equals(getNumberOfDocumentPages());
	}
	
	public void onDocumentSave(ActionEvent event) {
		saveDocument();
	}
	
	public void saveDocument() {
		saveDocument(getContractPdfWriter().buildPdf(false), getDocumentType());
	}

	public void saveDocument(byte[] data, ContractAttachmentType type) {
		try {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			ContractAttachment attach = obtainContractPdfDocument(type);
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			attach.setContract(getContract());
			attach.setData(data);
			attach.setAttachDate(new Date());
			attach.setAttachmentType(type);
			attach.setMimeType(MimeType.MIME_PDF);
			attach.setDescription(type.getName(locale));
			bean.insertOrUpdate(attach);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AbortProcessingException(e);
		}
	}
	
	private ContractAttachment obtainContractPdfDocument(ContractAttachmentType type) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), getContract().getId());
		if(getDocumentType()!=null){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), type);
		} else {
			criteria.addNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE));
		}
		
		List<ITransferObject> list = bean.getList(criteria);
		if(list.isEmpty() || list.size()>1){
			ContractAttachment attach = new ContractAttachment();
			attach.setAttachmentType(type);
			return attach;
		} else {
			return (ContractAttachment) list.get(0);
		}
	}
	
	public void onDocumentReload(ActionEvent event){
		try {
			loadDocument(true);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		} catch (UnsupportedContractDocumentException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		}
	}
	
	public void onDownloadPdf( ActionEvent event ) {
		try {
			download();
		} catch (IOException e) {
			String msg = "No se ha podido descargar el documento. (" +e.getMessage() + ")"; 
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	private void download() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		byte[] buffer = getContractPdfWriter().buildPdf(true);
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
	
	////////////////////////////////////
	// DOCUMENTS GENERATION
	////////////////////////////////////
	
	private List<SelectItem> availableDocumentList;
	
	private ContractAttachmentType[] selectedDocuments;

	private Map<ContractAttachmentType, byte[]> generatedDocumentMap;
	
	public ContractAttachmentType[] getSelectedDocuments() {
		return selectedDocuments;
	}
	public void setSelectedDocuments(ContractAttachmentType[] selectedDocuments) {
		this.selectedDocuments = selectedDocuments;
	}

	public boolean isGeneratonFinished(){
		return generatedDocumentMap!=null && generatedDocumentMap.size()>0;
	}
	
	public int getAvailableDocumentCount(){
		return getAvailableDocumentList().size();
	}
	
	public boolean isGenerationAvailable(){
		return ArrayUtils.contains(ISepeConstants.AVAILABLE_CONTRACT_MODEL_OPTIONS, modelOption) || 
				(getContract().getEndDate()!=null && getContractSuspensionCause()!=null);
	}
	
	public boolean isEnterpriseCertificateSelected(){
		return ArrayUtils.contains(selectedDocuments, ContractAttachmentType.ENTERPRISE_CERTIFICATE_DOC_DRAFT);	
	}
	
	public List<SelectItem> getAvailableDocumentList(){
		ContractController controller =  (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
//		if(availableDocumentList==null){
			availableDocumentList = new LinkedList<SelectItem>();
			SelectItem item = null;
			
			if(getModelOption()!=null && ArrayUtils.contains(ISepeConstants.AVAILABLE_CONTRACT_MODEL_OPTIONS, getModelOption())){
				if(controller.isExtendedContract()){
					item = new SelectItem(ContractAttachmentType.EXTENSION_DOC_DRAFT, ContractAttachmentType.EXTENSION_DOC_DRAFT.getName(AonUtil.getCurrentLocale()));
					availableDocumentList.add(item);
				}
				ContractUtils utils = ContractUtils.getInstance();
				item = new SelectItem(ContractAttachmentType.CONTRACT_DOC_DRAFT, ContractAttachmentType.CONTRACT_DOC_DRAFT.getName(AonUtil.getCurrentLocale()));
				availableDocumentList.add(item);
				if(!controller.isInternship()){
					item = new SelectItem(ContractAttachmentType.BASIC_COPY_DRAFT, ContractAttachmentType.BASIC_COPY_DRAFT.getName(AonUtil.getCurrentLocale()));
					availableDocumentList.add(item);
				}
				if( utils.isTrainingContract(getContract(), getCode()) && utils.getContractInfoMap(getContract()).get(ContractVariable.TRAINING_COURSE.getValue())!=null ){
					item = new SelectItem(ContractAttachmentType.TRAINING_ANNEX_II, ContractAttachmentType.TRAINING_ANNEX_II.getName(AonUtil.getCurrentLocale()));
					availableDocumentList.add(item);
					item = new SelectItem(ContractAttachmentType.TRAINING_CENTER_DIRECT_DEBIT, ContractAttachmentType.TRAINING_CENTER_DIRECT_DEBIT.getName(AonUtil.getCurrentLocale()));
					availableDocumentList.add(item);
				}
			}
			
			if(getContract().getEndDate()!=null){
//				TODO suspension_notice_letter
//				item = new SelectItem(ContractAttachmentType.ENTERPRISE_CERTIFICATE_DOC_DRAFT, ContractAttachmentType.ENTERPRISE_CERTIFICATE_DOC_DRAFT.getName(AonUtil.getCurrentLocale()));
//				availableDocumentList.add(item);
				item = new SelectItem(ContractAttachmentType.ENTERPRISE_CERTIFICATE_DOC_DRAFT, ContractAttachmentType.ENTERPRISE_CERTIFICATE_DOC_DRAFT.getName(AonUtil.getCurrentLocale()));
				availableDocumentList.add(item);
			}
			
//		}
		return availableDocumentList;
	}
	
	public void onDocumentGenerationShow(ActionEvent event){
		initialize(false);
		beforeDocumentShow();
		availableDocumentList = null;
		generatedDocumentMap = null;
		selectedDocuments = null;
		selectRequiredDocuments();
	}
	public void onSuspensionCauseSelect(ActionEvent event){
		selectedDocuments = null;
		selectRequiredDocuments();
	}
	
	public void onGenerateDocument(ActionEvent event){
		generateDocument();
	}
	
	public void selectRequiredDocuments(){
		ContractController controller =  (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		if(getContract().getEndDate()!=null && getContract().getEndDate().before(new Date())){
			selectedDocuments = (ContractAttachmentType[]) ArrayUtils.add(selectedDocuments, ContractAttachmentType.ENTERPRISE_CERTIFICATE_DOC_DRAFT);
		} else if(controller.isExtendedContract()){
			selectedDocuments = (ContractAttachmentType[]) ArrayUtils.add(selectedDocuments, ContractAttachmentType.EXTENSION_DOC_DRAFT);
		} else {
			selectAllDocuments();
		}
	}
		
	public void selectAllDocuments(){
		for(SelectItem item: getAvailableDocumentList()){
			selectedDocuments = (ContractAttachmentType[]) ArrayUtils.add(selectedDocuments, (ContractAttachmentType)item.getValue());
		}
	}
	
	public void generateDocument(){
		ContractUtils utils = ContractUtils.getInstance();
		
		generatedDocumentMap = new HashMap<ContractAttachmentType, byte[]>();
		
		// Documento del contrato
		if(ArrayUtils.contains(selectedDocuments, ContractAttachmentType.CONTRACT_DOC_DRAFT)){
			try {
				setDocumentType(ContractAttachmentType.CONTRACT_DOC_DRAFT);
				loadDocument(true);
				generatedDocumentMap.put(ContractAttachmentType.CONTRACT_DOC_DRAFT, getContractPdfWriter().buildPdf(true));
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage("No se ha podido generar el contrato");
				AonUtil.addErrorMessage(e.getMessage());
			} catch (UnsupportedContractDocumentException e) {
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage("No se ha podido generar el contrato");
				AonUtil.addErrorMessage(e.getMessage());
			} catch (Exception e){
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage("No se ha podido generar el contrato");
				AonUtil.addErrorMessage(e.getMessage());
			}
		}

		// Documento de las clausulas
		if(ArrayUtils.contains(selectedDocuments, ContractAttachmentType.CONTRACT_DOC_DRAFT) 
				&& generatedDocumentMap.containsKey(ContractAttachmentType.CONTRACT_DOC_DRAFT)){
			try {
				List<IAttachment> attachList = new LinkedList<IAttachment>();
				ContractAttachment contractAttach = new ContractAttachment();
				contractAttach.setData(generatedDocumentMap.get(ContractAttachmentType.CONTRACT_DOC_DRAFT));
				contractAttach.setMimeType(MimeType.MIME_PDF);
				attachList.add(contractAttach);
				
				ContractAttachment clausesAttach = new ContractAttachment();
				clausesAttach.setData(getReport(IPayrollConstants.CONTRACT_CLAUSES_REPORT_KEY));
				clausesAttach.setMimeType(MimeType.MIME_PDF);
				attachList.add(clausesAttach);
				
				generatedDocumentMap.put(ContractAttachmentType.CONTRACT_DOC_DRAFT, PdfUtils.mergePdf(attachList));
			} catch (ReportException e) {
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage("No se han podido generar las clausulas del contrato");
				AonUtil.addErrorMessage(e.getMessage());
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage("No se han podido generar las clausulas del contrato");
				AonUtil.addErrorMessage(e.getMessage());
			}
		}
		
		// Documento de la copia basica
		if(ArrayUtils.contains(selectedDocuments, ContractAttachmentType.BASIC_COPY_DRAFT)){
			try {
				setDocumentType(ContractAttachmentType.BASIC_COPY_DRAFT);
				loadDocument(true);
				generatedDocumentMap.put(ContractAttachmentType.BASIC_COPY_DRAFT, getContractPdfWriter().buildPdf(true));
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage("No se ha podido generar la copia basica");
				AonUtil.addErrorMessage(e.getMessage());
			} catch (UnsupportedContractDocumentException e) {
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage("No se ha podido generar la copia basica");
				AonUtil.addErrorMessage(e.getMessage());
			} catch (Exception e){
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage("No se ha podido generar la copia basica");
				AonUtil.addErrorMessage(e.getMessage());
			}
		}
		
		if( utils.isTrainingContract(getContract(), getCode()) && utils.getContractInfoMap(getContract()).get(ContractVariable.TRAINING_COURSE.getValue())!=null ){
			// Acuerdo actividad formativa, Anexo II del contrato de formacion (421)
			if(ArrayUtils.contains(selectedDocuments, ContractAttachmentType.TRAINING_ANNEX_II)){
				try {
					setDocumentType(ContractAttachmentType.TRAINING_ANNEX_II);
					loadDocument(true);
					generatedDocumentMap.put(ContractAttachmentType.TRAINING_ANNEX_II, getContractPdfWriter().buildPdf(true));
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
					AonUtil.addErrorMessage("No se ha podido generar el anexo II");
					AonUtil.addErrorMessage(e.getMessage());
				} catch (UnsupportedContractDocumentException e) {
					LOGGER.error(e.getMessage(), e);
					AonUtil.addErrorMessage("No se ha podido generar el anexo II");
					AonUtil.addErrorMessage(e.getMessage());
				} catch (Exception e){
					LOGGER.error(e.getMessage(), e);
					AonUtil.addErrorMessage("No se ha podido generar el anexo II");
					AonUtil.addErrorMessage(e.getMessage());
				}
			}
		
			// Domiciliacion bancaria del contrato de formacion (421)
			if(ArrayUtils.contains(selectedDocuments, ContractAttachmentType.TRAINING_CENTER_DIRECT_DEBIT)){
				try {
					generatedDocumentMap.put(ContractAttachmentType.TRAINING_CENTER_DIRECT_DEBIT, getReport(IPayrollConstants.TRAINING_DIRECT_DEBIT_REPORT_KEY));
				} catch (ReportException e) {
					LOGGER.error(e.getMessage(), e);
					AonUtil.addErrorMessage("No se ha podido generar la domiciliacion bancaria");
					AonUtil.addErrorMessage(e.getMessage());
				}
			}
		}
		
		// Documento de la prorroga
		if(ArrayUtils.contains(selectedDocuments, ContractAttachmentType.EXTENSION_DOC_DRAFT)){
			try {
				setDocumentType(ContractAttachmentType.EXTENSION_DOC_DRAFT);
				loadDocument(true);
				generatedDocumentMap.put(ContractAttachmentType.EXTENSION_DOC_DRAFT, getContractPdfWriter().buildPdf(true));
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage("No se ha podido generar la prorroga");
				AonUtil.addErrorMessage(e.getMessage());
			} catch (UnsupportedContractDocumentException e) {
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage("No se ha podido generar la prorroga");
				AonUtil.addErrorMessage(e.getMessage());
			} catch (Exception e){
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage("No se ha podido generar la prorroga");
				AonUtil.addErrorMessage(e.getMessage());
			}
		}
		
		// Certificado de empresa
		if(ArrayUtils.contains(selectedDocuments, ContractAttachmentType.ENTERPRISE_CERTIFICATE_DOC_DRAFT)){
			try {
				setDocumentType(ContractAttachmentType.ENTERPRISE_CERTIFICATE_DOC_DRAFT);
				loadDocument(true);
				generatedDocumentMap.put(ContractAttachmentType.ENTERPRISE_CERTIFICATE_DOC_DRAFT, getContractPdfWriter().buildPdf(true));
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage("No se ha podido generar el certificado de empresa");
				AonUtil.addErrorMessage(e.getMessage());
			} catch (UnsupportedContractDocumentException e) {
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage("No se ha podido generar el certificado de empresa");
				AonUtil.addErrorMessage(e.getMessage());
			} catch (Exception e){
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage("No se ha podido generar el certificado de empresa");
				AonUtil.addErrorMessage(e.getMessage());
			}
		}
		
	}
	
	private List<IAttachment> getGeneratedAttach(){
		List<ContractAttachmentType> types = new LinkedList<ContractAttachmentType>();
		List<IAttachment> list = new LinkedList<IAttachment>();
		types.add(ContractAttachmentType.CONTRACT_DOC_DRAFT);
		types.add(ContractAttachmentType.CONTRACT_CLAUSES);
		types.add(ContractAttachmentType.BASIC_COPY_DRAFT);
		types.add(ContractAttachmentType.TRAINING_ANNEX_II);
		types.add(ContractAttachmentType.TRAINING_CENTER_DIRECT_DEBIT);
		types.add(ContractAttachmentType.EXTENSION_DOC_DRAFT);
		types.add(ContractAttachmentType.ENTERPRISE_CERTIFICATE_DOC_DRAFT);
		for(ContractAttachmentType type: types){
			if(generatedDocumentMap.containsKey(type)){
				ContractAttachment attach = new ContractAttachment();
				attach.setAttachmentType(type);
				attach.setDescription(type.getName(AonUtil.getCurrentLocale()));
				attach.setData(generatedDocumentMap.get(type));
				list.add(attach);
			}
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private byte[] getReport( String report ) throws ReportException {
		ReportManager reportManager = new ReportManager();
		reportManager.setCollectionProvider( new SingleCollectionProvider(getContract()) );
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		reportManager.execute( out, report);
		return out.toByteArray();
	}	
	
	public void onSaveFiles(ActionEvent event){
		for(IAttachment attachment: getGeneratedAttach()){
			ContractAttachment attach = (ContractAttachment) attachment; 
			saveDocument(attach.getData(), attach.getAttachmentType());
		}
		ContractAttachController attachController = (ContractAttachController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_ATTACH_CONTROLLER);
		attachController.initializeModel();
	}

	public void onDownloadMergedFile(ActionEvent event){
		byte[] data = PdfUtils.mergePdf(getGeneratedAttach());
		InputStream in = new ByteArrayInputStream(data);
		long size = ArrayUtils.getLength(data);
		DownloadUtil.downloadAttachment("Contract-documents", MimeType.MIME_PDF, in, size);
	}
	
	public void onSendSelectedByEmail(ActionEvent event) throws ManagerBeanException, IOException {
		PayrollEmailUtil emailController = new PayrollEmailUtil();
		
		MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
		if (mailConfig.getMailAccountCount() > 0) {
			MessageController messageController = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
			messageController.initNewMessage();
			emailController.initMessageController(messageController, getContract(), getGeneratedAttach(), true);
			messageController.setShowNewMessageWindow(true);
		} else {
			AonUtil.addErrorMessageFromBundle(ICommonMessages.NOT_MAIL_ACCOUNTS);
		}
	}
	
	public String obtainClausesContent() throws ManagerBeanException {
		Contract contract = getContract();
		IManagerBean bean = BeanManager.getManagerBean(ContractClause.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_CONTRACT_ID), contract.getId());
		criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_LINE));
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			StringBuffer bf = new StringBuffer();
			int line = 0;
			for(ITransferObject to: list){
				line++;
				ContractClause clause = (ContractClause) to;
				bf.append(line);
				bf.append(". ");
				bf.append(clause.getName());
				bf.append("\n");
				bf.append(clause.getDescription());
				bf.append("\n\n");
			}
			return bf.toString();
		}
		return null;
	}
	
}
