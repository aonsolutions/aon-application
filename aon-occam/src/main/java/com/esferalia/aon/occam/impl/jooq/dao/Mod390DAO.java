package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsActivity.FS_ACTIVITY;
import static com.esferalia.aon.jooq.tables.FsActivityInfo.FS_ACTIVITY_INFO;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jooq.Field;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.FsModel390Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.FarmerRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Mod390DetailKey;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.SimpliedRegimeActivity;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013toMod390;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.Mod390toAEATIVA2013;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod390DAO {
	
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
		private boolean surcharge;
		private double percentage;
		private VatDeductionType vatDeductionType;
		
		public InvoiceType getInvoiceType() {
			return invoiceType;
		}
		public void setInvoiceType(InvoiceType invoiceType) {
			this.invoiceType = invoiceType;
		}
		public RectificationType getRectificationType() {
			return rectificationType;
		}
		public void setRectificationType(RectificationType rectificationType) {
			this.rectificationType = rectificationType;
		}
		public boolean isService() {
			return service;
		}
		public void setService(boolean service) {
			this.service = service;
		}
		public InvoiceTransactionType getTransaction() {
			return transaction;
		}
		public void setTransaction(InvoiceTransactionType transaction) {
			this.transaction = transaction;
		}
		public boolean isInvestment() {
			return investment;
		}
		public void setInvestment(boolean investment) {
			this.investment = investment;
		}
		public boolean isSurcharge() {
			return surcharge;
		}
		public void setSurcharge(boolean surcharge) {
			this.surcharge = surcharge;
		}
		public double getPercentage() {
			return percentage;
		}
		public void setPercentage(double percentage) {
			this.percentage = percentage;
		}
		public VatDeductionType getVatDeductionType() {
			return vatDeductionType;
		}
		public void setVatDeductionType(VatDeductionType vatDeductionType) {
			this.vatDeductionType = vatDeductionType;
		}
		public boolean isRectification() {
			return (rectificationType == RectificationType.SPECIAL_RECTIFIER);
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
		
		  K00_04 (Mod390DetailKey.K00_04, (vc -> (vc.isNationalSales() && !vc.isRectification() && vc.getPercentage() ==  4)))
		 ,K00_08 (Mod390DetailKey.K00_08, (vc -> (vc.isNationalSales() && !vc.isRectification() && vc.getPercentage() ==  8)))
		 ,K00_10 (Mod390DetailKey.K00_10, (vc -> (vc.isNationalSales() && !vc.isRectification() && vc.getPercentage() == 10)))
		 ,K00_18 (Mod390DetailKey.K00_18, (vc -> (vc.isNationalSales() && !vc.isRectification() && vc.getPercentage() == 18)))
		 ,K00_21 (Mod390DetailKey.K00_21, (vc -> (vc.isNationalSales() && !vc.isRectification() && vc.getPercentage() == 21)))
		 ,K01_04 (Mod390DetailKey.K01_04, null)
		 ,K01_08 (Mod390DetailKey.K01_08, null)
		 ,K01_10 (Mod390DetailKey.K01_10, null)
		 ,K01_18 (Mod390DetailKey.K01_18, null)
		 ,K01_21 (Mod390DetailKey.K01_21, null)
		 ,K02_04 (Mod390DetailKey.K02_04, null)
		 ,K02_08 (Mod390DetailKey.K02_08, null)
		 ,K02_10 (Mod390DetailKey.K02_10, null)
		 ,K02_18 (Mod390DetailKey.K02_18, null)
		 ,K02_21 (Mod390DetailKey.K02_21, null)
		 ,K03_18 (Mod390DetailKey.K03_18, null)
		 ,K03_21 (Mod390DetailKey.K03_21, null)
		 ,K04_04 (Mod390DetailKey.K04_04, (vc -> (vc.isIntracommunityPurchase() && vc.getPercentage() ==  4)))
		 ,K04_08 (Mod390DetailKey.K04_08, (vc -> (vc.isIntracommunityPurchase() && vc.getPercentage() ==  8)))
		 ,K04_10 (Mod390DetailKey.K04_10, (vc -> (vc.isIntracommunityPurchase() && vc.getPercentage() ==  10)))
		 ,K04_18 (Mod390DetailKey.K04_18, (vc -> (vc.isIntracommunityPurchase() && vc.getPercentage() ==  18)))
		 ,K04_21 (Mod390DetailKey.K04_21, (vc -> (vc.isIntracommunityPurchase() && vc.getPercentage() ==  21)))
	
		 ,K05_04 (Mod390DetailKey.K05_04, (vc -> (vc.isIntracommunityExpenses() && vc.getPercentage() ==  4)))
		 ,K05_08 (Mod390DetailKey.K05_08, (vc -> (vc.isIntracommunityExpenses() && vc.getPercentage() ==  8)))
		 ,K05_10 (Mod390DetailKey.K05_10, (vc -> (vc.isIntracommunityExpenses() && vc.getPercentage() == 10)))
		 ,K05_18 (Mod390DetailKey.K05_18, (vc -> (vc.isIntracommunityExpenses() && vc.getPercentage() == 18)))
		 ,K05_21 (Mod390DetailKey.K05_21, (vc -> (vc.isIntracommunityExpenses() && vc.getPercentage() == 21)))
		 ,K06	 (Mod390DetailKey.K06	, (vc -> ( vc.isOtherISPPurchase() 
				 								|| vc.isOtherISPExpenses() 
				 								|| vc.isCanCeuMelExpenses() 
				 								|| vc.isExtracommunityExpenses())))
		 
		 ,K07	 (Mod390DetailKey.K07	, (vc -> (vc.isNationalSales() && vc.isRectification())))
		 ,K08	 (Mod390DetailKey.K08	, null)
		 ,K09	 (Mod390DetailKey.K09	, null)
		 ,K10_05 (Mod390DetailKey.K10_05, null)
		 ,K10_1  (Mod390DetailKey.K10_1 , null)
		 ,K10_14 (Mod390DetailKey.K10_14, null)
		 ,K10_4  (Mod390DetailKey.K10_4 , null)
		 ,K10_52 (Mod390DetailKey.K10_52, null)
		 ,K10_175(Mod390DetailKey.K10_175,null)
		 ,K11	 (Mod390DetailKey.K11	, null)
		 ,K12	 (Mod390DetailKey.K12	, null)
		 ,K13	 (Mod390DetailKey.K13	, null)
		 
		 ,K14_04 (Mod390DetailKey.K14_04, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && vc.getPercentage() == 4))) 
		 ,K14_07 (Mod390DetailKey.K14_07, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && vc.getPercentage() == 7))) 
		 ,K14_08 (Mod390DetailKey.K14_08, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && vc.getPercentage() == 8))) 
		 ,K14_10 (Mod390DetailKey.K14_10, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && vc.getPercentage() == 10))) 
		 ,K14_16 (Mod390DetailKey.K14_16, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && vc.getPercentage() == 16)))
		 ,K14_18 (Mod390DetailKey.K14_18, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && vc.getPercentage() == 18)))
		 ,K14_21 (Mod390DetailKey.K14_21, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && vc.getPercentage() == 21))) 
		 ,K15	 (Mod390DetailKey.K15	, null)
		 
		 ,K16_04 (Mod390DetailKey.K16_04, null)
		 ,K16_07 (Mod390DetailKey.K16_07, null)
		 ,K16_08 (Mod390DetailKey.K16_08, null)
		 ,K16_10 (Mod390DetailKey.K16_10, null)
		 ,K16_16 (Mod390DetailKey.K16_16, null)
		 ,K16_18 (Mod390DetailKey.K16_18, null)
		 ,K16_21 (Mod390DetailKey.K16_21, null)
		 ,K17	 (Mod390DetailKey.K17	, null)
		 
		 ,K18_04 (Mod390DetailKey.K18_04, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && vc.getPercentage() ==  4)))
		 ,K18_07 (Mod390DetailKey.K18_07, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && vc.getPercentage() ==  7)))
		 ,K18_08 (Mod390DetailKey.K18_08, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && vc.getPercentage() ==  8)))
		 ,K18_10 (Mod390DetailKey.K18_10, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && vc.getPercentage() == 10))) 
		 ,K18_16 (Mod390DetailKey.K18_16, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && vc.getPercentage() == 16))) 
		 ,K18_18 (Mod390DetailKey.K18_18, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && vc.getPercentage() == 18))) 
		 ,K18_21 (Mod390DetailKey.K18_21, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && vc.getPercentage() == 21)))
		 ,K19	 (Mod390DetailKey.K19	, null)
		 
		 ,K20_04 (Mod390DetailKey.K20_04, null)
		 ,K20_07 (Mod390DetailKey.K20_07, null)
		 ,K20_08 (Mod390DetailKey.K20_08, null)
		 ,K20_10 (Mod390DetailKey.K20_10, null)
		 ,K20_16 (Mod390DetailKey.K20_16, null)
		 ,K20_18 (Mod390DetailKey.K20_18, null)
		 ,K20_21 (Mod390DetailKey.K20_21, null)
		 ,K21	 (Mod390DetailKey.K21	, null)
		 
		 ,K22_04 (Mod390DetailKey.K22_04, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && vc.getPercentage() == 4)))
		 ,K22_07 (Mod390DetailKey.K22_07, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && vc.getPercentage() == 4)))
		 ,K22_08 (Mod390DetailKey.K22_08, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && vc.getPercentage() == 4)))
		 ,K22_10 (Mod390DetailKey.K22_10, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && vc.getPercentage() == 4)))
		 ,K22_16 (Mod390DetailKey.K22_16, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && vc.getPercentage() == 4)))
		 ,K22_18 (Mod390DetailKey.K22_18, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && vc.getPercentage() == 4)))
		 ,K22_21 (Mod390DetailKey.K22_21, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && vc.getPercentage() == 4)))
		 ,K23	 (Mod390DetailKey.K23	, null)
		 
		 ,K24_04 (Mod390DetailKey.K24_04, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && vc.isInvestment() && vc.getPercentage() == 4)))
		 ,K24_07 (Mod390DetailKey.K24_07, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && vc.isInvestment() && vc.getPercentage() == 7)))
		 ,K24_08 (Mod390DetailKey.K24_08, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && vc.isInvestment() && vc.getPercentage() == 8)))
		 ,K24_10 (Mod390DetailKey.K24_10, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && vc.isInvestment() && vc.getPercentage() == 10)))
		 ,K24_16 (Mod390DetailKey.K24_16, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && vc.isInvestment() && vc.getPercentage() == 16)))
		 ,K24_18 (Mod390DetailKey.K24_18, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && vc.isInvestment() && vc.getPercentage() == 18)))
		 ,K24_21 (Mod390DetailKey.K24_21, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && vc.isInvestment() && vc.getPercentage() == 21)))
		 ,K25	 (Mod390DetailKey.K25	, null)
		 
		 ,K26_04 (Mod390DetailKey.K26_04, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && vc.getPercentage() ==  4)))
		 ,K26_07 (Mod390DetailKey.K26_07, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && vc.getPercentage() ==  7)))
		 ,K26_08 (Mod390DetailKey.K26_08, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && vc.getPercentage() ==  8)))
		 ,K26_10 (Mod390DetailKey.K26_10, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && vc.getPercentage() ==  10)))
		 ,K26_16 (Mod390DetailKey.K26_16, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && vc.getPercentage() ==  16)))
		 ,K26_18 (Mod390DetailKey.K26_18, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && vc.getPercentage() ==  18)))
		 ,K26_21 (Mod390DetailKey.K26_21, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && vc.getPercentage() ==  21)))
		 ,K27	 (Mod390DetailKey.K27	, null)
		 
		 ,K28_04 (Mod390DetailKey.K28_04, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && vc.getPercentage() ==  4)))
		 ,K28_07 (Mod390DetailKey.K28_07, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && vc.getPercentage() ==  7))) 
		 ,K28_08 (Mod390DetailKey.K28_08, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && vc.getPercentage() ==  8))) 
		 ,K28_10 (Mod390DetailKey.K28_10, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && vc.getPercentage() ==  10))) 
		 ,K28_16 (Mod390DetailKey.K28_16, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && vc.getPercentage() ==  16))) 
		 ,K28_18 (Mod390DetailKey.K28_18, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && vc.getPercentage() ==  18))) 
		 ,K28_21 (Mod390DetailKey.K28_21, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && vc.getPercentage() ==  21))) 
		 ,K29	 (Mod390DetailKey.K29   , null)
		 
		 ,K30_04 (Mod390DetailKey.K30_04, null)
		 ,K30_07 (Mod390DetailKey.K30_07, null)
		 ,K30_08 (Mod390DetailKey.K30_08, null)
		 ,K30_10 (Mod390DetailKey.K30_10, null)
		 ,K30_16 (Mod390DetailKey.K30_16, null)
		 ,K30_18 (Mod390DetailKey.K30_18, null)
		 ,K30_21 (Mod390DetailKey.K30_21, null)
		 ,K31	 (Mod390DetailKey.K31   , null)
		 
		 ,K32	 (Mod390DetailKey.K32, null)
		 ,K33	 (Mod390DetailKey.K33, null)
		 ,K34	 (Mod390DetailKey.K34, null)
		 ,K35	 (Mod390DetailKey.K35, null)
		 ,K36	 (Mod390DetailKey.K36, null)
		 ,K37	 (Mod390DetailKey.K37, null)
		 
		 ,B099	 (Mod390DetailKey.B099, (vc -> (vc.isNationalSales() && !vc.isRectification() && !vc.isSurcharge())))
		 ,B103	 (Mod390DetailKey.B103, (vc -> (vc.isIntracommunitySales() && !vc.isWithoutRightDeductionType())))
		 ,B104	 (Mod390DetailKey.B104, (vc -> (vc.isSales() && !vc.isWithoutRightDeductionType() && (vc.isExtracommunity() || vc.isCanCeuMel()) )))
		 ,B105	 (Mod390DetailKey.B105, (vc -> (vc.isSales() && !vc.isNational() && vc.isWithoutRightDeductionType())))
		 ,B110	 (Mod390DetailKey.B110, (vc -> (vc.isSales() && !vc.isWithoutRightDeductionType() && vc.isOtherISP())))
		 ,B112	 (Mod390DetailKey.B112, null)
		 ,B100	 (Mod390DetailKey.B100, null)
		 ,B101	 (Mod390DetailKey.B101, null)
		 ,B102	 (Mod390DetailKey.B102, (vc -> (vc.isNationalSales() && vc.isRectification() && vc.isSurcharge())))
		 ,B227	 (Mod390DetailKey.B227, null)
		 ,B228	 (Mod390DetailKey.B228, null)
		 ,B106	 (Mod390DetailKey.B106, null)
		 ,B107	 (Mod390DetailKey.B107, null)
		 ,B108	 (Mod390DetailKey.B108, null)
		 ;
		 
		private Mod390DetailKey key;
		private IMod390DetailKey accept;
			
		private DetailKey(Mod390DetailKey key, IMod390DetailKey accept) {
			this.key = key;
			this.accept = accept;
		}

		public Mod390DetailKey getKey() {
			return key;
		}
		public boolean accept(VatContext vc) {
			return (accept==null)?false:accept.accept(vc);
		}
		
		public static Mod390DetailKey[] getKeys(VatContext vc) {
			List<Mod390DetailKey> list = new ArrayList<Mod390DetailKey>();
			for (DetailKey key : DetailKey.values()) {
				if (key.accept(vc)) {
					list.add(key.getKey()); 
				}
			}
			return list.size()==0?null:list.toArray(new Mod390DetailKey[list.size()]);
		}
	}
	/*
	private static final String VAT_TAX_DECLARATION_SELECT = 
			"SELECT "  
				+SQLConstants.FS_VAT +"." + FsVatColumns.PERIOD +" "+FsVatColumns.PERIOD + ","  
				+SQLConstants.FS_VAT +"." + FsVatColumns.TAX_REFUND_REGISTRY +" "+FsVatColumns.TAX_REFUND_REGISTRY + ","
				+SQLConstants.FS_VAT_DECLARATION +"." + FsVatDeclarationColumns.DEPOSIT+" "+FsVatDeclarationColumns.DEPOSIT + ","  
				+SQLConstants.FS_VAT_DECLARATION+"."+FsVatDeclarationColumns.PAY_BACK+" "+FsVatDeclarationColumns.PAY_BACK + ","
				+SQLConstants.FS_VAT_DECLARATION+"."+FsVatDeclarationColumns.COMPENSATE+" "+FsVatDeclarationColumns.COMPENSATE
				+" FROM " + SQLConstants.FS_VAT
				+ " INNER JOIN " + SQLConstants.FS_VAT_DECLARATION 
				+" ON " +SQLConstants.FS_VAT_DECLARATION+"."+FsVatDeclarationColumns.FS_VAT
				+" = " + SQLConstants.FS_VAT+"."+FsVatColumns.ID
				+" WHERE " + SQLConstants.FS_VAT +"."+FsVatColumns.DOMAIN+"=?"
				+" AND " + SQLConstants.FS_VAT +"."+FsVatColumns.YEAR +"=?"
				+" AND "+SQLConstants.FS_VAT_DECLARATION+"."+FsVatDeclarationColumns.ADMINISTRATION+"=" + Administration.COMMON_TERRITORY.ordinal();

	private static final String VAT_TAX_DETAIL_SELECT = 
			"SELECT "
				+SQLConstants.FS_VAT_DETAIL +"." + FsVatDetailColumns.VAT_KEY +" "+FsVatDetailColumns.VAT_KEY + ","
				+SQLConstants.FS_VAT_DETAIL +"." + FsVatDetailColumns.TAXABLE_BASE +" "+FsVatDetailColumns.TAXABLE_BASE
				+" FROM " + SQLConstants.FS_VAT
				+ " INNER JOIN " + SQLConstants.FS_VAT_DETAIL 
				+" ON " +SQLConstants.FS_VAT_DETAIL+"."+FsVatDetailColumns.FS_VAT
				+" = " + SQLConstants.FS_VAT+"."+FsVatColumns.ID
				+" WHERE " + SQLConstants.FS_VAT +"."+FsVatColumns.DOMAIN+"=?"
				+" AND " + SQLConstants.FS_VAT +"."+FsVatColumns.YEAR +"=?"
				+" AND " + SQLConstants.FS_VAT +"."+FsVatColumns.PERIOD +"!=" + Period.YEAR.ordinal();  
*/
	public static Mod390 initialize(AONContext ctx, int year) {
		Mod390 mod390 = new Mod390();
		FiscalParameters params = AppParamDAO.getFiscalParameters(ctx,year);
		mod390.setEnterprise(params.getCompany());
		mod390.setDomain(ctx.getDomainId());
		mod390.setDocument(params.getDocument());
		mod390.setEnterpriseName(params.getName());
		mod390.setYear( 2014 );
		if (mod390.isLegalEntity()) mod390.setName(mod390.getEnterpriseName());
		else {
			String tmpName = mod390.getEnterpriseName();
			if (AonStringUtils.contains(tmpName, ',')) {
				mod390.setName(AonStringUtils.trim(AonStringUtils.substringAfter(tmpName, ",")));
				mod390.setFirstSurname(AonStringUtils.trim(AonStringUtils.substringBefore(tmpName, ",")));
			} else {
				mod390.setName(AonStringUtils.trim(AonStringUtils.substringBefore(tmpName, " ")));
				mod390.setFirstSurname(AonStringUtils.trim(AonStringUtils.substringAfter(tmpName, " ")));
			}
		}
		fillGeneralRegimeData(ctx, mod390);
		fillSimplifedRegimeData(ctx, mod390);
		return mod390;	
	}

	public static ArrayList<Mod390> getByDomain(AONContext ctx, int domain) {
		ctx.checkRead();
		ArrayList<Mod390> list = new ArrayList<Mod390>();
		ctx.getDslContext()
				.select(FS_MODEL390.fields())
				.from(FS_MODEL390)
				.join(DOMAIN)
				.on(FS_MODEL390.DOMAIN.equal(DOMAIN.ID))
				.where(FS_MODEL390.DOMAIN.equal(domain).or(
						DOMAIN.PARENT.equal(domain)))
				.orderBy(FS_MODEL390.YEAR.desc(), FS_MODEL390.NAME.asc(),
						FS_MODEL390.REPLACEMENT.asc())
				.fetchInto(FsModel390Record.class)
				.stream()
				.forEach(
						record -> {
							Mod390 mod390 = new Mod390();
							populate(record, mod390, false);
							list.add(mod390);
						});
		return list;
	}

	public static Mod390 getById(AONContext ctx, int id) {
		ctx.checkRead();
		Mod390 mod390 = new Mod390();
		ctx.getDslContext()
				.selectFrom(FS_MODEL390)
				.where(FS_MODEL390.ID.equal(id))
				.fetch()
				.stream()
				.forEach(
						record -> {
							populate(record, mod390, true);
						});
		if (mod390.getId() != null) {
			return mod390;
		}
		return null;
	}

	private static void populate(FsModel390Record record, Mod390 mod390, boolean populateModel)  {
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
		if (populateModel) {
			try {
				StringReader reader = new StringReader(record.getValue(FS_MODEL390.MODEL));
				JAXBContext context = JAXBContext.newInstance(AEATIVA2013.class);
				Unmarshaller um = context.createUnmarshaller();
				AEATIVA2013 iva = (AEATIVA2013) um.unmarshal(reader);
				AEATIVA2013toMod390.populate(mod390, iva);
			} catch (JAXBException e1) {
				throw new AonCoreException("XML PROBLEM");
			} catch (ParseException e) {
				throw new AonCoreException("XML PROBLEM");
			}
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


	public static Mod390 save(AONContext ctx, Mod390 mod390)  {
		if (mod390.getId() == null) {
			return insert(ctx, mod390);
		} else {
			return update(ctx, mod390);
		}
	}
	
	private static String getXMLModel( Mod390 mod390 ) {
		try {
			AEATIVA2013 iva = Mod390toAEATIVA2013.getAEATIVA2013(mod390);
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(AEATIVA2013.class);
			Marshaller um = context.createMarshaller();
			um.setProperty("jaxb.encoding", "ISO-8859-1");
			um.marshal(iva,writer);
			return writer.toString();
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new AonCoreException("Error en conversión XML",e);
		}				
	}
	

	private static Mod390 insert(AONContext ctx, Mod390 mod390) {
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
	
	private static Mod390 update(AONContext ctx, Mod390 mod390) {
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

	private static void validate(AONContext ctx, Mod390 mod390) {
		if (mod390.isReplacement()) {
			// Se comprueba que exista la declaración ssustituida.
			if (ctx.getDslContext().selectOne()
					.from(FS_MODEL390)
					.where(FS_MODEL390.YEAR.equal(mod390.getYear())
					.and(FS_MODEL390.ENTERPRISE.equal(mod390.getEnterprise()))
					.and(FS_MODEL390.RECEIPT.equal(mod390.getReplacedReceipt()))).fetchCount() == 0) 
				throw new AonCoreException(
						AonError.FISCAL_NO_REPLACED_DECLARATION.getMessage());

			// Se comprueba que no exista una declaraci?n sustitutiva.
			if (ctx.getDslContext().selectOne()
					.from(FS_MODEL390)
					.where(FS_MODEL390.YEAR.equal(mod390.getYear())
					.and(FS_MODEL390.ENTERPRISE.equal(mod390.getEnterprise()))
					.and(FS_MODEL390.REPLACEMENT.equal((byte) 1))					
					.and(FS_MODEL390.REPLACED_RECEIPT.equal(mod390.getReplacedReceipt()))).fetchCount() > 0 )
				throw new AonCoreException(AonError.FISCAL_DECLARATION_ALREADY_REPLACED.getMessage());
		} else {
			// Se comprueba que no exista ya una declaraci?n.
			if (ctx.getDslContext().selectOne()
					.from(FS_MODEL390)
					.where(FS_MODEL390.YEAR.equal(mod390.getYear())
					.and(FS_MODEL390.ENTERPRISE.equal(mod390.getEnterprise()))
					.and(FS_MODEL390.REPLACEMENT.equal((byte) 0))).fetchCount() > 0 ) 
				throw new AonCoreException(
						AonError.FISCAL_DECLARATION_ALREADY_EXISTS.getMessage());
		}
	}

	public static void delete(AONContext ctx, Mod390 mod390) {
		ctx.checkWrite();
		ctx.getDslContext().delete(FS_MODEL390)
				.where(FS_MODEL390.ID.equal(mod390.getId())).execute();
	}

	public static ArrayList<Mod390Detail> getMod390Details(AONContext ctx, Mod390 mod390 ) {
		Date firstDay = AonDateUtils.getYearFirstDay(mod390.getYear());
		Date lastDay = AonDateUtils.getYearLastDay(mod390.getYear());
		
		Field<BigDecimal> sumBase = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE).as(INVOICE_DETAIL.TAXABLE_BASE.getName());
		
		Field<Double> invoiceTaxQuota = DSL.round( (INVOICE_DETAIL.TAXABLE_BASE.mul(INVOICE_TAX.PERCENTAGE)).div(100), 2);
		Field<BigDecimal> sumQuotaOp = DSL.sum(DSL.decode()
				.when(INVOICE_TAX.QUOTA.notEqual(0.0),INVOICE_TAX.QUOTA)
				.when(INVOICE_TAX.QUOTA.equal(0.0), invoiceTaxQuota));		

		Field<Double> invoiceSurchargeQuota = DSL.round( (INVOICE_DETAIL.TAXABLE_BASE.mul(INVOICE_TAX.SURCHARGE)).div(100), 2);
		Field<BigDecimal> sumSurchargeQuotaOp = DSL.sum(DSL.decode()
				.when(INVOICE_TAX.SURCHARGE_QUOTA.notEqual(0.0),INVOICE_TAX.SURCHARGE_QUOTA)
				.when(INVOICE_TAX.SURCHARGE_QUOTA.equal(0.0), invoiceSurchargeQuota));		
		EnumMap<Mod390DetailKey, Mod390Detail> map = new EnumMap<Mod390DetailKey, Mod390Detail>(Mod390DetailKey.class);
		
		ctx.getDslContext().select(INVOICE.TYPE
				,INVOICE.RECTIFICATION_TYPE
				,INVOICE.SERVICE
				,INVOICE.TRANSACTION
				,INVOICE.INVESTMENT
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
				.groupBy(INVOICE.TYPE
						,INVOICE.RECTIFICATION_TYPE
						,INVOICE.SERVICE
						,INVOICE.TRANSACTION
						,INVOICE.INVESTMENT
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
							double surchargePercent = record.getValue(INVOICE_TAX.SURCHARGE).doubleValue();
							boolean surcharge = (surchargePercent > 0); 
							vc.setSurcharge(surcharge);
							double percentage = record.getValue(INVOICE_TAX.PERCENTAGE); 
							vc.setPercentage(percentage);
							vc.setVatDeductionType(VatDeductionType.values()[record.getValue(INVOICE_TAX.VAT_DEDUCTION_TYPE)]);
							double taxableBase = record.getValue(sumBase).doubleValue();
							double quota = record.getValue(sumQuotaOp).doubleValue();
							Mod390DetailKey[] keys = DetailKey.getKeys(vc);			
							if (keys != null) {
								for (Mod390DetailKey key : keys) {
									Mod390Detail detail = map.get(key);
									if (detail == null) detail = new Mod390Detail();
									detail.setKey(key);
									detail.setPercent(percentage);
									detail.setQuota( AonMathUtils.round(detail.getQuota()  + quota));
									detail.setTaxableBase( AonMathUtils.round( detail.getTaxableBase() + taxableBase));
									if (vc.isSurcharge() && vc.isNationalSales()) {
										double surchargeQuota = record.getValue(sumSurchargeQuotaOp).doubleValue();
										Mod390DetailKey surchargeKey = null;
										if (percentage == 0.5) surchargeKey = Mod390DetailKey.K10_05;
										else if (percentage == 1) surchargeKey = Mod390DetailKey.K10_1;
										else if (percentage == 1.4) surchargeKey = Mod390DetailKey.K10_14;
										else if (percentage == 4) surchargeKey = Mod390DetailKey.K10_4;
										else if (percentage == 5.2) surchargeKey = Mod390DetailKey.K10_52;
										else if (percentage == 1.75) surchargeKey = Mod390DetailKey.K10_175;
										if (surchargeKey != null) {
											detail = map.get(key);
											if (detail == null) detail = new Mod390Detail();
											detail.setKey(surchargeKey);
											detail.setPercent(surchargePercent);
											detail.setQuota( AonMathUtils.round(detail.getQuota()  + surchargeQuota));
											detail.setTaxableBase( AonMathUtils.round( detail.getTaxableBase() + taxableBase));
										}
									}
								}
							}
						}
				);
		return new ArrayList<Mod390Detail>(map.values());
	}
	
/*
	private static Mod390Detail getDetail(ArrayList<Mod390Detail> list, Mod390DetailKey key) {
		for (Mod390Detail detail : list) {
			if (detail.getKey() == key) {
				return detail;
			}
		}
		throw new IllegalArgumentException("La clave " + key + " no soportada");
	}

	private static ArrayList<Mod390Detail> initializeList() {
		ArrayList<Mod390Detail> list = new ArrayList<Mod390Detail>();
		Mod390Detail detail = null;
		for (Mod390DetailKey key : Mod390DetailKey.values()) {
			detail = new Mod390Detail();
			detail.setKey(key);
			detail.setPercent(key.getPercent());
			list.add(detail);
		}
		return list;
	}
*/
	private static Mod390 fillGeneralRegimeData(AONContext ctx, Mod390 mod390) {
		return mod390;
	}

/*	
	public static Mod303Results getMod303Results(int domain, int year,
			Connection conn)  throws AonSQLException  {
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(VAT_TAX_DECLARATION_SELECT,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, domain);
			stmt.setInt(2, year);
			Mod303Results mod303Results = new Mod303Results();
			rs = stmt.executeQuery();
			while (rs.next()) {
				Period period = Period.values()[rs.getInt(FsVatColumns.PERIOD)];
				boolean taxRefundRegistry = rs.getBoolean(FsVatColumns.TAX_REFUND_REGISTRY);
				double deposit = rs.getDouble(FsVatDeclarationColumns.DEPOSIT);
				double payBack = rs.getDouble(FsVatDeclarationColumns.PAY_BACK);
				mod303Results.setDepositSum(AonUtil.round(mod303Results.getDepositSum() + deposit));
				if ( taxRefundRegistry ) {
					mod303Results.setPaybackSum(AonUtil.round(mod303Results.getPaybackSum() + payBack));
				}
				// Last Period
				if (period == Period.M12 || period == Period.T4) {
					mod303Results.setLastPeriodPaybackResult(payBack);
					mod303Results.setLastPeriodCompensateResult(rs.getDouble(FsVatDeclarationColumns.COMPENSATE));
					if ( taxRefundRegistry ) {
						mod303Results.setLastPeriodPaybackResult(0);	
					}
				}
			}
			rs.close();
			stmt.close();
			
			stmt = conn.prepareStatement(VAT_TAX_DETAIL_SELECT,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, domain);
			stmt.setInt(2, year);
			rs = stmt.executeQuery();
			while (rs.next()) {
				double taxableBase = rs.getDouble(FsVatDetailColumns.TAXABLE_BASE);
				VatTaxKey key = VatTaxKey.valueOf( rs.getString(FsVatDetailColumns.VAT_KEY) );
				if (VatTaxKey.A1 == key ) {
					mod303Results.setNationalSales( AonUtil.round(mod303Results.getNationalSales() + taxableBase ) ); 
				} else if (VatTaxKey.A2 == key ) {
					mod303Results.setReSales( AonUtil.round(mod303Results.getReSales() + taxableBase ) );
				} else if (VatTaxKey.A4 == key ) {
					mod303Results.setISPSales( AonUtil.round(mod303Results.getISPSales() + taxableBase ) );
				} else if (VatTaxKey.A5 == key ) {
					mod303Results.setNationalSales( AonUtil.round(mod303Results.getNationalSales() + taxableBase ) );
				} else if (VatTaxKey.EI == key ) {
					mod303Results.setIntracommunitarySales( AonUtil.round(mod303Results.getIntracommunitarySales() + taxableBase ) );
				} else if (VatTaxKey.EX1 == key ) {
					mod303Results.setExtracommunitarySales( AonUtil.round(mod303Results.getExtracommunitarySales() + taxableBase ) );
				} else if (VatTaxKey.EX2 == key ) {
					mod303Results.setExtracommunitarySales( AonUtil.round(mod303Results.getExtracommunitarySales() + taxableBase ) );
				} else if (VatTaxKey.OO == key ) {
					mod303Results.setExtracommunitarySales( AonUtil.round(mod303Results.getExtracommunitarySales() + taxableBase ) );
				} else if (VatTaxKey.OS == key ) {
					mod303Results.setWithoutRightSales( AonUtil.round(mod303Results.getWithoutRightSales() + taxableBase ) );
				}
			}
			return mod303Results;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}
*/
	
	private static Mod390 fillSimplifedRegimeData(AONContext ctx, Mod390 mod390) {
		ctx.getDslContext().select(FS_MODEL_DETAIL.TYPE
				, FS_MODEL_DETAIL.DESCRIPTION
				, FS_MODEL_DETAIL.AMOUNT)
			.from(FS_MODEL)
			.join(FS_MODEL_DETAIL).on(FS_MODEL.ID.equal(FS_MODEL_DETAIL.FS_MODEL))
			.where(FS_MODEL.DOMAIN.equal(mod390.getDomain()))
			.and(FS_MODEL.YEAR.equal(mod390.getYear()))
			.and(FS_MODEL.MODEL.equal("303"))
		.fetch()
		.stream()
		.forEach( record -> {
			String description = record.getValue( FS_MODEL_DETAIL.DESCRIPTION );
			String type = record.getValue( FS_MODEL_DETAIL.TYPE );
			Mod303Key key = Mod303Key.getKeyWithValue(type);
			if (key != null && (key.isActivity() || key.isFarmer())) {
				final String epigraph  = AonStringUtils.trim(AonStringUtils.substringBefore(description, "-"));
				ctx.getDslContext().select(FS_ACTIVITY_INFO.INFO_KEY 
						,FS_ACTIVITY_INFO.TYPE 
						,FS_ACTIVITY_INFO.LINE  
						,FS_ACTIVITY_INFO.VALUE
						,FS_ACTIVITY_INFO.BASE 
						,FS_ACTIVITY.FARMER)
					.from(FS_ACTIVITY)
					.join(FS_ACTIVITY_INFO).on(FS_ACTIVITY.ID.equal(FS_ACTIVITY_INFO.FS_ACTIVITY))
					.where(FS_ACTIVITY.DOMAIN.equal(mod390.getDomain()))
					.and(FS_ACTIVITY.YEAR.equal(mod390.getYear()))
					.and(FS_ACTIVITY.EPIGRAPH.equal(epigraph))
					.and(
						   (FS_ACTIVITY_INFO.INFO_KEY.like("M%").and(FS_ACTIVITY_INFO.TYPE.equal((byte) 1)))
						.or(FS_ACTIVITY_INFO.INFO_KEY.like("X%").and(FS_ACTIVITY_INFO.TYPE.equal((byte) 6)))
						.or(FS_ACTIVITY_INFO.INFO_KEY.like("Y%").and(FS_ACTIVITY_INFO.TYPE.equal((byte) 7)))
					).fetch()
					.stream()
					.forEach( record2 -> {
						boolean farmer = AonEnumUtils.getBoolean( record2.getValue(FS_ACTIVITY.FARMER));
						int line = record2.getValue( FS_ACTIVITY_INFO.LINE );
						String strValue = record2.getValue(FS_ACTIVITY_INFO.VALUE);
						double value = 0.0;
						try {
							value = Double.parseDouble(strValue);
						} catch (NumberFormatException e) {
							// Nothing, zero as double.
						}
						double base = record2.getValue(FS_ACTIVITY_INFO.BASE);
						String infoKey = record2.getValue(FS_ACTIVITY_INFO.INFO_KEY);
						Byte infoType = record2.getValue(FS_ACTIVITY_INFO.TYPE);
						mod390.setSimpRegime1(null);
						mod390.setSimpRegime2(null);
						if (!farmer) {
							SimpliedRegimeActivity act =  new SimpliedRegimeActivity();
							act.setEpigrafe(epigraph);
							if (infoType == 6 ) {	// M311_DETAIL
								if ("X00".equals(infoKey)) act.setBoxC(value);
								else if ("X01".equals(infoKey)) act.setBoxC(value);
								else if ("X05".equals(infoKey)) act.setBoxD(value);
								else if ("X06".equals(infoKey)) act.setBoxE(value);
								else if ("X07".equals(infoKey)) act.setBoxF(value);
								else if ("X08".equals(infoKey)) act.setBoxG(value);
								else if ("X09".equals(infoKey)) act.setBoxH(value);
								else if ("X10".equals(infoKey)) act.setBoxI(value);
								else if ("X11".equals(infoKey)) act.setBoxJ(value);
							} else {
								act.setUnit(line,value);
								act.setAmount(line,base);
							}
							if (mod390.getSimpRegime1() == null) {
								mod390.setSimpRegime1(act);
							} else if (mod390.getSimpRegime2() == null) {
								mod390.setSimpRegime2(act);
							}
						} else {
							FarmerRegimeActivity act =  new FarmerRegimeActivity();
							act.setCodigo(epigraph);
							if ("Y01".equals(infoKey)) act.setIncomes(value); 
							else if ("Y02".equals(infoKey)) act.setQuotaIndex(value);
							else if ("Y03".equals(infoKey)) act.setAccrualQuota(value);
							else if ("Y07".equals(infoKey)) act.setInputQuotas(value);
							else if ("Y08".equals(infoKey)) act.setQuota(value);
							
							if (mod390.getFarmerRegime1() == null) {
								mod390.setFarmerRegime1(act);
							} else if (mod390.getFarmerRegime2() == null) {
								mod390.setFarmerRegime2(act);
							}
						}
						
					});
				}
			}
		);
		return mod390;
	}
	

}
 