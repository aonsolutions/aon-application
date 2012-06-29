package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.FinancePosDB;

@Entity
@Table(name="finance_pos")
public class FinancePos extends FinancePosDB {

	private static final long serialVersionUID = 1L;

}
