package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.Classpath;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;



public abstract class AbstractContractModel implements IContractPdfModel {
	
	private Double contractWidth;
	private Double contractHeight;
	private Integer numberOfContractPages;
	private Map<String, ContractPdfField> pdfFieldsMap;
	protected String modelName;
	
	public Collection<ContractPdfField> getPdfFields() {
		return getPdfFieldsMap().values();
	}
	
	public Map<String, ContractPdfField> getPdfFieldsMap() {
		if(pdfFieldsMap==null){
			pdfFieldsMap = new HashMap<String, ContractPdfField>();
		}
		return pdfFieldsMap;
	}
	
	public void setPdfFieldsMap(Map<String, ContractPdfField> pdfFieldsMap) {
		this.pdfFieldsMap = pdfFieldsMap;
	}
	
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

	@Override
	public Integer getNumberOfContractPages() {
		return numberOfContractPages;
	}
	
	public void setNumberOfContractPages(Integer numberOfContractPages) {
		this.numberOfContractPages = numberOfContractPages;
	}

	public byte[] buildPdf() {
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(modelName+".pdf"));
			
			setContractWidth((double)reader.getPageSize(1).getWidth());
			setContractHeight((double)reader.getPageSize(1).getHeight());
			setNumberOfContractPages(reader.getNumberOfPages());
			
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
			setContractWidth((double)reader.getPageSize(1).getWidth());
			setContractHeight((double)reader.getPageSize(1).getHeight());
			setNumberOfContractPages(reader.getNumberOfPages());
			
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected URL getContractModelUrl(String file) throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = Classpath.search(cl, MODELS_PATH, file);
		return urls[0];
	}
	
	protected void readPdfFields(PdfReader reader) throws IOException{
		setContractWidth((double)reader.getPageSize(1).getWidth());
		setContractHeight((double)reader.getPageSize(1).getHeight());
		setNumberOfContractPages(reader.getNumberOfPages());
		
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
	
	private void setPdfFieldValue(String name, String value){
		try {
			getPdfFieldsMap().get(name).setValue(value);
		} catch (NullPointerException npe) {
			// do nothing
		}
	}
	
	public void loadPdfCommonFields(Contract contract) throws ManagerBeanException{
		/* 
		 * Contract enterprise fields
		 */
		setPdfFieldValue(ENTERPRISE_CIF,contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
		setPdfFieldValue(ENTERPRISE_DIR_STAFF_NAME,null);
		setPdfFieldValue(ENTERPRISE_DIR_STAFF_NIF,null);
		setPdfFieldValue(ENTERPRISE_DIR_STAFF_CHARGE,null);
		setPdfFieldValue(ENTERPRISE_NAME,contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
		setPdfFieldValue(ENTERPRISE_ADDRESS,contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getFullAddress());
		setPdfFieldValue(ENTERPRISE_COUNTRY,contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getName(Locale.getDefault()));
		try {	
			setPdfFieldValue(ENTERPRISE_COUNTRY_CODE1,String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(0,1));
			setPdfFieldValue(ENTERPRISE_COUNTRY_CODE2,String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(1,2));
			setPdfFieldValue(ENTERPRISE_COUNTRY_CODE3,String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(2,3));
		} catch (StringIndexOutOfBoundsException aie) {
			// do nothing
		}
		setPdfFieldValue(ENTERPRISE_TOWN,contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getCity());
		setPdfFieldValue(ENTERPRISE_TOWN_CODE1,null);
		setPdfFieldValue(ENTERPRISE_TOWN_CODE2,null);
		setPdfFieldValue(ENTERPRISE_TOWN_CODE3,null);
		setPdfFieldValue(ENTERPRISE_TOWN_CODE4,null);
		setPdfFieldValue(ENTERPRISE_TOWN_CODE5,null);
		try {	
			setPdfFieldValue(ENTERPRISE_ZIP1,contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(0, 1));
			setPdfFieldValue(ENTERPRISE_ZIP2,contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(1, 2));
			setPdfFieldValue(ENTERPRISE_ZIP3,contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(2, 3));
			setPdfFieldValue(ENTERPRISE_ZIP4,contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(3, 4));
			setPdfFieldValue(ENTERPRISE_ZIP5,contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(4, 5));
		} catch (StringIndexOutOfBoundsException aie) {
			// do nothing
		}
		/* 
		 * Contract ccc fields
		 */
		setPdfFieldValue(CCC_REG1,null);
		setPdfFieldValue(CCC_REG2,null);
		setPdfFieldValue(CCC_REG3,null);
		setPdfFieldValue(CCC_REG4,null);
		setPdfFieldValue(CCC_PROV1,null);
		setPdfFieldValue(CCC_PROV2,null);
		setPdfFieldValue(CCC_NISS,contract.getEnterpriseCCC().getCcc());
		setPdfFieldValue(CCC_CONTROL_DIGIT1,null);
		setPdfFieldValue(CCC_CONTROL_DIGIT2,null);
		setPdfFieldValue(CCC_ACTIVITY,contract.getEnterpriseCCC().getActivity().getDescription());
		setPdfFieldValue(CCC_ACTIVITY_CODE1,null);
		setPdfFieldValue(CCC_ACTIVITY_CODE2,null);
		/* 
		 * Contract workplace fields
		 */
		setPdfFieldValue(WORKPLACE_COUNTRY,null);
		setPdfFieldValue(WORKPLACE_COUNTRY_CODE1,null);
		setPdfFieldValue(WORKPLACE_COUNTRY_CODE2,null);
		setPdfFieldValue(WORKPLACE_COUNTRY_CODE3,null);
		setPdfFieldValue(WORKPLACE_TOWN,contract.getWorkPlace().getAddress().getCity());
		try {
			setPdfFieldValue(WORKPLACE_TOWN_CODE1,contract.getWorkPlace().getAddress().getZip().substring(0, 1));
			setPdfFieldValue(WORKPLACE_TOWN_CODE2,contract.getWorkPlace().getAddress().getZip().substring(1, 2));
			setPdfFieldValue(WORKPLACE_TOWN_CODE3,contract.getWorkPlace().getAddress().getZip().substring(2, 3));
			setPdfFieldValue(WORKPLACE_TOWN_CODE4,contract.getWorkPlace().getAddress().getZip().substring(3, 4));
			setPdfFieldValue(WORKPLACE_TOWN_CODE5,contract.getWorkPlace().getAddress().getZip().substring(4, 5));
		} catch (StringIndexOutOfBoundsException aie) {
			// do nothing
		}
		/*
		 * Contract employee fields
		 */
		setPdfFieldValue(EMPLOYEE_NAME,contract.getPerson().getFullName());
		setPdfFieldValue(EMPLOYEE_NIF,contract.getPerson().getRegistry().getDocument());
		setPdfFieldValue(EMPLOYEE_BIRTH_DATE,contract.getPerson().getBirthDate().toString());
		setPdfFieldValue(EMPLOYEE_NSS,contract.getPerson().getSocialSecurityNumber());
		setPdfFieldValue(EMPLOYEE_FORMATION_CODE1,null);
		setPdfFieldValue(EMPLOYEE_FORMATION_CODE2,null);
		try {
			setPdfFieldValue(EMPLOYEE_COUNTRY_CODE1,String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(0,1));
			setPdfFieldValue(EMPLOYEE_COUNTRY_CODE2,String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(1,2));
			setPdfFieldValue(EMPLOYEE_COUNTRY_CODE3,String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(2,3));
		} catch (StringIndexOutOfBoundsException aie) {
			// do nothing
		}
		setPdfFieldValue(EMPLOYEE_ADDRESS_TOWN,contract.getPerson().getRegistry().getDefaultAddress().getCity());
		try {
			setPdfFieldValue(EMPLOYEE_ADDRESS_TOWN_CODE1,contract.getPerson().getRegistry().getDefaultAddress().getZip().substring(0, 1));
			setPdfFieldValue(EMPLOYEE_ADDRESS_TOWN_CODE2,contract.getPerson().getRegistry().getDefaultAddress().getZip().substring(1, 2));
			setPdfFieldValue(EMPLOYEE_ADDRESS_TOWN_CODE3,contract.getPerson().getRegistry().getDefaultAddress().getZip().substring(2, 3));
			setPdfFieldValue(EMPLOYEE_ADDRESS_TOWN_CODE4,contract.getPerson().getRegistry().getDefaultAddress().getZip().substring(3, 4));
			setPdfFieldValue(EMPLOYEE_ADDRESS_TOWN_CODE5,contract.getPerson().getRegistry().getDefaultAddress().getZip().substring(4, 5));
		} catch (StringIndexOutOfBoundsException aie) {
			// do nothing
		}
		setPdfFieldValue(EMPLOYEE_ADDRESS_COUNTRY,null);
		setPdfFieldValue(EMPLOYEE_ADDRESS_COUNTRY_CODE1,null);
		setPdfFieldValue(EMPLOYEE_ADDRESS_COUNTRY_CODE2,null);
		setPdfFieldValue(EMPLOYEE_ADDRESS_COUNTRY_CODE3,null);
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
	
	