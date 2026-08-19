package com.esferalia.aon.occam.api.model.registry;

import java.util.Collection;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.CustomerProperties;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.server.AonDateUtils;
 
/**
 * Semantica de expiracion de {@code customer}, equivalente a la de {@code domain}.
 *
 * <pre>
 *  status    | expiration_date       | domain.active | domain.expirationDate
 *  ----------+-----------------------+---------------+----------------------
 *  ACTIVE    | NULL                  | 1             | NULL
 *  INACTIVE  | NULL                  | 0             | NULL
 *  BLOCKED   | fecha (hoy si vacia)  | 1             | misma fecha
 * </pre>
 *
 * Invariantes que garantiza {@link #normalizeExpirationDate(RegistryStatus, Date)}:
 * <ul>
 *   <li>status = BLOCKED =&gt; expiration_date NOT NULL</li>
 *   <li>expiration_date NOT NULL =&gt; status = BLOCKED</li>
 * </ul>
 *
 * Un cliente BLOCKED cuya fecha aun no ha vencido se considera ACTIVO a efectos
 * de filtrado y de acceso.
 */
public final class RegistryExpirationUtils {
 
	private RegistryExpirationUtils() {}
 
	/**
	 * Estado efectivo a dia de hoy.
	 *
	 * ACTIVE / INACTIVE son estaticos. BLOCKED solo es bloqueo real cuando la
	 * fecha de expiracion ya ha vencido; hasta entonces cuenta como ACTIVE.
	 */
	public static RegistryStatus effective(RegistryStatus status, Date expirationDate) {
		RegistryStatus st = null == status ? RegistryStatus.ACTIVE : status;
 
		if (RegistryStatus.INACTIVE.equals(st))
			return RegistryStatus.INACTIVE;
 
		if (RegistryStatus.BLOCKED.equals(st))
			return isExpired(expirationDate) ? RegistryStatus.BLOCKED : RegistryStatus.ACTIVE;
 
		return RegistryStatus.ACTIVE;
	}
 
	/**
	 * Vencida = no esta en el futuro. Una fecha de hoy vence hoy; null se
	 * considera vencida (BLOCKED sin fecha bloquea de inmediato).
	 *
	 * Ojo: {@code AonDateUtils.isLessThanToday()} usa el criterio estricto
	 * contrario. Aqui usamos el inclusivo a proposito.
	 */
	public static boolean isExpired(Date expirationDate) {
		return !AonDateUtils.isFuture(expirationDate);
	}
 
	/**
	 * Fecha que hay que persistir en {@code customer.expiration_date} (y replicar
	 * en {@code domain.expirationDate}) para el estado indicado.
	 *
	 * ACTIVE / INACTIVE -&gt; null. BLOCKED -&gt; la fecha recibida sin hora, o hoy
	 * si viene vacia.
	 */
	public static Date normalizeExpirationDate(RegistryStatus status, Date expirationDate) {
		if (!RegistryStatus.BLOCKED.equals(status))
			return null;
 
		return null != expirationDate
				? AonDateUtils.getDateWithoutTime(expirationDate)
				: AonDateUtils.today();
	}
 
	/**
	 * Valor de {@code domain.active} para el estado indicado.
	 * Solo INACTIVE desactiva el dominio; BLOCKED lo deja activo con fecha.
	 */
	public static boolean domainActive(RegistryStatus status) {
		return !RegistryStatus.INACTIVE.equals(status);
	}
	
	/**
	 * Predicado SQL del estado EFECTIVO, equivalente a {@link #effective}.
	 *
	 * <pre>
	 *  ACTIVE   -> status IS NULL OR status = 0 OR (status = 2 AND expiration_date &gt;  hoy)
	 *  INACTIVE -> status = 1
	 *  BLOCKED  -> status = 2 AND (expiration_date IS NULL OR expiration_date &lt;= hoy)
	 * </pre>
	 *
	 * Si se piden varios estados se combinan con OR.
	 *
	 * @return null si la coleccion viene vacia (el llamante no debe filtrar)
	 */
	public static Filter effectiveStatusFilter(CustomerProperties f, Collection<RegistryStatus> statuses) {
		if (null == statuses || statuses.isEmpty())
			return null;
 
		java.sql.Date today = AonDateUtils.toSql(AonDateUtils.today());
 
		Filter result = null;
 
		for (RegistryStatus status : statuses) {
			Filter one = effectiveStatusFilter(f, status, today);
			result = (null == result) ? one : result.or(one);
		}
 
		return result;
	}
 
	private static Filter effectiveStatusFilter(CustomerProperties f, RegistryStatus status, java.sql.Date today) {
 
		if (RegistryStatus.INACTIVE.equals(status))
			return f.getStatusProperty().eq(RegistryStatus.INACTIVE.value());
 
		if (RegistryStatus.BLOCKED.equals(status))
			// NULL cuenta como vencida, igual que en isExpired(null)
			return f.getStatusProperty().eq(RegistryStatus.BLOCKED.value())
					.and(f.getExpirationDateProperty().isNull()
							.or(f.getExpirationDateProperty().le(today)));
 
		// ACTIVE: incluye status NULL (columna nullable, filas legacy)
		return f.getStatusProperty().isNull()
				.or(f.getStatusProperty().eq(RegistryStatus.ACTIVE.value()))
				.or(f.getStatusProperty().eq(RegistryStatus.BLOCKED.value())
						.and(f.getExpirationDateProperty().gt(today)));
	}
}
