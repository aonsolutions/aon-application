package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Geotree.GEOTREE;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.jooq.tables.records.GeozoneRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.Filter.EnterpriseFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.EnterpriseProperties;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.payroll.Enterprise;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class EnterpriseDAO {
	
	// -------------------------------------- Constructor

	private EnterpriseDAO() {
		throw new IllegalStateException("Utility Class");
	}
	
	// -------------------------------------- Enterprise Properties
	
	private static final EnterprisePropertiesDAO ENTERPRISE_PROPERTIES = new EnterprisePropertiesDAO();
	protected static class EnterprisePropertiesDAO implements EnterpriseProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, EnterpriseFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(EnterpriseFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE.REGISTRY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE.DOMAIN);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE.SCOPE);}
		@Override public Property<Integer> getCalendarProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE.CALENDAR);}
	}
	
	// -------------------------------------- Enterprise Filler
	
	public static class EnterpriseFiller extends Filler implements Function<Record, Enterprise> {

		@Override
		public Enterprise apply(Record r) {
			return new Enterprise()
					.setId(r.getValue(ENTERPRISE.REGISTRY))
					.setDomain(r.getValue(ENTERPRISE.DOMAIN))
					.setCalendar(r.getValue(ENTERPRISE.CALENDAR))
					.setScope(r.getValue(ENTERPRISE.SCOPE))
					.setDocumentType(AonEnumUtils.enumValue(DocumentType.class,r.getValue(REGISTRY.DOCUMENT_TYPE)))
					.setDocumentCountry(Country.safeValueOf(r.getValue(REGISTRY.DOCUMENT_COUNTRY)))
					.setDocument(r.getValue(REGISTRY.DOCUMENT))
					.setName(r.getValue(REGISTRY.NAME))
					.setAlias(r.getValue(REGISTRY.ALIAS))
					;
		}
	}
	
	// -------------------------------------- CRUD Methods
	
	private static SelectConditionStep<Record> select(AONContext ctx, EnterpriseFilter filter) {
		return ctx.getDslContext().select().from(ENTERPRISE)
			.join(REGISTRY).on(ENTERPRISE.REGISTRY.equal(REGISTRY.ID))
			.where(ENTERPRISE_PROPERTIES.getConditions(filter));
	}
	
	public static Optional<Enterprise> opt(AONContext ctx, EnterpriseFilter filter) {
		return select(ctx, filter)
			.limit(1)
			.fetch()
			.stream()
			.map(new EnterpriseFiller())
			.findFirst();
	}
	
	public static Enterprise get(AONContext ctx, EnterpriseFilter filter) {
		ctx.checkRead();
		
		Enterprise enterprise = select(ctx, filter)
			.limit(1)
			.stream()
			.map(new EnterpriseFiller())
			.findFirst()
			.orElse(new Enterprise());
		
		RegistryAddress registryAddress = RegistryAddressDAO.get(ctx, f -> f.getRegistryProperty().eq(enterprise.getId()));
		if (null != registryAddress && null != registryAddress.getStreetType()) registryAddress.setStreetType(StreetType.getForAeatCode(registryAddress.getStreetType().getAeatCode(), AonLanguage.SPANISH));
		enterprise.setAddress(registryAddress);
		
		List<RegistryMedia> medias = RegistryMediaDAO.getStream(ctx, f -> f.getRegistryProperty().eq(enterprise.getId())).collect(Collectors.toList());
		enterprise.setMedias(medias);
		
		enterprise.setDatas(
			EnterpriseDataDAO.streamByEnterprise(ctx, enterprise.getId() )
				.collect(Collectors.toCollection( LinkedList::new ))
		);
		
		return enterprise;
	}
	
	public static Enterprise save(AONContext ctx, Enterprise ct) {
		return ct.getId() != null ? update(ctx, ct) : insert(ctx, ct);
	}
	
	private static Enterprise insert(AONContext ctx, Enterprise enterprise) {
		ctx.checkWrite();

		Integer id = ctx.getDslContext().insertInto(ENTERPRISE)
			.set(ENTERPRISE.DOMAIN, enterprise.getDomain())
			.set(ENTERPRISE.SCOPE, enterprise.getScope())
			.set(ENTERPRISE.CALENDAR, enterprise.getCalendar())
			.returning(ENTERPRISE.REGISTRY).fetchOne().getRegistry();
			ctx.log().debug("INSERT ENTERPRISE id: " + id);	
			
		ctx.getDslContext().insertInto(REGISTRY)
			.set(REGISTRY.DOMAIN, enterprise.getDomain())
			.set(REGISTRY.DOCUMENT, enterprise.getDocument())
			.set(REGISTRY.DOCUMENT_TYPE, (byte) enterprise.getDocumentType().ordinal())
			.set(REGISTRY.DOCUMENT_COUNTRY, enterprise.getDocumentCountry().getIso2())
			.set(REGISTRY.NAME, enterprise.getName())
			.set(REGISTRY.ALIAS, enterprise.getAlias())
			.set(REGISTRY.NATIONALITY, enterprise.getDocumentCountry().getIso2())
			.returning(REGISTRY.ID).fetchOne().getId();
			ctx.log().debug("INSERT REGISTRY id: " + id);	
			
		saveRegistryAddress(ctx.getDslContext(), enterprise);
		enterprise.getMedias().forEach(media -> RegistryMediaDAO.save(ctx, media));
		EnterpriseDataDAO.save(ctx, enterprise.getDatas());
		
		return enterprise.setId(id);
	}

	private static Enterprise update(AONContext ctx, Enterprise enterprise) {
		ctx.checkWrite();
		ctx.getDslContext()
			.update(ENTERPRISE)
			.set(ENTERPRISE.CALENDAR, enterprise.getCalendar())
			.set(ENTERPRISE.SCOPE, enterprise.getScope())
			.where(ENTERPRISE.REGISTRY.eq(enterprise.getId()))
			.execute();		
		ctx.log().debug("UPDATE ENTERPRISE id: " + enterprise.getId());	
		ctx.getDslContext()
			.update(REGISTRY)
			.set(REGISTRY.DOCUMENT, enterprise.getDocument())
			.set(REGISTRY.DOCUMENT_TYPE, (byte) enterprise.getDocumentType().ordinal())
			.set(REGISTRY.DOCUMENT_COUNTRY, enterprise.getDocumentCountry().getIso2())
			.set(REGISTRY.NAME, enterprise.getName())
			.set(REGISTRY.ALIAS, enterprise.getAlias())
			.set(REGISTRY.NATIONALITY, enterprise.getDocumentCountry().getIso2())
			.where(REGISTRY.ID.eq(enterprise.getId()))
			.execute();
		ctx.log().debug("UPDATE REGISTRY id: " + enterprise.getId());
		
		saveRegistryAddress(ctx.getDslContext(), enterprise);
		enterprise.getMedias().forEach(media -> RegistryMediaDAO.save(ctx, media));
		EnterpriseDataDAO.save(ctx, enterprise.getDatas());
		
		return enterprise;
	}
	
	public static void updatrEnterpriseScope(AONContext ctx, Domain domain) {
		ctx.getDslContext()
			.update(ENTERPRISE)
			.set(ENTERPRISE.SCOPE, domain.getScope())
			.where(ENTERPRISE.DOMAIN.eq(domain.getId()))
			.execute();
	}
	
	
	private static void saveRegistryAddress(DSLContext dslContext, Enterprise enterprise) {
		Record1<Integer> geozone = dslContext.select(GEOZONE.ID)
				.from(GEOZONE)
				.where(GEOZONE.CODE.eq(enterprise.getAddress().getProvince()))
					.and(GEOZONE.DOMAIN.eq(enterprise.getDomain()))
				.fetchOne();
		
		Integer geozoneId = null;
		
		if(geozone == null){
			Result<Record1<String>> names = dslContext.select(GEOZONE.NAME)
				.from(GEOZONE)
				.where(GEOZONE.CODE.eq(enterprise.getAddress().getProvince()))
				.fetch();
			
			if(!names.isEmpty()){
				GeozoneRecord geozoneRecord  = dslContext.insertInto(GEOZONE)
						.set(GEOZONE.DOMAIN, enterprise.getDomain())
						.set(GEOZONE.NAME, names.get(0).value1())
						.set(GEOZONE.CODE, enterprise.getAddress().getProvince())
						.returning(GEOZONE.ID)
						.fetchOne();
					
				geozoneId = geozoneRecord.getId();
			}
		}else
			geozoneId = geozone.value1();

		Integer rAddressId = enterprise.getAddress().getId();
		
		if(null == rAddressId){
			RaddressRecord rAddressRecord = dslContext.insertInto(RADDRESS)
					.set(RADDRESS.DOMAIN, enterprise.getDomain())
					.set(RADDRESS.REGISTRY,  enterprise.getId())
					.set(RADDRESS.STREET_TYPE, enterprise.getAddress().getStreetType().getAeatCode())
					.set(RADDRESS.ADDRESS, enterprise.getAddress().getAddress())
					.set(RADDRESS.NUMBER, enterprise.getAddress().getNumber())
					.set(RADDRESS.ZIP, enterprise.getAddress().getZip())
					.set(RADDRESS.CITY, enterprise.getAddress().getCity())
					.set(RADDRESS.MUNICIPALITY_CODE, enterprise.getAddress().getMunicipalityCode())
					.set(RADDRESS.GEOZONE, geozoneId)
					.returning(RADDRESS.ID, RADDRESS.GEOZONE)
					.fetchOne();
			
			rAddressId = rAddressRecord.getId();
		}else{
			dslContext.update(RADDRESS)
					.set(RADDRESS.STREET_TYPE, enterprise.getAddress().getStreetType().getAeatCode())
					.set(RADDRESS.ADDRESS, enterprise.getAddress().getAddress())
					.set(RADDRESS.NUMBER, enterprise.getAddress().getNumber())
					.set(RADDRESS.ZIP, enterprise.getAddress().getZip())
					.set(RADDRESS.CITY, enterprise.getAddress().getCity())
					.set(RADDRESS.MUNICIPALITY_CODE, enterprise.getAddress().getMunicipalityCode())
					.set(RADDRESS.GEOZONE, geozoneId)
					.where(RADDRESS.ID.eq(rAddressId))
					.execute();
		}
		
		if(geozone == null && null != geozoneId){
			Integer rAddressGeozone = geozoneId;
			
			GeozoneRecord geozoneParentRecord  = dslContext.insertInto(GEOZONE)
					.set(GEOZONE.DOMAIN, enterprise.getDomain())
					.set(GEOZONE.NAME, "ESPA\u00d1A")
					.set(GEOZONE.CODE, "ES")
					.set(GEOZONE.SYSTEM, (byte) 1)
					.returning(GEOZONE.ID)
					.fetchOne();
			
			Integer geozoneParentId = geozoneParentRecord.getId();
			
			dslContext.insertInto(GEOTREE)
			.set(GEOTREE.DOMAIN, enterprise.getDomain())
			.set(GEOTREE.PARENT, geozoneParentId)
			.set(GEOTREE.CHILD, rAddressGeozone)
			.execute();
			
			dslContext.insertInto(GEOTREE)
			.set(GEOTREE.DOMAIN, enterprise.getDomain())
			.set(GEOTREE.PARENT, (Integer) null)
			.set(GEOTREE.CHILD, geozoneParentId)
			.execute();
		}
	}
	
}
