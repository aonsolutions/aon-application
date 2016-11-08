package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.ProjectReservation.PROJECT_RESERVATION;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;

import com.esferalia.aon.jooq.tables.records.ProjectRecord;
import com.esferalia.aon.jooq.tables.records.ProjectReservationRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.ProjectReservationFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.ProjectFilter;
import com.esferalia.aon.occam.api.model.Properties.ProjectProperties;
import com.esferalia.aon.occam.api.model.Properties.ProjectReservationProperties;
import com.esferalia.aon.occam.api.model.project.ProjectReservation;
import com.esferalia.aon.occam.api.model.registry.Project;

public class ProjectDAO {
	private static final ProjectPropertiesDAO PROJECT_PROPERTIES = new ProjectPropertiesDAO();
	private static final ProjectReservationPropertiesDAO PROJECT_RESERVATION_PROPERTIES = new ProjectReservationPropertiesDAO();

	
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
		@Override public Property<String> getCreditCardHolderProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.CREDIT_CARD_HOLDER);}
		@Override public Property<String> getCreditCardNumberProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.CREDIT_CARD_NUMBER);}
		@Override public Property<String> getCreditCardExpirationMonthProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.CREDIT_CARD_EXPIRATION_MONTH);}
		@Override public Property<String> getCreditCardExpirationYearProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.CREDIT_CARD_EXPIRATION_YEAR);}
		@Override public Property<String> getCreditCardCvvProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.CREDIT_CARD_CVV);}
		@Override public Property<Integer> getCreditPenaltyDaysProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT_RESERVATION.PENALTY_DAYS);}
		@Override public Property<Byte> getTouristTaxFreeProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT_RESERVATION.TOURIST_TAX_FREE);}
		@Override public Property<Byte> getCheckStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT_RESERVATION.CHECK_STATUS);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(PROJECT_RESERVATION.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(PROJECT_RESERVATION.MODIFICATION_DATE);}
		@Override public Property<String> getCancellationUserProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.CANCELLATION_USER);}
		@Override public Property<Timestamp> getCancellationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(PROJECT_RESERVATION.CANCELLATION_DATE);}
		@Override public Property<String> getTokenProperty() {return null;}// TODO new FilterDAO.PropertyDAO<String>(PROJECT_RESERVATION.TOKEN);}
		@Override public Property<Double> getPenaltyProperty() {return null;}// TODO new FilterDAO.PropertyDAO<Double>(PROJECT_RESERVATION.PENALTY);}
	}
	
	public static Project getProject(AONContext ctx, ProjectFilter filter){
		return ctx.getDslContext()
				.select().from(PROJECT).where(PROJECT_PROPERTIES.getConditions(filter)).limit(1)
				.fetchInto(PROJECT).stream().map(new FullProjectFiller()).findFirst().orElse(null);
	}
	
	public static LinkedList<Project> getProjectList(AONContext ctx, ProjectFilter filter){
		return ctx.getDslContext()
				.select().from(PROJECT).where(PROJECT_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(PROJECT).stream().map(new FullProjectFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static ProjectReservation getProjectReservation(AONContext ctx, Integer projectId){	
		return ctx.getDslContext()
				.select().from(PROJECT_RESERVATION).where(PROJECT_RESERVATION.PROJECT.eq(projectId))
				.limit(1).fetchInto(PROJECT_RESERVATION).stream().map(new FullProjectReservationFiller())
				.findFirst().orElse(null);
	}
	
	public static Stream<ProjectReservation> getProjectReservationStream(AONContext ctx, ProjectReservationFilter filter){	
		return ctx.getDslContext().select().from(PROJECT_RESERVATION).where(PROJECT_RESERVATION_PROPERTIES.getConditions(filter))
				.fetchInto(PROJECT_RESERVATION).stream().map(new FullProjectReservationFiller());
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
					.setCreditCardCvv(r.getCreditCardCvv())
					.setCreditCardExpirationMonth(r.getCreditCardExpirationMonth())
					.setCreditCardExpirationYear(r.getCreditCardExpirationYear())
					.setCreditCardHolder(r.getCreditCardHolder())
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
					.setPenaltyDays(r.getPenaltyDays())
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
					.setVatQuota(r.getVatQuota());
		// TODO		.setToken(r.getToken())
		// TODO		.setPenalty(r.getPenalty());
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
}
