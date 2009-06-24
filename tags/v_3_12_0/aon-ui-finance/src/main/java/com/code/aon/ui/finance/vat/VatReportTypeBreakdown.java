package com.code.aon.ui.finance.vat;

import java.util.HashMap;
import java.util.Map;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.enumeration.VatReportType;
import com.code.aon.finance.vat.Vat;

public class VatReportTypeBreakdown  {

	private VatReportType vatReportType;
	private Map<Double,VatBreakdown> map;
	private double base;
	private double quota;

	public VatReportTypeBreakdown(VatReportType vatReportType) {
		setVatReportType(vatReportType);
	}
	public VatReportType getVatReportType() {
		return vatReportType;
	}
	public void setVatReportType(VatReportType vatReportType) {
		this.vatReportType = vatReportType;
	}
	public Map<Double, VatBreakdown> getMap() {
		if (map== null) {
			map = new HashMap<Double, VatBreakdown>(); 
		}
		return map;
	}
	public void setMap(Map<Double, VatBreakdown> map) {
		this.map = map;
	}

	public void add(Vat vat, boolean surcharge) {
		double percent = surcharge?vat.getSurcharge():vat.getPercent();
		double quota = surcharge?vat.getSurchargeQuota():vat.getVatQuota();
		VatBreakdown vb = getMap().get(percent);
		if (vb == null) {
			vb = new VatBreakdown();
		}
		setBase(CommonUtil.round(getBase() + vat.getBase()));
		vb.setBase(CommonUtil.round(vb.getBase() + vat.getBase()));
		setQuota(CommonUtil.round(getQuota() + quota));
		vb.setQuota(CommonUtil.round(vb.getQuota() + quota));
		getMap().put(percent,vb);
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
