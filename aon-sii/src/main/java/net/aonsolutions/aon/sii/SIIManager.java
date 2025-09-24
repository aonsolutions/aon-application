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
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;

import net.aonsolutions.aon.sii.aeat.BienesInversion;
import net.aonsolutions.aon.sii.aeat.FacturasEmitidas;
import net.aonsolutions.aon.sii.aeat.FacturasRecibidas;
import net.aonsolutions.aon.sii.aeat.OperacionesIntracomunitarias;
import net.aonsolutions.aon.sii.aeat.SIIAeatPost;
import net.aonsolutions.aon.sii.araba.SIIArabaPost;
import net.aonsolutions.aon.sii.bizkaia.SIIBizkaiaPost;
import net.aonsolutions.aon.sii.gipuzkoa.SIIGipuzkoaPost;

public class SIIManager {
	private InvoiceCommunicationConfiguration icc;
	
	public SIIManager(InvoiceCommunicationConfiguration icc) {
		this.icc = icc;
	}
	
	public static SIIManager getInstance(InvoiceCommunicationConfiguration icc) {
		return new SIIManager(icc);
	}
	
	public InvoiceCommunicationConfiguration getConfiguration() {
		return icc;
	}
	public void setSiiConfiguration(InvoiceCommunicationConfiguration icc) {
		this.icc = icc;
	}

	// -------------------- FACTURAS 
	
	public JSONArray suministroFacturas(Domain domain, String login, Company company, Invoice invoice, LinkedList<VatContext> contextList, String terceros) throws Exception {
		return invoice.isSales() 
			? suministroFacturasEmitidas(domain, login, company, invoice, contextList, terceros)
			: suministroFacturasRecibidas(domain, login, company, invoice.getId(), contextList, terceros, false);
	}
	
	public JSONArray bajaFacturas(Domain domain, String login, Company company, Invoice invoice, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		return invoice.isSales() 
				? bajaFacturasEmitidas(domain, login, company, invoice.getId(), contextList, terceros)
				: bajaFacturasRecibidas(domain, login, company, invoice.getId(), contextList, terceros);
	}

	// -------------------- FACTURAS EMITIDAS	

