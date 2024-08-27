package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.CraBatchDetail.CRA_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.CraBatchDetailRecord;
import com.esferalia.aon.jooq.tables.records.GeozoneRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.Filter.EnterpriseCCCFilter;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.EnterpriseCCCPropertiesDAO;


public class EnterpriseCCCDAO {

	private EnterpriseCCCDAO() {
		  throw new IllegalStateException("Utility class");
    }
	
	private static final EnterpriseCCCPropertiesDAO ENTERPRISE_CCC_PROPERTIES = new EnterpriseCCCPropertiesDAO();
	
	public static Stream<EnterpriseCCC> getStream(AONContext ctx, EnterpriseCCCFilter filter) {
		ctx.checkRead();
		Supplier<Stream<EnterpriseCCC>> streamCCC = () -> ctx.getDslContext()
			.select()
			.from(ENTERPRISE_CCC)
			.leftJoin(GEOZONE)
			.on(GEOZONE.ID.eq(ENTERPRISE_CCC.GEOZONE))
			.where(ENTERPRISE_CCC_PROPERTIES.getConditions(filter))
			.orderBy(ENTERPRISE_CCC.ID.desc())
			.fetch().stream().map(new EnterpriseCCCFiller());
		
		streamCCC.get().forEach(enterpriseCCC -> {
			ContractRecord contractRecord = ctx.getDslContext().selectFrom(CONTRACT).where(CONTRACT.ENTERPRISE_CCC.eq(enterpriseCCC.getId())).limit(1).fetchOne();
			enterpriseCCC.setUseByContracts(null != contractRecord);
			
			CraBatchDetailRecord craBatchDetailRecord = ctx.getDslContext().selectFrom(CRA_BATCH_DETAIL).where(CRA_BATCH_DETAIL.ENTERPRISE_CCC.eq(enterpriseCCC.getId())).limit(1).fetchOne();
			enterpriseCCC.setUseByCra(null != craBatchDetailRecord);
		});
		
		return streamCCC.get();
	}
	
	public static List<EnterpriseCCC> getList(AONContext ctx, EnterpriseCCCFilter filter) {
		ctx.checkRead();
		Stream<EnterpriseCCC> streamCCC = ctx.getDslContext()
			.select()
			.from(ENTERPRISE_CCC)
			.leftJoin(GEOZONE)
			.on(GEOZONE.ID.eq(ENTERPRISE_CCC.GEOZONE))
			.where(ENTERPRISE_CCC_PROPERTIES.getConditions(filter))
			.orderBy(ENTERPRISE_CCC.ID.desc())
			.fetch().stream().map(new EnterpriseCCCFiller());
		
		List<EnterpriseCCC> cccs = streamCCC.collect(Collectors.toList());
		
		cccs.forEach(enterpriseCCC -> {
			Result<ContractRecord> contractRecord = ctx.getDslContext().selectFrom(CONTRACT).where(CONTRACT.ENTERPRISE_CCC.eq(enterpriseCCC.getId())).limit(1).fetch();
			enterpriseCCC.setUseByContracts(contractRecord.isNotEmpty());
			
			Result<CraBatchDetailRecord> craBatchDetailRecord = ctx.getDslContext().selectFrom(CRA_BATCH_DETAIL).where(CRA_BATCH_DETAIL.ENTERPRISE_CCC.eq(enterpriseCCC.getId())).limit(1).fetch();
			enterpriseCCC.setUseByCra(craBatchDetailRecord.isNotEmpty());
		});
		
		return cccs;
	}
	
	public static void save(AONContext ctx, List<EnterpriseCCC> enterpriseCccs) {
		if(enterpriseCccs.isEmpty()) return;
		
		enterpriseCccs.forEach(enterpriseCcc -> {
			if((enterpriseCcc.getId() == null || enterpriseCcc.getId() < 0) && !enterpriseCcc.isDeleted()) insert(ctx, enterpriseCcc);
			else if(enterpriseCcc.isDeleted()) delete(ctx, enterpriseCcc.getId());
			else update(ctx, enterpriseCcc);
		});
	}
	
	public static EnterpriseCCC save(AONContext ctx, EnterpriseCCC enterpriseCcc) {
		return enterpriseCcc.getId() != null ? update(ctx, enterpriseCcc) : insert(ctx, enterpriseCcc);
	}
	
