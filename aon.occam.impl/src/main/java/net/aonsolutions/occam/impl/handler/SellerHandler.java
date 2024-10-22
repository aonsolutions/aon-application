package net.aonsolutions.occam.impl.handler;


import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;

import  org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Registry;

import net.aonsolutions.occam.api.model.Seller;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.handler.RegistryHandler.RegistryFiller;

class SellerHandler {
    
	static final com.esferalia.aon.jooq.tables.Registry REGISTRY_SELLER = REGISTRY.as("registry_seller");
	
    private SellerHandler() {

    }
	
	private static SelectConditionStep<Record> select(AONContext ctx, int domain) {
		return ctx.getDslContext().select()
			.from(SELLER)
			.join(REGISTRY_SELLER).on(REGISTRY_SELLER.ID.eq(SELLER.REGISTRY))
			.where(SELLER.DOMAIN.eq(domain))
			.and(SELLER.SCOPE.in(ctx.getUserScopes(domain)))
		;
	}

	static class SellerFiller extends Filler<Seller> {
	
		@Override
		public Seller apply(Record r) {
			return build(r);
		}

		static Seller build(Record r) {
			return build(r, REGISTRY_SELLER);			
		}
		
		static Seller build(Record r, Registry registry) {
			if (isNull(r,SELLER.REGISTRY)) return null;
			return RegistryFiller.build(r, registry, Seller::new)
				.setId(getValue(r, SELLER.REGISTRY))
				.setScope(getValue(r, SELLER.SCOPE))
				.setActive(!getBoolean(r, SELLER.STATUS))
			;
		}

	}
	
	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	static Seller getRandom(AONContext ctx, int domain) {
		return select(ctx,domain)
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new SellerFiller())
			.findFirst()
			.orElse(null);
	}
}
