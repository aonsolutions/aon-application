package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Brand.BRAND;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.ItemComposition.ITEM_COMPOSITION;
import static com.esferalia.aon.jooq.tables.CatalogueItem.CATALOGUE_ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.ProductBooking.PRODUCT_BOOKING;
import static com.esferalia.aon.jooq.tables.ProductTag.PRODUCT_TAG;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.Tax.TAX;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.ProductProperties;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductBooking;
import com.esferalia.aon.occam.api.model.product.ProductBookingPriceType;
import com.esferalia.aon.occam.api.model.product.ProductBookingType;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductKind;
import com.esferalia.aon.occam.api.model.product.ProductParams;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.impl.jooq.dao.BrandDAO.BrandFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ProductCategoryDAO.ProductCategoryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO.TaskHolderFiller;
import com.esferalia.aon.occam.impl.jooq.dao.TaxDAO.TaxFiller;
import com.esferalia.aon.occam.impl.jooq.dao.WorkgroupDAO.WorkgroupFiller;
import com.esferalia.aon.occam.impl.jooq.validation.ProductAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.ProductDeleteValidation;
import com.esferalia.aon.occam.impl.jooq.validation.ProductValidation;
import com.esferalia.aon.watson.util.AonStringUtils;


public class ProductDAO {
	
	// Private constructor to prevent instantiation
	private ProductDAO() {
	
	}
	
	private static final ProductPropertiesDAO PRODUCT_PROPERTIES = new ProductPropertiesDAO();
	public static final com.esferalia.aon.jooq.tables.Tax VAT_ALIAS = TAX.as("vat");
	public static final com.esferalia.aon.jooq.tables.Tax RETENTION_ALIAS = TAX.as("retention");
	public static final com.esferalia.aon.jooq.tables.Registry TASK_HOLDER_ALIAS = REGISTRY.as("registry_task_holder");

