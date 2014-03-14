package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.IrpfResultDB;

@Entity
@Table(name="irpf_result")
public class IrpfResult extends IrpfResultDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	

}
