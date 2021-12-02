package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.DigitalCertificate;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificate.CertificateType;
import com.esferalia.aon.gwt.payroll.shared.EmployeeSegSocial;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.gwt.payroll.shared.SecondaryUserCertificate;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainDigitalCertificatesObject {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private DigitalCertificate digitalCertificateTGSS;
	private List<DigitalCertificate> digitalCertificateSEPEList;
	private List<SecondaryUserCertificate> secondaryUsers;
	
	private Integer enterpriseId;
	
	public MainDigitalCertificatesObject() {
		super();
		this.digitalCertificateTGSS = null;
		this.digitalCertificateSEPEList = new ArrayList<DigitalCertificate>();
		this.secondaryUsers = new ArrayList<SecondaryUserCertificate>();
		this.enterpriseId = null;
	}
	
	public void getEnterpriseId(Consumer<Integer> success, Consumer<Throwable> failure){
		
		impl.getEnterpriseId(new AsyncCallback<Integer>() {
			
			@Override
			public void onSuccess(Integer enterpriseIdIn) {
				enterpriseId = enterpriseIdIn;
				success.accept(enterpriseIdIn);	
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}
	
	
	public void getDigitalCertificateTGSS(Consumer<DigitalCertificate> success, Consumer<Throwable> failure){
		
		impl.getDigitalCertificateTGSS(new AsyncCallback<DigitalCertificate>() {
			
			@Override
			public void onSuccess(DigitalCertificate digitalCertificateTGSSIn) {
				digitalCertificateTGSS = digitalCertificateTGSSIn;
				success.accept(digitalCertificateTGSS);	
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}
	
	public void getDigitalCertificatesSEPE(Consumer<List<DigitalCertificate>> success, Consumer<Throwable> failure){
		
		impl.getDigitalCertificatesSEPE(new AsyncCallback<List<DigitalCertificate>>() {
			
			@Override
			public void onSuccess(List<DigitalCertificate> digitalCertificateSEPEListIn) {
				digitalCertificateSEPEList = digitalCertificateSEPEListIn;
				success.accept(digitalCertificateSEPEList);
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}
	
	public void deleteDigitalCertificate(DigitalCertificate digitalCertificate, Consumer<Void> success, Consumer<Throwable> failure){
		impl.deleteDigitalCertificate(digitalCertificate, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}
	
	public void verifyCertificate(CertificateType certificateType, Consumer<Void> success, Consumer<Throwable> failure){
		
		impl.verifyCertificate(certificateType, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				success.accept(result);	
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
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
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
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
	
	public void checkStatus(Consumer<EnterpriseStatus> success, Consumer<Throwable> failure) {
		//TODO: enterprise Id???
		impl.getEnterpriseStatus(enterpriseId, new AsyncCallback<EnterpriseStatus>() {
			@Override
			public void onFailure(Throwable caught) {
				failure.accept( caught );
			}
			
			 @Override
			public void onSuccess(EnterpriseStatus result) {
				 success.accept(result);
			}
		});
	}

	// ----------------------------------------------------------- GETTERS
	
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
	
	// ----------------------------------------------------------- AUXILIAR METHODS
	
	public DigitalCertificate getDigitalCertificateTGSS() {
		return digitalCertificateTGSS;
	}

	public List<DigitalCertificate> getDigitalCertificateSEPEList() {
		return digitalCertificateSEPEList;
	}

	public String checkIPFType(String ipf) {
		RegExp dniPattern = RegExp.compile("\\d{8}\\-?[A-HJ-NP-TV-Z]");

		if (dniPattern.test(ipf.toUpperCase()))
			return "1";
		else
			return "6";
	}
	
	public boolean hasMoraThanOneSEPECertificates() {
		int sepeCertificates = 0;
		for(DigitalCertificate digitalCertificate : digitalCertificateSEPEList) {
			if(digitalCertificate.getType() == CertificateType.SEPE)
				sepeCertificates++;
		}
		return sepeCertificates > 1;
	}

}
