package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.IrpfDataAscendantsDB;

@Entity
@Table(name="irpf_data_ascendants")
public class IrpfDataAscendants extends IrpfDataAscendantsDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
