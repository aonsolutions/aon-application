package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.Classpath;
import com.esferalia.aon.file.payroll.contract.model.DATOSEMPRESATYPE;
import com.esferalia.aon.file.payroll.contract.model.DATOSGENERALESCONTRATOTYPE;
import com.esferalia.aon.file.payroll.contract.model.DATOSTRABAJADORTYPE;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;



public abstract class AbstractContractModel implements IContractPdfModel {
	
	/* 
	 * Contract enterprise fields
	 */
	private final String ENTERPRISE_CIF = "cif";
	private final String ENTERPRISE_DIR_STAFF_NAME = "nomrepr";
	private final String ENTERPRISE_DIR_STAFF_NIF = "dnirep";
	private final String ENTERPRISE_DIR_STAFF_CHARGE = "cargorep";
	private final String ENTERPRISE_NAME = "razsoc";
	private final String ENTERPRISE_ADDRESS = "domsocialem";
	private final String ENTERPRISE_COUNTRY = "Texto1pais";
	private final String ENTERPRISE_COUNTRY_CODE1 = "codpaisem1";
	private final String ENTERPRISE_COUNTRY_CODE2 = "codpaisem2";
	private final String ENTERPRISE_COUNTRY_CODE3 = "codpaisem3";
	private final String ENTERPRISE_TOWN = "Texto3mun";
	private final String ENTERPRISE_TOWN_CODE1 = "codmuniem1";
	private final String ENTERPRISE_TOWN_CODE2 = "codmuniem2";
	private final String ENTERPRISE_TOWN_CODE3 = "codmuniem3";
	private final String ENTERPRISE_TOWN_CODE4 = "codmuniem4";
	private final String ENTERPRISE_TOWN_CODE5 = "codmuniem5";
	private final String ENTERPRISE_ZIP1 = "codpostem1";
	private final String ENTERPRISE_ZIP2 = "codpostem2";
	private final String ENTERPRISE_ZIP3 = "codpostem3";
	private final String ENTERPRISE_ZIP4 = "codpostem4";
	private final String ENTERPRISE_ZIP5 = "codpostem5";
	
	/* 
	 * Contract ccc fields
	 */
	private final String CCC_REG1 = "regimen1";
	private final String CCC_REG2 = "regimen2";
	private final String CCC_REG3 = "regimen3";
	private final String CCC_REG4 = "regimen4";
	private final String CCC_PROV1 = "provniss1";
	private final String CCC_PROV2 = "provniss2";
	private final String CCC_NISS = "numniss";
	private final String CCC_CONTROL_DIGIT1 = "digcon1";
	private final String CCC_CONTROL_DIGIT2 = "digcon2";
	private final String CCC_ACTIVITY = "litacteco";
	private final String CCC_ACTIVITY_CODE1 = "codacteco1";
	private final String CCC_ACTIVITY_CODE2 = "codacteco2";
	
	/* 
	 * Contract workplace fields
	 */
	private final String WORKPLACE_COUNTRY = "Texto4pais";
	private final String WORKPLACE_COUNTRY_CODE1 = "codpaisct1";
	private final String WORKPLACE_COUNTRY_CODE2 = "codpaisct2";
	private final String WORKPLACE_COUNTRY_CODE3 = "codpaisct3";
	private final String WORKPLACE_TOWN = "Texto5municipio";
	private final String WORKPLACE_TOWN_CODE1 = "codmunict1";
	private final String WORKPLACE_TOWN_CODE2 = "codmunict2";
	private final String WORKPLACE_TOWN_CODE3 = "codmunict3";
	private final String WORKPLACE_TOWN_CODE4 = "codmunict4";
	private final String WORKPLACE_TOWN_CODE5 = "codmunict5";
	
