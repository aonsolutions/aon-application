package com.esferalia.aon.occam.impl.jooq.dao.accounting.amortization;

import static com.esferalia.aon.jooq.tables.AmortizationDetail.AMORTIZATION_DETAIL;

import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.accounting.AmortizationDetail;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AmortizationDetailStatus;
import com.esferalia.aon.occam.api.model.type.AmortizationPeriod;
import com.esferalia.aon.occam.api.model.type.AmortizationPeriod.AmortizationPeriodVisitor;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.amortization.AmortizationDAO.AmortizationDetailFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

class AmortizationManager {

	private boolean brokenPeriod;
	private int brokenPeriodFirstDay;
	private int brokenPeriodFirstMonth;
	private int brokenPeriodLastDay;
	private int brokenPeriodLastMonth;
	
	
	public void deleteDetails(Amortization a) {
		a.clearDetails();
	}

	public void generateDetails(AONContext ctx, Amortization a) {
		ensureParams(ctx, a);
		deletePendingDetails(ctx, a);
		ensureFirstDay(ctx, a);
		
		Date amortizationFirstDay = a.getInitialDate();
		Date amortizationLastDay = null;
		double percent = a.getPercentage();
		double years = AonMathUtils.round(100 / percent);
		int initialDateYear = AonDateUtils.getYear(a.getInitialDate());
		int days = getAmortizationDays(initialDateYear,years);

		if (a.getDeadline() == null) {
			if (Math.floor(years) == years) {
				amortizationLastDay = DateUtils.setYears(amortizationFirstDay, (initialDateYear + (int) years));
				amortizationLastDay = DateUtils.addDays(amortizationLastDay, -1);
			} else {
				amortizationLastDay = AonDateUtils.addDays(amortizationFirstDay, days);
			}
		} else {
			amortizationLastDay = a.getDeadline();
			days = (int) AonDateUtils.getDaysBetweenDates(amortizationFirstDay, amortizationLastDay); 
		}

		double dayAllocation = a.getAmount() / days; // No se redondea a posta.
		
		Date periodFirst = a.getInitialDate();
		Date periodLast;
		double pending = a.getAmount();
		boolean lastFee = false;
		while (periodFirst.before(amortizationLastDay)) {
			periodLast = getPeriodLastDay(periodFirst,a.getFeePeriod());
			if (periodLast.after(amortizationLastDay)) {
				periodLast = amortizationLastDay;
			}
			if (DateUtils.isSameDay(periodLast, amortizationLastDay)) {
				lastFee = true;
			}
			periodLast = ensurePeriodLast(ctx, a, periodLast);
			
			AmortizationDetail detail  = insertable(ctx, a, periodFirst);
			if (detail == null) {
				detail = new AmortizationDetail();
				detail.setStatus(AmortizationDetailStatus.PENDING);
				double allocation;
				if (!lastFee) {
					long periodDays =  AonDateUtils.getDaysBetweenDates(periodFirst, periodLast) + 1;
					if (isPeriodComplete(periodFirst,periodLast,a.getFeePeriod())) {
						allocation = AonMathUtils.round((a.getAmount() * percent / 100) / a.getFeePeriod().getYearFraction());
					} else {
						// En caso de periodo no completo, se resuelve multiplicando 
						// el numero de dias por la cuota diaria.
						allocation = AonMathUtils.round(periodDays * dayAllocation);
					}
				} else {
					// La ultima cuota se cuadra por diferencia.
					allocation = AonMathUtils.round(pending - ((a.getSaleAmount() == null) ? 0 : a.getSaleAmount()));
				}
				pending = AonMathUtils.round(pending - allocation);
				detail.setAmortization(a.getId());
				detail.setDomain(a.getDomain());
				detail.setAllocation(allocation);
				detail.setCoefficient(AonMathUtils.round(allocation * 100 / a.getAmount()));
				detail.setFromDate(periodFirst);
				detail.setToDate(periodLast);
				detail.setFiscalAllocation(allocation);
				detail.setPending(pending);
				detail.setFiscalAccumulated(0.0);
				AmortizationDAO.save(ctx, detail);	
			} else {
				periodLast = detail.getToDate();
				pending = AonMathUtils.round(pending - detail.getAllocation());

			}
			periodFirst = DateUtils.addDays(periodLast, 1);
		}

	}

