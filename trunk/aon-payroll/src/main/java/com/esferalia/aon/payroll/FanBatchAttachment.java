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
@DiscriminatorValue(value="1")
public class FanBatchAttachment extends PayrollBatchAttachment {

	private static final long serialVersionUID = 1L;
	
	@Transient
	public FanBatch getFanBatch() {
		return obtainFanBatch(getSourceBatch());
	}
	public void setFanBatch(LeaveBatch to) {
		setSourceBatch(to.getId());
	}

	private FanBatch obtainFanBatch(Integer sourceBatch) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(FanBatch.class);
			return (FanBatch) bean.get(sourceBatch);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}
}