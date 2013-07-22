package com.esferalia.aon.ui.payroll.controller.contract;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeSet;

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

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contrata.ContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.contrata.enumeration.TERRORES;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.sepe.api.contrata.contratos.FICHEROCONTRATOS;
import com.esferalia.aon.sepe.api.contrata.contratos.RESPUESTACONTRATOTYPE;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.PayrollAppParamsController;
import com.esferalia.aon.ui.payroll.file.ContrataReader;
import com.esferalia.aon.ui.payroll.file.ContrataResponseReader;
import com.esferalia.aon.ui.payroll.file.ContrataWriter;
import com.esferalia.aon.ui.payroll.sepe.SEPEConnectionProvider;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;


public class ContractContrataController {
	
	private static final String AVAILABLE_CONTRACT_CODE_COMMUNICATION = "421;";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractContrataController.class.getName());
	
	private Contract contract;
	private ContractAttachment contrataAttach;
	private ContractContrataHandler handler;
	private String backAction;
	private Map<String, String> contractDataMap;
	private boolean showContrataLoginWindow;
	private boolean showCommunicationLogWindow;
	private boolean enabledContrataEdition;
	private String communicationLogContent;
	
	public String getCommunicationLogContent() {
		return communicationLogContent;
	}
	public void setCommunicationLogContent(String communicationLogContent) {
		this.communicationLogContent = communicationLogContent;
	}
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	public boolean isEnabledContrataEdition() {
		return enabledContrataEdition;
	}
	public void setEnabledContrataEdition(boolean enabledContrataEdition) {
		this.enabledContrataEdition = enabledContrataEdition;
	}
	public boolean isShowContrataLoginWindow() {
		return showContrataLoginWindow;
	}
	public void setShowContrataLoginWindow(boolean showContrataLoginWindow) {
		this.showContrataLoginWindow = showContrataLoginWindow;
	}
	public boolean isShowCommunicationLogWindow() {
		return showCommunicationLogWindow;
	}
	public void setShowCommunicationLogWindow(boolean showCommunicationLogWindow) {
		this.showCommunicationLogWindow = showCommunicationLogWindow;
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
	public void setContrataAttach(ContractAttachment contrataAttach) {
		this.contrataAttach = contrataAttach;
	}
	public ContractAttachment getContrataAttach(){
		return contrataAttach;
	}
	public ContractContrataHandler getHandler() {
		if(handler==null){
			handler = new ContractContrataHandler();
		}
		return handler;
	}
	public void setHandler(ContractContrataHandler handler) {
		this.handler = handler;
	}
	public ContrataParams getParams() {
		if(getHandler().getParams()==null){
			getHandler().setParams(new ContrataParams());
		}
		return getHandler().getParams();
	}
	protected Map<String, String> getContractDataMap() {
		return contractDataMap;
	}
	public List<SelectItem> getTownNames(){
		String BASE_NAME = "com.esferalia.aon.payroll.i18n.towns";
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME);
		List<SelectItem> towns = new LinkedList<SelectItem>();
		try {
			if(getParams().getContract()!=null){
				RegistryAddress address = getParams().getContract().getPerson().getRegistry().getDefaultAddress();
				TreeSet<String> tree = new TreeSet<String>(bundle.keySet());
				for(String key: tree){
					if(address==null || address.getGeozone()==null || key.startsWith(address.getGeozone().getCode())){
						String name = bundle.getString(key);
						SelectItem item = new SelectItem(key, name);
						towns.add(item);
					}
				}
			}
		} catch (ManagerBeanException e) {
			// NADA, se devuelve una lista vacia
		}
		return towns;
	}
	public List<SelectItem> getQualificationsNames(){
		String BASE_NAME = "com.esferalia.aon.payroll.i18n.qualifications";
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME);
		List<SelectItem> qualifications = new LinkedList<SelectItem>();
		if(getParams().getNivelFormativo()!=null){
			TreeSet<String> tree = new TreeSet<String>(bundle.keySet());
			for(String key: tree){
				if(key.startsWith(getParams().getNivelFormativo().getCode())){
					String name = bundle.getString(key);
					SelectItem item = new SelectItem(key, name);
					qualifications.add(item);
				}
			}
		}
		return qualifications;
	}
	public boolean isNew(){
		return getContrataAttach()==null||getContrataAttach().getId()==null;
	}
	public String getCommunicationAvailableCodes(){
		return "Comunicación implementada para los contratos con código: "+AVAILABLE_CONTRACT_CODE_COMMUNICATION;
	}
	public boolean isCommunicationAvailable(){
		if(getHandler().getContractCode()!=null){
			return AVAILABLE_CONTRACT_CODE_COMMUNICATION.contains(getHandler().getContractCode().getValue());
		}
		return false;
	}
	public boolean isCommunicationResponseReceived(){
		return obtainContrataResponseAttach()!=null;
	}
	

	private void reset(){
		setShowContrataLoginWindow(false);
		setContrataLoginRemember(false);
		setXmlResult(null);
		setDocument(null);
		setUser(null);
		setPasswd(null);
		setContrataPasswdVisible(false);
		setHandler(null);
	}
	
	public void initialize(Contract contract){
		reset();
		PayrollUtils utils = new PayrollUtils();
		contractDataMap = utils.getContractDataMap(contract);
		
		getParams().setContract(contract);
		getHandler().setContractCode(ContractCode.getContractCodeByValue(getContractDataMap().get(ContextVariable.TC2.getName())));

		contrataAttach = obtainContrataFileAttach();
	}
	
	public void onContrataDataShow(ActionEvent event) {
		setEnabledContrataEdition(true);
		setContract((Contract)FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER).getTo());
		
		initialize(getContract());
		
		if( getHandler().getContractCode()==null ){
			setEnabledContrataEdition(false);
			String msg = "El código de contrato no puede ser nulo.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		
		String code = getHandler().getContractCode().getValue();
		
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
		}
		if( code.equals(ContractCode.C408.getValue())
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
			
//			ContractAttachment attach = obtainContrataStatusAttach();
//			processXmlFile( attach );			
//			obtainContrataStatus();
			
			processXmlFile( getContrataAttach() );
			
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
	
	public void onAccept( ActionEvent event ) {
		try {
			generateXmlFile(getParams().getContract());
			ContractAttachment attach = new ContractAttachment();
			attach = getContrataAttach();
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			attach.setAttachDate(new Date());
			bean.insertOrUpdate(attach);
			ContractAttachController attachController = (ContractAttachController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_ATTACH_CONTROLLER);
			attachController.initializeModel();
		} catch (ManagerBeanException e) {
			String msg = "No se han podido guardar los datos de Contrat@";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
	}
	
	public void onDownloadContrataXml(ActionEvent event) throws ManagerBeanException{
		ContractAttachment attach = obtainContrataFileAttach();
		InputStream in = new ByteArrayInputStream(attach.getData());
		long size = ArrayUtils.getLength(attach.getData());
		DownloadUtil.downloadAttachment("contrato-"+getParams().getContract().getPerson().getRegistry().getDocument(), MimeType.MIME_PDF, in, size);
	}
	
	private ContractAttachment obtainContrataFileAttach(){
		return obtainContrataAttach(ContractAttachmentType.SPEE_CONTRATA_FILE);
	}
	
	private ContractAttachment obtainContrataResponseAttach(){
		return obtainContrataAttach(ContractAttachmentType.SPEE_CONTRATA_RESPONSE);
	}

	private ContractAttachment obtainContrataStatusAttach(){
		return obtainContrataAttach(ContractAttachmentType.SPEE_CONTRATA_STATUS);
	}
	
	private ContractAttachment obtainContrataAttach(ContractAttachmentType type){
		try {
			if(getParams()!=null && getParams().getContract()!=null && getParams().getContract().getId()!=null){
				IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), getParams().getContract().getId());
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), type);
				List<ITransferObject> list = bean.getList(criteria);
				if(!list.isEmpty()){
					return (ContractAttachment) list.get(0);
				}
			}
		} catch (ManagerBeanException e) {
			// NOTHING TO DO
		}
		return null;
	}
	
	private void processXmlFile(ContractAttachment contrataAttach) throws ManagerBeanException, IOException {
		if(contrataAttach!=null){
			ContrataReader reader = new ContrataReader();
			ContrataParams params = reader.readFile( new ByteArrayInputStream(contrataAttach.getData()) );
			params.setContract(contrataAttach.getContract());
			getHandler().setParams(params);
		}
	}
	
	private void generateXmlFile(Contract contract) throws ManagerBeanException{
		ContrataWriter writer = new ContrataWriter();
		try {
			File file = writer.createFile(getParams());
			FileInputStream fis = new FileInputStream(file);
			byte fileContent[] = new byte[(int)file.length()];
			fis.read(fileContent);
			if(getContrataAttach()==null){
				setContrataAttach(new ContractAttachment());
			}
			getContrataAttach().setContract(contract);
			getContrataAttach().setData(fileContent);
			getContrataAttach().setAttachmentType(ContractAttachmentType.SPEE_CONTRATA_FILE);
			getContrataAttach().setMimeType(MimeType.MIME_XML);
			getContrataAttach().setDescription("Fichero contrat@");
			fis.close();
		} catch(IOException e) {
			String msg = "No se han podido guardar los datos de Contrat@";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		} 
	}
	
	
	private String xmlResult;
	private boolean comunicationProcess;
	private boolean dataQueryProcess;
	
	public String getXmlResult() {
		return xmlResult;
	}
	public void setXmlResult(String xmlResult) {
		this.xmlResult = xmlResult;
	}
	
	public void onSendContrataFile(ActionEvent event){
		if(!isShowContrataLoginWindow()){
			searchContrataLogin();
			searchTestEnvironment();
		}
		if( StringUtils.isBlank(getUser()) || StringUtils.isBlank(getPasswd()) ){
			setShowContrataLoginWindow(true);
		} else {
			comunicationProcess = true;
			dataQueryProcess = false;
			DataCommunicationThread thread = new DataCommunicationThread();
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				throw new AbortProcessingException("Error de comunicacion con el S.E.P.E.");
			}
			saveCommunicationResultFile(ContractAttachmentType.SPEE_CONTRATA_RESPONSE);
		
			setShowContrataLoginWindow(false);
			xmlResult = "Datos enviados al S.E.P.E.";
			afterCommunicationFinished();
		}
	}
	
	public void onContrataDataQuery(ActionEvent event){
		setDocument(obtainContrataCommunicationNumber());
		if( StringUtils.isBlank(getDocument()) ){
			String msg = "No se puede obtener el número del envío de la comunicación.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		if(!isShowContrataLoginWindow()){
			searchContrataLogin();
			searchTestEnvironment();
		}
		if( StringUtils.isBlank(getUser()) || StringUtils.isBlank(getPasswd()) ){
			setShowContrataLoginWindow(true);
		} else {
			comunicationProcess = false;
			dataQueryProcess = true;
			DataCommunicationThread thread = new DataCommunicationThread();
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				throw new AbortProcessingException("Error de comunicacion con el S.E.P.E.");
			}
			saveCommunicationResultFile(ContractAttachmentType.SPEE_CONTRATA_STATUS);
			
			setShowContrataLoginWindow(false);
			xmlResult = "Consulta realizada al S.E.P.E.";
			afterCommunicationFinished();
			afterContrataDataQuery();
		}
	}

	public void onShowDataQuery(ActionEvent event){
		setXmlResult( obtainCommunicationStatus() );
	}
	
	private String obtainContrataCommunicationNumber() {
		ContractAttachment attach = obtainContrataResponseAttach();
		if(attach!=null && attach.getId()!=null){
			String result = new String(attach.getData()); 
			result = result.replaceAll("\n", "");
			result = StringUtils.removeStart(result, "<?xml version='1.0' encoding='ISO-8859-1'?>");
			result = StringUtils.removeStart(result, "<COMUNICACION>");
			result = StringUtils.removeStart(result, "<NUM_ENVIO>");
			result = StringUtils.removeEnd(result, "</COMUNICACION>");
			result = StringUtils.removeEnd(result, "</NUM_ENVIO>");
			return result;
		}
		return null;
	}
	
	private String obtainCommunicationStatus() {
		String status = "";
		ContractAttachment attach = obtainContrataStatusAttach();
		status += "<br /> ";
		status += attach.getAttachDate() + " - Resultado obtenido del SEPE";
		status += "<br /> <hr /> ";
		if(attach!=null && attach.getId()!=null){
			try {
//				ContrataResponseReader reader = new ContrataResponseReader();
//				reader.readFile(new ByteArrayInputStream(attach.getData()));
//				FICHEROCONTRATOS contratos = reader.getFicheroContratos();
				FICHEROCONTRATOS contratos = obtainFicheroContratos(attach);
				status += "ESTADO FICHERO:    " + contratos.getESTADOFICHERO();
				status += "<br /> ";
				
				contratos.getCONTRATOSPROCESADOS().getENVIO100AndENVIO130AndENVIO150();
//				RESPUESTACONTRATOTYPE respuestaContratos = reader.getRepuestaContrato(contratos.getCONTRATOSPROCESADOS().getENVIO100AndENVIO130AndENVIO150().get(0));
				RESPUESTACONTRATOTYPE respuestaContratos = obtainRespuestaContrato(contratos.getCONTRATOSPROCESADOS().getENVIO100AndENVIO130AndENVIO150().get(0));
				status += "FECHA ALTA:         " + respuestaContratos.getFECHAALTA();
				status += "<br /> ";
				status += "FECHA COMUNICACION: " + respuestaContratos.getFECHACOMUNICACION();
				status += "<br /> ";
				status += "ID CONTRATO:        " + respuestaContratos.getIDCONTRATO();
				status += "<br /> ";
				status += "LEY BONIF:          " + respuestaContratos.getLEYBONIF();
				status += "<br /> ";
				status += "LEY DEDUCCION:      " + respuestaContratos.getLEYDEDUCCION();
				status += "<br /> ";
				status += "LEY FOMENTO:        " + respuestaContratos.getLEYFOMENTO();
				status += "<br /> ";
				status += "LEY REDUCCION:      " + respuestaContratos.getLEYREDUCCION();
				status += "<br /> ";
				status += "OBLIG B:            " + respuestaContratos.getOBLIGCB();
				status += "<br /> ";
				status += "RESULTADO:          " + respuestaContratos.getRESULTADO();
				status += "<br /> ";
				status += "USUARIO:            " + respuestaContratos.getUSUARIO();
				status += "<br /> ";
				
//			System.out.println("ESTADO FICHERO:    " + contratos.getESTADOFICHERO());
//			System.out.println("NUMERO PROCESADOS: " + contratos.getNUMEROPROCESADOS());
//			System.out.println("VERSION:           " + contratos.getVersion());
//			System.out.println("FECHA ALTA:         " + respuestaContratos.getFECHAALTA());
//			System.out.println("FECHA COMUNICACION: " + respuestaContratos.getFECHACOMUNICACION());
//			System.out.println("ID CONTRATO:        " + respuestaContratos.getIDCONTRATO());
//			System.out.println("LEY BONIF:          " + respuestaContratos.getLEYBONIF());
//			System.out.println("LEY DEDUCCION:      " + respuestaContratos.getLEYDEDUCCION());
//			System.out.println("LEY FOMENTO:        " + respuestaContratos.getLEYFOMENTO());
//			System.out.println("LEY REDUCCION:      " + respuestaContratos.getLEYREDUCCION());
//			System.out.println("OBLIG B:            " + respuestaContratos.getOBLIGCB());
//			System.out.println("RESULTADO:          " + respuestaContratos.getRESULTADO());
//			System.out.println("USUARIO:            " + respuestaContratos.getUSUARIO());
				
				List<String> errores = respuestaContratos.getERRORES().getERROR();
				if(!errores.isEmpty()){
					status += "<br />";
					status += "ERRORES";
					status += "<br /> <hr /> ";
					for(String error: respuestaContratos.getERRORES().getERROR()){
						status += "ERROR: " + error + " - " + TERRORES.getEnumByValue(error).getDescription();
						status += "<br /> ";
//					System.out.println("ERROR: " + error + " - " + TERRORES.getEnumByValue(error).getDescription());
					}
				}
			} catch (ManagerBeanException e) {
				String msg = "No se ha podido obtener los datos del estado de las comunicaciones.";
				status += msg;
				status += "<br /> ";
			} catch (IOException e) {
				String msg = "No se ha podido obtener los datos del estado de las comunicaciones.";
				status += msg;
				status += "<br /> ";
			} catch (Throwable th) {
				String msg = "No se ha podido obtener los datos del estado de las comunicaciones.";
				status += msg;
				status += "<br /> ";
			}
		}
		return status;
	}

	private FICHEROCONTRATOS obtainFicheroContratos(ContractAttachment attach) throws ManagerBeanException, IOException, JAXBException, SAXException, ParserConfigurationException {
		ContrataResponseReader reader = new ContrataResponseReader();
		reader.readFile(new ByteArrayInputStream(attach.getData()));
		return reader.getFicheroContratos();
	}

	private RESPUESTACONTRATOTYPE obtainRespuestaContrato(Object object) {
		ContrataResponseReader reader = new ContrataResponseReader();
		return reader.getRepuestaContrato(object);
	}
	
	public void onContrataLogShow(ActionEvent event){
		communicationLogContent = "<div>";
		String communicationNumber = obtainContrataCommunicationNumber();
		if(StringUtils.isNotEmpty(communicationNumber)){
			
			communicationLogContent += obtainContrataResponseAttach().getAttachDate() + " - Contrato comunicado al SEPE";
			communicationLogContent += "<br /> <hr /> ";
			
			communicationLogContent += "NUM ENVIO:         " + communicationNumber;
			communicationLogContent += "<br /> ";
		}
		String status = obtainCommunicationStatus();
		if(StringUtils.isNotEmpty(status)){
			communicationLogContent += status ;
		}
		communicationLogContent += "</div>";
	}
	
	private void searchContrataLogin() {
		PayrollAppParamsController appParams = (PayrollAppParamsController) AonUtil.getRegisteredBean(IPayrollConstants.PAYROLL_APP_PARAMS_CONTROLLER_NAME);
		try {
			appParams.loadParameters();
		} catch (ManagerBeanException e) {
			// no se cargan los datos de login, se piden por pantalla
		}
		setUser(appParams.getContrataUser());
		setPasswd(appParams.getContrataPassword());
	}

	private void searchTestEnvironment() {
		PayrollAppParamsController appParams = (PayrollAppParamsController) AonUtil.getRegisteredBean(IPayrollConstants.PAYROLL_APP_PARAMS_CONTROLLER_NAME);
		try {
			appParams.loadParameters();
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido verificar el entorno de trabajo. Se activa el entorno de pruebas (TEST).";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			setContrataTestEnv(true);
		}
		setContrataTestEnv(appParams.getContrataTestEnviroment());
	}
	
	private void afterCommunicationFinished() {
		PayrollAppParamsController appParams = (PayrollAppParamsController) AonUtil.getRegisteredBean(IPayrollConstants.PAYROLL_APP_PARAMS_CONTROLLER_NAME);
		if(StringUtils.isNotBlank(getUser()) && StringUtils.isNotBlank(getPasswd())){
			if(isContrataLoginRemember()){
				appParams.setContrataUser(getUser());
				appParams.setContrataPassword(getPasswd());
				try {
					appParams.accept();
				} catch (ManagerBeanException e) {
					AonUtil.addInfoMessage("Los datos identificativos no se han podido guardar.");
				}
			}
		} 
	}
	
	private void afterContrataDataQuery() {
		if(getParams().getContract().getModel()==ContractModel.PE226){
			ContractAttachment attach = obtainContrataStatusAttach();
			if(attach!=null && attach.getId()!=null){
				try {
					FICHEROCONTRATOS contratos = obtainFicheroContratos(attach);
					contratos.getCONTRATOSPROCESADOS().getENVIO100AndENVIO130AndENVIO150();
					RESPUESTACONTRATOTYPE respuestaContratos = obtainRespuestaContrato(contratos.getCONTRATOSPROCESADOS().getENVIO100AndENVIO130AndENVIO150().get(0));
					respuestaContratos.getIDCONTRATO();
				} catch (ManagerBeanException e) {
				} catch (IOException e) {
				} catch (Throwable th) {
				}
			}
		}
	}


	private String document;
	private String user;
	private String mainUser;
	private String passwd;
	private boolean contrataLoginRemember;
	private boolean contrataTestEnv;
	private boolean contrataPasswdVisible;
	
	public String getDocument() {
		return document;
	}
	public boolean isContrataLoginRemember() {
		return contrataLoginRemember;
	}
	public void setContrataLoginRemember(boolean contrataLoginRemember) {
		this.contrataLoginRemember = contrataLoginRemember;
	}
	public boolean isContrataPasswdVisible() {
		return contrataPasswdVisible;
	}
	public void setContrataPasswdVisible(boolean contrataPasswdVisible) {
		this.contrataPasswdVisible = contrataPasswdVisible;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getMainUser() {
		return mainUser;
	}
	public void setMainUser(String mainUser) {
		this.mainUser = mainUser;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public boolean isContrataTestEnv() {
		return contrataTestEnv;
	}
	public void setContrataTestEnv(boolean contrataTestEnv) {
		this.contrataTestEnv = contrataTestEnv;
	}
	
	private void sendContrataFile(){
		xmlResult = SEPEConnectionProvider.processContrataComunication(isContrataTestEnv(), getContrataAttach().getData(), getUser(), getUser(), getPasswd());
//		ContrataManager.processDataComunication(getContrataAttach().getData(), getUser(), getUser(), getPasswd());
		System.out.println("RESPUESTA RESULTANTE DE LA COMUNICACION CON EL S.E.P.E. :  " );
		System.out.println(xmlResult);
	}

	private void contrataDataQuery(){
		
		xmlResult = SEPEConnectionProvider.processContrataQuery(isContrataTestEnv(), getDocument(), getUser(), getUser(), getPasswd());
		
//		setXmlResult(result);
		
		System.out.println("RESPUESTA RESULTANTE DE LA CONSULTA AL S.E.P.E. :  " );
		System.out.println(xmlResult);
//		xmlResult.replaceAll("\n", "<br />");
	}
	
	
	private ContractAttachment saveCommunicationResultFile(ContractAttachmentType type){
		ContractAttachment contrataResultAttach = obtainContrataAttach(type);
		if(contrataResultAttach==null){
			contrataResultAttach = new ContractAttachment();
			contrataResultAttach.setContract(getParams().getContract());
			contrataResultAttach.setAttachmentType(type);
			contrataResultAttach.setMimeType(MimeType.MIME_XML);
			if(type == ContractAttachmentType.SPEE_CONTRATA_RESPONSE){
				contrataResultAttach.setDescription("Respuesta contrat@");
			} else if(type == ContractAttachmentType.SPEE_CONTRATA_STATUS){
				contrataResultAttach.setDescription("Estado contrat@");
			}
		}
		try {
			InputStream is = new ByteArrayInputStream(xmlResult.getBytes());
			byte fileContent[] = new byte[(int)xmlResult.length()];
			is.read(fileContent);
			contrataResultAttach.setData(fileContent);
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
			contrataResultAttach.setAttachDate(new Date());
			contrataResultAttach = (ContractAttachment) bean.insertOrUpdate(contrataResultAttach);
			ContractAttachController attachController = (ContractAttachController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_ATTACH_CONTROLLER);
			attachController.initializeModel();
			return contrataResultAttach;
		} catch (ManagerBeanException e) {
			String msg = "No se han podido guardar los datos de respuesta de Contrat@";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
	}
	
	private class DataCommunicationThread extends Thread {
	    public void run() {
			if(comunicationProcess){
				sendContrataFile();
			} else if(dataQueryProcess){
				contrataDataQuery();
			}
	    }
	}
	
	
	
}
