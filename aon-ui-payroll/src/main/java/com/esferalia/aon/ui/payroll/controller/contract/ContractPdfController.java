package com.esferalia.aon.ui.payroll.controller.contract;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.ContractPdfField;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contract.pdf.annex.ModelPE230;
import com.esferalia.aon.file.payroll.contract.pdf.basicCopy.BasicCopy;
import com.esferalia.aon.file.payroll.contract.pdf.extension.Extension;
import com.esferalia.aon.file.payroll.contrata.ContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.payroll.enumeration.ContractOption;
import com.esferalia.aon.payroll.enumeration.ContractType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.file.ContractPdfWriter;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;
import com.esferalia.aon.ui.payroll.utils.PdfUtils;

public class ContractPdfController {
	
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
	private ContractOption contractOption;
	private ContractType contractType;
	private ContractModel contractModel;
	private ContractCode code;
	private ContractAttachment contractPdfDraft;	
	private String backAction;
	private ContractAttachmentType documentType;
	private ContrataParams contrataParams;
	
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
			contractPdfWriter = ContractPdfWriter.getInstance();
		}
		return contractPdfWriter;
	}
	public void setContractPdfWriter(ContractPdfWriter contractPdfWriter) {
		this.contractPdfWriter = contractPdfWriter;
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
	public boolean isNew(){
		return getContractPdfDraft()==null||getContractPdfDraft().getId()==null;
	}
	public ContractAttachmentType getContractPdfType(){
		return ContractAttachmentType.CONTRACT_DOCUMENT_DRAFT;
	}
	public ContractAttachmentType getBasicCopyPdfType(){
		return ContractAttachmentType.BASIC_COPY_DRAFT;
	}
	public ContractAttachmentType getAnnexIIPdfType(){
		return ContractAttachmentType.TRAINING_ANNEX_II;
	}
	public ContractAttachmentType getExtensionPdfType(){
		return ContractAttachmentType.CONTRACT_EXTENSION_DRAFT;
	}
	public ContrataParams getContrataParams() {
		return contrataParams;
	}
	public void setContrataParams(ContrataParams contrataParams) {
		this.contrataParams = contrataParams;
	}
	private void initialize() {
		setContract((Contract) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER).getTo());
		setContractPdfDraft( obtainContractPdfDraft() );
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
		StringBuilder builder = new StringBuilder(getDocumentPage().toString());
		builder.append(IMAGE_URL_PREFIX0);
		builder.append(IMAGE_URL_PREFIX1);
		if(getDocumentType()==ContractAttachmentType.CONTRACT_DOCUMENT_DRAFT){
			builder.append(getContractModel());
		} else if(getDocumentType()==ContractAttachmentType.BASIC_COPY_DRAFT){
			builder.append(BasicCopy.BASIC_COPY_NAME);
		} else if(getDocumentType()==ContractAttachmentType.TRAINING_ANNEX_II){
			builder.append(ModelPE230.MODEL_NAME);
		} else if(getDocumentType()==ContractAttachmentType.CONTRACT_EXTENSION_DRAFT){
			builder.append(Extension.EXTENSION_NAME);
		}
		builder.append(IMAGE_URL_PREFIX2);
		builder.append(getDocumentWidth());
		builder.append(IMAGE_URL_PREFIX3);
		builder.append(getDocumentHeight());
		imageUrl = builder.toString();
		return imageUrl;
	}
	
	public void beforeDocumentShow() {
		PayrollUtils utils = new PayrollUtils();
		String tc2 = utils.getContractDataMap(getContract()).get(ContextVariable.TC2.getName());
		String indefinite = utils.getContractDataMap(getContract()).get(ContextVariable.INDEFINITE.getName());
		String fullTime = utils.getContractDataMap(getContract()).get(ContextVariable.FULL_TIME.getName());
		if( getDocumentType()==ContractAttachmentType.CONTRACT_DOCUMENT_DRAFT && tc2!=null ){
			if(new Boolean(indefinite)){
				
			}
			if(new Boolean(fullTime)){
				
			}
			setCode(ContractCode.getContractCodeByValue(tc2));
			setContractModel(getContract().getModel());
		} else if( getDocumentType()==ContractAttachmentType.TRAINING_ANNEX_II ){
			
		}
	}
	
	public void onContractDocumentShow( ActionEvent event ) {
		setZoomFactor(2);
		setDocumentPage(1);
		try {
			loadDocument(false);
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
	
	public void loadDocument(boolean forceRefresh) throws UnsupportedContractDocumentException, IOException{
		initialize();
		setContractPdfWriter(null);
		if(getContract()==null || getContract().getId()==null){
			String msg = "Error al obtener los datos de contrato";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		beforeDocumentShow();
		if(getDocumentType()==ContractAttachmentType.CONTRACT_DOCUMENT_DRAFT && getContractModel()==null){
			String msg = "Modelo de contrato no reconocido.";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		ContractContrataController contrata = (ContractContrataController) AonUtil.getRegisteredBean("contractContrata");
		contrata.onContrataDataShow(null);
		if(contrata.getContrataAttach()!=null && contrata.getContrataAttach().getId()!=null){
			setContrataParams(contrata.getHandler().getParams());
		} else {
			setContrataParams(null);
		}
		loadPdfDocument(forceRefresh);
	}
	
	private void loadPdfDocument(boolean forceRefresh) throws UnsupportedContractDocumentException, IOException{
		if(getDocumentType()==ContractAttachmentType.CONTRACT_DOCUMENT_DRAFT){
			if(forceRefresh || getContractPdfDraft()==null || getContractPdfDraft().getId()==null){
				getContractPdfWriter().loadNewPdf(getContractModel(), getContract(), getContrataParams());
				completeNewPdfFields(ContractAttachmentType.CONTRACT_DOCUMENT_DRAFT);
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
		} else if(getDocumentType()==ContractAttachmentType.CONTRACT_EXTENSION_DRAFT){
			if(forceRefresh || getContractPdfDraft()==null || getContractPdfDraft().getId()==null){
				getContractPdfWriter().loadNewPdf(Extension.EXTENSION_NAME, getContract(), getContrataParams());
				completeNewPdfFields(ContractAttachmentType.CONTRACT_EXTENSION_DRAFT);
			} else {
				getContractPdfWriter().loadExistingPdf(Extension.EXTENSION_NAME, getContractPdfDraft());
			}
		}
	}
	
	private void completeNewPdfFields(ContractAttachmentType attachType) {
		ContractController controller = (ContractController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_CONTROLLER);
		if(attachType==ContractAttachmentType.CONTRACT_DOCUMENT_DRAFT){
			if(controller.getParams().getContractCode()==ContractCode.C421){
				getContractPdfWriter().getPdfDocument().getPdfFieldsMap().get("jornhoraefec").setValue(controller.getParams().getWorkSchedule());
			}
		} else if(attachType==ContractAttachmentType.TRAINING_ANNEX_II) {
			getContractPdfWriter().getPdfDocument().getPdfFieldsMap().get("horario").setValue(controller.getParams().getTrainingSchedule());
		}
	}
	
	private void createPdfThumbnail() throws IOException, UnsupportedContractDocumentException{
		String fileName = "";
		if(getDocumentType()==ContractAttachmentType.CONTRACT_DOCUMENT_DRAFT){
			fileName = getContractModel()+".pdf"; 
		} else if(getDocumentType()==ContractAttachmentType.BASIC_COPY_DRAFT){
			fileName = BasicCopy.BASIC_COPY_NAME+".pdf"; 
		} else if(getDocumentType()==ContractAttachmentType.TRAINING_ANNEX_II){
			fileName = ModelPE230.MODEL_NAME+".pdf"; 
		} else if(getDocumentType()==ContractAttachmentType.CONTRACT_EXTENSION_DRAFT){
			fileName = Extension.EXTENSION_NAME+".pdf"; 
		}
		URL url = getContractPdfWriter().getContractDocumentUrl(fileName);
		PdfUtils.createPdfWallpaper(url, getDocumentPage(), getDocumentWidth().intValue(), getDocumentHeight().intValue());
	}
	
	public void onChangeZoomFactor( ActionEvent event ) throws IOException, UnsupportedContractDocumentException {
		createPdfThumbnail();
		for(ContractPdfField field: getContractPdfWriter().getContractPdfFields()){
			field.setZoomFactor(getZoomFactor());
		}
	}
	public void onNextDocumentPage( ActionEvent event ) throws IOException, UnsupportedContractDocumentException {
		setDocumentPage(getDocumentPage()+1);
		createPdfThumbnail();
	}
	public void onPreviousDocumentPage( ActionEvent event ) throws IOException, UnsupportedContractDocumentException {
		setDocumentPage(getDocumentPage()-1);
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
		try {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			ContractAttachment attach = getContractPdfDraft();
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			attach.setContract(getContract());
			attach.setData(getContractPdfWriter().buildPdf());
			attach.setAttachDate(new Date());
			attach.setAttachmentType(getDocumentType());
			attach.setMimeType(MimeType.MIME_PDF);
			attach.setDescription(getDocumentType().getName(locale));
			bean.insertOrUpdate(attach);
			ContractAttachController attachController = (ContractAttachController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_ATTACH_CONTROLLER);
			attachController.initializeModel();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AbortProcessingException(e);
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
		byte[] buffer = getContractPdfWriter().buildPdf();
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
	
}
