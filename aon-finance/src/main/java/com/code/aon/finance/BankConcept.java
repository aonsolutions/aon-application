package com.code.aon.finance;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.account.IAccount;
import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.BankConceptDB;

@Entity
@Table(name="bank_concept")
@Heritable
public class BankConcept extends BankConceptDB implements IAccount {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}