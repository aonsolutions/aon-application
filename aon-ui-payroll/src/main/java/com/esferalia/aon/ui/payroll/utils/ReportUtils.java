package com.esferalia.aon.ui.payroll.utils;

import static com.esferalia.aon.watson.util.AonStringUtils.romanIntValue;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jasperreports.engine.JRImageRenderer;
import net.sf.jasperreports.engine.JRRenderable;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.util.ComparableComparator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.model.Payment;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.salary.ISalaryItem;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.payment.IPayment;
import com.ibm.icu.text.RuleBasedNumberFormat;

public class ReportUtils {
	
	
	public static class ReportSalaryItem<T extends Enum<T> & IResourceable> implements ISalaryItem<T> {
		
		protected ISalaryItem<T> salaryItem;
		
		public ReportSalaryItem(ISalaryItem<T> salaryItem) {
			this.salaryItem = salaryItem;
		}

		public T getType() {
			return salaryItem.getType();
		}

		public String getName() {
			return salaryItem.getName();
		}

		public double getAmount() {
			return salaryItem.getAmount();
		}

		public String getDescription() {
			String description = salaryItem.getDescription();
			Matcher matcher = StringComparator.ORDER.matcher(StringUtils.defaultIfEmpty(description, ""));
			if ( matcher.matches() )
				return matcher.group(2);
			return description;
		}
	}
	
	
	public static class ReportPayment extends ReportSalaryItem<PaymentType> implements IPayment{

		public ReportPayment(SalaryPayment payment) {
			super(payment);
		}

		@Override
		public String getExpression() {
			return ((SalaryPayment) salaryItem).getExpression();
		}
		
		
		public Double getUnits() throws ManagerBeanException{
			return ((SalaryPayment) salaryItem).getUnits();
		}

		public Salary getSalary(){
			return ((SalaryPayment) salaryItem).getSalary();
		}

		public Double getUnitAmount() throws ManagerBeanException{
			return ((SalaryPayment) salaryItem).getUnitAmount();
		}
		
		public String getPaymentConcept() {
			return ((SalaryPayment) salaryItem).getPaymentConcept();
		}
		
	}
	
	public static class PropertyComparator implements Comparator {
		
		private static final StringComparator STRING = new StringComparator();
		private static final ComparableComparator COMPARABLE = new ComparableComparator();
		
		public int compare(Object x, Object y) {
			if ( x instanceof String ) 
				return STRING.compare((String)x, (String)y);
			if ( x instanceof Comparable ) 
				return COMPARABLE.compare((Comparable)x, (Comparable)y);
			else 
				return 0;
		}
		
		public static final Comparator INSTANCE = new PropertyComparator();
	}

	public static class ComparableComparator implements Comparator<Comparable> {

		public int compare(Comparable x, Comparable y) {
			if ( x == y )
				return 0;
			if ( x == null )
				return 1;
			if ( y == null )
				return -1;
			return x.compareTo(y);
		}
		

		
	}
	
	public static class StringComparator implements Comparator<String> {

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
	
	public static String capitalize(String str) {
		return StringUtils.capitalize(StringUtils.lowerCase(str));
	}
	
	public static final <T> List<T> sort(Collection<T> collection,
			String property) {
		List<T> list = new LinkedList<T>(collection);
		Comparator<T> comparator = new BeanComparator(property,PropertyComparator.INSTANCE);
		Collections.sort(list, comparator);
		
		List<T> ret = new LinkedList<T>();
		for (T t : list)
			ret.add(wrap(t));
		
		return ret;
	}

	public static final <T> List<T> sort(Collection<T> collection,
			String... properties) {
		List<T> list = new LinkedList<T>(collection);
		Comparator<T> comparators[] = new Comparator[properties.length];
		for (int i = 0; i < properties.length; i++) {
			comparators[i] = new BeanComparator(properties[i],PropertyComparator.INSTANCE);

		}
		Comparator<T> comparator = new ChainedComparator<T>(comparators);
		Collections.sort(list, comparator);

		List<T> ret = new LinkedList<T>();
		for (T t : list)
			ret.add(wrap(t));
		
		return ret;
	}

	public static final <T> List<T> sort(Collection<T> collection,
			Comparator<T> comparator) {
		List<T> list = new LinkedList<T>(collection);
		Collections.sort(list, comparator);
		return list;
	}

