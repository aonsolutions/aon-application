package com.esferalia.aon.ui.sepe.controller;

import java.util.List;

import javax.faces.event.AbortProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.payroll.ContractInfo;
import com.esferalia.aon.payroll.ContractInfo.ContractSepeStatus;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContrataFileType;
import com.esferalia.aon.ui.sepe.controller.handler.ContrataProrrogasHandler;
import com.esferalia.aon.ui.sepe.utils.SEPEFileUtils;


public class ContrataProrrogasController extends ContrataAbstractController implements IContrataController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContrataProrrogasController.class.getName());
	
	private Integer extensionNumber;
	
	public Integer getExtensionNumber() {
		if(extensionNumber==null){
			extensionNumber = 1;
		}
		return extensionNumber;
	}
	public void setExtensionNumber(Integer extensionNumber) {
		this.extensionNumber = extensionNumber;
	}

	@Override
	public ContrataFileType getContrataFileType() {
		return ContrataFileType.EXTENSION;
	}

	@Override
	public String getContrataModelName() {
		return "extension";
	}

	@Override
	protected String getSchemaFileName() {
		return SEPEFileUtils.PRORROGAS_SCHEMA_FILE_NAME;
	}

	@Override
	protected ContractAttachmentType getAttachmentType() {
		return ContractAttachmentType.SEPE_EXTENSION_FILE;
	}

	@Override
	protected void initContrataFile() {
		List<IAttachment> list = obtainContrataList(ContractAttachmentType.SEPE_EXTENSION_FILE);
		if(list!=null && !list.isEmpty() && list.size()>=getExtensionNumber()){
			setGeneratedFile(list.get(getExtensionNumber()-1));
		}
	}

	@Override
	protected void initHandler() {
		setHandler( new ContrataProrrogasHandler() );
	}
	
	@Override
	protected void beforeContrataAccept() throws ManagerBeanException {
		if( isNevv() ){
			
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
			info.setName( ContractVariable.SEPE_EXTENSION.getValue() );
			info.setExpression( ContractSepeStatus.PENDING.getValue() );
			bean.insert(info);
		}
	}
		
	@Override
	protected void processSepeResult(String result) {
//		TODO
		LOGGER.info("PRORROGAS: processSepeResult in progress...");
	}
	
}
