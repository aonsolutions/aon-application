package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IProduct;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;

public class ProductImpl implements IProduct{
	
	// ------------------------------------- PRODUCT
	
	@Override
	public Product getProduct(AONContext ctx, Integer productId) {
		return ProductDAO.getProduct(ctx, productId);
	}
	
	@Override
	public void insert(AONContext ctx, Product p) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.insert(ctx, p);
		} );
	}

	@Override
	public void insertWithId(AONContext ctx, Product p) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.insertWithId(ctx, p);
		} );
		
	}

	@Override
	public void insert(AONContext ctx, Stream<Product> ps) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.insert(ctx, ps);
		} );		
	}

	@Override
	public void insertWithId(AONContext ctx, Stream<Product> ps) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.insertWithId(ctx, ps);
		} );		
	}
	
	@Override
	public void update(AONContext ctx, Product p) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.update(ctx, p);
		} );		
	}

	@Override
	public void delete(AONContext ctx, Product p) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.delete(ctx, p);
		} );
	}

	@Override
	public void delete(AONContext ctx, Stream<Product> ps) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.delete(ctx, ps);
		} );		
	}

	// ------------------------------------- PRODUCT_TAG
	
	@Override
	public void insertProductTag(AONContext ctx, ProductTag pt) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.insertProductTag(ctx, pt);
		} );		
	}

	@Override
	public void insertProductTag(AONContext ctx, Stream<ProductTag> pts) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.insertProductTag(ctx, pts);
		} );		
	}

	@Override
	public void updateProductTag(AONContext ctx, ProductTag pt) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.updateProductTag(ctx, pt);
		} );		
	}

	@Override
	public void deleteProductTag(AONContext ctx, ProductTag pt) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.deleteProductTag(ctx, pt);
		} );		
	}

	@Override
	public void deleteProductTag(AONContext ctx, Stream<ProductTag> pts) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.deleteProductTag(ctx, pts);
		} );		
	}

	// ------------------------------------- ITEM
	
	@Override
	public void insertItem(AONContext ctx, Item i) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.insertItem(ctx, i);
		} );
	}

	@Override
	public void insertItem(AONContext ctx, Stream<Item> is) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.insertItem(ctx, is);
		} );
	}

	@Override
	public void updateItem(AONContext ctx, Item i) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.updateItem(ctx, i);
		} );
	}

	@Override
	public void deleteItem(AONContext ctx, Item i) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.deleteItem(ctx, i);
		} );
	}

	@Override
	public void deleteItem(AONContext ctx, Stream<Item> is) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.deleteItem(ctx, is);
		} );
	}

	@Override
	public void insertItemWithId(AONContext ctx, Item i) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.insertItemWithId(ctx, i);
		} );
	}

	@Override
	public void insertItemWithId(AONContext ctx, Stream<Item> is) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.insertItemWithId(ctx, is);
		} );
	}

}
