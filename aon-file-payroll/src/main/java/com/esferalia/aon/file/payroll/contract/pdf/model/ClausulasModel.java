package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractClause;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class ClausulasModel extends AbstractContractModel {
	
	public final static String MODEL_NAME = "Clausulas";
	
	public ClausulasModel(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFieldValues(ContractCode code, Contract contract, IContrataParams contrataParams) throws UnsupportedContractDocumentException{
		
		// TODO
		try {
			setReader(new PdfReader(getContractModelUrl(documentName+".pdf")));
//			String range = "1-3";
//			ModelOption modelOption = ModelOption.valueOf(getContractInfoMap(contract).get(ContractVariable.CONTRACT_MODEL_OPTION.getValue()));
//			range += ","+modelOption.getPageNumber();
//			getReader().selectPages(range);
			readPdfFields();
			
//			ContrataContratoParams contrata = (ContrataContratoParams) contrataParams;
			
//			super.loadPdfCommonFields(contract, contrataParams);
			
			setPdfFieldValue(ClausesCommonFieldName.CONTENT.getValue(), obtainClausesContent(contract));

			SimpleDateFormat dateFormatter = new SimpleDateFormat();
			setPdfFieldValue(ClausesCommonFieldName.SIGN_TOWN.getValue(),contract.getWorkPlace().getAddress().getCity());
			dateFormatter.applyPattern("dd");
			setPdfFieldValue(ClausesCommonFieldName.SIGN_DAY.getValue(),dateFormatter.format(contract.getStartDate()));
			dateFormatter.applyPattern("MMMM");
			setPdfFieldValue(ClausesCommonFieldName.SIGN_MONTH.getValue(),dateFormatter.format(contract.getStartDate()));
			dateFormatter.applyPattern("yy");
			setPdfFieldValue(ClausesCommonFieldName.SIGN_YEAR.getValue(),dateFormatter.format(contract.getStartDate()));

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String obtainClausesContent(Contract contract) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractClause.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_CONTRACT_ID), contract.getId());
		criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_LINE));
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			StringBuffer bf = new StringBuffer();
			int line = 0;
			for(ITransferObject to: list){
				line++;
				ContractClause clause = (ContractClause) to;
				bf.append(line);
				bf.append(". ");
				bf.append(clause.getName());
				bf.append("\n");
				bf.append(clause.getDescription());
				bf.append("\n\n");
			}
			return bf.toString();
		}
		return null;
	}

	/*
	 * INNER CLASSES
	 */
	
	public enum ClausesCommonFieldName implements IContractFieldName{
		/*
		 * Page 1
		 */
		CONTENT("Texto209",Boolean.FALSE),
		SIGN_TOWN("munifirma",Boolean.FALSE),
		SIGN_DAY("diafirma",Boolean.FALSE),
		SIGN_MONTH("mesfirma",Boolean.FALSE),
		SIGN_YEAR("añofirma",Boolean.FALSE),
		;
		
		private String value;
		private boolean overridable;
		
		private ClausesCommonFieldName(String value, boolean overridable) {
			this.value = value;
			this.overridable = overridable;
		}
		
		@Override
		public boolean isOverridable(){
			return overridable;
		}
		@Override
		public String getValue() {
			return value;
		}
	}
	
}
	
	