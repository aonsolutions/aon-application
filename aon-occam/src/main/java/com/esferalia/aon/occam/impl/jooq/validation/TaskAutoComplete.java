package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;

import java.util.Optional;
import java.util.function.BiConsumer;

import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.ProjectRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.TaskHolderRecord;
import com.esferalia.aon.jooq.tables.records.WorkgroupRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.watson.error.AonCoreException;

public class TaskAutoComplete {
	
	private static com.esferalia.aon.jooq.tables.Domain PARENT_DOMAIN =  DOMAIN.as("parent_domain");
	
	private TaskAutoComplete() {
		throw new IllegalStateException("Utility class");
	}
	
	public static final BiConsumer<AONContext, Task> COMPLETE_REGISTRY = (ctx, task) -> {
	    if ( task.getRegistry() == null ) {
		task.setRegistry( new Registry() );
	    }
	    Registry registry = task.getRegistry();
	    if (registry.getId() == null ) {
		ctx.log().info("\t saving task: autocomplete registry: ");
		registry = new Registry();
		task.setRegistry(registry);
	    }
	};

	public static final BiConsumer<AONContext, Task> COMPLETE_PROJECT = (ctx, task) -> {
	    if ( task.getProject() == null ) {
		task.setProject( new Project() );
	    }
	    Project project = task.getProject();
	    if (project.getId() == null ) {
		ctx.log().info("\t saving task: autocomplete project: ");
		getProject(ctx, 
		project.getName(), 
		ctx.getDomainName())
		.ifPresent(projectRecord -> project.setId(projectRecord.getId()));
	    }
	};

	public static final BiConsumer<AONContext, Task> COMPLETE_SENDER = (ctx, task) -> {
	    if ( task.getSender() == null ) {
		task.setSender( new TaskHolder() );
	    }
	    TaskHolder sender = task.getSender();
	    if (sender.getId() == null) {
		ctx.log().info("\t saving task: autocomplete sender: ");
		getTaskHolder(ctx, 
		ctx.getUser(), 
		ctx.getDomainName())
		.ifPresent(taskHolderRecord -> sender.setRegistry(taskHolderRecord.getRegistry()));
	    }
	};

	public static final BiConsumer<AONContext, Task> COMPLETE_TASKHOLDER = (ctx, task) -> {
	    if ( task.getTaskHolder() == null ) {
		task.setTaskHolder( new TaskHolder() );
	    }
	    TaskHolder taskHolder = task.getTaskHolder();
	    if (taskHolder.getId() == null ) {
		ctx.log().info("\t saving task: autocomplete task holder: ");
//		getTaskHolder(ctx, 
//		taskHolder., 
//		ctx.getDomainName())
//		.ifPresent(taskHolderRecord -> taskHolder.setRegistry(taskHolderRecord.getRegistry()));
	    }
	};

	public static final BiConsumer<AONContext, Task> COMPLETE_WORKGROUP = (ctx, task) -> {
	    if ( task.getWorkgroup() == null ) {
		task.setWorkgroup( new Workgroup() );
	    }
	    Workgroup workgroup = task.getWorkgroup();
	    
	    if (workgroup.getId() == null ) {
		ctx.log().info("\t saving task: autocomplete workgroup: ");
		getWorkgroup(ctx, workgroup.getDescription(), ctx.getDomainName())
		.ifPresent(workgroupRecord -> workgroup.setId(workgroupRecord.getId()) );
	    }
	};

	public static final BiConsumer<AONContext, Task> COMPLETE_DOMAIN = (ctx, task) -> {
	    if ( task.getDomain() == null ) {
		task.setDomain( new Domain() );
	    }
	    Domain domain = task.getDomain();
	    if (domain.getId() == null) {
		ctx.log().info("\t saving task: autocomplete domain: ");
		getDomain(ctx, ctx.getDomainName())
		.ifPresent(domainRecord -> domain.setId(domainRecord.getId()));
	    }
	};
	
	public static final BiConsumer<AONContext, Task> COMPLETE_DUE_DATE = (ctx, task) -> {
		if (task.getDueDate() == null) {
			ctx.log().info("\t saving task: autocomplete due_date: ");
			task.setDueDate(task.getStartDate());
		}
	};

	public static void autoComplete(AONContext ctx, Task task) throws AonCoreException {
		COMPLETE_DUE_DATE
		.andThen(COMPLETE_DOMAIN)
		.andThen(COMPLETE_SENDER)
		.andThen(COMPLETE_PROJECT)
		.andThen(COMPLETE_REGISTRY)
		.andThen(COMPLETE_WORKGROUP)
		.accept(ctx, task);
	}

