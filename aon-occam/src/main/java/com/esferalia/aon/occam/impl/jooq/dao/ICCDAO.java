package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_LROE;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_NO_SIF;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_NO_VERIFACTU;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_SIF;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_SII;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_TBAI;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_VERIFACTU;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.EnterpriseDataNames;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonObjectUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class ICCDAO {
	
	public static final String ICC_PREFIX = "ICC_%";
	
	private ICCDAO() {

	}
	
	private static void ensure(CommunicationData toEnsure, EnterpriseDataNames name) {
		// El objeto a asegurar no puede ser nulo y debe tener el dataName correcto
		if (toEnsure == null || toEnsure.getDataName() != name) {
			throw new AonCoreException("El m\u00E9todo a invocar requiere un CommunicationData con dataName " + name);
		}
	}
	
	// -----------------------------------------------
	// ---------------- [ TicketBai ] ----------------  
	// -----------------------------------------------
	
	public static InvoiceCommunicationConfiguration enableTbai(AONContext ctx, Integer domainId, CommunicationData toEnable) {
		ensure( toEnable, ICC_TBAI );
		return ICCEnablerDAO.enable(ctx, domainId, toEnable);
	}
	public static InvoiceCommunicationConfiguration disableTbai(AONContext ctx, Integer domainId, CommunicationData toDisable) {
		ensure( toDisable, ICC_TBAI );
		return ICCDisablerDAO.disable(ctx, domainId, toDisable);
	}
	public static InvoiceCommunicationConfiguration editTbai(AONContext ctx, Integer domainId, Administration administration, CommunicationData toChange) {
		ensure( toChange, ICC_TBAI );
		return edit(ctx, domainId, administration, toChange);
	}
	
	// -----------------------------------------------
	// ------------------ [ LROE ] -------------------  
	// -----------------------------------------------
	
	public static InvoiceCommunicationConfiguration enableLroe(AONContext ctx, Integer domainId, CommunicationData toEnable) {
		ensure( toEnable, ICC_LROE );
		return ICCEnablerDAO.enable(ctx, domainId, toEnable);
	}
	public static InvoiceCommunicationConfiguration disableLroe(AONContext ctx, Integer domainId, CommunicationData toDisable) {
		ensure( toDisable, ICC_LROE );
		return ICCDisablerDAO.disable(ctx, domainId, toDisable);
	}
	public static InvoiceCommunicationConfiguration editLroe(AONContext ctx, Integer domainId, Administration administration, CommunicationData toChange) {
		ensure( toChange, ICC_LROE );
		return edit(ctx, domainId, administration, toChange);
	}

	// -----------------------------------------------
	// ---------------- [ Verifactu ] ----------------  
	// -----------------------------------------------

	public static InvoiceCommunicationConfiguration enableVerifactu(AONContext ctx, Integer domainId, CommunicationData toEnable) {
		ensure( toEnable, ICC_VERIFACTU );
		return ICCEnablerDAO.enable(ctx, domainId, toEnable);
	}
	public static InvoiceCommunicationConfiguration disableVerifactu(AONContext ctx, Integer domainId, CommunicationData toDisable) {
		ensure( toDisable, ICC_VERIFACTU );
		return ICCDisablerDAO.disable(ctx, domainId, toDisable);
	}
	public static InvoiceCommunicationConfiguration editVerifactu(AONContext ctx, Integer domainId, Administration administration, CommunicationData toChange) {
		ensure( toChange, ICC_VERIFACTU );
		return edit(ctx, domainId, administration, toChange);
	}
	
	
	// -----------------------------------------------
	// --------------- [ No Verifactu ] --------------  
	// -----------------------------------------------
	
	public static InvoiceCommunicationConfiguration enableNoVerifactu(AONContext ctx, Integer domainId, CommunicationData toEnable) {
		ensure( toEnable, ICC_NO_VERIFACTU );
		return ICCEnablerDAO.enable(ctx, domainId, toEnable);
	}
	public static InvoiceCommunicationConfiguration disableNoVerifactu(AONContext ctx, Integer domainId, CommunicationData toDisable) {
		ensure( toDisable, ICC_NO_VERIFACTU );
		return ICCDisablerDAO.disable(ctx, domainId, toDisable);
	}
	public static InvoiceCommunicationConfiguration editNoVerifactu(AONContext ctx, Integer domainId, Administration administration, CommunicationData toChange) {
		ensure( toChange, ICC_NO_VERIFACTU );
		return edit(ctx, domainId, administration, toChange);
	}

	// -----------------------------------------------
	// ------------------- [ SII ] -------------------  
	// -----------------------------------------------
	
	public static InvoiceCommunicationConfiguration enableSii(AONContext ctx, Integer domainId, CommunicationData toEnable) {
		ensure( toEnable, ICC_SII );
		return ICCEnablerDAO.enable(ctx, domainId, toEnable);
	}
	public static InvoiceCommunicationConfiguration disableSii(AONContext ctx, Integer domainId, CommunicationData toDisable) {
		ensure( toDisable, ICC_SII );
		return ICCDisablerDAO.disable(ctx, domainId, toDisable);
	}
	public static InvoiceCommunicationConfiguration editSii(AONContext ctx, Integer domainId, Administration administration, CommunicationData toChange) {
		ensure( toChange, ICC_SII );
		return edit(ctx, domainId, administration, toChange);
	}
	
	// -----------------------------------------------
	// ------------------- [ SIF ] -------------------  
	// -----------------------------------------------
	
	public static InvoiceCommunicationConfiguration enableSif(AONContext ctx, Integer domainId, CommunicationData toEnable) {
		ensure( toEnable, ICC_SIF );
		return ICCEnablerDAO.enable(ctx, domainId, toEnable);
	}
	public static InvoiceCommunicationConfiguration disableSif(AONContext ctx, Integer domainId, CommunicationData toDisable) {
		ensure( toDisable, ICC_SIF );
		return ICCDisablerDAO.disable(ctx, domainId, toDisable);
	}
	public static InvoiceCommunicationConfiguration editSif(AONContext ctx, Integer domainId, Administration administration, CommunicationData toChange) {
		ensure( toChange, ICC_SIF );
		return edit(ctx, domainId, administration, toChange);
	}
	
	// -----------------------------------------------
	// ----------------- [ NO SIF ] ------------------  
	// -----------------------------------------------

	public static InvoiceCommunicationConfiguration enableNoSif(AONContext ctx, Integer domainId, CommunicationData toEnable) {
		ensure( toEnable, ICC_NO_SIF );
		return ICCEnablerDAO.enable(ctx, domainId, toEnable);
	}
	
	// -----------------------------------------------
	// -------------------------------------- [ EDIT ]   
	// -----------------------------------------------
	private static InvoiceCommunicationConfiguration edit(AONContext ctx, Integer domainId, Administration administration, CommunicationData toChange) {
		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, domainId);
		config.dataStream()
			.filter( d -> AonNumberUtils.equals(d.getId(), toChange.getId()) )
			.filter( d -> d.getDataName() == toChange.getDataName() )
			.findFirst()
			.map(d -> mergeData(d, toChange))
			.ifPresent(d -> InvoiceCommunicationDAO.save(ctx, domainId, config));
		return InvoiceCommunicationDAO.get(ctx, domainId);
	}

	private static CommunicationData mergeData(CommunicationData d, CommunicationData toChange) {
		if (AonDateUtils.isNotSameDay(toChange.getStartDate(), d.getStartDate())) {
			d.setStartDate(toChange.getStartDate());
		}
		if (AonDateUtils.isNotSameDay(toChange.getEndDate(), d.getEndDate())) {
			d.setEndDate(toChange.getEndDate());
		}
		if (AonObjectUtils.optionalEquals( toChange.getExemptType(), d.getExemptType()) ) {
			d.setExemptType(toChange.getExemptType().orElse(null));
		}
		if (AonObjectUtils.optionalEquals( toChange.getAdministration(), d.getAdministration()) ) {
			d.setAdministration(toChange.getAdministration().orElse(null));
		}
		if (d.isTest() != toChange.isTest()) {
			d.setTest(toChange.isTest());
		}
		return d;
	}
	
}