package com.code.aon.ui.fiscal.vat;


import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.model.DataModel;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.enumeration.VatReportType;
import com.code.aon.fiscal.vat.Vat;
import com.code.aon.ui.common.serialize.SerializableListDataModel;

public class VatReportTypeBreakdown implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private VatReportType vatReportType;
	private Map<Double,VatBreakdown> map;
	private double base;
	private double quota;
	private DataModel model;

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
			map = new TreeMap<Double, VatBreakdown>(); 
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
			vb.setPercent(percent);
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
	
	public DataModel getModel() {
		if (model == null) {
			List<VatBreakdown> ret = new LinkedList<VatBreakdown>();
			ret.addAll( getMap().values());
			model = new SerializableListDataModel(ret);
		}
		return model;
	}
}
