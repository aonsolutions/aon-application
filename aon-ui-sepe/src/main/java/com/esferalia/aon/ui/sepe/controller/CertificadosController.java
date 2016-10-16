package com.esferalia.aon.ui.sepe.controller;

import static com.esferalia.aon.jooq.tables.Certifica2Batch.CERTIFICA2_BATCH;
import static com.esferalia.aon.jooq.tables.Certifica2BatchDetail.CERTIFICA2_BATCH_DETAIL;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
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
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Certifica2Batch;
import com.esferalia.aon.payroll.Certifica2BatchAttachment;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SepeBatchAttachment;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.SepeBatchAttachmentType;
import com.esferalia.aon.payroll.enumeration.SuspensionCause;
import com.esferalia.aon.ui.sepe.file.CertificadosWriter;
import com.esferalia.aon.ui.sepe.utils.CertificadosCommunicator;
import com.esferalia.aon.ui.sepe.utils.SEPEFileUtils;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;


public class CertificadosController implements ISepeHandler, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CertificadosController.class.getName());
	
	private boolean showLoginWindow;
	private boolean showGenerationWindow;
	
	private boolean newBatch;
	private boolean readOnly;
	
	private CertificadosCommunicator communicator;
	
	private Certifica2Batch batch;

	private Contract contract;
	private SuspensionCause suspensionCause;
	
	private IAttachment generatedFile;
	private IAttachment communicationIdFile;
	private IAttachment responseFile;
	
	private List<Certifica2BatchDetail> excludeEmployeeList;
	private boolean showExcludeEmployeeWindow;
	
	public List<Certifica2BatchDetail> getExcludeEmployeeList() {
		if(excludeEmployeeList==null){
			excludeEmployeeList = new LinkedList<Certifica2BatchDetail>();
		}
		return excludeEmployeeList;
	}

	public void setExcludeEmployeeList( List<Certifica2BatchDetail> excludeEmployeeList) {
		this.excludeEmployeeList = excludeEmployeeList;
	}

	public boolean isShowExcludeEmployeeWindow() {
		return showExcludeEmployeeWindow;
	}

	public void setShowExcludeEmployeeWindow(boolean showExcludeEmployeeWindow) {
		this.showExcludeEmployeeWindow = showExcludeEmployeeWindow;
	}
	
	
	@Override
	public boolean isShowLoginWindow() {
		return showLoginWindow;
	}
	public void setShowLoginWindow(boolean showLoginWindow) {
		this.showLoginWindow = showLoginWindow;
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
	public boolean isReadOnly() {
		return readOnly;
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
		contract = null;
		batch = null;
		newBatch = false;
		readOnly = false;
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
		Certifica2Batch batch = obtainBatch(contract);
		if(batch != null){
			initialize(batch);
		}
		this.readOnly = obtainBatchContractCount(contract)>1;
		this.contract = contract;
	}
	
	public void onCertificadosAccept( ActionEvent event ) {
		if(batch==null || batch.getId()==null){
			onResetBatch(event);
		}
		if(isNewBatch()){
			onBatchAccept(event);
		}
		createFile(batch);
		setShowGenerationWindow(false);
	}
	
	private void createFile(Certifica2Batch batch) {
		try {
			List<ITransferObject> detailList = getCertifica2DetailList(batch);
			checkEmployeeSalaries(detailList);
			if(!getExcludeEmployeeList().isEmpty()){
				setShowExcludeEmployeeWindow(true);
			} else {
				CertificadosWriter writer = new CertificadosWriter();
				File file = writer.createFile(batch, detailList);
				
				IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchAttachment.class);
				if (file != null) {
					FileInputStream in = new FileInputStream(file);
					byte[] data = IOUtils.toByteArray(in);
					Certifica2BatchAttachment attach;
					attach = new Certifica2BatchAttachment();
					attach.setCertifica2Batch(batch);
					attach.setDomain(batch.getDomain());
					attach.setMimeType(MimeType.MIME_XML);
					attach.setDescription(writer.getFileName());
					attach.setSize(null);
					attach.setAttachmentType(SepeBatchAttachmentType.GENERATED_FILE);
					attach.setScope(null);
					attach.setData(data);
					attach.setAttachDate(new Date());
					attach = (Certifica2BatchAttachment) bean.insertOrUpdate(attach);
					setGeneratedFile(attach);
					changeBatchStatus(batch, FileStatus.GENERATED);
				}
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on generateCertifica2File ["+e.getMessage()+"]");
		} catch (FileNotFoundException e) {
			AonUtil.addErrorMessage("error on generateCertifica2File ["+e.getMessage()+"]");
		} catch (IOException e) {
			AonUtil.addErrorMessage("error on generateCertifica2File ["+e.getMessage()+"]");
		}
	}
	
	private boolean validateCertificadosFile(InputStream input) {
		try {
			String schema = null;
			schema = SEPEFileUtils.CERTIFICADOS_SCHEMA_FILE_NAME;
			SEPEFileUtils.validateCertificadosXmlPattern(input, schema);
			return true;
		} catch (SAXException saxe) {
			String msg = "Error de validación de Certific@2: ausencia de datos o formato no correcto";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(saxe.getMessage() );
		} catch (IOException ioe) {
			String msg = "Error de I/O al validar los datos";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(ioe.getMessage() );
		} catch (Exception e) {
			String msg = "Error general al validar los datos";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage() );
		}
		return false;
	}
	
	private void checkEmployeeSalaries(List<ITransferObject> detailList) throws ManagerBeanException {
		excludeEmployeeList = null;
		SEPEUtils utils = SEPEUtils.getInstance();
		IManagerBean bean = BeanManager.getManagerBean(Salary.class);
		Criteria criteria = null;
		for(ITransferObject to: detailList){
			Certifica2BatchDetail detail = (Certifica2BatchDetail) to;
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_CONTRACT_ID), detail.getContract().getId());
			utils.completeChildDomainCriteria(criteria, bean.getFieldName(IEntityAlias.SALARY_DOMAIN));
			if(bean.getCount(criteria)<=0){
				getExcludeEmployeeList().add(detail);
			}
		}
	}
	
	public void onContinueExcludingEmployees(ActionEvent event) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
		for(Certifica2BatchDetail detail: getExcludeEmployeeList()){
			bean.remove(detail);
		}
