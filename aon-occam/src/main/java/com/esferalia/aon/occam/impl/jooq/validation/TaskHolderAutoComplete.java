package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskHolderType;
import com.esferalia.aon.watson.error.AonCoreException;

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
	
	public static void autoComplete(AONContext ctx, TaskHolder taskHolder) throws AonCoreException {
		COMPLETE_TYPE
		.andThen(COMPLETE_ATIVE)
			.accept(ctx, taskHolder);
	}

}
