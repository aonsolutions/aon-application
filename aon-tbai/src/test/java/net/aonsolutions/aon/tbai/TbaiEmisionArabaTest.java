package net.aonsolutions.aon.tbai;

import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.aonsolutions.AonSecret;
import com.esferalia.aon.occam.api.model.type.Administration;

/**
 * Alta y anulacion de facturas emitidas en el entorno de pruebas de TicketBAI de
 * la Hacienda Foral de Alava.
 *
 * Los tests son los de {@link AbstractTbaiEmisionTest}; aqui solo se concreta el
 * territorio. Los envios etiquetados como "envio" van a
 * https://pruebas-ticketbai.araba.eus/TicketBAI/v1/facturas/ y
 * https://pruebas-ticketbai.araba.eus/TicketBAI/v1/anulaciones/ (ver
 * {@link TbaiUri}), y se firman con la politica de firma de Alava
 * (https://ticketbai.araba.eus/tbai/sinadura/), tal y como exige el apartado 6
 * de la guia de entorno de pruebas.
 *
 * Segun esa misma guia, en pruebas vale cualquier NIF emisor correcto: si no es
 * contribuyente alaves o no esta de alta en IAE, el servicio responde con un
 * aviso que no es bloqueante. El certificado del envio, en cambio, si se
 * comprueba: los del kit de pruebas los emite una CA de desarrollo de Izenpe y el
 * servicio los rechaza con el error 001, asi que los envios se hacen con el
 * certificado de firma de AON, el mismo que usan los tests de verifactu.
 */
class TbaiEmisionArabaTest extends AbstractTbaiEmisionTest {

	/** Licencia TicketBAI del software en el entorno de pruebas de Alava. */
	private static final String LICENCIA_TBAI_PRUEBAS = "TBAIARbjlCHFMFK00416";

	/** Titular del certificado de firma de AON, que es el emisor de los envios. */
	private static final String NIF_EMISOR = "B01487271";
	private static final String NOMBRE_EMISOR = "AON SOLUTIONS, S.L.";

	@Override
	protected Administration administration() {
		return Administration.ALAVA;
	}

	@Override
	protected String licenciaPruebas() {
		return LICENCIA_TBAI_PRUEBAS;
	}

	/**
	 * Certificado de firma de AON, el mismo que usan los tests de verifactu
	 * (TBAIAlavaEnvironment.configurationWithCertificate). Es un certificado
	 * homologado y vigente, que es lo que exige el servicio de Alava.
	 */
	@Override
	protected Certificate certificadoEnvio() {
		return AonSecret.getSigCert();
	}

	/** El emisor es el titular del certificado, para que remitente y emisor coincidan. */
	@Override
	protected Company companyEnvio() {
		return company(NIF_EMISOR, NOMBRE_EMISOR);
	}
}
