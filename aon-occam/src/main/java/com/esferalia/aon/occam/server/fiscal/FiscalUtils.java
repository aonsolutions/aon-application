package com.esferalia.aon.occam.server.fiscal;

import java.util.Calendar;
import java.util.Date;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;

public class FiscalUtils {
	
	public static Date getPeriodStart( IFiscalModel fiscalModel) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, fiscalModel.getYear());
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MONTH, fiscalModel.getPeriod().getStartMonth());
		return c.getTime();
	}
	
	public static Date getPeriodEnd( IFiscalModel fiscalModel) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, fiscalModel.getPeriod().getDueMonth() + 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.YEAR, fiscalModel.getYear());
		c.add(Calendar.DAY_OF_MONTH, -1);
		return c.getTime();
	}

}
