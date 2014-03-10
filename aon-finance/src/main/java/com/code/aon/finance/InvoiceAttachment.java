package com.code.aon.finance;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;

import com.code.aon.common.AonVersion;
import com.code.aon.common.IAttachment;
import com.esferalia.aon.entity.master.InvoiceAttachmentDB;

@Entity
@Table(name="invoice_attach")
public class InvoiceAttachment extends InvoiceAttachmentDB implements IAttachment {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    private Integer size;
    
	@Formula("LENGTH(data)")
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}