	/*
	 * Contract employee fields
	 */
	private final String EMPLOYEE_NAME = "nomtrab";
	private final String EMPLOYEE_NIF = "dnitra";
	private final String EMPLOYEE_BIRTH_DATE = "fechanac";
	private final String EMPLOYEE_NSS = "numafinss";
	private final String EMPLOYEE_FORMATION_CODE1 = "codnivaca1";
	private final String EMPLOYEE_FORMATION_CODE2 = "codnivaca2";
	private final String EMPLOYEE_COUNTRY_CODE1 = "codnactra1";
	private final String EMPLOYEE_COUNTRY_CODE2 = "codnactra2";
	private final String EMPLOYEE_COUNTRY_CODE3 = "codnactra3";
	private final String EMPLOYEE_ADDRESS_TOWN = "Texto6mun";
	private final String EMPLOYEE_ADDRESS_TOWN_CODE1 = "codmunitra1";
	private final String EMPLOYEE_ADDRESS_TOWN_CODE2 = "codmunitra2";
	private final String EMPLOYEE_ADDRESS_TOWN_CODE3 = "codmunitra3";
	private final String EMPLOYEE_ADDRESS_TOWN_CODE4 = "codmunitra4";
	private final String EMPLOYEE_ADDRESS_TOWN_CODE5 = "codmunitra5";
	private final String EMPLOYEE_ADDRESS_COUNTRY = "Texto7padom";
	private final String EMPLOYEE_ADDRESS_COUNTRY_CODE1 = "codpaisdomtr1";
	private final String EMPLOYEE_ADDRESS_COUNTRY_CODE2 = "codpaisdomtr2";
	private final String EMPLOYEE_ADDRESS_COUNTRY_CODE3 = "codpaisdomtr3";
	
	protected String modelName;
	
//	private List<ContractPdfField> pdfFields;
	
	public Collection<ContractPdfField> getPdfFields() {
		return getPdfFieldsMap().values();
	}

//	public void setPdfFields(List<ContractPdfField> pdfFields) {
//		this.pdfFields = pdfFields;
//	}

	private Map<String, ContractPdfField> pdfFieldsMap;
	
	public Map<String, ContractPdfField> getPdfFieldsMap() {
		if(pdfFieldsMap==null){
			pdfFieldsMap = new HashMap<String, ContractPdfField>();
		}
		return pdfFieldsMap;
	}
	
	public void setPdfFieldsMap(Map<String, ContractPdfField> pdfFieldsMap) {
		this.pdfFieldsMap = pdfFieldsMap;
	}
	
	private final String MODELS_PATH = "com/esferalia/aon/file/payroll/contract/pdf/";
	
	
	
	private Double contractWidth;
	private Double contractHeight;
	
	@Override
	public Double getContractWidth() {
		return contractWidth;
	}

	public void setContractWidth(Double contractWidth) {
		this.contractWidth = contractWidth;
	}

	@Override
	public Double getContractHeight() {
		return contractHeight;
	}

	public void setContractHeight(Double contractHeight) {
		this.contractHeight = contractHeight;
	}

//	public abstract byte[] buildPdf();
	public byte[] buildPdf() {
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(modelName+".pdf"));
			setContractWidth((double)reader.getPageSize(1).getWidth());
			setContractHeight((double)reader.getPageSize(1).getHeight());
			ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
			PdfStamper stamp = new PdfStamper(reader, baos);
			AcroFields form = stamp.getAcroFields();
			
			
			String checkValue = null;
			for (Iterator<?> it = getPdfFields().iterator(); it.hasNext();) {
//				key = (String) it.next();
//				ContractPdfField field = getPdfFieldsMap().get(key);
				ContractPdfField field = (ContractPdfField) it.next();
				if(field.getType()==AcroFields.FIELD_TYPE_CHECKBOX){
					if(checkValue==null){
						checkValue = form.getAppearanceStates(field.getLabel())[0];
					}
					form.setField(field.getLabel(), field.getValue().equals("true")?checkValue:"");
				} else {
					form.setField(field.getLabel(), field.getValue());
				}
			}
//    		stamp.setFormFlattening(true);
			stamp.setFormFlattening(false);
			stamp.close();
			reader.close();
			return baos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public abstract void loadPdfFields(ContractCode code, Contract contract) throws UnsupportedContractModelException;

