package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
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
import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;
import com.code.aon.common.util.Classpath;
import com.code.aon.geozone.GeoTree;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryDirStaff;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.ContractPdfField;
import com.esferalia.aon.file.payroll.contract.pdf.IContractPdfDocument;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractInfo;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;


public abstract class AbstractContractModel implements IContractPdfDocument {
	
	public final static String CONTRACT_DOCUMENT_PATH = "com/esferalia/aon/file/payroll/contract/modelPdf/";
	
	private PdfReader reader;
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
	
	public PdfReader getReader() {
		return reader;
	}

	public void setReader(PdfReader reader) {
		this.reader = reader;
	}

	@Override
	public String getDocumentPath(){
		return CONTRACT_DOCUMENT_PATH;
	}
	
	private String getPagesRange(PdfReader reader){
		return "1-3,4";
	}
	
	@Override
	public byte[] buildPdf(boolean readOnly) {
		try {
			
//			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
//			reader.selectPages(getPagesRange(reader));
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

	public abstract void loadPdfFields(ContractCode code, Contract contract, IContrataParams contrataParams) throws UnsupportedContractDocumentException;

	public void loadPdfFields(ContractAttachment contractPdfDraft) {
		try {
			PdfReader reader = new PdfReader(contractPdfDraft.getData());
//			reader.selectPages(getPagesRange(reader));
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
			String msg = "No se ha podido cargar todos los datos del contrato en el documento.";
		}
	}
	
	protected URL getContractModelUrl(String file) throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = Classpath.search(cl, CONTRACT_DOCUMENT_PATH, file);
		return urls[0];
	}
	
	protected void readPdfFields(PdfReader reader) throws IOException{
		readPdfFields();
	}
	protected void readPdfFields() throws IOException{
//		reader.selectPages(getPagesRange(reader));
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
	
	protected void setPdfFieldValue(String name, String value){
		try {
			getPdfFieldsMap().get(name).setValue(value);
		} catch (NullPointerException npe) {
			// do nothing
		}
	}
	
	public void loadPdfCommonFields(Contract contract, IContrataParams contrataParams) throws ManagerBeanException{
//		ContrataContratoParams params = (ContrataContratoParams) contrataParams;
		
		// print all field keys of the pdf document
//		for(String key: getPdfFieldsMap().keySet()){
//			System.out.println(key);
//		}
		
//		/* 
//		 * Contract enterprise fields
//		 */
//		setPdfFieldValue(FieldName.ENTERPRISE_CIF.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
//		RegistryDirStaff rDirStaff = obtainRegistryDirStaff(contract); 
//		try {
//			setPdfFieldValue(FieldName.ENTERPRISE_DIR_STAFF_NAME.getValue(),rDirStaff.getName());
//			setPdfFieldValue(FieldName.ENTERPRISE_DIR_STAFF_NIF.getValue(),rDirStaff.getDocument());
//			String rDirStaddCharge = null;
//			if ( rDirStaff.isShareHolder() ){
//				rDirStaddCharge = "Socio";
//			} else if ( rDirStaff.isRepresentative() ){
//				rDirStaddCharge = "Apoderado";
//			} else if( rDirStaff.isDirector() ){
//				rDirStaddCharge = "Administrador";
//			} else if ( rDirStaff.isRepresentativeLabor() ){
//				rDirStaddCharge = "Representante laboral";
//			}
//			setPdfFieldValue(FieldName.ENTERPRISE_DIR_STAFF_CHARGE.getValue(),rDirStaddCharge);
//		} catch (NullPointerException npe) {
//			// do nothing
//		}
//		setPdfFieldValue(FieldName.ENTERPRISE_NAME.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
//		setPdfFieldValue(FieldName.ENTERPRISE_ADDRESS.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getFullAddress());
//		try {	
//			setPdfFieldValue(FieldName.ENTERPRISE_COUNTRY.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getName(getLocale()));
//			setPdfFieldValue(FieldName.ENTERPRISE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(0,1));
//			setPdfFieldValue(FieldName.ENTERPRISE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(1,2));
//			setPdfFieldValue(FieldName.ENTERPRISE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(2,3));
//		} catch (StringIndexOutOfBoundsException aie) {
//			// do nothing
//		} catch (NullPointerException npe) {
//			// do nothing
//		}
//		try {	
//			RegistryAddress address = contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress();
//			ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
//			setPdfFieldValue(FieldName.ENTERPRISE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
//			setPdfFieldValue(FieldName.ENTERPRISE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
//			setPdfFieldValue(FieldName.ENTERPRISE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
//			setPdfFieldValue(FieldName.ENTERPRISE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
//			setPdfFieldValue(FieldName.ENTERPRISE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
//			setPdfFieldValue(FieldName.ENTERPRISE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
//		} catch (StringIndexOutOfBoundsException aie) {
//			// do nothing
//		} catch (NullPointerException npe) {
//			// do nothing
//		}
//		try {	
//			setPdfFieldValue(FieldName.ENTERPRISE_ZIP1.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(0, 1));
//			setPdfFieldValue(FieldName.ENTERPRISE_ZIP2.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(1, 2));
//			setPdfFieldValue(FieldName.ENTERPRISE_ZIP3.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(2, 3));
//			setPdfFieldValue(FieldName.ENTERPRISE_ZIP4.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(3, 4));
//			setPdfFieldValue(FieldName.ENTERPRISE_ZIP5.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(4, 5));
//		} catch (StringIndexOutOfBoundsException aie) {
//			// do nothing
//		} catch (NullPointerException npe) {
//			// do nothing
//		}
//		/* 
//		 * Contract ccc fields
//		 */
//		if(contract.getEnterpriseCCC()!=null){
//			setPdfFieldValue(FieldName.CCC_REG1.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(0, 1));
//			setPdfFieldValue(FieldName.CCC_REG2.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(1, 2));
//			setPdfFieldValue(FieldName.CCC_REG3.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(2, 3));
//			setPdfFieldValue(FieldName.CCC_REG4.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(3, 4));
//			if(contract.getEnterpriseCCC().getCcc().length()==11){
//				setPdfFieldValue(FieldName.CCC_PROV1.getValue(),contract.getEnterpriseCCC().getCcc().substring(0, 1));
//				setPdfFieldValue(FieldName.CCC_PROV2.getValue(),contract.getEnterpriseCCC().getCcc().substring(1, 2));
//				setPdfFieldValue(FieldName.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc().substring(2, 9));
//				setPdfFieldValue(FieldName.CCC_CONTROL_DIGIT1.getValue(),contract.getEnterpriseCCC().getCcc().substring(9, 10));
//				setPdfFieldValue(FieldName.CCC_CONTROL_DIGIT2.getValue(),contract.getEnterpriseCCC().getCcc().substring(10, 11));
//			} else {
//				setPdfFieldValue(FieldName.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc());
//			}
//			setPdfFieldValue(FieldName.CCC_ACTIVITY.getValue(),contract.getEnterpriseCCC().getActivity().getDescription());
//			setPdfFieldValue(FieldName.CCC_ACTIVITY_CODE1.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(0, 1));
//			setPdfFieldValue(FieldName.CCC_ACTIVITY_CODE2.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(1, 2));
//		}
//		/* 
//		 * Contract workplace fields
//		 */
//		try {
//			GeoZone country = obtainCountry(contract.getWorkPlace().getAddress().getGeozone());
//			setPdfFieldValue(FieldName.WORKPLACE_COUNTRY.getValue(),country.getName());
//			setPdfFieldValue(FieldName.WORKPLACE_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
//			setPdfFieldValue(FieldName.WORKPLACE_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
//			setPdfFieldValue(FieldName.WORKPLACE_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
//		} catch (StringIndexOutOfBoundsException aie) {
//			// do nothing
//		} catch (NullPointerException npe) {
//			// do nothing
//		}
//		try {
//			RegistryAddress address = contract.getWorkPlace().getAddress();
//			ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
//			setPdfFieldValue(FieldName.WORKPLACE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
//			setPdfFieldValue(FieldName.WORKPLACE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
//			setPdfFieldValue(FieldName.WORKPLACE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
//			setPdfFieldValue(FieldName.WORKPLACE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
//			setPdfFieldValue(FieldName.WORKPLACE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
//			setPdfFieldValue(FieldName.WORKPLACE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
//		} catch (StringIndexOutOfBoundsException aie) {
//			// do nothing
//		} catch (NullPointerException npe) {
//			// do nothing
//		}
//		/*
//		 * Contract employee fields
//		 */
//		setPdfFieldValue(FieldName.EMPLOYEE_NAME.getValue(),contract.getPerson().getFullName());
//		setPdfFieldValue(FieldName.EMPLOYEE_NIF.getValue(),contract.getPerson().getRegistry().getDocument());
//		if(contract.getPerson().getBirthDate()!=null){
//			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//			setPdfFieldValue(FieldName.EMPLOYEE_BIRTH_DATE.getValue(),formatter.format(contract.getPerson().getBirthDate()));
//		}
//		setPdfFieldValue(FieldName.EMPLOYEE_NSS.getValue(),contract.getPerson().getSocialSecurityNumber());
//		if(params!=null && params.getNivelFormativo()!=null){
//			setPdfFieldValue(FieldName.EMPLOYEE_FORMATION_LEVEL.getValue(),params.getNivelFormativo().getDescription());
//			setPdfFieldValue(FieldName.EMPLOYEE_FORMATION_CODE1.getValue(),params.getNivelFormativo().getCode().substring(0, 1));
//			setPdfFieldValue(FieldName.EMPLOYEE_FORMATION_CODE2.getValue(),params.getNivelFormativo().getCode().substring(1, 2));
//		}
//		try {
//			setPdfFieldValue(FieldName.EMPLOYEE_COUNTRY.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getName(getLocale())));
//			setPdfFieldValue(FieldName.EMPLOYEE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(0,1));
//			setPdfFieldValue(FieldName.EMPLOYEE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(1,2));
//			setPdfFieldValue(FieldName.EMPLOYEE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(2,3));
//		} catch (StringIndexOutOfBoundsException aie) {
//			// do nothing
//		} catch (NullPointerException npe) {
//			// do nothing
//		}
//		try {
//			RegistryAddress address = contract.getPerson().getRegistry().getDefaultAddress();
//			ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
//			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
//			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
//			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
//			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
//			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
//			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
//		} catch (StringIndexOutOfBoundsException aie) {
//			// do nothing
//		} catch (NullPointerException npe) {
//			// do nothing
//		}
//		try {
//			GeoZone country = obtainCountry(contract.getPerson().getRegistry().getDefaultAddress().getGeozone());
//			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_COUNTRY.getValue(),country.getName());
//			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
//			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
//			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
//		} catch (StringIndexOutOfBoundsException aie) {
//			// do nothing
//		} catch (NullPointerException npe) {
//			// do nothing
//		}
//		
//		setPdfFieldValue(FieldName.ADDITIONAL_CLAUSES.getValue(),"");
//
//		SimpleDateFormat dateFormatter = new SimpleDateFormat();
//		setPdfFieldValue(FieldName.SIGN_TOWN.getValue(),contract.getWorkPlace().getAddress().getCity());
//		dateFormatter.applyPattern("dd");
//		setPdfFieldValue(FieldName.SIGN_DAY.getValue(),dateFormatter.format(contract.getStartDate()));
//		dateFormatter.applyPattern("MMMM");
//		setPdfFieldValue(FieldName.SIGN_MONTH.getValue(),dateFormatter.format(contract.getStartDate()));
//		dateFormatter.applyPattern("yy");
//		setPdfFieldValue(FieldName.SIGN_YEAR.getValue(),dateFormatter.format(contract.getStartDate()));
		
	}
	

	protected RegistryDirStaff obtainRegistryDirStaff(Contract contract) throws ManagerBeanException {
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
	
	protected RegistryDirStaff obtainRegistryDirStaff(Registry registry) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID), registry.getId());
		Expression exp1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE), new Date());
		Expression exp2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));
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

	protected Country obtainCountryByValue(GeoZone country) {
		for( Country c : Country.values() ) {
    		if ( c.getValue().equals(country.getCode()) ) {
    			return c;
    		}
    	}
    	return null;
	}
	
	public Map<String, String> getContractDataMap(Contract contract) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), contract.getStartDate());
			if(contract.getEndDate()!=null){
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), contract.getEndDate());
			} else {
				criteria.addNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
			}
			for(ITransferObject to: bean.getList(criteria)){
				ContractData data = (ContractData) to;
				map.put(data.getName(), data.getExpression()!=null?data.getExpression().replace('"', ' ').trim():null);
			}
		} catch (ManagerBeanException e) {
			// NADA, se devuelve un mapa vacio
			return map;
		}
		return map;
	}
	
	public Map<String, String> getContractInfoMap(Contract contract) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractInfo.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_CONTRACT_ID), contract.getId());
			for(ITransferObject to: bean.getList(criteria)){
				ContractInfo info = (ContractInfo) to;
				map.put(info.getName(), info.getExpression()!=null?info.getExpression().replace('"', ' ').trim():null);
			}
		} catch (ManagerBeanException e) {
			// NADA, se devuelve un mapa vacio
			return map;
		}
		return map;
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
	
	
	/*
	 * INNER CLASSES
	 */
	public interface IContractFieldName extends IStringEnum{
		public boolean isOverridable();
	}
	public interface IContractOptionField {
		
	}
	
	public enum ContractPdfModel {
		INDEFINITE,
		LEARNING,
		PRACTICE,
		TEMPORARY;
	}
	public enum ModelOption implements IResourceable {
	
		/**
		 * INDEFINIDO ORDINARIO (pag. 4)
		 */
		INDEFINITE_OPT1(ContractPdfModel.INDEFINITE, 4, ContractCode.C100, ContractCode.C200, ContractCode.C300),
		/**
		 * DE PERSONAS CON DISCAPACIDAD (pag. 5)
		 */
		INDEFINITE_OPT2(ContractPdfModel.INDEFINITE, 5,ContractCode.C130, ContractCode.C230, ContractCode.C330),
		/**
		 * DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO (pag.6)
		 */
		INDEFINITE_OPT3(ContractPdfModel.INDEFINITE, 6,ContractCode.C150, ContractCode.C250, ContractCode.C350),
		/**
		 * DE PERSONAS CON DISCAPACIDAD PROCEDENTES DE ENCLAVES LABORALES (pag.7)
		 */
		INDEFINITE_OPT4(ContractPdfModel.INDEFINITE, 7, ContractCode.C130, ContractCode.C230, ContractCode.C330),
		/**
		 * DE APOYO A LOS EMPRENDEDORES (pag.8)
		 */
		INDEFINITE_OPT5(ContractPdfModel.INDEFINITE, 8, ContractCode.C100, ContractCode.C200, ContractCode.C300, ContractCode.C150, ContractCode.C250, ContractCode.C350),
		/**
		 * DE UN JÓVEN POR MICROEMPRESAS Y EMPRESARIOS AUTÓNOMOS (pag.9)
		 */
		INDEFINITE_OPT6(ContractPdfModel.INDEFINITE, 9, ContractCode.C100, ContractCode.C200),
		/**
		 * DE NUEVO PROYECTO DE EMPRENDIMIENTO JOVEN (pag.10)
		 */
		INDEFINITE_OPT7(ContractPdfModel.INDEFINITE, 10, ContractCode.C100, ContractCode.C200, ContractCode.C300),
		/**
		 * A TIEMPO PARCIAL CON VINCULACIÓN FORMATIVA (pag.11)
		 */
		INDEFINITE_OPT8(ContractPdfModel.INDEFINITE, 11, ContractCode.C200, ContractCode.C300),
		/**
		 * DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL, VÍCTIMAS DE VIOLENCIA DE GÉNERO, DOMESTICA O VÍCTIMAS DE TERRORISMO (pag.12)
		 */
		INDEFINITE_OPT9(ContractPdfModel.INDEFINITE, 12, ContractCode.C150, ContractCode.C250, ContractCode.C350),
		/**
		 * DE EXCLUIDOS EN EMPRESAS DE INSERCIÓN (pag.13)
		 */
		INDEFINITE_OPT10(ContractPdfModel.INDEFINITE, 13, ContractCode.C150, ContractCode.C250, ContractCode.C350),
		/**
		 * DE MAYORES DE 52 AÑOS BENEFICIARIOS DE SUBSIDIOS POR DESEMPLEO (pag.14)
		 */
		INDEFINITE_OPT11(ContractPdfModel.INDEFINITE, 14, ContractCode.C100, ContractCode.C150, ContractCode.C300, ContractCode.C350),
		/**
		 * PROCENTE DE PRIMER EMPLEO JOVEN DE ETT. (pag.15)
		 */
		INDEFINITE_OPT12(ContractPdfModel.INDEFINITE, 15, ContractCode.C150, ContractCode.C250, ContractCode.C350),
		/**
		 * PROCEDENTE DE UN CONTRATO PARA LA FORMACIÓN Y EL APRENDIZAJE DE ETT (pag.16)
		 */
		INDEFINITE_OPT13(ContractPdfModel.INDEFINITE, 16, ContractCode.C100, ContractCode.C200, ContractCode.C300),
		/**
		 * PROCEDENTE DE UN CONTRATO EN PRÁCTICAS DE ETT. ( pág 17)
		 */
		INDEFINITE_OPT14(ContractPdfModel.INDEFINITE, 17, ContractCode.C150, ContractCode.C250, ContractCode.C350),
		/**
		 * DEL SERVICIO DEL HOGAR FAMILIAR (pag.18)
		 */
		INDEFINITE_OPT15(ContractPdfModel.INDEFINITE, 18, ContractCode.C100, ContractCode.C200),
		/**
		 * OTRAS SITUACIONES (pág19)
		 */
		INDEFINITE_OPT16(ContractPdfModel.INDEFINITE, 19, ContractCode.C990),
		/**
		 * CONVERSIÓN DE CONTRATO TEMPORAL EN CONTRATO INDEFINIDO (pag.20)
		 */
		INDEFINITE_OPT17(ContractPdfModel.INDEFINITE, 20, ContractCode.C109, ContractCode.C139, ContractCode.C189,
			ContractCode.C209, ContractCode.C239, ContractCode.C289,
			ContractCode.C309, ContractCode.C339, ContractCode.C389),
		
		/**
		 * FORMACIÓN Y APRENDIZAJE ( ORDINARIO ). ( pág.4 )
		 */
		LEARNING_OPT1(ContractPdfModel.LEARNING, 4, ContractCode.C421),
		/**
		 * DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL, VÍCTIMAS DE VIOLENCIA DE GÉNERO, DOMÉSTICA O VÍCTIMA DE TERRORISMO . ( pág.5 )
		 */
		LEARNING_OPT2(ContractPdfModel.LEARNING, 5, ContractCode.C450),
		/**
		 * DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO. ( pág.6 )
		 */
		LEARNING_OPT3(ContractPdfModel.LEARNING, 6, ContractCode.C421),
		/**
		 * DE TRABAJOS DE INTERÉS SOCIAL/FOMENTO DE EMPLEO AGRARIO. ( pág.7 )
		 */
		LEARNING_OPT4(ContractPdfModel.LEARNING, 7, ContractCode.C421),
		
		
		/**
		 * PRÁCTICAS ( ORDINARIO ). (pag. 4)
		 */
		PRACTICE_OPT1(ContractPdfModel.PRACTICE, 4, ContractCode.C420, ContractCode.C520),
		/**
		 * DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL, VÍCTIMAS DE VIOLENCIA DE GÉNERO, DOMESTICA O VÍCTIMA DE TERRORISMO .(pag.5)
		 */
		PRACTICE_OPT2(ContractPdfModel.PRACTICE, 5, ContractCode.C450, ContractCode.C550),
		/**
		 * DE TRABAJADORES MAYORES DE 52 AÑOS BENEFICIARIOS DE LOS SUBSIDIOS POR DESEMPLEO (pag.6)
		 */
		PRACTICE_OPT3(ContractPdfModel.PRACTICE, 6, ContractCode.C420),
		/**
		 * DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO. (pag.7)
		 */
		PRACTICE_OPT4(ContractPdfModel.PRACTICE, 7, ContractCode.C420, ContractCode.C520),
		/**
		 * DE TRABAJOS DE INTERES SOCIAL/FOMENTO DE EMPLEO AGRARIO. (pag.8)
		 */
		PRACTICE_OPT5(ContractPdfModel.PRACTICE, 8, ContractCode.C420, ContractCode.C520),
		
		
		/**
		 * OBRA O SERVICIO DETERMINADO. ( pág.4 )
		 */
		TEMPORARY_OPT1(ContractPdfModel.TEMPORARY, 4, ContractCode.C401, ContractCode.C501),
		/**
		 * EVENTUAL POR CIRCUNSTANCIAS DE LA PRODUCCIÓN. (pág.5 )
		 */
		TEMPORARY_OPT2(ContractPdfModel.TEMPORARY, 5, ContractCode.C402, ContractCode.C502),
		/**
		 * INTERINIDAD. ( pág.6 )
		 */
		TEMPORARY_OPT3(ContractPdfModel.TEMPORARY, 6, ContractCode.C410, ContractCode.C510),
		/**
		 * PRIMER EMPLEO JOVEN. ( pág.7 )
		 */
		TEMPORARY_OPT4(ContractPdfModel.TEMPORARY, 7, ContractCode.C402, ContractCode.C502),
		/**
		 * DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL, VÍCTIMAS DE VIOLENCIA DE GÉNERO, DOMÉSTICA O VÍCTIMA DE TERRORISMO. ( pág.8 )
		 */
		TEMPORARY_OPT5(ContractPdfModel.TEMPORARY, 8, ContractCode.C450, ContractCode.C550),
		/**
		 * DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL POR EMPRESA DE INSERCIÓN. ( pág.9 )
		 */
		TEMPORARY_OPT6(ContractPdfModel.TEMPORARY, 9, ContractCode.C450, ContractCode.C452, ContractCode.C550, ContractCode.C552),
		/**
		 * DE TRABAJADORES MAYORES DE 52 AÑOS BENEFICIARIOS DE LOS SUBSIDIOS POR DESEMPLEO. ( pág.10 )
		 */
		TEMPORARY_OPT7(ContractPdfModel.TEMPORARY, 10, ContractCode.C401, ContractCode.C402, ContractCode.C410, ContractCode.C990),
		/**
		 * SITUACIÓN DE JUBILACIÓN PARCIAL. ( pág.11 )
		 */
		TEMPORARY_OPT8(ContractPdfModel.TEMPORARY, 11, ContractCode.C540),
		/**
		 * RELEVO. ( pág.12 )
		 */
		TEMPORARY_OPT9(ContractPdfModel.TEMPORARY, 12, ContractCode.C441, ContractCode.C541),
		/**
		 * A TIEMPO PARCIAL CON VINCULACIÓN FORMATIVA. ( pág.13 )
		 */
		TEMPORARY_OPT10(ContractPdfModel.TEMPORARY, 13, ContractCode.C501, ContractCode.C502),
		/**
		 * DE TRABAJOS DE INTERÉS SOCIAL/FOMENTO DE EMPLEO AGRARIO. ( pág.14 )
		 */
		TEMPORARY_OPT11(ContractPdfModel.TEMPORARY, 14, ContractCode.C401, ContractCode.C402, ContractCode.C410, ContractCode.C450, ContractCode.C990,
				ContractCode.C501, ContractCode.C502, ContractCode.C510, ContractCode.C550, ContractCode.C990),
		/**
		 * DE TRABAJADORES DEL SERVICIO DEL HOGAR FAMILIAR. (pág.15 )
		 */
		TEMPORARY_OPT12(ContractPdfModel.TEMPORARY, 15, ContractCode.C401, ContractCode.C410, ContractCode.C501, ContractCode.C510),
		/**
		 * DE PERSONAS CON DISCAPACIDAD. (pág.16 )
		 */
		TEMPORARY_OPT13(ContractPdfModel.TEMPORARY, 16, ContractCode.C430, ContractCode.C530),
		/**
		 * DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO. (pág.17 )
		 */
		TEMPORARY_OPT14(ContractPdfModel.TEMPORARY, 17, ContractCode.C401, ContractCode.C402, ContractCode.C410, ContractCode.C430, ContractCode.C441, ContractCode.C990,
				ContractCode.C501, ContractCode.C502, ContractCode.C510, ContractCode.C530, ContractCode.C540, ContractCode.C541, ContractCode.C990),
		/**
		 * DE INVESTIGADORES. ( pág.18 )
		 */
		TEMPORARY_OPT15(ContractPdfModel.TEMPORARY, 18, ContractCode.C401, ContractCode.C420, ContractCode.C501, ContractCode.C520),
		/**
		 * DE TRABAJADOES/AS PENADOS EN INSTITUCIONES PENITENCIARIAS. (pág.19 )
		 */
		TEMPORARY_OPT16(ContractPdfModel.TEMPORARY, 19, ContractCode.C450, ContractCode.C550),
		/**
		 * DE MENORES Y JÓVENES EN CENTROS DE MENORES. ( SOMETIDOS A MEDIDADAS DE INTERNAMIENTO PREVISTAS EN LA LEY ORGÁNICA 5/2000 DE 21 DE ENERO ). ( pág.20 )
		 */
		TEMPORARY_OPT17(ContractPdfModel.TEMPORARY, 20, ContractCode.C450, ContractCode.C550),
		/**
		 * OTRAS SITUACIONES. ( pág.21 )
		 */
		TEMPORARY_OPT18(ContractPdfModel.TEMPORARY, 21, ContractCode.C990),
		
		;
		
		private ContractPdfModel pdfModel;
		private Integer pageNumber;
		private ContractCode[] codes;
		
		private ModelOption(ContractPdfModel pdfModel, Integer pageNumber, ContractCode... codes) {
			this.pdfModel = pdfModel;
			this.pageNumber = pageNumber;
			this.codes = codes;
		}
		
		public Integer getPageNumber() {
			return pageNumber;
		}
		
		public ContractCode[] getCodes() {
			return codes;
		}
		
		public String getPdfModel(){
			if(pdfModel==ContractPdfModel.INDEFINITE){
				return IndefiniteModel.MODEL_NAME;
			} else if(pdfModel==ContractPdfModel.LEARNING){
				return LearningModel.MODEL_NAME;
			} else if(pdfModel==ContractPdfModel.PRACTICE){
				return PracticeModel.MODEL_NAME;
			} else if(pdfModel==ContractPdfModel.TEMPORARY){
				return TemporaryModel.MODEL_NAME;
			}
			return null;
		}
		
		/** Message key prefix. */
		private static final String MSG_KEY_PREFIX = "aon_enum_contract_model_";
		
		@Override
		public String getName(Locale locale) {
		    ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
			return bundle.getString(MSG_KEY_PREFIX + toString());
		}
	}
	
	
	
	
	
	public enum FieldName implements IContractFieldName {
		
		/* 
		 * Contract enterprise fields
		 */
		ENTERPRISE_CIF("cif",Boolean.FALSE),
		ENTERPRISE_DIR_STAFF_NAME("nomrepr",Boolean.FALSE),
		ENTERPRISE_DIR_STAFF_NIF("dnirep",Boolean.FALSE),
		ENTERPRISE_DIR_STAFF_CHARGE("cargorep",Boolean.FALSE),
		ENTERPRISE_NAME("razsoc",Boolean.FALSE),
		ENTERPRISE_ADDRESS("domsocialem",Boolean.FALSE),
		ENTERPRISE_COUNTRY("Texto1pais",Boolean.FALSE),
		ENTERPRISE_COUNTRY_CODE1("codpaisem1",Boolean.FALSE),
		ENTERPRISE_COUNTRY_CODE2("codpaisem2",Boolean.FALSE),
		ENTERPRISE_COUNTRY_CODE3("codpaisem3",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY("Texto3mun",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY_CODE1("codmuniem1",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY_CODE2("codmuniem2",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY_CODE3("codmuniem3",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY_CODE4("codmuniem4",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY_CODE5("codmuniem5",Boolean.FALSE),
		ENTERPRISE_ZIP1("codpostem1",Boolean.FALSE),
		ENTERPRISE_ZIP2("codpostem2",Boolean.FALSE),
		ENTERPRISE_ZIP3("codpostem3",Boolean.FALSE),
		ENTERPRISE_ZIP4("codpostem4",Boolean.FALSE),
		ENTERPRISE_ZIP5("codpostem5",Boolean.FALSE),
		
		/* 
		 * Contract ccc fields
		 */
		CCC_REG1("regimen1",Boolean.FALSE),
		CCC_REG2("regimen2",Boolean.FALSE),
		CCC_REG3("regimen3",Boolean.FALSE),
		CCC_REG4("regimen4",Boolean.FALSE),
		CCC_PROV1("provniss1",Boolean.FALSE),
		CCC_PROV2("provniss2",Boolean.FALSE),
		CCC_NISS("numniss",Boolean.FALSE),
		CCC_CONTROL_DIGIT1("digcon1",Boolean.FALSE),
		CCC_CONTROL_DIGIT2("digcon2",Boolean.FALSE),
		CCC_ACTIVITY("litacteco",Boolean.FALSE),
		CCC_ACTIVITY_CODE1("codacteco1",Boolean.FALSE),
		CCC_ACTIVITY_CODE2("codacteco2",Boolean.FALSE),
		
		/* 
		 * Contract workplace fields
		 */
		WORKPLACE_COUNTRY("Texto4pais",Boolean.FALSE),
		WORKPLACE_COUNTRY_CODE1("codpaisct1",Boolean.FALSE),
		WORKPLACE_COUNTRY_CODE2("codpaisct2",Boolean.FALSE),
		WORKPLACE_COUNTRY_CODE3("codpaisct3",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY("Texto5municipio",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY_CODE1("codmunict1",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY_CODE2("codmunict2",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY_CODE3("codmunict3",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY_CODE4("codmunict4",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY_CODE5("codmunict5",Boolean.FALSE),
		
		/*
		 * Contract employee fields
		 */
		EMPLOYEE_NAME("nomtrab",Boolean.FALSE),
		EMPLOYEE_NIF("dnitra",Boolean.FALSE),
		EMPLOYEE_BIRTH_DATE("fechanac",Boolean.FALSE),
		EMPLOYEE_NSS("numafinss",Boolean.FALSE),
		EMPLOYEE_FORMATION_LEVEL("litnivaca",Boolean.FALSE),
		EMPLOYEE_FORMATION_CODE1("codnivaca1",Boolean.FALSE),
		EMPLOYEE_FORMATION_CODE2("codnivaca2",Boolean.FALSE),
		EMPLOYEE_COUNTRY("Texto5nacion1",Boolean.FALSE),
		EMPLOYEE_COUNTRY_CODE1("codnactra1",Boolean.FALSE),
		EMPLOYEE_COUNTRY_CODE2("codnactra2",Boolean.FALSE),
		EMPLOYEE_COUNTRY_CODE3("codnactra3",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY("Texto6mun",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1("codmunitra1",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2("codmunitra2",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3("codmunitra3",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4("codmunitra4",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5("codmunitra5",Boolean.FALSE),
		EMPLOYEE_ADDRESS_COUNTRY("Texto7padom",Boolean.FALSE),
		EMPLOYEE_ADDRESS_COUNTRY_CODE1("codpaisdomtr1",Boolean.FALSE),
		EMPLOYEE_ADDRESS_COUNTRY_CODE2("codpaisdomtr2",Boolean.FALSE),
		EMPLOYEE_ADDRESS_COUNTRY_CODE3("codpaisdomtr3",Boolean.FALSE),
		
		/*
		 * Other fields
		 */
		ADDITIONAL_CLAUSES("clausadici",Boolean.TRUE),
		SIGN_TOWN("munifirma",Boolean.FALSE),
		SIGN_DAY("diafirma",Boolean.FALSE),
		SIGN_MONTH("mesfirma",Boolean.FALSE),
		SIGN_YEAR("añofirma",Boolean.FALSE),
		;
		
		private String value;
		private boolean overridable;
		
		private FieldName(String value, boolean overridable) {
			this.value = value;
			this.overridable = overridable;
		}
		
		@Override
		public boolean isOverridable(){
			return overridable;
		}
		@Override
		public String getValue() {
			return value;
		}
	}
	
}
	
	