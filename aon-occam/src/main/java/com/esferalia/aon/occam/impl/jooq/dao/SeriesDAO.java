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
import com.esferalia.aon.occam.api.model.Series;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SeriesDAO {
	
	private SeriesDAO() {
		
	}
	
	// ****************************************************************
	// ***************************************************** [READ] ***
	// ****************************************************************
	
	public static Stream<Series> stream(AONContext ctx, Integer domainId){
		return ctx.getDslContext().select()
			.from(SERIES)
			.where(SERIES.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
			.fetch()
			.stream()
			.map(new SeriesFiller());	
	}
	
	public static Stream<Series> getStreamSuggestion(AONContext ctx, Integer domainId, String query) {
		return stream(ctx, domainId)
			.filter(s -> AonStringUtils.contains(s.getCode(), query)
					|| AonStringUtils.contains(s.getDescription(), query)
			);
	}

	private static class SeriesFiller extends Filler implements Function<Record, Series> {
		@Override
		public Series apply(Record r) {
			return new Series()
				.setId(getValue(r, SERIES.ID))
				.setDomain(getValue(r, SERIES.DOMAIN))
				.setScope(getValue(r, SERIES.SCOPE))
				.setCode(getValue(r, SERIES.CODE))
				.setDescription(getValue(r, SERIES.DESCRIPTION))
				.setActive(getBoolean( r, SERIES.ACTIVE))
				.setTas(getBoolean( r, SERIES.TAS))
				.setOffer(getBoolean( r, SERIES.OFFER))
				.setSales(getBoolean( r, SERIES.SALES))
				.setDelivery(getBoolean( r, SERIES.DELIVERY))
				.setInvoice(getBoolean( r, SERIES.INVOICE))
				.setRectification(getBoolean(r, SERIES.RECTIFICATION))
				.setPos(getBoolean( r, SERIES.POS))
				.setSecurityLevel(getEnum(r, SERIES.SECURITY_LEVEL, SecurityLevel.class))
			;
		}
	}
	// ****************************************************************
	// **************************************************** [OLD] ***
	// ****************************************************************
	
	@Deprecated
	private static final SeriesPropertiesDAO SERIES_PROPERTIES = new SeriesPropertiesDAO();

	@Deprecated
	private static class SeriesPropertiesDAO implements SeriesProperties {
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
	
	@Deprecated
	public static Stream<Series> getSeries(AONContext ctx, SeriesFilter filter){
		return ctx.getDslContext().select()
				.from(SERIES)
				.where(SERIES_PROPERTIES.getConditions(filter))
				.fetchInto(SERIES)
				.stream()
				.map(new SeriesFiller());	
	}
	
	/**
	 * @deprecated Don't use ctx.getUser()!. Pass user by param.
	 */
	@Deprecated
	public static LinkedList<Series> getSeriesDeliveryList(AONContext ctx, Integer scopeId){
		return ctx.getDslContext().select()
			.from(SERIES).join(USER_SCOPE).on(SERIES.SCOPE.eq(USER_SCOPE.SCOPE))
			.join(USER).on(USER_SCOPE.USER_ID.eq(USER.ID))
			.where(SERIES.SCOPE.eq(scopeId))
			.and(USER.LOGIN.eq(ctx.getUser()))
			.and(SERIES.DELIVERY.eq((byte) 1))
			.fetchInto(SERIES).stream().map(new SeriesFiller())
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	@Deprecated
	public static Stream<Series> getInvoiceSeries(AONContext ctx){
		return getSeries(ctx, 
			p -> 
				p.getDomainProperty().in( SecurityDAO.getInheritanceDomainIds(ctx) )
				.and(p.getActiveProperty().eq((byte) 1)  )
				.and(p.getInvoiceProperty().eq((byte) 1)  )
				);	
	}
	@Deprecated
	public static Stream<Series> getRectificationSeries(AONContext ctx){
		return getSeries(ctx, 
			p -> 
				p.getDomainProperty().in( SecurityDAO.getInheritanceDomainIds(ctx) )
				.and(p.getActiveProperty().eq((byte) 1)  )
				.and(p.getRectificationProperty().eq((byte) 1)  )
				);	
	}
	
}
