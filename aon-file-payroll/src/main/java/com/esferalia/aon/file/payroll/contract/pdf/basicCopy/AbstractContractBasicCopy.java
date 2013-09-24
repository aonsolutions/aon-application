package com.esferalia.aon.file.payroll.contract.pdf.basicCopy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.Classpath;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryDirStaff;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.ContractPdfField;
import com.esferalia.aon.file.payroll.contract.pdf.IContractPdfDocument;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;



public abstract class AbstractContractBasicCopy implements IContractPdfDocument {
	
	/* 
	 * Enterprise fields
	 */
	final String ENTERPRISE_CIF = "cif";
	final String ENTERPRISE_NAME = "razsoc";
	final String ENTERPRISE_ADDRESS = "domsocialem";
	final String ENTERPRISE_MUNICIPALITY_CODE1 = "codmuniem1";
	final String ENTERPRISE_MUNICIPALITY_CODE2 = "codmuniem2";
	final String ENTERPRISE_MUNICIPALITY_CODE3 = "codmuniem3";
	final String ENTERPRISE_MUNICIPALITY_CODE4 = "codmuniem4";
	final String ENTERPRISE_MUNICIPALITY_CODE5 = "codmuniem5";
	final String ENTERPRISE_DIR_STAFF_NAME = "nomrepr";
	
	/*
	 * Employee fields
	 */
	final String EMPLOYEE_NAME = "nomtrab";
	
	/*
	 * Contract fields
	 */
	final String CONTRACT_START_DATE = "fechaini";
	final String CONTRACT_TYPE = "cto";
	final String CONTRACT_TOTAL_DURATION = "totaldura1";
	final String CONTRACT_CATEGORY = "catetraba";
	final String CONTRACT_JOURNAL_HOURS_1 = "horasjorna1";
	final String CONTRACT_JOURNAL_HOURS_2 = "horasjorna2";
	final String CONTRACT_JOURNAL = "jornada";
	final String CONTRACT_REMUNERATION = "retribu";
	final String CONTRACT_REMUNERATION_EURO = "euros";
	final String CONTRACT_REMUNERATION_PERIOD = "perioretri";
	final String CONTRACT_VACATIONS = "vacaciones";
	
	final String CONTRACT_SIGN_TOWN = "munifirma";
	final String CONTRACT_SING_DAY = "diafirma";
	final String CONTRACT_SIGN_MONTH = "mesfirma";
	final String CONTRACT_SIGN_YEAR = "añofirma";
	
	public final static String CONTRACT_BASIC_COPY_PATH = "com/esferalia/aon/file/payroll/contract/basicCopyPdf/";
	
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
		return CONTRACT_BASIC_COPY_PATH;
	}
	
	@Override
	public byte[] buildPdf(boolean readOnly) {
		try {
			
			PdfReader reader = new PdfReader(getContractBasicCopyUrl(documentName+".pdf"));
			
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
			
			stamp.setFormFlattening(readOnly);
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
	
	protected URL getContractBasicCopyUrl(String file) throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = Classpath.search(cl, CONTRACT_BASIC_COPY_PATH, file);
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
		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		/* 
		 * Enterprise fields
		 */
		setPdfFieldValue(ENTERPRISE_CIF,contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
		RegistryDirStaff rDirStaff = obtainRegistryDirStaff(contract); 
		try {
			setPdfFieldValue(ENTERPRISE_DIR_STAFF_NAME,rDirStaff.getName());
		} catch (NullPointerException npe) {
			// do nothing
		}
		setPdfFieldValue(ENTERPRISE_NAME,contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
		
		RegistryAddress address = contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress();
		setPdfFieldValue(ENTERPRISE_ADDRESS,address.getFullAddress());
		try {
			// TODO: Enterprise municipality name in the contract basic copy?
//			ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
//			setPdfFieldValue(ENTERPRISE_MUNICIPALITY_NAME,bundle.getString(address.getMunicipalityCode()));
			setPdfFieldValue(ENTERPRISE_MUNICIPALITY_CODE1,address.getMunicipalityCode().substring(0, 1));
			setPdfFieldValue(ENTERPRISE_MUNICIPALITY_CODE2,address.getMunicipalityCode().substring(1, 2));
			setPdfFieldValue(ENTERPRISE_MUNICIPALITY_CODE3,address.getMunicipalityCode().substring(2, 3));
			setPdfFieldValue(ENTERPRISE_MUNICIPALITY_CODE4,address.getMunicipalityCode().substring(3, 4));
			setPdfFieldValue(ENTERPRISE_MUNICIPALITY_CODE5,address.getMunicipalityCode().substring(4, 5));
		} catch (StringIndexOutOfBoundsException aie) {
			// do nothing
		} catch (NullPointerException npe) {
			// do nothing
		}
		/*
		 * Employee fields
		 */
		setPdfFieldValue(EMPLOYEE_NAME,contract.getPerson().getFullName());
		/*
		 * Contract fields
		 */
		dateFormatter.applyPattern("dd/MM/yyyy");
		setPdfFieldValue(CONTRACT_START_DATE,dateFormatter.format(contract.getStartDate()));
		setPdfFieldValue(CONTRACT_TYPE, contract.getModel().getName(Locale.getDefault()));
		Integer durationInMonths = getMonthsBetweenDates(contract.getStartDate(), contract.getEndDate());
		if(durationInMonths!=null){
			setPdfFieldValue(CONTRACT_TOTAL_DURATION,durationInMonths!=null?durationInMonths+" meses":"");
		}
		setPdfFieldValue(CONTRACT_CATEGORY, contract.getCategoryDescription());
		setPdfFieldValue(CONTRACT_REMUNERATION,"Según Convenio");
//		setPdfFieldValue(CONTRACT_REMUNERATION_EURO, "euros brutos");
		setPdfFieldValue(CONTRACT_REMUNERATION_EURO, "");
//		setPdfFieldValue(CONTRACT_REMUNERATION_PERIOD, "mensuales");
		setPdfFieldValue(CONTRACT_REMUNERATION_PERIOD, "");
//		setPdfFieldValue(CONTRACT_VACATIONS, "30 días naturales por año trabajado");
		setPdfFieldValue(CONTRACT_VACATIONS, "Según Convenio");
		setPdfFieldValue(CONTRACT_SIGN_TOWN,contract.getWorkPlace().getAddress().getCity());
		setPdfFieldValue(CONTRACT_SING_DAY, String.valueOf(CommonUtil.getDay(contract.getStartDate())));
		dateFormatter.applyPattern("MMMM");
		setPdfFieldValue(CONTRACT_SIGN_MONTH, dateFormatter.format(contract.getStartDate()) );
		dateFormatter.applyPattern("yy");
		setPdfFieldValue(CONTRACT_SIGN_YEAR, dateFormatter.format(contract.getStartDate()));
	}
	
	private RegistryDirStaff obtainRegistryDirStaff(Contract contract) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID), contract.getWorkPlace().getEnterprise().getRegistry().getId());
		criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE), new Date());
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			return (RegistryDirStaff) list.get(0);
		}
		return null;
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
	
	private Integer getMonthsBetweenDates(Date startDate, Date endDate) {
		if(startDate!=null && endDate!=null){
			return (int) ((CommonUtil.getDaysBetweenDates(startDate, endDate, true))/30);
		}
		return null;
	}
	
}
	
	