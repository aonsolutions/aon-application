package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod390;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModel390.FS_MODEL390;
import static com.esferalia.aon.jooq.tables.FsModelDetail.FS_MODEL_DETAIL;
import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
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
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.mod390.FarmerRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902018;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902018.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902018DetailKey;
import com.esferalia.aon.occam.api.model.fiscal.mod390.SimpliedRegimeActivity;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303DAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018toMod390;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.Mod390toAEATIVA2018;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
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
		 
		 ,K14_04 (Mod3902018DetailKey.C0191, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && !vc.isRectification() && vc.getPercentage() == 4))) 
		 ,K14_10 (Mod3902018DetailKey.C0604, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && !vc.isRectification() && vc.getPercentage() == 10))) 
		 ,K14_21 (Mod3902018DetailKey.C0606, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && !vc.isInvestment() && !vc.isFarmerRegime() && !vc.isRectification() && vc.getPercentage() == 21))) 
		 ,K15	 (Mod3902018DetailKey.C0049	, null)
		 
		 ,K16_04 (Mod3902018DetailKey.C0507, null)
		 ,K16_10 (Mod3902018DetailKey.C0608, null)
		 ,K16_21 (Mod3902018DetailKey.C0610, null)
		 ,K17	 (Mod3902018DetailKey.C0513	, null)
		 
		 ,K18_04 (Mod3902018DetailKey.C0197, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && !vc.isRectification() && !vc.isFarmerRegime() && vc.getPercentage() ==  4)))
		 ,K18_10 (Mod3902018DetailKey.C0612, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && !vc.isRectification() && !vc.isFarmerRegime() && vc.getPercentage() == 10))) 
		 ,K18_21 (Mod3902018DetailKey.C0614, (vc -> ((vc.isNationalPurchase() || vc.isOtherISPPurchase() || vc.isNationalExpenses() || vc.isCanCeuMelExpenses() || vc.isOtherISPExpenses()) && vc.isInvestment() && !vc.isRectification() && !vc.isFarmerRegime() && vc.getPercentage() == 21)))
		 ,K19	 (Mod3902018DetailKey.C0051	, null)
		 
		 ,K20_04 (Mod3902018DetailKey.C0515, null)
		 ,K20_10 (Mod3902018DetailKey.C0616, null)
		 ,K20_21 (Mod3902018DetailKey.C0618, null)
		 ,K21	 (Mod3902018DetailKey.C0521	, null)
		 
		 ,K22_04 (Mod3902018DetailKey.C0203, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && !vc.isRectification() && !vc.isFarmerRegime() && vc.getPercentage() == 4)))
		 ,K22_10 (Mod3902018DetailKey.C0620, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && !vc.isRectification() && !vc.isFarmerRegime() && vc.getPercentage() == 10)))
		 ,K22_21 (Mod3902018DetailKey.C0622, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isInvestment() && !vc.isRectification() && !vc.isFarmerRegime() && vc.getPercentage() == 21)))
		 ,K23	 (Mod3902018DetailKey.C0053	, null)
		 
		 ,K24_04 (Mod3902018DetailKey.C0209, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isRectification() && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 4)))
		 ,K24_10 (Mod3902018DetailKey.C0624, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isRectification() && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 10)))
		 ,K24_21 (Mod3902018DetailKey.C0626, (vc -> ((vc.isExtracommunityPurchase() || vc.isCanCeuMelPurchase() || vc.isExtracommunityExpenses()) && !vc.isRectification() && vc.isInvestment() && !vc.isFarmerRegime() && vc.getPercentage() == 21)))
		 ,K25	 (Mod3902018DetailKey.C0055	, null)
		 
		 ,K26_04 (Mod3902018DetailKey.C0215, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && !vc.isFarmerRegime() && !vc.isRectification() && vc.getPercentage() ==  4)))
		 ,K26_10 (Mod3902018DetailKey.C0628, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && !vc.isFarmerRegime() && !vc.isRectification() && vc.getPercentage() ==  10)))
		 ,K26_21 (Mod3902018DetailKey.C0630, (vc -> (vc.isIntracommunityPurchase() && !vc.isInvestment() && !vc.isFarmerRegime() && !vc.isRectification() && vc.getPercentage() ==  21)))
		 ,K27	 (Mod3902018DetailKey.C0057	, null)
		 
		 ,K28_04 (Mod3902018DetailKey.C0221, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && !vc.isRectification() && !vc.isFarmerRegime() && vc.getPercentage() ==  4)))
		 ,K28_10 (Mod3902018DetailKey.C0632, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && !vc.isRectification() && !vc.isFarmerRegime() && vc.getPercentage() ==  10))) 
		 ,K28_21 (Mod3902018DetailKey.C0634, (vc -> ((vc.isIntracommunityPurchase() || vc.isIntracommunityExpenses()) && vc.isInvestment() && !vc.isRectification() && !vc.isFarmerRegime() && vc.getPercentage() ==  21))) 
		 ,K29	 (Mod3902018DetailKey.C0059   , null)
		 
		 ,K30_04 (Mod3902018DetailKey.C0588, (vc -> (vc.isIntracommunityExpenses() && !vc.isInvestment() && !vc.isFarmerRegime() && !vc.isRectification() && vc.getPercentage() ==  4)))
		 ,K30_10 (Mod3902018DetailKey.C0636, (vc -> (vc.isIntracommunityExpenses() && !vc.isInvestment() && !vc.isFarmerRegime() && !vc.isRectification() && vc.getPercentage() ==  10)))
		 ,K30_21 (Mod3902018DetailKey.C0638, (vc -> (vc.isIntracommunityExpenses() && !vc.isInvestment() && !vc.isFarmerRegime() && !vc.isRectification() && vc.getPercentage() ==  21)))
		 ,K31	 (Mod3902018DetailKey.C0598   , null)
		 
		 ,K32	 (Mod3902018DetailKey.C0061   , (vc -> ((vc.isPurchase() || vc.isExpenses()) && vc.isFarmerRegime())))
		 
		 // **************
		 ,K33	 (Mod3902018DetailKey.C0062,  (vc -> vc.isRectification() && (vc.isPurchase() || vc.isExpenses() )))
		 // **************
		 
		 ,K34	 (Mod3902018DetailKey.C0063, null)
		 ,K35	 (Mod3902018DetailKey.C0522, null)
		 ,K36	 (Mod3902018DetailKey.C0064, null)
		 ,K37	 (Mod3902018DetailKey.C0065, null)
		 
		 ,B099	 (Mod3902018DetailKey.C0099, (vc -> (vc.isNationalSales())))
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
		boolean found = false;
		if (year == 2018 || year == 2019 || year == 2020) {
			LinkedList<Mod390> mod390s = Mod390DAO.getByDomain(ctx, ctx.getDomainId());
			for (Mod390 m390 : mod390s) {
				if ( m390.getYear() == 2018 || m390.getYear() == 2019) {
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

	private static void fillFromnConfiguration(AONContext ctx, Mod3902018 mod390) {
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
			for (Record2<String,String> record : list ) {
						String document = record.getValue(RDIR_STAFF.DOCUMENT);
						String name = record.getValue(RDIR_STAFF.NAME);
						
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
				if (mod390.getYear() == 2020 || mod390.getYear() == 2019 || mod390.getYear() == 2018) {
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
			if (mod390.getYear() == 2020 || mod390.getYear() == 2019 || mod390.getYear() == 2018 || mod390.getYear() == 2016 || mod390.getYear() == 2017) {
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
	
	private static Mod3902018 update(AONContext ctx, Mod3902018 mod390) {
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
		double prorrata = AonMathUtils.round( mutProrrata.doubleValue() / 100);
		boolean mustApplyProrrata = (prorrata != AonMathUtils.round(0.00));		
		
		VATDAO.getVatBreakdown(ctx, mod390)
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
		// Regimen especial de criterio de caja.
		map.get(Mod3902018DetailKey.C0654).setTaxableBase( getVatAccrualPaymentOutputBase(ctx, mod390) );
		map.get(Mod3902018DetailKey.C0654).setQuota( getVatAccrualPaymentOutputQuota(ctx, mod390) );
		map.get(Mod3902018DetailKey.C0656).setTaxableBase( getVatAccrualPaymentInputBase(ctx, mod390) );
		map.get(Mod3902018DetailKey.C0656).setQuota( getVatAccrualPaymentInputQuota(ctx, mod390) );
		
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

	private static Mod3902018 fillSimplifedRegimeData(AONContext ctx, Mod3902018 mod390) {
		mod390.setSimpRegime1(new SimpliedRegimeActivity());
		mod390.setSimpRegime2(new SimpliedRegimeActivity());
		mod390.setFarmerRegime1(new FarmerRegimeActivity());
		mod390.setFarmerRegime2(new FarmerRegimeActivity());
		mod390.setFarmerRegime3(new FarmerRegimeActivity());
		mod390.setFarmerRegime4(new FarmerRegimeActivity());
		mod390.setFarmerRegime5(new FarmerRegimeActivity());
		return mod390;
	}
	private static void fillGeneralDeclarationResults(AONContext ctx, Mod3902018 mod390) {
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

}
