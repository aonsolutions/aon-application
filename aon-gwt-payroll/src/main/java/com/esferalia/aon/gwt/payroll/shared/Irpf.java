package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;


public class Irpf implements Serializable {
	
	

	public static class IrpfData implements Serializable{
		
		private Integer id;
		
		public Integer getId() {
			return id;
		}
		
		public void setId(Integer id) {
			this.id = id;
		}
		
	}

	public static class IrpfResult implements Serializable{
		
		private Integer id;
		private Date effectiveDate;
		
		public Integer getId() {
			return id;
		}
		
		public void setId(Integer id) {
			this.id = id;
		}
		
		public Date getEffectiveDate() {
			return effectiveDate;
		}
		
		public void setEffectiveDate(Date effectiveDate) {
			this.effectiveDate = effectiveDate;
		}
		
	}

	public static class IrpfRegularization implements Serializable{
		
		private Integer id;
		
		public Integer getId() {
			return id;
		}
		
		public void setId(Integer id) {
			this.id = id;
		}
	}

	private IrpfData irpfData;
	private IrpfResult irpfResult;
	private IrpfRegularization irpfRegularization;
	
	
	public IrpfResult getIrpfResult() {
		return irpfResult;
	}
	
	public void setIrpfResult(IrpfResult irpfResult) {
		this.irpfResult = irpfResult;
	}
	
	public IrpfData getIrpfData() {
		return irpfData;
	}
	
	public void setIrpfData(IrpfData irpfData) {
		this.irpfData = irpfData;
	}
	
	public IrpfRegularization getIrpfRegularization() {
		return irpfRegularization;
	}
	
	public void setIrpfRegularization(IrpfRegularization irpfRegularization) {
		this.irpfRegularization = irpfRegularization;
	}
	
	
}