	public static final <T> T first(Collection<T> collection, String property,
			Object value) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		for (T t : collection) {
			if (value == PropertyUtils.getProperty(t, property)) {
				return t;
			}
		}
		return null;

	}

	public static final <T> List<T> reduce(Collection<T> collection,
			String property, Object... values) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		List<T> list = new LinkedList<T>();
		for (T t : collection) {
			Object value = PropertyUtils.getProperty(t, property);
			if (contains(values, value)) {
				list.add(t);
			}
		}
		Comparator<T> comparator = new BeanComparator(property,PropertyComparator.INSTANCE);
		Collections.sort(list, comparator);
		return list;
	}

	public static JRRenderable getRenderer(RegistryAttachment rattach) {
		return JRImageRenderer.getInstance(rattach.getData());
	}

	public static RegistryAttachment getRAttach(Integer registryId,
			RegistryAttachmentType type) throws ManagerBeanException {
		IManagerBean beanManager = BeanManager
				.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(beanManager
				.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID),
				registryId);
		criteria.addEqualExpression(
				beanManager
						.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE),
				type);
		List<?> list = beanManager.getList(criteria);

		return list == null || list.isEmpty() ? null
				: (RegistryAttachment) list.get(0);
	}

	public static List<RegistryDirStaff> getRepresentativesLabor(Integer registryId) 
	
			throws ManagerBeanException {
		IManagerBean beanManager = BeanManager
				.getManagerBean(RegistryDirStaff.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(beanManager
				.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID),
				registryId);
		criteria.addEqualExpression(
				beanManager
						.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REPRESENTATIVE_LABOR),
				Boolean.TRUE);
		List<?> list = beanManager.getList(criteria);

		return (List<RegistryDirStaff>) list ;

	}

	
	public static String spellout(Locale locale, int integer) {
		return new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.SPELLOUT)
				.format(integer);
	}

	public static String spellout(Locale locale, double d) {
		return spellout(locale, d, 2);
	}

	public static String spellout(Locale locale, double d, int precision) {
		StringBuffer buffer = new StringBuffer();
		long integral = (long) Math.floor(d);
		long fractional = (long) Math.floor(CommonUtil.round((d - integral),2)
				* Math.pow(10, precision));
		RuleBasedNumberFormat format = new RuleBasedNumberFormat(locale,
				RuleBasedNumberFormat.SPELLOUT);
		buffer.append(format.format(integral));
		if ( fractional > 0  ) { 
			buffer.append(" con ");
			buffer.append(format.format(fractional));
		}

		return buffer.toString();
	}
	
	public static String toUpperCase(String str){
		return str != null ? str.toUpperCase() : null;
	}
	
	
	
	// ------------------------------------------------------------------------

	private static boolean contains(Object values[], Object value) {
		for (int i = 0; i < values.length; i++) {
			if (values[i] == value)
				return true;
		}
		return false;
	}

	public static long toExcelNumberFormat(Date date) {
		Calendar start = Calendar.getInstance();
		start.set(1900, Calendar.JANUARY, 1);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int days = 1;

		for (int i = 1900; i < calendar.get(Calendar.YEAR); start.set(
				Calendar.YEAR, ++i))
			days += start.getActualMaximum(Calendar.DAY_OF_YEAR);

		for (int i = Calendar.JANUARY; i < calendar.get(Calendar.MONTH); start
				.set(Calendar.MONTH, ++i))
			days += start.getActualMaximum(Calendar.DAY_OF_MONTH);

		days += calendar.get(Calendar.DAY_OF_MONTH);

		return days;
	}

	public static String ifEmpty(String a, String b) {
		return a == null || a.isEmpty() ? b : a;
	}

	private static class ChainedComparator<T> implements Comparator<T> {

		private Comparator<T> simpleComparators[];

		public ChainedComparator(Comparator<T>... simpleComparators) {
			this.simpleComparators = simpleComparators;
		}

		public int compare(T o1, T o2) {
			for (Comparator<T> comparator : simpleComparators) {
				int result = comparator.compare(o1, o2);
				if (result != 0) {
					return result;
				}
			}
			return 0;
		}
	}
	
	private static <T> T wrap(T t) {
		if ( t instanceof SalaryPayment){
			return (T) new ReportPayment((SalaryPayment)t);
		}
		return t;
	}

	public static void main(String[] args) {
		System.out.println(spellout(new Locale("es_ES"), 5495.00010, 5));
	}
}
