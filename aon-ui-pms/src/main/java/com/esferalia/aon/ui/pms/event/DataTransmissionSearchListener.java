package com.esferalia.aon.ui.pms.event;

import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.data.enumeration.DataAttachmentType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;
import com.esferalia.aon.entity.IEntityAlias;

public class DataTransmissionSearchListener extends ControllerSearchListenerEx {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Date creationDateFrom;
	private Date creationDateTo;
	private DataAttachmentType[] attachmentTypes;

	public Date getCreationDateFrom() {
		return creationDateFrom;
	}
	public void setCreationDateFrom(Date creationDateFrom) {
		this.creationDateFrom = creationDateFrom;
	}

	public Date getCreationDateTo() {
		return creationDateTo;
	}
	public void setCreationDateTo(Date creationDateTo) {
		this.creationDateTo = creationDateTo;
	}

	public DataAttachmentType[] getAttachmentTypes() {
		return attachmentTypes;
	}
	public void setAttachmentTypes(DataAttachmentType[] attachmentTypes) {
		this.attachmentTypes = attachmentTypes;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setCreationDateFrom(null);
		setCreationDateTo(null);
		setAttachmentTypes(null);
	}

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getCreationDateFrom() != null) {
			criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.DATA_ATTACHMENT_CREATION_DATE), getCreationDateFrom());
		}
		if (getCreationDateTo() != null) {
			Date creationDateTo = DateUtils.addMilliseconds(getCreationDateTo(), (int)(DateUtils.MILLIS_PER_DAY - DateUtils.MILLIS_PER_SECOND));
			criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.DATA_ATTACHMENT_CREATION_DATE), creationDateTo);
		}
		if (!ArrayUtils.isEmpty(getAttachmentTypes())) {
			String status = getController().resolveAlias(IEntityAlias.DATA_ATTACHMENT_TYPE);
			addEnumToCriteria(criteria, status, getAttachmentTypes());
		}
	}

}