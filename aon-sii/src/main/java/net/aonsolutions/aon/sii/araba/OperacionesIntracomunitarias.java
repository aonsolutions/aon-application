package net.aonsolutions.aon.sii.araba;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;

import https.sii_araba_eus.documentos.respuestasuministro.RespuestaLRBajaOComunitariasType;
import https.sii_araba_eus.documentos.respuestasuministro.RespuestaLROComunitariasType;
import https.sii_araba_eus.documentos.suministroinformacion.CabeceraSii;
import https.sii_araba_eus.documentos.suministroinformacion.CabeceraSiiBaja;
import https.sii_araba_eus.documentos.suministroinformacion.ClaveTipoComunicacionType;
import https.sii_araba_eus.documentos.suministroinformacion.CountryMiembroType;
import https.sii_araba_eus.documentos.suministroinformacion.CountryType2;
import https.sii_araba_eus.documentos.suministroinformacion.IDFacturaComunitariaType;
import https.sii_araba_eus.documentos.suministroinformacion.IDFacturaComunitariaType.IDEmisorFactura;
import https.sii_araba_eus.documentos.suministroinformacion.IDOtroType;
import https.sii_araba_eus.documentos.suministroinformacion.OperacionIntracomunitariaType;
import https.sii_araba_eus.documentos.suministroinformacion.PersonaFisicaJuridicaESType;
import https.sii_araba_eus.documentos.suministroinformacion.PersonaFisicaJuridicaType;
import https.sii_araba_eus.documentos.suministrolr.BajaLRDetOperacionIntracomunitaria;
import https.sii_araba_eus.documentos.suministrolr.LRBajaOperacionIntracomunitariaType;
import https.sii_araba_eus.documentos.suministrolr.LROperacionIntracomunitariaType;
import https.sii_araba_eus.documentos.suministrolr.SuministroLRDetOperacionIntracomunitaria;
import net.aonsolutions.aon.sii.IDType;

public class OperacionesIntracomunitarias extends SIIBuilt {

	public static OperacionesIntracomunitarias getInstance() {
		return new OperacionesIntracomunitarias();
	}
	
	public OperacionesIntracomunitarias() {

	}
	
	// ------------------- SUMINISTRO OPERACIONES INTRACOMUNITARIAS
	
		protected byte[] getSuministroOperacionesIntracomunitarias(SuministroLRDetOperacionIntracomunitaria suministro) {
			JAXBContext ctx;
			byte[] b = null;
			try {
				ctx = JAXBContext.newInstance(SuministroLRDetOperacionIntracomunitaria.class);
				b = writeXml(ctx, suministro);
			} catch (JAXBException | IOException e) {
				e.printStackTrace();
			}
			return b;
		}
			
		protected byte[] getRespuestaSuministroOperacionesIntracomunitarias(RespuestaLROComunitariasType suministro) {
			JAXBContext ctx;
			byte[] b = null;
			try {
				ctx = JAXBContext.newInstance(RespuestaLROComunitariasType.class);
				b = writeXml(ctx, suministro);
			} catch (JAXBException | IOException e) {
				e.printStackTrace();
			}
			return b;
		}
		
		protected byte[] getBajaOperacionesIntracomunitarias(BajaLRDetOperacionIntracomunitaria suministro){
			JAXBContext ctx;
			byte[] b = null;
			try {
				ctx = JAXBContext.newInstance(BajaLRDetOperacionIntracomunitaria.class);
				b = writeXml(ctx, suministro);
			} catch (JAXBException | IOException e) {
				e.printStackTrace();
			}
			return b;
		}
		
		protected byte[] getRespuestaBajaOperacionesIntracomunitarias(RespuestaLRBajaOComunitariasType suministro){
			JAXBContext ctx;
			byte[] b = null;
			try {
				ctx = JAXBContext.newInstance(RespuestaLRBajaOComunitariasType.class);
				b = writeXml(ctx, suministro);
			} catch (JAXBException | IOException e) {
				e.printStackTrace();
			}
			return b;
		}
		
