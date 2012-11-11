package com.esferalia.aon.ui.payroll.controller.contract;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.esferalia.aon.ui.payroll.utils.ContractPdfHandler;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;

public class ContractPdfController {
	
	private static final double FACTOR_1X = 1.2;
	private static final double FACTOR_2X = 1.4;
	private static final double FACTOR_3X = 1.6;
	private static final double FACTOR_4X = 1.8;

	private Integer contractPage;
	private Integer contractWidth;
	private Integer contractHeight;
	private int numberOfContractPages;
	private int zoomFactor;
	
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
//		return getFactorizedValue(contractWidth);
	}
	
	public void setContractWidth(Integer contractWidth) {
		this.contractWidth = contractWidth;
	}
	
	public Integer getContractHeight() {
		return getFactorizedValue(getContractPdfWriter().getContractHeight());
//		return getFactorizedValue(contractHeight);
	}
	
	public void setContractHeight(Integer contractHeight) {
		this.contractHeight = contractHeight;
	}
	public int getNumberOfContractPages() {
		return numberOfContractPages;
	}
	public void setNumberOfContractPages(int numberOfContractPages) {
		this.numberOfContractPages = numberOfContractPages;
	}
	

	private static final Logger LOGGER = LoggerFactory.getLogger(ContractPdfController.class.getName());
	private static final String IMAGE_URL_PREFIX0 = ".contractImage";
	private static final String IMAGE_URL_PREFIX1 = "?model=";
	private static final String IMAGE_URL_PREFIX2 = "&width=";
	private static final String IMAGE_URL_PREFIX3 = "&height=";
//	private static final int MAX_FILE_SIZE_MB = 3;
//	private static final int MAX_FILE_SIZE = MAX_FILE_SIZE_MB * 1024 * 1024;
//	private static final String CONTRACT_XML_CONTEXT_PATH = "com.esferalia.aon.ui.payroll.utils.contractMojo";
	
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
	
	public void initialize(Contract contract, String backAction) {
		setContract(contract);
		setBackAction(backAction);
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
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.PDF_DOCUMENT);
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
//		contractDataMap = null;
		
		String tc2 = getContractDataMap().get(ContextVariable.TC2.getName());
		String indefinite = getContractDataMap().get(ContextVariable.INDEFINITE.getName());
		String fullTime = getContractDataMap().get(ContextVariable.FULL_TIME.getName());
		
//		getContractPdfWriter().setContractPdfFields(null);
		if( tc2!=null ){
			if(new Boolean(indefinite)){
				
			}
			if(new Boolean(fullTime)){
				
			}
			
			setCode(ContractCode.getContractCodeByValue(tc2));
			
			// TODO EN DESARROLLO, solo se contempla cuando el TC2 es 100
			if(getCode()==ContractCode.C100){
				for (ContractOption contractOption:ContractOption.values()) {
					for (ContractType contractType:contractOption.getTypes()) {
						for (ContractCode c : contractType.getCodes()) {
							if(contractType==ContractType.PE170){
								setContractType(contractType);
							}
							if(contractOption==ContractOption.INDEFINITE){
								setContractOption(contractOption);
							}
//							if( c.equals(getCode()) ){
//								setContractType(contractType);
//								if(contractOption==ContractOption.INDEFINITE){
//									setContractOption(contractOption);
//								}
//								break;
//							}
						}
					}
				}
			} else {
				setCode(null);
				setContractType(null);
				setContractOption(null);
			}
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
//			readPdfFields(getContractPdfDraft().getData(),getContractModel());
//			getContractPdfHandler().readPdfFields(getContractModel());
			if(getContractPdfDraft()==null || getContractPdfDraft().getId()==null){
//				getContractPdfWriter().loadDefaultFields(getContract());
				getContractPdfWriter().loadPdf(getContractModel(), getContract());
			} else {
				getContractPdfWriter().loadPdf(getContractPdfDraft());
			}
			setContractPage(1);

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
	
	public void onNextContractPage( ActionEvent event ) {
		setContractPage(getContractPage()+1);
	}
	public void onFirstContractPage( ActionEvent event ) {
		setContractPage(1);
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
			attach.setAttachmentType(ContractAttachmentType.PDF_DOCUMENT);
			attach.setMimeType(MimeType.MIME_PDF);
			attach.setDescription("Contrato (Borrador)");
			bean.insertOrUpdate(attach);
			ContractAttachController attachController = (ContractAttachController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_ATTACH_CONTROLLER);
			attachController.initializeModel();
//		} catch (IOException e) {
//			LOGGER.error(e.getMessage(), e);
//			throw new AbortProcessingException(e);
//		} catch (UnsupportedContractModelException e) {
//			LOGGER.error(e.getMessage(), e);
//			throw new AbortProcessingException(e);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AbortProcessingException(e);
		}
	}
	
