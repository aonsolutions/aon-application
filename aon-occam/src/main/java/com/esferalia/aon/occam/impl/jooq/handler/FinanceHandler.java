package com.esferalia.aon.occam.impl.jooq.handler;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.invoice.Invoice;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.PayMethodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryBankDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryPayMethodDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FinanceHandler {

	// -------------------------------------------------------------
	// ------ FINANCE --- CALCULO EN FUNCION DE RPAYMETHOD ---------
	// -------------------------------------------------------------
	public static LinkedList<Finance> getFinancesForInvoice(AONContext ctx, Invoice invoice) {
		LinkedList<Finance> finances = new LinkedList<>();
		RegistryPayMethod rPayMethod = RegistryPayMethodDAO.getStream(ctx, prop -> prop.getDomainProperty()
				.eq(ctx.getDomainId()).and(prop.getRegistryProperty().eq(invoice.getRegistry()))).findFirst()
				.orElse(null);
		;
		RegistryBank rBank = null;
		if (rPayMethod != null && !rPayMethod.getRbank().isEmpty()) {
			rBank = RegistryBankDAO.getStream(ctx, prop -> prop.getDomainProperty().eq(ctx.getDomainId())
					.and(prop.getIdProperty().eq(rPayMethod.getRbank().getId()))).findFirst().orElse(null);
		}
		Date date = invoice.getIssueDate();
		int numberOfPymnts =  ((rPayMethod == null) || (rPayMethod.getNumberOfPymnts() == 0)) ? 1 : rPayMethod.getNumberOfPymnts();
		int daysToFirstPymnt = ((rPayMethod == null) || (rPayMethod.getDaysToFirstPymnt() == 0)) ? 0 : rPayMethod.getDaysToFirstPymnt();
		int daysBetwenPymnts = ((rPayMethod == null) || (rPayMethod.getDaysBetwenPymnts() == 0)) ? 0 : rPayMethod.getDaysBetwenPymnts();
		PayMethod payMethod = null;
		if (rPayMethod != null && !rPayMethod.getPayMethod().isEmpty()) {
			payMethod = PayMethodDAO.get(ctx, rPayMethod.getPayMethod().getId());
		}
		double paymentPrice = AonMathUtils.round(invoice.getTotal() / numberOfPymnts);
		for (int i = 0; i < numberOfPymnts; i++) {
			int days = (i==0?daysToFirstPymnt:daysBetwenPymnts);
			date = (rPayMethod==null? date : calculatePaymentDate(days, rPayMethod.getPymntDays(), date));
			Finance finance = buildFinance(invoice, date, payMethod , rBank, paymentPrice );  
			finances.add(finance);
		}
		double lastPaymentPrice = AonMathUtils.round(invoice.getTotal() - (paymentPrice * (numberOfPymnts - 1)));
		if ( !AonMathUtils.equals(paymentPrice, lastPaymentPrice)) {
			finances.getLast().setAmount(lastPaymentPrice);
		}
		return finances;
	}
	
	private static Date calculatePaymentDate(int daysNumber, String paymentDays, Date date) {
		Date paymentDate = AonDateUtils.addDays(date, daysNumber);
		String[] paymentDaysArray = AonStringUtils.split(paymentDays, ' ');
		if (paymentDaysArray.length > 0) {
			for (int i=0; i<paymentDaysArray.length; i++) {
				int daysInMonth = AonDateUtils.daysInMonth(paymentDate);
				int day = AonNumberUtils.toint(paymentDaysArray[i]);
				day = day>daysInMonth ? daysInMonth : day;
				if (AonDateUtils.getFragmentInDays(paymentDate, Calendar.MONTH) <= day) {
					return AonDateUtils.setDays(paymentDate, day);
				}
			}
			int day = AonNumberUtils.toint(paymentDaysArray[0]);
			if (day != 0) {
				paymentDate = AonDateUtils.addMonths(paymentDate, 1);
				int daysInMonth = AonDateUtils.daysInMonth(paymentDate);
				day = (day>daysInMonth) ? daysInMonth : day;
				return AonDateUtils.setDays(paymentDate, day);
			}
		}
		return paymentDate;
	}

	private static Finance buildFinance(Invoice invoice, Date date, PayMethod payMethod, RegistryBank rBank, double totalPrice) {
		return  new Finance()
			.setDomain(invoice.getDomain())
			.setPayment(invoice.getType() != InvoiceType.SALES)
			.setRegistry( new Registry().setId(invoice.getRegistry()))
			.setRegistryName(invoice.getRegistryName())
			.setRegistryDocument(invoice.getRegistryDocument())
			.setRegistryDocumentType(invoice.getRegistryDocumentType())
			.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry())
			.setAmount(totalPrice)
			.setConcept(invoice.getDocumentNumber())
			.setInvoice(new com.esferalia.aon.occam.api.model.finance.Invoice().setId( invoice.getId()))
			.setDueDate(date)
			.setPayMethod(payMethod==null?null:payMethod.getId())
			.setPayMethodName(payMethod==null?null:payMethod.getName())
			.setPayMethodType(payMethod==null?null:payMethod.getType())
			.setBankAccount((rBank==null) ? null : rBank.getBankAccount() )
			.setBankAlias((rBank==null) ? null : rBank.getAlias())
			.setBic((rBank==null) ? null : rBank.getBic())
			.setFinanceStatus(FinanceStatus.PENDING)
			.setSecurityLevel(invoice.getSecurityLevel())
			.setScope(invoice.getScope());
	}

}
