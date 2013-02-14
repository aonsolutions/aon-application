package com.code.aon.ui.cms.controller.support;

import com.code.aon.ql.Criteria;

public interface IOrderedControllerListener {

	void fireBeforeUseCriteria(Criteria criteria);

}
