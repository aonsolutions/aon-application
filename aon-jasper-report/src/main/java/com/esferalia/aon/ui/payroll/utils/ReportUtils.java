package com.esferalia.aon.ui.payroll.utils;

import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;
import static net.aonsolutions.payroll.report.SalaryReport.DSLCONTEXT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanComparator;

import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.salary.enumeration.PaymentType;

import net.aonsolutions.payroll.report.Payment;

public class ReportUtils {
	
	private static Map<String, Comparator<?>> PROPERTY_COMPARATORS = new HashMap<String, Comparator<?>>() {
		{
			put("description", StringComparator.INSTANCE);
		}
	};
	
	
	public static class CompositeComparator<T> implements Comparator<T> {
		
		private Collection<Comparator<T>> comparators;
		
		public CompositeComparator(Collection<Comparator<T>> comparators) {
			super();
			this.comparators = comparators;
		}

		@Override
		public int compare(T o1, T o2) {
			for ( Comparator<T> comparator : comparators ) {
				int compare = comparator.compare(o1, o2);
				if ( compare == 0 )
					continue;
				return compare;
			}
			return 0;
		}
		
	}

	public static class StringComparator implements Comparator<String> {
		
		private static final StringComparator INSTANCE = new StringComparator();

		private static Pattern ORDER = Pattern.compile(
				"^\\[(\\d+)\\]\\s*(.*)$");
		private static Pattern ROMAN = Pattern.compile(
				"^((C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3}))([\\W_]+.*)?$"
				,Pattern.CASE_INSENSITIVE);

		@Override
		public int compare(String s0, String s1) {
			
			if ( s0 == s1 )
				return 0;
			if (s0 == null )
				return -1;
			if (s1 == null )
				return 1;

			// By roman numerals, if exists
			Matcher order0 = ORDER.matcher(s0);
			Matcher order1 = ORDER.matcher(s1);
			boolean matches0 = order0.matches();
			boolean matches1 = order1.matches();
			
			if ( matches0 && matches1 ) {
				int compareTo = Integer.parseInt(order0.group(1)) - Integer.parseInt(order1.group(1)); 
				if (compareTo != 0) 
					return compareTo;
			}
			if ( matches0 && !matches1)
				return -1; 	
			if ( matches1 && !matches0)
				return 1;	
				
			// By roman numerals, if exists
			try {
				Matcher matcher0 = ROMAN.matcher(s0.trim());
				Matcher matcher1 = ROMAN.matcher(s1.trim());
				matches0 = matcher0.matches();
				matches1 = matcher1.matches();
				if (matches0 && matches1 ) {
					int roman0 = romanIntValue(matcher0.group(1));
					int roman1 = romanIntValue(matcher1.group(1));
					if (roman0 != roman1)
						return roman0 - roman1;
				}
				if ( matches0 && !matches1)
					return -1; 	
				if ( matches1 && !matches0)
					return 1;	
			} catch ( IllegalArgumentException e){
				// Not roman numeral. Due a bug at 'ROMAN' regular expression.
				// 'ROMAN' matches empty strings and strings like '[1] SALARIO'
			}

			return s0.compareTo(s1);
		}
	}
	
	public static class ReportPayment implements Payment {
		private Payment payment;

		public ReportPayment(Payment payment) {
			super();
			this.payment = payment;
		}

		public Integer getId() {
			return payment.getId();
		}

		public String getName() {
			return payment.getName();
		}

		public double getAmount() {
			return payment.getAmount();
		}

		public PaymentType getType() {
			return payment.getType();
		}

		public String getExpression() {
			return payment.getExpression();
		}

		public String getDescription() {
			String description = payment.getDescription();
			if ( description == null  )
				return null;
			
			Matcher matcher = StringComparator.ORDER.matcher(description);
			if ( matcher.matches() )
				return matcher.group(2);
			return description;			
		}

		public Integer getCRA() {
			return payment.getCRA();
		}
		
		

		
	}

