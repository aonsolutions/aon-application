package com.code.aon.registry;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.io.FileUtils;
import org.hibernate.annotations.Formula;

import com.code.aon.common.IAttachment;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.IScopable;
import com.esferalia.aon.entity.master.RegistryAttachmentDB;

@Entity
@Table(name="rattach")
public class RegistryAttachment extends RegistryAttachmentDB implements IAttachment,IScopable {

	private static final long serialVersionUID = 1L;

	private Integer size;

    public RegistryAttachment() {
    	setSecurityLevel( SecurityLevel.OFFICIAL );
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
		return FileUtils.byteCountToDisplaySize(getSize());
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}