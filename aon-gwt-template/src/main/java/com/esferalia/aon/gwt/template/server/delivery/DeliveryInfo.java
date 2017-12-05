package com.esferalia.aon.gwt.template.server.delivery;

import java.util.LinkedList;

public class DeliveryInfo {
	LinkedList<Clientes> clientList;
	LinkedList<Albv> albvList;
	LinkedList<AlbvDet> albvDetList;
	
	public static DeliveryInfo getInstance() {
		return new DeliveryInfo();
	}
	
	public DeliveryInfo() {
	
	}

	public LinkedList<Clientes> getClientList() {
		return clientList;
	}

	public DeliveryInfo setClientList(LinkedList<Clientes> clientList) {
		this.clientList = clientList;
		return this;
	}

	public LinkedList<Albv> getAlbvList() {
		return albvList;
	}

	public DeliveryInfo setAlbvList(LinkedList<Albv> albvList) {
		this.albvList = albvList;
		return this;
	}

	public LinkedList<AlbvDet> getAlbvDetList() {
		return albvDetList;
	}

	public DeliveryInfo setAlbvDetList(LinkedList<AlbvDet> albvDetList) {
		this.albvDetList = albvDetList;
		return this;
	}
	
	
}
