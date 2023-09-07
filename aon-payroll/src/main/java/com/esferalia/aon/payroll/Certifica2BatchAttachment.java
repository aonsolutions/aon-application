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
public class Certifica2BatchAttachment extends SepeBatchAttachment {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Transient
	public Certifica2Batch getCertifica2Batch() {
		return obtainCertifica2Batch(getSourceBatch());
	}
	public void setCertifica2Batch(Certifica2Batch to) {
		setSourceBatch(to.getId());
	}

	private Certifica2Batch obtainCertifica2Batch(Integer sourceBatch) {
		if(sourceBatch!=null){
			try {
				IManagerBean bean = BeanManager.getManagerBean(Certifica2Batch.class);
				return (Certifica2Batch) bean.get(sourceBatch);
			} catch (ManagerBeanException e) {
			}
		}
		return null;
	}
}