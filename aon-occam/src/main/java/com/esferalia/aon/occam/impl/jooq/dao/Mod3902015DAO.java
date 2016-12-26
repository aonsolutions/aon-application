package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModel390.FS_MODEL390;
import static com.esferalia.aon.jooq.tables.FsModelDetail.FS_MODEL_DETAIL;
import static com.esferalia.aon.jooq.tables.FsVat.FS_VAT;
import static com.esferalia.aon.jooq.tables.FsVatDeclaration.FS_VAT_DECLARATION;
import static com.esferalia.aon.jooq.tables.FsVatDetail.FS_VAT_DETAIL;
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
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.FsModel390Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015.FarmerRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015.SimpliedRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015DetailKey;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2015.AEATIVA2015;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2015.AEATIVA2015toMod390;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2015.Mod390toAEATIVA2015;
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

	public static class VatContext {
		
		private InvoiceType invoiceType;
		private RectificationType rectificationType;
		private boolean service;
		private InvoiceTransactionType transaction;
		private boolean investment;
		private boolean accrualRegime;
		private boolean surcharge;
		private double percentage;
		private double surchargePercent;
		private VatDeductionType vatDeductionType;
		private boolean farmerRegime;
		
		public InvoiceType getInvoiceType() {
			return invoiceType;
		}
		public VatContext setInvoiceType(InvoiceType invoiceType) {
			this.invoiceType = invoiceType;
			return this;
		}
		public RectificationType getRectificationType() {
			return rectificationType;
		}
		public VatContext setRectificationType(RectificationType rectificationType) {
			this.rectificationType = rectificationType;
			return this;
		}
		public boolean isService() {
			return service;
		}
		public VatContext setService(boolean service) {
			this.service = service;
			return this;
		}
		public InvoiceTransactionType getTransaction() {
			return transaction;
		}
		public VatContext setTransaction(InvoiceTransactionType transaction) {
			this.transaction = transaction;
			return this;
		}
		public boolean isInvestment() {
			return investment;
		}
		public VatContext setInvestment(boolean investment) {
			this.investment = investment;
			return this;
		}
		public boolean isSurcharge() {
			return surcharge;
		}
		public VatContext setSurcharge(boolean surcharge) {
			this.surcharge = surcharge;
			return this;
		}
		public double getSurchargePercent() {
			return surchargePercent;
		}
		public VatContext setSurchargePercent(double surchargePercent) {
			this.surchargePercent = surchargePercent;
			return this;
		}
		public boolean isAccrualRegime() {
			return accrualRegime;
		}
		public VatContext setAccrualRegime(boolean accrualRegime) {
			this.accrualRegime = accrualRegime;
			return this;
		}
		public double getPercentage() {
			return percentage;
		}
		public VatContext setPercentage(double percentage) {
			this.percentage = percentage;
			return this;
		}
		public VatDeductionType getVatDeductionType() {
			return vatDeductionType;
		}
		public VatContext setVatDeductionType(VatDeductionType vatDeductionType) {
			this.vatDeductionType = vatDeductionType;
			return this;
		}
		public boolean isFarmerRegime() {
			return farmerRegime;
		}
		public VatContext setFarmerRegime(boolean farmerRegime) {
			this.farmerRegime = farmerRegime;
			return this;
		}
		public boolean isRectification() {
			return (rectificationType == RectificationType.NORMAL_RECTIFIER);
		}
		public boolean isSales() {
			return (invoiceType == InvoiceType.SALES);
		}
		public boolean isPurchase() {
			return (invoiceType == InvoiceType.PURCHASE);
		}
		public boolean isExpenses() {
			return (invoiceType == InvoiceType.EXPENSES);
		}
		public boolean isNational() {
			return (transaction == InvoiceTransactionType.NATIONAL);
		}
		public boolean isIntracommunity() {
			return (transaction == InvoiceTransactionType.INTRACOMMUNITY);
		}
		public boolean isExtracommunity() {
			return (transaction == InvoiceTransactionType.EXTRACOMMUNITY);
		}
		public boolean isCanCeuMel() {
			return (transaction == InvoiceTransactionType.CAN_CEU_MEL);
		}
		public boolean isOtherISP() {
			return (transaction == InvoiceTransactionType.OTHER_ISP);
		}
		public boolean isNationalSales() {
			return isNational() && isSales();
		}
		public boolean isIntracommunitySales() {
		return isIntracommunity() && isSales();
	}
		public boolean isNationalPurchase() {
			return isNational() && isPurchase();
		}
		public boolean isOtherISPPurchase() {
			return isOtherISP() && isPurchase();
		}
		public boolean isIntracommunityPurchase(){
			return isIntracommunity() && isPurchase();
		} 
		public boolean isExtracommunityPurchase(){
			return isExtracommunity() && isPurchase();
		} 
		public boolean isCanCeuMelPurchase(){
			return isCanCeuMel() && isPurchase();
		}
		public boolean isNationalExpenses() {
			return isNational() && isExpenses();
		}
		public boolean isOtherISPExpenses() {
			return isOtherISP() && isExpenses();
		}
		public boolean isExtracommunityExpenses(){
			return isExtracommunity() && isExpenses();
		} 
		public boolean isCanCeuMelExpenses(){
			return isCanCeuMel() && isExpenses();
		}
		public boolean isIntracommunityExpenses(){
			return isIntracommunity() && isExpenses();
		} 
		public boolean isWithoutRightDeductionType() {
			return  vatDeductionType == VatDeductionType.WITHOUT_RIGHT;
		}
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
		 ,B653	 (Mod3902015DetailKey.B653, (vc -> (vc.isSales() && vc.isAccrualRegime() )))
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

	public static Mod3902015 initialize(AONContext ctx, int year) {
		FiscalParameters params = AppParamDAO.getFiscalParameters(ctx);
		Mod3902015 mod390 = new Mod3902015()
			.setEnterprise(params.getCompany())
			.setDomain(ctx.getDomainId())
			.setDocument(params.getDocument())
			.setEnterpriseName(params.getName())
			.setYear( year );
		
		if (year == 2016) {
			LinkedList<Mod390> mod390s = Mod390DAO.getByDomain(ctx, ctx.getDomainId());
			for (Mod390 m390 : mod390s) {
				if (m390.getYear() == 2015) {
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
		
		if (mod390.isLegalEntity()) {
			mod390.setName(mod390.getEnterpriseName());
		} else {
			String tmpName = mod390.getEnterpriseName();
			if (AonStringUtils.contains(tmpName, ',')) {
				mod390.setName(AonStringUtils.trim(AonStringUtils.substringAfter(tmpName, ",")));
				mod390.setFirstSurname(AonStringUtils.trim(AonStringUtils.substringBefore(tmpName, ",")));
			} else {
				mod390.setName(AonStringUtils.trim(AonStringUtils.substringBefore(tmpName, " ")));
				mod390.setFirstSurname(AonStringUtils.trim(AonStringUtils.substringAfter(tmpName, " ")));
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
		mod390.setId(record.getValue(FS_MODEL390.ID));
		mod390.setAdministration(record.getValue(FS_MODEL390.ADMINISTRATION));
		mod390.setYear(record.getValue(FS_MODEL390.YEAR));
		mod390.setDomain(record.getValue(FS_MODEL390.DOMAIN));
		mod390.setEnterprise(record.getValue(FS_MODEL390.ENTERPRISE));
		mod390.setDocument(record.getValue(FS_MODEL390.DOCUMENT));
		mod390.setEnterpriseName(record.getValue(FS_MODEL390.NAME));
		mod390.setReceipt(record.getValue(FS_MODEL390.RECEIPT));
		mod390.setReplacement(record.getValue(FS_MODEL390.REPLACEMENT) == 1);
		mod390.setReplacedReceipt(record.getValue(FS_MODEL390.REPLACED_RECEIPT));
		mod390.setComments(record.getValue(FS_MODEL390.COMMENTS));
		StringReader reader = new StringReader(record.getValue(FS_MODEL390.MODEL));
		try {
			if (mod390.getYear() == 2015 || mod390.getYear() == 2016) {
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
			if (mod390.getYear() == 2015 || mod390.getYear() == 2016) {
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
			.set(FS_MODEL390.ADMINISTRATION, mod390.getAdministration())
			.set(FS_MODEL390.STATUS, (byte) 0)
			.set(FS_MODEL390.SECURITY_LEVEL,AonEnumUtils.getByte(mod390.isConfidential()))
			.set(FS_MODEL390.DOCUMENT, mod390.getDocument())
			.set(FS_MODEL390.NAME, mod390.getName())
			.set(FS_MODEL390.COMPLEMENTARY, (byte) 0)
			.set(FS_MODEL390.REPLACEMENT,AonEnumUtils.getByte(mod390.isReplacement()))
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
			.set(FS_MODEL390.ADMINISTRATION, mod390.getAdministration())
			.set(FS_MODEL390.STATUS, (byte) 0)
			.set(FS_MODEL390.SECURITY_LEVEL,AonEnumUtils.getByte(mod390.isConfidential()))
			.set(FS_MODEL390.DOCUMENT, mod390.getDocument())
			.set(FS_MODEL390.NAME, mod390.getName())
			.set(FS_MODEL390.COMPLEMENTARY, (byte) 0)
			.set(FS_MODEL390.REPLACEMENT,AonEnumUtils.getByte(mod390.isReplacement()))
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

	private static LinkedList<Mod390Detail> getMod390Details(AONContext ctx, Mod3902015 mod390 ) {
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
									.setAccrualRegime(false)
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
							vc.setAccrualRegime(true);
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
							 AonDateUtils.toSql(AonDateUtils.getYearFirstDay(2014))
							,AonDateUtils.toSql(AonDateUtils.getYearLastDay(2014))) ))
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
								vc.setAccrualRegime(true);
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

			// **********************************************************************************************
			// **********************************************************************************************
			
			// Cálculo de la Regularizacion por aplicacion del porcentaje definitivo de prorrata 
			Record1<BigDecimal> record = ctx.getDslContext()
				.select(DSL.sum(FS_VAT_DETAIL.QUOTA))
				.from(FS_VAT_DETAIL)
				.join(FS_VAT).on(FS_VAT.ID.equal(FS_VAT_DETAIL.FS_VAT))
				.where(FS_VAT.DOMAIN.equal(mod390.getDomain()))
				.and(FS_VAT.YEAR.equal(mod390.getYear()))
				.and(FS_VAT_DETAIL.VAT_KEY.equal("RP"))
				.fetchOne();
			if (record != null) {
				BigDecimal quota = record.getValue(DSL.sum(FS_VAT_DETAIL.QUOTA));
				if (quota != null) {
					Mod390Detail detail = map.get(Mod3902015DetailKey.K35);
					if (detail == null) {
						detail = new Mod390Detail();
						map.put(Mod3902015DetailKey.K35, detail );
					}
					detail.setKey(Mod3902015DetailKey.K35);
					detail.setQuota(quota.doubleValue());
				}
			}
				
		return new LinkedList<Mod390Detail>(map.values());
	}
	
	private static Mod3902015 fillGeneralRegimeData(AONContext ctx, Mod3902015 mod390) {
		try {
			EnumMap<Mod3902015DetailKey, Mod390Detail> map = new EnumMap<Mod3902015DetailKey, Mod390Detail>(Mod3902015DetailKey.class);
			Mod390Detail det = null;
			LinkedList<Mod390Detail> details = getMod390Details(ctx, mod390);
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
		boolean fill(SimplifedRegimeContext src,Mod3902015 mod390);
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
		public static void fill(SimplifedRegimeContext src,Mod3902015 mod390) {
			for (SimplifiedRegimeFiller filler : SimplifiedRegimeFiller.values()) {
				if (filler.getKey() ==  src.getKey() && filler.getFiller() != null) {
					filler.getFiller().fill(src, mod390);
				}
			}
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
	
	private static void fillGeneralDeclarationResults(AONContext ctx, Mod3902015 mod390) {
		ctx.getDslContext().select(FS_VAT.PERIOD,FS_VAT.TAX_REFUND_REGISTRY
				,FS_VAT_DECLARATION.DEPOSIT,FS_VAT_DECLARATION.PAY_BACK,FS_VAT_DECLARATION.COMPENSATE)
				.from(FS_VAT)
				.join(FS_VAT_DECLARATION).on(FS_VAT.ID.equal(FS_VAT_DECLARATION.FS_VAT))
				.where(FS_VAT.DOMAIN.equal(mod390.getDomain()))
				.and(FS_VAT.YEAR.equal(mod390.getYear()))
				.and(FS_VAT_DECLARATION.ADMINISTRATION.equal((byte) Administration.COMMON_TERRITORY.ordinal()))
				.fetch()
				.stream()
				.forEach( record -> {
					Period period = Period.values()[record.getValue(FS_VAT.PERIOD)];
					boolean taxRefundRegistry = record.getValue(FS_VAT.TAX_REFUND_REGISTRY) == 1;
					mod390.setBox95( AonMathUtils.round(mod390.getBox95() + record.getValue(FS_VAT_DECLARATION.DEPOSIT)));
					if ( taxRefundRegistry ) {
						mod390.setBox96( AonMathUtils.round(mod390.getBox96() + record.getValue(FS_VAT_DECLARATION.PAY_BACK)));
						mod390.setTaxRefund(true);
					}
					// Last Period
					if (period == Period.M12 || period == Period.T4) {
						mod390.setBox97( record.getValue(FS_VAT_DECLARATION.COMPENSATE));
						mod390.setBox98( record.getValue(FS_VAT_DECLARATION.PAY_BACK));
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
}
