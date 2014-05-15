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
import com.esferalia.aon.file.payroll.contract.pdf.IContractFieldName;
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
		try {
			setReader(new PdfReader(getContractModelUrl(documentName+".pdf")));
			readPdfFields();
			
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
			// do nothing
		} catch (ManagerBeanException e) {
			// do nothing
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
		CONTENT("Texto209"),
		SIGN_TOWN("munifirma"),
		SIGN_DAY("diafirma"),
		SIGN_MONTH("mesfirma"),
		SIGN_YEAR("añofirma"),
		;
		
		private String value;
		
		private ClausesCommonFieldName(String value) {
			this.value = value;
		}
		
		@Override
		public boolean isOverridable(){
			return false;
		}
		@Override
		public boolean isCheck(){
			return false;
		}
		@Override
		public boolean isCommonValue() {
			return false;
		}
		@Override
		public String getValue() {
			return value;
		}
		@Override
		public IContractFieldName[] getCompositeValues() {
			return null;
		}

	}
	
}
	
	