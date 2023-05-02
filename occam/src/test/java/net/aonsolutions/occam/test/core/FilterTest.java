package net.aonsolutions.occam.test.core;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.regex.Pattern;

import org.jooq.Condition;
import org.jooq.Field;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.occam.api.Filter;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.dao.FilterDAO;
import net.aonsolutions.occam.dao.PropertyDAO;
import net.aonsolutions.occam.dao.DatePropertyDAO;
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
	
	private static final String FNAME = "[\\\"|']field[\\\"|']";
	private static final Pattern IS_NULL = Pattern.compile(FNAME + " is null");
	private static final Pattern IS_NOT_NULL = Pattern.compile(FNAME + " is not null");
	private static final Pattern EQ = Pattern.compile(FNAME + " = 1");
	private static final Pattern NE 		= Pattern.compile(FNAME + " <> 1");
	private static final Pattern GE 		= Pattern.compile(FNAME + " >= 1");
	private static final Pattern GT 		= Pattern.compile(FNAME + " > 1");
	private static final Pattern LE 		= Pattern.compile(FNAME + " <= 1");
	private static final Pattern LT 		= Pattern.compile(FNAME + " < 1");
	private static final Pattern BET 	= Pattern.compile(FNAME + " between 1 and 2");
	private static final Pattern LIKE  	= Pattern.compile(FNAME + " like '%1%'");
	private static final Pattern IN 		= Pattern.compile(FNAME + " in \\(\\s*1\\s*,\\s*2\\s*\\)");
	private static final Pattern NOT_IN 	= Pattern.compile(FNAME + " not in \\(\\s*1\\s*,\\s*2\\s*\\)");
	
	private static final Pattern SEQ = Pattern.compile(FNAME + " = [\\\"|']1[\\\"|']");
	private static final Pattern SNE 	= Pattern.compile(FNAME + " <> [\\\"|']1[\\\"|']");
	private static final Pattern SGE 	= Pattern.compile(FNAME + " >= [\\\"|']1[\\\"|']");
	private static final Pattern SGT 	= Pattern.compile(FNAME + " > [\\\"|']1[\\\"|']");
	private static final Pattern SLE 	= Pattern.compile(FNAME + " <= [\\\"|']1[\\\"|']");
	private static final Pattern SLT 	= Pattern.compile(FNAME + " < [\\\"|']1[\\\"|']");
	private static final Pattern SBET 	= Pattern.compile(FNAME + " between [\\\"|']1[\\\"|'] and [\\\"|']2[\\\"|']");
	private static final Pattern SLIKE   = Pattern.compile(FNAME + " like [\\\"|']1[\\\"|']");
	private static final Pattern SIN     = Pattern.compile(FNAME + 
		" in \\(\\s*[\\\"|\\']1[\\\"|\\']\\s*\\,\\s*[\\\"|\\']2[\\\"|\\']\\s*\\)", Pattern.MULTILINE);
	private static final Pattern SNOT_IN = Pattern.compile(FNAME + 
		" not in \\(\\s*[\\\"|\\']1[\\\"|\\']\\s*\\,\\s*[\\\"|\\']2[\\\"|\\']\\s*\\)", Pattern.MULTILINE);


	private static final String S1 = "1";
	private static final String S2 = "2";
	private static final byte B1 = 1;
	private static final byte B2 = 2;
	private static final Date D1 = AonDateUtils.toSql(AonDateUtils.getYearFirstDay(new java.util.Date()));
	private static final Date D2 = AonDateUtils.toSql(AonDateUtils.getYearLastDay(new java.util.Date()));
	
	private static final Pattern DEQ = Pattern.compile(FNAME + " = date [\\\"|']" + D1.toString() + "[\\\"|']");
	private static final Pattern DNE 		= Pattern.compile(FNAME + " <> date [\\\"|']" + D1.toString() + "[\\\"|']");
	private static final Pattern DGE 		= Pattern.compile(FNAME + " >= date [\\\"|']" + D1.toString() + "[\\\"|']");
	private static final Pattern DGT 		= Pattern.compile(FNAME + " > date [\\\"|']" + D1.toString() + "[\\\"|']");
	private static final Pattern DLE 		= Pattern.compile(FNAME + " <= date [\\\"|']" + D1.toString() + "[\\\"|']");
	private static final Pattern DLT 		= Pattern.compile(FNAME + " < date [\\\"|']" + D1.toString() + "[\\\"|']");
	private static final Pattern DBET 	= Pattern.compile(FNAME + " between date [\\\"|']" + D1.toString() + "[\\\"|'] and date [\\\"|']" + D2.toString() + "[\\\"|']");
	
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
		@Override public Property<Date> withDate() {return new DatePropertyDAO(DATE);}
		@Override public Property<Timestamp> withTimestamp() {return new PropertyDAO<>(TIMESTAMP);}
	}
	
	@Test()
	void propertyDAOTest() {
		assertTrue(IS_NULL.matcher(FILTER_TEST.getConditions(p -> p.withInteger().isNull()).toString()).matches());
		assertTrue(IS_NULL.matcher(FILTER_TEST.getConditions(p -> p.withInteger().eq(null)).toString()).matches());
		assertTrue(EQ.matcher(FILTER_TEST.getConditions(p -> p.withInteger().eq(1)).toString()).matches());
		assertTrue(IS_NOT_NULL.matcher(FILTER_TEST.getConditions(p -> p.withInteger().isNotNull()).toString()).matches());
		assertTrue(IS_NOT_NULL.matcher(FILTER_TEST.getConditions(p -> p.withInteger().ne(null)).toString()).matches());
		assertTrue(NE.matcher(FILTER_TEST.getConditions(p -> p.withInteger().ne(1)).toString()).matches());
		assertTrue(GE.matcher(FILTER_TEST.getConditions(p -> p.withInteger().ge(1)).toString()).matches());
		assertTrue(GT.matcher(FILTER_TEST.getConditions(p -> p.withInteger().gt(1)).toString()).matches());
		assertTrue(LE.matcher(FILTER_TEST.getConditions(p -> p.withInteger().le(1)).toString()).matches());
		assertTrue(LT.matcher(FILTER_TEST.getConditions(p -> p.withInteger().lt(1)).toString()).matches());
		assertTrue(BET.matcher(FILTER_TEST.getConditions(p -> p.withInteger().between(1,2)).toString() ).matches());
		assertTrue(LIKE.matcher(FILTER_TEST.getConditions(p -> p.withInteger().like(1)).toString() ).matches());
		assertTrue(IN.matcher(FILTER_TEST.getConditions(p -> p.withInteger().in(new Integer[] {1,2})).toString()).matches());
		assertTrue(NOT_IN.matcher(FILTER_TEST.getConditions(p -> p.withInteger().notIn(new Integer[] {1,2})).toString()).matches());
	}
	
	@Test()
	void bytePropertyDAOTest() {
		assertThat(FILTER_TEST.getConditions(p -> p.withByte().isNull()).toString(), matchesPattern(IS_NULL));
		assertThat(FILTER_TEST.getConditions(p -> p.withByte().eq(null)).toString(), matchesPattern(IS_NULL));
		assertThat(FILTER_TEST.getConditions(p -> p.withByte().eq(B1)).toString(), matchesPattern(EQ));
		assertThat(FILTER_TEST.getConditions(p -> p.withByte().isNotNull()).toString(), matchesPattern(IS_NOT_NULL));
		assertThat(FILTER_TEST.getConditions(p -> p.withByte().ne(null)).toString(), matchesPattern(IS_NOT_NULL));
		assertThat(FILTER_TEST.getConditions(p -> p.withByte().ne(B1)).toString(), matchesPattern(NE));
		assertThat(FILTER_TEST.getConditions(p -> p.withByte().ge(B1)).toString(), matchesPattern(GE));
		assertThat(FILTER_TEST.getConditions(p -> p.withByte().gt(B1)).toString(), matchesPattern(GT));
		assertThat(FILTER_TEST.getConditions(p -> p.withByte().le(B1)).toString(), matchesPattern(LE));
		assertThat(FILTER_TEST.getConditions(p -> p.withByte().lt(B1)).toString(), matchesPattern(LT));
		assertThat(FILTER_TEST.getConditions(p -> p.withByte().between(B1,B2)).toString(), matchesPattern(BET));
		assertThrows(UnsupportedOperationException.class, () -> FILTER_TEST.getConditions(p -> p.withByte().like(B1)) );
		assertThat(FILTER_TEST.getConditions(p -> p.withByte().in(new Byte[] {B1,B2})).toString(), matchesPattern(IN));
		assertThat(FILTER_TEST.getConditions(p -> p.withByte().notIn(new Byte[] {B1,B2})).toString(), matchesPattern(NOT_IN));
	}
	
	@Test()
	void stringPropertyDAOTest() {
		assertThat(FILTER_TEST.getConditions(p -> p.withString().isNull()).toString(), matchesPattern(IS_NULL));
		assertThat(FILTER_TEST.getConditions(p -> p.withString().eq(null)).toString(), matchesPattern(IS_NULL));
		assertThat(FILTER_TEST.getConditions(p -> p.withString().eq(S1)).toString(), matchesPattern(SEQ));
		assertThat(FILTER_TEST.getConditions(p -> p.withString().isNotNull()).toString(), matchesPattern(IS_NOT_NULL));
		assertThat(FILTER_TEST.getConditions(p -> p.withString().ne(null)).toString(), matchesPattern(IS_NOT_NULL));
		assertThat(FILTER_TEST.getConditions(p -> p.withString().ne(S1)).toString(), matchesPattern(SNE));
		assertThat(FILTER_TEST.getConditions(p -> p.withString().ge(S1)).toString(), matchesPattern(SGE));
		assertThat(FILTER_TEST.getConditions(p -> p.withString().gt(S1)).toString(), matchesPattern(SGT));
		assertThat(FILTER_TEST.getConditions(p -> p.withString().le(S1)).toString(), matchesPattern(SLE));
		assertThat(FILTER_TEST.getConditions(p -> p.withString().lt(S1)).toString(), matchesPattern(SLT));
		assertThat(FILTER_TEST.getConditions(p -> p.withString().between(S1,S2)).toString(), matchesPattern(SBET));
		assertThat(FILTER_TEST.getConditions(p -> p.withString().like(S1)).toString(), matchesPattern(SLIKE));
		assertThat(FILTER_TEST.getConditions(p -> p.withString().in(new String[] {S1,S2})).toString(), matchesPattern(SIN));
		assertThat(FILTER_TEST.getConditions(p -> p.withString().notIn(new String[] {S1,S2})).toString(), matchesPattern(SNOT_IN));
	}
	
	@Test()
	void datePropertyDAOTest() {
		assertThat(FILTER_TEST.getConditions(p -> p.withDate().isNull()).toString(), matchesPattern(IS_NULL));
		assertThat(FILTER_TEST.getConditions(p -> p.withDate().eq(null)).toString(), matchesPattern(IS_NULL));
		assertThat(FILTER_TEST.getConditions(p -> p.withDate().eq(D1)).toString(), matchesPattern(DEQ));
		assertThat(FILTER_TEST.getConditions(p -> p.withDate().isNotNull()).toString(), matchesPattern(IS_NOT_NULL));
		assertThat(FILTER_TEST.getConditions(p -> p.withDate().ne(null)).toString(), matchesPattern(IS_NOT_NULL));
		assertThat(FILTER_TEST.getConditions(p -> p.withDate().ne(D1)).toString(), matchesPattern(DNE));
		assertThat(FILTER_TEST.getConditions(p -> p.withDate().ge(D1)).toString(), matchesPattern(DGE));
		assertThat(FILTER_TEST.getConditions(p -> p.withDate().gt(D1)).toString(), matchesPattern(DGT));
		assertThat(FILTER_TEST.getConditions(p -> p.withDate().le(D1)).toString(), matchesPattern(DLE));
		assertThat(FILTER_TEST.getConditions(p -> p.withDate().lt(D1)).toString(), matchesPattern(DLT));
		assertThat(FILTER_TEST.getConditions(p -> p.withDate().between(D1,D2)).toString(), matchesPattern(DBET));
		assertThrows(UnsupportedOperationException.class, () -> FILTER_TEST.getConditions(p -> p.withDate().like(D1)) );
//		assertThat(FILTER_TEST.getConditions(p -> p.withByte().in(new Byte[] {B1,B2})).toString(), matchesPattern(IN));
//		assertThat(FILTER_TEST.getConditions(p -> p.withByte().notIn(new Byte[] {B1,B2})).toString(), matchesPattern(NOT_IN));
	}
	
	public static void main(String[] args) {
		Pattern p =  Pattern.compile(FNAME + 
				" in \\(\\s*[\\\"|\\']1[\\\"|\\']\\s*\\,\\s*[\\\"|\\']2[\\\"|\\']\\s*\\)", Pattern.MULTILINE);
		String a = "\"field\" in (\n  \"1\",\"2\"\n)";
		System.out.println( p.matcher(a).matches() );
						
		assertThat(FILTER_TEST.getConditions(pr -> pr.withString().in(new String[] {S1,S1})).toString(), matchesPattern(p));				
	}
	
}


