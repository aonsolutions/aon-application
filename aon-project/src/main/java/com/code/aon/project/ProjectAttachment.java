package com.code.aon.project;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;

import com.code.aon.common.IAttachment;
import com.esferalia.aon.entity.master.ProjectAttachmentDB;

@Entity
@Table(name="project_attach")
public class ProjectAttachment extends ProjectAttachmentDB implements IAttachment {

	private static final long serialVersionUID = 1L;

	private Integer size;

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
	
}
