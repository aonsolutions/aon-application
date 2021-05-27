package net.aonsolutions.aon.tbai.emision._beans.breakdowns;

import java.util.Optional;

public class NoNational implements Breakdown{

	private Service  service;
	private Delivery delivery;
	
	public NoNational(Service service, Delivery delivery) {
		this.service = service;
		this.delivery = delivery;
	}

	public Optional<Service> getService() {return Optional.ofNullable(service);}
	public void setService(Service service) {this.service = service;}

	public Optional<Delivery> getDelivery() {return Optional.ofNullable(delivery);}
	public void setDelivery(Delivery delivery) {this.delivery = delivery;}
	
}
