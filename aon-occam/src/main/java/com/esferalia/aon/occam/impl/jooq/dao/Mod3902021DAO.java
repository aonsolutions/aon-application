package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModel390.FS_MODEL390;
import static com.esferalia.aon.jooq.tables.FsModelDetail.FS_MODEL_DETAIL;
import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.FsModel390Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.fiscal.Address;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902021;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902021.FarmerRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902021.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902021.SimpliedRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902021DetailKey;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303DAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2021.AEATIVA2021;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2021.AEATIVA2021toMod390;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2021.Mod390toAEATIVA2021;
import com.esferalia.aon.occam.server.fiscal.AEATJson;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod3902021DAO {

	private static final byte ZERO_BYTE = 0;
	private static final byte ONE_BYTE = 1;
	
	public static final double PERCENT1 = 4.0;
	public static final double PERCENT2 = 10.0;
	public static final double PERCENT3 = 21.0;
	public static final double SURCHARGE_PERCENT1 = 0.5;
	public static final double SURCHARGE_PERCENT2 = 1.4;
	public static final double SURCHARGE_PERCENT3 = 5.2;
	public static final double SURCHARGE_PERCENT4 = 1.75;
	
	// 1 de Julio del 2012		
	private static final Date IVA_2021_CHANGE_DATE =  Date.from(LocalDateTime.of(2021, 7, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());	
	
	@FunctionalInterface
	public static interface IMod390DetailKey {
		boolean accept(Mod3902021 mod,VatContext vc);
	}

	public enum DetailKey implements Serializable {
		
		  K00_04 (Mod3902021DetailKey.C0002, (mod, vc) -> isCommonNationalSales(vc, mod) && hasPercent1(vc))
		 ,K00_10 (Mod3902021DetailKey.C0004, (mod, vc) -> isCommonNationalSales(vc, mod) && hasPercent2(vc))
		 ,K00_21 (Mod3902021DetailKey.C0006, (mod, vc) -> isCommonNationalSales(vc, mod) && hasPercent3(vc))
		 ,K01_04 (Mod3902021DetailKey.C0501, null)
		 ,K01_10 (Mod3902021DetailKey.C0503, null)
		 ,K01_21 (Mod3902021DetailKey.C0505, null)
		 ,K02_04 (Mod3902021DetailKey.C0008, null)
		 ,K02_10 (Mod3902021DetailKey.C0010, null)
		 ,K02_21 (Mod3902021DetailKey.C0012, null)
		 ,K03_21 (Mod3902021DetailKey.C0014, null)
		 ,K04_04 (Mod3902021DetailKey.C0022, (mod, vc) -> isIntracommunityPurchase(vc, mod) && hasPercent1(vc))
		 ,K04_10 (Mod3902021DetailKey.C0024, (mod, vc) -> isIntracommunityPurchase(vc, mod) && hasPercent2(vc))
		 ,K04_21 (Mod3902021DetailKey.C0026, (mod, vc) -> isIntracommunityPurchase(vc, mod) && hasPercent3(vc))
	
		 ,K05_04 (Mod3902021DetailKey.C0546, (mod, vc) -> isIntracommunityExpenses(vc, mod) && hasPercent1(vc))
		 ,K05_10 (Mod3902021DetailKey.C0548, (mod, vc) -> isIntracommunityExpenses(vc, mod) && hasPercent2(vc))
		 ,K05_21 (Mod3902021DetailKey.C0552, (mod, vc) -> isIntracommunityExpenses(vc, mod) && hasPercent3(vc))

		 ,K06	 (Mod3902021DetailKey.C0028, (mod, vc) -> isOperacionesISPFilter(vc, mod))
		 
		 ,K07	 (Mod3902021DetailKey.C0030, (mod, vc) -> isCommonNationalSalesRECT(vc, mod))
		 ,K08	 (Mod3902021DetailKey.C0032, null)
		 ,K09	 (Mod3902021DetailKey.C0034, null)
		 ,K10_05 (Mod3902021DetailKey.C0036, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isSurcharge() && hasSurchargePercent1(vc))
		 ,K10_14 (Mod3902021DetailKey.C0600, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isSurcharge() && hasSurchargePercent2(vc))
		 ,K10_52 (Mod3902021DetailKey.C0602, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isSurcharge() && hasSurchargePercent3(vc))
		 ,K10_175(Mod3902021DetailKey.C0042, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isSurcharge() && hasSurchargePercent4(vc))
		 ,K11	 (Mod3902021DetailKey.C0044, (mod, vc) -> isCommonNationalSalesRECT(vc, mod) && vc.isSurcharge()) 
		 ,K12	 (Mod3902021DetailKey.C0046, null)
		 ,K13	 (Mod3902021DetailKey.C0047, null)
		 
		 ,K14_04 (Mod3902021DetailKey.C0191, ((mod, vc) -> operacionesInterioresCorrientesFilter(vc) && vc.getPercentage() == 4)) 
		 ,K14_10 (Mod3902021DetailKey.C0604, ((mod, vc) -> operacionesInterioresCorrientesFilter(vc) && vc.getPercentage() == 10)) 
		 ,K14_21 (Mod3902021DetailKey.C0606, ((mod, vc) -> operacionesInterioresCorrientesFilter(vc) && vc.getPercentage() == 21)) 
		 ,K15	 (Mod3902021DetailKey.C0049, null)
		 
		 ,K16_04 (Mod3902021DetailKey.C0507, null)
		 ,K16_10 (Mod3902021DetailKey.C0608, null)
		 ,K16_21 (Mod3902021DetailKey.C0610, null)
		 ,K17	 (Mod3902021DetailKey.C0513, null)
		 
		 ,K18_04 (Mod3902021DetailKey.C0197, ((mod, vc) -> operacionesInterioresInversionFilter(vc) && vc.getPercentage() ==  4))
		 ,K18_10 (Mod3902021DetailKey.C0612, ((mod, vc) -> operacionesInterioresInversionFilter(vc) && vc.getPercentage() == 10)) 
		 ,K18_21 (Mod3902021DetailKey.C0614, ((mod, vc) -> operacionesInterioresInversionFilter(vc) && vc.getPercentage() == 21))
		 ,K19	 (Mod3902021DetailKey.C0051, null)
		 
		 ,K20_04 (Mod3902021DetailKey.C0515, null)
		 ,K20_10 (Mod3902021DetailKey.C0616, null)
		 ,K20_21 (Mod3902021DetailKey.C0618, null)
		 ,K21	 (Mod3902021DetailKey.C0521, null)
		 
		 ,K22_04 (Mod3902021DetailKey.C0203, ((mod, vc) -> importacionesCorrientesFilter(vc) && vc.getPercentage() == 4))
		 ,K22_10 (Mod3902021DetailKey.C0620, ((mod, vc) -> importacionesCorrientesFilter(vc) && vc.getPercentage() == 10))
		 ,K22_21 (Mod3902021DetailKey.C0622, ((mod, vc) -> importacionesCorrientesFilter(vc) && vc.getPercentage() == 21))
		 ,K23	 (Mod3902021DetailKey.C0053, null)
		 
		 ,K24_04 (Mod3902021DetailKey.C0209, ((mod, vc) -> importacionesInversionFilter(vc) && vc.getPercentage() == 4))
		 ,K24_10 (Mod3902021DetailKey.C0624, ((mod, vc) -> importacionesInversionFilter(vc) && vc.getPercentage() == 10))
		 ,K24_21 (Mod3902021DetailKey.C0626, ((mod, vc) -> importacionesInversionFilter(vc) && vc.getPercentage() == 21))
		 ,K25	 (Mod3902021DetailKey.C0055, null)
		 
		 ,K26_04 (Mod3902021DetailKey.C0215, ((mod, vc) -> adqIntracomunitariasCorrientesFilter(vc) && vc.getPercentage() == 4))
		 ,K26_10 (Mod3902021DetailKey.C0628, ((mod, vc) -> adqIntracomunitariasCorrientesFilter(vc) && vc.getPercentage() == 10))
		 ,K26_21 (Mod3902021DetailKey.C0630, ((mod, vc) -> adqIntracomunitariasCorrientesFilter(vc) && vc.getPercentage() == 21))
		 ,K27	 (Mod3902021DetailKey.C0057, null)
		 
		 ,K28_04 (Mod3902021DetailKey.C0221, ((mod, vc) -> adqIntracomunitariasInversionFilter(vc) && vc.getPercentage() == 4))
		 ,K28_10 (Mod3902021DetailKey.C0632, ((mod, vc) -> adqIntracomunitariasInversionFilter(vc) && vc.getPercentage() == 10)) 
		 ,K28_21 (Mod3902021DetailKey.C0634, ((mod, vc) -> adqIntracomunitariasInversionFilter(vc) && vc.getPercentage() == 21)) 
		 ,K29	 (Mod3902021DetailKey.C0059, null)
		 
		 ,K30_04 (Mod3902021DetailKey.C0588, ((mod, vc) -> adqIntracomunitariasServicios(vc) && vc.getPercentage() == 4))
		 ,K30_10 (Mod3902021DetailKey.C0636, ((mod, vc) -> adqIntracomunitariasServicios(vc) && vc.getPercentage() == 10))
		 ,K30_21 (Mod3902021DetailKey.C0638, ((mod, vc) -> adqIntracomunitariasServicios(vc) && vc.getPercentage() == 21))
		 ,K31	 (Mod3902021DetailKey.C0598, null)
		 
		 ,K32	 (Mod3902021DetailKey.C0061, ((mod, vc) -> ((vc.isPurchase() || vc.isExpenses()) && vc.isFarmerRegime())))
		 
		 // **************
		 ,K33	 (Mod3902021DetailKey.C0062, ((mod, vc) -> vc.isRectification() && (vc.isPurchase() || vc.isExpenses() )))
		 // **************
		 
		 ,K34	 (Mod3902021DetailKey.C0063, null)
		 ,K35	 (Mod3902021DetailKey.C0522, null)
		 ,K36	 (Mod3902021DetailKey.C0064, null)
		 ,K37	 (Mod3902021DetailKey.C0065, null)
		 
		 ,B099	 (Mod3902021DetailKey.C0099, ((mod, vc) -> (vc.isNationalSales())))
		 ,B653	 (Mod3902021DetailKey.C0653, ((mod, vc) -> (vc.isSales() && vc.isVatAccrualRegime() )))
		 ,B103	 (Mod3902021DetailKey.C0103, ((mod, vc) -> (vc.isIntracommunitySales() && !vc.isWithoutRightDeductionType())))
		 ,B104	 (Mod3902021DetailKey.C0104, ((mod, vc) -> (vc.isSales() && !vc.isWithoutRightDeductionType() && (vc.isExtracommunity() || vc.isCanCeuMel()) )))
		 ,B105	 (Mod3902021DetailKey.C0105, ((mod, vc) -> (vc.isSales() && !vc.isNational() && vc.isWithoutRightDeductionType())))
		 ,B110	 (Mod3902021DetailKey.C0110, ((mod, vc) -> (vc.isSales() && !vc.isWithoutRightDeductionType() && vc.isOtherISP())))
		 ,B125	 (Mod3902021DetailKey.C0125, null)
		 ,B126	 (Mod3902021DetailKey.C0126, null)
		 ,B127	 (Mod3902021DetailKey.C0127, null)
		 ,B128	 (Mod3902021DetailKey.C0128, null)
		 ,B100	 (Mod3902021DetailKey.C0100, null)
		 ,B101	 (Mod3902021DetailKey.C0101, null)
		 ,B102	 (Mod3902021DetailKey.C0102, ((mod, vc) -> (vc.isNationalSales() && vc.isSurcharge())))
		 ,B227	 (Mod3902021DetailKey.C0227, null)
		 ,B228	 (Mod3902021DetailKey.C0228, null)
		 ,B106	 (Mod3902021DetailKey.C0106, null)
		 ,B107	 (Mod3902021DetailKey.C0107, ((mod, vc) -> (vc.isNationalSales() && vc.isInvestment())))
		 ,B108	 (Mod3902021DetailKey.C0108, null)
		 ;
		 
		private Mod3902021DetailKey key;
		private IMod390DetailKey accept;
			
		private DetailKey(Mod3902021DetailKey key, IMod390DetailKey accept) {
			this.key = key;
			this.accept = accept;
		}

		public Mod3902021DetailKey getKey() {
			return key;
		}
		public boolean accept(Mod3902021 mod, VatContext vc) {
			return accept!=null && accept.accept(mod,vc);
		}
		
		public static Mod3902021DetailKey[] getKeys(Mod3902021 mod,VatContext vc) {
			List<Mod3902021DetailKey> list = new LinkedList<>();
			for (DetailKey key : DetailKey.values()) {
				if (key.accept(mod,vc)) {
					list.add(key.getKey()); 
				}
			}
			return list.isEmpty()?null:list.toArray(new Mod3902021DetailKey[list.size()]);
		}
	}

	public static Mod3902021 create(AONContext ctx, Mod390 model) {
		Mod3902021 mod390 =  new Mod3902021();
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
		boolean found = false;
		if (year == 2018 || year == 2019 || year == 2020 || year == 2021) {
			LinkedList<Mod390> mod390s = Mod390DAO.getByDomain(ctx, ctx.getDomainId());
			for (Mod390 m390 : mod390s) {
				if ( m390.getYear() > 2020) {
					Mod3902021 mod3902021 = Mod3902021DAO.getById(ctx, m390.getId());
					mod390.setMainActivity(mod3902021.getMainActivity());
					mod390.setActivity1(mod3902021.getActivity1());
					mod390.setActivity2(mod3902021.getActivity2());
					mod390.setActivity3(mod3902021.getActivity3());
					mod390.setActivity4(mod3902021.getActivity4());
					mod390.setActivity5(mod3902021.getActivity5());
					mod390.setAddress(mod3902021.getAddress());
					mod390.setLegalRepr1(mod3902021.getLegalRepr1());
					mod390.setLegalRepr2(mod3902021.getLegalRepr2());
					mod390.setLegalRepr3(mod3902021.getLegalRepr3());
					found = true;
					break;
				}
				if ( m390.getYear() == 2018 || m390.getYear() == 2019 || m390.getYear() == 2020) {
					Mod3902018 mod3902018 = Mod3902018DAO.getById(ctx, m390.getId());
					mod390.setMainActivity(mod3902018.getMainActivity());
					mod390.setActivity1(mod3902018.getActivity1());
					mod390.setActivity2(mod3902018.getActivity2());
					mod390.setActivity3(mod3902018.getActivity3());
					mod390.setActivity4(mod3902018.getActivity4());
					mod390.setActivity5(mod3902018.getActivity5());
					mod390.setAddress(mod3902018.getAddress());
					mod390.setLegalRepr1(mod3902018.getLegalRepr1());
					mod390.setLegalRepr2(mod3902018.getLegalRepr2());
					mod390.setLegalRepr3(mod3902018.getLegalRepr3());
					found = true;
					break;
				}
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
					found = true;
					break;
				}
			}
		}
		if (!found) {
			fillFromnConfiguration(ctx, mod390);	
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

	private static void fillFromnConfiguration(AONContext ctx, Mod3902021 mod390) {
		Company company = CompanyDAO.getCompany(ctx, mod390.getDomain());
		if (company != null) {
			LinkedList<Record2<String,String>> list = ctx.getDslContext().
				select(RDIR_STAFF.DOCUMENT,RDIR_STAFF.NAME)
					.from(RDIR_STAFF)
					.where(RDIR_STAFF.DOMAIN.eq(mod390.getDomain()))
					.and(RDIR_STAFF.REGISTRY.eq(company.getId()))
					.and(RDIR_STAFF.REPRESENTATIVE.eq( (byte) 1))
					.fetch()
					.stream()
					.collect(Collectors.toCollection(LinkedList::new));
			for (Record2<String,String> rec : list ) {
						String document = rec.getValue(RDIR_STAFF.DOCUMENT);
						String name = rec.getValue(RDIR_STAFF.NAME);
						
						if (mod390.isLegalEntity()) {
							LegalRepresentative legalRepr = new LegalRepresentative();
							legalRepr.setDocument(document);
							legalRepr.setName(name);
							mod390.setLegalRepr1(legalRepr);
							if (mod390.getLegalRepr1() == null) mod390.setLegalRepr1(legalRepr); 
							else if (mod390.getLegalRepr2() == null) mod390.setLegalRepr2(legalRepr);
							else if (mod390.getLegalRepr3() == null) mod390.setLegalRepr3(legalRepr);
						} else {
							Address address = new Address();
							address.setRdocument(document);
							address.setRname(name);
							mod390.setAddress(address);
							break;
						}
			}
		}
		
		
		
	}

	public static Mod3902021 getMod3902021(AONContext ctx, Mod390 m390) {
		if (m390.getId() == null) {
			return create(ctx, m390);
		}
		return getById(ctx, m390.getId());
	}

	public static Mod3902021 getById(AONContext ctx, int id) {
		ctx.checkRead();
		Mod3902021 mod390 = new Mod3902021();
		ctx.getDslContext()
				.selectFrom(FS_MODEL390)
				.where(FS_MODEL390.ID.equal(id))
				.fetch()
				.stream()
				.forEach(rec -> populate(rec, mod390));
		if (mod390.getId() != null) {
			return mod390;
		}
		return null;
	}

	private static void populate(FsModel390Record rec, Mod3902021 mod390)  {
		mod390
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
		
		String model = rec.getValue(FS_MODEL390.MODEL);
		if (AonStringUtils.contains(model, "<AEATIVA2021>"))  {
			StringReader reader = new StringReader(rec.getValue(FS_MODEL390.MODEL));
			try {
				if (mod390.getYear() == 2021) {
					JAXBContext context = JAXBContext.newInstance(AEATIVA2021.class);
					Unmarshaller um = context.createUnmarshaller();
					AEATIVA2021 iva = (AEATIVA2021) um.unmarshal(reader);
					AEATIVA2021toMod390.populate(mod390, iva);
					mod390.setXmlFormat("AEAT_2021");
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
		return ctx.getDslContext()
			.selectFrom(FS_MODEL390)
			.where(FS_MODEL390.ID.equal(id))
			.fetchOne(FS_MODEL390.MODEL);
	}


	public static Mod3902021 save(AONContext ctx, Mod3902021 mod390)  {
		if (mod390.getId() == null) {
			return insert(ctx, mod390);
		} else {
			return update(ctx, mod390);
		}
	}
	
	private static String getXMLModel( Mod3902021 mod390 ) {
		try {
			if (mod390.getYear() == 2021) {
				AEATIVA2021 iva = Mod390toAEATIVA2021.getAEATIVA2021(mod390);
				StringWriter writer = new StringWriter();
				JAXBContext context = JAXBContext.newInstance(AEATIVA2021.class);
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
	

	private static Mod3902021 insert(AONContext ctx, Mod3902021 mod390) {
		validate(ctx, mod390);
		FsModel390Record rec = ctx.getDslContext()
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
		mod390.setId(rec.getId());
		return mod390;
	}
	
	private static Mod3902021 update(AONContext ctx, Mod3902021 mod390) {
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

	private static void validate(AONContext ctx, Mod3902021 mod390) {
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

	public static void delete(AONContext ctx, Mod3902021 mod390) {
		ctx.checkWrite();
		ctx.getDslContext().delete(FS_MODEL390)
				.where(FS_MODEL390.ID.equal(mod390.getId())).execute();
	}
	
	private static LinkedList<Mod390Detail> getDetails(AONContext ctx, Mod3902021 mod390 ) {
		EnumMap<Mod3902021DetailKey, Mod390Detail> map = new EnumMap<>(Mod3902021DetailKey.class); 
		Mod390Detail det = null;
		for (Mod3902021DetailKey key : Mod3902021DetailKey.values() ) {
			if (key.accept(mod390.getYear())) {
				det = new Mod390Detail();
				det.setKey(key);
				det.setPercent(key.getPercent());
				map.put(key, det);
			}
		}
		
		final MutableDouble mutProrrata = new MutableDouble();
		/*
		 * 	BUsCAR prorrata??
		 * 
		 */
		double prorrata = AonMathUtils.round( mutProrrata.doubleValue() / 100);
		boolean mustApplyProrrata = (prorrata != AonMathUtils.round(0.00));		
		
		Date firstDay = AonDateUtils.getYearFirstDay(mod390.getYear());
		Date lastDay = AonDateUtils.getYearLastDay(mod390.getYear());
		VATDAO.getVatBreakdown(ctx, firstDay, lastDay, mod390)
		.forEach(vc -> {
			Mod3902021DetailKey[] keys = DetailKey.getKeys(mod390,vc);			
			if (keys != null) {
				for (Mod3902021DetailKey key : keys) {
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
		// Regimen especial de criterio de caja.
		map.get(Mod3902021DetailKey.C0654).setTaxableBase( getVatAccrualPaymentOutputBase(ctx, mod390) );
		map.get(Mod3902021DetailKey.C0654).setQuota( getVatAccrualPaymentOutputQuota(ctx, mod390) );
		map.get(Mod3902021DetailKey.C0656).setTaxableBase( getVatAccrualPaymentInputBase(ctx, mod390) );
		map.get(Mod3902021DetailKey.C0656).setQuota( getVatAccrualPaymentInputQuota(ctx, mod390) );
		
		// Cálculo de la Regularizacion por aplicacion del porcentaje definitivo de prorrata
		Mod303DAO.getMod303s(ctx, mod390.getDomain())
			.filter( m -> m.getYear( )  == mod390.getYear())
			.filter( Mod303::isAEAT)
			.filter( Mod303::isLastPeriod)
			.map(m -> Mod303DAO.get(ctx, m.getId()))
			.map(m -> m.getAmount(Mod303Key.CT_C44))
			.forEach( r -> 
				map.computeIfAbsent(Mod3902021DetailKey.C0522, k -> new Mod390Detail()
					.setKey(Mod3902021DetailKey.C0522)
					.setQuota(r)))
			;
		
		return new LinkedList<>(map.values());
	}
	
	private static Mod3902021 fillGeneralRegimeData(AONContext ctx, Mod3902021 mod390) {
		try {
			EnumMap<Mod3902021DetailKey, Mod390Detail> map = new EnumMap<>(Mod3902021DetailKey.class);
			Mod390Detail det = null;
			LinkedList<Mod390Detail> details = null;
			details = getDetails(ctx, mod390);
			for (Mod390Detail detail : details) {
				map.put(detail.getKey(), detail); 
			}
			if (!mod390.isSimplifiedRegime()) {
				mod390.setBox99(map.get(Mod3902021DetailKey.C0099).getTaxableBase());
				mod390.setBox100(0.0);
			} else {
				mod390.setBox99(0.0);
				mod390.setBox100(map.get(Mod3902021DetailKey.C0099).getTaxableBase());
			}
			mod390.setBox101(map.get(Mod3902021DetailKey.C0101).getTaxableBase());
			mod390.setBox102(map.get(Mod3902021DetailKey.C0102).getTaxableBase());
			mod390.setBox103(map.get(Mod3902021DetailKey.C0103).getTaxableBase());
			mod390.setBox104(map.get(Mod3902021DetailKey.C0104).getTaxableBase());
			mod390.setBox105(map.get(Mod3902021DetailKey.C0105).getTaxableBase());
			mod390.setBox106(map.get(Mod3902021DetailKey.C0106).getTaxableBase());
			mod390.setBox107(map.get(Mod3902021DetailKey.C0107).getTaxableBase());
			mod390.setBox108(map.get(Mod3902021DetailKey.C0108).getTaxableBase());
			mod390.setBox110(map.get(Mod3902021DetailKey.C0110).getTaxableBase());
			mod390.setBox125(map.get(Mod3902021DetailKey.C0125).getTaxableBase());
			mod390.setBox126(map.get(Mod3902021DetailKey.C0126).getTaxableBase());
			mod390.setBox127(map.get(Mod3902021DetailKey.C0127).getTaxableBase());
			mod390.setBox128(map.get(Mod3902021DetailKey.C0128).getTaxableBase());
			mod390.setBox125(map.get(Mod3902021DetailKey.C0125).getTaxableBase());
			mod390.setBox126(map.get(Mod3902021DetailKey.C0126).getTaxableBase());
			mod390.setBox127(map.get(Mod3902021DetailKey.C0127).getTaxableBase());
			mod390.setBox128(map.get(Mod3902021DetailKey.C0128).getTaxableBase());
			
			mod390.setBox227(map.get(Mod3902021DetailKey.C0227).getTaxableBase());
			mod390.setBox228(map.get(Mod3902021DetailKey.C0228).getTaxableBase());
			mod390.setBox654(map.get(Mod3902021DetailKey.C0654).getTaxableBase());
			mod390.setBox655(map.get(Mod3902021DetailKey.C0654).getQuota());
			mod390.setAccrualRegime( ( map.get(Mod3902021DetailKey.C0654).getTaxableBase()  != 0 || map.get(Mod3902021DetailKey.C0654).getQuota() != 0 ) );
			mod390.setBox656(map.get(Mod3902021DetailKey.C0656).getTaxableBase());
			mod390.setBox657(map.get(Mod3902021DetailKey.C0656).getQuota());
			mod390.setAccrualRegimeTarget((map.get(Mod3902021DetailKey.C0656).getTaxableBase()  != 0 || map.get(Mod3902021DetailKey.C0656).getQuota() != 0 ));

			// ----------------------------------------------------------------------------
			// En el caso de que el declarante este acogido al regimen simplificado
			// Se utiliza toda la funcionalidad del regimen general (lectura de facturas)
			// para rellenar los campos anteriores , del 99 al 657. Sin embargo la 
			// página 5 del modelo, o sea la del regimen general debe ir vacia, por lo 
			// que se incializa el mapa.
			if (mod390.isSimplifiedRegime()) {
				map = new EnumMap<>(Mod3902021DetailKey.class);
				for (Mod3902021DetailKey key : Mod3902021DetailKey.values() ) {
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
		} catch (Exception t) {
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
		boolean fill(SimplifedRegimeContext src,Mod3902021 mod390);
	}

	enum SimplifiedRegimeFiller {
		CAG1     (Mod303Key.CT_SA11,(src,mod390) -> {mod390.getFarmerRegime1().setCodigo(AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(),"-")));return true;}), 
		CAG1_V1  (Mod303Key.CT_SA12,(src,mod390) -> {mod390.getFarmerRegime1().setIncomes(src.getAmount());return true;}),
		CAG1_V2  (Mod303Key.CT_SA13,(src,mod390) -> {mod390.getFarmerRegime1().setQuotaIndex( AonMathUtils.round(src.getAmount() / 10000 ,5));return true;}),
		CAG1_V3  (Mod303Key.CT_SA14,(src,mod390) -> {mod390.getFarmerRegime1().setAccrualQuota(src.getAmount());return true;}),
		CAG1_V6  (Mod303Key.CT_SA17,(src,mod390) -> {mod390.getFarmerRegime1().setInputQuotas(src.getAmount());return true;}),
		CAG1_V7  (Mod303Key.CT_SA18,(src,mod390) -> {mod390.getFarmerRegime1().setQuota(src.getAmount());return true;}),
		
		CAG2     (Mod303Key.CT_SA21,(src,mod390) -> {mod390.getFarmerRegime2().setCodigo(AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-")));return true;}),
		CAG2_V1  (Mod303Key.CT_SA22,(src,mod390) -> {mod390.getFarmerRegime2().setIncomes(src.getAmount());return true;}),
		CAG2_V2  (Mod303Key.CT_SA23,(src,mod390) -> {mod390.getFarmerRegime2().setQuotaIndex(AonMathUtils.round(src.getAmount() / 10000 ,5));return true;}),
		CAG2_V3  (Mod303Key.CT_SA24,(src,mod390) -> {mod390.getFarmerRegime2().setAccrualQuota(src.getAmount());return true;}),
		CAG2_V6  (Mod303Key.CT_SA27,(src,mod390) -> {mod390.getFarmerRegime2().setInputQuotas(src.getAmount());return true;}),
		CAG2_V7  (Mod303Key.CT_SA28,(src,mod390) -> {mod390.getFarmerRegime2().setQuota(src.getAmount());return true;}),
		
		CAC1     (Mod303Key.CT_S101,(src,mod390) -> {mod390.getSimpRegime1().setEpigrafe(AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-")));return true;}),
		CAC1_M1U (Mod303Key.CT_S11I, (src,mod390) -> {mod390.getSimpRegime1().setUnit1(src.getAmount());return true;}),
		CAC1_M1I (Mod303Key.CT_S11R, (src,mod390) -> {mod390.getSimpRegime1().setAmount1(src.getAmount());return true;}),
		CAC1_M2U (Mod303Key.CT_S12I, (src,mod390) -> {mod390.getSimpRegime1().setUnit2(src.getAmount());return true;}),
		CAC1_M2I (Mod303Key.CT_S12R, (src,mod390) -> {mod390.getSimpRegime1().setAmount2(src.getAmount());return true;}),
		CAC1_M3U (Mod303Key.CT_S13I, (src,mod390) -> {mod390.getSimpRegime1().setUnit3(src.getAmount());return true;}),
		CAC1_M3I (Mod303Key.CT_S13R, (src,mod390) -> {mod390.getSimpRegime1().setAmount3(src.getAmount());return true;}),
		CAC1_M4U (Mod303Key.CT_S14I, (src,mod390) -> {mod390.getSimpRegime1().setUnit4(src.getAmount());return true;}),
		CAC1_M4I (Mod303Key.CT_S14R, (src,mod390) -> {mod390.getSimpRegime1().setAmount4(src.getAmount());return true;}),
		CAC1_M5U (Mod303Key.CT_S15I, (src,mod390) -> {mod390.getSimpRegime1().setUnit5(src.getAmount());return true;}),
		CAC1_M5I (Mod303Key.CT_S15R, (src,mod390) -> {mod390.getSimpRegime1().setAmount5(src.getAmount());return true;}),
		CAC1_M6U (Mod303Key.CT_S16I, (src,mod390) -> {mod390.getSimpRegime1().setUnit6(src.getAmount());return true;}),
		CAC1_M6I (Mod303Key.CT_S16R, (src,mod390) -> {mod390.getSimpRegime1().setAmount6(src.getAmount());return true;}),
		CAC1_M7U (Mod303Key.CT_S17I, (src,mod390) -> {mod390.getSimpRegime1().setUnit7(src.getAmount());return true;}),
		CAC1_M7I (Mod303Key.CT_S17R, (src,mod390) -> {mod390.getSimpRegime1().setAmount7(src.getAmount());return true;}),
		CAC1_C   (Mod303Key.CT_S117, (src,mod390) -> {
			mod390.getSimpRegime1().setBoxC(src.getAmount());
			mod390.setBox74( AonMathUtils.round(mod390.getBox74() + src.getAmount()));
			return true;}),
//		CAC1_C1  (Mod303Key.CT_S117, (src,mod390) -> {mod390.getSimpRegime1().setBoxC1(src.getAmount());return true;}),
		CAC1_D   (Mod303Key.CT_S118, null),
		CAC1_Z   (Mod303Key.CT_S119, null),
//		CAC1_ZA  (Mod303Key.CAC1_ZA , null),
//		CAC1_ZD  (Mod303Key.CAC1_ZD , null),
		CAC1_E   (Mod303Key.CT_S120 , null),
		CAC1_F   (Mod303Key.CT_S121 , null),
//		CAC1_G0  (Mod303Key.CAC1_G0 , (src,mod390) -> {mod390.getSimpRegime1().setBoxD(
//				AonMathUtils.round(mod390.getSimpRegime1().getBoxD() + src.getAmount()));return true;}),
		CAC1_G   (Mod303Key.CT_S122 , (src,mod390) -> {mod390.getSimpRegime1().setBoxD(
				AonMathUtils.round(mod390.getSimpRegime1().getBoxD() + src.getAmount()));return true;}),
		CAC1_H   (Mod303Key.CT_S123 , (src,mod390) -> {mod390.getSimpRegime1().setBoxE(src.getAmount());return true;}),
//		CAC1_HA  (Mod303Key.CAC1_HA , null),
//		CAC1_HD  (Mod303Key.CAC1_HD , null),
//		CAC1_HT  (Mod303Key.CAC1_HT , null),
		CAC1_I   (Mod303Key.CT_S124 , (src,mod390) -> {mod390.getSimpRegime1().setBoxF(src.getAmount());return true;}),
		CAC1_J   (Mod303Key.CT_S125 , (src,mod390) -> {mod390.getSimpRegime1().setBoxG(src.getAmount());return true;}),
		CAC1_K   (Mod303Key.CT_S126 , null),
		CAC1_L   (Mod303Key.CT_S127 , (src,mod390) -> {mod390.getSimpRegime1().setBoxI(src.getAmount());return true;}),
		CAC1_M   (Mod303Key.CT_S128 , (src,mod390) -> {mod390.getSimpRegime1().setBoxJ(src.getAmount());return true;}),
		
		
		CAC2     (Mod303Key.CT_S201    , (src,mod390) -> {
			String code = AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-"));					
			mod390.getSimpRegime2().setEpigrafe(code);
			return true;}),
		CAC2_M1U (Mod303Key.CT_S21I, (src,mod390) -> {mod390.getSimpRegime2().setUnit1(src.getAmount());return true;}),
		CAC2_M1I (Mod303Key.CT_S21R, (src,mod390) -> {mod390.getSimpRegime2().setAmount1(src.getAmount());return true;}),
		CAC2_M2U (Mod303Key.CT_S22I, (src,mod390) -> {mod390.getSimpRegime2().setUnit2(src.getAmount());return true;}),
		CAC2_M2I (Mod303Key.CT_S22R, (src,mod390) -> {mod390.getSimpRegime2().setAmount2(src.getAmount());return true;}),
		CAC2_M3U (Mod303Key.CT_S23I, (src,mod390) -> {mod390.getSimpRegime2().setUnit3(src.getAmount());return true;}),
		CAC2_M3I (Mod303Key.CT_S23R, (src,mod390) -> {mod390.getSimpRegime2().setAmount3(src.getAmount());return true;}),
		CAC2_M4U (Mod303Key.CT_S24I, (src,mod390) -> {mod390.getSimpRegime2().setUnit4(src.getAmount());return true;}),
		CAC2_M4I (Mod303Key.CT_S24R, (src,mod390) -> {mod390.getSimpRegime2().setAmount4(src.getAmount());return true;}),
		CAC2_M5U (Mod303Key.CT_S25I, (src,mod390) -> {mod390.getSimpRegime2().setUnit5(src.getAmount());return true;}),
		CAC2_M5I (Mod303Key.CT_S25R, (src,mod390) -> {mod390.getSimpRegime2().setAmount5(src.getAmount());return true;}),
		CAC2_M6U (Mod303Key.CT_S26I, (src,mod390) -> {mod390.getSimpRegime2().setUnit6(src.getAmount());return true;}),
		CAC2_M6I (Mod303Key.CT_S26R, (src,mod390) -> {mod390.getSimpRegime2().setAmount6(src.getAmount());return true;}),
		CAC2_M7U (Mod303Key.CT_S27I, (src,mod390) -> {mod390.getSimpRegime2().setUnit7(src.getAmount());return true;}),
		CAC2_M7I (Mod303Key.CT_S27R, (src,mod390) -> {mod390.getSimpRegime2().setAmount7(src.getAmount());return true;}),
		CAC2_C   (Mod303Key.CT_S217 , (src,mod390) ->	{
				mod390.getSimpRegime2().setBoxC(src.getAmount());
				mod390.setBox74( AonMathUtils.round(mod390.getBox74() + src.getAmount()));
				return true;
														}),
//		CAC2_C1  (Mod303Key.CAC2_C1 , (src,mod390) -> {mod390.getSimpRegime2().setBoxC1(src.getAmount());return true;}),
		CAC2_D   (Mod303Key.CT_S218, null),
		CAC2_Z   (Mod303Key.CT_S219 , null),
//		CAC2_ZA  (Mod303Key.CAC2_ZA , null),
//		CAC2_ZD  (Mod303Key.CAC2_ZD , null),
		CAC2_E   (Mod303Key.CT_S220 , null),
		CAC2_F   (Mod303Key.CT_S221 , null),
		
//		CAC2_G0  (Mod303Key.CAC2_G0 , (src,mod390) -> {mod390.getSimpRegime2().setBoxD(
//				AonMathUtils.round(mod390.getSimpRegime2().getBoxD() + src.getAmount()));return true;}),
		CAC2_G   (Mod303Key.CT_S222 , (src,mod390) -> {mod390.getSimpRegime2().setBoxD(
				AonMathUtils.round(mod390.getSimpRegime2().getBoxD() + src.getAmount()));return true;}),
		CAC2_H   (Mod303Key.CT_S223 , (src,mod390) -> {mod390.getSimpRegime2().setBoxE(src.getAmount());return true;}),
//		CAC2_HA  (Mod303Key.CAC2_HA , null),
//		CAC2_HD  (Mod303Key.CAC2_HD , null),
//		CAC2_HT  (Mod303Key.CAC2_HT , null),
		CAC2_I   (Mod303Key.CT_S224 , (src,mod390) -> {mod390.getSimpRegime2().setBoxF(src.getAmount());return true;}),
		CAC2_J	 (Mod303Key.CT_S225 , (src,mod390) -> {mod390.getSimpRegime2().setBoxG(src.getAmount());return true;}),
		CAC2_K   (Mod303Key.CT_S226 , null),
		CAC2_L   (Mod303Key.CT_S227 , (src,mod390) -> {mod390.getSimpRegime2().setBoxI(src.getAmount());return true;}),
		CAC2_M   (Mod303Key.CT_S228 , (src,mod390) -> {mod390.getSimpRegime2().setBoxJ(src.getAmount());return true;}),
		
//		C51      (Mod303Key.C51     , null),
//		C52      (Mod303Key.C52     , null),
//		C53      (Mod303Key.CT_S51  , null),
//		C54      (Mod303Key.C54     , null),
//		C55      (Mod303Key.C55     , null),
//		C56      (Mod303Key.C56     , null),
//		C57      (Mod303Key.C57     , null),
//		C58      (Mod303Key.C58     , null),
//		C71      (Mod303Key.C71     , null),
//		PBK      (Mod303Key.PBK     , null),
		
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
		public static void fill(SimplifedRegimeContext src,Mod3902021 mod390) {
			for (SimplifiedRegimeFiller filler : SimplifiedRegimeFiller.values()) {
				if (filler.getKey() ==  src.getKey() && filler.getFiller() != null) {
					filler.getFiller().fill(src, mod390);
				}
			}
		}
		
	}	
	
	private static Mod3902021 fillSimplifedRegimeData(AONContext ctx, Mod3902021 mod390) {
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
			.and(FS_MODEL.MODEL.equal(FiscalModelType.M303.getValue()))
		.fetch()
		.stream()
		.forEach( rec -> {
			String description = rec.getValue( FS_MODEL_DETAIL.DESCRIPTION );
			String type = rec.getValue( FS_MODEL_DETAIL.TYPE );
			double amount = rec.getValue( FS_MODEL_DETAIL.AMOUNT );
			Mod303Key key = Mod303Key.getKey(type);
			SimplifedRegimeContext src = new SimplifedRegimeContext(key, description, amount);
			SimplifiedRegimeFiller.fill(src,mod390);
			}
		);
		return mod390;
	}
	private static void fillGeneralDeclarationResults(AONContext ctx, Mod3902021 mod390) {
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
				if (m303.isFirstPeriod()) {
					mod390.setBox85(  m303.getAmount( Mod303Key.CT_C110) );
				}
				if (m303.isLastPeriod()) {
					mod390.setBox662(  m303.getAmount( Mod303Key.CT_C87) );
				}
			});
		
	}
	private static void fillSimplifiedDeclarationResults(AONContext ctx, Mod3902021 mod390) {
		mod390.setBox97( AonMathUtils.round(mod390.getBox97() * -1));
		mod390.setBox98( AonMathUtils.round(mod390.getBox98() * -1));
		if (mod390.getBox98() > 0) {
			mod390.setBox97( 0 ); 	
		}
		Record1<BigDecimal> rec = ctx.getDslContext()
			.select(DSL.sum(FS_MODEL_DETAIL.AMOUNT))
			.from(FS_MODEL)
			.join(FS_MODEL_DETAIL).on(FS_MODEL.ID.equal(FS_MODEL_DETAIL.FS_MODEL))
			.where(FS_MODEL.DOMAIN.equal(mod390.getDomain()))
			.and(FS_MODEL.YEAR.equal(mod390.getYear()))
			.and(FS_MODEL_DETAIL.AMOUNT.greaterThan(0.0))
			.and(FS_MODEL.MODEL.equal("303"))
			.and(FS_MODEL_DETAIL.TYPE.equal("303-71"))
			.fetchOne();
		if (rec != null) {
			BigDecimal quota = rec.getValue(DSL.sum(FS_MODEL_DETAIL.AMOUNT)); 
			if (quota != null) {
				mod390.setBox95( quota.doubleValue());
			}
		}
	}

	public static Mod3902021 changeStatus(AONContext ctx, Mod3902021 mod390, FiscalStatus newStatus) {
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
		} catch (Exception t) {
			throw new AonCoreException(t.getMessage());
		}
	}

	public static double getVatAccrualPaymentOutputBase(AONContext ctx, Mod390 mod390) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod390.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod390);
		return VATDAO.getVatAccrualPaymentOutputBase(ctx,fromDate,toDate);
	}

	public static double getVatAccrualPaymentOutputQuota(AONContext ctx, Mod390 mod390) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod390.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod390);
		return VATDAO.getVatAccrualPaymentOutputQuota(ctx,fromDate,toDate);
	}

	public static double getVatAccrualPaymentInputBase(AONContext ctx, Mod390 mod390) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod390.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod390);
		return VATDAO.getVatAccrualPaymentInputBase(ctx,fromDate,toDate);
	}

	public static double getVatAccrualPaymentInputQuota(AONContext ctx, Mod390 mod390) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod390.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod390);
		return VATDAO.getVatAccrualPaymentInputQuota(ctx,fromDate,toDate);
	}

	// -----------------------------------------------------------------------
	// --------------------------------------------------------------- FILTROS
	// -----------------------------------------------------------------------
	private static boolean isCommonNationalSales(VatContext vat, Mod3902021 mod) {
		return !vat.isVatSurchargeRegime() && vat.isNational()
				&& vat.isSales() && !vat.isRectification();
	}
	private static boolean isCommonNationalSalesRECT(VatContext vat, Mod3902021 mod) {
		return !vat.isVatSurchargeRegime() && vat.isNational()
				&& vat.isSales() && vat.isRectification();
	}
	private static boolean hasPercent1(VatContext vat) {
		return vat.getPercentage() == PERCENT1;
	}

	private static boolean hasPercent2(VatContext vat) {
		return vat.getPercentage() == PERCENT2;
	}

	private static boolean hasPercent3(VatContext vat) {
		return vat.getPercentage() == PERCENT3;
	}
	private static boolean hasSurchargePercent1(VatContext vat) {
		return vat.getSurchargePercent() == SURCHARGE_PERCENT1;
	}

	private static boolean hasSurchargePercent2(VatContext vat) {
		return vat.getSurchargePercent() == SURCHARGE_PERCENT2;
	}

	private static boolean hasSurchargePercent3(VatContext vat) {
		return vat.getSurchargePercent() == SURCHARGE_PERCENT3;
	}
	private static boolean hasSurchargePercent4(VatContext vat) {
		return vat.getSurchargePercent() == SURCHARGE_PERCENT4;
	}
	
	private static boolean isIntracommunityPurchase(VatContext vat, Mod3902021 mod) {
		return !vat.isVatSurchargeRegime() && !vat.isRectification() && vat.isIntracommunityPurchase();
	}
	private static boolean isIntracommunityExpenses(VatContext vat, Mod3902021 mod) {
		return !vat.isVatSurchargeRegime() && !vat.isRectification() && vat.isIntracommunityExpenses();
	}
	private static boolean isOperacionesISPFilter(VatContext vat, Mod3902021 mod) {
		return !vat.isVatSurchargeRegime()
				&& (vat.isOtherISPPurchase() || vat.isOtherISPExpenses() || vat.isExtracommunityExpenses()
						|| vat.isCanCeuMelExpenses() || (vat.isExtracommunityPurchase() && vat.isService())
						|| (vat.isCanCeuMelPurchase() && vat.isService()));
	}

	
	// **************************
	// **************************
	// **************************

	private static boolean importacionesCorrientesFilter(VatContext vat) {
		return commonImportacionesFilter(vat) && !vat.isInvestment();
	}

	private static boolean importacionesInversionFilter(VatContext vat) {
		return commonImportacionesFilter(vat) && vat.isInvestment();
	}

	private static boolean commonImportacionesFilter(VatContext vat) {
		boolean basicFilter =  !vat.isVatSurchargeRegime() 
				&& !vat.isRectification() 
				&& !vat.isService();
		if (basicFilter && (vat.isExtracommunityPurchase() || vat.isCanCeuMelPurchase())) {
			if (vat.getTaxDate().before( IVA_2021_CHANGE_DATE )) {
				basicFilter = true;
			} else {
				basicFilter = vat.hasDuaLinked() || vat.isVatImportation();
			}
			return basicFilter; 
		}
		return false;
	}
	
	private static boolean operacionesISPFilter(VatContext vat) {
		return !vat.isVatSurchargeRegime()
			&& (vat.isOtherISPPurchase() 
			 || vat.isOtherISPExpenses() 
			 || vat.isExtracommunityExpenses()
 			 || vat.isCanCeuMelExpenses() 
 			 || (vat.isExtracommunityPurchase() && vat.isService())
			 || (vat.isCanCeuMelPurchase() && vat.isService()));
	}

	private static boolean operacionesInterioresCorrientesFilter(VatContext vat) {
		return !vat.isVatSurchargeRegime() && !vat.isInvestment()
			&& !vat.isRectification() && !vat.isFarmerRegime() && AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || operacionesISPFilter(vat));
	}
	
	private static boolean operacionesInterioresInversionFilter(VatContext vat) {
		return !vat.isVatSurchargeRegime() && vat.isInvestment()
				&& !vat.isRectification() && !vat.isFarmerRegime() && AonMathUtils.isNotZero(vat.getPercentage())
				&& (vat.isNationalPurchase() || vat.isNationalExpenses() || operacionesISPFilter(vat));
	}
	
	private static boolean adqIntracomunitariasCorrientesFilter(VatContext vat) {
		return !vat.isInvestment() && adqIntracomunitariasFilter(vat);
	}
	private static boolean adqIntracomunitariasInversionFilter(VatContext vat) {
		return vat.isInvestment() && adqIntracomunitariasFilter(vat);
	}
	private static boolean adqIntracomunitariasFilter(VatContext vat) {
		return !vat.isVatSurchargeRegime() 
			&& !vat.isService()
			&& !vat.isRectification()
			&& vat.isIntracommunityPurchase();
	}
	private static boolean adqIntracomunitariasServicios(VatContext vat) {
		return !vat.isVatSurchargeRegime() 
				&& !vat.isRectification()
				&& (vat.isIntracommunityExpenses() || (vat.isIntracommunityPurchase() && vat.isService()));
	}

	public static Mod3902021 aeatPresentation(AONContext ctx, Mod3902021 mod, String aeatResponse) {
		if (AonStringUtils.isNotBlank(aeatResponse)) {
			DataResponseDAO.insertAEATResponse(ctx, mod, aeatResponse);
			AEATResponse response = AEATJson.toJSON(aeatResponse.getBytes());
			if (mod != null && mod.getId() != null) {
				ctx.getDslContext().update(FS_MODEL390)
					.set(FS_MODEL390.RECEIPT,response.getJustificante())
					.set(FS_MODEL390.STATUS, FiscalStatus.SENT.value())
					.where(FS_MODEL390.ID.equal(mod.getId()))
					.execute();
				return getById(ctx, mod.getId());
			}
		}
		return mod;
	}

	
	// vatSurchargeRegime
}





