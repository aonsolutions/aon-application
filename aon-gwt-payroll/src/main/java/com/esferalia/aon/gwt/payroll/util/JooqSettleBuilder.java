package com.esferalia.aon.gwt.payroll.util;

import static com.esferalia.aon.gwt.payroll.util.PayrollUtils.getDeductionPDFType;
import static com.esferalia.aon.gwt.payroll.util.PayrollUtils.getDeductionTypeDescription;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.maker.PdfMaker;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFDeduction;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFPayment;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.SettlePrintConfiguration;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.beans.Settlement;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.beans.Settlement.SettlementBuilder;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.Payment;
import com.esferalia.aon.occam.api.model.Settle;
import com.esferalia.aon.occam.impl.jooq.dao.SalaryDAO;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.watson.util.AonStringUtils;

/**
 * Class containing method/s to print settles from database data
 */
public class JooqSettleBuilder {
	
	/**
	 * Print the settle PDF file 
	 * @param settle - The Settle itself
	 * @param out	 - Stream to write
	 * @param lang	 - Printing language
	 * @throws CanNotCreatePdfException The pdf cannot print for some reason.
	 */
	public static void printSettle(Settle settle,OutputStream out,Locale lang) throws CanNotCreatePdfException {
		PdfMaker.printSettlement(out, new SettlePrintConfiguration(adaptToPDFObject(settle), null, lang));
	}

