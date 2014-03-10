package com.code.aon.account.bridge;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.account.IAccount;
import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.InvoiceTaxAccountDB;

@Entity
@Table(name="invoice_tax_account")
public class InvoiceTaxAccount extends InvoiceTaxAccountDB implements IAccount {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}