package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Brand.BRAND;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Tax.TAX;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Order.ProductOrder;
import com.esferalia.aon.occam.api.model.Properties.ProductProperties;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductKind;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.impl.jooq.dao.BrandDAO.BrandFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ProductCategoryDAO.ProductCategoryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertyOrdersDAO.ProductPropertyOrdersDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaxDAO.TaxFiller;
import com.esferalia.aon.occam.impl.jooq.validation.ProductAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.ProductValidation;


public class ProductDAO {
	
	private static final ProductPropertiesDAO PRODUCT_PROPERTIES = new ProductPropertiesDAO();
	private static final ProductPropertyOrdersDAO PRODUCT_PROPERTY_ORDER_DAO = new ProductPropertyOrdersDAO();
	public static final com.esferalia.aon.jooq.tables.Tax VAT_ALIAS = TAX.as("vat");
	public static final com.esferalia.aon.jooq.tables.Tax RETENTION_ALIAS = TAX.as("retention");

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
	
	public static long getProductCount(AONContext ctx, ProductFilter filter) {
		return ctx.getDslContext()
				.select()
				.from(PRODUCT)
				.where(PRODUCT_PROPERTIES.getConditions(filter))
				.fetch()
				.stream()
				.count();
	}
	
	public static Stream<Product> getStream(AONContext ctx, ProductFilter filter, Integer page, Integer perPage, ProductOrder order){	
		return getStream(ctx, filter, Optional.of(page), Optional.of(perPage), Optional.of(order));
	}
	
	public static Stream<Product> getStream(AONContext ctx, ProductFilter filter) {
		return getStream(ctx, filter, Optional.empty(), Optional.empty(), Optional.empty());
	}
	
	private static Stream<Product> getStream(AONContext ctx, ProductFilter filter, Optional<Integer> page, Optional<Integer> perPage, Optional<ProductOrder> order) {
		SelectConditionStep<Record> query = select(ctx, filter);
		if(order.isPresent()) {
			query.orderBy(PRODUCT_PROPERTY_ORDER_DAO.getOrders(order.get()));
		}
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

	public static Product get(AONContext ctx, ProductFilter filter) {
		return select(ctx, filter).limit(1)
		.fetch().stream().map(new ProductFiller())
		.findFirst().orElse(new Product());
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
	
	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
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
			return new Product()
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
		}
		
	}
}
