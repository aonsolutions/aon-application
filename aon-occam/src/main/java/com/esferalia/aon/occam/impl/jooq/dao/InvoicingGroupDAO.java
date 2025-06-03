package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.InvoicingGroup.INVOICING_GROUP;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.InvoicingGroupProperties;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroupFilter;

public class InvoicingGroupDAO {
	
	private InvoicingGroupDAO() {

	}

	private static final InvoicingGroupPropertiesDAO INVOICING_GROUP_PROPERTIES = new InvoicingGroupPropertiesDAO();
	private static class InvoicingGroupPropertiesDAO implements InvoicingGroupProperties {

		private Condition[] getConditions(InvoicingGroupFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICING_GROUP.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(INVOICING_GROUP.CREATION_USER);}
		@Override public Property<Integer> getCustomerProperty() {return new FilterDAO.PropertyDAO<>(INVOICING_GROUP.CUSTOMER);}
		@Override public Property<Byte> getCustomerGroupedProperty() {return new FilterDAO.PropertyDAO<>(INVOICING_GROUP.CUSTOMER_GROUPED);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(INVOICING_GROUP.DESCRIPTION);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(INVOICING_GROUP.DOMAIN);}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICING_GROUP.ID);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICING_GROUP.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(INVOICING_GROUP.MODIFICATION_USER);}
	}

	public static LinkedList<InvoicingGroup> getInvoicingGroupList(AONContext ctx, InvoicingGroupFilter filter){
		return ctx.getDslContext().select(INVOICING_GROUP.ID,INVOICING_GROUP.DESCRIPTION)
				.from(INVOICING_GROUP).where(INVOICING_GROUP_PROPERTIES.getConditions(filter))
				.fetchInto(INVOICING_GROUP).stream().map(new InvoicingGroupFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	private static InvoicingGroup insert(AONContext ctx, InvoicingGroup invoicingGroup) {
		Integer id = ctx.getDslContext()
			.insertInto(INVOICING_GROUP)
			.set(INVOICING_GROUP.DOMAIN, invoicingGroup.getDomain())
			.set(INVOICING_GROUP.CUSTOMER, invoicingGroup.getCustomer())
			.set(INVOICING_GROUP.CUSTOMER_GROUPED, invoicingGroup.getCustomerGrouped())
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
	
	public static InvoicingGroup save(AONContext ctx, InvoicingGroup invoicingGroup) {
		return invoicingGroup.getId() != null 
				? update(ctx, invoicingGroup) 
				: insert(ctx, invoicingGroup) ;
	}

	static class InvoicingGroupFiller implements Function<Record, InvoicingGroup> {

		@Override
		public InvoicingGroup apply(Record r) {
			return buildInvoicingGroup(r);			
		}
		
		static InvoicingGroup buildInvoicingGroup(Record r) {
			return new InvoicingGroup()
					.setId(r.getValue(INVOICING_GROUP.ID))
					.setDomain(r.getValue(INVOICING_GROUP.DOMAIN))
					.setCustomer(r.getValue(INVOICING_GROUP.CUSTOMER))
					.setCustomerGrouped(r.getValue(INVOICING_GROUP.CUSTOMER_GROUPED))
					.setDescription(r.getValue(INVOICING_GROUP.DESCRIPTION))
					.setCreationDate(r.getValue(INVOICING_GROUP.CREATION_DATE))
					.setCreationUser(r.getValue(INVOICING_GROUP.CREATION_USER))
					.setModificationDate(r.getValue(INVOICING_GROUP.MODIFICATION_DATE))
					.setModificationUser(r.getValue(INVOICING_GROUP.MODIFICATION_USER));		
		}
	}
	
}
