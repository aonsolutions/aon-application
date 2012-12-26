package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.SystemDeductionDB;

@Entity
@Table(name="system_deduction")
@Heritable
public class SystemDeduction extends SystemDeductionDB {

	private static final long serialVersionUID = 1L;

}
