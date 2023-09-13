package com.esferalia.aon.payroll;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;

@Entity
@DiscriminatorValue(value="2")
public class ContractBatchAttachment extends PayrollBatchAttachment {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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