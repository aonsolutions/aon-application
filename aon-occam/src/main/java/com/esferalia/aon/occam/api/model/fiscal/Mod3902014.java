package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod3902014 extends Mod390 {

	private static final long serialVersionUID = -6573468107163112737L;
	
	public static class SimpliedRegimeActivity implements Serializable {

		private static final long serialVersionUID = -9079100575010321532L;
		
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
		
		private static final long serialVersionUID = -4804784023604845693L;
		
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

		private static final long serialVersionUID = 3233560801623501230L;
		
		private Mod390DetailKey key;
		private double taxableBase;
		private double percent;
		private double quota;

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

	}

	public static enum Mod390DetailKey implements Serializable {
		  K00_04 (2,	 4.00, null,true,false)		
		 ,K00_08 (528,	 8.00, 2013,true,false)
		 ,K00_10 (4,	10.00, null,true,false)
		 ,K00_18 (530,	18.00, 2013,true,false)
		 ,K00_21 (6  ,	21.00, null,true,false)
		 ,K01_04 (501,	 4.00, null,true,false)
		 ,K01_08 (532,	 8.00, 2013,true,false)
		 ,K01_10 (503,	10.00, null,true,false)
		 ,K01_18 (534,	18.00, 2013,true,false)
		 ,K01_21 (505,	21.00, null,true,false)
		 ,K40_04 (644,	 4.00, null,true,false)
		 ,K40_10 (646,	10.00, null,true,false)
		 ,K40_21 (648,	21.00, null,true,false)
		 ,K02_04 (8  ,	 4.00, null,true,false)
		 ,K02_08 (536,	 8.00, 2013,true,false)
		 ,K02_10 (10 ,	10.00, null,true,false)
		 ,K02_18 (538,	18.00, 2013,true,false)
		 ,K02_21 (12 ,	21.00, null,true,false)
		 ,K03_18 (540,	18.00, 2013,true,false)
		 ,K03_21 (14 ,	21.00, null,true,false)
		 ,K04_04 (22 ,	 4.00, null,true,false)
		 ,K04_08 (542,	 8.00, 2013,true,false)
		 ,K04_10 (24 ,	10.00, null,true,false)
		 ,K04_18 (544,	18.00, 2013,true,false)
		 ,K04_21 (26 ,	21.00, null,true,false)
		 ,K05_04 (546,	 4.00, null,true,false)
		 ,K05_08 (550,   8.00, 2013,true,false)
		 ,K05_10 (548,  10.00, null,true,false)
		 ,K05_18 (554,  18.00, 2013,true,false)
		 ,K05_21 (552,  21.00, null,true,false)
		 ,K06	 (28,	 0.00, null,true,false)
		 ,K07	 (30,	 0.00, null,true,false)
		 ,K07_I	 (650,	 0.00, null,true,false)
		 ,K08	 (32,	 0.00, null,true,false)
		 ,K09	 (34,	 0.00, null,true,true )
		 ,K10_05 (36,	 0.50, null,true,false)
		 ,K10_1  (600,	 1.00, 2013,true,false)
		 ,K10_14 (600,	 1.40, null,true,false)
	 	 ,K10_4  (40,	 4.00, 2013,true,false)
		 ,K10_52 (602,	 5.20, null,true,false)
		 ,K10_175(42,	 1.75, null,true,false)
		 ,K11	 (44,	 0.00, null,true,false)
		 ,K12	 (46,	 0.00, null,true,false)
		 ,K13	 (47,	 0.00, null,false,true)
		 
		 ,K14_04 (191,	 4.00, null,true,false,true)
		 ,K14_07 (193,	 7.00, null,true,false,true)
		 ,K14_08 (556,	 8.00, null,true,false,true)
		 ,K14_10 (604,	10.00, null,true,false,true)
		 ,K14_16 (195,	16.00, null,true,false,true)
		 ,K14_18 (558,	18.00, null,true,false,true)
		 ,K14_21 (606,	21.00, null,true,false,true)
		 ,K15	 (49,	 0.00, null,true,true )
		 ,K16_04 (507,	 4.00, null,true,false,true)
		 ,K16_07 (509,	 7.00, null,true,false,true)
		 ,K16_08 (560,	 8.00, null,true,false,true)
		 ,K16_10 (608,	10.00, null,true,false,true)
		 ,K16_16 (511,	16.00, null,true,false,true)
		 ,K16_18 (562,	18.00, null,true,false,true)
		 ,K16_21 (610,	21.00, null,true,false,true)
		 ,K17	 (513,	 0.00, null,true,true )
		 ,K18_04 (197,	 4.00, null,true,false,true)
		 ,K18_07 (199,	 7.00, null,true,false,true)
		 ,K18_08 (564,	 8.00, null,true,false,true)
		 ,K18_10 (612,	10.00, null,true,false,true)
		 ,K18_16 (201,	16.00, null,true,false,true)
		 ,K18_18 (566,	18.00, null,true,false,true)
		 ,K18_21 (614,	21.00, null,true,false,true)
		 ,K19	 (51,	 0.00, null,true,true )
		 ,K20_04 (515,	 4.00, null,true,false,true)
		 ,K20_07 (517,	 7.00, null,true,false,true)
		 ,K20_08 (568,	 8.00, null,true,false,true)
		 ,K20_10 (616,	10.00, null,true,false,true)
		 ,K20_16 (519,	16.00, null,true,false,true)
		 ,K20_18 (570,	18.00, null,true,false,true)
		 ,K20_21 (618,	21.00, null,true,false,true)
		 ,K21	 (521,	 0.00, null,true,true )
		 ,K22_04 (203,	 4.00, null,true,false,true)
		 ,K22_07 (205,	 7.00, null,true,false,true)
		 ,K22_08 (272, 	 8.00, null,true,false,true)
		 ,K22_10 (620,	10.00, null,true,false,true)
		 ,K22_16 (207,	16.00, null,true,false,true)
		 ,K22_18 (574, 	18.00, null,true,false,true)
		 ,K22_21 (622,  21.00, null,true,false,true)
		 ,K23	 (53,    0.00, null,true,true )
		 ,K24_04 (209,   4.00, null,true,false,true)
		 ,K24_07 (211,   7.00, null,true,false,true)
		 ,K24_08 (576,   8.00, null,true,false,true)
		 ,K24_10 (624,  10.00, null,true,false,true)
		 ,K24_16 (213,  16.00, null,true,false,true)
		 ,K24_18 (578,  18.00, null,true,false,true)
		 ,K24_21 (626,  21.00, null,true,false,true)
		 ,K25	 (55,    0.00, null,true,true )
		 ,K26_04 (215,   4.00, null,true,false,true)
		 ,K26_07 (217,   7.00, null,true,false,true)
		 ,K26_08 (580,   8.00, null,true,false,true)
		 ,K26_10 (628,  10.00, null,true,false,true)
		 ,K26_16 (219,  16.00, null,true,false,true)
		 ,K26_18 (582,  18.00, null,true,false,true)
		 ,K26_21 (630,  21.00, null,true,false,true)
		 ,K27	 (57,    0.00, null,true,true )
		 ,K28_04 (221,   4.00, null,true,false,true)
		 ,K28_07 (223,   7.00, null,true,false,true)
		 ,K28_08 (584,   8.00, null,true,false,true)
		 ,K28_10 (632,  10.00, null,true,false,true)
		 ,K28_16 (225,  16.00, null,true,false,true)
		 ,K28_18 (586,  18.00, null,true,false,true)
		 ,K28_21 (634,  21.00, null,true,false,true)
		 ,K29	 (59,    0.00, null,true,true )
		 
		 ,K30_04 (588,   4.00, null,true,false,true)
		 ,K30_07 (590,   7.00, null,true,false,true)
		 ,K30_08 (592,   8.00, null,true,false,true)
		 ,K30_10 (636,  10.00, null,true,false,true)
		 ,K30_16 (594,  16.00, null,true,false,true)
		 ,K30_18 (596,  18.00, null,true,false,true)
		 ,K30_21 (638,  21.00, null,true,false,true)
		 ,K31	 (598,   0.00, null,true,true  )
		 
		 ,K32	 (61,    0.00, null,true,false )
		 	 
		 ,K33	 (62,    0.00, null,true ,false)
		 ,K33_I	 (652,   0.00, null,true ,false)
		 ,K34	 (63,    0.00, null,false,false)
		 ,K35	 (522,   0.00, null,false,false)
		 ,K36	 (64,    0.00, null,false,true )
		 ,K37	 (65,    0.00, null,false,true )
		 
		 ,B099	 (99,    0.00, null,true ,false)
		 ,B653	 (653,   0.00, null,true ,false)
		 ,B103	 (103,   0.00, null,true ,false)
		 ,B104	 (104,   0.00, null,true ,false)
		 ,B105	 (105,   0.00, null,true ,false)
		 ,B110	 (110,   0.00, null,true ,false)
		 ,B112	 (112,   0.00, null,true ,false)
		 ,B100	 (100,   0.00, null,true ,false)
		 ,B101	 (101,   0.00, null,true ,false)
		 ,B102	 (102,   0.00, null,true ,false)
		 ,B227	 (227,   0.00, null,true ,false)
		 ,B228	 (228,   0.00, null,true ,false)
		 ,B106	 (106,   0.00, null,true ,false)
		 ,B107	 (107,   0.00, null,true ,false)
		 ,B108	 (108,   0.00, null,true ,false) 
		 ,B654	 (654,   0.00, null,true ,false)
		 ,B656	 (654,   0.00, null,true ,false)
		 ;
		 
		private int box;
		private Double percent;
		private Integer year;
		private boolean taxableBaseAvailable;
		private boolean readonly;
		private boolean prorrata;
		
		private Mod390DetailKey(int box, Double percent, Integer year, boolean taxableBaseAvailable, boolean readonly) {
			this(box, percent, year, taxableBaseAvailable, readonly, false);	
		}
		
		private Mod390DetailKey(int box, Double percent, Integer year, boolean taxableBaseAvailable, boolean readonly, boolean prorrata) {
			this.percent = percent;
			this.box = box;
			this.year = year;
			this.taxableBaseAvailable = taxableBaseAvailable;
			this.readonly = readonly;
			this.prorrata = prorrata;
		}
		public Double getPercent() {
			return percent;
		}

		public int getBox() {
			return box;
		}
		public boolean accept( int year) {
			return (this.year == null || this.year == year); 
		}
		
		public boolean hasTaxableBaseAvailable(){
			return taxableBaseAvailable;
		}
		public boolean isReadonly() {
			return readonly;
		}
		public boolean isSurcharge() {
			return (this == K10_05
				|| this == K10_1
				|| this == K10_14
				|| this == K10_4
				|| this == K10_52
				|| this == K10_175
				|| this == K11
				|| this == B102);
		}
		public boolean isProrrataEnabled() {
			return prorrata;
		}
	}

	public static enum Mod390DetailKeyGroup implements Serializable {
		  DEV_001 ("R\u00E9gimen ordinario"
				  ,new Mod390DetailKey[]{Mod390DetailKey.K00_04,Mod390DetailKey.K00_08
				  ,Mod390DetailKey.K00_10,Mod390DetailKey.K00_18,Mod390DetailKey.K00_21})
		 ,DEV_002 ("Operaciones intragrupo"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K01_04,Mod390DetailKey.K01_08
				 ,Mod390DetailKey.K01_10,Mod390DetailKey.K01_18,Mod390DetailKey.K01_21})
		 ,DEV_003  ("R\u00E9gimen especial del criterio de caja"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K40_04,Mod390DetailKey.K40_10
				 ,Mod390DetailKey.K40_21})
		 ,DEV_004  ("R\u00E9gimen especial de bienes usados, objetos de arte, antig\u00fcedades y objetos de colecci\u00F3n"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K02_04,Mod390DetailKey.K02_08
				 ,Mod390DetailKey.K02_10,Mod390DetailKey.K02_18,Mod390DetailKey.K02_21})
		 ,DEV_005  ("R\u00E9gimen especial de agencias de viaje"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K03_18,Mod390DetailKey.K03_21})
		 ,DEV_006  ("Adquisiciones intracomunitarias de bienes"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K04_04,Mod390DetailKey.K04_08
				 ,Mod390DetailKey.K04_10,Mod390DetailKey.K04_18,Mod390DetailKey.K04_21})
		 ,DEV_007  ("Adquisiciones intracomunitarias de servicios"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K05_04,Mod390DetailKey.K05_08
				 ,Mod390DetailKey.K05_10,Mod390DetailKey.K05_18,Mod390DetailKey.K05_21})
		 ,DEV_008  ("IVA devengado en otros supuestos de inversi\u00F3n del sujeto pasivo"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K06})
		 ,DEV_009  ("Modificaci\u00F3n de bases y cuotas"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K07})
		 ,DEV_010  ("Modificaci\u00F3n de bases y cuotas de operaciones intragrupo"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K07_I})
		 ,DEV_011  ("Modificaci\u00F3n de bases y cuotas por auto de declaraci\u00F3n de concurso de acreedores"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K08})
		 ,DEV_012  ("Total bases y cuotas IVA"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K09})
		 ,DEV_013  ("Recargo de equivalencia",new Mod390DetailKey[]{Mod390DetailKey.K10_05,Mod390DetailKey.K10_1
				 ,Mod390DetailKey.K10_14,Mod390DetailKey.K10_4,Mod390DetailKey.K10_52
				 ,Mod390DetailKey.K10_175})
		 ,DEV_014  ("Modificaci\u00F3n recargo equivalencia"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K11})
		 ,DEV_015  ("Modificaci\u00F3n recargo equivalencia por auto de declaraci\u00F3n de concurso de acreedores"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K12})
		 ,DEV_016  ("Total cuotas IVA y recargo de equivalencia"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K13})
		 ,DED_001  ("IVA deducible en operaciones interiores de bienes y servicios corrientes"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K14_04,Mod390DetailKey.K14_07
				 ,Mod390DetailKey.K14_08,Mod390DetailKey.K14_10,Mod390DetailKey.K14_16
				 ,Mod390DetailKey.K14_18,Mod390DetailKey.K14_21})
		 ,DED_002  ("Total bases imponibles y cuotas deducibles en operaciones interiores de bienes y servicios corrientes"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K15})
		 ,DED_003  ("IVA deducible en operaciones intragrupo de bienes y servicios corrientes"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K16_04,Mod390DetailKey.K16_07
				 ,Mod390DetailKey.K16_08,Mod390DetailKey.K16_10,Mod390DetailKey.K16_16
				 ,Mod390DetailKey.K16_18,Mod390DetailKey.K16_21})
		 ,DED_004  ("Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes y servicios corrientes"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K17})
		 ,DED_005  ("IVA deducible en operaciones interiores de bienes de inversi\u00F3n"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K18_04,Mod390DetailKey.K18_07
				 ,Mod390DetailKey.K18_08,Mod390DetailKey.K18_10,Mod390DetailKey.K18_16
				 ,Mod390DetailKey.K18_18,Mod390DetailKey.K18_21})
		 ,DED_006  ("Total bases imponibles y cuotas deducibles en operaciones interiores de bienes de inversi\u00F3n"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K19})
		 ,DED_007  ("IVA deducible en operaciones intragrupo de bienes de inversi\u00F3n"
				 ,new Mod390DetailKey[]{Mod390DetailKey.K20_04,Mod390DetailKey.K20_07
				 ,Mod390DetailKey.K20_08,Mod390DetailKey.K20_10,Mod390DetailKey.K20_16
				 ,Mod390DetailKey.K20_18,Mod390DetailKey.K20_21})
		,DED_008  ("Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes de inversi\u00F3n"
				,new Mod390DetailKey[]{Mod390DetailKey.K21})
		,DED_009  ("IVA deducible en importaciones de bienes corrientes"
				,new Mod390DetailKey[]{Mod390DetailKey.K22_04,Mod390DetailKey.K22_07
				,Mod390DetailKey.K22_08,Mod390DetailKey.K22_10,Mod390DetailKey.K22_16
				,Mod390DetailKey.K22_18,Mod390DetailKey.K22_21})
		,DED_010  ("Total bases imponibles y cuotas deducibles en importaciones de bienes corrientes"
				,new Mod390DetailKey[]{Mod390DetailKey.K23})
		
		,DED_011  ("IVA deducible en importaciones de bienes de inversi\u00F3n"
				,new Mod390DetailKey[]{Mod390DetailKey.K24_04,Mod390DetailKey.K24_07
				,Mod390DetailKey.K24_08,Mod390DetailKey.K24_10,Mod390DetailKey.K24_16
				,Mod390DetailKey.K24_18,Mod390DetailKey.K24_21})
		,DED_012  ("Total bases imponibles y cuotas deducibles en importaciones de bienes de inversi\u00F3n",new Mod390DetailKey[]{Mod390DetailKey.K25})
		,DED_013  ("IVA deducible en adquisiciones intracomunitarias de bienes corrientes"
				,new Mod390DetailKey[]{Mod390DetailKey.K26_04,Mod390DetailKey.K26_07
				,Mod390DetailKey.K26_08,Mod390DetailKey.K26_10,Mod390DetailKey.K26_16
				,Mod390DetailKey.K26_18,Mod390DetailKey.K26_21})
		,DED_014  ("Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes corrientes"
				,new Mod390DetailKey[]{Mod390DetailKey.K27})
		,DED_015  ("IVA deducible en adquisiciones intracomunitarias de bienes de inversi\u00F3n"
				,new Mod390DetailKey[]{Mod390DetailKey.K28_04,Mod390DetailKey.K28_07
				,Mod390DetailKey.K28_08,Mod390DetailKey.K28_10,Mod390DetailKey.K28_16
				,Mod390DetailKey.K28_18,Mod390DetailKey.K28_21})
		,DED_016  ("Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes de inversi\u00F3n"
				,new Mod390DetailKey[]{Mod390DetailKey.K29})
		,DED_017  ("IVA deducible en adquisiciones intracomunitarias de servicios"
				,new Mod390DetailKey[]{Mod390DetailKey.K30_04,Mod390DetailKey.K30_07
				,Mod390DetailKey.K30_08,Mod390DetailKey.K30_10,Mod390DetailKey.K30_16
				,Mod390DetailKey.K30_18,Mod390DetailKey.K30_21})
		,DED_018  ("Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de servicios"
				,new Mod390DetailKey[]{Mod390DetailKey.K31})
		,DED_019  ("Compensaci\u00F3n en r\u00E9gimen especial de la agricultura, ganaderia y pesca"
				,new Mod390DetailKey[]{Mod390DetailKey.K32})
		,DED_020  ("Rectificaci\u00F3n de deducciones"
				,new Mod390DetailKey[]{Mod390DetailKey.K33})
		,DED_021  ("Rectificaci\u00F3n de deducciones por operaciones intragrupo"
				,new Mod390DetailKey[]{Mod390DetailKey.K33_I})
		,DED_022  ("Regularizaci\u00F3n de bienes de inversi\u00F3n"
				,new Mod390DetailKey[]{Mod390DetailKey.K34})
		,DED_023  ("Regularizaci\u00F3n por aplicaci\u00F3n porcentaje definitivo de prorrata"
				,new Mod390DetailKey[]{Mod390DetailKey.K35})
		,DED_024  ("Suma de deducciones"
				,new Mod390DetailKey[]{Mod390DetailKey.K36})
		,DED_025  ("Resultado r\u00E9gimen general"
				,new Mod390DetailKey[]{Mod390DetailKey.K37})
		 ;
		
		private String label;
		private Mod390DetailKey[] keys;
		
		private Mod390DetailKeyGroup(String label,Mod390DetailKey[] keys) {
			this.label = label;
			this.keys = keys;
		}
		public Mod390DetailKey[] getKeys() {
			return keys;
		}
		public String getLabel() {
			return label;
		}
	}
	
	public static class Prorrata implements Serializable {
		
		private static final long serialVersionUID = 8727162318077358277L;
		
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
	private double box653;
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
	private double box654;
	private double box655;
	private double box656;
	private double box657;
	
	private ArrayList<Prorrata> prorratas = new ArrayList<Mod3902014.Prorrata>();
	
	public boolean is2013() {
		return (getYear() == 2013); 
	}

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
	public ArrayList<Prorrata> getProrratas() {
		return prorratas;
	}
	public void setProrratas(ArrayList<Prorrata> prorratas) {
		this.prorratas = prorratas;
	}

	private static final Mod390DetailKey[] K09_FORMULA = new Mod390DetailKey[]{ Mod390DetailKey.K00_04
				,Mod390DetailKey.K00_08,Mod390DetailKey.K00_10,Mod390DetailKey.K00_18
				,Mod390DetailKey.K00_21,Mod390DetailKey.K01_04,Mod390DetailKey.K01_08
				,Mod390DetailKey.K01_10,Mod390DetailKey.K01_18,Mod390DetailKey.K01_21
				,Mod390DetailKey.K40_04,Mod390DetailKey.K40_10,Mod390DetailKey.K40_21
				,Mod390DetailKey.K02_04,Mod390DetailKey.K02_08,Mod390DetailKey.K02_10
				,Mod390DetailKey.K02_18,Mod390DetailKey.K02_21,Mod390DetailKey.K03_18
				,Mod390DetailKey.K03_21,Mod390DetailKey.K04_04,Mod390DetailKey.K04_08
				,Mod390DetailKey.K04_10,Mod390DetailKey.K04_18,Mod390DetailKey.K04_21
				,Mod390DetailKey.K05_04,Mod390DetailKey.K05_08,Mod390DetailKey.K05_10
				,Mod390DetailKey.K05_18,Mod390DetailKey.K05_21,Mod390DetailKey.K06
				,Mod390DetailKey.K07   ,Mod390DetailKey.K07_I ,Mod390DetailKey.K08};
	private static final Mod390DetailKey[] K13_FORMULA = new Mod390DetailKey[]{ Mod390DetailKey.K09
				,Mod390DetailKey.K10_05,Mod390DetailKey.K10_1 ,Mod390DetailKey.K10_14 
				,Mod390DetailKey.K10_4 ,Mod390DetailKey.K10_52,Mod390DetailKey.K10_175
				,Mod390DetailKey.K11   ,Mod390DetailKey.K12};	 
	private static final Mod390DetailKey[] K15_FORMULA = {
		Mod390DetailKey.K14_04, Mod390DetailKey.K14_07,
		Mod390DetailKey.K14_08, Mod390DetailKey.K14_10,
		Mod390DetailKey.K14_16, Mod390DetailKey.K14_18,
		Mod390DetailKey.K14_21 };

	private static final Mod390DetailKey[] K17_FORMULA = {
			Mod390DetailKey.K16_04, Mod390DetailKey.K16_07,
			Mod390DetailKey.K16_08, Mod390DetailKey.K16_10,
			Mod390DetailKey.K16_16, Mod390DetailKey.K16_18,
			Mod390DetailKey.K16_21 };
	
	private static final Mod390DetailKey[] K19_FORMULA = {
			Mod390DetailKey.K18_04, Mod390DetailKey.K18_07,
			Mod390DetailKey.K18_08, Mod390DetailKey.K18_10,
			Mod390DetailKey.K18_16, Mod390DetailKey.K18_18,
			Mod390DetailKey.K18_21 };
	
	private static final Mod390DetailKey[] K21_FORMULA = {
			Mod390DetailKey.K20_04, Mod390DetailKey.K20_07,
			Mod390DetailKey.K20_08, Mod390DetailKey.K20_10,
			Mod390DetailKey.K20_16, Mod390DetailKey.K20_18,
			Mod390DetailKey.K20_21 };
	
	private static final Mod390DetailKey[] K23_FORMULA = {
			Mod390DetailKey.K22_04, Mod390DetailKey.K22_07,
			Mod390DetailKey.K22_08, Mod390DetailKey.K22_10,
			Mod390DetailKey.K22_16, Mod390DetailKey.K22_18,
			Mod390DetailKey.K22_21 };
	
	private static final Mod390DetailKey[] K25_FORMULA = {
			Mod390DetailKey.K24_04, Mod390DetailKey.K24_07,
			Mod390DetailKey.K24_08, Mod390DetailKey.K24_10,
			Mod390DetailKey.K24_16, Mod390DetailKey.K24_18,
			Mod390DetailKey.K24_21 };
	
	private static final Mod390DetailKey[] K27_FORMULA = {
			Mod390DetailKey.K26_04, Mod390DetailKey.K26_07,
			Mod390DetailKey.K26_08, Mod390DetailKey.K26_10,
			Mod390DetailKey.K26_16, Mod390DetailKey.K26_18,
			Mod390DetailKey.K26_21 };
	
	private static final Mod390DetailKey[] K29_FORMULA = {
			Mod390DetailKey.K28_04, Mod390DetailKey.K28_07,
			Mod390DetailKey.K28_08, Mod390DetailKey.K28_10,
			Mod390DetailKey.K28_16, Mod390DetailKey.K28_18,
			Mod390DetailKey.K28_21 };

	private static final Mod390DetailKey[] K31_FORMULA = {
		Mod390DetailKey.K30_04, Mod390DetailKey.K30_07,
		Mod390DetailKey.K30_08, Mod390DetailKey.K30_10,
		Mod390DetailKey.K30_16, Mod390DetailKey.K30_18,
		Mod390DetailKey.K30_21};	

	private static final Mod390DetailKey[] K36_FORMULA = { Mod390DetailKey.K15,
		Mod390DetailKey.K17, Mod390DetailKey.K19, Mod390DetailKey.K21,
		Mod390DetailKey.K23, Mod390DetailKey.K25, Mod390DetailKey.K27,
		Mod390DetailKey.K29, Mod390DetailKey.K31, Mod390DetailKey.K32,
		Mod390DetailKey.K33, Mod390DetailKey.K33_I,
		Mod390DetailKey.K34, Mod390DetailKey.K35};

	public void calculate() {
		double k37Quota = 0;
		if (!isSimplifiedRegime()) {
			calculate(Mod390DetailKey.K09, K09_FORMULA);
			Mod390Detail k13 = calculate(Mod390DetailKey.K13, K13_FORMULA);
			calculate(Mod390DetailKey.K15, K15_FORMULA);
			calculate(Mod390DetailKey.K17, K17_FORMULA);
			calculate(Mod390DetailKey.K19, K19_FORMULA);
			calculate(Mod390DetailKey.K23, K21_FORMULA);
			calculate(Mod390DetailKey.K23, K23_FORMULA);
			calculate(Mod390DetailKey.K25, K25_FORMULA);
			calculate(Mod390DetailKey.K27, K27_FORMULA);
			calculate(Mod390DetailKey.K29, K29_FORMULA);
			calculate(Mod390DetailKey.K31, K31_FORMULA);
			Mod390Detail k36 = calculate(Mod390DetailKey.K36, K36_FORMULA);
			Mod390Detail k37 = ensure(Mod390DetailKey.K37);
			k37Quota = AonMathUtils.round(k13.getQuota() - k36.getQuota());
			k37.setQuota( k37Quota );
		}
		if (isSimplifiedRegime()) {
			box74 = AonMathUtils.round(getSimpRegime1().getBoxJ() + getSimpRegime2().getBoxJ());
			box75 = AonMathUtils.round(
					getFarmerRegime1().getQuota()
					+ getFarmerRegime2().getQuota()
					+ getFarmerRegime3().getQuota()				
					+ getFarmerRegime4().getQuota()				
					+ getFarmerRegime5().getQuota()				
					);
			box79 = AonMathUtils.round(box74 + box75 + box76 + box77 + box78 );
			box82 = AonMathUtils.round(box80 + box81);
			box83 = AonMathUtils.round(box79 - box82);
		} else {
			box74 = 0;
			box75 = 0;
		}
		box84 = AonMathUtils.round(k37Quota + box83);
		box86 = AonMathUtils.round(box84 - box85);
		box92 = AonMathUtils.round(box84 * box87 / 100);
		box94 = AonMathUtils.round(box92 - box93);
		box108 =  AonMathUtils.round(box99+box653+box103+box104+box105
				+box110+box112+box100+box101+box102+box227
				+box228-box106-box107);
	}
	
	public Mod390Detail ensure(Mod390DetailKey key) {
		Mod390Detail detail = getGeneralRegime().get(key);
		if (detail == null) {
			detail = new Mod390Detail();
			detail.setKey(key);
			detail.setPercent(key.getPercent());
			getGeneralRegime().put(key, detail);
		}
		return detail;
	}
	
	private Mod390Detail calculate(Mod390DetailKey key, Mod390DetailKey ... keys) {
		Mod390Detail detail = ensure(key);
		detail.setTaxableBase(0.0);
		detail.setQuota(0.0);
		for (Mod390DetailKey k : keys) {
			Mod390Detail det = getGeneralRegime().get(k);
			if (det != null) {
				detail.setTaxableBase( AonMathUtils.round(detail.getTaxableBase() + det.getTaxableBase()));
				detail.setQuota( AonMathUtils.round(detail.getQuota() + det.getQuota()));
			}
		}
		return detail;
	}

}

