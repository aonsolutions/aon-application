package net.aonsolutions.aon.sii;

import java.io.IOException;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;

import net.aonsolutions.aon.sii.aeat.BienesInversion;
import net.aonsolutions.aon.sii.aeat.FacturasEmitidas;
import net.aonsolutions.aon.sii.aeat.FacturasRecibidas;
import net.aonsolutions.aon.sii.aeat.OperacionesIntracomunitarias;
import net.aonsolutions.aon.sii.aeat.SIIAeatPost;
import net.aonsolutions.aon.sii.araba.SIIArabaPost;
import net.aonsolutions.aon.sii.bizkaia.SIIBizkaiaPost;
import net.aonsolutions.aon.sii.gipuzkoa.SIIGipuzkoaPost;

public class SIIManager {
	private SiiConfiguration siiConfiguration;
	
	public SIIManager(SiiConfiguration siiConfiguration) {
		this.siiConfiguration = siiConfiguration;
	}
	
	public static SIIManager getInstance(SiiConfiguration siiConfiguration) {
		return new SIIManager(siiConfiguration);
	}
	
	public SiiConfiguration getSiiConfiguration() {
		return siiConfiguration;
	}
	
	public void setSiiConfiguration(SiiConfiguration siiConfiguration) {
		this.siiConfiguration = siiConfiguration;
	}

	// -------------------- FACTURAS EMITIDAS