	/**
	 * Adapts Settle object to Settlement
	 * @param settle - The settle itself
	 * @return [Settlement] Filled Settlement.
	 */
	private static Settlement adaptToPDFObject(Settle settle) {
		SettlementBuilder builder = new SettlementBuilder();
		
		builder
		.setEmployeeName(settle.getEmployeeName())
		.setEmployeeCategory(settle.getEmployeeCategory())
		.setEmployeeAntiquity(settle.getStartDate())
		.setEmployeeNIF(settle.getEmployeeDocument())
		.setEnterpriseAddress(settle.getEnterpriseAddress())
		.setEnterpriseName(settle.getEnterpriseName())
		.setEnterpriseNIF(settle.getEnterpriseDocument())
		.setTotal(settle.getTotalLiquid())
		.setAccrualTotal(settle.getTotalPayment())
		.setDeductionTotal(settle.getTotalDeduction())
		.setDate(settle.getEndDate())
		.setLocation(settle.getLocation())
		.setEndCause(settle.getCause())
		.setExistRepresentative(settle.getRepresentativeDocument() != null);
		
		//PAYMENTS
		HashMap<Integer, ArrayList<PDFPayment>> paymentMap = new HashMap<Integer, ArrayList<PDFPayment>>();
		Collection<Payment> payments = settle.getPayments();
		payments.stream()
			.filter(JooqSettleBuilder::filter)
			.sorted(
					Comparator.comparing(p -> {
						return !(p.getDescription() == null || p.getDescription().isEmpty()) ? p.getDescription() : "zzzzzz"; 
					}
			))
			.forEach(p -> {
				String description = p.getDescription().replaceAll("\\[\\d*\\]", "");
				if (description.length() > 50) {
					try {
						description = PDFToolkit.croppedString(description, 260, PdfFonts.HELVETICA, 9f);
					} catch (IOException e) {}	
				}
				
				PDFPayment accrual = new PDFPayment(p.getAmount(), description);
				if (!paymentMap.containsKey(p.getPaymentType().ordinal()))
					paymentMap.put(p.getPaymentType().ordinal(), new ArrayList<PDFPayment>());
				 
				if (paymentMap.get(p.getPaymentType().ordinal()).stream().anyMatch(acc -> AonStringUtils.equalsIgnoreCase(p.getDescription(), acc.getDescription().get()))) {
					PDFPayment repAcc = paymentMap.get(p.getPaymentType().ordinal()).stream().findFirst().get();
					repAcc.setAmount(repAcc.getAmount().orElse(0d)+p.getAmount());
				} else
					paymentMap.get(p.getPaymentType().ordinal()).add(accrual);
			});
			builder.setPayments(paymentMap);
		
		//DEDUCTIONS
		ArrayList<String> inserted = new ArrayList<String>();
			
		Collection<com.esferalia.aon.occam.api.model.Salary.Deduction> deductions = settle.getDeductions();
		HashMap<Integer, ArrayList<PDFDeduction>> deductionsMap = new HashMap<Integer, ArrayList<PDFDeduction>>();
		
		deductions.stream().forEach(d -> {
		Double percent = null;
					
			try {
				percent = Double.parseDouble(d.getDescription().replaceAll("\\s", "").replaceAll("%", ""));
			} catch (NumberFormatException e) {}
					
			int type = getDeductionPDFType(d.getDeductionType().ordinal());
			String desc = d.getDescription();
					
			if(desc == null || desc.isEmpty()) 
				desc = getDeductionTypeDescription(d.getDeductionType().ordinal());
					
			PDFDeduction deduction = new PDFDeduction(d.getAmount(), desc, percent);
					
					
			if (!deductionsMap.containsKey(type))
				deductionsMap.put(type, new ArrayList<PDFDeduction>());
					
			if (deductionsMap.get(type)
					.stream()
					.anyMatch(p -> AonStringUtils.equalsIgnoreCase(p.getDescription().get(), deduction.getDescription().get()))
			){
				PDFDeduction ded = deductionsMap.get(type)
								.stream()
								.filter(p -> AonStringUtils.equalsIgnoreCase(deduction.getDescription().get(),p.getDescription().get())).findFirst().get();
				ded.setAmount(ded.getAmount().get()+deduction.getAmount().get());
			} else
				deductionsMap.get(type).add(deduction);
				inserted.add(Utilities.getDeductionType(d.getDeductionType().ordinal()));
		});
			

		if (deductionsMap.get(1)==null)
			deductionsMap.put(1, new ArrayList<PDFDeduction>());
		if (deductionsMap.get(2)==null)
				deductionsMap.put(2, new ArrayList<PDFDeduction>());
			
		if (!inserted.contains("CGC"))
			deductionsMap.get(1).add(new PDFDeduction(0d, "Contingencias comunes", 0d));
		if (!inserted.contains("DESMPL"))
			deductionsMap.get(1).add(new PDFDeduction(0d, "Desempleo", 0d));
		if (!inserted.contains("FP"))
			deductionsMap.get(1).add(new PDFDeduction(0d, "Formación profesional", 0d));
		if (!inserted.contains("IRPF"))
			deductionsMap.get(2).add(new PDFDeduction(0d, "Retribuciones dinerarias", 0d));

			builder.setDeductions(deductionsMap);
		return builder.build();
	}
	
	/**
	 * Get settles from database by id
	 * @param ctx
	 * @param id
	 * @return
	 */
	public static List<Settle> getSettlesFromDatabase(AONContext ctx, Integer[] id) {
		Stream<Settle> settle = SalaryDAO.getSettles(ctx, p -> p.getIdProperty().in(id), Settle::new);
		return settle.collect(Collectors.toList());
	}
	
	private static boolean filter (Payment payment) {
		return !(payment.getAmount() == 0 && !AonStringUtils.equalsIgnoreCase(payment.getName(), ContextVariable.PREST_IT));
	}

	/**
	 * Print PDF settles by id from database
	 * @param ids - The ids of the settles
	 * @param out - The stream to write in
	 * @param locale - Language of the PDF
	 * @throws CanNotCreatePdfException 
	 */
	public static void printSettles(String domainName,int enterprise, Integer[] ids, OutputStream out, Locale locale) throws CanNotCreatePdfException {
		try (AONContext aonContext = AONContext.getAONContext(domainName, "")) {
			List<Settle> settleList = getSettlesFromDatabase(aonContext, ids);
			for (Settle settle : settleList) 
				printSettle(settle, out, locale);
		}
	}
}