	public void loadPdfFields(ContractAttachment contractPdfDraft) {
		try {
			PdfReader reader = new PdfReader(contractPdfDraft.getData());
	//		setContractWidth((int)reader.getPageSize(1).getWidth());
	//		setContractHeight((int)reader.getPageSize(1).getHeight());
	//		numberOfContractPages = reader.getNumberOfPages();
			setContractWidth((double)reader.getPageSize(1).getWidth());
			setContractHeight((double)reader.getPageSize(1).getHeight());
			
			AcroFields form = reader.getAcroFields();
			HashMap<?,?> fields = form.getFields();
			String key;
			ContractPdfField field;
			for (Iterator<?> it = fields.keySet().iterator(); it.hasNext();) {
				key = (String) it.next();
				field = new ContractPdfField();
				if(form.getFieldType(key)==AcroFields.FIELD_TYPE_CHECKBOX){
					field.setType(AcroFields.FIELD_TYPE_CHECKBOX);
					field.setValue(form.getField(key).equals(form.getAppearanceStates(key)[0])?"true":"false");
				} else if(form.getFieldType(key)==AcroFields.FIELD_TYPE_TEXT){
					field.setType(AcroFields.FIELD_TYPE_TEXT);
					field.setValue(form.getField(key));
				} else {
					field.setType(AcroFields.FIELD_TYPE_NONE);;
				}
				Float f = form.getFieldPositions(key)[0];
				field.setPage(f.intValue());
				field.setLabel(key);
				field.setBottomCoordinates(getBottomCoordinates(form, key));
				field.setLeftCoordinates(getLeftCoordinates(form, key));
				field.setWidth(getInputTextWidth(form, key));
				field.setHeight(getInputTextHeight(form, key));
				field.setZoomFactor(2);
				if(field.getType()!=null){
//					getContractPdfFields().add(field);
					getPdfFieldsMap().put(key, field);
				}
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void loadPdfCommonFields(Contract contract) throws UnsupportedContractModelException, ManagerBeanException{
		/* 
		 * Contract enterprise fields
		 */
		getPdfFieldsMap().get(ENTERPRISE_CIF).setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
		getPdfFieldsMap().get(ENTERPRISE_DIR_STAFF_NAME).setValue(null);
		getPdfFieldsMap().get(ENTERPRISE_DIR_STAFF_NIF).setValue(null);
		getPdfFieldsMap().get(ENTERPRISE_DIR_STAFF_CHARGE).setValue(null);
		getPdfFieldsMap().get(ENTERPRISE_NAME).setValue(contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
		getPdfFieldsMap().get(ENTERPRISE_ADDRESS).setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getFullAddress());
		getPdfFieldsMap().get(ENTERPRISE_COUNTRY).setValue(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getName(Locale.getDefault()));
		getPdfFieldsMap().get(ENTERPRISE_COUNTRY_CODE1).setValue(String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(0,1));
		getPdfFieldsMap().get(ENTERPRISE_COUNTRY_CODE2).setValue(String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(1,2));
		getPdfFieldsMap().get(ENTERPRISE_COUNTRY_CODE3).setValue(String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(2,3));
		getPdfFieldsMap().get(ENTERPRISE_TOWN).setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getCity());
		getPdfFieldsMap().get(ENTERPRISE_TOWN_CODE1).setValue(null);
		getPdfFieldsMap().get(ENTERPRISE_TOWN_CODE2).setValue(null);
		getPdfFieldsMap().get(ENTERPRISE_TOWN_CODE3).setValue(null);
		getPdfFieldsMap().get(ENTERPRISE_TOWN_CODE4).setValue(null);
		getPdfFieldsMap().get(ENTERPRISE_TOWN_CODE5).setValue(null);
		getPdfFieldsMap().get(ENTERPRISE_ZIP1).setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(0, 1));
		getPdfFieldsMap().get(ENTERPRISE_ZIP2).setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(1, 2));
		getPdfFieldsMap().get(ENTERPRISE_ZIP3).setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(2, 3));
		getPdfFieldsMap().get(ENTERPRISE_ZIP4).setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(3, 4));
		getPdfFieldsMap().get(ENTERPRISE_ZIP5).setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(4, 5));
		/* 
		 * Contract ccc fields
		 */
		getPdfFieldsMap().get(CCC_REG1).setValue(null);
		getPdfFieldsMap().get(CCC_REG2).setValue(null);
		getPdfFieldsMap().get(CCC_REG3).setValue(null);
		getPdfFieldsMap().get(CCC_REG4).setValue(null);
		getPdfFieldsMap().get(CCC_PROV1).setValue(null);
		getPdfFieldsMap().get(CCC_PROV2).setValue(null);
		getPdfFieldsMap().get(CCC_NISS).setValue(contract.getEnterpriseCCC().getCcc());
		getPdfFieldsMap().get(CCC_CONTROL_DIGIT1).setValue(null);
		getPdfFieldsMap().get(CCC_CONTROL_DIGIT2).setValue(null);
		getPdfFieldsMap().get(CCC_ACTIVITY).setValue(contract.getEnterpriseCCC().getActivity().getDescription());
		getPdfFieldsMap().get(CCC_ACTIVITY_CODE1).setValue(null);
		getPdfFieldsMap().get(CCC_ACTIVITY_CODE2).setValue(null);
		/* 
		 * Contract workplace fields
		 */
		getPdfFieldsMap().get(WORKPLACE_COUNTRY).setValue(null);
		getPdfFieldsMap().get(WORKPLACE_COUNTRY_CODE1).setValue(null);
		getPdfFieldsMap().get(WORKPLACE_COUNTRY_CODE2).setValue(null);
		getPdfFieldsMap().get(WORKPLACE_COUNTRY_CODE3).setValue(null);
		getPdfFieldsMap().get(WORKPLACE_TOWN).setValue(contract.getWorkPlace().getAddress().getCity());
		getPdfFieldsMap().get(WORKPLACE_TOWN_CODE1).setValue(contract.getWorkPlace().getAddress().getZip().substring(0, 1));
		getPdfFieldsMap().get(WORKPLACE_TOWN_CODE2).setValue(contract.getWorkPlace().getAddress().getZip().substring(1, 2));
		getPdfFieldsMap().get(WORKPLACE_TOWN_CODE3).setValue(contract.getWorkPlace().getAddress().getZip().substring(2, 3));
		getPdfFieldsMap().get(WORKPLACE_TOWN_CODE4).setValue(contract.getWorkPlace().getAddress().getZip().substring(3, 4));
		getPdfFieldsMap().get(WORKPLACE_TOWN_CODE5).setValue(contract.getWorkPlace().getAddress().getZip().substring(4, 5));
		/*
		 * Contract employee fields
		 */
		getPdfFieldsMap().get(EMPLOYEE_NAME).setValue(contract.getPerson().getFullName());
		getPdfFieldsMap().get(EMPLOYEE_NIF).setValue(contract.getPerson().getRegistry().getDocument());
		getPdfFieldsMap().get(EMPLOYEE_BIRTH_DATE).setValue(contract.getPerson().getBirthDate().toString());
		getPdfFieldsMap().get(EMPLOYEE_NSS).setValue(contract.getPerson().getSocialSecurityNumber());
		getPdfFieldsMap().get(EMPLOYEE_FORMATION_CODE1).setValue(null);
		getPdfFieldsMap().get(EMPLOYEE_FORMATION_CODE2).setValue(null);
		getPdfFieldsMap().get(EMPLOYEE_COUNTRY_CODE1).setValue(String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(0,1));
		getPdfFieldsMap().get(EMPLOYEE_COUNTRY_CODE2).setValue(String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(1,2));
		getPdfFieldsMap().get(EMPLOYEE_COUNTRY_CODE3).setValue(String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(2,3));
		getPdfFieldsMap().get(EMPLOYEE_ADDRESS_TOWN).setValue(contract.getPerson().getRegistry().getDefaultAddress().getCity());
		if(contract.getPerson().getRegistry().getDefaultAddress()!=null 
				&& contract.getPerson().getRegistry().getDefaultAddress().getId()!=null
				&& contract.getPerson().getRegistry().getDefaultAddress().getZip().length()>=5){
			getPdfFieldsMap().get(EMPLOYEE_ADDRESS_TOWN_CODE1).setValue(contract.getPerson().getRegistry().getDefaultAddress().getZip().substring(0, 1));
			getPdfFieldsMap().get(EMPLOYEE_ADDRESS_TOWN_CODE2).setValue(contract.getPerson().getRegistry().getDefaultAddress().getZip().substring(1, 2));
			getPdfFieldsMap().get(EMPLOYEE_ADDRESS_TOWN_CODE3).setValue(contract.getPerson().getRegistry().getDefaultAddress().getZip().substring(2, 3));
			getPdfFieldsMap().get(EMPLOYEE_ADDRESS_TOWN_CODE4).setValue(contract.getPerson().getRegistry().getDefaultAddress().getZip().substring(3, 4));
			getPdfFieldsMap().get(EMPLOYEE_ADDRESS_TOWN_CODE5).setValue(contract.getPerson().getRegistry().getDefaultAddress().getZip().substring(4, 5));
			getPdfFieldsMap().get(EMPLOYEE_ADDRESS_COUNTRY).setValue(null);
			getPdfFieldsMap().get(EMPLOYEE_ADDRESS_COUNTRY_CODE1).setValue(null);
			getPdfFieldsMap().get(EMPLOYEE_ADDRESS_COUNTRY_CODE2).setValue(null);
			getPdfFieldsMap().get(EMPLOYEE_ADDRESS_COUNTRY_CODE3).setValue(null);
		}
	}
	
