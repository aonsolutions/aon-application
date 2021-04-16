package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;

import java.sql.Timestamp;
import java.util.function.Function;
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
import com.esferalia.aon.occam.api.model.Properties.ProductProperties;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductKind;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.impl.jooq.dao.ProductCategoryDAO.ProductCategoryFiller;
import com.esferalia.aon.occam.impl.jooq.validation.ProductAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.ProductValidation;


public class ProductDAO {
	
	private static final ProductPropertiesDAO PRODUCT_PROPERTIES = new ProductPropertiesDAO();

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
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(PRODUCT.NAME);}
		@Override public Property<String> getCodeProperty() {return new FilterDAO.PropertyDAO<String>(PRODUCT.CODE);}
		@Override public Property<Byte> getKindProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.KIND);}
		@Override public Property<Integer> getBrandProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.BRAND);}
		@Override public Property<Integer> getCategoryProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.CATEGORY);}
		@Override public Property<Byte> getInventoriableProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.INVENTORIABLE);}
		@Override public Property<Byte> getSerializableProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.SERIALIZABLE);}
		@Override public Property<Byte> getLotableProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.LOTABLE);}
		@Override public Property<Byte> getStatusProperty() { return new FilterDAO.PropertyDAO<Byte>(PRODUCT.STATUS);}
		@Override public Property<Integer> getVatProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.VAT);}
		@Override public Property<Integer> getRetentionProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.RETENTION);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.TYPE);}
		@Override public Property<Byte> getManufacturedProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.MANUFACTURED);}
		@Override public Property<Byte> getCompositionProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.COMPOSITION);}
		@Override public Property<Byte> getCompositionPriceProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.COMPOSITION_PRICE);}
		@Override public Property<Integer> getSalesAccountProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.SALES_ACCOUNT);}
		@Override public Property<Integer> getPurchaseAccountProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.PURCHASE_ACCOUNT);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(PRODUCT.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(PRODUCT.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(PRODUCT.NAME);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(PRODUCT.MODIFICATION_DATE);}
	}

	private static SelectConditionStep<Record> select(AONContext ctx, ProductFilter filter) {
		return ctx.getDslContext()
				.select()
				.from(PRODUCT)
				.leftOuterJoin(PCATEGORY).on(PCATEGORY.ID.eq(PRODUCT.CATEGORY))
				.where(PRODUCT_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<Product> getStream(AONContext ctx, ProductFilter filter) {
		return select(ctx, filter)
		.fetch().stream().map(new ProductFiller());
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
	
	
	protected static class ProductFiller implements Function<Record, Product> {
		@Override
		public Product apply(Record r) {
			return buildProduct(r);			
		}
		
		public static Product buildProduct(Record r) {
			return new Product()
					.setId(r.getValue(PRODUCT.ID))
					.setName(r.getValue(PRODUCT.NAME))
					.setDomain(new Domain().setId(r.getValue(PRODUCT.DOMAIN)))
					.setBrand(new Brand().setId(r.getValue(PRODUCT.BRAND)))
					.setCategory(ProductCategoryFiller.build(r))
					.setCode(r.getValue(PRODUCT.CODE))
					.setComposition(r.getValue(PRODUCT.COMPOSITION) == 1)
					.setCompositionPrice(r.getValue(PRODUCT.COMPOSITION_PRICE) == 1)
					.setInventoriable(r.getValue(PRODUCT.INVENTORIABLE) == 1)
					.setKind(ProductKind.safeValueOf(r.getValue(PRODUCT.KIND)))
					.setLotable(r.getValue(PRODUCT.LOTABLE) == 1)
					.setManufactured(r.getValue(PRODUCT.MANUFACTURED) == 1)
					.setPackaged(r.getValue(PRODUCT.PACKAGED) == 1)
					.setPurchaseAccount(new Account().setId(r.getValue(PRODUCT.PURCHASE_ACCOUNT)))
					.setRetention(new Tax().setType(TaxType.RETENTION).setId(r.getValue(PRODUCT.RETENTION)))
					.setSalesAccount(new Account().setId(r.getValue(PRODUCT.SALES_ACCOUNT)))
					.setSerializable(r.getValue(PRODUCT.SERIALIZABLE) == 1)
					.setStatus(ProductStatus.safeValueOf(r.getValue(PRODUCT.STATUS)))
					.setType(ProductType.safeValueOf(r.getValue(PRODUCT.TYPE)))
					.setVat(new Tax().setType(TaxType.VAT).setId(r.getValue(PRODUCT.VAT)))
					.setCreationDate(r.getValue(PRODUCT.CREATION_DATE))
					.setCreationUser(r.getValue(PRODUCT.CREATION_USER))
					.setModificationDate(r.getValue(PRODUCT.MODIFICATION_DATE))
					.setModificationUser(r.getValue(PRODUCT.MODIFICATION_USER));
		}
		
	}
}
