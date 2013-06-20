package com.esferalia.aon.payroll;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;

@Entity
@DiscriminatorValue(value="0")
public class LeaveBatchAttachment extends PayrollBatchAttachment {

	private static final long serialVersionUID = 1L;

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