package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ProposalDB;

@Entity
@Table(name="proposal")
public class Proposal extends ProposalDB {

	private static final long serialVersionUID = 1L;

}
