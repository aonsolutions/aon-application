package com.esferalia.aon.occam.server.fiscal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.server.AonDateUtils;

public class FiscalUtils {
	private static final String FILTRO = "Filtro:";
	private static final String ACTIVIDAD = " (Actividad:";
	private static final String HASTA = " (Hasta:";
	private static final String DESDE = " (Desde:";
	private static final String DD_MM_YYYY = "dd/MM/yyyy";

	private FiscalUtils() {
		
	}
	public static Period getMonthPeriod(Date date) {
		if (date == null) return null;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int month = c.get(Calendar.MONTH);
		Period period = null;
		for ( Period per : Period.values()) {
			if (per.getStartMonth() <= month &&
				per.getDueMonth() >= month &&
				per.isMonthPeriod()) {
				period = per;
				break;
			}
		}
		return period;
	}
	public static Period getQuarterPeriod(Date date) {
		if (date == null) return null;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int month = c.get(Calendar.MONTH);
		Period period = null;
		for ( Period per : Period.values()) {
			if (per.getStartMonth() <= month &&
				per.getDueMonth() >= month &&
				per.isQuarterPeriod()) {
				period = per;
				break;
			}
		}
		return period;
	}
	
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
	
	public static String toString(AccountingReportParams params) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DD_MM_YYYY);
		StringBuilder buf = new StringBuilder();
		if (params.getFromDate() != null) {
			buf.append(DESDE);
			buf.append( dateFormatter.format(params.getFromDate()));
			buf.append(")");
		}
		if (params.getToDate() != null) {
			buf.append(HASTA);
			buf.append( dateFormatter.format(params.getToDate()));
			buf.append(")");
		}
		if (params.getRegistry() != null) {
			buf.append(" (Titular:");
			buf.append(params.getRegistry());
			buf.append(")");
		}
		if (params.getActivity() != null) {
			buf.append(ACTIVIDAD);
			buf.append(params.getActivity());
			buf.append(")");
		}
		if (params.getOutput() != null) {
			buf.append(params.getOutput().booleanValue() ?" (Emitidas)":" (Recibidas)");
		}
		if (params.getVatSummaryType() != null) {
			buf.append(" (");
			buf.append(params.getVatSummaryType().getDescription());
			buf.append(")");
		}
		if (params.getPercent() != null) {
			buf.append(" (Porc:");
			buf.append(params.getPercent());
			buf.append(")");
		}
		if (params.getSurcharge()!= null) {
			buf.append(params.getSurcharge().booleanValue()?" (Rec.Equiv. SI)":" (Rec.Equiv. NO)");
		}
		if (params.getFarmerRegime()!= null) {
			buf.append(params.getFarmerRegime().booleanValue()?" (Reg.Agric. SI)":" (Reg.Agric. NO)");
		} 
		if (params.getAccrualRegime()!= null) {
			buf.append(params.getAccrualRegime().booleanValue()?" (Crit.Caja. SI)":" (Crit.Caja. NO)");
		} 
		if (params.getInvestment()!= null) {
			buf.append(params.getInvestment().booleanValue()?" (Bien Inv.)":" (Bien Corr.)");
		} 
		if (params.getService() != null) {
			buf.append(params.getService().booleanValue()?" (Serv. SI)":" (Serv. NO)");
		} 
		return buf.length()>0 ? buf.insert(0,FILTRO).toString():"";
	}
	
	public static String toString(IRPFParams params) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DD_MM_YYYY);
		StringBuilder buf = new StringBuilder();
		if (params.getFromDate() != null) {
			buf.append(DESDE);
			buf.append( dateFormatter.format(params.getFromDate()));
			buf.append(")");
		}
		if (params.getToDate() != null) {
			buf.append(HASTA);
			buf.append( dateFormatter.format(params.getToDate()));
			buf.append(")");
		}
		if (params.getRegistry() != null) {
			buf.append(" (Titular:");
			buf.append(params.getRegistry());
			buf.append(")");
		}
		if (params.getActivity() != null) {
			buf.append(ACTIVIDAD);
			buf.append(params.getActivity());
			buf.append(")");
		}
		if (params.getOutput() != null) {
			buf.append(params.getOutput().booleanValue() ?" (Emitidas)":" (Recibidas)");
		}
		if (params.getWithholdingType() != null) {
			buf.append(" (");
			buf.append(params.getWithholdingType().getAbbreviatedDescription());
			buf.append(")");
		}
		if (params.getPercent() != null) {
			buf.append(" (Porc:");
			buf.append(params.getPercent());
			buf.append(")");
		}
		if (params.getAccrualRegime()!= null) {
			buf.append(params.getAccrualRegime().booleanValue()?" (Crit.Caja. SI)":" (Crit.Caja. NO)");
		} 
		if (params.getInvestment()!= null) {
			buf.append(params.getInvestment().booleanValue()?" (Bien Inv.)":" (Bien Corr.)");
		} 
		if (params.getService() != null) {
			buf.append(params.getService().booleanValue() ?" (Serv. SI)":" (Serv. NO)");
		} 
		return buf.length()>0 ? buf.insert(0,FILTRO).toString():"";
	}

}
