package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_LROE;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_NO_SIF;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_NO_VERIFACTU;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_SIF;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_SII;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_TBAI;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_VERIFACTU;
import static com.esferalia.aon.occam.api.model.type.Administration.ALAVA;
import static com.esferalia.aon.occam.api.model.type.Administration.BIZKAIA;
import static com.esferalia.aon.occam.api.model.type.Administration.CANARIAS;
import static com.esferalia.aon.occam.api.model.type.Administration.COMMON_TERRITORY;
import static com.esferalia.aon.occam.api.model.type.Administration.GIPUZKOA;
import static com.esferalia.aon.occam.api.model.type.Administration.NAVARRA;

import java.util.Date;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.EnterpriseDataNames;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData.ExemptType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;

public class ICCDAO {
	
	public static final String ICC_PREFIX = "ICC_%";
	
	private ICCDAO() {

	}
	
	// -----------------------------------------------------
	// -------------------------------- [ Enable TicketBai ]
	// -----------------------------------------------------
	private static CommunicationData getTbaiData(Date startDate) {
		return getTbaiData(startDate, null, false);
	}
	private static CommunicationData getTbaiData(Date startDate, ExemptType exemptType) {
		return getTbaiData(startDate, exemptType, false);
	}
 	private static CommunicationData getTbaiData(Date startDate, boolean test) {
		return getTbaiData(startDate, null, test);
 	}
 	private static CommunicationData getTbaiData(Date startDate, ExemptType exemptType, boolean test) {
		return new CommunicationData()
			.setDataName(ICC_TBAI)
			.setStartDate( startDate)
			.setTest(test)
			.setExemptType( exemptType )
		;
	}
	public static InvoiceCommunicationConfiguration enableTbaiAraba(AONContext ctx, Integer domainId, Date date) {
		return enableTbai(ctx, domainId, ALAVA, getTbaiData(date));
	}
	public static InvoiceCommunicationConfiguration enableTbaiAraba(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableTbai(ctx, domainId, ALAVA, getTbaiData(date, exemptType));
	}
	public static InvoiceCommunicationConfiguration enableTbaiArabaTest(AONContext ctx, Integer domainId, Date date) {
		return enableTbai(ctx, domainId, ALAVA, getTbaiData(date, true));
	}
	public static InvoiceCommunicationConfiguration enableTbaiArabaTest(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableTbai(ctx, domainId, ALAVA, getTbaiData(date, exemptType, true));
	}
	
	public static InvoiceCommunicationConfiguration enableTbaiGipuzkoa(AONContext ctx, Integer domainId, Date date) {
		return enableTbai(ctx, domainId, GIPUZKOA, getTbaiData(date));
	}
	public static InvoiceCommunicationConfiguration enableTbaiGipuzkoaTest(AONContext ctx, Integer domainId, Date date) {
		return enableTbai(ctx, domainId, GIPUZKOA, getTbaiData(date, true));
	}
	public static InvoiceCommunicationConfiguration enableTbai(AONContext ctx, Integer domainId, Administration admon, Date date, ExemptType exemptType) {
		return enableTbai(ctx, domainId, admon, getTbaiData(date, exemptType));
	}
	private static InvoiceCommunicationConfiguration enableTbai(AONContext ctx, Integer domainId, Administration admon, CommunicationData data) {
		return ICCEnablerDAO.enable(ctx, domainId, admon, data);
	}
	// -----------------------------------------------------
	// ---------------------------- [ disable No Verifactu ]
	// -----------------------------------------------------
	public static InvoiceCommunicationConfiguration disableTbai(AONContext ctx, Integer domainId, Date atDate, Date endDate) {
		return ICCDisablerDAO.disable(ctx, domainId, atDate, ICC_TBAI , endDate);
	}
	
	
	// -----------------------------------------------------
	// ------------------------------------- [ Enable LROE ]
	// -----------------------------------------------------
	private static CommunicationData getLroeData(Date startDate) {
		return getLroeData(startDate, null, false);
	}
	private static CommunicationData getLroeData(Date startDate, ExemptType exemptType) {
		return getLroeData(startDate, exemptType, false);
	}
 	private static CommunicationData getLroeData(Date startDate, boolean test) {
		return getLroeData(startDate, null, test);
 	}
 	private static CommunicationData getLroeData(Date startDate, ExemptType exemptType, boolean test) {
		return new CommunicationData()
			.setDataName(ICC_LROE)
			.setStartDate( startDate)
			.setTest(test)
			.setExemptType( exemptType )
		;
	}
	public static InvoiceCommunicationConfiguration enableLroe(AONContext ctx, Integer domainId, Date date) {
		return ICCEnablerDAO.enable(ctx, domainId, BIZKAIA, getLroeData(date));
	}
	public static InvoiceCommunicationConfiguration enableLroe(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return ICCEnablerDAO.enable(ctx, domainId, BIZKAIA, getLroeData(date, exemptType));
	}
	public static InvoiceCommunicationConfiguration enableLroeTest(AONContext ctx, Integer domainId, Date date) {
		return ICCEnablerDAO.enable(ctx, domainId, BIZKAIA, getLroeData(date, true));
	}
	public static InvoiceCommunicationConfiguration enableLroeTest(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return ICCEnablerDAO.enable(ctx, domainId, BIZKAIA, getLroeData(date, exemptType, true));
	}
	// -----------------------------------------------------
	// ---------------------------- [ disable No Verifactu ]
	// -----------------------------------------------------
	public static InvoiceCommunicationConfiguration disableLroe(AONContext ctx, Integer domainId, Date atDate, Date endDate) {
		return ICCDisablerDAO.disable(ctx, domainId, atDate, ICC_LROE, endDate);
	}

