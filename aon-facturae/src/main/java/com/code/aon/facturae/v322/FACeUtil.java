package com.code.aon.facturae.v322;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.security.User;

public class FACeUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FACeUtil.class.getName());
	
	private static final String FACE_PREFFIX = "FACe_";
	
	private static final String CENTRE_CODE_SUFFIX = "_CENTRE_CODE";
	
	private static final String ADDRESS_SUFFIX = "_ADDRESS";

	private static final String DELIVERY_NUMBER_SUFFIX = "_DELIVERY_NUMBER";
	
	private static final String SEQUENCE_NUMBER_SUFFIX = "_SEQUENCE_NUMBER";

	private static final String ISSUER_CONTRACT_REFERENCE_SUFFIX = "_ISSUER_CONTRACT_REFERENCE";
	
	private static final String FISCAL = "FISCAL";
	
	private static final String RECEPTOR = "RECEPTOR";
	
	private static final String PAGADOR = "PAGADOR";
	
	private static final String COMPRADOR = "COMPRADOR";
	
	private static final String INVOICE = "INVOICE";
	
	public static final String FACE_ENABLED = FACE_PREFFIX + "ENABLED";
	
	public static final String FACE_FISCAL_CENTRE_CODE = FACE_PREFFIX + FISCAL + CENTRE_CODE_SUFFIX;
	
	public static final String FACE_FISCAL_ADDRESS = FACE_PREFFIX + FISCAL + ADDRESS_SUFFIX;
	
	public static final String FACE_FISCAL_ROLE_TYPE_CODE = "01";
	
	public static final String FACE_FISCAL_DESCRIPTION = "Oficina Contable";

	public static final String FACE_RECEPTOR_CENTRE_CODE = FACE_PREFFIX + RECEPTOR + CENTRE_CODE_SUFFIX;
	
	public static final String FACE_RECEPTOR_ADDRESS = FACE_PREFFIX + RECEPTOR + ADDRESS_SUFFIX;
	
	public static final String FACE_RECEPTOR_ROLE_TYPE_CODE = "02";
	
	public static final String FACE_RECEPTOR_DESCRIPTION = "Órgano Gestor";

	public static final String FACE_PAGADOR_CENTRE_CODE = FACE_PREFFIX + PAGADOR + CENTRE_CODE_SUFFIX;
	
	public static final String FACE_PAGADOR_ADDRESS = FACE_PREFFIX + PAGADOR + ADDRESS_SUFFIX;
	
	public static final String FACE_PAGADOR_ROLE_TYPE_CODE = "03";
	
	public static final String FACE_PAGADOR_DESCRIPTION = "Unidad Tramitadora";

	public static final String FACE_COMPRADOR_CENTRE_CODE = FACE_PREFFIX + COMPRADOR + CENTRE_CODE_SUFFIX;
	
	public static final String FACE_COMPRADOR_ADDRESS = FACE_PREFFIX + COMPRADOR + ADDRESS_SUFFIX;
	
	public static final String FACE_COMPRADOR_ROLE_TYPE_CODE = "04";
	
	public static final String FACE_COMPRADOR_DESCRIPTION = "Subdirección de compras";
	
	public static final String FACE_VENDEDOR_ROLE_TYPE_CODE = "06";
	
	public static final String FACE_VENDEDOR_DESCRIPTION = "Vendedor";

	public static final String FACE_INVOICE_DELIVERY_NUMBER = FACE_PREFFIX + INVOICE + DELIVERY_NUMBER_SUFFIX;
	
	public static final String FACE_INVOICE_SEQUENCE_NUMBER = FACE_PREFFIX + INVOICE + SEQUENCE_NUMBER_SUFFIX;

	public static final String FACE_INVOICE_ISSUER_CONTRACT_REFERENCE = FACE_PREFFIX + INVOICE + ISSUER_CONTRACT_REFERENCE_SUFFIX;
	
	public static final String[] FACE_CONSTANTS = {
		FACeUtil.FACE_ENABLED,
		FACeUtil.FACE_FISCAL_CENTRE_CODE, FACeUtil.FACE_FISCAL_ADDRESS,
		FACeUtil.FACE_RECEPTOR_CENTRE_CODE, FACeUtil.FACE_RECEPTOR_ADDRESS,
		FACeUtil.FACE_PAGADOR_CENTRE_CODE, FACeUtil.FACE_PAGADOR_ADDRESS,
		FACeUtil.FACE_COMPRADOR_CENTRE_CODE, FACeUtil.FACE_COMPRADOR_ADDRESS
	};	
	public static final String[] FACE_REQUIRED_CONSTANTS = {
		FACeUtil.FACE_INVOICE_DELIVERY_NUMBER, FACeUtil.FACE_INVOICE_SEQUENCE_NUMBER, 
		FACeUtil.FACE_INVOICE_ISSUER_CONTRACT_REFERENCE
	};	

	public static RegistryNote getRegistryNote(Domain domain, User user, String key, Integer registryId ) {
		return AON.getRegistryNote(domain, user.getLogin(), f -> f.getRegistryProperty().eq(registryId)
				.and(f.getNoteTypeProperty().eq(NoteType.FACTURAE.value()))
				.and(f.getDescriptionProperty().eq(key)));
    }
	
	public static boolean isDefined(Domain domain, User user, Invoice invoice) {
		if(invoice.isSales()) {
			RegistryNote rnote = AON.getRegistryNote(domain, user.getLogin(), f -> f.getRegistryProperty().eq(invoice.getRegistry())
					.and(f.getNoteTypeProperty().eq(NoteType.FACTURAE.value()))
					.and(f.getDescriptionProperty().eq(FACE_ENABLED))
					.and(f.getCommentsProperty().eq(Boolean.TRUE.toString().toLowerCase())));
			return rnote != null && rnote.getId() != null;
		}
		return false;
	}
	
	public static String getValue(Domain domain, User user, String key, Invoice invoice ) {
		RegistryNote note = getRegistryNote(domain, user, key, invoice.getRegistry());
		if ( note != null ) {
			return StringUtils.trimToNull(note.getComments());
		}
		return null;
	}	
	
}