	private void deletePendingDetails(AONContext ctx, Amortization a) {
		ctx.getDslContext().delete(AMORTIZATION_DETAIL)
			.where(AMORTIZATION_DETAIL.AMORTIZATION.eq(a.getId()))
			  .and(AMORTIZATION_DETAIL.STATUS.eq(AmortizationDetailStatus.PENDING.value()))
			.execute();
	}

	private boolean isPeriodComplete(Date first, Date last, AmortizationPeriod feePeriod) {
		Date periodFirstDay = getPeriodFirstDay(first,feePeriod);
		Date periodLastDay = getPeriodLastDay(first,feePeriod);
		return DateUtils.isSameDay(periodFirstDay,first) && DateUtils.isSameDay(periodLastDay,last);
	}

	private Date getPeriodLastDay(Date date, AmortizationPeriod feePeriod) {
		return feePeriod.visit( new AmortizationPeriodVisitor<Date>() {
			@Override
			public Date visitYearly() {
				return (brokenPeriod)
					?getBrokenPeriodLastDay(date)
					:AonDateUtils.getYearLastDay(date);
			}
			@Override
			public Date visitMonthly() {
				return checkBroken( AonDateUtils.getMonthLastDay(date));
			}
			@Override
			public Date visitBiMonthly() {
				return checkBroken(AonDateUtils.getBiMonthLastDay(date));
			}
			@Override
			public Date visitQuarterly() {
				return checkBroken(AonDateUtils.getQuarterLastDay(date));
			}
			@Override
			public Date visitFourMonthly() {
				return checkBroken(AonDateUtils.getFourMonthLastDay(date));
			}
			@Override
			public Date visitHalfYearly() {
				return checkBroken(AonDateUtils.getHalfYearLastDay(date));
			}
			
			private Date checkBroken( Date lastDay) {
				if (brokenPeriod) {
					Date bpld = getBrokenPeriodLastDay(date);
					if ( lastDay.after( bpld) ) lastDay = bpld;
				}
				return lastDay;
			}
		});
	}
	
