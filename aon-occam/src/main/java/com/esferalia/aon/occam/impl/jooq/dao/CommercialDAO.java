package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.CommercialActivity.COMMERCIAL_ACTIVITY;
import static com.esferalia.aon.jooq.tables.CommercialTracking.COMMERCIAL_TRACKING;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record13;

import com.esferalia.aon.jooq.tables.records.CommercialActivityRecord;
import com.esferalia.aon.jooq.tables.records.CommercialTrackingRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.CommercialActivity;
import com.esferalia.aon.occam.api.model.CommercialActivityFilter;
import com.esferalia.aon.occam.api.model.CommercialTracking;
import com.esferalia.aon.occam.api.model.CommercialTrackingFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.CommercialActivityProperties;
import com.esferalia.aon.occam.api.model.Properties.CommercialTrackingProperties;
import com.esferalia.aon.occam.api.model.registry.CommissionType;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;

public class CommercialDAO {

	private static final CommercialTrackingPropertiesDAO COMMERCIAL_TRACKING_PROPERTIES = new CommercialTrackingPropertiesDAO();
	private static final CommercialActivityPropertiesDAO COMMERCIAL_ACTIVITY_PROPERTIES = new CommercialActivityPropertiesDAO();
	private static class CommercialTrackingPropertiesDAO implements CommercialTrackingProperties {
		private Condition[] getConditions(CommercialTrackingFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getActivityProperty(){return new FilterDAO.PropertyDAO<Integer>(COMMERCIAL_TRACKING.ACTIVITY);}
		@Override public Property<Byte> getAlldayProperty() {return new FilterDAO.PropertyDAO<Byte>(COMMERCIAL_TRACKING.ALLDAY);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<String>(COMMERCIAL_TRACKING.COMMENTS);}
		@Override public Property<Timestamp> getDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(COMMERCIAL_TRACKING.DATE);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(COMMERCIAL_TRACKING.DOMAIN);}
		@Override public Property<Timestamp> getEndDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(COMMERCIAL_TRACKING.END_DATE);}
		@Override public Property<String> getEventIdProperty() {return new FilterDAO.PropertyDAO<String>(COMMERCIAL_TRACKING.EVENTID);}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(COMMERCIAL_TRACKING.ID);}
		@Override public Property<String> getLocationProperty() {return new FilterDAO.PropertyDAO<String>(COMMERCIAL_TRACKING.LOCATION);}
		@Override public Property<Integer> getNextCommercialTrackingProperty() {return new FilterDAO.PropertyDAO<Integer>(COMMERCIAL_TRACKING.NEXT_COMMERCIAL_TRACKING);}
		@Override public Property<Integer> getOfferProperty() {return new FilterDAO.PropertyDAO<Integer>(COMMERCIAL_TRACKING.OFFER);}
		@Override public Property<Integer> getProjectCommercialProperty() {return new FilterDAO.PropertyDAO<Integer>(COMMERCIAL_TRACKING.PROJECT_COMMERCIAL);}
		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<Integer>(COMMERCIAL_TRACKING.SELLER);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(COMMERCIAL_TRACKING.STATUS);}
	}
	private static class CommercialActivityPropertiesDAO implements CommercialActivityProperties {
		private Condition[] getConditions(CommercialActivityFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(COMMERCIAL_ACTIVITY.DOMAIN);}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(COMMERCIAL_ACTIVITY.ID);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(COMMERCIAL_ACTIVITY.NAME);}
		@Override public Property<Integer> getProbabilityProperty() {return new FilterDAO.PropertyDAO<Integer>(COMMERCIAL_ACTIVITY.PROBABILITY);}
		@Override public Property<Integer> getSurveyProperty() {return new FilterDAO.PropertyDAO<Integer>(COMMERCIAL_ACTIVITY.SURVEY);}	
	}
	
	public static CommercialTracking getCommercialTracking(AONContext ctx, CommercialTrackingFilter filter){
		return ctx.getDslContext()
				.select().from(COMMERCIAL_TRACKING).where(COMMERCIAL_TRACKING_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(COMMERCIAL_TRACKING).stream().map(new FullCommercialTrackingFiller())
				.findFirst().orElse(null);
	}
	
	public static Stream<CommercialTracking> getCommercialTrackingStream(AONContext ctx, CommercialTrackingFilter filter){
		return ctx.getDslContext()
				.select().from(COMMERCIAL_TRACKING).where(COMMERCIAL_TRACKING_PROPERTIES.getConditions(filter))
				.fetchInto(COMMERCIAL_TRACKING).stream().map(new FullCommercialTrackingFiller());
	}
	
	public static void updateEventId(AONContext ctx, Integer ctId, String eventId){
		ctx.getDslContext().update(COMMERCIAL_TRACKING)
		.set(COMMERCIAL_TRACKING.EVENTID, eventId)
		.where(COMMERCIAL_TRACKING.ID.eq(ctId))
		.execute();
	}
	
	
	public static CommercialActivity getCommercialActivity(AONContext ctx, CommercialActivityFilter filter){
		return ctx.getDslContext()
				.select().from(COMMERCIAL_ACTIVITY).where(COMMERCIAL_ACTIVITY_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(COMMERCIAL_ACTIVITY).stream().map(new FullCommercialActivityFiller())
				.findFirst().orElse(null);
	}
	
	public static LinkedList<CommercialActivity> getCommercialActivityList(AONContext ctx, CommercialActivityFilter filter){
		return ctx.getDslContext()
				.select().from(COMMERCIAL_ACTIVITY).where(COMMERCIAL_ACTIVITY_PROPERTIES.getConditions(filter))
				.fetchInto(COMMERCIAL_ACTIVITY).stream().map(new FullCommercialActivityFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	


	
	private static class FullCommercialTrackingFiller implements Function<CommercialTrackingRecord, CommercialTracking> {
		@Override
		public CommercialTracking apply(CommercialTrackingRecord r) {
			return new CommercialTracking()
					.setActivity(r.getActivity())
					.setAllday(r.getAllday() == 1)
					.setComments(r.getComments())
					.setDate(r.getDate())
					.setDomain(r.getDomain())
					.setEndDate(r.getEndDate())
					.setEventId(r.getEventid())
					.setId(r.getId())
					.setLocation(r.getLocation())
					.setNextCommercialTracking(r.getNextCommercialTracking())
					.setOffer(r.getOffer())
					.setProjectCommercial(r.getProjectCommercial())
					.setSeller(r.getSeller())
					.setStatus(r.getStatus());			
		}
	}
	private static class FullCommercialActivityFiller implements Function<CommercialActivityRecord, CommercialActivity> {
		@Override
		public CommercialActivity apply(CommercialActivityRecord r) {
			return new CommercialActivity()
					.setDomain(r.getDomain())
					.setId(r.getId())
					.setName(r.getName())
					.setProbability(r.getProbability())
					.setSurvey(r.getSurvey());			
		}
	}
	
	

}
