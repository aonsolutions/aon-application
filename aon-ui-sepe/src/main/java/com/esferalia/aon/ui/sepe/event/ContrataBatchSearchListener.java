package com.esferalia.aon.ui.sepe.event;

import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

public class ContrataBatchSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Date startDate;
	private Date endDate;
	private Enterprise enterprise;
	private FileStatus[] fileStatus;
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public FileStatus[] getFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(FileStatus[] fileStatus) {
		this.fileStatus = fileStatus;
	}

	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		setStartDate(DateUtils.addDays(new Date(), -10));
		setEnterprise((Enterprise) BeanManager.getManagerBean(Enterprise.class).createNewTo());
		FileStatus[] defaultFileStatus = {FileStatus.GENERATED, FileStatus.PENDING, FileStatus.DENIED};
		setFileStatus(defaultFileStatus);
	}

	@Override
	protected void completeCriteria(Criteria criteria)
			throws ManagerBeanException, ExpressionException {
		super.completeCriteria(criteria);
		SEPEUtils.getInstance().completeChildDomainCriteria(criteria, "ContrataBatch.domain", false);
		if(getStartDate()!=null){
			setStartDate(DateUtils.setHours(getStartDate(), 23));
			setStartDate(DateUtils.setMinutes(getStartDate(), 59));
			setStartDate(DateUtils.setSeconds(getStartDate(), 59));
			String alias = getController().resolveAlias(IEntityAlias.CONTRATA_BATCH_DATE);
			getController().getCriteria().addGreaterThanOrEqualExpression(alias, getStartDate());
		}
		if(getEndDate()!=null){
			setEndDate(DateUtils.setHours(getEndDate(), 23));
			setEndDate(DateUtils.setMinutes(getEndDate(), 59));
			setEndDate(DateUtils.setSeconds(getEndDate(), 59));
			String alias = getController().resolveAlias(IEntityAlias.CONTRATA_BATCH_DATE);
			getController().getCriteria().addLessThanOrEqualExpression(alias, getEndDate());
		}
		if (!ArrayUtils.isEmpty(getFileStatus())) {
			String status = getController().resolveAlias(IEntityAlias.CONTRATA_BATCH_STATUS);
			addEnumToCriteria(criteria, status, getFileStatus());
		}
	}


}