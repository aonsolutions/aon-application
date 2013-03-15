package com.esferalia.aon.gwt.payroll.shared;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FunctionDescriptor extends VariableDescriptor {

	private static final Map<Class<?>, String> CLAZZ_PARAMS = new HashMap<Class<?>, String>() {
		{
			put(Number.class, "cantidad");
			put(Date.class, "fecha");
			put(String.class, "texto");
			put(Boolean.class, "valor_l\u00f3gico");
			put(Object.class, "valor");
		}
	};

	String parameters[];
	Integer parametersTypes[];

	@Override
	public String getSyntax() {
		
		StringBuffer buffer = new StringBuffer("( ");
		if (parameters != null) {
			for (int i = 0; i < parameters.length; i++) {
				buffer.append(i > 0 ? ", " : "");
				buffer.append(parameters[i]);
			}
		}
		else if (parametersTypes != null) {
			for (int i = 0; i < parametersTypes.length; i++) {
				buffer.append(i > 0 ? "," : "");
				buffer.append(CLAZZ_PARAMS.get(getClass(parametersTypes[i])));
			}
		}
		buffer.append(" )");
		
		return buffer.toString();
	}

	public String[] getParameters() {
		return parameters;
	}

	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}

	public Class<?>[] getParametersTypes() {
		Class<?> clazzes[] = new Class<?>[parametersTypes.length];
		for (int i = 0; i < parametersTypes.length; i++)
			clazzes[i] = getClass(parametersTypes[i]);
		return clazzes;
	}

	public void setParametersTypes(Class<?>[] parametersClazzes) {
		parametersTypes = new Integer[parametersClazzes.length];
		for (int i = 0; i < parametersClazzes.length; i++)
			parametersTypes[i] = findClazz(parametersClazzes[i]);
	}

}