package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.watson.error.AonCoreException;

public class TaskAutoComplete {
	
	
	private TaskAutoComplete() {
		throw new IllegalStateException("Utility class");
	}
	
	public static final BiConsumer<AONContext, Task> COMPLETE_DUE_DATE = (ctx, task) -> {
		if (task.getDueDate() == null) {
			ctx.log().info("\t saving task: autocomplete due_date: ");
			task.setDueDate(task.getStartDate());
		}
	};
	
	public static void autoComplete(AONContext ctx, Task task) throws AonCoreException {
		COMPLETE_DUE_DATE
			.accept(ctx, task);
	}


}
