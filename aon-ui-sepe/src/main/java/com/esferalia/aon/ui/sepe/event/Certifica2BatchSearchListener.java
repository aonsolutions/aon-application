package com.esferalia.aon.ui.sepe.event;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

public class Certifica2BatchSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Enterprise enterprise;
	private FileStatus fileStatus;
	
	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public FileStatus getFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(FileStatus fileStatus) {
		this.fileStatus = fileStatus;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setEnterprise((Enterprise) BeanManager.getManagerBean(Enterprise.class).createNewTo());
		setFileStatus(FileStatus.PENDING);
	}

	@Override
	protected void completeCriteria(Criteria criteria)
			throws ManagerBeanException, ExpressionException {
		SEPEUtils.getInstance().completeChildDomainCriteria(criteria, "Certifica2Batch.domain", false);
		if(getEnterprise()!=null && getEnterprise().getId()!=null){
			criteria.addEqualExpression(getFieldName(IEntityAlias.CERTIFICA2BATCH_ENTERPRISE_ID), getEnterprise().getId());
		}
		if(getFileStatus()!=null){
			criteria.addEqualExpression(getFieldName(IEntityAlias.CERTIFICA2BATCH_STATUS), getFileStatus());
		}
	}


}