	protected static class ProductPropertiesDAO implements ProductProperties {
		protected Select<Record> build(SelectJoinStep<Record> select,ProductFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(ProductFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.NAME);}
		@Override public Property<String> getCodeProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.CODE);}
		@Override public Property<Byte> getKindProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.KIND);}
		@Override public Property<Integer> getBrandProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.BRAND);}
		@Override public Property<Integer> getCategoryProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.CATEGORY);}
		@Override public Property<Byte> getInventoriableProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.INVENTORIABLE);}
		@Override public Property<Byte> getSerializableProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.SERIALIZABLE);}
		@Override public Property<Byte> getLotableProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.LOTABLE);}
		@Override public Property<Byte> getStatusProperty() { return new FilterDAO.PropertyDAO<>(PRODUCT.STATUS);}
		@Override public Property<Integer> getVatProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.VAT);}
		@Override public Property<Integer> getRetentionProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.RETENTION);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.TYPE);}
		@Override public Property<Byte> getManufacturedProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.MANUFACTURED);}
		@Override public Property<Byte> getCompositionProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.COMPOSITION);}
		@Override public Property<Byte> getCompositionPriceProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.COMPOSITION_PRICE);}
		@Override public Property<Integer> getSalesAccountProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.SALES_ACCOUNT);}
		@Override public Property<Integer> getPurchaseAccountProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.PURCHASE_ACCOUNT);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.NAME);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.MODIFICATION_DATE);}
	}

	private static SelectConditionStep<Record> select(AONContext ctx, ProductFilter filter) {		
		return ctx.getDslContext()
				.select()
				.from(PRODUCT)
				.leftOuterJoin(PCATEGORY).on(PCATEGORY.ID.eq(PRODUCT.CATEGORY))
				.leftOuterJoin(BRAND).on(BRAND.ID.eq(PRODUCT.BRAND))
				.leftOuterJoin(VAT_ALIAS).on(VAT_ALIAS.ID.eq(PRODUCT.VAT))
				.leftOuterJoin(RETENTION_ALIAS).on(RETENTION_ALIAS.ID.eq(PRODUCT.RETENTION))
				.where(PRODUCT_PROPERTIES.getConditions(filter))
				.and(PRODUCT.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)));
	}
	
	public static Stream<Product> getStream(AONContext ctx, ProductFilter filter, Integer page, Integer perPage){	
		return getStream(ctx, filter, Optional.of(page), Optional.of(perPage));
	}
	
	public static Stream<Product> getStream(AONContext ctx, ProductFilter filter) {
		return getStream(ctx, filter, Optional.empty(), Optional.empty());
	}
	
	private static Stream<Product> getStream(AONContext ctx, ProductFilter filter, Optional<Integer> page, Optional<Integer> perPage) {
		SelectConditionStep<Record> query = select(ctx, filter);
		
		if(page.isPresent() && perPage.isPresent()) {
			Integer per = perPage.get();
			Integer p = page.get();
			query.limit(per).offset(per * (p -1));
		}
		
		return query.fetch().stream().map(new ProductFiller());
	}
	
	public static LinkedList<Product> getList(AONContext ctx, ProductFilter filter) {
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Product> getList(AONContext ctx, ProductParams params) {
		Condition condition = paramsToCondition(ctx, params);
		
		SelectConditionStep<Record> select = ctx.getDslContext()
		.select()
		.from(PRODUCT)
		.join(ITEM).on(ITEM.PRODUCT.eq(PRODUCT.ID))
		.leftOuterJoin(PCATEGORY).on(PCATEGORY.ID.eq(PRODUCT.CATEGORY))
		.leftOuterJoin(BRAND).on(BRAND.ID.eq(PRODUCT.BRAND))
		.leftOuterJoin(VAT_ALIAS).on(VAT_ALIAS.ID.eq(PRODUCT.VAT))
		.leftOuterJoin(RETENTION_ALIAS).on(RETENTION_ALIAS.ID.eq(PRODUCT.RETENTION))
		.leftOuterJoin(TAX).on(PRODUCT.VAT.eq(TAX.ID))
		.where(condition);
		
		if(params.isAsc()) {
			if(AonStringUtils.equals(params.getOrderBy(), "code"))
				select.orderBy(PRODUCT.CODE);
			else if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(PRODUCT.NAME);
			else if(AonStringUtils.equals(params.getOrderBy(), "category"))
				select.orderBy(PCATEGORY.NAME);
			else if(AonStringUtils.equals(params.getOrderBy(), "composite"))
				select.orderBy(PRODUCT.COMPOSITION);
			else if(AonStringUtils.equals(params.getOrderBy(), "status"))
				select.orderBy(PRODUCT.STATUS);
			else if(AonStringUtils.equals(params.getOrderBy(), "pack"))
				select.orderBy(PRODUCT.MANUFACTURED);
		} else {
			if(AonStringUtils.equals(params.getOrderBy(), "code"))
				select.orderBy(PRODUCT.CODE.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(PRODUCT.NAME.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "category"))
				select.orderBy(PCATEGORY.NAME.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "composite"))
				select.orderBy(PRODUCT.COMPOSITION.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "status"))
				select.orderBy(PRODUCT.STATUS.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "pack"))
				select.orderBy(PRODUCT.MANUFACTURED.desc());
		}
		
		LinkedList<Product> products = select.limit(params.getOffset(), params.getLimit())
				.fetch()
				.stream()
				.map(new ProductFiller())
				.collect(Collectors.toCollection(LinkedList::new));
		return products;
	}
	
	public static LinkedList<ProductBooking> getBookingList(AONContext ctx, ProductParams params) {
		Condition condition = paramsToCondition(ctx, params);
		
		SelectConditionStep<Record> select = ctx.getDslContext()
		.select()
		.from(PRODUCT_BOOKING)
		.join(PRODUCT).on(PRODUCT.ID.eq(PRODUCT_BOOKING.PRODUCT))
		.join(ITEM).on(ITEM.PRODUCT.eq(PRODUCT.ID))
		.leftOuterJoin(PCATEGORY).on(PCATEGORY.ID.eq(PRODUCT.CATEGORY))
		.leftOuterJoin(BRAND).on(BRAND.ID.eq(PRODUCT.BRAND))
		.leftOuterJoin(VAT_ALIAS).on(VAT_ALIAS.ID.eq(PRODUCT.VAT))
		.leftOuterJoin(RETENTION_ALIAS).on(RETENTION_ALIAS.ID.eq(PRODUCT.RETENTION))
		.leftOuterJoin(TAX).on(PRODUCT.VAT.eq(TAX.ID))
		.leftOuterJoin(WORKGROUP).on(WORKGROUP.ID.eq(PRODUCT_BOOKING.WORKGROUP))
		.leftOuterJoin(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(PRODUCT_BOOKING.TASK_HOLDER))
		.leftOuterJoin(TASK_HOLDER_ALIAS).on(TASK_HOLDER_ALIAS.ID.eq(TASK_HOLDER.REGISTRY))
		.where(condition);
		
		if(params.isAsc()) {
			if(AonStringUtils.equals(params.getOrderBy(), "code"))
				select.orderBy(PRODUCT.CODE);
			else if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(PRODUCT.NAME);
			else if(AonStringUtils.equals(params.getOrderBy(), "category"))
				select.orderBy(PCATEGORY.NAME);
			else if(AonStringUtils.equals(params.getOrderBy(), "composite"))
				select.orderBy(PRODUCT.COMPOSITION);
			else if(AonStringUtils.equals(params.getOrderBy(), "status"))
				select.orderBy(PRODUCT.STATUS);
			else if(AonStringUtils.equals(params.getOrderBy(), "pack"))
				select.orderBy(PRODUCT.MANUFACTURED);
		} else {
			if(AonStringUtils.equals(params.getOrderBy(), "code"))
				select.orderBy(PRODUCT.CODE.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(PRODUCT.NAME.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "category"))
				select.orderBy(PCATEGORY.NAME.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "composite"))
				select.orderBy(PRODUCT.COMPOSITION.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "status"))
				select.orderBy(PRODUCT.STATUS.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "pack"))
				select.orderBy(PRODUCT.MANUFACTURED.desc());
		}
		
		LinkedList<ProductBooking> productsBooking = select.limit(params.getOffset(), params.getLimit())
				.fetch()
				.stream()
				.map(new ProductBookingFiller())
				.collect(Collectors.toCollection(LinkedList::new));
		
		return productsBooking;
	}
	
	private static Condition paramsToCondition(AONContext ctx, ProductParams params) {
		Condition condition = PRODUCT.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)).or(PRODUCT.DOMAIN.eq(params.getDomain()));
		
		if(AonStringUtils.isNotBlank(params.getDescription()))
			condition = condition.and(
						PRODUCT.NAME.like("%" + params.getDescription() + "%")
						.or(PRODUCT.CODE.like("%" + params.getDescription() + "%"))
			);
		
		if(null != params.getCategory())
			condition = condition.and(PRODUCT.CATEGORY.eq(params.getCategory()));
		
		if(null != params.getType())
			condition = condition.and(PRODUCT.TYPE.eq(params.getType().value()));
		
		if(null != params.getStatus())
			condition = condition.and(PRODUCT.STATUS.eq(params.getStatus().value()));
		
