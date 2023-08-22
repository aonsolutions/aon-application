package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.Rseller.RSELLER;


import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import  org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.jooq.tables.Rseller;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.SellerFilter;
import com.esferalia.aon.occam.api.model.Properties.SellerProperties;
import com.esferalia.aon.occam.api.model.commission.CommissionType;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.SellerStatus;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;

public class SellerDAO {
    
    private SellerDAO() {

    }
	
	public static final com.esferalia.aon.jooq.tables.Registry SELLER_ALIAS = REGISTRY.as("registry_seller");
	private static final SellerPropertiesDAO SELLER_PROPERTIES = new SellerPropertiesDAO();
	
	public static class SellerPropertiesDAO implements SellerProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, SellerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(SellerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(SELLER.REGISTRY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(SELLER.DOMAIN);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(SELLER.STATUS);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(SELLER.SCOPE);}
		@Override public Property<Integer> getCommissionTypeProperty() {return new FilterDAO.PropertyDAO<>(SELLER.COMMISSION_TYPE);}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.ID);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.DOCUMENT);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.DOCUMENT_TYPE);}
		@Override public Property<String> getDocumentCountryProperty() {return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.DOCUMENT_COUNTRY);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.NAME);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.ALIAS);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.TYPE);}
		@Override public Property<String> getNationalityProperty() {return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.NATIONALITY);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.SECURITY_LEVEL);}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, SellerFilter filter) {
		return ctx.getDslContext().select()
				.from(SELLER)
				.join(SELLER_ALIAS).on(SELLER_ALIAS.ID.eq(SELLER.REGISTRY))
				.join(SCOPE).on(SCOPE.ID.eq(SELLER.SCOPE)).join(RSELLER).on(SELLER.DOMAIN.eq(RSELLER.DOMAIN))
				.where(SELLER_PROPERTIES.getConditions(filter));	
	}

	//ORIGINAL
//	private static SelectConditionStep<Record> select(AONContext ctx, SellerFilter filter) {
//		return ctx.getDslContext().select()
//				.from(SELLER)
//				.join(SELLER_ALIAS).on(SELLER_ALIAS.ID.eq(SELLER.REGISTRY))
//				.join(SCOPE).on(SCOPE.ID.eq(SELLER.SCOPE))
//				.where(SELLER_PROPERTIES.getConditions(filter));	
//	}
	
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
	
	public static Stream<Seller> getStream(AONContext ctx, SellerFilter filter, int offset, int limit){
		return select(ctx, filter)
				.orderBy(SELLER_ALIAS.NAME)
				.offset(offset)
				.limit(limit)
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
