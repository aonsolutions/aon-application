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
@DiscriminatorValue(value="2")
public class ContractBatchAttachment extends PayrollBatchAttachment {

	private static final long serialVersionUID = 1L;
	
	@Transient
	public ContractBatch getContractBatch() {
		return obtainContractBatch(getSourceBatch());
	}
	public void setContractBatch(ContractBatch to) {
		setSourceBatch(to.getId());
	}

	private ContractBatch obtainContractBatch(Integer sourceBatch) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractBatch.class);
			return (ContractBatch) bean.get(sourceBatch);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}
}