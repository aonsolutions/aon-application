package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry;
import com.esferalia.aon.occam.api.model.SalaryFilter;
import com.esferalia.aon.occam.api.model.SalaryProperties;

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
	public SalaryAccountEntry getSalaryAccountEntry(AONContext ctx,
			Integer enterprise, Date from, Date to, String concept,
			Integer registryBank);

	public Stream<Salary> getSalaries(AONContext ctx,
			SalaryFilter filter, Supplier<Salary> supplier);


}
