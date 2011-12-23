package com.code.aon.fiscal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Mod347Status;
import com.esferalia.aon.entity.master.Mod347DB;

@Entity
@Table(name="fs_mod347")
public class Mod347 extends Mod347DB {
	
	private static final long serialVersionUID = 1L;

	public static final Double MINIMUM_AMOUNT = 3005.60;
	
	private Double minimumAmount;
	private boolean generateLines;
	
    @Transient
	public boolean isFinished() {
		return getStatus() == Mod347Status.FINISHED;
	}
	
	@Transient
	public boolean isExtraDeclaration() {
		return (isComplementary() || isReplacement() );
	}

    @Transient
    public Double getMinimumAmount() {
		return minimumAmount;
	}
	public void setMinimumAmount(Double minimumAmount) {
		this.minimumAmount = minimumAmount;
	}

    @Transient
	public boolean isGenerateLines() {
		return generateLines;
	}
	public void setGenerateLines(boolean generateLines) {
		this.generateLines = generateLines;
	}

}
