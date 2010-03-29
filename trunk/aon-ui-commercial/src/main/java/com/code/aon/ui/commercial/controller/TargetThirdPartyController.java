package com.code.aon.ui.commercial.controller;

import com.code.aon.commercial.Target;
import com.code.aon.commercial.TargetThirdParty;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;

public class TargetThirdPartyController extends LinesController {

	public void thirdPartyData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Target thirdParty = (Target)event.getNewValue();
			((TargetThirdParty)getTo()).setThirdParty(thirdParty);
		}
	}

}
