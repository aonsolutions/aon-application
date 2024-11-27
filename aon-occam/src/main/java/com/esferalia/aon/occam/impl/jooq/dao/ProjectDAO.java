package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.ProjectCommercial.PROJECT_COMMERCIAL;
import static com.esferalia.aon.jooq.tables.ProjectReservation.PROJECT_RESERVATION;
import static com.esferalia.aon.jooq.tables.ProjectType.PROJECT_TYPE;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.jooq.tables.records.ProjectReservationRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
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
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectTypeDAO.ProjectTypeFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;
import com.esferalia.aon.occam.impl.jooq.validation.ProjectAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.ProjectValidation;
import com.esferalia.aon.watson.server.AonDateUtils;

public class ProjectDAO {
	
	private ProjectDAO() {

	}
	
	private static final ProjectPropertiesDAO PROJECT_PROPERTIES = new ProjectPropertiesDAO();
	private static final ProjectReservationPropertiesDAO PROJECT_RESERVATION_PROPERTIES = new ProjectReservationPropertiesDAO();
	private static final ProjectCommercialPropertiesDAO PROJECT_COMMERCIAL_PROPERTIES = new ProjectCommercialPropertiesDAO();
	
	protected static class ProjectPropertiesDAO implements ProjectProperties {
		protected Condition[] getConditions(ProjectFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.ID);} 
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.ACTIVE);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.ALIAS);}
		@Override public Property<Byte> getCommercialProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.COMMERCIAL);}
		@Override public Property<Date> getDateProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.DATE);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.NAME);}
		@Override public Property<Integer> getProjectTypeProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.PROJECT_TYPE);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.REGISTRY);}
		@Override public Property<Byte> getReservationProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.RESERVATION);}
		@Override public Property<Byte> getTasProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.TAS);}
		
		
		@Override public Property<String> getRegistryNameProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NAME);}
		@Override public Property<String> getTypeDescriptionProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_TYPE.DESCRIPTION);}
	}
	
	protected static class ProjectCommercialPropertiesDAO implements ProjectCommercialProperties {
		protected Condition[] getConditions(ProjectCommercialFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_COMMERCIAL.PROJECT);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_COMMERCIAL.DOMAIN);}
		@Override public Property<Integer> getTargetProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_COMMERCIAL.TARGET);}
		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_COMMERCIAL.SELLER);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_COMMERCIAL.COMMENTS);}
		@Override public Property<Byte> getSourceProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_COMMERCIAL.SOURCE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_COMMERCIAL.STATUS);}
		@Override public Property<Date> getStatusDateProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_COMMERCIAL.STATUS_DATE);}
		@Override public Property<Integer> getProbabilityProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_COMMERCIAL.PROBABILITY);}
		@Override public Property<Date> getDateProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.DATE);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.NAME);}
	}
	
	protected static class ProjectReservationPropertiesDAO implements ProjectReservationProperties {
		protected Condition[] getConditions(ProjectReservationFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.PROJECT);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.DOMAIN);}
		@Override public Property<Integer> getHotelProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.HOTEL);}
		@Override public Property<Integer> getHotelReservationProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.HOTEL_RESERVATION);}
		@Override public Property<String> getCodeProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.CODE);}
		@Override public Property<Date> getStartDateProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.START_DATE);}
		@Override public Property<Timestamp> getStartTimeProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.START_TIME);}
		@Override public Property<Date> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.END_DATE);}
		@Override public Property<Timestamp> getEndTimeProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.END_TIME);}
		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.SELLER);}
		@Override public Property<Integer> getAgencyProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.AGENCY);}
		@Override public Property<Double> getAgencyCommissionPercentProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.AGENCY_COMMISSION_PERCENT);}
		@Override public Property<Double> getAgencyCommissionAmountProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.AGENCY_COMMISSION_AMOUNT);}
		@Override public Property<Byte> getAgencyRebateProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.AGENCY_REBATE);}
		@Override public Property<Integer> getCompanyProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.COMPANY);}
		@Override public Property<Double> getDiscountPercentProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.DISCOUNT_PERCENT);}
		@Override public Property<Double> getDiscountAmountProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.DISCOUNT_AMOUNT);}
		@Override public Property<Byte> getBookingHolderProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.BOOKING_HOLDER);}
		@Override public Property<Double> getTaxableBaseProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.TAXABLE_BASE);}
		@Override public Property<Double> getVatQuotaProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.VAT_QUOTA);}
		@Override public Property<Double> getOtherTaxQuotaProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.OTHER_TAX_QUOTA);}
		@Override public Property<Double> getTotalProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.TOTAL);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.COMMENTS);}
		@Override public Property<String> getRemarksProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.REMARKS);}
		@Override public Property<Byte> getSourceProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.SOURCE);}
		@Override public Property<String> getCrsCodeProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.CRS_CODE);}
		@Override public Property<Double> getAdvanceProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.ADVANCE);}
		@Override public Property<Byte> getAdvanceInvoicedProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.ADVANCE_INVOICED);}
		@Override public Property<Byte> getEarlyCheckOutProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.EARLY_CHECK_OUT);}
		@Override public Property<Byte> getPrepayProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.PREPAY);}
		@Override public Property<String> getBankTransactionProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.BANK_TRANSACTION);}
		@Override public Property<String> getCreditCardNumberProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.CREDIT_CARD_NUMBER);}
		@Override public Property<String> getCreditCardExpirationMonthProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.CREDIT_CARD_EXPIRATION_MONTH);}
		@Override public Property<String> getCreditCardExpirationYearProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.CREDIT_CARD_EXPIRATION_YEAR);}
		@Override public Property<String> getPenaltyValueProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.PENALTY_VALUE);}
		@Override public Property<Byte> getTouristTaxFreeProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.TOURIST_TAX_FREE);}
		@Override public Property<Byte> getCheckStatusProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.CHECK_STATUS);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.MODIFICATION_DATE);}
		@Override public Property<String> getCancellationUserProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.CANCELLATION_USER);}
		@Override public Property<Timestamp> getCancellationDateProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.CANCELLATION_DATE);}
		@Override public Property<String> getTokenProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.TOKEN);}
		@Override public Property<Double> getPenaltyAmountProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.PENALTY_AMOUNT);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.STATUS);}
		@Override public Property<Timestamp> getPenaltyDateProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.PENALTY_DATE);}
		@Override public Property<String> getCreditCardTypeProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_RESERVATION.CREDIT_CARD_TYPE);}
	}

	private static SelectConditionStep<Record> select(AONContext ctx, ProjectFilter filter) {
		return ctx.getDslContext().select()
			.from(PROJECT)
			.join(DOMAIN).on(PROJECT.DOMAIN.eq(DOMAIN.ID))
			.join(REGISTRY).on(PROJECT.REGISTRY.eq(REGISTRY.ID))
			.leftOuterJoin(PROJECT_TYPE).on(PROJECT.PROJECT_TYPE.eq(PROJECT_TYPE.ID))			
			.where(PROJECT_PROPERTIES.getConditions(filter));
	}
	
	public static Project get(AONContext ctx, ProjectFilter filter){
		return select(ctx, filter).limit(1).fetch().stream().map(new ProjectFiller())
				.findFirst().orElse(new Project());
	}
	
	public static Stream<Project> getStream(AONContext ctx, ProjectFilter filter){
		return select(ctx, filter).fetch().stream().map(new ProjectFiller());
	}
	
	public static Stream<Project> getStream(AONContext ctx, ProjectFilter filter, Integer page, Integer perPage){
		return select(ctx, filter)
			.limit(perPage).offset(perPage * (page -1))
			.fetch().stream().map(new ProjectFiller());
	}
	
	/**
	 * @deprecated  Replaced by getStream(AONContext ctx, ProjectFilter filter) 
	 * 	or getStream(AONContext ctx, ProjectFilter filter, Integer page, Integer perPage) 
	 */
	@Deprecated(forRemoval = true )
	public static Stream<Project> getProjectStream(AONContext ctx, ProjectFilter filter){
		return ctx.getDslContext()
				.select().from(PROJECT).where(PROJECT_PROPERTIES.getConditions(filter))
				.fetchInto(PROJECT).stream().map(new ProjectFiller());
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
				.leftOuterJoin(PROJECT_TYPE).on(PROJECT.PROJECT_TYPE.eq(PROJECT_TYPE.ID))
				.where(PROJECT_COMMERCIAL_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new FullProjectCommercialFiller());
	}

	public static Project save(AONContext ctx, Project project){
		if(project.isDirty()) {
			ProjectAutoComplete.autoComplete(ctx, project);
			ProjectValidation.validate(ctx, project);
			
			project = project.getId() != null
				? update(ctx, project)
				: insert(ctx, project);
		}
		
		setProjectHolders(ctx, project);
		
		setProjectActivities(ctx, project);
		
		return project.setDirty(false);
	}
	
	
	private static void setProjectHolders(AONContext ctx, Project project) {
		if(!project.getProjectHolders().isEmpty()) {
			project.getProjectHolders()
			.forEach(holder->{				
				holder.setProject(project.getId());
				ProjectHolderDAO.save(ctx, holder);
			});
	    } else if(!project.getProjectHolder().isEmpty()) {
			project.getProjectHolder().setProject(project.getId());
			project.setProjectHolder(ProjectHolderDAO.save(ctx, project.getProjectHolder()));
		}
	}
	
	private static void setProjectActivities(AONContext ctx, Project project) {
		if(!project.getProjectActivities().isEmpty()) {
			project.getProjectActivities()
			.forEach(activity->{				
				activity.setProject(project.getId());
				if(!activity.isRemoved()) {
					ProjectActivityDAO.save(ctx, activity);
				} else if(activity.getId()!=null) {
					ProjectActivityDAO.delete(ctx, activity.getId());	
				}
			});
	    }
	}
	
	public static Project update(AONContext ctx, Project project){
		ctx.getDslContext().update(PROJECT)
				.set(PROJECT.ACTIVE, project.isActive() ? (byte) 1: (byte)0)
				.set(PROJECT.ALIAS, project.getAlias())
				.set(PROJECT.COMMERCIAL, project.isCommercial() ? (byte) 1: (byte)0)
				.set(PROJECT.DATE, new Date(project.getDate().getTime()))
				.set(PROJECT.DOMAIN, project.getDomain().getId())
				.set(PROJECT.NAME, project.getName())
				.set(PROJECT.PROJECT_TYPE, project.getType().getId())
				.set(PROJECT.REGISTRY, project.getRegistry().getId())
				.set(PROJECT.RESERVATION, project.isReservation() ? (byte) 1: (byte) 0)
				.set(PROJECT.TAS, project.isTas() ? (byte) 1 :  (byte) 0)
				.where(PROJECT.ID.eq(project.getId())).execute();
		return project;
	}
	
	public static Project insert(AONContext ctx, Project project){
		Integer id = ctx.getDslContext().insertInto(PROJECT)
		.set(PROJECT.ACTIVE, project.isActive() ? (byte) 1: (byte)0)
		.set(PROJECT.ALIAS, project.getAlias())
		.set(PROJECT.COMMERCIAL, project.isCommercial() ? (byte) 1: (byte)0)
		.set(PROJECT.DATE, new Date(project.getDate().getTime()))
		.set(PROJECT.DOMAIN, project.getDomain().getId())
		.set(PROJECT.NAME, project.getName())
		.set(PROJECT.PROJECT_TYPE, project.getType().getId())
		.set(PROJECT.REGISTRY, project.getRegistry().getId())
		.set(PROJECT.RESERVATION, project.isReservation() ? (byte) 1: (byte) 0)
		.set(PROJECT.TAS, project.isTas() ? (byte) 1 :  (byte) 0)
		.returning(PROJECT.ID).fetchOne().getValue(PROJECT.ID);
		
		return project.setId(id);
	}

	
	public static void delete(AONContext ctx, Integer id) {
		ProjectHolderDAO.delete(ctx, f -> f.getProjectProperty().eq(id));
		ProjectActivityDAO.delete(ctx, f -> f.getProjectProperty().eq(id));
		deleteProjectCommercial(ctx, id);
		delete(ctx, f -> f.getIdProperty().eq(id));
	}

	private static void delete(AONContext ctx, ProjectFilter filter) {
		ctx.getDslContext().delete(PROJECT)
		.where(PROJECT_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	/**
	 * @deprecated  Replaced by save(AONContext ctx, Project project) or insert(AONContext ctx, Project project) 
	 */
	@Deprecated(forRemoval = true )
	public static Integer insertProject(AONContext ctx, Project project){
		return ctx.getDslContext().insertInto(PROJECT, PROJECT.ACTIVE, PROJECT.ALIAS,
					PROJECT.COMMERCIAL, PROJECT.DATE, PROJECT.DOMAIN, PROJECT.NAME, PROJECT.PROJECT_TYPE,
					PROJECT.REGISTRY, PROJECT.RESERVATION, PROJECT.TAS)
				.values(project.isActive()?(byte)1:(byte)0, project.getAlias(), project.isCommercial()?(byte)1:(byte)0,
						new Date(project.getDate().getTime()), project.getDomain().getId(), project.getName(), project.getProjectTypeId(),
						project.getRegistry().getId(), project.isReservation()?(byte)1:(byte)0, project.isTas()?(byte)1:(byte)0)
				.returning(PROJECT.ID).fetchOne().getId();
	}
	
	public static ProjectCommercial saveProjectCommercial(CloseableAONContext ctx, ProjectCommercial projectCommercial) {
		Optional<ProjectCommercial> existingProjectCommercial = getProjectCommercialStream(ctx, 
				f -> f.getNameProperty().eq(projectCommercial.getName())
					.and(f.getDateProperty().eq(AonDateUtils.toSql(projectCommercial.getDate())))
					.and(f.getSellerProperty().eq(projectCommercial.getSeller()))
					.and(f.getTargetProperty().eq(projectCommercial.getTarget()))).findFirst();
		
		if(!existingProjectCommercial.isPresent()) {
			
			if(projectCommercial.getId() == null) {
				save(ctx, projectCommercial);
				insertProjectCommercial(ctx, projectCommercial);
			} else {
				updateProjectCommercial(ctx, projectCommercial);
			}
			
		} else {
			updateProjectCommercial(ctx, projectCommercial);
		}
		
		return projectCommercial;
	}
	
	public static Integer insertProjectCommercial(AONContext ctx, ProjectCommercial pc){
		ctx.getDslContext().insertInto(PROJECT_COMMERCIAL, PROJECT_COMMERCIAL.PROJECT, PROJECT_COMMERCIAL.DOMAIN, PROJECT_COMMERCIAL.TARGET, PROJECT_COMMERCIAL.SELLER,
				PROJECT_COMMERCIAL.SOURCE, PROJECT_COMMERCIAL.COMMENTS, PROJECT_COMMERCIAL.STATUS, PROJECT_COMMERCIAL.STATUS_DATE)
				.values(pc.getId(), ctx.getDomainId(), pc.getTarget(), pc.getSeller(), pc.getSource(), pc.getComments(), pc.getStatus(), AonDateUtils.toSql(pc.getStatusDate())).execute();
		
		return pc.getId();
	}
	
	public static Integer updateProjectCommercial(AONContext ctx, ProjectCommercial pc){
		ctx.getDslContext().update(PROJECT_COMMERCIAL)
			.set(PROJECT_COMMERCIAL.SOURCE, pc.getSource())
			.set(PROJECT_COMMERCIAL.COMMENTS, pc.getComments())
			.set(PROJECT_COMMERCIAL.STATUS, pc.getStatus())
			.set(PROJECT_COMMERCIAL.STATUS_DATE, AonDateUtils.toSql(pc.getStatusDate()))
			.execute();
		
		return pc.getId();
	}
	
	private static void deleteProjectCommercial(AONContext ctx, Integer id) {
		ctx.getDslContext().delete(PROJECT_COMMERCIAL).where(PROJECT_COMMERCIAL.PROJECT.eq(id)).execute();
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
	
	public static class ProjectFiller extends Filler implements Function<Record, Project> {
		
		@Override
		public Project apply(Record r) {
			return build(r);
		}
		
		public static Project build(Record r) {
			return new Project()
				.setId(r.getValue(PROJECT.ID))
				.setDomain(checkField(r, DOMAIN.ID)
					? DomainFiller.build(r)
					: new Domain().setId(r.getValue(PROJECT.DOMAIN)))
				.setName(r.getValue(PROJECT.NAME))
				.setRegistry(checkField(r, REGISTRY.ID)
					? RegistryFiller.build(r, REGISTRY)
					: new Registry().setId(r.getValue(PROJECT.REGISTRY)))
				.setAlias(r.getValue(PROJECT.ALIAS))
				.setDate(r.getValue(PROJECT.DATE))
				.setType(checkField(r, PROJECT_TYPE.ID)
					? ProjectTypeFiller.build(r)
					: new ProjectType().setId(r.getValue(PROJECT.PROJECT_TYPE)))
				.setActive(getBoolean(r, PROJECT.ACTIVE))
				.setCommercial(getBoolean(r, PROJECT.COMMERCIAL))
				.setReservation(getBoolean(r, PROJECT.RESERVATION))
				.setTas(getBoolean(r, PROJECT.TAS))
				.setDirty(false);
		}

	}
	
	private static class FullProjectCommercialFiller extends Filler implements Function<Record, ProjectCommercial> {
		
		@Override
		public ProjectCommercial apply(Record r) {
			return  new ProjectCommercial()
				.copy(ProjectFiller.build(r))
				.setTarget(r.getValue(PROJECT_COMMERCIAL.TARGET))
				.setSeller(r.getValue(PROJECT_COMMERCIAL.SELLER))
				.setComments(r.getValue(PROJECT_COMMERCIAL.COMMENTS))
				.setSource(r.getValue(PROJECT_COMMERCIAL.SOURCE))
				.setStatus(r.getValue(PROJECT_COMMERCIAL.STATUS))
				.setStatusDate(r.getValue(PROJECT_COMMERCIAL.STATUS_DATE));	
		}

	}
	
	private static class FixProjectCommercial  {
		Integer project;
		String document;
		
		private FixProjectCommercial() {}

		public Integer getProject() {
			return project;
		}

		public FixProjectCommercial setProject(Integer project) {
			this.project = project;
			return this;
		}

		public String getDocument() {
			return document;
		}

		public FixProjectCommercial setDocument(String document) {
			this.document = document;
			return this;
		}
	}
	
	private static class FixProjectCommercialFiller implements Function<Record, FixProjectCommercial> {
		
		@Override
		public FixProjectCommercial apply(Record r) {
			return new FixProjectCommercial()
					.setProject(r.getValue(PROJECT_COMMERCIAL.PROJECT))
					.setDocument(r.getValue(REGISTRY.DOCUMENT));	
		}
	}
	
	public static void fixProjectCommercial(AONContext ctx) {
		ctx.getDslContext().select(PROJECT_COMMERCIAL.PROJECT, REGISTRY.DOCUMENT)
		.from(PROJECT_COMMERCIAL).join(REGISTRY).on(PROJECT_COMMERCIAL.TARGET.eq(REGISTRY.ID))
		.where(PROJECT_COMMERCIAL.DOMAIN.eq(ctx.getDomainId()))
		.and(PROJECT_COMMERCIAL.DOMAIN.ne(REGISTRY.DOMAIN))
		.fetch().stream().map(new FixProjectCommercialFiller())
		.forEach(r -> {
			Target target = TargetDAO.get(ctx,  f-> f.getDomainProperty().eq(ctx.getDomainId())
					.and(f.getDocumentProperty().eq(r.getDocument())));
			
			if(target.isEmpty()) {
				ctx.getDslContext().update(PROJECT).set(PROJECT.REGISTRY, target.getId()).where(PROJECT.ID.eq(r.getProject())).execute();
				ctx.getDslContext().update(PROJECT_COMMERCIAL).set(PROJECT_COMMERCIAL.TARGET, target.getId()).where(PROJECT_COMMERCIAL.PROJECT.eq(r.getProject())).execute();
			}
		});
	}
	
}