//		loadDetails();
//		onCreateDisk(event);
		onCertificadosAccept(event);
	}
	
	public void changeBatchStatus(Certifica2Batch batch, FileStatus status) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Certifica2Batch.class);
		if(batch != null){
			batch.setStatus(status);
			bean.update(batch);
		}
	}
	
	private List<ITransferObject> getCertifica2DetailList(Certifica2Batch batch) {
		try {
			SEPEUtils utils = SEPEUtils.getInstance();
			IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DETAIL_CERTIFICA2BATCH_ID), batch.getId());
			utils.completeChildDomainCriteria(criteria, bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DETAIL_DOMAIN));
			criteria.addOrder("Certifica2BatchDetail.contract.enterpriseCCC.activity.type");
			criteria.addOrder("Certifica2BatchDetail.contract.enterpriseCCC.ccc");
			criteria.addOrder("Certifica2BatchDetail.contract.person.registry.document");
			return bean.getList(criteria);
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("No se ha podido obtener la relación de empleados");
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
				batch.setDate(new Date());
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
			detail.setSuspensionCause(getSuspensionCause());
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
		DownloadUtil.downloadAttachment("certificado-empresa_"+getContract().getPerson().getFullName(), MimeType.MIME_XML, in, size);
	}

	public SelectSeekStep1<Record3<Integer,Timestamp,Integer>,Timestamp> getBatchSelect(AONContext ctx, Contract contract){
		SelectConditionStep<Record1<Integer>> contractBatchSelect = ctx.getDslContext()
				.select(CERTIFICA2_BATCH_DETAIL.CERTIFICA2_BATCH)
				.from(CERTIFICA2_BATCH_DETAIL)
				.where(CERTIFICA2_BATCH_DETAIL.CONTRACT.eq(contract.getId()));
		return ctx.getDslContext().select(CERTIFICA2_BATCH.ID,CERTIFICA2_BATCH.DATE,DSL.count(CERTIFICA2_BATCH_DETAIL.CONTRACT))
				.from(CERTIFICA2_BATCH).leftOuterJoin(CERTIFICA2_BATCH_DETAIL).onKey()
				.where(CERTIFICA2_BATCH_DETAIL.CERTIFICA2_BATCH.in(contractBatchSelect))
				.groupBy(CERTIFICA2_BATCH.ID)
				.orderBy(CERTIFICA2_BATCH.DATE.desc());
	}
	private Integer obtainBatchContractCount(Contract contract){
		AONContext ctx = AONContext.getAONContext(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), AonUtil.getRemoteUser());
		Result<Record3<Integer, Timestamp, Integer>> result = getBatchSelect(ctx, contract).fetch();
		if(result.size()>0){
			return result.get(0).value3();
		}
		return 0;
	}
	private Certifica2Batch obtainBatch(Contract contract){
		AONContext ctx = AONContext.getAONContext(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), AonUtil.getRemoteUser());
		Result<Record3<Integer, Timestamp, Integer>> result = getBatchSelect(ctx, contract).fetch();
		try {
			if(result.size()>0){
				IManagerBean bean = BeanManager.getManagerBean(Certifica2Batch.class);
				return (Certifica2Batch) bean.get(result.get(0).value1());
			}
		} catch (ManagerBeanException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
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
			String msg = "No se ha podido obtener el fichero " + type.getName(AonUtil.getCurrentLocale());
			AonUtil.addErrorMessage(msg);
			LOGGER.error("Error obtaining certific@2 batch attach");
		}
		return null;
	}
	
	
	@Override
	public void onSendSepeFile(ActionEvent event){
		if(validateCertificadosFile(new ByteArrayInputStream(getGeneratedFile().getData()))){
			getCommunicator().setDataCommunication(true);
			if(!isShowLoginWindow()){
				getCommunicator().initialize();
			}
			if( getCommunicator().isLoginRequired() ){
				setShowLoginWindow(true);
			} else {
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
			getCommunicator().setDocument(document);
			String result = getCommunicator().communicate();
			saveSepeResponseFile(SepeBatchAttachmentType.RESPONSE_FILE, result);
			setShowLoginWindow(false);
			processSepeResult(result);
		}
	}
	
	private void processSepeResult(String result) {
		// nada
	}
	
	public void onRemoveSepeFiles(ActionEvent event){
		try {
			IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchAttachment.class);
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
			String status = getCommunicator().obtainCommunicationStatus(getResponseFile().getData(), getContract());
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
	
	
}
