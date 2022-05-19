package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Offer.OFFER;
import static com.esferalia.aon.jooq.tables.OfferDetail.OFFER_DETAIL;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;
import static com.esferalia.aon.jooq.tables.Target.TARGET;
import static com.esferalia.aon.jooq.tables.Tax.TAX;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.occam.impl.jooq.dao.SellerDAO.SELLER_ALIAS;
import static com.esferalia.aon.occam.impl.jooq.dao.TargetDAO.TARGET_ALIAS;
import static com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO.SUPPLIER_ALIAS;

import java.sql.Timestamp;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.management.Offer;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.management.OfferFilter;
import com.esferalia.aon.occam.api.model.management.OfferProperties;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.OfferStatus;
import com.esferalia.aon.occam.api.model.type.OfferType;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO.ItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PayMethodDAO.PayMethodFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectDAO.ProjectFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO.RegistryAddressFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SellerDAO.SellerFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO.SupplierFiller;
import com.esferalia.aon.occam.impl.jooq.dao.TargetDAO.TargetFiller;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO.WorkplaceFiller;
import com.esferalia.aon.watson.util.AonStringUtils;

public class OfferDAO {
	
	private OfferDAO() {
	
	}
	
	private static final OfferPropertiesDAO OFFER_PROPERTIES = new OfferPropertiesDAO();
	private static class OfferPropertiesDAO implements OfferProperties {

		private Condition[] getConditions(OfferFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}
		
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(OFFER.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(OFFER.DOMAIN);}
		@Override public Property<Date> getStartIssueDateProperty() {return new FilterDAO.DatePropertyDAO(OFFER.ISSUE_DATE);}
		@Override public Property<Date> getEndIssueDateProperty() {return new FilterDAO.DatePropertyDAO(OFFER.ISSUE_DATE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(OFFER.STATUS);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(OFFER.SCOPE);}
		@Override public Property<Byte> getConfidentialProperty() {return new FilterDAO.PropertyDAO<>(OFFER.SECURITY_LEVEL);}

		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<>(OFFER.SELLER);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<>(OFFER.WORKPLACE);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<>(OFFER.SERIES);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<>(OFFER.NUMBER);}
		@Override public Property<Integer> getTargetProperty() {return new FilterDAO.PropertyDAO<>(OFFER.TARGET);}

		@Override public Property<Integer> getSupplierProperty() {return new FilterDAO.PropertyDAO<>(OFFER.SUPPLIER);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<>(OFFER.PROJECT);}

		@Override public Property<Date> getIssueDateProperty() {return new FilterDAO.DatePropertyDAO(OFFER.ISSUE_DATE);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(OFFER.TYPE);}

		@Override public Property<Byte> getSignedProperty() {return new FilterDAO.PropertyDAO<>(OFFER.SIGNED);}

		@Override public Property<String> getExternalReferenceProperty() {return new FilterDAO.PropertyDAO<>(OFFER.EXTERNAL_REFERENCE);}
	}

	
	public static int getNextNumber(AONContext ctx, String series ) {
		Integer next = ctx.getDslContext()
			.select( DSL.max(OFFER.NUMBER))
			.from(OFFER)
			.where(OFFER.DOMAIN.eq(ctx.getDomainId()))
			.and(AonStringUtils.isBlank(series)
				? OFFER.SERIES.isNull().or(DSL.trim(OFFER.SERIES).eq(""))
				: OFFER.SERIES.eq(series))
			.fetch()
			.stream()
			.mapToInt(rec -> (rec != null && rec.getValue(DSL.max(OFFER.NUMBER)) != null) 
					? rec.getValue(DSL.max(OFFER.NUMBER)) 
					: 0)
			.findFirst()
			.orElse(0);
		if(next < 0) next = 0;
		return ++next;
	}
	
	public static Offer getOffer(AONContext ctx, OfferFilter filter) {
		return ctx.getDslContext()
			.select()
			.from(OFFER)
			.where(OFFER_PROPERTIES.getConditions(filter))
			.orderBy(OFFER.ID.desc())
			.limit(1)
			.fetch()
			.stream()
			.map(new OfferFiller())
			.findFirst().orElse(new Offer());	
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
			.select()
			.from(OFFER)
			.join(OFFER_DETAIL).on(OFFER_DETAIL.OFFER.equal(OFFER.ID))
			.join(TARGET).on(TARGET.REGISTRY.equal(OFFER.TARGET))
			.join(TARGET_ALIAS).on(TARGET_ALIAS.ID.equal(TARGET.REGISTRY))
			.leftOuterJoin(RADDRESS).on(RADDRESS.REGISTRY.equal(TARGET_ALIAS.ID).and(RADDRESS.TYPE.equal((byte) 0)))
			.leftOuterJoin(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
			.leftOuterJoin(SCOPE).on(SCOPE.ID.equal(OFFER.SCOPE))
			.leftOuterJoin(PROJECT).on(PROJECT.ID.equal(OFFER.PROJECT))
			.leftOuterJoin(ITEM).on(ITEM.ID.equal(OFFER_DETAIL.ITEM))
			.leftOuterJoin(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
			.leftOuterJoin(TAX).on(PRODUCT.VAT.equal(TAX.ID))
			.leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.equal(PCATEGORY.ID))
			.leftOuterJoin(SELLER).on(SELLER.REGISTRY.equal(OFFER.SELLER))
			.leftOuterJoin(SELLER_ALIAS).on(SELLER_ALIAS.ID.equal(OFFER.SELLER))
			.leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.equal(OFFER.WORKPLACE))
			.where(OFFER_PROPERTIES.getConditions(filter))
			.orderBy(OFFER.ISSUE_DATE,OFFER.SERIES,OFFER.NUMBER,OFFER_DETAIL.LINE)
			.fetch();
		
	}