	protected byte[] getSuministroFacturasEmitidas(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) {
    	LinkedList<VatContext> modList = contextList.stream().filter(v->  "Correcto".equals(v.getSiiStatus())
    			|| "AceptadoConErrores".equals(v.getSiiStatus())
    			|| "Anulada".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

    	LinkedList<VatContext> newList = contextList.stream().filter(v-> "Pendiente".equals(v.getSiiStatus())
    			|| "Incorrecto".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

    	if(getSiiConfiguration().isAraba()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.araba.FacturasEmitidas.getInstance().getSuministroFacturasEmitidas(domain, login, company, invoiceId, newList, SendType.ALTA_EMITIDAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.araba.FacturasEmitidas.getInstance().getSuministroFacturasEmitidas(domain, login, company, invoiceId, newList, SendType.MOD_EMITIDAS.isModificacion(), terceros);
			}
		} else if(getSiiConfiguration().isGipuzkoa()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.gipuzkoa.FacturasEmitidas.getInstance().getSuministroFacturasEmitidas(domain, login, company, invoiceId, newList, SendType.ALTA_EMITIDAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.gipuzkoa.FacturasEmitidas.getInstance().getSuministroFacturasEmitidas(domain, login, company, invoiceId, newList, SendType.MOD_EMITIDAS.isModificacion(), terceros);
			}
		} else if(getSiiConfiguration().isBizkaia()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.bizkaia.FacturasEmitidas.getInstance().getSuministroFacturasEmitidas(domain, login, company, invoiceId, newList, SendType.ALTA_EMITIDAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.bizkaia.FacturasEmitidas.getInstance().getSuministroFacturasEmitidas(domain, login, company, invoiceId, newList, SendType.MOD_EMITIDAS.isModificacion(), terceros);
			}
		} else {
			if(newList.size() > 0){
				return FacturasEmitidas.getInstance().getSuministroFacturasEmitidas(domain, login, company, invoiceId, newList, SendType.ALTA_EMITIDAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return FacturasEmitidas.getInstance().getSuministroFacturasEmitidas(domain, login, company, invoiceId, newList, SendType.MOD_EMITIDAS.isModificacion(), terceros);
			}
		}
		return null;
	}
	
	protected JSONArray suministroFacturasEmitidas(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getSiiConfiguration(), SIIType.FACTURAS_EMITIDAS);
    	LinkedList<VatContext> modList = contextList.stream().filter(v->  "Correcto".equals(v.getSiiStatus())
    			|| "AceptadoConErrores".equals(v.getSiiStatus())
    			|| "Anulada".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

    	LinkedList<VatContext> newList = contextList.stream().filter(v-> "Pendiente".equals(v.getSiiStatus())
    			|| "Incorrecto".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		if(getSiiConfiguration().isAraba()) {
			if(newList.size() > 0){
				return SIIArabaPost.getInstance(getSiiConfiguration()).suministroFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_EMITIDAS);
			}

			if(modList.size() > 0){
				return SIIArabaPost.getInstance(getSiiConfiguration()).suministroFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_EMITIDAS);
			}
		} else if(getSiiConfiguration().isGipuzkoa()) {
			if(newList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).suministroFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_EMITIDAS);
			}

			if(modList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).suministroFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_EMITIDAS);
			}
		} else if(getSiiConfiguration().isBizkaia()) {
			if(newList.size() > 0){
				return SIIBizkaiaPost.getInstance(getSiiConfiguration()).suministroFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_EMITIDAS);
			}

			if(modList.size() > 0){
				return SIIBizkaiaPost.getInstance(getSiiConfiguration()).suministroFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_EMITIDAS);
			}
		} else { //if(getSiiConfiguration().isCommonTerritory() || getSiiConfiguration().isNafarroa()) {
			if(newList.size() > 0){
				return SIIAeatPost.getInstance(getSiiConfiguration()).suministroFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_EMITIDAS);
			}

			if(modList.size() > 0){
				return SIIAeatPost.getInstance(getSiiConfiguration()).suministroFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_EMITIDAS);
			}
		}
		return new JSONArray();
	}
	
	protected JSONArray bajaFacturasEmitidas(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getSiiConfiguration(), SIIType.FACTURAS_EMITIDAS);
		
		if(getSiiConfiguration().isAraba()) {
    		return SIIArabaPost.getInstance(getSiiConfiguration()).bajaFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isGipuzkoa()) {
    		return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).bajaFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isBizkaia()) {
    		return SIIBizkaiaPost.getInstance(getSiiConfiguration()).bajaFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getSiiConfiguration()).bajaFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri);
	}
	
	protected JSONArray suministroFacturasEmitidasCobros(Domain domain, String login, Company company, LinkedList<Finance> financeList, Integer invoiceId) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getSiiConfiguration(), SIIType.FACTURAS_EMITIDAS_COBROS);

		if(getSiiConfiguration().isAraba()) {
    		return SIIArabaPost.getInstance(getSiiConfiguration()).suministroFacturasEmitidasCobros(domain, login, company, financeList, invoiceId, uri);
    	} else if(getSiiConfiguration().isGipuzkoa()) {
    		return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).suministroFacturasEmitidasCobros(domain, login, company, financeList, invoiceId, uri);
    	} else if(getSiiConfiguration().isBizkaia()) {
    		return SIIBizkaiaPost.getInstance(getSiiConfiguration()).suministroFacturasEmitidasCobros(domain, login, company, financeList, invoiceId, uri);
    	} else return SIIAeatPost.getInstance(getSiiConfiguration()).suministroFacturasEmitidasCobros(domain, login, company, financeList, invoiceId, uri);
    }

	 // -------------------- FACTURAS RECIBIDAS
	
	protected byte[] getSuministroFacturasRecibidas(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, boolean errorPeriodo) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
    	LinkedList<VatContext> modList = contextList.stream().filter(v-> v.getSiiStatus().equals("Correcto")
    			|| v.getSiiStatus().equals("AceptadoConErrores")
    			|| v.getSiiStatus().equals("Anulada")).collect(Collectors.toCollection(LinkedList::new));

    	LinkedList<VatContext> newList = contextList.stream().filter(v-> v.getSiiStatus().equals("Pendiente")
    			|| v.getSiiStatus().equals("Incorrecto")).collect(Collectors.toCollection(LinkedList::new));

		if(getSiiConfiguration().isAraba()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.araba.FacturasRecibidas.getInstance().getSuministroFacturasRecibidas(domain, login, company, invoiceId, newList, SendType.ALTA_RECIBIDAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.araba.FacturasRecibidas.getInstance().getSuministroFacturasRecibidas(domain, login, company, invoiceId, newList, SendType.MOD_RECIBIDAS.isModificacion(), terceros);
			}
    	} else if(getSiiConfiguration().isGipuzkoa()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.gipuzkoa.FacturasRecibidas.getInstance().getSuministroFacturasRecibidas(domain, login, company, invoiceId, newList, SendType.ALTA_RECIBIDAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.gipuzkoa.FacturasRecibidas.getInstance().getSuministroFacturasRecibidas(domain, login, company, invoiceId, newList, SendType.MOD_RECIBIDAS.isModificacion(), terceros);
			}
    	} else if(getSiiConfiguration().isBizkaia()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.bizkaia.FacturasRecibidas.getInstance().getSuministroFacturasRecibidas(domain, login, company, invoiceId, newList, SendType.ALTA_RECIBIDAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.bizkaia.FacturasRecibidas.getInstance().getSuministroFacturasRecibidas(domain, login, company, invoiceId, newList, SendType.MOD_RECIBIDAS.isModificacion(), terceros);
			}
		} else {
    		if(newList.size() > 0){
				return FacturasRecibidas.getInstance().getSuministroFacturasRecibidas(domain, login, company, invoiceId, newList, SendType.ALTA_RECIBIDAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return FacturasRecibidas.getInstance().getSuministroFacturasRecibidas(domain, login, company, invoiceId, modList, SendType.MOD_RECIBIDAS.isModificacion(), terceros);
			}
		}
    	return null;
    }
	
	protected JSONArray suministroFacturasRecibidas(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, boolean errorPeriodo) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getSiiConfiguration(), SIIType.FACTURAS_RECIBIDAS);

    	LinkedList<VatContext> modList = contextList.stream().filter(v-> v.getSiiStatus().equals("Correcto")
    			|| v.getSiiStatus().equals("AceptadoConErrores")
    			|| v.getSiiStatus().equals("Anulada")).collect(Collectors.toCollection(LinkedList::new));

    	LinkedList<VatContext> newList = contextList.stream().filter(v-> v.getSiiStatus().equals("Pendiente")
    			|| v.getSiiStatus().equals("Incorrecto")).collect(Collectors.toCollection(LinkedList::new));

		if(getSiiConfiguration().isAraba()) {
			if(newList.size() > 0){
				return SIIArabaPost.getInstance(getSiiConfiguration()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_RECIBIDAS, false);
			}

			if(modList.size() > 0){
				return SIIArabaPost.getInstance(getSiiConfiguration()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_RECIBIDAS, errorPeriodo);
			}
    	} else if(getSiiConfiguration().isGipuzkoa()) {
			if(newList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_RECIBIDAS, false);
			}

			if(modList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_RECIBIDAS, errorPeriodo);
			}
    	} else if(getSiiConfiguration().isBizkaia()) {
			if(newList.size() > 0){
				return SIIBizkaiaPost.getInstance(getSiiConfiguration()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_RECIBIDAS, false);
			}

			if(modList.size() > 0){
				return SIIBizkaiaPost.getInstance(getSiiConfiguration()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_RECIBIDAS, errorPeriodo);
			}
		} else {
			if(newList.size() > 0){
				return SIIAeatPost.getInstance(getSiiConfiguration()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_RECIBIDAS, false);
			}

			if(modList.size() > 0){
				return SIIAeatPost.getInstance(getSiiConfiguration()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_RECIBIDAS, errorPeriodo);
			}
		}
    	return new JSONArray();
    }
	
	protected JSONArray bajaFacturasRecibidas(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getSiiConfiguration(), SIIType.FACTURAS_RECIBIDAS);

		if(getSiiConfiguration().isAraba()) {
    		return SIIArabaPost.getInstance(getSiiConfiguration()).bajaFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isGipuzkoa()) {
    		return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).bajaFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isBizkaia()) {
    		return SIIBizkaiaPost.getInstance(getSiiConfiguration()).bajaFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getSiiConfiguration()).bajaFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri);
	}
	
	protected JSONArray suministroFacturasRecibidasPagos(Domain domain, String login, Company company, LinkedList<Finance> financeList, Integer invoiceId) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getSiiConfiguration(), SIIType.FACTURAS_RECIBIDAS_PAGOS);

		if(getSiiConfiguration().isAraba()) {
    		return SIIArabaPost.getInstance(getSiiConfiguration()).suministroFacturasRecibidasPagos(domain, login, company, financeList, invoiceId, uri);
    	} else if(getSiiConfiguration().isGipuzkoa()) {
    		return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).suministroFacturasRecibidasPagos(domain, login, company, financeList, invoiceId, uri);
    	} else if(getSiiConfiguration().isBizkaia()) {
    		return SIIBizkaiaPost.getInstance(getSiiConfiguration()).suministroFacturasRecibidasPagos(domain, login, company, financeList, invoiceId, uri);
    	} else return SIIAeatPost.getInstance(getSiiConfiguration()).suministroFacturasRecibidasPagos(domain, login, company, financeList, invoiceId, uri);
    }
	
	 // -------------------- BIENES DE INVERSIÓN 
	
	protected byte[] getSuministroBienesInversion(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		LinkedList<VatContext> modList = contextList.stream().filter(v->  "Correcto".equals(v.getSiiStatus())
				|| "AceptadoConErrores".equals(v.getSiiStatus())
				|| "Anulada".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		LinkedList<VatContext> newList = contextList.stream().filter(v-> "Pendiente".equals(v.getSiiStatus())
				|| "Incorrecto".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		if(getSiiConfiguration().isAraba()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.araba.BienesInversion.getInstance().getSuministroBienesInversion(domain, login, company, invoiceId, contextList, SendType.ALTA_INVERSION.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.araba.BienesInversion.getInstance().getSuministroBienesInversion(domain, login, company, invoiceId, contextList, SendType.MOD_INVERSION.isModificacion(), terceros);
			}
    	} else if(getSiiConfiguration().isGipuzkoa()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.gipuzkoa.BienesInversion.getInstance().getSuministroBienesInversion(domain, login, company, invoiceId, contextList, SendType.ALTA_INVERSION.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.gipuzkoa.BienesInversion.getInstance().getSuministroBienesInversion(domain, login, company, invoiceId, contextList, SendType.MOD_INVERSION.isModificacion(), terceros);
			}
    	} else if(getSiiConfiguration().isBizkaia()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.bizkaia.BienesInversion.getInstance().getSuministroBienesInversion(domain, login, company, invoiceId, contextList, SendType.ALTA_INVERSION.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.bizkaia.BienesInversion.getInstance().getSuministroBienesInversion(domain, login, company, invoiceId, contextList, SendType.MOD_INVERSION.isModificacion(), terceros);
			}
		} else {
			if(newList.size() > 0){
				return BienesInversion.getInstance().getSuministroBienesInversion(domain, login, company, invoiceId, contextList, SendType.ALTA_INVERSION.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return BienesInversion.getInstance().getSuministroBienesInversion(domain, login, company, invoiceId, contextList, SendType.MOD_INVERSION.isModificacion(), terceros);
			}
		}
		return null;
	}
	
	protected JSONArray suministroBienesInversion(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getSiiConfiguration(), SIIType.BIENES_INVERSION);

		LinkedList<VatContext> modList = contextList.stream().filter(v->  "Correcto".equals(v.getSiiStatus())
				|| "AceptadoConErrores".equals(v.getSiiStatus())
				|| "Anulada".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		LinkedList<VatContext> newList = contextList.stream().filter(v-> "Pendiente".equals(v.getSiiStatus())
				|| "Incorrecto".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));


		if(getSiiConfiguration().isAraba()) {
			if(newList.size() > 0){
				return SIIArabaPost.getInstance(getSiiConfiguration()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.ALTA_INVERSION);
			}

			if(modList.size() > 0){
				return SIIArabaPost.getInstance(getSiiConfiguration()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.MOD_INVERSION);
			}
    	} else if(getSiiConfiguration().isGipuzkoa()) {
			if(newList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.ALTA_INVERSION);
			}

			if(modList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.MOD_INVERSION);
			}
    	} else if(getSiiConfiguration().isBizkaia()) {
			if(newList.size() > 0){
				return SIIBizkaiaPost.getInstance(getSiiConfiguration()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.ALTA_INVERSION);
			}

			if(modList.size() > 0){
				return SIIBizkaiaPost.getInstance(getSiiConfiguration()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.MOD_INVERSION);
			}
		} else {
			if(newList.size() > 0){
				return SIIAeatPost.getInstance(getSiiConfiguration()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.ALTA_INVERSION);
			}

			if(modList.size() > 0){
				return SIIAeatPost.getInstance(getSiiConfiguration()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.MOD_INVERSION);
			}
		}
		return new JSONArray();
	}

	protected JSONArray bajaBienesInversion(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getSiiConfiguration(), SIIType.BIENES_INVERSION);
		
		if(getSiiConfiguration().isAraba()) {
    		return SIIArabaPost.getInstance(getSiiConfiguration()).bajaBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isGipuzkoa()) {
    		return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).bajaBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isBizkaia()) {
    		return SIIBizkaiaPost.getInstance(getSiiConfiguration()).bajaBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getSiiConfiguration()).bajaBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri);
	}

	 // -------------------- OPERACIONES INTRACOMUNITARIAS

	protected byte[] getSuministroOperacionesIntracomunitarias(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String tipoOp, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		LinkedList<VatContext> modList = contextList.stream().filter(v->  "Correcto".equals(v.getSiiStatus())
				|| "AceptadoConErrores".equals(v.getSiiStatus())
				|| "Anulada".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		LinkedList<VatContext> newList = contextList.stream().filter(v-> "Pendiente".equals(v.getSiiStatus())
				|| "Incorrecto".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		if(getSiiConfiguration().isAraba()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.araba.OperacionesIntracomunitarias.getInstance().getSuministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, SendType.ALTA_INTRACOMUNITARIAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.araba.OperacionesIntracomunitarias.getInstance().getSuministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, SendType.MOD_INTRACOMUNITARIAS.isModificacion(), terceros);			}
    	} else if(getSiiConfiguration().isGipuzkoa()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.gipuzkoa.OperacionesIntracomunitarias.getInstance().getSuministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, SendType.ALTA_INTRACOMUNITARIAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.gipuzkoa.OperacionesIntracomunitarias.getInstance().getSuministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, SendType.MOD_INTRACOMUNITARIAS.isModificacion(), terceros);			}
    	} else if(getSiiConfiguration().isBizkaia()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.bizkaia.OperacionesIntracomunitarias.getInstance().getSuministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, SendType.ALTA_INTRACOMUNITARIAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.bizkaia.OperacionesIntracomunitarias.getInstance().getSuministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, SendType.MOD_INTRACOMUNITARIAS.isModificacion(), terceros);
			}
		} else {
			if(newList.size() > 0){
				return OperacionesIntracomunitarias.getInstance().getSuministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, SendType.ALTA_INTRACOMUNITARIAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return OperacionesIntracomunitarias.getInstance().getSuministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, SendType.MOD_INTRACOMUNITARIAS.isModificacion(), terceros);
			}
		}
		return null;
	}
		
	
	protected JSONArray suministroOperacionesIntracomunitarias(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String tipoOp, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getSiiConfiguration(), SIIType.OPERACIONES_INTRACOMUNITARIAS);

		LinkedList<VatContext> modList = contextList.stream().filter(v->  "Correcto".equals(v.getSiiStatus())
				|| "AceptadoConErrores".equals(v.getSiiStatus())
				|| "Anulada".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		LinkedList<VatContext> newList = contextList.stream().filter(v-> "Pendiente".equals(v.getSiiStatus())
				|| "Incorrecto".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		if(getSiiConfiguration().isAraba()) {
			if(newList.size() > 0){
				return SIIArabaPost.getInstance(getSiiConfiguration()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.ALTA_INTRACOMUNITARIAS);
			}

			if(modList.size() > 0){
				return SIIArabaPost.getInstance(getSiiConfiguration()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.MOD_INTRACOMUNITARIAS);
			}
    	} else if(getSiiConfiguration().isGipuzkoa()) {
			if(newList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.ALTA_INTRACOMUNITARIAS);
			}

			if(modList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.MOD_INTRACOMUNITARIAS);
			}
    	} else if(getSiiConfiguration().isBizkaia()) {
			if(newList.size() > 0){
				return SIIBizkaiaPost.getInstance(getSiiConfiguration()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.ALTA_INTRACOMUNITARIAS);
			}

			if(modList.size() > 0){
				return SIIBizkaiaPost.getInstance(getSiiConfiguration()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.MOD_INTRACOMUNITARIAS);
			}
		} else {
			if(newList.size() > 0){
				return SIIAeatPost.getInstance(getSiiConfiguration()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.ALTA_INTRACOMUNITARIAS);
			}

			if(modList.size() > 0){
				return SIIAeatPost.getInstance(getSiiConfiguration()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.MOD_INTRACOMUNITARIAS);
			}
		}
		return new JSONArray();
	}
		
	protected JSONArray bajaOperacionesIntracomunitarias(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getSiiConfiguration(), SIIType.OPERACIONES_INTRACOMUNITARIAS);

		if(getSiiConfiguration().isAraba()) {
    		return SIIArabaPost.getInstance(getSiiConfiguration()).bajaOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isGipuzkoa()) {
    		return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).bajaOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isBizkaia()) {
    		return SIIBizkaiaPost.getInstance(getSiiConfiguration()).bajaOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getSiiConfiguration()).bajaOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, terceros, uri);
	}

 // -------------------- COBROS METALICO
	
    protected JSONObject suministroCobrosMetalico(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getSiiConfiguration(), SIIType.COBROS_METALICO);

		if(getSiiConfiguration().isAraba()) {
			return SIIArabaPost.getInstance(getSiiConfiguration()).suministroCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isGipuzkoa()) {
			return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).suministroCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isBizkaia()) {
			return SIIBizkaiaPost.getInstance(getSiiConfiguration()).suministroCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getSiiConfiguration()).suministroCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    }

    protected JSONObject bajaCobrosMetalico(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) {
		String uri = SIIUri.getInstance().getURI(getSiiConfiguration(), SIIType.COBROS_METALICO);

		if(getSiiConfiguration().isAraba()) {
			return SIIArabaPost.getInstance(getSiiConfiguration()).bajaCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isGipuzkoa()) {
			return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).bajaCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isBizkaia()) {
			return SIIBizkaiaPost.getInstance(getSiiConfiguration()).bajaCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getSiiConfiguration()).bajaCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    }

    // -------------------- OPERACIONES SEGUROS
	
    protected JSONObject suministroOperacionesSeguros(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getSiiConfiguration(), SIIType.OPERACIONES_SEGUROS);

		if(getSiiConfiguration().isAraba()) {
			return SIIArabaPost.getInstance(getSiiConfiguration()).suministroOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isGipuzkoa()) {
			return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).suministroOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isBizkaia()) {
			return SIIBizkaiaPost.getInstance(getSiiConfiguration()).suministroOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getSiiConfiguration()).suministroOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    }

    protected JSONObject bajaOperacionesSeguros(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) {
		String uri = SIIUri.getInstance().getURI(getSiiConfiguration(), SIIType.OPERACIONES_SEGUROS);

		if(getSiiConfiguration().isAraba()) {
			return SIIArabaPost.getInstance(getSiiConfiguration()).bajaOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isGipuzkoa()) {
			return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).bajaOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isBizkaia()) {
			return SIIBizkaiaPost.getInstance(getSiiConfiguration()).bajaOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getSiiConfiguration()).bajaOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);

    }

    // -------------------- AGENCIAS VIAJES
	
    protected JSONObject suministroAgenciasViajes(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getSiiConfiguration(), SIIType.AGENCIAS_VIAJES);

		if(getSiiConfiguration().isAraba()) {
			return SIIArabaPost.getInstance(getSiiConfiguration()).suministroAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isGipuzkoa()) {
			return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).suministroAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isBizkaia()) {
			return SIIBizkaiaPost.getInstance(getSiiConfiguration()).suministroAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getSiiConfiguration()).suministroAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    }

    protected JSONObject bajaAgenciasViajes(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) {
		String uri = SIIUri.getInstance().getURI(getSiiConfiguration(), SIIType.AGENCIAS_VIAJES);

		if(getSiiConfiguration().isAraba()) {
			return SIIArabaPost.getInstance(getSiiConfiguration()).bajaAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isGipuzkoa()) {
			return SIIGipuzkoaPost.getInstance(getSiiConfiguration()).bajaAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getSiiConfiguration().isBizkaia()) {
			return SIIBizkaiaPost.getInstance(getSiiConfiguration()).bajaAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getSiiConfiguration()).bajaAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    }

}
