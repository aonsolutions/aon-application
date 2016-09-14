package com.esferalia.aon.ui.sepe.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contrata.ContrataProrrogaParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractInfo;
import com.esferalia.aon.payroll.ContractInfo.ContractSepeStatus;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.ContrataBatch;
import com.esferalia.aon.payroll.ContrataBatchAttachment;
import com.esferalia.aon.payroll.ContrataBatchDetail;
import com.esferalia.aon.payroll.SepeBatchAttachment;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContrataFileType;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.SepeBatchAttachmentType;
import com.esferalia.aon.sepe.api.contrata.contratos.FICHEROCONTRATOS;
import com.esferalia.aon.sepe.api.contrata.contratos.RESPUESTACONTRATOTYPE;
import com.esferalia.aon.ui.sepe.controller.handler.ContrataContratosHandler;
import com.esferalia.aon.ui.sepe.controller.handler.ContrataProrrogasHandler;
import com.esferalia.aon.ui.sepe.controller.handler.ContrataTransformacionesHandler;
import com.esferalia.aon.ui.sepe.controller.handler.IContrataHandler;
import com.esferalia.aon.ui.sepe.file.ContrataWriter;
import com.esferalia.aon.ui.sepe.utils.ContrataCommunicator;
import com.esferalia.aon.ui.sepe.utils.SEPEFileUtils;