	public static OfferDetail getOfferDetail(AONContext ctx, OfferFilter filter) {
		return getOfferDetails(ctx, filter).findFirst().orElse(new OfferDetail());
	}
	
	public static Stream<OfferDetail> getOfferDetails(AONContext ctx, OfferFilter filter) {
		return getFullOffers(ctx, filter)
			.stream()
			.map(new OfferDetailFiller());
	}
	
	public static Offer updateOffer(AONContext ctx, Offer offer) {
		ctx.checkWrite();
		Timestamp modificationDate = null;
		modificationDate = new java.sql.Timestamp(new java.util.Date().getTime());
		
		ctx.getDslContext()
				.update(OFFER)
				.set(OFFER.DOMAIN, offer.getDomain())
				.set(OFFER.SIGNED, offer.getSigned() != null && offer.getSigned() ? (byte) 1 : (byte) 0)
				.set(OFFER.STATUS, offer.getStatus().value())
				.set(OFFER.EXTERNAL_REFERENCE, offer.getExternalReference())
				.set(OFFER.MODIFICATION_DATE, modificationDate)
				.set(OFFER.MODIFICATION_USER, ctx.getUser())
				.where(OFFER.ID.eq(offer.getId()))
				.execute();
		return offer;
	}
	
	public static Offer insertOffer(AONContext ctx, Offer offer) {
		ctx.checkWrite();
		Timestamp modificationDate = null;
		modificationDate = new java.sql.Timestamp(new java.util.Date().getTime());
		Byte signed = (byte)(offer.getSigned() != null && offer.getSigned() ? 1 : 0);
		java.sql.Date issueDate = new java.sql.Date(offer.getIssueDate().getTime());
		Integer id = ctx.getDslContext()
			.insertInto(OFFER, OFFER.DOMAIN, OFFER.EXTERNAL_REFERENCE, OFFER.ISSUE_DATE, OFFER.NUMBER, 
				OFFER.PROJECT, OFFER.SCOPE, OFFER.SELLER, OFFER.SERIES, OFFER.SIGNED, OFFER.STATUS,
				OFFER.SUPPLIER, OFFER.TARGET, OFFER.TYPE, OFFER.VERSION, OFFER.WORKPLACE,
				OFFER.BANK_ACCOUNT, OFFER.BIC, OFFER.COMMENTS, OFFER.REMARKS,
				OFFER.CREATION_DATE, OFFER.CREATION_USER, OFFER.MODIFICATION_DATE, OFFER.MODIFICATION_USER)
			.values(offer.getDomain(), offer.getExternalReference(), issueDate, offer.getNumber(), offer.getProject() != null ? offer.getProject().getId() : null, offer.getScope().getId(),
				offer.getSeller() != null ? offer.getSeller().getId(): null, offer.getSeries(),signed , offer.getStatus().value(), 
				offer.getSupplier() != null ? offer.getSupplier().getId(): null, offer.getTarget().getId(), offer.getType().value(), offer.getVersion().shortValue(), 
				offer.getWorkPlace() != null ? offer.getWorkPlace().getId(): null, offer.getBankAccount(), offer.getBic(),
				offer.getComments(), offer.getRemarks(),
				modificationDate, ctx.getUser(),modificationDate, ctx.getUser())
			.returning(OFFER.ID).fetchOne().getId();
		offer.setId(id);
		return offer;
	}
	
	public static OfferDetail insertOfferDetail(AONContext ctx, OfferDetail offerDetail) {
		ctx.checkWrite();
		Timestamp modificationDate = null;
		modificationDate = new java.sql.Timestamp(new java.util.Date().getTime());
		Integer id = ctx.getDslContext().insertInto(OFFER_DETAIL, OFFER_DETAIL.DESCRIPTION, OFFER_DETAIL.DISCOUNT_EXPR, OFFER_DETAIL.DOMAIN,
				OFFER_DETAIL.ITEM, OFFER_DETAIL.LINE, OFFER_DETAIL.OFFER, OFFER_DETAIL.PRICE, OFFER_DETAIL.QUANTITY, OFFER_DETAIL.STATUS,
				OFFER_DETAIL.CREATION_DATE, OFFER_DETAIL.CREATION_USER, OFFER_DETAIL.MODIFICATION_DATE, OFFER_DETAIL.MODIFICATION_USER)
		.values(offerDetail.getDescription(), offerDetail.getDiscountExpression(), offerDetail.getDomain(), offerDetail.getItem().getId(),
				offerDetail.getLine(), offerDetail.getOffer().getId(), offerDetail.getPrice(), offerDetail.getQuantity(), offerDetail.getStatus().value(), 
				modificationDate, ctx.getUser(),modificationDate, ctx.getUser())
		.returning(OFFER_DETAIL.ID).fetchOne().getId();
		offerDetail.setId(id);
		return offerDetail;
	}
		
