package com.esferalia.aon.occam.server.fiscal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.VatParams;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.server.AonDateUtils;

public class FiscalUtils {
	private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	
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
	
	public static String toString(VatParams params) {
		StringBuffer buf = new StringBuffer();
		if (params.getFromDate() != null) {
			buf.append(" (Desde:");
			buf.append( DATE_FORMATTER.format(params.getFromDate()));
			buf.append(")");
		}
		if (params.getToDate() != null) {
			buf.append(" (Hasta:");
			buf.append( DATE_FORMATTER.format(params.getToDate()));
			buf.append(")");
		}
		if (params.getRegistry() != null) {
			buf.append(" (Titular:");
			buf.append(params.getRegistry());
			buf.append(")");
		}
		if (params.getActivity() != null) {
			buf.append(" (Actividad:");
			buf.append(params.getActivity());
			buf.append(")");
		}
		if (params.getOutput() != null) {
			buf.append(params.getOutput() ?" (Emitidas)":" (Recibidas)");
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
			buf.append(params.getSurcharge()?" (Rec.Equiv. SI)":" (Rec.Equiv. NO)");
		}
		
		if (params.getFarmerRegime()!= null) {
			buf.append(params.getFarmerRegime()?" (Reg.Agric. SI)":" (Reg.Agric. NO)");
		} 
		if (params.getAccrualRegime()!= null) {
			buf.append(params.getAccrualRegime()?" (Crit.Caja. SI)":" (Crit.Caja. NO)");
		} 
		if (params.getInvestment()!= null) {
			buf.append(params.getInvestment()?" (Bien Inv.)":" (Bien Corr.)");
		} 
		if (params.getService() != null) {
			buf.append(params.getService() ?" (Serv. SI)":" (Serv. NO)");
		} 
		return buf.length()>0 ? buf.insert(0,"Filtro:").toString():"";
	}
	
	public static String toString(IRPFParams params) {
		StringBuffer buf = new StringBuffer();
		if (params.getFromDate() != null) {
			buf.append(" (Desde:");
			buf.append( DATE_FORMATTER.format(params.getFromDate()));
			buf.append(")");
		}
		if (params.getToDate() != null) {
			buf.append(" (Hasta:");
			buf.append( DATE_FORMATTER.format(params.getToDate()));
			buf.append(")");
		}
		if (params.getRegistry() != null) {
			buf.append(" (Titular:");
			buf.append(params.getRegistry());
			buf.append(")");
		}
		if (params.getActivity() != null) {
			buf.append(" (Actividad:");
			buf.append(params.getActivity());
			buf.append(")");
		}
		if (params.getOutput() != null) {
			buf.append(params.getOutput() ?" (Emitidas)":" (Recibidas)");
		}
		if (params.getWithholdingType() != null) {
			buf.append(" (");
			buf.append(params.getWithholdingType().getDescription());
			buf.append(")");
		}
		if (params.getPercent() != null) {
			buf.append(" (Porc:");
			buf.append(params.getPercent());
			buf.append(")");
		}
		if (params.getAccrualRegime()!= null) {
			buf.append(params.getAccrualRegime()?" (Crit.Caja. SI)":" (Crit.Caja. NO)");
		} 
		if (params.getInvestment()!= null) {
			buf.append(params.getInvestment()?" (Bien Inv.)":" (Bien Corr.)");
		} 
		if (params.getService() != null) {
			buf.append(params.getService() ?" (Serv. SI)":" (Serv. NO)");
		} 
		return buf.length()>0 ? buf.insert(0,"Filtro:").toString():"";
	}

}
