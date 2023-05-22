package com.esferalia.aon.payroll;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;

@Entity
@DiscriminatorValue(value="1")
public class ContrataBatchAttachment extends SepeBatchAttachment {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Transient
	public ContrataBatch getContrataBatch() {
		return obtainContrataBatch(getSourceBatch());
	}
	public void setContrataBatch(ContrataBatch to) {
		setSourceBatch(to.getId());
	}

	private ContrataBatch obtainContrataBatch(Integer sourceBatch) {
		if(sourceBatch!=null){
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContrataBatch.class);
				return (ContrataBatch) bean.get(sourceBatch);
			} catch (ManagerBeanException e) {
			}
		}
		return null;
	}
}