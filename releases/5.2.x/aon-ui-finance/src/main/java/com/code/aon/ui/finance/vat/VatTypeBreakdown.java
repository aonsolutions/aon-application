package com.code.aon.ui.finance.vat;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.enumeration.VatReportType;
import com.code.aon.finance.enumeration.VatType;
import com.code.aon.finance.vat.Vat;

public class VatTypeBreakdown  {

	private VatType vatType;
	private Map<VatReportType,VatReportTypeBreakdown> map;
	private DataModel model;
	
	private double base;
	private double quota;
	
	public VatTypeBreakdown(VatType vatType) {
		setVatType(vatType);
	}
	
	public VatType getVatType() {
		return vatType;
	}
	public void setVatType(VatType vatType) {
		this.vatType = vatType;
	}
	
	public Map<VatReportType, VatReportTypeBreakdown> getMap() {
		if (map== null) {
			map = new HashMap<VatReportType, VatReportTypeBreakdown>();
			map.put(VatReportType.GENERAL, new VatReportTypeBreakdown(VatReportType.GENERAL));
			map.put(VatReportType.SURCHARGE, new VatReportTypeBreakdown(VatReportType.SURCHARGE));
			map.put(VatReportType.INTRACOMMUNITY, new VatReportTypeBreakdown(VatReportType.INTRACOMMUNITY));
			map.put(VatReportType.EXTRACOMMUNITY, new VatReportTypeBreakdown(VatReportType.EXTRACOMMUNITY));
		}
		return map;
	}
	public void setMap(Map<VatReportType, VatReportTypeBreakdown> map) {
		this.map = map;
	}
	
	public void addVat(Vat vat) {
		VatReportType reportType = vat.getReportType();
		VatReportTypeBreakdown vrtb = getMap().get(reportType);
		if (vrtb == null) {
			vrtb = new VatReportTypeBreakdown(reportType);
			getMap().put(reportType,vrtb);	
		}
		vrtb.add(vat,false);
		setBase( CommonUtil.round(getBase() + vat.getBase()));
		setQuota( CommonUtil.round(getQuota() + vat.getVatQuota()));
		if (reportType == VatReportType.GENERAL && vat.getSurcharge() > 0 ) {
			VatReportTypeBreakdown v = getMap().get(VatReportType.SURCHARGE);
			if (v == null) {
				v = new VatReportTypeBreakdown(VatReportType.SURCHARGE);
				getMap().put(VatReportType.SURCHARGE,v);	
			}
			v.add(vat,true);
			setQuota( CommonUtil.round(getQuota() + vat.getSurchargeQuota()));
		}
	}

	public DataModel getModel() {
		if (model == null) {
			Collection<VatReportTypeBreakdown> c = getMap().values();
			List<VatReportTypeBreakdown> list = new LinkedList<VatReportTypeBreakdown>();
			list.addAll(c);
			model = new ListDataModel(list);
		}
		return model;
	}

	public double getBase() {
		return base;
	}

	public void setBase(double base) {
		this.base = base;
	}

	public double getQuota() {
		return quota;
	}

	public void setQuota(double quota) {
		this.quota = quota;
	}
}
