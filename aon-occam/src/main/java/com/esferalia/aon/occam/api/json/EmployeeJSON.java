package com.esferalia.aon.occam.api.json;

import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.payroll.Employee;

public class EmployeeJSON {

	private EmployeeJSON() {
	
	}
	
	
	public static JSONArray toJSON(List<Employee> employees) {
		return toJSON(employees.stream());
	}
	
	public static JSONArray toJSON(Stream<Employee> employees) {
		JSONArray array = new JSONArray();
		employees.forEach(employee -> array.put(toJSON(employee)));
		return array;
	}
	
	public static JSONObject toJSON(Employee employee) {
		JSONObject json = new JSONObject();
		json.put("regime", employee.getRegime());
		json.put("ctaCti", employee.getCcc());
		json.put("ipf", employee.getDni());
		json.put("naf", employee.getNaf());
		json.put("startDate", employee.getStartDate());
		
		employee.getContractType().ifPresent(contract-> json.put("contractType", contract));
		employee.getName().ifPresent(name-> json.put("name", name));
		employee.getEndDate().ifPresent(endDate -> json.put("endDate", endDate));
		employee.getOccupation().ifPresent(ocup -> json.put("occupation", ocup));
		employee.getFactor().ifPresent(factor -> json.put("factor", factor));
		employee.getWorkplaceName().ifPresent(w -> json.put("workplaceName", w));
		employee.getQuoteGroup().ifPresent(gc -> json.put("quoteGroup", gc));

		return json;
	}
}
