package com.code.aon.ui.ecommerce.controller;

import com.code.aon.commercial.Target;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;

public class CartTarget {
	
	/** The registry. */
	//private Registry registry;
	
	/** The advertising. */
	//private Advertising advertising;
	
	private Target target;
	
	private RegistryAddress mainAddress;
	
	private RegistryMedia phone;
	
	private RegistryMedia fax;
	
	private RegistryMedia email;
	
	private RegistryMedia web;	
	
	public Target getTarget() {
		return target;
	}
	
	public void setTarget(Target target) {
		this.target = target;
	}

//	public Registry getRegistry() {
//		return registry;
//	}
//	
//	public void setRegistry(Registry registry) {
//		this.registry = registry;
//	}
//	
//	public Advertising getAdvertising() {
//		return advertising;
//	}
//	
//	public void setAdvertising(Advertising advertising) {
//		this.advertising = advertising;
//	}
	
	public RegistryAddress getMainAddress() {
		return mainAddress;
	}

	public void setMainAddress(RegistryAddress mainAddress) {
		this.mainAddress = mainAddress;
	}

	public RegistryMedia getPhone() {
		return phone;
	}

	public void setPhone(RegistryMedia phone) {
		this.phone = phone;
	}

	public RegistryMedia getFax() {
		return fax;
	}

	public void setFax(RegistryMedia fax) {
		this.fax = fax;
	}

	public RegistryMedia getEmail() {
		return email;
	}

	public void setEmail(RegistryMedia email) {
		this.email = email;
	}

	public RegistryMedia getWeb() {
		return web;
	}

	public void setWeb(RegistryMedia web) {
		this.web = web;
	}

}
