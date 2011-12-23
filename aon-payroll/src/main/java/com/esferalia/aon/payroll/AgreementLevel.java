package com.esferalia.aon.payroll;


import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.AgreementLevelDB;

@Entity
@Table(name="agreement_level")
public class AgreementLevel extends AgreementLevelDB {

	private static final long serialVersionUID = 1L;

}