	private static Optional<DomainRecord> getDomain(AONContext aonContext, String name) {
		return
		aonContext
		.getDslContext()
		.select()
		.from(DOMAIN)
		.where(DOMAIN.NAME.eq(name))
		.fetchOptionalInto(DOMAIN)
		;
	}

	private static Optional<ProjectRecord> getProject(AONContext aonContext, String projectName, String domainName) {
		return
		aonContext
		.getDslContext()
		.select()
		.from(DOMAIN)
		.innerJoin(PROJECT)
		.on(PROJECT.DOMAIN.eq(DOMAIN.ID))
		.where(DOMAIN.NAME.eq(domainName))
		.and(PROJECT.NAME.eq(projectName).or(PROJECT.ALIAS.eq(projectName)))
		.fetchOptionalInto(PROJECT)
		.or(() -> 
		aonContext
		.getDslContext()
		.select()
		.from(DOMAIN)
		.innerJoin(PARENT_DOMAIN)
		.on(DOMAIN.PARENT.eq(PARENT_DOMAIN.ID))
		.innerJoin(PROJECT)
		.on(DOMAIN.ID.eq(PROJECT.DOMAIN))
		.where(DOMAIN.NAME.eq(domainName))
		.and(PROJECT.NAME.eq(projectName).or(PROJECT.ALIAS.eq(projectName)))
		.fetchOptionalInto(PROJECT)
		)
		;
	}

	private static Optional<WorkgroupRecord> getWorkgroup(AONContext aonContext, String workGroupDescription, String domainName ) {
		return
		aonContext
		.getDslContext()
		.select()
		.from(DOMAIN)
		.innerJoin(WORKGROUP)
		.on(WORKGROUP.DOMAIN.eq(DOMAIN.ID))
		.where(DOMAIN.NAME.eq(domainName))
		.and(WORKGROUP.DESCRIPTION.eq(workGroupDescription))
		.fetchOptionalInto(WORKGROUP)
		// try at parent domain
		.or( () ->
		aonContext
		.getDslContext()
		.select()
		.from(DOMAIN)
		.innerJoin(DOMAIN.as(PARENT_DOMAIN))
		.on(DOMAIN.PARENT.eq(PARENT_DOMAIN.ID))
		.innerJoin(WORKGROUP)
		.on(WORKGROUP.DOMAIN.eq(PARENT_DOMAIN.ID))
		.where(DOMAIN.NAME.eq(domainName))
		.and(WORKGROUP.DESCRIPTION.eq(workGroupDescription))
		.fetchOptionalInto(WORKGROUP)
		)
		// try at insert new one 
		.or( () -> 
		aonContext
		.getDslContext()
		.insertInto(WORKGROUP)
		.columns(
		WORKGROUP.DOMAIN
		, WORKGROUP.STATUS
		, WORKGROUP.DESCRIPTION)
		.select(
		DSL.select(
		DOMAIN.ID
		, DSL.val((byte)0)
		, DSL.val(workGroupDescription))
		.from(DOMAIN)
		.where(DOMAIN.NAME.eq(domainName)))
		.returning()
		.fetchOptional()
		)
		;
	}
	
