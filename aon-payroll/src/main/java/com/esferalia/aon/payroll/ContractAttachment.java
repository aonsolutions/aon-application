package com.esferalia.aon.payroll;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.annotations.Formula;

import com.code.aon.common.IAttachment;
import com.code.aon.common.IBlobManager;
import com.code.aon.common.IBlobObject;
import com.code.aon.common.dao.hibernate.HibernateBlobManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.IScopable;
import com.esferalia.aon.entity.master.ContractAttachmentDB;

@Entity
@Table(name="contract_attach")
public class ContractAttachment extends ContractAttachmentDB implements IAttachment, IScopable, IBlobObject {

	private static final long serialVersionUID = 1L;

    private Integer size;
    
    private byte[] data;

    public ContractAttachment() {
    	setSecurityLevel( SecurityLevel.OFFICIAL);
    }
    
    @Formula("IFNULL(LENGTH(data),0)")
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

    @Transient
	@Column(name="data")
	public byte[] getData() {
    	if ( data != null ) {
    		return data;
    	}
		return getManager().getBlob(this, DATA_PROPERTY);
	}

	public void setData(byte[] data) {
		this.data = data;
		setSize(ArrayUtils.getLength(data));
	}
	
	@Override
	@Transient
	public String[] getBlobProperties() {
		return DATA_BLOB_PROPERTIES;
	}

	@Override
	@Transient
	public Serializable getReference() {
		return getId();
	}

	@Override
	@Transient
	public IBlobManager getManager() {
		return HibernateBlobManager.getInstance();
	}

	@Override
	public void reset() {
		this.data = null;
	}
	
}