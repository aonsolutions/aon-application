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
public class FanBatchAttachment extends PayrollBatchAttachment {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Transient
	public FanBatch getFanBatch() {
		return obtainFanBatch(getSourceBatch());
	}
	public void setFanBatch(FanBatch to) {
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