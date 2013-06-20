package com.esferalia.aon.payroll;


import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;

import com.code.aon.common.IAttachment;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.IScopable;
import com.esferalia.aon.entity.master.ContractAttachmentDB;

@Entity
@Table(name="contract_attach")
public class ContractAttachment extends ContractAttachmentDB implements IAttachment, IScopable {

	private static final long serialVersionUID = 1L;

    private Integer size;

    public ContractAttachment() {
    	setSecurityLevel( SecurityLevel.OFFICIAL);
    }
    
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
	
	@Transient
	public boolean isPdfType(){
		return this.getMimeType()==MimeType.MIME_PDF;
	}

}