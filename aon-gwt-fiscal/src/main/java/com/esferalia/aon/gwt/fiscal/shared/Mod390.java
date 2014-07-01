package com.esferalia.aon.gwt.fiscal.shared;

import java.io.Serializable;
import java.util.Map;

import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.gwt.fiscal.shared.FiscalEnum.Mod390DetailKey;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.view.client.ProvidesKey;

/**
 * @author ecastellano
 *
 */
@SuppressWarnings("serial")
public class Mod390 implements Serializable, IsSerializable {

	public static final ProvidesKey<Mod390> PROVIDES_KEY = new ProvidesKey<Mod390>() {
		@Override
		public Object getKey(Mod390 mod390) {
			return mod390 == null ? null : mod390.getId();
		}
	};

	private static final String LEGAL_ENTITY_PATTERN = "^[0-9|X|Y|Z].*";

	private Integer id;
	private int domain;
	private int enterprise;
	private String enterpriseName;
	private int year;
	private int administration;
	private boolean confidential;
	private boolean replacement;
	private String receipt;
	private String replacedReceipt;
	private String comments;
	
	private String document;
	private String name;
	private String firstSurname;
	private String secondSurname;
	
	private String contactPhone;

	
	private boolean insolvencyDeclarations;
	private boolean taxRefund;
	private boolean specialGroupRegime; 
	private String groupNumber; 
	private boolean groupDependent; 
	private boolean groupRegimeType; 
	private String groupDocument;
	private boolean groupDeclarations;
		
	
	private Activity mainActivity;
	private Activity activity1;
	private Activity activity2;
	private Activity activity3;
	private Activity activity4;
	private Activity activity5;
	private boolean mod347;
	private String mergedDeclarationDocument;
	private String mergedDeclarationName;
	
	private Address address; 
	
	private LegalRepresentative legalRepr1; 
	private LegalRepresentative legalRepr2; 
	private LegalRepresentative legalRepr3;
	
	private Map<Mod390DetailKey,Mod390Detail> generalRegime;
	
	private SimpliedRegimeActivity simpRegime1;
	private SimpliedRegimeActivity simpRegime2;
	
	private FarmerRegimeActivity farmerRegime1;
	private FarmerRegimeActivity farmerRegime2;
	private FarmerRegimeActivity farmerRegime3;
	private FarmerRegimeActivity farmerRegime4;
	private FarmerRegimeActivity farmerRegime5;
	
	private double box74;
	private double box75;
	private double box76;
	private double box77;
	private double box78;
	private double box79;
	private double box80;
	private double box81;
	private double box82;
	private double box83;
	
