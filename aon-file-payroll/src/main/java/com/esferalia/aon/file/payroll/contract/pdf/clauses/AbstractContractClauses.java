package com.esferalia.aon.file.payroll.contract.pdf.clauses;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.Classpath;
import com.esferalia.aon.file.payroll.contract.pdf.ContractPdfField;
import com.esferalia.aon.file.payroll.contract.pdf.IContractPdfDocument;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;



public abstract class AbstractContractClauses implements IContractPdfDocument<IContrataParams> {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	/* 
	 * Enterprise fields
	 */
//	public final static String CLAUSES = "clausulas";
	public final static String CLAUSES_ENTERPRISE_NAME = "NOMBRE_EMP";
	public final static String CLAUSES_EMPLOYEE_NAME = "NOMBRE_TRABAJADOR";
	public final static String CLAUSES_CONTENT = "ADICIONAL1";
	public final static String CLAUSES_SIGN_TOWN = "NOMLOCA_S";
	public final static String CLAUSES_SIGN_DAY = "DIAC";
	public final static String CLAUSES_SIGN_MONTH = "MESC";
	public final static String CLAUSES_SIGN_YEAR = "ANOC";
	
	
	public final static String CONTRACT_CLAUSES_PATH = "com/esferalia/aon/file/payroll/contract/clausesPdf/";
	
	private Double documentWidth;
	private Double documentHeight;
	private Integer numberOfDocumentPages;
	private Map<String, ContractPdfField> pdfFieldsMap;
	protected String documentName;
	private Locale locale;
	
	public Locale getLocale() {
		return locale;
	}

	@Override
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
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
		return CONTRACT_CLAUSES_PATH;
	}
	
	@Override
	public byte[] buildPdf(boolean readOnly) {
		try {
			
			PdfReader reader = new PdfReader(getContractClausesUrl(documentName+".pdf"));
			
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
	
	protected URL getContractClausesUrl(String file) throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = Classpath.search(cl, CONTRACT_CLAUSES_PATH, file);
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
		setPdfFieldValue(CLAUSES_ENTERPRISE_NAME,"");
		setPdfFieldValue(CLAUSES_EMPLOYEE_NAME,"");
		setPdfFieldValue(CLAUSES_CONTENT,"");
		setPdfFieldValue(CLAUSES_SIGN_TOWN,"");
		setPdfFieldValue(CLAUSES_SIGN_DAY,"");
		setPdfFieldValue(CLAUSES_SIGN_MONTH,"");
		setPdfFieldValue(CLAUSES_SIGN_YEAR,"");
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
	
	