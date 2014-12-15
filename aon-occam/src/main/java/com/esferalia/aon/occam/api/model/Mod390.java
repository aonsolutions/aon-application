package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Map;

import com.esferalia.aon.watson.util.AonStringUtils;

/**
 * @author ecastellano
 *
 */
@SuppressWarnings("serial")
public class Mod390 implements Serializable {

	private static final String LEGAL_ENTITY_PATTERN = "^[0-9|X|Y|Z].*";

	public static class Activity implements Serializable {

		private String description;
		private String key;
		private String epigraph;
		
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getEpigraph() {
			return epigraph;
		}
		public void setEpigraph(String epigraph) {
			this.epigraph = epigraph;
		}
	}

	public static class Address implements Serializable {

		private String rdocument;
		private String rname;
		private String rstreetType;
		private String rstreetName;
		private String rstreetNumber;
		private String rstreetStair;
		private String rstreetFloor;
		private String rstreetDoor;
		private String rphone;
		private String rtown;
		private int rprovince;
		private String rzip;

		public String getRdocument() {
			return rdocument;
		}

		public void setRdocument(String rdocument) {
			this.rdocument = rdocument;
		}

		public String getRname() {
			return rname;
		}

		public void setRname(String rname) {
			this.rname = rname;
		}

		public String getRstreetType() {
			return rstreetType;
		}

		public void setRstreetType(String rstreetType) {
			this.rstreetType = rstreetType;
		}

		public String getRstreetName() {
			return rstreetName;
		}

		public void setRstreetName(String rstreetName) {
			this.rstreetName = rstreetName;
		}

		public String getRstreetNumber() {
			return rstreetNumber;
		}

		public void setRstreetNumber(String rstreetNumber) {
			this.rstreetNumber = rstreetNumber;
		}

		public String getRstreetStair() {
			return rstreetStair;
		}

		public void setRstreetStair(String rstreetStair) {
			this.rstreetStair = rstreetStair;
		}

		public String getRstreetFloor() {
			return rstreetFloor;
		}

		public void setRstreetFloor(String rstreetFloor) {
			this.rstreetFloor = rstreetFloor;
		}

		public String getRstreetDoor() {
			return rstreetDoor;
		}

		public void setRstreetDoor(String rstreetDoor) {
			this.rstreetDoor = rstreetDoor;
		}

		public String getRphone() {
			return rphone;
		}

		public void setRphone(String rphone) {
			this.rphone = rphone;
		}

		public String getRtown() {
			return rtown;
		}

		public void setRtown(String rtown) {
			this.rtown = rtown;
		}

		public int getRprovince() {
			return rprovince;
		}

		public void setRprovince(int rprovince) {
			this.rprovince = rprovince;
		}

		public String getRzip() {
			return rzip;
		}

		public void setRzip(String rzip) {
			this.rzip = rzip;
		}
	}
	
	public static class SimpliedRegimeActivity implements Serializable {

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
	    
	}

	public static class FarmerRegimeActivity implements Serializable {
		
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

		private Integer id;
		private Mod390DetailKey key;
		private double taxableBase;
		private double percent;
		private double quota;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Mod390DetailKey getKey() {
			return key;
		}
		public void setKey(Mod390DetailKey key) {
			this.key = key;
		}

		public int getTaxableBaseBox() {
			
			// Unica excepcion en todo el modelo.	
			if (key == Mod390DetailKey.K33) return 639;
			
			return (key.getBox() - 1);
		}

		public double getTaxableBase() {
			return taxableBase;
		}

		public void setTaxableBase(double taxableBase) {
			this.taxableBase = taxableBase;
		}

		public double getPercent() {
			return percent;
		}

		public void setPercent(double percent) {
			this.percent = percent;
		}

		public int getBox() {
			return key.getBox();
		}

		public double getQuota() {
			return quota;
		}

		public void setQuota(double quota) {
			this.quota = quota;
		}

		public boolean isShowDescription() {
			return (key.getRowspan() > 0);
		}
	}

	public static enum Mod390DetailKey implements Serializable {
		
