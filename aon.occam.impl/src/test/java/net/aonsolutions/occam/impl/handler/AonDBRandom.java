package net.aonsolutions.occam.impl.handler;

import java.util.LinkedList;
import java.util.stream.Collectors;

import net.aonsolutions.occam.api.model.Account;
import net.aonsolutions.occam.api.model.Activity;
import net.aonsolutions.occam.api.model.AonRandom;
import net.aonsolutions.occam.api.model.Creditor;
import net.aonsolutions.occam.api.model.Customer;
import net.aonsolutions.occam.api.model.Geozone;
import net.aonsolutions.occam.api.model.InvestAsset;
import net.aonsolutions.occam.api.model.Seller;
import net.aonsolutions.occam.api.model.Supplier;
import net.aonsolutions.occam.api.model.Tariff;
import net.aonsolutions.occam.impl.AONContext;

public class AonDBRandom {
	private AonDBRandom() {
		
	}
	// ----------------------------------------------------------------------- [ACCOUNT]
	public static Account getCreditorAccount(AONContext ctx, int domain ) {
		return AccountHandler.getRandom(ctx, domain, p -> p.getCodeProperty().like("410%"));
	}
	
	public static Account getCustomerAccount(AONContext ctx, int domain ) {
		return AccountHandler.getRandom(ctx, domain, p -> p.getCodeProperty().like("410%"));
	}

	public static Account getSupplierAccount(AONContext ctx, int domain ) {
		return AccountHandler.getRandom(ctx, domain, p -> p.getCodeProperty().like("400%"));
	}
	
	// ----------------------------------------------------------------------- [ACTIVITY]
	public static Activity getActivity(AONContext ctx, int domain) {
		return AonRandom.gt(10)
			? ctx.getActivities(domain).filter(Activity::isMain).findFirst().orElse(null)
			: AonRandom.random( ctx.getActivities(domain).collect(Collectors.toCollection(LinkedList::new))).orElse(null)
		;
	}
	// ----------------------------------------------------------------- [CREDITOR]
	public static Creditor getCreditor(AONContext ctx, int domain) {
		return CreditorHandler.getRandom(ctx, domain, null);
	}
	// ----------------------------------------------------------------- [CUSTOMER]
	public static Customer getCustomer(AONContext ctx, int domain) {
		return CustomerHandler.getRandom(ctx, domain, null);
	}
	// ----------------------------------------------------------------- [GEOZONE]
	public static Geozone getGeozone(AONContext ctx, int domain) {
		return GeozoneHandler.getRandom(ctx, domain, null);
	}
	// ----------------------------------------------------------------- [INVEST ASSET]
	public static InvestAsset getInvestAsset(AONContext ctx, int domain) {
		return AonRandom
			.random( ctx.getInvestAssets(domain).collect(Collectors.toCollection(LinkedList::new)))
			.orElse(null);
	}
	// ----------------------------------------------------------------------- [SELLER]
	public static Seller getSeller(AONContext ctx, int domain) {
		return SellerHandler.getRandom(ctx, domain);
	}
	// ----------------------------------------------------------------------- [SUPPLIER]
	public static Supplier getSupplier(AONContext ctx, int domain) {
		return SupplierHandler.getRandom(ctx, domain, null);
	}
	// ----------------------------------------------------------------------- [TARIFF]
	public  static Tariff getPurchaseTariff(AONContext ctx, int domain ) {
		return TariffHandler.getRandom(ctx, domain, f -> f.getPurchaseProperty().eq((byte)1));
	}
	
	public  static Tariff getSalesTariff(AONContext ctx, int domain ) {
		return TariffHandler.getRandom(ctx, domain, f -> f.getPurchaseProperty().eq((byte)0));
	}
}

