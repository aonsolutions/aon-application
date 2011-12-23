package com.code.aon.accounting;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.AccountHelperDB;

@Entity
@Table(name="account_helper")
public class AccountHelper extends AccountHelperDB {

	private static final long serialVersionUID = 1L;

}