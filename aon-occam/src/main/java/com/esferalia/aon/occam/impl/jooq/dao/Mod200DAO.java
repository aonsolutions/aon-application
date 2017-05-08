package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.FsModel200.FS_MODEL200;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class Mod200DAO extends FiscalModelDAO {
	
	public static Stream<Mod200> getMod200s(AONContext ctx,int domain) {
		ctx.checkRead();
		return ctx.getDslContext()
				.select()
				.from(FS_MODEL200)
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
	
	public static class Mod200Filler  implements Function<Record,Mod200> {

		@Override
		public Mod200 apply(Record record) {
			return new Mod200() 
				.setId(record.getValue(FS_MODEL200.ID))
				.setDomain(record.getValue(FS_MODEL200.DOMAIN))
				.setDomainName(null)
				.setYear(record.getValue(FS_MODEL200.YEAR))
				.setAdministration(Administration.safeValueOf(record.getValue(FS_MODEL200.ADMINISTRATION)))
				
				// TODO - Support
				.setStatus( FiscalStatus.PENDING )
				
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
				.setResult(record.getValue(FS_MODEL200.AMOUNT))
				
				// TODO - Support
				.setCreationUser(null)
				.setCreationDate(null)
				.setModificationUser(null)
				.setModificationDate(null)
			;
		}
	}

}
