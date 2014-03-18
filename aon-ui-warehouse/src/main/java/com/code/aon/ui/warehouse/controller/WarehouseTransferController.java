package com.code.aon.ui.warehouse.controller;

import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.warehouse.WarehouseTransfer;

public class WarehouseTransferController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		int number = obtainMaxNumber((String) event.getNewValue());
		if (this.getTo() != null) {
			((WarehouseTransfer) this.getTo()).setNumber(number);
		}
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
		return SeriesNumberUtil.obtainNumber(seriesId, StringUtils.capitalize(this.getBeanName()));
	}

}