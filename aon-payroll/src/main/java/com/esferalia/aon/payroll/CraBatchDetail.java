package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.entity.master.CraBatchDetailDB;
import com.esferalia.aon.payroll.util.PayrollUtils;

@Entity
@Table(name="cra_batch_detail")
public class CraBatchDetail extends CraBatchDetailDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Transient
	public String getFullQuoteRegime() throws ManagerBeanException {
		if (this.getCcc()!=null) {
			return PayrollUtils.getInstance().getRegimeCode(this.getCcc()) + this.getCcc().getCcc();
		}
		return null;
	}

}
