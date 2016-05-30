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
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Certifica2Batch;
import com.esferalia.aon.payroll.Certifica2BatchAttachment;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.SepeBatchAttachment;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.SepeBatchAttachmentType;
import com.esferalia.aon.payroll.enumeration.SuspensionCause;
import com.esferalia.aon.ui.sepe.file.CertificadosWriter;
import com.esferalia.aon.ui.sepe.utils.CertificadosCommunicator;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;


public class CertificadosController implements ISepeHandler, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CertificadosController.class.getName());
	
	private boolean showBatchWindow;
	private boolean showCommunicationWindow;
	private boolean showLoginWindow;
	private boolean showGenerationWindow;
	
	private boolean newBatch;
	
	private CertificadosCommunicator communicator;
	
	private Certifica2Batch batch;

	private Contract contract;
	private SuspensionCause suspensionCause;
	
	private IAttachment generatedFile;
	private IAttachment communicationIdFile;
	private IAttachment responseFile;
	
	
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
	
	public boolean isShowGenerationWindow() {
		return showGenerationWindow;
	}
	public void setShowGenerationWindow(boolean showGenerationWindow) {
		this.showGenerationWindow = showGenerationWindow;
	}
	
	@Override
	public CertificadosCommunicator getCommunicator() {
		if(communicator==null){
			communicator = new CertificadosCommunicator();
		}
		return communicator;
	}
	
	public void setCommunicator(CertificadosCommunicator communicator) {
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
	public List<SelectItem> getPendingBatchList() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(Certifica2Batch.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_STATUS), FileStatus.PENDING);
			for(ITransferObject to: bean.getList(criteria)){
				Certifica2Batch batch = (Certifica2Batch) to;
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
		return getGeneratedFile()!=null && getGeneratedFile().getId()!=null;
	}
	
	public Certifica2Batch getBatch() {
		return batch;
	}

	public void setBatch(Certifica2Batch batch) {
		this.batch = batch;
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
	public SuspensionCause getSuspensionCause() {
		return suspensionCause;
	}
	public void setSuspensionCause(SuspensionCause suspensionCause) {
		this.suspensionCause = suspensionCause;
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
	public boolean isCommunicationFinished() {
		return isCommunicationResponseReceived() && getCommunicator().isCommunicationFinished(getResponseFile().getData());
	}
	
	private void reset(){
		setContract(null);
		setBatch(null);
		newBatch = false;
		suspensionCause = null;
		showLoginWindow = false;
		communicator = null;
		generatedFile = null;
		communicationIdFile = null;
		responseFile = null;
	}
	
	
	public void initialize(Certifica2Batch batch){
		reset();
		if(batch == null){
			String msg = "No se a podido establecer el tipo de comunicación.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		this.batch = batch;
		setGeneratedFile(obtainCertificadosAttach(SepeBatchAttachmentType.GENERATED_FILE));
		setCommunicationIdFile(obtainCertificadosAttach(SepeBatchAttachmentType.COMMUNICATION_ID));
		setResponseFile(obtainCertificadosAttach(SepeBatchAttachmentType.RESPONSE_FILE));
	}
	
	public void initialize(Contract contract){
		reset();
		if(contract == null){
			String msg = "No se a podido establecer el tipo de comunicación.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		this.contract = contract;
		setGeneratedFile(obtainCertificadosAttach(ContractAttachmentType.SEPE_CERTIFICADOS_FILE));
		setCommunicationIdFile(obtainCertificadosAttach(ContractAttachmentType.SEPE_CERTIFICADOS_COMMUNICATION_ID));
		setResponseFile(obtainCertificadosAttach(ContractAttachmentType.SEPE_CERTIFICADOS_RESPONSE));
	}
	
	public void onCertificadosAccept( ActionEvent event ) {
		try {
			generateContractCertificadosFile(getContract());
			ContractAttachment attach = new ContractAttachment();
			attach = (ContractAttachment) getGeneratedFile();
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			attach.setAttachDate(new Date());
			attach.setAttachmentType(ContractAttachmentType.SEPE_CERTIFICADOS_FILE);
			bean.insertOrUpdate(attach);
			setShowCommunicationWindow(true);
			setShowGenerationWindow(false);
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido generar el certificado de empresa.";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
	}
	
	@Override
	public void onResetBatch(ActionEvent event) {
		batch = new Certifica2Batch();
		newBatch = true;
	}

	@Override
	public void onBatchAccept(ActionEvent event) {
		if(isNewBatch()){
			try {
				IManagerBean bean = BeanManager.getManagerBean(Certifica2Batch.class);
				batch.setStatus(FileStatus.PENDING);
				batch.setEnterprise(getContract().getWorkPlace().getEnterprise());
				batch = (Certifica2Batch) bean.insert(batch);
			} catch (ManagerBeanException e) {
				String msg = "No se ha podido crear la remesa";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(msg, e);
			}
		}
		try {
			IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
			Certifica2BatchDetail detail = new Certifica2BatchDetail();
			detail.setCertifica2Batch(batch);
			detail.setContract(getContract());
//			detail.setSuspensionCause(getSuspensionCause());
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
		DownloadUtil.downloadAttachment("certificado-empresa_"+getContract().getPerson().getFullName(), MimeType.MIME_XML, in, size);
	}

	
	private IAttachment obtainCertificadosAttach(ContractAttachmentType type){
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
			LOGGER.error("Error obtaining certific@2 contract attach");
		}
		return null;
	}
	
	private IAttachment obtainCertificadosAttach(SepeBatchAttachmentType type){
		try {
			if(batch!=null && batch.getId()!=null){
				IManagerBean bean = BeanManager.getManagerBean(SepeBatchAttachment.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SEPE_BATCH_ATTACHMENT_SOURCE_BATCH), getBatch().getId());
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SEPE_BATCH_ATTACHMENT_ATTACHMENT_TYPE), type);
				SEPEUtils utils = SEPEUtils.getInstance();
				utils.completeChildDomainCriteria(criteria, bean.getFieldName(IEntityAlias.SEPE_BATCH_ATTACHMENT_DOMAIN));
				List<ITransferObject> list = bean.getList(criteria);
				if(!list.isEmpty()){
					return (IAttachment) list.get(0);
				}
			}
		} catch (ManagerBeanException e) {
			// NOTHING TO DO
			String msg = "No se ha podido obtener el fichero " + type.getName(AonUtil.getCurrentLocale());
			AonUtil.addErrorMessage(msg);
			LOGGER.error("Error obtaining certific@2 batch attach");
		}
		return null;
	}
	
	private void generateContractCertificadosFile(Contract contract) throws ManagerBeanException{
		CertificadosWriter writer = new CertificadosWriter();
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		Certifica2Batch batch = new Certifica2Batch();
		batch.setEnterprise(contract.getWorkPlace().getEnterprise());
		Certifica2BatchDetail detail = new Certifica2BatchDetail();
		detail.setContract(contract);
		if(detail.getSuspensionCause()==null){
			detail.setSuspensionCause(getSuspensionCause());
		}
		list.add(detail);
		try {
			File file = writer.createFile(batch, list);
			FileInputStream fis = new FileInputStream(file);
			byte fileContent[] = new byte[(int)file.length()];
			fis.read(fileContent);
			ContractAttachment attach = new ContractAttachment();
			attach.setContract(contract);
			attach.setData(fileContent);
			attach.setAttachmentType(ContractAttachmentType.SEPE_CERTIFICADOS_FILE);
			attach.setMimeType(MimeType.MIME_XML);
			attach.setDescription(writer.getFileName());
			setGeneratedFile(attach);
			fis.close();
		} catch(IOException e) {
			String msg = "No se han podido guardar los datos de Certific@2";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		} 
	}
	
	
	@Override
	public void onSendSepeFile(ActionEvent event){
		getCommunicator().setDataCommunication(true);
		if(!isShowLoginWindow()){
			getCommunicator().initialize();
		}
		if( getCommunicator().isLoginRequired() ){
			setShowLoginWindow(true);
		} else {
			getCommunicator().setDocument(new String(getGeneratedFile().getData()));
			String result = getCommunicator().communicate();
			if(isBatchView()){
				saveSepeResponseFile(SepeBatchAttachmentType.COMMUNICATION_ID, result);
			} else if(!isBatchView()){
				saveSepeResponseFile(ContractAttachmentType.SEPE_CERTIFICADOS_COMMUNICATION_ID, result);
			} else {
				String msg = "No se ha podido guardar la respuesta obtenida del SEPE";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			setShowLoginWindow(false);
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
			getCommunicator().setDocument(document);
			String result = getCommunicator().communicate();
			if(isBatchView()){
				saveSepeResponseFile(SepeBatchAttachmentType.RESPONSE_FILE, result);
			} else if(!isBatchView()){
				saveSepeResponseFile(ContractAttachmentType.SEPE_CERTIFICADOS_RESPONSE, result);
			} else {
				String msg = "No se ha podido guardar la respuesta obtenida del SEPE";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			
			setShowLoginWindow(false);
			processSepeResult(result);
		}
	}
	
	private void processSepeResult(String result) {
		// nada
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
			if(getGeneratedFile()!=null && getGeneratedFile().getId()!=null){
				bean.remove(getGeneratedFile());
			}
			initialize(getContract());
		} catch (ManagerBeanException e) {
			String msg = "No se han podido borrar los datos de Certific@2";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
	}
	
	@Override
	public String getCommunicationLogContent() {
		String communicationLogContent = "<div>";
		if( isCommunicationIdReceived() ){
			communicationLogContent += "<div style='background-color:#E4E4E4; width:100%; padding:5px;'><b>Datos comunicados al SEPE</b></div>";
			communicationLogContent += "NUM ENVIO:         " + getCommunicator().obtainCommunicationNumber(getCommunicationIdFile().getData());
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
		Certifica2BatchAttachment resultAttach = (Certifica2BatchAttachment) obtainCertificadosAttach(type);
		if(resultAttach==null){
			resultAttach = new Certifica2BatchAttachment();
			if(type == SepeBatchAttachmentType.COMMUNICATION_ID){
				resultAttach.setDescription("ID comunicacion Certific@2");
			} else if(type == SepeBatchAttachmentType.RESPONSE_FILE){
				resultAttach.setDescription("Respuesta Certific@2");
			}
			resultAttach.setCertifica2Batch(getBatch());
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
			String msg = "No se han podido guardar los datos de respuesta de Certific@2";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		} 
		
		try {
			IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchAttachment.class);
			resultAttach.setAttachDate(new Date());
			resultAttach.setDomain(getBatch().getEnterprise().getDomain());
			Certifica2BatchAttachment attach = (Certifica2BatchAttachment) bean.insertOrUpdate(resultAttach);
			if(type == SepeBatchAttachmentType.COMMUNICATION_ID){
				setCommunicationIdFile(attach);
			} else if(type == SepeBatchAttachmentType.RESPONSE_FILE){
				setResponseFile(attach);
			}
		} catch (ManagerBeanException e) {
			String msg = "No se han podido guardar los datos de respuesta de Certific@2";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
	}
	
	private void saveSepeResponseFile(ContractAttachmentType type, String data){
		ContractAttachment resultAttach = (ContractAttachment) obtainCertificadosAttach(type);
		if(resultAttach==null){
			resultAttach = new ContractAttachment();
			if(type == ContractAttachmentType.SEPE_CERTIFICADOS_COMMUNICATION_ID){
				resultAttach.setDescription("ID comunicacion Certific@2");
			} else if(type == ContractAttachmentType.SEPE_CERTIFICADOS_RESPONSE){
				resultAttach.setDescription("Respuesta Certifica@2");
			}
			resultAttach.setContract(getContract());
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
			String msg = "No se han podido guardar los datos de respuesta de Certific@2";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		} 
		
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			resultAttach.setAttachDate(new Date());
			resultAttach.setDomain(getContract().getDomain());
			ContractAttachment attach = (ContractAttachment) bean.insertOrUpdate(resultAttach);
			if(type == ContractAttachmentType.SEPE_CERTIFICADOS_COMMUNICATION_ID){
				setCommunicationIdFile(attach);
			} else if(type == ContractAttachmentType.SEPE_CERTIFICADOS_RESPONSE){
				setResponseFile(attach);
			}
		} catch (ManagerBeanException e) {
			String msg = "No se han podido guardar los datos de respuesta de Certific@2";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
	}
	
	
}