	protected byte[] getSuministroFacturasEmitidas(Domain domain, String login, Company company, Invoice invoice, LinkedList<VatContext> contextList, String terceros) {		
		LinkedList<VatContext> modList = contextList.stream().filter(v->  "Correcto".equals(v.getSiiStatus())
    			|| "AceptadoConErrores".equals(v.getSiiStatus())
    			|| "Anulada".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

    	LinkedList<VatContext> newList = contextList.stream().filter(v-> "Pendiente".equals(v.getSiiStatus())
    			|| "Incorrecto".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));
    	
    	if(getConfiguration().isAraba()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.araba.FacturasEmitidas.getInstance().getSuministroFacturasEmitidas(domain, login, company, invoice.getId(), newList, SendType.ALTA_EMITIDAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.araba.FacturasEmitidas.getInstance().getSuministroFacturasEmitidas(domain, login, company, invoice.getId(), newList, SendType.MOD_EMITIDAS.isModificacion(), terceros);
			}
		} else if(getConfiguration().isGipuzkoa()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.gipuzkoa.FacturasEmitidas.getInstance().getSuministroFacturasEmitidas(domain, login, company, invoice.getId(), newList, SendType.ALTA_EMITIDAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.gipuzkoa.FacturasEmitidas.getInstance().getSuministroFacturasEmitidas(domain, login, company, invoice.getId(), newList, SendType.MOD_EMITIDAS.isModificacion(), terceros);
			}
		} else if(getConfiguration().isBizkaia()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.bizkaia.FacturasEmitidas.getInstance().getSuministroFacturasEmitidas(domain, login, company, invoice.getId(), newList, SendType.ALTA_EMITIDAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.bizkaia.FacturasEmitidas.getInstance().getSuministroFacturasEmitidas(domain, login, company, invoice.getId(), newList, SendType.MOD_EMITIDAS.isModificacion(), terceros);
			}
		} else {
			if(newList.size() > 0){
				return FacturasEmitidas.getInstance().getSuministroFacturasEmitidas(domain, login, company, invoice, newList, SendType.ALTA_EMITIDAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return FacturasEmitidas.getInstance().getSuministroFacturasEmitidas(domain, login, company, invoice, newList, SendType.MOD_EMITIDAS.isModificacion(), terceros);
			}
		}
		return null;
	}
	
	protected JSONArray suministroFacturasEmitidas(Domain domain, String login, Company company, Invoice invoice, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getConfiguration(), SIIType.FACTURAS_EMITIDAS);
    	LinkedList<VatContext> modList = contextList.stream().filter(v->  "Correcto".equals(v.getSiiStatus())
    			|| "AceptadoConErrores".equals(v.getSiiStatus())
    			|| "Anulada".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

    	LinkedList<VatContext> newList = contextList.stream().filter(v-> "Pendiente".equals(v.getSiiStatus())
    			|| "Incorrecto".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		if(getConfiguration().isAraba()) {
			if(newList.size() > 0){
				return SIIArabaPost.getInstance(getConfiguration()).suministroFacturasEmitidas(domain, login, company, invoice.getId(), contextList, terceros, uri, newList, SendType.ALTA_EMITIDAS);
			}

			if(modList.size() > 0){
				return SIIArabaPost.getInstance(getConfiguration()).suministroFacturasEmitidas(domain, login, company, invoice.getId(), contextList, terceros, uri, modList, SendType.MOD_EMITIDAS);
			}
		} else if(getConfiguration().isGipuzkoa()) {
			if(newList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getConfiguration()).suministroFacturasEmitidas(domain, login, company, invoice.getId(), contextList, terceros, uri, newList, SendType.ALTA_EMITIDAS);
			}

			if(modList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getConfiguration()).suministroFacturasEmitidas(domain, login, company, invoice.getId(), contextList, terceros, uri, modList, SendType.MOD_EMITIDAS);
			}
		} else if(getConfiguration().isBizkaia()) {
			if(newList.size() > 0){
				return SIIBizkaiaPost.getInstance(getConfiguration()).suministroFacturasEmitidas(domain, login, company, invoice.getId(), contextList, terceros, uri, newList, SendType.ALTA_EMITIDAS);
			}

			if(modList.size() > 0){
				return SIIBizkaiaPost.getInstance(getConfiguration()).suministroFacturasEmitidas(domain, login, company, invoice.getId(), contextList, terceros, uri, modList, SendType.MOD_EMITIDAS);
			}
		} else { //if(getConfiguration().isCommonTerritory() || getConfiguration().isNafarroa()) {
			if(newList.size() > 0){
				return SIIAeatPost.getInstance(getConfiguration()).suministroFacturasEmitidas(domain, login, company, invoice, contextList, terceros, uri, newList, SendType.ALTA_EMITIDAS);
			}

			if(modList.size() > 0){
				return SIIAeatPost.getInstance(getConfiguration()).suministroFacturasEmitidas(domain, login, company, invoice, contextList, terceros, uri, modList, SendType.MOD_EMITIDAS);
			}
		}
		return new JSONArray();
	}
	
	public JSONArray bajaFacturasEmitidas(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getConfiguration(), SIIType.FACTURAS_EMITIDAS);
		
