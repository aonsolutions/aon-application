package com.code.aon.fiscal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.fiscal.enumeration.Mod347Status;
import com.esferalia.aon.entity.master.Mod347DB;

@Entity
@Table(name="fs_mod347")
public class Mod347 extends Mod347DB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public static final Double MINIMUM_AMOUNT = 3005.06;
	
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
