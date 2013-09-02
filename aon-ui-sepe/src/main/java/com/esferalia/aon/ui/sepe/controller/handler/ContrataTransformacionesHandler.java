package com.esferalia.aon.ui.sepe.controller.handler;

import java.io.IOException;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.IAttachment;
import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contrata.ContrataTransformacionesParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;


public class ContrataTransformacionesHandler implements IContrataHandler {
	
	private Contract contract;
	
	private ContrataTransformacionesParams params;
	
	private ContractCode contractCode;

	
	@Override
	public Contract getContract() {
		return contract;
	}
	
	@Override
	public ContrataTransformacionesParams getParams() {
		if(params==null){
			params = new ContrataTransformacionesParams();
		}
		return params;
	}

	@Override
	public ContractCode getContractCode() {
		return contractCode;
	}
	
	@Override
	public boolean isCommunicationAvailable(){
		if(getContractCode()!=null){
			return ArrayUtils.contains(ISepeConstants.AVAILABLE_TRANSFORM_CODE_COMMUNICATION, getContractCode().getValue());
		}
		return false;
	}
	
	@Override
	public void initialize(Contract contract){
		SEPEUtils utils = new SEPEUtils();
		this.contract = contract;
		this.contractCode = ContractCode.getContractCodeByValue( utils.getContractDataMap(this.contract).get(ContextVariable.TC2.getName()) );
	}
	
	@Override
	public void loadContrataData(IAttachment attach) throws ManagerBeanException, IOException{
		// TODO make ContrataReader return correct params type
//		try {
//			if(contrataAttach!=null){
//				ContrataReader reader = new ContrataReader();
//				this.params = reader.readFile( new ByteArrayInputStream(contrataAttach.getData()) );
//			}
//		} catch (ManagerBeanException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
		
}
