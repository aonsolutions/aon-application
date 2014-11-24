package com.esferalia.aon.occam.api;

import java.util.Date;

import com.esferalia.aon.occam.api.model.SalaryAccountEntry;

public interface ISalary {

	/**
	 * Genera el apunte de nóminas para las nóminas de la empresa indicadas en
	 * el periodo comprendido entre las fechas.
	 * 
	 * @param ctx
	 *            Contexto AON
	 * @param enterprise
	 *            Código de empresa
	 * @param from
	 *            Fecha desde
	 * @param to
	 *            Fecha hasta
	 * @param concept
	 *            Concepto del apunte contable, si es <code>null</code>, se
	 *            aplicará el valor por defcto.
	 * @param registryBank
	 *            Código del banco por el cual se pagarán las nóminas, si es
	 *            <code>null</code>, la partida se destinará a
	 *            "Remuneraciones pendientes de pago".
	 * 
	 * @return La collección de apuntes generados.
	 */
	public SalaryAccountEntry getSalaryAccountEntry(AONContext ctx, Integer enterprise,
			Date from, Date to, String concept, Integer registryBank);

}
