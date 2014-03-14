package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.IrpfDataAscendantsDB;

@Entity
@Table(name="irpf_data_ascendants")
public class IrpfDataAscendants extends IrpfDataAscendantsDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