//	public void readPdfFields(byte[] pdf, ContractModel model) throws IOException{
//		if(pdf==null){
//			if(model==null){
//				throw new AbortProcessingException("El modelo no se ha cargado correctamente o no existe");
//			}
//			readPdfFields(new PdfReader(getContractPdfWriter().getContractModelUrl(model+".pdf")));
//		} else {
//			readPdfFields(new PdfReader(pdf)); 
//		}
//	}
	
//	public void readPdfFields(ContractModel model) throws IOException{
//		if(model==null){
//			String msg = "El modelo no se ha cargado correctamente o no existe";
//			AonUtil.addErrorMessage(msg);
//			throw new AbortProcessingException(msg);
//		}
//		readPdfFields(new PdfReader(getContractPdfWriter().getContractModelUrl(model+".pdf")));
//	}
	
//	private void readPdfFields(PdfReader reader) throws IOException{
//		
//		setContractWidth((int)reader.getPageSize(1).getWidth());
//		setContractHeight((int)reader.getPageSize(1).getHeight());
//		
//		setNumberOfContractPages(reader.getNumberOfPages());
//		AcroFields form = reader.getAcroFields();
//		HashMap<?,?> fields = form.getFields();
//		String key;
//		ContractPdfField field;
//		for (Iterator<?> it = fields.keySet().iterator(); it.hasNext();) {
//			key = (String) it.next();
//			field = new ContractPdfField();
//			if(form.getFieldType(key)==AcroFields.FIELD_TYPE_CHECKBOX){
//					field.setType(AcroFields.FIELD_TYPE_CHECKBOX);
//					field.setValue(form.getField(key).equals(form.getAppearanceStates(key)[0])?"true":"false");
//			} else if(form.getFieldType(key)==AcroFields.FIELD_TYPE_TEXT){
//					field.setType(AcroFields.FIELD_TYPE_TEXT);
//					field.setValue(form.getField(key));
//			} else {
//				field.setType(AcroFields.FIELD_TYPE_NONE);;
//			}
//			Float f = form.getFieldPositions(key)[0];
//			field.setPage(f.intValue());
//			field.setLabel(key);
//			field.setBottomCoordinates(getBottomCoordinates(form, key));
//			field.setLeftCoordinates(getLeftCoordinates(form, key));
//			field.setWidth(getInputTextWidth(form, key));
//			field.setHeight(getInputTextHeight(form, key));
//			field.setZoomFactor(getZoomFactor());
//			if(field.getType()!=null){
//				getContractPdfWriter().getContractPdfFields().add(field);
//			}
//		}
//		reader.close();
//	}
	
	/*
	 * [page, llx, lly, urx, ury]
	 */
//	private String getBottomCoordinates(AcroFields form, String key){
//		return Float.toString(form.getFieldPositions(key)[2]);
//	}
//	private String getLeftCoordinates(AcroFields form, String key){
//		return Float.toString(form.getFieldPositions(key)[1]);
//	}
//	private String getInputTextWidth(AcroFields form, String key){
//		Float f = form.getFieldPositions(key)[3]-form.getFieldPositions(key)[1];
//		return String.valueOf(f.intValue());
//	}
//	private String getInputTextHeight(AcroFields form, String key){
//		return Float.toString(form.getFieldPositions(key)[4]-form.getFieldPositions(key)[2]);
//	}
	
}
