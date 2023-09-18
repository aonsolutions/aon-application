package com.code.aon.fiscal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.fiscal.enumeration.Mod349Status;
import com.esferalia.aon.entity.master.Mod349DB;

@Entity
@Table(name="fs_mod349")
public class Mod349 extends Mod349DB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean generateLines;
	
    @Transient
	public boolean isFinished() {
		return getStatus() == Mod349Status.FINISHED;
	}
	
	@Transient
	public boolean isExtraDeclaration() {
		return (isComplementary() || isReplacement() );
	}

    @Transient
	public boolean isGenerateLines() {
		return generateLines;
	}
	public void setGenerateLines(boolean generateLines) {
		this.generateLines = generateLines;
	}

}
