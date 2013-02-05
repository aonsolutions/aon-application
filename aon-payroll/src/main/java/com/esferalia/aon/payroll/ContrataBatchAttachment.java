package com.esferalia.aon.payroll;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;

@Entity
@Table(name="payroll_batch_attach")
@DiscriminatorValue(value="4")
public class ContrataBatchAttachment extends PayrollBatchAttachment {

	private static final long serialVersionUID = 1L;
	
	@Transient
	public ContrataBatch getContrataBatch() {
		return obtainContrataBatch(getSourceBatch());
	}
	public void setContrataBatch(ContrataBatch to) {
		setSourceBatch(to.getId());
	}

	private ContrataBatch obtainContrataBatch(Integer sourceBatch) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContrataBatch.class);
			return (ContrataBatch) bean.get(sourceBatch);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}
}