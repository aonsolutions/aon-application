package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod425;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModel390.FS_MODEL390;
import static com.esferalia.aon.jooq.tables.FsModelDetail.FS_MODEL_DETAIL;
import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;

import java.io.Serializable;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jooq.Condition;
import org.jooq.exception.DataAccessException;

import com.esferalia.aon.jooq.tables.records.FsModel390Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.Address;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025.Mod425Detail;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025DetailKey;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303Declaration;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod390.Mod390DAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod425_2025.DEC;
import com.esferalia.aon.occam.impl.jooq.dao.mod425_2025.DECToMod425;
import com.esferalia.aon.occam.impl.jooq.dao.mod425_2025.Mod425ToDEC;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

public class Mod4252025DAO {

	private static final byte ZERO_BYTE = 0;
	private static final byte ONE_BYTE = 1;
	
	private static final double PERCENT0 = 0.0;
	private static final double PERCENT3 = 3.0;
	private static final double PERCENT5 = 5.0;
	private static final double PERCENT7 = 7.0;
	private static final double PERCENT95 = 9.5;
	private static final double PERCENT15 = 15.0;
	private static final double PERCENT20 = 10.0;
	
	@FunctionalInterface
	private static interface IMod425DetailKey {
		boolean accept(Mod4252025 mod,VatContext vc);
	}

	private static class KeyedVatContext  {
		private Mod4252025DetailKeyDAO key;
		private VatContext vc;
		
		private KeyedVatContext(Mod4252025DetailKeyDAO key,VatContext vc) {
			this.key = key;
			this.vc = vc;
		}
		public Mod4252025DetailKeyDAO getKey() {
			return key;
		}
		public VatContext getVatContext() {
			return vc;
		}
	}
	
	private enum Mod4252025DetailKeyDAO implements Serializable {
		// BASE IMPONIBLE, TIPOS Y CUOTAS
	 	// Régimen ordinario
		  C003  (Mod4252025DetailKey.C003 , (mod, vc) -> isCommonNationalSales(vc, mod) && !vc.isVatAccrualRegime() && hasPercent0(vc))
		 ,C006  (Mod4252025DetailKey.C006 , (mod, vc) -> isCommonNationalSales(vc, mod) && !vc.isVatAccrualRegime() && hasPercent3(vc))
		 ,C009  (Mod4252025DetailKey.C009 , (mod, vc) -> isCommonNationalSales(vc, mod) && !vc.isVatAccrualRegime() && hasPercent7(vc))		
		 ,C012  (Mod4252025DetailKey.C012 , (mod, vc) -> isCommonNationalSales(vc, mod) && !vc.isVatAccrualRegime() && hasPercent95(vc))
		 ,C015  (Mod4252025DetailKey.C015 , (mod, vc) -> isCommonNationalSales(vc, mod) && !vc.isVatAccrualRegime() && hasPercent15(vc))
		 ,C018  (Mod4252025DetailKey.C018 , (mod, vc) -> isCommonNationalSales(vc, mod) && !vc.isVatAccrualRegime() && hasPercent20(vc))
		 ,C018B (Mod4252025DetailKey.C018B, (mod, vc) -> isCommonNationalSales(vc, mod) && !vc.isVatAccrualRegime() && hasPercent5(vc))
		 
	 	// Régimen especial de bienes usados
		 ,C021	(Mod4252025DetailKey.C021, null)
		 ,C024	(Mod4252025DetailKey.C024, null)
		 ,C027	(Mod4252025DetailKey.C027, null)
		 ,C030	(Mod4252025DetailKey.C030, null)
		 ,C033	(Mod4252025DetailKey.C033, null)
		 
	 	// Régimen especial de objetos de arte, antigüedades y objetos de colección
		 ,C036	(Mod4252025DetailKey.C036, null)
		 ,C039	(Mod4252025DetailKey.C039, null)
		 ,C042	(Mod4252025DetailKey.C042, null)
		 ,C045	(Mod4252025DetailKey.C045, null)
		 ,C048	(Mod4252025DetailKey.C048, null)
		 
