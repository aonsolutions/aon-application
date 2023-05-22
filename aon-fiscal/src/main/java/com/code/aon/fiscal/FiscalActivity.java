package com.code.aon.fiscal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.fiscal.activity.Sector;
import com.esferalia.aon.entity.master.FiscalActivityDB;

@Entity
@Table(name="fs_activity")
public class FiscalActivity extends FiscalActivityDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Transient
	private Sector sector;

	@Transient
	public Sector getSector() {
		return sector;
	}
	public void setSector(Sector sector) {
		this.sector = sector;
		initializeEpigraph();
	}

	private void initializeEpigraph() {
		setEpigraph(null);
		setDescription(null);
	}
	
}
