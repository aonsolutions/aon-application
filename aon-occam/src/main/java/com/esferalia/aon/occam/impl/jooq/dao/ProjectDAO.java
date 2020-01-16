package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.ProjectType.PROJECT_TYPE;
import static com.esferalia.aon.jooq.tables.ProjectCommercial.PROJECT_COMMERCIAL;
import static com.esferalia.aon.jooq.tables.ProjectReservation.PROJECT_RESERVATION;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.jooq.tables.records.ProjectRecord;
import com.esferalia.aon.jooq.tables.records.ProjectReservationRecord;
import com.esferalia.aon.jooq.tables.records.ProjectTypeRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.ProjectCommercialFilter;
import com.esferalia.aon.occam.api.model.Filter.ProjectReservationFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.ProjectFilter;
import com.esferalia.aon.occam.api.model.Properties.ProjectCommercialProperties;
import com.esferalia.aon.occam.api.model.Properties.ProjectProperties;
import com.esferalia.aon.occam.api.model.Properties.ProjectReservationProperties;
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.project.ProjectReservation;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.watson.server.AonDateUtils;

public class ProjectDAO {
	private static final ProjectPropertiesDAO PROJECT_PROPERTIES = new ProjectPropertiesDAO();
	private static final ProjectReservationPropertiesDAO PROJECT_RESERVATION_PROPERTIES = new ProjectReservationPropertiesDAO();
	private static final ProjectCommercialPropertiesDAO PROJECT_COMMERCIAL_PROPERTIES = new ProjectCommercialPropertiesDAO();
	
