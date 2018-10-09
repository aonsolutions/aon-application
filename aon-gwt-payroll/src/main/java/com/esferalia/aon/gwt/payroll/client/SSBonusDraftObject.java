package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SSBonusDraftObject {
	
	Integer contractId = null;
	DomainEmployeesServiceAsync employeesService = null;
	List<SSBonusData> ssBonuses;

// ------------------------------------------------------ VARIABLES ------------------------------------------------------	

// ---------------------------------------------------- CLASS METHODS ----------------------------------------------------	
	
	public SSBonusDraftObject(Employee employee, DomainEmployeesServiceAsync employeesService) {
		this.contractId = employee.getId();
		this.employeesService = employeesService;
		this.ssBonuses = new ArrayList<SSBonusData>();
	}
	
	public List<SSBonusData> getBonuses(){
		return this.ssBonuses;
	}
	
	public SSBonusData getBonus(Integer id){
		for(SSBonusData bonus : this.ssBonuses){
			if(bonus.getId() == id)
				return bonus;
		}
		return null;
	}
	
	public void newBonus(Integer id, Date startDate, Date endDate, String description, Byte type, String expression){
//		Window.alert("ID : " + id + ", startDate : " + startDate + ", endDate : " + endDate + 
//					", description : " + description + ", type : " + type + ", expression : " + expression);
		SSBonusData newBonus = new SSBonusData(id, startDate, endDate, description, type, expression);
		this.ssBonuses.add(newBonus);
	}
	
	public void modifyBonus(Integer id, Date startDate, Date endDate, String description, Byte type, String expression) {
//		Window.alert("ID : " + id + ", startDate : " + startDate + ", endDate : " + endDate + 
//				", description : " + description + ", type : " + type + ", expression : " + expression);
		for(SSBonusData bonus : ssBonuses){
			if(id == bonus.getId()){
				bonus.setStartDate(startDate);
				bonus.setEndDate(endDate);
				bonus.setDescription(description);
				bonus.setType(type);
				bonus.setFormula(expression);
			}
		}
	}
	
	public void deleteBonus(Integer id_bonus) {
		SSBonusData bonus = getBonus(id_bonus);
		if(null != bonus)
			ssBonuses.remove(bonus);
	}

	public int getLastBonusesId() {
		int index = -1;
		for(SSBonusData bonus : ssBonuses){
			if(index < bonus.getId())
				index = bonus.getId();
		}
		return index;
	}

	public void setBonusStartDate(Date value) {
		// TODO Auto-generated method stub
		
	}

	public void setBonusEndDate(Date value) {
		// TODO Auto-generated method stub
		
	}

	public void setBonusDescription(String string) {
		// TODO Auto-generated method stub
		
	}

	public void setBonusType(int selectedIndex) {
		// TODO Auto-generated method stub
		
	}

	public void setBonusExpression(String value) {
		// TODO Auto-generated method stub
		
	}

	// ---------------------------------------------- DATABASE METHODS SYNC  ---------------------------------------------
	
	public void initializeSSBonuses(Consumer<List<SSBonusData>> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeSSBonuses(contractId, new AsyncCallback<List<SSBonusData>>() {
			
			@Override
			public void onSuccess(List<SSBonusData> result) {
				ssBonuses = result;
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}

	public void updateDBBonuses(Consumer<List<SSBonusData>> success, Consumer<Throwable> failure){
		employeesService.setEmployeeSSBonuses(contractId, ssBonuses, new AsyncCallback<List<SSBonusData>>() {
			
			@Override
			public void onSuccess(List<SSBonusData> result) {
				ssBonuses = result;
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------- SETTERS NEW EMPLOYEE INFO -------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
	
}
