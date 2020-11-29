package com.esferalia.aon.ui.payroll.utils;

import static com.esferalia.aon.watson.util.AonStringUtils.romanIntValue;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.jooq.tables.Contract;
import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.EnterpriseCcc;
import com.esferalia.aon.jooq.tables.Rattach;
import com.esferalia.aon.jooq.tables.RdirStaff;
import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.jooq.tables.User;
import com.esferalia.aon.jooq.tables.records.RattachRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalaryItem;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;
import com.ibm.icu.text.RuleBasedNumberFormat;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.AonDataSource;
import net.sf.jasperreports.engine.JRImageRenderer;
import net.sf.jasperreports.engine.JRRenderable;

public class ReportUtils {
	
	private static Map<CCCType, SSRegimeType> SS_REGIMES = new HashMap<CCCType, SSRegimeType>(){
		{
			put(CCCType.AGRICULTURAL, SSRegimeType.AGRICULTURAL);
			put(CCCType.HOME_EMPLOYEES, SSRegimeType.DOMESTIC_EMPLOYEES);
			put(CCCType.ARTIST, SSRegimeType.ARTIST);
		}
	};
	
	

	
	public static final ThreadLocal<String> domain = new ThreadLocal<String>();
	
	public static final ThreadLocal<Map<String,byte[]>> datas = ThreadLocal.withInitial(HashMap<String,byte[]>::new);
	
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

