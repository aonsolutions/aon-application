package com.esferalia.aon.file.payroll.contract.pdf.model;

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
import com.code.aon.common.enumeration.IStringEnum;
import com.code.aon.common.util.Classpath;
import com.code.aon.geozone.GeoTree;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryDirStaff;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.ContractPdfField;
import com.esferalia.aon.file.payroll.contract.pdf.IContractPdfDocument;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
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
		return CONTRACT_DOCUMENT_PATH;
	}
	
	@Override
	public byte[] buildPdf(boolean readOnly) {
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
	
	public void loadPdfCommonFields(Contract contract, IContrataParams contrataParams) throws ManagerBeanException{
		ContrataContratoParams params = (ContrataContratoParams) contrataParams;
		
		/* 
		 * Contract enterprise fields
		 */
		setPdfFieldValue(FieldName.ENTERPRISE_CIF.getName(),contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
		RegistryDirStaff rDirStaff = obtainRegistryDirStaff(contract); 
		try {
			setPdfFieldValue(FieldName.ENTERPRISE_DIR_STAFF_NAME.getName(),rDirStaff.getName());
			setPdfFieldValue(FieldName.ENTERPRISE_DIR_STAFF_NIF.getName(),rDirStaff.getDocument());
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
			setPdfFieldValue(FieldName.ENTERPRISE_DIR_STAFF_CHARGE.getName(),rDirStaddCharge);
		} catch (NullPointerException npe) {
			// do nothing
		}
		setPdfFieldValue(FieldName.ENTERPRISE_NAME.getName(),contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
		setPdfFieldValue(FieldName.ENTERPRISE_ADDRESS.getName(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getFullAddress());
		try {	
			setPdfFieldValue(FieldName.ENTERPRISE_COUNTRY.getName(),contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getName(getLocale()));
			setPdfFieldValue(FieldName.ENTERPRISE_COUNTRY_CODE1.getName(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(0,1));
			setPdfFieldValue(FieldName.ENTERPRISE_COUNTRY_CODE2.getName(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(1,2));
			setPdfFieldValue(FieldName.ENTERPRISE_COUNTRY_CODE3.getName(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(2,3));
		} catch (StringIndexOutOfBoundsException aie) {
			// do nothing
		} catch (NullPointerException npe) {
			// do nothing
		}
		try {	
			RegistryAddress address = contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress();
			ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
			setPdfFieldValue(FieldName.ENTERPRISE_MUNICIPALITY.getName(),bundle.getString(address.getMunicipalityCode()));
			setPdfFieldValue(FieldName.ENTERPRISE_MUNICIPALITY_CODE1.getName(),address.getMunicipalityCode().substring(0, 1));
			setPdfFieldValue(FieldName.ENTERPRISE_MUNICIPALITY_CODE2.getName(),address.getMunicipalityCode().substring(1, 2));
			setPdfFieldValue(FieldName.ENTERPRISE_MUNICIPALITY_CODE3.getName(),address.getMunicipalityCode().substring(2, 3));
			setPdfFieldValue(FieldName.ENTERPRISE_MUNICIPALITY_CODE4.getName(),address.getMunicipalityCode().substring(3, 4));
			setPdfFieldValue(FieldName.ENTERPRISE_MUNICIPALITY_CODE5.getName(),address.getMunicipalityCode().substring(4, 5));
		} catch (StringIndexOutOfBoundsException aie) {
			// do nothing
		} catch (NullPointerException npe) {
			// do nothing
		}
		try {	
			setPdfFieldValue(FieldName.ENTERPRISE_ZIP1.getName(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(0, 1));
			setPdfFieldValue(FieldName.ENTERPRISE_ZIP2.getName(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(1, 2));
			setPdfFieldValue(FieldName.ENTERPRISE_ZIP3.getName(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(2, 3));
			setPdfFieldValue(FieldName.ENTERPRISE_ZIP4.getName(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(3, 4));
			setPdfFieldValue(FieldName.ENTERPRISE_ZIP5.getName(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(4, 5));
		} catch (StringIndexOutOfBoundsException aie) {
			// do nothing
		} catch (NullPointerException npe) {
			// do nothing
		}
		/* 
		 * Contract ccc fields
		 */
		if(contract.getEnterpriseCCC()!=null){
			setPdfFieldValue(FieldName.CCC_REG1.getName(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(0, 1));
			setPdfFieldValue(FieldName.CCC_REG2.getName(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(1, 2));
			setPdfFieldValue(FieldName.CCC_REG3.getName(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(2, 3));
			setPdfFieldValue(FieldName.CCC_REG4.getName(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(3, 4));
			if(contract.getEnterpriseCCC().getCcc().length()==11){
				setPdfFieldValue(FieldName.CCC_PROV1.getName(),contract.getEnterpriseCCC().getCcc().substring(0, 1));
				setPdfFieldValue(FieldName.CCC_PROV2.getName(),contract.getEnterpriseCCC().getCcc().substring(1, 2));
				setPdfFieldValue(FieldName.CCC_NISS.getName(),contract.getEnterpriseCCC().getCcc().substring(2, 9));
				setPdfFieldValue(FieldName.CCC_CONTROL_DIGIT1.getName(),contract.getEnterpriseCCC().getCcc().substring(9, 10));
				setPdfFieldValue(FieldName.CCC_CONTROL_DIGIT2.getName(),contract.getEnterpriseCCC().getCcc().substring(10, 11));
			} else {
				setPdfFieldValue(FieldName.CCC_NISS.getName(),contract.getEnterpriseCCC().getCcc());
			}
			setPdfFieldValue(FieldName.CCC_ACTIVITY.getName(),contract.getEnterpriseCCC().getActivity().getDescription());
			setPdfFieldValue(FieldName.CCC_ACTIVITY_CODE1.getName(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(0, 1));
			setPdfFieldValue(FieldName.CCC_ACTIVITY_CODE2.getName(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(1, 2));
		}
		/* 
		 * Contract workplace fields
		 */
		try {
			GeoZone country = obtainCountry(contract.getWorkPlace().getAddress().getGeozone());
			setPdfFieldValue(FieldName.WORKPLACE_COUNTRY.getName(),country.getName());
			setPdfFieldValue(FieldName.WORKPLACE_COUNTRY_CODE1.getName(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
			setPdfFieldValue(FieldName.WORKPLACE_COUNTRY_CODE2.getName(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
			setPdfFieldValue(FieldName.WORKPLACE_COUNTRY_CODE3.getName(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
		} catch (StringIndexOutOfBoundsException aie) {
			// do nothing
		} catch (NullPointerException npe) {
			// do nothing
		}
		try {
			RegistryAddress address = contract.getWorkPlace().getAddress();
			ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
			setPdfFieldValue(FieldName.WORKPLACE_MUNICIPALITY.getName(),bundle.getString(address.getMunicipalityCode()));
			setPdfFieldValue(FieldName.WORKPLACE_MUNICIPALITY_CODE1.getName(),address.getMunicipalityCode().substring(0, 1));
			setPdfFieldValue(FieldName.WORKPLACE_MUNICIPALITY_CODE2.getName(),address.getMunicipalityCode().substring(1, 2));
			setPdfFieldValue(FieldName.WORKPLACE_MUNICIPALITY_CODE3.getName(),address.getMunicipalityCode().substring(2, 3));
			setPdfFieldValue(FieldName.WORKPLACE_MUNICIPALITY_CODE4.getName(),address.getMunicipalityCode().substring(3, 4));
			setPdfFieldValue(FieldName.WORKPLACE_MUNICIPALITY_CODE5.getName(),address.getMunicipalityCode().substring(4, 5));
		} catch (StringIndexOutOfBoundsException aie) {
			// do nothing
		} catch (NullPointerException npe) {
			// do nothing
		}
		/*
		 * Contract employee fields
		 */
		setPdfFieldValue(FieldName.EMPLOYEE_NAME.getName(),contract.getPerson().getFullName());
		setPdfFieldValue(FieldName.EMPLOYEE_NIF.getName(),contract.getPerson().getRegistry().getDocument());
		if(contract.getPerson().getBirthDate()!=null){
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			setPdfFieldValue(FieldName.EMPLOYEE_BIRTH_DATE.getName(),formatter.format(contract.getPerson().getBirthDate()));
		}
		setPdfFieldValue(FieldName.EMPLOYEE_NSS.getName(),contract.getPerson().getSocialSecurityNumber());
		if(params!=null && params.getNivelFormativo()!=null){
			setPdfFieldValue(FieldName.EMPLOYEE_FORMATION_LEVEL.getName(),params.getNivelFormativo().getDescription());
			setPdfFieldValue(FieldName.EMPLOYEE_FORMATION_CODE1.getName(),params.getNivelFormativo().getCode().substring(0, 1));
			setPdfFieldValue(FieldName.EMPLOYEE_FORMATION_CODE2.getName(),params.getNivelFormativo().getCode().substring(1, 2));
		}
		try {
			setPdfFieldValue(FieldName.EMPLOYEE_COUNTRY.getName(),String.valueOf(contract.getPerson().getRegistry().getNationality().getName(getLocale())));
			setPdfFieldValue(FieldName.EMPLOYEE_COUNTRY_CODE1.getName(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(0,1));
			setPdfFieldValue(FieldName.EMPLOYEE_COUNTRY_CODE2.getName(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(1,2));
			setPdfFieldValue(FieldName.EMPLOYEE_COUNTRY_CODE3.getName(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(2,3));
		} catch (StringIndexOutOfBoundsException aie) {
			// do nothing
		} catch (NullPointerException npe) {
			// do nothing
		}
		try {
			RegistryAddress address = contract.getPerson().getRegistry().getDefaultAddress();
			ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_MUNICIPALITY.getName(),bundle.getString(address.getMunicipalityCode()));
			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1.getName(),address.getMunicipalityCode().substring(0, 1));
			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2.getName(),address.getMunicipalityCode().substring(1, 2));
			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3.getName(),address.getMunicipalityCode().substring(2, 3));
			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4.getName(),address.getMunicipalityCode().substring(3, 4));
			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5.getName(),address.getMunicipalityCode().substring(4, 5));
		} catch (StringIndexOutOfBoundsException aie) {
			// do nothing
		} catch (NullPointerException npe) {
			// do nothing
		}
		try {
			GeoZone country = obtainCountry(contract.getPerson().getRegistry().getDefaultAddress().getGeozone());
			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_COUNTRY.getName(),country.getName());
			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_COUNTRY_CODE1.getName(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_COUNTRY_CODE2.getName(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
			setPdfFieldValue(FieldName.EMPLOYEE_ADDRESS_COUNTRY_CODE3.getName(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
		} catch (StringIndexOutOfBoundsException aie) {
			// do nothing
		} catch (NullPointerException npe) {
			// do nothing
		}
		
		// START TODO: obtain from ContractInfo
		setPdfFieldValue(FieldName.HOLIDAYS.getName(),"");
		// END TODO: obtain from ContractInfo
		
		setPdfFieldValue(FieldName.ADDITIONAL_CLAUSES.getName(),"");

		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		setPdfFieldValue(FieldName.SIGN_TOWN.getName(),contract.getWorkPlace().getAddress().getCity());
		dateFormatter.applyPattern("dd");
		setPdfFieldValue(FieldName.SIGN_DAY.getName(),dateFormatter.format(contract.getStartDate()));
		dateFormatter.applyPattern("MMMM");
		setPdfFieldValue(FieldName.SIGN_MONTH.getName(),dateFormatter.format(contract.getStartDate()));
		dateFormatter.applyPattern("yy");
		setPdfFieldValue(FieldName.SIGN_YEAR.getName(),dateFormatter.format(contract.getStartDate()));
		
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
				map.put(data.getName(), data.getExpression().replace('"', ' ').trim());
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
				map.put(info.getName(), info.getExpression().replace('"', ' ').trim());
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
		public String getName();
		public boolean isOverridable();
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
		 * Common fields
		 */
		HOLIDAYS("vacaciones",Boolean.TRUE),
		
		/*
		 * Other fields
		 */
		ADDITIONAL_CLAUSES("clausadici",Boolean.TRUE),
		SIGN_TOWN("munifirma",Boolean.FALSE),
		SIGN_DAY("diafirma",Boolean.FALSE),
		SIGN_MONTH("mesfirma",Boolean.FALSE),
		SIGN_YEAR("añofirma",Boolean.FALSE),
		;
		
		
		
		private String name;
		private boolean overridable;
		
		private FieldName(String name, boolean overridable) {
			this.name = name;
			this.overridable = overridable;
		}
		
		public String getName(){
			return name;
		}
		public boolean isOverridable(){
			return overridable;
		}

		@Override
		public String getValue() {
			return name;
		}
	}
	
}
	
	