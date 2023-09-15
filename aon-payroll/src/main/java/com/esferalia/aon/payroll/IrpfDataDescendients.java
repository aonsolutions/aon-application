package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.IrpfDataDescendientsDB;

@Entity
@Table(name="irpf_data_descendients")
public class IrpfDataDescendients extends IrpfDataDescendientsDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