		if(getConfiguration().isAraba()) {
    		return SIIArabaPost.getInstance(getConfiguration()).bajaFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isGipuzkoa()) {
    		return SIIGipuzkoaPost.getInstance(getConfiguration()).bajaFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isBizkaia()) {
    		return SIIBizkaiaPost.getInstance(getConfiguration()).bajaFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getConfiguration()).bajaFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri);
	}
	
	protected JSONArray suministroFacturasEmitidasCobros(Domain domain, String login, Company company, LinkedList<Finance> financeList, Integer invoiceId) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getConfiguration(), SIIType.FACTURAS_EMITIDAS_COBROS);

		if(getConfiguration().isAraba()) {
    		return SIIArabaPost.getInstance(getConfiguration()).suministroFacturasEmitidasCobros(domain, login, company, financeList, invoiceId, uri);
    	} else if(getConfiguration().isGipuzkoa()) {
    		return SIIGipuzkoaPost.getInstance(getConfiguration()).suministroFacturasEmitidasCobros(domain, login, company, financeList, invoiceId, uri);
    	} else if(getConfiguration().isBizkaia()) {
    		return SIIBizkaiaPost.getInstance(getConfiguration()).suministroFacturasEmitidasCobros(domain, login, company, financeList, invoiceId, uri);
    	} else return SIIAeatPost.getInstance(getConfiguration()).suministroFacturasEmitidasCobros(domain, login, company, financeList, invoiceId, uri);
    }

	 // -------------------- FACTURAS RECIBIDAS
	
	protected byte[] getSuministroFacturasRecibidas(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, boolean errorPeriodo) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
    	LinkedList<VatContext> modList = contextList.stream().filter(v-> v.getSiiStatus().equals("Correcto")
    			|| v.getSiiStatus().equals("AceptadoConErrores")
    			|| v.getSiiStatus().equals("Anulada")).collect(Collectors.toCollection(LinkedList::new));

    	LinkedList<VatContext> newList = contextList.stream().filter(v-> v.getSiiStatus().equals("Pendiente")
    			|| v.getSiiStatus().equals("Incorrecto")).collect(Collectors.toCollection(LinkedList::new));

		if(getConfiguration().isAraba()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.araba.FacturasRecibidas.getInstance().getSuministroFacturasRecibidas(domain, login, company, invoiceId, newList, SendType.ALTA_RECIBIDAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.araba.FacturasRecibidas.getInstance().getSuministroFacturasRecibidas(domain, login, company, invoiceId, newList, SendType.MOD_RECIBIDAS.isModificacion(), terceros);
			}
    	} else if(getConfiguration().isGipuzkoa()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.gipuzkoa.FacturasRecibidas.getInstance().getSuministroFacturasRecibidas(domain, login, company, invoiceId, newList, SendType.ALTA_RECIBIDAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.gipuzkoa.FacturasRecibidas.getInstance().getSuministroFacturasRecibidas(domain, login, company, invoiceId, newList, SendType.MOD_RECIBIDAS.isModificacion(), terceros);
			}
    	} else if(getConfiguration().isBizkaia()) {
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
		String uri = SIIUri.getInstance().getURI(getConfiguration(), SIIType.FACTURAS_RECIBIDAS);

    	LinkedList<VatContext> modList = contextList.stream().filter(v-> v.getSiiStatus().equals("Correcto")
    			|| v.getSiiStatus().equals("AceptadoConErrores")
    			|| v.getSiiStatus().equals("Anulada")).collect(Collectors.toCollection(LinkedList::new));

    	LinkedList<VatContext> newList = contextList.stream().filter(v-> v.getSiiStatus().equals("Pendiente")
    			|| v.getSiiStatus().equals("Incorrecto")).collect(Collectors.toCollection(LinkedList::new));

		if(getConfiguration().isAraba()) {
			if(newList.size() > 0){
				return SIIArabaPost.getInstance(getConfiguration()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_RECIBIDAS, false);
			}

			if(modList.size() > 0){
				return SIIArabaPost.getInstance(getConfiguration()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_RECIBIDAS, errorPeriodo);
			}
    	} else if(getConfiguration().isGipuzkoa()) {
			if(newList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getConfiguration()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_RECIBIDAS, false);
			}

			if(modList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getConfiguration()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_RECIBIDAS, errorPeriodo);
			}
    	} else if(getConfiguration().isBizkaia()) {
			if(newList.size() > 0){
				return SIIBizkaiaPost.getInstance(getConfiguration()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_RECIBIDAS, false);
			}

			if(modList.size() > 0){
				return SIIBizkaiaPost.getInstance(getConfiguration()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_RECIBIDAS, errorPeriodo);
			}
		} else {
			if(newList.size() > 0){
				return SIIAeatPost.getInstance(getConfiguration()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_RECIBIDAS, false);
			}

			if(modList.size() > 0){
				return SIIAeatPost.getInstance(getConfiguration()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_RECIBIDAS, errorPeriodo);
			}
		}
    	return new JSONArray();
    }
	
	public JSONArray bajaFacturasRecibidas(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getConfiguration(), SIIType.FACTURAS_RECIBIDAS);

		if(getConfiguration().isAraba()) {
    		return SIIArabaPost.getInstance(getConfiguration()).bajaFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isGipuzkoa()) {
    		return SIIGipuzkoaPost.getInstance(getConfiguration()).bajaFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isBizkaia()) {
    		return SIIBizkaiaPost.getInstance(getConfiguration()).bajaFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getConfiguration()).bajaFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri);
	}
	
	protected JSONArray suministroFacturasRecibidasPagos(Domain domain, String login, Company company, LinkedList<Finance> financeList, Integer invoiceId) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getConfiguration(), SIIType.FACTURAS_RECIBIDAS_PAGOS);

		if(getConfiguration().isAraba()) {
    		return SIIArabaPost.getInstance(getConfiguration()).suministroFacturasRecibidasPagos(domain, login, company, financeList, invoiceId, uri);
    	} else if(getConfiguration().isGipuzkoa()) {
    		return SIIGipuzkoaPost.getInstance(getConfiguration()).suministroFacturasRecibidasPagos(domain, login, company, financeList, invoiceId, uri);
    	} else if(getConfiguration().isBizkaia()) {
    		return SIIBizkaiaPost.getInstance(getConfiguration()).suministroFacturasRecibidasPagos(domain, login, company, financeList, invoiceId, uri);
    	} else return SIIAeatPost.getInstance(getConfiguration()).suministroFacturasRecibidasPagos(domain, login, company, financeList, invoiceId, uri);
    }
	
	 // -------------------- BIENES DE INVERSIÓN 
	
	protected byte[] getSuministroBienesInversion(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		LinkedList<VatContext> modList = contextList.stream().filter(v->  "Correcto".equals(v.getSiiStatus())
				|| "AceptadoConErrores".equals(v.getSiiStatus())
				|| "Anulada".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		LinkedList<VatContext> newList = contextList.stream().filter(v-> "Pendiente".equals(v.getSiiStatus())
				|| "Incorrecto".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		if(getConfiguration().isAraba()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.araba.BienesInversion.getInstance().getSuministroBienesInversion(domain, login, company, invoiceId, contextList, SendType.ALTA_INVERSION.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.araba.BienesInversion.getInstance().getSuministroBienesInversion(domain, login, company, invoiceId, contextList, SendType.MOD_INVERSION.isModificacion(), terceros);
			}
    	} else if(getConfiguration().isGipuzkoa()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.gipuzkoa.BienesInversion.getInstance().getSuministroBienesInversion(domain, login, company, invoiceId, contextList, SendType.ALTA_INVERSION.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.gipuzkoa.BienesInversion.getInstance().getSuministroBienesInversion(domain, login, company, invoiceId, contextList, SendType.MOD_INVERSION.isModificacion(), terceros);
			}
    	} else if(getConfiguration().isBizkaia()) {
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
		String uri = SIIUri.getInstance().getURI(getConfiguration(), SIIType.BIENES_INVERSION);

		LinkedList<VatContext> modList = contextList.stream().filter(v->  "Correcto".equals(v.getSiiStatus())
				|| "AceptadoConErrores".equals(v.getSiiStatus())
				|| "Anulada".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		LinkedList<VatContext> newList = contextList.stream().filter(v-> "Pendiente".equals(v.getSiiStatus())
				|| "Incorrecto".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));


		if(getConfiguration().isAraba()) {
			if(newList.size() > 0){
				return SIIArabaPost.getInstance(getConfiguration()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.ALTA_INVERSION);
			}

			if(modList.size() > 0){
				return SIIArabaPost.getInstance(getConfiguration()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.MOD_INVERSION);
			}
    	} else if(getConfiguration().isGipuzkoa()) {
			if(newList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getConfiguration()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.ALTA_INVERSION);
			}

			if(modList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getConfiguration()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.MOD_INVERSION);
			}
    	} else if(getConfiguration().isBizkaia()) {
			if(newList.size() > 0){
				return SIIBizkaiaPost.getInstance(getConfiguration()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.ALTA_INVERSION);
			}

			if(modList.size() > 0){
				return SIIBizkaiaPost.getInstance(getConfiguration()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.MOD_INVERSION);
			}
		} else {
			if(newList.size() > 0){
				return SIIAeatPost.getInstance(getConfiguration()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.ALTA_INVERSION);
			}

			if(modList.size() > 0){
				return SIIAeatPost.getInstance(getConfiguration()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.MOD_INVERSION);
			}
		}
		return new JSONArray();
	}

	protected JSONArray bajaBienesInversion(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getConfiguration(), SIIType.BIENES_INVERSION);
		
		if(getConfiguration().isAraba()) {
    		return SIIArabaPost.getInstance(getConfiguration()).bajaBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isGipuzkoa()) {
    		return SIIGipuzkoaPost.getInstance(getConfiguration()).bajaBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isBizkaia()) {
    		return SIIBizkaiaPost.getInstance(getConfiguration()).bajaBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getConfiguration()).bajaBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri);
	}

	 // -------------------- OPERACIONES INTRACOMUNITARIAS

	protected byte[] getSuministroOperacionesIntracomunitarias(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String tipoOp, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		LinkedList<VatContext> modList = contextList.stream().filter(v->  "Correcto".equals(v.getSiiStatus())
				|| "AceptadoConErrores".equals(v.getSiiStatus())
				|| "Anulada".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		LinkedList<VatContext> newList = contextList.stream().filter(v-> "Pendiente".equals(v.getSiiStatus())
				|| "Incorrecto".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		if(getConfiguration().isAraba()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.araba.OperacionesIntracomunitarias.getInstance().getSuministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, SendType.ALTA_INTRACOMUNITARIAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.araba.OperacionesIntracomunitarias.getInstance().getSuministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, SendType.MOD_INTRACOMUNITARIAS.isModificacion(), terceros);			}
    	} else if(getConfiguration().isGipuzkoa()) {
			if(newList.size() > 0){
				return net.aonsolutions.aon.sii.gipuzkoa.OperacionesIntracomunitarias.getInstance().getSuministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, SendType.ALTA_INTRACOMUNITARIAS.isModificacion(), terceros);
			}

			if(modList.size() > 0){
				return net.aonsolutions.aon.sii.gipuzkoa.OperacionesIntracomunitarias.getInstance().getSuministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, SendType.MOD_INTRACOMUNITARIAS.isModificacion(), terceros);			}
    	} else if(getConfiguration().isBizkaia()) {
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
		String uri = SIIUri.getInstance().getURI(getConfiguration(), SIIType.OPERACIONES_INTRACOMUNITARIAS);

		LinkedList<VatContext> modList = contextList.stream().filter(v->  "Correcto".equals(v.getSiiStatus())
				|| "AceptadoConErrores".equals(v.getSiiStatus())
				|| "Anulada".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		LinkedList<VatContext> newList = contextList.stream().filter(v-> "Pendiente".equals(v.getSiiStatus())
				|| "Incorrecto".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		if(getConfiguration().isAraba()) {
			if(newList.size() > 0){
				return SIIArabaPost.getInstance(getConfiguration()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.ALTA_INTRACOMUNITARIAS);
			}

			if(modList.size() > 0){
				return SIIArabaPost.getInstance(getConfiguration()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.MOD_INTRACOMUNITARIAS);
			}
    	} else if(getConfiguration().isGipuzkoa()) {
			if(newList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getConfiguration()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.ALTA_INTRACOMUNITARIAS);
			}

			if(modList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getConfiguration()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.MOD_INTRACOMUNITARIAS);
			}
    	} else if(getConfiguration().isBizkaia()) {
			if(newList.size() > 0){
				return SIIBizkaiaPost.getInstance(getConfiguration()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.ALTA_INTRACOMUNITARIAS);
			}

			if(modList.size() > 0){
				return SIIBizkaiaPost.getInstance(getConfiguration()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.MOD_INTRACOMUNITARIAS);
			}
		} else {
			if(newList.size() > 0){
				return SIIAeatPost.getInstance(getConfiguration()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.ALTA_INTRACOMUNITARIAS);
			}

			if(modList.size() > 0){
				return SIIAeatPost.getInstance(getConfiguration()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.MOD_INTRACOMUNITARIAS);
			}
		}
		return new JSONArray();
	}
		
	protected JSONArray bajaOperacionesIntracomunitarias(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getConfiguration(), SIIType.OPERACIONES_INTRACOMUNITARIAS);

		if(getConfiguration().isAraba()) {
    		return SIIArabaPost.getInstance(getConfiguration()).bajaOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isGipuzkoa()) {
    		return SIIGipuzkoaPost.getInstance(getConfiguration()).bajaOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isBizkaia()) {
    		return SIIBizkaiaPost.getInstance(getConfiguration()).bajaOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getConfiguration()).bajaOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, terceros, uri);
	}

 // -------------------- COBROS METALICO
	
    protected JSONObject suministroCobrosMetalico(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getConfiguration(), SIIType.COBROS_METALICO);

		if(getConfiguration().isAraba()) {
			return SIIArabaPost.getInstance(getConfiguration()).suministroCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isGipuzkoa()) {
			return SIIGipuzkoaPost.getInstance(getConfiguration()).suministroCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isBizkaia()) {
			return SIIBizkaiaPost.getInstance(getConfiguration()).suministroCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getConfiguration()).suministroCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    }

    protected JSONObject bajaCobrosMetalico(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) {
		String uri = SIIUri.getInstance().getURI(getConfiguration(), SIIType.COBROS_METALICO);

		if(getConfiguration().isAraba()) {
			return SIIArabaPost.getInstance(getConfiguration()).bajaCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isGipuzkoa()) {
			return SIIGipuzkoaPost.getInstance(getConfiguration()).bajaCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isBizkaia()) {
			return SIIBizkaiaPost.getInstance(getConfiguration()).bajaCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getConfiguration()).bajaCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    }

    // -------------------- OPERACIONES SEGUROS
	
    protected JSONObject suministroOperacionesSeguros(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getConfiguration(), SIIType.OPERACIONES_SEGUROS);

		if(getConfiguration().isAraba()) {
			return SIIArabaPost.getInstance(getConfiguration()).suministroOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isGipuzkoa()) {
			return SIIGipuzkoaPost.getInstance(getConfiguration()).suministroOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isBizkaia()) {
			return SIIBizkaiaPost.getInstance(getConfiguration()).suministroOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getConfiguration()).suministroOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    }

    protected JSONObject bajaOperacionesSeguros(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) {
		String uri = SIIUri.getInstance().getURI(getConfiguration(), SIIType.OPERACIONES_SEGUROS);

		if(getConfiguration().isAraba()) {
			return SIIArabaPost.getInstance(getConfiguration()).bajaOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isGipuzkoa()) {
			return SIIGipuzkoaPost.getInstance(getConfiguration()).bajaOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isBizkaia()) {
			return SIIBizkaiaPost.getInstance(getConfiguration()).bajaOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getConfiguration()).bajaOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);

    }

    // -------------------- AGENCIAS VIAJES
	
    protected JSONObject suministroAgenciasViajes(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = SIIUri.getInstance().getURI(getConfiguration(), SIIType.AGENCIAS_VIAJES);

		if(getConfiguration().isAraba()) {
			return SIIArabaPost.getInstance(getConfiguration()).suministroAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isGipuzkoa()) {
			return SIIGipuzkoaPost.getInstance(getConfiguration()).suministroAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isBizkaia()) {
			return SIIBizkaiaPost.getInstance(getConfiguration()).suministroAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getConfiguration()).suministroAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    }

    protected JSONObject bajaAgenciasViajes(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) {
		String uri = SIIUri.getInstance().getURI(getConfiguration(), SIIType.AGENCIAS_VIAJES);

		if(getConfiguration().isAraba()) {
			return SIIArabaPost.getInstance(getConfiguration()).bajaAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isGipuzkoa()) {
			return SIIGipuzkoaPost.getInstance(getConfiguration()).bajaAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(getConfiguration().isBizkaia()) {
			return SIIBizkaiaPost.getInstance(getConfiguration()).bajaAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else return SIIAeatPost.getInstance(getConfiguration()).bajaAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    }

}