	protected static class ProjectPropertiesDAO implements ProjectProperties {
		protected Condition[] getConditions(ProjectFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT.ID);} 
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT.ACTIVE);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT.ALIAS);}
		@Override public Property<Byte> getCommercialProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT.COMMERCIAL);}
		@Override public Property<Date> getDateProperty() {return new FilterDAO.PropertyDAO<Date>(PROJECT.DATE);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT.NAME);}
		@Override public Property<Integer> getProjectTypeProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT.PROJECT_TYPE);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT.REGISTRY);}
		@Override public Property<Byte> getReservationProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT.RESERVATION);}
		@Override public Property<Byte> getTasProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT.TAS);}
	}
	
	protected static class ProjectCommercialPropertiesDAO implements ProjectCommercialProperties {
		protected Condition[] getConditions(ProjectCommercialFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT_COMMERCIAL.PROJECT);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT_COMMERCIAL.DOMAIN);}
		@Override public Property<Integer> getTargetProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT_COMMERCIAL.TARGET);}
		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT_COMMERCIAL.SELLER);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_COMMERCIAL.COMMENTS);}
		@Override public Property<Byte> getSourceProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT_COMMERCIAL.SOURCE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT_COMMERCIAL.STATUS);}
		@Override public Property<Date> getStatusDateProperty() {return new FilterDAO.PropertyDAO<Date>(PROJECT_COMMERCIAL.STATUS_DATE);}
	}
	
	protected static class ProjectReservationPropertiesDAO implements ProjectReservationProperties {
		protected Condition[] getConditions(ProjectReservationFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT_RESERVATION.PROJECT);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT_RESERVATION.DOMAIN);}
		@Override public Property<Integer> getHotelProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT_RESERVATION.HOTEL);}
		@Override public Property<Integer> getHotelReservationProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT_RESERVATION.HOTEL_RESERVATION);}
		@Override public Property<String> getCodeProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.CODE);}
		@Override public Property<Date> getStartDateProperty() {return new FilterDAO.PropertyDAO<Date>(PROJECT_RESERVATION.START_DATE);}
		@Override public Property<Timestamp> getStartTimeProperty() {return new FilterDAO.PropertyDAO<Timestamp>(PROJECT_RESERVATION.START_TIME);}
		@Override public Property<Date> getEndDateProperty() {return new FilterDAO.PropertyDAO<Date>(PROJECT_RESERVATION.END_DATE);}
		@Override public Property<Timestamp> getEndTimeProperty() {return new FilterDAO.PropertyDAO<Timestamp>(PROJECT_RESERVATION.END_TIME);}
		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT_RESERVATION.SELLER);}
		@Override public Property<Integer> getAgencyProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT_RESERVATION.AGENCY);}
		@Override public Property<Double> getAgencyCommissionPercentProperty() {return new FilterDAO.PropertyDAO<Double>(PROJECT_RESERVATION.AGENCY_COMMISSION_PERCENT);}
		@Override public Property<Double> getAgencyCommissionAmountProperty() {return new FilterDAO.PropertyDAO<Double>(PROJECT_RESERVATION.AGENCY_COMMISSION_AMOUNT);}
		@Override public Property<Byte> getAgencyRebateProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT_RESERVATION.AGENCY_REBATE);}
		@Override public Property<Integer> getCompanyProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT_RESERVATION.COMPANY);}
		@Override public Property<Double> getDiscountPercentProperty() {return new FilterDAO.PropertyDAO<Double>(PROJECT_RESERVATION.DISCOUNT_PERCENT);}
		@Override public Property<Double> getDiscountAmountProperty() {return new FilterDAO.PropertyDAO<Double>(PROJECT_RESERVATION.DISCOUNT_AMOUNT);}
		@Override public Property<Byte> getBookingHolderProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT_RESERVATION.BOOKING_HOLDER);}
		@Override public Property<Double> getTaxableBaseProperty() {return new FilterDAO.PropertyDAO<Double>(PROJECT_RESERVATION.TAXABLE_BASE);}
		@Override public Property<Double> getVatQuotaProperty() {return new FilterDAO.PropertyDAO<Double>(PROJECT_RESERVATION.VAT_QUOTA);}
		@Override public Property<Double> getOtherTaxQuotaProperty() {return new FilterDAO.PropertyDAO<Double>(PROJECT_RESERVATION.OTHER_TAX_QUOTA);}
		@Override public Property<Double> getTotalProperty() {return new FilterDAO.PropertyDAO<Double>(PROJECT_RESERVATION.TOTAL);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.COMMENTS);}
		@Override public Property<String> getRemarksProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.REMARKS);}
		@Override public Property<Byte> getSourceProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT_RESERVATION.SOURCE);}
		@Override public Property<String> getCrsCodeProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.CRS_CODE);}
		@Override public Property<Double> getAdvanceProperty() {return new FilterDAO.PropertyDAO<Double>(PROJECT_RESERVATION.ADVANCE);}
		@Override public Property<Byte> getAdvanceInvoicedProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT_RESERVATION.ADVANCE_INVOICED);}
		@Override public Property<Byte> getEarlyCheckOutProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT_RESERVATION.EARLY_CHECK_OUT);}
		@Override public Property<Byte> getPrepayProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT_RESERVATION.PREPAY);}
		@Override public Property<String> getBankTransactionProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.BANK_TRANSACTION);}
		@Override public Property<String> getCreditCardNumberProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.CREDIT_CARD_NUMBER);}
		@Override public Property<String> getCreditCardExpirationMonthProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.CREDIT_CARD_EXPIRATION_MONTH);}
		@Override public Property<String> getCreditCardExpirationYearProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.CREDIT_CARD_EXPIRATION_YEAR);}
		@Override public Property<String> getPenaltyValueProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.PENALTY_VALUE);}
		@Override public Property<Byte> getTouristTaxFreeProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT_RESERVATION.TOURIST_TAX_FREE);}
		@Override public Property<Byte> getCheckStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT_RESERVATION.CHECK_STATUS);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(PROJECT_RESERVATION.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(PROJECT_RESERVATION.MODIFICATION_DATE);}
		@Override public Property<String> getCancellationUserProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.CANCELLATION_USER);}
		@Override public Property<Timestamp> getCancellationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(PROJECT_RESERVATION.CANCELLATION_DATE);}
		@Override public Property<String> getTokenProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.TOKEN);}
		@Override public Property<Double> getPenaltyAmountProperty() {return new FilterDAO.PropertyDAO<Double>(PROJECT_RESERVATION.PENALTY_AMOUNT);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT_RESERVATION.STATUS);}
		@Override public Property<Timestamp> getPenaltyDateProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.PENALTY_DATE);}
		@Override public Property<String> getCreditCardTypeProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.CREDIT_CARD_TYPE);}
	}

	public static Stream<Project> getProjectStream(AONContext ctx, ProjectFilter filter){
		return ctx.getDslContext()
				.select().from(PROJECT).where(PROJECT_PROPERTIES.getConditions(filter))
				.fetchInto(PROJECT).stream().map(new FullProjectFiller());
	}

	public static ProjectType getProjectType(AONContext ctx, String description){
		return ctx.getDslContext()
				.select().from(PROJECT_TYPE).where(PROJECT_TYPE.DESCRIPTION.eq(description))
				.fetchInto(PROJECT_TYPE).stream().map(new ProjectTypeFiller()).findFirst().orElse(null);
	}
	
	public static ProjectReservation getProjectReservation(AONContext ctx, ProjectReservationFilter filter){	
		return ctx.getDslContext()
				.select().from(PROJECT_RESERVATION).where(PROJECT_RESERVATION_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(PROJECT_RESERVATION).stream().map(new FullProjectReservationFiller())
				.findFirst().orElse(null);
	}
	
	public static Stream<ProjectReservation> getProjectReservationStream(AONContext ctx, ProjectReservationFilter filter){	
		return ctx.getDslContext().select().from(PROJECT_RESERVATION).where(PROJECT_RESERVATION_PROPERTIES.getConditions(filter))
				.fetchInto(PROJECT_RESERVATION).stream().map(new FullProjectReservationFiller());
	}

	public static Stream<ProjectCommercial> getProjectCommercialStream(AONContext ctx, ProjectCommercialFilter filter){	
		return ctx.getDslContext().select()
				.from(PROJECT).join(PROJECT_COMMERCIAL).on(PROJECT.ID.eq(PROJECT_COMMERCIAL.PROJECT))
				.where(PROJECT_COMMERCIAL_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new FullProjectCommercialFiller());
	}
	
	public static Integer insertProject(AONContext ctx, Project project){

		return ctx.getDslContext().insertInto(PROJECT, PROJECT.ACTIVE, PROJECT.ALIAS,
					PROJECT.COMMERCIAL, PROJECT.DATE, PROJECT.DOMAIN, PROJECT.NAME, PROJECT.PROJECT_TYPE,
					PROJECT.REGISTRY, PROJECT.RESERVATION, PROJECT.TAS)
				.values(project.isActive()?(byte)1:(byte)0, project.getAlias(), project.isCommercial()?(byte)1:(byte)0,
						new Date(project.getDate().getTime()), project.getDomain(), project.getName(), project.getProjectTypeId(),
						project.getRegistryId(), project.isReservation()?(byte)1:(byte)0, project.isTas()?(byte)1:(byte)0)
				.returning(PROJECT.ID).fetchOne().getId();
	}
	
	public static Integer insertProjectCommercial(AONContext ctx, ProjectCommercial pc){
		ctx.getDslContext().insertInto(PROJECT_COMMERCIAL, PROJECT_COMMERCIAL.PROJECT, PROJECT_COMMERCIAL.DOMAIN, PROJECT_COMMERCIAL.TARGET, PROJECT_COMMERCIAL.SELLER,
				PROJECT_COMMERCIAL.SOURCE, PROJECT_COMMERCIAL.COMMENTS, PROJECT_COMMERCIAL.STATUS, PROJECT_COMMERCIAL.STATUS_DATE)
				.values(pc.getId(), ctx.getDomainId(), pc.getTarget(), pc.getSeller(), pc.getSource(), pc.getComments(), pc.getStatus(), AonDateUtils.toSql(pc.getStatusDate())).execute();
		
		return pc.getId();
	}
	
	private static class FullProjectReservationFiller implements Function<ProjectReservationRecord, ProjectReservation> {
		
		@Override
		public ProjectReservation apply(ProjectReservationRecord r) {
			return new ProjectReservation().setAdvance(r.getAdvance())
					.setAdvanceInvoiced(r.getAdvanceInvoiced())
					.setAgency(r.getAgency())
					.setAgencyCommissionAmount(r.getAgencyCommissionAmount())
					.setAgencyCommissionPercent(r.getAgencyCommissionPercent())
					.setAgencyRebate(r.getAgencyRebate())
					.setBankTransaction(r.getBankTransaction())
					.setBookingHolder(r.getBookingHolder())
					.setCancellationDate(r.getCancellationDate())
					.setCancellationUser(r.getCancellationUser())
					.setCheckStatus(r.getCheckStatus())
					.setCode(r.getCode())
					.setComments(r.getComments())
					.setCompany(r.getCompany())
					.setCreationDate(r.getCreationDate())
					.setCreationUser(r.getCreationUser())
					.setCreditCardExpirationMonth(r.getCreditCardExpirationMonth())
					.setCreditCardExpirationYear(r.getCreditCardExpirationYear())
					.setCreditCardNumber(r.getCreditCardNumber())
					.setCrsCode(r.getCrsCode())
					.setDiscountAmount(r.getDiscountAmount())
					.setDiscountPercent(r.getDiscountPercent())		
					.setDomain(new Domain().setId(r.getDomain()))
					.setEarlyCheckOut(r.getEarlyCheckOut())
					.setEndDate(r.getEndDate())
					.setEndTime(r.getEndTime())
					.setHotel(r.getHotel())
					.setHotelReservation(r.getHotelReservation())
					.setModificationDate(r.getModificationDate())
					.setModificationUser(r.getModificationUser())
					.setOtherTaxQuota(r.getOtherTaxQuota())
					.setPenaltyValue(r.getPenaltyValue())
					.setPrepay(r.getPrepay())
					.setProject(r.getProject())
					.setRemarks(r.getRemarks())
					.setSeller(r.getSeller())
					.setSource(r.getSource())
					.setStartDate(r.getStartDate())
					.setStartTime(r.getStartTime())
					.setStatus(r.getStatus())
					.setTaxableBase(r.getTaxableBase())
					.setTotal(r.getTotal())
					.setVatQuota(r.getVatQuota())
					.setToken(r.getToken())
					.setPenaltyAmount(r.getPenaltyAmount())
					.setPenaltyDate(r.getPenaltyDate());
		}

	}
	
	private static class ProjectTypeFiller implements Function<ProjectTypeRecord, ProjectType> {
		
		@Override
		public ProjectType apply(ProjectTypeRecord r) {
			return new ProjectType()
					.setActive(r.getActive().equals(0))
					.setDomain(r.getDomain())
					.setId(r.getId())
					.setDescription(r.getDescription());
		}

	}
	
	private static class FullProjectFiller implements Function<ProjectRecord, Project> {
		
		@Override
		public Project apply(ProjectRecord r) {
			return new Project()
					.setActive(r.getActive().equals(0))
					.setAlias(r.getAlias())
					.setCommercial(r.getCommercial().equals(0))
					.setDate(r.getDate())
					.setDomain(r.getDomain())
					.setId(r.getId())
					.setName(r.getName())
					.setProjectTypeId(r.getProjectType())
					.setReservation(r.getReservation().equals(0))
					.setTas(r.getTas().equals(0));
		}

	}
	
	private static class FullProjectCommercialFiller implements Function<Record, ProjectCommercial> {
		
		@Override
		public ProjectCommercial apply(Record r) {
			ProjectCommercial pc = new ProjectCommercial();
			pc.setActive(r.getValue(PROJECT.ACTIVE).equals(0));
			pc.setAlias(r.getValue(PROJECT.ALIAS));
			pc.setCommercial(r.getValue(PROJECT.COMMERCIAL).equals(0));
			pc.setDate(r.getValue(PROJECT.DATE));
			pc.setDomain(r.getValue(PROJECT.DOMAIN));
			pc.setId(r.getValue(PROJECT.ID));
			pc.setName(r.getValue(PROJECT.NAME));
			pc.setProjectTypeId(r.getValue(PROJECT.PROJECT_TYPE));
			pc.setReservation(r.getValue(PROJECT.RESERVATION).equals(0));
			pc.setTas(r.getValue(PROJECT.TAS).equals(0));
			return pc.setTarget(r.getValue(PROJECT_COMMERCIAL.TARGET))
					.setSeller(r.getValue(PROJECT_COMMERCIAL.SELLER))
					.setComments(r.getValue(PROJECT_COMMERCIAL.COMMENTS))
					.setSource(r.getValue(PROJECT_COMMERCIAL.SOURCE))
					.setStatus(r.getValue(PROJECT_COMMERCIAL.STATUS))
					.setStatusDate(r.getValue(PROJECT_COMMERCIAL.STATUS_DATE));	
		}

	}
}
