package com.esferalia.aon.occam.impl.jooq.dao.stat;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.FeeProperties;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.stat.fee.IFeeChartTypeVisitor;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.impl.jooq.dao.FeeDAO;
import com.esferalia.aon.watson.server.AonDateUtils;

public class FeeChartTypeVisitor implements IFeeChartTypeVisitor {

	private static final String CATEGORY_STR = "category";
	private static final String CUSTOMER_STR = "customer";
	private static final String SELLER_STR = "seller";
	private static final String WORKPLACE_STR = "workplace";
	private static final String PERIOD_STR = "period";

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
		
		LinkedList<Fee> list = FeeDAO.getFeeStream(ctx, f -> getFeeFilter(from, to, ctx.getDomainId(), params.getFilterMap(), f))
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


	public Filter getFeeFilter(Date from, Date to, Integer domainId,
			HashMap<String, String[]> filterMap, FeeProperties f) {
		Filter filter = f.getFinalDateProperty().ge(AonDateUtils.toSql(from))
				.or(f.getFinalDateProperty().isNull())
			.and(f.getBillingDateProperty().lt(AonDateUtils.toSql(to)))
			.and(f.getDomainProperty().eq(domainId));
		
		if(filterMap.containsKey(CATEGORY_STR)){
			Filter fcategory = f.getCategoryProperty().eq(Integer.parseInt(filterMap.get(CATEGORY_STR)[0])); 
			for(Integer i = 1; i < filterMap.get(CATEGORY_STR).length ; i++){
				fcategory = fcategory.or(f.getCategoryProperty().eq(Integer.parseInt(filterMap.get(CATEGORY_STR)[i])));
			}
			filter = filter.and(fcategory);
		}
		
		if(filterMap.containsKey(CUSTOMER_STR)){
			Filter fcustomer = f.getCustomerProperty().eq(Integer.parseInt(filterMap.get(CUSTOMER_STR)[0])); 
			for(Integer i = 1; i < filterMap.get(CUSTOMER_STR).length ; i++){
				fcustomer = fcustomer.or(f.getCustomerProperty().eq(Integer.parseInt(filterMap.get(CUSTOMER_STR)[i])));
			}
			filter = filter.and(fcustomer);
		}
		
		if(filterMap.containsKey(SELLER_STR)){
			Filter fseller = f.getSellerProperty().eq(Integer.parseInt(filterMap.get(SELLER_STR)[0])); 
			for(Integer i = 1; i < filterMap.get(SELLER_STR).length ; i++){
				fseller = fseller.or(f.getSellerProperty().eq(Integer.parseInt(filterMap.get(SELLER_STR)[i])));
			}
			filter = filter.and(fseller);
		}
		
		if(filterMap.containsKey(WORKPLACE_STR)){
			Filter fworkplace = f.getWorkplaceProperty().eq(Integer.parseInt(filterMap.get(WORKPLACE_STR)[0])); 
			for(Integer i = 1; i < filterMap.get(WORKPLACE_STR).length ; i++){
				fworkplace = fworkplace.or(f.getWorkplaceProperty().eq(Integer.parseInt(filterMap.get(WORKPLACE_STR)[i])));
			}
			filter = filter.and(fworkplace);
		}
		
		if(filterMap.containsKey(PERIOD_STR)){
			Filter fperiod = f.getPeriodProperty().eq((short) Integer.parseInt(filterMap.get(PERIOD_STR)[0])); 
			for(Integer i = 1; i < filterMap.get(PERIOD_STR).length ; i++){
				fperiod = fperiod.or(f.getPeriodProperty().eq((short) Integer.parseInt(filterMap.get(PERIOD_STR)[i])));
			}
			filter = filter.and(fperiod);
		}
		return filter;
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
