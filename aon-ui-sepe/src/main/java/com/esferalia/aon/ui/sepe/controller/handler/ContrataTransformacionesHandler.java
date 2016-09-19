package com.esferalia.aon.ui.sepe.controller.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contrata.ContrataTransformacionesParams;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.contrata.TEJINDIS;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.file.ContrataReader;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;


public class ContrataTransformacionesHandler implements IContrataHandler, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
		SEPEUtils utils = SEPEUtils.getInstance();
		this.contract = contract;
		this.contractCode = ContractCode.getContractCodeByValue( utils.getContractDataMap(this.contract).get(ContextVariable.TC2.getName()) );
		getParams().setFechaInicio(contract.getStartDate());
		getParams().setSourceStartDate(contract.getSeniorityDate());
//		getParams().setFechaTerminoReal(fechaTerminoReal);
//		getParams().setCno(obtainCno(utils.getContractDataMap(this.contract).get(ContextVariable.CNO.getName())));
	}
	
	@Override
	public void loadContrataData(IAttachment attach) throws ManagerBeanException, IOException{
		// TODO make ContrataReader return correct params type
		if(attach==null){
			attach = new ContractAttachment();
		} 
		else {
			ContrataReader reader = new ContrataReader();
			this.params = (ContrataTransformacionesParams) reader.readFile( new ByteArrayInputStream(attach.getData()) );
			if(StringUtils.isBlank(params.getSourceContractSepeId())){
				params.setSourceContractSepeId( SEPEUtils.getInstance().getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()) );
			}
		}
	}
	
	private CNO obtainCno(String expression) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(CNO.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CNO_CODE), expression);
			List<ITransferObject> list = bean.getList(criteria);
			if( !list.isEmpty() ){
				return (CNO) list.get(0);
			} else {
				return (CNO) bean.createNewTo();
			}
		} catch (ManagerBeanException e) {
			// do nothing ...
		}
		return null;
	}
	
	public Boolean getShowDatosBonificacionPanel(){
		// TODO: create data input panel in view
		return getContractCode()==ContractCode.C109 || getContractCode()==ContractCode.C139
				|| getContractCode()==ContractCode.C209 || getContractCode()==ContractCode.C239
				|| getContractCode()==ContractCode.C309 || getContractCode()==ContractCode.C339;
	}

	// //////////////////////////////////////
	// checks datos especificos contrato
	// //////////////////////////////////////
	public Boolean getShowColectivoBonificacion() {
		return true;
	}
		
}
