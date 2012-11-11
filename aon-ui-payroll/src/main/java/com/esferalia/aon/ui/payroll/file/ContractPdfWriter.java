package com.esferalia.aon.ui.payroll.file;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.Classpath;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.ui.registry.controller.RegistryDirStaffLinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.model.CONTRATO100TYPE;
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
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;


public class ContractPdfWriter {
	
	private static ContractPdfWriter instance;
//	private List<ContractPdfField> contractFields;
//	private Integer contractPage;
//	private Integer contractWidth;
//	private Integer contractHeight;
//	private int numberOfContractPages;
	private URL contractModelUrl;
//	private int zoomFactor;
//	private static final double FACTOR_1X = 1.2;
//	private static final double FACTOR_2X = 1.4;
//	private static final double FACTOR_3X = 1.6;
//	private static final double FACTOR_4X = 1.8;
	
	private ContractPdfWriter(){
	}
	
	public static ContractPdfWriter getInstance(){
		if (instance == null) {
			instance = new ContractPdfWriter();
		}
		return instance;
	}
	
//	public Integer getContractWidth() {
//		return getFactorizedValue(contractWidth);
//	}
//	
//	public void setContractWidth(Integer contractWidth) {
//		this.contractWidth = contractWidth;
//	}
//	
//	public Integer getContractHeight() {
//		return getFactorizedValue(contractHeight);
//	}
//	
//	public void setContractHeight(Integer contractHeight) {
//		this.contractHeight = contractHeight;
//	}
	
//	public int getFactorizedValue(double value){
//		if(getZoomFactor()==0){
//			return (int)value;
//		} else if(getZoomFactor()==1){
//			return (int)(value*FACTOR_1X);
//		} else if(getZoomFactor()==2){
//			return (int)(value*FACTOR_2X);
//		} else if(getZoomFactor()==3){
//			return (int)(value*FACTOR_3X);
//		} else if(getZoomFactor()==4){
//			return (int)(value*FACTOR_4X);
//		}
//		return (int)value;
//	}
//	
//	public int getZoomFactor() {
//		return zoomFactor;
//	}
//	
//	public void setZoomFactor(int zoomFactor) {
//		this.zoomFactor = zoomFactor;
//	}
	
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
//		if(contractFields==null){
//			contractFields = new LinkedList<ContractPdfField>();
//		}
//		return contractFields;
		return pdfModel.getPdfFields();
	}
//	public void setContractPdfFields(List<ContractPdfField> contractFields) {
//		this.contractFields = contractFields;
//	}
//	public Integer getContractPage() {
//		return contractPage;
//	}
//	public void setContractPage(Integer contractPage) {
//		this.contractPage = contractPage;
//	}
//	public int getNumberOfContractPages() {
//		return numberOfContractPages;
//	}
//	public void setNumberOfContractPages(int numberOfContractPages) {
//		this.numberOfContractPages = numberOfContractPages;
//	}
	
