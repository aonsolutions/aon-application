package com.esferalia.aon.payroll;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;

@Entity
@DiscriminatorValue(value="0")
public class LeaveBatchAttachment extends PayrollBatchAttachment {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public LeaveBatch getLeaveBatch() {
		return obtainLeaveBatch(getSourceBatch());
	}
	public void setLeaveBatch(LeaveBatch to) {
		setSourceBatch(to.getId());
	}

	private LeaveBatch obtainLeaveBatch(Integer sourceBatch) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(LeaveBatch.class);
			return (LeaveBatch) bean.get(sourceBatch);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}
	
}