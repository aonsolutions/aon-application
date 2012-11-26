package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;

import com.code.aon.common.IAttachment;
import com.code.aon.config.IScopable;
import com.esferalia.aon.entity.master.FanBatchAttachmentDB;

@Entity
@Table(name="fan_batch_attach")
public class FanBatchAttachment extends FanBatchAttachmentDB implements IAttachment, IScopable {

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