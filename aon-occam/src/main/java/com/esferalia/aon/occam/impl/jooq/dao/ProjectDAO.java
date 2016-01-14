package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.ProjectReservation.PROJECT_RESERVATION;

import java.sql.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;

import com.esferalia.aon.jooq.tables.records.ProjectRecord;
import com.esferalia.aon.jooq.tables.records.ProjectReservationRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.ProjectFilter;
import com.esferalia.aon.occam.api.model.Properties.ProjectProperties;
import com.esferalia.aon.occam.api.model.project.ProjectReservation;
import com.esferalia.aon.occam.api.model.registry.Project;

public class ProjectDAO {
	private static final ProjectPropertiesDAO PROJECT_PROPERTIES = new ProjectPropertiesDAO();

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