//		if(null != params.getProductComposition())
//			condition = condition.and(PRODUCT.COMPOSITION.eq(params.getProductComposition() ? (byte) 1 : 0));
		
		if(null != params.getDomainType())
			condition = condition.and(ITEM.BARCODE.isNull()
					.or(
							DSL.length(ITEM.BARCODE).eq(12).and(getDomainTypeChar(params.getDomainType()))
					));
		
		return condition;
	}

	private static Condition getDomainTypeChar(DomainType domainType) {
		switch (domainType) {
			case CONSULTANCY:
				return DSL.substring(ITEM.BARCODE, DSL.inline(4), DSL.inline(1)).eq("1");
			case GARAGE:
				return DSL.substring(ITEM.BARCODE, DSL.inline(5), DSL.inline(1)).eq("1");
			case ACADEMY:
				return DSL.substring(ITEM.BARCODE, DSL.inline(6), DSL.inline(1)).eq("1");
			case HOTEL:
				return DSL.substring(ITEM.BARCODE, DSL.inline(7), DSL.inline(1)).eq("1");
			case ADMIN:
				return DSL.substring(ITEM.BARCODE, DSL.inline(8), DSL.inline(1)).eq("1");
			case OFFICE:
				return DSL.substring(ITEM.BARCODE, DSL.inline(9), DSL.inline(1)).eq("1");
			case GENERIC:
				return DSL.substring(ITEM.BARCODE, DSL.inline(10), DSL.inline(1)).eq("1");
			case COMMERCE:
				return DSL.substring(ITEM.BARCODE, DSL.inline(11), DSL.inline(1)).eq("1");
			case KIT_DIGITAL:
				return DSL.substring(ITEM.BARCODE, DSL.inline(12), DSL.inline(1)).eq("1");
			default:
				return DSL.substring(ITEM.BARCODE, DSL.inline(3), DSL.inline(1)).eq("1");
//				return DSL.substring(ITEM.BARCODE, 2, 3).eq("1");
		}
		
	}

	public static Product get(AONContext ctx, ProductFilter filter) {
		return select(ctx, filter).limit(1)
		.fetch().stream().map(new ProductFiller())
		.findFirst().orElse(new Product());
	}
	
	public static ProductBooking getBooking(AONContext ctx, ProductFilter filter) {
		return ctx.getDslContext()
				.select()
				.from(PRODUCT_BOOKING)
				.join(PRODUCT).on(PRODUCT.ID.eq(PRODUCT_BOOKING.PRODUCT))
				.join(ITEM).on(ITEM.PRODUCT.eq(PRODUCT.ID))
				.leftOuterJoin(PCATEGORY).on(PCATEGORY.ID.eq(PRODUCT.CATEGORY))
				.leftOuterJoin(BRAND).on(BRAND.ID.eq(PRODUCT.BRAND))
				.leftOuterJoin(VAT_ALIAS).on(VAT_ALIAS.ID.eq(PRODUCT.VAT))
				.leftOuterJoin(RETENTION_ALIAS).on(RETENTION_ALIAS.ID.eq(PRODUCT.RETENTION))
				.leftOuterJoin(TAX).on(PRODUCT.VAT.eq(TAX.ID))
				.leftOuterJoin(WORKGROUP).on(WORKGROUP.ID.eq(PRODUCT_BOOKING.WORKGROUP))
				.leftOuterJoin(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(PRODUCT_BOOKING.TASK_HOLDER))
				.leftOuterJoin(TASK_HOLDER_ALIAS).on(TASK_HOLDER_ALIAS.ID.eq(TASK_HOLDER.REGISTRY))
				.where(PRODUCT_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new ProductBookingFiller())
				.findFirst().orElse(new ProductBooking());
	}
	
	public static Product save(AONContext ctx, Product product) {
		if(!product.getCategory().isEmpty())
			ProductCategoryDAO.save(ctx, product.getCategory());
	
		ProductAutoComplete.autoComplete(ctx, product);
		ProductValidation.validate(ctx, product);
		Product existProduct = get(ctx, f -> f.getDomainProperty().eq(product.getDomain().getId()).and(f.getCodeProperty().eq(product.getCode())));
		if(product.getId() == null && existProduct.getId() != null) product.setId(existProduct.getId());
		return product.getId() != null
			? update(ctx, product)
			: insert(ctx, product);
	}
	
	public static Product insert(AONContext ctx, Product product) {
		ctx.checkWrite();
		ProductValidation.insertValidate(ctx, product);
		Timestamp now = new java.sql.Timestamp(new java.util.Date().getTime());
		Integer id = ctx.getDslContext().insertInto(PRODUCT)
		.set(PRODUCT.DOMAIN, product.getDomain().getId())
		.set(PRODUCT.NAME, product.getName())
		.set(PRODUCT.CODE, product.getCode())
		.set(PRODUCT.BRAND, product.getBrand().getId())
		.set(PRODUCT.CATEGORY, product.getCategory().getId())
		.set(PRODUCT.INVENTORIABLE, product.isInventoriable() ? (byte) 1 : 0)
		.set(PRODUCT.SERIALIZABLE, product.isSerializable() ? (byte) 1 : 0)
		.set(PRODUCT.LOTABLE, product.isLotable() ? (byte) 1 : 0)
		.set(PRODUCT.STATUS, product.getStatus().value())
		.set(PRODUCT.VAT, product.getVat().getId())
		.set(PRODUCT.RETENTION, product.getRetention().getId())
		.set(PRODUCT.TYPE, product.getType().value())
		.set(PRODUCT.MANUFACTURED, product.isManufactured() ? (byte) 1 : 0)
		.set(PRODUCT.COMPOSITION, product.isComposition() ? (byte) 1 : 0)
		.set(PRODUCT.COMPOSITION_PRICE, product.isCompositionPrice() ? (byte) 1 : 0)
		.set(PRODUCT.SALES_ACCOUNT, product.getSalesAccount().getId())
		.set(PRODUCT.PURCHASE_ACCOUNT, product.getPurchaseAccount().getId())
		.set(PRODUCT.KIND, product.getKind().value())
		.set(PRODUCT.PACKAGED, product.isPackaged() ? (byte) 1: 0)
		.set(PRODUCT.PERISHABLE, product.isPerishable() ? (byte) 1: 0)
		.set(PRODUCT.DAYS_TO_EXPIRE, product.getDaysToExpire())
		.set(PRODUCT.CREATION_USER, ctx.getUser())
		.set(PRODUCT.CREATION_DATE, now) 
		.set(PRODUCT.MODIFICATION_USER, ctx.getUser())
		.set(PRODUCT.MODIFICATION_DATE, now)
		.returning(PRODUCT.ID).fetchOne().getValue(PRODUCT.ID);
		return product.setId(id);	
	}
	
	public static Product update(AONContext ctx, Product product) {
		ctx.checkWrite();
		//ProductValidation.validate(ctx, product);
		Timestamp now = new java.sql.Timestamp(new java.util.Date().getTime());
		
		ctx.getDslContext().update(PRODUCT)
		.set(PRODUCT.DOMAIN, product.getDomain().getId())
		.set(PRODUCT.NAME, product.getName())
		.set(PRODUCT.CODE, product.getCode())
		.set(PRODUCT.BRAND, product.getBrand().getId())
		.set(PRODUCT.CATEGORY, product.getCategory().getId())
		.set(PRODUCT.INVENTORIABLE, product.isInventoriable() ? (byte) 1 : 0)
		.set(PRODUCT.SERIALIZABLE, product.isSerializable() ? (byte) 1 : 0)
		.set(PRODUCT.LOTABLE, product.isLotable() ? (byte) 1 : 0)
		.set(PRODUCT.STATUS, product.getStatus().value())
		.set(PRODUCT.VAT, product.getVat().getId())
		.set(PRODUCT.RETENTION, product.getRetention().getId())
		.set(PRODUCT.TYPE, product.getType().value())
		.set(PRODUCT.MANUFACTURED, product.isManufactured() ? (byte) 1 : 0)
		.set(PRODUCT.COMPOSITION, product.isComposition() ? (byte) 1 : 0)
		.set(PRODUCT.COMPOSITION_PRICE, product.isCompositionPrice() ? (byte) 1 : 0)
		.set(PRODUCT.SALES_ACCOUNT, product.getSalesAccount().getId())
		.set(PRODUCT.PURCHASE_ACCOUNT, product.getPurchaseAccount().getId())
		.set(PRODUCT.KIND, product.getKind().value())
		.set(PRODUCT.PACKAGED, product.isPackaged() ? (byte) 1: 0)
		.set(PRODUCT.PERISHABLE, product.isPerishable() ? (byte) 1: 0)
		.set(PRODUCT.DAYS_TO_EXPIRE, product.getDaysToExpire())
		.set(PRODUCT.MODIFICATION_USER, ctx.getUser())
		.set(PRODUCT.MODIFICATION_DATE, now)
		.where(PRODUCT.ID.eq(product.getId()))
		.execute();
	
		return product;	
	}
	
	public static ProductBooking saveBooking(AONContext ctx, ProductBooking product) {
		if(!product.getCategory().isEmpty())
			ProductCategoryDAO.save(ctx, product.getCategory());
	
		ProductAutoComplete.autoComplete(ctx, product);
		ProductValidation.validate(ctx, product);
		Product existProduct = get(ctx, f -> f.getDomainProperty().eq(product.getDomain().getId()).and(f.getCodeProperty().eq(product.getCode())));
		if(product.getId() == null && existProduct.getId() != null) product.setId(existProduct.getId());
		return product.getId() != null
			? updateProductBooking(ctx, product)
			: insertProductBooking(ctx, product);
	}
	
	private static ProductBooking insertProductBooking(AONContext ctx, ProductBooking product) {
		insert(ctx, product);
		
		if(null == product.getPosition())
			getNexProductBookingPos(ctx, product);
		
		ctx.getDslContext().insertInto(PRODUCT_BOOKING)
				.set(PRODUCT_BOOKING.PRODUCT, product.getId())
				.set(PRODUCT_BOOKING.DOMAIN, product.getDomain().getId())
				.set(PRODUCT_BOOKING.TYPE, product.getBookingType().value())
				.set(PRODUCT_BOOKING.POS, product.getPosition())
				.set(PRODUCT_BOOKING.JSON, getJsonInfo(product))
				.set(PRODUCT_BOOKING.WORKGROUP, null == product.getWorkgroup() ? null : product.getWorkgroup().getId())
				.set(PRODUCT_BOOKING.TASK_HOLDER, null == product.getTaskHolder() ? null : product.getTaskHolder().getId())
				.set(PRODUCT_BOOKING.CREATION_USER, ctx.getUser())
				.set(PRODUCT_BOOKING.CREATION_DATE, new Timestamp(new Date().getTime()))
				.execute()
				;
		
		return product;
	}

	private static ProductBooking updateProductBooking(AONContext ctx, ProductBooking product) {
		update(ctx, product);
		
		if(null == product.getPosition())
			getNexProductBookingPos(ctx, product);
		
		ctx.getDslContext().update(PRODUCT_BOOKING)
			.set(PRODUCT_BOOKING.TYPE, product.getBookingType().value())
			.set(PRODUCT_BOOKING.POS, product.getPosition())
			.set(PRODUCT_BOOKING.JSON, getJsonInfo(product))
			.set(PRODUCT_BOOKING.WORKGROUP, null == product.getWorkgroup() ? null : product.getWorkgroup().getId())
			.set(PRODUCT_BOOKING.TASK_HOLDER, null == product.getTaskHolder() ? null : product.getTaskHolder().getId())
			.set(PRODUCT_BOOKING.MODIFICATION_USER, ctx.getUser())
			.set(PRODUCT_BOOKING.MODIFICATION_DATE, new Timestamp(new Date().getTime()))
			.where(PRODUCT_BOOKING.PRODUCT.eq(product.getId()))
			.execute()
			;
		
		return product;
	}
	
	private static void getNexProductBookingPos(AONContext ctx, ProductBooking product) {
		Integer count = ctx.getDslContext().selectCount().from(PRODUCT_BOOKING)
			.where(PRODUCT_BOOKING.DOMAIN.eq(product.getDomain().getId()))
			.and(PRODUCT_BOOKING.TYPE.eq(product.getBookingType().value()))
			.fetchOne()
			.value1();
		
		product.setPosition(count);
	}

	private static String getJsonInfo(ProductBooking product) {
		org.json.JSONObject json = new org.json.JSONObject();
		
		json.put("isBookingComposition", product.isBookingComposition());
		json.put("isConsole", product.isConsole());
		
		if(null != product.isNoBooking())
			json.put("noBooking", product.isNoBooking());
		
		if(null != product.isWebhook()) {
			json.put("webhook", product.isWebhook());
			json.put("webhookProductId", product.getWebhookProductId());
		}
		
		if(null != product.getAonApps() && !product.getAonApps().isEmpty()) {
			org.json.JSONArray aonAppsArr = new org.json.JSONArray();
			product.getAonApps().forEach(app -> aonAppsArr.put(app.name()));
			json.put("aonApps", aonAppsArr);
		}
		
		if(null != product.getDomainTypes() && !product.getDomainTypes().isEmpty()) {
			org.json.JSONArray domainTypesArr = new org.json.JSONArray();
			product.getDomainTypes().forEach(domainType -> domainTypesArr.put(domainType.name()));
			json.put("domainTypes", domainTypesArr);
		}
		
		json.put("descriptionTemplate", product.getDescriptionTemplate());
		
		if(null != product.getProjectType())
			json.put("projectType", product.getProjectType().getId());
		
		if(null != product.getBookingPriceType())
			json.put("bookingPriceType", product.getBookingPriceType().name());
		else
			json.put("bookingPriceType", ProductBookingPriceType.PVP.name());
		
		return json.toString();
	}

	public static void delete(AONContext ctx, Integer id) {
		
		ProductDeleteValidation.validate(ctx, id);
		
		ctx.checkWrite();
		
		ctx.getDslContext()
			.delete(CATALOGUE_ITEM)
			.where(CATALOGUE_ITEM.PRODUCT.eq(id));
		
		ctx.getDslContext()
			.delete(ITEM_COMPOSITION)
			.where(ITEM_COMPOSITION.ITEM.eq(
					ctx.getDslContext().select(ITEM.ID)
						.from(ITEM)
						.where(ITEM.PRODUCT.equal(id))
						.fetchOne(ITEM.ID)
			))
			.execute();
		
		ctx.getDslContext()
			.delete(ITEM)
			.where(ITEM.PRODUCT.equal(id))
			.execute();
		
		ctx.getDslContext()
			.delete(PRODUCT_TAG)
			.where(PRODUCT_TAG.PRODUCT.equal(id))
			.execute();
		
		ctx.getDslContext()
			.delete(PRODUCT_BOOKING)
			.where(PRODUCT_BOOKING.PRODUCT.equal(id))
			.execute();
		
		ctx.getDslContext()
			.delete(PRODUCT)
			.where(PRODUCT.ID.equal(id))
			.execute();
	}
	
	
	protected static class ProductFiller extends Filler implements Function<Record, Product> {
		@Override
		public Product apply(Record r) {
			return buildProduct(r);			
		}
		
		public static Product buildProduct(Record r) {
			return build(r, PRODUCT);
		}
		
		public static Product build(Record r, com.esferalia.aon.jooq.tables.Product alias) {
			Product product = new Product()
					.setId(getValue(r, alias.ID))
					.setName(getValue(r, alias.NAME))
					.setDomain(new Domain().setId(getValue(r, alias.DOMAIN)))
					.setBrand(checkField(r, BRAND.ID)
						? BrandFiller.build(r)
						: new Brand().setId(getValue(r, alias.BRAND)))
					.setCategory(checkField(r, PCATEGORY.ID)
							? ProductCategoryFiller.build(r)
							: new ProductCategory().setId(getValue(r, alias.CATEGORY)))
					.setCode(getValue(r, alias.CODE))
					.setComposition(getBoolean(r, alias.COMPOSITION))
					.setCompositionPrice(getBoolean(r, alias.COMPOSITION_PRICE))
					.setInventoriable(getBoolean(r, alias.INVENTORIABLE))
					.setKind(ProductKind.safeValueOf(getValue(r, alias.KIND)))
					.setLotable(getBoolean(r, alias.LOTABLE))
					.setManufactured(getBoolean(r, alias.MANUFACTURED))
					.setPackaged(getBoolean(r, alias.PACKAGED))
					.setPurchaseAccount(new Account().setId(getValue(r, alias.PURCHASE_ACCOUNT)))
					.setRetention(checkField(r, RETENTION_ALIAS.ID)
							? TaxFiller.build(r, RETENTION_ALIAS)
							: new Tax().setType(TaxType.RETENTION).setId(getValue(r, alias.RETENTION)))
					.setSalesAccount(new Account().setId(getValue(r, alias.SALES_ACCOUNT)))
					.setSerializable(getBoolean(r, alias.SERIALIZABLE))
					.setStatus(ProductStatus.safeValueOf(getValue(r, alias.STATUS)))
					.setType(ProductType.safeValueOf(getValue(r, alias.TYPE)))
					.setVat(checkField(r, VAT_ALIAS.ID)
						? TaxFiller.build(r, VAT_ALIAS)
						: new Tax().setType(TaxType.VAT).setId(getValue(r, alias.VAT)))
					.setPerishable(getBoolean(r, alias.PERISHABLE))
					.setDaysToExpire(getValue(r, alias.DAYS_TO_EXPIRE))
					.setCreationDate(getValue(r, alias.CREATION_DATE))
					.setCreationUser(getValue(r, alias.CREATION_USER))
					.setModificationDate(getValue(r, alias.MODIFICATION_DATE))
					.setModificationUser(getValue(r, alias.MODIFICATION_USER));
			
			if(checkField(r, ITEM.ID) && r.get(ITEM.ID) != null) {
				product.setItem(
						new Item()
						.setId(getValue(r, ITEM.ID))
						.setDomain(new Domain().setId(getValue(r, ITEM.DOMAIN)))
						.setDetail(getValue(r, ITEM.DETAIL))
						.setDetail2(getValue(r, ITEM.DETAIL2))
						.setDetail3(getValue(r, ITEM.DETAIL3))
						.setDescription(getValue(r, ITEM.DESCRIPTION))
						.setSerialNumber(getValue(r, ITEM.SERIAL_NUMBER))
						.setSerialDate(getValue(r, ITEM.SERIAL_DATE))
						.setExpireDate(getValue(r, ITEM.EXPIRE_DATE))
						.setPrice(getDouble(r, ITEM.PRICE))
						.setStatus(ProductStatus.safeValueOf(getValue(r, ITEM.STATUS)))
						.setExpensesPercent(getDouble(r, ITEM.EXPENSES_PERCENT))
						.setExpensesFixed(getDouble(r, ITEM.EXPENSES_FIXED))
						.setProfitPercent(getDouble(r, ITEM.PROFIT_PERCENT))
						.setPurchasePrice(getDouble(r, ITEM.PURCHASE_PRICE))				
						.setInternet(getBoolean(r, ITEM.INTERNET))
						.setBarcode(getValue(r, ITEM.BARCODE))
						.setPackFormatTag(new Tag().setId(getValue(r, ITEM.PACK_FORMAT_TAG)))
						.setPackUnits(getInteger(r, ITEM.PACK_UNITS))
						.setPackUnitsTag(new Tag().setId(getValue(r, ITEM.PACK_UNITS_TAG)))
						.setPackMeasurement(getDouble(r, ITEM.PACK_MEASUREMENT))
						.setPackMeasurementTag(new Tag().setId(getValue(r, ITEM.PACK_MEASUREMENT_TAG)))
						.setStockUnitTag(new Tag().setId(getValue(r, ITEM.STOCK_UNIT_TAG)))
						.setCreationUser(getValue(r, ITEM.CREATION_USER))
						.setCreationDate(getValue(r, alias.CREATION_DATE))
						.setModificationUser(getValue(r, ITEM.MODIFICATION_USER))
						.setModificationDate(getValue(r, ITEM.MODIFICATION_DATE))
				);
			}
			
			return product;
		}
		
	}
	
	protected static class ProductBookingFiller extends Filler implements Function<Record, ProductBooking> {
		@Override
		public ProductBooking apply(Record r) {
			return buildProduct(r);			
		}
		
		public static ProductBooking buildProduct(Record r) {
			return build(r, PRODUCT);
		}
		
		public static ProductBooking build(Record r, com.esferalia.aon.jooq.tables.Product alias) {
			ProductBooking productBooking = new ProductBooking();
			productBooking.setId(getValue(r, alias.ID));
			productBooking.setName(getValue(r, alias.NAME));
			productBooking.setDomain(new Domain().setId(getValue(r, alias.DOMAIN)));
			productBooking.setBrand(checkField(r, BRAND.ID)
						? BrandFiller.build(r)
						: new Brand().setId(getValue(r, alias.BRAND)));
			productBooking.setCategory(checkField(r, PCATEGORY.ID)
							? ProductCategoryFiller.build(r)
							: new ProductCategory().setId(getValue(r, alias.CATEGORY)));
			productBooking.setCode(getValue(r, alias.CODE));
			productBooking.setComposition(getBoolean(r, alias.COMPOSITION));
			productBooking.setCompositionPrice(getBoolean(r, alias.COMPOSITION_PRICE));
			productBooking.setInventoriable(getBoolean(r, alias.INVENTORIABLE));
			productBooking.setKind(ProductKind.safeValueOf(getValue(r, alias.KIND)));
			productBooking.setLotable(getBoolean(r, alias.LOTABLE));
			productBooking.setManufactured(getBoolean(r, alias.MANUFACTURED));
			productBooking.setPackaged(getBoolean(r, alias.PACKAGED));
			productBooking.setPurchaseAccount(new Account().setId(getValue(r, alias.PURCHASE_ACCOUNT)));
			productBooking.setRetention(checkField(r, RETENTION_ALIAS.ID)
							? TaxFiller.build(r, RETENTION_ALIAS)
							: new Tax().setType(TaxType.RETENTION).setId(getValue(r, alias.RETENTION)));
			productBooking.setSalesAccount(new Account().setId(getValue(r, alias.SALES_ACCOUNT)));
			productBooking.setSerializable(getBoolean(r, alias.SERIALIZABLE));
			productBooking.setStatus(ProductStatus.safeValueOf(getValue(r, alias.STATUS)));
			productBooking.setType(ProductType.safeValueOf(getValue(r, alias.TYPE)));
			productBooking.setVat(checkField(r, VAT_ALIAS.ID)
						? TaxFiller.build(r, VAT_ALIAS)
						: new Tax().setType(TaxType.VAT).setId(getValue(r, alias.VAT)));
			productBooking.setPerishable(getBoolean(r, alias.PERISHABLE));
			productBooking.setDaysToExpire(getValue(r, alias.DAYS_TO_EXPIRE));
			productBooking.setCreationDate(getValue(r, alias.CREATION_DATE));
			productBooking.setCreationUser(getValue(r, alias.CREATION_USER));
			productBooking.setModificationDate(getValue(r, alias.MODIFICATION_DATE));
			productBooking.setModificationUser(getValue(r, alias.MODIFICATION_USER));
			
			if(checkField(r, ITEM.ID) && r.get(ITEM.ID) != null) {
				productBooking.setItem(
						new Item()
						.setId(getValue(r, ITEM.ID))
						.setDomain(new Domain().setId(getValue(r, ITEM.DOMAIN)))
						.setDetail(getValue(r, ITEM.DETAIL))
						.setDetail2(getValue(r, ITEM.DETAIL2))
						.setDetail3(getValue(r, ITEM.DETAIL3))
						.setDescription(getValue(r, ITEM.DESCRIPTION))
						.setSerialNumber(getValue(r, ITEM.SERIAL_NUMBER))
						.setSerialDate(getValue(r, ITEM.SERIAL_DATE))
						.setExpireDate(getValue(r, ITEM.EXPIRE_DATE))
						.setPrice(getDouble(r, ITEM.PRICE))
						.setStatus(ProductStatus.safeValueOf(getValue(r, ITEM.STATUS)))
						.setExpensesPercent(getDouble(r, ITEM.EXPENSES_PERCENT))
						.setExpensesFixed(getDouble(r, ITEM.EXPENSES_FIXED))
						.setProfitPercent(getDouble(r, ITEM.PROFIT_PERCENT))
						.setPurchasePrice(getDouble(r, ITEM.PURCHASE_PRICE))				
						.setInternet(getBoolean(r, ITEM.INTERNET))
						.setBarcode(getValue(r, ITEM.BARCODE))
						.setPackFormatTag(new Tag().setId(getValue(r, ITEM.PACK_FORMAT_TAG)))
						.setPackUnits(getInteger(r, ITEM.PACK_UNITS))
						.setPackUnitsTag(new Tag().setId(getValue(r, ITEM.PACK_UNITS_TAG)))
						.setPackMeasurement(getDouble(r, ITEM.PACK_MEASUREMENT))
						.setPackMeasurementTag(new Tag().setId(getValue(r, ITEM.PACK_MEASUREMENT_TAG)))
						.setStockUnitTag(new Tag().setId(getValue(r, ITEM.STOCK_UNIT_TAG)))
						.setCreationUser(getValue(r, ITEM.CREATION_USER))
						.setCreationDate(getValue(r, alias.CREATION_DATE))
						.setModificationUser(getValue(r, ITEM.MODIFICATION_USER))
						.setModificationDate(getValue(r, ITEM.MODIFICATION_DATE))
				);
			}
			
			productBooking.setBookingType(ProductBookingType.safeValueOf(getValue(r, PRODUCT_BOOKING.TYPE)));
			productBooking.setPosition(getValue(r, PRODUCT_BOOKING.POS));
			
			if(checkField(r, WORKGROUP.ID) && r.get(WORKGROUP.ID) != null)
				productBooking.setWorkgroup(WorkgroupFiller.build(r));
			
			if(checkField(r, TASK_HOLDER.REGISTRY) && r.get(TASK_HOLDER.REGISTRY) != null)
				productBooking.setTaskHolder(TaskHolderFiller.build(r, TASK_HOLDER_ALIAS));
			
			productBooking.setCreationUser(getValue(r, PRODUCT_BOOKING.CREATION_USER));
			productBooking.setCreationDate(getValue(r, PRODUCT_BOOKING.CREATION_DATE));
			productBooking.setModificationUser(getValue(r, PRODUCT_BOOKING.MODIFICATION_USER));
			productBooking.setModificationDate(getValue(r, PRODUCT_BOOKING.MODIFICATION_DATE));
			
			if(!isNull(r, PRODUCT_BOOKING.JSON))
				parseJsonInfo(productBooking, getValue(r, PRODUCT_BOOKING.JSON));
			else {
				productBooking.setBookingComposition(false);
				productBooking.setConsole(false);
				productBooking.setAonApps(new ArrayList<AonApp>());
				productBooking.setDomainTypes(new ArrayList<DomainType>());
			}
			
			return productBooking;
		}
		
		private static void parseJsonInfo(ProductBooking productBooking, String jsonStr) {
			org.json.JSONObject info = new org.json.JSONObject(jsonStr);
			
			productBooking.setBookingComposition(info.has("isBookingComposition") && info.getBoolean("isBookingComposition"));
			productBooking.setConsole(info.has("isConsole") && info.getBoolean("isConsole"));
			productBooking.setNoBooking(info.has("noBooking") && info.getBoolean("noBooking"));
			productBooking.setWebhook(info.has("webhook") && info.getBoolean("webhook"));
			if(productBooking.isWebhook())
				productBooking.setWebhookProductId(info.getString("webhookProductId"));
			
			List<AonApp> aonApps = new ArrayList<AonApp>();
			if(info.has("aonApps")) {
				org.json.JSONArray aonAppsArr = info.getJSONArray("aonApps");
				if(aonAppsArr.length() > 0) {
					for(int i=0; i < aonAppsArr.length(); i++)
						aonApps.add(AonApp.safeValueOf(aonAppsArr.getString(i)));
				}
			}
			productBooking.setAonApps(aonApps);
			
			List<DomainType> domainTypes = new ArrayList<DomainType>();
			if(info.has("domainTypes")) {
				org.json.JSONArray domainTypesArr = info.getJSONArray("domainTypes");
				if(domainTypesArr.length() > 0) {
					for(int i=0; i < domainTypesArr.length(); i++)
						domainTypes.add(DomainType.safeValueOf(domainTypesArr.getString(i)));
				}
			}
			productBooking.setDomainTypes(domainTypes);
			
			if(info.has("descriptionTemplate"))
				productBooking.setDescriptionTemplate(info.getString("descriptionTemplate"));
			
			if(info.has("projectType"))
				productBooking.setProjectType(new ProjectType().setId( info.getInt("projectType") ));
			
			if(info.has("bookingPriceType"))
				productBooking.setBookingPriceType(ProductBookingPriceType.safeValueOf(info.getString("bookingPriceType")));
			else
				productBooking.setBookingPriceType(ProductBookingPriceType.PVP);
		}
		
	}
}
