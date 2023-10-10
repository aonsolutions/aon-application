package com.esferalia.aon.in.payroll.pdf.maker.payroll.bean;

import static com.esferalia.aon.watson.util.AonNumberUtils.zeroIfNull;
import static com.esferalia.aon.watson.util.AonStringUtils.isEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.esferalia.aon.in.payroll.pdf.maker.payroll.IPayrollTemplate;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class DefaultPayrollFuseBox {
	
	private DefaultPayrollFuseBox() {
		throw new IllegalStateException("Utility class");
	}
	
	private static final Integer[] EXTRA_HOUR_CRAS = {2,3};
	private static final Integer[] EXTRA_GRATIFICATIONS_CRAS = {4,5};
	private static final Integer[] IN_KIND_CRAS = {13,14,15,16,17,18,19,20,21,22};
	private static final Integer[] INDEMN_CRAS = {51,52,53,54};
	
	private static final Pattern[] SALARY_PATTERN = {
					Pattern.compile("^\\s*salario.*$", Pattern.CASE_INSENSITIVE),
					Pattern.compile("^\\s*s\\.\\s*base*$", Pattern.CASE_INSENSITIVE)
			};
	
	public static boolean isComplementoSalarial(int cra, PDFPayment payment) {
		if (cra != 1 || payment == null)
			return false;
		return !isPrestIt(cra, payment) && !isSalary(cra, payment);
	}
	
	public static boolean isPrestIt(int cra, PDFPayment payment) {
		if (cra == 100) {
			return true;
		} else if (cra != 1 || payment == null || isEmpty(payment.getDescription().orElse("")))
			return false;
		return AonStringUtils.equalsIgnoreCase("PREST_IT", payment.getDescription().orElse(""));
	}
	
	public static boolean isSalary(int cra, PDFPayment payment) {
		if (cra != 1 || payment == null || isEmpty(payment.getDescription().orElse("")))
			return false;
		return Arrays.stream(SALARY_PATTERN)
				.anyMatch(pattern -> pattern.matcher(payment.getDescription().orElse(""))
						.matches());
	}
	
	public static List<PDFPayment> getComplementosSalariales(Optional<Map<Integer, ArrayList<PDFPayment>>> allPayments) {
		return allPayments.orElse(Collections.emptyMap())
			.getOrDefault(1, new ArrayList<>())
			.stream()
			.filter(p -> isComplementoSalarial(1, p))
			.collect(Collectors.toUnmodifiableList());
	}
	
	public static List<PDFPayment> getSalaries(Optional<Map<Integer, ArrayList<PDFPayment>>> allPayments) {
		return allPayments.orElse(Collections.emptyMap())
				.getOrDefault(1, new ArrayList<>())
				.stream()
				.filter(p -> isSalary(1, p))
				.collect(Collectors.toUnmodifiableList());
	}
	
	public static double getSalarySum(Optional<Map<Integer, ArrayList<PDFPayment>>> allPayments) {
		return allPayments.orElse(Collections.emptyMap())
				.getOrDefault(1, new ArrayList<>())
				.stream()
				.filter(p -> isSalary(1, p))
				.mapToDouble(p -> p.getAmount().orElse(0d)).sum();
	}

	public static List<PDFPayment> getPrestSS(Optional<Map<Integer, ArrayList<PDFPayment>>> allPayments) {
		return allPayments.orElse(Collections.emptyMap())
				.getOrDefault(100, new ArrayList<>())
				.stream()
				.filter(p -> isPrestIt(100, p))
				.collect(Collectors.toUnmodifiableList());
	}

	public static double getExtraHoursPayment(Optional<Map<Integer, ArrayList<PDFPayment>>> allPayments) {
		return getSumByCras(allPayments, EXTRA_HOUR_CRAS);
	}
	
	public static double getExtraGratificationPayment(Optional<Map<Integer, ArrayList<PDFPayment>>> allPayments) {
		return getSumByCras(allPayments, EXTRA_GRATIFICATIONS_CRAS);
	}
	
	public static double getInKindPayment(Optional<Map<Integer, ArrayList<PDFPayment>>> allPayments) {
		return getSumByCras(allPayments, IN_KIND_CRAS);
	}
	
	public static List<PDFPayment> getIndemns(Optional<Map<Integer, ArrayList<PDFPayment>>> allPayments) {
		return getPaymentsByCras(allPayments, INDEMN_CRAS);
	}
	
	public static List<PDFPayment> getInfos(Optional<Map<Integer, ArrayList<PDFPayment>>> allPayments) {
		return getPaymentsByCras(allPayments, new Integer [] {IPayrollTemplate.INFO} );
	}

	public static List<PDFPayment> getNotes(Optional<Map<Integer, ArrayList<PDFPayment>>> allPayments) {
		return getPaymentsByCras(allPayments, new Integer [] {IPayrollTemplate.NOTE} );
	}
	
	public static List<PDFPayment> getWarnings(Optional<Map<Integer, ArrayList<PDFPayment>>> allPayments) {
		return getPaymentsByCras(allPayments, new Integer [] {IPayrollTemplate.WARNING} );
	}
	

	public static List<PDFPayment> getOtherPayments(Optional<Map<Integer, ArrayList<PDFPayment>>> allPayments) {
		if (allPayments.isEmpty()) {
			return Collections.emptyList();
		}
		List<PDFPayment> payments = new ArrayList<>();
		Set<Integer> filteredCras = new HashSet<>();
		filteredCras.add(1);
		filteredCras.add(100);
		
		filteredCras.add(IPayrollTemplate.INFO);
		filteredCras.add(IPayrollTemplate.NOTE);
		filteredCras.add(IPayrollTemplate.WARNING);
		
		filteredCras.addAll(Arrays.asList(EXTRA_HOUR_CRAS));
		filteredCras.addAll(Arrays.asList(EXTRA_GRATIFICATIONS_CRAS));
		filteredCras.addAll(Arrays.asList(IN_KIND_CRAS));
		filteredCras.addAll(Arrays.asList(INDEMN_CRAS));
		for (Entry<Integer, ArrayList<PDFPayment>> entry : allPayments.orElse(Collections.emptyMap()).entrySet()) {
			if (!filteredCras.contains(entry.getKey()) && entry.getValue() != null) {
				payments.addAll(entry.getValue().stream()
				.filter(Objects::nonNull)
				.collect(Collectors.toList()));
			}
		}
		return payments;
	}
	
	private static double getSumByCras(Optional<Map<Integer, ArrayList<PDFPayment>>> allPayments, Integer[] cras) {
		if (cras == null || allPayments.isEmpty()) {
			return 0;
		}
		double sum = 0;
		for (int cra : cras) {			
			sum += allPayments.orElse(Collections.emptyMap())
					.getOrDefault(cra, new ArrayList<>())
					.stream()
					.filter(Objects::nonNull)
					.mapToDouble(p -> p.getAmount().orElse(0d)).sum();
		}
		return sum;
	}

	private static List<PDFPayment> getPaymentsByCras(Optional<Map<Integer, ArrayList<PDFPayment>>> allPayments, Integer[] cras) {
		if (cras == null || allPayments.isEmpty()) {
			return Collections.emptyList();
		}
		List<PDFPayment> payments = new ArrayList<>();
		for (int cra : cras) {			
			payments.addAll(allPayments.orElse(Collections.emptyMap())
					.getOrDefault(cra, new ArrayList<>())
					.stream()
					.filter(Objects::nonNull)
					.collect(Collectors.toList()));
		}
		return Collections.unmodifiableList(payments);
	}
	
	//DEDUCTIONS
	
	private static List<PDFDeduction> getDeductionsByType(final Optional<Map<Integer, ArrayList<PDFDeduction>>> allDeductions, final DeductionType deductionType) {
		if (deductionType == null) {
			return Collections.emptyList();
		}
		ArrayList<PDFDeduction> deds = new ArrayList<>();
		allDeductions
		.orElse(Collections.emptyMap())
		.values()
		.stream()
		.filter(Objects::nonNull)
		.forEach(dl -> 
			deds.addAll(
				 dl
				.stream()
				.filter(d -> d != null && deductionType.equals(d.getDeductionType().orElse(DeductionType.OTHER)))
				.collect(Collectors.toList())
			)
		);
		return Collections.unmodifiableList(deds);
	}
	
	public static PDFDeduction getSingleDeductionByType(final Optional<Map<Integer, ArrayList<PDFDeduction>>> allDeductions, DeductionType deductionType, String deductionDescription) {
		String name = null;
		double percent = 0;
		double amount = 0;		
		for (PDFDeduction d : getDeductionsByType(allDeductions, deductionType)) {
			percent += zeroIfNull(d.getPercent().orElse(0d));
			amount += zeroIfNull(d.getAmount().orElse(0d));
			name = d.getName().orElse(name);
		}
		return new PDFDeduction(amount, name, deductionDescription, percent, deductionType);
	}
	
	public static PDFDeduction getSingleDeductionByType(final Optional<Map<Integer, ArrayList<PDFDeduction>>> allDeductions, DeductionType deductionType, String deductionDescription, Predicate<PDFDeduction> filter) {
		String name = null;
		double percent = 0;
		double amount = 0;
		for (PDFDeduction d : getDeductionsByType(allDeductions, deductionType)) {
		    	if ( filter.test(d)) {
        		    	percent += zeroIfNull(d.getPercent().orElse(0d));
        			amount += zeroIfNull(d.getAmount().orElse(0d));
        			name = d.getName().orElse(name);
		    	}
		}
		return new PDFDeduction(amount, name , deductionDescription, percent, deductionType);
	}

	public static List<PDFDeduction> getOtherDeductions(final Optional<Map<Integer, ArrayList<PDFDeduction>>> allDeductions) {
		
		DeductionType[] filteredTypes = {	DeductionType.COMMON_CONTINGENCY,
							DeductionType.MEI,
							DeductionType.UNEMPLOYMENT,
							DeductionType.JOB_TRAINING,
							DeductionType.NON_STRUCTURAL_OVERTIME,
							DeductionType.STRUCTURAL_OVERTIME,
							DeductionType.IRPF,
							DeductionType.ADVANCE_PAYMENT,
							DeductionType.IN_KIND};
		ArrayList<PDFDeduction> deds = new ArrayList<>();
		allDeductions
		.orElse(Collections.emptyMap())
		.values()
		.stream()
		.filter(Objects::nonNull)
		.forEach(dl -> 
		deds.addAll(
				dl
				.stream()
				.filter(d -> d != null && !Arrays.asList(filteredTypes).contains(d.getDeductionType().orElse(DeductionType.OTHER)))
				.collect(Collectors.toList())
				)
				);
		return Collections.unmodifiableList(deds);
	}
	
	public static PDFDeduction getOtherDeduction(final Optional<Map<Integer, ArrayList<PDFDeduction>>> allDeductions, String deductionName) {
	    	String name = null;
		double percent = 0;
		double amount = 0;		
		for (PDFDeduction d : getOtherDeductions(allDeductions)) {
			percent += zeroIfNull(d.getPercent().orElse(0d));
			amount += zeroIfNull(d.getAmount().orElse(0d));
			name = d.getName().orElse(name);
		}
		return new PDFDeduction(amount, name, deductionName, percent, DeductionType.OTHER);
	}
	
	
}
