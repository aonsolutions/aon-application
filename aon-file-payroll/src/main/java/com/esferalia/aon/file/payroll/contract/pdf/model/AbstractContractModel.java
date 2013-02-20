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
import com.esferalia.aon.file.payroll.contract.pdf.ContractPdfField;
import com.esferalia.aon.file.payroll.contract.pdf.IContractPdfDocument;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;



public abstract class AbstractContractModel implements IContractPdfDocument {
	
	/* 
	 * Contract enterprise fields
	 */
	final String ENTERPRISE_CIF = "cif";
	final String ENTERPRISE_DIR_STAFF_NAME = "nomrepr";
	final String ENTERPRISE_DIR_STAFF_NIF = "dnirep";
	final String ENTERPRISE_DIR_STAFF_CHARGE = "cargorep";
	final String ENTERPRISE_NAME = "razsoc";
	final String ENTERPRISE_ADDRESS = "domsocialem";
	final String ENTERPRISE_COUNTRY = "Texto1pais";
	final String ENTERPRISE_COUNTRY_CODE1 = "codpaisem1";
	final String ENTERPRISE_COUNTRY_CODE2 = "codpaisem2";
	final String ENTERPRISE_COUNTRY_CODE3 = "codpaisem3";
	final String ENTERPRISE_TOWN = "Texto3mun";
	final String ENTERPRISE_TOWN_CODE1 = "codmuniem1";
	final String ENTERPRISE_TOWN_CODE2 = "codmuniem2";
	final String ENTERPRISE_TOWN_CODE3 = "codmuniem3";
	final String ENTERPRISE_TOWN_CODE4 = "codmuniem4";
	final String ENTERPRISE_TOWN_CODE5 = "codmuniem5";
	final String ENTERPRISE_ZIP1 = "codpostem1";
	final String ENTERPRISE_ZIP2 = "codpostem2";
	final String ENTERPRISE_ZIP3 = "codpostem3";
	final String ENTERPRISE_ZIP4 = "codpostem4";
	final String ENTERPRISE_ZIP5 = "codpostem5";
	
	/* 
	 * Contract ccc fields
	 */
	final String CCC_REG1 = "regimen1";
	final String CCC_REG2 = "regimen2";
	final String CCC_REG3 = "regimen3";
	final String CCC_REG4 = "regimen4";
	final String CCC_PROV1 = "provniss1";
	final String CCC_PROV2 = "provniss2";
	final String CCC_NISS = "numniss";
	final String CCC_CONTROL_DIGIT1 = "digcon1";
	final String CCC_CONTROL_DIGIT2 = "digcon2";
	final String CCC_ACTIVITY = "litacteco";
	final String CCC_ACTIVITY_CODE1 = "codacteco1";
	final String CCC_ACTIVITY_CODE2 = "codacteco2";
	
	/* 
	 * Contract workplace fields
	 */
	final String WORKPLACE_COUNTRY = "Texto4pais";
	final String WORKPLACE_COUNTRY_CODE1 = "codpaisct1";
	final String WORKPLACE_COUNTRY_CODE2 = "codpaisct2";
	final String WORKPLACE_COUNTRY_CODE3 = "codpaisct3";
	final String WORKPLACE_TOWN = "Texto5municipio";
	final String WORKPLACE_TOWN_CODE1 = "codmunict1";
	final String WORKPLACE_TOWN_CODE2 = "codmunict2";
	final String WORKPLACE_TOWN_CODE3 = "codmunict3";
	final String WORKPLACE_TOWN_CODE4 = "codmunict4";
	final String WORKPLACE_TOWN_CODE5 = "codmunict5";
	
	/*
	 * Contract employee fields
	 */
	final String EMPLOYEE_NAME = "nomtrab";
	final String EMPLOYEE_NIF = "dnitra";
	final String EMPLOYEE_BIRTH_DATE = "fechanac";
	final String EMPLOYEE_NSS = "numafinss";
	final String EMPLOYEE_FORMATION_CODE1 = "codnivaca1";
	final String EMPLOYEE_FORMATION_CODE2 = "codnivaca2";
	final String EMPLOYEE_COUNTRY_CODE1 = "codnactra1";
	final String EMPLOYEE_COUNTRY_CODE2 = "codnactra2";
	final String EMPLOYEE_COUNTRY_CODE3 = "codnactra3";
	final String EMPLOYEE_ADDRESS_TOWN = "Texto6mun";
	final String EMPLOYEE_ADDRESS_TOWN_CODE1 = "codmunitra1";
	final String EMPLOYEE_ADDRESS_TOWN_CODE2 = "codmunitra2";
	final String EMPLOYEE_ADDRESS_TOWN_CODE3 = "codmunitra3";
	final String EMPLOYEE_ADDRESS_TOWN_CODE4 = "codmunitra4";
	final String EMPLOYEE_ADDRESS_TOWN_CODE5 = "codmunitra5";
	final String EMPLOYEE_ADDRESS_COUNTRY = "Texto7padom";
	final String EMPLOYEE_ADDRESS_COUNTRY_CODE1 = "codpaisdomtr1";
	final String EMPLOYEE_ADDRESS_COUNTRY_CODE2 = "codpaisdomtr2";
	final String EMPLOYEE_ADDRESS_COUNTRY_CODE3 = "codpaisdomtr3";
	
	public final static String CONTRACT_DOCUMENT_PATH = "com/esferalia/aon/file/payroll/contract/modelPdf/";
	
