package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod202;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Stream;

import org.jooq.Condition;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Filter.FiscalModelFilter;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.esferalia.aon.occam.api.model.type.CNAE2009ToCNAE2025;
import com.esferalia.aon.occam.api.model.type.CNAE2025;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelValidation;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod202.Mod202Declaration.ComplementaryBeahaviour;
import com.esferalia.aon.occam.server.fiscal.AEATJson;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod202DAO extends FiscalModelDAO {
	
	private static final String DD_MM_YYYY = "ddMMyyyy";

	public static Stream<Mod202> getMod202s(AONContext ctx,int domain, FiscalModelFilter filter) {
		return FiscalModelDAO.getFullFiscalModels(ctx,domain,FiscalModelType.M202,filter, Mod202::new)
			.map( Mod202DAO::fillFiscalModel );
	}
	
	public static Stream<Mod202> getMod202s(AONContext ctx,int domain) {
		return getMod202s(ctx, domain, null);
	}
	
	public static Mod202 get(AONContext ctx,int id) {
		ctx.checkRead();
		Mod202 mod202 = FiscalModelDAO.get(ctx,Mod202::new,id); 
		return fillFiscalModel(mod202);
	}
	
	protected static Mod202 fillFiscalModel(Mod202 mod202) {
		
		String date = mod202.getDescription(Mod202Key.P02);
		mod202.setInitialDate(null);
		if (AonStringUtils.isNotEmpty( date )) {
			SimpleDateFormat formatter = new SimpleDateFormat(DD_MM_YYYY);
			mod202.setInitialDate(AonDateUtils.parse( date, formatter));
		}
		
//		String cnaeCode = mod202.getDescription(Mod202Key.P03);
//		mod202.setCnae(null);
//		if (AonStringUtils.isNotEmpty( cnaeCode )) {
//			CNAE2009 cnae = CNAE2009.valueOfCode(cnaeCode); 
//			mod202.setCnae(cnae);
//		}
		mod202.setCnae(mod202.getDescription(Mod202Key.P03));
		
		return mod202;
	}
	
	
	public static Stream<Mod202> getSamePeriodFiscalModels(AONContext ctx,Mod202 fm) {
		return FiscalModelDAO.getSamePeriodFiscalModels(ctx, fm, Mod202::new);
	}
	
	public static Stream<Mod202> getSamePeriodEffectiveModels(AONContext ctx, Mod202 mod) {
		Condition cond = FS_MODEL.YEAR.eq(mod.getYear())
			.and(FS_MODEL.PERIOD.eq(mod.getPeriod().value()));
		if (mod.getId() != null) {
			cond = cond.and(FS_MODEL.ID.ne(mod.getId())); 
		}
		return getEffectiveModels(ctx, mod, cond ); 
	}
	
	public static Stream<Mod202> getEffectiveModels(AONContext ctx, Mod202 mod202, Condition cond ) {
		LinkedHashMap<Period, LinkedList<Mod202>> map = new LinkedHashMap<>();
		getSelect(ctx)
			.where(FS_MODEL.DOMAIN.eq(mod202.getDomain()))
			.and(FS_MODEL.MODEL.eq( mod202.getModel().getValue() ))
			.and(FS_MODEL.ADMINISTRATION.eq(mod202.getAdministration().value()))
			.and( cond )
			.orderBy(FS_MODEL.PERIOD.desc(),FS_MODEL.ID.asc())
			.fetch()
			.stream()
			.map(rec -> new FiscalModelFiller<Mod202>().apply(rec, Mod202::new))
			.filter( mod-> mod202.getPeriod().isMonthPeriod() == mod.getPeriod().isMonthPeriod())
			.filter( mod-> mod202.getPeriod().isQuarterPeriod() == mod.getPeriod().isQuarterPeriod())
			.forEach( mod -> {
				Mod202Declaration dec = Mod202Declaration.getInstance(mod);
				map.computeIfAbsent(mod.getPeriod(), k -> new LinkedList<>());
				if ( isEffectiveReplacement(dec, mod) ) {
					map.get(mod.getPeriod()).clear();
					map.get(mod.getPeriod()).add( mod);					
				} else {
					if (map.get(mod.getPeriod())
						.stream()
						.noneMatch( m -> isEffectiveReplacement(dec, mod)) ) {
						map.get(mod.getPeriod()).add( mod );
					}
				}
			});
		return map.values()
			.stream()
			.flatMap(Collection<Mod202>::stream)
			.map(mod -> fillModelDetails(ctx,mod))
		;
	}
	private static boolean isEffectiveReplacement( Mod202Declaration dec, Mod202 mod) {
		return mod.isReplacement() || (mod.isComplementary() && dec.getComplementaryBehaviour(mod) == ComplementaryBeahaviour.REPLACEMENT); 
	}

	public static Stream<Mod202> getSamePeriodModels(AONContext ctx,Mod202 fm) {
		return FiscalModelDAO.getSamePeriodModels(ctx, fm, Mod202::new);
	}
	
	public static Mod202 calculate(AONContext ctx, Mod202 mod202) {
		Mod202Declaration dec = Mod202Declaration.getInstance(mod202);
		return dec.calculate( ctx, mod202 ); 
	}

	public static Mod202 save(AONContext ctx, Mod202 mod202) {
		beforeSave(mod202);
		calculate(ctx, mod202);
		return FiscalModelDAO.save(ctx, mod202);
	}
	
	private static void beforeSave(Mod202 mod202) {

		if (mod202.getInitialDate() != null) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(DD_MM_YYYY);
				String date = formatter.format(mod202.getInitialDate());
				mod202.putDescription(Mod202Key.P02,date);
			} catch (NumberFormatException e) {
				mod202.putDescription(Mod202Key.P02,null);
			}
		} else {
			mod202.putDescription(Mod202Key.P02,null);
		}
		
