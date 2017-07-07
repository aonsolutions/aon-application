package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ContextDescriptor implements Serializable {


	private Map<String, VariableDescriptor> variableDescriptors;

	public ContextDescriptor() {
		variableDescriptors = new HashMap<String, VariableDescriptor>();
	}
	
	public Map<String, VariableDescriptor> getVariableDescriptors(){
		return this.variableDescriptors;
	}

	public Set<String> getVariables() {
		return variableDescriptors.keySet();
	}

	public VariableDescriptor get(String var) {
		return variableDescriptors.get(var);
	}

	public <T> void add(String name, String description, Class<?> type, String value) {
		VariableDescriptor variable = new VariableDescriptor();
		variable.setType(type);
		variable.setValue(value);
		variable.setDescription(description);
		variableDescriptors.put(name, variable);
	}

	public void add(String name, String description, Class<?> returnType,
			Class<?> parametersTypes[]) {
		FunctionDescriptor function = new FunctionDescriptor();
		function.setDescription(description);
		function.setType(returnType);
		function.setParametersTypes(parametersTypes);
		variableDescriptors.put(name, function);
	}

	public <T> void add(String name, String description, Class<T> returnType,
			String parameters[]) {
		FunctionDescriptor function = new FunctionDescriptor();
		function.setType(returnType);
		function.setParameters(parameters);
		function.setDescription(description);
		variableDescriptors.put(name, function);
	}

	public static boolean isKnownType(Class<?> type) {
		do {
			for (Class<?> clazz : VariableDescriptor.TYPES) {
				if ( type == clazz )
					return true;
			}
			type = type.getSuperclass();
		} while ( type != null );
		
		return false;
	}

	public void remove(String varName) {
		this.variableDescriptors.remove(varName);
		
	}

	public void add(ContextDescriptor contextDescriptorPayments) {
		this.variableDescriptors.putAll(contextDescriptorPayments.getVariableDescriptors());
	}
}
