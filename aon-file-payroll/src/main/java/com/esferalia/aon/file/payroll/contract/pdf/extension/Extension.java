package com.esferalia.aon.file.payroll.contract.pdf.extension;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.geozone.GeoZone;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.enumeration.DocumentType;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.file.payroll.contrata.ContrataProrrogaParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractInfo;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.util.PayrollUtils;
import com.lowagie.text.pdf.PdfReader;



public class Extension extends AbstractContractExtension {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
		
		try {
			PdfReader reader = new PdfReader(getContractExtensionUrl(documentName+".pdf"));
			readPdfFields(reader);

			SimpleDateFormat dateFormatter = new SimpleDateFormat();
			Map<String, String>  map = getContractInfoMap(contract);
			
			ContrataContratoParams contratoParams = null;
			try {
				if(params!=null && params.size()>0){
					contratoParams = (ContrataContratoParams) params.get(0);
				}
			} catch (Exception e) {
				String msg = "No se puede generar el documento de la prorroga, no hay datos del contrato origen.";
				throw new UnsupportedContractDocumentException(msg);
			}
			ContrataProrrogaParams prorrogaParams = null;
			try {
				if(params!=null && params.size()>1){
					prorrogaParams = (ContrataProrrogaParams) params.get(1);
				}
			} catch (Exception e) {
				String msg = "No se puede generar el documento de la prorroga, faltan los datos de la prorroga.";
				throw new UnsupportedContractDocumentException(msg);
			}
			
			super.loadPdfCommonFields(contract, params);
			
			
			/* 
			 * Contract enterprise fields
			 */
			setPdfFieldValue(ENTERPRISE_CIF,contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
			RegistryDirStaff rDirStaff = obtainRegistryDirStaff(contract.getWorkPlace().getEnterprise().getRegistry());
			try {
				setPdfFieldValue(ENTERPRISE_DIR_STAFF_NAME,rDirStaff.getName());
				setPdfFieldValue(ENTERPRISE_DIR_STAFF_NIF,rDirStaff.getDocument());
				String rDirStaddCharge = null;
				if ( rDirStaff.isShareHolder() ){
					rDirStaddCharge = "Socio";
				} else if ( rDirStaff.isRepresentative() ){
					rDirStaddCharge = "Apoderado";
				} else if( rDirStaff.isDirector() ){
					rDirStaddCharge = "Administrador";
				} else if ( rDirStaff.isRepresentativeLabor() ){
					rDirStaddCharge = "Representante laboral";
				}
				setPdfFieldValue(ENTERPRISE_DIR_STAFF_CHARGE,rDirStaddCharge);
			} catch (NullPointerException npe) {
				// do nothing
			}
			setPdfFieldValue(ENTERPRISE_NAME,contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
			setPdfFieldValue(ENTERPRISE_ADDRESS,contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getFullAddress());
			try {	
				setPdfFieldValue(ENTERPRISE_COUNTRY,contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getName(getLocale()));
				setPdfFieldValue(ENTERPRISE_COUNTRY_CODE1,String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(ENTERPRISE_COUNTRY_CODE2,String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(ENTERPRISE_COUNTRY_CODE3,String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			try {
				RegistryAddress address = contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress();
				if(StringUtils.isNotBlank(address.getMunicipalityCode())){
					ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
					setPdfFieldValue(ENTERPRISE_MUNICIPALITY,bundle.getString(address.getMunicipalityCode()));
					setPdfFieldValue(ENTERPRISE_MUNICIPALITY_CODE1,address.getMunicipalityCode().substring(0, 1));
					setPdfFieldValue(ENTERPRISE_MUNICIPALITY_CODE2,address.getMunicipalityCode().substring(1, 2));
					setPdfFieldValue(ENTERPRISE_MUNICIPALITY_CODE3,address.getMunicipalityCode().substring(2, 3));
					setPdfFieldValue(ENTERPRISE_MUNICIPALITY_CODE4,address.getMunicipalityCode().substring(3, 4));
					setPdfFieldValue(ENTERPRISE_MUNICIPALITY_CODE5,address.getMunicipalityCode().substring(4, 5));
				}
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			try {	
				setPdfFieldValue(ENTERPRISE_ZIP1,contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(0, 1));
				setPdfFieldValue(ENTERPRISE_ZIP2,contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(1, 2));
				setPdfFieldValue(ENTERPRISE_ZIP3,contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(2, 3));
				setPdfFieldValue(ENTERPRISE_ZIP4,contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(3, 4));
				setPdfFieldValue(ENTERPRISE_ZIP5,contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/* 
			 * Contract ccc fields
			 */
			if(contract.getEnterpriseCCC()!=null){
				String regimeCode = PayrollUtils.getInstance().getRegimeCode(contract.getEnterpriseCCC());
				if(regimeCode!=null){
					setPdfFieldValue(CCC_REG1,regimeCode.substring(0, 1));
					setPdfFieldValue(CCC_REG2,regimeCode.substring(1, 2));
					setPdfFieldValue(CCC_REG3,regimeCode.substring(2, 3));
					setPdfFieldValue(CCC_REG4,regimeCode.substring(3, 4));
				}
				if(contract.getEnterpriseCCC().getCcc().length()==11){
					setPdfFieldValue(CCC_PROV1,contract.getEnterpriseCCC().getCcc().substring(0, 1));
					setPdfFieldValue(CCC_PROV2,contract.getEnterpriseCCC().getCcc().substring(1, 2));
					setPdfFieldValue(CCC_NISS,contract.getEnterpriseCCC().getCcc().substring(2, 9));
					setPdfFieldValue(CCC_CONTROL_DIGIT1,contract.getEnterpriseCCC().getCcc().substring(9, 10));
					setPdfFieldValue(CCC_CONTROL_DIGIT2,contract.getEnterpriseCCC().getCcc().substring(10, 11));
				} else {
					setPdfFieldValue(CCC_NISS,contract.getEnterpriseCCC().getCcc());
				}
				setPdfFieldValue(CCC_ACTIVITY,contract.getEnterpriseCCC().getActivity().getDescription());
				setPdfFieldValue(CCC_ACTIVITY_CODE1,contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(0, 1));
				setPdfFieldValue(CCC_ACTIVITY_CODE2,contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(1, 2));
			}
			/* 
			 * Contract workplace fields
			 */
			try {
				GeoZone country = obtainCountry(contract.getWorkPlace().getAddress().getGeozone());
				setPdfFieldValue(WORKPLACE_COUNTRY,country.getName());
				setPdfFieldValue(WORKPLACE_COUNTRY_CODE1,String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(WORKPLACE_COUNTRY_CODE2,String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(WORKPLACE_COUNTRY_CODE3,String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
				
			try {
				RegistryAddress address = contract.getWorkPlace().getAddress();
				if(StringUtils.isNotBlank(address.getMunicipalityCode())){
					ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
					setPdfFieldValue(WORKPLACE_MUNICIPALITY,bundle.getString(address.getMunicipalityCode()));
					setPdfFieldValue(WORKPLACE_MUNICIPALITY_CODE1,address.getMunicipalityCode().substring(0, 1));
					setPdfFieldValue(WORKPLACE_MUNICIPALITY_CODE2,address.getMunicipalityCode().substring(1, 2));
					setPdfFieldValue(WORKPLACE_MUNICIPALITY_CODE3,address.getMunicipalityCode().substring(2, 3));
					setPdfFieldValue(WORKPLACE_MUNICIPALITY_CODE4,address.getMunicipalityCode().substring(3, 4));
					setPdfFieldValue(WORKPLACE_MUNICIPALITY_CODE5,address.getMunicipalityCode().substring(4, 5));
				}
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/*
			 * Contract employee fields
			 */
			setPdfFieldValue(EMPLOYEE_NAME,contract.getPerson().getFullName());
			setPdfFieldValue(EMPLOYEE_NIF,contract.getPerson().getRegistry().getDocument());
			if(contract.getPerson().getBirthDate()!=null){
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				setPdfFieldValue(EMPLOYEE_BIRTH_DATE,formatter.format(contract.getPerson().getBirthDate()));
			}
			setPdfFieldValue(EMPLOYEE_NSS,contract.getPerson().getSocialSecurityNumber());
			if(contratoParams!=null && contratoParams.getNivelFormativo()!=null){
				setPdfFieldValue(EMPLOYEE_FORMATION_LEVEL,contratoParams.getNivelFormativo().getDescription());
				setPdfFieldValue(EMPLOYEE_FORMATION_CODE1,contratoParams.getNivelFormativo().getCode().substring(0, 1));
				setPdfFieldValue(EMPLOYEE_FORMATION_CODE2,contratoParams.getNivelFormativo().getCode().substring(1, 2));
			}
			try {
				setPdfFieldValue(EMPLOYEE_COUNTRY,String.valueOf(contract.getPerson().getRegistry().getNationality().getName(getLocale())));
				setPdfFieldValue(EMPLOYEE_COUNTRY_CODE1,String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(EMPLOYEE_COUNTRY_CODE2,String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(EMPLOYEE_COUNTRY_CODE3,String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getPerson().getRegistry().getDefaultAddress();
				if(StringUtils.isNotBlank(address.getMunicipalityCode())){				
					ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
					setPdfFieldValue(EMPLOYEE_ADDRESS_MUNICIPALITY,bundle.getString(address.getMunicipalityCode()));
					setPdfFieldValue(EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1,address.getMunicipalityCode().substring(0, 1));
					setPdfFieldValue(EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2,address.getMunicipalityCode().substring(1, 2));
					setPdfFieldValue(EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3,address.getMunicipalityCode().substring(2, 3));
					setPdfFieldValue(EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4,address.getMunicipalityCode().substring(3, 4));
					setPdfFieldValue(EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5,address.getMunicipalityCode().substring(4, 5));
				}
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				GeoZone country = obtainCountry(contract.getPerson().getRegistry().getDefaultAddress().getGeozone());
				setPdfFieldValue(EMPLOYEE_ADDRESS_COUNTRY,country.getName());
				setPdfFieldValue(EMPLOYEE_ADDRESS_COUNTRY_CODE1,String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(EMPLOYEE_ADDRESS_COUNTRY_CODE2,String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(EMPLOYEE_ADDRESS_COUNTRY_CODE3,String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			////
			////
			////
			
			getPdfFieldsMap().get(PE191_PREVIOUS_CONTRACT_MODEL).setValue(code.getName(getLocale()));
			getPdfFieldsMap().get(PE191_RD).setValue("vigente a fecha inicio de contrato");
			
			getPdfFieldsMap().get(PE191_SEPE_NAME).setValue(contract.getWorkPlace().getAddress().getCity());
			getPdfFieldsMap().get(PE191_EXTENSION_NUMBER).setValue(getContractExtensionNumber(contract));

			dateFormatter.applyPattern("dd/MM/yyyy");
			getPdfFieldsMap().get(PE191_EXTENSION_START_DATE).setValue(dateFormatter.format(prorrogaParams.getFechaInicio()));
			getPdfFieldsMap().get(PE191_EXTENSION_END_DATE).setValue(dateFormatter.format(prorrogaParams.getFechaFin()));
			Integer extensionDurationInMonths = getMonthsBetweenDates(prorrogaParams.getFechaInicio(), prorrogaParams.getFechaFin());
			if(extensionDurationInMonths==null){
				extensionDurationInMonths = 0;
			}
			getPdfFieldsMap().get(PE191_EXTENSION_MONTH_COUNT).setValue(extensionDurationInMonths.toString());
			
			getPdfFieldsMap().get(PE191_CONTRACT_START_DATE).setValue(dateFormatter.format(contract.getStartDate()));
			Integer contractDurationInMonths = getMonthsBetweenDates(contratoParams.getStartDate(), contratoParams.getEndDate());
			if(contractDurationInMonths==null){
				contractDurationInMonths = 0;
			}
			getPdfFieldsMap().get(PE191_CONTRACT_MONTH_COUNT).setValue(contractDurationInMonths.toString());
			
			getPdfFieldsMap().get(PE191_SEPE_TOWN).setValue(contract.getWorkPlace().getAddress().getCity());
			getPdfFieldsMap().get(PE191_CONTRACT_REGULATION_DATE).setValue(dateFormatter.format(contract.getStartDate()));
			
			String sepeId = prorrogaParams.getClaveContrato();
			if(StringUtils.isBlank(sepeId)){
				sepeId = map.get(ContractVariable.SEPE_CONTRACT_ID.getValue());
			}
			if(StringUtils.isBlank(sepeId)){
				Person person = contract.getPerson();
				if(StringUtils.isNotEmpty(person.getRegistry().getDocument())){
					if(person.getRegistry().getDocumentType()==DocumentType.NIF){
						sepeId = "D"+person.getRegistry().getDocument();
					} else if(person.getRegistry().getDocumentType()==DocumentType.NIE){
						sepeId = "E"+person.getRegistry().getDocument();
					}
				}
				sepeId += "-"+dateFormatter.format(contract.getStartDate());
			}
			getPdfFieldsMap().get(PE191_CONTRACT_SEPE_ID).setValue(sepeId);
			
			Integer totalDurationInMonths = contractDurationInMonths + extensionDurationInMonths;
			getPdfFieldsMap().get(PE191_TOTAL_DURATION1).setValue(totalDurationInMonths.toString());
			getPdfFieldsMap().get(PE191_TOTAL_DURATION2).setValue("meses");
			
			getPdfFieldsMap().get(PE191_SIGN_TOWN).setValue(contract.getWorkPlace().getAddress().getCity());
			dateFormatter.applyPattern("dd");
			getPdfFieldsMap().get(PE191_SIGN_DAY).setValue(dateFormatter.format(prorrogaParams.getFechaInicio()));
			dateFormatter.applyPattern("MMMM");
			getPdfFieldsMap().get(PE191_SIGN_MONTH).setValue(dateFormatter.format(prorrogaParams.getFechaInicio()));
			dateFormatter.applyPattern("yy");
			getPdfFieldsMap().get(PE191_SIGN_YEAR).setValue(dateFormatter.format(prorrogaParams.getFechaInicio()));
			
			
			
		} catch (IOException e) {
			// do nothing
		} catch (ManagerBeanException e) {
			// do nothing
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
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.SEPE_EXTENSION_FILE);
		return bean.getCount(criteria);
	}

	private Integer getMonthsBetweenDates(Date startDate, Date endDate) {
		if(startDate!=null && endDate!=null){
			return (int) ((CommonUtil.getDaysBetweenDates(startDate, endDate, true))/30);
		}
		return null;
	}

	protected RegistryDirStaff obtainRegistryDirStaff(Registry registry) throws ManagerBeanException {
		return getHandler().obtainRegistryDirStaff(registry);
	}
}
	
	