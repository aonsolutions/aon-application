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

import java.util.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Result;

import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.management.Offer;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.management.OfferFilter;
import com.esferalia.aon.occam.api.model.management.OfferProperties;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Seller;
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
	}

	private static final Registry SELLER_ALIAS = REGISTRY.as("seller");
	
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
				,REGISTRY.DOCUMENT
				,REGISTRY.DOCUMENT_TYPE
				,REGISTRY.DOCUMENT_COUNTRY
				,REGISTRY.NAME
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
			return new OfferDetail()
				.setId(record.getValue(OFFER_DETAIL.ID))
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
					.setRegistry(record.getValue(OFFER.TARGET))
					.setRegistryDocument(record.getValue(REGISTRY.DOCUMENT))
					.setRegistryDocumentType(
							AonEnumUtils.enumValue(DocumentType.class,
									record.getValue(REGISTRY.DOCUMENT_TYPE)))
					.setRegistryDocumentCountry(
							Country.safeValueOf(record
									.getValue(REGISTRY.DOCUMENT_COUNTRY)))
					.setRegistryName(record.getValue(REGISTRY.NAME))
					.setRegistryProvinceCode(record.getValue(GEOZONE.CODE))
					.setRegistryProvince(record.getValue(GEOZONE.NAME))
					.setRegistryTown(record.getValue(RADDRESS.CITY))
					.setRegistryZIP(record.getValue(RADDRESS.ZIP))
					.setScope(record.getValue(SCOPE.DESCRIPTION))						
					.setProject( record.getValue( PROJECT.NAME ))
					.setSeller((record.getValue(OFFER.SELLER) == null)
							? null
							: new Seller()
							.setId( record.getValue(OFFER.SELLER) )
							.setRegistryName( record.getValue(SELLER_ALIAS.NAME) ))
						.setWorkPlace(record.getValue(WORKPLACE.DESCRIPTION))
					)
				.setLine(record.getValue( OFFER_DETAIL.LINE ))
				.setDescription(record.getValue( OFFER_DETAIL.DESCRIPTION ))
				.setQuantity(record.getValue(OFFER_DETAIL.QUANTITY))
				.setPrice(record.getValue(OFFER_DETAIL.PRICE))
				.setDiscountExpression(record.getValue(OFFER_DETAIL.DISCOUNT_EXPR))
				.setItem((record.getValue(OFFER_DETAIL.ITEM) == null)
					? null
					: new Item()
						.setId(record.getValue(OFFER_DETAIL.ITEM))
						.setCategory( record.getValue( PCATEGORY.NAME ) )
						.setProductId( record.getValue( PRODUCT.ID ) )
						.setName( record.getValue( PRODUCT.NAME ) )
						.setCode(record.getValue( PRODUCT.CODE ) )
						.setDetail(record.getValue( ITEM.DETAIL ))
						.setDetail2(record.getValue( ITEM.DETAIL2 ))
						.setDetail3(record.getValue( ITEM.DETAIL3 ))
						.setDescription(record.getValue( ITEM.DESCRIPTION )));
		}
		
	}
	
}
