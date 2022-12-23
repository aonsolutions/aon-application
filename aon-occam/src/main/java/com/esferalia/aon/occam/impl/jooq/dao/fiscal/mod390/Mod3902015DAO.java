package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod390;

import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModel390.FS_MODEL390;
import static com.esferalia.aon.jooq.tables.FsModelDetail.FS_MODEL_DETAIL;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

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

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.FsModel390Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.mod390.FarmerRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902015.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902015.SimpliedRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902015DetailKey;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303DAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2015.AEATIVA2015;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2015.AEATIVA2015toMod390;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2015.Mod390toAEATIVA2015;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod3902015DAO {

	private static byte ZERO_BYTE = 0;
	private static byte ONE_BYTE = 1;
	
	@FunctionalInterface
	public static interface IMod390DetailKey {
		boolean accept(VatContext vc);
	}

	public static enum DetailKey implements Serializable {
		
		  K00_04 (Mod3902015DetailKey.K00_04, (vc -> (vc.isNationalSales() && !vc.isRectification() && vc.getPercentage() ==  4)))
		 ,K00_10 (Mod3902015DetailKey.K00_10, (vc -> (vc.isNationalSales() && !vc.isRectification() && vc.getPercentage() == 10)))
		 ,K00_21 (Mod3902015DetailKey.K00_21, (vc -> (vc.isNationalSales() && !vc.isRectification() && vc.getPercentage() == 21)))
		 ,K01_04 (Mod3902015DetailKey.K01_04, null)
		 ,K01_10 (Mod3902015DetailKey.K01_10, null)
		 ,K01_21 (Mod3902015DetailKey.K01_21, null)
		 ,K02_04 (Mod3902015DetailKey.K02_04, null)
		 ,K02_10 (Mod3902015DetailKey.K02_10, null)
		 ,K02_21 (Mod3902015DetailKey.K02_21, null)
		 ,K03_21 (Mod3902015DetailKey.K03_21, null)
		 ,K04_04 (Mod3902015DetailKey.K04_04, (vc -> (vc.isIntracommunityPurchase() && vc.getPercentage() ==  4)))
		 ,K04_10 (Mod3902015DetailKey.K04_10, (vc -> (vc.isIntracommunityPurchase() && vc.getPercentage() ==  10)))
		 ,K04_21 (Mod3902015DetailKey.K04_21, (vc -> (vc.isIntracommunityPurchase() && vc.getPercentage() ==  21)))
	
		 ,K05_04 (Mod3902015DetailKey.K05_04, (vc -> (vc.isIntracommunityExpenses() && vc.getPercentage() ==  4)))
		 ,K05_10 (Mod3902015DetailKey.K05_10, (vc -> (vc.isIntracommunityExpenses() && vc.getPercentage() == 10)))
		 ,K05_21 (Mod3902015DetailKey.K05_21, (vc -> (vc.isIntracommunityExpenses() && vc.getPercentage() == 21)))
		 ,K06	 (Mod3902015DetailKey.K06	, (vc -> ( vc.isOtherISPPurchase() 
				 								|| vc.isOtherISPExpenses() 
				 								|| vc.isCanCeuMelExpenses() 
				 								|| vc.isExtracommunityExpenses())))
		 
		 ,K07	 (Mod3902015DetailKey.K07	, (vc -> (vc.isNationalSales() && vc.isRectification())))
		 ,K08	 (Mod3902015DetailKey.K08	, null)
		 ,K09	 (Mod3902015DetailKey.K09	, null)
		 ,K10_05 (Mod3902015DetailKey.K10_05, (vc -> (vc.isSurcharge() && !vc.isRectification() && vc.isNationalSales() && vc.getSurchargePercent() == 0.5)))
		 ,K10_14 (Mod3902015DetailKey.K10_14, (vc -> (vc.isSurcharge() && !vc.isRectification() && vc.isNationalSales() && vc.getSurchargePercent() == 1.4)))
		 ,K10_52 (Mod3902015DetailKey.K10_52, (vc -> (vc.isSurcharge() && !vc.isRectification() && vc.isNationalSales() && vc.getSurchargePercent() == 5.2)))
		 ,K10_175(Mod3902015DetailKey.K10_175,(vc -> (vc.isSurcharge() && !vc.isRectification() && vc.isNationalSales() && vc.getSurchargePercent() == 1.75)))
		 ,K11	 (Mod3902015DetailKey.K11	, (vc -> (vc.isSurcharge() &&  vc.isRectification() && vc.isNationalSales()))) 
		 ,K12	 (Mod3902015DetailKey.K12	, null)
		 ,K13	 (Mod3902015DetailKey.K13	, null)
		 
		 ,K14_04 (Mod3902015DetailKey.K14_04, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 4))) 
		 ,K14_07 (Mod3902015DetailKey.K14_07, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 7))) 
		 ,K14_08 (Mod3902015DetailKey.K14_08, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 8))) 
		 ,K14_10 (Mod3902015DetailKey.K14_10, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 10))) 
		 ,K14_16 (Mod3902015DetailKey.K14_16, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 16)))
		 ,K14_18 (Mod3902015DetailKey.K14_18, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 18)))
		 ,K14_21 (Mod3902015DetailKey.K14_21, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 21))) 
		 ,K15	 (Mod3902015DetailKey.K15	, null)
		 
		 ,K16_04 (Mod3902015DetailKey.K16_04, null)
		 ,K16_07 (Mod3902015DetailKey.K16_07, null)
		 ,K16_08 (Mod3902015DetailKey.K16_08, null)
		 ,K16_10 (Mod3902015DetailKey.K16_10, null)
		 ,K16_16 (Mod3902015DetailKey.K16_16, null)
		 ,K16_18 (Mod3902015DetailKey.K16_18, null)
		 ,K16_21 (Mod3902015DetailKey.K16_21, null)
		 ,K17	 (Mod3902015DetailKey.K17	, null)
		 
		 ,K18_04 (Mod3902015DetailKey.K18_04, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  4)))
		 ,K18_07 (Mod3902015DetailKey.K18_07, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  7)))
		 ,K18_08 (Mod3902015DetailKey.K18_08, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  8)))
		 ,K18_10 (Mod3902015DetailKey.K18_10, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 10))) 
		 ,K18_16 (Mod3902015DetailKey.K18_16, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 16))) 
		 ,K18_18 (Mod3902015DetailKey.K18_18, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 18))) 
		 ,K18_21 (Mod3902015DetailKey.K18_21, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 21)))
		 ,K19	 (Mod3902015DetailKey.K19	, null)
		 
		 ,K20_04 (Mod3902015DetailKey.K20_04, null)
		 ,K20_07 (Mod3902015DetailKey.K20_07, null)
		 ,K20_08 (Mod3902015DetailKey.K20_08, null)
		 ,K20_10 (Mod3902015DetailKey.K20_10, null)
		 ,K20_16 (Mod3902015DetailKey.K20_16, null)
		 ,K20_18 (Mod3902015DetailKey.K20_18, null)
		 ,K20_21 (Mod3902015DetailKey.K20_21, null)
		 ,K21	 (Mod3902015DetailKey.K21	, null)
		 
		 ,K22_04 (Mod3902015DetailKey.K22_04, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 4)))
		 ,K22_07 (Mod3902015DetailKey.K22_07, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 7)))
		 ,K22_08 (Mod3902015DetailKey.K22_08, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 8)))
		 ,K22_10 (Mod3902015DetailKey.K22_10, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 10)))
		 ,K22_16 (Mod3902015DetailKey.K22_16, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 16)))
		 ,K22_18 (Mod3902015DetailKey.K22_18, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 18)))
		 ,K22_21 (Mod3902015DetailKey.K22_21, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 21)))
		 ,K23	 (Mod3902015DetailKey.K23	, null)
		 
		 ,K24_04 (Mod3902015DetailKey.K24_04, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 4)))
		 ,K24_07 (Mod3902015DetailKey.K24_07, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 7)))
		 ,K24_08 (Mod3902015DetailKey.K24_08, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 8)))
		 ,K24_10 (Mod3902015DetailKey.K24_10, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 10)))
		 ,K24_16 (Mod3902015DetailKey.K24_16, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 16)))
		 ,K24_18 (Mod3902015DetailKey.K24_18, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 18)))
		 ,K24_21 (Mod3902015DetailKey.K24_21, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 21)))
		 ,K25	 (Mod3902015DetailKey.K25	, null)
		 
		 ,K26_04 (Mod3902015DetailKey.K26_04, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  4)))
		 ,K26_07 (Mod3902015DetailKey.K26_07, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  7)))
		 ,K26_08 (Mod3902015DetailKey.K26_08, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  8)))
		 ,K26_10 (Mod3902015DetailKey.K26_10, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  10)))
		 ,K26_16 (Mod3902015DetailKey.K26_16, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  16)))
		 ,K26_18 (Mod3902015DetailKey.K26_18, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  18)))
		 ,K26_21 (Mod3902015DetailKey.K26_21, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  21)))
		 ,K27	 (Mod3902015DetailKey.K27	, null)
		 
		 ,K28_04 (Mod3902015DetailKey.K28_04, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  4)))
		 ,K28_07 (Mod3902015DetailKey.K28_07, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  7))) 
		 ,K28_08 (Mod3902015DetailKey.K28_08, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  8))) 
		 ,K28_10 (Mod3902015DetailKey.K28_10, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  10))) 
		 ,K28_16 (Mod3902015DetailKey.K28_16, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  16))) 
		 ,K28_18 (Mod3902015DetailKey.K28_18, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  18))) 
		 ,K28_21 (Mod3902015DetailKey.K28_21, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  21))) 
		 ,K29	 (Mod3902015DetailKey.K29   , null)
		 
		 ,K30_04 (Mod3902015DetailKey.K30_04, (vc -> (vc.isIntracommunityExpenses() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  4)))
		 ,K30_07 (Mod3902015DetailKey.K30_07, (vc -> (vc.isIntracommunityExpenses() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  7)))
		 ,K30_08 (Mod3902015DetailKey.K30_08, (vc -> (vc.isIntracommunityExpenses() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  8)))
		 ,K30_10 (Mod3902015DetailKey.K30_10, (vc -> (vc.isIntracommunityExpenses() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  10)))
		 ,K30_16 (Mod3902015DetailKey.K30_16, (vc -> (vc.isIntracommunityExpenses() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  16)))
		 ,K30_18 (Mod3902015DetailKey.K30_18, (vc -> (vc.isIntracommunityExpenses() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  18)))
		 ,K30_21 (Mod3902015DetailKey.K30_21, (vc -> (vc.isIntracommunityExpenses() && !vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() ==  21)))
		 ,K31	 (Mod3902015DetailKey.K31   , null)
		 
		 ,K32	 (Mod3902015DetailKey.K32   , (vc -> ((vc.isPurchase() || vc.isExpenses()) && vc.isFarmerRegime())))
		 
		 ,K33	 (Mod3902015DetailKey.K33, null)
		 ,K34	 (Mod3902015DetailKey.K34, null)
		 ,K35	 (Mod3902015DetailKey.K35, null)
		 ,K36	 (Mod3902015DetailKey.K36, null)
		 ,K37	 (Mod3902015DetailKey.K37, null)
		 
		 ,B099	 (Mod3902015DetailKey.B099, (vc -> (vc.isNationalSales() && !vc.isRectification())))
		 ,B653	 (Mod3902015DetailKey.B653, (vc -> (vc.isSales() && vc.isVatAccrualRegime() )))
		 ,B103	 (Mod3902015DetailKey.B103, (vc -> (vc.isIntracommunitySales() && !vc.isWithoutRightDeductionType())))
		 ,B104	 (Mod3902015DetailKey.B104, (vc -> (vc.isSales() && !vc.isWithoutRightDeductionType() && (vc.isExtracommunity() || vc.isCanCeuMel()) )))
		 ,B105	 (Mod3902015DetailKey.B105, (vc -> (vc.isSales() && !vc.isNational() && vc.isWithoutRightDeductionType())))
		 ,B110	 (Mod3902015DetailKey.B110, (vc -> (vc.isSales() && !vc.isWithoutRightDeductionType() && vc.isOtherISP())))
		 ,B112	 (Mod3902015DetailKey.B112, null)
		 ,B100	 (Mod3902015DetailKey.B100, null)
		 ,B101	 (Mod3902015DetailKey.B101, null)
		 ,B102	 (Mod3902015DetailKey.B102, (vc -> (vc.isNationalSales() && vc.isSurcharge())))
		 ,B227	 (Mod3902015DetailKey.B227, null)
		 ,B228	 (Mod3902015DetailKey.B228, null)
		 ,B106	 (Mod3902015DetailKey.B106, null)
		 ,B107	 (Mod3902015DetailKey.B107, (vc -> (vc.isNationalSales() && vc.isInvestment())))
		 ,B108	 (Mod3902015DetailKey.B108, null)
		 ;
		 
		private Mod3902015DetailKey key;
		private IMod390DetailKey accept;
			
		private DetailKey(Mod3902015DetailKey key, IMod390DetailKey accept) {
			this.key = key;
			this.accept = accept;
		}

		public Mod3902015DetailKey getKey() {
			return key;
		}
		public boolean accept(VatContext vc) {
			return (accept==null)?false:accept.accept(vc);
		}
		
		public static Mod3902015DetailKey[] getKeys(VatContext vc) {
			List<Mod3902015DetailKey> list = new LinkedList<Mod3902015DetailKey>();
			for (DetailKey key : DetailKey.values()) {
				if (key.accept(vc)) {
					list.add(key.getKey()); 
				}
			}
			return list.size()==0?null:list.toArray(new Mod3902015DetailKey[list.size()]);
		}
	}

	public static Mod3902015 create(AONContext ctx, Mod390 model) {
		Mod3902015 mod390 =  new Mod3902015();
		mod390.setId(model.getId());
		mod390.setDomain(model.getDomain());
		mod390.setDomainName(model.getDomainName());
		mod390.setEnterprise(model.getEnterprise());
		mod390.setEnterpriseName(model.getEnterpriseName());
		mod390.setYear(model.getYear());
		mod390.setAdministration(model.getAdministration());
		mod390.setStatus(model.getStatus());
		mod390.setOldStyle(model.isOldStyle());
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
		if (year == 2016 || year == 2017) {
			LinkedList<Mod390> mod390s = Mod390DAO.getByDomain(ctx, ctx.getDomainId());
			for (Mod390 m390 : mod390s) {
				if ( (year == 2016 && m390.getYear() == 2015)
				  || (year == 2017 && m390.getYear() == 2016)){
					Mod3902015 mod3902015 = getById(ctx, m390.getId());
					mod390.setMainActivity(mod3902015.getMainActivity());
					mod390.setActivity1(mod3902015.getActivity1());
					mod390.setActivity2(mod3902015.getActivity2());
					mod390.setActivity3(mod3902015.getActivity3());
					mod390.setActivity4(mod3902015.getActivity4());
					mod390.setActivity5(mod3902015.getActivity5());
					mod390.setAddress(mod3902015.getAddress());
					mod390.setLegalRepr1(mod3902015.getLegalRepr1());
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

	public static Mod3902015 getMod3902015(AONContext ctx, Mod390 m390) {
		if (m390.getId() == null) {
			return create(ctx, m390);
		}
		return getById(ctx, m390.getId());
	}

	public static Mod3902015 getById(AONContext ctx, int id) {
		ctx.checkRead();
		Mod3902015 mod390 = new Mod3902015();
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

	private static void populate(FsModel390Record record, Mod3902015 mod390)  {
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
		StringReader reader = new StringReader(record.getValue(FS_MODEL390.MODEL));
		try {
			if (mod390.getYear() == 2015 || mod390.getYear() == 2016 || mod390.getYear() == 2017) {
				JAXBContext context = JAXBContext.newInstance(AEATIVA2015.class);
				Unmarshaller um = context.createUnmarshaller();
				AEATIVA2015 iva = (AEATIVA2015) um.unmarshal(reader);
				AEATIVA2015toMod390.populate(mod390, iva);
			} else {
				throw new IllegalArgumentException("Ejercicio incorrecto.");
			}
		} catch (ParseException | JAXBException e1) {
			e1.printStackTrace();
			throw new AonCoreException("XML PROBLEM",e1);
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


	public static Mod3902015 save(AONContext ctx, Mod3902015 mod390)  {
		if (mod390.getId() == null) {
			return insert(ctx, mod390);
		} else {
			return update(ctx, mod390);
		}
	}
	
	private static String getXMLModel( Mod3902015 mod390 ) {
		try {
			if (mod390.getYear() == 2015 || mod390.getYear() == 2016 || mod390.getYear() == 2017) {
				AEATIVA2015 iva = Mod390toAEATIVA2015.getAEATIVA2015(mod390);
				StringWriter writer = new StringWriter();
				JAXBContext context = JAXBContext.newInstance(AEATIVA2015.class);
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
	

	private static Mod3902015 insert(AONContext ctx, Mod3902015 mod390) {
		validate(ctx, mod390);
		FsModel390Record record = ctx.getDslContext()
			.insertInto(FS_MODEL390)
			.set(FS_MODEL390.DOMAIN, mod390.getDomain())
			.set(FS_MODEL390.ENTERPRISE, mod390.getEnterprise())
			.set(FS_MODEL390.YEAR, mod390.getYear())
			.set(FS_MODEL390.ADMINISTRATION,mod390.getAdministration().value())
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
	
	private static Mod3902015 update(AONContext ctx, Mod3902015 mod390) {
		validate(ctx, mod390);
		ctx.getDslContext()
			.update(FS_MODEL390)
			.set(FS_MODEL390.YEAR, mod390.getYear())
			.set(FS_MODEL390.ADMINISTRATION,mod390.getAdministration().value())
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

	private static void validate(AONContext ctx, Mod3902015 mod390) {
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

	public static void delete(AONContext ctx, Mod3902015 mod390) {
		ctx.checkWrite();
		ctx.getDslContext().delete(FS_MODEL390)
				.where(FS_MODEL390.ID.equal(mod390.getId())).execute();
	}
	
	private static LinkedList<Mod390Detail> getDetails(AONContext ctx, Mod3902015 mod390 ) {
		EnumMap<Mod3902015DetailKey, Mod390Detail> map = new EnumMap<Mod3902015DetailKey, Mod390Detail>(Mod3902015DetailKey.class); 
		Mod390Detail det = null;
		for (Mod3902015DetailKey key : Mod3902015DetailKey.values() ) {
			if (key.accept(mod390.getYear())) {
				det = new Mod390Detail();
				det.setKey(key);
				det.setPercent(key.getPercent());
				map.put(key, det);
			}
		}
		
		final MutableDouble mutProrrata = new MutableDouble();
		double prorrata = AonMathUtils.round( mutProrrata.doubleValue() / 100);
		boolean mustApplyProrrata = (prorrata != AonMathUtils.round(0.00));		
		
		VATDAO.getVatBreakdown(ctx, mod390)
		.forEach(vc -> {
			Mod3902015DetailKey[] keys = DetailKey.getKeys(vc);			
			if (keys != null) {
				for (Mod3902015DetailKey key : keys) {
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
		return new LinkedList<Mod390Detail>(map.values());
	}
	
	private static LinkedList<Mod390Detail> getDetailsOldStyle(AONContext ctx, Mod3902015 mod390 ) {
		Date firstDay = AonDateUtils.getYearFirstDay(mod390.getYear());
		Date lastDay = AonDateUtils.getYearLastDay(mod390.getYear());
		
		Field<BigDecimal> sumBase = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE).as(INVOICE_DETAIL.TAXABLE_BASE.getName());
		
		Field<Double> invoiceTaxQuota = DSL.round( (INVOICE_DETAIL.TAXABLE_BASE.mul(INVOICE_TAX.PERCENTAGE)).div(100), 2);
		Field<Double> invoiceTaxQuotaDecode = DSL.decode()
				.when(INVOICE_TAX.QUOTA.notEqual(0.0),INVOICE_TAX.QUOTA)
				.when(INVOICE_TAX.QUOTA.equal(0.0), invoiceTaxQuota);
		Field<BigDecimal> sumQuotaOp = DSL.sum(invoiceTaxQuotaDecode);		

		Field<Double> invoiceSurchargeQuota = DSL.round( (INVOICE_DETAIL.TAXABLE_BASE.mul(INVOICE_TAX.SURCHARGE)).div(100), 4);
		Field<BigDecimal> sumSurchargeQuotaOp = DSL.sum(DSL.decode()
				.when(INVOICE_TAX.SURCHARGE_QUOTA.notEqual(0.0),INVOICE_TAX.SURCHARGE_QUOTA)
				.when(INVOICE_TAX.SURCHARGE_QUOTA.equal(0.0), invoiceSurchargeQuota));		
		EnumMap<Mod3902015DetailKey, Mod390Detail> map = new EnumMap<Mod3902015DetailKey, Mod390Detail>(Mod3902015DetailKey.class); 
		Mod390Detail det = null;
		for (Mod3902015DetailKey key : Mod3902015DetailKey.values() ) {
			if (key.accept(mod390.getYear())) {
				det = new Mod390Detail();
				det.setKey(key);
				det.setPercent(key.getPercent());
				map.put(key, det);
			}
		}
		
		final MutableDouble mutProrrata = new MutableDouble();
		double prorrata = AonMathUtils.round( mutProrrata.doubleValue() / 100);
		boolean mustApplyProrrata = (prorrata != AonMathUtils.round(0.00));		
		
		// Se buscan las facturas que tengan IVA y no sean de criterio de caja. 
		ctx.getDslContext().select(INVOICE.TYPE
				,INVOICE.RECTIFICATION_TYPE
				,INVOICE.SERVICE
				,INVOICE.TRANSACTION
				,INVOICE.INVESTMENT
				,INVOICE.WITHHOLDING_FARMER
				,INVOICE_TAX.PERCENTAGE
				,INVOICE_TAX.SURCHARGE
				,INVOICE_TAX.VAT_DEDUCTION_TYPE
				,sumBase
				,sumQuotaOp
				,sumSurchargeQuotaOp)
				.from(INVOICE_TAX)
				.join(INVOICE_DETAIL).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
				.join(INVOICE).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.where(INVOICE_TAX.DOMAIN.equal(mod390.getDomain()))
				.and(INVOICE_TAX.TAX_TYPE.equal((byte) 1))
				.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
				.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal((byte) 0))	// No Criterio de Caja.
				.groupBy(INVOICE.TYPE
						,INVOICE.RECTIFICATION_TYPE
						,INVOICE.SERVICE
						,INVOICE.TRANSACTION
						,INVOICE.INVESTMENT
						,INVOICE.WITHHOLDING_FARMER
						,INVOICE_TAX.PERCENTAGE
						,INVOICE_TAX.SURCHARGE
						,INVOICE_TAX.VAT_DEDUCTION_TYPE)
				.fetch()
				.stream()
				.forEach(
						record -> {
							VatContext vc = new VatContext()
									.setInvoiceType(InvoiceType.values()[record.getValue(INVOICE.TYPE)])
									.setRectificationType(RectificationType.values()[record.getValue(INVOICE.RECTIFICATION_TYPE)])
									.setService(record.getValue(INVOICE.SERVICE) == 1)
									.setTransaction(InvoiceTransactionType.values()[record.getValue(INVOICE.TRANSACTION)])
									.setInvestment(record.getValue(INVOICE.INVESTMENT) == 1)
									.setVatAccrualRegime(false)
									.setFarmerRegime(record.getValue(INVOICE.WITHHOLDING_FARMER) == 1)
									.setSurchargePercent(record.getValue(INVOICE_TAX.SURCHARGE).doubleValue())
									.setSurcharge(AonMathUtils.round(record.getValue(INVOICE_TAX.SURCHARGE).doubleValue()) > 0)
									.setPercentage(record.getValue(INVOICE_TAX.PERCENTAGE))
									.setVatDeductionType(
											record.getValue(INVOICE_TAX.VAT_DEDUCTION_TYPE) == null
											?VatDeductionType.WITH_RIGHT
											:VatDeductionType.safeValueOf(record.getValue(INVOICE_TAX.VAT_DEDUCTION_TYPE)));
							double percentage = record.getValue(INVOICE_TAX.PERCENTAGE);
							double taxableBase = record.getValue(sumBase).doubleValue();
							double quota = record.getValue(sumQuotaOp).doubleValue();
							double surchargeQuota = record.getValue(sumSurchargeQuotaOp).doubleValue();
							Mod3902015DetailKey[] keys = DetailKey.getKeys(vc);			
							if (keys != null) {
								for (Mod3902015DetailKey key : keys) {
									Mod390Detail detail = map.get(key);
									if (detail == null) {
										detail = new Mod390Detail();
										map.put(key, detail );
									}
									detail.setKey(key);
									detail.setPercent(percentage);
									double q = key.isSurcharge()?surchargeQuota:quota;
									if ( mustApplyProrrata &&  key.isProrrataEnabled() ) {
										q = AonMathUtils.round(q * prorrata);
									}
									detail.setQuota( AonMathUtils.round(detail.getQuota()  + q));
									detail.setTaxableBase( AonMathUtils.round( detail.getTaxableBase() + taxableBase));
								}
							}
						}
				);
		
		Field<Double> financeTrackingAmountDecode0 = DSL.decode()
				.when(FINANCE_TRACKING.TYPE.equal((byte) 1), FINANCE_TRACKING.AMOUNT)
				.otherwise(FINANCE_TRACKING.AMOUNT.mul(-1));
		Field<BigDecimal> financeTrackingAmountDecode = DSL.sum(financeTrackingAmountDecode0);
		
		ctx.getDslContext().select(INVOICE_TAX.ID
				,INVOICE.TYPE
				,INVOICE.RECTIFICATION_TYPE
				,INVOICE.SERVICE
				,INVOICE.TRANSACTION
				,INVOICE.INVESTMENT
				,INVOICE.WITHHOLDING_FARMER
				,INVOICE_TAX.PERCENTAGE
				,INVOICE_TAX.SURCHARGE
				,INVOICE_TAX.VAT_DEDUCTION_TYPE
				,INVOICE.TAXABLE_BASE
				,INVOICE.VAT_QUOTA
				,INVOICE.RETENTION_QUOTA
				,INVOICE.TOTAL
				,INVOICE_TAX.BASE
				,invoiceTaxQuotaDecode
				,sumSurchargeQuotaOp
				,financeTrackingAmountDecode)
				.from(FINANCE_TRACKING)
				.join(FINANCE).on(FINANCE_TRACKING.FINANCE.equal(FINANCE.ID))
				.join(INVOICE).on(FINANCE.INVOICE.equal(INVOICE.ID))
				.join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
				.join(INVOICE_TAX).on(INVOICE_DETAIL.ID.equal(INVOICE_TAX.INVOICE_DETAIL))
				.where(FINANCE_TRACKING.DOMAIN.equal(mod390.getDomain()))
				.and(FINANCE_TRACKING.TRACKING_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
				.and(FINANCE_TRACKING.TYPE.in((byte)1, (byte)2))
				.and(INVOICE_TAX.TAX_TYPE.equal((byte) 1))
				// Lo anterior al 2014 no interesa. Ese dia empezo la aplicación del regimen de caja.
				.and(INVOICE.TAX_DATE.greaterOrEqual( AonDateUtils.toSql(AonDateUtils.getYearFirstDay(2014))))  
				.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal((byte) 1))	//  Criterio de Caja.
				.groupBy(INVOICE_TAX.ID
						,INVOICE_TAX.PERCENTAGE
						,INVOICE_TAX.SURCHARGE
						,INVOICE_TAX.VAT_DEDUCTION_TYPE)
				.fetch()
				.stream()
				.forEach(
						record -> {
							VatContext vc = new VatContext();
							vc.setInvoiceType(InvoiceType.values()[record.getValue(INVOICE.TYPE)]);
							vc.setRectificationType(RectificationType.values()[record.getValue(INVOICE.RECTIFICATION_TYPE)]);
							vc.setService(record.getValue(INVOICE.SERVICE) == 1);
							vc.setTransaction(InvoiceTransactionType.values()[record.getValue(INVOICE.TRANSACTION)]);
							vc.setInvestment(record.getValue(INVOICE.INVESTMENT) == 1);
							vc.setVatAccrualRegime(true);
							vc.setFarmerRegime(record.getValue(INVOICE.WITHHOLDING_FARMER) == 1);
							
							double surchargePercent = record.getValue(INVOICE_TAX.SURCHARGE).doubleValue();
							vc.setSurchargePercent(surchargePercent);
							boolean surcharge = (surchargePercent > 0);
							vc.setSurcharge(surcharge);
							
							double taxableBase = record.getValue(INVOICE_TAX.BASE);
							double quota = record.getValue(invoiceTaxQuotaDecode);
							double surchargeQuota = record.getValue(sumSurchargeQuotaOp).doubleValue();
							double percentage = record.getValue(INVOICE_TAX.PERCENTAGE); 
							vc.setPercentage(percentage);
							double invoiceBase = record.getValue(INVOICE.TAXABLE_BASE);
							double invoiceVat  = record.getValue(INVOICE.VAT_QUOTA);
							double invoiceRetention = record.getValue(INVOICE.RETENTION_QUOTA);
							double invoiceTotal = record.getValue(INVOICE.TOTAL);
							double financeAmount = record.getValue(financeTrackingAmountDecode).doubleValue();
							
							invoiceTotal = AonMathUtils.round(invoiceBase + invoiceVat - invoiceRetention);
							taxableBase = AonMathUtils.round(financeAmount * taxableBase / invoiceTotal,4);
							quota = AonMathUtils.round(taxableBase * percentage / 100);
							vc.setVatDeductionType(VatDeductionType.values()[record.getValue(INVOICE_TAX.VAT_DEDUCTION_TYPE)]);

							Mod3902015DetailKey[] keys = DetailKey.getKeys(vc);			
							if (keys != null) {
								for (Mod3902015DetailKey key : keys) {
									Mod390Detail detail = map.get(key);
									if (detail == null) {
										detail = new Mod390Detail();
										map.put(key, detail );
									}
									detail.setKey(key);
									detail.setPercent(percentage);
									double q = key.isSurcharge()?surchargeQuota:quota;
									if ( mustApplyProrrata &&  key.isProrrataEnabled() ) {
										q = AonMathUtils.round(q * prorrata);
									}
									detail.setQuota( AonMathUtils.round(detail.getQuota()  + q));
									detail.setTaxableBase( AonMathUtils.round( detail.getTaxableBase() + taxableBase));
								}
							}
						}
				);

			ctx.getDslContext().select(INVOICE.TYPE
					,DSL.sum(INVOICE.TAXABLE_BASE)
					,DSL.sum(INVOICE.VAT_QUOTA))
					.from(INVOICE)
					.where(INVOICE.DOMAIN.equal(mod390.getDomain()))
					.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
					.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal((byte) 1))	// Criterio de Caja.
					.groupBy(INVOICE.TYPE)
					.having(DSL.sum(INVOICE.VAT_QUOTA).greaterThan( new BigDecimal(0)) )
					.fetch()
					.stream()
					.forEach(
							record -> {
								InvoiceType type = InvoiceType.values()[record.getValue(INVOICE.TYPE)];
								Mod3902015DetailKey key = type==InvoiceType.SALES?Mod3902015DetailKey.B654:Mod3902015DetailKey.B656;
								Mod390Detail detail = map.get(key);
								if (detail == null) {
									detail = new Mod390Detail();
									map.put(key, detail );
								}
								detail.setKey(key);
								double tb = AonMathUtils.round( record.getValue(DSL.sum(INVOICE.TAXABLE_BASE)).doubleValue());
								detail.setTaxableBase(tb);
								double quota = AonMathUtils.round(record.getValue(DSL.sum(INVOICE.VAT_QUOTA)).doubleValue());
								detail.setQuota(quota);
								}
					);
			// **********************************************************************************************
			// **********************************************************************************************
			Field<BigDecimal> sumFinanceAmount = DSL.sum( FINANCE.AMOUNT);
			
			ctx.getDslContext().select(INVOICE_TAX.ID
					,INVOICE.TYPE
					,INVOICE.RECTIFICATION_TYPE
					,INVOICE.SERVICE
					,INVOICE.TRANSACTION
					,INVOICE.INVESTMENT
					,INVOICE.WITHHOLDING_FARMER
					,INVOICE_TAX.PERCENTAGE
					,INVOICE_TAX.SURCHARGE
					,INVOICE_TAX.VAT_DEDUCTION_TYPE
					,INVOICE.TAXABLE_BASE
					,INVOICE.VAT_QUOTA
					,INVOICE.RETENTION_QUOTA
					,INVOICE.TOTAL
					,INVOICE_TAX.BASE
					,invoiceTaxQuotaDecode
					,sumSurchargeQuotaOp
					,sumFinanceAmount)
					.from(FINANCE)
					.join(INVOICE).on(FINANCE.INVOICE.equal(INVOICE.ID))
					.join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
					.join(INVOICE_TAX).on(INVOICE_DETAIL.ID.equal(INVOICE_TAX.INVOICE_DETAIL))
					.where(FINANCE.DOMAIN.equal(mod390.getDomain()))
					.and(FINANCE.STATUS.eq((byte)0)
					.and(INVOICE_TAX.TAX_TYPE.equal((byte) 1))
					.and(INVOICE.TAX_DATE.between(
							 AonDateUtils.toSql(AonDateUtils.getYearFirstDay((mod390.getYear() - 1)))
							,AonDateUtils.toSql(AonDateUtils.getYearLastDay((mod390.getYear() - 1))))))
					.and(INVOICE.VAT_ACCRUAL_PAYMENT.equal((byte) 1))	// Criterio de Caja.
					.groupBy(INVOICE_TAX.ID
							,INVOICE_TAX.PERCENTAGE
							,INVOICE_TAX.SURCHARGE
							,INVOICE_TAX.VAT_DEDUCTION_TYPE)
					.fetch()
					.stream()
					.forEach(
							record -> {
								VatContext vc = new VatContext();
								vc.setInvoiceType(InvoiceType.values()[record.getValue(INVOICE.TYPE)]);
								vc.setRectificationType(RectificationType.values()[record.getValue(INVOICE.RECTIFICATION_TYPE)]);
								vc.setService(record.getValue(INVOICE.SERVICE) == 1);
								vc.setTransaction(InvoiceTransactionType.values()[record.getValue(INVOICE.TRANSACTION)]);
								vc.setInvestment(record.getValue(INVOICE.INVESTMENT) == 1);
								vc.setVatAccrualRegime(true);
								vc.setFarmerRegime(record.getValue(INVOICE.WITHHOLDING_FARMER) == 1);
								
								double surchargePercent = record.getValue(INVOICE_TAX.SURCHARGE).doubleValue();
								vc.setSurchargePercent(surchargePercent);
								boolean surcharge = (surchargePercent > 0);
								vc.setSurcharge(surcharge);
								
								double taxableBase = record.getValue(INVOICE_TAX.BASE);
								double quota = record.getValue(invoiceTaxQuotaDecode);
								double surchargeQuota = record.getValue(sumSurchargeQuotaOp).doubleValue();
								double percentage = record.getValue(INVOICE_TAX.PERCENTAGE); 
								vc.setPercentage(percentage);
								double invoiceBase = record.getValue(INVOICE.TAXABLE_BASE);
								double invoiceVat  = record.getValue(INVOICE.VAT_QUOTA);
								double invoiceRetention = record.getValue(INVOICE.RETENTION_QUOTA);
								double invoiceTotal = record.getValue(INVOICE.TOTAL);
								double financeAmount = record.getValue(sumFinanceAmount).doubleValue();
								
								invoiceTotal = AonMathUtils.round(invoiceBase + invoiceVat - invoiceRetention);
								taxableBase = AonMathUtils.round(financeAmount * taxableBase / invoiceTotal,4);
								quota = AonMathUtils.round(taxableBase * percentage / 100);
								vc.setVatDeductionType(VatDeductionType.values()[record.getValue(INVOICE_TAX.VAT_DEDUCTION_TYPE)]);

								Mod3902015DetailKey[] keys = DetailKey.getKeys(vc);			
								if (keys != null) {
									for (Mod3902015DetailKey key : keys) {
										Mod390Detail detail = map.get(key);
										if (detail == null) {
											detail = new Mod390Detail();
											map.put(key, detail );
										}
										detail.setKey(key);
										detail.setPercent(percentage);
										double q = key.isSurcharge()?surchargeQuota:quota;
										if ( mustApplyProrrata &&  key.isProrrataEnabled() ) {
											q = AonMathUtils.round(q * prorrata);
										}
										detail.setQuota( AonMathUtils.round(detail.getQuota()  + q));
										detail.setTaxableBase( AonMathUtils.round( detail.getTaxableBase() + taxableBase));
										
									}
								}
							}
					);
		return new LinkedList<Mod390Detail>(map.values());
	}

	private static Mod3902015 fillGeneralRegimeData(AONContext ctx, Mod3902015 mod390) {
		try {
			EnumMap<Mod3902015DetailKey, Mod390Detail> map = new EnumMap<Mod3902015DetailKey, Mod390Detail>(Mod3902015DetailKey.class);
			Mod390Detail det = null;
			LinkedList<Mod390Detail> details = null;
			if (mod390.isOldStyle()) {
				details = getDetailsOldStyle(ctx, mod390);
			} else {
				details = getDetails(ctx, mod390);
			}
			
			for (Mod390Detail detail : details) {
				map.put(detail.getKey(), detail); 
			}
			if (!mod390.isSimplifiedRegime()) {
				mod390.setBox99(map.get(Mod3902015DetailKey.B099).getTaxableBase());
				mod390.setBox100(0.0);
			} else {
				mod390.setBox99(0.0);
				mod390.setBox100(map.get(Mod3902015DetailKey.B099).getTaxableBase());
			}
			mod390.setBox101(map.get(Mod3902015DetailKey.B101).getTaxableBase());
			mod390.setBox102(map.get(Mod3902015DetailKey.B102).getTaxableBase());
			mod390.setBox103(map.get(Mod3902015DetailKey.B103).getTaxableBase());
			mod390.setBox104(map.get(Mod3902015DetailKey.B104).getTaxableBase());
			mod390.setBox105(map.get(Mod3902015DetailKey.B105).getTaxableBase());
			mod390.setBox106(map.get(Mod3902015DetailKey.B106).getTaxableBase());
			mod390.setBox107(map.get(Mod3902015DetailKey.B107).getTaxableBase());
			mod390.setBox108(map.get(Mod3902015DetailKey.B108).getTaxableBase());
			mod390.setBox110(map.get(Mod3902015DetailKey.B110).getTaxableBase());
			mod390.setBox112(map.get(Mod3902015DetailKey.B112).getTaxableBase());
			mod390.setBox227(map.get(Mod3902015DetailKey.B227).getTaxableBase());
			mod390.setBox228(map.get(Mod3902015DetailKey.B228).getTaxableBase());
			mod390.setBox654(map.get(Mod3902015DetailKey.B654).getTaxableBase());
			mod390.setBox655(map.get(Mod3902015DetailKey.B654).getQuota());
			mod390.setAccrualRegime( ( map.get(Mod3902015DetailKey.B654).getTaxableBase()  != 0 || map.get(Mod3902015DetailKey.B654).getQuota() != 0 ) );
			mod390.setBox656(map.get(Mod3902015DetailKey.B656).getTaxableBase());
			mod390.setBox657(map.get(Mod3902015DetailKey.B656).getQuota());
			mod390.setAccrualRegimeTarget((map.get(Mod3902015DetailKey.B656).getTaxableBase()  != 0 || map.get(Mod3902015DetailKey.B656).getQuota() != 0 ));

			// ----------------------------------------------------------------------------
			// En el caso de que el declarante este acogido al regimen simplificado
			// Se utiliza toda la funcionalidad del regimen general (lectura de facturas)
			// para rellenar los campos anteriores , del 99 al 657. Sin embargo la 
			// página 5 del modelo, o sea la del regimen general debe ir vacia, por lo 
			// que se incializa el mapa.
			if (mod390.isSimplifiedRegime()) {
				map = new EnumMap<Mod3902015DetailKey, Mod390Detail>(Mod3902015DetailKey.class);
				for (Mod3902015DetailKey key : Mod3902015DetailKey.values() ) {
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

	
	private static Mod3902015 fillSimplifedRegimeData(AONContext ctx, Mod3902015 mod390) {
		mod390.setSimpRegime1(new SimpliedRegimeActivity());
		mod390.setSimpRegime2(new SimpliedRegimeActivity());
		mod390.setFarmerRegime1(new FarmerRegimeActivity());
		mod390.setFarmerRegime2(new FarmerRegimeActivity());
		mod390.setFarmerRegime3(new FarmerRegimeActivity());
		mod390.setFarmerRegime4(new FarmerRegimeActivity());
		mod390.setFarmerRegime5(new FarmerRegimeActivity());
		return mod390;
	}
	private static void fillGeneralDeclarationResults(AONContext ctx, Mod3902015 mod390) {
		Mod303DAO.getMod303s(ctx, mod390.getDomain())
			.filter(m303 -> m303.getYear() == mod390.getYear())
			.filter(m303 -> m303.getAdministration() == mod390.getAdministration())
			.forEach(m303 -> {
				Period period = m303.getPeriod();
				if (m303.getDeclarationResultType() == FiscalModelDeclarationType.BANK
				 || m303.getDeclarationResultType() == FiscalModelDeclarationType.DEPOSIT
				 || m303.getDeclarationResultType() == FiscalModelDeclarationType.DEPOSIT_CCT) {
					mod390.setBox95( AonMathUtils.round(mod390.getBox95() + m303.getDeclarationResult()));
				} 
				if ( m303.isEnrolledInDevolutionRegistry()) {
					mod390.setTaxRefund(true);
				}
				if (m303.getDeclarationResultType() == FiscalModelDeclarationType.PAYBACK
				 || m303.getDeclarationResultType() == FiscalModelDeclarationType.PAYBACK_CCT) {
					if (m303.isEnrolledInDevolutionRegistry()) {
						mod390.setBox96( AonMathUtils.round(mod390.getBox96() + (m303.getDeclarationResult() * (-1))));
					}
					if (period == Period.M12 || period == Period.T4) {
						mod390.setBox98( m303.getDeclarationResult() * (-1));	
					} 
				}
				if (m303.getDeclarationResultType() == FiscalModelDeclarationType.COMPENSATE
				 && (period == Period.M12 || period == Period.T4)) {
					mod390.setBox97(  AonMathUtils.round( m303.getDeclarationResult() * (-1) ));
				}
			});
		
	}
	
	private static void fillSimplifiedDeclarationResults(AONContext ctx, Mod3902015 mod390) {
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

	public static Mod3902015 changeStatus(AONContext ctx, Mod3902015 mod390, FiscalStatus newStatus) {
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
