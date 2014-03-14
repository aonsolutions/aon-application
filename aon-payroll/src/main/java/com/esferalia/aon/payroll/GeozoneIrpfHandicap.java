package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.GeozoneIrpfHandicapDB;

@Entity
@Table(name="geozone_irpf_handicap")
public class GeozoneIrpfHandicap extends GeozoneIrpfHandicapDB  {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
