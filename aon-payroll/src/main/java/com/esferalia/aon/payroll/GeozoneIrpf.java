package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.entity.master.GeozoneIrpfDB;

@Entity
@Table(name="geozone_irpf")
public class GeozoneIrpf extends GeozoneIrpfDB {
	
	private static final long serialVersionUID = 1L;

	@Transient
	public Integer getYear(){
		return CommonUtil.getYear(getStartDate());
	}
	
}