	public static final <T> List<T> filter(Collection<T> collection,
			String property, Object... values) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		List<T> list = new LinkedList<T>();
		for (T t : collection) {
			Object value = PropertyUtils.getProperty(t, property);
			if (!contains(values, value)) {
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

	public static RegistryAttachment _getRAttach(Integer registryId,
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

	public static RegistryAttachment getRAttach(Integer registryId,
			RegistryAttachmentType type) throws ManagerBeanException {
		Connection conn = null;
		try {
			conn = AonDataSource.getInstance().getConnection(domain.get());
			AONContext aonContext = new AONContext(conn);
			DSLContext dslContext = aonContext.getDslContext();
			
			RattachRecord rattachRecord =
			dslContext
			.select()
			.from(Rattach.RATTACH)
			.where(Rattach.RATTACH.REGISTRY.eq(registryId))
			.and(Rattach.RATTACH.TYPE.eq((byte) type.ordinal()))
			.fetchInto(Rattach.RATTACH)
			.stream()
			.findFirst()
			.orElseGet(()->null)
			;
			
			if ( rattachRecord == null )
				return null;
			
			
			RegistryAttachment registryAttachment = new RegistryAttachment();
			registryAttachment.setId(rattachRecord.getId());
			registryAttachment.setDomain(rattachRecord.getDomain());
			byte data [] = rattachRecord.getData();
			registryAttachment.setData(data);
			registryAttachment.setDriveId(rattachRecord.getDriveId());
			registryAttachment.setDescription(rattachRecord.getDescription());
			registryAttachment.setAttachDate(rattachRecord.getAttachDate());
			registryAttachment.setDparentId(rattachRecord.getDparentId());
			registryAttachment.setCreationDate(rattachRecord.getCreationDate());
			registryAttachment.setCreationUser(rattachRecord.getCreationUser());
			
			if ( data == null )
				registryAttachment.setData(data = datas.get().get(registryAttachment.getDriveId()));
			
			try {
				if ( data == null ) {
					
					String login = 
					dslContext
					.select()
					.from(User.USER)
					.where(User.USER.DOMAIN.eq(registryAttachment.getDomain()))
					.fetchAny(User.USER.LOGIN)
					;
					if ( login == null ) 
						login = 
						dslContext
						.select()
						.from(User.USER)
						.where(User.USER.DOMAIN.in(DSL.select(Domain.DOMAIN.PARENT).from(Domain.DOMAIN).where(Domain.DOMAIN.ID.eq(registryAttachment.getDomain()))
						))
						.fetchAny(User.USER.LOGIN)
						;
						
					registryAttachment.setData(data = DriveUtils.getByteFile(
							domain.get(), 
							registryAttachment.getDomain(), 
							login, 
							registryAttachment.getDriveId(), 
							registryAttachment.getId()));
					
					datas.get().put(registryAttachment.getDriveId(), data);
				}
			} catch ( Throwable t ) {
				t.printStackTrace();
				return null;
			}
			
			return registryAttachment;
		} catch (AonConnectionException e) {
			throw new ManagerBeanException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
		
	}

	public static List<RegistryDirStaff> getRepresentativesLabor(Integer registryId) 
	
			throws ManagerBeanException {
		Connection conn = null;
		try {
			conn = AonDataSource.getInstance().getConnection(domain.get());
			AONContext aonContext = new AONContext(conn);
			DSLContext dslContext = aonContext.getDslContext();
			
			List<RegistryDirStaff> registryDirStaffs = 
			dslContext
			.select()
			.from(RdirStaff.RDIR_STAFF)
			.innerJoin(Registry.REGISTRY)
			.onKey()
			.where(RdirStaff.RDIR_STAFF.REGISTRY.eq(registryId))
			.and(RdirStaff.RDIR_STAFF.REPRESENTATIVE_LABOR.eq((byte)1))
			.fetch(( record ) -> {
				RegistryDirStaff registryDirStaff = new RegistryDirStaff();

				registryDirStaff.setId(record.get(RdirStaff.RDIR_STAFF.ID));
				registryDirStaff.setDomain(record.get(RdirStaff.RDIR_STAFF.DOMAIN));
				registryDirStaff.setName(record.get(RdirStaff.RDIR_STAFF.NAME));
				registryDirStaff.setDocument(record.get(RdirStaff.RDIR_STAFF.DOCUMENT));
				registryDirStaff.setDirector(record.get(RdirStaff.RDIR_STAFF.DIRECTOR) == (byte)1);
				registryDirStaff.setChargeDescription(record.get(RdirStaff.RDIR_STAFF.CHARGE_DESCRIPTION));
				registryDirStaff.setDueDate(record.get(RdirStaff.RDIR_STAFF.DUE_DATE));
				registryDirStaff.setNominalValue(record.get(RdirStaff.RDIR_STAFF.NOMINAL_VALUE));
				registryDirStaff.setPercentShare(record.get(RdirStaff.RDIR_STAFF.PERCENT_SHARE));
				registryDirStaff.setRepresentative(record.get(RdirStaff.RDIR_STAFF.REPRESENTATIVE) == (byte)1);
				registryDirStaff.setRepresentativeLabor(record.get(RdirStaff.RDIR_STAFF.REPRESENTATIVE_LABOR) == (byte)1);
				registryDirStaff.setShareNumber(record.get(RdirStaff.RDIR_STAFF.SHARE_NUMBER));
				registryDirStaff.setShareHolder(record.get(RdirStaff.RDIR_STAFF.SHAREHOLDER) == (byte)1);
				
				com.code.aon.registry.Registry registry = new com.code.aon.registry.Registry();
				registry.setId(record.get(Registry.REGISTRY.ID));
				registry.setDomain(record.get(Registry.REGISTRY.DOMAIN));
				registry.setName(record.get(Registry.REGISTRY.NAME));
				registry.setDocument(record.get(Registry.REGISTRY.DOCUMENT));
				//registry.setDocumentCountry(record.get(Registry.REGISTRY.DOCUMENT_COUNTRY));
				registryDirStaff.setRegistry(registry);

				return registryDirStaff;
			})
			;
			
			return registryDirStaffs;
			
		} catch (AonConnectionException e) {
			throw new ManagerBeanException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
		
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

	public static CCCType getCCCType(Salary salary) 
			{
		Connection conn = null;
		try {
			conn = AonDataSource.getInstance().getConnection(domain.get());
			AONContext aonContext = new AONContext(conn);
			DSLContext dslContext = aonContext.getDslContext();
			
			Byte cccType =
			dslContext
			.select()
			.from(Contract.CONTRACT)
			.innerJoin(EnterpriseCcc.ENTERPRISE_CCC).onKey()
			.where(Contract.CONTRACT.ID.eq(salary.getContract().getId()))
			.fetchOne(EnterpriseCcc.ENTERPRISE_CCC.TYPE)
			;
			
			return CCCType.values()[cccType];
			
		} catch (Exception e) {
			return CCCType.PRINCIPAL;
			
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
		
	}
	

	private static boolean contains(Object values[], Object value) {
		for (int i = 0; i < values.length; i++) {
			if ( AonUtils.equals(values[i], value))
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
	
	public static String getFullQuoteRegime(Salary salary ) {
		return SS_REGIMES.getOrDefault(getCCCType(salary), SSRegimeType.GENERAL).getCode() + (salary.getCcc() != null ? salary.getCcc() : "" );
	}
	
	
	public  static String getTotalSSForecolor(Salary salary) {
		return getTotalEmployeeAndEnterpriseForecolor(salary);
	}

	public  static String getTotalLiquidForecolor(Salary salary) {
		return getTotalEmployeeAndEnterpriseForecolor(salary);
	}

	public  static String getTotalSSEnterpriseForecolor(Salary salary) {
		return getTotalEnterpriseForecolor(salary);
	}

	public  static String getTotalSSEmployeeForecolor(Salary salary) {
		return getSocialSecurityContributionsForecolor(salary);
	}
	
	private  static String getTotalEnterpriseForecolor(Salary salary) {
		try {
			Double totalEnterprise = Double.parseDouble(salary.getSalaryData("TOTAL_ENTERPRISE"));			
			return AonUtils.equals(totalEnterprise, salary.getTotalEnterprise()) ? "#0000FF" : "#FF0000";
		} catch ( Exception  e) {
			return "";
		}
	}
	
	private  static String getSocialSecurityContributionsForecolor(Salary salary) {
		try {
			Double socialSecurityContributions = Double.parseDouble(salary.getSalaryData("SOCIAL_SECURITY_CONTRIBUTIONS"));
			return AonUtils.equals(socialSecurityContributions, salary.getSocialSecurityContributions()) ? "#0000FF" : "#FF0000";
		} catch ( Exception  e) {
			return "";
		}
	}

	private  static String getTotalEmployeeAndEnterpriseForecolor(Salary salary) {
		try {
			double totalEnterprise = Double.parseDouble(salary.getSalaryData("TOTAL_ENTERPRISE"));
			if ( totalEnterprise != salary.getTotalEnterprise() )
				return "#FF0000";
			
			double socialSecurityContributions = Double.parseDouble(salary.getSalaryData("SOCIAL_SECURITY_CONTRIBUTIONS"));
			return socialSecurityContributions == (double) salary.getSocialSecurityContributions() ? "#0000FF" : "#FF0000";
		} catch ( Exception  e) {
			return "";
		}
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
