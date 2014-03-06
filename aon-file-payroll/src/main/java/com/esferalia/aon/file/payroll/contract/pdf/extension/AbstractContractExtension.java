package com.esferalia.aon.file.payroll.contract.pdf.extension;

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
import java.util.ResourceBundle;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.util.Classpath;
import com.code.aon.geozone.GeoTree;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryDirStaff;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.ContractPdfField;
import com.esferalia.aon.file.payroll.contract.pdf.IContractPdfDocument;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
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



public abstract class AbstractContractExtension implements IContractPdfDocument {
	
	/* 
	 * Contract enterprise fields
	 */
	final String ENTERPRISE_CIF = "cif";
	final String ENTERPRISE_DIR_STAFF_NAME = "nomrepr";
	final String ENTERPRISE_DIR_STAFF_NIF = "dnirep";
	final String ENTERPRISE_DIR_STAFF_CHARGE = "cargorep";
	final String ENTERPRISE_NAME = "razsoc";
	final String ENTERPRISE_ADDRESS = "domsocialem";
	final String ENTERPRISE_COUNTRY = "Texto1pais1";
	final String ENTERPRISE_COUNTRY_CODE1 = "codpaisem1";
	final String ENTERPRISE_COUNTRY_CODE2 = "codpaisem2";
	final String ENTERPRISE_COUNTRY_CODE3 = "codpaisem3";
	final String ENTERPRISE_MUNICIPALITY = "Texto2muni1";
	final String ENTERPRISE_MUNICIPALITY_CODE1 = "codmuniem1";
	final String ENTERPRISE_MUNICIPALITY_CODE2 = "codmuniem2";
	final String ENTERPRISE_MUNICIPALITY_CODE3 = "codmuniem3";
	final String ENTERPRISE_MUNICIPALITY_CODE4 = "codmuniem4";
	final String ENTERPRISE_MUNICIPALITY_CODE5 = "codmuniem5";
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
	final String WORKPLACE_COUNTRY = "Texto3pais3";
	final String WORKPLACE_COUNTRY_CODE1 = "codpaisct1";
	final String WORKPLACE_COUNTRY_CODE2 = "codpaisct2";
	final String WORKPLACE_COUNTRY_CODE3 = "codpaisct3";
	final String WORKPLACE_MUNICIPALITY = "Texto4muni3";
	final String WORKPLACE_MUNICIPALITY_CODE1 = "codmunict1";
	final String WORKPLACE_MUNICIPALITY_CODE2 = "codmunict2";
	final String WORKPLACE_MUNICIPALITY_CODE3 = "codmunict3";
	final String WORKPLACE_MUNICIPALITY_CODE4 = "codmunict4";
	final String WORKPLACE_MUNICIPALITY_CODE5 = "codmunict5";
	
	/*
	 * Contract employee fields
	 */
	final String EMPLOYEE_NAME = "nomtrab";
	final String EMPLOYEE_NIF = "dnitra";
	final String EMPLOYEE_BIRTH_DATE = "fechanac";
	final String EMPLOYEE_NSS = "numafinss";
	final String EMPLOYEE_FORMATION_LEVEL = "nivel_formativo";
	final String EMPLOYEE_FORMATION_CODE1 = "codnivaca1";
	final String EMPLOYEE_FORMATION_CODE2 = "codnivaca2";
	final String EMPLOYEE_COUNTRY = "Texto5nacion1";
	final String EMPLOYEE_COUNTRY_CODE1 = "codnactra1";
	final String EMPLOYEE_COUNTRY_CODE2 = "codnactra2";
	final String EMPLOYEE_COUNTRY_CODE3 = "codnactra3";
	final String EMPLOYEE_ADDRESS_MUNICIPALITY = "Texto6domi20";
	final String EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1 = "codmunitra1";
	final String EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2 = "codmunitra2";
	final String EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3 = "codmunitra3";
	final String EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4 = "codmunitra4";
	final String EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5 = "codmunitra5";
	final String EMPLOYEE_ADDRESS_COUNTRY = "Texto7domi21";
	final String EMPLOYEE_ADDRESS_COUNTRY_CODE1 = "codpaisdomtr1";
	final String EMPLOYEE_ADDRESS_COUNTRY_CODE2 = "codpaisdomtr2";
	final String EMPLOYEE_ADDRESS_COUNTRY_CODE3 = "codpaisdomtr3";
	
