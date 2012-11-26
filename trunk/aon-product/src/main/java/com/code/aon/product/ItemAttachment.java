package com.code.aon.product;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;

import com.code.aon.common.IAttachment;
import com.esferalia.aon.entity.master.ItemAttachmentDB;

@Entity
@Table(name="iattach")
public class ItemAttachment extends ItemAttachmentDB implements IAttachment, Cloneable {

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