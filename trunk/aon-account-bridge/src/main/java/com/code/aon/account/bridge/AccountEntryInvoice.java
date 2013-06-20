package com.code.aon.account.bridge;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.AccountEntryInvoiceDB;

@Entity
@Table(name="account_entry_invoice")
public class AccountEntryInvoice extends AccountEntryInvoiceDB {

	private static final long serialVersionUID = 1L;

}