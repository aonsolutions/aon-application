package com.esferalia.aon.payroll;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.AgreementLevelDB;

@Entity
@Table(name="agreement_level")
@Heritable
public class AgreementLevel extends AgreementLevelDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
