package com.esferalia.aon.occam.impl.jooq.dao.stat;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.stat.fee.IFeeChartTypeVisitor;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.impl.jooq.dao.FeeDAO;
import com.esferalia.aon.occam.impl.jooq.dao.StatDAO;
import com.esferalia.aon.watson.server.AonDateUtils;

public class FeeChartTypeVisitor implements IFeeChartTypeVisitor {

	private AONContext ctx;
	private StatParams params;
	private StatData<String, String, Double> table;	
	
	public FeeChartTypeVisitor(AONContext ctx, StatParams params, StatData<String,String,Double> table) {
		this.ctx = ctx;
		this.params = params;
		this.table = table;
	}

	@Override
	public void visitFeeType() {
		Date from = AonDateUtils.getDate(AonDateUtils.getYear(params.getFrom()),
				AonDateUtils.getMonth(params.getFrom()), 1);
		Date to = AonDateUtils.addDays(AonDateUtils.addYears(from, 1), -1);
		HashMap<String, Double> map = new HashMap<String, Double>();
		
		LinkedList<Fee> list = FeeDAO.getFeeStream(ctx, f -> StatDAO.getFeeFilter(from, to, ctx.getDomainId(), params.getFilterMap(), f))
				.collect(Collectors.toCollection(LinkedList::new));
		
		for(Integer i = 0; i < 12 ; i++){
			Date date = AonDateUtils.addMonths(from, i);
			String monthKey = AonDateUtils.getYear(date)+ "/" 
					+ (AonDateUtils.getMonth(date) < 9 ? "0" : "") 
					+(AonDateUtils.getMonth(date)+ 1);	
			Double d = list.stream()
			.filter(r -> {
				int fromMonth = AonDateUtils.getMonth(date) + 1;
				int billingMonth = AonDateUtils.getMonth(r.getBillingDate()) +1;
				int period = BillingPeriod.values()[r.getPeriod()].getValue();
				Boolean endDate = r.getEndDate() == null || (r.getEndDate() != null && date.compareTo(r.getEndDate()) <= 0);
				if(period == 0) return endDate && fromMonth == billingMonth && 
						AonDateUtils.getYear(r.getBillingDate()) == AonDateUtils.getYear(date);
				return endDate && r.getBillingDate().compareTo(date) <= 0 
						&& (fromMonth % period) == (billingMonth % period);
			})
			.mapToDouble(r -> r.getPrice() * r.getQuantity() * (r.getDiscount() != null ? ((100 - r.getDiscount())/100) :1.0)
				* getPercent(r.getStartDate(), r.getEndDate(), date, BillingPeriod.values()[r.getPeriod()].getValue())).sum();						
			map.put(monthKey, d);
		}
		map.keySet().stream().sorted((n1,n2)-> n1.compareTo(n2))
		.forEach(key -> table.put(key, "Cuotas", map.get(key)));
	}


	public static Double getPercent(Date startDate, Date endDate, Date date, int period){
		if(period == 0 || (startDate.compareTo(date) < 0 && (endDate == null 
				|| AonDateUtils.addMonths(date, period).compareTo(endDate) <= 0 )))
			return 1.0;
		
		int sMonth = AonDateUtils.getMonth(startDate);
		int sYear = AonDateUtils.getYear(startDate);
		int eMonth = endDate != null ? AonDateUtils.getMonth(endDate) : -1;
		int eYear = endDate != null ? AonDateUtils.getYear(endDate) : -1;
		
		Integer d1 = 0;
		Integer d2 = 0;
		for(Integer i = 0; i < period; i++){
			Date d = AonDateUtils.addMonths(date, i);
			int month = AonDateUtils.getMonth(d);
			int year = AonDateUtils.getYear(d);
			Integer lastDay = AonDateUtils.getDay(AonDateUtils.getMonthLastDay(d));
			Integer start = lastDay;
			Integer end = 0;
			if(sMonth == month && sYear == year)
				start = lastDay - (AonDateUtils.getDay(startDate) - 1);
			if(eMonth == month && eYear == year)
				end = lastDay - AonDateUtils.getDay(endDate);
			
			d1 = d1 + start - end;
			d2 = d2 + lastDay;
		}
		return d1.doubleValue()/d2.doubleValue();
	}

}