//	@Override
//	public void buildPdfCommon(Object contractDocument) throws UnsupportedContractModelException{
//		try {
//			PdfReader reader = new PdfReader(getContractModelUrl(modelName+".pdf"));
//			setContractWidth((double)reader.getPageSize(1).getWidth());
//			setContractHeight((double)reader.getPageSize(1).getHeight());
//			ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
//			PdfStamper stamp = new PdfStamper(reader, baos);
//			AcroFields form = stamp.getAcroFields();
//			String checkValue = null;
//			for(ContractPdfField field: getPdfFields()){
//				if(field.getType()==AcroFields.FIELD_TYPE_CHECKBOX){
//					if(checkValue==null){
//						checkValue = form.getAppearanceStates(field.getLabel())[0];
//					}
//					form.setField(field.getLabel(), field.getValue().equals("true")?checkValue:"");
//				} else {
//					form.setField(field.getLabel(), field.getValue());
//				}
//			}
////	    	stamp.setFormFlattening(true);
//			stamp.setFormFlattening(false);
//			stamp.close();
//			reader.close();
////	    	return baos.toByteArray();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (DocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	protected URL getContractModelUrl(String file) throws IOException {
//		if(getContractModelUrl()==null){
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URL[] urls = Classpath.search(cl, MODELS_PATH, file);
//			contractModelUrl = urls[0];
//		}
//		return contractModelUrl;
		return urls[0];
	}
	
