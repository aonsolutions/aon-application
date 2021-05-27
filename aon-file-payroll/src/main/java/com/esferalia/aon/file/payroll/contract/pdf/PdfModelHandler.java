package com.esferalia.aon.file.payroll.contract.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.geozone.GeoTree;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryDirStaff;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractInfo;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;


public class PdfModelHandler implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Double documentWidth;
	private Double documentHeight;
	private Integer numberOfDocumentPages;
	private Map<String, ContractPdfField> pdfFieldsMap;
	

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
	
	public Double getDocumentWidth() {
		return documentWidth;
	}

	public void setDocumentWidth(Double documentWidth) {
		this.documentWidth = documentWidth;
	}

	public Double getDocumentHeight() {
		return documentHeight;
	}

	public void setDocumentHeight(Double documentHeight) {
		this.documentHeight = documentHeight;
	}

	public Integer getNumberOfDocumentPages() {
		return numberOfDocumentPages;
	}
	
	public void setNumberOfDocumentPages(Integer numberOfDocumentPages) {
		this.numberOfDocumentPages = numberOfDocumentPages;
	}
	
	
	public byte[] buildPdf(PdfReader reader, boolean readOnly) {
		try {
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
			// do nothing
		} catch (DocumentException e) {
			// do nothing
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
//				field.setValue(key);
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
			// do nothing
		}
	}
	
	
	public void readPdfFields(PdfReader reader) throws IOException{
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
//			field.setValue(key);
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
	
	public void setPdfFieldValue(String name, String value){
		try {
			getPdfFieldsMap().get(name).setValue(value);
		} catch (NullPointerException npe) {
			// do nothing
		}
	}
	
	public RegistryDirStaff obtainRegistryDirStaff(Registry registry) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID), registry.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REPRESENTATIVE_LABOR), Boolean.TRUE);
		Expression exp1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE), new Date());
		Expression exp2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			return (RegistryDirStaff) list.get(0);
		} else {
			bean = BeanManager.getManagerBean(RegistryDirStaff.class);
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID), registry.getId());
			exp1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE), new Date());
			exp2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));
			list = bean.getList(criteria);
			if(!list.isEmpty()){
				return (RegistryDirStaff) list.get(0);
			}
		}
		return null;
	}
	
	public GeoZone obtainCountry(GeoZone geoZone) throws ManagerBeanException {
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

	public Country obtainCountryByValue(GeoZone country) {
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

	public Map<String, String> getContractDataMap(Contract contract, Date startDate, Date endDate, boolean current) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			if(startDate!=null){
				criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), startDate);
			}
			if(endDate!=null){
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), endDate);
			}
			criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), current);
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
	
	
}
	
	