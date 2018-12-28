package com.code.aon.data;

import static com.code.aon.common.BlobObjectAction.READ;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Formula;

import com.code.aon.AonVersion;
import com.code.aon.common.BlobObjectAction;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IBlobManager;
import com.code.aon.common.IBlobObject;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.dao.hibernate.BlobEntityListener;
import com.code.aon.common.dao.hibernate.HibernateBlobManager;
import com.code.aon.data.enumeration.DataAttachmentSource;
import com.code.aon.data.enumeration.DataAttachmentType;
import com.code.aon.google.apis.DriveUtils;
import com.esferalia.aon.entity.master.DataAttachmentDB;

@Entity
@Table(name="data_attach")
@Heritable(force=true)
@EntityListeners(BlobEntityListener.class)
public class DataAttachment extends DataAttachmentDB implements IAttachment, IBlobObject, IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private byte[] data;

	private Integer size;

	@Transient
	public boolean isSourceInvoice() {
		return getSource() == DataAttachmentSource.INVOICE;
	}
	@Transient
	public boolean isSourceFbatch() {
		return getSource() == DataAttachmentSource.FBATCH;
	}
	@Transient
	public boolean isSourceProduction() {
		return getSource() == DataAttachmentSource.PRODUCTION;
	}
 
	@Transient
	public boolean isTypeRequest() {
		return getType() == DataAttachmentType.REQUEST;
	}
	@Transient
	public boolean isTypeResponseOk() {
		return getType() == DataAttachmentType.RESPONSE_OK;
	}
	@Transient
	public boolean isTypeResponseError() {
		return getType() == DataAttachmentType.RESPONSE_ERROR;
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

	@Formula("LENGTH(data)")
	public Integer getSize() {
		return size;	
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	
	@Transient
	public String getSizeToDisplay() {
		return FileUtils.byteCountToDisplaySize(getSize()!=null?getSize():0);
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
	@Transient
	public String[] getBlobProperties() {
		return DATA_BLOB_PROPERTIES;
	}

	@Override
	public void reset() {
		this.data = null;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.appendSuper(super.hashCode())
			.append(size)
			.toHashCode();
	}
	@Override
	public String getAonType() {
		return "data";
	}
	@Override
	public void setAonType(String aonType) {
		
	}   	

}