	private double box84;
	private double box85;
	private double box86;
	private double box87;
	private double box88;
	private double box89;
	private double box90;
	private double box91;
	private double box92;
	private double box93;
	private double box94;
	private double box95;
	private double box96;
	private double box524;
	private double box97;
	private double box98;
	private double box525;
	private double box526;
	private double box99;
	private double box103;
	private double box104;
	private double box105;
	private double box110;
	private double box112;
	private double box100;
	private double box101;
	private double box102;
	private double box227;
	private double box228;
	private double box106;
	private double box107;
	private double box108;
	private double box230;
	private double box109;
	private double box231;
	private double box232;
	private double box111;
	private double box113;
	private double box523;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	public int getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(int enterprise) {
		this.enterprise = enterprise;
	}
	public String getEnterpriseName() {
		return enterpriseName;
	}
	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getAdministration() {
		return administration;
	}
	public void setAdministration(int administration) {
		this.administration = administration;
	}
	public boolean isConfidential() {
		return confidential;
	}
	public void setConfidential(boolean confidential) {
		this.confidential = confidential;
	}
	public boolean isReplacement() {
		return replacement;
	}
	public void setReplacement(boolean replacement) {
		this.replacement = replacement;
	}
	public String getReceipt() {
		return receipt;
	}
	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}
	public String getReplacedReceipt() {
		return replacedReceipt;
	}
	public void setReplacedReceipt(String replacedReceipt) {
		this.replacedReceipt = replacedReceipt;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public boolean isLegalEntity() {
		return !AonUtil.isEmpty(document) && !document.matches(LEGAL_ENTITY_PATTERN);
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFirstSurname() {
		return firstSurname;
	}
	public void setFirstSurname(String firstSurname) {
		this.firstSurname = firstSurname;
	}
	public String getSecondSurname() {
		return secondSurname;
	}
	public void setSecondSurname(String secondSurname) {
		this.secondSurname = secondSurname;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public boolean isInsolvencyDeclarations() {
		return insolvencyDeclarations;
	}
	public void setInsolvencyDeclarations(boolean insolvencyDeclarations) {
		this.insolvencyDeclarations = insolvencyDeclarations;
	}
	public boolean isTaxRefund() {
		return taxRefund;
	}
	public void setTaxRefund(boolean taxRefund) {
		this.taxRefund = taxRefund;
	}
	public boolean isSpecialGroupRegime() {
		return specialGroupRegime;
	}
	public void setSpecialGroupRegime(boolean specialGroupRegime) {
		this.specialGroupRegime = specialGroupRegime;
	}
	public String getGroupNumber() {
		return groupNumber;
	}
	public void setGroupNumber(String groupNumber) {
		this.groupNumber = groupNumber;
	}
	public boolean isGroupDependent() {
		return groupDependent;
	}
	public void setGroupDependent(boolean groupDependent) {
		this.groupDependent = groupDependent;
	}
	public boolean isGroupRegimeType() {
		return groupRegimeType;
	}
	public void setGroupRegimeType(boolean groupRegimeType) {
		this.groupRegimeType = groupRegimeType;
	}
	public String getGroupDocument() {
		return groupDocument;
	}
	public void setGroupDocument(String groupDocument) {
		this.groupDocument = groupDocument;
	}
	public boolean isGroupDeclarations() {
		return groupDeclarations;
	}
	public void setGroupDeclarations(boolean groupDeclarations) {
		this.groupDeclarations = groupDeclarations;
	}
	public boolean isMod347() {
		return mod347;
	}
	public void setMod347(boolean mod347) {
		this.mod347 = mod347;
	}
	public String getMergedDeclarationDocument() {
		return mergedDeclarationDocument;
	}
	public void setMergedDeclarationDocument(String mergedDeclarationDocument) {
		this.mergedDeclarationDocument = mergedDeclarationDocument;
	}
	public String getMergedDeclarationName() {
		return mergedDeclarationName;
	}
	public void setMergedDeclarationName(String mergedDeclarationName) {
		this.mergedDeclarationName = mergedDeclarationName;
	}
	public Activity getMainActivity() {
		return mainActivity;
	}
	public void setMainActivity(Activity mainActivity) {
		this.mainActivity = mainActivity;
	}
	public Activity getActivity1() {
		return activity1;
	}
	public void setActivity1(Activity activity1) {
		this.activity1 = activity1;
	}
	public Activity getActivity2() {
		return activity2;
	}
	public void setActivity2(Activity activity2) {
		this.activity2 = activity2;
	}
	public Activity getActivity3() {
		return activity3;
	}
	public void setActivity3(Activity activity3) {
		this.activity3 = activity3;
	}
	public Activity getActivity4() {
		return activity4;
	}
	public void setActivity4(Activity activity4) {
		this.activity4 = activity4;
	}
	public Activity getActivity5() {
		return activity5;
	}
	public void setActivity5(Activity activity5) {
		this.activity5 = activity5;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public LegalRepresentative getLegalRepr1() {
		return legalRepr1;
	}
	public void setLegalRepr1(LegalRepresentative legalRepr1) {
		this.legalRepr1 = legalRepr1;
	}
	public LegalRepresentative getLegalRepr2() {
		return legalRepr2;
	}
	public void setLegalRepr2(LegalRepresentative legalRepr2) {
		this.legalRepr2 = legalRepr2;
	}
	public LegalRepresentative getLegalRepr3() {
		return legalRepr3;
	}
	public void setLegalRepr3(LegalRepresentative legalRepr3) {
		this.legalRepr3 = legalRepr3;
	}
	public Map<Mod390DetailKey, Mod390Detail> getGeneralRegime() {
		return generalRegime;
	}
	public void setGeneralRegime(Map<Mod390DetailKey, Mod390Detail> generalRegime) {
		this.generalRegime = generalRegime;
	}
	public SimpliedRegimeActivity getSimpRegime1() {
		return simpRegime1;
	}
	public void setSimpRegime1(SimpliedRegimeActivity simpRegime1) {
		this.simpRegime1 = simpRegime1;
	}
	public SimpliedRegimeActivity getSimpRegime2() {
		return simpRegime2;
	}
	public void setSimpRegime2(SimpliedRegimeActivity simpRegime2) {
		this.simpRegime2 = simpRegime2;
	}
	public boolean isSimplifiedRegime() {
		return (
				(getSimpRegime1() != null && !AonUtil.isEmpty(getSimpRegime1().getEpigrafe()))
			||  (getSimpRegime2() != null && !AonUtil.isEmpty(getSimpRegime2().getEpigrafe()))
			||  (getFarmerRegime1() != null && !AonUtil.isEmpty(getFarmerRegime1().getCodigo()))
			||  (getFarmerRegime2() != null && !AonUtil.isEmpty(getFarmerRegime2().getCodigo()))
			||  (getFarmerRegime3() != null && !AonUtil.isEmpty(getFarmerRegime3().getCodigo()))
			||  (getFarmerRegime4() != null && !AonUtil.isEmpty(getFarmerRegime4().getCodigo()))
			||  (getFarmerRegime5() != null && !AonUtil.isEmpty(getFarmerRegime5().getCodigo()))
				); 
	}
	public FarmerRegimeActivity getFarmerRegime1() {
		return farmerRegime1;
	}
	public void setFarmerRegime1(FarmerRegimeActivity farmerRegime1) {
		this.farmerRegime1 = farmerRegime1;
	}
	public FarmerRegimeActivity getFarmerRegime2() {
		return farmerRegime2;
	}
	public void setFarmerRegime2(FarmerRegimeActivity farmerRegime2) {
		this.farmerRegime2 = farmerRegime2;
	}
	public FarmerRegimeActivity getFarmerRegime3() {
		return farmerRegime3;
	}
	public void setFarmerRegime3(FarmerRegimeActivity farmerRegime3) {
		this.farmerRegime3 = farmerRegime3;
	}
	public FarmerRegimeActivity getFarmerRegime4() {
		return farmerRegime4;
	}
	public void setFarmerRegime4(FarmerRegimeActivity farmerRegime4) {
		this.farmerRegime4 = farmerRegime4;
	}
	public FarmerRegimeActivity getFarmerRegime5() {
		return farmerRegime5;
	}
	public void setFarmerRegime5(FarmerRegimeActivity farmerRegime5) {
		this.farmerRegime5 = farmerRegime5;
	}
	public double getBox74() {
		return box74;
	}
	public void setBox74(double box74) {
		this.box74 = box74;
	}
	public double getBox75() {
		return box75;
	}
	public void setBox75(double box75) {
		this.box75 = box75;
	}
	public double getBox76() {
		return box76;
	}
	public void setBox76(double box76) {
		this.box76 = box76;
	}
	public double getBox77() {
		return box77;
	}
	public void setBox77(double box77) {
		this.box77 = box77;
	}
	public double getBox78() {
		return box78;
	}
	public void setBox78(double box78) {
		this.box78 = box78;
	}
	public double getBox79() {
		return box79;
	}
	public void setBox79(double box79) {
		this.box79 = box79;
	}
	public double getBox80() {
		return box80;
	}
	public void setBox80(double box80) {
		this.box80 = box80;
	}
	public double getBox81() {
		return box81;
	}
	public void setBox81(double box81) {
		this.box81 = box81;
	}
	public double getBox82() {
		return box82;
	}
	public void setBox82(double box82) {
		this.box82 = box82;
	}
	public double getBox83() {
		return box83;
	}
	public void setBox83(double box83) {
		this.box83 = box83;
	}
	public double getBox84() {
		return box84;
	}
	public void setBox84(double box84) {
		this.box84 = box84;
	}
	public double getBox85() {
		return box85;
	}
	public void setBox85(double box85) {
		this.box85 = box85;
	}
	public double getBox86() {
		return box86;
	}
	public void setBox86(double box86) {
		this.box86 = box86;
	}
	public double getBox87() {
		return box87;
	}
	public void setBox87(double box87) {
		this.box87 = box87;
	}
	public double getBox88() {
		return box88;
	}
	public void setBox88(double box88) {
		this.box88 = box88;
	}
	public double getBox89() {
		return box89;
	}
	public void setBox89(double box89) {
		this.box89 = box89;
	}
	public double getBox90() {
		return box90;
	}
	public void setBox90(double box90) {
		this.box90 = box90;
	}
	public double getBox91() {
		return box91;
	}
	public void setBox91(double box91) {
		this.box91 = box91;
	}
	public double getBox92() {
		return box92;
	}
	public void setBox92(double box92) {
		this.box92 = box92;
	}
	public double getBox93() {
		return box93;
	}
	public void setBox93(double box93) {
		this.box93 = box93;
	}
	public double getBox94() {
		return box94;
	}
	public void setBox94(double box94) {
		this.box94 = box94;
	}
	public double getBox95() {
		return box95;
	}
	public void setBox95(double box95) {
		this.box95 = box95;
	}
	public double getBox96() {
		return box96;
	}
	public void setBox96(double box96) {
		this.box96 = box96;
	}
	public double getBox524() {
		return box524;
	}
	public void setBox524(double box524) {
		this.box524 = box524;
	}
	public double getBox97() {
		return box97;
	}
	public void setBox97(double box97) {
		this.box97 = box97;
	}
	public double getBox98() {
		return box98;
	}
	public void setBox98(double box98) {
		this.box98 = box98;
	}
	public double getBox525() {
		return box525;
	}
	public void setBox525(double box525) {
		this.box525 = box525;
	}
	public double getBox526() {
		return box526;
	}
	public void setBox526(double box526) {
		this.box526 = box526;
	}
	public double getBox99() {
		return box99;
	}
	public void setBox99(double box99) {
		this.box99 = box99;
	}
	public double getBox103() {
		return box103;
	}
	public void setBox103(double box103) {
		this.box103 = box103;
	}
	public double getBox104() {
		return box104;
	}
	public void setBox104(double box104) {
		this.box104 = box104;
	}
	public double getBox105() {
		return box105;
	}
	public void setBox105(double box105) {
		this.box105 = box105;
	}
	public double getBox110() {
		return box110;
	}
	public void setBox110(double box110) {
		this.box110 = box110;
	}
	public double getBox112() {
		return box112;
	}
	public void setBox112(double box112) {
		this.box112 = box112;
	}
	public double getBox100() {
		return box100;
	}
	public void setBox100(double box100) {
		this.box100 = box100;
	}
	public double getBox101() {
		return box101;
	}
	public void setBox101(double box101) {
		this.box101 = box101;
	}
	public double getBox102() {
		return box102;
	}
	public void setBox102(double box102) {
		this.box102 = box102;
	}
	public double getBox227() {
		return box227;
	}
	public void setBox227(double box227) {
		this.box227 = box227;
	}
	public double getBox228() {
		return box228;
	}
	public void setBox228(double box228) {
		this.box228 = box228;
	}
	public double getBox106() {
		return box106;
	}
	public void setBox106(double box106) {
		this.box106 = box106;
	}
	public double getBox107() {
		return box107;
	}
	public void setBox107(double box107) {
		this.box107 = box107;
	}
	public double getBox108() {
		return box108;
	}
	public void setBox108(double box108) {
		this.box108 = box108;
	}
	public double getBox230() {
		return box230;
	}
	public void setBox230(double box230) {
		this.box230 = box230;
	}
	public double getBox109() {
		return box109;
	}
	public void setBox109(double box109) {
		this.box109 = box109;
	}
	public double getBox231() {
		return box231;
	}
	public void setBox231(double box231) {
		this.box231 = box231;
	}
	public double getBox232() {
		return box232;
	}
	public void setBox232(double box232) {
		this.box232 = box232;
	}
	public double getBox111() {
		return box111;
	}
	public void setBox111(double box111) {
		this.box111 = box111;
	}
	public double getBox113() {
		return box113;
	}
	public void setBox113(double box113) {
		this.box113 = box113;
	}
	public double getBox523() {
		return box523;
	}
	public void setBox523(double box523) {
		this.box523 = box523;
	}
}

