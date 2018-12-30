package com.code.aon.project;

import static com.code.aon.common.BlobObjectAction.READ;

import java.io.Serializable;
import java.util.Date;

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
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.dao.hibernate.HibernateBlobManager;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.project.enumeration.ProjectAttachmentType;
import com.esferalia.aon.entity.master.ProjectAttachmentDB;

@Entity
@Table(name="project_attach")
public class ProjectAttachment extends ProjectAttachmentDB implements IAttachment, IBlobObject, IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Integer size;

	private byte[] data;

	public ProjectAttachment() {
		setSecurityLevel(SecurityLevel.OFFICIAL);
		setAttachDate(new Date());
		setAttachType(ProjectAttachmentType.DOCUMENT);
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

	@Transient
	public boolean isDocument() {
		return getAttachType() == ProjectAttachmentType.DOCUMENT;
	}

	@Override
	@Transient
	public String getAonType() {
		return "project";
	}

	@Override
	@Transient
	public void setAonType(String aonType) {

	}

}
