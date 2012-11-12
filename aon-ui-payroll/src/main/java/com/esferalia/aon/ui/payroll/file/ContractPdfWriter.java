package com.esferalia.aon.ui.payroll.file;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.Classpath;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.ContractPdfFactory;
import com.esferalia.aon.file.payroll.contract.pdf.model.ContractPdfField;
import com.esferalia.aon.file.payroll.contract.pdf.model.IContractPdfModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE170;
import com.esferalia.aon.file.payroll.contract.pdf.model.UnsupportedContractModelException;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;


public class ContractPdfWriter {
	
	private static ContractPdfWriter instance;
	private URL contractModelUrl;
	private IContractPdfModel pdfModel;
	private Map<String, String> contractDataMap;
	
	private ContractPdfWriter(){
	}
	
	public static ContractPdfWriter getInstance(){
		if (instance == null) {
			instance = new ContractPdfWriter();
		}
		return instance;
	}
	
	public URL getContractModelUrl() {
		return contractModelUrl;
	}
	public void setContractModelUrl(URL contractModelUrl) {
		this.contractModelUrl = contractModelUrl;
	}
	public URL getContractModelUrl(String file) throws IOException {
		if(getContractModelUrl()==null){
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URL[] urls = Classpath.search(cl, IPayrollConstants.MODEL_PATH, file);
			contractModelUrl = urls[0];
		}
		return contractModelUrl;
	}
	public Collection<ContractPdfField> getContractPdfFields() {
		return pdfModel.getPdfFields();
	}
	public double getContractWidth() {
		return pdfModel.getContractWidth();
	}
	public double getContractHeight() {
		return pdfModel.getContractHeight();
	}
	public int getNumberOfContractPages() {
		return pdfModel.getNumberOfContractPages();
	}
	
	
	public byte[] buildPdf() {
		return pdfModel.buildPdf();
	}
	
	public void loadPdf(ContractModel model, Contract contract) throws IOException, UnsupportedContractModelException {
		ContractPdfFactory factory = new ContractPdfFactory();
		pdfModel = factory.createContractModel(model.toString());
//		pdfModel = factory.createContractModel(ModelPE170.MODEL_NAME);
//		CONTRATO100TYPE type = new CONTRATO100TYPE();
			
//		ContractCode code = ContractCode.C100;
		String tc2 = getContractDataMap(contract).get(ContextVariable.TC2.getName());
			
		pdfModel.loadPdfFields(ContractCode.getContractCodeByValue(tc2), contract);
	}

	public void loadPdf(ContractAttachment contractPdfDraft) {
		ContractPdfFactory factory = new ContractPdfFactory();
		pdfModel = factory.createContractModel(ModelPE170.MODEL_NAME);
		pdfModel.loadPdfFields(contractPdfDraft);
	}
	
	private Map<String, String> getContractDataMap(Contract contract) {
//		if(contractDataMap==null){
			contractDataMap = new HashMap<String, String>();
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
				criteria.addNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
				for(ITransferObject to: bean.getList(criteria)){
					ContractData data = (ContractData) to;
					contractDataMap.put(data.getName(), data.getExpression().replace('"', ' ').trim());
				}
			} catch (ManagerBeanException e) {
				// NADA, se devuelve un mapa vacio
				return contractDataMap;
			}
//		}
		return contractDataMap;
	}
	
	
