package com.esferalia.aon.ui.payroll.controller.contract;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.generated.contratos.CONTRATOS;
import com.esferalia.aon.file.payroll.contract.generated.contratos.FICHEROCONTRATOS;
import com.esferalia.aon.file.payroll.contract.generated.prorrogas.PRORROGAS;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.TRANSFORMACIONES;
import com.esferalia.aon.file.payroll.contract.model.IContratoType;
import com.esferalia.aon.file.payroll.contract.model.IProrrogaType;
import com.esferalia.aon.file.payroll.contract.model.ITransformacionType;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.contrata.ContrataManager;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.file.ContrataParams;
import com.esferalia.aon.ui.payroll.file.ContrataReader;
import com.esferalia.aon.ui.payroll.file.ContrataWriter;
import com.esferalia.aon.ui.payroll.file.ContrataReader.ContractValidationEventHandler;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;


public class ContractContrataController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractContrataController.class.getName());
	
	private ContractAttachment contrataAttach;
	private ContractContrataHandler handler;
	private String backAction;
	private Map<String, String> contractDataMap;
	private boolean showContrataLoginWindow;
	
	
	public boolean isShowContrataLoginWindow() {
		return showContrataLoginWindow;
	}
	public void setShowContrataLoginWindow(boolean showContrataLoginWindow) {
		this.showContrataLoginWindow = showContrataLoginWindow;
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
		return handler;
	}
	public void setHandler(ContractContrataHandler handler) {
		this.handler = handler;
	}
	public ContrataParams getParams() {
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
			RegistryAddress address = getHandler().getParams().getContract().getPerson().getRegistry().getDefaultAddress();
			TreeSet<String> tree = new TreeSet<String>(bundle.keySet());
			for(String key: tree){
				if(address==null || key.startsWith(address.getGeozone().getCode())){
					String name = bundle.getString(key);
					SelectItem item = new SelectItem(key, name);
					towns.add(item);
				}
			}
			return towns;
		} catch (ManagerBeanException e) {
			// NADA, se devuelve una lista vacia
		}
		return null;
	}
	public boolean isNew(){
		return getContrataAttach()==null||getContrataAttach().getId()==null;
	}
	public boolean isCommunicationResponseReceived(){
		return obtainContrataResponseAttach(getHandler().getParams().getContract())!=null;
	}
	

	public void initialize(Contract contract){
		PayrollUtils utils = new PayrollUtils();
		contractDataMap = utils.getContractDataMap(contract);
		contrataAttach = obtainContrataAttach(contract);
		
		setXmlResult(null);
		setDocument(null);
		setUser(null);
		setPasswd(null);
		
		setHandler(new ContractContrataHandler());
		getHandler().setParams(new ContrataParams());
		getHandler().getParams().setContract(contract);
		getHandler().setContractCode(ContractCode.getContractCodeByValue(getContractDataMap().get(ContextVariable.TC2.getName())));
	}
	
	public void onContractaDataShow(ActionEvent event) {
		String code = getContractDataMap().get(ContextVariable.TC2.getName());
		
		if( code.equals(ContractCode.C109.getValue())
				 || code.equals(ContractCode.C139.getValue())
				 || code.equals(ContractCode.C189.getValue())
				 || code.equals(ContractCode.C209.getValue())
				 || code.equals(ContractCode.C239.getValue())
				 || code.equals(ContractCode.C289.getValue())
				 || code.equals(ContractCode.C309.getValue())
				 || code.equals(ContractCode.C389.getValue()) ){
			// Transformaciones de contrato
		}
		if( code.equals(ContractCode.C408.getValue())
				 || code.equals(ContractCode.C418.getValue())
				 || code.equals(ContractCode.C508.getValue())
				 || code.equals(ContractCode.C518.getValue()) ){
			String msg = "Tipo contrato no implementado para generar el fichero contrat@";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		try {
			processXmlFile( getContrataAttach() );
		} catch (ManagerBeanException e) {
			String msg = "No se han podido obtener los datos del fichero (XML) de contrata previamente guardado.";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		} catch (IOException e) {
			String msg = "No se han podido obtener los datos del fichero (XML) de contrata previamente guardado.";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
	}
	
	public void onAccept( ActionEvent event ) {
		try {
			generateXmlFile(getHandler().getParams().getContract());
			ContractAttachment attach = new ContractAttachment();
			attach = getContrataAttach();
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			attach.setAttachDate(new Date());
			bean.insertOrUpdate(attach);
			ContractAttachController attachController = (ContractAttachController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_ATTACH_CONTROLLER);
			attachController.initializeModel();
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido guardar el documento (XML) de contrata";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
	}
	
	private ContractAttachment obtainContrataAttach(Contract contract){
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), contract.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.SPEE_CONTRATA_FILE);
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				return (ContractAttachment) list.get(0);
			}
		} catch (ManagerBeanException e) {
			// NOTHING TO DO
		}
		return null;
	}
	private ContractAttachment obtainContrataResponseAttach(Contract contract){
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), contract.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.SPEE_CONTRATA_RESPONSE);
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				return (ContractAttachment) list.get(0);
			}
		} catch (ManagerBeanException e) {
			// NOTHING TO DO
		}
		return null;
	}
	
	private void processXmlFile(ContractAttachment contrataAttach) throws ManagerBeanException, IOException {
		if(contrataAttach!=null){
			ContrataReader reader = new ContrataReader();
			ContrataParams params = reader.readFile( contrataAttach );
			params.setContract(contrataAttach.getContract());
			getHandler().setParams(params);
		}
	}
	
	private void generateXmlFile(Contract contract) throws ManagerBeanException{
		ContrataWriter writer = new ContrataWriter();
		try {
			File file = writer.createFile(getHandler().getParams());
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
			String msg = "No se ha podido generar el documento (XML) de contrata";
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
//		xmlResult = "Fichero enviado al S.E.P.E.";
		
		
//		setDocument("C0056300");
//		setUser("A01306190");
//		setPasswd("945121010");
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
		
		
		
		
		StringUtils.chomp(xmlResult);
		xmlResult = xmlResult.replaceAll("\n", "");
		xmlResult = StringUtils.removeStart(xmlResult, "<?xml version='1.0' encoding='ISO-8859-1'?>");
		xmlResult = StringUtils.removeStart(xmlResult, "<COMUNICACION>");
		xmlResult = StringUtils.removeStart(xmlResult, "<NUM_ENVIO>");
		xmlResult = StringUtils.removeEnd(xmlResult, "</COMUNICACION>");
		xmlResult = StringUtils.removeEnd(xmlResult, "</NUM_ENVIO>");
		
//		<?xml version='1.0' encoding='ISO-8859-1'?>
//		<COMUNICACION>
//		<NUM_ENVIO>C0056363</NUM_ENVIO>
//		</COMUNICACION>
		
		/* CONSULTA DEL ESTADO DE COMUNICACION */
//		setDocument(xmlResult);
////		setUser("A01306190");
////		setPasswd("945121010");
//		comunicationProcess = false;
//		dataQueryProcess = true;
//		thread = new DataCommunicationThread();
//		thread.start();
//		try {
//			thread.join();
//		} catch (InterruptedException e) {
//			throw new AbortProcessingException("Error de comunicacion con el S.E.P.E.");
//		}
//		ContractAttachment attach = saveCommunicationResultFile(ContractAttachmentType.SPEE_CONTRATA_STATUS);
//		FICHEROCONTRATOS contratos;
//		try {
//			contratos = readFile(attach);
//			
//			System.out.println(contratos.getESTADOFICHERO());
//			System.out.println(contratos.getNUMEROPROCESADOS());
//			System.out.println(contratos.getVersion());
//			
//			contratos.getCONTRATOSPROCESADOS().getENVIO100AndENVIO130AndENVIO150();
//			
//		} catch (ManagerBeanException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		setShowContrataLoginWindow(false);
		xmlResult = "Datos enviados al S.E.P.E.";
	}
	public void onContrataDataQuery(ActionEvent event){
//		dataQueryProcess=true;
//		comunicationProcess = false;
//		TestThread thread = new TestThread();
//		thread.start();
		obtainContrataDataQueryResult();
	}
		
	private void obtainContrataDataQueryResult() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			
			
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), getParams().getContract().getId()); 
			Projection projection = Projection.max(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ID));
	        Object value = bean.getUniqueResult(projection, criteria);
	        if(value != null ){
	        	ContractAttachment dataQueryAttach = (ContractAttachment) bean.get(((Integer) value).intValue());
	        	setXmlResult( new String(dataQueryAttach.getData()) );
	        } else {
	        	setXmlResult("No se ha podido recuperar el documento de respuesta (XML) de contrata");
	        }
			
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido recuperar el documento de respuesta (XML) de contrata";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
	}


	private String document;
	private String user;
	private String mainUser;
	private String passwd;
	
	
	public String getDocument() {
		return document;
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
	private void sendContrataFile(){
		xmlResult = ContrataManager.processDataComunication(getContrataAttach().getData(), getUser(), getUser(), getPasswd());
		ContrataManager.processDataComunication(getContrataAttach().getData(), getUser(), getUser(), getPasswd());
		System.out.println("RESPUESTA RESULTANTE DE LA COMUNICACION CON EL S.E.P.E. :  " );
		System.out.println(xmlResult);
	}

	private void contrataDataQuery(){
		
//		setDocument("C0056300");
//		setUser("A01306190");
//		setPasswd("945121010");
		
		String result = ContrataManager.processDataQuery(getDocument(), getUser(), getUser(), getPasswd());
		
		setXmlResult(result);
		
		System.out.println("RESPUESTA RESULTANTE DE LA CONSULTA AL S.E.P.E. :  " );
		System.out.println(getXmlResult());
//		xmlResult.replaceAll("\n", "<br />");
	}
	
	
	private ContractAttachment saveCommunicationResultFile(ContractAttachmentType type){
		ContractAttachment contrataResultAttach = new ContractAttachment();
		try {
			InputStream is = new ByteArrayInputStream(xmlResult.getBytes());
			byte fileContent[] = new byte[(int)xmlResult.length()];
			is.read(fileContent);
			contrataResultAttach.setContract(getParams().getContract());
			contrataResultAttach.setData(fileContent);
			contrataResultAttach.setAttachmentType(type);
			contrataResultAttach.setMimeType(MimeType.MIME_XML);
			if(type == ContractAttachmentType.SPEE_CONTRATA_RESPONSE){
				contrataResultAttach.setDescription("Respuesta contrat@");
			} else if(type == ContractAttachmentType.SPEE_CONTRATA_STATUS){
				contrataResultAttach.setDescription("Estado contrat@");
			}
			is.close();
		} catch(IOException e) {
			String msg = "No se ha podido generar el documento de respuesta (XML) de contrata";
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
			String msg = "No se ha podido generar el documento de respuesta (XML) de contrata";
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
	
	private FICHEROCONTRATOS readFile(ContractAttachment attach) throws ManagerBeanException, IOException{
		
		final String CONTRATA_CONTRATOS_MODEL_PATH = "com.esferalia.aon.file.payroll.contract.generated.contratos";
		
		byte[] f = attach.getData();
		if(f!=null && f.length>0){
			File file = File.createTempFile("aon-temp", ".XML");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(f);
			fos.close();
			try {
			
				FICHEROCONTRATOS contratos = null;
				JAXBContext jaxbContext = JAXBContext.newInstance(CONTRATA_CONTRATOS_MODEL_PATH);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				unmarshaller.setEventHandler(new ContractValidationEventHandler());
				contratos = (FICHEROCONTRATOS) unmarshaller.unmarshal(file);
				
				return contratos;

			} catch (JAXBException e) {
				String msg = "Error al obtener los datos del documento xml de contrata";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg, e);
			}
		}
		return null;
	}
	
	public class ContractValidationEventHandler implements ValidationEventHandler {
		public boolean handleEvent(ValidationEvent ve) {
			if (ve.getSeverity() == ValidationEvent.FATAL_ERROR || ve.getSeverity() == ValidationEvent.ERROR) {
				ValidationEventLocator locator = ve.getLocator();
				// Print message from valdation event
				System.out.println("Invalid booking document: " + locator.getURL());
				System.out.println("Error: " + ve.getMessage());
				// Output line and column number
				System.out.println("Error at column "
						+ locator.getColumnNumber() + ", line "
						+ locator.getLineNumber());
			}
			return true;
		}
	}
	
}