public class ContrataController implements IContrataHandler, ISepeHandler, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContrataController.class.getName());
	
	private boolean contratoFile;
	private boolean prorrogaFile;
	private boolean transformacionFile;
	
	private boolean showBatchWindow;
	private boolean showCommunicationWindow;
	private boolean showLoginWindow;
	private boolean enabledContrataEdition;
	private boolean showExtensionContrataWindow;
	private boolean showTransformContrataWindow;
	
	private boolean updateRequired;
	
	private boolean newBatch;
	
	private IContrataHandler handler;
	
	private ContrataCommunicator communicator;
	
	private IAttachment generatedFile;
	private IAttachment communicationIdFile;
	private IAttachment responseFile;

	private ContrataBatch batch;
	

	public boolean isShowTransformContrataWindow() {
		return showTransformContrataWindow;
	}
	public void setShowTransformContrataWindow(boolean showTransformContrataWindow) {
		this.showTransformContrataWindow = showTransformContrataWindow;
	}
	public boolean isShowExtensionContrataWindow() {
		return showExtensionContrataWindow;
	}
	public void setShowExtensionContrataWindow(boolean showExtensionContrataWindow) {
		this.showExtensionContrataWindow = showExtensionContrataWindow;
	}
	public boolean isEnabledContrataEdition() {
		return enabledContrataEdition;
	}
	public boolean isContratoFile() {
		return contratoFile;
	}
	public void setContratoFile(boolean contratoFile) {
		this.contratoFile = contratoFile;
	}
	public boolean isTransformacionFile() {
		return transformacionFile;
	}
	public void setTransformacionFile(boolean transformacionFile) {
		this.transformacionFile = transformacionFile;
	}
	public boolean isProrrogaFile() {
		return prorrogaFile;
	}
	public void setProrrogaFile(boolean prorrogaFile) {
		this.prorrogaFile = prorrogaFile;
	}
	public ContrataFileType getContrataFileType(){
		if(isContratoFile()){
			return ContrataFileType.CONTRACT;
		} else if(isProrrogaFile()){
			return ContrataFileType.EXTENSION;
		} else if(isTransformacionFile()){
			return ContrataFileType.TRANSFORMATION;
		}
		return null;
	}
	public void setEnabledContrataEdition(boolean enabledContrataEdition) {
		this.enabledContrataEdition = enabledContrataEdition;
	}
	
	@Override
	public boolean isShowLoginWindow() {
		return showLoginWindow;
	}
	public void setShowLoginWindow(boolean showLoginWindow) {
		this.showLoginWindow = showLoginWindow;
	}
	
	@Override
	public boolean isShowBatchWindow() {
		return showBatchWindow;
	}
	public void setShowBatchWindow(boolean showBatchWindow) {
		this.showBatchWindow = showBatchWindow;
	}
	
	@Override
	public boolean isShowCommunicationWindow() {
		return showCommunicationWindow;
	}
	public void setShowCommunicationWindow(boolean showCommunicationWindow) {
		this.showCommunicationWindow = showCommunicationWindow;
	}

	public ContrataBatch getBatch() {
		return batch;
	}
	
	public void setBatch(ContrataBatch batch) {
		this.batch = batch;
	}
	
	@Override
	public Contract getContract() {
		return getHandler().getContract();
	}
	
	@Override
	public ContractCode getContractCode() {
		return getHandler().getContractCode();
	}
	
	public IContrataHandler getHandler() {
		return handler;
	}
	
	public void setHandler(IContrataHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public ContrataCommunicator getCommunicator() {
		if(communicator==null){
			communicator = new ContrataCommunicator();
		}
		return communicator;
	}
	
	public void setCommunicator(ContrataCommunicator communicator) {
		this.communicator = communicator;
	}
	
	@Override
	public IAttachment getGeneratedFile() {
		return generatedFile;
	}
	public void setGeneratedFile(IAttachment generatedFile) {
		this.generatedFile = generatedFile;
	}
	
	@Override
	public IAttachment getCommunicationIdFile() {
		return communicationIdFile;
	}
	public void setCommunicationIdFile(IAttachment communicationIdFile) {
		this.communicationIdFile = communicationIdFile;
	}

	@Override
	public IAttachment getResponseFile() {
		return responseFile;
	}
	public void setResponseFile(IAttachment responseFile) {
		this.responseFile = responseFile;
	}
	
	@Override
	public IContrataParams getParams() {
		return getHandler().getParams();
	}
	
	@Override
	public Boolean isBatchView() {
		if(getBatch()!=null || getContract()!=null){
			return getBatch()!=null;
		}
		return null;
	}
	
	@Override
	public boolean isNewBatch() {
		return newBatch;
	}
	
	public boolean isUpdateRequired() {
		return updateRequired;
	}
	public void setUpdateRequired(boolean updateRequired) {
		this.updateRequired = updateRequired;
	}
	
	public String getContrataModelName(){
		if(isContratoFile()){
			return "contract";
		} else if(isProrrogaFile()){
			return "extension";
		} else if(isTransformacionFile()){
			return "transform";
		} else if(getBatch()!=null){
			return "batch";
		}
		return null;
	}
	
	@Override
	public List<SelectItem> getPendingBatchList() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContrataBatch.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRATA_BATCH_STATUS), FileStatus.PENDING);
			if(isContratoFile()){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRATA_BATCH_TYPE), ContrataFileType.CONTRACT);
			} else if(isProrrogaFile()){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRATA_BATCH_TYPE), ContrataFileType.EXTENSION);
			} else if(isTransformacionFile()){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRATA_BATCH_TYPE), ContrataFileType.TRANSFORMATION);
			} else {
				return list;
			}
			for(ITransferObject to: bean.getList(criteria)){
				ContrataBatch batch = (ContrataBatch) to;
				SelectItem item = new SelectItem(batch, batch.getDate().toString());
				list.add(item);
			}
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido obtener la lista de remesas";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
		return list;
	}
	
	@Override
	public boolean isNevv(){
		return getGeneratedFile()==null || getGeneratedFile().getId()==null;
	}
	
	public String getCommunicationAvailableCodes(){
//		return "Comunicación implementada para los contratos con código: "+AVAILABLE_CONTRACT_CODE_COMMUNICATION;
		return "";
	}
	
	@Override
	public boolean isCommunicationAvailable(){
		return getBatch() != null || getHandler().isCommunicationAvailable();
	}
	
	@Override
	public boolean isCommunicationIdReceived(){
		return getCommunicationIdFile()!=null && getCommunicationIdFile().getId()!=null;
	}

	@Override
	public boolean isCommunicationResponseReceived(){
		return getResponseFile()!=null && getResponseFile().getId()!=null;
	}

	@Override
	public boolean isCommunicationAccepted(){
		return isCommunicationIdReceived() && getCommunicator().isCommunicationAccepted(getCommunicationIdFile().getData());
	}
	
	@Override
	public boolean isCommunicationFinished(){
		return isCommunicationResponseReceived() && getCommunicator().isCommunicationFinished(getResponseFile().getData());
	}
	
	@Override
	public void loadContrataData(IAttachment attach) throws ManagerBeanException, IOException{
		getHandler().loadContrataData(getGeneratedFile());
	}
	

	private void reset(){
		setShowLoginWindow(false);
		updateRequired = false;
		handler = null;
		communicator = null;
		generatedFile = null;
		communicationIdFile = null;
		responseFile = null;
		batch = null;
		newBatch = false;
	}
	
	public void initialize(ContrataBatch batch){
		reset();
		if(batch == null){
			String msg = "No se a podido establecer el tipo de comunicación.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		this.batch = batch;
		setGeneratedFile(obtainContrataAttach(SepeBatchAttachmentType.GENERATED_FILE));
		setCommunicationIdFile(obtainContrataAttach(SepeBatchAttachmentType.COMMUNICATION_ID));
		setResponseFile(obtainContrataAttach(SepeBatchAttachmentType.RESPONSE_FILE));
		
		//TODO prorrogas? transformaciones?  
		if(isContratoFile()){
			setHandler( new ContrataContratosHandler() );
		} else if(isProrrogaFile()){
			setHandler( new ContrataProrrogasHandler() );
		} else if(isTransformacionFile()){
			setHandler( new ContrataTransformacionesHandler() );
		}
		
	}
	
	public void initialize(Contract contract){
		reset();
		if(contract == null){
			String msg = "No se a podido establecer el tipo de comunicación.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		ContractAttachmentType generatedType = null;
		ContractAttachmentType communicationIdType = null;
		ContractAttachmentType responseType = null;
		if(isContratoFile()){
			setHandler( new ContrataContratosHandler() );
			generatedType = ContractAttachmentType.SEPE_CONTRACT_FILE;
			communicationIdType = ContractAttachmentType.SEPE_CONTRACT_COMMUNICATION_ID;
			responseType = ContractAttachmentType.SEPE_CONTRACT_RESPONSE;
		} else if(isProrrogaFile()){
			setHandler( new ContrataProrrogasHandler() );
			generatedType = ContractAttachmentType.SEPE_EXTENSION_FILE;
			communicationIdType = ContractAttachmentType.SEPE_EXTENSION_COMMUNICATION_ID;
			responseType = ContractAttachmentType.SEPE_EXTENSION_RESPONSE;
		} else if(isTransformacionFile()){
			setHandler( new ContrataTransformacionesHandler() );
			generatedType = ContractAttachmentType.SEPE_TRANSFORM_FILE;
			communicationIdType = ContractAttachmentType.SEPE_TRANSFORM_COMMUNICATION_ID;
			responseType = ContractAttachmentType.SEPE_TRANSFORM_RESPONSE;
		}
		getHandler().initialize(contract);
		setGeneratedFile(obtainContrataAttach(generatedType));
		setCommunicationIdFile(obtainContrataAttach(communicationIdType));
		setResponseFile(obtainContrataAttach(responseType));
	}
	
	public void onContrataCommunicatioShow(ActionEvent event) {
		onContrataDataShow(event);
		try {
			generateContractContrataFile(getContract());
			updateContrataFile();
		} catch (ManagerBeanException e) {
			// nada
		}
	}
	
	public void onContrataDataShow(ActionEvent event) {
		setEnabledContrataEdition(true);
		
		if( getContractCode()==null ){
			setEnabledContrataEdition(false);
		} else {
			String code = getContractCode().getValue();
			
			if( code.equals(ContractCode.C109.getValue())
					|| code.equals(ContractCode.C139.getValue())
					|| code.equals(ContractCode.C189.getValue())
					|| code.equals(ContractCode.C209.getValue())
					|| code.equals(ContractCode.C239.getValue())
					|| code.equals(ContractCode.C289.getValue())
					|| code.equals(ContractCode.C309.getValue())
					|| code.equals(ContractCode.C389.getValue()) ){
				// Transformaciones de contrato
				setEnabledContrataEdition(false);
//			String msg = "Transformaciones de contrato sin implementación para comunicaciones con Contrat@.";
//			AonUtil.addErrorMessage(msg);
//			throw new AbortProcessingException(msg);
			} else if( code.equals(ContractCode.C408.getValue())
					|| code.equals(ContractCode.C418.getValue())
					|| code.equals(ContractCode.C508.getValue())
					|| code.equals(ContractCode.C518.getValue()) ){
				// Contratos de caracter administrativo
				setEnabledContrataEdition(false);
//			String msg = "Tipo de contrato sin implementación para comunicaciones con Contrat@. (Códigos de contrato 408, 418, 508 y 518)";
//			AonUtil.addErrorMessage(msg);
//			throw new AbortProcessingException(msg);
			}
			
			try {
//				if(isEnabledContrataEdition()){
					loadContrataData(getGeneratedFile());
					setUpdateRequired(true);
//				}
			} catch (ManagerBeanException e) {
				String msg = "No se han podido obtener los datos de Contrat@ previamente guardados.";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(msg, e);
			} catch (IOException e) {
				String msg = "No se han podido obtener los datos de Contrat@ previamente guardados.";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(msg, e);
			}
		}
		
	}
	
	public void onContrataAccept( ActionEvent event ) {
		try {
			if(getGeneratedFile()==null){
				setGeneratedFile(new ContractAttachment());
			}			
			
			generateContractContrataFile(getContract());
			
			beforeContrataAccept();
			updateContrataFile();
			afterContrataAccept();
		} catch (ManagerBeanException e) {
			String msg = "No se han podido guardar los datos de Contrat@";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
	}
	
	private void updateContrataFile(){
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			bean.insertOrUpdate(getGeneratedFile());
		} catch (ManagerBeanException e) {
			String msg = "No se han podido guardar los datos de Contrat@";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
		}
	}

	private void beforeContrataAccept() throws ManagerBeanException {
		if( isNevv() && (isProrrogaFile() || isTransformacionFile()) ){
			
			IManagerBean bean;
			try {
				bean = BeanManager.getManagerBean(ContractInfo.class);
			} catch (ManagerBeanException e) {
				String msg = "Imposible grabar los datos de contrato. (" +e.getMessage() + ")";
				throw new AbortProcessingException(msg,e);
			}
			ContractInfo info;
			info = new ContractInfo();
			info.setContract( getContract() );
			info.setStartDate( getContract().getStartDate() );
			info.setEndDate( getContract().getEndDate() );
			if(isContratoFile()){
				info.setName( ContractVariable.SEPE_CONTRACT.getValue() );
			} else if(isProrrogaFile()){
				info.setName( ContractVariable.SEPE_EXTENSION.getValue() );
			} else if(isTransformacionFile()){
				info.setName( ContractVariable.SEPE_TRANSFORM.getValue() );
			}
			info.setExpression( ContractSepeStatus.PENDING.getValue() );
			bean.insert(info);
		}
		
	}
	private void afterContrataAccept() throws ManagerBeanException {
		if(isProrrogaFile()){
			IManagerBean bean;
			try {
				bean = BeanManager.getManagerBean(Contract.class);
			} catch (ManagerBeanException e) {
				String msg = "Imposible grabar los datos de contrato. (" +e.getMessage() + ")";
				throw new AbortProcessingException(msg,e);
			}
			getContract().setEndDate(((ContrataProrrogaParams)getHandler().getParams()).getFechaFin());
			bean.restoreNullSubPOJOs(getContract());
			bean.update(getContract());
		}
		
	}
	
	public void undoContractExtension() {
		if( isProrrogaFile() ){
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
				if(getGeneratedFile()!=null && getGeneratedFile().getId()!=null){
					bean.remove(getGeneratedFile().getId());
				}
				if(getCommunicationIdFile()!=null && getCommunicationIdFile().getId()!=null){
					bean.remove(getCommunicationIdFile().getId());
				}
				if(getResponseFile()!=null && getResponseFile().getId()!=null){
					bean.remove(getResponseFile().getId());
				}
			} catch (ManagerBeanException e) {
				String msg = "Imposible borrar los datos de la prorroga (ficheros SEPE). (" +e.getMessage() + ")";
				throw new AbortProcessingException(msg,e);
			}
		}
	}

	public boolean validateContrataData() {
		if(getContract()!=null && getBatch()==null){
			InputStream is = new ByteArrayInputStream(getGeneratedFile().getData());
			String contractCode = getHandler().getContractCode().getValue();
			String schema = null;
			if( contratoFile ){
				schema = SEPEFileUtils.CONTRATOS_SCHEMA_FILE_NAME;
			} else if( transformacionFile ) {
				schema = SEPEFileUtils.TRANSFORMACIONES_SCHEMA_FILE_NAME;
			} else if( prorrogaFile ) {
				schema = SEPEFileUtils.PRORROGAS_SCHEMA_FILE_NAME;
			}
			try {
				SEPEFileUtils.validateContrataXmlPattern(is, schema, contractCode);
			} catch (SAXException saxe) {
				String msg = "Error de validación de Contrat@ (ausencia de datos o formato no correcto)";
				AonUtil.addErrorMessage(msg);
				AonUtil.addErrorMessage(saxe.getMessage() );
				return false;
			} catch (IOException ioe) {
				String msg = "Error de I/O al validar los datos";
				AonUtil.addErrorMessage(msg);
				AonUtil.addErrorMessage(ioe.getMessage() );
				return false;
			} catch (Exception e) {
				String msg = "Error general al validar los datos";
				AonUtil.addErrorMessage(msg);
				AonUtil.addErrorMessage(e.getMessage() );
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void onResetBatch(ActionEvent event) {
		batch = new ContrataBatch();
		newBatch = true;
	}

	@Override
	public void onBatchAccept(ActionEvent event) {
		if(isNewBatch()){
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContrataBatch.class);
				if(isContratoFile()){
					batch.setType(ContrataFileType.CONTRACT);
				} else if(isProrrogaFile()){
					batch.setType(ContrataFileType.EXTENSION);
				} else if(isTransformacionFile()){
					batch.setType(ContrataFileType.TRANSFORMATION);
				} else {
					String msg = "No se ha definido correctamente el tipo de la remesa.";
					LOGGER.error(msg);
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
				batch.setStatus(FileStatus.PENDING);
				batch = (ContrataBatch) bean.insert(batch);
			} catch (ManagerBeanException e) {
				String msg = "No se ha podido crear la remesa";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(msg, e);
			}
		}
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContrataBatchDetail.class);
			ContrataBatchDetail detail = new ContrataBatchDetail();
			detail.setContrataBatch(batch);
			detail.setContract(getContract());
			bean.insert(detail);
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido incluir el contrato en la remesa";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
	}
	
	@Override
	public void onDownloadSepeXml(ActionEvent event){
		byte[] data = getGeneratedFile().getData();
		InputStream in = new ByteArrayInputStream(data);
		long size = ArrayUtils.getLength(data);
		DownloadUtil.downloadAttachment("contrato-"+getContract().getPerson().getRegistry().getDocument(), MimeType.MIME_XML, in, size);
	}
	
	
	private IAttachment obtainContrataAttach(ContractAttachmentType type){
		try {
			if(getContract()!=null && getContract().getId()!=null){
				IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), getContract().getId());
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), type);
				List<ITransferObject> list = bean.getList(criteria);
				if(!list.isEmpty()){
					return (ContractAttachment) list.get(0);
				}
			}
		} catch (ManagerBeanException e) {
			// NOTHING TO DO
			String msg = "No se ha podido obtener el fichero " + type.getName(AonUtil.getCurrentLocale());
			AonUtil.addErrorMessage(msg);
			LOGGER.error("Error obtaining contrat@ contract attach");
		}
		return null;
	}
	
	private IAttachment obtainContrataAttach(SepeBatchAttachmentType type){
		try {
			if(batch!=null && batch.getId()!=null){
				IManagerBean bean = BeanManager.getManagerBean(SepeBatchAttachment.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SEPE_BATCH_ATTACHMENT_SOURCE_BATCH), getBatch().getId());
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SEPE_BATCH_ATTACHMENT_ATTACHMENT_TYPE), type);
				List<ITransferObject> list = bean.getList(criteria);
				if(!list.isEmpty()){
					return (IAttachment) list.get(0);
				}
			}
		} catch (ManagerBeanException e) {
			// NOTHING TO DO
			String msg = "No se ha podido obtener el fichero " + type.getName(AonUtil.getCurrentLocale());
			AonUtil.addErrorMessage(msg);
			LOGGER.error("Error obtaining contrat@ batch attach");
		}
		return null;
	}
	
	private void generateContractContrataFile(Contract contract) throws ManagerBeanException{
		ContrataWriter writer = new ContrataWriter(contract);
		writer.setContratoFile(isContratoFile());
		writer.setProrrogaFile(isProrrogaFile());
		writer.setTransformacionFile(isTransformacionFile());
	
		try {
			File file = writer.createFile(getParams());
			FileInputStream fis = new FileInputStream(file);
			byte fileContent[] = new byte[(int)file.length()];
			fis.read(fileContent);
			ContractAttachment attach = (ContractAttachment) getGeneratedFile();
			attach.setContract(contract);
			attach.setData(fileContent);
			attach.setAttachDate(new Date());
			if(isContratoFile()){
				attach.setAttachmentType(ContractAttachmentType.SEPE_CONTRACT_FILE);
				attach.setDescription("CONTRATO - Contrat@");
			} else if(isProrrogaFile()){
				attach.setAttachmentType(ContractAttachmentType.SEPE_EXTENSION_FILE);
				attach.setDescription("PRORROGA - Contrat@");
			} else if(isTransformacionFile()){
				attach.setAttachmentType(ContractAttachmentType.SEPE_TRANSFORM_FILE);
				attach.setDescription("TRANSFORMACION - Contrat@");
			}
			attach.setMimeType(MimeType.MIME_XML);
			fis.close();
		} catch(IOException e) {
			String msg = "No se han podido guardar los datos de Contrat@";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		} 
	}
	
	@Override
	public void onSendSepeFile(ActionEvent event){
		if( validateContrataData() ){
			getCommunicator().setDataCommunication(true);
			if(!isShowLoginWindow()){
				getCommunicator().initialize();
			}
			if( getCommunicator().isLoginRequired() ){
				setShowLoginWindow(true);
			} else {
				getCommunicator().setContrataFileType(getContrataFileType());
				getCommunicator().setDocument(new String(getGeneratedFile().getData()));
				String result = getCommunicator().communicate();
				if(isBatchView()){
					saveSepeResponseFile(SepeBatchAttachmentType.COMMUNICATION_ID, result);
				} else if(!isBatchView()){
					if(isContratoFile()){
						saveSepeResponseFile(ContractAttachmentType.SEPE_CONTRACT_COMMUNICATION_ID, result);
					} else if(isProrrogaFile()){
						saveSepeResponseFile(ContractAttachmentType.SEPE_EXTENSION_COMMUNICATION_ID, result);
					} else if(isTransformacionFile()){
						saveSepeResponseFile(ContractAttachmentType.SEPE_TRANSFORM_COMMUNICATION_ID, result);
					}
				} else {
					String msg = "No se ha podido guardar la respuesta obtenida del SEPE";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
				setShowLoginWindow(false);
			}
		}
	}
	
	@Override
	public void onSepeDataQuery(ActionEvent event){
		getCommunicator().setDataQuery(true);
		String document = getCommunicator().obtainCommunicationNumber(getCommunicationIdFile().getData());
		if( StringUtils.isBlank(document) ){
			String msg = "No se puede obtener el número del envío de la comunicación.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		if(!isShowLoginWindow()){
			getCommunicator().initialize();
		}
		if( getCommunicator().isLoginRequired() ){
			setShowLoginWindow(true);
		} else {
			getCommunicator().setContrataFileType(getContrataFileType());
			getCommunicator().setDocument(document);
			String result = getCommunicator().communicate();
			if(isBatchView()){
				saveSepeResponseFile(SepeBatchAttachmentType.RESPONSE_FILE, result);
			} else if(!isBatchView()){
				if(isContratoFile()){
					saveSepeResponseFile(ContractAttachmentType.SEPE_CONTRACT_RESPONSE, result);
				} else if(isProrrogaFile()){
					saveSepeResponseFile(ContractAttachmentType.SEPE_EXTENSION_RESPONSE, result);
				} else if(isTransformacionFile()){
					saveSepeResponseFile(ContractAttachmentType.SEPE_TRANSFORM_RESPONSE, result);
				}
			} else {
				String msg = "No se ha podido guardar la respuesta obtenida del SEPE";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			
			setShowLoginWindow(false);
			processSepeResult(result);
		}
	}
	
	public void onRemoveSepeFiles(ActionEvent event){
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			if(getCommunicationIdFile()!=null && getCommunicationIdFile().getId()!=null){
				bean.remove(getCommunicationIdFile());
			}
			if(getResponseFile()!=null && getResponseFile().getId()!=null){
				bean.remove(getResponseFile());
			}
			initialize(getContract());
		} catch (ManagerBeanException e) {
			String msg = "No se han podido guardar los datos de respuesta de Contrat@";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
	}
	
	public void onProcessSepeResult(ActionEvent event){
		processSepeResult(new String(getResponseFile().getData()));
	}
	
	private void processSepeResult(String result) {
		
		final String ACCEPTED 				= "ACEPTADO";
		final String ACCEPTED_WITH_ERRORS 	= "ACEPTADO CON ERRORES";
		final String REJECTED 				= "RECHAZADO";
		final String WRONG_CONTRACT_ID 		= "E0000000000000";
		
		if(result!=null && (result.contains(ACCEPTED) || result.contains(ACCEPTED_WITH_ERRORS)) && !result.contains(REJECTED)){
			try {
				getCommunicator().setContrataFileType(getContrataFileType());
				FICHEROCONTRATOS contratos = getCommunicator().obtainFicheroContratos(result.getBytes());
				for(Object o: contratos.getCONTRATOSPROCESADOS().getENVIO100AndENVIO130AndENVIO150()){
					RESPUESTACONTRATOTYPE respuestaContratos = getCommunicator().obtainRespuestaContrato(o);
					try {
						if(!respuestaContratos.getIDCONTRATO().equals(WRONG_CONTRACT_ID)){
							Contract contract = isBatchView()?obtainContract(respuestaContratos):getContract();
							IManagerBean bean = BeanManager.getManagerBean(ContractInfo.class);
							ContractInfo info = new ContractInfo();
							info.setContract(contract);
							info.setStartDate(contract.getStartDate());
							info.setEndDate(contract.getEndDate());
							info.setName( ContractVariable.SEPE_CONTRACT_ID.getValue() );
							info.setExpression("\"" + respuestaContratos.getIDCONTRATO() + "\"");
							bean.insert(info);
						}
					} catch (ManagerBeanException e) {
						String msg = "Error al grabar el ID de contrato obtenido del SEPE. (" +e.getMessage() + ")";
						AonUtil.addErrorMessage(msg);
					}
				}
			} catch (IOException e) {
				String msg = "No se han podido procesar los datos de respuesta de Contrat@";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				AonUtil.addErrorMessage(e.getMessage());
			} catch (JAXBException e) {
				String msg = "No se han podido procesar los datos de respuesta de Contrat@";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				AonUtil.addErrorMessage(e.getMessage());
			} catch (SAXException e) {
				String msg = "No se han podido procesar los datos de respuesta de Contrat@";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				AonUtil.addErrorMessage(e.getMessage());
			} catch (ParserConfigurationException e) {
				String msg = "No se han podido procesar los datos de respuesta de Contrat@";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				AonUtil.addErrorMessage(e.getMessage());
			}
		}
	}
	
	private Contract obtainContract(RESPUESTACONTRATOTYPE respuestaContratos) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getCommunicationLogContent() {
		getCommunicator().setContrataFileType(getContrataFileType());
		String communicationLogContent = "<div>";
		if( isCommunicationIdReceived() ){
			if( getCommunicationIdFile().getData()!=null ){
				communicationLogContent += "<div style='background-color:#E4E4E4; width:100%; padding:5px;'><b>Datos comunicados al SEPE</b></div>";
				communicationLogContent += "NUM ENVIO:         " + getCommunicator().obtainCommunicationNumber(getCommunicationIdFile().getData());
			}
		}
		if( isCommunicationResponseReceived() ){
			String status = getCommunicator().obtainCommunicationStatus(getResponseFile().getData());
			if(StringUtils.isNotEmpty(status)){
				communicationLogContent += status;
			}
		}
		communicationLogContent += "</div>";
		return communicationLogContent;
	}



	private void saveSepeResponseFile(SepeBatchAttachmentType type, String data){
		ContrataBatchAttachment resultAttach = (ContrataBatchAttachment) obtainContrataAttach(type);
		if(resultAttach==null){
			resultAttach = new ContrataBatchAttachment();
			if(type == SepeBatchAttachmentType.COMMUNICATION_ID){
				resultAttach.setDescription("ID comunicacion contrat@");
			} else if(type == SepeBatchAttachmentType.RESPONSE_FILE){
				resultAttach.setDescription("Respuesta contrat@");
			}
			resultAttach.setContrataBatch(getBatch());
			resultAttach.setAttachmentType(type);
			resultAttach.setMimeType(MimeType.MIME_XML);
		}
		try {
			InputStream is = new ByteArrayInputStream(data.getBytes());
			byte fileContent[] = new byte[data.length()];
			is.read(fileContent);
			resultAttach.setData(fileContent);
			is.close();
		} catch(IOException e) {
			String msg = "No se han podido guardar los datos de respuesta de Contrat@";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		} 
		
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContrataBatchAttachment.class);
			resultAttach.setAttachDate(new Date());
			ContrataBatchAttachment attach = (ContrataBatchAttachment) bean.insertOrUpdate(resultAttach);
			if(type == SepeBatchAttachmentType.COMMUNICATION_ID){
				setCommunicationIdFile(attach);
			} else if(type == SepeBatchAttachmentType.RESPONSE_FILE){
				setResponseFile(attach);
			}
		} catch (ManagerBeanException e) {
			String msg = "No se han podido guardar los datos de respuesta de Contrat@";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
	}
	
	private void saveSepeResponseFile(ContractAttachmentType type, String data){
		ContractAttachment resultAttach = (ContractAttachment) obtainContrataAttach(type);
		if(resultAttach==null){
			resultAttach = new ContractAttachment();
			if(type == ContractAttachmentType.SEPE_CONTRACT_COMMUNICATION_ID){
				resultAttach.setDescription("ID comunicacion Contrat@ (contrato)");
			} else if(type == ContractAttachmentType.SEPE_CONTRACT_RESPONSE){
				resultAttach.setDescription("Respuesta Contrat@ (contrato)");
			} else if(type == ContractAttachmentType.SEPE_EXTENSION_COMMUNICATION_ID){
				resultAttach.setDescription("ID comunicacion Contrat@ (prorroga)");
			} else if(type == ContractAttachmentType.SEPE_EXTENSION_RESPONSE){
				resultAttach.setDescription("Respuesta Contrat@ (prorroga)");
			} else if(type == ContractAttachmentType.SEPE_TRANSFORM_COMMUNICATION_ID){
				resultAttach.setDescription("ID comunicacion Contrat@ (transformacion)");
			} else if(type == ContractAttachmentType.SEPE_TRANSFORM_RESPONSE){
				resultAttach.setDescription("Respuesta Contrat@ (transformacion)");
			}
			resultAttach.setAttachmentType(type);
			resultAttach.setContract(getHandler().getContract());
			resultAttach.setMimeType(MimeType.MIME_XML);
		}
		try {
			InputStream is = new ByteArrayInputStream(data.getBytes());
			byte fileContent[] = new byte[data.length()];
			is.read(fileContent);
			resultAttach.setData(fileContent);
			is.close();
		} catch(IOException e) {
			String msg = "No se han podido guardar los datos de respuesta de Contrat@";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		} 
		
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			resultAttach.setAttachDate(new Date());
			ContractAttachment attach = (ContractAttachment) bean.insertOrUpdate(resultAttach);
			if(type == ContractAttachmentType.SEPE_CONTRACT_COMMUNICATION_ID
					|| type == ContractAttachmentType.SEPE_EXTENSION_COMMUNICATION_ID
					|| type == ContractAttachmentType.SEPE_TRANSFORM_COMMUNICATION_ID){
				setCommunicationIdFile(attach);
			} else if(type == ContractAttachmentType.SEPE_CONTRACT_RESPONSE
					|| type == ContractAttachmentType.SEPE_EXTENSION_RESPONSE
					|| type == ContractAttachmentType.SEPE_TRANSFORM_RESPONSE){
				setResponseFile(attach);
			}
		} catch (ManagerBeanException e) {
			String msg = "No se han podido guardar los datos de respuesta de Contrat@";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
	}
	
	
}