		  K00_04 (4			,2		,true,true,5,5)
		 ,K00_08 (8			,528	,true,true,0,5)
		 ,K00_10 (10		,4		,true,true,0,5)
		 ,K00_18 (18		,530	,true,true,0,5)
		 ,K00_21 (21		,6		,true,true,0,5)
		 ,K01_04 (4			,501	,true,true,5,5)
		 ,K01_08 (8			,532	,true,true,0,5)
		 ,K01_10 (10		,503	,true,true,0,5)
		 ,K01_18 (18		,534	,true,true,0,5)
		 ,K01_21 (21		,505	,true,true,0,5)
		 ,K02_04 (4			,8		,true,true,5,5)
		 ,K02_08 (8			,536	,true,true,0,5)
		 ,K02_10 (10		,10		,true,true,0,5)
		 ,K02_18 (18		,538	,true,true,0,5)
		 ,K02_21 (21		,12		,true,true,0,5)
		 ,K03_18 (18		,540	,true,true,2,5)
		 ,K03_21 (21		,14		,true,true,0,5)
		 ,K04_04 (4			,22		,true,true,5,5)
		 ,K04_08 (8			,542	,true,true,0,5)
		 ,K04_10 (10		,43		,true,true,0,5)
		 ,K04_18 (18		,544	,true,true,0,5)
		 ,K04_21 (21		,26		,true,true,0,5)
		 ,K05_04 (4			,546	,true,true,5,5)
		 ,K05_08 (8			,550	,true,true,0,5)
		 ,K05_10 (10		,548	,true,true,0,5)
		 ,K05_18 (18		,554	,true,true,0,5)
		 ,K05_21 (21		,552	,true,true,0,5)
		 ,K06	 (0			,28		,true,true,1,5)
		 ,K07	 (0			,30		,true,true,1,5)
		 ,K08	 (0			,32		,true,true,1,5)
		 ,K09	 (0			,34		,true,false,1,5)
		 ,K10_05 (0.5		,36		,true,true,6,5)
		 ,K10_1  (1			,38		,true,true,0,5)
		 ,K10_14 (1.4		,600	,true,true,0,5)
		 ,K10_4  (4			,40		,true,true,0,5)
		 ,K10_52 (5.2		,602	,true,true,0,5)
		 ,K10_175(1.75		,42		,true,true,0,5)
		 ,K11	 (0			,44		,true,true,1,5)
		 ,K12	 (0			,46		,true,true,1,5)
		 ,K13	 (0			,47		,false,false,1,5)
		 
		 ,K14_04 (4			,191	,true,true,7,5)
		 ,K14_07 (7			,193	,true,true,0,5)
		 ,K14_08 (8			,556	,true,true,0,5)
		 ,K14_10 (10		,604	,true,true,0,5)
		 ,K14_16 (16		,195	,true,true,0,5)
		 ,K14_18 (18		,558	,true,true,0,5)
		 ,K14_21 (21		,606	,true,true,0,5)
		 ,K15	 (0			,49		,true,false,1,5)
		 
		 ,K16_04 (4			,507	,true,true,7,5)
		 ,K16_07 (7			,509	,true,true,0,5)
		 ,K16_08 (8			,560	,true,true,0,5)
		 ,K16_10 (10		,608	,true,true,0,5)
		 ,K16_16 (16		,511	,true,true,0,5)
		 ,K16_18 (18		,562	,true,true,0,5)
		 ,K16_21 (21		,610	,true,true,0,5)
		 ,K17	 (0			,513	,true,false,1,5)
		 
		 ,K18_04 (4			,197	,true,true,7,5)
		 ,K18_07 (7			,199	,true,true,0,5)
		 ,K18_08 (8			,564	,true,true,0,5)
		 ,K18_10 (10		,612	,true,true,0,5)
		 ,K18_16 (16		,201	,true,true,0,5)
		 ,K18_18 (18		,566	,true,true,0,5)
		 ,K18_21 (21		,614	,true,true,0,5)
		 ,K19	 (0			,51		,true,false,1,5)
		 
		 ,K20_04 (4			,515	,true,true,7,5)
		 ,K20_07 (7			,517	,true,true,0,5)
		 ,K20_08 (8			,568	,true,true,0,5)
		 ,K20_10 (10		,616	,true,true,0,5)
		 ,K20_16 (16		,519	,true,true,0,5)
		 ,K20_18 (18		,570	,true,true,0,5)
		 ,K20_21 (21		,618	,true,true,0,5)
		 ,K21	 (0			,521	,true,false,1,5)
		 
		 ,K22_04 (4			,203	,true,true,7,5)
		 ,K22_07 (7			,205	,true,true,0,5)
		 ,K22_08 ( 8		,272	,true,true,0,5)
		 ,K22_10 (10		,620	,true,true,0,5)
		 ,K22_16 (16		,207	,true,true,0,5)
		 ,K22_18 (18		,574	,true,true,0,5)
		 ,K22_21 (21		,622	,true,true,0,5)
		 ,K23	 (0			,53		,true,false,1,5)
		 