//	public void loadDefaultFields(Contract contract) throws ManagerBeanException {
//		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
//		RegistryDirStaffLinesController rDirStaff = (RegistryDirStaffLinesController)AonUtil.getRegisteredBean(IPayrollConstants.ENTERPRISE_DIR_STAFF_CONTROLLER);
//		rDirStaff.getCriteria().addGreaterThanOrEqualExpression(rDirStaff.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE), contract.getStartDate());
//		rDirStaff.onSearch(null);
//		RegistryDirStaff dir = null; 
//		if(rDirStaff.getModel().getRowCount()<=0){
//			String msg = "La empresa no tiene el representante definido";
//			AonUtil.addErrorMessage(msg);
////			throw new AbortProcessingException(msg);
//		} else {
//			rDirStaff.onSelectFirst(null);
//			dir = (RegistryDirStaff)rDirStaff.getTo();
//			rDirStaff.clearCriteria();
//		}
//		if(contract.getPerson().getRegistry().getDefaultAddress().getGeozone()==null){
//			String msg = "La persona no tiene el geozone definido";
//			AonUtil.addErrorMessage(msg);
////			throw new AbortProcessingException(msg);
//		}
//		if(StringUtils.isEmpty(contract.getWorkPlace().getAddress().getZip())
//				|| contract.getWorkPlace().getAddress().getZip().length()!=5){
//			String msg = "El c.p. del centro de trabajo no esta definido";
//			AonUtil.addErrorMessage(msg);
//			contract.getWorkPlace().getAddress().setZip("00000");
////			throw new AbortProcessingException(msg);
//		} 
//		if(contract.getPerson().getBirthDate()==null){
//			String msg = "La persona no tiene la fecha de nacimiento definida";
//			AonUtil.addErrorMessage(msg);
//			contract.getPerson().setBirthDate(new Date());
////			throw new AbortProcessingException(msg);
//		} 
//		if(contract.getPerson().getRegistry().getDefaultAddress()==null){
//			String msg = "La persona no tiene ninguna direccion definida";
//			AonUtil.addErrorMessage(msg);
//		} 
//		for(ContractPdfField field: getContractPdfFields()){
////			if(field.getPage().equals(1)){
////				System.out.println(field.getLabel());
////			}
//			try{
//			if(field.getLabel().equals("Texto1")){
//				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			// nombre representante
//			if(field.getLabel().equals("Texto2")){
//				field.setValue(dir.getName());
//			}
//			// nif representante
//			if(field.getLabel().equals("Texto3")){
//				field.setValue(dir.getDocument());
//			}
//			// en concepto de que
//			if(field.getLabel().equals("Texto4")){
//				if(dir.isShareHolder()){
//					field.setValue("Socio");
//				}
//				if(dir.isDirector()){
//					field.setValue("Apoderado");
//				}
//				if(dir.isRepresentative()){
//					field.setValue("Representante");
//				}
//			}
//			// nombre empresa
//			if(field.getLabel().equals("Texto5")){
//				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
//			}
//			//direccion empresa
//			if(field.getLabel().equals("Texto6")){
//				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getFullAddress());
//			}
//			// pais empresa
//			if(field.getLabel().equals("Texto7")){
//				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getName(locale));
//			}
//			if(field.getLabel().equals("Cifra1")){
//				field.setValue(String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(0,0));
//			}
//			if(field.getLabel().equals("Cifra2")){
//				field.setValue(String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(1,1));
//			}
//			if(field.getLabel().equals("Cifra3")){
//				field.setValue(String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(2,2));
//			}
//			// municipio empresa
//			if(field.getLabel().equals("Texto8")){
//				field.setValue(contract.getWorkPlace().getAddress().getCity());
//			}
//			if(field.getLabel().equals("Cifra4")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			if(field.getLabel().equals("Cifra5")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			if(field.getLabel().equals("Cifra6")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			if(field.getLabel().equals("Cifra7")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			if(field.getLabel().equals("Cifra8")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			// CP
//			if(field.getLabel().equals("Cifra9")){
//				field.setValue(contract.getWorkPlace().getAddress().getZip().substring(0,0));
//			}
//			if(field.getLabel().equals("Cifra10")){
//				field.setValue(contract.getWorkPlace().getAddress().getZip().substring(1,1));
//			}
//			if(field.getLabel().equals("Cifra11")){
//				field.setValue(contract.getWorkPlace().getAddress().getZip().substring(2,2));
//			}
//			if(field.getLabel().equals("Cifra12")){
//				field.setValue(contract.getWorkPlace().getAddress().getZip().substring(3,3));
//			}
//			if(field.getLabel().equals("Cifra13")){
//				field.setValue(contract.getWorkPlace().getAddress().getZip().substring(4,4));
//			}
//			// numero ccc
//			if(field.getLabel().equals("Cifra14")){
////				field.setValue(contract.getCcc().getCCC());
//			}
//			if(field.getLabel().equals("Cifra15")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			} 
//			if(field.getLabel().equals("Cifra16")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			} 
//			if(field.getLabel().equals("Cifra17")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			} 
//			if(field.getLabel().equals("Cifra18")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			} 
//			if(field.getLabel().equals("Cifra19")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			} 
//			if(field.getLabel().equals("Texto9")){
//				field.setValue(contract.getEnterpriseCCC().getCcc());
//			} 
//			if(field.getLabel().equals("Cifra20")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			if(field.getLabel().equals("Cifra21")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			// actividad economica
//			if(field.getLabel().equals("Texto10")){
//				field.setValue(contract.getEnterpriseCCC().getActivity().getDescription());
//			}
//			if(field.getLabel().equals("Cifra22")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			if(field.getLabel().equals("Cifra23")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			// pais centro trabajo
//			if(field.getLabel().equals("Texto11")){
//				field.setValue(contract.getWorkPlace().getAddress().getRegistry().getNationality().getName(locale));
//			}
//			if(field.getLabel().equals("Cifra24")){
//				field.setValue(String.valueOf(contract.getWorkPlace().getAddress().getRegistry().getNationality().getIsoNum()).substring(0,0));
//			}
//			if(field.getLabel().equals("Cifra25")){
//				field.setValue(String.valueOf(contract.getWorkPlace().getAddress().getRegistry().getNationality().getIsoNum()).substring(1,1));
//			}
//			if(field.getLabel().equals("Cifra26")){
//				field.setValue(String.valueOf(contract.getWorkPlace().getAddress().getRegistry().getNationality().getIsoNum()).substring(2,2));
//			}
//			// municipio centro trabajo
//			if(field.getLabel().equals("Texto12")){
//				field.setValue(contract.getWorkPlace().getAddress().getCity());
//			}
//			if(field.getLabel().equals("Cifra27")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			if(field.getLabel().equals("Cifra28")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			if(field.getLabel().equals("Cifra29")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			if(field.getLabel().equals("Cifra30")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			if(field.getLabel().equals("Cifra31")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			// nombre empleado
//			if(field.getLabel().equals("Texto13")){
//				field.setValue(contract.getPerson().getRegistry().getFullName());
//			}
//			// cif empleado
//			if(field.getLabel().equals("Texto14")){
//				field.setValue(contract.getPerson().getRegistry().getDocument());
//			}
//			// fecha nacimiento empleado
//			if(field.getLabel().equals("Texto15")){
//				field.setValue(contract.getPerson().getBirthDate().toString());
//				
//			}
//			//numero ss empleado
//			if(field.getLabel().equals("Texto16")){
//				field.setValue(contract.getPerson().getSocialSecurityNumber());
//			}
//			// nivel formativo empleado
//			if(field.getLabel().equals("Texto17")){
////				field.setValue(contract.getPerson());
//			}
//			if(field.getLabel().equals("Cifra32")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			if(field.getLabel().equals("Cifra33")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			// nacionalidad empleado
//			if(field.getLabel().equals("Texto18")){
//				field.setValue(contract.getPerson().getRegistry().getNationality().getName(locale));
//			}
//			if(field.getLabel().equals("Cifra34")){
//				field.setValue(String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(0,0));
//			}
//			if(field.getLabel().equals("Cifra35")){
//				field.setValue(String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(1,1));
//			}
//			if(field.getLabel().equals("Cifra36")){
//				field.setValue(String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(2,2));
//			}
//			//  municipio domicilio empleado
//			if(field.getLabel().equals("Texto19")){
//				if(contract.getPerson().getRegistry().getDefaultAddress()!=null){
//					field.setValue(contract.getPerson().getRegistry().getDefaultAddress().getCity());
//				}
//			}
//			if(field.getLabel().equals("Cifra37")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			if(field.getLabel().equals("Cifra38")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			if(field.getLabel().equals("Cifra39")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			if(field.getLabel().equals("Cifra40")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			if(field.getLabel().equals("Cifra41")){
////				field.setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//			}
//			// pais domicilio empleado
//			if(field.getLabel().equals("Texto20")){
//				field.setValue(contract.getPerson().getRegistry().getDefaultAddress().getRegistry().getNationality().getName(locale));
//			}
//			if(field.getLabel().equals("Cifra42")){
//				field.setValue(String.valueOf(contract.getPerson().getRegistry().getDefaultAddress().getRegistry().getNationality().getIsoNum()).substring(0,0));
//			}
//			if(field.getLabel().equals("Cifra43")){
//				field.setValue(String.valueOf(contract.getPerson().getRegistry().getDefaultAddress().getRegistry().getNationality().getIsoNum()).substring(1,1));
//			}
//			if(field.getLabel().equals("Cifra44")){
//				field.setValue(String.valueOf(contract.getPerson().getRegistry().getDefaultAddress().getRegistry().getNationality().getIsoNum()).substring(2,2));
//			}
//		} catch(NullPointerException e){
////			AonUtil.addErrorMessage("Existen campos nulos");
//			// Solo se avisa de la existencia de campos nulos
//		}
//		}
//	}
	
}
