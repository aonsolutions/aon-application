package com.esferalia.aon.occam.impl.jooq.dao.console;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.sql.Timestamp;
import java.util.Date;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

public class ConsoleDAO {
	
	private static final String INFORMATION_SCHEMA = "information_schema";
	private static final String MYSQL = "mysql";
	private static final String PERFORMANCE_SCHEMA = "performance_schema";
	
	protected ConsoleDAO() {
	}
	
	public static Stream<Domain> getDomains(CloseableAONContext ctx, DomainParams params) {
		Field<Integer> count = DSL.count().as("userCount");
		Field<Integer> userDomain = USER.DOMAIN.as("userCountDomain");
		Table<Record2<Integer,Integer>> userCount = ctx.getDslContext().select(userDomain,count)
			.from(USER)
			.where( USER.ACTIVE.eq((byte) 1) )
			.groupBy(userDomain)
			.asTable();
		return ctx.getDslContext().select()
			.from(DOMAIN)
			.leftOuterJoin(userCount).on(DOMAIN.ID.eq(userDomain))
			.where( getFilter(params) )
			.offset(params.getOffset())
			.limit(params.getLimit())
			.fetch()
			.stream()
			.map(rec -> new Pair<>(rec, new DomainFiller().apply(rec)) )
			.map( pair -> pair.getRight().setDefinedUsers( AonNumberUtils.zeroIfNull(pair.getLeft().getValue(count)) ) );
	}
	
	public static Stream<Schema> getSchemas(AONContext ctx) {
		return  ctx.getDslContext()
			.meta()
			.getSchemas()
			.stream();
	}
	
	public static Stream<Schema> getAONSchemas(AONContext ctx) {
		return getSchemas(ctx)
			.filter(schema -> !INFORMATION_SCHEMA.equals(schema.getName()))
			.filter(schema -> !MYSQL.equals(schema.getName()))
			.filter(schema -> !PERFORMANCE_SCHEMA.equals(schema.getName()))
			;			
	}

	public static Domain changeActive(CloseableAONContext ctx, Integer domainId, boolean active) {
		int count = ctx.getDslContext()
			.update(DOMAIN)
			.set(DOMAIN.ACTIVE, AonEnumUtils.getByte( active ))
			.where(DOMAIN.ID.eq(domainId))
			.execute();
		Domain dom = DomainDAO.getDomain(ctx, domainId);
		if (dom != null) {
			ctx.log().info("Domain {0} - {1} - Active changed --> {2} ({3} rows)"
					,dom.getId()
					,dom.getName()
					,dom.isActive()
					,count
					);
		} else {
			ctx.log().info("Domain active not changed");
		}
		return dom;
	}

	public static Domain changeExpirationDate(CloseableAONContext ctx, Integer domainId, Date expireDate) {
		int count = ctx.getDslContext()
			.update(DOMAIN)
			.set(DOMAIN.EXPIRATIONDATE, AonDateUtils.toSql( expireDate ))
			.where(DOMAIN.ID.eq(domainId))
			.execute();
		Domain dom = DomainDAO.getDomain(ctx, domainId);
		if (dom != null) {
			ctx.log().info("Domain {0} - {1} - Expiration Date changed --> {2} ({3} rows)"
					,dom.getId()
					,dom.getName()
					,dom.getExpirationDate()
					,count
					);
		} else {
			ctx.log().info("Domain active not changed");
		}
		return dom;
	}
	private static Condition and(Condition a, Condition b) {
		return (a==null)?b:a.and(b);
	}
	private static Condition getFilter(DomainParams params) {
		Condition c = null;
		if (params.getId() != null ) {
			c = and(c, DOMAIN.ID.eq(params.getId()));
		}
		if (AonStringUtils.isNotBlank(params.getQuery())) {
			String q = AonStringUtils.PERCENT + params.getQuery() + AonStringUtils.PERCENT;
			c = and(c, DOMAIN.NAME.like(q).or(DOMAIN.DESCRIPTION.like(q))); 
		}
		if (params.getParent() != null ) {
			c = and(c, DOMAIN.PARENT.eq(params.getParent())); 
		}
		if (params.getOrphan() != null ) {
			if (params.getOrphan().booleanValue()) {
				c = and(c, DOMAIN.PARENT.isNull());
			} else {
				c = and(c, DOMAIN.PARENT.isNotNull());
			}
		}
		if (params.getType() != null ) {
			c = and(c, DOMAIN.TYPE.eq(params.getType().byteValue())); 
		}
		if (params.getActive() != null ) {
			c = and(c, DOMAIN.ACTIVE.eq(AonEnumUtils.getByte(params.getActive()))); 
		}
		if (params.getEnableHeredity() != null ) {
			c = and(c, DOMAIN.ENABLEHEREDITY.eq(AonEnumUtils.getByte(params.getEnableHeredity()))); 
		}
		if (params.getDomainManagement() != null ) {
			c = and(c, DOMAIN.DOMAINMANAGEMENT.eq(AonEnumUtils.getByte(params.getDomainManagement()))); 
		}
		if (params.getFromLastAccess() != null ) {
			c = and(c, DOMAIN.LASTACCESS_DATE.ge(new Timestamp( params.getFromLastAccess().getTime()))); 
		}
		if (params.getToLastAccess() != null ) {
			c = and(c, DOMAIN.LASTACCESS_DATE.le(new Timestamp( params.getToLastAccess().getTime()))); 
		}
		
		if (params.getFromExpirationDate() != null ) {
			c = and(c, DOMAIN.EXPIRATIONDATE.ge(AonDateUtils.toSql(params.getFromExpirationDate()))); 
		}
		if (params.getToExpirationDate() != null ) {
			c = and(c, DOMAIN.EXPIRATIONDATE.le(AonDateUtils.toSql(params.getToExpirationDate())));
		}
		c = c==null?DSL.trueCondition():c;
		return c;
	}
}
