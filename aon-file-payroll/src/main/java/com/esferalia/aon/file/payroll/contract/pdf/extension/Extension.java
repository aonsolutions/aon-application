package com.esferalia.aon.file.payroll.contract.pdf.extension;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContrataProrrogaParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractInfo;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
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
	public void loadPdfFieldValues(ContractCode code, Contract contract, List<IContrataParams> params) throws UnsupportedContractDocumentException{
		// TODO
		
		try {
			PdfReader reader = new PdfReader(getContractExtensionUrl(documentName+".pdf"));
			SimpleDateFormat dateFormatter = new SimpleDateFormat();
			Map<String, String>  map = getContractInfoMap(contract);
			
			readPdfFields(reader);
			
			ContrataProrrogaParams prorrogaParams = (ContrataProrrogaParams) params.get(0);
			
			super.loadPdfCommonFields(contract, params);
			
			getPdfFieldsMap().get(PE191_PREVIOUS_CONTRACT_MODEL).setValue(code.getName(getLocale()));
			getPdfFieldsMap().get(PE191_RD).setValue("vigente a fecha inicio de contrato");
			
			getPdfFieldsMap().get(PE191_REPRESENTATIVE_NAME).setValue(null);
			getPdfFieldsMap().get(PE191_REPRESENTATIVE_DOCUMENT).setValue(null);
			getPdfFieldsMap().get(PE191_REPRESENTATIVE_FUNCTION).setValue(null);
			
			getPdfFieldsMap().get(PE191_SEPE_NAME).setValue(contract.getWorkPlace().getAddress().getCity());
			getPdfFieldsMap().get(PE191_EXTENSION_NUMBER).setValue(getContractExtensionNumber(contract));

			dateFormatter.applyPattern("dd/MM/yyyy");
			getPdfFieldsMap().get(PE191_EXTENSION_START_DATE).setValue(dateFormatter.format(prorrogaParams.getFechaInicio()));
			getPdfFieldsMap().get(PE191_EXTENSION_END_DATE).setValue(dateFormatter.format(prorrogaParams.getFechaFin()));
			Integer extensionDurationInMonths = getMonthsBetweenDates(prorrogaParams.getFechaInicio(), prorrogaParams.getFechaFin());
			getPdfFieldsMap().get(PE191_EXTENSION_MONTH_COUNT).setValue(extensionDurationInMonths.toString());
			
			getPdfFieldsMap().get(PE191_CONTRACT_START_DATE).setValue(dateFormatter.format(contract.getStartDate()));
			Integer contractDurationInMonths = getMonthsBetweenDates(contract.getStartDate(), contract.getEndDate());
			getPdfFieldsMap().get(PE191_CONTRACT_MONTH_COUNT).setValue(contractDurationInMonths!=null?contractDurationInMonths.toString():null);
			
			getPdfFieldsMap().get(PE191_SEPE_TOWN).setValue(contract.getWorkPlace().getAddress().getCity());
			getPdfFieldsMap().get(PE191_CONTRACT_REGULATION_DATE).setValue(dateFormatter.format(contract.getStartDate()));
			
			getPdfFieldsMap().get(PE191_CONTRACT_SEPE_ID).setValue(map.get(ContractVariable.SEPE_CONTRACT_ID.getValue()));
			
			Integer totalDurationInMonths = contractDurationInMonths + extensionDurationInMonths;
			getPdfFieldsMap().get(PE191_TOTAL_DURATION1).setValue(totalDurationInMonths!=null?totalDurationInMonths.toString():null);
			getPdfFieldsMap().get(PE191_TOTAL_DURATION2).setValue("meses");
			
			getPdfFieldsMap().get(PE191_SIGN_TOWN).setValue(contract.getWorkPlace().getAddress().getCity());
			dateFormatter.applyPattern("dd");
			getPdfFieldsMap().get(PE191_SIGN_DAY).setValue(dateFormatter.format(prorrogaParams.getFechaInicio()));
			dateFormatter.applyPattern("MMMM");
			getPdfFieldsMap().get(PE191_SIGN_MONTH).setValue(dateFormatter.format(prorrogaParams.getFechaInicio()));
			dateFormatter.applyPattern("yy");
			getPdfFieldsMap().get(PE191_SIGN_YEAR).setValue(dateFormatter.format(prorrogaParams.getFechaInicio()));
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Map<String, String> getContractInfoMap(Contract contract) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractInfo.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_CONTRACT_ID), contract.getId());
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_START_DATE), contract.getStartDate());
			if(contract.getEndDate()!=null){
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_END_DATE), contract.getEndDate());
			} else {
				criteria.addNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_END_DATE));
			}
			for(ITransferObject to: bean.getList(criteria)){
				ContractInfo info = (ContractInfo) to;
				map.put(info.getName(), info.getExpression().replace('"', ' ').trim());
			}
		} catch (ManagerBeanException e) {
			// NADA, se devuelve un mapa vacio
			return map;
		}
		return map;
	}
	
	private String getContractExtensionNumber(Contract contract) throws ManagerBeanException {
		int count = getContractExtensionCount(contract);
		if(count==1){
			return "primera";
		} else if(count==2){
			return "segunda";
		} else if(count==3){
			return "tercera";
		} else if(count==4){
			return "cuarta";
		}
		return null;
	}
	
	private int getContractExtensionCount(Contract contract) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), contract.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.EXTENSION_DOC);
		return bean.getCount(criteria)+1;
	}

	private Integer getMonthsBetweenDates(Date startDate, Date endDate) {
		if(startDate!=null && endDate!=null){
			return (int) ((CommonUtil.getDaysBetweenDates(startDate, endDate, true))/30);
		}
		return null;
	}

}
	
	