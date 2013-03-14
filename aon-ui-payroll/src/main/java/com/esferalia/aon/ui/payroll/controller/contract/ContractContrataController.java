package com.esferalia.aon.ui.payroll.controller.contract;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeSet;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
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
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;


public class ContractContrataController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractContrataController.class.getName());
	
	private ContractAttachment contrataAttach;
	private ContractContrataHandler handler;
	private String backAction;
	private Map<String, String> contractDataMap;
	private boolean pollEnabled;
	private boolean showContrataLoginWindow;
	
	
	public boolean isShowContrataLoginWindow() {
		return showContrataLoginWindow;
	}
	public void setShowContrataLoginWindow(boolean showContrataLoginWindow) {
		this.showContrataLoginWindow = showContrataLoginWindow;
	}
	public boolean isPollEnabled() {
		return pollEnabled;
	}
	public void setPollEnabled(boolean pollEnabled) {
		this.pollEnabled = pollEnabled;
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
	

	public void initialize(Contract contract){
		PayrollUtils utils = new PayrollUtils();
		contractDataMap = utils.getContractDataMap(contract);
		contrataAttach = obtainContrataAttach(contract);
		
		setXmlResult(null);
		setPollEnabled(false);
		
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
		}
		if( code.equals(ContractCode.C408.getValue())
				 || code.equals(ContractCode.C418.getValue())
				 || code.equals(ContractCode.C508.getValue())
				 || code.equals(ContractCode.C518.getValue()) ){
			String msg = "Prorroga de contrato no implementado para generar el fichero contrat@";
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
		xmlResult = "Fichero enviado al S.E.P.E.";
		setPollEnabled(true);
		comunicationProcess = true;
		dataQueryProcess=false;
		TestThread thread = new TestThread();
		thread.start();
//		saveContrataResultFile();
	}
	public void onContrataDataQuery(ActionEvent event){
		setPollEnabled(true);
		dataQueryProcess=true;
		comunicationProcess = false;
		TestThread thread = new TestThread();
		thread.start();
	}
		
	private String user;
	private String mainUser;
	private String passwd;
	
	
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
		String DOCUMENTO = "C0056300";
		xmlResult = ContrataManager.processDataQuery(DOCUMENTO, getUser(), getUser(), getPasswd());
		
		System.out.println("RESPUESTA RESULTANTE DE LA CONSULTA AL S.E.P.E. :  " );
		System.out.println(xmlResult);
		xmlResult.replaceAll("\n", "<br />");
	}
	
	private void saveContrataResultFile(){
		ContractAttachment contrataResultAttach = new ContractAttachment();
		try {
			FileInputStream fis = new FileInputStream(xmlResult);
			byte fileContent[] = new byte[(int)xmlResult.length()];
			fis.read(fileContent);
			contrataResultAttach.setContract(getParams().getContract());
			contrataResultAttach.setData(fileContent);
			contrataResultAttach.setAttachmentType(null);
			contrataResultAttach.setMimeType(MimeType.MIME_XML);
			contrataResultAttach.setDescription("Fichero respuesta contrat@");
			fis.close();
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
			bean.insertOrUpdate(contrataResultAttach);
			ContractAttachController attachController = (ContractAttachController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_ATTACH_CONTROLLER);
			attachController.initializeModel();
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido generar el documento de respuesta (XML) de contrata";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
	}
	
	private class TestThread extends Thread {
	    public void run() {
			if(comunicationProcess){
				sendContrataFile();
				xmlResult = "Fichero enviado al S.E.P.E.";
			} else if(dataQueryProcess){
				contrataDataQuery();
				xmlResult = "consulta";
			}
//			setPollEnabled(false);
	    }
	}

}
