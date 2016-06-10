package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Series.SERIES;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.SeriesFilter;
import com.esferalia.aon.occam.api.model.Properties.SeriesProperties;
import com.esferalia.aon.occam.api.model.warehouse.Series;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class SeriesDAO {
	
	private static final SeriesPropertiesDAO SERIES_PROPERTIES = new SeriesPropertiesDAO();

	protected static class SeriesPropertiesDAO implements SeriesProperties {
		protected Condition[] getConditions(SeriesFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(SERIES.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(SERIES.DOMAIN);}	
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<Integer>(SERIES.SCOPE);}	
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(SERIES.DESCRIPTION);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<Byte>(SERIES.ACTIVE);}
		@Override public Property<String> getCodeProperty() {return new FilterDAO.PropertyDAO<String>(SERIES.CODE);}
		@Override public Property<Byte> getTasProperty() {return new FilterDAO.PropertyDAO<Byte>(SERIES.TAS);}
		@Override public Property<Byte> getOfferProperty() {return new FilterDAO.PropertyDAO<Byte>(SERIES.OFFER);}
		@Override public Property<Byte> getSalesProperty() {return new FilterDAO.PropertyDAO<Byte>(SERIES.SALES);}
		@Override public Property<Byte> getDeliveryProperty() {return new FilterDAO.PropertyDAO<Byte>(SERIES.DELIVERY);}
		@Override public Property<Byte> getInvoiceProperty() {return new FilterDAO.PropertyDAO<Byte>(SERIES.INVOICE);}
		@Override public Property<Byte> getRectificationProperty() {return new FilterDAO.PropertyDAO<Byte>(SERIES.RECTIFICATION);}
		@Override public Property<Byte> getPosProperty() {return new FilterDAO.PropertyDAO<Byte>(SERIES.POS);}
		@Override public Property<Byte> getConfidentialProperty() {return new FilterDAO.PropertyDAO<Byte>(SERIES.SECURITY_LEVEL);}
	}
	
	public static Stream<Series> getSeries(AONContext ctx, SeriesFilter filter){
		return ctx.getDslContext().select()
				.from(SERIES)
				.where(SERIES_PROPERTIES.getConditions(filter))
				.fetchInto(SERIES)
				.stream()
				.map(new FullSeriesFiller());	
	}
	
	public static LinkedList<Series> getSeriesDeliveryList(AONContext ctx, Integer scopeId){
		return ctx.getDslContext().select()
			.from(SERIES).join(USER_SCOPE).on(SERIES.SCOPE.eq(USER_SCOPE.SCOPE))
			.join(USER).on(USER_SCOPE.USER_ID.eq(USER.ID))
			.where(SERIES.SCOPE.eq(scopeId))
			.and(USER.LOGIN.eq(ctx.getUser()))
			.and(SERIES.DELIVERY.eq((byte) 1))
			.fetchInto(SERIES).stream().map(new FullSeriesFiller())
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Stream<Series> getInvoiceSeries(AONContext ctx){
		return getSeries(ctx, 
				p -> 
				p.getDomainProperty().in( SecurityDAO.getInheritanceDomainIds(ctx) )
				.and(p.getActiveProperty().eq((byte) 1)  )
				.and(p.getInvoiceProperty().eq((byte) 1)  )
				);	
	}

	//	public static LinkedList<Workplace> getWorkplaceList(AONContext ctx, WorkplaceFilter filter){
//		return ctx.getDslContext().select().from(WORKPLACE)
//				.where(WORKPLACE_PROPERTIES.getConditions(filter))
//				.and(SecurityDAO.getUserScopesCondition(ctx, WORKPLACE.SCOPE))
//				.orderBy(WORKPLACE.DESCRIPTION)
//				.fetchInto(WORKPLACE)
//				.stream()
//				.map(new FullWorkplaceFiller())
//				.collect(Collectors.toCollection(LinkedList::new));	
//	}
	
	private static class FullSeriesFiller implements Function<Record, Series> {
		
		@Override
		public Series apply(Record r) {
			return new Series()
					.setId(r.getValue(SERIES.ID))
					.setDomain(r.getValue(SERIES.DOMAIN))
					.setScope(r.getValue(SERIES.SCOPE))
					.setCode(r.getValue(SERIES.CODE))
					.setDescription(r.getValue(SERIES.DESCRIPTION))
					.setActive(AonEnumUtils.getBoolean( r.getValue(SERIES.ACTIVE)))
					.setTas(AonEnumUtils.getBoolean( r.getValue(SERIES.ACTIVE)))
					.setOffer(AonEnumUtils.getBoolean( r.getValue(SERIES.ACTIVE)))
					.setSales(AonEnumUtils.getBoolean( r.getValue(SERIES.ACTIVE)))
					.setDelivery(AonEnumUtils.getBoolean( r.getValue(SERIES.ACTIVE)))
					.setInvoice(AonEnumUtils.getBoolean( r.getValue(SERIES.ACTIVE)))
					.setRectification(AonEnumUtils.getBoolean( r.getValue(SERIES.ACTIVE)))
					.setPos(AonEnumUtils.getBoolean( r.getValue(SERIES.ACTIVE)))
					.setConfidential(AonEnumUtils.getBoolean( r.getValue(SERIES.ACTIVE)))
					;
		}
	}
	
}
