package net.aonsolutions.aon.tbai.lroe;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.AonDateUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionBienInversionType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesBienesInversionType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.BienInversionType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.BienesInversionType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.EntregaBienesInversionPeriodoRegularizacionType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDBienInversionModelo240Type;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_3_bienes_altamodifpeticion_v1_0_1.LROEPJ240BienesAltaModifPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_3_bienes_anulacionpeticion_v1_0_0.LROEPJ240BienesAnulacionPeticion;
import net.aonsolutions.aon.tbai.LroeData;
import net.aonsolutions.aon.tbai.responses.LROEResponse;

public class LROE240_3 extends LROE240 {
	
	private static final long serialVersionUID = 1L;
	
	private static final String CAPITULO = "3";
	
	private LROEPJ240BienesAltaModifPeticion build(Company company, List<Invoice> invoices, LROEInfo info) {
		LROEPJ240BienesAltaModifPeticion lroe =  new LROEPJ240BienesAltaModifPeticion();
		lroe.setCabecera(buildCabecera(company, info));

		BienesInversionType bienes = new BienesInversionType();		
		invoices.stream().forEach(invoice -> {
			bienes.getBien().addAll(buildBienes(invoice));
		});
		return lroe;
	}
	
	private List<BienInversionType> buildBienes(Invoice invoice) {
		LinkedList<BienInversionType> bienes = new LinkedList<>();
		invoice.getDetails().stream().forEach(detail -> {
			if(detail.getInvestAsset() != null && detail.getInvestAssetData() != null && detail.getInvestAssetData().getId() != null) {
				bienes.add(buildBien(invoice, detail.getInvestAssetData()));
			}
		});
		return bienes;
	}
	
	private BienInversionType buildBien(Invoice invoice, InvestAsset investAsset) {
		BienInversionType bien = new BienInversionType();
		EntregaBienesInversionPeriodoRegularizacionType entrega = new EntregaBienesInversionPeriodoRegularizacionType();
		entrega.setFacturaEntrega(buildFactura(invoice));
		entrega.setRegularizacionDeduccionEfectuada("");
		bien.setEntregaBienesInversionPeriodoRegularizacion(entrega);
		bien.setFechaInicioUtilizacion(AonDateUtils.format(investAsset.getStartDate(), DATE_FORMAT));
		bien.setIdentificacionBien(investAsset.getDescription());
		bien.setProrrataAnualDefinitiva(0);
		bien.setRegularizacionAnualDeduccion("");
		return bien;
	}
	
	private IDFacturaType buildFactura(Invoice invoice) {
		IDFacturaType factura = new IDFacturaType();
		factura.setFechaExpedicionFactura(AonDateUtils.format(invoice.getIssueDate(), DATE_FORMAT));
		factura.setSerieFactura(invoice.getSeries());
		factura.setNumFactura(Integer.toString(invoice.getNumber()));
		return factura;
	}
	
	public LROEResponse alta(TbaiConfiguration tbaiConfiguration, Company company, Invoice invoice) {
		LinkedList<Invoice> invoices = new LinkedList<>();
		invoices.add(invoice);
		return alta(tbaiConfiguration, company, invoices);
	}
	
	public LROEResponse alta(TbaiConfiguration tbaiConfiguration, Company company, List<Invoice> invoices) {
		try {
			LROEInfo info = buildInfo(OperacionEnum.A_00);
			 
			final LROEPJ240BienesAltaModifPeticion p240 = build(company, invoices, info); 
			final JAXBContext jaxbContext = JAXBContext.newInstance(LROEPJ240BienesAltaModifPeticion.class);
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p240, bos );
			
			byte[] xml = bos.toByteArray();
			DataRequest dataRequest = LroeData.saveRequest(company.getDomain(), new User().setLogin(""), invoices, info, xml);
			byte[] data = toGzip(xml);
			return send(tbaiConfiguration, buildJSON(company, info), data).setDataRequest(dataRequest);
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public LROEInfo buildInfo(OperacionEnum operacion) {
		return new LROEInfo(MODEL_240, CAPITULO, null, operacion);
	}
	
	private LROEPJ240BienesAnulacionPeticion buildBaja(Company company, List<Invoice> invoices, LROEInfo info) {	
		LROEPJ240BienesAnulacionPeticion lroe = new LROEPJ240BienesAnulacionPeticion();
		lroe.setCabecera(buildCabecera(company, info));
		
		AnulacionesBienesInversionType bienes = new AnulacionesBienesInversionType();		
		invoices.stream().forEach(invoice -> {
			bienes.getBien().addAll(buildAnulacionBienes(invoice));
		});
		return lroe;
	}
	
	private List<AnulacionBienInversionType> buildAnulacionBienes(Invoice invoice) {
		LinkedList<AnulacionBienInversionType> bienes = new LinkedList<>();
		invoice.getDetails().stream().forEach(detail -> {
			if(detail.getInvestAsset() != null && detail.getInvestAssetData() != null && detail.getInvestAssetData().getId() != null) {
				bienes.add(buildAnulacionBien(invoice, detail.getInvestAssetData()));
			}
		});
		return bienes;
	}
	
	private AnulacionBienInversionType buildAnulacionBien(Invoice invoice, InvestAsset investAsset) {
		AnulacionBienInversionType bien = new AnulacionBienInversionType();
		IDBienInversionModelo240Type  bienID= new IDBienInversionModelo240Type();
		bienID.setFechaInicioUtilizacion(AonDateUtils.format(investAsset.getStartDate(), DATE_FORMAT));
		bienID.setIdentificacionBien(investAsset.getDescription());
		bien.setIDBien(bienID);
		return bien;
	}
	
	
	public LROEResponse anulacion(TbaiConfiguration tbaiConfiguration, Company company, Invoice invoice) {
		LinkedList<Invoice> invoices = new LinkedList<>();
		invoices.add(invoice);
		return anulacion(company, tbaiConfiguration, invoices);
	}
	
	public LROEResponse anulacion(Company company, TbaiConfiguration tbaiConfiguration, List<Invoice> invoices) {
		try {
			LROEInfo info = buildInfo(OperacionEnum.AN_0);
			final LROEPJ240BienesAnulacionPeticion p240 = buildBaja(company, invoices, info); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPJ240BienesAnulacionPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p240, bos );
			byte[] xml = bos.toByteArray();
			DataRequest dataRequest = LroeData.saveRequest(company.getDomain(), new User().setLogin(""), invoices, info, xml);
			byte[] data = toGzip(xml);
			return send(tbaiConfiguration, buildJSON(company, info), data).setDataRequest(dataRequest);
		} catch (Exception e) {
			return error(e);
		}
	}
}
