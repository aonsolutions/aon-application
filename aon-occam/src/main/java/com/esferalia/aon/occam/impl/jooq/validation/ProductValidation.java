package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.ProductTag.PRODUCT_TAG;
import static com.esferalia.aon.jooq.tables.Tag.TAG;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.product.OldProduct;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ProductValidation {
	
	// ------------------------------------ PRODUCT
	
	/**
	 * El dominio del producto no puede estar vacio.
	 */
	public static BiConsumer<OldProduct,AONContext> EMPTY_DOMAIN = (p,ctx) -> {
		if (p.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * El nombre del producto no puede estar vacio.
	 */
	public static BiConsumer<OldProduct,AONContext> CHECK_PRODUCT = (p,ctx) -> {
		if(p.getName() == null)
			throw new AonCoreException(AonError.EMPTY_PRODUCT_NAME.getMessage());
		if(p.isSerializable() && !p.isInventoriable())
			p.setInventoriable(true);
		if(p.isInventoriable() && p.isComposition())
			p.setComposition(false);
		if(p.isComposition() && p.isCompositionPrice())
			p.setCompositionPrice(false);	
	};
	
	/**
	 * El código del producto no puede estar vacio.
	 * 	&&
	 * El código del producto no puede estar duplicado en el mismo dominio.
	 *  &&
	 * El código del producto no puede estar duplicado con un producto del dominio padre o hijo.
	 */
	public static BiConsumer<OldProduct, AONContext> CHECK_VALID_CODE = (p, ctx) -> {
		if(p.getCode() == null)
			throw new AonCoreException(AonError.EMPTY_PRODUCT_CODE.getMessage());
		
		int count = ctx.getDslContext().selectCount()
				.from(PRODUCT)
				.where(PRODUCT.DOMAIN.eq(p.getDomain()))
				.and(PRODUCT.CODE.eq(p.getCode()))
				.fetchOne(0,int.class);
		if (count>0) 
			throw new AonCoreException(AonError.DUPLICATE_PRODUCT_CODE.format(p.getCode()));
		
		/*count = ctx.getDslContext().selectCount()
				.from(PRODUCT).join(DOMAIN).on(PRODUCT.DOMAIN.eq(DOMAIN.PARENT))
				.where(DOMAIN.ID.eq(p.getDomain()))
				.and(PRODUCT.CODE.eq(p.getCode()))
				.fetchOne(0,int.class);
		if (count>0){
			String domain = ctx.getDslContext().select(DOMAIN.NAME)
					.from(PRODUCT).join(DOMAIN).on(PRODUCT.DOMAIN.eq(DOMAIN.PARENT))
					.where(DOMAIN.ID.eq(p.getDomain()))
					.and(PRODUCT.CODE.eq(p.getCode()))
					.fetchOne().value1();
			throw new AonCoreException(AonError.DUPLICATE_PRODUCT_CODE_DOMAIN.format(domain));
		}
		count = ctx.getDslContext().selectCount()
				.from(PRODUCT).join(DOMAIN).on(PRODUCT.DOMAIN.eq(DOMAIN.ID))
				.where(DOMAIN.PARENT.in(ctx.getDslContext().select(DOMAIN.PARENT)
											.from(DOMAIN)
											.where(DOMAIN.ID.eq(p.getDomain()))))
				.and(PRODUCT.CODE.eq(p.getCode()))
				.fetchOne(0,int.class);
		if(count>0){
			String domain = ctx.getDslContext().select(DOMAIN.NAME)
					.from(PRODUCT).join(DOMAIN).on(PRODUCT.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.PARENT.in(ctx.getDslContext().select(DOMAIN.PARENT)
												.from(DOMAIN)
												.where(DOMAIN.ID.eq(p.getDomain()))))
					.and(PRODUCT.CODE.eq(p.getCode()))
					.fetchOne().value1();
			throw new AonCoreException(AonError.DUPLICATE_PRODUCT_CODE_DOMAIN.format(domain));
		}*/

	};
	
	public static void validate(AONContext ctx, OldProduct p) throws AonCoreException {
		EMPTY_DOMAIN
			.andThen(CHECK_PRODUCT)
			.andThen(CHECK_VALID_CODE)
			.accept(p, ctx);
	}
	
	// ------------------------------------ PRODUCT_TAG
	
	/**
	 * El dominio de productTag no puede estar vacio.
	 */
	public static BiConsumer<ProductTag,AONContext> EMPTY_DOMAIN_PRODUCT_TAG = (pt,ctx) -> {
		if (pt.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * El product de productTag no puede estar vacio.
	 */
	public static BiConsumer<ProductTag,AONContext> EMPTY_PRODUCT = (pt,ctx) -> {
		if (pt.getProduct() == null) 
			throw new AonCoreException(AonError.EMPTY_PRODUCT.getMessage());
	};
	
	/**
	 * La etiqueta de productTag no puede estar vacio.
	 */
	public static BiConsumer<ProductTag,AONContext> EMPTY_TAG = (pt,ctx) -> {
		if (pt.getTag() == null) 
			throw new AonCoreException(AonError.EMPTY_TAG.getMessage());
	};

	/**
	 * El product de productTag no existe en el dominio.
	 */
	public static BiConsumer<ProductTag,AONContext> EXIST_PRODUCT = (pt,ctx) -> {
		int count = ctx.getDslContext().selectCount()
				.from(PRODUCT)
				.where(PRODUCT.ID.eq(pt.getProduct()))
				.and(PRODUCT.DOMAIN.eq(pt.getDomain()))
				.fetchOne(0,int.class);
		if (count>0) 
			throw new AonCoreException(AonError.EXIST_PRODUCT.getMessage());
	};
	
	/**
	 * La etiqueta de productTag no existe en el dominio.
	 */
	public static BiConsumer<ProductTag,AONContext> EXIST_TAG = (pt,ctx) -> {
		int count = ctx.getDslContext().selectCount()
				.from(TAG)
				.where(TAG.ID.eq(pt.getTag().getId()))
				.and(TAG.DOMAIN.eq(pt.getDomain()))
				.fetchOne(0,int.class);
		if (count<=0) 
			throw new AonCoreException(AonError.EXIST_TAG.getMessage());
	};
	
	/**
	 * La etiqueta de un producto no puede estar duplicada.
	 */
	public static BiConsumer<ProductTag,AONContext> DUPLICATE_PRODUCT_TAG = (pt,ctx) -> {
		int count = ctx.getDslContext().selectCount()
				.from(PRODUCT_TAG)
				.where(PRODUCT_TAG.DOMAIN.eq(pt.getDomain()))
				.and(PRODUCT_TAG.PRODUCT.eq(pt.getProduct()))
				.and(PRODUCT_TAG.TAG.eq(pt.getTag().getId()))
				.fetchOne(0,int.class);
		if (count>0){ 
			String tag = ctx.getDslContext().select(TAG.NAME)
					.from(TAG)
					.where(TAG.ID.eq(pt.getTag().getId()))
					.fetchOne().value1();
			throw new AonCoreException(AonError.DUPLICATE_PRODUCT_TAG.format(tag));
		}
	};
	public static void validateProductTag(AONContext ctx, ProductTag pt) throws AonCoreException {
		EMPTY_DOMAIN_PRODUCT_TAG
			.andThen(EMPTY_PRODUCT)
			.andThen(EMPTY_TAG)
			//.andThen(EXIST_PRODUCT)
			//.andThen(EXIST_TAG)
			//.andThen(DUPLICATE_PRODUCT_TAG)
			.accept(pt, ctx);
	}
	
	// ------------------------------------ ITEM
	
	/**
	 * El dominio de Item no puede estar vacio.
	 */
	public static BiConsumer<OldItem,AONContext> EMPTY_DOMAIN_ITEM = (i,ctx) -> {
		if (i.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * El id de Producto de Item no puede estar vacio.
	 */
	public static BiConsumer<OldItem,AONContext> EMPTY_PRODUCT_ITEM = (i,ctx) -> {
		if (i.getProductId() == null) 
			throw new AonCoreException(AonError.EMPTY_PRODUCT.getMessage());
	};
	
	/**
	 * El dominio de Item no puede estar vacio.
	 */
	public static BiConsumer<OldItem,AONContext> EXIST_PRODUCT_ITEM = (i,ctx) -> {
		int count = ctx.getDslContext().selectCount()
				.from(PRODUCT)
				.where(PRODUCT.ID.eq(i.getProductId()))
				.and(PRODUCT.DOMAIN.eq(i.getDomain()))
				.fetchOne(0,int.class);
		if (count>0) 
			throw new AonCoreException(AonError.EXIST_PRODUCT.getMessage());
	};
	
	/**
	 * El Código de barras del producto no puede estar duplicado en el mismo dominio.
	 * 	&&
	 * El Código de barras del producto no puede estar duplicado con el dominio padre o hijo.
	 */
	public static BiConsumer<OldItem, AONContext> CHECK_VALID_BARCODE = (i,ctx) -> {
		if(i.getBarcode() != null){
			int count = ctx.getDslContext().selectCount()
					.from(ITEM)
					.where(ITEM.DOMAIN.eq(i.getDomain()))
					.and(ITEM.BARCODE.eq(i.getBarcode()))
					.fetchOne(0,int.class);
			if(count>0)
				throw new AonCoreException(AonError.DUPLICATE_BARCODE.format(i.getBarcode()));
			
			count = ctx.getDslContext().selectCount()
					.from(ITEM).join(DOMAIN).on(ITEM.DOMAIN.eq(DOMAIN.PARENT))
					.where(DOMAIN.ID.eq(i.getDomain()))
					.and(ITEM.BARCODE.eq(i.getBarcode()))
					.fetchOne(0,int.class);
			if (count>0){
				String domain = ctx.getDslContext().select(DOMAIN.NAME)
						.from(ITEM).join(DOMAIN).on(ITEM.DOMAIN.eq(DOMAIN.PARENT))
						.where(DOMAIN.ID.eq(i.getDomain()))
						.and(ITEM.BARCODE.eq(i.getBarcode()))
						.fetchOne().value1();
				throw new AonCoreException(AonError.DUPLICATE_BARCODE_DOMAIN.format(domain));
			}
			Boolean heredity = ctx.getDslContext().select(DOMAIN.ENABLEHEREDITY)
					.from(DOMAIN).where(DOMAIN.ID.eq(i.getDomain())).fetchOne().value1() == 1;
			if(heredity) {
				count = ctx.getDslContext().selectCount()
					.from(ITEM).join(DOMAIN).on(ITEM.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.ID.in(ctx.getDslContext().select(DOMAIN.PARENT)
												.from(DOMAIN)
												.where(DOMAIN.ID.eq(i.getDomain()))))
					.and(ITEM.BARCODE.eq(i.getBarcode()))
					.fetchOne(0,int.class);
				if(count>0){
					String domain = ctx.getDslContext().select(DOMAIN.NAME)
						.from(ITEM).join(DOMAIN).on(ITEM.DOMAIN.eq(DOMAIN.ID))
						.where(DOMAIN.ID.in(ctx.getDslContext().select(DOMAIN.PARENT)
													.from(DOMAIN)
													.where(DOMAIN.ID.eq(i.getDomain()))))
						.and(ITEM.BARCODE.eq(i.getBarcode()))
						.fetchOne().value1();
					throw new AonCoreException(AonError.DUPLICATE_BARCODE_DOMAIN.format(domain));
				}
			}
		}
	};
	
	/**
	 * El detalle de un producto no puede estar duplicado para un mismo dominio.
	 */
	public static BiConsumer<OldItem, AONContext> CHECK_VALID_DETAILS = (i,ctx) -> {
		int count;
		if(i.getDetail() != null)
			count = ctx.getDslContext().selectCount()
					.from(ITEM)
					.where(ITEM.DOMAIN.eq(i.getDomain()))
					.and(ITEM.DETAIL.eq(i.getDetail()))
					.and(ITEM.PRODUCT.eq(i.getProductId()))
					.fetchOne(0,int.class);
		else
			count = ctx.getDslContext().selectCount()
			.from(ITEM)
			.where(ITEM.DOMAIN.eq(i.getDomain()))
			.and(ITEM.DETAIL.isNull())
			.and(ITEM.PRODUCT.eq(i.getProductId()))
			.fetchOne(0,int.class);
		int count2;
		if(i.getDetail2() != null)
			count2 = ctx.getDslContext().selectCount()
				.from(ITEM)
				.where(ITEM.DOMAIN.eq(i.getDomain()))
				.and(ITEM.DETAIL2.eq(i.getDetail2()))
				.and(ITEM.PRODUCT.eq(i.getProductId()))
				.fetchOne(0,int.class);
		else
			count2 = ctx.getDslContext().selectCount()
				.from(ITEM)
				.where(ITEM.DOMAIN.eq(i.getDomain()))
				.and(ITEM.DETAIL2.isNull())
				.and(ITEM.PRODUCT.eq(i.getProductId()))
				.fetchOne(0,int.class);
		int count3;
		if(i.getDetail3() != null)
			count3 = ctx.getDslContext().selectCount()
				.from(ITEM)
				.where(ITEM.DOMAIN.eq(i.getDomain()))
				.and(ITEM.DETAIL3.eq(i.getDetail3()))
				.and(ITEM.PRODUCT.eq(i.getProductId()))
				.fetchOne(0,int.class);
		else
			count3 = ctx.getDslContext().selectCount()
				.from(ITEM)
				.where(ITEM.DOMAIN.eq(i.getDomain()))
				.and(ITEM.DETAIL3.isNull())
				.and(ITEM.PRODUCT.eq(i.getProductId()))
				.fetchOne(0,int.class);
		
		int count4;
		if(i.getSerialNumber() != null)
			count4 = ctx.getDslContext().selectCount()
				.from(ITEM)
				.where(ITEM.DOMAIN.eq(i.getDomain()))
				.and(ITEM.SERIAL_NUMBER.eq(i.getSerialNumber()))
				.and(ITEM.PRODUCT.eq(i.getProductId()))
				.fetchOne(0, int.class);
		else count4 = ctx.getDslContext().selectCount()
				.from(ITEM)
				.where(ITEM.DOMAIN.eq(i.getDomain()))
				.and(ITEM.SERIAL_NUMBER.isNull())
				.and(ITEM.PRODUCT.eq(i.getProductId()))
				.fetchOne(0, int.class);
		
		if(count>0 && count2>0 && count3>0 && count4>0)
			throw new AonCoreException(AonError.DUPLICATE_DETAILS.format(i.getDetails()));
		
	};
	
	/**
	 * EL número de serie del producto no puede estar duplicado.
	 */
	public static BiConsumer<OldItem, AONContext> CHECK_VALID_SERIAL_NUMBER = (i,ctx) -> {		
		if(i.getSerialNumber() != null){
			int count = ctx.getDslContext().selectCount()
				.from(ITEM)
				.where(ITEM.DOMAIN.eq(i.getDomain()))
				.and(ITEM.PRODUCT.eq(i.getProductId()))
				.and(ITEM.SERIAL_NUMBER.eq(i.getSerialNumber()))
				.fetchOne(0,int.class);
			if(count>0)
				throw new AonCoreException(AonError.DUPLICATE_SERIAL_NUMBER.format(i.getSerialNumber()));

		}
	};
	
	/**
	 * El producto tiene que ser inventariable.
	 */
	public static BiConsumer<Integer, AONContext> CHECK_INVENTORIABLE = (itemId,ctx) -> {
		byte inventoriable = ctx.getDslContext().select(PRODUCT.INVENTORIABLE)
				.from(PRODUCT.leftJoin(ITEM).on(ITEM.PRODUCT.eq(PRODUCT.ID)))
				.where(ITEM.ID.eq(itemId))
				.fetchOne(PRODUCT.INVENTORIABLE);
		if(inventoriable==0)
			throw new AonCoreException("El producto no es inventariable");
	};
	
	public static void validateItem(AONContext ctx, OldItem i) throws AonCoreException{
		EMPTY_DOMAIN_ITEM
			.andThen(EMPTY_PRODUCT_ITEM)
			//.andThen(EXIST_PRODUCT_ITEM)
			.andThen(CHECK_VALID_BARCODE)
			.andThen(CHECK_VALID_DETAILS)
			.andThen(CHECK_VALID_SERIAL_NUMBER)
			.accept(i, ctx);
	}
	
	public static void validateStocking(AONContext ctx, Integer itemId) throws AonCoreException{
		(CHECK_INVENTORIABLE)
			.accept(itemId, ctx);
	}
	
}
