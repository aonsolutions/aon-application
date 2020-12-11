package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeSegSocial;
import com.esferalia.aon.gwt.payroll.shared.SecondaryUserCertificate;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainDigitalCertificatesObject {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private List<DigitalCertificate> digitalCertificateList;
	private List<SecondaryUserCertificate> secondaryUsers;
	
	public MainDigitalCertificatesObject() {
		super();
		this.digitalCertificateList = new ArrayList<DigitalCertificate>();
		this.secondaryUsers = new ArrayList<SecondaryUserCertificate>();
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
	
	public void deleteDigitalCertificate(String typeStr, Consumer<Void> success, Consumer<Throwable> failure){
		Byte type = Byte.parseByte(typeStr);
		impl.deleteDigitalCertificate(type, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void accept) {
				getDigitalCertificates(s -> {
					success.accept(accept);	
				}, f -> {});
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}
	
	public void getSecondaryUsers(Consumer<List<SecondaryUserCertificate>> success, Consumer<Throwable> failure){
		
		impl.getSecondaryUsers(new AsyncCallback<List<SecondaryUserCertificate>>() {
			
			@Override
			public void onSuccess(List<SecondaryUserCertificate> result) {
				secondaryUsers = result;
				success.accept(result);	
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}
	
	public void deleteSecondaryUser(SecondaryUserCertificate secondaryUserCertificate, Consumer<Void> success, Consumer<Throwable> failure){
		String naf = secondaryUserCertificate.getNaf();
		ArrayList<String> nssList = new ArrayList<String>();
		nssList.add(naf);
		
		impl.getIpfxNaf(nssList, new AsyncCallback<EmployeeSegSocial>() {
			
			@Override
			public void onSuccess(EmployeeSegSocial result) {
				String ipf = result.getIpf();
				String ipfType = checkIPFType(ipf);
				
				impl.deleteSecondaryUser(ipfType, ipf, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						success.accept(result);	
					}

					@Override
					public void onFailure(Throwable caught) { }
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
		});
		
	}
	
	public void createSecondaryUser(SecondaryUserCertificate secondaryUserCertificate, Consumer<Void> success, Consumer<Throwable> failure){
		String naf = secondaryUserCertificate.getNaf();
		ArrayList<String> nssList = new ArrayList<String>();
		nssList.add(naf);
		
		impl.getIpfxNaf(nssList, new AsyncCallback<EmployeeSegSocial>() {
			
			@Override
			public void onSuccess(EmployeeSegSocial result) {
				String ipf = result.getIpf();
				String ipfType = checkIPFType(ipf);
				
				impl.createSecondaryUser(ipfType, ipf, naf, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						success.accept(result);;
					}
	
					@Override
					public void onFailure(Throwable caught) {
						failure.accept(caught);
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
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
	
	public void setDescription(byte certificateType, String description) {
		checkAndCreateCertificateType(certificateType);
		for(DigitalCertificate digitalCertificate : digitalCertificateList) 
			if(digitalCertificate.getType() == certificateType)
				digitalCertificate.setDescription(description);
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
				return StringUtils.isBlank(digitalCertificate.getDescription()) ? "Certficado sin nombre" : digitalCertificate.getDescription();
		
		return "Certficado sin nombre";
	}

	public boolean hasData(byte certificateType) {
		for(DigitalCertificate digitalCertificate : digitalCertificateList) 
			if(digitalCertificate.getType() == certificateType)
				return digitalCertificate.getHasCertificate();
		
		return false;
	}
	
	public List<DigitalCertificate> getDigitalCertificateList(){
		return digitalCertificateList;
	}
	
	public List<SecondaryUserCertificate> getSecondaryUsers(boolean showInactives){
		List<SecondaryUserCertificate> activeUsers = new ArrayList<SecondaryUserCertificate>();
		
		if(!showInactives) {
			for(SecondaryUserCertificate secondaryUserCertificate : this.secondaryUsers) {
				if(!AonStringUtils.equalsIgnoreCase(secondaryUserCertificate.getSituation(), "Baja"))
					activeUsers.add(secondaryUserCertificate);
			}
			
			return activeUsers;
		} else
			return secondaryUsers;
		
	}
	
	public String checkIPFType(String ipf) {
		RegExp dniPattern = RegExp.compile("\\d{8}\\-?[A-HJ-NP-TV-Z]");

		if (dniPattern.test(ipf.toUpperCase()))
			return "1";
		else
			return "2";
	}
}
