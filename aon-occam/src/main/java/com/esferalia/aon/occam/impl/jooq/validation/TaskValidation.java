package com.esferalia.aon.occam.impl.jooq.validation;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.watson.error.AonCoreException;

public class TaskValidation {
	
	private TaskValidation() {
		throw new IllegalStateException("Utility class");
	}
	
	public static void validate(AONContext ctx, Task task) throws AonCoreException{
		// TODO 
	}

	public static void validateDeletion(AONContext ctx, Integer id) {
		// TODO Auto-generated method stub
	}
	
	
}
