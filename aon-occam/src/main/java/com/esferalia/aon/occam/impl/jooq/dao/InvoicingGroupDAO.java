package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.InvoicingGroup.INVOICING_GROUP;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.Timestamp;
import java.util.Date;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CustomerFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class InvoicingGroupDAO {
	
	private InvoicingGroupDAO() {

	}

	// ****************************************************************
	// 		 			 	 	READ METHODS
	// ****************************************************************
	
	private static SelectConditionStep<Record> select( AONContext ctx, Integer domainId) {
		return ctx.getDslContext()
			.select()
			.from(INVOICING_GROUP)
			.innerJoin(CUSTOMER).on(CUSTOMER.REGISTRY.eq(INVOICING_GROUP.CUSTOMER))
			.innerJoin(REGISTRY).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
			.where(INVOICING_GROUP.DOMAIN.eq(domainId))
		;
	}
	
	public static Stream<InvoicingGroup> stream(AONContext ctx, Integer domainId){
		ctx.checkWrite();
		return select(ctx, domainId)
			.fetch()
			.stream()
			.map( new InvoicingGroupFiller() )
		;
	}

	private static Stream<InvoicingGroup> stream(AONContext ctx, Integer domainId, Supplier<Condition> cond){
		ctx.checkWrite();
		return select(ctx, domainId)
			.and(cond.get())
			.fetch()
			.stream()
			.map( new InvoicingGroupFiller() )
		;
	}
	
	public static Stream<InvoicingGroup> streamNameLike(AONContext ctx, Integer domainId, String query) {
		return stream(ctx, domainId, () -> INVOICING_GROUP.DESCRIPTION.containsIgnoreCase(query));
	}
	public static Stream<InvoicingGroup> streamNameEqual(AONContext ctx, Integer domainId, String name) {
		return stream(ctx, domainId, () -> INVOICING_GROUP.DESCRIPTION.eq(name));
	}
	
	// ****************************************************************
	// 		 			 	 	WRITE METHODS
	// ****************************************************************
	public static InvoicingGroup save(AONContext ctx, InvoicingGroup invoicingGroup) {
		ctx.checkWrite();
		return invoicingGroup.getId() != null 
				? update(ctx, invoicingGroup) 
				: insert(ctx, invoicingGroup) ;
	}
	
	private static InvoicingGroup insert(AONContext ctx, InvoicingGroup invoicingGroup) {
		if (invoicingGroup.getCustomer() == null || invoicingGroup.getCustomer().getId() == null) {
			throw new AonCoreException("Cliente no válido para el grupo de facturación");
		}
		Integer id = ctx.getDslContext()
			.insertInto(INVOICING_GROUP)
			.set(INVOICING_GROUP.DOMAIN, invoicingGroup.getDomain())
			.set(INVOICING_GROUP.CUSTOMER, invoicingGroup.getCustomer().getId())
			.set(INVOICING_GROUP.CUSTOMER_GROUPED, AonEnumUtils.getByte( invoicingGroup.isCustomerGrouped() ) )
			.set(INVOICING_GROUP.DESCRIPTION, invoicingGroup.getDescription())
			.set(INVOICING_GROUP.CREATION_DATE,  new Timestamp(new Date().getTime()))
			.set(INVOICING_GROUP.CREATION_USER, ctx.getUser())
			.set(INVOICING_GROUP.MODIFICATION_DATE,  new Timestamp(new Date().getTime()))
			.set(INVOICING_GROUP.MODIFICATION_USER, ctx.getUser())
			.returning(INVOICING_GROUP.ID).fetchOne().getValue(INVOICING_GROUP.ID);
		return invoicingGroup.setId(id);
	}
	
	private static InvoicingGroup update(AONContext ctx, InvoicingGroup invoicingGroup) {
		ctx.getDslContext()
			.update(INVOICING_GROUP)
			.set(INVOICING_GROUP.DESCRIPTION, invoicingGroup.getDescription())
			.set(INVOICING_GROUP.MODIFICATION_DATE,  new Timestamp(new Date().getTime()))
			.set(INVOICING_GROUP.MODIFICATION_USER, ctx.getUser())
			.where(INVOICING_GROUP.ID.eq(invoicingGroup.getId()))
			.execute();

		
		return invoicingGroup;
	}
	
	

	// ****************************************************************
	// 		 			 	 	FILLERS
	// ****************************************************************
	static class InvoicingGroupFiller extends Filler implements Function<Record, InvoicingGroup> {

		@Override
		public InvoicingGroup apply(Record r) {
			return buildInvoicingGroup(r);			
		}
		
		static InvoicingGroup buildInvoicingGroup(Record r) {
			return new InvoicingGroup()
				.setId(getValue(r, INVOICING_GROUP.ID))
				.setDomain(getValue(r, INVOICING_GROUP.DOMAIN))
				.setCustomer(CustomerFiller.build(r))
				.setCustomerGrouped(getBoolean(r, INVOICING_GROUP.CUSTOMER_GROUPED))
				.setDescription(getValue(r, INVOICING_GROUP.DESCRIPTION))
				.setCreationDate(getValue(r, INVOICING_GROUP.CREATION_DATE))
				.setCreationUser(getValue(r, INVOICING_GROUP.CREATION_USER))
				.setModificationDate(getValue(r, INVOICING_GROUP.MODIFICATION_DATE))
				.setModificationUser(getValue(r, INVOICING_GROUP.MODIFICATION_USER));		
		}
	}
	
}
