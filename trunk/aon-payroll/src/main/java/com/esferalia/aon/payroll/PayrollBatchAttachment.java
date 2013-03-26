package com.esferalia.aon.payroll;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;

import com.code.aon.common.IAttachment;
import com.code.aon.config.IScopable;
import com.esferalia.aon.entity.master.PayrollBatchAttachmentDB;
import com.esferalia.aon.payroll.enumeration.BatchType;



@Entity
@Table(name="payroll_batch_attach")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="source_type")
@DiscriminatorValue(value="true")
public class PayrollBatchAttachment extends PayrollBatchAttachmentDB implements IAttachment, IScopable {

	private static final long serialVersionUID = 1L;

	private BatchType sourceType;
	
	private Integer size;
	
	@Transient
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
	
	@Column(name="source_type", insertable=false, updatable=false)
	public BatchType getSourceType() {
		return sourceType;
	}
	public void setSourceType(BatchType sourceType) {
		this.sourceType = sourceType;
	}

}