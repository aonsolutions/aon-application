package com.esferalia.aon.occam.mod200.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel200.FS_MODEL200;
import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.mod200.api.model.Mod200;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyAdministrator;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class Mod200DAO {
	
	public static Stream<Mod200> getMod200s(AONContext ctx,int domain) {
		ctx.checkRead();
		return ctx.getDslContext()
				.select()
				.from(FS_MODEL200)
				.join(DOMAIN).on(FS_MODEL200.DOMAIN.equal(DOMAIN.ID))
//				.leftOuterJoin(FINANCE).on(FINANCE.ID.equal(FS_MODEL.FINANCE))
//				.leftOuterJoin(REGISTRY).on(REGISTRY.ID.equal(FINANCE.REGISTRY))
//				.leftOuterJoin(SCOPE).on(FINANCE.SCOPE.equal(SCOPE.ID))
//				.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
				.where(FS_MODEL200.DOMAIN.eq(domain))
				.orderBy(FS_MODEL200.YEAR.desc(),FS_MODEL200.ID.desc())
				.fetch()
				.stream()
				.map( new Mod200Filler() )
				;
	}
	
	public static class Mod200Filler implements Function<Record,Mod200> {

		@Override
		public Mod200 apply(Record record) {
			return new Mod200() 
				.setId(record.getValue(FS_MODEL200.ID))
				.setDomain(record.getValue(FS_MODEL200.DOMAIN))
				.setDomainName(record.getValue(DOMAIN.DESCRIPTION))
				.setYear(record.getValue(FS_MODEL200.YEAR))
				.setAdministration(Administration.safeValueOf(record.getValue(FS_MODEL200.ADMINISTRATION)))
				.setStatus(FiscalStatus.safeValueOf(record.getValue(FS_MODEL200.STATUS)))
				
				// TODO - Support
				.setFinance(null)
				
				.setComplementary( AonEnumUtils.getBoolean( record.getValue(FS_MODEL200.COMPLEMENTARY)))
				.setReplacement( false )
				.setNumber(record.getValue(FS_MODEL200.RECEIPT ))
				.setReplacedNumber(record.getValue(FS_MODEL200.COMPLEMENTARY_RECEIPT))
				.setComments(record.getValue(FS_MODEL200.COMMENTS ))
				.setDocument(record.getValue(FS_MODEL200.DOCUMENT ))
				.setName(record.getValue(FS_MODEL200.NAME))
				.setResultType(record.getValue(FS_MODEL200.RESULT_TYPE))
				.setResult(record.getValue(FS_MODEL200.AMOUNT) == null? 0.0 : record.getValue(FS_MODEL200.AMOUNT) )

				.setCreationUser(record.getValue(FS_MODEL200.CREATION_USER))
				.setCreationDate(record.getValue(FS_MODEL200.CREATION_DATE))
				.setModificationUser(record.getValue(FS_MODEL200.MODIFICATION_USER))
				.setModificationDate(record.getValue(FS_MODEL200.MODIFICATION_DATE))				
			;
		}
	}

	public static LinkedList<Mod200CompanyAdministrator> getDirStaff(AONContext ctx,int domain) {
		return ctx.getDslContext()
				.select(RDIR_STAFF.DOCUMENT,RDIR_STAFF.NAME,RDIR_STAFF.DIRECTOR,RDIR_STAFF.SHAREHOLDER,RDIR_STAFF.PERCENT_SHARE,RDIR_STAFF.NOMINAL_VALUE,RDIR_STAFF.REPRESENTATIVE)
				.from(COMPANY)
				.join(RDIR_STAFF).on( COMPANY.REGISTRY.equal(RDIR_STAFF.REGISTRY) )
				.where(COMPANY.DOMAIN.equal(domain))
				.fetch()
				.stream()
				.map( rec -> new Mod200CompanyAdministrator()
						.setDocument(rec.getValue(RDIR_STAFF.DOCUMENT) )
						.setName(rec.getValue(RDIR_STAFF.NAME) )
						.setShareholder( rec.getValue(RDIR_STAFF.SHAREHOLDER) == 1 )
						.setAdministrator( rec.getValue(RDIR_STAFF.DIRECTOR) == 1 )
						.setPercent(rec.getValue(RDIR_STAFF.PERCENT_SHARE) )
						.setNominalValue(rec.getValue(RDIR_STAFF.NOMINAL_VALUE)) 
						.setRepresentative( rec.getValue(RDIR_STAFF.REPRESENTATIVE) == 1 )
					)
				.collect(Collectors.toCollection(LinkedList::new ));
	}

	public static Mod200 saveComments(AONContext ctx, Mod200 mod200) {
		try {
			ctx.checkWrite();
			if (mod200.getId() != null) {
				ctx.getDslContext().update(FS_MODEL200)
					.set(FS_MODEL200.COMMENTS,mod200.getComments())
					.where(FS_MODEL200.ID.equal(mod200.getId()))
					.execute();
			}
			return mod200;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Exception t) {
			throw new AonCoreException(t.getMessage());
		}
	}
	
}