	private Double documentWidth;
	private Double documentHeight;
	private Integer numberOfDocumentPages;
	private Map<String, ContractPdfField> pdfFieldsMap;
	protected String documentName;
	
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
	public Double getDocumentWidth() {
		return documentWidth;
	}

	public void setDocumentWidth(Double documentWidth) {
		this.documentWidth = documentWidth;
	}

	@Override
	public Double getDocumentHeight() {
		return documentHeight;
	}

	public void setDocumentHeight(Double documentHeight) {
		this.documentHeight = documentHeight;
	}

	@Override
	public Integer getNumberOfDocumentPages() {
		return numberOfDocumentPages;
	}
	
	public void setNumberOfDocumentPages(Integer numberOfDocumentPages) {
		this.numberOfDocumentPages = numberOfDocumentPages;
	}
	
	@Override
	public String getDocumentPath(){
		return CONTRACT_DOCUMENT_PATH;
	}
	
	public byte[] buildPdf() {
		try {
			
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			
			setDocumentWidth((double)reader.getPageSize(1).getWidth());
			setDocumentHeight((double)reader.getPageSize(1).getHeight());
			setNumberOfDocumentPages(reader.getNumberOfPages());
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
			PdfStamper stamp = new PdfStamper(reader, baos);
			
			AcroFields form = stamp.getAcroFields();
			
			String checkValue = null;
			for (Iterator<?> it = getPdfFields().iterator(); it.hasNext();) {
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

	public abstract void loadPdfFields(ContractCode code, Contract contract) throws UnsupportedContractDocumentException;

	public void loadPdfFields(ContractAttachment contractPdfDraft) {
		try {
			PdfReader reader = new PdfReader(contractPdfDraft.getData());
			setDocumentWidth((double)reader.getPageSize(1).getWidth());
			setDocumentHeight((double)reader.getPageSize(1).getHeight());
			setNumberOfDocumentPages(reader.getNumberOfPages());
			
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
			String msg = "No se ha podido cargar todos los datos del centrato en el documento.";
		}
	}
	
	protected URL getContractModelUrl(String file) throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = Classpath.search(cl, CONTRACT_DOCUMENT_PATH, file);
		return urls[0];
	}
	
	protected void readPdfFields(PdfReader reader) throws IOException{
		setDocumentWidth((double)reader.getPageSize(1).getWidth());
		setDocumentHeight((double)reader.getPageSize(1).getHeight());
		setNumberOfDocumentPages(reader.getNumberOfPages());
		
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
			PdfDictionary mergedField = form.getFieldItem( key ).getMerged( 0 );
			PdfNumber maxLengthNumber = mergedField.getAsNumber( PdfName.MAXLEN );
			if (maxLengthNumber != null) {
			  field.setMaxLength(maxLengthNumber.intValue());
			}
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
		} catch (NullPointerException npe) {
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
		} catch (NullPointerException npe) {
			// do nothing
		}
		/* 
		 * Contract ccc fields
		 */
		if(contract.getEnterpriseCCC()!=null){
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
		}
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
		} catch (NullPointerException npe) {
			// do nothing
		}
		/*
		 * Contract employee fields
		 */
		setPdfFieldValue(EMPLOYEE_NAME,contract.getPerson().getFullName());
		setPdfFieldValue(EMPLOYEE_NIF,contract.getPerson().getRegistry().getDocument());
		setPdfFieldValue(EMPLOYEE_BIRTH_DATE,contract.getPerson().getBirthDate()!=null?contract.getPerson().getBirthDate().toString():null);
		setPdfFieldValue(EMPLOYEE_NSS,contract.getPerson().getSocialSecurityNumber());
		setPdfFieldValue(EMPLOYEE_FORMATION_CODE1,null);
		setPdfFieldValue(EMPLOYEE_FORMATION_CODE2,null);
		try {
			setPdfFieldValue(EMPLOYEE_COUNTRY_CODE1,String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(0,1));
			setPdfFieldValue(EMPLOYEE_COUNTRY_CODE2,String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(1,2));
			setPdfFieldValue(EMPLOYEE_COUNTRY_CODE3,String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(2,3));
		} catch (StringIndexOutOfBoundsException aie) {
			// do nothing
		} catch (NullPointerException npe) {
			// do nothing
		}
		setPdfFieldValue(EMPLOYEE_ADDRESS_TOWN,contract.getPerson().getRegistry().getDefaultAddress()!=null?contract.getPerson().getRegistry().getDefaultAddress().getCity():null);
		try {
			setPdfFieldValue(EMPLOYEE_ADDRESS_TOWN_CODE1,contract.getPerson().getRegistry().getDefaultAddress().getZip().substring(0, 1));
			setPdfFieldValue(EMPLOYEE_ADDRESS_TOWN_CODE2,contract.getPerson().getRegistry().getDefaultAddress().getZip().substring(1, 2));
			setPdfFieldValue(EMPLOYEE_ADDRESS_TOWN_CODE3,contract.getPerson().getRegistry().getDefaultAddress().getZip().substring(2, 3));
			setPdfFieldValue(EMPLOYEE_ADDRESS_TOWN_CODE4,contract.getPerson().getRegistry().getDefaultAddress().getZip().substring(3, 4));
			setPdfFieldValue(EMPLOYEE_ADDRESS_TOWN_CODE5,contract.getPerson().getRegistry().getDefaultAddress().getZip().substring(4, 5));
		} catch (StringIndexOutOfBoundsException aie) {
			// do nothing
		} catch (NullPointerException npe) {
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
	
	