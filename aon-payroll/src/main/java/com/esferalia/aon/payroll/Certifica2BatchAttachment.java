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
@DiscriminatorValue(value="3")
public class Certifica2BatchAttachment extends PayrollBatchAttachment {

	private static final long serialVersionUID = 1L;
	
	@Transient
	public Certifica2Batch getCertifica2Batch() {
		return obtainCertifica2Batch(getSourceBatch());
	}
	public void setCertifica2Batch(Certifica2Batch to) {
		setSourceBatch(to.getId());
	}

	private Certifica2Batch obtainCertifica2Batch(Integer sourceBatch) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Certifica2Batch.class);
			return (Certifica2Batch) bean.get(sourceBatch);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}
}