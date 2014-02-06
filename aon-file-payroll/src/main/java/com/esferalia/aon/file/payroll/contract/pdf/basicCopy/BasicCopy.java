package com.esferalia.aon.file.payroll.contract.pdf.basicCopy;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryDirStaff;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.contrata.TEQPTIEM;
import com.lowagie.text.pdf.PdfReader;



public class BasicCopy extends AbstractContractBasicCopy {
	
	public final static String BASIC_COPY_NAME = "ContractBasicCopy";
	
	public BasicCopy(){
		super.documentName = BASIC_COPY_NAME;
	}
	
	public void loadPdfFieldValues(ContractCode code, Contract contract, IContrataParams contrataParams) throws UnsupportedContractDocumentException{
		
		
		try {
			setReader(new PdfReader(getContractBasicCopyUrl(documentName+".pdf")));
			readPdfFields();
//			super.loadPdfCommonFields(contract);

			ContrataContratoParams params = (ContrataContratoParams) contrataParams;
			SimpleDateFormat dateFormatter = new SimpleDateFormat();
			
			/* 
			 * Enterprise fields
			 */
			setPdfFieldValue(BasicCopyField.ENTERPRISE_CIF.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
			RegistryDirStaff rDirStaff = getHandler().obtainRegistryDirStaff(contract.getWorkPlace().getEnterprise().getRegistry()); 
			try {
				setPdfFieldValue(BasicCopyField.ENTERPRISE_DIR_STAFF_NAME.getValue(),rDirStaff.getName());
			} catch (NullPointerException npe) {
				// do nothing
			}
			setPdfFieldValue(BasicCopyField.ENTERPRISE_NAME.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
			
			RegistryAddress address = contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress();
			setPdfFieldValue(BasicCopyField.ENTERPRISE_ADDRESS.getValue(),address.getFullAddress());
			try {
				// TODO: Must include enterprise municipality name in the contract basic copy?
//				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
//				setPdfFieldValue(ENTERPRISE_MUNICIPALITY_NAME,bundle.getString(address.getMunicipalityCode()));
				setPdfFieldValue(BasicCopyField.ENTERPRISE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
				setPdfFieldValue(BasicCopyField.ENTERPRISE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
				setPdfFieldValue(BasicCopyField.ENTERPRISE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
				setPdfFieldValue(BasicCopyField.ENTERPRISE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
				setPdfFieldValue(BasicCopyField.ENTERPRISE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/*
			 * Employee fields
			 */
			setPdfFieldValue(BasicCopyField.EMPLOYEE_NAME.getValue(),contract.getPerson().getFullName());
			
			/*
			 * Contract fields
			 */
			dateFormatter.applyPattern("dd/MM/yyyy");
			setPdfFieldValue(BasicCopyField.CONTRACT_START_DATE.getValue(),dateFormatter.format(contract.getStartDate()));
			
			
			if(code!=null){
				setPdfFieldValue(BasicCopyField.CONTRACT_TYPE.getValue(), code.getName(getLocale()));
			}	
//			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(ContractVariable.CONTRACT_MODEL_OPTION.getValue()))){
//				setPdfFieldValue(BasicCopyField.CONTRACT_TYPE.getValue(), getContractInfoMap(contract).get(ContractVariable.CONTRACT_MODEL_OPTION.getValue()));
//			}
			
			if(code.getValue().startsWith("1") || code.getValue().startsWith("2")){
				setPdfFieldValue(BasicCopyField.CONTRACT_TOTAL_DURATION.getValue(),"INDEFINIDO");
			} else if(code.getValue().startsWith("4") || code.getValue().startsWith("5")){
				setPdfFieldValue(BasicCopyField.CONTRACT_TOTAL_DURATION.getValue(),"TEMPORAL");
			} else if(code.getValue().startsWith("3")){
				setPdfFieldValue(BasicCopyField.CONTRACT_TOTAL_DURATION.getValue(),"DISCONTINUO");
			}
			
			setPdfFieldValue(BasicCopyField.CONTRACT_CATEGORY.getValue(), contract.getCategoryDescription());
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(BasicCopyField.SALARY_AMOUNT.toString()))){
				setPdfFieldValue(BasicCopyField.SALARY_AMOUNT.getValue(), getContractInfoMap(contract).get(BasicCopyField.SALARY_AMOUNT.toString()));
			}
//			setPdfFieldValue(CONTRACT_REMUNERATION_EURO, "euros brutos");
			setPdfFieldValue(BasicCopyField.SALARY_AMOUNT_EURO.getValue(), "");
			
//			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(BasicCopyField.SALARY_PERIOD.toString()))){
//				setPdfFieldValue(BasicCopyField.SALARY_PERIOD.getValue(), getContractInfoMap(contract).get(BasicCopyField.SALARY_PERIOD.toString()));
//			}
			setPdfFieldValue(BasicCopyField.SALARY_PERIOD.getValue(), "");
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(BasicCopyField.HOLIDAYS.toString()))){
				setPdfFieldValue(BasicCopyField.HOLIDAYS.getValue(), getContractInfoMap(contract).get(BasicCopyField.HOLIDAYS.toString()));
			}
			
			setPdfFieldValue(BasicCopyField.CONTRACT_SIGN_TOWN.getValue(),contract.getWorkPlace().getAddress().getCity());
			setPdfFieldValue(BasicCopyField.CONTRACT_SING_DAY.getValue(), String.valueOf(CommonUtil.getDay(contract.getStartDate())));
			dateFormatter.applyPattern("MMMM");
			setPdfFieldValue(BasicCopyField.CONTRACT_SIGN_MONTH.getValue(), dateFormatter.format(contract.getStartDate()) );
			dateFormatter.applyPattern("yy");
			setPdfFieldValue(BasicCopyField.CONTRACT_SIGN_YEAR.getValue(), dateFormatter.format(contract.getStartDate()));
			
			
			if(contrataParams!=null){
				String horasJornada = params.getHorasJornada();
				if(horasJornada!=null){
					getPdfFieldsMap().get(BasicCopyField.CONTRACT_JOURNAL_HOURS_1.getValue()).setValue(String.valueOf(Integer.parseInt(horasJornada)));				
//					getPdfFieldsMap().get(CONTRACT_JOURNAL_HOURS_2).setValue(String.valueOf(minutosJornada));;
					
					if(params.getTipoJornada()==TEQPTIEM.TEQPTIEM_A){
						getPdfFieldsMap().get(BasicCopyField.CONTRACT_JOURNAL.getValue()).setValue("HORAS ANUALES");;
					} else if (params.getTipoJornada()==TEQPTIEM.TEQPTIEM_D){
						getPdfFieldsMap().get(BasicCopyField.CONTRACT_JOURNAL.getValue()).setValue("HORAS DIARIAS");;
					} else if (params.getTipoJornada()==TEQPTIEM.TEQPTIEM_M){
						getPdfFieldsMap().get(BasicCopyField.CONTRACT_JOURNAL.getValue()).setValue("HORAS MENSUALES");;
					} else if (params.getTipoJornada()==TEQPTIEM.TEQPTIEM_S){
						getPdfFieldsMap().get(BasicCopyField.CONTRACT_JOURNAL.getValue()).setValue("HORAS SEMANALES");;
					}
				}
			}
			
		} catch (IOException e) {
			// do nothing
		} catch (ManagerBeanException e) {
			// do nothing
		}
	}
	
	

}
	
	