	private static EnterpriseCCC insert(AONContext ctx, EnterpriseCCC enterpriseCcc) {
		ctx.checkWrite();
		Integer id = ctx.getDslContext().insertInto(ENTERPRISE_CCC)
				.set(ENTERPRISE_CCC.DOMAIN, enterpriseCcc.getDomain())
				.set(ENTERPRISE_CCC.CCC, enterpriseCcc.getCcc())
				.set(ENTERPRISE_CCC.TYPE, enterpriseCcc.getType())
				.set(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, enterpriseCcc.getEnterpriseActivity())	
				.set(ENTERPRISE_CCC.GEOZONE, checkGeozone(ctx, enterpriseCcc))
			.returning(ENTERPRISE_CCC.ID).fetchOne().getId();
		ctx.log().debug("INSERT ENTERPRISE_CCC id: " + id);		
		return enterpriseCcc.setId(id);
	}
	
	private static EnterpriseCCC update(AONContext ctx, EnterpriseCCC enterpriseCcc) {
		ctx.checkWrite();
		ctx.getDslContext()
			.update(ENTERPRISE_CCC)
			.set(ENTERPRISE_CCC.CCC, enterpriseCcc.getCcc())
			.set(ENTERPRISE_CCC.TYPE, enterpriseCcc.getType())
			.set(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, enterpriseCcc.getEnterpriseActivity())	
			.set(ENTERPRISE_CCC.GEOZONE, checkGeozone(ctx, enterpriseCcc))
			.where(ENTERPRISE_CCC.ID.eq(enterpriseCcc.getId()))
			.execute();		
		ctx.log().debug("UPDATE ENTERPRISE_CCC id: " + enterpriseCcc.getId());		
		return enterpriseCcc;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		ctx.getDslContext().delete(ENTERPRISE_CCC).where(ENTERPRISE_CCC.ID.eq(id)).execute();	
		ctx.log().debug("DELETE ENTERPRISE_CCC id: " + id);		
	}
	
	public static EnterpriseCCC get(AONContext ctx, EnterpriseCCCFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext()
				.select()
				.from(ENTERPRISE_CCC)
				.where(ENTERPRISE_CCC_PROPERTIES.getConditions(filter))
				.stream()
				.map( new EnterpriseCCCFiller() )
				.findFirst()
				.orElse(null);
	}
	
	private static Integer checkGeozone(AONContext ctx, EnterpriseCCC enterpriseCcc) {
		if(null != enterpriseCcc.getGeozone()) return enterpriseCcc.getGeozone();
		
		Record1<Integer> geozone = ctx.getDslContext().select(GEOZONE.ID)
				.from(GEOZONE)
				.where(GEOZONE.CODE.eq(enterpriseCcc.getGeozoneCode()))
					.and(GEOZONE.DOMAIN.eq(enterpriseCcc.getDomain()))
				.fetchOne();
		
		Integer geozoneId = null;
		
		if(geozone == null){
			Result<Record1<String>> names = ctx.getDslContext().select(GEOZONE.NAME)
				.from(GEOZONE)
				.where(GEOZONE.CODE.eq(enterpriseCcc.getGeozoneCode()))
				.fetch();
			
			if(!names.isEmpty()){
				GeozoneRecord geozoneRecord  = ctx.getDslContext().insertInto(GEOZONE)
						.set(GEOZONE.DOMAIN, enterpriseCcc.getDomain())
						.set(GEOZONE.NAME, names.get(0).value1())
						.set(GEOZONE.CODE, enterpriseCcc.getGeozoneCode())
						.returning(GEOZONE.ID)
						.fetchOne();
					
				geozoneId = geozoneRecord.getId();
			}
		}else
			geozoneId = geozone.value1();
		
		return geozoneId;
	}
	
	public static class EnterpriseCCCFiller  implements Function<Record, EnterpriseCCC> {
		@Override
		public EnterpriseCCC apply(Record r) {
			return new EnterpriseCCC()
					.setId(r.getValue(ENTERPRISE_CCC.ID))
					.setDomain(r.getValue(ENTERPRISE_CCC.DOMAIN))
					.setCcc(r.getValue(ENTERPRISE_CCC.CCC))
					.setGeozone(r.getValue(ENTERPRISE_CCC.GEOZONE))
					.setGeozoneCode(r.getValue(GEOZONE.CODE))
					.setGeozoneDescription(r.getValue(GEOZONE.NAME))
					.setEnterpriseActivity(r.getValue(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY))
					.setType(r.getValue(ENTERPRISE_CCC.TYPE))
					.setDeleted(false)
					.setUseByContracts(false)
					.setUseByCra(false)
					;
		}
	}
	
}
