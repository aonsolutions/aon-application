package com.esferalia.aon.ui.pms.util;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.PosShift;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.esferalia.aon.entity.IEntityAlias;

public class PmsUtils {

	public boolean isUserPosOpen() throws ManagerBeanException {
		IManagerBean posShiftBean = BeanManager.getManagerBean(PosShift.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_USER_ID), UserUtils.getInstance().getLoggedUser().getId());
		criteria.addEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_POS_INVOICEABLE), false);
		criteria.addNullExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_END_TIME));
		return (posShiftBean.getCount(criteria) > 0);
	}

}
