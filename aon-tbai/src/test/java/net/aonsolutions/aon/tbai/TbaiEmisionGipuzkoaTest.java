package net.aonsolutions.aon.tbai;

import com.esferalia.aon.occam.api.model.type.Administration;

/**
 * Alta y anulacion de facturas emitidas en el entorno de pruebas de TicketBAI de
 * la Diputacion Foral de Gipuzkoa.
 *
 * Los tests son los de {@link AbstractTbaiEmisionTest}; aqui solo se concreta el
 * territorio. Los envios etiquetados como "envio" van a
 * https://tbai-z.prep.gipuzkoa.eus/sarrerak/alta y
 * https://tbai-z.prep.gipuzkoa.eus/sarrerak/baja (ver {@link TbaiUri}), y se
 * firman con la politica de firma de Gipuzkoa
 * (https://www.gipuzkoa.eus/ticketbai/sinadura).
 */
class TbaiEmisionGipuzkoaTest extends AbstractTbaiEmisionTest {

	/** Licencia TicketBAI del software en el entorno de pruebas de Gipuzkoa. */
	private static final String LICENCIA_TBAI_PRUEBAS = "TBAIGIPRE00000000131";

	@Override
	protected Administration administration() {
		return Administration.GIPUZKOA;
	}

	@Override
	protected String licenciaPruebas() {
		return LICENCIA_TBAI_PRUEBAS;
	}
}
