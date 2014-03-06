package com.code.aon.common.event;

import java.util.EventObject;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.ql.Criteria;

public class FinderBeanEvent extends EventObject {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Class<ITransferObject> entityClass; 

	public FinderBeanEvent(Criteria criteria, Class<ITransferObject> entityClass) {
		super(criteria);
		this.entityClass = entityClass;
	}

	public Criteria getCriteria() {
		return (Criteria) super.getSource();
	}

	public Class<ITransferObject> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<ITransferObject> entityClass) {
		this.entityClass = entityClass;
	}
	
	
}