//		if (mod202.getCnae() != null ) {
//			try {
//				mod202.putDescription(Mod202Key.P03, mod202.getCnae().getCode());
//			} catch (NumberFormatException e) {
//				mod202.putDescription(Mod202Key.P03,null);
//			}
//		} else {
//			mod202.putDescription(Mod202Key.P03,null);
//		}
		mod202.putDescription(Mod202Key.P03, mod202.getCnae());

	}
	
	public static Mod202 saveComments(AONContext ctx, Mod202 mod202) {
		FiscalModelDAO.saveComments(ctx, mod202);
		return mod202;
	}
	
	public static Mod202 initialize(AONContext ctx,Mod202 mod202) {
		if (mod202 == null) {
			mod202 = new Mod202();
			mod202.setDomain(ctx.getDomainId());
//			mod202.setAdministration(Administration.COMMON_TERRITORY);
//			initializeFiscalModel(ctx, mod202);
//			if (mod202.getPeriod() == Period.T2) {
//				mod202.setPeriod(Period.T1);
//			}
//			if (mod202.getPeriod() == Period.T3) {
//				mod202.setPeriod(Period.T2);
//			}
//			if (mod202.getPeriod() == Period.T4) {
//				mod202.setPeriod(Period.T3);
//			}
		}
		mod202.setAdministration(Administration.COMMON_TERRITORY);
		initializeFiscalModel(ctx, mod202);
		Mod202Declaration dec = Mod202Declaration.getInstance(mod202);
		dec.initialize( ctx, mod202 );
		dec.ensureDetails(mod202);
		return mod202;
	}
	
	public static Mod202 initializeForFinish(AONContext ctx,Mod202 mod202) {
		calculate(ctx, mod202);
		Mod202Declaration dec = Mod202Declaration.getInstance(mod202);
		dec.initializeDeclarationType(mod202);
		return FiscalModelDAO.initializeForFinish(ctx, mod202);
	}

	public static Mod202 create(AONContext ctx,Mod202 mod202) {
		Mod202Declaration dec = Mod202Declaration.getInstance(mod202);
		dec.ensureDetails(mod202);
		dec.uniqueInitialize(ctx, mod202);
		return save(ctx, mod202);
	}
	
	public static Mod202 markAsFinished(AONContext ctx,Mod202 mod202) {
		FiscalModelValidation.statusChange(mod202, FiscalStatus.FINISHED);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod202);
		mod202 = FiscalModelDAO.finish(ctx, mod202);
		mod202 = save(ctx, mod202);
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, mod202.getFinanceId())) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod202;
	}

	public static Mod202 markAsPending(AONContext ctx,Mod202 mod202) {
		FiscalModelValidation.statusChange(mod202, FiscalStatus.PENDING);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod202);
		mod202.setStatus(FiscalStatus.PENDING);
		mod202.setDeclarationResult(null);
		mod202.setDeclarationResultType(null);
		mod202.setFinance(null);
		mod202 = save(ctx, mod202);
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, mod202.getFinanceId())) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod202;
	}

	public static Mod202 markAsSent(AONContext ctx,Mod202 mod202) {
		FiscalModelValidation.statusChange(mod202, FiscalStatus.SENT);
		mod202.setStatus(FiscalStatus.SENT);
		mod202 = save(ctx, mod202);
		return mod202;
	}
	
	public static Mod202 markAsCustomerCheck(AONContext ctx,Mod202 mod202) {
		FiscalModelValidation.statusChange(mod202, FiscalStatus.CUSTOMER_CHECK);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod202);
		mod202 = FiscalModelDAO.finish(ctx, mod202);
		mod202.setStatus(FiscalStatus.CUSTOMER_CHECK);
		mod202 = save(ctx, mod202);
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, mod202.getFinanceId())) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod202;
	}

	public static Mod202 markAsCustomerAccepted(AONContext ctx,Mod202 mod202) {
		FiscalModelValidation.statusChange(mod202, FiscalStatus.CUSTOMER_ACCEPTED);
		mod202.setStatus(FiscalStatus.CUSTOMER_ACCEPTED);
		mod202 = save(ctx, mod202);
		return mod202;
	}

	public static Mod202 markAsCustomerRejected(AONContext ctx,Mod202 mod202, String reason) {
		FiscalModelValidation.statusChange(mod202, FiscalStatus.CUSTOMER_REJECTED);
		mod202.setStatus(FiscalStatus.CUSTOMER_REJECTED);
		if (AonStringUtils.isNotBlank(reason)) {
			String comments = mod202.getComments();
			if (AonStringUtils.isNotBlank(comments)) {
				comments = AonStringUtils.join(comments, "\n", reason);
			} else {
				comments = reason; 
			}
			mod202.setComments( comments );
		}
		mod202 = save(ctx, mod202);
		return mod202;
	}
	
	public static Mod202 aeatPresentation(AONContext ctx, Mod202 mod202, String aeatResponse) {
		if (AonStringUtils.isNotBlank(aeatResponse)) {
			DataResponseDAO.insertAEATResponse(ctx, mod202, aeatResponse);
			AEATResponse response = AEATJson.toJSON(aeatResponse.getBytes());
			Mod202 changed = get(ctx, mod202.getId());
			if (changed != null) {
				changed.setNumber(response.getJustificante());
				return markAsSent(ctx, changed);
			}
		}
		return mod202;
	}
	
	// FALTA - SE OBTIENE EL CNAE DE LA ACTIVIDAD PRINCIPAL DE LA EMPRESA (CNAE2009 HASTA 2025 Y CNAE2025 A PARTIR DE 2026)
	// EL PROBLEMA ES QUE AHORA NO EXISTE EL CNAE2025 EN LA ACTIVIDAD PRINCIPAL DE LA EMPRESA, POR AHORA SE GRABA EL EQUIVALENTE.
	static String getMainActivityCNAE(AONContext ctx, Mod202 mod) {
		Date atDate = AonDateUtils.getDate(mod.getYear(), mod.getPeriod().getStartMonth(),1);
		EnterpriseActivity activity  = CompanyDAO.getEnterpriseActivities(ctx, ctx.getDomainId(), atDate)
			.filter( ea -> ea.isPrincipal() )
			.findFirst()
			.orElse(null);
		String cnaeCode = activity == null ? null : activity.getCnaeCode();		
		cnaeCode = AonStringUtils.substring(cnaeCode,0,2) + "." + AonStringUtils.substring(cnaeCode,2,4);
		
		if (mod.getYear() >= 2026) {
			// A PARTIR DEL EJERCICIO 2026 SE GRABA EL EQUIVALENTE CNAE2025 DEL CNAE2009 GRABADO EN LA ACTIVIDAD, PORQUE LA ACTIVIDAD NO TIENE CNAE2025
			CNAE2025 cnae2025 = getCNAE2025ByDefault(cnaeCode) ;
			mod.setCnae(cnae2025 == null ? null : cnae2025.getCode());
		} else {
			// Hasta el ejercicio 2025 se graba el CNAE2009
	 		CNAE2009 cnae2009 = CNAE2009.valueOfCode(cnaeCode);
			mod.setCnae(cnae2009 == null ? null : cnae2009.getCode());
		}
		return cnaeCode;
	}
	
	// Devuelve el CNAE2025 por defecto, para el código CNAE2009 que se le pasa como parámetro
	// CRITERIO PARA OBTENER EL CNAE2025 A PARTIR DEL CNAE2009:
	// - SI EL CNAE2009 TIENE UN SOLO EQUIVALENTE EN EL CNAE2025, SE DEVUELVE ESE CNAE2025
	// - SI EL CNAE2009 TIENE VARIOS EQUIVALENTES EN EL CNAE2025, SE BUSCA EN LA LISTA DE CONVERSION ESPECIFICA, 
	//   SI NO EXISTE EN ESA LISTA, SE BUSCA EL QUE TENGA EL MISMO CODIGO, SI NO SE ENCUENTRA SE DEVUELVE NULO
	public static CNAE2025 getCNAE2025ByDefault(String code2009) {
		
		Map<String, String> conversionEspecifica = new HashMap<>();
		conversionEspecifica.put("01.49", "01.48");
		conversionEspecifica.put("14.13", "14.21");
		conversionEspecifica.put("14.14", "14.22");
		conversionEspecifica.put("14.19", "14.29");
		conversionEspecifica.put("16.10", "16.11");
		conversionEspecifica.put("16.29", "16.28");
		conversionEspecifica.put("22.19", "22.12");
		conversionEspecifica.put("22.23", "22.24");
		conversionEspecifica.put("22.29", "22.26");
		conversionEspecifica.put("23.61", "");
		conversionEspecifica.put("25.11", "");
		conversionEspecifica.put("25.61", "");
		conversionEspecifica.put("28.49", "28.42");
		conversionEspecifica.put("30.30", "");
		conversionEspecifica.put("35.14", "35.15");
		conversionEspecifica.put("35.15", "35.12");
		conversionEspecifica.put("35.16", "35.11");
		conversionEspecifica.put("35.17", "35.11");
		conversionEspecifica.put("35.18", "35.12");
		conversionEspecifica.put("35.19", "");
		conversionEspecifica.put("38.21", "");
		conversionEspecifica.put("38.22", "");
		conversionEspecifica.put("38.32", "38.21");
		conversionEspecifica.put("41.21", "41.01");
		conversionEspecifica.put("41.22", "41.02");
		conversionEspecifica.put("43.29", "43.24");
		conversionEspecifica.put("43.39", "43.35");
		conversionEspecifica.put("45.11", "");
		conversionEspecifica.put("45.19", "");
		conversionEspecifica.put("45.31", "46.72");
		conversionEspecifica.put("45.32", "47.82");
		conversionEspecifica.put("45.40", "");
		conversionEspecifica.put("46.51", "46.50");
		conversionEspecifica.put("46.52", "46.50");
		conversionEspecifica.put("46.65", "46.47");
		conversionEspecifica.put("46.66", "46.89");
		conversionEspecifica.put("46.69", "46.64");
		conversionEspecifica.put("46.71", "46.81");
		conversionEspecifica.put("46.72", "46.82");
		conversionEspecifica.put("46.73", "46.83");
		conversionEspecifica.put("46.74", "46.84");
		conversionEspecifica.put("46.75", "46.85");
		conversionEspecifica.put("46.76", "46.86");
		conversionEspecifica.put("46.77", "46.87");
		conversionEspecifica.put("47.19", "47.12");
		conversionEspecifica.put("47.29", "47.27");
		conversionEspecifica.put("47.41", "47.40");
		conversionEspecifica.put("47.42", "47.40");
		conversionEspecifica.put("47.43", "47.40");
		conversionEspecifica.put("47.59", "47.55");
		conversionEspecifica.put("47.63", "47.69");
		conversionEspecifica.put("47.64", "47.63");
		conversionEspecifica.put("47.65", "47.64");
		conversionEspecifica.put("47.81", "47.11");
		conversionEspecifica.put("47.82", "47.12");
		conversionEspecifica.put("47.89", "47.12");
		conversionEspecifica.put("47.99", "47.12");
		conversionEspecifica.put("49.10", "");
		conversionEspecifica.put("49.32", "49.33");
		conversionEspecifica.put("52.29", "52.26");
		conversionEspecifica.put("56.10", "");
		conversionEspecifica.put("61.20", "61.10");
		conversionEspecifica.put("63.11", "63.10");
		conversionEspecifica.put("63.12", "63.91");
		conversionEspecifica.put("64.20", "64.21");
		conversionEspecifica.put("64.30", "");
		conversionEspecifica.put("66.12", "66.14");
		conversionEspecifica.put("74.10", "74.14");
		conversionEspecifica.put("74.90", "74.99");
		conversionEspecifica.put("82.11", "82.10");
		conversionEspecifica.put("82.19", "");
		conversionEspecifica.put("86.90", "86.99");
		conversionEspecifica.put("90.03", "");
		conversionEspecifica.put("91.04", "");
		conversionEspecifica.put("96.01", "96.10");
		conversionEspecifica.put("96.02", "");
		
		CNAE2025 cnae2025 = null;	
		CNAE2009ToCNAE2025 cn = CNAE2009ToCNAE2025.valueOfCode(code2009);
		if (cn != null) {
			if (cn.getCode2025().length > 1) {
				if (conversionEspecifica.containsKey(code2009)) {
					cnae2025 = CNAE2025.valueOfCode(conversionEspecifica.get(code2009));					
				} else {
					for (String code2025 : cn.getCode2025()) {
						if (code2025.equals(code2009)) {
							cnae2025 = CNAE2025.valueOfCode(code2025);
							break;
						}
					}
				}
			} else {
				cnae2025 = CNAE2025.valueOfCode(cn.getCode2025()[0]);
			}
		}
		return cnae2025;
			
	}

	
}
