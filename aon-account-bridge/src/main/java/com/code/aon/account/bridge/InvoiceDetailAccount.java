package com.code.aon.account.bridge;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.account.IAccount;
import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.InvoiceDetailAccountDB;

@Entity
@Table(name="invoice_detail_account")
public class InvoiceDetailAccount extends InvoiceDetailAccountDB implements IAccount {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}