package net.aonsolutions.aon.tbai;

import static net.aonsolutions.aon.tbai.TbaiValidationTest.fecha;
import static net.aonsolutions.aon.tbai.TbaiValidationTest.texto;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;

import ticketbai.emision.Cabecera;
import ticketbai.emision.CabeceraFacturaType;
import ticketbai.emision.CausaExencionType;
import ticketbai.emision.CausaNoSujetaType;
import ticketbai.emision.ClaveTipoFacturaType;
import ticketbai.emision.ClaveTipoRectificativaType;
import ticketbai.emision.ClavesType;
import ticketbai.emision.CountryType2;
import ticketbai.emision.DatosFacturaType;
import ticketbai.emision.DesgloseFacturaType;
import ticketbai.emision.DesgloseIVAType;
import ticketbai.emision.DesgloseTipoOperacionType;
import ticketbai.emision.Destinatarios;
import ticketbai.emision.DetalleExentaType;
import ticketbai.emision.DetalleIVAType;
import ticketbai.emision.DetalleNoExentaType;
import ticketbai.emision.DetalleNoSujeta;
import ticketbai.emision.DetallesFacturaType;
import ticketbai.emision.Emisor;
import ticketbai.emision.EmitidaPorTercerosType;
import ticketbai.emision.EncadenamientoFacturaAnteriorType;
import ticketbai.emision.EntidadDesarrolladoraType;
import ticketbai.emision.Entrega;
import ticketbai.emision.ExentaType;
import ticketbai.emision.Factura;
import ticketbai.emision.FacturaRectificativaType;
import ticketbai.emision.FacturasRectificadasSustituidasType;
import ticketbai.emision.HuellaTBAI;
import ticketbai.emision.IDClaveType;
import ticketbai.emision.IDDestinatario;
import ticketbai.emision.IDDetalleFacturaType;
import ticketbai.emision.IDFacturaRectificadaSustituidaType;
import ticketbai.emision.IDOtro;
import ticketbai.emision.ImporteRectificacionSustitutivaType;
import ticketbai.emision.NoExentaType;
import ticketbai.emision.NoSujetaType;
import ticketbai.emision.SiNoType;
import ticketbai.emision.SoftwareFacturacionType;
import ticketbai.emision.SujetaType;
import ticketbai.emision.Sujetos;
import ticketbai.emision.TicketBai;
import ticketbai.emision.TipoDesgloseType;
import ticketbai.emision.TipoOperacionSujetaNoExentaType;

/**
 * Tests de {@link TbaiValidation#validateEmision}.
 *
 * Cada test parte de un fichero de alta valido (ver {@link #emision()}), modifica
 * unicamente lo que se quiere comprobar y verifica los errores que se acumulan en
 * la excepcion, en el mismo orden en el que se ejecutan las validaciones.
 *
 * La factura del fichero de alta es la misma que construye Invoice2tbai para una
 * factura nacional no exenta con una unica base al 21% (ver
 * AbstractTbaiEmisionTest).
 */
class TbaiValidationEmisionTest {

	/** NIF valido del emisor de la factura. */
	private static final String NIF_EMISOR = "99980200M";

	/** NIF valido del destinatario de la factura. */
	private static final String NIF_DESTINATARIO = "B00000034";

	/** NIF valido de la entidad desarrolladora (ver Invoice2tbai). */
	private static final String NIF_ENTIDAD = "B01487271";

	/** NIF con el formato del tipo NIFType pero con la letra de control erronea. */
	private static final String NIF_LETRA_ERRONEA = "12345678A";

	/** Cadena de nueve caracteres que no cumple el formato del tipo NIFType. */
	private static final String NIF_FORMATO_ERRONEO = "AAAAAAAAA";

	// *****************************************************************
	// ************************ [CABECERA] *****************************
	// *****************************************************************

