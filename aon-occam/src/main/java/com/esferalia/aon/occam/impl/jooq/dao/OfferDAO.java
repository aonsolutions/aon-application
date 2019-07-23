package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Offer.OFFER;
import static com.esferalia.aon.jooq.tables.OfferDetail.OFFER_DETAIL;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Target.TARGET;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.jooq.tables.Tax.TAX;

import java.util.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Result;

import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.management.Offer;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.management.OfferFilter;
import com.esferalia.aon.occam.api.model.management.OfferProperties;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.OfferStatus;
import com.esferalia.aon.occam.api.model.type.OfferType;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class OfferDAO {
	
	private static final OfferPropertiesDAO OFFER_PROPERTIES = new OfferPropertiesDAO();
	private static class OfferPropertiesDAO implements OfferProperties {

		private Condition[] getConditions(OfferFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}
		
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(OFFER.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(OFFER.DOMAIN);}
		@Override public Property<Date> getStartIssueDateProperty() {return new FilterDAO.DatePropertyDAO(OFFER.ISSUE_DATE);}
		@Override public Property<Date> getEndIssueDateProperty() {return new FilterDAO.DatePropertyDAO(OFFER.ISSUE_DATE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(OFFER.STATUS);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<Integer>(OFFER.SCOPE);}
		@Override public Property<Byte> getConfidentialProperty() {return new FilterDAO.PropertyDAO<Byte>(OFFER.SECURITY_LEVEL);}

		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<>(OFFER.SELLER);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<>(OFFER.WORKPLACE);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<>(OFFER.SERIES);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<>(OFFER.NUMBER);}
		@Override public Property<Integer> getTargetProperty() {return new FilterDAO.PropertyDAO<>(OFFER.TARGET);}

		@Override public Property<Integer> getSupplierProperty() {return new FilterDAO.PropertyDAO<>(OFFER.SUPPLIER);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<>(OFFER.PROJECT);}
	}

	private static final Registry SELLER_ALIAS = REGISTRY.as("seller");
	
	public static Offer getOffer(AONContext ctx, OfferFilter filter) {
		return ctx.getDslContext()
			.select()
			.from(OFFER)
			.where(OFFER_PROPERTIES.getConditions(filter))
			.orderBy(OFFER.ID.desc())
			.limit(1)
			.fetch()
			.stream()
			.map(new OfferFiller()).findFirst().get();	
	}
	
	public static Stream<Offer> getOfferStream(AONContext ctx, OfferFilter filter) {
		return ctx.getDslContext()
			.select()
			.from(OFFER)
			.where(OFFER_PROPERTIES.getConditions(filter))
			.orderBy(OFFER.ID.desc())
			.fetch()
			.stream()
			.map(new OfferFiller());	
	}
	
	private static Result<Record> getFullOffers(AONContext ctx, OfferFilter filter) {
		ctx.checkRead();

		return ctx.getDslContext()
			.select(
				 OFFER.ID
				,OFFER.DOMAIN
				,OFFER.TYPE
				,OFFER.STATUS
				,OFFER.SERIES
				,OFFER.NUMBER
				,OFFER.ISSUE_DATE
				,OFFER.TARGET
				,OFFER.SUPPLIER
				,OFFER.WORKPLACE
				,REGISTRY.DOCUMENT
				,REGISTRY.DOCUMENT_TYPE
				,REGISTRY.DOCUMENT_COUNTRY
				,REGISTRY.NAME
				,GEOZONE.ID
				,GEOZONE.CODE
				,GEOZONE.NAME
				,RADDRESS.ZIP
				,RADDRESS.CITY
				,OFFER.SELLER
				,SELLER_ALIAS.NAME
				,SCOPE.DESCRIPTION
				,PROJECT.NAME
				,OFFER_DETAIL.LINE
				,OFFER_DETAIL.ITEM
				,PCATEGORY.NAME
				,PRODUCT.ID
				,PRODUCT.NAME
				,PRODUCT.CODE
				,PRODUCT.VAT
				,TAX.ID
				,TAX.PERCENTAGE
				,ITEM.DETAIL
				,ITEM.DETAIL2
				,ITEM.DETAIL3
				,ITEM.DESCRIPTION
				,OFFER_DETAIL.ID
				,OFFER_DETAIL.DESCRIPTION
				,OFFER_DETAIL.QUANTITY
				,OFFER_DETAIL.PRICE
				,OFFER_DETAIL.DISCOUNT_EXPR
				,WORKPLACE.DESCRIPTION
				,PRODUCT.CATEGORY
			)
			.from(OFFER)
			.join(OFFER_DETAIL).on(OFFER_DETAIL.OFFER.equal(OFFER.ID))
			.join(TARGET).on(TARGET.REGISTRY.equal(OFFER.TARGET))
			.join(REGISTRY).on(REGISTRY.ID.equal(TARGET.REGISTRY))
			.leftOuterJoin(RADDRESS).on(RADDRESS.REGISTRY.equal(REGISTRY.ID).and(RADDRESS.TYPE.equal((byte) 0)))
			.leftOuterJoin(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
			.leftOuterJoin(SCOPE).on(SCOPE.ID.equal(OFFER.SCOPE))
			.leftOuterJoin(PROJECT).on(PROJECT.ID.equal(OFFER.PROJECT))
			.leftOuterJoin(ITEM).on(ITEM.ID.equal(OFFER_DETAIL.ITEM))
			.leftOuterJoin(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
			.leftOuterJoin(TAX).on(PRODUCT.VAT.equal(TAX.ID))
			.leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.equal(PCATEGORY.ID))
			.leftOuterJoin(SELLER_ALIAS).on(SELLER_ALIAS.ID.equal(OFFER.SELLER))
			.leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.equal(OFFER.WORKPLACE))
			.where(OFFER_PROPERTIES.getConditions(filter))
			.orderBy(OFFER.ISSUE_DATE,OFFER.SERIES,OFFER.NUMBER,OFFER_DETAIL.LINE)
			.fetch();
		
	}

	public static Stream<OfferDetail> getOfferDetails(AONContext ctx, OfferFilter filter) {
		return getFullOffers(ctx, filter)
			.stream()
			.map(new FullOfferDetailFiller());
	}
		
	private static class FullOfferDetailFiller  implements Function<Record,OfferDetail> {

		@Override
		public OfferDetail apply(Record record) {
			Boolean od = record.getValue(OFFER_DETAIL.ID) != null;
			return new OfferDetail()
				.setId(od ? record.getValue(OFFER_DETAIL.ID) : null)
				.setOffer(new Offer()
					.setId(record.getValue(OFFER.ID))
					.setDomain(record.getValue(OFFER.DOMAIN))
					.setType(
							AonEnumUtils.enumValue(OfferType.class,
									record.getValue(OFFER.TYPE)))
					.setStatus(
							AonEnumUtils.enumValue(OfferStatus.class,
									record.getValue(OFFER.STATUS)))
					.setSeries(record.getValue(OFFER.SERIES))
					.setNumber(record.getValue(OFFER.NUMBER))
					.setIssueDate(record.getValue(OFFER.ISSUE_DATE))
					.setTarget((Target) new Target()
							.setId(record.getValue(OFFER.TARGET))
							.setDocument(record.getValue(REGISTRY.DOCUMENT))
							.setDocumentType(
									AonEnumUtils.enumValue(DocumentType.class,
											record.getValue(REGISTRY.DOCUMENT_TYPE)))
							.setDocumentCountry(
									Country.safeValueOf(record
											.getValue(REGISTRY.DOCUMENT_COUNTRY)))
							.setName(record.getValue(REGISTRY.NAME))
							.setAddress(new RAddress()
									.setGeozone(record.getValue(GEOZONE.ID))
									.setGeozoneName(record.getValue(GEOZONE.NAME))
									.setGeozoneCode(record.getValue(GEOZONE.CODE))
									.setCity(record.getValue(RADDRESS.CITY))
									.setZip(record.getValue(RADDRESS.ZIP))
							)
					)
					.setScope(record.getValue(SCOPE.DESCRIPTION))						
					.setProject( record.getValue( PROJECT.NAME ))
					.setSupplier((Supplier) new Supplier().setId(record.getValue(OFFER.SUPPLIER)))
					.setSeller((record.getValue(OFFER.SELLER) == null)
							? null
							: new Seller()
							.setId( record.getValue(OFFER.SELLER) )
							.setRegistryName( record.getValue(SELLER_ALIAS.NAME) ))
					.setWorkPlace((record.getValue(OFFER.WORKPLACE) == null)
							? null
							: new Workplace().setId(record.getValue(OFFER.WORKPLACE))
								.setDescription(record.getValue(WORKPLACE.DESCRIPTION)))
						
				)
				.setLine(od ? record.getValue( OFFER_DETAIL.LINE ) : null)
				.setDescription(od ? record.getValue( OFFER_DETAIL.DESCRIPTION ) : null)
				.setQuantity(od ? record.getValue(OFFER_DETAIL.QUANTITY) : null)
				.setPrice(od ? record.getValue(OFFER_DETAIL.PRICE) : null)
				.setDiscountExpression(od ? record.getValue(OFFER_DETAIL.DISCOUNT_EXPR) : null)
				.setItem((!od || record.getValue(OFFER_DETAIL.ITEM) == null)
					? null
					: new Item()
						.setId(record.getValue(OFFER_DETAIL.ITEM))
						.setCategory( record.getValue( PCATEGORY.NAME ) )
						.setProduct(new Product().setCategory(record.getValue(PRODUCT.CATEGORY)))
						.setProductId( record.getValue( PRODUCT.ID ) )
						.setName( record.getValue( PRODUCT.NAME ) )
						.setCode(record.getValue( PRODUCT.CODE ) )
						.setDetail(record.getValue( ITEM.DETAIL ))
						.setDetail2(record.getValue( ITEM.DETAIL2 ))
						.setDetail3(record.getValue( ITEM.DETAIL3 ))
						.setVat(new Tax().setId(record.getValue(TAX.ID))
								.setPercentage(record.getValue(TAX.PERCENTAGE)))
						.setDescription(record.getValue( ITEM.DESCRIPTION )));
		}
		
	}
	
	private static class OfferFiller  implements Function<Record, Offer> {

		@Override
		public Offer apply(Record record) {
			return new Offer()
					.setId(record.getValue(OFFER.ID))
					.setDomain(record.getValue(OFFER.DOMAIN))
					.setType(
							AonEnumUtils.enumValue(OfferType.class,
									record.getValue(OFFER.TYPE)))
					.setStatus(
							AonEnumUtils.enumValue(OfferStatus.class,
									record.getValue(OFFER.STATUS)))
					.setSeries(record.getValue(OFFER.SERIES))
					.setNumber(record.getValue(OFFER.NUMBER))
					.setIssueDate(record.getValue(OFFER.ISSUE_DATE))
					.setTarget((Target) new Target()
							.setId(record.getValue(OFFER.TARGET))
					)
					.setSeller((record.getValue(OFFER.SELLER) == null)
							? null
							: new Seller()
							.setId( record.getValue(OFFER.SELLER) ))
					.setSupplier((Supplier) 
						((record.getValue(OFFER.SUPPLIER) == null)
							? null
							: new Supplier()
							.setId( record.getValue(OFFER.SUPPLIER))))
					.setWorkPlace(new Workplace().setId(record.getValue(OFFER.WORKPLACE)));
		}
		
	}
	
}
