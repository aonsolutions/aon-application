package com.code.aon.ui.common;

import com.code.aon.common.ILogger;
import com.code.aon.ui.util.AonUtil;

/**
 * The Class DefaultLogger.
 */
public class FacesMessageLogger implements ILogger {

	@Override
	public void error(String msg) {
		AonUtil.addErrorMessage(msg);
	}

	@Override
	public void info(String msg) {
		AonUtil.addInfoMessage(msg);
	}

	@Override
	public void warn(String msg) {
		AonUtil.addWarningMessage(msg);
	}

}
