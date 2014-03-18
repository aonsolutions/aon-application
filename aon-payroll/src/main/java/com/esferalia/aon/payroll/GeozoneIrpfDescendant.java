package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.GeozoneIrpfDescendantDB;

@Entity
@Table(name="geozone_irpf_descendant")
public class GeozoneIrpfDescendant extends GeozoneIrpfDescendantDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
