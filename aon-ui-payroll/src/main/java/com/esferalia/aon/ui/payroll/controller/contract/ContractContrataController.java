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
	//	public ContractContrataFactory getFactory() {
//		return factory;
//	}
//	public void setFactory(ContractContrataFactory factory) {
//		this.factory = factory;
//	}
	public ContrataParams getParams() {
		return getHandler().getParams();
	}
	

	public void initialize(Contract contract){
		contractDataMap = null;
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
//			String msg = "Transformacion de contrato no implementado para generar el fichero contrat@";
//			AonUtil.addErrorMessage(msg);
//			throw new AbortProcessingException(msg);
		}
		if( code.equals(ContractCode.C408.getValue())
				 || code.equals(ContractCode.C418.getValue())
				 || code.equals(ContractCode.C508.getValue())
				 || code.equals(ContractCode.C518.getValue()) ){
			String msg = "PRorroga de contrato no implementado para generar el fichero contrat@";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		try {
			readXmlFile(getHandler().getParams().getContract());
		} catch (ManagerBeanException e) {
			String msg = "Error al guardar el documento xml de contrata";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
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
			String msg = "Error al guardar el documento xml de contrata";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	private ContractAttachment obtainContrataAttach(Contract contract){
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), contract.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.SPEE_CONTRATA);
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				return (ContractAttachment) list.get(0);
			}
		} catch (ManagerBeanException e) {
			// NOTHING TO TO
		}
		return null;
	}
	
	private void readXmlFile(Contract contract) throws ManagerBeanException {
		setContrataAttach( obtainContrataAttach(contract) );
		if(getContrataAttach()!=null){
			try {
				ContrataReader reader = new ContrataReader();
				ContrataParams params = reader.readFile( getContrataAttach() );
				getHandler().setParams(params);
				getHandler().getParams().setContract(contract);
			} catch(IOException e) {
				String msg = "Error al guardar el documento xml de contrata";
				LOGGER.error(msg);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg, e);
			} 
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
			getContrataAttach().setAttachmentType(ContractAttachmentType.SPEE_CONTRATA);
			getContrataAttach().setMimeType(MimeType.MIME_XML);
			getContrataAttach().setDescription("Fichero contrat@");
			fis.close();
		} catch(IOException e) {
			String msg = "Error al guardar el documento xml de contrata";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} 
	}

	
	private Map<String, String> contractDataMap;
	
	protected Map<String, String> getContractDataMap() {
		if(contractDataMap==null || contractDataMap.isEmpty()){
			PayrollUtils utils = new PayrollUtils();
			contractDataMap = utils.getContractDataMap(getHandler().getParams().getContract());
		}
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
	
}
