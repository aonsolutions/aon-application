package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;

import java.util.function.Function;
import java.util.stream.Stream;

import  org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.SellerFilter;
import com.esferalia.aon.occam.api.model.commission.CommissionType;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.SellerStatus;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.SellerPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;

public class SellerDAO {
	
	public static final com.esferalia.aon.jooq.tables.Registry SELLER_ALIAS = REGISTRY.as("registry_seller");
	
	private static final SellerPropertiesDAO SELLER_PROPERTIES = new SellerPropertiesDAO();
	
	private static SelectConditionStep<Record> select(AONContext ctx, SellerFilter filter) {
		return ctx.getDslContext().select()
				.from(SELLER)
				.join(SELLER_ALIAS).on(SELLER_ALIAS.ID.eq(SELLER.REGISTRY))
				.join(SCOPE).on(SCOPE.ID.eq(SELLER.SCOPE))
				.where(SELLER_PROPERTIES.getConditions(filter));	
	}

	public static Seller get(AONContext ctx, SellerFilter filter){
		return select(ctx, filter).limit(1).fetch().stream().map(new SellerFiller())
			.findFirst()
			.orElse(new Seller());
	}
	
	public static Seller get(AONContext ctx, Integer id){
		return get(ctx, f -> f.getRegistryProperty().eq(id));
	}
	
	public static Stream<Seller> getStream(AONContext ctx, SellerFilter filter){
		return select(ctx, filter)
			.orderBy(SELLER_ALIAS.NAME)
			.fetch().stream().map(new SellerFiller());
	}	
	
	public static Seller save(AONContext ctx, Seller seller) {
		ctx.checkWrite();
		// TODO AUTOCOMPLETE && VALIDATION
		boolean nullId = (seller.getId() == null); 
		seller.copy(RegistryDAO.save(ctx, seller));
		return nullId || get(ctx, seller.getId()).isEmpty()
			? insert(ctx, seller) : update(ctx, seller);
	}

	private static Seller insert(AONContext ctx, Seller seller){
		ctx.getDslContext().insertInto(SELLER)
			.set(SELLER.REGISTRY, seller.getId())
			.set(SELLER.DOMAIN, seller.getDomain().getId())
			.set(SELLER.COMMISSION_TYPE, seller.getCommissionType().getId())
			.set(SELLER.SCOPE, seller.getScope().getId())
			.set(SELLER.STATUS, seller.getStatus().value())
			.execute();
		return seller;
	}
	
	private static Seller update(AONContext ctx, Seller seller){
		ctx.checkWrite();
		int count = ctx.getDslContext().update(SELLER)
			.set(SELLER.DOMAIN, seller.getDomain().getId())
			.set(SELLER.COMMISSION_TYPE, seller.getCommissionType().getId())
			.set(SELLER.SCOPE, seller.getScope().getId())
			.set(SELLER.STATUS, seller.getStatus().value())
			.where(SELLER.REGISTRY.eq(seller.getId()))
			.execute();
		ctx.log().info("UPDATE SUPPLIER id: " + seller.getId() + ". (" + count + " rows)");		
		return seller;
	}
	
	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f -> f.getRegistryProperty().eq(id));
	}
	
	public static void delete(AONContext ctx, SellerFilter filter){
		ctx.getDslContext().delete(SELLER)
		.where(SELLER_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	
	protected static class SellerFiller extends Filler implements Function<Record,Seller> {
	
		@Override
		public Seller apply(Record r) {
			return build(r);
		}
		
		public static Seller build(Record r) {
			return  new Seller()
					.copy(RegistryFiller.build(r, SELLER_ALIAS))
					.setId(r.getValue(SELLER.REGISTRY))
					.setDomain(r.getValue(SELLER.DOMAIN))
					.setStatus(SellerStatus.safeValueOf(r.getValue(SELLER.STATUS)))
					.setCommissionType(new CommissionType().setId(r.getValue(SELLER.COMMISSION_TYPE)))
					.setScope(checkField(r, SCOPE.ID)
						? ScopeFiller.buildScope(r)
						: new Scope().setId(r.getValue(SELLER.SCOPE)));
		}
	}
	
}
