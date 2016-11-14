package com.esferalia.aon.file.payroll.contract.pdf.basicCopy;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryDirStaff;
import com.esferalia.aon.file.payroll.contract.pdf.PdfFieldIndefinite;
import com.esferalia.aon.file.payroll.contract.pdf.PdfFieldTemporary;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.contrata.TEQPTIEM;
import com.lowagie.text.pdf.PdfReader;



public class BasicCopy extends AbstractContractBasicCopy {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public final static String BASIC_COPY_NAME = "ContractBasicCopy";
	
	public BasicCopy(){
		super.documentName = BASIC_COPY_NAME;
	}
	
	public void loadPdfFieldValues(ContractCode code, Contract contract, List<IContrataParams> contrataParams) throws UnsupportedContractDocumentException{
		
		try {
			PdfReader reader = new PdfReader(getContractBasicCopyUrl(documentName+".pdf"));
			readPdfFields(reader);

			ContrataContratoParams contrata = null;
			if(contrataParams!=null && contrataParams.size()>0){
				contrata = (ContrataContratoParams) contrataParams.get(0);
			}
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
				if(StringUtils.isNotBlank(address.getMunicipalityCode())){
					setPdfFieldValue(BasicCopyField.ENTERPRISE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
					setPdfFieldValue(BasicCopyField.ENTERPRISE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
					setPdfFieldValue(BasicCopyField.ENTERPRISE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
					setPdfFieldValue(BasicCopyField.ENTERPRISE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
					setPdfFieldValue(BasicCopyField.ENTERPRISE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
					ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
					setPdfFieldValue(BasicCopyField.ENTERPRISE_MUNICIPALITY_NAME.getValue(),bundle.getString(address.getMunicipalityCode()));
				}
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
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(BasicCopyField.HOLIDAYS.toString()))){
				setPdfFieldValue(BasicCopyField.HOLIDAYS.getValue(), getContractInfoMap(contract).get(BasicCopyField.HOLIDAYS.toString()));
			}
			
			setPdfFieldValue(BasicCopyField.CONTRACT_SIGN_TOWN.getValue(),contract.getWorkPlace().getAddress().getCity());
			setPdfFieldValue(BasicCopyField.CONTRACT_SING_DAY.getValue(), String.valueOf(CommonUtil.getDay(contract.getStartDate())));
			dateFormatter.applyPattern("MMMM");
			setPdfFieldValue(BasicCopyField.CONTRACT_SIGN_MONTH.getValue(), dateFormatter.format(contract.getStartDate()) );
			dateFormatter.applyPattern("yy");
			setPdfFieldValue(BasicCopyField.CONTRACT_SIGN_YEAR.getValue(), dateFormatter.format(contract.getStartDate()));
			
			String weekHours = getContractDataMap(contract).get(ContextVariable.WEEK_HOURS.toString());
			boolean isFullTimeDiscontinuous = code.getValue().startsWith("3") && StringUtils.isBlank(weekHours);
			boolean isPartialTimeDiscontinuous = code.getValue().startsWith("3") && StringUtils.isNotBlank(weekHours);
			if(code.getValue().startsWith("1") || code.getValue().startsWith("4") || isFullTimeDiscontinuous){
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.FULL_TIME_START_TIME.toString()))
					&& StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.FULL_TIME_END_TIME.toString()))){
					String journalPeriod = "";
					if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.FULL_TIME_WEEK_HOURS.toString()))){
						journalPeriod += getContractInfoMap(contract).get(PdfFieldIndefinite.FULL_TIME_WEEK_HOURS.toString());				
						if(!StringUtils.containsIgnoreCase(journalPeriod,"HORAS")){
							journalPeriod += " HORAS";
						}
					}
					journalPeriod += " (" + getContractInfoMap(contract).get(PdfFieldIndefinite.FULL_TIME_START_TIME.toString()) + " - ";
					journalPeriod += getContractInfoMap(contract).get(PdfFieldIndefinite.FULL_TIME_END_TIME.toString()) + ")";
					getPdfFieldsMap().get(BasicCopyField.CONTRACT_JOURNAL.getValue()).setValue(journalPeriod);
				}
			} else if(code.getValue().startsWith("2") || code.getValue().startsWith("5") || isPartialTimeDiscontinuous){
				if(contrata!=null){
					String horasJornada = null;
					if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.FULL_TIME_WEEK_HOURS.toString()))){
						horasJornada = getContractInfoMap(contract).get(PdfFieldIndefinite.FULL_TIME_WEEK_HOURS.toString());				
					}
					if(horasJornada==null){
						horasJornada = String.valueOf(Integer.parseInt(contrata.getHorasJornada()));
						horasJornada += " HORA" + (Integer.parseInt(contrata.getHorasJornada())==1?"":"S");
						if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_A){
							horasJornada += " ANUAL";
							horasJornada += Integer.parseInt(contrata.getHorasJornada())==1?"":"ES";
						} else if (contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_D){
							horasJornada += " DIARIA";
							horasJornada += Integer.parseInt(contrata.getHorasJornada())==1?"":"S";
						} else if (contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_M){
							horasJornada += " MENSUAL";
							horasJornada += Integer.parseInt(contrata.getHorasJornada())==1?"":"ES";
						} else if (contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_S){
							horasJornada += " SEMANAL";
							horasJornada += Integer.parseInt(contrata.getHorasJornada())==1?"":"ES";
						}
					}
					String fullTimeStart = getContractInfoMap(contract).get(PdfFieldTemporary.FULL_TIME_START_TIME.toString());
					String fullTimeEnd = getContractInfoMap(contract).get(PdfFieldTemporary.FULL_TIME_END_TIME.toString());
					if(fullTimeStart!=null || fullTimeEnd!=null){
						horasJornada += " (" + (fullTimeStart!=null?fullTimeStart:"");
						horasJornada += fullTimeStart!=null && fullTimeEnd!=null?" - ":"";
						horasJornada += (fullTimeEnd!=null?fullTimeEnd:"") + ")";
					}
					getPdfFieldsMap().get(BasicCopyField.CONTRACT_JOURNAL.getValue()).setValue(horasJornada);
				}
			}
			
		} catch (IOException e) {
			// do nothing
		} catch (ManagerBeanException e) {
			// do nothing
		}
	}
	
}
	
	