	public static class OfferDetailFiller extends Filler implements Function<Record,OfferDetail> {

		@Override
		public OfferDetail apply(Record r) {
			return build(r);
		}
		
		public static OfferDetail build(Record r) {
			return new OfferDetail()
				.setId(r.getValue(OFFER_DETAIL.ID))
				.setOffer(checkField(r, OFFER.ID)
					? OfferFiller.build(r)
					: new Offer().setId(r.getValue(OFFER_DETAIL.OFFER)))
				.setLine(r.getValue(OFFER_DETAIL.LINE))
				.setDescription(r.getValue(OFFER_DETAIL.DESCRIPTION))
				.setQuantity(r.getValue(OFFER_DETAIL.QUANTITY))
				.setPrice(r.getValue(OFFER_DETAIL.PRICE))
				.setDiscountExpression(r.getValue(OFFER_DETAIL.DISCOUNT_EXPR))
				.setItem(checkField(r, ITEM.ID)
					? ItemFiller.build(r)
					: new Item().setId(r.getValue(OFFER_DETAIL.ITEM)));
		}
		
	}
	
	
	public static class OfferFiller extends Filler implements Function<Record, Offer> {

		@Override
		public Offer apply(Record r) {
			return build(r);
		}
		
		public static Offer build(Record r) {
			return new Offer()
					.setId(r.getValue(OFFER.ID))
					.setDomain(r.getValue(OFFER.DOMAIN))
					.setProject(checkField(r, PROJECT.ID)
							? ProjectFiller.build(r)
							: new Project().setId(r.getValue(OFFER.PROJECT)))
					.setType(OfferType.safeValueOf(r.getValue(OFFER.TYPE)))
					.setStatus(OfferStatus.safeValueOf(r.getValue(OFFER.STATUS)))
					.setSeries(r.getValue(OFFER.SERIES))
					.setNumber(r.getValue(OFFER.NUMBER))
					.setIssueDate(r.getValue(OFFER.ISSUE_DATE))
					.setTarget(checkField(r, TARGET.REGISTRY)
							? TargetFiller.build(r, TARGET_ALIAS)
							: new Target().copy(new Registry().setId(r.getValue(OFFER.TARGET))))
					.setSeller(checkField(r, SELLER.REGISTRY)
							? SellerFiller.build(r)
							: new Seller().setId(r.getValue(OFFER.SELLER)))
					.setSupplier(checkField(r, SUPPLIER.REGISTRY) 
							? SupplierFiller.buildSupplier(r, SUPPLIER_ALIAS)
							: new Supplier().setId(r.getValue(OFFER.SUPPLIER)))
					.setWorkPlace(checkField(r, WORKPLACE.ID)
							? WorkplaceFiller.build(r)
							: new Workplace().setId(r.getValue(OFFER.WORKPLACE)))
					.setScope(checkField(r, SCOPE.ID)
							? ScopeFiller.buildScope(r)
							: new Scope().setId(r.getValue(OFFER.SCOPE)))
					.setAddress(checkField(r, RADDRESS.ID)
							? RegistryAddressFiller.build(r)
							: new RegistryAddress().setId(r.getValue(OFFER.ADDRESS)))
					.setExternalReference(r.getValue(OFFER.EXTERNAL_REFERENCE))
					.setComments(r.getValue(OFFER.COMMENTS))
					.setRemarks(r.getValue(OFFER.REMARKS))
					.setBankAccount(r.getValue(OFFER.BANK_ACCOUNT))
					.setBic(r.getValue(OFFER.BIC))
					.setPaymethod(checkField(r, PAY_METHOD.ID)
							? PayMethodFiller.build(r)
							: new PayMethod().setId(r.getValue(OFFER.PAY_METHOD)))
					.setNumberOfPayments(r.getValue(OFFER.NUMBER_OF_PYMNTS).intValue())
					.setDaysToFirstPayment(r.getValue(OFFER.DAYS_TO_FIRST_PYMNT).intValue())
					.setDaysBetweenPayments(r.getValue(OFFER.DAYS_BETWEEN_PYMNTS).intValue())
					.setPaymentDays(r.getValue(OFFER.PYMNT_DAYS))
					.setCreationDate(r.getValue(OFFER.CREATION_DATE))
					.setCreationUser(r.getValue(OFFER.CREATION_USER))
					.setModificationDate(r.getValue(OFFER.MODIFICATION_DATE))
					.setModificationUser(r.getValue(OFFER.MODIFICATION_USER));
		}
		
	}
	
}
