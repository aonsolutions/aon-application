package com.esferalia.aon.payroll;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.AgreementDB;

@Entity
@Table(name="agreement")
@Heritable
public class Agreement extends AgreementDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
