package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel390.FS_MODEL390;

import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import com.esferalia.aon.jooq.tables.records.FsModel390Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod390DAO {

	public static LinkedList<Mod390> getByDomain(AONContext ctx, int domain) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(FS_MODEL390.fields())
			.from(FS_MODEL390)
			.join(DOMAIN).on(FS_MODEL390.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL390.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.orderBy(FS_MODEL390.YEAR.desc(), FS_MODEL390.NAME.asc(),FS_MODEL390.REPLACEMENT.asc())
			.fetchInto(FsModel390Record.class)
			.stream()
			.map( new Mod390Filler() )
			.collect(Collectors.toCollection(LinkedList::new));
	}

	private static class Mod390Filler implements Function<Record, Mod390> {
		
		@Override
		public Mod390 apply(Record record) {
			return new Mod390()
				.setId(record.getValue(FS_MODEL390.ID))
				.setAdministration( com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Administration.class,record.getValue(FS_MODEL390.ADMINISTRATION)))
				.setReplacement( record.getValue(FS_MODEL390.REPLACEMENT)==1 )
				.setComplementary(record.getValue(FS_MODEL390.COMPLEMENTARY)==1 )
				.setStatus(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(FiscalStatus.class,record.getValue(FS_MODEL390.STATUS)))
				.setYear(record.getValue(FS_MODEL390.YEAR))
				.setDomain(record.getValue(FS_MODEL390.DOMAIN))
				.setEnterprise(record.getValue(FS_MODEL390.ENTERPRISE))
				.setDocument(record.getValue(FS_MODEL390.DOCUMENT))
				.setName(record.getValue(FS_MODEL390.NAME))
				.setEnterpriseName(record.getValue(FS_MODEL390.NAME))
				;
		}
		
	}

	public static Mod390 create(AONContext ctx, Mod390 mod390) {
		if (mod390.getYear() < 2014) {
			throw new AonCoreException("La generaci\u00F3n de modelos anteriores al ejercicio 2014 no est\u00E1 soportada");
		} else if (mod390.getYear() == 2014) {
			return Mod3902014DAO.create(ctx, mod390);
		} 
		return Mod3902015DAO.create(ctx, mod390);
	}

	public static Mod390 initialize(AONContext ctx, int year) {
		if (year == 0) {
			Date today = new Date();
			int month = AonDateUtils.getMonth(today);
			year = AonDateUtils.getYear(today);
			if (month < 2) {
				year = year - 1;
			}
		}
		
		if (year < 2014) {
			throw new AonCoreException("La generaci\u00F3n de modelos anteriores al ejercicio 2014 no est\u00E1 soportada");
		} 
		FiscalParameters params = AppParamDAO.getFiscalParameters(ctx);
		Mod390 mod390 = new Mod390();
		mod390.setAdministration( params.getAdministration(Administration.COMMON_TERRITORY) );
		mod390.setStatus(FiscalStatus.PENDING);
		mod390.setOldStyle(true);
		mod390.setEnterprise(params.getCompany());
		mod390.setDomain(ctx.getDomainId());
		mod390.setDocument(params.getDocument());
		mod390.setEnterpriseName(params.getName());
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
	
}