	// -----------------------------------------------------
	// --------------------------------- [ Enable Verifactu]
	// -----------------------------------------------------
	private static CommunicationData getVerifactuData(Date startDate) {
		return getVerifactuData(startDate, null, false);
	}
	private static CommunicationData getVerifactuData(Date startDate, ExemptType exemptType) {
		return getVerifactuData(startDate, exemptType, false);
	}
 	private static CommunicationData getVerifactuData(Date startDate, boolean test) {
		return getVerifactuData(startDate, null, test);
 	}
 	private static CommunicationData getVerifactuData(Date startDate, ExemptType exemptType, boolean test) {
		return new CommunicationData()
			.setDataName(ICC_VERIFACTU)
			.setStartDate( startDate)
			.setTest(test)
			.setExemptType( exemptType )
		;
	}
	public static InvoiceCommunicationConfiguration enableVerifactu(AONContext ctx, Integer domainId, Date date) {
		return enableVerifactu(ctx, domainId, COMMON_TERRITORY, getVerifactuData(date));
	}
	public static InvoiceCommunicationConfiguration enableVerifactu(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableVerifactu(ctx, domainId, COMMON_TERRITORY, getVerifactuData(date, exemptType));
	}
	public static InvoiceCommunicationConfiguration enableVerifactu(AONContext ctx, Integer domainId, Administration admon, Date date, ExemptType exemptType) {
		return enableVerifactu(ctx, domainId, admon, getVerifactuData(date, exemptType));
	}
	
	public static InvoiceCommunicationConfiguration enableVerifactuTest(AONContext ctx, Integer domainId, Date date) {
		return enableVerifactu(ctx, domainId, COMMON_TERRITORY, getVerifactuData(date, true));
	}
	public static InvoiceCommunicationConfiguration enableVerifactuTest(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableVerifactu(ctx, domainId, COMMON_TERRITORY, getVerifactuData(date, exemptType, true));
	}
	
	public static InvoiceCommunicationConfiguration enableVerifactuCanarias(AONContext ctx, Integer domainId, Date date) {
		return enableVerifactu(ctx, domainId, CANARIAS, getVerifactuData(date));
	}
	public static InvoiceCommunicationConfiguration enableVerifactuCanarias(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableVerifactu(ctx, domainId, CANARIAS, getVerifactuData(date, exemptType));
	}
	
	public static InvoiceCommunicationConfiguration enableVerifactuCanariasTest(AONContext ctx, Integer domainId, Date date) {
		return enableVerifactu(ctx, domainId, CANARIAS, getVerifactuData(date, true));
	}
	public static InvoiceCommunicationConfiguration enableVerifactuCanariasTest(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableVerifactu(ctx, domainId, CANARIAS, getVerifactuData(date, exemptType, true));
	}
	
	private static InvoiceCommunicationConfiguration enableVerifactu(AONContext ctx, Integer domainId, Administration admon, CommunicationData data) {
		return ICCEnablerDAO.enable(ctx, domainId, admon, data);
	}
	// -----------------------------------------------------
	// ---------------------------- [ disable No Verifactu ]
	// -----------------------------------------------------
	public static InvoiceCommunicationConfiguration disableVerifactu(AONContext ctx, Integer domainId, Date atDate, Date endDate) {
		return ICCDisablerDAO.disable(ctx, domainId, atDate, ICC_VERIFACTU , endDate);
	}
	
