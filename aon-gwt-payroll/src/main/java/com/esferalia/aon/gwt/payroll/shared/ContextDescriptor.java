package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ContextDescriptor implements Serializable {


	private Map<String, ArrayList<VariableDescriptor>> variableDescriptors;

	public ContextDescriptor() {
		variableDescriptors = new HashMap<String, ArrayList<VariableDescriptor>>();
	}
	
	public Map<String, ArrayList<VariableDescriptor>> getVariableDescriptors(){
		return Collections.unmodifiableMap(this.variableDescriptors);
	}

	public Set<String> getVariables() {
		return variableDescriptors.keySet();
	}

	public VariableDescriptor get(String var) {
		if(null != variableDescriptors.get(var))
			return variableDescriptors.get(var).get(0);
		return null;
	}
	
	public ArrayList<VariableDescriptor> getList(String var) {
		if(null != variableDescriptors.get(var))
			return variableDescriptors.get(var);
		return null;
	}

	public <T> void add(String name, String description, Class<?> type, String value) {
		VariableDescriptor variable = new VariableDescriptor();
		variable.setType(type);
		variable.setValue(value);
		variable.setDescription(description);
		
		if (null == this.variableDescriptors.get(name)){
			ArrayList<VariableDescriptor> variableList = new ArrayList<>();
			variableList.add(variable);
			variableDescriptors.put(name, variableList);
		}else
			variableDescriptors.get(name).add(variable);
		
		
	}

	public void add(String name, String description, Class<?> returnType,
			Class<?> parametersTypes[]) {
		FunctionDescriptor function = new FunctionDescriptor();
		function.setDescription(description);
		function.setType(returnType);
		function.setParametersTypes(parametersTypes);
		
		if (null == this.variableDescriptors.get(name)){
			ArrayList<VariableDescriptor> variableList = new ArrayList<>();
			variableList.add(function);
			variableDescriptors.put(name, variableList);
		}else
			variableDescriptors.get(name).add(function);
	}

	public <T> void add(String name, String description, Class<T> returnType,
			String parameters[]) {
		FunctionDescriptor function = new FunctionDescriptor();
		function.setType(returnType);
		function.setParameters(parameters);
		function.setDescription(description);
		
		if (null == this.variableDescriptors.get(name)){
			ArrayList<VariableDescriptor> variableList = new ArrayList<>();
			variableList.add(function);
			variableDescriptors.put(name, variableList);
		}else
			variableDescriptors.get(name).add(function);
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
	
	public void removeListVar(String varName) {
		this.variableDescriptors.get(varName).clear();
	}

	public void add(String key, ArrayList<VariableDescriptor> variables) {
		this.variableDescriptors.put(key, variables);
	}
	
	public void add(String key, VariableDescriptor variable) {
		if (null == this.variableDescriptors.get(key)){
			ArrayList<VariableDescriptor> variableList = new ArrayList<>();
			variableList.add(variable);
			variableDescriptors.put(key, variableList);
		}else
			variableDescriptors.get(key).add(variable);
	}

	public void mix(ContextDescriptor contextDescriptor) {
		for (String key : contextDescriptor.getVariables()){
			if(this.variableDescriptors.containsKey(key))
				this.variableDescriptors.put(key, contextDescriptor.getList(key));
		}
		
		
	}

	public void add(String key) {
		this.variableDescriptors.put(key, new ArrayList<VariableDescriptor>());	
	}
}
