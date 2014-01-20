package com.esferalia.aon.gwt.fiscal.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


public interface FiscalEnum {

	public enum Administration implements Serializable, IsSerializable{
		ALAVA, BIZKAIA, GIPUZKOA, NAVARRA, COMMON_TERRITORY, UNKNOWN;
	}

	public enum Province implements Serializable, IsSerializable{
		DESCONOCIDO, ARABA, ALBACETE, ALICANTE, ALMERIA, AVILA, BADAJOZ, 
		ILLES_BALEARS, BARCELONA, BURGOS, CACERES, CADIZ, CASTELLON, 
		CIUDAD_REAL, CORDOBA, A_CORUNA, CUENCA, GIRONA, GRANADA, GUADALAJARA, 
		GIPUZKOA, HUELVA, HUESCA, JAEN, LEON, LLEIDA, LA_RIOJA, LUGO, MADRID, 
		MALAGA, MURCIA, NAVARRA, OURENSE, ASTURIAS, PALENCIA, LAS_PALMAS, 
		PONTEVEDRA, SALAMANCA, TENERIFE, CANTABRIA, SEGOVIA, SEVILLA, SORIA, 
		TARRAGONA, TERUEL, TOLEDO, VALENCIA, VALLADOLID, BIZKAIA, ZAMORA, 
		ZARAGOZA, CEUTA, MELILLA, NO_RESIDENTE;
	}

	public enum ActivityGroup implements Serializable, IsSerializable{
		GROUP1("1"), GROUP2("2"), GROUP3("2"), GROUP4("3"), GROUP5("4"), GROUP6(
				"5"), GROUP7("6");

		String key;

		private ActivityGroup(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}
	
	public enum Mod390DetailKey implements Serializable, IsSerializable {
		
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
}