	private static Optional<TaskHolderRecord> getTaskHolder(AONContext aonContext, String userLogin, String domainName) {
		return
		aonContext
		.getDslContext()
		.select()
		.from(DOMAIN)
		.innerJoin(USER)
		.on(USER.DOMAIN.eq(DOMAIN.ID))
		.innerJoin(TASK_HOLDER)
		.on(USER.ID.eq(TASK_HOLDER.USER_ID))
		.where(USER.LOGIN.eq(userLogin))
		.and(DOMAIN.NAME.eq(domainName))
		.fetchOptionalInto(TASK_HOLDER)
		// try at parent domain
		.or( () ->
        		aonContext
        		.getDslContext()
        		.select()
        		.from(DOMAIN)
        		.innerJoin(DOMAIN.as(PARENT_DOMAIN))
        		.on(DOMAIN.PARENT.eq(PARENT_DOMAIN.ID))
        		.innerJoin(USER)
        		.on(USER.DOMAIN.eq(PARENT_DOMAIN.ID))
        		.innerJoin(TASK_HOLDER)
        		.on(USER.ID.eq(TASK_HOLDER.USER_ID))
        		.where(USER.LOGIN.eq(userLogin))
        		.and(DOMAIN.NAME.eq(domainName))
        		.fetchOptionalInto(TASK_HOLDER)
		)
		// try at insert new one
		.or( () -> getRegistry(aonContext, userLogin, domainName)
		    	.map( r -> aonContext
		    	    .getDslContext()
			    .insertInto(TASK_HOLDER)
			    .columns(
			    TASK_HOLDER.DOMAIN
			    , TASK_HOLDER.USER_ID
			    , TASK_HOLDER.REGISTRY
			    , TASK_HOLDER.ACTIVE
			    , TASK_HOLDER.TYPE)
			    .select( 
			    DSL.select(
			    USER.DOMAIN
			    , USER.ID
			    , USER.REGISTRY
			    , DSL.val((byte)1)  // active
			    , DSL.val((byte)0)) // internal
			    .from(DOMAIN)
			    .innerJoin(USER)
			    .on(USER.DOMAIN.eq(DOMAIN.ID))
			    .where(USER.LOGIN.eq(userLogin))
			    .and(DOMAIN.NAME.eq(domainName))
			    )
			    .returning()
			    .fetchOneInto(TASK_HOLDER)
			    )
		);
		
	}

	
	private static Optional<RegistryRecord> getRegistry(AONContext aonContext, String userLogin, String domainName) {
		return
		aonContext
		.getDslContext()
		.select()
		.from(DOMAIN)
		.innerJoin(USER)
		.on(USER.DOMAIN.eq(DOMAIN.ID))
		.innerJoin(REGISTRY)
		.on(USER.REGISTRY.eq(REGISTRY.ID))
		.where(USER.LOGIN.eq(userLogin))
		.and(DOMAIN.NAME.eq(domainName))
		.fetchOptionalInto(REGISTRY)
		// try at parent domain
		.or( () ->
        		aonContext
        		.getDslContext()
        		.select()
        		.from(DOMAIN)
        		.innerJoin(DOMAIN.as(PARENT_DOMAIN))
        		.on(DOMAIN.PARENT.eq(PARENT_DOMAIN.ID))
        		.innerJoin(USER)
        		.on(USER.DOMAIN.eq(PARENT_DOMAIN.ID))
        		.innerJoin(REGISTRY)
        		.on(USER.REGISTRY.eq(REGISTRY.ID))
        		.where(USER.LOGIN.eq(userLogin))
        		.and(DOMAIN.NAME.eq(domainName))
        		.fetchOptionalInto(REGISTRY)
		)
		// try at insert new one
		.or( () ->  {
        		RegistryRecord registryRecord =
        		aonContext
        		.getDslContext()
        		.insertInto(REGISTRY)
        		.columns(
        		REGISTRY.DOMAIN
        		, REGISTRY.NAME)
        		.select( 
        		DSL.select(
        		USER.DOMAIN
        		, USER.NAME) 
        		.from(DOMAIN)
        		.innerJoin(USER)
        		.on(USER.DOMAIN.eq(DOMAIN.ID))
        		.where(USER.LOGIN.eq(userLogin))
        		.and(DOMAIN.NAME.eq(domainName))
        		)
        		.returning()
        		.fetchOptional()
        		.orElseGet(() -> 
        		aonContext
        		.getDslContext()
        		.insertInto(REGISTRY)
        		.columns(
        		REGISTRY.DOMAIN
        		, REGISTRY.NAME)
        		.select( 
        		DSL.select(
        		USER.DOMAIN
        		, USER.NAME) 
        		.from(DOMAIN)
        		.innerJoin(DOMAIN.as(PARENT_DOMAIN))
        		.on(DOMAIN.PARENT.eq(PARENT_DOMAIN.ID))
        		.innerJoin(USER)
        		.on(USER.DOMAIN.eq(PARENT_DOMAIN.ID))
        		.where(USER.LOGIN.eq(userLogin))
        		.and(DOMAIN.NAME.eq(domainName))
        		)
        		.returning()
        		.fetchOne()
        		);
        		
        		aonContext
        		.getDslContext()
        		.update(USER.innerJoin(DOMAIN).on(USER.DOMAIN.eq(DOMAIN.ID)))
        		.set(USER.REGISTRY, registryRecord.getId())
        		.where(USER.LOGIN.eq(userLogin))
        		.and(DOMAIN.NAME.eq(domainName))
        		.execute();
        		
        		return Optional.of(registryRecord);
		}
		)
		;
	}
	
	
}
