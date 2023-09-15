package com.esferalia.aon.payroll;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.fiscal.enumeration.RetentionRegime;
import com.code.aon.fiscal.enumeration.VatRegime;
import com.esferalia.aon.entity.master.EnterpriseActivityDB;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;

@Entity
@Table(name="enterprise_activity")
public class EnterpriseActivity extends EnterpriseActivityDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Set<EnterpriseCCC> cccs = new HashSet<EnterpriseCCC>();

	public EnterpriseActivity() {
		setType(SSRegimeType.GENERAL);
		setVatRegime(VatRegime.GENERAL);
		setRetentionRegime(RetentionRegime.NORMAL_DIRECT_EVALUATION);
	}

	@OneToMany(mappedBy = "activity", cascade={CascadeType.REMOVE})
	public Set<EnterpriseCCC> getCccs() {
		return cccs;
	}
	public void setCccs(Set<EnterpriseCCC> cccs) {
		this.cccs = cccs;
	}
	

}