	public final static String CONTRACT_EXTENSION_PATH = "com/esferalia/aon/file/payroll/contract/extensionPdf/";
	
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
		return CONTRACT_EXTENSION_PATH;
	}
	
	@Override
	public byte[] buildPdf(boolean readOnly) {
		try {
			
			PdfReader reader = new PdfReader(getContractExtensionUrl(documentName+".pdf"));
			
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
	
	protected URL getContractExtensionUrl(String file) throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = Classpath.search(cl, CONTRACT_EXTENSION_PATH, file);
		return urls[0];
	}
	
	protected void readPdfFields(PdfReader reader) throws IOException {
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
	
	public void loadPdfCommonFields(Contract contract, IContrataParams contrataParams) throws ManagerBeanException{
		
		ContrataContratoParams params = (ContrataContratoParams) contrataParams;
		
		/* 
		 * Contract enterprise fields
		 */
		setPdfFieldValue(ENTERPRISE_CIF,contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
		RegistryDirStaff rDirStaff = obtainRegistryDirStaff(contract); 
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
			ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
			setPdfFieldValue(ENTERPRISE_MUNICIPALITY,bundle.getString(address.getMunicipalityCode()));
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
			setPdfFieldValue(CCC_REG1,contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(0, 1));
			setPdfFieldValue(CCC_REG2,contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(1, 2));
			setPdfFieldValue(CCC_REG3,contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(2, 3));
			setPdfFieldValue(CCC_REG4,contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(3, 4));
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
			ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
			setPdfFieldValue(WORKPLACE_MUNICIPALITY,bundle.getString(address.getMunicipalityCode()));
			setPdfFieldValue(WORKPLACE_MUNICIPALITY_CODE1,address.getMunicipalityCode().substring(0, 1));
			setPdfFieldValue(WORKPLACE_MUNICIPALITY_CODE2,address.getMunicipalityCode().substring(1, 2));
			setPdfFieldValue(WORKPLACE_MUNICIPALITY_CODE3,address.getMunicipalityCode().substring(2, 3));
			setPdfFieldValue(WORKPLACE_MUNICIPALITY_CODE4,address.getMunicipalityCode().substring(3, 4));
			setPdfFieldValue(WORKPLACE_MUNICIPALITY_CODE5,address.getMunicipalityCode().substring(4, 5));
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
		if(params!=null && params.getNivelFormativo()!=null){
			setPdfFieldValue(EMPLOYEE_FORMATION_LEVEL,params.getNivelFormativo().getDescription());
			setPdfFieldValue(EMPLOYEE_FORMATION_CODE1,params.getNivelFormativo().getCode().substring(0, 1));
			setPdfFieldValue(EMPLOYEE_FORMATION_CODE2,params.getNivelFormativo().getCode().substring(1, 2));
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
			ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
			setPdfFieldValue(EMPLOYEE_ADDRESS_MUNICIPALITY,bundle.getString(address.getMunicipalityCode()));
			setPdfFieldValue(EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1,address.getMunicipalityCode().substring(0, 1));
			setPdfFieldValue(EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2,address.getMunicipalityCode().substring(1, 2));
			setPdfFieldValue(EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3,address.getMunicipalityCode().substring(2, 3));
			setPdfFieldValue(EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4,address.getMunicipalityCode().substring(3, 4));
			setPdfFieldValue(EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5,address.getMunicipalityCode().substring(4, 5));
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
	
	protected GeoZone obtainCountry(GeoZone geoZone) throws ManagerBeanException {
		IManagerBean geoTreeBean = BeanManager.getManagerBean(GeoTree.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(geoTreeBean.getFieldName(IEntityAlias.GEO_TREE_CHILD_ID), geoZone.getId());
		Iterator<ITransferObject> iter = geoTreeBean.getList(criteria).iterator();
		while(iter.hasNext()){
			GeoTree geoTree = (GeoTree)iter.next();
			return geoTree.getParent();
		}
		return null;
	}

	private Country obtainCountryByValue(GeoZone country) {
		for( Country c : Country.values() ) {
    		if ( c.getValue().equals(country.getCode()) ) {
    			return c;
    		}
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
	
}
	
	