		/**
		 * Libro de registro de Determinadas Operaciones Intracomunitarias.
		 * 
		 * @param company
		 * @param invoiceList
		 */
		protected SuministroLRDetOperacionIntracomunitaria suministroOperacionesIntracomunitarias(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String tipoOp, Boolean mod, String terceros, String auth) {
			SuministroLRDetOperacionIntracomunitaria suministro = new SuministroLRDetOperacionIntracomunitaria();
			
			// CABECERA
			suministro.setCabecera(cabecera(company, mod, terceros));
			
			// BODY
			invoiceList.stream().forEach(invoice -> {
				VatContext vat = contextList.stream().filter(f -> f.getInvoice().equals(invoice)).findFirst().orElse(new VatContext());
				
				LROperacionIntracomunitariaType opIntracomunitaria = new LROperacionIntracomunitariaType();
				
				opIntracomunitaria.setPeriodoLiquidacion(periodoLiquidacion(vat, false));
				
				IDFacturaComunitariaType idFactura = new IDFacturaComunitariaType();
				idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));
				
				IDEmisorFactura emisor = new IDEmisorFactura();
				if(vat.getInvoiceType().equals(InvoiceType.SALES)){
					emisor.setNIF(company.getDocument());
					emisor.setNombreRazon(company.getName());
				} else {
					emisor.setNombreRazon(vat.getRegistryName());
					if(vat.getRegistryDocumentCountry().equals(Country.ES)){
						emisor.setNIF(vat.getRegistryDocument());
					} else {
						IDOtroType otro = new IDOtroType();
						otro.setCodigoPais(CountryType2.valueOf(vat.getRegistryDocumentCountry().getIso2()));
						
						String document = vat.getRegistryDocument();
						if(!document.substring(0,2).equals(vat.getRegistryDocumentCountry().getIso2())) {
							document = vat.getRegistryDocumentCountry().getIso2() + document;
						}
						otro.setID(document);				
						
						otro.setIDType(IDType.NIF_IVA.getName()); 
						emisor.setIDOtro(otro);
					}
				}
				idFactura.setIDEmisorFactura(emisor);
				
				idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());
				opIntracomunitaria.setIDFactura(idFactura);

				opIntracomunitaria.setContraparte(contraparteIntracomunitario(vat));

				OperacionIntracomunitariaType oit = new OperacionIntracomunitariaType();
				oit.setTipoOperacion(tipoOp);// A(art 70) || B (art 16, 9)
				oit.setClaveDeclarado(vat.getInvoiceType().equals(InvoiceType.SALES) ? "D" : "R");
				oit.setEstadoMiembro(CountryMiembroType.valueOf(vat.getRegistryDocumentCountry().getIso2()));
				//oit.setPlazoOperacion(""); //OPTIONAL
				String desc = "";
				for (VatContext vatContext : contextList) {
					desc = desc + vatContext.getDetailDescription();
				}
				oit.setDescripcionBienes(desc.length() > 39 ? desc.substring(0, 39) : desc);// TODO
				RAddress address = AON.getRAddres(domain.getName(), domain.getId(), login, vat.getRegistry());
				oit.setDireccionOperador(address.getFullAddress() != null && !address.getFullAddress().equals("") ? address.getFullAddress() : "Sin direcci\u00f3n");
				//oit.setFacturasODocumentacion(""); // OPTIONAL
				
				opIntracomunitaria.setOperacionIntracomunitaria(oit);
				
