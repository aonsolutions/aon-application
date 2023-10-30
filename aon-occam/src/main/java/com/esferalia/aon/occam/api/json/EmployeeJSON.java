package com.esferalia.aon.occam.api.json;

import static java.util.Objects.isNull;

import java.time.LocalDate;
import java.util.Date;
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
		json.put("id", employee.getEmployeeId());
		
		employee.getName().ifPresent(name-> json.put("name", name));
		employee.getEndDate().ifPresent(endDate -> json.put("endDate", endDate));
		employee.getWorkplaceName().ifPresent(w -> json.put("workplaceName", w));
		
		
		employee.getContractType().ifPresent(contract-> json.put("contractType", contract));
		employee.getOccupation().ifPresent(ocup -> json.put("occupation", ocup));
		employee.getFactor().ifPresent(factor -> json.put("factor", factor));
		employee.getQuoteGroup().ifPresent(gc -> json.put("quoteGroup", gc));

		return json;
	}
	
    @SuppressWarnings("deprecation")
    public static LocalDate toLocalDate(Date date) {
		return isNull(date) ? null : LocalDate.of(date.getYear() + 1900, date.getMonth() + 1, date.getDate()); 
    }
}
