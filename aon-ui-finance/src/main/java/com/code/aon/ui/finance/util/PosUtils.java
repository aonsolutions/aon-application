package com.code.aon.ui.finance.util;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Pos;
import com.code.aon.finance.PosShift;
import com.code.aon.finance.enumeration.Shift;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PosUtils {

	public static boolean isUserPosShiftOpened() {
		try {
			IManagerBean posShiftBean = BeanManager.getManagerBean(PosShift.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_USERNAME), UserUtils.getInstance().getLoggedUser().getLogin());
			criteria.addNullExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_END_TIME));
			return (posShiftBean.getCount(criteria) > 0);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage());
		}
	}

	public static PosShift getUserPosShift() {
		try {
			IManagerBean posShiftBean = BeanManager.getManagerBean(PosShift.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_USERNAME), UserUtils.getInstance().getLoggedUser().getLogin());
			criteria.addNullExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_END_TIME));
			for (ITransferObject ito : posShiftBean.getList(criteria)) {
				return (PosShift)ito;
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage());
		}

		return null;
	}

	public static boolean isPosShiftAlreadyOpened(Pos pos, Shift shift) {
		try {
			IManagerBean posShiftBean = BeanManager.getManagerBean(PosShift.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_POS_ID), pos.getId());
			criteria.addEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_SHIFT), shift);
			criteria.addNullExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_END_TIME));
			return (posShiftBean.getCount(criteria) > 0);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage());
		}
	}

}
