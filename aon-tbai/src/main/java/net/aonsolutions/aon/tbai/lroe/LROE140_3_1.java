package net.aonsolutions.aon.tbai.lroe;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.occam.api.model.InvestAssetRegime;
import com.esferalia.aon.occam.api.model.InvestAssetType;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.watson.server.AonDateUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.SiNoEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.TipoBienEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.TituloEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.BienAltaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.BienesAltaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DatosTipoBienType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_3_1_bienes_alta_altamodifpeticion_v1_0_1.LROEPF140BienesAltaAltaModifPeticion;
import net.aonsolutions.aon.tbai.exceptions.http.StatusCodeException;
import net.aonsolutions.aon.tbai.responses.LROEResponse;

public class LROE140_3_1 extends LROE140 {
	
	private static final String CAPITULO = "3";
	private static final String SUBCAPITULO = "3.1";
	
	private static LROEPF140BienesAltaAltaModifPeticion build(Person person, Invoice invoice, LROEInfo info) {
		LROEPF140BienesAltaAltaModifPeticion lroe =  new LROEPF140BienesAltaAltaModifPeticion();
		lroe.setCabecera(buildCabecera(person, info));
		lroe.setBienesAlta(buildBienes(invoice));
		return lroe;
	}
	
	private static BienesAltaType buildBienes(Invoice invoice) {
		BienesAltaType bienes = new BienesAltaType();
		for (InvoiceDetail detail : invoice.getDetails()) {
			if(detail.getInvestAsset() != null) {
				BienAltaType bien = new BienAltaType();
				bien.setEpigrafe(detail.getInvestAssetData().getActivity().getEpigraph());
				bien.setReferenciaBien(Integer.toString(detail.getInvestAssetData().getId()));
				bien.setFechaInicioUtilizacion(AonDateUtils.format(detail.getInvestAssetData().getStartDate(), "dd-MM-yyyy"));
				bien.setFechaOperacion(AonDateUtils.format(invoice.getIssueDate(), "dd-MM-yyyy"));
				bien.setMarcaBienInversionEfectosIVA(SiNoEnum.N);
				bien.setPorcentajeAfectacion(new BigDecimal(detail.getInvestAssetData().getVatPercent()));
				bien.setTipoBien(getTipoBien(detail.getInvestAssetData().getType()));
				bien.setAmortizacionAcumulada31DiciEjercicioAnt("");
				bien.setMarcaBienInversionEfectosIVA(SiNoEnum.N);
				bien.setSistemaAmortizacion("");
				bien.setTitulo(getTitulo(detail.getInvestAssetData().getRegime()));
				DatosTipoBienType datos = new DatosTipoBienType();
				bien.setDatosBien(datos);
				bienes.getBienAlta().add(bien);
			}
		}

		return bienes;
	}
	
	private static TipoBienEnum getTipoBien(InvestAssetType type) {
		if(InvestAssetType.PREMISES.equals(type))
			return TipoBienEnum.A;
		else if(InvestAssetType.OTHER_BUILDING.equals(type))
			return TipoBienEnum.B;
		else if(InvestAssetType.MEANS_OF_TRANSPORT.equals(type))
			return TipoBienEnum.C;
		else if(InvestAssetType.FIXED_PHONE.equals(type))
			return TipoBienEnum.D;
		else if(InvestAssetType.CELLULAR_PHONE.equals(type))
			return TipoBienEnum.E;
		else if(InvestAssetType.FURNITURE.equals(type))
			return TipoBienEnum.G;
		else if(InvestAssetType.MACHINERY.equals(type))
			return TipoBienEnum.H;
		else if(InvestAssetType.COMPUTER_EQUIPMENT.equals(type))
			return TipoBienEnum.I;
		else if(InvestAssetType.INSTALLATION.equals(type))
			return TipoBienEnum.J;
		else if(InvestAssetType.ACCOUNT_GROUP_20_ASSET.equals(type))
			return TipoBienEnum.K;
		else if(InvestAssetType.ACCOUNT_GROUP_21_ASSET.equals(type))
			return TipoBienEnum.L;
		else if(InvestAssetType.ACCOUNT_GROUP_23_ASSET.equals(type))
			return TipoBienEnum.M;
		else if(InvestAssetType.BUILDING_PLOT.equals(type))
			return TipoBienEnum.N;
		return null;
	}
	
	private static TituloEnum getTitulo(InvestAssetRegime regime) {
		if(InvestAssetRegime.PROPERTY.equals(regime))
			return TituloEnum.P;
		else if(InvestAssetRegime.RENTING.equals(regime))
			return TituloEnum.A;
		else if(InvestAssetRegime.FINANCIAL_LEASING.equals(regime))
			return TituloEnum.F;
		else if(InvestAssetRegime.OTHER.equals(regime))
			return TituloEnum.O;
		return null;
	}
	
	public static LROEResponse alta(TbaiConfiguration tbaiConfiguration, Person person, Invoice invoice) throws StatusCodeException {
		try {
			LROEInfo info = new LROEInfo(MODEL_140, CAPITULO, SUBCAPITULO, OperacionEnum.A_00);
			final LROEPF140BienesAltaAltaModifPeticion p140 = build(person, invoice, info); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPF140BienesAltaAltaModifPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p140, bos );
			byte[] data = toGzip(bos.toByteArray());
			return send(tbaiConfiguration, buildJSON(person, info), data);
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public static void modificacion(TbaiConfiguration tbaiConfiguration, Invoice invoice, byte[] xml)  {

	}
	
	public static void anulacion(TbaiConfiguration tbaiConfiguration, Invoice invoice, byte[] xml)  {

	}
}
