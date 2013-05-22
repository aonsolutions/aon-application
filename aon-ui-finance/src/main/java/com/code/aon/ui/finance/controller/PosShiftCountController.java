package com.code.aon.ui.finance.controller;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.PosShiftCount;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;

public class PosShiftCountController extends BasicController {

	@Override
	protected ITransferObject add() throws ManagerBeanException {
		PosShiftCount to = (PosShiftCount)getTo();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getFieldName(IEntityAlias.POS_SHIFT_COUNT_POS_SHIFT_ID), to.getPosShift().getId());
		criteria.addEqualExpression(getFieldName(IEntityAlias.POS_SHIFT_COUNT_PAY_METHOD_ID), to.getPayMethod().getId());
		for (ITransferObject ito : getManagerBean().getList(criteria)) {
			PosShiftCount posShiftCount = (PosShiftCount)ito;
			posShiftCount.setAmount(CommonUtil.round(posShiftCount.getAmount() + to.getAmount()));
			if (posShiftCount.getAmount() != 0) {
				posShiftCount = (PosShiftCount)getManagerBean().update(posShiftCount);
			} else {
				getManagerBean().remove(posShiftCount);
			}
			return posShiftCount;
		}
		return super.add();
	}

}
