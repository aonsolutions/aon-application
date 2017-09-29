package net.aonsolutions.vat.change;

import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsVat.FS_VAT;
import static com.esferalia.aon.jooq.tables.FsVatDeclaration.FS_VAT_DECLARATION;
import static com.esferalia.aon.jooq.tables.FsVatDetail.FS_VAT_DETAIL;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;

import org.jooq.Record;
import org.jooq.conf.ParamType;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.modules.Module;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016.Epigraph;
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

	private static PrintWriter LOG;
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
			LOG = new PrintWriter( new FileWriter(  File.createTempFile("AON_VAT_", ".log") ), true);
			ctx.getDslContext().settings().setRenderSchema(false);
			ctx.getDslContext().settings().setParamType( ParamType.INLINED );
			log("Connected!");
			ctx.getDslContext().transaction( configuration -> passToFiscalModel(ctx) );
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			LOG.flush();
			LOG.close();
			AonDatabaseUtil.closeQuietly(c);
			ctx.close();
			System.out.println( ((new Date()).getTime() - start.getTime() ) + " ms.");
		}
	}

	private static void log(String msg) {
		LOG.println( msg );
		
	}
	
	private static void passToFiscalModel(AONContext ctx) {
		log(  "[START] Régimen Simplificado " );
		transforrmOldSimplified(ctx);
		log(  "[END] Régimen Simplificado " );
		
		log(  "[START] Régimen General" );
		passVatTax(ctx);	
		log(  "[END] Régimen General" );
	}
	
	private static void transforrmOldSimplified(AONContext ctx) {
		ctx.getDslContext()
			.select()
			.from(FS_MODEL)
			.leftOuterJoin(FINANCE).on(FINANCE.ID.equal(FS_MODEL.FINANCE))
			.leftOuterJoin(REGISTRY).on(REGISTRY.ID.equal(FINANCE.REGISTRY))
			.leftOuterJoin(SCOPE).on(FINANCE.SCOPE.equal(SCOPE.ID))
			.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
			.where(FS_MODEL.MODEL.eq(FiscalModelType.M303.getName()))
			.orderBy(FS_MODEL.YEAR.desc(),FS_MODEL.MODEL.asc(),FS_MODEL.PERIOD.desc())
			.fetch()
			.stream()		
			.map( record -> Mod303DAO.map303(new Mod303(),record))
			.peek(fm -> Mod303DAO.getModelDetails(ctx,fm).forEach( detail -> fm.put( detail)))
			.filter(mod -> mod.getMap() != null && (mod.getMap().containsKey("303-AG1") || mod.getMap().containsKey("303-AC1")))
			.forEach( mod -> transformToNewMap(ctx,mod));
	}
	
	@FunctionalInterface
	private static interface IMod303KeyConverter {
		void convert(Mod303 mod, FiscalModelDetail det, Mod303 map);
	}

	private static enum OldMod303Key {
		 CAG1		("303-AG1"		,(mod,det,map) -> {
			map.putDescription(Mod303Key.CT_SA11, AonStringUtils.trim(AonStringUtils.substringBefore( det.getDescription() , "-")));
			map.putDescription(Mod303Key.CT_SA1D, AonStringUtils.trim(AonStringUtils.substringAfter( det.getDescription() , "-")));
		})
		,CAG1_V1	("303-AG1V1"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA12, det.getAmount() ))
		,CAG1_V2	("303-AG1V2" 	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA13, det.getAmount() ))	
		,CAG1_V3	("303-AG1V3"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA14, det.getAmount() ))
		,CAG1_V4	("303-AG1V4"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA15, det.getAmount() ))
		,CAG1_V5	("303-AG1V5"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA16, det.getAmount() ))
		,CAG1_V6	("303-AG1V6"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA17, det.getAmount() ))
		,CAG1_V7	("303-AG1V7"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA18, det.getAmount() ))

		,CAG2		("303-AG2"		,(mod,det,map) -> {
			map.putDescription(Mod303Key.CT_SA21, AonStringUtils.trim(AonStringUtils.substringBefore( det.getDescription() , "-")));
			map.putDescription(Mod303Key.CT_SA2D, AonStringUtils.trim(AonStringUtils.substringAfter( det.getDescription() , "-")));
		})
		,CAG2_V1	("303-AG2V1"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA22, det.getAmount() ))
		,CAG2_V2	("303-AG2V2" 	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA23, det.getAmount() ))	
		,CAG2_V3	("303-AG2V3"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA24, det.getAmount() ))
		,CAG2_V4	("303-AG2V4"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA25, det.getAmount() ))
		,CAG2_V5	("303-AG2V5"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA26, det.getAmount() ))
		,CAG2_V6	("303-AG2V6"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA27, det.getAmount() ))
		,CAG2_V7	("303-AG2V7"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA28, det.getAmount() ))
		
		,CAG3		("303-AG3"		,(mod,det,map) -> {
			map.putDescription(Mod303Key.CT_SA31, AonStringUtils.trim(AonStringUtils.substringBefore( det.getDescription() , "-")));
			map.putDescription(Mod303Key.CT_SA3D, AonStringUtils.trim(AonStringUtils.substringAfter( det.getDescription() , "-")));
		})
		,CAG3_V1	("303-AG3V1"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA32, det.getAmount() ))
		,CAG3_V2	("303-AG3V2" 	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA33, det.getAmount() ))	
		,CAG3_V3	("303-AG3V3"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA34, det.getAmount() ))
		,CAG3_V4	("303-AG3V4"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA35, det.getAmount() ))
		,CAG3_V5	("303-AG3V5"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA36, det.getAmount() ))
		,CAG3_V6	("303-AG3V6"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA37, det.getAmount() ))
		,CAG3_V7	("303-AG3V7"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA38, det.getAmount() ))

		,CAG4		("303-AG4"		,(mod,det,map) -> {
			map.putDescription(Mod303Key.CT_SA41, AonStringUtils.trim(AonStringUtils.substringBefore( det.getDescription() , "-")));
			map.putDescription(Mod303Key.CT_SA4D, AonStringUtils.trim(AonStringUtils.substringAfter( det.getDescription() , "-")));
		})
		,CAG4_V1	("303-AG4V1"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA42, det.getAmount() ))
		,CAG4_V2	("303-AG4V2" 	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA43, det.getAmount() ))
		,CAG4_V3	("303-AG4V3"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA44, det.getAmount() ))
		,CAG4_V4	("303-AG4V4"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA45, det.getAmount() ))
		,CAG4_V5	("303-AG4V5"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA46, det.getAmount() ))
		,CAG4_V6	("303-AG4V6"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA47, det.getAmount() ))
		,CAG4_V7	("303-AG4V7"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA48, det.getAmount() ))
 
		// ACTIVIDAD 1		
		,CAC1     ("303-AC1"		,(mod,det,map) 	-> {
			String epigraph = AonStringUtils.trim(AonStringUtils.substringBefore( det.getDescription() , "-"));
			map.putDescription(Mod303Key.CT_S101, epigraph);
			map.putDescription(Mod303Key.CT_S10D, AonStringUtils.trim(AonStringUtils.substringAfter( det.getDescription() , "-")));
			if ("722".equals(epigraph)) {
				map.putAmount(Mod303Key.CT_S102, 1);	
			}
		})
		,CAC1_M1U ("303-AC1M1U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S11I, det.getAmount())) 
		,CAC1_M1I ("303-AC1M1I", (mod,det,map)	-> fillModule(mod,det,0, "303-AC1", Mod303Key.CT_S11U, Mod303Key.CT_S11F, Mod303Key.CT_S11D, Mod303Key.CT_S11R,Mod303Key.CT_S11I,map))
		,CAC1_M2U ("303-AC1M2U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S12I, det.getAmount() ))
		,CAC1_M2I ("303-AC1M2I", (mod,det,map)	-> fillModule(mod,det,1, "303-AC1", Mod303Key.CT_S12U, Mod303Key.CT_S12F, Mod303Key.CT_S12D, Mod303Key.CT_S12R,Mod303Key.CT_S12I,map))
		,CAC1_M3U ("303-AC1M3U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S13I, det.getAmount() ))
		,CAC1_M3I ("303-AC1M3I", (mod,det,map)	-> fillModule(mod,det,2, "303-AC1", Mod303Key.CT_S13U, Mod303Key.CT_S13F, Mod303Key.CT_S13D, Mod303Key.CT_S13R,Mod303Key.CT_S13I,map))
		,CAC1_M4U ("303-AC1M4U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S14I, det.getAmount() ))
		,CAC1_M4I ("303-AC1M4I", (mod,det,map)	-> fillModule(mod,det,3, "303-AC1", Mod303Key.CT_S14U, Mod303Key.CT_S14F, Mod303Key.CT_S14D, Mod303Key.CT_S14R,Mod303Key.CT_S14I,map))
		,CAC1_M5U ("303-AC1M5U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S15I, det.getAmount() ))
		,CAC1_M5I ("303-AC1M5I", (mod,det,map)	-> fillModule(mod,det,4, "303-AC1", Mod303Key.CT_S15U, Mod303Key.CT_S15F, Mod303Key.CT_S15D, Mod303Key.CT_S15R,Mod303Key.CT_S15I,map))
		,CAC1_M6U ("303-AC1M6U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S16I, det.getAmount() ))
		,CAC1_M6I ("303-AC1M6I", (mod,det,map)	-> fillModule(mod,det,5, "303-AC1", Mod303Key.CT_S16U, Mod303Key.CT_S16F, Mod303Key.CT_S16D, Mod303Key.CT_S16R,Mod303Key.CT_S16I,map))
		,CAC1_M7U ("303-AC1M7U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S17I, det.getAmount() ))
		,CAC1_M7I ("303-AC1M7I", (mod,det,map)	-> fillModule(mod,det,6, "303-AC1", Mod303Key.CT_S17U, Mod303Key.CT_S17F, Mod303Key.CT_S17D, Mod303Key.CT_S17R,Mod303Key.CT_S17I,map))
		,CAC1_C   ("303-AC1C" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S117, det.getAmount() ))
		,CAC1_D   ("303-AC1D" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S118, det.getAmount() ))
		,CAC1_Z   ("303-AC1Z" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S119, det.getAmount() ))
		,CAC1_ZA  ("303-AC1ZA", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S1X1, det.getAmount() ))
		,CAC1_ZD  ("303-AC1ZD", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S1X2, det.getAmount() ))
		,CAC1_E   ("303-AC1E" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S120, det.getAmount() ))
		,CAC1_F   ("303-AC1F" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S121, det.getAmount() ))
		,CAC1_G   ("303-AC1G" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S122, det.getAmount() ))
		,CAC1_H   ("303-AC1H" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S123, det.getAmount() ))
		,CAC1_HA  ("303-AC1HA", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S1X1, det.getAmount() ))
		,CAC1_HD  ("303-AC1HD", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S1X2, det.getAmount() ))
		,CAC1_I   ("303-AC1I" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S124, det.getAmount() ))
		,CAC1_J   ("303-AC1J" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S125, det.getAmount() ))
		,CAC1_K   ("303-AC1K" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S126, det.getAmount() ))
		,CAC1_L   ("303-AC1L" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S127, det.getAmount() ))
		,CAC1_M   ("303-AC1M" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S128, det.getAmount() ))
		
		// ACTIVIDAD 2		
		,CAC2    ("303-AC2"		,(mod,det,map) 	-> {
			String epigraph = AonStringUtils.trim(AonStringUtils.substringBefore( det.getDescription() , "-"));
			map.putDescription(Mod303Key.CT_S201, epigraph);
			map.putDescription(Mod303Key.CT_S20D, AonStringUtils.trim(AonStringUtils.substringAfter( det.getDescription() , "-")));
			if ("722".equals(epigraph)) {
				map.putAmount(Mod303Key.CT_S202, 1);	
			}
		})
		,CAC2_M1U ("303-AC2M1U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S21I, det.getAmount() ))
		,CAC2_M1I ("303-AC2M1I", (mod,det,map)	-> fillModule(mod,det,0, "303-AC2", Mod303Key.CT_S21U, Mod303Key.CT_S21F, Mod303Key.CT_S21D, Mod303Key.CT_S21R,Mod303Key.CT_S21I,map))
		,CAC2_M2U ("303-AC2M2U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S22I, det.getAmount() ))
		,CAC2_M2I ("303-AC2M2I", (mod,det,map)	-> fillModule(mod,det,1, "303-AC2", Mod303Key.CT_S22U, Mod303Key.CT_S22F, Mod303Key.CT_S22D, Mod303Key.CT_S22R,Mod303Key.CT_S22I,map))
		,CAC2_M3U ("303-AC2M3U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S23I, det.getAmount() ))
		,CAC2_M3I ("303-AC2M3I", (mod,det,map)	-> fillModule(mod,det,2, "303-AC2", Mod303Key.CT_S23U, Mod303Key.CT_S23F, Mod303Key.CT_S23D, Mod303Key.CT_S23R,Mod303Key.CT_S23I,map))
		,CAC2_M4U ("303-AC2M4U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S24I, det.getAmount() ))
		,CAC2_M4I ("303-AC2M4I", (mod,det,map)	-> fillModule(mod,det,3, "303-AC2", Mod303Key.CT_S24U, Mod303Key.CT_S24F, Mod303Key.CT_S24D, Mod303Key.CT_S24R,Mod303Key.CT_S24I,map))
		,CAC2_M5U ("303-AC2M5U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S25I, det.getAmount() ))
		,CAC2_M5I ("303-AC2M5I", (mod,det,map)	-> fillModule(mod,det,4, "303-AC2", Mod303Key.CT_S25U, Mod303Key.CT_S25F, Mod303Key.CT_S25D, Mod303Key.CT_S25R,Mod303Key.CT_S25I,map))
		,CAC2_M6U ("303-AC2M6U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S26I, det.getAmount() ))
		,CAC2_M6I ("303-AC2M6I", (mod,det,map)	-> fillModule(mod,det,5, "303-AC2", Mod303Key.CT_S26U, Mod303Key.CT_S26F, Mod303Key.CT_S26D, Mod303Key.CT_S26R,Mod303Key.CT_S26I,map))
		,CAC2_M7U ("303-AC2M7U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S27I, det.getAmount() ))
		,CAC2_M7I ("303-AC2M7I", (mod,det,map)	-> fillModule(mod,det,6, "303-AC2", Mod303Key.CT_S27U, Mod303Key.CT_S27F, Mod303Key.CT_S27D, Mod303Key.CT_S27R,Mod303Key.CT_S27I,map))
		,CAC2_C   ("303-AC2C" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S217, det.getAmount()))
		,CAC2_D   ("303-AC2D" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S218, det.getAmount()))
		,CAC2_Z   ("303-AC2Z" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S219, det.getAmount()))
		,CAC2_ZA  ("303-AC2ZA", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S2X1, det.getAmount()))
		,CAC2_ZD  ("303-AC2ZD", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S2X2, det.getAmount()))
		,CAC2_E   ("303-AC2E" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S220, det.getAmount()))
		,CAC2_F   ("303-AC2F" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S221, det.getAmount()))
		,CAC2_G   ("303-AC2G" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S222, det.getAmount()))
		,CAC2_H   ("303-AC2H" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S223, det.getAmount()))
		,CAC2_HA  ("303-AC2HA", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S2X1, det.getAmount()))
		,CAC2_HD  ("303-AC2HD", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S2X2, det.getAmount()))
		,CAC2_I   ("303-AC2I" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S224, det.getAmount()))
		,CAC2_J   ("303-AC2J" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S225, det.getAmount()))
		,CAC2_K   ("303-AC2K" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S226, det.getAmount()))
		,CAC2_L   ("303-AC2L" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S227, det.getAmount()))
		,CAC2_M   ("303-AC2M" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S228, det.getAmount()))

		// ACTIVIDAD 3		
		,CAC3    ("303-AC3"		,(mod,det,map) 	-> {
			String epigraph = AonStringUtils.trim(AonStringUtils.substringBefore( det.getDescription() , "-"));
			map.putDescription(Mod303Key.CT_S301, epigraph);
			map.putDescription(Mod303Key.CT_S30D, AonStringUtils.trim(AonStringUtils.substringAfter( det.getDescription() , "-")));
			if ("722".equals(epigraph)) {
				map.putAmount(Mod303Key.CT_S302, 1);	
			}
		})
		,CAC3_M1U ("303-AC3M1U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S31I, det.getAmount() ))
		,CAC3_M1I ("303-AC3M1I", (mod,det,map)	-> fillModule(mod,det,0, "303-AC3", Mod303Key.CT_S31U, Mod303Key.CT_S31F, Mod303Key.CT_S31D, Mod303Key.CT_S31R,Mod303Key.CT_S31I,map))
		,CAC3_M2U ("303-AC3M2U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S32I, det.getAmount() ))
		,CAC3_M2I ("303-AC3M2I", (mod,det,map)	-> fillModule(mod,det,1, "303-AC3", Mod303Key.CT_S32U, Mod303Key.CT_S32F, Mod303Key.CT_S32D, Mod303Key.CT_S32R,Mod303Key.CT_S32I,map))
		,CAC3_M3U ("303-AC3M3U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S33I, det.getAmount() ))
		,CAC3_M3I ("303-AC3M3I", (mod,det,map)	-> fillModule(mod,det,2, "303-AC3", Mod303Key.CT_S33U, Mod303Key.CT_S33F, Mod303Key.CT_S33D, Mod303Key.CT_S33R,Mod303Key.CT_S33I,map))
		,CAC3_M4U ("303-AC3M4U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S34I, det.getAmount() ))
		,CAC3_M4I ("303-AC3M4I", (mod,det,map)	-> fillModule(mod,det,3, "303-AC3", Mod303Key.CT_S34U, Mod303Key.CT_S34F, Mod303Key.CT_S34D, Mod303Key.CT_S34R,Mod303Key.CT_S34I,map))
		,CAC3_M5U ("303-AC3M5U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S35I, det.getAmount() ))
		,CAC3_M5I ("303-AC3M5I", (mod,det,map)	-> fillModule(mod,det,4, "303-AC3", Mod303Key.CT_S35U, Mod303Key.CT_S35F, Mod303Key.CT_S35D, Mod303Key.CT_S35R,Mod303Key.CT_S35I,map))
		,CAC3_M6U ("303-AC3M6U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S36I, det.getAmount() ))
		,CAC3_M6I ("303-AC3M6I", (mod,det,map)	-> fillModule(mod,det,5, "303-AC3", Mod303Key.CT_S36U, Mod303Key.CT_S36F, Mod303Key.CT_S36D, Mod303Key.CT_S36R,Mod303Key.CT_S36I,map))
		,CAC3_M7U ("303-AC3M7U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S37I, det.getAmount() ))
		,CAC3_M7I ("303-AC3M7I", (mod,det,map)	-> fillModule(mod,det,6, "303-AC3", Mod303Key.CT_S37U, Mod303Key.CT_S37F, Mod303Key.CT_S37D, Mod303Key.CT_S37R,Mod303Key.CT_S37I,map))
		,CAC3_C   ("303-AC3C" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S317, det.getAmount()))
		,CAC3_D   ("303-AC3D" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S318, det.getAmount()))
		,CAC3_Z   ("303-AC3Z" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S319, det.getAmount()))
		,CAC3_ZA  ("303-AC3ZA", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S3X1, det.getAmount()))
		,CAC3_ZD  ("303-AC3ZD", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S3X2, det.getAmount()))
		,CAC3_E   ("303-AC3E" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S320, det.getAmount()))
		,CAC3_F   ("303-AC3F" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S321, det.getAmount()))
		,CAC3_G   ("303-AC3G" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S322, det.getAmount()))
		,CAC3_H   ("303-AC3H" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S323, det.getAmount()))
		,CAC3_HA  ("303-AC3HA", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S3X1, det.getAmount()))
		,CAC3_HD  ("303-AC3HD", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S3X2, det.getAmount()))
		,CAC3_I   ("303-AC3I" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S324, det.getAmount()))
		,CAC3_J   ("303-AC3J" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S325, det.getAmount()))
		,CAC3_K   ("303-AC3K" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S326, det.getAmount()))
		,CAC3_L   ("303-AC3L" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S327, det.getAmount()))
		,CAC3_M   ("303-AC3M" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S328, det.getAmount()))

		// ACTIVIDAD 4		
		,CAC4    ("303-AC3"		,(mod,det,map) 	-> {
			String epigraph = AonStringUtils.trim(AonStringUtils.substringBefore( det.getDescription() , "-"));
			map.putDescription(Mod303Key.CT_S401, epigraph);
			map.putDescription(Mod303Key.CT_S40D, AonStringUtils.trim(AonStringUtils.substringAfter( det.getDescription() , "-")));
			if ("722".equals(epigraph)) {
				map.putAmount(Mod303Key.CT_S402, 1);	
			}
		})
		,CAC4_M1U ("303-AC4M1U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S41I, det.getAmount() ))
		,CAC4_M1I ("303-AC4M1I", (mod,det,map)	-> fillModule(mod,det,0, "303-AC4", Mod303Key.CT_S41U, Mod303Key.CT_S41F, Mod303Key.CT_S41D, Mod303Key.CT_S41R,Mod303Key.CT_S41I,map))
		,CAC4_M2U ("303-AC4M2U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S42I, det.getAmount() ))
		,CAC4_M2I ("303-AC4M2I", (mod,det,map)	-> fillModule(mod,det,1, "303-AC4", Mod303Key.CT_S42U, Mod303Key.CT_S42F, Mod303Key.CT_S42D, Mod303Key.CT_S42R,Mod303Key.CT_S42I,map))
		,CAC4_M3U ("303-AC4M3U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S43I, det.getAmount() ))
		,CAC4_M3I ("303-AC4M3I", (mod,det,map)	-> fillModule(mod,det,2, "303-AC4", Mod303Key.CT_S43U, Mod303Key.CT_S43F, Mod303Key.CT_S43D, Mod303Key.CT_S43R,Mod303Key.CT_S43I,map))
		,CAC4_M4U ("303-AC4M4U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S44I, det.getAmount() ))
		,CAC4_M4I ("303-AC4M4I", (mod,det,map)	-> fillModule(mod,det,3, "303-AC4", Mod303Key.CT_S44U, Mod303Key.CT_S44F, Mod303Key.CT_S44D, Mod303Key.CT_S44R,Mod303Key.CT_S44I,map))
		,CAC4_M5U ("303-AC4M5U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S45I, det.getAmount() ))
		,CAC4_M5I ("303-AC4M5I", (mod,det,map)	-> fillModule(mod,det,4, "303-AC4", Mod303Key.CT_S45U, Mod303Key.CT_S45F, Mod303Key.CT_S45D, Mod303Key.CT_S45R,Mod303Key.CT_S45I,map))
		,CAC4_M6U ("303-AC4M6U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S46I, det.getAmount() ))
		,CAC4_M6I ("303-AC4M6I", (mod,det,map)	-> fillModule(mod,det,5, "303-AC4", Mod303Key.CT_S46U, Mod303Key.CT_S46F, Mod303Key.CT_S46D, Mod303Key.CT_S46R,Mod303Key.CT_S46I,map))
		,CAC4_M7U ("303-AC4M7U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S47I, det.getAmount() ))
		,CAC4_M7I ("303-AC4M7I", (mod,det,map)	-> fillModule(mod,det,6, "303-AC4", Mod303Key.CT_S47U, Mod303Key.CT_S47F, Mod303Key.CT_S47D, Mod303Key.CT_S47R,Mod303Key.CT_S47I,map))
		,CAC4_C   ("303-AC4C" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S417, det.getAmount()))
		,CAC4_D   ("303-AC4D" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S418, det.getAmount()))
		,CAC4_Z   ("303-AC4Z" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S419, det.getAmount()))
		,CAC4_ZA  ("303-AC4ZA", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S4X1, det.getAmount()))
		,CAC4_ZD  ("303-AC4ZD", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S4X2, det.getAmount()))
		,CAC4_E   ("303-AC4E" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S420, det.getAmount()))
		,CAC4_F   ("303-AC4F" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S421, det.getAmount()))
		,CAC4_G   ("303-AC4G" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S422, det.getAmount()))
		,CAC4_H   ("303-AC4H" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S423, det.getAmount()))
		,CAC4_HA  ("303-AC4HA", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S4X1, det.getAmount()))
		,CAC4_HD  ("303-AC4HD", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S4X2, det.getAmount()))
		,CAC4_I   ("303-AC4I" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S424, det.getAmount()))
		,CAC4_J   ("303-AC4J" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S425, det.getAmount()))
		,CAC4_K   ("303-AC4K" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S426, det.getAmount()))
		,CAC4_L   ("303-AC4L" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S427, det.getAmount()))
		,CAC4_M   ("303-AC4M" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S428, det.getAmount()))

		,C47      ("303-47" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S47, det.getAmount()))
		,C48      ("303-48" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S48, det.getAmount()))
		,C49      ("303-49" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S49, det.getAmount()))
		,C50      ("303-50" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S50, det.getAmount()))
		,C51      ("303-51" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S51, det.getAmount()))
		,C52      ("303-52" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S52, det.getAmount()))
		,C53      ("303-53" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S53, det.getAmount()))
		,C54      ("303-54" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S54, det.getAmount()))
		,C55      ("303-55" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S55, det.getAmount()))
		,C56      ("303-56" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S56, det.getAmount()))
		,C57      ("303-57" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S57, det.getAmount()))
		,C58      ("303-58" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S58, det.getAmount()))



		,C59      ("303-59" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C59, det.getAmount()))
		,C60      ("303-60" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C60, det.getAmount()))
		,C61      ("303-61" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C61, det.getAmount()))
		,C62      ("303-62" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C62, det.getAmount()))
		,C63      ("303-63" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C63, det.getAmount()))
		,C74      ("303-74" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C74, det.getAmount()))
		,C75      ("303-75" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C75, det.getAmount()))
		,C64      ("303-64" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C64, det.getAmount()))
		,C65      ("303-65" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C65, det.getAmount()))
		,C66      ("303-66" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C66, det.getAmount()))
		,C67      ("303-67" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C67, det.getAmount()))
		,C68      ("303-68" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C68, det.getAmount()))
		,C69      ("303-69" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C69, det.getAmount()))
		,C70      ("303-70" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C70, det.getAmount()))
		,C71      ("303-71" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C71, det.getAmount()))

		,IAC_01	 ("303-IAC01", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U1C, det.getDescription()))
		,IAE_01	 ("303-IAE01", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U1E, det.getDescription()))
		,IAD_01	 ("303-IAD01", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U1D, det.getDescription()))
		
		,IAC_02	 ("303-IAC02", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U2C, det.getDescription()))
		,IAE_02	 ("303-IAE02", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U2E, det.getDescription()))
		,IAD_02	 ("303-IAD02", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U2D, det.getDescription()))
		
		,IAC_03	 ("303-IAC03", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U3C, det.getDescription()))
		,IAE_03	 ("303-IAE03", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U3E, det.getDescription()))
		,IAD_03	 ("303-IAD03", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U3D, det.getDescription()))
		
		,IAC_04	 ("303-IAC04", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U4C, det.getDescription()))
		,IAE_04	 ("303-IAE04", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U4E, det.getDescription()))
		,IAD_04	 ("303-IAD04", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U4D, det.getDescription()))
		
		,IAC_05	 ("303-IAC05", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U5C, det.getDescription()))
		,IAE_05	 ("303-IAE05", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U5E, det.getDescription()))
		,IAD_05	 ("303-IAD05", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U5D, det.getDescription()))
		
		
		,C80      ("303-80" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C80, det.getAmount()))
		,C81      ("303-81" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C81, det.getAmount()))
		,C82      ("303-82" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C82, det.getAmount()))
		,C83      ("303-83" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C83, det.getAmount()))
		,C84      ("303-84" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C84, det.getAmount()))
		,C85      ("303-85" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C85, det.getAmount()))
		,C86      ("303-86" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C86, det.getAmount()))
		,C87      ("303-87" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C87, det.getAmount()))
		,C88      ("303-88" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C88, det.getAmount()))
		,D		  ("303-D"  , (mod,det,map)	-> map.putAmount(Mod303Key.CT_U13, det.getAmount()))
