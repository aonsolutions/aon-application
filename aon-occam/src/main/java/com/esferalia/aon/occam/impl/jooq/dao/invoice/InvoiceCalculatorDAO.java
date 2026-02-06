package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import static com.esferalia.aon.jooq.tables.Item.ITEM;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class InvoiceCalculatorDAO {
	
	private InvoiceCalculatorDAO() {
		
	}
	
	public static Invoice calculate(AONContext ctx, Invoice i) {
		i.detailStream().forEach(d -> calculate(ctx, d));
		i.refreshTaxBreakdown();
		i.getTaxBreakdown().
			ifPresent( tb -> {
				i.setTaxableBase( i.getTaxableBaseSum() );
				i.setVatQuota( tb.getVatQuota(i) );
				i.setRetentionQuota( tb.getRetentionQuota() );
				i.setTotal( tb.getTotal(i) );
			})
		;
		return i;
	}

	public static InvoiceDetail calculate(AONContext ctx, InvoiceDetail detail) {
		return calculate(ctx, detail, false);
	}

	private static InvoiceDetail calculate(AONContext ctx, InvoiceDetail detail, boolean refreshPrice) {
		double price = refreshPrice
			? getItemPrice(ctx, detail)
			: detail.getPrice();
		price = (price + detail.getTaxes()) * detail.getQuantity();
		MutableDouble mprice = new MutableDouble(price); 
		AonCollectionUtils.stream( detail.getDiscountExpression().getDiscounts() )
			.forEach(disc -> mprice.setValue(mprice.getValue() * ( 1 - disc / 100)));
		double base = AonMathUtils.round(mprice.getValue(), 4);
		detail.taxStream().forEach( t -> t.setBase( base ));
		return detail.setTaxableBase( base );
	}
	
	private static double getItemPrice(AONContext ctx, InvoiceDetail detail) {
		return detail.optItem()
			.map( Item::getId )
			.flatMap( id  -> ctx.getDslContext()
				.select( ITEM.PRICE )
				.from(ITEM)
				.where(ITEM.ID.eq(detail.getItem().getId()))
				.fetch()
				.stream()
				.map( r -> r.getValue(ITEM.PRICE) )
				.findFirst() 
			)
			.orElse( 0.0 )
		;
	}
	
}