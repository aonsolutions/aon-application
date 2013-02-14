package com.code.aon.finance;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.BankConceptDB;

@Entity
@Table(name="bank_concept")
@Heritable
public class BankConcept extends BankConceptDB {

	private static final long serialVersionUID = 1L;

}