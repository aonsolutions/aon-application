package com.code.aon.product;

import static com.code.aon.common.BlobObjectAction.READ;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Formula;

import com.code.aon.AonVersion;
import com.code.aon.common.BlobObjectAction;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IBlobManager;
import com.code.aon.common.IBlobObject;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.common.dao.hibernate.HibernateBlobManager;
import com.code.aon.google.apis.DriveUtils;
import com.esferalia.aon.entity.master.ItemAttachmentDB;

@Entity
@Table(name="iattach")
@Heritable
public class ItemAttachment extends ItemAttachmentDB implements IAttachment, Cloneable, IBlobObject {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
    private Integer size;
	
	private byte[] data;

    
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
	@Column(name="data")
	public byte[] getData() {
    	if ( data != null ) {
    		return data;
    	}
		return getManager(READ).getBlob(this, DATA_PROPERTY);
	}

	public void setData(byte[] data) {
		this.data = data;
		setSize(ArrayUtils.getLength(data));
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.appendSuper(super.hashCode())
			.append(size)
			.toHashCode();
	}   	
	
	@Override
	@Transient
	public String[] getBlobProperties() {
		return DATA_BLOB_PROPERTIES;
	}

	@Override
	@Transient
	public Serializable getReference( String property ) {
		return (getDriveId()!=null) ? getDriveId() : getId();
	}

	@Override
	@Transient
	public IBlobManager getManager( BlobObjectAction action ) {
		if ( getDriveId() != null ) {
			return DriveUtils.getInstace();
		}
		return HibernateBlobManager.getInstance();
	}

	@Override
	public void reset() {
		this.data = null;
	}

	@Override
	public String getAonType() {
		return "item";
	}

	@Override
	public void setAonType(String aonType) {
		
	}
	
}