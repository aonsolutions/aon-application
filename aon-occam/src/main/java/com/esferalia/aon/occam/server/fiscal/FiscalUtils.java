package com.esferalia.aon.occam.server.fiscal;

import java.util.Calendar;
import java.util.Date;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.server.AonDateUtils;

public class FiscalUtils {
	
	public static Date getPeriodStart( int year, Period period) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MONTH, period.getStartMonth());
		return c.getTime();
	}
	
	public static Date getPeriodStart( IFiscalModel fiscalModel) {
		return getPeriodStart(fiscalModel.getYear(),fiscalModel.getPeriod());
	}
	
	public static Date getPeriodEnd( int year, Period period) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, period.getDueMonth() + 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.YEAR, year);
		c.add(Calendar.DAY_OF_MONTH, -1);
		return c.getTime();
	}
	
	public static Date getPeriodEnd( IFiscalModel fiscalModel) {
		return getPeriodEnd(fiscalModel.getYear(),fiscalModel.getPeriod());
	}

	
	public static boolean isInPeriodRange(IFiscalModel mod, Date date ) {
		if (date == null || mod == null) return false;
		Date start = AonDateUtils.truncate( getPeriodStart(mod), Calendar.DAY_OF_MONTH);
		Date end = AonDateUtils.truncate( getPeriodEnd(mod), Calendar.DAY_OF_MONTH);
		return !( date.before(start) || date.after(end));
	}
	
	

}
