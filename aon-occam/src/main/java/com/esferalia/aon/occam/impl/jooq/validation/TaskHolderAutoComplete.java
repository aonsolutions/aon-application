package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskHolderType;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class TaskHolderAutoComplete {
	
	public static BiConsumer<AONContext, TaskHolder> COMPLETE_TYPE = (ctx, taskHolder) -> {
		if (taskHolder.getType() == null) {
			ctx.log().info("\t saving task holder: autocomplete type: " + TaskHolderType.INTERNAL);
			taskHolder.setType(TaskHolderType.INTERNAL);
		}
	};

	public static BiConsumer<AONContext, TaskHolder> COMPLETE_ATIVE = (ctx, taskHolder) -> {
		if (taskHolder.isActive() == null) {
			ctx.log().info("\t saving task holder: autocomplete active: 1");
			taskHolder.setActive(true);
		}
	};
	
	public static BiConsumer<AONContext, TaskHolder> COMPLETE_REGISTRY = (ctx, taskHolder) -> {
		if (taskHolder.getId() == null && taskHolder.getUserId() != null) {
			User user = SecurityDAO.getUser(ctx, taskHolder.getUserId());
			if(user.getRegistry() != null) {
				taskHolder.setId(user.getRegistry());
			} else if(!AonStringUtils.isBlank(taskHolder.getDocument())) {
				Registry r = RegistryDAO.getStream(ctx, f -> 
					f.getDomainProperty().eq(taskHolder.getDomain().getId())
					.and(f.getDocumentProperty().eq(taskHolder.getDocument())))
					.findFirst().orElse(new Registry());
				if(!r.isEmpty()) taskHolder.copy(r);
			}
			
			if(taskHolder.getId() == null && AonStringUtils.isBlank(taskHolder.getDocument()) && user.getAuth() != null) {
				Auth a = AON_SOLUTIONS.getAuth(user.getAuth().getAuth());
				taskHolder.setDocument(a.getDocument())
						.setName(a.getName()+ " "+ a.getSurname())
						.setAlias(a.getName());
			}
		}
	};
	
	public static void autoComplete(AONContext ctx, TaskHolder taskHolder) throws AonCoreException {
		COMPLETE_TYPE
		.andThen(COMPLETE_ATIVE)
		.andThen(COMPLETE_REGISTRY)
			.accept(ctx, taskHolder);
	}

}
