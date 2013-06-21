package com.esferalia.aon.file.payroll.contract.pdf.extension;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContractExtensionParams;
import com.esferalia.aon.file.payroll.contrata.ContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class Extension extends AbstractContractExtension {
	
	final static String PE191_PREVIOUS_CONTRACT_MODEL = "modelocont";
	final static String PE191_RD = "rdcont";
	
	final static String PE191_REPRESENTATIVE_NAME = "nombdtoempresa";
	final static String PE191_REPRESENTATIVE_DOCUMENT = "nifempresa";
	final static String PE191_REPRESENTATIVE_FUNCTION = "repre empresa";
	
	final static String PE191_SEPE_NAME = "nombse";
	final static String PE191_EXTENSION_NUMBER = "numproga";
	final static String PE191_EXTENSION_MONTH_COUNT = "mesesdurapro";
	final static String PE191_EXTENSION_START_DATE = "fechainipro";
	final static String PE191_EXTENSION_END_DATE = "fechaterpro";
	final static String PE191_CONTRACT_START_DATE = "fechainicont";
	final static String PE191_CONTRACT_MONTH_COUNT = "mesesduracont";
	final static String PE191_SEPE_TOWN = "munif";
	final static String PE191_CONTRACT_REGULATION_DATE = "fecharegcont";
	final static String PE191_CONTRACT_SEPE_ID = "idcontconv";
	final static String PE191_TOTAL_DURATION1 = "Texto8proooo21";
	final static String PE191_TOTAL_DURATION2 = "totaldura";
	
	final static String PE191_SIGN_TOWN = "munifirma";
	final static String PE191_SIGN_DAY = "diasfirma";
	final static String PE191_SIGN_MONTH = "mesfirma";
	final static String PE191_SIGN_YEAR = "año";
	
	
	public final static String EXTENSION_NAME = "PE191";
	
	public Extension(){
		super.documentName = EXTENSION_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract, ContrataParams contrataParams) throws UnsupportedContractDocumentException{
		// TODO
		
		try {
			PdfReader reader = new PdfReader(getContractExtensionUrl(documentName+".pdf"));
			SimpleDateFormat dateFormatter = new SimpleDateFormat();
			
			readPdfFields(reader);
			
			super.loadPdfCommonFields(contract, contrataParams);
			
			getPdfFieldsMap().get(PE191_PREVIOUS_CONTRACT_MODEL).setValue(null);
			getPdfFieldsMap().get(PE191_RD).setValue(null);
			
			getPdfFieldsMap().get(PE191_REPRESENTATIVE_NAME).setValue(null);
			getPdfFieldsMap().get(PE191_REPRESENTATIVE_DOCUMENT).setValue(null);
			getPdfFieldsMap().get(PE191_REPRESENTATIVE_FUNCTION).setValue(null);
			
			getPdfFieldsMap().get(PE191_SEPE_NAME).setValue(contract.getWorkPlace().getAddress().getCity());
			getPdfFieldsMap().get(PE191_EXTENSION_NUMBER).setValue(String.valueOf(getContractExtensionCount(contract)));

			dateFormatter.applyPattern("dd/MM/yyyy");
//			getPdfFieldsMap().get(PE191_EXTENSION_START_DATE).setValue(dateFormatter.format());
//			getPdfFieldsMap().get(PE191_EXTENSION_END_DATE).setValue(dateFormatter.format());
//			Integer extensionDurationInMonths = getMonthsBetweenDates();
//			getPdfFieldsMap().get(PE191_EXTENSION_MONTH_COUNT).setValue(extensionDurationInMonths);
			
			getPdfFieldsMap().get(PE191_CONTRACT_START_DATE).setValue(dateFormatter.format(contract.getStartDate()));
			Integer contractDurationInMonths = getMonthsBetweenDates(contract.getStartDate(), contract.getEndDate());
			getPdfFieldsMap().get(PE191_CONTRACT_MONTH_COUNT).setValue(contractDurationInMonths!=null?contractDurationInMonths.toString():null);
			
			getPdfFieldsMap().get(PE191_SEPE_TOWN).setValue(contract.getWorkPlace().getAddress().getCity());
			getPdfFieldsMap().get(PE191_CONTRACT_REGULATION_DATE).setValue(null);
			getPdfFieldsMap().get(PE191_CONTRACT_SEPE_ID).setValue(null);
			
//			Integer totalDurationInMonths = contractDurationInMonths + extensionDurationInMonths;
//			getPdfFieldsMap().get(PE191_TOTAL_DURATION1).setValue(totalDurationInMonths!=null?totalDurationInMonths.toString():null);
			getPdfFieldsMap().get(PE191_TOTAL_DURATION2).setValue("meses");
			
			getPdfFieldsMap().get(PE191_SIGN_TOWN).setValue(contract.getWorkPlace().getAddress().getCity());
//			dateFormatter.applyPattern("dd");
//			getPdfFieldsMap().get(PE191_SIGN_DAY).setValue(dateFormatter.format(extensionParams.getStartDate()));
//			dateFormatter.applyPattern("MMMM");
//			getPdfFieldsMap().get(PE191_SIGN_MONTH).setValue(dateFormatter.format(extensionParams.getStartDate()));
//			dateFormatter.applyPattern("yy");
//			getPdfFieldsMap().get(PE191_SIGN_YEAR).setValue(dateFormatter.format(extensionParams.getStartDate()));
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int getContractExtensionCount(Contract contract) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), contract.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.CONTRACT_EXTENSION);
		return bean.getCount(criteria)+1;
	}

	private Integer getMonthsBetweenDates(Date startDate, Date endDate) {
		if(startDate!=null && endDate!=null){
			return (int) ((CommonUtil.getDaysBetweenDates(startDate, endDate, true))/30);
		}
		return null;
	}

}
	
	