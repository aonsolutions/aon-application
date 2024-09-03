package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Certificate.CertificateOwner;
import com.esferalia.aon.occam.api.model.CertificateInfo;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.gwt.payroll.shared.EmployeeSegSocial;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.gwt.payroll.shared.SecondaryUserCertificate;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainCertificatesObject {
	
	// -------------------------------------------------- Variables
	
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private List<Certificate> certificateList;
	private List<SecondaryUserCertificate> secondaryUsers;
	
	private Integer enterpriseId;
	
	// -------------------------------------------------- Constructor
	
	public MainCertificatesObject() {
		super();
		this.certificateList = new ArrayList<>();
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
	
	public void getDomainId(Consumer<Integer> success, Consumer<Throwable> failure) {
		impl.getDomain(new AsyncCallback<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	// -------------------------------------------------- DataBase methods (DigitalCertificate)
	
	public void getCertificates(Consumer<List<Certificate>> success, Consumer<Throwable> failure){
		
		impl.getCertificates(true, new AsyncCallback<List<Certificate>>() {
			
			@Override
			public void onSuccess(List<Certificate> certificateListDB) {
				certificateList = certificateListDB;
				success.accept(certificateListDB);	
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	public void deleteCertificate(Certificate certificate, Consumer<Void> success, Consumer<Throwable> failure){
		impl.deleteCertificate(certificate, new AsyncCallback<Void>() {
			
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
	
	public void downloadCertificate(Integer certificateId, String filePath, Consumer<Void> success, Consumer<Throwable> failure){
		impl.downloadCertificate(certificateId, filePath, new AsyncCallback<Void>() {
			
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
	
	public void verifyCertificate(Integer certificateId, List<CertificateType> tags, Consumer<Void> success, Consumer<Throwable> failure){
		
		impl.verifyCertificate(certificateId, tags, new AsyncCallback<Void>() {
			
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
	
	public void getCertificateInfo(Integer certificateId, Consumer<CertificateInfo> success, Consumer<Throwable> failure){
		
		impl.getCertificateInfo(certificateId, new AsyncCallback<CertificateInfo>() {
			
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
	
	public void getSecondaryUsers(Integer rattachId, Consumer<List<SecondaryUserCertificate>> success, Consumer<Throwable> failure){
		
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
	
	public void getSecondaryUsersPDF(Integer rattachId, Consumer<String> success, Consumer<Throwable> failure){
		
		impl.getSecondaryUsersPDF(rattachId, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				success.accept(result);	
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	public void getAssignedCCCsPDF(Integer rattachId, Consumer<String> success, Consumer<Throwable> failure){
		
		impl.getAssignedCCCsPDF(rattachId, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
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
			return "6";
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
	
	public List<Certificate> getCertificateList(){
		return this.certificateList;
	}

	public List<Certificate> getUserCertificateList() {
		List<Certificate> certificateUserList = new ArrayList<>();
		
		for(Certificate certificate : certificateList)
			if(certificate.getOwner() == CertificateOwner.USER)
				certificateUserList.add(certificate);
		
		return certificateUserList;
	}

	public List<Certificate> getEnterpriseCertificateList() {
		List<Certificate> certificateEnterpriseList = new ArrayList<>();
		
		for(Certificate certificate : certificateList)
			if(certificate.getOwner() == CertificateOwner.ENTERPRISE)
				certificateEnterpriseList.add(certificate);
		
		return certificateEnterpriseList;
	}

	public void createCertificate(CertificateOwner owner) {
		Certificate certificate = new Certificate()
			.setOwner(owner);
		
		certificateList.add(certificate);
	}

	public boolean hasOtherHasType(CertificateType type, CertificateOwner owner) {
		for(Certificate certificate : certificateList)
			if(null != certificate.getTags() && certificate.getOwner().equals(owner))
				for(CertificateType certificateType : certificate.getTags())
					if(certificateType == type)
						return true;
				
		return false;
	}

	public Integer getCertificateTGSSId(CertificateOwner owner) {
		for(Certificate certificate : certificateList)
			if(certificate.getOwner().equals(owner))
				for(CertificateType tag : certificate.getTags())
					if(tag.equals(CertificateType.TGSS))
						return certificate.getId();
		return null;
	}

}
