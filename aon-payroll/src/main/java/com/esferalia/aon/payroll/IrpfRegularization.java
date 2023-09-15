package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.IrpfRegularizationDB;

@Entity
@Table(name="irpf_regularization")
public class IrpfRegularization extends  IrpfRegularizationDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
