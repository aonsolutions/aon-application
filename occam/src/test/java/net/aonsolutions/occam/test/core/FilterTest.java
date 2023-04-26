package net.aonsolutions.occam.test.core;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.regex.Pattern;

import org.jooq.Condition;
import org.jooq.Field;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.Filter;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.dao.FilterDAO;
import net.aonsolutions.occam.dao.PropertyDAO;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;

@ExtendWith(TimingExtension.class)	
class FilterTest extends AbstractOccamTest {
	
	private static final String FIELD_NAME = "field";
	private static final Field<Integer> INTEGER = DOMAIN.ID.as(FIELD_NAME);
	private static final Field<Byte> BYTE = DOMAIN.TYPE.as(FIELD_NAME);
	private static final Field<String> STRING = DOMAIN.NAME.as(FIELD_NAME);
	private static final Field<Date> DATE = DOMAIN.EXPIRATIONDATE.as(FIELD_NAME);
	private static final Field<Timestamp> TIMESTAMP = DOMAIN.CREATION_DATE.as(FIELD_NAME);
	
	private static final Pattern IS_NULL = Pattern.compile("[\\\"|']field[\\\"|'] is null");
	
	private static final String IS_NOT_NULL = "\"field\" is not null";
	
	private static final String EQ 		= "\"field\" = 1";
	private static final String NE 		= "\"field\" <> 1";
	private static final String GE 		= "\"field\" >= 1";
	private static final String GT 		= "\"field\" > 1";
	private static final String LE 		= "\"field\" <= 1";
	private static final String LT 		= "\"field\" < 1";
	private static final String BET 	= "\"field\" between 1 and 2";
	private static final String LIKE  	= "\"field\" like '%1%'";
	private static final String IN 		= "\"field\" in (  1, 2)";
	private static final String NOT_IN 	= "\"field\" not in (  1, 2)";
	
	private static final String SEQ 	= "\"field\" = '1'";
	private static final String SNE 	= "\"field\" <> '1'";
	private static final String SGE 	= "\"field\" >= '1'";
	private static final String SGT 	= "\"field\" > '1'";
	private static final String SLE 	= "\"field\" <= '1'";
	private static final String SLT 	= "\"field\" < '1'";
	private static final String SBET 	= "\"field\" between '1' and '2'";
	private static final String SLIKE   = "\"field\" like '%1%'";
	private static final String SIN     = "\"field\" in (  '1', '2')";
	private static final String SNOT_IN = "\"field\" not in (  '1', '2')";

	private final String S1 = "1";
	private final String S2 = "2";
	private final byte B1 = 1;
	private final byte B2 = 2;
	
	@FunctionalInterface
	public interface TestFilter {
		Filter filter(FilterTests properties);
	}

	private interface FilterTests {
		Property<Integer> withInteger();
		Property<String> withString();
		Property<Byte> withByte();
		Property<Date> withDate();
		Property<Timestamp> withTimestamp();
	}
	private static final FilterTestDAO FILTER_TEST = new FilterTestDAO();
	private static class FilterTestDAO implements FilterTests {
		
		protected Condition getConditions(TestFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.getCondition();
		}
		
		@Override public Property<Integer> withInteger() {return new PropertyDAO<>(INTEGER);}
		@Override public Property<String> withString() {return new PropertyDAO<>(STRING);}
		@Override public Property<Byte> withByte() {return new PropertyDAO<>(BYTE);}
		@Override public Property<Date> withDate() {return new PropertyDAO<>(DATE);}
		@Override public Property<Timestamp> withTimestamp() {return new PropertyDAO<>(TIMESTAMP);}
	}
	
	@Test()
	void propertyDAOTest() {
		assertTrue(IS_NULL.matcher(FILTER_TEST.getConditions(p -> p.withInteger().isNull()).toString()).matches());
		assertTrue(IS_NULL.matcher(FILTER_TEST.getConditions(p -> p.withInteger().eq(null)).toString()).matches());
		assertEquals(EQ, FILTER_TEST.getConditions(p -> p.withInteger().eq(1)).toString() );
		assertEquals(IS_NOT_NULL, FILTER_TEST.getConditions(p -> p.withInteger().isNotNull()).toString() );
		assertEquals(IS_NOT_NULL, FILTER_TEST.getConditions(p -> p.withInteger().ne(null)).toString() );
		assertEquals(NE, FILTER_TEST.getConditions(p -> p.withInteger().ne(1)).toString() );
		assertEquals(GE, FILTER_TEST.getConditions(p -> p.withInteger().ge(1)).toString() );
		assertEquals(GT, FILTER_TEST.getConditions(p -> p.withInteger().gt(1)).toString() );
		assertEquals(LE, FILTER_TEST.getConditions(p -> p.withInteger().le(1)).toString() );
		assertEquals(LT, FILTER_TEST.getConditions(p -> p.withInteger().lt(1)).toString() );
		assertEquals(BET, FILTER_TEST.getConditions(p -> p.withInteger().between(1,2)).toString() );
		assertEquals(LIKE, FILTER_TEST.getConditions(p -> p.withInteger().like(1)).toString() );
		String in = FILTER_TEST.getConditions(p -> p.withInteger().in(new Integer[] {1,2})).toString();
		assertEquals(IN, AonStringUtils.removeTabsAndNewLine(in));
		String notIn = FILTER_TEST.getConditions(p -> p.withInteger().notIn(new Integer[] {1,2})).toString() ;
		assertEquals(NOT_IN, AonStringUtils.removeTabsAndNewLine(notIn));
	}
	
