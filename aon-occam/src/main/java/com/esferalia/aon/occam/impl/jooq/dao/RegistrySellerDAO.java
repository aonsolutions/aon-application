package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Rseller.RSELLER;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;

import java.sql.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import  org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.jooq.tables.records.RsellerRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RegistrySellerFilter;
import com.esferalia.aon.occam.api.model.Properties.RegistrySellerProperties;
import com.esferalia.aon.occam.api.model.registry.RegistrySeller;
import com.esferalia.aon.occam.api.model.type.RegistrySellerStatus;
import com.esferalia.aon.occam.api.model.type.RegistrySellerType;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SellerDAO.SellerFiller;
import com.esferalia.aon.watson.server.AonDateUtils;

public class RegistrySellerDAO {
    
    private RegistrySellerDAO() {

    }
	
    public static final com.esferalia.aon.jooq.tables.Registry SELLER_ALIAS = SellerDAO.SELLER_COMERCIAL_ALIAS;
	private static final RegistrySellerPropertiesDAO REGISTRY_SELLER_PROPERTIES = new RegistrySellerPropertiesDAO();
	
	public static class RegistrySellerPropertiesDAO implements RegistrySellerProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, RegistrySellerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(RegistrySellerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(RSELLER.REGISTRY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RSELLER.DOMAIN);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(RSELLER.STATUS);}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RSELLER.ID);}
		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<>(RSELLER.SELLER);}
		@Override public Property<Date> getStartDateProperty() {return new FilterDAO.PropertyDAO<>(RSELLER.START_DATE);}
		@Override public Property<Date> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(RSELLER.END_DATE);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(RSELLER.TYPE);}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, RegistrySellerFilter filter) {
		return ctx.getDslContext().select()
				.from(RSELLER)
				.innerJoin(DOMAIN).on(DOMAIN.ID.eq(RSELLER.DOMAIN))
				.innerJoin(SELLER).on(SELLER.REGISTRY.eq(RSELLER.SELLER))
				.innerJoin(SELLER_ALIAS).on(SELLER.REGISTRY.eq(SELLER_ALIAS.ID))
				.where(REGISTRY_SELLER_PROPERTIES.getConditions(filter));	
	}

	public static RegistrySeller get(AONContext ctx, RegistrySellerFilter filter){
		return select(ctx, filter).limit(1).fetch().stream().map(new RegistrySellerFiller())
			.findFirst()
			.orElse(new RegistrySeller());
	}
	
	public static RegistrySeller get(AONContext ctx, Integer id){
		return get(ctx, f -> f.getRegistryProperty().eq(id));
	}
	
	public static Stream<RegistrySeller> getStream(AONContext ctx, RegistrySellerFilter filter){
		return select(ctx, filter)
			.fetch().stream().map(new RegistrySellerFiller());
	}	
	
	public static Stream<RegistrySeller> getStream(AONContext ctx, RegistrySellerFilter filter, int offset, int limit) {
		return select(ctx, filter)
				.limit(limit)
				.offset(offset)
				.fetch().stream().map(new RegistrySellerFiller());
	}	
	
	public static RegistrySeller save(AONContext ctx, RegistrySeller registrySeller) {
		ctx.checkWrite();
		// TODO AUTOCOMPLETE && VALIDATION
		boolean nullId = (registrySeller.getId() == null); 
		return nullId || get(ctx, registrySeller.getId()).getId() == null
			? insert(ctx, registrySeller) : update(ctx, registrySeller);
	}

	private static RegistrySeller insert(AONContext ctx, RegistrySeller registrySeller) {
		RsellerRecord inserted = ctx.getDslContext().insertInto(RSELLER)
			.set(RSELLER.ID, registrySeller.getId())
			.set(RSELLER.DOMAIN, registrySeller.getDomain().getId())
			.set(RSELLER.SELLER, registrySeller.getSeller().getId())
			.set(RSELLER.REGISTRY, registrySeller.getRegistry())
			.set(RSELLER.START_DATE, AonDateUtils.toSql(registrySeller.getStartDate()))
			.set(RSELLER.END_DATE, AonDateUtils.toSql(registrySeller.getEndDate()))
			.set(RSELLER.STATUS, registrySeller.getStatus().value())
			.set(RSELLER.TYPE, registrySeller.getType() != null ? registrySeller.getType().value() : null)
			.returning(RSELLER.ID, RSELLER.TYPE)
			.fetchOne();
			if (inserted != null) {
				registrySeller.setId(inserted.getId()).setType(RegistrySellerType.safeValueOf(inserted.getType()));
			}
		return registrySeller;
	}
	
	private static RegistrySeller update(AONContext ctx, RegistrySeller registrySeller){
		ctx.checkWrite();
		int count = ctx.getDslContext().update(RSELLER)
			.set(RSELLER.DOMAIN, registrySeller.getDomain().getId())
			.set(RSELLER.SELLER, registrySeller.getSeller().getId())
			.set(RSELLER.REGISTRY, registrySeller.getRegistry())
			.set(RSELLER.START_DATE, AonDateUtils.toSql(registrySeller.getStartDate()))
			.set(RSELLER.END_DATE, AonDateUtils.toSql(registrySeller.getEndDate()))
			.set(RSELLER.STATUS, registrySeller.getStatus().value())
			.set(RSELLER.TYPE, registrySeller.getType() != null ? registrySeller.getType().value() : null)
			.where(RSELLER.REGISTRY.eq(registrySeller.getId()))
			.execute();
		ctx.log().info("UPDATE RSELLER id: " + registrySeller.getId() + ". (" + count + " rows)");		
		return registrySeller;
	}
	
	public static int delete(AONContext ctx, Integer id){
		return delete(ctx, f -> f.getRegistryProperty().eq(id));
	}
	
	public static int delete(AONContext ctx, RegistrySellerFilter filter){
		return ctx.getDslContext().delete(RSELLER)
		.where(REGISTRY_SELLER_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	protected static class RegistrySellerFiller extends Filler implements Function<Record,RegistrySeller> {
		
		@Override
		public RegistrySeller apply(Record r) {
			return build(r);
		}
		
		public static RegistrySeller build(Record r) {
			
			return new RegistrySeller()
					.setId(r.getValue(RSELLER.ID))
					.setDomain(DomainFiller.build(r))
					.setSeller(SellerFiller.build(r, true))
					.setRegistry(r.getValue(RSELLER.REGISTRY))
					.setStartDate(r.getValue(RSELLER.START_DATE))
					.setEndDate(r.getValue(RSELLER.END_DATE))
					.setStatus(RegistrySellerStatus.safeValueOf(r.getValue(RSELLER.STATUS)))
					.setType(RegistrySellerType.safeValueOf(r.getValue(RSELLER.TYPE)))
					;
		}
	}
	
}
