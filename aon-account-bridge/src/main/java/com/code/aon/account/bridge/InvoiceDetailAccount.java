package com.code.aon.account.bridge;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.account.IAccount;
import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.InvoiceDetailAccountDB;

@Entity
@Table(name="invoice_detail_account")
public class InvoiceDetailAccount extends InvoiceDetailAccountDB implements IAccount {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}