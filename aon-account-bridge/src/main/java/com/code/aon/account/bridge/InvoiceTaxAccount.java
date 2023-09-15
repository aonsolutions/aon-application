package com.code.aon.account.bridge;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.account.IAccount;
import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.InvoiceTaxAccountDB;

@Entity
@Table(name="invoice_tax_account")
public class InvoiceTaxAccount extends InvoiceTaxAccountDB implements IAccount {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}