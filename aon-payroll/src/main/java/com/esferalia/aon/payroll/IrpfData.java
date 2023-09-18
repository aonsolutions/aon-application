package com.esferalia.aon.payroll;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.IrpfDataDB;

@Entity
@Table(name="irpf_data")
public class IrpfData extends IrpfDataDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Set<IrpfDataDescendients> descendients = new HashSet<IrpfDataDescendients>();
	private Set<IrpfDataAscendants> ascendants = new HashSet<IrpfDataAscendants>();
	
	@OneToMany(mappedBy = "irpfData", cascade={CascadeType.REMOVE})
	public Set<IrpfDataDescendients> getDescendients() {
		return this.descendients;
	}
	public void setDescendients(Set<IrpfDataDescendients> descendients) {
		this.descendients = descendients;
	}
	
	@OneToMany(mappedBy = "irpfData", cascade={CascadeType.REMOVE})
	public Set<IrpfDataAscendants> getAscendants() {
		return this.ascendants;
	}
	public void setAscendants(Set<IrpfDataAscendants> ascendants) {
		this.ascendants = ascendants;
	}

}
