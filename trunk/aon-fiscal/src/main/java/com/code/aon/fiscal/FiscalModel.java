package com.code.aon.fiscal;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.fiscal.enumeration.FiscalModelStatus;
import com.esferalia.aon.entity.master.FiscalModelDB;

@Entity
@Table(name="fs_model")
public class FiscalModel extends FiscalModelDB {
	
	private static final long serialVersionUID = 1L;
	
	public FiscalModel() {
		setStatus(FiscalModelStatus.PENDING);
		setReplacement(false);
		setComplementary(false);
		setSecurityLevel(SecurityLevel.OFFICIAL);
	}
	
	@Transient
	public boolean isReadOnly() {
		return (getStatus() != FiscalModelStatus.PENDING);
	}

	@Transient
	public boolean isPending() {
		return (getStatus() == FiscalModelStatus.PENDING);
	}

	@Transient
	public boolean isFinished() {
		return (getStatus() == FiscalModelStatus.FINISHED);
	}

	@Transient
	public boolean isExtraDeclaration() {
		return (isReplacement() || isComplementary());
	}
}
