package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE226 extends AbstractContractModel {
	
	/*
	 * Contract page 1
	 */
	final static String PE226_QUOTE_BONUS = "si_bonif_cuota";
	final static String PE226_QUOTE_NO_BONUS = "no_bonif_cuota";
	final static String PE226_REPRESENTATIVE_NAME = "nomreptra";
	final static String PE226_REPRESENTATIVE_DOCUMENT = "dnireptra";
	final static String PE226_REPRESENTATIVE_FUNCTION = "calireptra";
	final static String PE226_QUOTE_BONUS_YES = "verificacion123";
	final static String PE226_QUOTE_BONUS_NO = "verificacion1234";
	final static String PE226_EMPLOYEE_OPT1 = "entre16y30";
	final static String PE226_EMPLOYEE_OPT2 = "trabdiscap";
	final static String PE226_EMPLOYEE_OPT3 = "alumproy";
	final static String PE226_EMPLOYEE_PROFFESION = "profetraba";
	final static String PE226_EMPLOYEE_CATEGORY = "catetraba";
	final static String PE226_CNO1 = "cno1";
	final static String PE226_CNO2 = "cno2";
	final static String PE226_CNO3 = "cno3";
	final static String PE226_CNO4 = "cno4";

	/*
	 * Contract page 2
	 */
	final static String PE226_YEAR_1_JOURNAL = "año1";
	final static String PE226_YEAR_2_3_JOURNAL = "año2y3";
	final static String PE226_TOTAL_HOURS = "trabefec";
	final static String PE226_JOURNAL_PERCENT = "porcforma";
	final static String PE226_COLLECTIVE_AGREEMENT = "convcole";
	final static String PE226_JOURNAL_HORUS = "jornhoraefec";
	final static String PE226_CONTRACT_DURATION = "totaldura";
	final static String PE226_START_DATE = "fechaini";
	final static String PE226_END_DATE = "fechafin";
	final static String PE226_TEST_PERIOD = "periodoprue";
	final static String PE226_SALARY = "retribu";
	final static String PE226_SALARY_PERIOD = "perioretri";
	final static String PE226_VACATIONS = "vacaciones";
	final static String PE226_REDUCCTION_75 = "porc_75_250";
	final static String PE226_REDUCTION_100 = "porc_100_249";
	final static String PE226_COLLECTIVE_AGREEMENT2 = "convcole2";
	final static String PE226_SIGN_TOWN = "munifirma";
	final static String PE226_MORE_CLAUSE = "clausadici";
	final static String PE226_SING_DAY = "diafirma";
	final static String PE226_SIGN_MONTH = "mesfirma";
	final static String PE226_SIGN_YEAR = "añofirma";
	
	public final static String MODEL_NAME = "PE226";
	
	public ModelPE226(){
		super.modelName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract) throws UnsupportedContractModelException{
		// TODO
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(modelName+".pdf"));
			
			readPdfFields(reader);
			
			if(code == ContractCode.C421){
				if( isQuoteBonus(contract)!=null ){
					if( isQuoteBonus(contract) ){
						getPdfFieldsMap().get(PE226_QUOTE_BONUS).setValue("true");
					} else if( !isQuoteBonus(contract) ){
						getPdfFieldsMap().get(PE226_QUOTE_NO_BONUS).setValue("true");
					}
				}
			} else {
				throw new UnsupportedContractModelException("El modelo de contrato seleccionado es incorrecto");
			}
			
			super.loadPdfCommonFields(contract);
			
			getPdfFieldsMap().get(PE226_REPRESENTATIVE_NAME).setValue("");
			getPdfFieldsMap().get(PE226_REPRESENTATIVE_DOCUMENT).setValue("");
			getPdfFieldsMap().get(PE226_REPRESENTATIVE_FUNCTION).setValue("");
			getPdfFieldsMap().get(PE226_QUOTE_BONUS_YES).setValue("");
			getPdfFieldsMap().get(PE226_QUOTE_BONUS_NO).setValue("");
			getPdfFieldsMap().get(PE226_EMPLOYEE_OPT1).setValue("");
			getPdfFieldsMap().get(PE226_EMPLOYEE_OPT2).setValue("");
			getPdfFieldsMap().get(PE226_EMPLOYEE_OPT3).setValue("");
			getPdfFieldsMap().get(PE226_EMPLOYEE_PROFFESION).setValue("");
			getPdfFieldsMap().get(PE226_EMPLOYEE_CATEGORY).setValue("");
			
			Map<String, String>  map = getContractDataMap(contract);
			String cno = map.get(ContextVariable.CNO.getName());
			if( !StringUtils.isEmpty(cno) ){
				getPdfFieldsMap().get(PE226_CNO1).setValue(cno.substring(0, 1));
				getPdfFieldsMap().get(PE226_CNO2).setValue(cno.substring(1, 2));
				getPdfFieldsMap().get(PE226_CNO3).setValue(cno.substring(2, 3));
				getPdfFieldsMap().get(PE226_CNO4).setValue(cno.substring(3, 4));
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Boolean isQuoteBonus(Contract contract) {
		// TODO 
		return null;
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
	
	