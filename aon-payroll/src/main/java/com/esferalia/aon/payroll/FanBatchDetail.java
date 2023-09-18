package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.entity.master.FanBatchDetailDB;
import com.esferalia.aon.payroll.util.PayrollUtils;

@Entity
@Table(name="fan_batch_detail")
public class FanBatchDetail extends FanBatchDetailDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Transient
	public String getFullQuoteRegime() throws ManagerBeanException {
		if (this.getCcc()!=null) {
			return PayrollUtils.getInstance().getRegimeCode(this.getCcc()) + this.getCcc().getCcc();
		}
		return null;
	}

}
