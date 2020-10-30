package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainDigitalCertificatesObject {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private List<DigitalCertificate> digitalCertificateList;
	
	public MainDigitalCertificatesObject() {
		super();
		this.digitalCertificateList = new ArrayList<DigitalCertificate>();
	}
	
	public void getDigitalCertificates(Consumer<List<DigitalCertificate>> success, Consumer<Throwable> failure){
		
		impl.getDigitalCertificates(new AsyncCallback<List<DigitalCertificate>>() {
			
			@Override
			public void onSuccess(List<DigitalCertificate> digitalCertificateListDB) {
				digitalCertificateList = digitalCertificateListDB;
				success.accept(digitalCertificateListDB);	
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}
	
	public void setDigitalCertificates(Consumer<Void> success, Consumer<Throwable> failure){
		
		impl.setDigitalCertificates(digitalCertificateList, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void accept) {
				success.accept(accept);	
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}

	public void setConfidential(byte certificateType, boolean isConfidential) {
		checkAndCreateCertificateType(certificateType);
		for(DigitalCertificate digitalCertificate : digitalCertificateList) 
			if(digitalCertificate.getType() == certificateType)
				digitalCertificate.setConfidential(isConfidential);
	}

	public void setPassword(byte certificateType, String password) {
		checkAndCreateCertificateType(certificateType);
		for(DigitalCertificate digitalCertificate : digitalCertificateList) 
			if(digitalCertificate.getType() == certificateType)
				digitalCertificate.setPassword(password);
	}

	private void checkAndCreateCertificateType(byte certificateType) {
		boolean exists = false;
		for(DigitalCertificate digitalCertificate : digitalCertificateList) 
			if(digitalCertificate.getType() == certificateType)
				exists = true;
		
		if(!exists) {
			DigitalCertificate digitalCertificate = new DigitalCertificate();
			digitalCertificate.setType(certificateType);
			digitalCertificateList.add(digitalCertificate);
		}
	}

	public String getDescription(byte certificateType) {
		for(DigitalCertificate digitalCertificate : digitalCertificateList) 
			if(digitalCertificate.getType() == certificateType)
				return digitalCertificate.getDescription();
		
		return "";
	}
		
}