		 ,K24_04 (4			,209	,true,true,7,5)
		 ,K24_07 (7			,211	,true,true,0,5)
		 ,K24_08 (8			,576	,true,true,0,5)
		 ,K24_10 (10		,624	,true,true,0,5)
		 ,K24_16 (16		,213	,true,true,0,5)
		 ,K24_18 (18		,578	,true,true,0,5)
		 ,K24_21 (21		,626	,true,true,0,5)
		 ,K25	 (0			,55		,true,false,1,5)
		 
		 ,K26_04 (4			,215	,true,true,7,5)
		 ,K26_07 (7			,217	,true,true,0,5)
		 ,K26_08 (8			,580	,true,true,0,5)
		 ,K26_10 (10		,628	,true,true,0,5)
		 ,K26_16 (16		,219	,true,true,0,5)
		 ,K26_18 (18		,582	,true,true,0,5)
		 ,K26_21 (21		,630	,true,true,0,5)
		 ,K27	 (0			,57		,true,false,1,5)
		 
		 ,K28_04 (4			,221	,true,true,7,5)
		 ,K28_07 (7			,223	,true,true,0,5)
		 ,K28_08 (8			,584	,true,true,0,5)
		 ,K28_10 (10		,632	,true,true,0,5)
		 ,K28_16 (16		,225	,true,true,0,5)
		 ,K28_18 (18		,586	,true,true,0,5)
		 ,K28_21 (21		,634	,true,true,0,5)
		 ,K29	 (0			,59		,true,false,1,5)
		 
		 ,K30_04 (4			,588	,true,true,7,5)
		 ,K30_07 (7			,590	,true,true,0,5)
		 ,K30_08 (8			,592	,true,true,0,5)
		 ,K30_10 (10		,636	,true,true,0,5)
		 ,K30_16 (16		,594	,true,true,0,5)
		 ,K30_18 (18		,596	,true,true,0,5)
		 ,K30_21 (21		,638	,true,true,0,5)
		 ,K31	 (0			,598	,true,false,1,5)
		 
		 ,K32	 (0			,61		,true,true,1,5)
		 ,K33	 (0			,62		,true,true,1,5)
		 ,K34	 (0			,63		,false,true,1,5)
		 ,K35	 (0			,522	,false,true,1,5)
		 ,K36	 (0			,64		,false,false,1,5)
		 ,K37	 (0			,65		,false,false,1,5)
		 
		 ,B099	 (0			,99		,true,true,0,10)
		 ,B103	 (0			,103	,true,true,0,10)
		 ,B104	 (0			,104	,true,true,0,10)
		 ,B105	 (0			,105	,true,true,0,10)
		 ,B110	 (0			,110	,true,true,0,10)
		 ,B112	 (0			,112	,true,true,0,10)
		 ,B100	 (0			,100	,true,true,0,10)
		 ,B101	 (0			,101	,true,true,0,10)
		 ,B102	 (0			,102	,true,true,0,10)
		 ,B227	 (0			,227	,true,true,0,10)
		 ,B228	 (0			,228	,true,true,0,10)
		 ,B106	 (0			,106	,true,true,0,10)
		 ,B107	 (0			,107	,true,true,0,10)
		 ,B108	 (0			,108	,true,true,0,10)
		 ;
		 
		private double percent;
		private int box;
		private boolean showTaxableBase;
		private boolean editable;
		private int rowspan;
		private int page;
			
		private Mod390DetailKey(double percent, int box,
				boolean showTaxableBase, boolean editable, int rowspan, int page ) {
			this.percent = percent;
			this.box = box;
			this.showTaxableBase = showTaxableBase;
			this.editable = editable;
			this.rowspan = rowspan; 
			this.page = page;
		}

		public double getPercent() {
			return percent;
		}

		public int getBox() {
			return box;
		}

		public boolean isShowTaxableBase() {
			return showTaxableBase;
		}

		public boolean isEditable() {
			return editable;
		}
		
		public int getRowspan() {
			return rowspan;
		}
		
		public boolean isPage5Key() {
			return (page == 5);
		}
		public boolean isPage10Key() {
			return (page == 10);
		}
		
	}

	private Integer id;
	private int domain;
	private int enterprise;
	private String enterpriseName;
	private int year;
	private byte administration;
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
	public byte getAdministration() {
		return administration;
	}
	public void setAdministration(byte administration) {
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
		return AonStringUtils.isNotEmpty(document) && !document.matches(LEGAL_ENTITY_PATTERN);
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

