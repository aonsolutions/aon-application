package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.occam.api.model.type.Administration.ALAVA;
import static com.esferalia.aon.occam.api.model.type.Administration.BIZKAIA;
import static com.esferalia.aon.occam.api.model.type.Administration.COMMON_TERRITORY;
import static com.esferalia.aon.occam.api.model.type.Administration.GIPUZKOA;
import static com.esferalia.aon.occam.api.model.type.Administration.NAVARRA;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.mvel2.MVEL;

import com.esferalia.aon.jooq.tables.records.FsModelRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod111KeyInfo;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod111DAO extends FiscalModelDAO {

	private static class RetentionContext {
		private FiscalModel  fiscalModel;
		private WithholdingType withholdingType;
		private boolean perceptorCount;
		private boolean perception;
		private boolean retention;
		private boolean salary;
		private boolean inKind;
		
		private RetentionContext(FiscalModel fiscalModel) {
			this.fiscalModel = fiscalModel; 
		}
		public RetentionContext setWithholdingType(WithholdingType withholdingType) {
			this.withholdingType = withholdingType;
			return this;
		}
		public RetentionContext setPerceptorCount(boolean perceptorCount) {
			this.perceptorCount = perceptorCount;
			return this;
		} 
		public RetentionContext setPerception(boolean perception) {
			this.perception = perception;
			return this;
		}
		public RetentionContext setRetention(boolean retention) {
			this.retention = retention;
			return this;
		}
		public RetentionContext setSalary(boolean salary) {
			this.salary = salary;
			return this;
		}
		public RetentionContext setInKind(boolean inKind) {
			this.inKind = inKind;
			return this;
		}
		
		private boolean isAraba() {
			return (fiscalModel.getAdministration() == ALAVA);
		}
		private boolean isBizkaia() {
			return (fiscalModel.getAdministration() == BIZKAIA);
		}
		private boolean isAEAT() {
			return (fiscalModel.getAdministration() == COMMON_TERRITORY);
		}
		private boolean isSalary() {
			return salary;
		}
		public boolean isInKind() {
			return inKind;
		}
		private boolean isProfessional() {
			return (withholdingType == WithholdingType.PROFESSIONAL);
		}
//		private boolean isRenting() {
//			return (withholdingType == WithholdingType.RENTING);
//		}
//		private boolean isMovableCapital() {
//			return (withholdingType == WithholdingType.MOVABLE_CAPITAL);
//		}
		private boolean isFarmer() {
			return (withholdingType == WithholdingType.FARMER);
		}
		private boolean isTransportOperator() {
			return (withholdingType == WithholdingType.TRANSPORT_OPERATOR);
		}
		private boolean isPerceptorCount() {
			return perceptorCount;
		}
		private boolean isPerception() {
			return perception;
		}
		private boolean isRetention() {
			return retention;
		}
	}

	@FunctionalInterface
	private static interface IModelInfoProvider {
		String obtain(AONContext ctx, Mod111 mod,Mod111Key key);
	}
	private static final String INFO_MSG = "<pre class='aon-fixed-font aon-font-medium aon-margin-bottom'>{0}<pre>";
	private static final String NONE_INFO = "No hay datos";
	private static enum Mod111KeyInfoDAO {
		 NONE( ((ctx, mod, key) -> MessageFormat.format(INFO_MSG, NONE_INFO)) )
		,INVOICE( ((ctx, mod, key) -> MessageFormat.format(INFO_MSG, getInvoicesInfo(ctx, mod, key))))
		,SALARY( ((ctx, mod, key) -> MessageFormat.format(INFO_MSG, getSalaryInfo(ctx, mod, key))) )
		,COMPUTE( ((ctx, mod, key) -> MessageFormat.format(INFO_MSG, NONE_INFO)) )
		;
		private IModelInfoProvider provider;
		
		private Mod111KeyInfoDAO (IModelInfoProvider provider) {
			this.provider = provider;
		}
		public String getInfo(AONContext ctx, Mod111 mod,Mod111Key key) {
			return provider.obtain(ctx, mod, key);
		}
	}
	
	@FunctionalInterface
	private static interface IModelAccepter {
		boolean accept(Mod111 mod);
	}
	@FunctionalInterface
	public static interface IValueAccepter {
		boolean accept(RetentionContext rc);
	}

	private static enum Mod111KeyDAO {
		// *************************************************************************
		// *************************************************************** ALAVA ***
		// *************************************************************************
		 AR_907(Mod111Key.AR_907, (mod -> mod.getAdministration() == ALAVA && mod.getYear() > 2015))
		,AR_908(Mod111Key.AR_908, (mod -> mod.getAdministration() == ALAVA && mod.getYear() > 2015))
		,AR_909(Mod111Key.AR_909, (mod -> mod.getAdministration() == ALAVA && mod.getYear() > 2015))
		,AR_C50(Mod111Key.AR_C50, (mod -> mod.getAdministration() == ALAVA)
				, (rc -> rc.isAraba() && rc.isSalary() && !rc.isInKind() && rc.isPerceptorCount() ))
		,AR_C60(Mod111Key.AR_C60, (mod -> mod.getAdministration() == ALAVA)
				, (rc -> rc.isAraba() && rc.isSalary() && !rc.isInKind() && rc.isPerception() ))
		,AR_C70(Mod111Key.AR_C70, (mod -> mod.getAdministration() == ALAVA)
				, (rc -> rc.isAraba() && rc.isSalary() && !rc.isInKind() && rc.isRetention() ))
		,AR_C51(Mod111Key.AR_C51, (mod -> mod.getAdministration() == ALAVA))
		,AR_C61(Mod111Key.AR_C61, (mod -> mod.getAdministration() == ALAVA))
		,AR_C71(Mod111Key.AR_C71, (mod -> mod.getAdministration() == ALAVA))
		,AR_C52(Mod111Key.AR_C52, (mod -> mod.getAdministration() == ALAVA))
		,AR_C62(Mod111Key.AR_C62, (mod -> mod.getAdministration() == ALAVA))
		,AR_C72(Mod111Key.AR_C72, (mod -> mod.getAdministration() == ALAVA))
		,AR_C53(Mod111Key.AR_C53, (mod -> mod.getAdministration() == ALAVA))
		,AR_C63(Mod111Key.AR_C63, (mod -> mod.getAdministration() == ALAVA))
		,AR_C73(Mod111Key.AR_C73, (mod -> mod.getAdministration() == ALAVA))
		,AR_C54(Mod111Key.AR_C54, (mod -> mod.getAdministration() == ALAVA)
				, (rc -> rc.isAraba() && rc.isPerceptorCount() && (rc.isProfessional() || rc.isTransportOperator())))
		,AR_C64(Mod111Key.AR_C64, (mod -> mod.getAdministration() == ALAVA)
				, (rc -> rc.isAraba() && rc.isPerception() && (rc.isProfessional() || rc.isTransportOperator())))
		,AR_C74(Mod111Key.AR_C74, (mod -> mod.getAdministration() == ALAVA)
				, (rc -> rc.isAraba() && rc.isRetention() && (rc.isProfessional() || rc.isTransportOperator())))
		,AR_C58(Mod111Key.AR_C58, (mod -> mod.getAdministration() == ALAVA))
		,AR_C68(Mod111Key.AR_C68, (mod -> mod.getAdministration() == ALAVA))
		,AR_C78(Mod111Key.AR_C78, (mod -> mod.getAdministration() == ALAVA))
		,AR_C55(Mod111Key.AR_C55, (mod -> mod.getAdministration() == ALAVA)
				, (rc -> rc.isAraba() && rc.isPerceptorCount() && rc.isFarmer()))
		,AR_C65(Mod111Key.AR_C65, (mod -> mod.getAdministration() == ALAVA)
				, (rc -> rc.isAraba() && rc.isPerception() && rc.isFarmer() )) 
		,AR_C75(Mod111Key.AR_C75, (mod -> mod.getAdministration() == ALAVA)
				, (rc -> rc.isAraba() && rc.isRetention() && rc.isFarmer() ))
		,AR_C56(Mod111Key.AR_C56, (mod -> mod.getAdministration() == ALAVA)
				, (rc -> rc.isAraba() && rc.isSalary() && rc.isInKind() && rc.isPerceptorCount() ))
		,AR_C66(Mod111Key.AR_C66, (mod -> mod.getAdministration() == ALAVA)
				, (rc -> rc.isAraba() && rc.isSalary() && rc.isInKind() && rc.isPerception() ))
		,AR_C76(Mod111Key.AR_C76, (mod -> mod.getAdministration() == ALAVA)
				, (rc -> rc.isAraba() && rc.isSalary() && rc.isInKind() && rc.isRetention() ))
		,AR_C57(Mod111Key.AR_C57, (mod -> mod.getAdministration() == ALAVA))
		,AR_C67(Mod111Key.AR_C67, (mod -> mod.getAdministration() == ALAVA))
		,AR_C77(Mod111Key.AR_C77, (mod -> mod.getAdministration() == ALAVA))
		,AR_C80(Mod111Key.AR_C80, (mod -> mod.getAdministration() == ALAVA && mod.getYear() > 2015)
				, "AR_C50+AR_C51+AR_C52+AR_C53+AR_C54+AR_C58+AR_C55+AR_C56+AR_C57")
		,AR_C81(Mod111Key.AR_C81, (mod -> mod.getAdministration() == ALAVA && mod.getYear() > 2015)
				, "AR_C60+AR_C61+AR_C62+AR_C63+AR_C64+AR_C68+AR_C65+AR_C66+AR_C67")
		,AR_C82(Mod111Key.AR_C82, (mod -> mod.getAdministration() == ALAVA)
				, "AR_C70+AR_C71+AR_C72+AR_C73+AR_C74+AR_C78+AR_C75+AR_C76+AR_C77")
		,AR_C83(Mod111Key.AR_C83, (mod -> mod.getAdministration() == ALAVA && mod.getYear() > 2015))
		,AR_C84(Mod111Key.AR_C84, (mod -> mod.getAdministration() == ALAVA))
		,AR_C85(Mod111Key.AR_C85, (mod -> mod.getAdministration() == ALAVA))
		,AR_C87(Mod111Key.AR_C87, (mod -> mod.getAdministration() == ALAVA && mod.getYear() <= 2015)
				, "AR_C82+AR_C84+AR_C85")
		,AR_C87B(Mod111Key.AR_C87, (mod -> mod.getAdministration() == ALAVA && mod.getYear() > 2015)
				, "AR_C82-AR_C83+AR_C84+AR_C85")
		
		// *************************************************************************
		// ************************************************************* BIZKAIA ***
		// *************************************************************************
		,BZ_C01 (Mod111Key.BZ_C01 , (mod -> mod.getAdministration() == BIZKAIA)
				, (rc -> rc.isBizkaia() && rc.isSalary() && !rc.isInKind() && rc.isPerceptorCount() ))
		,BZ_C12 (Mod111Key.BZ_C12 , (mod -> mod.getAdministration() == BIZKAIA)
				, (rc -> rc.isBizkaia() && rc.isSalary() && !rc.isInKind() && rc.isPerception() ))
		,BZ_C23 (Mod111Key.BZ_C23 , (mod -> mod.getAdministration() == BIZKAIA)
				, (rc -> rc.isBizkaia() && rc.isSalary() && !rc.isInKind() && rc.isRetention() ))
		,BZ_C02 (Mod111Key.BZ_C02 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C13 (Mod111Key.BZ_C13 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C24 (Mod111Key.BZ_C24 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C03 (Mod111Key.BZ_C03 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C14 (Mod111Key.BZ_C14 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C25 (Mod111Key.BZ_C25 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C04 (Mod111Key.BZ_C04 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C15 (Mod111Key.BZ_C15 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C26 (Mod111Key.BZ_C26 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C05 (Mod111Key.BZ_C05 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C16 (Mod111Key.BZ_C16 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C27 (Mod111Key.BZ_C27 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C06 (Mod111Key.BZ_C06 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C17 (Mod111Key.BZ_C17 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C28 (Mod111Key.BZ_C28 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C07 (Mod111Key.BZ_C07 , (mod -> mod.getAdministration() == BIZKAIA)
				, (rc -> rc.isBizkaia() && rc.isPerceptorCount() && (rc.isProfessional() || rc.isTransportOperator())))
		,BZ_C18 (Mod111Key.BZ_C18 , (mod -> mod.getAdministration() == BIZKAIA)
				, (rc -> rc.isBizkaia() && rc.isPerception() && (rc.isProfessional() || rc.isTransportOperator())))
		,BZ_C29 (Mod111Key.BZ_C29 , (mod -> mod.getAdministration() == BIZKAIA)
				, (rc -> rc.isBizkaia() && rc.isRetention() && (rc.isProfessional() || rc.isTransportOperator())))
		,BZ_C50 (Mod111Key.BZ_C50 , (mod -> mod.getAdministration() == BIZKAIA && mod.getPeriod().isQuarterPeriod()))
		,BZ_C51 (Mod111Key.BZ_C51 , (mod -> mod.getAdministration() == BIZKAIA && mod.getPeriod().isQuarterPeriod()))
		,BZ_C52 (Mod111Key.BZ_C52 , (mod -> mod.getAdministration() == BIZKAIA && mod.getPeriod().isQuarterPeriod()))
		,BZ_C08 (Mod111Key.BZ_C08 , (mod -> mod.getAdministration() == BIZKAIA)
				, (rc -> rc.isBizkaia() && rc.isPerceptorCount() && rc.isFarmer()))
		,BZ_C19 (Mod111Key.BZ_C19 , (mod -> mod.getAdministration() == BIZKAIA)
				, (rc -> rc.isBizkaia() && rc.isPerception() && rc.isFarmer()))
		,BZ_C30 (Mod111Key.BZ_C30 , (mod -> mod.getAdministration() == BIZKAIA)
				, (rc -> rc.isBizkaia() && rc.isRetention() && rc.isFarmer()))
		,BZ_C09 (Mod111Key.BZ_C09 , (mod -> mod.getAdministration() == BIZKAIA)
				, (rc -> rc.isBizkaia() && rc.isSalary() && rc.isInKind() && rc.isPerceptorCount() ))
		,BZ_C20 (Mod111Key.BZ_C20 , (mod -> mod.getAdministration() == BIZKAIA)
				, (rc -> rc.isBizkaia() && rc.isSalary() && rc.isInKind() && rc.isPerception() ))
		,BZ_C31 (Mod111Key.BZ_C31 , (mod -> mod.getAdministration() == BIZKAIA)
				, (rc -> rc.isBizkaia() && rc.isSalary() && rc.isInKind() && rc.isRetention() ))
		,BZ_C10 (Mod111Key.BZ_C10 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C21 (Mod111Key.BZ_C21 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C32 (Mod111Key.BZ_C32 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C11 (Mod111Key.BZ_C11 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C22 (Mod111Key.BZ_C22 , (mod -> mod.getAdministration() == BIZKAIA))
		,BZ_C33 (Mod111Key.BZ_C33 , (mod -> mod.getAdministration() == BIZKAIA))
		
		,BZ_C34M(Mod111Key.BZ_C34T, (mod -> mod.getAdministration() == BIZKAIA && mod.getPeriod().isMonthPeriod())
				, "BZ_C01+BZ_C02+BZ_C03+BZ_C04+BZ_C05+BZ_C06+BZ_C07+BZ_C08+BZ_C09+BZ_C10+BZ_C11")
		,BZ_C35M(Mod111Key.BZ_C35T, (mod -> mod.getAdministration() == BIZKAIA && mod.getPeriod().isMonthPeriod())
				, "BZ_C12+BZ_C13+BZ_C14+BZ_C15+BZ_C16+BZ_C17+BZ_C18+BZ_C19+BZ_C20+BZ_C21+BZ_C22")
		,BZ_C36M(Mod111Key.BZ_C36T, (mod -> mod.getAdministration() == BIZKAIA && mod.getPeriod().isMonthPeriod())
				, "BZ_C23+BZ_C24+BZ_C25+BZ_C26+BZ_C27+BZ_C28+BZ_C29+BZ_C30+BZ_C31+BZ_C32+BZ_C33")
		
		,BZ_C34T(Mod111Key.BZ_C34T, (mod -> mod.getAdministration() == BIZKAIA && mod.getPeriod().isQuarterPeriod())
				, "BZ_C01+BZ_C02+BZ_C03+BZ_C04+BZ_C05+BZ_C06+BZ_C07+BZ_C50+BZ_C08+BZ_C09+BZ_C10+BZ_C11")
		,BZ_C35T(Mod111Key.BZ_C35T, (mod -> mod.getAdministration() == BIZKAIA && mod.getPeriod().isQuarterPeriod())
				, "BZ_C12+BZ_C13+BZ_C14+BZ_C15+BZ_C16+BZ_C17+BZ_C18+BZ_C51+BZ_C19+BZ_C20+BZ_C21+BZ_C22")
		,BZ_C36T(Mod111Key.BZ_C36T, (mod -> mod.getAdministration() == BIZKAIA && mod.getPeriod().isQuarterPeriod())
				, "BZ_C23+BZ_C24+BZ_C25+BZ_C26+BZ_C27+BZ_C28+BZ_C29+BZ_C52+BZ_C30+BZ_C31+BZ_C32+BZ_C33")
		
		,BZ_C39T(Mod111Key.BZ_C39 , (mod -> mod.getAdministration() == BIZKAIA)
				, "BZ_C36T")
		
		// *************************************************************************
		// **************************************************** COMMON TERRITORY ***
		// *************************************************************************
		,CT_C01(Mod111Key.CT_C01, (mod -> mod.getAdministration() == COMMON_TERRITORY)
				, (rc -> rc.isAEAT() && rc.isSalary() && !rc.isInKind() && rc.isPerceptorCount() ))
		,CT_C02(Mod111Key.CT_C02, (mod -> mod.getAdministration() == COMMON_TERRITORY)
				, (rc -> rc.isAEAT() && rc.isSalary() && !rc.isInKind() && rc.isPerception() ))
		,CT_C03(Mod111Key.CT_C03, (mod -> mod.getAdministration() == COMMON_TERRITORY)
				, (rc -> rc.isAEAT() && rc.isSalary() && !rc.isInKind() && rc.isRetention() ))
		,CT_C04(Mod111Key.CT_C04, (mod -> mod.getAdministration() == COMMON_TERRITORY)
				, (rc -> rc.isAEAT() && rc.isSalary() && rc.isInKind() && rc.isPerceptorCount() ))
		,CT_C05(Mod111Key.CT_C05, (mod -> mod.getAdministration() == COMMON_TERRITORY)
				, (rc -> rc.isAEAT() && rc.isSalary() && rc.isInKind() && rc.isPerception() ))
		,CT_C06(Mod111Key.CT_C06, (mod -> mod.getAdministration() == COMMON_TERRITORY)
				, (rc -> rc.isAEAT() && rc.isSalary() && rc.isInKind() && rc.isRetention() ))
		,CT_C07(Mod111Key.CT_C07, (mod -> mod.getAdministration() == COMMON_TERRITORY)
				, (rc -> rc.isAEAT() && !rc.isSalary() && rc.isPerceptorCount() ))
		,CT_C08(Mod111Key.CT_C08, (mod -> mod.getAdministration() == COMMON_TERRITORY)
				, (rc -> rc.isAEAT() && !rc.isSalary() && rc.isPerception() ))
		,CT_C09(Mod111Key.CT_C09, (mod -> mod.getAdministration() == COMMON_TERRITORY)
				, (rc -> rc.isAEAT() && !rc.isSalary() && rc.isRetention() ))
		,CT_C10(Mod111Key.CT_C10, (mod -> mod.getAdministration() == COMMON_TERRITORY))
		,CT_C11(Mod111Key.CT_C11, (mod -> mod.getAdministration() == COMMON_TERRITORY))
		,CT_C12(Mod111Key.CT_C12, (mod -> mod.getAdministration() == COMMON_TERRITORY))
		,CT_C13(Mod111Key.CT_C13, (mod -> mod.getAdministration() == COMMON_TERRITORY))
		,CT_C14(Mod111Key.CT_C14, (mod -> mod.getAdministration() == COMMON_TERRITORY))
		,CT_C15(Mod111Key.CT_C15, (mod -> mod.getAdministration() == COMMON_TERRITORY))
		,CT_C16(Mod111Key.CT_C16, (mod -> mod.getAdministration() == COMMON_TERRITORY))
		,CT_C17(Mod111Key.CT_C17, (mod -> mod.getAdministration() == COMMON_TERRITORY))
		,CT_C18(Mod111Key.CT_C18, (mod -> mod.getAdministration() == COMMON_TERRITORY))
		,CT_C19(Mod111Key.CT_C19, (mod -> mod.getAdministration() == COMMON_TERRITORY))
		,CT_C20(Mod111Key.CT_C20, (mod -> mod.getAdministration() == COMMON_TERRITORY))
		,CT_C21(Mod111Key.CT_C21, (mod -> mod.getAdministration() == COMMON_TERRITORY))
		,CT_C22(Mod111Key.CT_C22, (mod -> mod.getAdministration() == COMMON_TERRITORY))
		,CT_C23(Mod111Key.CT_C23, (mod -> mod.getAdministration() == COMMON_TERRITORY))
		,CT_C24(Mod111Key.CT_C24, (mod -> mod.getAdministration() == COMMON_TERRITORY))
		,CT_C25(Mod111Key.CT_C25, (mod -> mod.getAdministration() == COMMON_TERRITORY))
		,CT_C26(Mod111Key.CT_C26, (mod -> mod.getAdministration() == COMMON_TERRITORY))
		,CT_C27(Mod111Key.CT_C27, (mod -> mod.getAdministration() == COMMON_TERRITORY))
		,CT_C28(Mod111Key.CT_C28, (mod -> mod.getAdministration() == COMMON_TERRITORY)
				, "CT_C03+CT_C06+CT_C09+CT_C12+CT_C15+CT_C18+CT_C21+CT_C24" )
		,CT_C29(Mod111Key.CT_C29, (mod -> mod.getAdministration() == COMMON_TERRITORY))
		,CT_C30(Mod111Key.CT_C30, (mod -> mod.getAdministration() == COMMON_TERRITORY)
				, "CT_C28-CT_C29" )
		
		// *************************************************************************
		// ************************************************************ GIPUZKOA ***
		// *************************************************************************
		,GP_C01(Mod111Key.GP_C01, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C02(Mod111Key.GP_C02, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C03(Mod111Key.GP_C03, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C04(Mod111Key.GP_C04, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C05(Mod111Key.GP_C05, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C06(Mod111Key.GP_C06, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C07(Mod111Key.GP_C07, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C08(Mod111Key.GP_C08, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C09(Mod111Key.GP_C09, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C10(Mod111Key.GP_C10, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C11(Mod111Key.GP_C11, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C12(Mod111Key.GP_C12, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C13(Mod111Key.GP_C13, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C14(Mod111Key.GP_C14, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C15(Mod111Key.GP_C15, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C16(Mod111Key.GP_C16, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C17(Mod111Key.GP_C17, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C18(Mod111Key.GP_C18, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C19(Mod111Key.GP_C19, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C20(Mod111Key.GP_C20, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C21(Mod111Key.GP_C21, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C22(Mod111Key.GP_C22, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C23(Mod111Key.GP_C23, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C24(Mod111Key.GP_C24, (mod -> mod.getAdministration() == GIPUZKOA))
		,GP_C25(Mod111Key.GP_C25, (mod -> mod.getAdministration() == GIPUZKOA))

		// *************************************************************************
		// ************************************************************* NAVARRA ***
		// *************************************************************************
		,NF_A1(Mod111Key.NF_A1, (mod -> mod.getAdministration() == NAVARRA))
		;
		
		private Mod111Key key;
		private IModelAccepter acceptModel;
		private IValueAccepter acceptValue;
		private String expression;

		private Mod111KeyDAO(Mod111Key key, IModelAccepter acceptModel) {
			this(key,acceptModel,null,null);
		}
		private Mod111KeyDAO(Mod111Key key, IModelAccepter acceptModel, IValueAccepter acceptValue) {
			this(key,acceptModel,acceptValue,null);
		}
		private Mod111KeyDAO(Mod111Key key, IModelAccepter acceptModel,String expression) {
			this(key,acceptModel,null,expression);
		}
		private Mod111KeyDAO(Mod111Key key, IModelAccepter acceptModel, IValueAccepter acceptValue,String expression) {
			this.key = key;
			this.acceptModel =  acceptModel;
			this.acceptValue =  acceptValue;
			this.expression =  expression;
		}
		
		public Mod111Key getKey() {
			return key;
		}
		public boolean acceptModel(Mod111 mod) {
			return  (acceptModel.accept(mod));
		}
		public boolean acceptValue(RetentionContext rc) {
			return  acceptValue != null && acceptValue.accept(rc);
		}
		public String getExpression() {
			return expression;
		}
	}

	public static Stream<Mod111> getMod111s(AONContext ctx,int domain) {
		return ctx.getDslContext().selectFrom(FS_MODEL)
				.where(FS_MODEL.DOMAIN.eq(domain))
				.and(FS_MODEL.MODEL.eq( FiscalModelType.M111.getValue() ))
				.orderBy(FS_MODEL.YEAR.desc(),FS_MODEL.MODEL.asc(),FS_MODEL.PERIOD.desc())
				.fetch()
				.stream()
				.map( record -> Mod111DAO.map(new Mod111(),record));
	}
	
	public static Mod111 getMod111(AONContext ctx,int id) {
		ctx.checkRead();
		FsModelRecord record = ctx.getDslContext().selectFrom(FS_MODEL)
			.where(FS_MODEL.ID.eq(id))
			.fetchOne();
		if (record != null) {
			Mod111 mod111 = new Mod111(); 
			populate(mod111,record);
			fillModelDetails(ctx,mod111);
			return mod111;
		}
		return null;
	}
	
	public static Mod111 saveMod111(AONContext ctx, Mod111 mod111) {
		calculateMod111(ctx, mod111);
		FiscalModel fm = save(ctx, mod111);
		return getMod111(ctx, fm.getId());
	}
	
	public static Mod111 calculateMod111(AONContext ctx, Mod111 mod111) {
		LinkedHashMap<String, Object> mvelCtx = new LinkedHashMap<String, Object>();
		for (String key : mod111.getMap().keySet()) {
			Mod111Key mod111Key = Mod111Key.getKey(key);
			if (mod111Key != null) {
				FiscalModelDetail detail = mod111.getMap().get(key);
				mvelCtx.put(mod111Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}
		for (Mod111KeyDAO key : Mod111KeyDAO.values()) {
			
			if (AonStringUtils.isNotEmpty( key.getExpression()) && key.acceptModel(mod111)) {
				Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.getKey().toString(), amount);
				mod111.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		return mod111; 
	}
	
	public static Mod111 initializeMod111(AONContext ctx,Mod111 mod111) {
		if (mod111 == null) {
			mod111 = new Mod111();
		}
		initializeFiscalModel(ctx, mod111);
		return mod111;
	}
	
	public static Mod111 createMod111(AONContext ctx,Mod111 mod111) {
		for (Mod111KeyDAO key : Mod111KeyDAO.values()) {
			if (key.acceptModel(mod111)) {
				FiscalModelDetail detail = mod111.ensureDetail(key.getKey());
				detail.setExpression(key.getExpression());
			}
		}
		createFromInvoices(ctx,mod111);
		createFromSalary(ctx,mod111);
		return calculateMod111(ctx, mod111);
	}

	private static void createFromSalary(AONContext ctx, Mod111 mod111) {
		java.sql.Date dateFrom = AonDateUtils.toSql( getPeriodStart(mod111));	
		java.sql.Date dateTo = AonDateUtils.toSql( getPeriodEnd(mod111));
		
		final RetentionContext rc1 = new RetentionContext(mod111).setSalary(true).setPerceptorCount(true);
		final RetentionContext rc2 = new RetentionContext(mod111).setSalary(true).setPerception(true);
		final RetentionContext rc3 = new RetentionContext(mod111).setSalary(true).setRetention(true);
		final RetentionContext rcik1 = new RetentionContext(mod111).setSalary(true).setInKind(true).setPerceptorCount(true);
		final RetentionContext rcik2 = new RetentionContext(mod111).setSalary(true).setInKind(true).setPerception(true);
		final RetentionContext rcik3 = new RetentionContext(mod111).setSalary(true).setInKind(true).setRetention(true);
		
		Set<String> documents = new HashSet<String>();
		ctx.getDslContext()
				.select(SALARY.EMPLOYEE_DOCUMENT,
						SALARY.IRPF_BASE, 
						SALARY.MONEY_IRPF_BASE, 
						SALARY.INKIND_IRPF_BASE,
						SALARY.TOTAL_IRPF)
				.from(SALARY)
				.join(CONTRACT).on(SALARY.CONTRACT.equal(CONTRACT.ID))
				.join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
				.where(SALARY.DOMAIN.equal(mod111.getDomain()))
				.and(SALARY.ISSUE_DATE.between(dateFrom,dateTo))
				.and(WORKPLACE.ECONOMICAGREEMENT.equal(mod111.getAdministration().getValue()))
				.fetch()
				.stream()
				.map( new IrpfSalaryBreakdown() )
				.forEach(
						br -> {
							int perceptor = 0;
							int inKindPerceptor = 0;
							if ( !documents.contains(br.getDocument()) ) { 
								documents.add(br.getDocument());
								perceptor = perceptor + (br.isMoneyRetention()?1:0);
								inKindPerceptor = inKindPerceptor + (br.isInKindRetention()?1:0);
							}

							for (Mod111KeyDAO key : Mod111KeyDAO.values()) {
								if (key.acceptValue(rc1)) mod111.ensureDetail(key.getKey()).addAmount(perceptor);
								if (key.acceptValue(rc2)) mod111.ensureDetail(key.getKey()).addAmount(br.getMoneyBase());
								if (key.acceptValue(rc3)) mod111.ensureDetail(key.getKey()).addAmount(br.getMoneyQuota());
								if (key.acceptValue(rcik1)) mod111.ensureDetail(key.getKey()).addAmount(inKindPerceptor);
								if (key.acceptValue(rcik2)) mod111.ensureDetail(key.getKey()).addAmount(br.getInKindBase());
								if (key.acceptValue(rcik3)) mod111.ensureDetail(key.getKey()).addAmount(br.getInKindQuota());
							}
							
						});
	}

	private static Date getPeriodStart( FiscalModel fiscalModel) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, fiscalModel.getYear());
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MONTH, fiscalModel.getPeriod().getStartMonth());
		return c.getTime();
	}
	
	private static Date getPeriodEnd( FiscalModel fiscalModel) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, fiscalModel.getPeriod().getDueMonth() + 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.YEAR, fiscalModel.getYear());
		c.add(Calendar.DAY_OF_MONTH, -1);
		return c.getTime();
	}

	public static String getMod111Info(AONContext ctx, Mod111 mod111, Mod111Key key, Mod111KeyInfo infoKey) {
		Mod111KeyInfoDAO k = Mod111KeyInfoDAO.valueOf(infoKey.toString());
		return k.getInfo(ctx, mod111, key);
	}

	private static String getSalaryInfo(AONContext ctx, Mod111 mod111, Mod111Key key) {
		Date start = AonDateUtils.getDate(mod111.getYear(), mod111.getPeriod().getStartMonth(), 1);
		java.sql.Date dateFrom = AonDateUtils.toSql(start);
		Date end = AonDateUtils.getDate(mod111.getYear(), mod111.getPeriod().getDueMonth(), 1);
		end = AonDateUtils.getMonthLastDay(end);
		java.sql.Date dateTo = AonDateUtils.toSql(end);		
		
		return IRPFFormatter.formatSalaries(ctx.getDslContext()
			.select(SALARY.ISSUE_DATE,
				SALARY.EMPLOYEE_DOCUMENT,
				SALARY.EMPLOYEE_NAME,
				SALARY.IRPF_BASE, 
				SALARY.MONEY_IRPF_BASE, 
				SALARY.INKIND_IRPF_BASE,
				SALARY.TOTAL_IRPF)
			.from(SALARY)
			.join(CONTRACT).on(SALARY.CONTRACT.equal(CONTRACT.ID))
			.join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
			.where(SALARY.DOMAIN.equal(mod111.getDomain()))
			.and(SALARY.ISSUE_DATE.between(dateFrom,dateTo))
			.and(WORKPLACE.ECONOMICAGREEMENT.equal(mod111.getAdministration().getValue()))
			.orderBy(SALARY.EMPLOYEE_DOCUMENT, SALARY.ISSUE_DATE)
			.fetch()
			.stream()
			.map( new IrpfSalaryBreakdown() )
			.collect(Collectors.toCollection(LinkedList::new))
		);
	}
	
	private static class IrpfSalaryBreakdown implements Function<Record, IrpfBreakdown> {
		@Override
		public IrpfBreakdown apply(Record rec) {
			double base = rec.getValue(SALARY.IRPF_BASE);
			double quota = rec.getValue(SALARY.TOTAL_IRPF);
			double moneyBase = base;
			double moneyQuota = quota;
			double inKindBase = rec.getValue(SALARY.INKIND_IRPF_BASE);
			double inKindQuota = 0;
			if (inKindBase != 0) {
				moneyBase = rec.getValue(SALARY.MONEY_IRPF_BASE);
				moneyQuota = AonMathUtils.round( moneyBase * quota  / base ); 	
				inKindQuota = AonMathUtils.round( quota - moneyQuota);
			} else {
				moneyBase = base;
				moneyQuota = quota;
			}
			
			return new IrpfBreakdown()
					.setDocument(rec.getValue(SALARY.EMPLOYEE_DOCUMENT))
					.setName(rec.field(SALARY.EMPLOYEE_NAME) != null ? rec.getValue(SALARY.EMPLOYEE_NAME) : null)
					.setIssueDate(rec.field(SALARY.ISSUE_DATE) != null ? rec.getValue(SALARY.ISSUE_DATE): null)
					.setMoneyBase(moneyBase)
					.setMoneyQuota(moneyQuota)
					.setInKindBase(inKindBase)
					.setInKindQuota(inKindQuota)
					;
		}
	}

	private static void createFromInvoices(AONContext ctx, final Mod111 mod111) {

		java.sql.Date dateFrom = AonDateUtils.toSql( getPeriodStart(mod111));	
		java.sql.Date dateTo = AonDateUtils.toSql( getPeriodEnd(mod111));

		Field<BigDecimal> sumBase = DSL.sum(INVOICE_TAX.BASE);
		Field<Double> quotaOp = DSL.round((INVOICE_TAX.BASE.mul(INVOICE_TAX.PERCENTAGE)).div(100), 2);
		Field<BigDecimal> quotaOpSum = DSL.sum(DSL.decode()
				.when(INVOICE_TAX.QUOTA.notEqual(0.0), INVOICE_TAX.QUOTA)
				.when(INVOICE_TAX.QUOTA.equal(0.0), quotaOp));
		ctx.getDslContext()
				.select(INVOICE.RDOCUMENT, INVOICE_TAX.WITHHOLDING_TYPE, sumBase,quotaOpSum)
				.from(INVOICE)
				.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
				.where(INVOICE.DOMAIN.equal(mod111.getDomain()))
				.and(INVOICE.TYPE.notEqual(InvoiceType.SALES.value() ))
				.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.RETENTION.value()))
				.and(INVOICE_TAX.WITHHOLDING_TYPE.in(
						 WithholdingType.PROFESSIONAL.value()
						,WithholdingType.FARMER.value()
						,WithholdingType.TRANSPORT_OPERATOR.value())) 
				.and(INVOICE.ISSUE_DATE.between(dateFrom,dateTo))
				.groupBy(INVOICE.RDOCUMENT, INVOICE_TAX.WITHHOLDING_TYPE)
				.fetch()
				.stream()
				.forEach(
						rec -> {
							WithholdingType type = WithholdingType.values()[rec.getValue(INVOICE_TAX.WITHHOLDING_TYPE)];
							double base = rec.getValue( sumBase ).doubleValue();
							double retention = rec.getValue( quotaOpSum ).doubleValue();
							RetentionContext rc1 = new RetentionContext(mod111)
									.setWithholdingType(type).setPerceptorCount(true);
							RetentionContext rc2 = new RetentionContext(mod111)
									.setWithholdingType(type).setPerception(true);
							RetentionContext rc3 = new RetentionContext(mod111)
									.setWithholdingType(type).setRetention(true);
							
							for (Mod111KeyDAO key : Mod111KeyDAO.values()) {
								if (key.acceptValue(rc1)) {
									mod111.ensureDetail(key.getKey()).addAmount(1.0);
								}
								if (key.acceptValue(rc2)) {
									mod111.ensureDetail(key.getKey()).addAmount(base);
								}
								if (key.acceptValue(rc3)) {
									mod111.ensureDetail(key.getKey()).addAmount(retention);
								}
							}
						});
	}
	
	private static String getInvoicesInfo(AONContext ctx, Mod111 mod111, Mod111Key key) {
		
		Date start = AonDateUtils.getDate(mod111.getYear(), mod111.getPeriod().getStartMonth(), 1);
		java.sql.Date dateFrom = AonDateUtils.toSql(start);
		Date end = AonDateUtils.getDate(mod111.getYear(), mod111.getPeriod().getDueMonth(), 1);
		end = AonDateUtils.getMonthLastDay(end);
		java.sql.Date dateTo = AonDateUtils.toSql(end);		

		return IRPFFormatter.formatInvoices(ctx.getDslContext()
			.select(INVOICE.ID
				,INVOICE.TYPE,INVOICE.SERIES,INVOICE.NUMBER,INVOICE.REFERENCE_CODE
				,INVOICE.ISSUE_DATE,INVOICE.TAX_DATE
				,INVOICE.RDOCUMENT,INVOICE.RNAME
				,INVOICE_TAX.WITHHOLDING_TYPE
				,INVOICE_TAX.BASE,INVOICE_TAX.PERCENTAGE,INVOICE_TAX.QUOTA)
			.from(INVOICE)
			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
			.where(INVOICE.DOMAIN.equal(mod111.getDomain()))
			.and(INVOICE.TYPE.notEqual(InvoiceType.SALES.value() ))
			.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.RETENTION.value()))
			.and(INVOICE_TAX.WITHHOLDING_TYPE.in(
					 WithholdingType.PROFESSIONAL.value()
					,WithholdingType.FARMER.value()
					,WithholdingType.TRANSPORT_OPERATOR.value())) 
			.and(INVOICE.TAX_DATE.between(dateFrom,dateTo))
			.orderBy(INVOICE.RDOCUMENT,INVOICE.ISSUE_DATE, INVOICE_TAX.WITHHOLDING_TYPE)
			.fetch()
			.stream()
			.map( new IrpfInvoiceBreakdown() )
			.collect(Collectors.toCollection(LinkedList::new)
		));
	}

	private static class IrpfInvoiceBreakdown implements Function<Record, IrpfBreakdown> {
		@Override
		public IrpfBreakdown apply(Record rec) {
			double base = rec.getValue(INVOICE_TAX.BASE);
			double percent = rec.getValue(INVOICE_TAX.PERCENTAGE);
			double quota = rec.getValue(INVOICE_TAX.QUOTA);
			if (AonMathUtils.isZero(quota)) {
				quota =  AonMathUtils.round(base * percent / 100);
			}
			return new IrpfBreakdown()
					.setInvoice(rec.getValue(INVOICE.ID))
					.setInvoiceType(AonEnumUtils.enumValue(InvoiceType.class,rec.getValue(INVOICE.TYPE)))
					.setSeries(rec.getValue(INVOICE.SERIES))
					.setNumber(rec.getValue(INVOICE.NUMBER))
					.setReferenceCode(rec.getValue(INVOICE.REFERENCE_CODE))
					.setDocument(rec.getValue(INVOICE.RDOCUMENT))
					.setName(rec.getValue(INVOICE.RNAME))
					.setIssueDate(rec.getValue(INVOICE.ISSUE_DATE))
					.setTaxDate(rec.getValue(INVOICE.TAX_DATE))
					.setWithholdingType(AonEnumUtils.enumValue(WithholdingType.class,rec.getValue(INVOICE_TAX.WITHHOLDING_TYPE)))
					.setBase(base)
					.setPercent(percent)
					.setQuota(quota)
					;
		}
	}

	
}
