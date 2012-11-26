package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.IrpfDataDescendientsDB;

@Entity
@Table(name="irpf_data_descendients")
public class IrpfDataDescendients extends IrpfDataDescendientsDB {
	
	private static final long serialVersionUID = 511613164148322475L;

}