//	public void readPdfFields(byte[] pdf, ContractModel model) throws IOException{
//		if(pdf==null){
//			if(model==null){
//				throw new FileNotFoundException("El modelo no se ha cargado correctamente o no existe");
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
//			throw new FileNotFoundException(msg);
//		}
//		readPdfFields(new PdfReader(getContractModelUrl(model+".pdf")));
//	}
//	
	protected void readPdfFields(PdfReader reader) throws IOException{
		
//		setContractWidth((int)reader.getPageSize(1).getWidth());
//		setContractHeight((int)reader.getPageSize(1).getHeight());
//		
//		numberOfContractPages = reader.getNumberOfPages();
		
		
//		setPdfFieldsMap(new HashMap<String, ContractPdfField>());
		
		
		AcroFields form = reader.getAcroFields();
		HashMap<?,?> fields = form.getFields();
		String key;
		ContractPdfField field;
		for (Iterator<?> it = fields.keySet().iterator(); it.hasNext();) {
			key = (String) it.next();
			field = new ContractPdfField();
			if(form.getFieldType(key)==AcroFields.FIELD_TYPE_CHECKBOX){
					field.setType(AcroFields.FIELD_TYPE_CHECKBOX);
					field.setValue(form.getField(key).equals(form.getAppearanceStates(key)[0])?"true":"false");
			} else if(form.getFieldType(key)==AcroFields.FIELD_TYPE_TEXT){
					field.setType(AcroFields.FIELD_TYPE_TEXT);
					field.setValue(form.getField(key));
			} else {
				field.setType(AcroFields.FIELD_TYPE_NONE);;
			}
			Float f = form.getFieldPositions(key)[0];
			field.setPage(f.intValue());
			field.setLabel(key);
			field.setBottomCoordinates(getBottomCoordinates(form, key));
			field.setLeftCoordinates(getLeftCoordinates(form, key));
			field.setWidth(getInputTextWidth(form, key));
			field.setHeight(getInputTextHeight(form, key));
			field.setZoomFactor(2);
			if(field.getType()!=null){
				getPdfFieldsMap().put(key, field);
			}
		}
		reader.close();
	}
	
	/*
	 * [page, llx, lly, urx, ury]
	 */
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
	
	