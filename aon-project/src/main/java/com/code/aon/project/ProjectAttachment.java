package com.code.aon.project;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Formula;

import com.code.aon.AonVersion;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IBlobManager;
import com.code.aon.common.IBlobObject;
import com.code.aon.common.dao.hibernate.HibernateBlobManager;
import com.code.aon.common.enumeration.SecurityLevel;
import com.esferalia.aon.entity.master.ProjectAttachmentDB;

@Entity
@Table(name="project_attach")
public class ProjectAttachment extends ProjectAttachmentDB implements IAttachment, IBlobObject {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Integer size;
	
	private byte[] data;

	public ProjectAttachment() {
		setSecurityLevel(SecurityLevel.OFFICIAL);
		setAttachDate(new Date());
	}

	@Formula("IFNULL(LENGTH(data),0)")
	public Integer getSize() {
    	if ( data == null && getDriveId() != null )
    		return 666;
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
		return getManager().getBlob(this, DATA_PROPERTY);
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
