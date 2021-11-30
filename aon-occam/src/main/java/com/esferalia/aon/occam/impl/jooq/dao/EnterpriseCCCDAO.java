package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Record;

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
		return ctx.getDslContext()
			.select()
			.from(ENTERPRISE_CCC)
			.where(ENTERPRISE_CCC_PROPERTIES.getConditions(filter))
			.orderBy(ENTERPRISE_CCC.ID.desc())
			.fetch().stream().map(new EnterpriseCCCFiller());
	}
	
	public static EnterpriseCCC save(AONContext ctx, EnterpriseCCC enterpriseCcc) {
		return enterpriseCcc.getId() !=null ? update(ctx, enterpriseCcc) : insert(ctx, enterpriseCcc);
	}
	
	private static EnterpriseCCC insert(AONContext ctx, EnterpriseCCC enterpriseCcc) {
		ctx.checkWrite();
		Integer id = ctx.getDslContext().insertInto(ENTERPRISE_CCC)
				.set(ENTERPRISE_CCC.DOMAIN, enterpriseCcc.getDomain())
				.set(ENTERPRISE_CCC.CCC, enterpriseCcc.getCcc())
				.set(ENTERPRISE_CCC.TYPE, enterpriseCcc.getType())
				.set(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, enterpriseCcc.getEnterpriseActivity())	
				.set(ENTERPRISE_CCC.GEOZONE,enterpriseCcc.getGeozone())
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
			.set(ENTERPRISE_CCC.GEOZONE,enterpriseCcc.getGeozone())
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
	


	
	public static class EnterpriseCCCFiller  implements Function<Record, EnterpriseCCC> {
		@Override
		public EnterpriseCCC apply(Record r) {
			return new EnterpriseCCC()
					.setId(r.getValue(ENTERPRISE_CCC.ID))
					.setDomain(r.getValue(ENTERPRISE_CCC.DOMAIN))
					.setCcc(r.getValue(ENTERPRISE_CCC.CCC))
					.setGeozone(r.getValue(ENTERPRISE_CCC.GEOZONE))
					.setEnterpriseActivity(r.getValue(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY))
					.setType(r.getValue(ENTERPRISE_CCC.TYPE))
					;
		}
	}
	
}
