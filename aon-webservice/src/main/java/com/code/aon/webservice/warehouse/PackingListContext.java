package com.code.aon.webservice.warehouse;

import java.util.List;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.Income;

public class PackingListContext {

	CarrierPacking carrierPacking;
	Carrier carrier;
	CompanyFull company;
	List<Delivery> deliveries;
	List<Income> incomes;

	public PackingListContext() {

	}
	
	public CarrierPacking getCarrierPacking() {
		return carrierPacking;
	}
	
	public PackingListContext setCarrierPacking(CarrierPacking carrierPacking) {
		this.carrierPacking = carrierPacking;
		return this;
	}
	
	public CompanyFull getCompany() {
		return company;
	}
	
	public PackingListContext setCompany(CompanyFull company) {
		this.company = company;
		return this;
	}
	
	public Carrier getCarrier() {
		return carrier;
	}
	
	public PackingListContext setCarrier(Carrier carrier) {
		this.carrier = carrier;
		return this;
	}
	
	public List<Delivery> getDeliveries() {
		return deliveries;
	}
	
	public PackingListContext setDeliveries(List<Delivery> deliveries) {
		this.deliveries = deliveries;
		return this;
	}
	
	public List<Income> getIncomes() {
		return incomes;
	}
	
	public PackingListContext setIncomes(List<Income> incomes) {
		this.incomes = incomes;
		return this;
	}
}