//	public void readPdfFields(byte[] pdf, ContractModel model) throws IOException{
//		if(pdf==null){
//			if(model==null){
//				throw new AbortProcessingException("El modelo no se ha cargado correctamente o no existe");
//			}
//			readPdfFields(new PdfReader(getContractModelUrl(model+".pdf")));
//		} else {
//			readPdfFields(new PdfReader(pdf)); 
//		}
//	}
//	
//	public void readPdfFields(ContractModel model) throws IOException{
//		if(model==null){
//			String msg = "El modelo no se ha cargado correctamente o no existe";
//			AonUtil.addErrorMessage(msg);
//			throw new AbortProcessingException(msg);
//		}
//		readPdfFields(new PdfReader(getContractModelUrl(model+".pdf")));
//	}
//	
//	private void readPdfFields(PdfReader reader) throws IOException{
//		
//		setContractWidth((int)reader.getPageSize(1).getWidth());
//		setContractHeight((int)reader.getPageSize(1).getHeight());
//		
//		numberOfContractPages = reader.getNumberOfPages();
//		AcroFields form = reader.getAcroFields();
//		HashMap<?,?> fields = form.getFields();
//		String key;
//		ContractPdfField field;
//		for (Iterator<?> it = fields.keySet().iterator(); it.hasNext();) {
//			key = (String) it.next();
//			field = new ContractPdfField();
//			if(form.getFieldType(key)==AcroFields.FIELD_TYPE_CHECKBOX){
//					field.setType(AcroFields.FIELD_TYPE_CHECKBOX);
//					field.setValue(form.getField(key).equals(form.getAppearanceStates(key)[0])?"true":"false");
//			} else if(form.getFieldType(key)==AcroFields.FIELD_TYPE_TEXT){
//					field.setType(AcroFields.FIELD_TYPE_TEXT);
//					field.setValue(form.getField(key));
//			} else {
//				field.setType(AcroFields.FIELD_TYPE_NONE);;
//			}
//			Float f = form.getFieldPositions(key)[0];
//			field.setPage(f.intValue());
//			field.setLabel(key);
//			field.setBottomCoordinates(getBottomCoordinates(form, key));
//			field.setLeftCoordinates(getLeftCoordinates(form, key));
//			field.setWidth(getInputTextWidth(form, key));
//			field.setHeight(getInputTextHeight(form, key));
//			if(field.getType()!=null){
//				getContractPdfFields().add(field);
//			}
//		}
//		reader.close();
//	}
	
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
	
	private IContractPdfModel pdfModel;
	
	public byte[] buildPdf() {
		return pdfModel.buildPdf();
	}
	
	public void loadPdf(ContractModel model, Contract contract) throws IOException, UnsupportedContractModelException {
//		PdfReader reader = new PdfReader(getContractModelUrl(model+".pdf"));
//		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
//		PdfStamper stamp = new PdfStamper(reader, baos);
//	    AcroFields form = stamp.getAcroFields();
//	    String checkValue = null;
//	    for(ContractPdfField field: getContractPdfFields()){
//			if(field.getType()==AcroFields.FIELD_TYPE_CHECKBOX){
//				if(checkValue==null){
//					checkValue = form.getAppearanceStates(field.getLabel())[0];
//				}
//				form.setField(field.getLabel(), field.getValue().equals("true")?checkValue:"");
//			} else {
//				form.setField(field.getLabel(), field.getValue());
//			}
//		}
////	    stamp.setFormFlattening(true);
//	    stamp.setFormFlattening(false);
//	    stamp.close();
//	    reader.close();
//	    return baos.toByteArray();
		
		
		ContractPdfFactory factory = new ContractPdfFactory();
		pdfModel = factory.createContractModel(ModelPE170.MODEL_NAME);
//		try {
//			CONTRATO100TYPE type = new CONTRATO100TYPE();
			
//		ContractCode code = ContractCode.C100;
		String tc2 = getContractDataMap(contract).get(ContextVariable.TC2.getName());
			
		pdfModel.loadPdfFields(ContractCode.getContractCodeByValue(tc2), contract);
//		} catch (UnsupportedContractModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
	}

	public void loadPdf(ContractAttachment contractPdfDraft) {
		ContractPdfFactory factory = new ContractPdfFactory();
		pdfModel = factory.createContractModel(ModelPE170.MODEL_NAME);
		pdfModel.loadPdfFields(contractPdfDraft);
		
		
//		try {
//			PdfReader reader = new PdfReader(contractPdfDraft.getData());
////			setContractWidth((int)reader.getPageSize(1).getWidth());
////			setContractHeight((int)reader.getPageSize(1).getHeight());
////			numberOfContractPages = reader.getNumberOfPages();
//			AcroFields form = reader.getAcroFields();
//			HashMap<?,?> fields = form.getFields();
//			String key;
//			ContractPdfField field;
//			for (Iterator<?> it = fields.keySet().iterator(); it.hasNext();) {
//				key = (String) it.next();
//				field = new ContractPdfField();
//				if(form.getFieldType(key)==AcroFields.FIELD_TYPE_CHECKBOX){
//					field.setType(AcroFields.FIELD_TYPE_CHECKBOX);
//					field.setValue(form.getField(key).equals(form.getAppearanceStates(key)[0])?"true":"false");
//				} else if(form.getFieldType(key)==AcroFields.FIELD_TYPE_TEXT){
//					field.setType(AcroFields.FIELD_TYPE_TEXT);
//					field.setValue(form.getField(key));
//				} else {
//					field.setType(AcroFields.FIELD_TYPE_NONE);;
//				}
//				Float f = form.getFieldPositions(key)[0];
//				field.setPage(f.intValue());
//				field.setLabel(key);
//				field.setBottomCoordinates(getBottomCoordinates(form, key));
//				field.setLeftCoordinates(getLeftCoordinates(form, key));
//				field.setWidth(getInputTextWidth(form, key));
//				field.setHeight(getInputTextHeight(form, key));
//				if(field.getType()!=null){
//					getContractPdfFields().add(field);
//				}
//			}
//			reader.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public double getContractWidth() {
		return pdfModel.getContractWidth();
	}
	public double getContractHeight() {
		return pdfModel.getContractHeight();
	}
	
	private Map<String, String> contractDataMap;
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

	
//	/*
//	 * [page, llx, lly, urx, ury]
//	 */
	private String getBottomCoordinates(AcroFields form, String key){
		return Float.toString(form.getFieldPositions(key)[2]);
	}
	private String getLeftCoordinates(AcroFields form, String key){
		return Float.toString(form.getFieldPositions(key)[1]);
	}
	private String getInputTextWidth(AcroFields form, String key){
		Float f = form.getFieldPositions(key)[3]-form.getFieldPositions(key)[1];
		return String.valueOf(f.intValue());
	}
	private String getInputTextHeight(AcroFields form, String key){
		return Float.toString(form.getFieldPositions(key)[4]-form.getFieldPositions(key)[2]);
	}
	
	
	
}
