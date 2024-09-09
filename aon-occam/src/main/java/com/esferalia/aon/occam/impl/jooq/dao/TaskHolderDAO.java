package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.TaskHolderWorkgroup.TASK_HOLDER_WORKGROUP;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.TaskHolderFilter;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Properties.TaskHolderProperties;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.security.TaskHolderWorkgroup;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskHolderType;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;
import com.esferalia.aon.occam.impl.jooq.validation.TaskHolderAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.TaskHolderValidation;

public class TaskHolderDAO {
	
	private TaskHolderDAO() {
	
	}
    public static final com.esferalia.aon.jooq.tables.Registry TASK_HOLDER_ALIAS = REGISTRY.as("registry_task_holder");
	private static final TaskHolderPropertiesDAO TASK_HOLDER_PROPERTIES = new TaskHolderPropertiesDAO();
	public static class TaskHolderPropertiesDAO implements TaskHolderProperties {
		
		
		protected Condition[] getConditions(TaskHolderFilter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER.DOMAIN);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER.TYPE);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER.ACTIVE);}
		@Override public Property<Integer> getUserIdProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER.USER_ID);}
		@Override public Property<Integer> getCostProfileProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER.COST_PROFILE);}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER.REGISTRY);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER_ALIAS.DOCUMENT);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER_ALIAS.DOCUMENT_TYPE);}
		@Override public Property<String> getDocumentCountryProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER_ALIAS.DOCUMENT_COUNTRY);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER_ALIAS.NAME);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER_ALIAS.ALIAS);}
		@Override public Property<String> getNationalityProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER_ALIAS.NATIONALITY);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER_ALIAS.SECURITY_LEVEL);}
	}

	
	public static class TaskHolderFiller extends Filler implements Function<Record, TaskHolder> {

		public TaskHolder apply(Record r) {
			return build(r, TASK_HOLDER_ALIAS);
		}
		
		/**
		 * Por defecto el valor del Field de REGISTRY es TASK_HOLDER_ALIAS
		 * @param r
		 * @return
		 */
		public static TaskHolder build(Record r) {
			return build(r, TASK_HOLDER_ALIAS);
		}
		
		/**
		 * 
		 * @param r
		 * @param registry si el valor es null por defecto TASK_HOLDER_ALIAS
		 * @return
		 */
		public static TaskHolder build(Record r, com.esferalia.aon.jooq.tables.Registry registry) {
			if(registry == null) registry = TASK_HOLDER_ALIAS;
			return new TaskHolder()
					.copy(RegistryFiller.build(r, registry))
					.setRegistry(r.getValue(TASK_HOLDER.REGISTRY))
					.setActive(getBoolean(r, TASK_HOLDER.ACTIVE))
					.setCostProfile(r.getValue(TASK_HOLDER.COST_PROFILE))
					.setType(TaskHolderType.safeValueOf(r.getValue(TASK_HOLDER.TYPE)))
					.setUserId(r.getValue(TASK_HOLDER.USER_ID));
		}
		
		/**
		 * 
		 * @param r
		 * @param th si el valor es null por defecto TASK_HOLDER
		 * @param registry si el valor es null por defecto TASK_HOLDER_ALIAS
		 * @return
		 */
		public static TaskHolder build(Record r, com.esferalia.aon.jooq.tables.TaskHolder th, com.esferalia.aon.jooq.tables.Registry registry) {
			if(th == null) th = TASK_HOLDER;
			if(registry == null) registry = TASK_HOLDER_ALIAS;
			return new TaskHolder()
					.copy(RegistryFiller.build(r, registry))
					.setRegistry(r.getValue(th.REGISTRY))
					.setActive(getBoolean(r, th.ACTIVE))
					.setCostProfile(r.getValue(th.COST_PROFILE))
					.setType(TaskHolderType.safeValueOf(r.getValue(th.TYPE)))
					.setUserId(r.getValue(th.USER_ID));
		}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, TaskHolderFilter filter) {
		return ctx.getDslContext().select()
				.from(TASK_HOLDER)
				.join(TASK_HOLDER_ALIAS).on(TASK_HOLDER_ALIAS.ID.eq(TASK_HOLDER.REGISTRY))
				.join(DOMAIN).on(TASK_HOLDER_ALIAS.DOMAIN.eq(DOMAIN.ID)) // TODO REVISAR DONDE SE UTILIZA, BORRAR SI NO ES NECESARIO!!!!
				.where(TASK_HOLDER_PROPERTIES.getConditions(filter));
		
	}
	
	public static Stream<TaskHolder> getStream(AONContext ctx, TaskHolderFilter filter, Options... options){
		Options option = new Options();
		if(options.length > 0) option = options[0];
		if(option.isFull() && option.isPagination()) return getFullStream(ctx, filter, option.getPage(), option.getPerPage());
		else if(option.isFull()) return getFullStream(ctx, filter);
		else if(option.isPagination()) return getStream(ctx, filter, option.getPage(), option.getPerPage());
		else return select(ctx,filter)
				.orderBy(TASK_HOLDER_ALIAS.NAME)
				.fetch()
				.stream()
				.map(new TaskHolderFiller());

	}
	
	public static Stream<TaskHolder> getStream(AONContext ctx, TaskHolderFilter filter, int page, int perPage){
		return select(ctx,filter)
			.orderBy(TASK_HOLDER_ALIAS.NAME)
			.limit(perPage)
			.offset(perPage * (page -1))
			.fetch()
			.stream()
			.map(new TaskHolderFiller());
	}

	public static Stream<TaskHolder> getFullStream(AONContext ctx, TaskHolderFilter filter){
		return getStream(ctx, filter)
			.map(th -> {
				List<Workgroup> workgroups = WorkgroupDAO.getWorkgroupByTaskHolderStream(ctx, f -> f.getDomainProperty().eq(th.getDomain().getId()), th.getId()).toList();
				return th.setWorkgroups(workgroups);
			});
	}
	
	public static Stream<TaskHolder> getFullStream(AONContext ctx, TaskHolderFilter filter, int page, int perPage){
		return getStream(ctx, filter, page, perPage)
			.map(th -> {
				List<Workgroup> workgroups = WorkgroupDAO.getWorkgroupByTaskHolderStream(ctx, f -> f.getDomainProperty().eq(th.getDomain().getId()), th.getId()).toList();
				return th.setWorkgroups(workgroups);
			});
	}

	public static List<TaskHolder> getList(AONContext ctx, TaskHolderFilter filter, Options...options){
		return getStream(ctx, filter, options)
			.collect(Collectors.toCollection(LinkedList::new));
	}
	


	public static TaskHolder get(AONContext ctx, Integer id){
		return get(ctx, p -> p.getIdProperty().eq(id));
	}
	
	public static TaskHolder get(AONContext ctx, TaskHolderFilter filter, Options...options){
		return select(ctx,filter)
			.limit(1)
			.fetch()
			.stream()
			.map(new TaskHolderFiller())
			.findFirst()
			.orElse(new TaskHolder());
	}
	
	public static TaskHolder save(AONContext ctx, TaskHolder taskHolder) {
		ctx.checkWrite();
		TaskHolderAutoComplete.autoComplete(ctx, taskHolder);
		TaskHolderValidation.validate(ctx, taskHolder);
		
		boolean nullId = (taskHolder.getId() == null); 
		taskHolder = RegistryDAO.save(ctx, taskHolder);
		return nullId || get(ctx, taskHolder.getId()).isEmpty() 
			? insert(ctx, taskHolder) : update(ctx, taskHolder);
	}

	private static TaskHolder insert(AONContext ctx, TaskHolder taskHolder){
		ctx.getDslContext().insertInto(TASK_HOLDER)
			.set(TASK_HOLDER.REGISTRY, taskHolder.getId())
			.set(TASK_HOLDER.DOMAIN, taskHolder.getDomain().getId())
			.set(TASK_HOLDER.TYPE, taskHolder.getType().value())
			.set(TASK_HOLDER.USER_ID, taskHolder.getUserId())
			.set(TASK_HOLDER.COST_PROFILE, taskHolder.getCostProfile())
			.set(TASK_HOLDER.ACTIVE, taskHolder.getActiveValue())
			.execute();
		ctx.log().debug("INSERT TASK HOLDER id: {0}",taskHolder.getId());		
		return taskHolder;
	}
	
	private static TaskHolder update(AONContext ctx, TaskHolder taskHolder){
		ctx.checkWrite();
		int count = ctx.getDslContext().update(TASK_HOLDER)
			.set(TASK_HOLDER.DOMAIN, taskHolder.getDomain().getId())
			.set(TASK_HOLDER.TYPE, taskHolder.getType().value())
			.set(TASK_HOLDER.USER_ID, taskHolder.getUserId())
			.set(TASK_HOLDER.COST_PROFILE, taskHolder.getCostProfile())
			.set(TASK_HOLDER.ACTIVE, taskHolder.getActiveValue())
			.where(TASK_HOLDER.REGISTRY.eq(taskHolder.getId()))
			.execute();
		ctx.log().debug("UPDATE TASK HOLDER id: {0}. ({1} rows)",taskHolder.getId(),count);		
		return taskHolder;
	}

	public static TaskHolder delete(AONContext ctx, TaskHolder taskHolder) {
		delete(ctx, taskHolder.getId());
		return taskHolder;
	}
	
	public static void delete(AONContext ctx, Integer taskHolderId) {
		ctx.checkWrite();

		// TODO CONTROLAR FOREIGN KEYS
		// course_instructor, daily_tracking, mk_action, mk_campaign, project_holder
		// project_tas, seller, task, task_holder_workgroup, task_workflow, timecontrol
		
		int count = ctx.getDslContext().delete(TASK_HOLDER)
			.where(TASK_HOLDER.REGISTRY.eq(taskHolderId))
			.execute();
		ctx.log().debug("DELETE TASK HOLDER id: {0} ({1} rows)", taskHolderId, count);
	}

	// *************************************************
	// ********** FUNCIONES A REVISAR ******************
	// *************************************************
	
	@Deprecated
	public static Stream<TaskHolder> getTaskHolderWorkgroup(AONContext ctx, TaskHolderFilter filter, Integer workgroupId, int ofs, int limit){
		SelectConditionStep<Record> r = ctx.getDslContext().select()
				.from(TASK_HOLDER)
				.join(TASK_HOLDER_ALIAS).on(TASK_HOLDER_ALIAS.ID.eq(TASK_HOLDER.REGISTRY))
				.leftOuterJoin(TASK_HOLDER_WORKGROUP).on(TASK_HOLDER.REGISTRY.eq(TASK_HOLDER_WORKGROUP.TASK_HOLDER))
				.where(TASK_HOLDER_PROPERTIES.getConditions(filter));
		if(workgroupId != null && workgroupId>0)
			r.and(TASK_HOLDER_WORKGROUP.WORKGROUP.eq(workgroupId));
	
		return r.orderBy(TASK_HOLDER_ALIAS.NAME).offset(ofs)
				.limit(limit).fetch().stream().map(new TaskHolderFiller());
		// return r.orderBy(TASK_HOLDER_ALIAS.NAME).fetch().stream().map(new TaskHolderFiller());

	}

	@Deprecated
	public static List<TaskHolder> getAviableSellerTaskHolders(AONContext ctx) {
		List<Integer> sellerTaskHolders = ctx.getDslContext().selectDistinct(SELLER.TASK_HOLDER)
			.from(SELLER)
			.where(SELLER.DOMAIN.eq(ctx.getDomainId()))
			.fetch(SELLER.TASK_HOLDER);
		
		Stream<TaskHolder> taskHolders = getStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		
		return taskHolders.filter(taskHolder -> !sellerTaskHolders.contains(taskHolder.getId())).collect(Collectors.toList());
	}
	
	
	public static void saveTaskHolderWorkgroups(AONContext ctx, TaskHolder taskHolder){
		taskHolder.getWorkgroups().stream()
		.forEach(workgroup -> {
			if(workgroup.isRemoved()) {
				TaskHolderWorkgroupDAO.delete(ctx, f -> f.getTaskHolderProperty().eq(taskHolder.getId()).and(f.getWorkgroupProperty().eq(workgroup.getId())));
			} else {
				TaskHolderWorkgroupDAO.save(ctx, 
						new TaskHolderWorkgroup()
						.setDomain(workgroup.getDomain())
						.setTaskHolder(taskHolder.getId())
						.setWorkgroup(workgroup));

			}
		});
	}
	
	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	
	public static TaskHolder getRandom(AONContext ctx, TaskHolderFilter filter) {
		return select(ctx, filter)
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new TaskHolderFiller())
			.findFirst()
			.orElse(null);
	}
	
}

