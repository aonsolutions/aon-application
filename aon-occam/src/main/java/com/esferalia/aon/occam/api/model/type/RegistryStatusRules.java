package com.esferalia.aon.occam.api.model.type;

import java.util.Date;

/**
 * Regla de estado efectivo de un cliente. Compartida entre servidor y GWT.
 *
 * <pre>
 *  status 0 ACTIVE   -> nunca lleva fecha -> ACTIVE
 *  status 1 INACTIVE -> nunca lleva fecha -> INACTIVE
 *  status 2 BLOCKED  -> lleva SIEMPRE fecha:
 *       fecha  > hoy -> ACTIVE  (el bloqueo aun no ha llegado)
 *       fecha <= hoy -> BLOCKED
 *  status nulo -> ACTIVE (criterio conservador)
 * </pre>
 *
 * IMPORTANTE: esta clase solo puede usar API emulada por GWT. No anadir
 * Calendar, LocalDate ni utilidades de com.esferalia.aon.watson.server.
 */
public final class RegistryStatusRules {
 
	private RegistryStatusRules() {}
 
	public static RegistryStatus effective(RegistryStatus status, Date expirationDate) {
		RegistryStatus st = null == status ? RegistryStatus.ACTIVE : status;
 
		if (RegistryStatus.INACTIVE.equals(st))
			return RegistryStatus.INACTIVE;
 
		if (RegistryStatus.BLOCKED.equals(st))
			return isExpired(expirationDate) ? RegistryStatus.BLOCKED : RegistryStatus.ACTIVE;
 
		return RegistryStatus.ACTIVE;
	}
 
	/** Vencida = no esta en el futuro. Una fecha nula se considera vencida. */
	public static boolean isExpired(Date expirationDate) {
		if (null == expirationDate)
			return true;
 
		return !truncate(expirationDate).after(truncate(new Date()));
	}
 
	/** Solo INACTIVE apaga el dominio; BLOCKED lo deja activo con fecha. */
	public static boolean domainActive(RegistryStatus status) {
		return !RegistryStatus.INACTIVE.equals(status);
	}
 
	/** true si es un bloqueo programado que aun no ha entrado en vigor. */
	public static boolean isBlockScheduled(RegistryStatus status, Date expirationDate) {
		return RegistryStatus.BLOCKED.equals(status) && !isExpired(expirationDate);
	}
 
	/**
	 * Medianoche del dia de la fecha dada.
	 *
	 * Se usan los getters obsoletos de Date a proposito: son los unicos que
	 * GWT emula. Calendar no esta disponible en cliente.
	 */
	@SuppressWarnings("deprecation")
	private static Date truncate(Date date) {
		return new Date(date.getYear(), date.getMonth(), date.getDate());
	}
}