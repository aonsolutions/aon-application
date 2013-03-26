package com.esferalia.aon.payroll;


import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.AgreementDB;

@Entity
@Table(name="agreement")
@Heritable
public class Agreement extends AgreementDB {

	private static final long serialVersionUID = 1L;

}
