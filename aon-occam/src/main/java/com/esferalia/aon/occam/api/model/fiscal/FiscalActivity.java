package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedHashMap;

import com.esferalia.aon.watson.util.AonNumberUtils;

public class FiscalActivity implements Serializable {

	private static final long serialVersionUID = -8409745811046443519L;
	
	private Integer id;
	private Integer domain;
	private Integer year;
	private String epigraph;
	private String description;
	private boolean farmer;
	private double maxPerson;
	private double maxImport;
	private double vatPercent;
	
	private LinkedHashMap<Integer,LinkedHashMap<Integer,FiscalActivityInfo>> map
		= new LinkedHashMap<Integer,LinkedHashMap<Integer,FiscalActivityInfo>>();
	
	public Integer getId() {
		return id;
	}
	public FiscalActivity setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public FiscalActivity setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getYear() {
		return year;
	}
	public FiscalActivity setYear(Integer year) {
		this.year = year;
		return this;
	}
	
	public String getEpigraph() {
		return epigraph;
	}
	public FiscalActivity setEpigraph(String epigraph) {
		this.epigraph = epigraph;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	public FiscalActivity setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public boolean isFarmer() {
		return farmer;
	}
	public FiscalActivity setFarmer(boolean farmer) {
		this.farmer = farmer;
		return this;
	}
	
	public double getMaxPerson() {
		return maxPerson;
	}
	public FiscalActivity setMaxPerson(double maxPerson) {
		this.maxPerson = maxPerson;
		return this;
	}
	
	public double getMaxImport() {
		return maxImport;
	}
	public FiscalActivity setMaxImport(double maxImport) {
		this.maxImport = maxImport;
		return this;
	}
	
	public double getVatPercent() {
		return vatPercent;
	}
	public FiscalActivity setVatPercent(double vatPercent) {
		this.vatPercent = vatPercent;
		return this;
	}
	
	public LinkedHashMap<Integer, LinkedHashMap<Integer,FiscalActivityInfo>> getMap() {
		return map;
	}
	public void setMap(LinkedHashMap<Integer, LinkedHashMap<Integer, FiscalActivityInfo>> map) {
		this.map = map;
	}
	
	public FiscalActivity add(FiscalActivityInfo fai) {
		try {
			LinkedHashMap<Integer,FiscalActivityInfo> detailMap =  getMap().get( fai.getInfoType().ordinal() );
			if (detailMap == null) {
				detailMap = new LinkedHashMap<Integer, FiscalActivityInfo>();
				getMap().put(fai.getInfoType().ordinal(), detailMap);
			}
			detailMap.put(fai.getInfoKey().ordinal(), fai);
			return this;
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}
	public boolean hasInfo() {
		return getMap().containsKey( FiscalActivityInfoKeyType.INFO.ordinal() );
	}
	public boolean hasIRPFModules() {
		return getMap().containsKey( FiscalActivityInfoKeyType.IRPF_MODULE.ordinal());
	}
	public boolean hasIRPFInfo() {
		return getMap().containsKey( FiscalActivityInfoKeyType.IRPF_INFO.ordinal());
	}
	public boolean hasVATModules() {
		return getMap().containsKey( FiscalActivityInfoKeyType.VAT_MODULE.ordinal());
	}
	public boolean hasVATInfo() {
		return getMap().containsKey( FiscalActivityInfoKeyType.VAT_INFO.ordinal());
	}
	public boolean hasInfoOrModules() {
		return hasInfo() || hasIRPFModules() || hasIRPFInfo() 
			|| hasVATModules() || hasVATInfo();
	}
	public void setValue(FiscalActivityInfoKeyType type, FiscalActivityInfoKey key, double value) {
		String val = AonNumberUtils.toString(value);
		this.setValue(type, key, val);
	}
	public void setValue(FiscalActivityInfoKeyType type, FiscalActivityInfoKey key, String value) {
		this.setValue(type.ordinal(), key.ordinal(), value);
	}
	public void setValue(Integer type, Integer key, Double value) {
		this.setValue(type, key, value==null?null:value.toString());
	}
	public void setValue(Integer type, Integer key, String value) {
		if ( getMap().get(type) == null || getMap().get(type).get(key) == null) {
			throw new IllegalStateException("No existe la clave " + key);
		}
		getMap().get(type).get(key).setValue(value);
	}
	public void setBase(Integer type, Integer key, double base) {
		if ( getMap().get(type) == null || getMap().get(type).get(key) == null) {
			throw new IllegalStateException("No existe la clave " + key);
		}
		getMap().get(type).get(key).setBase(base);
	}
	
	public String getValue(Integer type, Integer key) {
		if ( getMap().get(type) == null || getMap().get(type).get(key) == null ) {
			return null;
		}
		return getMap().get(type).get(key).getValue();
	}
	
	public double getDoubleValue(FiscalActivityInfoKey key) {
		return this.getDoubleValue(key.getType(), key);
	}
	
	public double getDoubleValue(FiscalActivityInfoKeyType type, FiscalActivityInfoKey key) {
		return this.getDoubleValue(type.ordinal(), key.ordinal());
	}
	public double getDoubleValue(Integer type, Integer key) {
		if (getMap().get(type).get(key) != null) {
			return AonNumberUtils.todouble( getMap().get(type).get(key).getValue());
		}
		return 0.0;
	}
	
	public double getInfoDoubleValue(FiscalActivityInfoKey key) {
		return getDoubleValue(FiscalActivityInfoKeyType.INFO, key);
	}
//	public void setInfoValue(FiscalActivityInfoKey key, double value) {
//		setValue(FiscalActivityInfoKeyType.INFO, key, value);
//	}
	public double getIRPFInfoDoubleValue(FiscalActivityInfoKey key) {
		return getDoubleValue(FiscalActivityInfoKeyType.IRPF_INFO, key);
	}
	public void setIRPFInfoValue(FiscalActivityInfoKey key, double value) {
		setValue(FiscalActivityInfoKeyType.IRPF_INFO, key, value);
	}
	public FiscalActivityInfo getIRPFModule(FiscalActivityInfoKey key) {
		return getMap().get(FiscalActivityInfoKeyType.IRPF_MODULE.ordinal()).get(key.ordinal());
	}
	public double getIRPFModuleDoubleValue(FiscalActivityInfoKey key) {
		return getDoubleValue(FiscalActivityInfoKeyType.IRPF_MODULE, key);
	}
	public void setIRPFModuleValue(FiscalActivityInfoKey key, double value) {
		setValue(FiscalActivityInfoKeyType.IRPF_MODULE, key, value);
	}
//	public double getVATInfoDoubleValue(FiscalActivityInfoKey key) {
//		return getDoubleValue(FiscalActivityInfoKeyType.VAT_INFO, key);
//	}
	public void setVATInfoValue(FiscalActivityInfoKey key, double value) {
		setValue(FiscalActivityInfoKeyType.VAT_INFO, key, value);
	}
//	public double getVATModuleDoubleValue(FiscalActivityInfoKey key) {
//		return getDoubleValue(FiscalActivityInfoKeyType.VAT_MODULE, key);
//	}
//	public void setVATModuleValue(FiscalActivityInfoKey key, double value) {
//		setValue(FiscalActivityInfoKeyType.VAT_MODULE, key, value);
//	}
	public double getModuleDetailDoubleValue(FiscalActivityInfoKey key) {
		return getDoubleValue(FiscalActivityInfoKeyType.MODULE_DETAIL, key);
	}
//	public void setModuleDetailValue(FiscalActivityInfoKey key, double value) {
//		setValue(FiscalActivityInfoKeyType.MODULE_DETAIL, key, value);
//	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		String CR = "\n";
		buf.append(" ------ " + getId() + " - " + getEpigraph() + CR );
		for (Integer type : getMap().keySet() ) {
			for (FiscalActivityInfo fai : getMap().get(type).values() ) {
				buf.append(fai.getInfoType() 
						+ " - " + fai.getInfoKey()
						+ " - " + fai.getValue()
						+ CR);		
			}
		}
		buf.append(" ------ ");
		return buf.toString();
	}

}
