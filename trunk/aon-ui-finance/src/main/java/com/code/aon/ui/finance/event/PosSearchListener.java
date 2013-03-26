package com.code.aon.ui.finance.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PosSearchListener extends ControllerSearchListener {

	private WorkPlace workPlace;

	public WorkPlace getWorkPlace() {
		return workPlace;
	}
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setWorkPlace((WorkPlace)BeanManager.getManagerBean(WorkPlace.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getWorkPlace() != null && getWorkPlace().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.POS_WORK_PLACE_ID), getWorkPlace().getId());			
		} else {
			CompanyCollectionsController companyCollections = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			if (companyCollections.getCurrentUserWorkPlacesCount() > 0) {
				criteria.addInExpression(getFieldName(IEntityAlias.POS_WORK_PLACE_ID), companyCollections.getCurrentUserWorkPlacesIds());
			}
		}
	}

}