	private Date getBrokenPeriodLastDay(Date date) {
	    LocalDate localDate = date.toInstant()
	    	.atZone(ZoneId.systemDefault())
	    	.toLocalDate();
	    LocalDate candidate = localDate
    		.withMonth(brokenPeriodLastMonth)
    		.withDayOfMonth(brokenPeriodLastDay);
	    if (localDate.isAfter(candidate)) {
	        candidate = candidate.plusYears(1);
	    }
	    return Date.from(candidate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	
	private Date getPeriodFirstDay(Date date,AmortizationPeriod feePeriod) {
		if (feePeriod == AmortizationPeriod.MONTHLY) {
			return  AonDateUtils.getMonthFirstDay(date);	
		} else  if (feePeriod == AmortizationPeriod.BI_MONTHLY) {
			return  AonDateUtils.getBiMonthFirstDay(date);	
		} else  if (feePeriod == AmortizationPeriod.QUARTERLY) {
			return  AonDateUtils.getQuarterFirstDay(date);	
		} else  if (feePeriod == AmortizationPeriod.FOUR_MONTHLY) {
			return  AonDateUtils.getFourMonthFirstDay(date);	
		} else  if (feePeriod == AmortizationPeriod.HALF_YEARLY) {
			return  AonDateUtils.getHalfYearFirstDay(date);	
		}
	    if (brokenPeriod) {
	        LocalDate localDate = date.toInstant()
        		.atZone(ZoneId.systemDefault())
        		.toLocalDate();
	        LocalDate result = localDate
    			.withMonth(brokenPeriodFirstMonth)
        		.withDayOfMonth(brokenPeriodFirstDay);
	        return Date.from(result.atStartOfDay(ZoneId.systemDefault()).toInstant());
	    }
	    return AonDateUtils.getYearFirstDay(date);
	}

	private AmortizationDetail insertable(AONContext ctx, Amortization a, Date first) {
		return ctx.getDslContext()
			.select()
			.from(AMORTIZATION_DETAIL)
			.where(AMORTIZATION_DETAIL.AMORTIZATION.eq(a.getId()))
			.and(AMORTIZATION_DETAIL.FROM_DATE.eq(AonDateUtils.toSql(first)))
			.orderBy(AMORTIZATION_DETAIL.FROM_DATE.asc())
			.fetch()
			.stream()
			.map(r -> new AmortizationDetailFiller().apply(r))
			.findFirst()
			.orElse(null);
	}

	private void ensureFirstDay(AONContext ctx, Amortization a) {
		ctx.getDslContext()
			.select()
			.from(AMORTIZATION_DETAIL)
			.where(AMORTIZATION_DETAIL.AMORTIZATION.eq(a.getId()))
			.orderBy(AMORTIZATION_DETAIL.FROM_DATE.asc())
			.fetch()
			.stream()
			.map(r -> new AmortizationDetailFiller().apply(r))
			.filter(d -> !DateUtils.isSameDay(d.getFromDate(), a.getInitialDate()))
			.findFirst()
			.ifPresent(d -> {
				d.setFromDate(a.getInitialDate());
				AmortizationDAO.save( ctx, d);
			})
		;
	}

	private Date ensurePeriodLast(AONContext ctx, Amortization a, Date last) {
		return ctx.getDslContext()
			.select()
			.from(AMORTIZATION_DETAIL)
			.where(AMORTIZATION_DETAIL.AMORTIZATION.eq(a.getId()))
			.and(AMORTIZATION_DETAIL.FROM_DATE.lessOrEqual(AonDateUtils.toSql(last)))
			.and(AMORTIZATION_DETAIL.TO_DATE.greaterOrEqual(AonDateUtils.toSql(last)))
			.orderBy(AMORTIZATION_DETAIL.FROM_DATE)
			.fetch()
			.stream()
			.map(r -> new AmortizationDetailFiller().apply(r))
			.map( d -> DateUtils.addDays(d.getFromDate(), -1) )
			.findFirst()
			.orElse(last);
	}

	private void ensureParams(AONContext ctx, Amortization a) {
		if (a.getDomain() == null) throw new AonCoreException("El dominio es un dato requerido");
		if (a.getInitialDate() == null) throw new AonCoreException("La fecha inical es un dato requerido");
		if (a.getAmount() == null) throw new AonCoreException("El importe es un dato requerido");
		
		brokenPeriod = false;
		brokenPeriodFirstDay = -1;
		brokenPeriodFirstMonth = -1;
		brokenPeriodLastDay = -1;
		brokenPeriodLastMonth = -1;
		List<AccountPeriod> list = AccountPeriodDAO.getPeriods(ctx, f -> f.getDomainProperty().eq(a.getDomain()))
			.collect(Collectors.toCollection(LinkedList::new));
		if ( AonCollectionUtils.isNotEmpty(list)) {
			AccountPeriod period = (AccountPeriod) list.get(0);
			Date initiationDate = period.getInitiationDate() instanceof java.sql.Date 
				? new Date(period.getInitiationDate().getTime())
				: period.getInitiationDate();
			LocalDate first = initiationDate.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate();
	        if (!first.equals(first.withDayOfYear(1))) {
	            brokenPeriod = true;
	            brokenPeriodFirstDay   = first.getDayOfMonth();
	            brokenPeriodFirstMonth = first.getMonthValue();
				Date periodDeadline = period.getDeadline() instanceof java.sql.Date 
					? new Date(period.getDeadline().getTime()) 
					: period.getDeadline();
	            LocalDate deadline = periodDeadline.toInstant()
            		.atZone(ZoneId.systemDefault())
            		.toLocalDate();
	            brokenPeriodLastDay   = deadline.getDayOfMonth();
	            brokenPeriodLastMonth = deadline.getMonthValue();
	        }
		}
		
	}

	private int getAmortizationDays(int initialDateYear, double years) {
		int days = 0;
		int intYears = (int) Math.floor(years);
		double modYears = AonMathUtils.round(years - intYears);
		for (int i = 0; i < years - (modYears == 0 ? 0 : 1); i++) {
			days += Year.isLeap(initialDateYear + i) ? 366 : 365;
		}
		if (modYears > 0) {
			int lastYearDays = Year.isLeap(initialDateYear + intYears) ? 366 : 365;
			lastYearDays = (int) Math.ceil(lastYearDays * modYears);
			days += lastYearDays;
		}
		return days;
	}
	
	public AccountEntry recordAllocation(AONContext ctx, Amortization a, AmortizationDetail detail) {
		Date accountEntryDate = detail.getToDate();
		AccountPeriod period = AccountPeriodDAO.getActivePeriod( ctx, accountEntryDate);
		if (period == null) {
			throw new AonCoreException("No existe periodo definido para la fecha del apunte.");
		}
		AccountEntry entry = new AccountEntry();
		if ( a.getInvestAsset() != null) {
			EnterpriseActivity act = a.getInvestAsset().getActivity();
			entry.setActivity(act == null ? null : a.getInvestAsset().getActivity().getId()); 
		}
		entry.setPeriod(period.getId());
		entry.setEntryDate(accountEntryDate);
		entry.setSecurityLevel( a.getSecurityLevel() );
		entry.setEntryType(AccountEntryType.AMORTIZATION);
		Account accumulated = a.getAccumulatedAccount();
		Account allocation  = a.getAllocationAccount();
		
		AccountEntryDetail all = new AccountEntryDetail();
		all.setAccount(allocation);
		all.setBalancingAccount(accumulated);
		String concept = "Amort. - " + a.getFixedAssetAccount().getDescription();
		concept = StringUtils.abbreviate(concept, 32);
		all.setConcept(concept);
		all.setLine(0);
		all.setDebit(detail.getAllocation());

		AccountEntryDetail acc = new AccountEntryDetail();
		acc.setAccount(accumulated);
		acc.setBalancingAccount(allocation);
		acc.setConcept(concept);
		acc.setLine(1);
		acc.setCredit(detail.getAllocation());
		Integer id =  AccountEntryDAO.save( ctx, entry);
		return AccountEntryDAO.getAccountEntry(ctx, id);
	}

	public void unrecordAllocation(AONContext ctx, Integer acccountEntryId) {
		AccountEntryDAO.delete(ctx, acccountEntryId);
	}
	
	
	public void checkSale(AONContext ctx, Amortization a) {
		Date cancelDate = DateUtils.addDays(a.getDeadline(), -1);
		ctx.getDslContext().select()
			.from(AMORTIZATION_DETAIL)
			.where(AMORTIZATION_DETAIL.AMORTIZATION.eq(a.getId()))
			.and(AMORTIZATION_DETAIL.STATUS.notEqual(AmortizationDetailStatus.PENDING.value()))
			.and(AMORTIZATION_DETAIL.TO_DATE.greaterThan(AonDateUtils.toSql(cancelDate)))
			.fetch()
			.stream()
			.findFirst()
			.ifPresent(r -> {
				throw new AonCoreException("Existe una cuota posterior a la fecha de cancelaci\u00F3n, bloqueada o contabilizada.");
			});
	}

	public void sale(AONContext ctx, Amortization a) {
		Date cancelDate = DateUtils.addDays(a.getDeadline(), -1);
		ctx.getDslContext().select()
		.from(AMORTIZATION_DETAIL)
		.where(AMORTIZATION_DETAIL.AMORTIZATION.eq(a.getId()))
		.and(AMORTIZATION_DETAIL.TO_DATE.greaterThan(AonDateUtils.toSql(cancelDate)))
		.orderBy(AMORTIZATION_DETAIL.FROM_DATE.asc())
		.fetch()
		.stream()
		.map(r -> new AmortizationDetailFiller().apply(r))
		.forEach(d -> {
			Date from = d.getFromDate();
			Date to =  d.getToDate();
			if (from.equals(cancelDate) || from.before(cancelDate)) {
				int days = (int) AonDateUtils.getDaysBetweenDates(from, to);
				int newDays = (int) AonDateUtils.getDaysBetweenDates(from, cancelDate );
				double newAllocation = AonMathUtils.round( d.getAllocation() * newDays / days );
				d.setToDate(cancelDate);
				d.setAllocation(newAllocation);
				AmortizationDAO.save(ctx, d);
			} else {
				AmortizationDAO.delete(ctx, d.getId());
			}
		});
	}
	
}
