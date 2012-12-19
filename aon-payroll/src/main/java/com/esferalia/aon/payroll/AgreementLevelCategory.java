package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.AgreementLevelCategoryDB;

@Entity
@Table(name="agreement_level_category")
@Heritable
public class AgreementLevelCategory extends AgreementLevelCategoryDB  {

	private static final long serialVersionUID = 1L;


}
