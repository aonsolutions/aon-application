package com.code.aon.audit;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ActionDeniedDB;

@Entity
@Table(name="action_denied")
public class ActionDenied extends ActionDeniedDB {

	private static final long serialVersionUID = 1L;
	
}
