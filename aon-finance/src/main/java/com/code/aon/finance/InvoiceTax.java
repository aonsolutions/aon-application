package com.code.aon.finance;


import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.esferalia.aon.entity.master.InvoiceTaxDB;

@Entity
@Table(name="invoice_tax")
public class InvoiceTax extends InvoiceTaxDB implements ITransferObject {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}