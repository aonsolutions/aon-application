package com.esferalia.aon.payroll;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.EnterpriseActivityDB;

@Entity
@Table(name="enterprise_activity")
public class EnterpriseActivity extends EnterpriseActivityDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Set<EnterpriseCCC> cccs = new HashSet<EnterpriseCCC>();
	
	@OneToMany(mappedBy = "activity", cascade={CascadeType.REMOVE})
	public Set<EnterpriseCCC> getCccs() {
		return cccs;
	}
	public void setCccs(Set<EnterpriseCCC> cccs) {
		this.cccs = cccs;
	}
	

}
