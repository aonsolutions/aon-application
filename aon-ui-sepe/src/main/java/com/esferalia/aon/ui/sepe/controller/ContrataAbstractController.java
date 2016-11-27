package com.esferalia.aon.ui.sepe.controller;

import static com.esferalia.aon.jooq.tables.ContrataBatch.CONTRATA_BATCH;
import static com.esferalia.aon.jooq.tables.ContrataBatchDetail.CONTRATA_BATCH_DETAIL;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectSeekStep1;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContrataBatch;
import com.esferalia.aon.payroll.ContrataBatchAttachment;
import com.esferalia.aon.payroll.ContrataBatchDetail;
import com.esferalia.aon.payroll.SepeBatchAttachment;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContrataFileType;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.SepeBatchAttachmentType;
import com.esferalia.aon.ui.sepe.controller.handler.IContrataHandler;
import com.esferalia.aon.ui.sepe.file.ContrataWriter;
import com.esferalia.aon.ui.sepe.utils.ContrataCommunicator;
import com.esferalia.aon.ui.sepe.utils.SEPEFileUtils;


public abstract class ContrataAbstractController implements IContrataController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContrataAbstractController.class.getName());
	
	private boolean showLoginWindow;
	private boolean enabledContrataEdition;
	
	private boolean showContrataWindow;
	
	private boolean newBatch;
	private boolean readOnly;
	
	private IContrataHandler handler;
	
	private ContrataCommunicator communicator;
	
	private IAttachment generatedFile;
	private IAttachment communicationIdFile;
	private IAttachment responseFile;

	private ContrataBatch batch;

	

	public abstract ContrataFileType getContrataFileType();
	public abstract String getContrataModelName();
	protected abstract void initContrataFile();
	protected abstract void initHandler();
	protected abstract void beforeContrataAccept() throws ManagerBeanException;
	protected abstract String getSchemaFileName();
	protected abstract ContractAttachmentType getAttachmentType();	
	protected abstract void processSepeResult(String result);
	

	public boolean isEnabledContrataEdition() {
		return enabledContrataEdition;
	}
	public void setEnabledContrataEdition(boolean enabledContrataEdition) {
		this.enabledContrataEdition = enabledContrataEdition;
	}

	public boolean isShowContrataWindow() {
		return showContrataWindow;
	}
	public void setShowContrataWindow(boolean showContrataWindow) {
		this.showContrataWindow = showContrataWindow;
	}
	
	
	@Override
	public boolean isShowLoginWindow() {
		return showLoginWindow;
	}
	public void setShowLoginWindow(boolean showLoginWindow) {
		this.showLoginWindow = showLoginWindow;
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
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
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
		handler = null;
		communicator = null;
		generatedFile = null;
		communicationIdFile = null;
		responseFile = null;
		batch = null;
		newBatch = false;
		readOnly = false;
		initHandler();
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
	}
	
	public void initialize(Contract contract){
		reset();
		if(contract == null){
			String msg = "No se a podido establecer el tipo de comunicación.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		ContrataBatch batch = obtainBatch(contract);
		if(batch != null){
			initialize(batch);
			setGeneratedFile(null);
		}
		getHandler().initialize(contract);
		initContrataFile();
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
//			afterContrataAccept();
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

	
	public boolean validateContrataData() {
		if(getContract()!=null && getBatch()==null){
			InputStream is = new ByteArrayInputStream(getGeneratedFile().getData());
			String contractCode = getHandler().getContractCode().getValue();
			String schema = getSchemaFileName();
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
				if(getContrataFileType()!=null){
					batch.setType(getContrataFileType());
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
			detail.setStatus(FileStatus.PENDING);
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
	
	
	public SelectSeekStep1<Record3<Integer,Timestamp,Integer>,Timestamp> getBatchSelect(AONContext ctx, Contract contract){
		ContrataFileType type = null;
		if(getContrataFileType()!=null){
			type = getContrataFileType();
		} else {
			throw new IllegalArgumentException("Tipo de fichero no disponible.");
		}
		SelectConditionStep<Record1<Integer>> contractBatchSelect = ctx.getDslContext()
				.select(CONTRATA_BATCH_DETAIL.CONTRATA_BATCH)
				.from(CONTRATA_BATCH_DETAIL)
				.where(CONTRATA_BATCH_DETAIL.CONTRACT.eq(contract.getId()));
		return ctx.getDslContext().select(CONTRATA_BATCH.ID,CONTRATA_BATCH.DATE,DSL.count(CONTRATA_BATCH_DETAIL.CONTRACT))
				.from(CONTRATA_BATCH).leftOuterJoin(CONTRATA_BATCH_DETAIL).onKey()
				.where(CONTRATA_BATCH_DETAIL.CONTRATA_BATCH.in(contractBatchSelect))
				.and(CONTRATA_BATCH.TYPE.eq((byte) type.ordinal()))
				.groupBy(CONTRATA_BATCH.ID)
				.orderBy(CONTRATA_BATCH.DATE.desc());
	}
//	private Integer obtainBatchContractCount(Contract contract){
//		AONContext ctx = AONContext.getAONContext(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), AonUtil.getRemoteUser());
//		Result<Record3<Integer, Timestamp, Integer>> result = getBatchSelect(ctx, contract).fetch();
//		if(result.size()>0){
//			return result.get(0).value3();
//		}
//		return 0;
//	}
	private ContrataBatch obtainBatch(Contract contract){
		AONContext ctx = AONContext.getAONContext(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), AonUtil.getRemoteUser());
		Result<Record3<Integer, Timestamp, Integer>> result = getBatchSelect(ctx, contract).fetch();
		try {
			if(result.size()>0){
				IManagerBean bean = BeanManager.getManagerBean(ContrataBatch.class);
				return (ContrataBatch) bean.get(result.get(0).value1());
			}
		} catch (ManagerBeanException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
		}
		return null;
	}
	
	protected List<IAttachment> obtainContrataList(ContractAttachmentType type){
		try {
			if(getContract()!=null && getContract().getId()!=null){
				IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), getContract().getId());
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), type);
				criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACH_DATE), true);
				List<ITransferObject> list = bean.getList(criteria);
				if(!list.isEmpty()){
					return list.stream().map(to -> (ContractAttachment)to).collect(Collectors.toList());
				}
			}
		} catch (ManagerBeanException e) {
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
			String msg = "No se ha podido obtener el fichero " + type.getName(AonUtil.getCurrentLocale());
			AonUtil.addErrorMessage(msg);
			LOGGER.error("Error obtaining contrat@ batch attach");
		}
		return null;
	}
	
	private void generateContractContrataFile(Contract contract) throws ManagerBeanException{
		ContrataWriter writer = new ContrataWriter(contract, getContrataFileType());
	
		try {
			File file = writer.createFile(getParams());
			FileInputStream fis = new FileInputStream(file);
			byte fileContent[] = new byte[(int)file.length()];
			fis.read(fileContent);
			ContractAttachment attach = (ContractAttachment) getGeneratedFile();
			attach.setContract(contract);
			attach.setData(fileContent);
			attach.setAttachDate(new Date());
			attach.setAttachmentType(getAttachmentType());
			attach.setDescription(getAttachmentType().toString()
					.replaceAll("SEPE", "")
					.replaceAll("FILE", "")
					.replaceAll("_", "") + " - Contrat@");
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
				saveSepeResponseFile(SepeBatchAttachmentType.COMMUNICATION_ID, result);
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
			saveSepeResponseFile(SepeBatchAttachmentType.RESPONSE_FILE, result);
			setShowLoginWindow(false);
			processSepeResult(result);
		}
	}
	
	public void onRemoveSepeFiles(ActionEvent event){
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContrataBatchAttachment.class);
			if(getCommunicationIdFile()!=null && getCommunicationIdFile().getId()!=null){
				bean.remove(getCommunicationIdFile());
			}
			if(getResponseFile()!=null && getResponseFile().getId()!=null){
				bean.remove(getResponseFile());
			}
			if(getGeneratedFile()!=null && getGeneratedFile().getId()!=null){
				bean.remove(getGeneratedFile());
			}
			if(isBatchView()){
				initialize(getBatch());
			} else {
				initialize(getContract());
			}
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
			String status = getCommunicator().obtainCommunicationStatus(getResponseFile().getData(), getContract());
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
	
	
}
