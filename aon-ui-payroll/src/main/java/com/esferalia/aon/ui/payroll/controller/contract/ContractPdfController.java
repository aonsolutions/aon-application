package com.esferalia.aon.ui.payroll.controller.contract;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.Classpath;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.model.ContractPdfField;
import com.esferalia.aon.file.payroll.contract.pdf.model.UnsupportedContractModelException;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.payroll.enumeration.ContractOption;
import com.esferalia.aon.payroll.enumeration.ContractType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.file.ContractPdfWriter;
import com.esferalia.aon.ui.payroll.utils.PdfToImage;

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

	private Integer contractPage;
	private int zoomFactor;
	private Contract contract;
	private ContractPdfWriter contractPdfWriter;
	private String imageUrl;
	private ContractOption contractOption;
	private ContractType contractType;
	private ContractModel contractModel;
	private ContractCode code;
	private Map<String, String> contractDataMap;
	private ContractAttachment contractPdfDraft;	
	private String backAction;
	
	public Integer getContractPage() {
		return contractPage;
	}
	public void setContractPage(Integer contractPage) {
		this.contractPage = contractPage;
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
	
	public int getZoomFactor() {
		return zoomFactor;
	}
	
	public void setZoomFactor(int zoomFactor) {
		this.zoomFactor = zoomFactor;
	}
	
	public Integer getContractWidth() {
		return getFactorizedValue(getContractPdfWriter().getContractWidth());
	}
	
	public Integer getContractHeight() {
		return getFactorizedValue(getContractPdfWriter().getContractHeight());
	}
	
	public int getNumberOfContractPages() {
		return getContractPdfWriter().getNumberOfContractPages();
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
	public boolean isNew(){
		return getContractPdfDraft()==null||getContractPdfDraft().getId()==null;
	}
	
	public void initialize(Contract contract) {
		setContract(contract);
		contractDataMap = null;
		contractPdfDraft = null;
	}
	
	public String getImageUrl() {
		StringBuilder builder = new StringBuilder(getContractPage().toString());
		builder.append(IMAGE_URL_PREFIX0);
		builder.append(IMAGE_URL_PREFIX1);
		builder.append(getContractModel());
		builder.append(IMAGE_URL_PREFIX2);
		builder.append(getContractWidth());
		builder.append(IMAGE_URL_PREFIX3);
		builder.append(getContractHeight());
		imageUrl = builder.toString();
		return imageUrl;
	}
	
	private Map<String, String> getContractDataMap() {
		if(contractDataMap==null){
			contractDataMap = new HashMap<String, String>();
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), getContract().getId());
				criteria.addNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
				for(ITransferObject to: bean.getList(criteria)){
					ContractData data = (ContractData) to;
					contractDataMap.put(data.getName(), data.getExpression().replace('"', ' ').trim());
				}
			} catch (ManagerBeanException e) {
				// NADA, se devuelve un mapa vacio
				return contractDataMap;
			}
		}
		return contractDataMap;
	}
	
	private ContractAttachment getContractPdfDraft() {
		if(contractPdfDraft==null){
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), getContract().getId());
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.CONTRACT_DRAFT);
				List<ITransferObject> list = bean.getList(criteria);
				if(!list.isEmpty()){
					return (ContractAttachment) list.get(0);
				}
			} catch (ManagerBeanException e) {
				// NADA, se devuelve una nueva instancia
			}
		}
		return new ContractAttachment();
	}

	public void beforeDocumentShow() {
		String tc2 = getContractDataMap().get(ContextVariable.TC2.getName());
		String indefinite = getContractDataMap().get(ContextVariable.INDEFINITE.getName());
		String fullTime = getContractDataMap().get(ContextVariable.FULL_TIME.getName());
		if( tc2!=null ){
			if(new Boolean(indefinite)){
				
			}
			if(new Boolean(fullTime)){
				
			}
			setCode(ContractCode.getContractCodeByValue(tc2));
			setContractModel(getContract().getModel());
		}
	}
	
	public void onContractDocumentShow( ActionEvent event ) {
		try {
			if(getContract()==null || getContract().getId()==null){
				String msg = "Error al obtener los datos de contrato";
				LOGGER.error(msg);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}

			beforeDocumentShow();
			
			if(getContractModel()==null){
				String msg = "Modelo de contrato no reconocido.";
				LOGGER.error(msg);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			
			setZoomFactor(2);

			if(getContractPdfDraft()==null || getContractPdfDraft().getId()==null){
				getContractPdfWriter().loadPdf(getContractModel(), getContract());
			} else {
				getContractPdfWriter().loadPdf(getContractPdfDraft(), getContract());
			}

			setContractPage(1);
			
			createPdfThumbnail();

		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		} catch (UnsupportedContractModelException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		}
	}
	
	private void createPdfThumbnail() throws IOException{
		final String SCHEMA = getContractModel()+".pdf"; 
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = Classpath.search(cl, IPayrollConstants.MODEL_PATH, SCHEMA);
		PdfToImage.create(urls[0], getContractPage(), getContractWidth().intValue(), getContractHeight().intValue());
	}
	
	public void onChangeZoomFactor( ActionEvent event ) throws IOException {
		createPdfThumbnail();
		
		for(ContractPdfField field: getContractPdfWriter().getContractPdfFields()){
			field.setZoomFactor(getZoomFactor());
		}
		
	}
	public void onNextContractPage( ActionEvent event ) throws IOException {
		setContractPage(getContractPage()+1);
		createPdfThumbnail();
	}
	public void onPreviousContractPage( ActionEvent event ) throws IOException {
		setContractPage(getContractPage()-1);
		createPdfThumbnail();
	}
	public boolean isFirstContractPage() {
		return getContractPage().equals(1);
	}
	public boolean isLastContractPage() {
		return getContractPage().equals(getNumberOfContractPages());
	}
	
	public void onDocumentSave(ActionEvent event) {
		try {
			ContractAttachment attach = getContractPdfDraft();
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			attach.setContract(getContract());
			attach.setData(getContractPdfWriter().buildPdf());
			attach.setAttachDate(new Date());
			attach.setAttachmentType(ContractAttachmentType.CONTRACT_DRAFT);
			attach.setMimeType(MimeType.MIME_PDF);
			attach.setDescription("Contrato laboral");
			bean.insertOrUpdate(attach);
			ContractAttachController attachController = (ContractAttachController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_ATTACH_CONTROLLER);
			attachController.initializeModel();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AbortProcessingException(e);
		}
	}
	
}
