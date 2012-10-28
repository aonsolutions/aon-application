package com.code.aon.fiscal;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.esferalia.aon.entity.master.FiscalBatchDB;

@Entity
@Table(name="fs_batch")
public class FiscalBatch extends FiscalBatchDB {
	
	private static final long serialVersionUID = 1L;

	@Transient
	public boolean isGenerated() {
		return (this.getIssueDate() != null);
	}
	
}
