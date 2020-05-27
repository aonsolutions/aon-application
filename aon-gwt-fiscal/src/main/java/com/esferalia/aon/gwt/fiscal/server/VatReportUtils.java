package com.esferalia.aon.gwt.fiscal.server;

import java.text.SimpleDateFormat;

import com.esferalia.aon.occam.api.model.AccountingReportParams;

public class VatReportUtils {

	private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");

	public static String toString(AccountingReportParams params) {
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
	
}