				suministro.getRegistroLRDetOperacionIntracomunitaria().add(opIntracomunitaria);
			});
			return suministro;
		}
			
		protected BajaLRDetOperacionIntracomunitaria bajaOperacionesIntracomunitarias(Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> vatList, String terceros, String auth) {
			BajaLRDetOperacionIntracomunitaria baja = new BajaLRDetOperacionIntracomunitaria();
			baja.setCabecera(cabeceraBaja(company, terceros));
			
			invoiceList.stream().forEach(invoice -> {
				VatContext vat = vatList.stream().filter(f -> f.getInvoice().equals(invoice)).findFirst().orElse(new VatContext()); 
			
				LRBajaOperacionIntracomunitariaType factura = new LRBajaOperacionIntracomunitariaType();
				
				IDFacturaComunitariaType idFactura = new IDFacturaComunitariaType();
				idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));
				idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());
				IDEmisorFactura emisor = new IDEmisorFactura();
				if(vat.getInvoiceType().equals(InvoiceType.SALES)){
					emisor.setNIF(company.getDocument());
					emisor.setNombreRazon(company.getName());
				} else {
					emisor.setNombreRazon(vat.getRegistryName());
					if(vat.getRegistryDocumentCountry().equals(Country.ES)){
						emisor.setNIF(vat.getRegistryDocument());
					} else {
						IDOtroType otro = new IDOtroType();
						otro.setCodigoPais(CountryType2.valueOf(vat.getRegistryDocumentCountry().getIso2()));
						
						String document = vat.getRegistryDocument();
						if(!document.substring(0,2).equals(vat.getRegistryDocumentCountry().getIso2())) {
							document = vat.getRegistryDocumentCountry().getIso2() + document;
						}
						otro.setID(document);
						otro.setIDType(IDType.NIF_IVA.getName()); //valueOf(vat.getRegistryDocumentType()).getName());
						emisor.setIDOtro(otro);
					}
				}
				idFactura.setIDEmisorFactura(emisor);
				
				factura.setIDFactura(idFactura);
				
				factura.setPeriodoLiquidacion(periodoLiquidacion(vat, false));
				
				baja.getRegistroLRBajaDetOperacionIntracomunitaria().add(factura);
			});
			return baja;
		}
			
		// -------------------- FUNCIONES
		
		/**
		 * Devuelve la cabecera.
		 * 
		 * @param company
		 * @return CabeceraSii
		 */
		public CabeceraSii cabecera(Company company, Boolean mod, String terceros){
			CabeceraSii cabecera = new CabeceraSii();
			cabecera.setIDVersionSii("1.0");
			cabecera.setTipoComunicacion(mod ? ClaveTipoComunicacionType.A_1 : ClaveTipoComunicacionType.A_0);
			PersonaFisicaJuridicaESType titular = new PersonaFisicaJuridicaESType();
			titular.setNIF(company.getDocument());
			titular.setNombreRazon(company.getName());
			if(terceros != null && !terceros.equals("false"))
				titular.setNIFRepresentante(terceros);
			cabecera.setTitular(titular);
			
			
			return cabecera;
		}
		public CabeceraSii cabecera(Company company){
			CabeceraSii cabecera = new CabeceraSii();
			cabecera.setIDVersionSii("1.0");
			cabecera.setTipoComunicacion(ClaveTipoComunicacionType.A_0);
			PersonaFisicaJuridicaESType titular = new PersonaFisicaJuridicaESType();
			titular.setNIF(company.getDocument());
			titular.setNombreRazon(company.getName());
			cabecera.setTitular(titular);
			return cabecera;
		}
		
		/**
		 * Devuelve la cabecera para bajas.
		 * 
		 * @param company
		 * @return CabeceraSiiBaja
		 */
		private CabeceraSiiBaja cabeceraBaja(Company company, String terceros){
			CabeceraSiiBaja cabecera = new CabeceraSiiBaja();
			cabecera.setIDVersionSii("1.0");
			PersonaFisicaJuridicaESType titular = new PersonaFisicaJuridicaESType();
			titular.setNIF(company.getDocument());
			titular.setNombreRazon(company.getName());
			if(terceros != null && !terceros.equals("false"))
				titular.setNIFRepresentante(terceros);
			cabecera.setTitular(titular);
			return cabecera;
		}
		
		private PersonaFisicaJuridicaType contraparteIntracomunitario(VatContext vat) {
			PersonaFisicaJuridicaType contraparte = new PersonaFisicaJuridicaType();
			contraparte.setNombreRazon(vat.getRegistryName());
			if(vat.getRegistryDocumentCountry().equals(Country.ES)
					&& validateNif(vat.getRegistryDocument(), vat.getRegistryName(),vat.getRegistryDocumentType())){
				contraparte.setNIF(vat.getRegistryDocument());
			} else {
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.valueOf(vat.getRegistryDocumentCountry().getIso2()));

				String document = vat.getRegistryDocument();
				if(!document.substring(0,2).equals(vat.getRegistryDocumentCountry().getIso2())) {
					document = vat.getRegistryDocumentCountry().getIso2() + document;
				}
				otro.setID(document);		
				
				otro.setIDType(IDType.NIF_IVA.getName());
				contraparte.setIDOtro(otro);
			}	
			return contraparte;
		}
		
		public Boolean validateNif(String nif, String name, DocumentType type) {
			return !type.equals(DocumentType.NOT_CENSUSED);
			/*
			VNifV1Ent vnif = new VNifV1Ent();
			vnif.setNif(nif);
			vnif.setNombre(name);
			return NIFPost.getInstance(cert, pass).vnifV1(vnif);
			*/
		}
		
		public static byte[] writeXml(JAXBContext ctx, Object object) throws JAXBException, IOException{		
			Marshaller marshaller = ctx.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			marshaller.marshal(object, baos);
			baos.close();

			return baos.toByteArray();
		}

		public static Object readXml(JAXBContext ctx, byte[] xmlFile) throws JAXBException{
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
		
			InputStream input = new ByteArrayInputStream(xmlFile);
			return unmarshaller.unmarshal(input);
		}
		

}
