package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;

import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod3902021 extends Mod390  {

	private static final long serialVersionUID = 6217100697466741197L;
	
	public static class DeductionRegime implements Serializable {
		
		private static final long serialVersionUID = 4277980489328993084L;
		
		private double base1;
		private double quota1;
		private double base2;
		private double quota2;
		private double base3;
		private double quota3;
		private double base4;
		private double quota4;
		private double base5;
		private double quota5;
		private double base6;
		private double quota6;
		private double base7;
		private double quota7;
		private double base8;
		private double quota8;
		private double quota9;
		private double quota10;
		public double getBase1() {
			return base1;
		}
		public void setBase1(double base1) {
			this.base1 = base1;
		}
		public double getQuota1() {
			return quota1;
		}
		public void setQuota1(double quota1) {
			this.quota1 = quota1;
		}
		public double getBase2() {
			return base2;
		}
		public void setBase2(double base2) {
			this.base2 = base2;
		}
		public double getQuota2() {
			return quota2;
		}
		public void setQuota2(double quota2) {
			this.quota2 = quota2;
		}
		public double getBase3() {
			return base3;
		}
		public void setBase3(double base3) {
			this.base3 = base3;
		}
		public double getQuota3() {
			return quota3;
		}
		public void setQuota3(double quota3) {
			this.quota3 = quota3;
		}
		public double getBase4() {
			return base4;
		}
		public void setBase4(double base4) {
			this.base4 = base4;
		}
		public double getQuota4() {
			return quota4;
		}
		public void setQuota4(double quota4) {
			this.quota4 = quota4;
		}
		public double getBase5() {
			return base5;
		}
		public void setBase5(double base5) {
			this.base5 = base5;
		}
		public double getQuota5() {
			return quota5;
		}
		public void setQuota5(double quota5) {
			this.quota5 = quota5;
		}
		public double getBase6() {
			return base6;
		}
		public void setBase6(double base6) {
			this.base6 = base6;
		}
		public double getQuota6() {
			return quota6;
		}
		public void setQuota6(double quota6) {
			this.quota6 = quota6;
		}
		public double getBase7() {
			return base7;
		}
		public void setBase7(double base7) {
			this.base7 = base7;
		}
		public double getQuota7() {
			return quota7;
		}
		public void setQuota7(double quota7) {
			this.quota7 = quota7;
		}
		public double getBase8() {
			return base8;
		}
		public void setBase8(double base8) {
			this.base8 = base8;
		}
		public double getQuota8() {
			return quota8;
		}
		public void setQuota8(double quota8) {
			this.quota8 = quota8;
		}
		public double getQuota9() {
			return quota9;
		}
		public void setQuota9(double quota9) {
			this.quota9 = quota9;
		}
		public double getQuota10() {
			return quota10;
		}
		public void setQuota10(double quota10) {
			this.quota10 = quota10;
		}
	}
	
	public static class SimpliedRegimeActivity implements Serializable {

		private static final long serialVersionUID = 6202543820337953651L;
		
		private String epigrafe;
	    private double unit1;
	    private double amount1;
	    private double unit2;
	    private double amount2;
	    private int    moduleIndex3;
	    private double unit3;
	    private double amount3;
	    private double unit4;
	    private double amount4;
	    private double unit5;
	    private double amount5;
	    private double unit6;
	    private double amount6;
	    private double unit7;
	    private double amount7;
	    private double boxC;
	    private double boxC1;
	    private double boxD;
	    private double boxE;
	    private double boxF;
	    private double boxG;
	    private double boxH;
	    private double boxI;
	    private double boxJ;
	    
		public String getEpigrafe() {
			return epigrafe;
		}
		public void setEpigrafe(String epigrafe) {
			this.epigrafe = epigrafe;
		}
		public double getUnit1() {
			return unit1;
		}
		public void setUnit1(double unit1) {
			this.unit1 = unit1;
		}
		public double getAmount1() {
			return amount1;
		}
		public void setAmount1(double amount1) {
			this.amount1 = amount1;
		}
		public double getUnit2() {
			return unit2;
		}
		public void setUnit2(double unit2) {
			this.unit2 = unit2;
		}
		public double getAmount2() {
			return amount2;
		}
		public void setAmount2(double amount2) {
			this.amount2 = amount2;
		}
		public int getModuleIndex3() {
			return moduleIndex3;
		}
		public void setModuleIndex3(int moduleIndex3) {
			this.moduleIndex3 = moduleIndex3;
		}
		public double getUnit3() {
			return unit3;
		}
		public void setUnit3(double unit3) {
			this.unit3 = unit3;
		}
		public double getAmount3() {
			return amount3;
		}
		public void setAmount3(double amount3) {
			this.amount3 = amount3;
		}
		public double getUnit4() {
			return unit4;
		}
		public void setUnit4(double unit4) {
			this.unit4 = unit4;
		}
		public double getAmount4() {
			return amount4;
		}
		public void setAmount4(double amount4) {
			this.amount4 = amount4;
		}
		public double getUnit5() {
			return unit5;
		}
		public void setUnit5(double unit5) {
			this.unit5 = unit5;
		}
		public double getAmount5() {
			return amount5;
		}
		public void setAmount5(double amount5) {
			this.amount5 = amount5;
		}
		public double getUnit6() {
			return unit6;
		}
		public void setUnit6(double unit6) {
			this.unit6 = unit6;
		}
		public double getAmount6() {
			return amount6;
		}
		public void setAmount6(double amount6) {
			this.amount6 = amount6;
		}
		public double getUnit7() {
			return unit7;
		}
		public void setUnit7(double unit7) {
			this.unit7 = unit7;
		}
		public double getAmount7() {
			return amount7;
		}
		public void setAmount7(double amount7) {
			this.amount7 = amount7;
		}
		public double getBoxC() {
			return boxC;
		}
		public void setBoxC(double boxC) {
			this.boxC = boxC;
		}
		public double getBoxC1() {
			return boxC1;
		}
		public void setBoxC1(double boxC1) {
			this.boxC1 = boxC1;
		}
		public double getBoxD() {
			return boxD;
		}
		public void setBoxD(double boxD) {
			this.boxD = boxD;
		}
		public double getBoxE() {
			return boxE;
		}
		public void setBoxE(double boxE) {
			this.boxE = boxE;
		}
		public double getBoxF() {
			return boxF;
		}
		public void setBoxF(double boxF) {
			this.boxF = boxF;
		}
		public double getBoxG() {
			return boxG;
		}
		public void setBoxG(double boxG) {
			this.boxG = boxG;
		}
		public double getBoxH() {
			return boxH;
		}
		public void setBoxH(double boxH) {
			this.boxH = boxH;
		}
		public double getBoxI() {
			return boxI;
		}
		public void setBoxI(double boxI) {
			this.boxI = boxI;
		}
		public double getBoxJ() {
			return boxJ;
		}
		public void setBoxJ(double boxJ) {
			this.boxJ = boxJ;
		}
		public void setUnit(int line, double value) {
			if (line == 1) setUnit1(value);
			else if (line == 2) setUnit2(value);
			else if (line == 3) setUnit3(value);
			else if (line == 4) setUnit4(value);
			else if (line == 5) setUnit5(value);
			else if (line == 6) setUnit6(value);
			else if (line == 7) setUnit7(value);
		}
		public void setAmount(int line, double value) {
			if (line == 1) setAmount1(value);
			else if (line == 2) setAmount2(value);
			else if (line == 3) setAmount3(value);
			else if (line == 4) setAmount4(value);
			else if (line == 5) setAmount5(value);
			else if (line == 6) setAmount6(value);
			else if (line == 7) setAmount7(value);
		}
	}

	public static class FarmerRegimeActivity implements Serializable {
		
		private static final long serialVersionUID = 2201792990904258961L;
		
		protected String codigo;
	    protected double incomes;
	    protected double quotaIndex;
	    protected double accrualQuota;
	    protected double inputQuotas;
	    protected double quota;
	    
		public String getCodigo() {
			return codigo;
		}
		public void setCodigo(String codigo) {
			this.codigo = codigo;
		}
		public double getIncomes() {
			return incomes;
		}
		public void setIncomes(double incomes) {
			this.incomes = incomes;
		}
		public double getQuotaIndex() {
			return quotaIndex;
		}
		public void setQuotaIndex(double quotaIndex) {
			this.quotaIndex = quotaIndex;
		}
		public double getAccrualQuota() {
			return accrualQuota;
		}
		public void setAccrualQuota(double accrualQuota) {
			this.accrualQuota = accrualQuota;
		}
		public double getInputQuotas() {
			return inputQuotas;
		}
		public void setInputQuotas(double inputQuotas) {
			this.inputQuotas = inputQuotas;
		}
		public double getQuota() {
			return quota;
		}
		public void setQuota(double quota) {
			this.quota = quota;
		}
	    
	}

	public static class Mod390Detail implements Serializable {

		private static final long serialVersionUID = 6537639113193543031L;
		
		private Mod3902021DetailKey key;
		private double taxableBase;
		private double percent;
		private double quota;

		public Mod3902021DetailKey getKey() {
			return key;
		}
		public Mod390Detail setKey(Mod3902021DetailKey key) {
			this.key = key;
			return this;
		}

		public int getTaxableBaseBox() {
			
			// Unica excepcion en todo el modelo.	
			if (key == Mod3902021DetailKey.C0062) return 639;
			
			return (key.getBox() - 1);
		}

		public double getTaxableBase() {
			return taxableBase;
		}

		public Mod390Detail setTaxableBase(double taxableBase) {
			this.taxableBase = taxableBase;
			return this;
		}

		public double getPercent() {
			return percent;
		}

		public Mod390Detail setPercent(double percent) {
			this.percent = percent;
			return this;
		}

		public int getBox() {
			return key.getBox();
		}

		public double getQuota() {
			return quota;
		}

		public Mod390Detail setQuota(double quota) {
			this.quota = quota;
			return this;
		}

	}

	
	public static class Prorrata implements Serializable {

		private static final long serialVersionUID = -2787127406538620223L;
		
		private String activity;
		private String cnae;
		private double amount;
		private double amountWithRight;
		private double percent;
		private String type;
		
		public String getActivity() {
			return activity;
		}
		public void setActivity(String activity) {
			this.activity = activity;
		}
		public String getCnae() {
			return cnae;
		}
		public void setCnae(String cnae) {
			this.cnae = cnae;
		}
		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}
		public double getAmountWithRight() {
			return amountWithRight;
		}
		public void setAmountWithRight(double amountWithRight) {
			this.amountWithRight = amountWithRight;
		}
		public double getPercent() {
			return percent;
		}
		public void setPercent(double percent) {
			this.percent = percent;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
	}

	
	private boolean confidential;
	private boolean replacementDueInsolvencyState;
	private boolean insolvencyDeclarations;
	private boolean insolvencyStateThisYear;
	private boolean insolvencyStateLastPeriod;
	private boolean accrualRegime;
	private boolean accrualRegimeTarget;
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
	
	private Map<Mod3902021DetailKey,Mod390Detail> generalRegime;
	
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
	
	private double box658;
	private double box659;
	
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
	private double box662;
	private double box525;
	private double box526;
	private double box99;
	private double box653;
	private double box103;
	private double box104;
	private double box105;
	private double box110;
	private double box125;
	private double box126;
	private double box127;
	private double box128;
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
	private double box654;
	private double box655;
	private double box656;
	private double box657;
	
	private LinkedList<Prorrata> prorratas = new LinkedList<>();
	
	private DeductionRegime regime1;
	private DeductionRegime regime2;
	private DeductionRegime regime3;
	
	public boolean isConfidential() {
		return confidential;
	}
	public void setConfidential(boolean confidential) {
		this.confidential = confidential;
	}
	public boolean isReplacementDueInsolvencyState() {
		return replacementDueInsolvencyState;
	}
	public void setReplacementDueInsolvencyState(
			boolean replacementDueInsolvencyState) {
		this.replacementDueInsolvencyState = replacementDueInsolvencyState;
	}
	public boolean isInsolvencyDeclarations() {
		return insolvencyDeclarations;
	}
	public void setInsolvencyDeclarations(boolean insolvencyDeclarations) {
		this.insolvencyDeclarations = insolvencyDeclarations;
	}
	
	public boolean isInsolvencyStateThisYear() {
		return insolvencyStateThisYear;
	}
	public void setInsolvencyStateThisYear(boolean insolvencyStateThisYear) {
		this.insolvencyStateThisYear = insolvencyStateThisYear;
	}
	public boolean isInsolvencyStateLastPeriod() {
		return insolvencyStateLastPeriod;
	}
	public void setInsolvencyStateLastPeriod(boolean insolvencyStateLastPeriod) {
		this.insolvencyStateLastPeriod = insolvencyStateLastPeriod;
	}
	public boolean isAccrualRegime() {
		return accrualRegime;
	}
	public void setAccrualRegime(boolean accrualRegime) {
		this.accrualRegime = accrualRegime;
	}
	public boolean isAccrualRegimeTarget() {
		return accrualRegimeTarget;
	}
	public void setAccrualRegimeTarget(boolean accrualRegimeTarget) {
		this.accrualRegimeTarget = accrualRegimeTarget;
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
	public Address ensureAddress() {
		if (address == null) setAddress(new Address());
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public LegalRepresentative getLegalRepr1() {
		return legalRepr1;
	}
	public LegalRepresentative ensureLegalRepr1() {
		if (legalRepr1 == null) setLegalRepr1(new LegalRepresentative());
		return legalRepr1;
	}
	public void setLegalRepr1(LegalRepresentative legalRepr1) {
		this.legalRepr1 = legalRepr1;
	}
	public LegalRepresentative getLegalRepr2() {
		return legalRepr2;
	}
	public LegalRepresentative ensureLegalRepr2() {
		if (legalRepr2 == null) setLegalRepr2(new LegalRepresentative());
		return legalRepr2;
	}
	public void setLegalRepr2(LegalRepresentative legalRepr2) {
		this.legalRepr2 = legalRepr2;
	}
	public LegalRepresentative getLegalRepr3() {
		return legalRepr3;
	}
	public LegalRepresentative ensureLegalRepr3() {
		if (legalRepr3 == null) setLegalRepr3(new LegalRepresentative());
		return legalRepr3;
	}
	public void setLegalRepr3(LegalRepresentative legalRepr3) {
		this.legalRepr3 = legalRepr3;
	}
	public Map<Mod3902021DetailKey, Mod390Detail> getGeneralRegime() {
		return generalRegime;
	}
	public void setGeneralRegime(Map<Mod3902021DetailKey, Mod390Detail> generalRegime) {
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
				(getSimpRegime1() != null && AonStringUtils.isNotEmpty(getSimpRegime1().getEpigrafe()))
			||  (getSimpRegime2() != null && AonStringUtils.isNotEmpty(getSimpRegime2().getEpigrafe()))
			||  (getFarmerRegime1() != null && AonStringUtils.isNotEmpty(getFarmerRegime1().getCodigo()))
			||  (getFarmerRegime2() != null && AonStringUtils.isNotEmpty(getFarmerRegime2().getCodigo()))
			||  (getFarmerRegime3() != null && AonStringUtils.isNotEmpty(getFarmerRegime3().getCodigo()))
			||  (getFarmerRegime4() != null && AonStringUtils.isNotEmpty(getFarmerRegime4().getCodigo()))
			||  (getFarmerRegime5() != null && AonStringUtils.isNotEmpty(getFarmerRegime5().getCodigo()))
				); 
	}
	public FarmerRegimeActivity getFarmerRegime1() {
		return farmerRegime1;
	}
	public FarmerRegimeActivity ensureFarmerRegime1() {
		if (farmerRegime1 == null) setFarmerRegime1(new FarmerRegimeActivity());
		return farmerRegime1;
	}
	public void setFarmerRegime1(FarmerRegimeActivity farmerRegime1) {
		this.farmerRegime1 = farmerRegime1;
	}
	public FarmerRegimeActivity getFarmerRegime2() {
		return farmerRegime2;
	}
	public FarmerRegimeActivity ensureFarmerRegime2() {
		if (farmerRegime2 == null) setFarmerRegime2(new FarmerRegimeActivity());
		return farmerRegime2;
	}
	public void setFarmerRegime2(FarmerRegimeActivity farmerRegime2) {
		this.farmerRegime2 = farmerRegime2;
	}
	public FarmerRegimeActivity getFarmerRegime3() {
		return farmerRegime3;
	}
	public FarmerRegimeActivity ensureFarmerRegime3() {
		if (farmerRegime3 == null) setFarmerRegime3(new FarmerRegimeActivity());
		return farmerRegime3;
	}
	public void setFarmerRegime3(FarmerRegimeActivity farmerRegime3) {
		this.farmerRegime3 = farmerRegime3;
	}
	public FarmerRegimeActivity getFarmerRegime4() {
		return farmerRegime4;
	}
	public FarmerRegimeActivity ensureFarmerRegime4() {
		if (farmerRegime4 == null) setFarmerRegime4(new FarmerRegimeActivity());
		return farmerRegime4;
	}
	public void setFarmerRegime4(FarmerRegimeActivity farmerRegime4) {
		this.farmerRegime4 = farmerRegime4;
	}
	public FarmerRegimeActivity getFarmerRegime5() {
		return farmerRegime5;
	}
	public FarmerRegimeActivity ensureFarmerRegime5() {
		if (farmerRegime5 == null) setFarmerRegime5(new FarmerRegimeActivity());
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
	public double getBox658() {
		return box658;
	}
	public void setBox658(double box658) {
		this.box658 = box658;
	}
	public double getBox659() {
		return box659;
	}
	public void setBox659(double box659) {
		this.box659 = box659;
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
	public double getBox662() {
		return box662;
	}
	public void setBox662(double box662) {
		this.box662 = box662;
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
	public double getBox653() {
		return box653;
	}
	public void setBox653(double box653) {
		this.box653 = box653;
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
	public double getBox125() {
		return box125;
	}
	public void setBox125(double box125) {
		this.box125 = box125;
	}
	public double getBox126() {
		return box126;
	}
	public void setBox126(double box126) {
		this.box126 = box126;
	}
	public double getBox127() {
		return box127;
	}
	public void setBox127(double box127) {
		this.box127 = box127;
	}
	public double getBox128() {
		return box128;
	}
	public void setBox128(double box128) {
		this.box128 = box128;
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
	public double getBox654() {
		return box654;
	}
	public void setBox654(double box654) {
		this.box654 = box654;
	}
	public double getBox655() {
		return box655;
	}
	public void setBox655(double box655) {
		this.box655 = box655;
	}
	public double getBox656() {
		return box656;
	}
	public void setBox656(double box656) {
		this.box656 = box656;
	}
	public double getBox657() {
		return box657;
	}
	public void setBox657(double box657) {
		this.box657 = box657;
	}
	public LinkedList<Prorrata> getProrratas() {
		return prorratas;
	}
	public void setProrratas(LinkedList<Prorrata> prorratas) {
		this.prorratas = prorratas;
	}
	
	public Prorrata getProrrata(int index) {
		return (prorratas!=null && index < prorratas.size()) ? prorratas.get(index) :null;
	}
	
	public DeductionRegime getRegime1() {
		return regime1;
	}
	public DeductionRegime ensureRegime1() {
		if (regime1 == null) setRegime1(new DeductionRegime());
		return regime1;
	}
	public void setRegime1(DeductionRegime regime1) {
		this.regime1 = regime1;
	}
	public DeductionRegime getRegime2() {
		return regime2;
	}
	public DeductionRegime ensureRegime2() {
		if (regime2 == null) setRegime2(new DeductionRegime());
		return regime2;
	}
	public void setRegime2(DeductionRegime regime2) {
		this.regime2 = regime2;
	}
	public DeductionRegime getRegime3() {
		return regime3;
	}
	public DeductionRegime ensureRegime3() {
		if (regime3 == null) setRegime3(new DeductionRegime());
		return regime3;
	}
	public void setRegime3(DeductionRegime regime3) {
		this.regime3 = regime3;
	}

	private static final Mod3902021DetailKey[] K09_FORMULA = new Mod3902021DetailKey[]{ Mod3902021DetailKey.C0002
				,Mod3902021DetailKey.C0004
				,Mod3902021DetailKey.C0006,Mod3902021DetailKey.C0501
				,Mod3902021DetailKey.C0503,Mod3902021DetailKey.C0505
				,Mod3902021DetailKey.C0644,Mod3902021DetailKey.C0646,Mod3902021DetailKey.C0648
				,Mod3902021DetailKey.C0008,Mod3902021DetailKey.C0010
				,Mod3902021DetailKey.C0012
				,Mod3902021DetailKey.C0014,Mod3902021DetailKey.C0022
				,Mod3902021DetailKey.C0024,Mod3902021DetailKey.C0026
				,Mod3902021DetailKey.C0546,Mod3902021DetailKey.C0548
				,Mod3902021DetailKey.C0552,Mod3902021DetailKey.C0028
				,Mod3902021DetailKey.C0030   ,Mod3902021DetailKey.C0650 ,Mod3902021DetailKey.C0032};
	private static final Mod3902021DetailKey[] K13_FORMULA = new Mod3902021DetailKey[]{ Mod3902021DetailKey.C0034
				,Mod3902021DetailKey.C0036,Mod3902021DetailKey.C0600 
				,Mod3902021DetailKey.C0602,Mod3902021DetailKey.C0042
				,Mod3902021DetailKey.C0044   ,Mod3902021DetailKey.C0046};	 
	private static final Mod3902021DetailKey[] K15_FORMULA = {
		Mod3902021DetailKey.C0191, Mod3902021DetailKey.C0604,Mod3902021DetailKey.C0606 };

	private static final Mod3902021DetailKey[] K17_FORMULA = {
			Mod3902021DetailKey.C0507, Mod3902021DetailKey.C0608, Mod3902021DetailKey.C0610 };
	
	private static final Mod3902021DetailKey[] K19_FORMULA = {
			Mod3902021DetailKey.C0197, Mod3902021DetailKey.C0612, Mod3902021DetailKey.C0614 };
	
	private static final Mod3902021DetailKey[] K21_FORMULA = {
			Mod3902021DetailKey.C0515, Mod3902021DetailKey.C0616, Mod3902021DetailKey.C0618 };
	
	private static final Mod3902021DetailKey[] K23_FORMULA = {
			Mod3902021DetailKey.C0203, Mod3902021DetailKey.C0620, Mod3902021DetailKey.C0622 };
	
	private static final Mod3902021DetailKey[] K25_FORMULA = {
			Mod3902021DetailKey.C0209, Mod3902021DetailKey.C0624, Mod3902021DetailKey.C0626 };
	
	private static final Mod3902021DetailKey[] K27_FORMULA = {
			Mod3902021DetailKey.C0215, Mod3902021DetailKey.C0628, Mod3902021DetailKey.C0630 };
	
	private static final Mod3902021DetailKey[] K29_FORMULA = {
			Mod3902021DetailKey.C0221, Mod3902021DetailKey.C0632, Mod3902021DetailKey.C0634 };

	private static final Mod3902021DetailKey[] K31_FORMULA = {
		Mod3902021DetailKey.C0588, Mod3902021DetailKey.C0636, Mod3902021DetailKey.C0638};	

	private static final Mod3902021DetailKey[] K36_FORMULA = { Mod3902021DetailKey.C0049,
		Mod3902021DetailKey.C0513, Mod3902021DetailKey.C0051, Mod3902021DetailKey.C0521,
		Mod3902021DetailKey.C0053, Mod3902021DetailKey.C0055, Mod3902021DetailKey.C0057,
		Mod3902021DetailKey.C0059, Mod3902021DetailKey.C0598, Mod3902021DetailKey.C0061,
		Mod3902021DetailKey.C0661,Mod3902021DetailKey.C0062, Mod3902021DetailKey.C0652,
		Mod3902021DetailKey.C0063, Mod3902021DetailKey.C0522};

	public void calculate() {
		double k37Quota = 0;
//		if (!isSimplifiedRegime()) {
			calculate(Mod3902021DetailKey.C0034, K09_FORMULA);
			Mod390Detail k13 = calculate(Mod3902021DetailKey.C0047, K13_FORMULA);
			calculate(Mod3902021DetailKey.C0049, K15_FORMULA);
			calculate(Mod3902021DetailKey.C0513, K17_FORMULA);
			calculate(Mod3902021DetailKey.C0051, K19_FORMULA);
			calculate(Mod3902021DetailKey.C0521, K21_FORMULA);
			calculate(Mod3902021DetailKey.C0053, K23_FORMULA);
			calculate(Mod3902021DetailKey.C0055, K25_FORMULA);
			calculate(Mod3902021DetailKey.C0057, K27_FORMULA);
			calculate(Mod3902021DetailKey.C0059, K29_FORMULA);
			calculate(Mod3902021DetailKey.C0598, K31_FORMULA);
			Mod390Detail k36 = calculate(Mod3902021DetailKey.C0064, K36_FORMULA);
			Mod390Detail k37 = ensure(Mod3902021DetailKey.C0065);
			k37Quota = AonMathUtils.round(k13.getQuota() - k36.getQuota());
			k37.setQuota( k37Quota );
//		}
		if (isSimplifiedRegime()) {
			box74 = AonMathUtils.round(
					(getSimpRegime1()==null?0:getSimpRegime1().getBoxJ()) 
				  + (getSimpRegime2()==null?0:getSimpRegime2().getBoxJ()));
			box75 = AonMathUtils.round(
					  (getFarmerRegime1()!=null?getFarmerRegime1().getQuota():0)
					+ (getFarmerRegime2()!=null?getFarmerRegime2().getQuota():0)
					+ (getFarmerRegime3()!=null?getFarmerRegime3().getQuota():0)
					+ (getFarmerRegime4()!=null?getFarmerRegime4().getQuota():0)
					+ (getFarmerRegime5()!=null?getFarmerRegime5().getQuota():0)
					);
			box79 = AonMathUtils.round(box74 + box75 + box76 + box77 + box78 );
			box82 = AonMathUtils.round(box80 + box81);
			box83 = AonMathUtils.round(box79 - box82);
		} else {
			box74 = 0;
			box75 = 0;
			box79 = 0;
			box82 = 0;
			box83 = 0;
		}
		box84 = AonMathUtils.round(k37Quota + box83);
		box86 = AonMathUtils.round(box84 + box659 - box85);
		box92 = AonMathUtils.round(box84 * box87 / 100);
		box94 = AonMathUtils.round(box92 + box659 - box93);
		box108 =  AonMathUtils.round(box99+box653+box103+box104+box105
				+box110+box125+box126+box127
				+box128+box100+box101+box102
				+box227+box228-box106-box107);
	}
	
	public Mod390Detail ensure(Mod3902021DetailKey key) {
		Mod390Detail detail = getGeneralRegime().get(key);
		if (detail == null) {
			detail = new Mod390Detail();
			detail.setKey(key);
			detail.setPercent(key.getPercent());
			getGeneralRegime().put(key, detail);
		}
		return detail;
	}
	
	private Mod390Detail calculate(Mod3902021DetailKey key, Mod3902021DetailKey ... keys) {
		Mod390Detail detail = ensure(key);
		detail.setTaxableBase(0.0);
		detail.setQuota(0.0);
		for (Mod3902021DetailKey k : keys) {
			Mod390Detail det = getGeneralRegime().get(k);
			if (det != null) {
				detail.setTaxableBase( AonMathUtils.round(detail.getTaxableBase() + det.getTaxableBase()));
				detail.setQuota( AonMathUtils.round(detail.getQuota() + det.getQuota()));
			}
		}
		return detail;
	}
	
}

