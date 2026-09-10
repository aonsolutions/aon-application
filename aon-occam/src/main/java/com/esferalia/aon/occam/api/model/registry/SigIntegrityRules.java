package com.esferalia.aon.occam.api.model.registry;

import java.util.Date;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.server.AonDateUtils;

/**
 * Reglas de alineacion de estados cliente/dominio para despachos SIG.
 *
 * El estado almacenado del CLIENTE manda. Lo unico que viaja del dominio al
 * cliente es la fecha, y solo cuando el cliente esta BLOCKED sin fecha propia.
 *
 * <pre>
 *  cliente   | domain.active | domain.expirationDate | customer.expirationDate
 *  ----------+---------------+-----------------------+------------------------
 *  ACTIVE    | 1             | NULL                  | NULL
 *  INACTIVE  | 0             | NULL                  | NULL
 *  BLOCKED   | 1             | fecha                 | misma fecha
 * </pre>
 */
public final class SigIntegrityRules {

    private SigIntegrityRules() {}

    /**
     * Fecha que deben compartir cliente y dominio tras alinear.
     * BLOCKED: la del cliente si la tiene, si no la del dominio, si no hoy.
     * ACTIVE / INACTIVE: null (el INACTIVE con fecha hay que limpiarlo).
     */
    public static Date targetDate(RegistryStatus customerStatus, Date customerDate, Date domainDate) {
        Date base = null != customerDate ? customerDate : domainDate;
        return RegistryExpirationUtils.normalizeExpirationDate(customerStatus, base);
    }

    public static boolean targetDomainActive(RegistryStatus customerStatus) {
        return RegistryExpirationUtils.domainActive(customerStatus);
    }

    /**
     * Unico punto de verdad para "se puede entrar hoy al dominio".
     * Usa Domain.isExpired() a proposito: cuando se corrija el criterio de
     * vencimiento, solo hay que tocar aqui.
     */
    public static boolean domainAccessible(Domain domain) {
        return null != domain && domain.isActive() && !domain.isExpired();
    }

    public static boolean sameDay(Date a, Date b) {
        if (null == a && null == b) return true;
        if (null == a || null == b) return false;
        return AonDateUtils.isSameDay(a, b);
    }
}