	// -----------------------------------------------------
	// ----------------------------- [ Enable No Verifactu ]
	// -----------------------------------------------------
	private static CommunicationData getNoVerifactuData(Date startDate) {
		return getNoVerifactuData(startDate, null, false);
	}
	private static CommunicationData getNoVerifactuData(Date startDate, ExemptType exemptType) {
		return getNoVerifactuData(startDate, exemptType, false);
	}
 	private static CommunicationData getNoVerifactuData(Date startDate, boolean test) {
		return getNoVerifactuData(startDate, null, test);
 	}
 	private static CommunicationData getNoVerifactuData(Date startDate, ExemptType exemptType, boolean test) {
		return new CommunicationData()
			.setDataName(ICC_NO_VERIFACTU)
			.setStartDate( startDate)
			.setTest(test)
			.setExemptType( exemptType )
		;
	}
	public static InvoiceCommunicationConfiguration enableNoVerifactu(AONContext ctx, Integer domainId, Date date) {
		return enableNoVerifactu(ctx, domainId, COMMON_TERRITORY, getNoVerifactuData(date));
	}
	public static InvoiceCommunicationConfiguration enableNoVerifactu(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableNoVerifactu(ctx, domainId, COMMON_TERRITORY, getNoVerifactuData(date, exemptType));
	}
	public static InvoiceCommunicationConfiguration enableNoVerifactuTest(AONContext ctx, Integer domainId, Date date) {
		return enableNoVerifactu(ctx, domainId, COMMON_TERRITORY, getNoVerifactuData(date, true));
	}
	public static InvoiceCommunicationConfiguration enableNoVerifactuTest(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableNoVerifactu(ctx, domainId, COMMON_TERRITORY, getNoVerifactuData(date, exemptType, true));
	}
	
	public static InvoiceCommunicationConfiguration enableNoVerifactuCanarias(AONContext ctx, Integer domainId, Date date) {
		return enableNoVerifactu(ctx, domainId, CANARIAS, getNoVerifactuData(date));
	}
	public static InvoiceCommunicationConfiguration enableNoVerifactuCanarias(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableNoVerifactu(ctx, domainId, CANARIAS, getNoVerifactuData(date, exemptType));
	}
	
	public static InvoiceCommunicationConfiguration enableNoVerifactuCanariasTest(AONContext ctx, Integer domainId, Date date) {
		return enableNoVerifactu(ctx, domainId, CANARIAS, getNoVerifactuData(date, true));
	}
	public static InvoiceCommunicationConfiguration enableNoVerifactuCanariasTest(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableNoVerifactu(ctx, domainId, CANARIAS, getNoVerifactuData(date, exemptType, true));
	}
	
	public static InvoiceCommunicationConfiguration enableNoVerifactu(AONContext ctx, Integer domainId, Administration admon, Date date) {
		return enableNoVerifactu(ctx, domainId, admon, getNoVerifactuData(date));
	}
	public static InvoiceCommunicationConfiguration enableNoVerifactu(AONContext ctx, Integer domainId, Administration admon, Date date, ExemptType exemptType) {
		return enableNoVerifactu(ctx, domainId, admon, getNoVerifactuData(date, exemptType));
	}
	private static InvoiceCommunicationConfiguration enableNoVerifactu(AONContext ctx, Integer domainId, Administration admon, CommunicationData data) {
		return ICCEnablerDAO.enable(ctx, domainId, admon, data);
	}
	// -----------------------------------------------------
	// ---------------------------- [ disable No Verifactu ]
	// -----------------------------------------------------
	public static InvoiceCommunicationConfiguration disableNoVerifactu(AONContext ctx, Integer domainId, Date atDate, Date endDate) {
		return ICCDisablerDAO.disable(ctx, domainId, atDate, ICC_NO_VERIFACTU , endDate);
	}

