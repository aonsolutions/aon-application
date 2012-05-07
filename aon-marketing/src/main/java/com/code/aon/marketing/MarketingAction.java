package com.code.aon.marketing;


import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.MarketingActionDB;

@Entity
@Table(name="mk_action")
public class MarketingAction extends MarketingActionDB {

	private static final long serialVersionUID = 1L;
	
}