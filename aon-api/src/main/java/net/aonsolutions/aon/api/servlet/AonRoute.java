package net.aonsolutions.aon.api.servlet;

import java.util.function.Function;

import org.json.JSONObject;

import net.aonsolutions.aon.api.ewok.AonApiData;

public class AonRoute {
		
	String path;
	Function<AonApiData, Object> function;
	JSONObject variables;
	
	public AonRoute() {
			
	}
	
	public AonRoute(String path, Function<AonApiData, Object> function) {
		this.path = path;
		this.function = function;
	}
		
	public String getPath() {
		return path;
	}
		
	public AonRoute setPath(String path) {
		this.path = path;
		return this;
	}
	
	public Function<AonApiData, Object> getFunction() {
		return function;
	}
	
	public AonRoute setFunction(Function<AonApiData, Object> function) {
		this.function = function;
		return this;
	}
	
	public JSONObject getVariables() {
		if(variables == null) {
			variables = new JSONObject();
		}
		return variables;
	}
	
	public boolean checkPath(String path) {
		String[] arr1 = getPath().split("/");
		String[] arr2 = path.split("/");
		if(arr1.length != arr2.length) return false;
		for(Integer i = 1; i < arr1.length; i++) {
			if(isVariable(arr1[i])) {
				getVariables().put(arr1[i].replace(":", ""), arr2[i]);
			} else if(!arr1[i].equalsIgnoreCase(arr2[i])) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isVariable(String str) {
		
		return ":".equals(str.substring(0, 1));
	}
}
