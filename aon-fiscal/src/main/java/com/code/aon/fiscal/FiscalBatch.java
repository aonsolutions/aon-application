package com.code.aon.fiscal;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.FiscalBatchDB;

@Entity
@Table(name="fs_batch")
public class FiscalBatch extends FiscalBatchDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public boolean isGenerated() {
		return (this.getIssueDate() != null);
	}
	
}
