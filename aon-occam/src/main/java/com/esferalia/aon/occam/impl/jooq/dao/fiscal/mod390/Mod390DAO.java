package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod390;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel390.FS_MODEL390;

import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod390DAO {

	public static Stream<Mod390> getHeaders(AONContext ctx, int domain) {
		return getHeaders(ctx, domain, null);
	}
	public static Stream<Mod390> getHeaders(AONContext ctx, int domain, Integer scope) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(
				 FS_MODEL390.ID
				,FS_MODEL390.DOMAIN
				,FS_MODEL390.ENTERPRISE
				,FS_MODEL390.YEAR
				,FS_MODEL390.ADMINISTRATION
				,FS_MODEL390.STATUS
				,FS_MODEL390.SECURITY_LEVEL
				,FS_MODEL390.DOCUMENT
				,FS_MODEL390.NAME
				,FS_MODEL390.COMPLEMENTARY
				,FS_MODEL390.REPLACEMENT
				,FS_MODEL390.COMMENTS
				,FS_MODEL390.RECEIPT
				,FS_MODEL390.REPLACED_RECEIPT
				,FS_MODEL390.RESPONSE
				,DOMAIN.DESCRIPTION
			)
			.from(FS_MODEL390)
			.join(DOMAIN).on(FS_MODEL390.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL390.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.and( scope == null ? DSL.trueCondition() : DOMAIN.SCOPE.equal(scope))
			.orderBy(FS_MODEL390.YEAR.desc(), FS_MODEL390.NAME.asc(),FS_MODEL390.REPLACEMENT.asc())
			.fetch()
			.stream()
			.map( new Mod390Filler() );
	}

	public static LinkedList<Mod390> getByDomain(AONContext ctx, int domain) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(
				 FS_MODEL390.ID
				,FS_MODEL390.DOMAIN
				,FS_MODEL390.ENTERPRISE
				,FS_MODEL390.YEAR
				,FS_MODEL390.ADMINISTRATION
				,FS_MODEL390.STATUS
				,FS_MODEL390.SECURITY_LEVEL
				,FS_MODEL390.DOCUMENT
				,FS_MODEL390.NAME
				,FS_MODEL390.COMPLEMENTARY
				,FS_MODEL390.REPLACEMENT
				,FS_MODEL390.COMMENTS
				,FS_MODEL390.RECEIPT
				,FS_MODEL390.REPLACED_RECEIPT
				,FS_MODEL390.RESPONSE
				,DOMAIN.DESCRIPTION
			)
			.from(FS_MODEL390)
			.join(DOMAIN).on(FS_MODEL390.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL390.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.orderBy(FS_MODEL390.YEAR.desc(), FS_MODEL390.NAME.asc(),FS_MODEL390.REPLACEMENT.asc())
			.fetch()
			.stream()
			.map( new Mod390Filler() )
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Mod390 getById(AONContext ctx, int domain,Integer id) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(
				 FS_MODEL390.ID
				,FS_MODEL390.DOMAIN
				,FS_MODEL390.ENTERPRISE
				,FS_MODEL390.YEAR
				,FS_MODEL390.ADMINISTRATION
				,FS_MODEL390.STATUS
				,FS_MODEL390.SECURITY_LEVEL
				,FS_MODEL390.DOCUMENT
				,FS_MODEL390.NAME
				,FS_MODEL390.COMPLEMENTARY
				,FS_MODEL390.REPLACEMENT
				,FS_MODEL390.COMMENTS
				,FS_MODEL390.RECEIPT
				,FS_MODEL390.REPLACED_RECEIPT
				,FS_MODEL390.RESPONSE
				,DOMAIN.DESCRIPTION
			)
			.from(FS_MODEL390)
			.join(DOMAIN).on(FS_MODEL390.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL390.ID.equal(id))
			.and(FS_MODEL390.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.orderBy(FS_MODEL390.YEAR.desc(), FS_MODEL390.NAME.asc(),FS_MODEL390.REPLACEMENT.asc())
			.fetch()
			.stream()
			.map( new Mod390Filler() )
			.findFirst()
			.orElse(null);
	}

	private static class Mod390Filler implements Function<Record, Mod390> {
		
		@Override
		public Mod390 apply(Record record) {
			Mod390 mod390 = new Mod390()
				.setId(record.getValue(FS_MODEL390.ID))
				.setAdministration( com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Administration.class,record.getValue(FS_MODEL390.ADMINISTRATION)))
				.setReplacement( record.getValue(FS_MODEL390.REPLACEMENT)==1 )
				.setComplementary(record.getValue(FS_MODEL390.COMPLEMENTARY)==1 )
				.setStatus(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(FiscalStatus.class,record.getValue(FS_MODEL390.STATUS)))
				.setYear(record.getValue(FS_MODEL390.YEAR))
				.setDomain(record.getValue(FS_MODEL390.DOMAIN))
				.setDomainName(record.getValue(DOMAIN.DESCRIPTION))
				.setEnterprise(record.getValue(FS_MODEL390.ENTERPRISE))
				.setDocument(record.getValue(FS_MODEL390.DOCUMENT))
				.setName(record.getValue(FS_MODEL390.NAME))
				.setEnterpriseName(record.getValue(FS_MODEL390.NAME))
				;
			if (mod390.getYear() < 2015) mod390.setStatus(FiscalStatus.BLOCKED); 
			return mod390;
		}
		
	}

//	public static Mod390 create(AONContext ctx, Mod390 mod390) {
//		if (mod390.getYear() < 2015) {
//			throw new AonCoreException("La generaci\u00F3n de modelos anteriores al ejercicio 2014 no est\u00E1 soportada");
//		} else if (mod390.getYear() == 2015 || mod390.getYear() == 2016 || mod390.getYear() == 2017) {
//			return Mod3902015DAO.create(ctx, mod390);
//		} else if (mod390.getYear() == 2018 || mod390.getYear() == 2019 || mod390.getYear() == 2020) {
//			return Mod3902018DAO.create(ctx, mod390);
//		} 
//		return Mod3902018DAO.create(ctx, mod390);
//	}

	public static Mod390 initialize(AONContext ctx, int year) {
		if (year == 0) {
			Date today = new Date();
			int month = AonDateUtils.getMonth(today);
			year = AonDateUtils.getYear(today);
			if (month < 2) {
				year = year - 1;
			} 
		}
		
		if (year < 2018) {
			throw new AonCoreException("La generaci\u00F3n de modelos anteriores al ejercicio 2018 no est\u00E1 soportada");
		} 
		AonConfiguration conf = ConfigurationDAO.getConfiguration(ctx);
		Mod390 mod390 = new Mod390();
		mod390.setAdministration( conf.fiscal().getAdministration(Administration.COMMON_TERRITORY) );
		mod390.setStatus(FiscalStatus.PENDING);
		mod390.setOldStyle(true);
		mod390.setEnterprise(conf.getCompany().getId());
		mod390.setDomain(ctx.getDomainId());
		mod390.setDocument(conf.getCompany().getDocument());
		mod390.setEnterpriseName(conf.getCompany().getName());
		mod390.setYear( year );
		if (mod390.isLegalEntity()) mod390.setName(mod390.getEnterpriseName());
		else {
			String tmpName = mod390.getEnterpriseName();
			if (AonStringUtils.contains(tmpName, ',')) {
				mod390.setName(AonStringUtils.trim(AonStringUtils.substringAfter(tmpName, ",")));
				mod390.setFirstSurname(AonStringUtils.trim(AonStringUtils.substringBefore(tmpName, ",")));
			} else {
				mod390.setName(AonStringUtils.trim(AonStringUtils.substringBefore(tmpName, " ")));
				mod390.setFirstSurname(AonStringUtils.trim(AonStringUtils.substringAfter(tmpName, " ")));
			}
		}
		return mod390;	
	}

	public static Mod390 saveComments(AONContext ctx, Mod390 mod390) {
		try {
			ctx.checkWrite();
			if (mod390.getId() != null) {
				ctx.getDslContext().update(FS_MODEL390)
					.set(FS_MODEL390.COMMENTS,mod390.getComments())
					.where(FS_MODEL390.ID.equal(mod390.getId()))
					.execute();
			}
			return mod390;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Throwable t) {
			throw new AonCoreException(t.getMessage());
		}
	}
	
	public static void delete(AONContext ctx, Mod390 mod390) {
		ctx.checkWrite();
		ctx.getDslContext().delete(FS_MODEL390)
				.where(FS_MODEL390.ID.equal(mod390.getId())).execute();
	}
	
}
