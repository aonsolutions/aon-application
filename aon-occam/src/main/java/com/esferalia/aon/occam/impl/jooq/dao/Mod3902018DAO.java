package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModel390.FS_MODEL390;
import static com.esferalia.aon.jooq.tables.FsModelDetail.FS_MODEL_DETAIL;
import static com.esferalia.aon.jooq.tables.FsVat.FS_VAT;
import static com.esferalia.aon.jooq.tables.FsVatDetail.FS_VAT_DETAIL;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jooq.Record1;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.FsModel390Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018.FarmerRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018.SimpliedRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018DetailKey;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018toMod390;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.Mod390toAEATIVA2018;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod3902018DAO {

	private static byte ZERO_BYTE = 0;
	private static byte ONE_BYTE = 1;
	
	@FunctionalInterface
	public static interface IMod390DetailKey {
		boolean accept(VatContext vc);
	}

	public enum Mod303Key implements Serializable  {

		
		H1       ("303-H1"),
		CAG1     ("303-AG1"),
			CAG1_V1  ("303-AG1V1"),
			CAG1_V2  ("303-AG1V2"),	
			CAG1_V3  ("303-AG1V3"),	
			CAG1_V4  ("303-AG1V4"),
			CAG1_V5  ("303-AG1V5"),
			CAG1_V6  ("303-AG1V6"),
			CAG1_V7  ("303-AG1V7"),	
		CAG2     ("303-AG2"  ),
			CAG2_V1  ("303-AG2V1"),
			CAG2_V2  ("303-AG2V2"),	
			CAG2_V3  ("303-AG2V3"),	
			CAG2_V4  ("303-AG2V4"),
			CAG2_V5  ("303-AG2V5"),
			CAG2_V6  ("303-AG2V6"),
			CAG2_V7  ("303-AG2V7"),	
		CAG3     ("303-AG3"  ),
			CAG3_V1  ("303-AG3V1"),
			CAG3_V2  ("303-AG3V2"),	
			CAG3_V3  ("303-AG3V3"),	
			CAG3_V4  ("303-AG3V4"),
			CAG3_V5  ("303-AG3V5"),
			CAG3_V6  ("303-AG3V6"),
			CAG3_V7  ("303-AG3V7"),	
		CAG4     ("303-AG4"  ),
			CAG4_V1  ("303-AG4V1"),
			CAG4_V2  ("303-AG4V2"),	
			CAG4_V3  ("303-AG4V3"),	
			CAG4_V4  ("303-AG4V4"),
			CAG4_V5  ("303-AG4V5"),
			CAG4_V6  ("303-AG4V6"),
			CAG4_V7  ("303-AG4V7"),
		H2       ("303-H2"   ),
		CAC1     ("303-AC1"  ),
			CAC1_M1U ("303-AC1M1U" ),
			CAC1_M1I ("303-AC1M1I" ),
			CAC1_M2U ("303-AC1M2U" ),
			CAC1_M2I ("303-AC1M2I" ),
			CAC1_M3U ("303-AC1M3U" ),
			CAC1_M3I ("303-AC1M3I" ),
			CAC1_M4U ("303-AC1M4U" ),
			CAC1_M4I ("303-AC1M4I" ),
			CAC1_M5U ("303-AC1M5U" ),
			CAC1_M5I ("303-AC1M5I" ),
			CAC1_M6U ("303-AC1M6U" ),
			CAC1_M6I ("303-AC1M6I" ),
			CAC1_M7U ("303-AC1M7U" ),
			CAC1_M7I ("303-AC1M7I" ),
			CAC1_C   ("303-AC1C"   ),
			CAC1_D   ("303-AC1D"   ),
			CAC1_Z   ("303-AC1Z"   ),
			CAC1_ZA  ("303-AC1ZA"  ),
			CAC1_ZD  ("303-AC1ZD"  ),
			CAC1_E   ("303-AC1E"   ),
			CAC1_F   ("303-AC1F"   ),
			CAC1_G0  ("303-AC1G0"   ),
			CAC1_G   ("303-AC1G"   ),
			CAC1_H   ("303-AC1H"   ),
			CAC1_HA  ("303-AC1HA"  ),
			CAC1_HD  ("303-AC1HD"  ),
			CAC1_HT  ("303-AC1HT"  ),
			CAC1_I   ("303-AC1I"   ),
			CAC1_J   ("303-AC1J"   ),
			CAC1_K   ("303-AC1K"   ),
			CAC1_L   ("303-AC1L"   ),
			CAC1_M   ("303-AC1M"   ),
		CAC2     ("303-AC2"  ),
			CAC2_M1U ("303-AC2M1U" ),
			CAC2_M1I ("303-AC2M1I" ),
			CAC2_M2U ("303-AC2M2U" ),
			CAC2_M2I ("303-AC2M2I" ),
			CAC2_M3U ("303-AC2M3U" ),
			CAC2_M3I ("303-AC2M3I" ),
			CAC2_M4U ("303-AC2M4U" ),
			CAC2_M4I ("303-AC2M4I" ),
			CAC2_M5U ("303-AC2M5U" ),
			CAC2_M5I ("303-AC2M5I" ),
			CAC2_M6U ("303-AC2M6U" ),
			CAC2_M6I ("303-AC2M6I" ),
			CAC2_M7U ("303-AC2M7U" ),
			CAC2_M7I ("303-AC2M7I" ),
			CAC2_C   ("303-AC2C"   ),
			CAC2_D   ("303-AC2D"   ),
			CAC2_Z   ("303-AC2Z"   ),
			CAC2_ZA  ("303-AC2ZA"  ),
			CAC2_ZD  ("303-AC2ZD"  ),
			CAC2_E   ("303-AC2E"   ),
			CAC2_F   ("303-AC2F"   ),
			CAC2_G0  ("303-AC2G0"   ),
			CAC2_G   ("303-AC2G"   ),
			CAC2_H   ("303-AC2H"   ),
			CAC2_HA  ("303-AC2HA"  ),
			CAC2_HD  ("303-AC2HD"  ),
			CAC2_HT  ("303-AC2HT"  ),
			CAC2_I   ("303-AC2I"   ),
			CAC2_J   ("303-AC2J"   ),
			CAC2_K   ("303-AC2K"   ),
			CAC2_L   ("303-AC2L"   ),
			CAC2_M   ("303-AC2M"   ),
		CAC3     ("303-AC3"  ),
			CAC3_M1U ("303-AC3M1U" ), 
			CAC3_M1I ("303-AC3M1I" ),
			CAC3_M2U ("303-AC3M2U" ),
			CAC3_M2I ("303-AC3M2I" ),
			CAC3_M3U ("303-AC3M3U" ),
			CAC3_M3I ("303-AC3M3I" ),
			CAC3_M4U ("303-AC3M4U" ),
			CAC3_M4I ("303-AC3M4I" ),
			CAC3_M5U ("303-AC3M5U" ),
			CAC3_M5I ("303-AC3M5I" ),
			CAC3_M6U ("303-AC3M6U" ),
			CAC3_M6I ("303-AC3M6I" ),
			CAC3_M7U ("303-AC3M7U" ),
			CAC3_M7I ("303-AC3M7I" ),
			CAC3_C   ("303-AC3C"   ),
			CAC3_D   ("303-AC3D"   ),
			CAC3_Z   ("303-AC3Z"   ),
			CAC3_ZA  ("303-AC3ZA"  ),
			CAC3_ZD  ("303-AC3ZD"  ),
			CAC3_E   ("303-AC3E"   ),
			CAC3_F   ("303-AC3F"   ),
			CAC3_G0  ("303-AC3G0"   ),
			CAC3_G   ("303-AC3G"   ),
			CAC3_H   ("303-AC3H"   ),
			CAC3_HA  ("303-AC3HA"  ),
			CAC3_HD  ("303-AC3HD"  ),
			CAC3_HT  ("303-AC3HT"  ),
			CAC3_I   ("303-AC3I"   ),
			CAC3_J   ("303-AC3J"   ),
			CAC3_K   ("303-AC3K"   ),
			CAC3_L   ("303-AC3L"   ),
			CAC3_M   ("303-AC3M"   ),
		CAC4     ("303-AC4"  ),
			CAC4_M1U ("303-AC4M1U"), 
			CAC4_M1I ("303-AC4M1I" ),
			CAC4_M2U ("303-AC4M2U" ),
			CAC4_M2I ("303-AC4M2I" ),
			CAC4_M3U ("303-AC4M3U" ),
			CAC4_M3I ("303-AC4M3I" ),
			CAC4_M4U ("303-AC4M4U" ),
			CAC4_M4I ("303-AC4M4I" ),
			CAC4_M5U ("303-AC4M5U" ),
			CAC4_M5I ("303-AC4M5I" ),
			CAC4_M6U ("303-AC4M6U" ),
			CAC4_M6I ("303-AC4M6I" ),
			CAC4_M7U ("303-AC4M7U" ),
			CAC4_M7I ("303-AC4M7I" ),
			CAC4_C   ("303-AC4C"   ),
			CAC4_D   ("303-AC4D"   ),
			CAC4_Z   ("303-AC4Z"   ),
			CAC4_ZA  ("303-AC4ZA"  ),
			CAC4_ZD  ("303-AC4ZD"  ),
			CAC4_E   ("303-AC4E"   ),
			CAC4_F   ("303-AC4F"   ),
			CAC4_G0  ("303-AC4G0"   ),
			CAC4_G   ("303-AC4G"   ),
			CAC4_H   ("303-AC4H"   ),
			CAC4_HA  ("303-AC4HA"  ),
			CAC4_HD  ("303-AC4HD"  ),
			CAC4_HT  ("303-AC4HT"  ),
			CAC4_I   ("303-AC4I"   ),
			CAC4_J   ("303-AC4J"   ),
			CAC4_K   ("303-AC4K"   ),
			CAC4_L   ("303-AC4L"   ),
			CAC4_M   ("303-AC4M"   ),
		C47      ("303-47"   ),
		C48      ("303-48"   ),
		C49      ("303-49"   ),
		C50      ("303-50"   ),
		H3       ("303-H3"   ),
		C51      ("303-51"   ),
		C52      ("303-52"   ),
		C53      ("303-53"   ),
		C54      ("303-54"   ),
		H4       ("303-H4"   ),
		C55      ("303-55"   ),
		C56      ("303-56"   ),
		C57      ("303-57"   ),
		C58      ("303-58"   ),
		H5       ("303-H5"   ),
		C64      ("303-64"   ),
		C65      ("303-65"   ),
		C66      ("303-66"   ),
		C67      ("303-67"   ),
		C68      ("303-68"   ),
		C69      ("303-69"   ),
		C70      ("303-70"   ),
		C71      ("303-71"   ),
		PBK  	 ("303-PBK"  ),
		C59      ("303-59"   ),
		C60      ("303-60"   ),
		C61      ("303-61"   ),
		C62      ("303-62"   ),
		C63      ("303-63"   ),
		C74      ("303-74"   ),
		C75      ("303-75"   ),
		IAC_01	 ("303-IAC01"),
		IAE_01	 ("303-IAE01"),
		IAD_01	 ("303-IAD01"),
		IAC_02	 ("303-IAC02"),
		IAE_02	 ("303-IAE02"),
		IAD_02	 ("303-IAD02"),
		IAC_03	 ("303-IAC03"),
		IAE_03	 ("303-IAE03"),
		IAD_03	 ("303-IAD03"),
		IAC_04	 ("303-IAC04"),
		IAE_04	 ("303-IAE04"),
		IAD_04	 ("303-IAD04"),
		IAC_05	 ("303-IAC05"),
		IAE_05	 ("303-IAE05"),
		IAD_05	 ("303-IAD05"),
		IAC_06	 ("303-IAC06"),
		IAE_06	 ("303-IAE06"),
		IAD_06	 ("303-IAD06"),
		D		 ("303-D"    ),
		C79      ("303-79"   ),
		C80      ("303-80"   ),
		C81      ("303-81"   ),
		C82      ("303-82"   ),
		C83      ("303-83"   ),
		C84      ("303-84"   ),
		C85      ("303-85"   ),
		C86      ("303-86"   ),
		C87      ("303-87"   ),
		C88      ("303-88"   )
		;
		
	    public static Mod303Key getKeyWithValue( String value ) {
	    	for (Mod303Key key : Mod303Key.values() ) {
	    		if (AonStringUtils.equals(value, key.getValue())) {
	    			return key;
	    		}
	    	}
	    	throw new IllegalArgumentException("No enum constant " + Mod303Key.class.getName() + " for value " + value);
	    }
	    
	    public static final String ACTIVITIES_PREFIX = "303-AC";
	    public static final String FARMING_ACTIVITIES_PREFIX = "303-AG";
	    
	    private String value;
	    
		private Mod303Key(String value) {
			this.value = value;
		}
	    
		public boolean isFarmer(){
			return getValue().startsWith(FARMING_ACTIVITIES_PREFIX);
		}

		public boolean isActivity(){
			return getValue().startsWith(ACTIVITIES_PREFIX);
		}

		public String getValue() {
			return value;
		}
	}

	public static enum DetailKey implements Serializable {
		
		  K00_04 (Mod3902018DetailKey.C0002, (vc -> (vc.isNationalSales() && !vc.isRectification() && vc.getPercentage() ==  4)))
		 ,K00_10 (Mod3902018DetailKey.C0004, (vc -> (vc.isNationalSales() && !vc.isRectification() && vc.getPercentage() == 10)))
		 ,K00_21 (Mod3902018DetailKey.C0006, (vc -> (vc.isNationalSales() && !vc.isRectification() && vc.getPercentage() == 21)))
		 ,K01_04 (Mod3902018DetailKey.C0501, null)
		 ,K01_10 (Mod3902018DetailKey.C0503, null)
		 ,K01_21 (Mod3902018DetailKey.C0505, null)
		 ,K02_04 (Mod3902018DetailKey.C0008, null)
		 ,K02_10 (Mod3902018DetailKey.C0010, null)
		 ,K02_21 (Mod3902018DetailKey.C0012, null)
		 ,K03_21 (Mod3902018DetailKey.C0014, null)
		 ,K04_04 (Mod3902018DetailKey.C0022, (vc -> (vc.isIntracommunityPurchase() && vc.getPercentage() ==  4)))
		 ,K04_10 (Mod3902018DetailKey.C0024, (vc -> (vc.isIntracommunityPurchase() && vc.getPercentage() ==  10)))
		 ,K04_21 (Mod3902018DetailKey.C0026, (vc -> (vc.isIntracommunityPurchase() && vc.getPercentage() ==  21)))
	
		 ,K05_04 (Mod3902018DetailKey.C0546, (vc -> (vc.isIntracommunityExpenses() && vc.getPercentage() ==  4)))
		 ,K05_10 (Mod3902018DetailKey.C0548, (vc -> (vc.isIntracommunityExpenses() && vc.getPercentage() == 10)))
		 ,K05_21 (Mod3902018DetailKey.C0552, (vc -> (vc.isIntracommunityExpenses() && vc.getPercentage() == 21)))
		 ,K06	 (Mod3902018DetailKey.C0028	, (vc -> ( vc.isOtherISPPurchase() 
				 								|| vc.isOtherISPExpenses() 
				 								|| vc.isCanCeuMelExpenses() 
				 								|| vc.isExtracommunityExpenses())))
		 
		 ,K07	 (Mod3902018DetailKey.C0030	, (vc -> (vc.isNationalSales() && vc.isRectification())))
		 ,K08	 (Mod3902018DetailKey.C0032	, null)
		 ,K09	 (Mod3902018DetailKey.C0034	, null)
		 ,K10_05 (Mod3902018DetailKey.C0036, (vc -> (vc.isSurcharge() && !vc.isRectification() && vc.isNationalSales() && vc.getSurchargePercent() == 0.5)))
		 ,K10_14 (Mod3902018DetailKey.C0600, (vc -> (vc.isSurcharge() && !vc.isRectification() && vc.isNationalSales() && vc.getSurchargePercent() == 1.4)))
		 ,K10_52 (Mod3902018DetailKey.C0602, (vc -> (vc.isSurcharge() && !vc.isRectification() && vc.isNationalSales() && vc.getSurchargePercent() == 5.2)))
		 ,K10_175(Mod3902018DetailKey.C0042,(vc -> (vc.isSurcharge() && !vc.isRectification() && vc.isNationalSales() && vc.getSurchargePercent() == 1.75)))
		 ,K11	 (Mod3902018DetailKey.C0044	, (vc -> (vc.isSurcharge() &&  vc.isRectification() && vc.isNationalSales()))) 
		 ,K12	 (Mod3902018DetailKey.C0046	, null)
		 ,K13	 (Mod3902018DetailKey.C0047	, null)
		 
		 ,K14_04 (Mod3902018DetailKey.C0191, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 4))) 
		 ,K14_10 (Mod3902018DetailKey.C0604, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 10))) 
		 ,K14_21 (Mod3902018DetailKey.C0606, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 21))) 
		 ,K15	 (Mod3902018DetailKey.C0049	, null)
		 
		 ,K16_04 (Mod3902018DetailKey.C0507, null)
		 ,K16_10 (Mod3902018DetailKey.C0608, null)
		 ,K16_21 (Mod3902018DetailKey.C0610, null)
		 ,K17	 (Mod3902018DetailKey.C0513	, null)
		 
		 ,K18_04 (Mod3902018DetailKey.C0197, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  4)))
		 ,K18_10 (Mod3902018DetailKey.C0612, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 10))) 
		 ,K18_21 (Mod3902018DetailKey.C0614, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 21)))
		 ,K19	 (Mod3902018DetailKey.C0051	, null)
		 
		 ,K20_04 (Mod3902018DetailKey.C0515, null)
		 ,K20_10 (Mod3902018DetailKey.C0616, null)
		 ,K20_21 (Mod3902018DetailKey.C0618, null)
		 ,K21	 (Mod3902018DetailKey.C0521	, null)
		 
		 ,K22_04 (Mod3902018DetailKey.C0203, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 4)))
		 ,K22_10 (Mod3902018DetailKey.C0620, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 10)))
		 ,K22_21 (Mod3902018DetailKey.C0622, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 21)))
		 ,K23	 (Mod3902018DetailKey.C0053	, null)
		 
		 ,K24_04 (Mod3902018DetailKey.C0209, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 4)))
		 ,K24_10 (Mod3902018DetailKey.C0624, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 10)))
		 ,K24_21 (Mod3902018DetailKey.C0626, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 21)))
		 ,K25	 (Mod3902018DetailKey.C0055	, null)
		 
		 ,K26_04 (Mod3902018DetailKey.C0215, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  4)))
		 ,K26_10 (Mod3902018DetailKey.C0628, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  10)))
		 ,K26_21 (Mod3902018DetailKey.C0630, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  21)))
		 ,K27	 (Mod3902018DetailKey.C0057	, null)
		 
		 ,K28_04 (Mod3902018DetailKey.C0221, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  4)))
		 ,K28_10 (Mod3902018DetailKey.C0632, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  10))) 
		 ,K28_21 (Mod3902018DetailKey.C0634, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  21))) 
		 ,K29	 (Mod3902018DetailKey.C0059   , null)
		 
		 ,K30_04 (Mod3902018DetailKey.C0588, (vc -> (vc.isIntracommunityExpenses() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  4)))
		 ,K30_10 (Mod3902018DetailKey.C0636, (vc -> (vc.isIntracommunityExpenses() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  10)))
		 ,K30_21 (Mod3902018DetailKey.C0638, (vc -> (vc.isIntracommunityExpenses() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  21)))
		 ,K31	 (Mod3902018DetailKey.C0598   , null)
		 
		 ,K32	 (Mod3902018DetailKey.C0061   , (vc -> ((vc.isPurchase() || vc.isExpenses()) && vc.isFarmerRegime())))
		 
		 ,K33	 (Mod3902018DetailKey.C0062, null)
		 ,K34	 (Mod3902018DetailKey.C0063, null)
		 ,K35	 (Mod3902018DetailKey.C0522, null)
		 ,K36	 (Mod3902018DetailKey.C0064, null)
		 ,K37	 (Mod3902018DetailKey.C0065, null)
		 
		 ,B099	 (Mod3902018DetailKey.C0099, (vc -> (vc.isNationalSales() && !vc.isRectification())))
		 ,B653	 (Mod3902018DetailKey.C0653, (vc -> (vc.isSales() && vc.isVatAccrualRegime() )))
		 ,B103	 (Mod3902018DetailKey.C0103, (vc -> (vc.isIntracommunitySales() && !vc.isWithoutRightDeductionType())))
		 ,B104	 (Mod3902018DetailKey.C0104, (vc -> (vc.isSales() && !vc.isWithoutRightDeductionType() && (vc.isExtracommunity() || vc.isCanCeuMel()) )))
		 ,B105	 (Mod3902018DetailKey.C0105, (vc -> (vc.isSales() && !vc.isNational() && vc.isWithoutRightDeductionType())))
		 ,B110	 (Mod3902018DetailKey.C0110, (vc -> (vc.isSales() && !vc.isWithoutRightDeductionType() && vc.isOtherISP())))
		 ,B112	 (Mod3902018DetailKey.C0112, null)
		 ,B100	 (Mod3902018DetailKey.C0100, null)
		 ,B101	 (Mod3902018DetailKey.C0101, null)
		 ,B102	 (Mod3902018DetailKey.C0102, (vc -> (vc.isNationalSales() && vc.isSurcharge())))
		 ,B227	 (Mod3902018DetailKey.C0227, null)
		 ,B228	 (Mod3902018DetailKey.C0228, null)
		 ,B106	 (Mod3902018DetailKey.C0106, null)
		 ,B107	 (Mod3902018DetailKey.C0107, (vc -> (vc.isNationalSales() && vc.isInvestment())))
		 ,B108	 (Mod3902018DetailKey.C0108, null)
		 ;
		 
		private Mod3902018DetailKey key;
		private IMod390DetailKey accept;
			
		private DetailKey(Mod3902018DetailKey key, IMod390DetailKey accept) {
			this.key = key;
			this.accept = accept;
		}

		public Mod3902018DetailKey getKey() {
			return key;
		}
		public boolean accept(VatContext vc) {
			return (accept==null)?false:accept.accept(vc);
		}
		
		public static Mod3902018DetailKey[] getKeys(VatContext vc) {
			List<Mod3902018DetailKey> list = new LinkedList<Mod3902018DetailKey>();
			for (DetailKey key : DetailKey.values()) {
				if (key.accept(vc)) {
					list.add(key.getKey()); 
				}
			}
			return list.size()==0?null:list.toArray(new Mod3902018DetailKey[list.size()]);
		}
	}

	public static Mod3902018 create(AONContext ctx, Mod390 model) {
		Mod3902018 mod390 =  new Mod3902018();
		mod390.setId(model.getId());
		mod390.setDomain(model.getDomain());
		mod390.setDomainName(model.getDomainName());
		mod390.setEnterprise(model.getEnterprise());
		mod390.setEnterpriseName(model.getEnterpriseName());
		mod390.setYear(model.getYear());
		mod390.setAdministration(model.getAdministration());
		mod390.setStatus(model.getStatus());
		mod390.setOldStyle(false);
		mod390.setReplacement(model.isReplacement());
		mod390.setComplementary(model.isComplementary());
		mod390.setWithoutActivity(model.isWithoutActivity());
		mod390.setDocument(model.getDocument());
		mod390.setName(model.getName());
		mod390.setFirstSurname(model.getFirstSurname());
		mod390.setSecondSurname(model.getSecondSurname());
		mod390.setContactPhone(model.getContactPhone());
		mod390.setReceipt(model.getReceipt());
		mod390.setReplacedReceipt(model.getReplacedReceipt());
		mod390.setComments(model.getComments());
		mod390.setCreationUser(model.getCreationUser());
		mod390.setCreationDate(model.getCreationDate());
		mod390.setModificationUser(model.getModificationUser());
		mod390.setModificationDate(model.getModificationDate());
		
		int year = mod390.getYear();
		if (year == 2018) {
			LinkedList<Mod390> mod390s = Mod390DAO.getByDomain(ctx, ctx.getDomainId());
			for (Mod390 m390 : mod390s) {
				if ( m390.getYear() == 2015 || m390.getYear() == 2016 || m390.getYear() == 2017) {
					Mod3902015 mod3902015 = Mod3902015DAO.getById(ctx, m390.getId());
					mod390.setMainActivity(mod3902015.getMainActivity());
					mod390.setActivity1(mod3902015.getActivity1());
					mod390.setActivity2(mod3902015.getActivity2());
					mod390.setActivity3(mod3902015.getActivity3());
					mod390.setActivity4(mod3902015.getActivity4());
					mod390.setActivity5(mod3902015.getActivity5());
					mod390.setAddress(mod3902015.getAddress());
					mod390.setLegalRepr1(mod3902015.getLegalRepr1());
					mod390.setLegalRepr2(mod3902015.getLegalRepr2());
					mod390.setLegalRepr3(mod3902015.getLegalRepr3());
					break;
				}
			}
		}
		fillSimplifedRegimeData(ctx, mod390);
		fillGeneralRegimeData(ctx, mod390);
		if (mod390.isSimplifiedRegime()) {
			fillSimplifiedDeclarationResults(ctx, mod390);
		} else {
			fillGeneralDeclarationResults(ctx, mod390);
		}
		mod390.calculate();
		return mod390;	
	}

	public static Mod3902018 getMod3902018(AONContext ctx, Mod390 m390) {
		if (m390.getId() == null) {
			return create(ctx, m390);
		}
		return getById(ctx, m390.getId());
	}

	public static Mod3902018 getById(AONContext ctx, int id) {
		ctx.checkRead();
		Mod3902018 mod390 = new Mod3902018();
		ctx.getDslContext()
				.selectFrom(FS_MODEL390)
				.where(FS_MODEL390.ID.equal(id))
				.fetch()
				.stream()
				.forEach(
						record -> {
							populate(record, mod390);
						});
		if (mod390.getId() != null) {
			return mod390;
		}
		return null;
	}

	private static void populate(FsModel390Record record, Mod3902018 mod390)  {
		mod390
		.setId(record.getValue(FS_MODEL390.ID))
		.setAdministration( com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Administration.class,record.getValue(FS_MODEL390.ADMINISTRATION)))
		.setReplacement( record.getValue(FS_MODEL390.REPLACEMENT)==1 )
		.setComplementary(record.getValue(FS_MODEL390.COMPLEMENTARY)==1 )
		.setStatus(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(FiscalStatus.class,record.getValue(FS_MODEL390.STATUS)))
		.setYear(record.getValue(FS_MODEL390.YEAR))
		.setDomain(record.getValue(FS_MODEL390.DOMAIN))
		.setEnterprise(record.getValue(FS_MODEL390.ENTERPRISE))
		.setDocument(record.getValue(FS_MODEL390.DOCUMENT))
		.setEnterpriseName(record.getValue(FS_MODEL390.NAME))
		.setReceipt(record.getValue(FS_MODEL390.RECEIPT))
		.setReplacedReceipt(record.getValue(FS_MODEL390.REPLACED_RECEIPT))
		.setComments(record.getValue(FS_MODEL390.COMMENTS));
		
		String model = record.getValue(FS_MODEL390.MODEL);
		if (AonStringUtils.contains(model, "<AEATIVA2018>"))  {
			StringReader reader = new StringReader(record.getValue(FS_MODEL390.MODEL));
			try {
				if (mod390.getYear() == 2018) {
					JAXBContext context = JAXBContext.newInstance(AEATIVA2018.class);
					Unmarshaller um = context.createUnmarshaller();
					AEATIVA2018 iva = (AEATIVA2018) um.unmarshal(reader);
					AEATIVA2018toMod390.populate(mod390, iva);
					mod390.setXmlFormat("AEAT_2018");
				} else {
					throw new IllegalArgumentException("Ejercicio incorrecto.");
				}
			} catch (ParseException | JAXBException e1) {
				e1.printStackTrace();
				throw new AonCoreException("XML PROBLEM",e1);
			}
		} else {
			mod390.setXmlFormat("INVALID");
		}
		
	}
	
	public static String getXMLContentById(AONContext ctx, Integer id) {
		ctx.checkRead();
		String model = ctx.getDslContext()
				.selectFrom(FS_MODEL390)
				.where(FS_MODEL390.ID.equal(id))
				.fetchOne(FS_MODEL390.MODEL);
		return model;
	}


	public static Mod3902018 save(AONContext ctx, Mod3902018 mod390)  {
		if (mod390.getId() == null) {
			return insert(ctx, mod390);
		} else {
			return update(ctx, mod390);
		}
	}
	
	private static String getXMLModel( Mod3902018 mod390 ) {
		try {
			if (mod390.getYear() == 2018 || mod390.getYear() == 2016 || mod390.getYear() == 2017) {
				AEATIVA2018 iva = Mod390toAEATIVA2018.getAEATIVA2018(mod390);
				StringWriter writer = new StringWriter();
				JAXBContext context = JAXBContext.newInstance(AEATIVA2018.class);
				Marshaller um = context.createMarshaller();
				um.setProperty("jaxb.encoding", "ISO-8859-1");
				um.marshal(iva,writer);
				return AonStringUtils.trim(writer.toString());
			} else {
				throw new IllegalArgumentException("Ejercicio incorrecto.");
			}
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new AonCoreException("Error en conversión XML",e);
		}				
	}
	

	private static Mod3902018 insert(AONContext ctx, Mod3902018 mod390) {
		validate(ctx, mod390);
		FsModel390Record record = ctx.getDslContext()
			.insertInto(FS_MODEL390)
			.set(FS_MODEL390.DOMAIN, mod390.getDomain())
			.set(FS_MODEL390.ENTERPRISE, mod390.getEnterprise())
			.set(FS_MODEL390.YEAR, mod390.getYear())
			.set(FS_MODEL390.ADMINISTRATION,mod390.getAdministration().getValue())
			.set(FS_MODEL390.STATUS,AonEnumUtils.getByte( mod390.getStatus()  ))
			.set(FS_MODEL390.SECURITY_LEVEL,AonEnumUtils.getByte(mod390.isConfidential()))
			.set(FS_MODEL390.COMPLEMENTARY, (byte) 0)
			.set(FS_MODEL390.REPLACEMENT,AonEnumUtils.getByte(mod390.isReplacement()))
			.set(FS_MODEL390.DOCUMENT, mod390.getDocument())
			.set(FS_MODEL390.NAME, mod390.getName())
			.set(FS_MODEL390.COMMENTS, mod390.getComments())
			.set(FS_MODEL390.RECEIPT, mod390.getReceipt())
			.set(FS_MODEL390.REPLACED_RECEIPT, mod390.getReplacedReceipt())
			.set(FS_MODEL390.MODEL, getXMLModel(mod390))
			.returning(FS_MODEL390.ID).fetchOne();
		mod390.setId(record.getId());
		return mod390;
	}
	
	private static Mod3902018 update(AONContext ctx, Mod3902018 mod390) {
		validate(ctx, mod390);
		ctx.getDslContext()
			.update(FS_MODEL390)
			.set(FS_MODEL390.YEAR, mod390.getYear())
			.set(FS_MODEL390.ADMINISTRATION,mod390.getAdministration().getValue())
			.set(FS_MODEL390.STATUS,AonEnumUtils.getByte( mod390.getStatus()  ))
			.set(FS_MODEL390.SECURITY_LEVEL,AonEnumUtils.getByte(mod390.isConfidential()))
			.set(FS_MODEL390.COMPLEMENTARY, (byte) 0)
			.set(FS_MODEL390.REPLACEMENT,AonEnumUtils.getByte(mod390.isReplacement()))
			.set(FS_MODEL390.SECURITY_LEVEL,AonEnumUtils.getByte(mod390.isConfidential()))
			.set(FS_MODEL390.DOCUMENT, mod390.getDocument())
			.set(FS_MODEL390.NAME, mod390.getName())
			.set(FS_MODEL390.COMMENTS, mod390.getComments())
			.set(FS_MODEL390.RECEIPT, mod390.getReceipt())
			.set(FS_MODEL390.REPLACED_RECEIPT, mod390.getReplacedReceipt())
			.set(FS_MODEL390.MODEL, getXMLModel(mod390))
			.where(FS_MODEL390.ID.equal(mod390.getId())).execute();
		return mod390;
	}

	private static void validate(AONContext ctx, Mod3902018 mod390) {
		if (mod390.isReplacement()) {
			// Se comprueba que exista la declaración sustituida.
			if (!ctx.getDslContext().selectOne()
					.from(FS_MODEL390)
					.where(FS_MODEL390.YEAR.equal(mod390.getYear())
					.and(FS_MODEL390.ENTERPRISE.equal(mod390.getEnterprise()))
//					.and(FS_MODEL390.RECEIPT.equal(mod390.getReplacedReceipt()))
					)
					.fetch()
					.stream()
					.findFirst()
					.isPresent()) 
				throw new AonCoreException(
						AonError.FISCAL_NO_REPLACED_DECLARATION.getMessage());
			

			// Se comprueba que no exista una declaración sustitutiva.
			if (ctx.getDslContext().selectOne()
					.from(FS_MODEL390)
					.where(FS_MODEL390.YEAR.equal(mod390.getYear())
					.and(FS_MODEL390.ENTERPRISE.equal(mod390.getEnterprise()))
					.and(FS_MODEL390.REPLACEMENT.equal( ONE_BYTE ))					
					.and(FS_MODEL390.REPLACED_RECEIPT.equal(mod390.getReplacedReceipt())))
					.and(	(mod390.getId()!=null)
							?FS_MODEL390.ID.ne(mod390.getId())
							:FS_MODEL390.ID.eq(FS_MODEL390.ID)
							)
					.fetch()
					.stream()
					.findFirst()
					.isPresent()) 
					throw new AonCoreException(AonError.FISCAL_DECLARATION_ALREADY_REPLACED.getMessage());
		} else {
			// Se comprueba que no exista ya una declaración.
			if (ctx.getDslContext().selectOne()
				.from(FS_MODEL390)
				.where(FS_MODEL390.YEAR.equal(mod390.getYear())
				.and(FS_MODEL390.ENTERPRISE.equal(mod390.getEnterprise()))
				.and(FS_MODEL390.REPLACEMENT.equal(ZERO_BYTE)))
				.and(	(mod390.getId()!=null)
						?FS_MODEL390.ID.ne(mod390.getId())
						:FS_MODEL390.ID.eq(FS_MODEL390.ID)
						)
				.fetch()
				.stream()
				.findFirst()
				.isPresent()) 
				throw new AonCoreException(
						AonError.FISCAL_DECLARATION_ALREADY_EXISTS.getMessage());
		}
	}

	public static void delete(AONContext ctx, Mod3902018 mod390) {
		ctx.checkWrite();
		ctx.getDslContext().delete(FS_MODEL390)
				.where(FS_MODEL390.ID.equal(mod390.getId())).execute();
	}
	
	private static LinkedList<Mod390Detail> getDetails(AONContext ctx, Mod3902018 mod390 ) {
		EnumMap<Mod3902018DetailKey, Mod390Detail> map = new EnumMap<Mod3902018DetailKey, Mod390Detail>(Mod3902018DetailKey.class); 
		Mod390Detail det = null;
		for (Mod3902018DetailKey key : Mod3902018DetailKey.values() ) {
			if (key.accept(mod390.getYear())) {
				det = new Mod390Detail();
				det.setKey(key);
				det.setPercent(key.getPercent());
				map.put(key, det);
			}
		}
		
		final MutableDouble mutProrrata = new MutableDouble();
		ctx.getDslContext()
				.select(FS_VAT.PRORATA)
				.from(FS_VAT)
				.where(FS_VAT.DOMAIN.equal(mod390.getDomain()))
				.and(FS_VAT.YEAR.equal(mod390.getYear()))
				.fetch()
				.stream()
				.forEach( record -> {
						mutProrrata.setValue( record.getValue(FS_VAT.PRORATA) );
					}
				);
		double prorrata = AonMathUtils.round( mutProrrata.doubleValue() / 100);
		boolean mustApplyProrrata = (prorrata != AonMathUtils.round(0.00));		
		
		Date firstDay = AonDateUtils.getYearFirstDay(mod390.getYear());
		Date lastDay = AonDateUtils.getYearLastDay(mod390.getYear());
		VATDAO.getVatBreakdown(ctx, firstDay, lastDay, mod390)
		.forEach(vc -> {
			Mod3902018DetailKey[] keys = DetailKey.getKeys(vc);			
			if (keys != null) {
				for (Mod3902018DetailKey key : keys) {
					Mod390Detail detail = map.get(key);
					if (detail == null) {
						detail = new Mod390Detail();
						map.put(key, detail );
					}
					detail.setKey(key);
					detail.setPercent(vc.getPercentage());
					double q = key.isSurcharge()?vc.getSurchargeQuota():vc.getQuota();
					if (key.isProrrataEnabled()) {
						q = vc.getDeductibleQuota();
						if ( mustApplyProrrata ) {
							q = AonMathUtils.round(q * prorrata);
						}
					}
					detail.setQuota( AonMathUtils.round(detail.getQuota()  + q));
					detail.setTaxableBase( AonMathUtils.round( detail.getTaxableBase() + vc.getBase()));
				}
			}
			
		});
		// Cálculo de la Regularizacion por aplicacion del porcentaje definitivo de prorrata 
		Record1<BigDecimal> record = ctx.getDslContext()
			.select(DSL.sum(FS_VAT_DETAIL.QUOTA))
			.from(FS_VAT_DETAIL)
			.join(FS_VAT).on(FS_VAT.ID.equal(FS_VAT_DETAIL.FS_VAT))
			.where(FS_VAT.DOMAIN.equal(mod390.getDomain()))
			.and(FS_VAT.YEAR.equal(mod390.getYear()))
			.and(FS_VAT_DETAIL.VAT_KEY.equal("RP"))
			.fetchOne();
	 // Mod303Key.CT_C44
		if (record != null) {
			BigDecimal quota = record.getValue(DSL.sum(FS_VAT_DETAIL.QUOTA));
			if (quota != null) {
				Mod390Detail detail = map.get(Mod3902018DetailKey.C0522);
				if (detail == null) {
					detail = new Mod390Detail();
					map.put(Mod3902018DetailKey.C0522, detail );
				}
				detail.setKey(Mod3902018DetailKey.C0522);
				detail.setQuota(quota.doubleValue());
			}
		}
		return new LinkedList<Mod390Detail>(map.values());
	}
	
	private static Mod3902018 fillGeneralRegimeData(AONContext ctx, Mod3902018 mod390) {
		try {
			EnumMap<Mod3902018DetailKey, Mod390Detail> map = new EnumMap<Mod3902018DetailKey, Mod390Detail>(Mod3902018DetailKey.class);
			Mod390Detail det = null;
			LinkedList<Mod390Detail> details = null;
			details = getDetails(ctx, mod390);
			for (Mod390Detail detail : details) {
				map.put(detail.getKey(), detail); 
			}
			if (!mod390.isSimplifiedRegime()) {
				mod390.setBox99(map.get(Mod3902018DetailKey.C0099).getTaxableBase());
				mod390.setBox100(0.0);
			} else {
				mod390.setBox99(0.0);
				mod390.setBox100(map.get(Mod3902018DetailKey.C0099).getTaxableBase());
			}
			mod390.setBox101(map.get(Mod3902018DetailKey.C0101).getTaxableBase());
			mod390.setBox102(map.get(Mod3902018DetailKey.C0102).getTaxableBase());
			mod390.setBox103(map.get(Mod3902018DetailKey.C0103).getTaxableBase());
			mod390.setBox104(map.get(Mod3902018DetailKey.C0104).getTaxableBase());
			mod390.setBox105(map.get(Mod3902018DetailKey.C0105).getTaxableBase());
			mod390.setBox106(map.get(Mod3902018DetailKey.C0106).getTaxableBase());
			mod390.setBox107(map.get(Mod3902018DetailKey.C0107).getTaxableBase());
			mod390.setBox108(map.get(Mod3902018DetailKey.C0108).getTaxableBase());
			mod390.setBox110(map.get(Mod3902018DetailKey.C0110).getTaxableBase());
			mod390.setBox112(map.get(Mod3902018DetailKey.C0112).getTaxableBase());
			mod390.setBox227(map.get(Mod3902018DetailKey.C0227).getTaxableBase());
			mod390.setBox228(map.get(Mod3902018DetailKey.C0228).getTaxableBase());
			mod390.setBox654(map.get(Mod3902018DetailKey.C0654).getTaxableBase());
			mod390.setBox655(map.get(Mod3902018DetailKey.C0654).getQuota());
			mod390.setAccrualRegime( ( map.get(Mod3902018DetailKey.C0654).getTaxableBase()  != 0 || map.get(Mod3902018DetailKey.C0654).getQuota() != 0 ) );
			mod390.setBox656(map.get(Mod3902018DetailKey.C0656).getTaxableBase());
			mod390.setBox657(map.get(Mod3902018DetailKey.C0656).getQuota());
			mod390.setAccrualRegimeTarget((map.get(Mod3902018DetailKey.C0656).getTaxableBase()  != 0 || map.get(Mod3902018DetailKey.C0656).getQuota() != 0 ));

			// ----------------------------------------------------------------------------
			// En el caso de que el declarante este acogido al regimen simplificado
			// Se utiliza toda la funcionalidad del regimen general (lectura de facturas)
			// para rellenar los campos anteriores , del 99 al 657. Sin embargo la 
			// página 5 del modelo, o sea la del regimen general debe ir vacia, por lo 
			// que se incializa el mapa.
			if (mod390.isSimplifiedRegime()) {
				map = new EnumMap<Mod3902018DetailKey, Mod390Detail>(Mod3902018DetailKey.class);
				for (Mod3902018DetailKey key : Mod3902018DetailKey.values() ) {
					if (key.accept(mod390.getYear())) {
						det = new Mod390Detail();
						det.setKey(key);
						det.setPercent(key.getPercent());
						map.put(key, det);
					}
				}
			}
			// ----------------------------------------------------------------------------
			
			mod390.setGeneralRegime(map);
			return mod390;
		} catch (Throwable t) {
			t.printStackTrace();
			throw t; 
		}
	}

	static class SimplifedRegimeContext {
		private Mod303Key key;
		private String description;
		private double amount;

		public SimplifedRegimeContext(Mod303Key key,String description,double amount) {
			this.key = key;
			this.description = description;
			this.amount = amount;
		}
		public Mod303Key getKey() {
			return key;
		}
		public void setKey(Mod303Key key) {
			this.key = key;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}
	}
	
	@FunctionalInterface
	static interface ISimplifiedRegimeFiller {
		boolean fill(SimplifedRegimeContext src,Mod3902018 mod390);
	}

	static enum SimplifiedRegimeFiller {
		CAG1     (Mod303Key.CAG1    , 
				(src,mod390) -> {
					String code = AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-"));					
					mod390.getFarmerRegime1().setCodigo(code);
					return true;
								}), 
		CAG1_V1  (Mod303Key.CAG1_V1 , (src,mod390) -> {mod390.getFarmerRegime1().setIncomes(src.getAmount());return true;}),
		CAG1_V2  (Mod303Key.CAG1_V2 , (src,mod390) -> {mod390.getFarmerRegime1().setQuotaIndex( AonMathUtils.round(src.getAmount() / 10000 ,5));return true;}),
		CAG1_V3  (Mod303Key.CAG1_V3 , (src,mod390) -> {mod390.getFarmerRegime1().setAccrualQuota(src.getAmount());return true;}),
		CAG1_V6  (Mod303Key.CAG1_V6 , (src,mod390) -> {mod390.getFarmerRegime1().setInputQuotas(src.getAmount());return true;}),
		CAG1_V7  (Mod303Key.CAG1_V7 , (src,mod390) -> {
				mod390.getFarmerRegime1().setQuota(src.getAmount());
				mod390.setBox75( AonMathUtils.round(mod390.getBox75() + src.getAmount()));
				return true;
													}),
		CAG2     (Mod303Key.CAG2    , 
				(src,mod390) -> {
					String code = AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-"));					
					mod390.getFarmerRegime2().setCodigo(code);
					return true;
								}),
		CAG2_V1  (Mod303Key.CAG2_V1 , (src,mod390) -> {mod390.getFarmerRegime2().setIncomes(src.getAmount());return true;}),
		CAG2_V2  (Mod303Key.CAG2_V2 , (src,mod390) -> {mod390.getFarmerRegime2().setQuotaIndex(AonMathUtils.round(src.getAmount() / 10000 ,5));return true;}),
		CAG2_V3  (Mod303Key.CAG2_V3 , (src,mod390) -> {mod390.getFarmerRegime2().setAccrualQuota(src.getAmount());return true;}),
		CAG2_V6  (Mod303Key.CAG2_V6 , (src,mod390) -> {mod390.getFarmerRegime2().setInputQuotas(src.getAmount());return true;}),
		CAG2_V7  (Mod303Key.CAG2_V7 , (src,mod390) -> {
				mod390.getFarmerRegime2().setQuota(src.getAmount());
				mod390.setBox75( AonMathUtils.round(mod390.getBox75() + src.getAmount()));
				return true;
												}),
		CAC1     (Mod303Key.CAC1    , 
				(src,mod390) -> {
					String code = AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-"));					
					mod390.getSimpRegime1().setEpigrafe(code);
					return true;
								}),
		CAC1_M1U (Mod303Key.CAC1_M1U, (src,mod390) -> {mod390.getSimpRegime1().setUnit1(src.getAmount());return true;}),
		CAC1_M1I (Mod303Key.CAC1_M1I, (src,mod390) -> {mod390.getSimpRegime1().setAmount1(src.getAmount());return true;}),
		CAC1_M2U (Mod303Key.CAC1_M2U, (src,mod390) -> {mod390.getSimpRegime1().setUnit2(src.getAmount());return true;}),
		CAC1_M2I (Mod303Key.CAC1_M2I, (src,mod390) -> {mod390.getSimpRegime1().setAmount2(src.getAmount());return true;}),
		CAC1_M3U (Mod303Key.CAC1_M3U, (src,mod390) -> {mod390.getSimpRegime1().setUnit3(src.getAmount());return true;}),
		CAC1_M3I (Mod303Key.CAC1_M3I, (src,mod390) -> {mod390.getSimpRegime1().setAmount3(src.getAmount());return true;}),
		CAC1_M4U (Mod303Key.CAC1_M4U, (src,mod390) -> {mod390.getSimpRegime1().setUnit4(src.getAmount());return true;}),
		CAC1_M4I (Mod303Key.CAC1_M4I, (src,mod390) -> {mod390.getSimpRegime1().setAmount4(src.getAmount());return true;}),
		CAC1_M5U (Mod303Key.CAC1_M5U, (src,mod390) -> {mod390.getSimpRegime1().setUnit5(src.getAmount());return true;}),
		CAC1_M5I (Mod303Key.CAC1_M5I, (src,mod390) -> {mod390.getSimpRegime1().setAmount5(src.getAmount());return true;}),
		CAC1_M6U (Mod303Key.CAC1_M6U, (src,mod390) -> {mod390.getSimpRegime1().setUnit6(src.getAmount());return true;}),
		CAC1_M6I (Mod303Key.CAC1_M6I, (src,mod390) -> {mod390.getSimpRegime1().setAmount6(src.getAmount());return true;}),
		CAC1_M7U (Mod303Key.CAC1_M7U, (src,mod390) -> {mod390.getSimpRegime1().setUnit7(src.getAmount());return true;}),
		CAC1_M7I (Mod303Key.CAC1_M7I, (src,mod390) -> {mod390.getSimpRegime1().setAmount7(src.getAmount());return true;}),
		CAC1_C   (Mod303Key.CAC1_C  , (src,mod390) -> {
				mod390.getSimpRegime1().setBoxC(src.getAmount());
				mod390.setBox74( AonMathUtils.round(mod390.getBox74() + src.getAmount()));
				return true;
													}),
		CAC1_D   (Mod303Key.CAC1_D  , null),
		CAC1_Z   (Mod303Key.CAC1_Z  , null),
		CAC1_ZA  (Mod303Key.CAC1_ZA , null),
		CAC1_ZD  (Mod303Key.CAC1_ZD , null),
		CAC1_E   (Mod303Key.CAC1_E  , null),
		CAC1_F   (Mod303Key.CAC1_F  , null),
		CAC1_G0  (Mod303Key.CAC1_G0 , (src,mod390) -> {mod390.getSimpRegime1().setBoxD(
				AonMathUtils.round(mod390.getSimpRegime1().getBoxD() + src.getAmount()));return true;}),
		CAC1_G   (Mod303Key.CAC1_G  , (src,mod390) -> {mod390.getSimpRegime1().setBoxD(
				AonMathUtils.round(mod390.getSimpRegime1().getBoxD() + src.getAmount()));return true;}),
		CAC1_H   (Mod303Key.CAC1_H  , (src,mod390) -> {mod390.getSimpRegime1().setBoxE(src.getAmount());return true;}),
		CAC1_HA  (Mod303Key.CAC1_HA , null),
		CAC1_HD  (Mod303Key.CAC1_HD , null),
		CAC1_HT  (Mod303Key.CAC1_HT , null),
		CAC1_I   (Mod303Key.CAC1_I  , (src,mod390) -> {mod390.getSimpRegime1().setBoxF(src.getAmount());return true;}),
		CAC1_J   (Mod303Key.CAC1_J  , (src,mod390) -> {mod390.getSimpRegime1().setBoxG(src.getAmount());return true;}),
		CAC1_K   (Mod303Key.CAC1_K  , null),
		CAC1_L   (Mod303Key.CAC1_L  , (src,mod390) -> {mod390.getSimpRegime1().setBoxI(src.getAmount());return true;}),
		CAC1_M   (Mod303Key.CAC1_M  , (src,mod390) -> {mod390.getSimpRegime1().setBoxJ(src.getAmount());return true;}),
		CAC2     (Mod303Key.CAC2    , 
				(src,mod390) -> {
					String code = AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-"));					
					mod390.getSimpRegime2().setEpigrafe(code);
					return true;
								}),
		CAC2_M1U (Mod303Key.CAC2_M1U, (src,mod390) -> {mod390.getSimpRegime2().setUnit1(src.getAmount());return true;}),
		CAC2_M1I (Mod303Key.CAC2_M1I, (src,mod390) -> {mod390.getSimpRegime2().setAmount1(src.getAmount());return true;}),
		CAC2_M2U (Mod303Key.CAC2_M2U, (src,mod390) -> {mod390.getSimpRegime2().setUnit2(src.getAmount());return true;}),
		CAC2_M2I (Mod303Key.CAC2_M2I, (src,mod390) -> {mod390.getSimpRegime2().setAmount2(src.getAmount());return true;}),
		CAC2_M3U (Mod303Key.CAC2_M3U, (src,mod390) -> {mod390.getSimpRegime2().setUnit3(src.getAmount());return true;}),
		CAC2_M3I (Mod303Key.CAC2_M3I, (src,mod390) -> {mod390.getSimpRegime2().setAmount3(src.getAmount());return true;}),
		CAC2_M4U (Mod303Key.CAC2_M4U, (src,mod390) -> {mod390.getSimpRegime2().setUnit4(src.getAmount());return true;}),
		CAC2_M4I (Mod303Key.CAC2_M4I, (src,mod390) -> {mod390.getSimpRegime2().setAmount4(src.getAmount());return true;}),
		CAC2_M5U (Mod303Key.CAC2_M5U, (src,mod390) -> {mod390.getSimpRegime2().setUnit5(src.getAmount());return true;}),
		CAC2_M5I (Mod303Key.CAC2_M5I, (src,mod390) -> {mod390.getSimpRegime2().setAmount5(src.getAmount());return true;}),
		CAC2_M6U (Mod303Key.CAC2_M6U, (src,mod390) -> {mod390.getSimpRegime2().setUnit6(src.getAmount());return true;}),
		CAC2_M6I (Mod303Key.CAC2_M6I, (src,mod390) -> {mod390.getSimpRegime2().setAmount6(src.getAmount());return true;}),
		CAC2_M7U (Mod303Key.CAC2_M7U, (src,mod390) -> {mod390.getSimpRegime2().setUnit7(src.getAmount());return true;}),
		CAC2_M7I (Mod303Key.CAC2_M7I, (src,mod390) -> {mod390.getSimpRegime2().setAmount7(src.getAmount());return true;}),
		CAC2_C   (Mod303Key.CAC2_C  , (src,mod390) ->	{
				mod390.getSimpRegime2().setBoxC(src.getAmount());
				mod390.setBox74( AonMathUtils.round(mod390.getBox74() + src.getAmount()));
				return true;
														}),
		CAC2_D   (Mod303Key.CAC2_D  , null),
		CAC2_Z   (Mod303Key.CAC2_Z  , null),
		CAC2_ZA  (Mod303Key.CAC2_ZA , null),
		CAC2_ZD  (Mod303Key.CAC2_ZD , null),
		CAC2_E   (Mod303Key.CAC2_E  , null),
		CAC2_F   (Mod303Key.CAC2_F  , null),
		
		CAC2_G0  (Mod303Key.CAC2_G0 , (src,mod390) -> {mod390.getSimpRegime2().setBoxD(
				AonMathUtils.round(mod390.getSimpRegime2().getBoxD() + src.getAmount()));return true;}),
		CAC2_G   (Mod303Key.CAC2_G  , (src,mod390) -> {mod390.getSimpRegime2().setBoxD(
				AonMathUtils.round(mod390.getSimpRegime2().getBoxD() + src.getAmount()));return true;}),
		CAC2_H   (Mod303Key.CAC2_H  , (src,mod390) -> {mod390.getSimpRegime2().setBoxE(src.getAmount());return true;}),
		CAC2_HA  (Mod303Key.CAC2_HA , null),
		CAC2_HD  (Mod303Key.CAC2_HD , null),
		CAC2_HT  (Mod303Key.CAC2_HT , null),
		CAC2_I   (Mod303Key.CAC2_I  , (src,mod390) -> {mod390.getSimpRegime2().setBoxF(src.getAmount());return true;}),
		CAC2_J	 (Mod303Key.CAC2_J  , (src,mod390) -> {mod390.getSimpRegime2().setBoxG(src.getAmount());return true;}),
		CAC2_K   (Mod303Key.CAC2_K  , null),
		CAC2_L   (Mod303Key.CAC2_L  , (src,mod390) -> {mod390.getSimpRegime2().setBoxI(src.getAmount());return true;}),
		CAC2_M   (Mod303Key.CAC2_M  , (src,mod390) -> {mod390.getSimpRegime2().setBoxJ(src.getAmount());return true;}),
		C51      (Mod303Key.C51     , (src,mod390) -> {mod390.setBox76(src.getAmount());return true;}),
		C52      (Mod303Key.C52     , (src,mod390) -> {mod390.setBox78(src.getAmount());return true;}),
		C53      (Mod303Key.C53     , (src,mod390) -> {mod390.setBox77(src.getAmount());return true;}),
		C54      (Mod303Key.C54     , (src,mod390) -> {mod390.setBox79(src.getAmount());return true;}),
		C55      (Mod303Key.C55     , (src,mod390) -> {mod390.setBox80(src.getAmount());return true;}),
		C56      (Mod303Key.C56     , (src,mod390) -> {mod390.setBox81(src.getAmount());return true;}),
		C57      (Mod303Key.C57     , (src,mod390) -> {mod390.setBox82(src.getAmount());return true;}),
		C58      (Mod303Key.C58     , (src,mod390) -> {mod390.setBox83(src.getAmount());return true;}),
		C71      (Mod303Key.C71     , (src,mod390) -> {
										mod390.setBox97((src.getAmount() < 0)?AonMathUtils.round(mod390.getBox95() + src.getAmount()):0.0);
										return true;
													}),
		PBK      (Mod303Key.PBK     , (src,mod390) -> {mod390.setBox98( AonMathUtils.round(mod390.getBox95() + src.getAmount()) );return true;}),
		;
		
		private Mod303Key key;
		private ISimplifiedRegimeFiller filler;
			
		private SimplifiedRegimeFiller(Mod303Key key,ISimplifiedRegimeFiller filler) {
			this.key = key;
			this.filler = filler;
		}
		public Mod303Key getKey() {
			return key;
		}
		public ISimplifiedRegimeFiller getFiller() {
			return filler;
		}
		public static void fill(SimplifedRegimeContext src,Mod3902018 mod390) {
			for (SimplifiedRegimeFiller filler : SimplifiedRegimeFiller.values()) {
				if (filler.getKey() ==  src.getKey() && filler.getFiller() != null) {
					filler.getFiller().fill(src, mod390);
				}
			}
		}
		
	}	
	
	private static Mod3902018 fillSimplifedRegimeData(AONContext ctx, Mod3902018 mod390) {
		mod390.setSimpRegime1(new SimpliedRegimeActivity());
		mod390.setSimpRegime2(new SimpliedRegimeActivity());
		mod390.setFarmerRegime1(new FarmerRegimeActivity());
		mod390.setFarmerRegime2(new FarmerRegimeActivity());
		mod390.setFarmerRegime3(new FarmerRegimeActivity());
		mod390.setFarmerRegime4(new FarmerRegimeActivity());
		mod390.setFarmerRegime5(new FarmerRegimeActivity());
		ctx.getDslContext().select(FS_MODEL_DETAIL.TYPE
				, FS_MODEL_DETAIL.DESCRIPTION
				, FS_MODEL_DETAIL.AMOUNT)
			.from(FS_MODEL)
			.join(FS_MODEL_DETAIL).on(FS_MODEL.ID.equal(FS_MODEL_DETAIL.FS_MODEL))
			.where(FS_MODEL.DOMAIN.equal(mod390.getDomain()))
			.and(FS_MODEL.YEAR.equal(mod390.getYear()))
			.and(FS_MODEL.PERIOD.equal( (byte) Period.T4.ordinal()))
			.and(FS_MODEL.MODEL.equal("303"))
		.fetch()
		.stream()
		.forEach( record -> {
			String description = record.getValue( FS_MODEL_DETAIL.DESCRIPTION );
			String type = record.getValue( FS_MODEL_DETAIL.TYPE );
			double amount = record.getValue( FS_MODEL_DETAIL.AMOUNT );
			Mod303Key key = Mod303Key.getKeyWithValue(type);
			SimplifedRegimeContext src = new SimplifedRegimeContext(key, description, amount);
			SimplifiedRegimeFiller.fill(src,mod390);
			}
		);
		return mod390;
	}
	private static void fillGeneralDeclarationResults(AONContext ctx, Mod3902018 mod390) {
		Mod303DAO.getMod303s(ctx, mod390.getDomain())
			.filter(m303 -> m303.getYear() == mod390.getYear())
			.filter(m303 -> m303.getAdministration() == mod390.getAdministration())
			.forEach(m303 -> {
				Period period = m303.getPeriod();
				if (m303.getDeclarationType() == FiscalModelDeclarationType.BANK
				 || m303.getDeclarationType() == FiscalModelDeclarationType.DEPOSIT
				 || m303.getDeclarationType() == FiscalModelDeclarationType.DEPOSIT_CCT) {
					mod390.setBox95( AonMathUtils.round(mod390.getBox95() + m303.getResult()));
				} 
				if ( m303.isEnrolledInDevolutionRegistry()) {
					mod390.setTaxRefund(true);
				}
				if (m303.getDeclarationType() == FiscalModelDeclarationType.PAYBACK
				 || m303.getDeclarationType() == FiscalModelDeclarationType.PAYBACK_CCT) {
					if (m303.isEnrolledInDevolutionRegistry()) {
						mod390.setBox96( AonMathUtils.round(mod390.getBox96() + (m303.getResult() * (-1))));
					}
					if (period == Period.M12 || period == Period.T4) {
						mod390.setBox98( m303.getResult() * (-1));	
					} 
				}
				if (m303.getDeclarationType() == FiscalModelDeclarationType.COMPENSATE
				 && (period == Period.M12 || period == Period.T4)) {
					mod390.setBox97(  AonMathUtils.round( m303.getResult() * (-1) ));
				}
			});
		
	}
	private static void fillSimplifiedDeclarationResults(AONContext ctx, Mod3902018 mod390) {
		mod390.setBox97( AonMathUtils.round(mod390.getBox97() * -1));
		mod390.setBox98( AonMathUtils.round(mod390.getBox98() * -1));
		if (mod390.getBox98() > 0) {
			mod390.setBox97( 0 ); 	
		}
		Record1<BigDecimal> record = ctx.getDslContext()
			.select(DSL.sum(FS_MODEL_DETAIL.AMOUNT))
			.from(FS_MODEL)
			.join(FS_MODEL_DETAIL).on(FS_MODEL.ID.equal(FS_MODEL_DETAIL.FS_MODEL))
			.where(FS_MODEL.DOMAIN.equal(mod390.getDomain()))
			.and(FS_MODEL.YEAR.equal(mod390.getYear()))
			.and(FS_MODEL_DETAIL.AMOUNT.greaterThan(0.0))
			.and(FS_MODEL.MODEL.equal("303"))
			.and(FS_MODEL_DETAIL.TYPE.equal("303-71"))
			.fetchOne();
		if (record != null) {
			BigDecimal quota = record.getValue(DSL.sum(FS_MODEL_DETAIL.AMOUNT)); 
			if (quota != null) {
				mod390.setBox95( quota.doubleValue());
			}
		}
	}

	public static Mod3902018 changeStatus(AONContext ctx, Mod3902018 mod390, FiscalStatus newStatus) {
		try {
			ctx.checkWrite();
			if (mod390.getId() != null) {
				mod390.setStatus(newStatus);
				ctx.getDslContext().update(FS_MODEL390)
					.set(FS_MODEL390.STATUS,AonEnumUtils.getByte( mod390.getStatus()))
					.where(FS_MODEL390.ID.equal(mod390.getId()))
					.execute();
			}
			return mod390;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Throwable t) {
			throw new AonCoreException(t.getMessage());
		}
	}

}
