package com.code.aon.purchase;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.purchase.enumeration.ProposalStatus;
import com.esferalia.aon.entity.master.ProposalDB;

@Entity
@Table(name="proposal")
public class Proposal extends ProposalDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Set<ProposalDetail> lines = new HashSet<ProposalDetail>();
	
	@OneToMany(mappedBy = "proposal", cascade={CascadeType.REMOVE})
	public Set<ProposalDetail> getLines() {
		return this.lines;
	}
	public void setLines(Set<ProposalDetail> lines) {
		this.lines = lines;
	}
	
	@Transient
	public boolean isPending(){
		return this.getStatus()==ProposalStatus.PENDING;
	}

}