	@Test
	void emisionObligatoria_Test() {
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class,
			() -> TbaiValidation.validateEmision(null));
		assertEquals(List.of(InvoiceCommunicationError.TBAI_002), e.getMessages());
	}

	@Test
	void emisionValida_Test() {
		assertValida(t -> { });
	}

	@Test
	void cabeceraObligatoria_Test() {
		assertErrores(t -> t.setCabecera(null), InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void idVersionTbaiObligatorio_Test() {
		assertErrores(t -> t.getCabecera().setIDVersionTBAI(null), InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void idVersionTbaiIncorrecto_Test() {
		assertErrores(t -> t.getCabecera().setIDVersionTBAI("1.1"), InvoiceCommunicationError.TBAI_002);
	}

	// *****************************************************************
	// ************************* [SUJETOS] *****************************
	// *****************************************************************

	@Test
	void sujetosObligatorios_Test() {
		assertErrores(t -> t.setSujetos(null), InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void emisorObligatorio_Test() {
		assertErrores(t -> t.getSujetos().setEmisor(null), InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void emisorNifObligatorio_Test() {
		assertErrores(t -> t.getSujetos().getEmisor().setNIF(null), InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void emisorNifFormatoErroneo_Test() {
		assertErrores(t -> t.getSujetos().getEmisor().setNIF(NIF_FORMATO_ERRONEO),
			InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void emisorNifNoValido_Test() {
		assertErrores(t -> t.getSujetos().getEmisor().setNIF(NIF_LETRA_ERRONEA),
			InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void emisorNombreRazonSocialObligatorio_Test() {
		assertErrores(t -> t.getSujetos().getEmisor().setApellidosNombreRazonSocial(null),
			InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void emisorNombreRazonSocialDemasiadoLargo_Test() {
		assertErrores(t -> t.getSujetos().getEmisor().setApellidosNombreRazonSocial(texto(121)),
			InvoiceCommunicationError.TBAI_002);
	}

	// *****************************************************************
	// *********************** [DESTINATARIOS] *************************
	// *****************************************************************

	/** 10-51: la factura completa debe tener al menos un destinatario. */
	@Test
	void facturaCompletaSinDestinatarios_Test() {
		assertErrores(t -> t.getSujetos().setDestinatarios(null), InvoiceCommunicationError.TBAI_010_051);
	}

	/** La factura simplificada no necesita destinatarios. */
	@Test
	void facturaSimplificadaSinDestinatarios_Test() {
		assertValida(t -> {
			t.getSujetos().setDestinatarios(null);
			t.getFactura().getCabeceraFactura().setFacturaSimplificada(SiNoType.S);
		});
	}

	/** 10-52: con VariosDestinatarios igual a N no pueden informarse varios. */
	@Test
	void variosDestinatariosNoPermitidos_Test() {
		assertErrores(t -> t.getSujetos().getDestinatarios().getIDDestinatario().add(destinatario()),
			InvoiceCommunicationError.TBAI_010_052);
	}

	/** 10-53: con VariosDestinatarios igual a S debe indicarse al menos uno. */
	@Test
	void variosDestinatariosSinDestinatarios_Test() {
		assertErrores(t -> {
				t.getSujetos().setDestinatarios(null);
				t.getSujetos().setVariosDestinatarios(SiNoType.S);
			}
			, InvoiceCommunicationError.TBAI_010_051
			, InvoiceCommunicationError.TBAI_010_053
		);
	}

	/** 10-68: el NIF del destinatario nacional debe ser correcto. */
	@Test
	void destinatarioNifNoValido_Test() {
		assertErrores(t -> primerDestinatario(t).setNIF(NIF_LETRA_ERRONEA),
			InvoiceCommunicationError.TBAI_010_068);
	}

	@Test
	void destinatarioSinIdentificacion_Test() {
		assertErrores(t -> primerDestinatario(t).setNIF(null), InvoiceCommunicationError.TBAI_002);
	}

	/** 10-54: el pais del destinatario no nacional es obligatorio. */
	@Test
	void destinatarioExtranjeroSinPais_Test() {
		assertErrores(t -> {
				setDestinatarioExtranjero(t, idOtro(null, "02", "FR12345678"));
				setDesgloseEntrega(t);
			}
			, InvoiceCommunicationError.TBAI_010_054
		);
	}

	/** 10-55: el NIF-IVA del destinatario debe tener el formato correcto. */
	@Test
	void destinatarioNifIvaIncorrecto_Test() {
		assertErrores(t -> {
				setDestinatarioExtranjero(t, idOtro(CountryType2.FR, "02", "12345678"));
				setDesgloseEntrega(t);
			}
			, InvoiceCommunicationError.TBAI_010_055
		);
	}

	/** 10-248: falta el identificador del destinatario no nacional. */
	@Test
	void destinatarioExtranjeroSinIdentificador_Test() {
		assertErrores(t -> {
				setDestinatarioExtranjero(t, idOtro(CountryType2.FR, "06", null));
				setDesgloseEntrega(t);
			}
			, InvoiceCommunicationError.TBAI_010_248
		);
	}

	/** 10-57: no pueden mezclarse destinatarios nacionales y extranjeros. */
	@Test
	void destinatariosNacionalesYExtranjeros_Test() {
		assertErrores(t -> {
				IDDestinatario extranjero = destinatario();
				extranjero.setNIF(null);
				extranjero.setIDOtro(idOtro(CountryType2.FR, "02", "FR12345678"));
				t.getSujetos().getDestinatarios().getIDDestinatario().add(extranjero);
				t.getSujetos().setVariosDestinatarios(SiNoType.S);
				setDesgloseEntrega(t);
			}
			, InvoiceCommunicationError.TBAI_010_057
		);
	}

	/** 10-61: con destinatario extranjero el desglose debe ser a nivel de operacion. */
	@Test
	void destinatarioExtranjeroConDesglosePorFactura_Test() {
		assertErrores(t -> setDestinatarioExtranjero(t, idOtro(CountryType2.FR, "02", "FR12345678")),
			InvoiceCommunicationError.TBAI_010_061);
	}

	@Test
	void destinatarioNombreRazonSocialObligatorio_Test() {
		assertErrores(t -> primerDestinatario(t).setApellidosNombreRazonSocial(null),
			InvoiceCommunicationError.TBAI_004);
	}

	/** 016-024 y 016-025: si hay destinatarios el domicilio es obligatorio. */
	@Test
	void destinatarioSinDomicilio_Test() {
		assertErrores(t -> primerDestinatario(t).setDireccion(null),
			InvoiceCommunicationError.TBAI_016_024);
		assertErrores(t -> primerDestinatario(t).setCodigoPostal(null),
			InvoiceCommunicationError.TBAI_016_025);
	}

	// *****************************************************************
	// ******************** [CABECERA DE FACTURA] **********************
	// *****************************************************************

	@Test
	void facturaObligatoria_Test() {
		assertErrores(t -> t.setFactura(null), InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void cabeceraFacturaObligatoria_Test() {
		assertErrores(t -> t.getFactura().setCabeceraFactura(null), InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void serieEnBlanco_Test() {
		assertErrores(t -> cabeceraFactura(t).setSerieFactura("  "), InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void serieDemasiadoLarga_Test() {
		assertErrores(t -> cabeceraFactura(t).setSerieFactura(texto(21)),
			InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void serieConCaracteresProhibidos_Test() {
		assertErrores(t -> cabeceraFactura(t).setSerieFactura("TEST#1"),
			InvoiceCommunicationError.TBAI_004);
	}

	/** Las series DFA_ estan reservadas para las facturas de FakturAraba. */
	@Test
	void serieReservadaDeLaDiputacion_Test() {
		assertErrores(t -> cabeceraFactura(t).setSerieFactura("DFA_1"),
			InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void numeroFacturaObligatorio_Test() {
		assertErrores(t -> cabeceraFactura(t).setNumFactura(null), InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void numeroFacturaConCaracteresProhibidos_Test() {
		assertErrores(t -> cabeceraFactura(t).setNumFactura("19%"), InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void fechaExpedicionObligatoria_Test() {
		assertErrores(t -> cabeceraFactura(t).setFechaExpedicionFactura(null),
			InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void fechaExpedicionConFormatoErroneo_Test() {
		assertErrores(t -> cabeceraFactura(t).setFechaExpedicionFactura("2026-01-01"),
			InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void fechaExpedicionInexistente_Test() {
		assertErrores(t -> cabeceraFactura(t).setFechaExpedicionFactura("31-02-2026"),
			InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void fechaExpedicionPosteriorAHoy_Test() {
		assertErrores(t -> cabeceraFactura(t).setFechaExpedicionFactura(fecha(1)),
			InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void horaExpedicionObligatoria_Test() {
		assertErrores(t -> cabeceraFactura(t).setHoraExpedicionFactura(null),
			InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void horaExpedicionConFormatoErroneo_Test() {
		assertErrores(t -> cabeceraFactura(t).setHoraExpedicionFactura("12:00"),
			InvoiceCommunicationError.TBAI_002);
	}

	/** 10-159: la factura sustitutiva de una simplificada debe ser completa. */
	@Test
	void facturaSustitutivaDeSimplificadaNoCompleta_Test() {
		assertErrores(t -> {
				cabeceraFactura(t).setFacturaEmitidaSustitucionSimplificada(SiNoType.S);
				cabeceraFactura(t).setFacturaSimplificada(SiNoType.S);
				setFacturasRectificadas(t);
			}
			, InvoiceCommunicationError.TBAI_010_159
		);
	}

	/** 10-249: no puede ser sustitutiva de una simplificada y rectificativa. */
	@Test
	void facturaSustitutivaYRectificativa_Test() {
		assertErrores(t -> {
				cabeceraFactura(t).setFacturaEmitidaSustitucionSimplificada(SiNoType.S);
				setRectificativa(t, ClaveTipoFacturaType.R_1, ClaveTipoRectificativaType.I);
				setFacturasRectificadas(t);
			}
			, InvoiceCommunicationError.TBAI_010_249
		);
	}

	/** 10-124: las facturas rectificativas deben indicar la serie. */
	@Test
	void facturaRectificativaSinSerie_Test() {
		assertErrores(t -> {
				setRectificativa(t, ClaveTipoFacturaType.R_1, ClaveTipoRectificativaType.I);
				setFacturasRectificadas(t);
				cabeceraFactura(t).setSerieFactura(null);
			}
			, InvoiceCommunicationError.TBAI_010_124
		);
	}

	/** 10-235: las facturas rectificativas deben indicar la factura rectificada. */
	@Test
	void facturaRectificativaSinFacturaRectificada_Test() {
		assertErrores(t -> setRectificativa(t, ClaveTipoFacturaType.R_1, ClaveTipoRectificativaType.I),
			InvoiceCommunicationError.TBAI_010_235);
	}

	/** 10-172: sin rectificacion ni sustitucion no puede haber facturas rectificadas. */
	@Test
	void facturasRectificadasEnFacturaNormal_Test() {
		assertErrores(t -> setFacturasRectificadas(t), InvoiceCommunicationError.TBAI_010_172);
	}

	/** 10-125 y 10-126: los importes rectificados exigen rectificacion por sustitucion. */
	@Test
	void importesRectificadosSinSustitucion_Test() {
		assertErrores(t -> {
				setRectificativa(t, ClaveTipoFacturaType.R_1, ClaveTipoRectificativaType.I);
				setFacturasRectificadas(t);
				ImporteRectificacionSustitutivaType importes = new ImporteRectificacionSustitutivaType();
				importes.setBaseRectificada("100");
				importes.setCuotaRectificada("21");
				cabeceraFactura(t).getFacturaRectificativa().setImporteRectificacionSustitutiva(importes);
			}
			, InvoiceCommunicationError.TBAI_010_125
			, InvoiceCommunicationError.TBAI_010_126
		);
	}

	/** 10-70: la fecha de la factura rectificada debe ser valida. */
	@Test
	void fechaFacturaRectificadaPosteriorAHoy_Test() {
		assertErrores(t -> {
				setRectificativa(t, ClaveTipoFacturaType.R_1, ClaveTipoRectificativaType.I);
				setFacturasRectificadas(t);
				facturaRectificada(t).setFechaExpedicionFactura(fecha(1));
			}
			, InvoiceCommunicationError.TBAI_010_070
		);
	}

	/** 10-69: el numero de la factura rectificada no admite caracteres extranos. */
	@Test
	void numeroFacturaRectificadaConCaracteresProhibidos_Test() {
		assertErrores(t -> {
				setRectificativa(t, ClaveTipoFacturaType.R_1, ClaveTipoRectificativaType.I);
				setFacturasRectificadas(t);
				facturaRectificada(t).setNumFactura("18&");
			}
			, InvoiceCommunicationError.TBAI_010_069
		);
	}

	// *****************************************************************
	// ********************* [DATOS DE FACTURA] ************************
	// *****************************************************************

	@Test
	void datosFacturaObligatorios_Test() {
		assertErrores(t -> t.getFactura().setDatosFactura(null), InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void descripcionFacturaObligatoria_Test() {
		assertErrores(t -> datosFactura(t).setDescripcionFactura(null),
			InvoiceCommunicationError.TBAI_004);
	}

	/** 003: es obligatorio informar las lineas de detalle de la factura. */
	@Test
	void detallesFacturaObligatorios_Test() {
		assertErrores(t -> datosFactura(t).setDetallesFactura(null), InvoiceCommunicationError.TBAI_003);
	}

	@Test
	void descripcionDetalleObligatoria_Test() {
		assertErrores(t -> primerDetalle(t).setDescripcionDetalle(null),
			InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void importeTotalFacturaObligatorio_Test() {
		assertErrores(t -> datosFactura(t).setImporteTotalFactura(null),
			InvoiceCommunicationError.TBAI_004);
	}

	/** 10-59: la fecha de operacion no puede ser posterior a la fecha actual. */
	@Test
	void fechaOperacionPosteriorAHoy_Test() {
		assertErrores(t -> datosFactura(t).setFechaOperacion(fecha(1))
			, InvoiceCommunicationError.TBAI_010_059
			, InvoiceCommunicationError.TBAI_010_090
		);
	}

	/** 10-90: la fecha de operacion no puede ser posterior a la de expedicion. */
	@Test
	void fechaOperacionPosteriorALaExpedicion_Test() {
		assertErrores(t -> {
				cabeceraFactura(t).setFechaExpedicionFactura(fecha(-2));
				datosFactura(t).setFechaOperacion(fecha(-1));
			}
			, InvoiceCommunicationError.TBAI_010_090
		);
	}

	/** 10-129: la base imponible a coste exige la clave de regimen de IVA 06. */
	@Test
	void baseImponibleACosteSinClave06_Test() {
		assertErrores(t -> datosFactura(t).setBaseImponibleACoste("100"),
			InvoiceCommunicationError.TBAI_010_129);
	}

	/** 10-130: la clave de regimen de IVA 06 exige la base imponible a coste. */
	@Test
	void clave06SinBaseImponibleACoste_Test() {
		assertErrores(t -> setClaves(t, "06"), InvoiceCommunicationError.TBAI_010_130);
	}

	@Test
	void clavesObligatorias_Test() {
		assertErrores(t -> datosFactura(t).setClaves(null), InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void claveNoValida_Test() {
		assertErrores(t -> setClaves(t, "99"), InvoiceCommunicationError.TBAI_002);
	}

	/**
	 * 10-131: las claves de regimen de IVA deben ser compatibles entre si. La clave
	 * 04 anade el aviso 10-184 porque no admite el desglose sujeto y no exento sin
	 * inversion del sujeto pasivo.
	 */
	@Test
	void clavesIncompatibles_Test() {
		assertErrores(t -> setClaves(t, "01", "04")
			, InvoiceCommunicationError.TBAI_010_131
			, InvoiceCommunicationError.TBAI_010_184
		);
	}

	/**
	 * La clave 53 es incompatible con el resto y debe ir sola. Ademas solo admite
	 * el desglose no sujeto con causa OT (aviso 10-133).
	 */
	@Test
	void clave53ConOtraClave_Test() {
		assertErrores(t -> setClaves(t, "53", "01")
			, InvoiceCommunicationError.TBAI_010_131
			, InvoiceCommunicationError.TBAI_010_133
		);
	}

	/** 10-72 y 10-79: el importe total debe cuadrar con los detalles y el desglose. */
	@Test
	void importeTotalNoCuadra_Test() {
		assertErrores(t -> datosFactura(t).setImporteTotalFactura("200")
			, InvoiceCommunicationError.TBAI_010_072
			, InvoiceCommunicationError.TBAI_010_079
		);
	}

	// *****************************************************************
	// *********************** [DESGLOSES] *****************************
	// *****************************************************************

	@Test
	void tipoDesgloseObligatorio_Test() {
		assertErrores(t -> t.getFactura().setTipoDesglose(null)
			, InvoiceCommunicationError.TBAI_002
			, InvoiceCommunicationError.TBAI_010_079
			, InvoiceCommunicationError.TBAI_010_078
		);
	}

	/** 10-63: el desglose a nivel de factura debe tener Sujeta o NoSujeta. */
	@Test
	void desglosePorFacturaSinInformacion_Test() {
		assertErrores(t -> t.getFactura().getTipoDesglose().getDesgloseFactura().setSujeta(null)
			, InvoiceCommunicationError.TBAI_004
			, InvoiceCommunicationError.TBAI_010_063
			, InvoiceCommunicationError.TBAI_010_079
			, InvoiceCommunicationError.TBAI_010_078
		);
	}

	/** 10-66: el desglose sujeto debe tener Exenta o NoExenta. */
	@Test
	void desgloseSujetoSinInformacion_Test() {
		assertErrores(t -> sujeta(t).setNoExenta(null)
			, InvoiceCommunicationError.TBAI_004
			, InvoiceCommunicationError.TBAI_010_066
			, InvoiceCommunicationError.TBAI_010_079
			, InvoiceCommunicationError.TBAI_010_078
		);
	}

	/** 10-253: las causas de no sujecion no pueden repetirse. */
	@Test
	void causasDeNoSujecionRepetidas_Test() {
		assertErrores(t -> {
				NoSujetaType noSujeta = new NoSujetaType();
				noSujeta.getDetalleNoSujeta().add(detalleNoSujeta(CausaNoSujetaType.VT, "0"));
				noSujeta.getDetalleNoSujeta().add(detalleNoSujeta(CausaNoSujetaType.VT, "0"));
				t.getFactura().getTipoDesglose().getDesgloseFactura().setNoSujeta(noSujeta);
			}
			, InvoiceCommunicationError.TBAI_010_253
		);
	}

	/** 10-180, 10-181, 10-85 y 10-86: la clave 02 exige un desglose sujeto y exento. */
	@Test
	void clave02ConDesgloseNoExento_Test() {
		assertErrores(t -> setClaves(t, "02")
			, InvoiceCommunicationError.TBAI_010_180
			, InvoiceCommunicationError.TBAI_010_181
			, InvoiceCommunicationError.TBAI_010_085
			, InvoiceCommunicationError.TBAI_010_086
		);
	}

	/** Exportacion: clave 02 con el desglose sujeto y exento que le corresponde. */
	@Test
	void clave02ConDesgloseExento_Test() {
		assertValida(t -> {
			setClaves(t, "02");
			setDesgloseExento(t, CausaExencionType.E_2, "121");
		});
	}

	/** 10-168: la causa de exencion E5 exige un destinatario intracomunitario. */
	@Test
	void exencionE5SinDestinatarioIntracomunitario_Test() {
		assertErrores(t -> setDesgloseExento(t, CausaExencionType.E_5, "121"),
			InvoiceCommunicationError.TBAI_010_168);
	}

	/** Entrega intracomunitaria exenta por el articulo 25. */
	@Test
	void exencionE5ConDestinatarioIntracomunitario_Test() {
		assertValida(t -> {
			setDestinatarioExtranjero(t, idOtro(CountryType2.FR, "02", "FR12345678"));
			setDesgloseExento(t, CausaExencionType.E_5, "121");
			setDesgloseEntrega(t);
		});
	}

	/** 10-174: con la clave 01 la causa de exencion no puede ser E2 ni E3. */
	@Test
	void exencionE2ConClave01_Test() {
		assertErrores(t -> setDesgloseExento(t, CausaExencionType.E_2, "121"),
			InvoiceCommunicationError.TBAI_010_174);
	}

	/** Inversion del sujeto pasivo: no se informan el tipo impositivo ni la cuota. */
	@Test
	void inversionSujetoPasivoValida_Test() {
		assertValida(t -> setInversionSujetoPasivo(t));
	}

	/** 10-83: la inversion del sujeto pasivo no admite factura simplificada. */
	@Test
	void inversionSujetoPasivoEnFacturaSimplificada_Test() {
		assertErrores(t -> {
				setInversionSujetoPasivo(t);
				cabeceraFactura(t).setFacturaSimplificada(SiNoType.S);
			}
			, InvoiceCommunicationError.TBAI_010_083
		);
	}

	/** 10-82: la inversion del sujeto pasivo solo admite las claves 01, 04, 05, 06 y 12. */
	@Test
	void inversionSujetoPasivoConClaveIncompatible_Test() {
		assertErrores(t -> {
				setInversionSujetoPasivo(t);
				setClaves(t, "07");
			}
			, InvoiceCommunicationError.TBAI_010_082
		);
	}

	/** 10-135 y 10-136: con inversion del sujeto pasivo no hay tipo ni cuota. */
	@Test
	void inversionSujetoPasivoConTipoYCuota_Test() {
		assertErrores(t -> {
				setInversionSujetoPasivo(t);
				primerDetalleIVA(t).setTipoImpositivo("21");
				primerDetalleIVA(t).setCuotaImpuesto("21");
				datosFactura(t).setImporteTotalFactura("121");
				primerDetalle(t).setImporteTotal("121");
			}
			, InvoiceCommunicationError.TBAI_010_135
			, InvoiceCommunicationError.TBAI_010_136
		);
	}

	/** 10-265: el tipo impositivo es obligatorio con las claves distintas de 03 y 09. */
	@Test
	void tipoImpositivoObligatorio_Test() {
		assertErrores(t -> primerDetalleIVA(t).setTipoImpositivo(null),
			InvoiceCommunicationError.TBAI_010_265);
	}

	/** 10-266: la cuota del impuesto es obligatoria con las claves distintas de 03 y 09. */
	@Test
	void cuotaImpuestoObligatoria_Test() {
		assertErrores(t -> {
				primerDetalleIVA(t).setCuotaImpuesto(null);
				datosFactura(t).setImporteTotalFactura("100");
				primerDetalle(t).setImporteTotal("100");
			}
			, InvoiceCommunicationError.TBAI_010_266
		);
	}

	/** 10-74: la cuota del impuesto debe estar bien calculada. */
	@Test
	void cuotaImpuestoMalCalculada_Test() {
		assertErrores(t -> {
				primerDetalleIVA(t).setCuotaImpuesto("30");
				datosFactura(t).setImporteTotalFactura("130");
				primerDetalle(t).setImporteTotal("130");
			}
			, InvoiceCommunicationError.TBAI_010_074
		);
	}

	/** 10-177: la base imponible y la cuota deben tener el mismo signo. */
	@Test
	void baseImponibleYCuotaDeDistintoSigno_Test() {
		assertErrores(t -> {
				primerDetalleIVA(t).setCuotaImpuesto("-21");
				datosFactura(t).setImporteTotalFactura("79");
				primerDetalle(t).setImporteTotal("79");
			}
			, InvoiceCommunicationError.TBAI_010_177
			, InvoiceCommunicationError.TBAI_010_074
		);
	}

	/** 10-84: el recargo de equivalencia exige la clave de regimen de IVA 51 o 52. */
	@Test
	void recargoDeEquivalenciaSinClave51NiClave52_Test() {
		assertErrores(t -> primerDetalleIVA(t)
			.setOperacionEnRecargoDeEquivalenciaORegimenSimplificado(SiNoType.S),
			InvoiceCommunicationError.TBAI_010_084);
	}

	/** 10-196: las claves 51 y 52 exigen alguna linea en recargo de equivalencia. */
	@Test
	void clave52SinRecargoDeEquivalencia_Test() {
		assertErrores(t -> setClaves(t, "52"), InvoiceCommunicationError.TBAI_010_196);
	}

	// *****************************************************************
	// ********************* [HUELLA TICKETBAI] ************************
	// *****************************************************************

	@Test
	void huellaObligatoria_Test() {
		assertErrores(t -> t.setHuellaTBAI(null), InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void encadenamientoValido_Test() {
		assertValida(t -> t.getHuellaTBAI().setEncadenamientoFacturaAnterior(encadenamiento(texto(100))));
	}

	/** 016-017: la firma de la factura anterior debe contener 100 caracteres. */
	@Test
	void firmaFacturaAnteriorIncompleta_Test() {
		assertErrores(t -> t.getHuellaTBAI().setEncadenamientoFacturaAnterior(encadenamiento(texto(50))),
			InvoiceCommunicationError.TBAI_016_017);
	}

	@Test
	void softwareObligatorio_Test() {
		assertErrores(t -> t.getHuellaTBAI().setSoftware(null), InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void licenciaTbaiObligatoria_Test() {
		assertErrores(t -> t.getHuellaTBAI().getSoftware().setLicenciaTBAI(null),
			InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void entidadDesarrolladoraSinDatos_Test() {
		assertErrores(t -> t.getHuellaTBAI().getSoftware().getEntidadDesarrolladora().setNIF(null),
			InvoiceCommunicationError.TBAI_016);
	}

	@Test
	void entidadDesarrolladoraConNifNoValido_Test() {
		assertErrores(t -> t.getHuellaTBAI().getSoftware().getEntidadDesarrolladora()
			.setNIF(NIF_LETRA_ERRONEA), InvoiceCommunicationError.TBAI_011);
	}

	@Test
	void nombreSoftwareObligatorio_Test() {
		assertErrores(t -> t.getHuellaTBAI().getSoftware().setNombre(null),
			InvoiceCommunicationError.TBAI_011);
	}

	@Test
	void versionSoftwareObligatoria_Test() {
		assertErrores(t -> t.getHuellaTBAI().getSoftware().setVersion(null),
			InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void numSerieDispositivoDemasiadoLargo_Test() {
		assertErrores(t -> t.getHuellaTBAI().setNumSerieDispositivo(texto(31)),
			InvoiceCommunicationError.TBAI_002);
	}

	// *****************************************************************
	// ***************** [ACUMULACION DE LOS ERRORES] ******************
	// *****************************************************************

	/** Se acumulan los errores de todos los campos que incumplen alguna validacion. */
	@Test
	void variosErrores_Test() {
		assertErrores(t -> {
				t.getSujetos().getEmisor().setNIF(NIF_FORMATO_ERRONEO);
				t.getHuellaTBAI().getSoftware().setNombre(null);
				t.getHuellaTBAI().getSoftware().setVersion(null);
			}
			, InvoiceCommunicationError.TBAI_002
			, InvoiceCommunicationError.TBAI_011
			, InvoiceCommunicationError.TBAI_004
		);
	}

	/** El mismo error no se repite cuando varios campos incumplen la misma validacion. */
	@Test
	void erroresNoDuplicados_Test() {
		assertErrores(t -> {
				cabeceraFactura(t).setNumFactura(null);
				t.getHuellaTBAI().getSoftware().setLicenciaTBAI(null);
				t.getHuellaTBAI().getSoftware().setVersion(null);
			}
			, InvoiceCommunicationError.TBAI_004
		);
	}

	// *****************************************************************
	// *************************** [UTILES] ****************************
	// *****************************************************************

	private void assertValida(Consumer<TicketBai> completa) {
		TicketBai emision = emision();
		completa.accept(emision);
		assertDoesNotThrow(() -> TbaiValidation.validateEmision(emision));
	}

	private void assertErrores(Consumer<TicketBai> completa, InvoiceCommunicationError... errores) {
		TicketBai emision = emision();
		completa.accept(emision);
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class,
			() -> TbaiValidation.validateEmision(emision));
		assertEquals(List.of(errores), e.getMessages());
	}

	/**
	 * Fichero de alta valido, sin el bloque Signature, que se incorpora al firmarlo
	 * despues de serializarlo.
	 */
	static TicketBai emision() {
		TicketBai emision = new TicketBai();

		Cabecera cabecera = new Cabecera();
		cabecera.setIDVersionTBAI("1.2");
		emision.setCabecera(cabecera);

		emision.setSujetos(sujetos());
		emision.setFactura(factura());
		emision.setHuellaTBAI(huella());
		return emision;
	}

	private static Sujetos sujetos() {
		Emisor emisor = new Emisor();
		emisor.setNIF(NIF_EMISOR);
		emisor.setApellidosNombreRazonSocial("PRUEBA AAA BBB");

		Destinatarios destinatarios = new Destinatarios();
		destinatarios.getIDDestinatario().add(destinatario());

		Sujetos sujetos = new Sujetos();
		sujetos.setEmisor(emisor);
		sujetos.setDestinatarios(destinatarios);
		sujetos.setVariosDestinatarios(SiNoType.N);
		sujetos.setEmitidaPorTercerosODestinatario(EmitidaPorTercerosType.N);
		return sujetos;
	}

	private static IDDestinatario destinatario() {
		IDDestinatario destinatario = new IDDestinatario();
		destinatario.setNIF(NIF_DESTINATARIO);
		destinatario.setApellidosNombreRazonSocial("CONSULTORIA GALDETU");
		destinatario.setCodigoPostal("01001");
		destinatario.setDireccion("GRAN VIA 2");
		return destinatario;
	}

	private static Factura factura() {
		CabeceraFacturaType cabecera = new CabeceraFacturaType();
		cabecera.setSerieFactura("TEST1");
		cabecera.setNumFactura("19");
		cabecera.setFechaExpedicionFactura(fecha(0));
		cabecera.setHoraExpedicionFactura("12:00:00");
		cabecera.setFacturaSimplificada(SiNoType.N);
		cabecera.setFacturaEmitidaSustitucionSimplificada(SiNoType.N);

		IDDetalleFacturaType detalle = new IDDetalleFacturaType();
		detalle.setDescripcionDetalle("Servicio de pruebas TicketBAI");
		detalle.setCantidad("1");
		detalle.setImporteUnitario("100");
		detalle.setDescuento("0");
		detalle.setImporteTotal("121");

		DetallesFacturaType detalles = new DetallesFacturaType();
		detalles.getIDDetalleFactura().add(detalle);

		DatosFacturaType datos = new DatosFacturaType();
		datos.setFechaOperacion(fecha(0));
		datos.setDescripcionFactura("FACTURA TEST1/19");
		datos.setDetallesFactura(detalles);
		datos.setImporteTotalFactura("121");
		datos.setClaves(claves("01"));

		DesgloseFacturaType desgloseFactura = new DesgloseFacturaType();
		desgloseFactura.setSujeta(sujetaNoExenta());

		TipoDesgloseType desglose = new TipoDesgloseType();
		desglose.setDesgloseFactura(desgloseFactura);

		Factura factura = new Factura();
		factura.setCabeceraFactura(cabecera);
		factura.setDatosFactura(datos);
		factura.setTipoDesglose(desglose);
		return factura;
	}

	/** Operacion sujeta y no exenta con una unica base al 21%. */
	private static SujetaType sujetaNoExenta() {
		DetalleIVAType detalleIVA = new DetalleIVAType();
		detalleIVA.setBaseImponible("100");
		detalleIVA.setTipoImpositivo("21");
		detalleIVA.setCuotaImpuesto("21");
		detalleIVA.setTipoRecargoEquivalencia("0");
		detalleIVA.setCuotaRecargoEquivalencia("0");
		detalleIVA.setOperacionEnRecargoDeEquivalenciaORegimenSimplificado(SiNoType.N);

		DesgloseIVAType desgloseIVA = new DesgloseIVAType();
		desgloseIVA.getDetalleIVA().add(detalleIVA);

		DetalleNoExentaType detalleNoExenta = new DetalleNoExentaType();
		detalleNoExenta.setTipoNoExenta(TipoOperacionSujetaNoExentaType.S_1);
		detalleNoExenta.setDesgloseIVA(desgloseIVA);

		NoExentaType noExenta = new NoExentaType();
		noExenta.getDetalleNoExenta().add(detalleNoExenta);

		SujetaType sujeta = new SujetaType();
		sujeta.setNoExenta(noExenta);
		return sujeta;
	}

	private static HuellaTBAI huella() {
		EntidadDesarrolladoraType entidad = new EntidadDesarrolladoraType();
		entidad.setNIF(NIF_ENTIDAD);

		SoftwareFacturacionType software = new SoftwareFacturacionType();
		software.setLicenciaTBAI("TBAIBI00000000PRUEBA");
		software.setEntidadDesarrolladora(entidad);
		software.setNombre("aonSolutions");
		software.setVersion("9.23");

		HuellaTBAI huella = new HuellaTBAI();
		huella.setSoftware(software);
		huella.setNumSerieDispositivo("1");
		return huella;
	}

	private static ClavesType claves(String... valores) {
		ClavesType claves = new ClavesType();
		for (String valor : valores) {
			IDClaveType clave = new IDClaveType();
			clave.setClaveRegimenIvaOpTrascendencia(valor);
			claves.getIDClave().add(clave);
		}
		return claves;
	}

	private static EncadenamientoFacturaAnteriorType encadenamiento(String firma) {
		EncadenamientoFacturaAnteriorType encadenamiento = new EncadenamientoFacturaAnteriorType();
		encadenamiento.setSerieFacturaAnterior("TEST1");
		encadenamiento.setNumFacturaAnterior("18");
		encadenamiento.setFechaExpedicionFacturaAnterior(fecha(-1));
		encadenamiento.setSignatureValueFirmaFacturaAnterior(firma);
		return encadenamiento;
	}

	private static IDOtro idOtro(CountryType2 pais, String tipo, String id) {
		IDOtro idOtro = new IDOtro();
		idOtro.setCodigoPais(pais);
		idOtro.setIDType(tipo);
		idOtro.setID(id);
		return idOtro;
	}

	private static DetalleNoSujeta detalleNoSujeta(CausaNoSujetaType causa, String importe) {
		DetalleNoSujeta detalle = new DetalleNoSujeta();
		detalle.setCausa(causa);
		detalle.setImporte(importe);
		return detalle;
	}

	private static CabeceraFacturaType cabeceraFactura(TicketBai emision) {
		return emision.getFactura().getCabeceraFactura();
	}

	private static DatosFacturaType datosFactura(TicketBai emision) {
		return emision.getFactura().getDatosFactura();
	}

	private static IDDetalleFacturaType primerDetalle(TicketBai emision) {
		return datosFactura(emision).getDetallesFactura().getIDDetalleFactura().get(0);
	}

	private static IDDestinatario primerDestinatario(TicketBai emision) {
		return emision.getSujetos().getDestinatarios().getIDDestinatario().get(0);
	}

	private static SujetaType sujeta(TicketBai emision) {
		return emision.getFactura().getTipoDesglose().getDesgloseFactura() != null
			? emision.getFactura().getTipoDesglose().getDesgloseFactura().getSujeta()
			: emision.getFactura().getTipoDesglose().getDesgloseTipoOperacion().getEntrega().getSujeta();
	}

	private static DetalleIVAType primerDetalleIVA(TicketBai emision) {
		return sujeta(emision).getNoExenta().getDetalleNoExenta().get(0).getDesgloseIVA()
			.getDetalleIVA().get(0);
	}

	private static IDFacturaRectificadaSustituidaType facturaRectificada(TicketBai emision) {
		return cabeceraFactura(emision).getFacturasRectificadasSustituidas()
			.getIDFacturaRectificadaSustituida().get(0);
	}

	private static void setClaves(TicketBai emision, String... valores) {
		datosFactura(emision).setClaves(claves(valores));
	}

	private static void setRectificativa(TicketBai emision, ClaveTipoFacturaType codigo,
			ClaveTipoRectificativaType tipo) {
		FacturaRectificativaType rectificativa = new FacturaRectificativaType();
		rectificativa.setCodigo(codigo);
		rectificativa.setTipo(tipo);
		cabeceraFactura(emision).setFacturaRectificativa(rectificativa);
	}

	private static void setFacturasRectificadas(TicketBai emision) {
		IDFacturaRectificadaSustituidaType rectificada = new IDFacturaRectificadaSustituidaType();
		rectificada.setSerieFactura("TEST1");
		rectificada.setNumFactura("18");
		rectificada.setFechaExpedicionFactura(fecha(-1));

		FacturasRectificadasSustituidasType rectificadas = new FacturasRectificadasSustituidasType();
		rectificadas.getIDFacturaRectificadaSustituida().add(rectificada);
		cabeceraFactura(emision).setFacturasRectificadasSustituidas(rectificadas);
	}

	/** Identifica al destinatario con el bloque IDOtro en lugar del NIF. */
	private static void setDestinatarioExtranjero(TicketBai emision, IDOtro idOtro) {
		IDDestinatario destinatario = primerDestinatario(emision);
		destinatario.setNIF(null);
		destinatario.setIDOtro(idOtro);
	}

	/** Pasa el desglose a nivel de operacion, con la entrega de bienes. */
	private static void setDesgloseEntrega(TicketBai emision) {
		TipoDesgloseType desglose = emision.getFactura().getTipoDesglose();
		Entrega entrega = new Entrega();
		entrega.setSujeta(desglose.getDesgloseFactura().getSujeta());
		entrega.setNoSujeta(desglose.getDesgloseFactura().getNoSujeta());

		DesgloseTipoOperacionType desgloseOperacion = new DesgloseTipoOperacionType();
		desgloseOperacion.setEntrega(entrega);
		desglose.setDesgloseFactura(null);
		desglose.setDesgloseTipoOperacion(desgloseOperacion);
	}

	/** Sustituye el desglose no exento por un desglose exento por el importe total. */
	private static void setDesgloseExento(TicketBai emision, CausaExencionType causa, String base) {
		DetalleExentaType detalleExenta = new DetalleExentaType();
		detalleExenta.setCausaExencion(causa);
		detalleExenta.setBaseImponible(base);

		ExentaType exenta = new ExentaType();
		exenta.getDetalleExenta().add(detalleExenta);

		SujetaType sujeta = sujeta(emision);
		sujeta.setNoExenta(null);
		sujeta.setExenta(exenta);
	}

	/**
	 * Operacion con inversion del sujeto pasivo: el tipo impositivo, la cuota y el
	 * recargo se informan a cero y el importe total es la base imponible.
	 */
	private static void setInversionSujetoPasivo(TicketBai emision) {
		sujeta(emision).getNoExenta().getDetalleNoExenta().get(0)
			.setTipoNoExenta(TipoOperacionSujetaNoExentaType.S_2);
		DetalleIVAType detalleIVA = primerDetalleIVA(emision);
		detalleIVA.setTipoImpositivo("0");
		detalleIVA.setCuotaImpuesto("0");
		detalleIVA.setTipoRecargoEquivalencia("0");
		detalleIVA.setCuotaRecargoEquivalencia("0");
		datosFactura(emision).setImporteTotalFactura("100");
		primerDetalle(emision).setImporteTotal("100");
	}
}
