package net.aonsolutions.vat.change;

import static com.esferalia.aon.jooq.tables.FsVat.FS_VAT;
import static com.esferalia.aon.jooq.tables.FsVatDeclaration.FS_VAT_DECLARATION;
import static com.esferalia.aon.jooq.tables.FsVatDetail.FS_VAT_DETAIL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;

import org.jooq.Record;
import org.jooq.conf.ParamType;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.Mod303DAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod303.IMod303KeyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod303.Mod303Declaration;
import com.esferalia.aon.watson.server.AonDatabaseUtil;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class VatToMod303 {
	
	private static String password = "password";
	private static String user = "root";
	private static String url = "jdbc:mysql://127.0.0.1:3306/pro-aonsolutions-net";
	
	private static int count;

	private static String getPassword() {
		return password;
	}
	private static String getUser() {
		return user;
	}
	private static String getUrl() {
		return url;
	}
	

	public static void main(String[] args) throws ClassNotFoundException, SQLException  {
		Date start = new Date();
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		Connection c = DriverManager.getConnection(getUrl(),getUser(),getPassword());
		AONContext ctx = new AONContext(c);
		try {
			ctx.getDslContext().settings().setRenderSchema(false);
			ctx.getDslContext().settings().setParamType( ParamType.INLINED );
			log("Connected!");
			ctx.getDslContext().transaction( configuration -> passVatTax(ctx) );
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			AonDatabaseUtil.closeQuietly(c);
			ctx.close();
			System.out.println( ((new Date()).getTime() - start.getTime() ) + " ms.");
		}
	}

	private static void log(String msg) {
		System.out.println( msg );		
	}

	private static void passVatTax(AONContext ctx) {
		ctx.getDslContext().select()
			.from(FS_VAT)
			.leftOuterJoin(FS_VAT_DECLARATION).on(FS_VAT_DECLARATION.FS_VAT.eq(FS_VAT.ID))
			.where( FS_VAT.YEAR.gt(2013))
//			.where( FS_VAT.DOMAIN.eq(802))
//			.limit(1000)
			.fetch()
			.stream()
			
			.filter(rec -> !AonNumberUtils.equals(rec.getValue(FS_VAT_DECLARATION.ADMINISTRATION),Administration.NAVARRA.getValue()))
			.map( rec -> {
				count++;
				Mod303 mod = new Mod303(); 
				mod.setDomain(rec.getValue(FS_VAT.DOMAIN) )
					.setYear(rec.getValue(FS_VAT.YEAR) )
					.setPeriod( com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Period.class, rec.getValue(FS_VAT.PERIOD) ))
					.setAdministration( (Administration.safeValueOf(rec.getValue(FS_VAT_DECLARATION.ADMINISTRATION)) != null)
							? Administration.safeValueOf(rec.getValue(FS_VAT_DECLARATION.ADMINISTRATION)) 
									: Administration.COMMON_TERRITORY )
					.setComplementary(AonEnumUtils.getBoolean(rec.getValue(FS_VAT.COMPLEMENTARY)))
					.setReplacement(AonEnumUtils.getBoolean(rec.getValue(FS_VAT.REPLACEMENT)))
					.setReplacedNumber(rec.getValue(FS_VAT.REPLACED_NUMBER) )
					.setConfidential( com.esferalia.aon.watson.util.AonEnumUtils.enumValue(SecurityLevel.class,rec.getValue(FS_VAT.SECURITY_LEVEL)) == SecurityLevel.CONFIDENTIAL)
					;
				mod = Mod303DAO.initializeMod303(ctx, mod);
				mod.setDocument( AonStringUtils.substring(mod.getDocument(), 0, 9) );
				mod.setName( AonStringUtils.substring(mod.getName(), 0, 45) );
				mod.setSurname( AonStringUtils.substring(mod.getSurname(), 0, 30) );
				mod.setPhone( AonStringUtils.substring(mod.getPhone(), 0, 9) );
				mod.setStreetNumber(AonStringUtils.substring(mod.getStreetNumber(), 0, 4) );
				mod.setZip(AonStringUtils.substring(mod.getZip(), 0, 5) );
				System.out.print(".");
				if (count % 100 == 0) System.out.println( " ----> " + count);
				Mod303Declaration dec = Mod303DeclarationCHANGE.getInstance(mod);
				for (IMod303KeyDAO key : dec.getKeys()) {
					mod.ensureDetail(key.getKey());
				}
				mod.setComments(rec.getValue(FS_VAT.COMMENTS));
				mod.setStatus( FiscalStatus.BLOCKED);
				mod.putAmount(Mod303Key.CM_003, rec.getValue(FS_VAT.PRORATA));
				mod.putAmount(Mod303Key.CM_002, rec.getValue(FS_VAT.TAX_REFUND_REGISTRY));
				if (mod.isAEAT()) fillAEATDetails( ctx, mod, rec);
				if (mod.isBizkaia()) fillBizkaiaDetails( ctx, mod, rec);
				if (mod.isAraba()) fillArabaDetails( ctx, mod, rec);
				if (mod.isGipuzkoa()) fillGipuzkoaDetails( ctx, mod, rec);
				return mod;
			}
		)
		.forEach( mod -> {
			double result0 = mod.getResult(); 
			Mod303 calculated = Mod303DAO.calculateMod303(ctx, mod,Mod303DeclarationCHANGE.getInstance(mod));
			double result1 = calculated.getResult();
			if ( result0 != result1 ) {
				System.out.println();
				log( count 
						+ "\t" + mod.getDomain() 
						+ "\t" + mod.getYear() 
						+ "\t" + mod.getPeriod() 
						+ "\t" + AonStringUtils.substring( mod.getAdministration().getDescription(), 0, 4)						
						+ "\t" + mod.getDocument()
						+ "\t" + mod.getName()
						+ "\t" + "DIFERENTE ..: " + result0 +  " <> " + result1
						);
			}
			Mod303DAO.save(ctx, mod);
		});
	}
	private static double ensure(Double value) {
		return value==null?0.0:value.doubleValue();
	}

	private static void fillAEATDetails(AONContext ctx, Mod303 mod, Record rec) {
		ctx.getDslContext().select()
		.from(FS_VAT_DETAIL)
		.where(FS_VAT_DETAIL.FS_VAT.eq( rec.getValue(FS_VAT.ID)) )
		.fetch()
		.stream()
		.map(recDet -> (new VatTaxDetail())
				.setId(recDet.getValue(FS_VAT_DETAIL.ID))
				.setVatTax(recDet.getValue(FS_VAT_DETAIL.FS_VAT))
				.setDomain(recDet.getValue(FS_VAT_DETAIL.DOMAIN)) 
				.setKey(recDet.getValue(FS_VAT_DETAIL.VAT_KEY))
				
				.setPercent(recDet.getValue(FS_VAT_DETAIL.PERCENT))
				
				.setTaxableBaseAccumulated(recDet.getValue(FS_VAT_DETAIL.ACU_TAXABLE_BASE))
				.setTaxableBaseDeclared(recDet.getValue(FS_VAT_DETAIL.DEC_TAXABLE_BASE))
				.setTaxableBaseResult(recDet.getValue(FS_VAT_DETAIL.RES_TAXABLE_BASE))
				.setTaxableBaseAdjust(recDet.getValue(FS_VAT_DETAIL.ADJ_TAXABLE_BASE))
				.setTaxableBase(recDet.getValue(FS_VAT_DETAIL.TAXABLE_BASE))
				
				.setQuotaAccumulated(recDet.getValue(FS_VAT_DETAIL.ACU_QUOTA))
				.setQuotaDeclared(recDet.getValue(FS_VAT_DETAIL.DEC_QUOTA))
				.setQuotaResult( recDet.getValue(FS_VAT_DETAIL.RES_QUOTA))
				.setQuotaAdjust(recDet.getValue(FS_VAT_DETAIL.ADJ_QUOTA))
				.setQuota(recDet.getValue(FS_VAT_DETAIL.QUOTA))
				
				.setDeductibleQuotaAccumulated(recDet.getValue(FS_VAT_DETAIL.ACU_DEDUCTIBLE_QUOTA))
				.setDeductibleQuotaDeclared(recDet.getValue(FS_VAT_DETAIL.DEC_DEDUCTIBLE_QUOTA))
				.setDeductibleQuotaResult( recDet.getValue(FS_VAT_DETAIL.RES_DEDUCTIBLE_QUOTA))
				.setDeductibleQuotaAdjust(recDet.getValue(FS_VAT_DETAIL.ADJ_DEDUCTIBLE_QUOTA))
				.setDeductibleQuota(recDet.getValue(FS_VAT_DETAIL.DEDUCTIBLE_QUOTA))
				)
		.forEach(det -> VatTaxKey.AEAT_KEY_MAP.get(det.getKey()).accept(mod, det) );
		
		mod.ensureDetail(Mod303Key.CT_C65).addAmount( ensure(rec.getValue(FS_VAT_DECLARATION.PERCENT) ));
		mod.ensureDetail(Mod303Key.CT_C66).addAmount( ensure(rec.getValue(FS_VAT_DECLARATION.QUOTA) ));
		mod.ensureDetail(Mod303Key.CT_C67).addAmount( ensure(rec.getValue(FS_VAT_DECLARATION.PREV_YEAR_COMPENSATE_QUOTA) ));
		mod.ensureDetail(Mod303Key.CT_C70).addAmount( ensure(rec.getValue(FS_VAT_DECLARATION.DONE_REFUNDS) ));
		mod.ensureDetail(Mod303Key.CT_C70).addAmount( ensure(rec.getValue(FS_VAT_DECLARATION.DONE_DEPOSITS)) * (-1) );
		
		double compensate = ensure(rec.getValue(FS_VAT_DECLARATION.COMPENSATE));
		double pay_back = ensure(rec.getValue(FS_VAT_DECLARATION.PAY_BACK));
		double deposit = ensure(rec.getValue(FS_VAT_DECLARATION.DEPOSIT));
		if (pay_back > 0.0)   {
			mod.ensureDetail(Mod303Key.CT_C71).addAmount( pay_back * -1 );
			mod.putDescription(Mod303Key.CM_004 , FiscalModelDeclarationType.PAYBACK.getValue()); 
		} else if (compensate > 0.0)   {
			mod.ensureDetail(Mod303Key.CT_C71).addAmount( compensate * -1 );
			mod.putDescription(Mod303Key.CM_004 , FiscalModelDeclarationType.COMPENSATE.getValue()); 
		} else {
			mod.ensureDetail(Mod303Key.CT_C71).addAmount( deposit );
			mod.putDescription(Mod303Key.CM_004 , FiscalModelDeclarationType.DEPOSIT.getValue()); 
		}
		
		mod.putAmount(Mod303Key.CM_005, 0);	// Solo regimen general.
		mod.putAmount(Mod303Key.CT_A02, 2);	// Solo regimen general.		
	}
	
	private static void fillArabaDetails(AONContext ctx, Mod303 mod, Record rec) {
		ctx.getDslContext().select()
		.from(FS_VAT_DETAIL)
		.where(FS_VAT_DETAIL.FS_VAT.eq( rec.getValue(FS_VAT.ID)) )
		.fetch()
		.stream()
		.map(recDet -> (new VatTaxDetail())
				.setId(recDet.getValue(FS_VAT_DETAIL.ID))
				.setVatTax(recDet.getValue(FS_VAT_DETAIL.FS_VAT))
				.setDomain(recDet.getValue(FS_VAT_DETAIL.DOMAIN)) 
				.setKey(recDet.getValue(FS_VAT_DETAIL.VAT_KEY))
				
				.setPercent(recDet.getValue(FS_VAT_DETAIL.PERCENT))
				
				.setTaxableBaseAccumulated(recDet.getValue(FS_VAT_DETAIL.ACU_TAXABLE_BASE))
				.setTaxableBaseDeclared(recDet.getValue(FS_VAT_DETAIL.DEC_TAXABLE_BASE))
				.setTaxableBaseResult(recDet.getValue(FS_VAT_DETAIL.RES_TAXABLE_BASE))
				.setTaxableBaseAdjust(recDet.getValue(FS_VAT_DETAIL.ADJ_TAXABLE_BASE))
				.setTaxableBase(recDet.getValue(FS_VAT_DETAIL.TAXABLE_BASE))
				
				.setQuotaAccumulated(recDet.getValue(FS_VAT_DETAIL.ACU_QUOTA))
				.setQuotaDeclared(recDet.getValue(FS_VAT_DETAIL.DEC_QUOTA))
				.setQuotaResult( recDet.getValue(FS_VAT_DETAIL.RES_QUOTA))
				.setQuotaAdjust(recDet.getValue(FS_VAT_DETAIL.ADJ_QUOTA))
				.setQuota(recDet.getValue(FS_VAT_DETAIL.QUOTA))
				
				.setDeductibleQuotaAccumulated(recDet.getValue(FS_VAT_DETAIL.ACU_DEDUCTIBLE_QUOTA))
				.setDeductibleQuotaDeclared(recDet.getValue(FS_VAT_DETAIL.DEC_DEDUCTIBLE_QUOTA))
				.setDeductibleQuotaResult( recDet.getValue(FS_VAT_DETAIL.RES_DEDUCTIBLE_QUOTA))
				.setDeductibleQuotaAdjust(recDet.getValue(FS_VAT_DETAIL.ADJ_DEDUCTIBLE_QUOTA))
				.setDeductibleQuota(recDet.getValue(FS_VAT_DETAIL.DEDUCTIBLE_QUOTA))
				)
		.forEach(det -> {
			VatTaxKey.ARABA_KEY_MAP.get(det.getKey()).accept(mod, det);	
		});

		
		mod.ensureDetail(Mod303Key.AR_C040).addAmount( ensure(rec.getValue(FS_VAT_DECLARATION.PERCENT) ));
		double quota = ensure(rec.getValue(FS_VAT_DECLARATION.QUOTA) );
		mod.ensureDetail(Mod303Key.AR_C044).addAmount( quota );
		double prevCompensate = ensure(rec.getValue(FS_VAT_DECLARATION.PREV_YEAR_COMPENSATE_QUOTA) );
		mod.ensureDetail(Mod303Key.AR_C045).addAmount( prevCompensate );
		
		mod.ensureDetail(Mod303Key.AR_C060).addAmount( AonMathUtils.round(quota - prevCompensate) );
		

		mod.ensureDetail(Mod303Key.AR_C061).addAmount( ensure(rec.getValue(FS_VAT_DECLARATION.EXTRA_CHARGE) ));
		mod.ensureDetail(Mod303Key.AR_C062).addAmount( ensure(rec.getValue(FS_VAT_DECLARATION.DELAY_INTEREST) ));
		mod.ensureDetail(Mod303Key.AR_C063).addAmount( ensure(rec.getValue(FS_VAT_DECLARATION.DONE_REFUNDS) ));
		mod.ensureDetail(Mod303Key.AR_C063).addAmount( ensure(rec.getValue(FS_VAT_DECLARATION.DONE_DEPOSITS)) * (-1) );

		double compensate = ensure(rec.getValue(FS_VAT_DECLARATION.COMPENSATE));
		double pay_back = ensure(rec.getValue(FS_VAT_DECLARATION.PAY_BACK));
		double deposit = ensure(rec.getValue(FS_VAT_DECLARATION.DEPOSIT));
		if (pay_back > 0.0)   {
			mod.ensureDetail(Mod303Key.AR_C080).addAmount( pay_back * -1 );
			mod.putDescription(Mod303Key.CM_004 , FiscalModelDeclarationType.PAYBACK.getValue()); 
		} else if (compensate > 0.0)   {
			mod.ensureDetail(Mod303Key.AR_C080).addAmount( compensate * -1 );
			mod.putDescription(Mod303Key.CM_004 , FiscalModelDeclarationType.COMPENSATE.getValue()); 
		} else {
			mod.ensureDetail(Mod303Key.AR_C080).addAmount( deposit );
			mod.putDescription(Mod303Key.CM_004 , FiscalModelDeclarationType.DEPOSIT.getValue()); 
		}
		
		mod.putAmount(Mod303Key.AR_C911, AonMathUtils.isNotZero( mod.getAmount(Mod303Key.AR_C183) )?1:0); // Es destinatario de operaciones a las que se aplique el regimen especial del criterio de caja?
		mod.putAmount(Mod303Key.CM_005, 0);	// Solo regimen general.
	}
	
	private static void fillGipuzkoaDetails(AONContext ctx, Mod303 mod, Record rec) {
		ctx.getDslContext().select()
		.from(FS_VAT_DETAIL)
		.where(FS_VAT_DETAIL.FS_VAT.eq( rec.getValue(FS_VAT.ID)) )
		.fetch()
		.stream()
		.map(recDet -> (new VatTaxDetail())
				.setId(recDet.getValue(FS_VAT_DETAIL.ID))
				.setVatTax(recDet.getValue(FS_VAT_DETAIL.FS_VAT))
				.setDomain(recDet.getValue(FS_VAT_DETAIL.DOMAIN)) 
				.setKey(recDet.getValue(FS_VAT_DETAIL.VAT_KEY))
				
				.setPercent(recDet.getValue(FS_VAT_DETAIL.PERCENT))
				
				.setTaxableBaseAccumulated(recDet.getValue(FS_VAT_DETAIL.ACU_TAXABLE_BASE))
				.setTaxableBaseDeclared(recDet.getValue(FS_VAT_DETAIL.DEC_TAXABLE_BASE))
				.setTaxableBaseResult(recDet.getValue(FS_VAT_DETAIL.RES_TAXABLE_BASE))
				.setTaxableBaseAdjust(recDet.getValue(FS_VAT_DETAIL.ADJ_TAXABLE_BASE))
				.setTaxableBase(recDet.getValue(FS_VAT_DETAIL.TAXABLE_BASE))
				
				.setQuotaAccumulated(recDet.getValue(FS_VAT_DETAIL.ACU_QUOTA))
				.setQuotaDeclared(recDet.getValue(FS_VAT_DETAIL.DEC_QUOTA))
				.setQuotaResult( recDet.getValue(FS_VAT_DETAIL.RES_QUOTA))
				.setQuotaAdjust(recDet.getValue(FS_VAT_DETAIL.ADJ_QUOTA))
				.setQuota(recDet.getValue(FS_VAT_DETAIL.QUOTA))
				
				.setDeductibleQuotaAccumulated(recDet.getValue(FS_VAT_DETAIL.ACU_DEDUCTIBLE_QUOTA))
				.setDeductibleQuotaDeclared(recDet.getValue(FS_VAT_DETAIL.DEC_DEDUCTIBLE_QUOTA))
				.setDeductibleQuotaResult( recDet.getValue(FS_VAT_DETAIL.RES_DEDUCTIBLE_QUOTA))
				.setDeductibleQuotaAdjust(recDet.getValue(FS_VAT_DETAIL.ADJ_DEDUCTIBLE_QUOTA))
				.setDeductibleQuota(recDet.getValue(FS_VAT_DETAIL.DEDUCTIBLE_QUOTA))
				)
		.forEach(det -> {
			VatTaxKey.GIPUZKOA_KEY_MAP.get(det.getKey()).accept(mod, det);	
		});

		mod.ensureDetail(Mod303Key.GP_C027).addAmount( ensure(rec.getValue(FS_VAT_DECLARATION.PERCENT) ));
		double quota = ensure(rec.getValue(FS_VAT_DECLARATION.QUOTA) );
		mod.ensureDetail(Mod303Key.GP_C028).addAmount( quota );
		double prevCompensate = ensure(rec.getValue(FS_VAT_DECLARATION.PREV_YEAR_COMPENSATE_QUOTA) );
		mod.ensureDetail(Mod303Key.GP_C029).addAmount( prevCompensate );
		
		double compensate = ensure(rec.getValue(FS_VAT_DECLARATION.COMPENSATE));
		double pay_back = ensure(rec.getValue(FS_VAT_DECLARATION.PAY_BACK));
		double deposit = ensure(rec.getValue(FS_VAT_DECLARATION.DEPOSIT));
		if (pay_back > 0.0)   {
			mod.ensureDetail(Mod303Key.GP_C035).addAmount( pay_back * -1 );
			mod.putDescription(Mod303Key.CM_004, FiscalModelDeclarationType.PAYBACK.getValue()); 
		} else if (compensate > 0.0)   {
			mod.ensureDetail(Mod303Key.GP_C035).addAmount( compensate * -1 );
			mod.putDescription(Mod303Key.CM_004, FiscalModelDeclarationType.COMPENSATE.getValue());
		} else {
			mod.ensureDetail(Mod303Key.GP_C035).addAmount( deposit );
			mod.putDescription(Mod303Key.CM_004, FiscalModelDeclarationType.DEPOSIT.getValue()); 
		}
		
		mod.putAmount(Mod303Key.CM_005, 0);	// Solo regimen general.
	}
	
	private static void fillBizkaiaDetails(AONContext ctx, Mod303 mod, Record rec) {
//		throw new IllegalArgumentException("BIZKAIA");
	}
}


