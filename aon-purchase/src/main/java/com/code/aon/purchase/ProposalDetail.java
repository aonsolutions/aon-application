package com.code.aon.purchase;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ProposalDetailDB;

@Entity
@Table(name="proposal_detail")
public class ProposalDetail extends ProposalDetailDB {

	private static final long serialVersionUID = 1L;

}
