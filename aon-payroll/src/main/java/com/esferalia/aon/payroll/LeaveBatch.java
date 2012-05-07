package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.entity.master.LeaveBatchDB;

@Entity
@Table(name="leave_batch")
public class LeaveBatch extends LeaveBatchDB implements ITransferObject {
	
	private static final long serialVersionUID = 1L;

}
