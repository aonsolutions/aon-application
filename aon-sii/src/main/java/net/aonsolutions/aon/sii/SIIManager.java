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
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Administration;

import net.aonsolutions.aon.sii.aeat.SIIAeatPost;
import net.aonsolutions.aon.sii.araba.SIIArabaPost;
import net.aonsolutions.aon.sii.bizkaia.SIIBizkaiaPost;
import net.aonsolutions.aon.sii.gipuzkoa.SIIGipuzkoaPost;

public class SIIManager {
	private Boolean pruebas = false;
	private Administration administration;
	private byte[] cert;
	private String pass;

	public SIIManager(byte[] cert, String pass, Administration administration) {
		this.cert = cert;
		this.pass = pass;
		this.administration = administration;
	}

	public static SIIManager getInstance(byte[] cert, String pass, Administration administration) {
		return new SIIManager(cert, pass, administration);
	}

	public Administration getAdministration() {
		return administration;
	}

	public void setAdministration(Administration administration) {
		this.administration = administration;
	}

	public byte[] getCert() {
		return cert;
	}

	public void setCert(byte[] cert) {
		this.cert = cert;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	private Boolean isAeat() {
		return Administration.COMMON_TERRITORY.equals(administration) || Administration.UNKNOWN.equals(administration);
 	}

	private Boolean isAraba() {
		return Administration.ALAVA.equals(administration);
 	}

	private Boolean isGipuzkoa() {
		return Administration.GIPUZKOA.equals(administration);
 	}

	private Boolean isBizkaia() {
		return Administration.BIZKAIA.equals(administration);
 	}

	private Boolean isNavarra() {
		return Administration.NAVARRA.equals(administration);
 	}

	 // -------------------- FACTURAS EMITIDAS
	protected JSONArray suministroFacturasEmitidas(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		System.out.println("suministroFacturasEmitidas");
		String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_EMITIDAS, administration) : SIIUri.getInstance().getURI(SIIType.FACTURAS_EMITIDAS, administration);

    	LinkedList<VatContext> modList = contextList.stream().filter(v->  "Correcto".equals(v.getSiiStatus())
    			|| "AceptadoConErrores".equals(v.getSiiStatus())
    			|| "Anulada".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

    	LinkedList<VatContext> newList = contextList.stream().filter(v-> "Pendiente".equals(v.getSiiStatus())
    			|| "Incorrecto".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		if(isAeat() || isNavarra()) {
			if(newList.size() > 0){
				return SIIAeatPost.getInstance(getCert(), getPass()).suministroFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_EMITIDAS);
			}

			if(modList.size() > 0){
				return SIIAeatPost.getInstance(getCert(), getPass()).suministroFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_EMITIDAS);
			}
		} else if(isAraba()) {
			if(newList.size() > 0){
				return SIIArabaPost.getInstance(getCert(), getPass()).suministroFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_EMITIDAS);
			}

			if(modList.size() > 0){
				return SIIArabaPost.getInstance(getCert(), getPass()).suministroFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_EMITIDAS);
			}
		} else if(isGipuzkoa()) {
			if(newList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getCert(), getPass()).suministroFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_EMITIDAS);
			}

			if(modList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getCert(), getPass()).suministroFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_EMITIDAS);
			}
		} else if(isBizkaia()) {
			if(newList.size() > 0){
				return SIIBizkaiaPost.getInstance(getCert(), getPass()).suministroFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_EMITIDAS);
			}

			if(modList.size() > 0){
				return SIIBizkaiaPost.getInstance(getCert(), getPass()).suministroFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_EMITIDAS);
			}
		}
		return new JSONArray();
	}
	
	protected JSONArray bajaFacturasEmitidas(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_EMITIDAS, administration) : SIIUri.getInstance().getURI(SIIType.FACTURAS_EMITIDAS, administration);

		if(isAeat() || isNavarra()) {
			return SIIAeatPost.getInstance(getCert(), getPass()).bajaFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isAraba()) {
    		return SIIArabaPost.getInstance(getCert(), getPass()).bajaFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isGipuzkoa()) {
    		return SIIGipuzkoaPost.getInstance(getCert(), getPass()).bajaFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isBizkaia()) {
    		return SIIBizkaiaPost.getInstance(getCert(), getPass()).bajaFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	}
		return new JSONArray();
	}
	
	protected JSONArray suministroFacturasEmitidasCobros(Domain domain, String login, Company company, LinkedList<Finance> financeList, Integer invoiceId) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_EMITIDAS_COBROS, getAdministration()) : SIIUri.getInstance().getURI(SIIType.FACTURAS_EMITIDAS_COBROS, getAdministration());

		if(isAeat() || isNavarra()) {
			return SIIAeatPost.getInstance(getCert(), getPass()).suministroFacturasEmitidasCobros(domain, login, company, financeList, invoiceId, uri);
    	} else if(isAraba()) {
    		return SIIArabaPost.getInstance(getCert(), getPass()).suministroFacturasEmitidasCobros(domain, login, company, financeList, invoiceId, uri);
    	} else if(isGipuzkoa()) {
    		return SIIGipuzkoaPost.getInstance(getCert(), getPass()).suministroFacturasEmitidasCobros(domain, login, company, financeList, invoiceId, uri);
    	} else if(isBizkaia()) {
    		return SIIBizkaiaPost.getInstance(getCert(), getPass()).suministroFacturasEmitidasCobros(domain, login, company, financeList, invoiceId, uri);
    	}
		return new JSONArray();
    }

	 // -------------------- FACTURAS RECIBIDAS
	
	protected JSONArray suministroFacturasRecibidas(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		System.out.println("SII SUMINISTRO FACTURAS RECIBIDAS - ID => " + invoiceId);
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_RECIBIDAS, getAdministration()) : SIIUri.getInstance().getURI(SIIType.FACTURAS_RECIBIDAS, getAdministration());

    	LinkedList<VatContext> modList = contextList.stream().filter(v-> v.getSiiStatus().equals("Correcto")
    			|| v.getSiiStatus().equals("AceptadoConErrores")
    			|| v.getSiiStatus().equals("Anulada")).collect(Collectors.toCollection(LinkedList::new));

    	LinkedList<VatContext> newList = contextList.stream().filter(v-> v.getSiiStatus().equals("Pendiente")
    			|| v.getSiiStatus().equals("Incorrecto")).collect(Collectors.toCollection(LinkedList::new));

    	if(isAeat() || isNavarra())
    	{
    		System.out.println("SII SUMINISTRO FR - AEAT");
			if(newList.size() > 0){
				return SIIAeatPost.getInstance(getCert(), getPass()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_RECIBIDAS);
			}

			if(modList.size() > 0){
				return SIIAeatPost.getInstance(getCert(), getPass()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_RECIBIDAS);
			}
		} else if(isAraba()) {
			if(newList.size() > 0){
				return SIIArabaPost.getInstance(getCert(), getPass()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_RECIBIDAS);
			}

			if(modList.size() > 0){
				return SIIArabaPost.getInstance(getCert(), getPass()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_RECIBIDAS);
			}
		} else if(isGipuzkoa()) {
			if(newList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getCert(), getPass()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_RECIBIDAS);
			}

			if(modList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getCert(), getPass()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_RECIBIDAS);
			}
		} else if(isBizkaia()) {
			if(newList.size() > 0){
				return SIIBizkaiaPost.getInstance(getCert(), getPass()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, newList, SendType.ALTA_RECIBIDAS);
			}

			if(modList.size() > 0){
				return SIIBizkaiaPost.getInstance(getCert(), getPass()).suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, modList, SendType.MOD_RECIBIDAS);
			}
		}
    	return new JSONArray();
    }
	
	protected JSONArray bajaFacturasRecibidas(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_EMITIDAS, administration) : SIIUri.getInstance().getURI(SIIType.FACTURAS_EMITIDAS, administration);

		if(isAeat() || isNavarra()) {
			return SIIAeatPost.getInstance(getCert(), getPass()).bajaFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isAraba()) {
    		return SIIArabaPost.getInstance(getCert(), getPass()).bajaFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isGipuzkoa()) {
    		return SIIGipuzkoaPost.getInstance(getCert(), getPass()).bajaFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isBizkaia()) {
    		return SIIBizkaiaPost.getInstance(getCert(), getPass()).bajaFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri);
    	}
		return new JSONArray();
	}
	
	protected JSONArray suministroFacturasRecibidasPagos(Domain domain, String login, Company company, LinkedList<Finance> financeList, Integer invoiceId) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_EMITIDAS_COBROS, getAdministration()) : SIIUri.getInstance().getURI(SIIType.FACTURAS_EMITIDAS_COBROS, getAdministration());

		if(isAeat() || isNavarra()) {
			return SIIAeatPost.getInstance(getCert(), getPass()).suministroFacturasRecibidasPagos(domain, login, company, financeList, invoiceId, uri);
    	} else if(isAraba()) {
    		return SIIArabaPost.getInstance(getCert(), getPass()).suministroFacturasRecibidasPagos(domain, login, company, financeList, invoiceId, uri);
    	} else if(isGipuzkoa()) {
    		return SIIGipuzkoaPost.getInstance(getCert(), getPass()).suministroFacturasRecibidasPagos(domain, login, company, financeList, invoiceId, uri);
    	} else if(isBizkaia()) {
    		return SIIBizkaiaPost.getInstance(getCert(), getPass()).suministroFacturasRecibidasPagos(domain, login, company, financeList, invoiceId, uri);
    	}
		return new JSONArray();
    }
	
	 // -------------------- BIENES DE INVERSIÓN 
	
	protected JSONArray suministroBienesInversion(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.BIENES_INVERSION, administration) : SIIUri.getInstance().getURI(SIIType.BIENES_INVERSION, administration);

		LinkedList<VatContext> modList = contextList.stream().filter(v->  "Correcto".equals(v.getSiiStatus())
				|| "AceptadoConErrores".equals(v.getSiiStatus())
				|| "Anulada".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		LinkedList<VatContext> newList = contextList.stream().filter(v-> "Pendiente".equals(v.getSiiStatus())
				|| "Incorrecto".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));


		if(isAeat() || isNavarra()) {
			if(newList.size() > 0){
				return SIIAeatPost.getInstance(getCert(), getPass()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.ALTA_INVERSION);
			}

			if(modList.size() > 0){
				return SIIAeatPost.getInstance(getCert(), getPass()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.MOD_INVERSION);
			}
		} else if(isAraba()) {
			if(newList.size() > 0){
				return SIIArabaPost.getInstance(getCert(), getPass()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.ALTA_INVERSION);
			}

			if(modList.size() > 0){
				return SIIArabaPost.getInstance(getCert(), getPass()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.MOD_INVERSION);
			}
		} else if(isGipuzkoa()) {
			if(newList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getCert(), getPass()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.ALTA_INVERSION);
			}

			if(modList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getCert(), getPass()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.MOD_INVERSION);
			}
		} else if(isBizkaia()) {
			if(newList.size() > 0){
				return SIIBizkaiaPost.getInstance(getCert(), getPass()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.ALTA_INVERSION);
			}

			if(modList.size() > 0){
				return SIIBizkaiaPost.getInstance(getCert(), getPass()).suministroBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri, SendType.MOD_INVERSION);
			}
		}
		return new JSONArray();
	}

	protected JSONArray bajaBienesInversion(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.BIENES_INVERSION, administration) : SIIUri.getInstance().getURI(SIIType.BIENES_INVERSION, administration);

		if(isAeat() || isNavarra()) {
			return SIIAeatPost.getInstance(getCert(), getPass()).bajaBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isAraba()) {
    		return SIIArabaPost.getInstance(getCert(), getPass()).bajaBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isGipuzkoa()) {
    		return SIIGipuzkoaPost.getInstance(getCert(), getPass()).bajaBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isBizkaia()) {
    		return SIIBizkaiaPost.getInstance(getCert(), getPass()).bajaBienesInversion(domain, login, company, invoiceId, contextList, terceros, uri);
    	}
		return new JSONArray();
	}

	 // -------------------- OPERACIONES INTRACOMUNITARIAS
	
	protected JSONArray suministroOperacionesIntracomunitarias(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String tipoOp, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.OPERACIONES_INTRACOMUNITARIAS, administration) : SIIUri.getInstance().getURI(SIIType.OPERACIONES_INTRACOMUNITARIAS, administration);

		LinkedList<VatContext> modList = contextList.stream().filter(v->  "Correcto".equals(v.getSiiStatus())
				|| "AceptadoConErrores".equals(v.getSiiStatus())
				|| "Anulada".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		LinkedList<VatContext> newList = contextList.stream().filter(v-> "Pendiente".equals(v.getSiiStatus())
				|| "Incorrecto".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));

		if(isAeat() || isNavarra()) {
			if(newList.size() > 0){
				return SIIAeatPost.getInstance(getCert(), getPass()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.ALTA_INTRACOMUNITARIAS);
			}

			if(modList.size() > 0){
				return SIIAeatPost.getInstance(getCert(), getPass()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.MOD_INTRACOMUNITARIAS);
			}
		} else if(isAraba()) {
			if(newList.size() > 0){
				return SIIArabaPost.getInstance(getCert(), getPass()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.ALTA_INTRACOMUNITARIAS);
			}

			if(modList.size() > 0){
				return SIIArabaPost.getInstance(getCert(), getPass()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.MOD_INTRACOMUNITARIAS);
			}
		} else if(isGipuzkoa()) {
			if(newList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getCert(), getPass()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.ALTA_INTRACOMUNITARIAS);
			}

			if(modList.size() > 0){
				return SIIGipuzkoaPost.getInstance(getCert(), getPass()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.MOD_INTRACOMUNITARIAS);
			}
		} else if(isBizkaia()) {
			if(newList.size() > 0){
				return SIIBizkaiaPost.getInstance(getCert(), getPass()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.ALTA_INTRACOMUNITARIAS);
			}

			if(modList.size() > 0){
				return SIIBizkaiaPost.getInstance(getCert(), getPass()).suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, terceros, uri, SendType.MOD_INTRACOMUNITARIAS);
			}
		}
		return new JSONArray();
	}
		
	protected JSONArray bajaOperacionesIntracomunitarias(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
		String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.OPERACIONES_INTRACOMUNITARIAS, administration) : SIIUri.getInstance().getURI(SIIType.OPERACIONES_INTRACOMUNITARIAS, administration);

		if(isAeat() || isNavarra()) {
			return SIIAeatPost.getInstance(getCert(), getPass()).bajaOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isAraba()) {
    		return SIIArabaPost.getInstance(getCert(), getPass()).bajaOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isGipuzkoa()) {
    		return SIIGipuzkoaPost.getInstance(getCert(), getPass()).bajaOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isBizkaia()) {
    		return SIIBizkaiaPost.getInstance(getCert(), getPass()).bajaOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, terceros, uri);
    	}
		return new JSONArray();
	}

 // -------------------- COBROS METALICO
	
    protected JSONObject suministroCobrosMetalico(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.COBROS_METALICO, getAdministration()) : SIIUri.getInstance().getURI(SIIType.COBROS_METALICO, getAdministration());

    	if(isAeat() || isNavarra()) {
			return SIIAeatPost.getInstance(getCert(), getPass()).suministroCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isAraba()) {
			return SIIArabaPost.getInstance(getCert(), getPass()).suministroCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isGipuzkoa()) {
			return SIIGipuzkoaPost.getInstance(getCert(), getPass()).suministroCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isBizkaia()) {
			return SIIBizkaiaPost.getInstance(getCert(), getPass()).suministroCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	}
    	return new JSONObject();
    }

    protected JSONObject bajaCobrosMetalico(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.COBROS_METALICO, getAdministration()) : SIIUri.getInstance().getURI(SIIType.COBROS_METALICO, getAdministration());

    	if(isAeat() || isNavarra()) {
			return SIIAeatPost.getInstance(getCert(), getPass()).bajaCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isAraba()) {
			return SIIArabaPost.getInstance(getCert(), getPass()).bajaCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isGipuzkoa()) {
			return SIIGipuzkoaPost.getInstance(getCert(), getPass()).bajaCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isBizkaia()) {
			return SIIBizkaiaPost.getInstance(getCert(), getPass()).bajaCobrosMetalico(domain, login, company, invoiceId, contextList, terceros, uri);
    	}
    	return new JSONObject();
    }

    // -------------------- OPERACIONES SEGUROS
	
    protected JSONObject suministroOperacionesSeguros(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.OPERACIONES_SEGUROS, getAdministration()) : SIIUri.getInstance().getURI(SIIType.OPERACIONES_SEGUROS, getAdministration());

    	if(isAeat() || isNavarra()) {
			return SIIAeatPost.getInstance(getCert(), getPass()).suministroOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isAraba()) {
			return SIIArabaPost.getInstance(getCert(), getPass()).suministroOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isGipuzkoa()) {
			return SIIGipuzkoaPost.getInstance(getCert(), getPass()).suministroOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isBizkaia()) {
			return SIIBizkaiaPost.getInstance(getCert(), getPass()).suministroOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	}
    	return new JSONObject();
    }

    protected JSONObject bajaOperacionesSeguros(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.OPERACIONES_SEGUROS, getAdministration()) : SIIUri.getInstance().getURI(SIIType.OPERACIONES_SEGUROS, getAdministration());
    	if(isAeat() || isNavarra()) {
			return SIIAeatPost.getInstance(getCert(), getPass()).bajaOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isAraba()) {
			return SIIArabaPost.getInstance(getCert(), getPass()).bajaOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isGipuzkoa()) {
			return SIIGipuzkoaPost.getInstance(getCert(), getPass()).bajaOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isBizkaia()) {
			return SIIBizkaiaPost.getInstance(getCert(), getPass()).bajaOperacionesSeguros(domain, login, company, invoiceId, contextList, terceros, uri);
    	}
    	return new JSONObject();
    }

    // -------------------- AGENCIAS VIAJES
	
    protected JSONObject suministroAgenciasViajes(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) throws JAXBException, ParserConfigurationException, SOAPException, IOException {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.AGENCIAS_VIAJES, getAdministration()) : SIIUri.getInstance().getURI(SIIType.AGENCIAS_VIAJES, getAdministration());
    	if(isAeat() || isNavarra()) {
			return SIIAeatPost.getInstance(getCert(), getPass()).suministroAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isAraba()) {
			return SIIArabaPost.getInstance(getCert(), getPass()).suministroAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isGipuzkoa()) {
			return SIIGipuzkoaPost.getInstance(getCert(), getPass()).suministroAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isBizkaia()) {
			return SIIBizkaiaPost.getInstance(getCert(), getPass()).suministroAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	}
    	return new JSONObject();
    }

    protected JSONObject bajaAgenciasViajes(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.AGENCIAS_VIAJES, getAdministration()) : SIIUri.getInstance().getURI(SIIType.AGENCIAS_VIAJES, getAdministration());
    	if(isAeat() || isNavarra()) {
			return SIIAeatPost.getInstance(getCert(), getPass()).bajaAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isAraba()) {
			return SIIArabaPost.getInstance(getCert(), getPass()).bajaAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isGipuzkoa()) {
			return SIIGipuzkoaPost.getInstance(getCert(), getPass()).bajaAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	} else if(isBizkaia()) {
			return SIIBizkaiaPost.getInstance(getCert(), getPass()).bajaAgenciasViajes(domain, login, company, invoiceId, contextList, terceros, uri);
    	}
    	return new JSONObject();
    }

}