/*		

 		,PBK  	  ("303-PBK", (mod,det,map)	-> map.putDescription(Mod303Key.CM_004, det.getAmount()<0?FiscalModelDeclarationType.PAYBACK.getValue():""))
???	,CT_U14("303-CTU14",false,null,"Exonerado de presentar el modelo 390 y con volumen de operaciones cero.")

*/			
			;
		
		private String value;
		private IMod303KeyConverter converter;
		
		private OldMod303Key(String value) {
			this(value,null);
		}
		private OldMod303Key(String value,IMod303KeyConverter converter) {
			this.value = value;
			this.converter = converter;
		}
		
		private String getValue() {
			return value;
		}
		private void convert(Mod303 mod, FiscalModelDetail det, Mod303 map) {
			if (this.converter != null) converter.convert(mod, det, map);
		}
	    private static OldMod303Key getKeyWithValue( String value ) {
	    	for (OldMod303Key key : OldMod303Key.values() ) {
	    		if (AonStringUtils.equals(value, key.getValue())) {
	    			return key;
	    		}
	    	}
	    	return null;
	    }
	}
	private static void transformToNewMap(AONContext ctx,Mod303 mod303) {
		count++;
		Mod303 map = new Mod303();
		Iterator<FiscalModelDetail> iter = mod303.getMap().values().iterator(); 
		while (iter.hasNext()) {
			FiscalModelDetail det = iter.next();
			OldMod303Key oldKey = OldMod303Key.getKeyWithValue(det.getType());
			if (oldKey != null) {
				oldKey.convert(mod303, det, map);
			}
		}
		boolean isPayaback = AonMathUtils.isNotZero( mod303.getAmount("303-PBK")); 
		mod303.setMap(map.getMap());
		Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
		for (IMod303KeyDAO key : dec.getKeys()) {
			mod303.ensureDetail(key.getKey());
		}
		dec.fillSimplifiedRegime(mod303);
		mod303.putAmount(Mod303Key.CT_A02, 0); // Solo regimen simplificado.
		System.out.print(".");
		if (count % 100 == 0) System.out.println( " ----> " + count);
		
		
		ctx.getDslContext()
		.update(FS_MODEL)
			.set(FS_MODEL.MODEL, FiscalModelType.M310.getValue() )
		.where(FS_MODEL.ID.equal(mod303.getId()))
		.execute();
		
		if (mod303.isFinished()) {
			if (mod303.getResult() == 0  ) { 
				mod303.setDeclarationType(FiscalModelDeclarationType.NEGATIVE);
			} else {
				if (mod303.getResult() < 0  ) {
					if (isPayaback) {
						mod303.setDeclarationType(FiscalModelDeclarationType.PAYBACK);
					} else {
						mod303.setDeclarationType(FiscalModelDeclarationType.COMPENSATE);
					}
				} else {
					Finance finance = mod303.getFinance();
					if (finance != null) {
						if (finance.getBankAccount() != null && AonStringUtils.isNotBlank(finance.getBankAccount().getIban())) {
							mod303.setDeclarationType(FiscalModelDeclarationType.BANK);			
						} else {
							mod303.setDeclarationType(FiscalModelDeclarationType.DEPOSIT);
						}
					}
				}
			}
			
		}
		mod303.setId(null);
		save(ctx, mod303);
	}
	
	private static void fillModule(Mod303 mod,FiscalModelDetail det,int idx, String epiKey,Mod303Key unitKey, Mod303Key factorKey,Mod303Key descKey, Mod303Key resultKey, Mod303Key amountKey,Mod303 map) {
		String epigraph = AonStringUtils.trim(AonStringUtils.substringBefore( mod.getDescription(epiKey) , "-"));
		Epigraph epi = Modules2016.Epigraph.getEpigraph(epigraph);
		double amount = det.getAmount(); 
		if (epi != null) {
			Module module = null;
			if (epi.getVATModules() != null && epi.getVATModules().length > idx) {
				module = epi.getVATModules()[idx];	
			}
			map.putDescription	(descKey, module==null?null:module.getKey().getDescription() );
			map.putDescription	(unitKey, module==null?null:module.getUnit() );
			map.putAmount	(factorKey, module==null? 0.0 :module.getAmount() );
		}
		map.putAmount(resultKey, amount );
	}
	

	private static void passVatTax(AONContext ctx) {
		ctx.getDslContext().select()
			.from(FS_VAT)
			.leftOuterJoin(FS_VAT_DECLARATION).on(FS_VAT_DECLARATION.FS_VAT.eq(FS_VAT.ID))
			.where( FS_VAT.YEAR.gt(2013))
			.and( FS_VAT.PERIOD.notEqual( Period.YEAR.getValue() ))
//			.and( FS_VAT.DOMAIN.eq(216))
//			.limit(1000)
			.orderBy(FS_VAT.DOMAIN.asc(),FS_VAT.YEAR.desc(),FS_VAT.PERIOD.desc())
			.fetch()
			.stream()
			
			.filter(rec -> !AonNumberUtils.equals(rec.getValue(FS_VAT_DECLARATION.ADMINISTRATION),Administration.NAVARRA.getValue()))
			.map( rec -> {
				count++;
				Mod303 mod = new Mod303(); 
				mod.setDomain(rec.getValue(FS_VAT.DOMAIN) )
					.setYear(rec.getValue(FS_VAT.YEAR) )
					.setPeriod( com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Period.class, rec.getValue(FS_VAT.PERIOD) ))
					.setAdministration( Administration.safeValueOf(rec.getValue(FS_VAT_DECLARATION.ADMINISTRATION)) )
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
				Mod303Declaration dec = Mod303Declaration.getInstance(mod);
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
		.forEach( mod -> save(ctx,mod) )
		;
	}
	private static void save(AONContext ctx,Mod303 mod303) {
//		double result0 = mod303.getResult(); 
//		Mod303 calculated = Mod303DAO.calculateMod303(ctx, mod303,Mod303Declaration.getInstance(mod303));
//		double result1 = calculated.getResult();
//		if ( result0 != result1 && AonMathUtils.absRounded(result0 - result1) > 0.5 ) {
//			log( AonStringUtils.rightPad(count,6) 
//					+ AonStringUtils.rightPad(mod303.getDomain(),9) 
//					+ mod303.getYear() 
//					+ " " + AonStringUtils.rightPad(mod303.getPeriod().getName(),6) 
//					+  AonStringUtils.rightPad(AonStringUtils.substring( mod303.getAdministration().getDescription(), 0, 6),7)						
//					+  AonStringUtils.rightPad(AonStringUtils.abbreviate(mod303.getDocument(),9),10)
//					+  AonStringUtils.rightPad(AonStringUtils.abbreviate(mod303.getFullName(), 40),41)
//					+ " DIFERENTE ..: " + result0 +  " <> " + result1
//					+ " GAP ..: " + (result0 - result1)
//					);
//		}
		Mod303DAO.saveOnlyMod303(ctx, mod303);
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
		mod.putAmount(Mod303Key.CT_A08, AonMathUtils.isNotZero( mod.getAmount(Mod303Key.CT_C75) )?1:0); // Es destinatario de operaciones a las que se aplique el regimen especial del criterio de caja?
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
			VatTaxKey.BIZKAIA_KEY_MAP.get(det.getKey()).accept(mod, det);	
		});

		mod.ensureDetail(Mod303Key.BZ_C032).addAmount( ensure(rec.getValue(FS_VAT_DECLARATION.PERCENT) ));

		double quota = ensure(rec.getValue(FS_VAT_DECLARATION.QUOTA) );
		mod.ensureDetail(Mod303Key.BZ_C033).addAmount( quota );
		
		double prevCompensate = ensure(rec.getValue(FS_VAT_DECLARATION.PREV_YEAR_COMPENSATE_QUOTA) );
		mod.ensureDetail(Mod303Key.BZ_C034).addAmount( prevCompensate );
		
		mod.ensureDetail(Mod303Key.BZ_C036).addAmount( AonMathUtils.round(quota - prevCompensate) );
		
		mod.ensureDetail(Mod303Key.BZ_C042).addAmount( ensure(rec.getValue(FS_VAT_DECLARATION.DONE_REFUNDS) ));
		mod.ensureDetail(Mod303Key.BZ_C041).addAmount( ensure(rec.getValue(FS_VAT_DECLARATION.DONE_DEPOSITS)) * (-1) );

		double compensate = ensure(rec.getValue(FS_VAT_DECLARATION.COMPENSATE));
		double pay_back = ensure(rec.getValue(FS_VAT_DECLARATION.PAY_BACK));
		double deposit = ensure(rec.getValue(FS_VAT_DECLARATION.DEPOSIT));
		if (pay_back > 0.0)   {
			mod.ensureDetail(Mod303Key.BZ_C039).addAmount( pay_back * -1 );
			mod.putDescription(Mod303Key.CM_004 , FiscalModelDeclarationType.PAYBACK.getValue()); 
		} else if (compensate > 0.0)   {
			mod.ensureDetail(Mod303Key.BZ_C038).addAmount( compensate * -1 );
			mod.putDescription(Mod303Key.CM_004 , FiscalModelDeclarationType.COMPENSATE.getValue()); 
		} else {
			mod.ensureDetail(Mod303Key.BZ_C040).addAmount( deposit );
			mod.putDescription(Mod303Key.CM_004 , FiscalModelDeclarationType.DEPOSIT.getValue()); 
		}
		mod.ensureDetail(Mod303Key.BZ_C043).addAmount( ensure(rec.getValue(FS_VAT_DECLARATION.TOTAL_TAX_DEBT) ));
		
		mod.putAmount(Mod303Key.BZ_C187, AonMathUtils.isNotZero( mod.getAmount(Mod303Key.BZ_C203) )?1:0); // Es destinatario de operaciones a las que se aplique el regimen especial del criterio de caja?
		
		mod.putAmount(Mod303Key.CM_005, 0);	// Solo regimen general.
	}
}





