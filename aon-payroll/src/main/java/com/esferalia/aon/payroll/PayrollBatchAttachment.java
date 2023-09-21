package com.esferalia.aon.payroll;

import static com.code.aon.common.BlobObjectAction.READ;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.annotations.Formula;

import com.code.aon.AonVersion;
import com.code.aon.common.BlobObjectAction;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IBlobManager;
import com.code.aon.common.IBlobObject;
import com.code.aon.common.dao.hibernate.HibernateBlobManager;
import com.code.aon.config.IScopable;
import com.code.aon.google.apis.DriveUtils;
import com.esferalia.aon.entity.master.PayrollBatchAttachmentDB;
import com.esferalia.aon.payroll.enumeration.PayrollBatchType;



@Entity
@Table(name="payroll_batch_attach")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="source_type")
@DiscriminatorValue(value="true")
public class PayrollBatchAttachment extends PayrollBatchAttachmentDB implements IAttachment, IScopable, IBlobObject {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private PayrollBatchType sourceType;
	
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
	
	@Column(name="source_type", insertable=false, updatable=false)
	public PayrollBatchType getSourceType() {
		return sourceType;
	}
	public void setSourceType(PayrollBatchType sourceType) {
		this.sourceType = sourceType;
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
	public String getAonType() {
		return "payroll";
	}
	@Override
	public void setAonType(String aonType) {
		
	}
	
}