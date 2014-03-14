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

import com.code.aon.common.AonVersion;
import com.code.aon.common.IAttachment;
import com.code.aon.config.IScopable;
import com.esferalia.aon.entity.master.PayrollBatchAttachmentDB;
import com.esferalia.aon.payroll.enumeration.PayrollBatchType;



@Entity
@Table(name="payroll_batch_attach")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="source_type")
@DiscriminatorValue(value="true")
public class PayrollBatchAttachment extends PayrollBatchAttachmentDB implements IAttachment, IScopable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private PayrollBatchType sourceType;
	
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
	public PayrollBatchType getSourceType() {
		return sourceType;
	}
	public void setSourceType(PayrollBatchType sourceType) {
		this.sourceType = sourceType;
	}

}