	@Test()
	void bytePropertyDAOTest() {
		assertTrue(IS_NULL.matcher(FILTER_TEST.getConditions(p -> p.withByte().isNull()).toString()).matches());
		assertTrue(IS_NULL.matcher(FILTER_TEST.getConditions(p -> p.withByte().eq(null)).toString()).matches());
		assertEquals(EQ, FILTER_TEST.getConditions(p -> p.withByte().eq(B1)).toString() );
		assertEquals(IS_NOT_NULL, FILTER_TEST.getConditions(p -> p.withByte().isNotNull()).toString() );
		assertEquals(IS_NOT_NULL, FILTER_TEST.getConditions(p -> p.withByte().ne(null)).toString() );
		assertEquals(NE, FILTER_TEST.getConditions(p -> p.withByte().ne(B1)).toString() );
		assertEquals(GE, FILTER_TEST.getConditions(p -> p.withByte().ge(B1)).toString() );
		assertEquals(GT, FILTER_TEST.getConditions(p -> p.withByte().gt(B1)).toString() );
		assertEquals(LE, FILTER_TEST.getConditions(p -> p.withByte().le(B1)).toString() );
		assertEquals(LT, FILTER_TEST.getConditions(p -> p.withByte().lt(B1)).toString() );
		assertEquals(BET, FILTER_TEST.getConditions(p -> p.withByte().between(B1,B2)).toString() );
		assertThrows(UnsupportedOperationException.class, () -> FILTER_TEST.getConditions(p -> p.withByte().like(B1)) );
		String in = FILTER_TEST.getConditions(p -> p.withByte().in(new Byte[] {B1,B2})).toString();
		assertEquals(IN, AonStringUtils.removeTabsAndNewLine(in));
		String notIn = FILTER_TEST.getConditions(p -> p.withByte().notIn(new Byte[] {B1,B2})).toString() ;
		assertEquals(NOT_IN, AonStringUtils.removeTabsAndNewLine(notIn));
	}
	
	@Test()
	void stringPropertyDAOTest() {
		assertTrue(IS_NULL.matcher(FILTER_TEST.getConditions(p -> p.withString().isNull()).toString()).matches());
		assertTrue(IS_NULL.matcher(FILTER_TEST.getConditions(p -> p.withString().eq(null)).toString()).matches());
//		assertEquals(SEQ, FILTER_TEST.getConditions(p -> p.withString().eq(S1)).toString() );
//		assertEquals(IS_NOT_NULL, FILTER_TEST.getConditions(p -> p.withString().isNotNull()).toString() );
//		assertEquals(IS_NOT_NULL, FILTER_TEST.getConditions(p -> p.withString().ne(null)).toString() );
//		assertEquals(SNE, FILTER_TEST.getConditions(p -> p.withString().ne(S1)).toString() );
//		assertEquals(SGE, FILTER_TEST.getConditions(p -> p.withString().ge(S1)).toString() );
//		assertEquals(SGT, FILTER_TEST.getConditions(p -> p.withString().gt(S1)).toString() );
//		assertEquals(SLE, FILTER_TEST.getConditions(p -> p.withString().le(S1)).toString() );
//		assertEquals(SLT, FILTER_TEST.getConditions(p -> p.withString().lt(S1)).toString() );
//		assertEquals(SBET, FILTER_TEST.getConditions(p -> p.withString().between(S1,S2)).toString() );
//		assertEquals(SLIKE,FILTER_TEST.getConditions(p -> p.withString().like(S1)).toString() );
//		String in = FILTER_TEST.getConditions(p -> p.withString().in(new String[] {S1,S2})).toString();
//		assertEquals(SIN, AonStringUtils.removeTabsAndNewLine(in));
//		String notIn = FILTER_TEST.getConditions(p -> p.withString().notIn(new String[] {S1,S2})).toString() ;
//		assertEquals(SNOT_IN, AonStringUtils.removeTabsAndNewLine(notIn));
	}
	
}