	// -----------------------------------------------------
	// -------------------------------------- [ Enable SII ]
	// -----------------------------------------------------
	private static CommunicationData getSiiData(Date startDate) {
		return getSiiData(startDate, null, false);
	}
	private static CommunicationData getSiiData(Date startDate, ExemptType exemptType) {
		return getSiiData(startDate, exemptType, false);
	}
 	private static CommunicationData getSiiData(Date startDate, boolean test) {
		return getSiiData(startDate, null, test);
 	}
 	private static CommunicationData getSiiData(Date startDate, ExemptType exemptType, boolean test) {
		return new CommunicationData()
			.setDataName(ICC_SII)
			.setStartDate( startDate)
			.setTest(test)
			.setExemptType( exemptType )
		;
	}
	public static InvoiceCommunicationConfiguration enableSii(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, COMMON_TERRITORY, getSiiData(date));
	}
	public static InvoiceCommunicationConfiguration enableSii(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableSii(ctx, domainId, COMMON_TERRITORY, getSiiData(date,exemptType));
	}
	public static InvoiceCommunicationConfiguration enableSiiTest(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, COMMON_TERRITORY, getSiiData(date, true));
	}
	public static InvoiceCommunicationConfiguration enableSiiTest(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableSii(ctx, domainId, COMMON_TERRITORY, getSiiData(date, exemptType, true));
	}
	public static InvoiceCommunicationConfiguration enableSiiCanarias(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, CANARIAS, getSiiData(date));
	}
	public static InvoiceCommunicationConfiguration enableSiiCanarias(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableSii(ctx, domainId, CANARIAS, getSiiData(date, exemptType));
	}
	public static InvoiceCommunicationConfiguration enableSiiCanariasTest(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, CANARIAS, getSiiData(date, true));
	}
	public static InvoiceCommunicationConfiguration enableSiiCanariasTest(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableSii(ctx, domainId, CANARIAS, getSiiData(date, exemptType, true));
	}
	public static InvoiceCommunicationConfiguration enableSiiNavarra(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, NAVARRA, getSiiData(date));
	}
	public static InvoiceCommunicationConfiguration enableSiiNavarra(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableSii(ctx, domainId, NAVARRA, getSiiData(date, exemptType));
	}
	public static InvoiceCommunicationConfiguration enableSiiNavarraTest(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, NAVARRA, getSiiData(date, true));
	}
	public static InvoiceCommunicationConfiguration enableSiiNavarraTest(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableSii(ctx, domainId, NAVARRA, getSiiData(date, exemptType, true));
	}
	public static InvoiceCommunicationConfiguration enableSiiAraba(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, ALAVA, getSiiData(date));
	}
	public static InvoiceCommunicationConfiguration enableSiiAraba(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableSii(ctx, domainId, ALAVA, getSiiData(date, exemptType));
	}
	public static InvoiceCommunicationConfiguration enableSiiArabaTest(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, ALAVA, getSiiData(date, true));
	}
	public static InvoiceCommunicationConfiguration enableSiiArabaTest(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableSii(ctx, domainId, ALAVA, getSiiData(date, exemptType, true));
	}
	public static InvoiceCommunicationConfiguration enableSiiGipuzkoa(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, GIPUZKOA, getSiiData(date));
	}
	public static InvoiceCommunicationConfiguration enableSiiGipuzkoa(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableSii(ctx, domainId, GIPUZKOA, getSiiData(date, exemptType));
	}
	public static InvoiceCommunicationConfiguration enableSiiGipuzkoaTest(AONContext ctx, Integer domainId, Date date) {
		return enableSii(ctx, domainId, GIPUZKOA, getSiiData(date, true));
	}
	public static InvoiceCommunicationConfiguration enableSiiGipuzkoaTest(AONContext ctx, Integer domainId, Date date, ExemptType exemptType) {
		return enableSii(ctx, domainId, GIPUZKOA, getSiiData(date, exemptType, true));
	}
	public static InvoiceCommunicationConfiguration enableSii(AONContext ctx, Integer domainId, Administration admon, Date date, ExemptType exemptType) {
		return enableSii(ctx, domainId, admon, getSiiData(date,exemptType));
	}
	private static InvoiceCommunicationConfiguration enableSii(AONContext ctx, Integer domainId, Administration admon, CommunicationData data) {
		return ICCEnablerDAO.enable(ctx, domainId, admon, data);
	}
	// -----------------------------------------------------
	// ---------------------------- [ disable No Verifactu ]
	// -----------------------------------------------------
	public static InvoiceCommunicationConfiguration disableSii(AONContext ctx, Integer domainId, Date atDate, Date endDate) {
		return ICCDisablerDAO.disable(ctx, domainId, atDate, ICC_SII , endDate);
	}
	
	
	// -----------------------------------------------------
	// -------------------------------------- [ Enable SIF ]
	// -----------------------------------------------------
 	private static CommunicationData getSifData(Date startDate) {
		return new CommunicationData()
			.setDataName(ICC_SIF)
			.setStartDate( startDate)
		;
	}
	public static InvoiceCommunicationConfiguration enableSif(AONContext ctx, Integer domainId, Administration admon, Date date) {
		return ICCEnablerDAO.enable(ctx, domainId, admon, getSifData(date));
	}
	
	// -----------------------------------------------------
	// ----------------------------------- [ Enable NO SIF ]
	// -----------------------------------------------------
 	private static CommunicationData getNoSifData(Date startDate) {
		return new CommunicationData()
			.setDataName(ICC_NO_SIF)
			.setStartDate( startDate)
		;
	}
	public static InvoiceCommunicationConfiguration enableNoSif(AONContext ctx, Integer domainId, Administration admon, Date date) {
		return ICCEnablerDAO.enable(ctx, domainId, admon, getNoSifData(date));
	}
	
}
