package com.esferalia.aon.file.payroll.contract.pdf.annex;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE229 extends AbstractAnnexModel {
	
	
	public final static String MODEL_NAME = "PE229";
	
	public ModelPE229(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract) throws UnsupportedContractDocumentException{
		// TODO
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			
			readPdfFields(reader);
			
			super.loadPdfCommonFields(contract);
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Boolean isQuoteBonus(Contract contract) {
		String subsidized = getContractDataMap(contract).get(ContextVariable.SUBSIDIZED.getName());
		return Boolean.parseBoolean(subsidized);
	}
	
	public Map<String, String> getContractDataMap(Contract contract) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			criteria.addNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
			for(ITransferObject to: bean.getList(criteria)){
				ContractData data = (ContractData) to;
				map.put(data.getName(), data.getExpression().replace('"', ' ').trim());
			}
		} catch (ManagerBeanException e) {
			// NADA, se devuelve un mapa vacio
			return map;
		}
		return map;
	}
	
}
	
	