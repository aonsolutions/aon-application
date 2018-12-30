package com.esferalia.aon.payroll;


import static com.code.aon.common.BlobObjectAction.READ;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.annotations.Formula;

import com.code.aon.AonVersion;
import com.code.aon.common.BlobObjectAction;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IBlobManager;
import com.code.aon.common.IBlobObject;
import com.code.aon.common.dao.hibernate.HibernateBlobManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.IScopable;
import com.code.aon.google.apis.DriveUtils;
import com.esferalia.aon.entity.master.ContractAttachmentDB;

@Entity
@Table(name="contract_attach")
public class ContractAttachment extends ContractAttachmentDB implements IAttachment, IScopable, IBlobObject {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
		return getManager(READ).getBlob(this, DATA_PROPERTY);
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
	@Transient
	public String getAonType() {
		return "contract";
	}

	@Override
	@Transient
	public void setAonType(String aonType) {

	}

}
