package com.esferalia.aon.salary.expression;

import java.util.Collections;
import java.util.Map;

import com.code.aon.AonVersion;

public class HideException extends RemoveException {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Map<String, ITimedVariable<?>> context ;

	public HideException() {
		this(Collections.emptyMap());
	}

	public HideException(String message) {
		this(message, Collections.emptyMap());
	}

	public HideException(Map<String, ITimedVariable<?>> context) {
		super();
		this.context = context;
	}
	
	public HideException(String message, Map<String, ITimedVariable<?>> context) {
		super(message);
		this.context = context;
	}
	
	public Map<String, ITimedVariable<?>> getContext() {
		return context;
	}
	

}
