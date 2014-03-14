package com.esferalia.aon.payroll;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.EnterpriseActivityDB;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.enumeration.contrata.TCHRGCOT;

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
	
	@Transient
	public String getQuoteRegimeCode(){
		if(this.getType()==SSRegimeType.GENERAL){
			return TCHRGCOT.TCHRGCOT_0111.getCode();
		} else if(this.getType()==SSRegimeType.AGRICULTURAL){
			return TCHRGCOT.TCHRGCOT_0613.getCode();
		} else if(this.getType()==SSRegimeType.DOMESTIC_EMPLOYEES){
			return TCHRGCOT.TCHRGCOT_0138.getCode();
		} else if(this.getType()==SSRegimeType.SELF_EMPLOYED){
			return TCHRGCOT.TCHRGCOT_0521.getCode();
		} else if(this.getType()==SSRegimeType.COAL_MINING){
			return TCHRGCOT.TCHRGCOT_0911.getCode();
		} else if(this.getType()==SSRegimeType.SEA_WORKERS){
			return TCHRGCOT.TCHRGCOT_0800.getCode();
		} else if(this.getType()==SSRegimeType.STUDENT_INSURANCE){
			return TCHRGCOT.TCHRGCOT_1911.getCode();
		} else if(this.getType()==SSRegimeType.ARTIST){
			return TCHRGCOT.TCHRGCOT_0112.getCode();
		}
		return null;
	}

}
