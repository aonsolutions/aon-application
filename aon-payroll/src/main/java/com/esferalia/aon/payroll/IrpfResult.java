package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.IrpfResultDB;

@Entity
@Table(name="irpf_result")
public class IrpfResult extends IrpfResultDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	

}
