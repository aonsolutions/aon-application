package com.code.aon.fiscal;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.annotations.Formula;

import com.code.aon.common.IBlobManager;
import com.code.aon.common.IBlobObject;
import com.code.aon.common.dao.hibernate.HibernateBlobManager;
import com.esferalia.aon.entity.master.FiscalBatchDB;

@Entity
@Table(name="fs_batch")
public class FiscalBatch extends FiscalBatchDB implements IBlobObject {
	
	private static final long serialVersionUID = 1L;
	
	private byte[] data;
	
	private Integer size;

	@Transient
	public boolean isGenerated() {
		return (this.getIssueDate() != null);
	}
	
    @Formula("IFNULL(LENGTH(data),0)")
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
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
