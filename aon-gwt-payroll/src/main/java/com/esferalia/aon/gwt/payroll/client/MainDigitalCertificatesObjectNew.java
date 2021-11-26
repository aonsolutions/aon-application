package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.CertificateInfo;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificateNew;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificateNew.CertificateOwner;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificateNew.CertificateType;
import com.esferalia.aon.gwt.payroll.shared.EmployeeSegSocial;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.gwt.payroll.shared.SecondaryUserCertificate;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainDigitalCertificatesObjectNew {
	
	// -------------------------------------------------- Variables
	
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private List<DigitalCertificateNew> digitalCertificateList;
	private List<SecondaryUserCertificate> secondaryUsers;
	
	private Integer enterpriseId;
	
	// -------------------------------------------------- Constructor
	
	public MainDigitalCertificatesObjectNew() {
		super();
		this.digitalCertificateList = new ArrayList<>();
		this.secondaryUsers = new ArrayList<>();
		this.enterpriseId = null;
	}
	
	// -------------------------------------------------- DataBase methods
	
	public void getEnterpriseId(Consumer<Integer> success, Consumer<Throwable> failure){
		
		impl.getEnterpriseId(new AsyncCallback<Integer>() {
			
			@Override
			public void onSuccess(Integer enterpriseIdIn) {
				enterpriseId = enterpriseIdIn;
				success.accept(enterpriseIdIn);	
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	public void getDomainUserRoles(Consumer<DomainUserRoles> success, Consumer<Throwable> failure) {
		impl.getDomainUserRoles(new AsyncCallback<DomainUserRoles>() {
			
			@Override
			public void onSuccess(DomainUserRoles result) {
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	// -------------------------------------------------- DataBase methods (DigitalCertificate)
	
	public void getDigitalCertificates(Consumer<List<DigitalCertificateNew>> success, Consumer<Throwable> failure){
		
		impl.getDigitalCertificates(new AsyncCallback<List<DigitalCertificateNew>>() {
			
			@Override
			public void onSuccess(List<DigitalCertificateNew> digitalCertificateListDB) {
				digitalCertificateList = digitalCertificateListDB;
				success.accept(digitalCertificateListDB);	
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	public void deleteDigitalCertificate(DigitalCertificateNew digitalCertificate, Consumer<Void> success, Consumer<Throwable> failure){
		impl.deleteDigitalCertificate(digitalCertificate, new AsyncCallback<Void>() {
			
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
	
	public void verifyCertificate(Integer rattachId, List<CertificateType> tags, Consumer<Void> success, Consumer<Throwable> failure){
		
		impl.verifyCertificate(rattachId, tags, new AsyncCallback<Void>() {
			
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
	
	public void validateCertJava(Integer rattachId, Consumer<CertificateInfo> success, Consumer<Throwable> failure){
		
		impl.validateCertJava(rattachId, new AsyncCallback<CertificateInfo>() {
			
			@Override
			public void onSuccess(CertificateInfo result) {
				success.accept(result);	
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	// -------------------------------------------------- DataBase methods (Secondary users)
	
	public void getSecondaryUsers(Integer rattachId, Consumer<List<SecondaryUserCertificate>> success, Consumer<Throwable> failure) {
		
		impl.getSecondaryUsers(rattachId, new AsyncCallback<List<SecondaryUserCertificate>>() {
			
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
	
	public void deleteSecondaryUser(Integer rattachId, SecondaryUserCertificate secondaryUserCertificate, Consumer<Void> success, Consumer<Throwable> failure){
		String naf = secondaryUserCertificate.getNaf();
		ArrayList<String> nssList = new ArrayList<>();
		nssList.add(naf);
		
		impl.getIpfxNaf(nssList, new AsyncCallback<EmployeeSegSocial>() {
			
			@Override
			public void onSuccess(EmployeeSegSocial result) {
				String ipf = result.getIpf();
				String ipfType = checkIPFType(ipf);
				
				impl.deleteSecondaryUser(rattachId, ipfType, ipf, new AsyncCallback<Void>() {
					
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
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	public void createSecondaryUser(Integer rattachId, SecondaryUserCertificate secondaryUserCertificate, Consumer<Void> success, Consumer<Throwable> failure){
		String naf = secondaryUserCertificate.getNaf();
		ArrayList<String> nssList = new ArrayList<>();
		nssList.add(naf);
		
		impl.getIpfxNaf(nssList, new AsyncCallback<EmployeeSegSocial>() {
			
			@Override
			public void onSuccess(EmployeeSegSocial result) {
				String ipf = result.getIpf();
				String ipfType = checkIPFType(ipf);
				
				impl.createSecondaryUser(rattachId, ipfType, ipf, naf, new AsyncCallback<Void>() {
					
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
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	// -------------------------------------------------- DataBase methods (Secondary users auxiliar method)
	
	public String checkIPFType(String ipf) {
		RegExp dniPattern = RegExp.compile("\\d{8}\\-?[A-HJ-NP-TV-Z]");

		if (dniPattern.test(ipf.toUpperCase()))
			return "1";
		else
			return "2";
	}
	
	// -------------------------------------------------- DataBase methods (checkStatus)
	
	public void checkStatus(Consumer<EnterpriseStatus> success, Consumer<Throwable> failure) {
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

	// -------------------------------------------------- SecondaryUsers
	
	public List<SecondaryUserCertificate> getSecondaryUsers(boolean showInactives){
		List<SecondaryUserCertificate> activeUsers = new ArrayList<>();
		
		if(!showInactives) {
			for(SecondaryUserCertificate secondaryUserCertificate : this.secondaryUsers) {
				if(!AonStringUtils.equalsIgnoreCase(secondaryUserCertificate.getSituation(), "Baja"))
					activeUsers.add(secondaryUserCertificate);
			}
			
			return activeUsers;
		} else
			return secondaryUsers;
		
	}
	
	// -------------------------------------------------- Getter methods
	
	public List<DigitalCertificateNew> getDigitalCertificateList(){
		return this.digitalCertificateList;
	}

	public List<DigitalCertificateNew> getUserCertificateList() {
		List<DigitalCertificateNew> certificateList = new ArrayList<>();
		
		for(DigitalCertificateNew digitalCertificate : digitalCertificateList)
			if(digitalCertificate.getOwner() == CertificateOwner.USER)
				certificateList.add(digitalCertificate);
		
		return certificateList;
	}

	public List<DigitalCertificateNew> getEnterpriseCertificateList() {
		List<DigitalCertificateNew> certificateList = new ArrayList<>();
		
		for(DigitalCertificateNew digitalCertificate : digitalCertificateList)
			if(digitalCertificate.getOwner() == CertificateOwner.ENTERPRISE)
				certificateList.add(digitalCertificate);
		
		return certificateList;
	}

	public void createNewCertificate(CertificateOwner owner) {
		DigitalCertificateNew digitalCertificate = new DigitalCertificateNew();
		digitalCertificate.setOwner(owner);
		digitalCertificate.setHasCertificate(false);
		digitalCertificateList.add(digitalCertificate);
	}

	public boolean hasOtherHasType(CertificateType type, CertificateOwner owner) {
		for(DigitalCertificateNew digitalCertificate : digitalCertificateList)
			if(null != digitalCertificate.getTags() && digitalCertificate.getOwner().equals(owner))
				for(CertificateType certificateType : digitalCertificate.getTags())
					if(certificateType == type)
						return true;
				
		return false;
	}

	public Integer getCertificateTGSSId(CertificateOwner owner) {
		for(DigitalCertificateNew digitalCertificate : digitalCertificateList)
			if(digitalCertificate.getOwner().equals(owner))
				for(CertificateType tag : digitalCertificate.getTags())
					if(tag.equals(CertificateType.TGSS))
						return digitalCertificate.getRattachId();
		return null;
	}

}