		 // Régimen especial del criterio de caja
		 ,C051	(Mod4252025DetailKey.C051 , (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isVatAccrualRegime() && hasPercent0(vc))
		 ,C054 	(Mod4252025DetailKey.C054 , (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isVatAccrualRegime() && hasPercent3(vc))
		 ,C057 	(Mod4252025DetailKey.C057 , (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isVatAccrualRegime() && hasPercent7(vc))		
		 ,C060	(Mod4252025DetailKey.C060 , (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isVatAccrualRegime() && hasPercent95(vc))
		 ,C063 	(Mod4252025DetailKey.C063 , (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isVatAccrualRegime() && hasPercent15(vc))
		 ,C066	(Mod4252025DetailKey.C066 , (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isVatAccrualRegime() && hasPercent20(vc))
		 ,C066B (Mod4252025DetailKey.C066B, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isVatAccrualRegime() && hasPercent5(vc))
		 
	 	// Régimen especial de agencias de viaje
		 ,C069	(Mod4252025DetailKey.C069 , null)
		 
		 // Modificación de bases y rectificación de cuotas impositivas repercutidas
		 ,C071	(Mod4252025DetailKey.C071, (mod, vc) -> modificacionBasesYCuotasFilter(vc))
		 
	 	// Modificación de bases y cuotas por procedimientos de concurso de acreedores o créditos incobrables 
		 ,C073	(Mod4252025DetailKey.C073, null)
		 
	 	// Total bases IGIC
		 ,C074	(Mod4252025DetailKey.C074, null)
		 
	 	// Operaciones con inversión del sujeto pasivo
		 ,C076 	(Mod4252025DetailKey.C076, (mod, vc) -> isOperacionesISPFilter(vc))
		 
		 // Cuotas devueltas en Régimen de viajeros
		 ,C078	(Mod4252025DetailKey.C078, null)
		 
	 	// Total cuotas devengadas	
		 ,C079	(Mod4252025DetailKey.C079, null)
		 
		 // DEDUCCIONES
		 
		 ,C081 (Mod4252025DetailKey.C081, ((mod, vc) -> operacionesInterioresCorrientesFilter(vc)))                      // IGIC deducible en operaciones interiores corrientes
		 ,C083 (Mod4252025DetailKey.C083, ((mod, vc) -> operacionesInterioresInversionFilter(vc)))                       // IGIC deducible en operaciones interiores con bienes de inversión
		 ,C085 (Mod4252025DetailKey.C085, ((mod, vc) -> importacionesCorrientesFilter(vc)))                              // IGIC deducible por importaciones de bienes corrientes
		 ,C087 (Mod4252025DetailKey.C087, ((mod, vc) -> importacionesInversionFilter(vc)))                               // IGIC deducible por importaciones de bienes de inversión
		 ,C089 (Mod4252025DetailKey.C089, ((mod, vc) -> vc.isRectification() && (vc.isPurchase() || vc.isExpenses() )))  // Rectificación de deducciones
		 ,C090 (Mod4252025DetailKey.C090, ((mod, vc) -> compensacionesRegAgrarioFilter(vc)))                             // Compensación en régimen especial de la agricultura, ganaderia y pesca
		 ,C091 (Mod4252025DetailKey.C091, null)                                                                          // Regularización de cuotas soportadas por bienes de inversión
		 ,C092 (Mod4252025DetailKey.C092, null)                                                                          // Regularización de cuotas soportadas antes del inicio de la actividad
		 ,C093 (Mod4252025DetailKey.C093, null)                                                                          // Regularización por aplicación del porcentaje definitivo de prorrata
		 ,C094 (Mod4252025DetailKey.C094, null)                                                                          // Total cuotas deducibles
		 
		 // RESULTADO DE LAS AUTOLIQUIDACIONES
		 
		 ,C095 (Mod4252025DetailKey.C095, null) // Resultado régimen general
		 
		 // OPERACIONES ESPECÍFICAS

		 // Operaciones específicas
		 ,C120 (Mod4252025DetailKey.C120, ((mod, vc) -> (vc.isNationalSales() && !vc.isVatAccrualRegime() && vc.isVatGeneralRegime(mod.isSimplifiedRegime()?VATRegime.SIMPLIFIED:VATRegime.GENERAL)))) // Operaciones en régimen general
//		 ,C121 (Mod4252025DetailKey.C121, null) // Operaciones a las que habiéndoles sido aplicado el régimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el artículo 18 Ley 20/1991	 
		 ,C122 (Mod4252025DetailKey.C122, ((mod, vc) -> (vc.isSales() && !vc.isWithoutRightDeductionType() && vc.isExtracommunity() && !vc.isService()))) // Exportaciones definitivas y operaciones asimiladas a la exportación
//		 ,C123 (Mod4252025DetailKey.C123, null) // Operaciones relativas a áreas exentas
//		 ,C124 (Mod4252025DetailKey.C124, null) // Operaciones interiores exentas por el artículo 25 de la Ley 19/1994 realizadas por el sujeto pasivo
//		 ,C125 (Mod4252025DetailKey.C125, null) // Otras operaciones exentas con derecho a deducción
		 ,C126 (Mod4252025DetailKey.C126, ((mod, vc) -> ((vc.isSales() && !vc.isNational() && vc.isWithoutRightDeductionType()) || (vc.isNationalSales() && AonMathUtils.isZero(vc.getPercentage()) && vc.getVatRegime() != null && vc.isActivityVatExempt())))) // Operaciones exentas sin derecho a deducción
//		 ,C127 (Mod4252025DetailKey.C127, null) // Operaciones en régimen simplificado
		 ,C128 (Mod4252025DetailKey.C128, ((mod, vc) -> (vc.isSales() && !vc.isWithoutRightDeductionType() && (vc.isOtherISP() || vc.isCanCeuMel() || (vc.isExtracommunity() && vc.isService()))))) // Operaciones no sujetas por reglas de localización o con inversión del sujeto pasivo
//		 ,C129 (Mod4252025DetailKey.C129, null) // Operaciones en régimen especial de la agricultura, ganadería y pesca
//		 ,C130 (Mod4252025DetailKey.C130, null) // Operaciones en regímenes especiales de bienes usados, objetos de arte, antigüedades o colección
//		 ,C131 (Mod4252025DetailKey.C131, null) // Operaciones en régimen especial de agencias de viajes
//		 ,C132 (Mod4252025DetailKey.C132, null) // Entregas de bienes inmuebles y operaciones financieras no habituales
//		 ,C133 (Mod4252025DetailKey.C133, null) // Entregas de bienes de inversión para el transmitente
//		 ,C134 (Mod4252025DetailKey.C134, null) // Total volumen de operaciones
//		 ,C135 (Mod4252025DetailKey.C135, null) // Importaciones de bienes de inversión exentos por el artículo 25 de la Ley 19/1994
//		 ,C136 (Mod4252025DetailKey.C136, null) // Cuotas de I.G.I.C. soportado no deducible
//		 ,C137 (Mod4252025DetailKey.C137, null) // Otras operaciones no sujetas con derecho a deducción (artículo 29.4.1ªg) Ley 20/1991)
		 
		 // Exclusivamente para aquellos sujetos pasivos acogidos al régimen especial de criterio de caja y para aquellos que sean destinatarios de operaciones afectadas por el mismo
//		 ,C139 (Mod4252025DetailKey.C139, null) //	Importes de las entregas de bienes y prestaciones de servicios a las que Base Cuota habiéndoles aplicado el régimen especial de criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el art. 18 de la Ley 20/1991 138
//		 ,C141 (Mod4252025DetailKey.C141, null) //	Importes de las adquisiciones de bienes y servicios a las que sea de aplicación o afecte el régimen especial del criterio de caja conforme a la regla general de devengo contenida en el art. 18 de la Ley 20/1991
		
		 // Declaración informativa del volumen de operaciones en el régimen especial del pequeño empresario o profesional (exclusivamente a cumplimentar por los sujetos pasivos acogidos al REPEP)
//		 ,C142 (Mod4252025DetailKey.C142, null) //	Importe de operaciones habituales u ocasionales sujetas al IGIC exentas por Régimen especial del pequeño empresario o profesional
//		 ,C143 (Mod4252025DetailKey.C143, null) //	Importe de operaciones sujetas al IGIC exentas por Régimen especial del comerciante minorista
//		 ,C144 (Mod4252025DetailKey.C144, null) //	Importe de entregas de bienes y prestaciones de servicios no sujetas al IGIC imputables a la sede de la actividad económica situada en Canarias
//		 ,C145 (Mod4252025DetailKey.C145, null) //	Importe de entregas de bienes y prestaciones de servicios no sujetas al IGIC imputables a otras sedes o establecimientos situados fuera de Canarias
//		 ,C146 (Mod4252025DetailKey.C146, null) //	Importe en el supuesto de transmisión de la totalidad o parte del patrimonio empresarial o profesional
//		 ,C147 (Mod4252025DetailKey.C147, null) //	Total volumen de operaciones en el REPEP
		 ;		 
		 
		private Mod4252025DetailKey key;
		private IMod425DetailKey accept;
			
		private Mod4252025DetailKeyDAO(Mod4252025DetailKey key, IMod425DetailKey accept) {
			this.key = key;
			this.accept = accept;
		}

		public Mod4252025DetailKey getKey() {
			return key;
		}
		public boolean accept(Mod4252025 mod, VatContext vc) {
			return accept!=null && accept.accept(mod,vc);
		}
		
	}
	
	private static EnumMap<Mod4252025DetailKey, Mod425Detail> getDetails(AONContext ctx, Mod4252025 mod425 ) {
		EnumMap<Mod4252025DetailKey, Mod425Detail> map = new EnumMap<>(Mod4252025DetailKey.class);
		Arrays.stream( Mod4252025DetailKey.values() )
			.forEach(key ->  map.put(key, new Mod425Detail().setKey(key).setPercent(key.getPercent())));
		VATDAO.getVatBreakdown(ctx, mod425)
			.flatMap(vt -> Arrays.stream( Mod4252025DetailKeyDAO.values() )
			.filter(key -> key.accept(mod425, vt))
			.map(key -> new KeyedVatContext(key, vt)))
			.map(kvt -> checkProrrated(ctx, mod425, kvt))
			.map(kvt -> new Pair<KeyedVatContext,Mod425Detail>(kvt, map.computeIfAbsent(kvt.getKey().getKey(), k -> new Mod425Detail().setKey(k).setPercent(kvt.getVatContext().getPercentage()))))
			.forEach(Mod4252025DAO::add);
		
		// Regimen especial de criterio de caja.
		double vatAccBase = getVatAccrualPaymentOutputBase(ctx, mod425); 
		mod425.setBox138(vatAccBase);
		mod425.setBox139(getVatAccrualPaymentOutputQuota(ctx, mod425));
		mod425.setBox140(getVatAccrualPaymentInputBase(ctx, mod425));
		mod425.setBox141(getVatAccrualPaymentInputQuota(ctx, mod425));
		
		// Cálculo de la Regularizacion por aplicacion del porcentaje definitivo de prorrata
		
		// Si se asigna el valor habría que sumar los declarado en las declaraciones y no sacer los datos de las facturas.
		
		Condition cond = FS_MODEL.YEAR.eq(mod425.getYear())
				.and(FS_MODEL.PERIOD.eq(Period.T4.value()).or(FS_MODEL.PERIOD.eq(Period.M12.value())) );
		Mod303DAO.getEffectiveModels(ctx, mod425, cond)
			.filter( m -> m.getYear( )  == mod425.getYear())
			.filter( Mod303::isCanarias)
			.filter( Mod303::isLastPeriod)
			.map(m -> m.getAmount(Mod303Key.CA_C039))
			.forEach( r -> map.computeIfAbsent(Mod4252025DetailKey.C093, k -> new Mod425Detail().setKey(k)).setQuota(r))
		;
		return map;
	}
	
	private static void add(Pair<KeyedVatContext, Mod425Detail> pair) {
		KeyedVatContext kvt = pair.getLeft();
		Mod4252025DetailKey key = kvt.getKey().getKey();
		Mod425Detail detail = pair.getRight();
		double q = kvt.getVatContext().getQuota();
		if (key.isProrrataEnabled()) {
			if (kvt.getVatContext().isProrrated())
				q = kvt.getVatContext().getProrrateQuota();
			else
				q = kvt.getVatContext().getDeductibleQuota();
		}
		detail.setQuota( AonMathUtils.round(detail.getQuota() + q));
		detail.setTaxableBase( AonMathUtils.round( detail.getTaxableBase() + kvt.getVatContext().getBase()));
	}
	
	private static void fillGeneralDeclarationResults(AONContext ctx, Mod4252025 mod425) {
		Mod303DAO.getEffectiveModels(ctx, mod425, FS_MODEL.YEAR.eq(mod425.getYear()))
			.forEach(m303 -> {
				if (m303.isEnrolledInDevolutionRegistry()) {
					mod425.setTaxRefund(true);
				}
				if (m303.isToDeposit()) {
					mod425.setBox116( AonMathUtils.round(mod425.getBox116() + m303.getDeclarationResult()));
				} else if (m303.isToPayback()) {
					if (m303.isEnrolledInDevolutionRegistry()) {
						mod425.setBox117( AonMathUtils.round(mod425.getBox117() + (m303.getDeclarationResult() * (-1))));
					}
					if (m303.isLastPeriod()) {
						mod425.setBox119( AonMathUtils.round( m303.getDeclarationResult() * (-1) ));
					} 
				} else if (m303.isToCompensate() && m303.isLastPeriod()) {
					mod425.setBox118( AonMathUtils.round( m303.getDeclarationResult() * (-1) ));
				}
				if (m303.isFirstPeriod()) {
					mod425.setBox114( m303.getAmount( Mod303Key.CA_C043) );
				} 
			});
		
	}
	
	private static void fillFromConfiguration(AONContext ctx, Mod4252025 mod425) {
		Company company = CompanyDAO.getCompany(ctx, mod425.getDomain());
		if (company != null) {
			
			initializeIdentificationData(ctx, mod425);
			
			// Representantes
			ctx.getDslContext().select(RDIR_STAFF.DOCUMENT,RDIR_STAFF.NAME)
				.from(RDIR_STAFF)
				.where(RDIR_STAFF.DOMAIN.eq(mod425.getDomain()))
				.and(RDIR_STAFF.REGISTRY.eq(company.getId()))
				.and(RDIR_STAFF.REPRESENTATIVE.eq( (byte) 1))
				.fetch()
				.stream()
				.forEach( rec -> {
					String document = rec.getValue(RDIR_STAFF.DOCUMENT);
					String name = rec.getValue(RDIR_STAFF.NAME);					
					if (mod425.isLegalEntity()) {
						LegalRepresentative legalRepr = new LegalRepresentative()
							.setDocument(document)
							.setName(name);
						if (mod425.getLegalRepr1() == null) mod425.setLegalRepr1(legalRepr); 
						else if (mod425.getLegalRepr2() == null) mod425.setLegalRepr2(legalRepr);
						else if (mod425.getLegalRepr3() == null) mod425.setLegalRepr3(legalRepr);
					} else {
						if (mod425.getAddress() == null) {
							mod425.setAddress(new Address().setRdocument(document).setRname(name));
						}
					}
				});
		}
	}
	
	private static Mod4252025 create(AONContext ctx, Mod390 model) {
		Mod4252025 mod425 = new Mod4252025();
		mod425.setId(model.getId());
		mod425.setDomain(model.getDomain());
		mod425.setDomainName(model.getDomainName());
		mod425.setEnterprise(model.getEnterprise());
		mod425.setEnterpriseName(model.getEnterpriseName());
		mod425.setYear(model.getYear());
		mod425.setAdministration(model.getAdministration());
		mod425.setStatus(model.getStatus());
		mod425.setOldStyle(false);
		mod425.setReplacement(model.isReplacement());
		mod425.setComplementary(model.isComplementary());
		mod425.setWithoutActivity(model.isWithoutActivity());
		mod425.setDocument(model.getDocument());
		mod425.setName(model.getName());
		mod425.setFirstSurname(model.getFirstSurname());
		mod425.setSecondSurname(model.getSecondSurname());
		mod425.setContactPhone(model.getContactPhone());
		mod425.setReceipt(model.getReceipt());
		mod425.setReplacedReceipt(model.getReplacedReceipt());
		mod425.setComments(model.getComments());
		mod425.setCreationUser(model.getCreationUser());
		mod425.setCreationDate(model.getCreationDate());
		mod425.setModificationUser(model.getModificationUser());
		mod425.setModificationDate(model.getModificationDate());
		
		// Buscar si existe algún ejercicio anterior (EL 2025 ES EL PRIMER EJERCICIO DEL M425)
		boolean found = false;
//		boolean found = Mod390DAO.getByDomain(ctx, ctx.getDomainId())
//			.stream()
//			.filter(m390 -> m390.getYear() == 2024)
//			.map(m390 -> Mod3902023DAO.getById(ctx, m390.getId()))
//			.map(mod3902023 -> {
//				mod425.setMainActivity(mod3902023.getMainActivity());
//				mod425.setActivity1(mod3902023.getActivity1());
//				mod425.setActivity2(mod3902023.getActivity2());
//				mod425.setActivity3(mod3902023.getActivity3());
//				mod425.setActivity4(mod3902023.getActivity4());
//				mod425.setActivity5(mod3902023.getActivity5());
//				mod425.setAddress(mod3902023.getAddress());
//				mod425.setLegalRepr1(mod3902023.getLegalRepr1());
//				mod425.setLegalRepr2(mod3902023.getLegalRepr2());
//				mod425.setLegalRepr3(mod3902023.getLegalRepr3());
//				return mod3902023;
//			})
//			.findAny()
//			.isPresent();
//		
//		if (!found)
//			found = Mod390DAO.getByDomain(ctx, ctx.getDomainId())
//				.stream()
//				.filter(m390 -> m390.getYear() == 2022)
//				.map(m390 -> Mod3902022DAO.getById(ctx, m390.getId()))
//				.map(mod3902022 -> {
//					mod425.setMainActivity(mod3902022.getMainActivity());
//					mod425.setActivity1(mod3902022.getActivity1());
//					mod425.setActivity2(mod3902022.getActivity2());
//					mod425.setActivity3(mod3902022.getActivity3());
//					mod425.setActivity4(mod3902022.getActivity4());
//					mod425.setActivity5(mod3902022.getActivity5());
//					mod425.setAddress(mod3902022.getAddress());
//					mod425.setLegalRepr1(mod3902022.getLegalRepr1());
//					mod425.setLegalRepr2(mod3902022.getLegalRepr2());
//					mod425.setLegalRepr3(mod3902022.getLegalRepr3());
//					return mod3902022;
//				})
//				.findAny()
//				.isPresent();
//		
//		if (!found)
//			found = Mod390DAO.getByDomain(ctx, ctx.getDomainId())
//				.stream()
//				.filter(m390 -> m390.getYear() == 2021)
//				.map(m390 -> Mod3902021DAO.getById(ctx, m390.getId()))
//				.map(mod3902021 -> {
//					mod425.setMainActivity(mod3902021.getMainActivity());
//					mod425.setActivity1(mod3902021.getActivity1());
//					mod425.setActivity2(mod3902021.getActivity2());
//					mod425.setActivity3(mod3902021.getActivity3());
//					mod425.setActivity4(mod3902021.getActivity4());
//					mod425.setActivity5(mod3902021.getActivity5());
//					mod425.setAddress(mod3902021.getAddress());
//					mod425.setLegalRepr1(mod3902021.getLegalRepr1());
//					mod425.setLegalRepr2(mod3902021.getLegalRepr2());
//					mod425.setLegalRepr3(mod3902021.getLegalRepr3());
//					return mod3902021;
//				})
//				.findAny()
//				.isPresent();
//		
//		if (!found)
//			found =  Mod390DAO.getByDomain(ctx, ctx.getDomainId())
//				.stream()
//				.filter(m390 -> m390.getYear() == 2018 || m390.getYear() == 2019 || m390.getYear() == 2020)
//				.map(m390 -> Mod3902018DAO.getById(ctx, m390.getId()))
//				.map(mod3902018 -> {
//					mod425.setMainActivity(mod3902018.getMainActivity());
//					mod425.setActivity1(mod3902018.getActivity1());
//					mod425.setActivity2(mod3902018.getActivity2());
//					mod425.setActivity3(mod3902018.getActivity3());
//					mod425.setActivity4(mod3902018.getActivity4());
//					mod425.setActivity5(mod3902018.getActivity5());
//					mod425.setAddress(mod3902018.getAddress());
//					mod425.setLegalRepr1(mod3902018.getLegalRepr1());
//					mod425.setLegalRepr2(mod3902018.getLegalRepr2());
//					mod425.setLegalRepr3(mod3902018.getLegalRepr3());
//					return mod3902018;
//				})
//				.findAny()
//				.isPresent();
		
		if (!found) {
			fillFromConfiguration(ctx, mod425);	
		}
		
		// FALTA - REGIMEN SIMPLIFICADO CUANDO SE HAGA EL MODELO 421
//		fillSimplifedRegimeData(ctx, mod425);
		fillGeneralRegimeData(ctx, mod425);
		if (mod425.isSimplifiedRegime()) {
			// FALTA - REGIMEN SIMPLIFICADO CUANDO SE HAGA EL MODELO 421
//			fillSimplifiedDeclarationResults(ctx, mod425);
		} else {
			fillGeneralDeclarationResults(ctx, mod425);
		}
		mod425.calculate();
		return mod425;	
	}

	public static Mod4252025 getMod4252025(AONContext ctx, Mod390 m425) {
		if (m425.getId() == null) {
			return create(ctx, m425);
		}
		return getById(ctx, m425.getId());
	}

	public static Mod4252025 getById(AONContext ctx, int id) {
		ctx.checkRead();
		Mod4252025 mod425 = new Mod4252025();
		ctx.getDslContext()
				.selectFrom(FS_MODEL390)
				.where(FS_MODEL390.ID.equal(id))
				.fetch()
				.stream()
				.forEach(rec -> populate(rec, mod425));
		if (mod425.getId() != null) {
			return mod425;
		}
		return null;
	}

	private static void populate(FsModel390Record rec, Mod4252025 mod425)  {
		mod425
		.setId(rec.getValue(FS_MODEL390.ID))
		.setAdministration( com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Administration.class,rec.getValue(FS_MODEL390.ADMINISTRATION)))
		.setReplacement( rec.getValue(FS_MODEL390.REPLACEMENT)==1 )
		.setComplementary(rec.getValue(FS_MODEL390.COMPLEMENTARY)==1 )
		.setStatus(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(FiscalStatus.class,rec.getValue(FS_MODEL390.STATUS)))
		.setYear(rec.getValue(FS_MODEL390.YEAR))
		.setDomain(rec.getValue(FS_MODEL390.DOMAIN))
		.setEnterprise(rec.getValue(FS_MODEL390.ENTERPRISE))
		.setDocument(rec.getValue(FS_MODEL390.DOCUMENT))
		.setEnterpriseName(rec.getValue(FS_MODEL390.NAME))
		.setReceipt(rec.getValue(FS_MODEL390.RECEIPT))
		.setReplacedReceipt(rec.getValue(FS_MODEL390.REPLACED_RECEIPT))
		.setComments(rec.getValue(FS_MODEL390.COMMENTS));
		
		// Cargar resto de los dato del modelo desde el XML. Se controla que la cabecera del XML sea la correcta
		String model = rec.getValue(FS_MODEL390.MODEL);
		if (AonStringUtils.contains(model, "<DEC MOD=\"425\" ANY=\""+mod425.getYear()+"\""))  { 
			StringReader reader = new StringReader(rec.getValue(FS_MODEL390.MODEL));
			try {
				if (mod425.getYear() >= 2025) {
					JAXBContext context = JAXBContext.newInstance(DEC.class);
					Unmarshaller um = context.createUnmarshaller();
					DEC dec = (DEC) um.unmarshal(reader);
					DECToMod425.populate(mod425, dec);
					mod425.setXmlFormat("DEC_425_2025");
				} else {
					throw new IllegalArgumentException("Ejercicio incorrecto.");
				}
			} catch (ParseException | JAXBException e1) {
				e1.printStackTrace();
				throw new AonCoreException("XML PROBLEM",e1);
			}
		} else {
			mod425.setXmlFormat("INVALID");
		}
		
	}
	
//	private static String getXMLContentById(AONContext ctx, Integer id) {
//		ctx.checkRead();
//		return ctx.getDslContext()
//			.selectFrom(FS_MODEL390)
//			.where(FS_MODEL390.ID.equal(id))
//			.fetchOne(FS_MODEL390.MODEL);
//	}

	public static Mod4252025 save(AONContext ctx, Mod4252025 mod425)  {
		if (mod425.getId() == null) {
			return insert(ctx, mod425);
		} else {
			return update(ctx, mod425);
		}
	}
	
	private static String getXMLModel(Mod4252025 mod425) {
		try {
			if (mod425.getYear() >= 2025) {
				return Mod425ToDEC.getDeclaration(mod425); 
			} else {
				throw new IllegalArgumentException("Ejercicio incorrecto.");
			}
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new AonCoreException("Error en conversión XML",e);
		}				
	}
	
	private static Mod4252025 insert(AONContext ctx, Mod4252025 mod425) {
		validate(ctx, mod425);
		FsModel390Record rec = ctx.getDslContext()
			.insertInto(FS_MODEL390)
			.set(FS_MODEL390.DOMAIN, mod425.getDomain())
			.set(FS_MODEL390.ENTERPRISE, mod425.getEnterprise())
			.set(FS_MODEL390.YEAR, mod425.getYear())
			.set(FS_MODEL390.ADMINISTRATION,mod425.getAdministration().value())
			.set(FS_MODEL390.STATUS,AonEnumUtils.getByte( mod425.getStatus()  ))
			.set(FS_MODEL390.SECURITY_LEVEL,AonEnumUtils.getByte(mod425.isConfidential()))
			.set(FS_MODEL390.COMPLEMENTARY, (byte) 0)
			.set(FS_MODEL390.REPLACEMENT,AonEnumUtils.getByte(mod425.isReplacement()))
			.set(FS_MODEL390.DOCUMENT, mod425.getDocument())
			.set(FS_MODEL390.NAME, mod425.getName())
			.set(FS_MODEL390.COMMENTS, mod425.getComments())
			.set(FS_MODEL390.RECEIPT, mod425.getReceipt())
			.set(FS_MODEL390.REPLACED_RECEIPT, mod425.getReplacedReceipt())
			.set(FS_MODEL390.MODEL, getXMLModel(mod425))
			.returning(FS_MODEL390.ID).fetchOne();
		mod425.setId(rec.getId());
		return mod425;
	}
	
	private static Mod4252025 update(AONContext ctx, Mod4252025 mod425) {
		validate(ctx, mod425);
		mod425.calculate();
		ctx.getDslContext()
			.update(FS_MODEL390)
			.set(FS_MODEL390.YEAR, mod425.getYear())
			.set(FS_MODEL390.ADMINISTRATION,mod425.getAdministration().value())
			.set(FS_MODEL390.STATUS,AonEnumUtils.getByte( mod425.getStatus()  ))
			.set(FS_MODEL390.SECURITY_LEVEL,AonEnumUtils.getByte(mod425.isConfidential()))
			.set(FS_MODEL390.COMPLEMENTARY, (byte) 0)
			.set(FS_MODEL390.REPLACEMENT,AonEnumUtils.getByte(mod425.isReplacement()))
			.set(FS_MODEL390.SECURITY_LEVEL,AonEnumUtils.getByte(mod425.isConfidential()))
			.set(FS_MODEL390.DOCUMENT, mod425.getDocument())
			.set(FS_MODEL390.NAME, mod425.getName())
			.set(FS_MODEL390.COMMENTS, mod425.getComments())
			.set(FS_MODEL390.RECEIPT, mod425.getReceipt())
			.set(FS_MODEL390.REPLACED_RECEIPT, mod425.getReplacedReceipt())
			.set(FS_MODEL390.MODEL, getXMLModel(mod425))
			.where(FS_MODEL390.ID.equal(mod425.getId())).execute();
		return mod425;
	}

	// FALTA - AHORA TAMBIEN HAY QUE TENER EN CUENTA LA ADMINISTRACION Y TAMBIEN EN EL MODELO 390 DEL EJERCICIO 2025,
	// PUES PODRIA HABER 2 MODELOS PARA EL MISMO AÑO DE DIFERENTES ADMINISTRACIONES
	private static void validate(AONContext ctx, Mod4252025 mod425) {
		if (mod425.isReplacement()) {
			// Se comprueba que exista la declaración sustituida.
			if (!ctx.getDslContext().selectOne()
					.from(FS_MODEL390)
					.where(FS_MODEL390.YEAR.equal(mod425.getYear())
					.and(FS_MODEL390.ENTERPRISE.equal(mod425.getEnterprise()))
//					.and(FS_MODEL390.RECEIPT.equal(mod390.getReplacedReceipt()))
					.and(FS_MODEL390.ADMINISTRATION.equal(mod425.getAdministration().value()))
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
					.where(FS_MODEL390.YEAR.equal(mod425.getYear())
					.and(FS_MODEL390.ENTERPRISE.equal(mod425.getEnterprise()))
					.and(FS_MODEL390.ADMINISTRATION.equal(mod425.getAdministration().value()))
					.and(FS_MODEL390.REPLACEMENT.equal( ONE_BYTE ))					
					.and(FS_MODEL390.REPLACED_RECEIPT.equal(mod425.getReplacedReceipt())))
					.and(	(mod425.getId()!=null)
							?FS_MODEL390.ID.ne(mod425.getId())
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
				.where(FS_MODEL390.YEAR.equal(mod425.getYear())
				.and(FS_MODEL390.ENTERPRISE.equal(mod425.getEnterprise()))
				.and(FS_MODEL390.ADMINISTRATION.equal(mod425.getAdministration().value()))
				.and(FS_MODEL390.REPLACEMENT.equal(ZERO_BYTE)))
				.and(	(mod425.getId()!=null)
						?FS_MODEL390.ID.ne(mod425.getId())
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

	public static void delete(AONContext ctx, Mod4252025 mod425) {
		ctx.checkWrite();
		ctx.getDslContext().delete(FS_MODEL390)
				.where(FS_MODEL390.ID.equal(mod425.getId())).execute();
	}
	
	private static Mod4252025 fillGeneralRegimeData(AONContext ctx, Mod4252025 mod425) {
		try {
			Mod425Detail det = null;
			EnumMap<Mod4252025DetailKey, Mod425Detail> map = getDetails(ctx, mod425);
			
			// Operaciones esecíficas
			mod425.setBox120(map.get(Mod4252025DetailKey.C120).getTaxableBase()); // 120 Operaciones en régimen general
//			mod425.setBox121(map.get(Mod4252025DetailKey.C121).getTaxableBase()); // 121 Operaciones a las que habiéndoles sido aplicado el régimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el artículo 18 Ley 20/1991
			mod425.setBox122(map.get(Mod4252025DetailKey.C122).getTaxableBase()); // 122 Exportaciones definitivas y operaciones asimiladas a la exportación
//			mod425.setBox123(map.get(Mod4252025DetailKey.C123).getTaxableBase()); // 123 Operaciones relativas a áreas exentas
//			mod425.setBox124(map.get(Mod4252025DetailKey.C124).getTaxableBase()); // 124 Operaciones interiores exentas por el artículo 25 de la Ley 19/1994 realizadas por el sujeto pasivo
//			mod425.setBox125(map.get(Mod4252025DetailKey.C125).getTaxableBase()); // 125 Otras operaciones exentas con derecho a deducción
			mod425.setBox126(map.get(Mod4252025DetailKey.C126).getTaxableBase()); // 126 Operaciones exentas sin derecho a deducción
//			mod425.setBox127(map.get(Mod4252025DetailKey.C127).getTaxableBase()); // 127 Operaciones en régimen simplificado
			mod425.setBox128(map.get(Mod4252025DetailKey.C128).getTaxableBase()); // 128 Operaciones no sujetas por reglas de localización o con inversión del sujeto pasivo
//			mod425.setBox129(map.get(Mod4252025DetailKey.C129).getTaxableBase()); // 129 Operaciones en régimen especial de la agricultura, ganadería y pesca 
//			mod425.setBox130(map.get(Mod4252025DetailKey.C130).getTaxableBase()); // 130 Operaciones en regímenes especiales de bienes usados, objetos de arte, antigüedades o colección
//			mod425.setBox131(map.get(Mod4252025DetailKey.C131).getTaxableBase()); // 131 Operaciones en régimen especial de agencias de viajes
//			mod425.setBox132(map.get(Mod4252025DetailKey.C132).getTaxableBase()); // 132 Entregas de bienes inmuebles y operaciones financieras no habituales
//			mod425.setBox133(map.get(Mod4252025DetailKey.C133).getTaxableBase()); // 133 Entregas de bienes de inversión para el transmitente
//			mod425.setBox134(map.get(Mod4252025DetailKey.C134).getTaxableBase()); // 134 Total volumen de operaciones
//			mod425.setBox135(map.get(Mod4252025DetailKey.C135).getTaxableBase()); // 135 Importaciones de bienes de inversión exentos por el artículo 25 de la Ley 19/1994
//			mod425.setBox136(map.get(Mod4252025DetailKey.C136).getTaxableBase()); // 136 Cuotas de I.G.I.C. soportado no deducible
//			mod425.setBox137(map.get(Mod4252025DetailKey.C137).getTaxableBase()); // 137 Otras operaciones no sujetas con derecho a deducción (artículo 29.4.1ªg) Ley 20/1991)
			
			// Exclusivamente para aquellos sujetos pasivos acogidos al régimen especial de criterio de caja y para aquellos	que sean destinatarios de operaciones afectadas por el mismo
//			mod425.setBox138(map.get(Mod4252025DetailKey.C139).getTaxableBase()); // 138 Importes de las entregas de bienes y prestaciones de servicios a las que Base Cuota habiéndoles aplicado el régimen especial de criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el art. 18 de la Ley 20/1991 - Base
//			mod425.setBox139(map.get(Mod4252025DetailKey.C139).getQuota()); // 139 Importes de las entregas de bienes y prestaciones de servicios a las que Base Cuota habiéndoles aplicado el régimen especial de criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el art. 18 de la Ley 20/1991 - Cuota
//			mod425.setBox140(map.get(Mod4252025DetailKey.C141).getTaxableBase()); // 140 Importes de las adquisiciones de bienes y servicios a las que sea de aplicación o afecte el régimen especial del criterio de caja conforme a la regla general de devengo contenida en el art. 18 de la Ley 20/1991 - Base
//			mod425.setBox141(map.get(Mod4252025DetailKey.C141).getQuota()); // 141 Importes de las adquisiciones de bienes y servicios a las que sea de aplicación o afecte el régimen especial del criterio de caja conforme a la regla general de devengo contenida en el art. 18 de la Ley 20/1991 - Cuota
//			mod425.setAccrualRegime( ( map.get(Mod4252025DetailKey.C139).getTaxableBase()  != 0 || map.get(Mod4252025DetailKey.C139).getQuota() != 0 ) );		
//			mod425.setAccrualRegimeTarget((map.get(Mod4252025DetailKey.C141).getTaxableBase()  != 0 || map.get(Mod4252025DetailKey.C141).getQuota() != 0 ));
			mod425.setAccrualRegimeTarget(mod425.getBox140() != 0 || mod425.getBox141() != 0);

			// Declaración informativa del volumen de operaciones en el régimen especial del pequeño empresario o profesional
//			mod425.setBox142(map.get(Mod4252025DetailKey.C142).getTaxableBase()); // 142 Importe de operaciones habituales u ocasionales sujetas al IGIC exentas por Régimen especial del pequeño empresario o profesional
//			mod425.setBox143(map.get(Mod4252025DetailKey.C143).getTaxableBase()); // 143 Importe de operaciones sujetas al IGIC exentas por Régimen especial del comerciante minorista
//			mod425.setBox144(map.get(Mod4252025DetailKey.C144).getTaxableBase()); // 144 Importe de entregas de bienes y prestaciones de servicios no sujetas al IGIC imputables a la sede de la actividad económica situada en Canarias
//			mod425.setBox145(map.get(Mod4252025DetailKey.C145).getTaxableBase()); // 145 Importe de entregas de bienes y prestaciones de servicios no sujetas al IGIC imputables a otras sedes o establecimientos situados fuera de Canarias
//			mod425.setBox146(map.get(Mod4252025DetailKey.C146).getTaxableBase()); // 146 Importe en el supuesto de transmisión de la totalidad o parte del patrimonio empresarial o profesional
//			mod425.setBox147(map.get(Mod4252025DetailKey.C147).getTaxableBase()); // 147 Total volumen de operaciones en el REPEP
			
			// FALTA - REVISAR SI ESTO DEBE SEGUIR SIENDO ASI
			// ----------------------------------------------------------------------------
			// En el caso de que el declarante este acogido al regimen simplificado
			// Se utiliza toda la funcionalidad del regimen general (lectura de facturas)
			// para rellenar los campos anteriores, del 120 al 147. Sin embargo la 
			// página 2 del modelo, o sea la del regimen general debe ir vacia, por lo 
			// que se incializa el mapa.
			if (mod425.isSimplifiedRegime()) {
				map = new EnumMap<>(Mod4252025DetailKey.class);
				for (Mod4252025DetailKey key : Mod4252025DetailKey.values() ) {
					det = new Mod425Detail();
					det.setKey(key);
					det.setPercent(key.getPercent());
					map.put(key, det);
				}
			}
			// ----------------------------------------------------------------------------
			
			mod425.setGeneralRegime(map);
			return mod425;
		} catch (Exception t) {
			t.printStackTrace();
			throw t; 
		}
	}

//	static class SimplifedRegimeContext {
//		private Mod303Key key;
//		private String description;
//		private double amount;
//
//		public SimplifedRegimeContext(Mod303Key key,String description,double amount) {
//			this.key = key;
//			this.description = description;
//			this.amount = amount;
//		}
//		public Mod303Key getKey() {
//			return key;
//		}
//		public void setKey(Mod303Key key) {
//			this.key = key;
//		}
//		public String getDescription() {
//			return description;
//		}
//		public void setDescription(String description) {
//			this.description = description;
//		}
//		public double getAmount() {
//			return amount;
//		}
//		public void setAmount(double amount) {
//			this.amount = amount;
//		}
//	}
	
//	@FunctionalInterface
//	static interface ISimplifiedRegimeFiller {
//		boolean fill(SimplifedRegimeContext src, Mod4252025 mod425);
//	}

//	enum SimplifiedRegimeFiller {
//		
////		CAG1     (Mod303Key.CT_SA11,(src,mod425) -> {mod425.getFarmerRegime1().setCodigo(AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(),"-")));return true;}), 
////		CAG1_V1  (Mod303Key.CT_SA12,(src,mod425) -> {mod425.getFarmerRegime1().setIncomes(src.getAmount());return true;}),
////		CAG1_V2  (Mod303Key.CT_SA13,(src,mod425) -> {mod425.getFarmerRegime1().setQuotaIndex( AonMathUtils.round(src.getAmount() / 10000 ,5));return true;}),
////		CAG1_V3  (Mod303Key.CT_SA14,(src,mod425) -> {mod425.getFarmerRegime1().setAccrualQuota(src.getAmount());return true;}),
////		CAG1_V4  (Mod303Key.CT_SA1R,(src,mod425) -> {mod425.getFarmerRegime1().setDanaReduction(src.getAmount());return true;}),
////		CAG1_V6  (Mod303Key.CT_SA1A,(src,mod425) -> {mod425.getFarmerRegime1().setInputQuotas(src.getAmount());return true;}),
////		CAG1_V7  (Mod303Key.CT_SA18,(src,mod425) -> {mod425.getFarmerRegime1().setQuota(src.getAmount());return true;}),
////		                                    
////		CAG2     (Mod303Key.CT_SA21,(src,mod425) -> {mod425.getFarmerRegime2().setCodigo(AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-")));return true;}),
////		CAG2_V1  (Mod303Key.CT_SA22,(src,mod425) -> {mod425.getFarmerRegime2().setIncomes(src.getAmount());return true;}),
////		CAG2_V2  (Mod303Key.CT_SA23,(src,mod425) -> {mod425.getFarmerRegime2().setQuotaIndex(AonMathUtils.round(src.getAmount() / 10000 ,5));return true;}),
////		CAG2_V3  (Mod303Key.CT_SA24,(src,mod425) -> {mod425.getFarmerRegime2().setAccrualQuota(src.getAmount());return true;}),
////		CAG2_V4  (Mod303Key.CT_SA2R,(src,mod425) -> {mod425.getFarmerRegime2().setDanaReduction(src.getAmount());return true;}),
////		CAG2_V6  (Mod303Key.CT_SA2A,(src,mod425) -> {mod425.getFarmerRegime2().setInputQuotas(src.getAmount());return true;}),
////		CAG2_V7  (Mod303Key.CT_SA28,(src,mod425) -> {mod425.getFarmerRegime2().setQuota(src.getAmount());return true;}),
////		                                    
////		CAG3     (Mod303Key.CT_SA31,(src,mod425) -> {mod425.getFarmerRegime3().setCodigo(AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-")));return true;}),
////		CAG3_V1  (Mod303Key.CT_SA32,(src,mod425) -> {mod425.getFarmerRegime3().setIncomes(src.getAmount());return true;}),
////		CAG3_V2  (Mod303Key.CT_SA33,(src,mod425) -> {mod425.getFarmerRegime3().setQuotaIndex(AonMathUtils.round(src.getAmount() / 10000 ,5));return true;}),
////		CAG3_V3  (Mod303Key.CT_SA34,(src,mod425) -> {mod425.getFarmerRegime3().setAccrualQuota(src.getAmount());return true;}),
////		CAG3_V4  (Mod303Key.CT_SA3R,(src,mod425) -> {mod425.getFarmerRegime3().setDanaReduction(src.getAmount());return true;}),
////		CAG3_V6  (Mod303Key.CT_SA3A,(src,mod425) -> {mod425.getFarmerRegime3().setInputQuotas(src.getAmount());return true;}),
////		CAG3_V7  (Mod303Key.CT_SA38,(src,mod425) -> {mod425.getFarmerRegime3().setQuota(src.getAmount());return true;}),
////		                                    
////		CAG4     (Mod303Key.CT_SA41,(src,mod425) -> {mod425.getFarmerRegime4().setCodigo(AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-")));return true;}),
////		CAG4_V1  (Mod303Key.CT_SA42,(src,mod425) -> {mod425.getFarmerRegime4().setIncomes(src.getAmount());return true;}),
////		CAG4_V2  (Mod303Key.CT_SA43,(src,mod425) -> {mod425.getFarmerRegime4().setQuotaIndex(AonMathUtils.round(src.getAmount() / 10000 ,5));return true;}),
////		CAG4_V3  (Mod303Key.CT_SA44,(src,mod425) -> {mod425.getFarmerRegime4().setAccrualQuota(src.getAmount());return true;}),
////		CAG4_V4  (Mod303Key.CT_SA4R,(src,mod425) -> {mod425.getFarmerRegime4().setDanaReduction(src.getAmount());return true;}),
////		CAG4_V6  (Mod303Key.CT_SA4A,(src,mod425) -> {mod425.getFarmerRegime4().setInputQuotas(src.getAmount());return true;}),
////		CAG4_V7  (Mod303Key.CT_SA48,(src,mod425) -> {mod425.getFarmerRegime4().setQuota(src.getAmount());return true;}),
//		
//		CAC1     (Mod303Key.CT_S101,(src,mod425) -> {mod425.getSimpRegime1().setEpigrafe(AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-")));return true;}),
//		CAC1_M1U (Mod303Key.CT_S11I, (src,mod425) -> {mod425.getSimpRegime1().setUnit1(src.getAmount());return true;}),
//		CAC1_M1I (Mod303Key.CT_S11R, (src,mod425) -> {mod425.getSimpRegime1().setAmount1(src.getAmount());return true;}),
//		CAC1_M2U (Mod303Key.CT_S12I, (src,mod425) -> {mod425.getSimpRegime1().setUnit2(src.getAmount());return true;}),
//		CAC1_M2I (Mod303Key.CT_S12R, (src,mod425) -> {mod425.getSimpRegime1().setAmount2(src.getAmount());return true;}),
//		CAC1_M3U (Mod303Key.CT_S13I, (src,mod425) -> {mod425.getSimpRegime1().setUnit3(src.getAmount());return true;}),
//		CAC1_M3I (Mod303Key.CT_S13R, (src,mod425) -> {mod425.getSimpRegime1().setAmount3(src.getAmount());return true;}),
//		CAC1_M4U (Mod303Key.CT_S14I, (src,mod425) -> {mod425.getSimpRegime1().setUnit4(src.getAmount());return true;}),
//		CAC1_M4I (Mod303Key.CT_S14R, (src,mod425) -> {mod425.getSimpRegime1().setAmount4(src.getAmount());return true;}),
//		CAC1_M5U (Mod303Key.CT_S15I, (src,mod425) -> {mod425.getSimpRegime1().setUnit5(src.getAmount());return true;}),
//		CAC1_M5I (Mod303Key.CT_S15R, (src,mod425) -> {mod425.getSimpRegime1().setAmount5(src.getAmount());return true;}),
//		CAC1_M6U (Mod303Key.CT_S16I, (src,mod425) -> {mod425.getSimpRegime1().setUnit6(src.getAmount());return true;}),
//		CAC1_M6I (Mod303Key.CT_S16R, (src,mod425) -> {mod425.getSimpRegime1().setAmount6(src.getAmount());return true;}),
//		CAC1_M7U (Mod303Key.CT_S17I, (src,mod425) -> {mod425.getSimpRegime1().setUnit7(src.getAmount());return true;}),
//		CAC1_M7I (Mod303Key.CT_S17R, (src,mod425) -> {mod425.getSimpRegime1().setAmount7(src.getAmount());return true;}),
//		CAC1_C   (Mod303Key.CT_S117, (src,mod425) -> {
//			mod425.getSimpRegime1().setBoxC(src.getAmount());
//			mod425.setBox74( AonMathUtils.round(mod425.getBox74() + src.getAmount()));
//			return true;}),
//		
//		CAC1_C1  (Mod303Key.CT_S1R1, (src,mod425) -> {mod425.getSimpRegime1().setBoxC1(src.getAmount());return true;}),
//		CAC1_C2  (Mod303Key.CT_S1R2, (src,mod425) -> {mod425.getSimpRegime1().setBoxC2(src.getAmount());return true;}),
//		CAC1_D   (Mod303Key.CT_S118, null),
//		CAC1_Z   (Mod303Key.CT_S119, null),
////		CAC1_ZA  (Mod303Key.CAC1_ZA , null),
////		CAC1_ZD  (Mod303Key.CAC1_ZD , null),
//		CAC1_E   (Mod303Key.CT_S120 , null),
//		CAC1_F   (Mod303Key.CT_S121 , null),
////		CAC1_G0  (Mod303Key.CAC1_G0 , (src,mod390) -> {mod390.getSimpRegime1().setBoxD(
////				AonMathUtils.round(mod390.getSimpRegime1().getBoxD() + src.getAmount()));return true;}),
//		CAC1_G   (Mod303Key.CT_S122 , (src,mod425) -> {mod425.getSimpRegime1().setBoxD(
//				AonMathUtils.round(mod425.getSimpRegime1().getBoxD() + src.getAmount()));return true;}),
//		CAC1_H   (Mod303Key.CT_S123 , (src,mod425) -> {mod425.getSimpRegime1().setBoxE(src.getAmount());return true;}),
////		CAC1_HA  (Mod303Key.CAC1_HA , null),
////		CAC1_HD  (Mod303Key.CAC1_HD , null),
////		CAC1_HT  (Mod303Key.CAC1_HT , null),
//		CAC1_I   (Mod303Key.CT_S124 , (src,mod425) -> {mod425.getSimpRegime1().setBoxF(src.getAmount());return true;}),
//		CAC1_J   (Mod303Key.CT_S125 , (src,mod425) -> {mod425.getSimpRegime1().setBoxG(src.getAmount());return true;}),
//		CAC1_K   (Mod303Key.CT_S126 , null),  
//		CAC1_L   (Mod303Key.CT_S127 , (src,mod425) -> {mod425.getSimpRegime1().setBoxI(src.getAmount());return true;}),
//		CAC1_M   (Mod303Key.CT_S128 , (src,mod425) -> {mod425.getSimpRegime1().setBoxJ(src.getAmount());return true;}),
//		
//		
//		CAC2     (Mod303Key.CT_S201    , (src,mod425) -> {
//			String code = AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-"));					
//			mod425.getSimpRegime2().setEpigrafe(code);
//			return true;}),
//		CAC2_M1U (Mod303Key.CT_S21I, (src,mod425) -> {mod425.getSimpRegime2().setUnit1(src.getAmount());return true;}),
//		CAC2_M1I (Mod303Key.CT_S21R, (src,mod425) -> {mod425.getSimpRegime2().setAmount1(src.getAmount());return true;}),
//		CAC2_M2U (Mod303Key.CT_S22I, (src,mod425) -> {mod425.getSimpRegime2().setUnit2(src.getAmount());return true;}),
//		CAC2_M2I (Mod303Key.CT_S22R, (src,mod425) -> {mod425.getSimpRegime2().setAmount2(src.getAmount());return true;}),
//		CAC2_M3U (Mod303Key.CT_S23I, (src,mod425) -> {mod425.getSimpRegime2().setUnit3(src.getAmount());return true;}),
//		CAC2_M3I (Mod303Key.CT_S23R, (src,mod425) -> {mod425.getSimpRegime2().setAmount3(src.getAmount());return true;}),
//		CAC2_M4U (Mod303Key.CT_S24I, (src,mod425) -> {mod425.getSimpRegime2().setUnit4(src.getAmount());return true;}),
//		CAC2_M4I (Mod303Key.CT_S24R, (src,mod425) -> {mod425.getSimpRegime2().setAmount4(src.getAmount());return true;}),
//		CAC2_M5U (Mod303Key.CT_S25I, (src,mod425) -> {mod425.getSimpRegime2().setUnit5(src.getAmount());return true;}),
//		CAC2_M5I (Mod303Key.CT_S25R, (src,mod425) -> {mod425.getSimpRegime2().setAmount5(src.getAmount());return true;}),
//		CAC2_M6U (Mod303Key.CT_S26I, (src,mod425) -> {mod425.getSimpRegime2().setUnit6(src.getAmount());return true;}),
//		CAC2_M6I (Mod303Key.CT_S26R, (src,mod425) -> {mod425.getSimpRegime2().setAmount6(src.getAmount());return true;}),
//		CAC2_M7U (Mod303Key.CT_S27I, (src,mod425) -> {mod425.getSimpRegime2().setUnit7(src.getAmount());return true;}),
//		CAC2_M7I (Mod303Key.CT_S27R, (src,mod425) -> {mod425.getSimpRegime2().setAmount7(src.getAmount());return true;}),
//		CAC2_C   (Mod303Key.CT_S217 , (src,mod425) ->	{
//				mod425.getSimpRegime2().setBoxC(src.getAmount());
//				mod425.setBox74( AonMathUtils.round(mod425.getBox74() + src.getAmount()));
//				return true;
//														}),
//		CAC2_C1  (Mod303Key.CT_S2R1 , (src,mod425) -> {mod425.getSimpRegime2().setBoxC1(src.getAmount());return true;}),
//		CAC2_C2  (Mod303Key.CT_S2R2 , (src,mod425) -> {mod425.getSimpRegime2().setBoxC2(src.getAmount());return true;}),
//		CAC2_D   (Mod303Key.CT_S218, null),
//		CAC2_Z   (Mod303Key.CT_S219 , null),
////		CAC2_ZA  (Mod303Key.CAC2_ZA , null),
////		CAC2_ZD  (Mod303Key.CAC2_ZD , null),
//		CAC2_E   (Mod303Key.CT_S220 , null),
//		CAC2_F   (Mod303Key.CT_S221 , null),
//		
////		CAC2_G0  (Mod303Key.CAC2_G0 , (src,mod390) -> {mod390.getSimpRegime2().setBoxD(
////				AonMathUtils.round(mod390.getSimpRegime2().getBoxD() + src.getAmount()));return true;}),
//		CAC2_G   (Mod303Key.CT_S222 , (src,mod425) -> {mod425.getSimpRegime2().setBoxD(
//				AonMathUtils.round(mod425.getSimpRegime2().getBoxD() + src.getAmount()));return true;}),
//		CAC2_H   (Mod303Key.CT_S223 , (src,mod425) -> {mod425.getSimpRegime2().setBoxE(src.getAmount());return true;}),
////		CAC2_HA  (Mod303Key.CAC2_HA , null),
////		CAC2_HD  (Mod303Key.CAC2_HD , null),
////		CAC2_HT  (Mod303Key.CAC2_HT , null),
//		CAC2_I   (Mod303Key.CT_S224 , (src,mod425) -> {mod425.getSimpRegime2().setBoxF(src.getAmount());return true;}),
//		CAC2_J	 (Mod303Key.CT_S225 , (src,mod425) -> {mod425.getSimpRegime2().setBoxG(src.getAmount());return true;}),
//		CAC2_K   (Mod303Key.CT_S226 , null),  
//		CAC2_L   (Mod303Key.CT_S227 , (src,mod425) -> {mod425.getSimpRegime2().setBoxI(src.getAmount());return true;}),
//		CAC2_M   (Mod303Key.CT_S228 , (src,mod425) -> {mod425.getSimpRegime2().setBoxJ(src.getAmount());return true;}),
//		
////		C51      (Mod303Key.C51     , null),
////		C52      (Mod303Key.C52     , null),
////		C53      (Mod303Key.CT_S51  , null),
////		C54      (Mod303Key.C54     , null),
////		C55      (Mod303Key.C55     , null),
////		C56      (Mod303Key.C56     , null),
////		C57      (Mod303Key.C57     , null),
////		C58      (Mod303Key.C58     , null),
////		C71      (Mod303Key.C71     , null),
////		PBK      (Mod303Key.PBK     , null),
//		
//		;
//		
//		private Mod303Key key;
//		private ISimplifiedRegimeFiller filler;
//			
//		private SimplifiedRegimeFiller(Mod303Key key,ISimplifiedRegimeFiller filler) {
//			this.key = key;
//			this.filler = filler;
//		}
//		public Mod303Key getKey() {
//			return key;
//		}
//		public ISimplifiedRegimeFiller getFiller() {
//			return filler;
//		}
//		public static void fill(SimplifedRegimeContext src,Mod4252025 mod425) {
//			for (SimplifiedRegimeFiller filler : SimplifiedRegimeFiller.values()) {
//				if (filler.getKey() ==  src.getKey() && filler.getFiller() != null) {
//					filler.getFiller().fill(src, mod425);
//				}
//			}
//		}
//		
//	}	

//	private static Mod4252025 fillSimplifedRegimeData(AONContext ctx, Mod4252025 mod425) {
//		mod425.setSimpRegime1(new SimpliedRegimeActivity425());
//		mod425.setSimpRegime2(new SimpliedRegimeActivity425());
////		mod425.setFarmerRegime1(new FarmerRegimeActivity());
////		mod425.setFarmerRegime2(new FarmerRegimeActivity());
////		mod425.setFarmerRegime3(new FarmerRegimeActivity());
////		mod425.setFarmerRegime4(new FarmerRegimeActivity());
////		mod425.setFarmerRegime5(new FarmerRegimeActivity());
//		ctx.getDslContext().select(FS_MODEL_DETAIL.TYPE
//				, FS_MODEL_DETAIL.DESCRIPTION
//				, FS_MODEL_DETAIL.AMOUNT)
//			.from(FS_MODEL)
//			.join(FS_MODEL_DETAIL).on(FS_MODEL.ID.equal(FS_MODEL_DETAIL.FS_MODEL))
//			.where(FS_MODEL.DOMAIN.equal(mod425.getDomain()))
//			.and(FS_MODEL.YEAR.equal(mod425.getYear()))
//			.and(FS_MODEL.PERIOD.equal( (byte) Period.T4.ordinal()))
//			.and(FS_MODEL.MODEL.equal(FiscalModelType.M303.getValue()))
//		.fetch()
//		.stream()
//		.forEach( rec -> {
//			String description = rec.getValue( FS_MODEL_DETAIL.DESCRIPTION );
//			String type = rec.getValue( FS_MODEL_DETAIL.TYPE );
//			double amount = rec.getValue( FS_MODEL_DETAIL.AMOUNT );
//			Mod303Key key = Mod303Key.getKey(type);
//			SimplifedRegimeContext src = new SimplifedRegimeContext(key, description, amount);
//			SimplifiedRegimeFiller.fill(src,mod425);
//			}
//		);
//		return mod425;
//	}
	
//	private static void fillSimplifiedDeclarationResults(AONContext ctx, Mod4252025 mod425) {
//		mod425.setBox97( AonMathUtils.round(mod425.getBox97() * -1));
//		mod425.setBox98( AonMathUtils.round(mod425.getBox98() * -1));
//		if (mod425.getBox98() > 0) {
//			mod425.setBox97( 0 ); 	
//		}
//		Record1<BigDecimal> rec = ctx.getDslContext()
//			.select(DSL.sum(FS_MODEL_DETAIL.AMOUNT))
//			.from(FS_MODEL)
//			.join(FS_MODEL_DETAIL).on(FS_MODEL.ID.equal(FS_MODEL_DETAIL.FS_MODEL))
//			.where(FS_MODEL.DOMAIN.equal(mod425.getDomain()))
//			.and(FS_MODEL.YEAR.equal(mod425.getYear()))
//			.and(FS_MODEL_DETAIL.AMOUNT.greaterThan(0.0))
//			.and(FS_MODEL.MODEL.equal("303"))
//			.and(FS_MODEL_DETAIL.TYPE.equal("303-71"))
//			.fetchOne();
//		if (rec != null) {
//			BigDecimal quota = rec.getValue(DSL.sum(FS_MODEL_DETAIL.AMOUNT)); 
//			if (quota != null) {
//				mod425.setBox95( quota.doubleValue());
//			}
//		}
//	}

	public static Mod4252025 changeStatus(AONContext ctx, Mod4252025 mod425, FiscalStatus newStatus) {
		try {
			ctx.checkWrite();
			if (mod425.getId() != null) {
				mod425.setStatus(newStatus);
				ctx.getDslContext().update(FS_MODEL390)
					.set(FS_MODEL390.STATUS,AonEnumUtils.getByte(mod425.getStatus()))
					.where(FS_MODEL390.ID.equal(mod425.getId()))
					.execute();
			}
			return mod425;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Exception t) {
			throw new AonCoreException(t.getMessage());
		}
	}

	private static double getVatAccrualPaymentOutputBase(AONContext ctx, Mod390 mod425) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod425.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod425);
		return Mod390DAO.getVatAccrualPaymentOutputBase(ctx,fromDate,toDate);
	}

	private static double getVatAccrualPaymentOutputQuota(AONContext ctx, Mod390 mod425) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod425.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod425);
		return Mod390DAO.getVatAccrualPaymentOutputQuota(ctx,fromDate,toDate);
	}

	private static double getVatAccrualPaymentInputBase(AONContext ctx, Mod390 mod425) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod425.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod425);
		return Mod390DAO.getVatAccrualPaymentInputBase(ctx,fromDate,toDate);
	}

	private static double getVatAccrualPaymentInputQuota(AONContext ctx, Mod390 mod425) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod425.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod425);
		return Mod390DAO.getVatAccrualPaymentInputQuota(ctx,fromDate,toDate);
	}

	// CANARIAS NO TIENE PRESENTACION DIRECTA
//	public static Mod4252025 aeatPresentation(AONContext ctx, Mod4252025 mod, String aeatResponse) {
//		if (AonStringUtils.isNotBlank(aeatResponse)) {
//			DataResponseDAO.insertAEATResponse(ctx, mod, aeatResponse);
//			AEATResponse response = AEATJson.toJSON(aeatResponse.getBytes());
//			if (mod != null && mod.getId() != null) {
//				ctx.getDslContext().update(FS_MODEL390)
//					.set(FS_MODEL390.RECEIPT,response.getJustificante())
//					.set(FS_MODEL390.STATUS, FiscalStatus.SENT.value())
//					.where(FS_MODEL390.ID.equal(mod.getId()))
//					.execute();
//				return getById(ctx, mod.getId());
//			}
//		}
//		return mod;
//	}

	// -----------------------------------------------------------------------
	// --------------------------------------------------------------- FILTROS
	// -----------------------------------------------------------------------
	
	private static boolean hasPercent0(VatContext vat) {
		return vat.getPercentage() == PERCENT0;
	}
	private static boolean hasPercent3(VatContext vat) {
		return vat.getPercentage() == PERCENT3;
	}
	private static boolean hasPercent5(VatContext vat) {
		return vat.getPercentage() == PERCENT5;
	}
	private static boolean hasPercent7(VatContext vat) {
		return vat.getPercentage() == PERCENT7;
	}
	private static boolean hasPercent95(VatContext vat) {
		return vat.getPercentage() == PERCENT95;
	}
	private static boolean hasPercent15(VatContext vat) {
		return vat.getPercentage() == PERCENT15;
	}
	private static boolean hasPercent20(VatContext vat) {
		return vat.getPercentage() == PERCENT20;
	}

	private static boolean isCommonNationalSales(VatContext vat, Mod4252025 mod) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& vat.isNational()
			&& vat.isSales() 
			&& !vat.isRectification();
	}
	
	private static boolean isOperacionesISPFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime()
			&& (vat.isOtherISPPurchase() 
			 || vat.isOtherISPExpenses() 
			 || vat.isExtracommunityExpenses()
			 || vat.isCanCeuMelExpenses() 
			 || (vat.isExtracommunityPurchase() && vat.isService())
			 || (vat.isCanCeuMelPurchase() && vat.isService()));
	}
	
	private static boolean operacionesInterioresCorrientesFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& !vat.isInvestment()
			&& !vat.isRectification() 
			&& !vat.isFarmerRegime() 
			&& AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || isOperacionesISPFilter(vat));
	}
	
	private static boolean operacionesInterioresInversionFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& vat.isInvestment()
			&& !vat.isRectification() 
			&& !vat.isFarmerRegime() 
			&& AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || isOperacionesISPFilter(vat));
	}
	
	private static boolean importacionesCorrientesFilter(VatContext vat) {
		return commonImportacionesFilter(vat) && !vat.isInvestment();
	}
	private static boolean importacionesInversionFilter(VatContext vat) {
		return commonImportacionesFilter(vat) && vat.isInvestment();
	}
	
	private static boolean commonImportacionesFilter(VatContext vat) {
		boolean basicFilter =  vat.isVatGeneralRegime(VATRegime.GENERAL) 
				&& !vat.isVatSurchargeRegime() 
				&& !vat.isRectification() 
				&& !vat.isService();
		if (basicFilter && (vat.isExtracommunityPurchase() || vat.isCanCeuMelPurchase())) {
			if (vat.getTaxDate().before( Mod303Declaration.IVA_2021_CHANGE_DATE )) {
				basicFilter = true;
			} else {
				basicFilter = vat.hasDuaLinked() || vat.isVatImportation();
			}
			return basicFilter; 
		}
		return false;
	}
	
	private static boolean compensacionesRegAgrarioFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& vat.isFarmerRegime()
			&& !vat.isRectification() 
			&& (vat.isNationalPurchase() || vat.isNationalExpenses());
	}
	
	private static boolean modificacionBasesYCuotasFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
				&& !vat.isVatSurchargeRegime() 
				&& vat.isRectification()
				&& (vat.isNationalSales() 
					|| (vat.isIntracommunityPurchase() || vat.isIntracommunityExpenses())
					|| (vat.isOtherISPPurchase() || vat.isOtherISPExpenses() || vat.isExtracommunityExpenses()
						|| vat.isCanCeuMelExpenses() || (vat.isExtracommunityPurchase() && vat.isService())
						|| (vat.isCanCeuMelPurchase() && vat.isService())));
	}
	
	// Comprobar si la factura esta unida a un modelo 303 con porcentaje de prorrata
	private static KeyedVatContext checkProrrated(AONContext ctx, Mod390 mod425, KeyedVatContext kvc) {
		
		Double prorratePercent = 
		ctx.getDslContext().select(FS_MODEL_DETAIL.AMOUNT)
			.from(ALCATRAZ)
			.leftJoin(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
			.leftJoin(FS_MODEL_DETAIL).on(FS_MODEL_DETAIL.FS_MODEL.equal(FS_MODEL.ID))
			.where(ALCATRAZ.INVOICE.equal(kvc.getVatContext().getInvoice()))
			.and(FS_MODEL.MODEL.equal("IVA"))
			.and(FS_MODEL_DETAIL.TYPE.equal("303-CM003"))  // Porcentaje de prorrata
			.and(FS_MODEL.YEAR.equal(mod425.getYear()))
			.fetchAny(FS_MODEL_DETAIL.AMOUNT);
		
		if (prorratePercent != null && prorratePercent > 0) {
			kvc.getVatContext().setProrrated(true);
			kvc.getVatContext().setProrratePercent(prorratePercent);	
			kvc.getVatContext().setProrrateQuota(AonMathUtils.round(kvc.getVatContext().getDeductibleQuota() * prorratePercent / 100));
		}
		return kvc;
		
	}

	// Inicializar los datos del domicilio del declarante
	private static void initializeIdentificationData(AONContext ctx, Mod4252025 fm) {
		Company company = CompanyDAO.getCompany(ctx, fm.getDomain());
		Enterprise enterprise = CompanyDAO.getEnterprise(ctx, company.getId() );
		if (enterprise != null) {
			fm.setStreetInitial(enterprise.getStreetType() == null ? null : enterprise.getStreetType().getAeatCode());
			fm.setStreetName(enterprise.getAddress());
			fm.setStreetNumber(enterprise.getNumber());
			fm.setTown(enterprise.getCity());
			fm.setTownCode(enterprise.getTown());
			fm.setProvinceCode(Integer.toString(Province.safeValue( enterprise.getProvince())));
			fm.setZip(AonStringUtils.defaultIfBlank(enterprise.getZip(), "00000"));
			fm.setContactPhone(enterprise.getPhone());
		}
		
	}

}