	public static String toUpperCase(String str){
		return str != null ? str.toUpperCase() : null;
	}

	public static final <T> List<?> sort(Collection<T> collection, String property) {
		BeanComparator<T> comparator = 
				new BeanComparator<T>(property, PROPERTY_COMPARATORS.get(property));
		
		return collection.stream().sorted(comparator)
				.map( o -> (o instanceof Payment) ? new ReportPayment((Payment)o) : o  )
				.collect(Collectors.toList());
		
	}

	public static final <T> List<?> sort(Collection<T> collection, String ...properties) {
		
		Collection<Comparator<T>> comparators = new ArrayList<Comparator<T>>();
		for ( String property: properties )
			comparators.add(new BeanComparator<T>(property, PROPERTY_COMPARATORS.get(property)));
		
		Comparator<T> comparator = new CompositeComparator<>(comparators);
		return collection.stream()
				.sorted(comparator)
				.map( o -> (o instanceof Payment) ? new ReportPayment((Payment)o) : o  )
				.collect(Collectors.toList());
	}

	public static String capitalize(String string) {
		
		char[] chars = string.toLowerCase().toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You can add other
				found = false;
			}
		}
		return String.valueOf(chars);
	}

	public static RegistryAttachment getRAttach(Integer registryId, RegistryAttachmentType type) {
		byte data [] = 
		DSLCONTEXT.get().select()
		.from(RATTACH)
		.where(RATTACH.REGISTRY.eq(registryId))
		.and(RATTACH.TYPE.eq((byte)type.ordinal()))
		.fetchAny(RATTACH.DATA)
		;
		
		return data == null ? null : new RegistryAttachment().setData(data);
	}

	public static String getFullQuoteRegime(Salary salary) {
		return (salary.getCcc() != null ? salary.getCcc() : "");
	}
	
	public static List<RegistryDirStaff> getRepresentativesLabor(Integer registryId){
		return  
		DSLCONTEXT.get().select()
		.from(RDIR_STAFF)
		.where(RDIR_STAFF.REGISTRY.eq(registryId))
		.and(RDIR_STAFF.REPRESENTATIVE_LABOR.isTrue())
		.fetch()
		.stream()
		.map( r -> new RegistryDirStaff() )
		.collect(Collectors.toList())
		
		;
	}

	public static String spellout(Locale locale, int i) {
		return "";//spellout(locale, d, 2);
	}

	public static String spellout(Locale locale, double d) {
		return "";//spellout(locale, d, 2);
	}
	
	public static String spellout(Locale locale, double d, int p) {
		return "";//spellout(locale, d, 2);
	}

	private static int romanIntValue(String string) {
        String number = string.toUpperCase();
        if (number.isEmpty())
                return 0;
        if (number.startsWith("M"))
                return 1000 + romanIntValue(number.substring(1));
        if (number.startsWith("CM"))
                return 900 + romanIntValue(number.substring(2));
        if (number.startsWith("D"))
                return 500 + romanIntValue(number.substring(1));
        if (number.startsWith("CD"))
                return 400 + romanIntValue(number.substring(2));
        if (number.startsWith("C"))
                return 100 + romanIntValue(number.substring(1));
        if (number.startsWith("XC"))
                return 90 + romanIntValue(number.substring(2));
        if (number.startsWith("L"))
                return 50 + romanIntValue(number.substring(1));
        if (number.startsWith("XL"))
                return 40 + romanIntValue(number.substring(2));
        if (number.startsWith("X"))
                return 10 + romanIntValue(number.substring(1));
        if (number.startsWith("IX"))
                return 9 + romanIntValue(number.substring(2));
        if (number.startsWith("V"))
                return 5 + romanIntValue(number.substring(1));
        if (number.startsWith("IV"))
                return 4 + romanIntValue(number.substring(2));
        if (number.startsWith("I"))
                return 1 + romanIntValue(number.substring(1));
        throw new IllegalArgumentException("unexpected roman numerals");
    }
    
    

}
