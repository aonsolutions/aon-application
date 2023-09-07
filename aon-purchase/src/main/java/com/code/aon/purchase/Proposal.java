package com.code.aon.purchase;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.config.IScopable;
import com.code.aon.purchase.enumeration.ProposalStatus;
import com.esferalia.aon.entity.master.ProposalDB;

@Entity
@Table(name="proposal")
public class Proposal extends ProposalDB implements IScopable, IAuditable {

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
