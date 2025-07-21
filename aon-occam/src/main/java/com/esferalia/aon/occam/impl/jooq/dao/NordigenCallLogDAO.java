package com.esferalia.aon.occam.impl.jooq.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.jooq.Record1;

import static com.esferalia.aon.jooq.tables.NordigenCallLog.NORDIGEN_CALL_LOG;

import com.esferalia.aon.occam.api.AONContext;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

public class NordigenCallLogDAO {

    private NordigenCallLogDAO() {
        throw new IllegalStateException("Utility class");
    }

    public static void insertCall(AONContext ctx, int domain, Integer rbankId, String callType, boolean wasRateLimited, LocalDateTime retryAfter, Integer reintentos) {
        ctx.getDslContext()
            .insertInto(NORDIGEN_CALL_LOG,
                NORDIGEN_CALL_LOG.DOMAIN,
                NORDIGEN_CALL_LOG.RBANK,
                NORDIGEN_CALL_LOG.CALL_TYPE,
                NORDIGEN_CALL_LOG.CALL_TIME,
                NORDIGEN_CALL_LOG.WAS_RATE_LIMITED,
                NORDIGEN_CALL_LOG.RETRY_AFTER,
                NORDIGEN_CALL_LOG.RETRY_COUNT)
            .values(
                domain,
                rbankId,
                callType,
                Timestamp.valueOf(LocalDateTime.now()),
                wasRateLimited ? (byte) 1 : (byte) 0,
                retryAfter != null ? Timestamp.valueOf(retryAfter) : null,
                reintentos != null ? reintentos : 3
            ).execute();
    }
 
 public static int getCallsMadeToday(AONContext ctx, int domain, Integer rbankId, String callType) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twentyFourHoursAgo = now.minusHours(24); // Hace 24 horas desde el momento actual

        // Consultar la cantidad de llamadas y el último retry_count dentro de las últimas 24 horas
        Integer retryCount = ctx.getDslContext()
            .select(NORDIGEN_CALL_LOG.RETRY_COUNT)
            .from(NORDIGEN_CALL_LOG)
            .where(NORDIGEN_CALL_LOG.DOMAIN.eq(domain))
            .and(NORDIGEN_CALL_LOG.RBANK.eq(rbankId))
            .and(NORDIGEN_CALL_LOG.CALL_TYPE.eq(callType))
            .and(NORDIGEN_CALL_LOG.CALL_TIME.greaterOrEqual(Timestamp.valueOf(twentyFourHoursAgo)))
            .orderBy(NORDIGEN_CALL_LOG.CALL_TIME.desc())  // Ordenar por el más reciente
            .limit(1)  // Solo tomamos el más reciente
            .fetchOne(NORDIGEN_CALL_LOG.RETRY_COUNT);

        // Si no se encuentra retryCount (por ejemplo, si no hay llamadas en las últimas 24 horas)
        if (retryCount == null) {
            return 0;  // Si no se encontraron llamadas
        }
        
        return retryCount + 1;
    }

    
    public static LocalDateTime getLatestRetryAfter(AONContext ctx, int domain, Integer rbankId, String callType) {
        Record1<Timestamp> record = ctx.getDslContext()
            .select(NORDIGEN_CALL_LOG.RETRY_AFTER)
            .from(NORDIGEN_CALL_LOG)
            .where(NORDIGEN_CALL_LOG.DOMAIN.eq(domain))
            .and(NORDIGEN_CALL_LOG.RBANK.eq(rbankId))
            .and(NORDIGEN_CALL_LOG.CALL_TYPE.eq(callType))
            .and(NORDIGEN_CALL_LOG.RETRY_AFTER.isNotNull())
            .orderBy(NORDIGEN_CALL_LOG.CALL_TIME.desc())
            .limit(1)
            .fetchOne();

        return record != null ? record.value1().toLocalDateTime() : null;
    }
    
    public static void resetCalls(AONContext ctx, int domain, Integer rbankId, String callType) {
        ctx.getDslContext()
           .deleteFrom(NORDIGEN_CALL_LOG)
           .where(NORDIGEN_CALL_LOG.DOMAIN.eq(domain))
           .and(NORDIGEN_CALL_LOG.RBANK.eq(rbankId))
           .and(NORDIGEN_CALL_LOG.CALL_TYPE.eq(callType))
           .execute();
    }

    public static Map<Integer, LocalDateTime> getRecentCallTimes(
      AONContext ctx, int domain, Integer rbankId, String callType
    ) {
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime from = now.minusHours(24);

      return ctx.getDslContext()
          .select(NORDIGEN_CALL_LOG.CALL_TIME, NORDIGEN_CALL_LOG.RETRY_COUNT)
          .from(NORDIGEN_CALL_LOG)
          .where(NORDIGEN_CALL_LOG.DOMAIN.eq(domain))
          .and(NORDIGEN_CALL_LOG.RBANK.eq(rbankId))
          .and(NORDIGEN_CALL_LOG.CALL_TYPE.eq(callType))
          .and(NORDIGEN_CALL_LOG.CALL_TIME.greaterOrEqual(Timestamp.valueOf(from)))
          .orderBy(NORDIGEN_CALL_LOG.CALL_TIME.asc())
          .fetch(record -> new AbstractMap.SimpleEntry<>(
              record.get(NORDIGEN_CALL_LOG.RETRY_COUNT),
              record.get(NORDIGEN_CALL_LOG.CALL_TIME).toLocalDateTime()
          ))
          .stream()
          .collect(Collectors.toMap(
              AbstractMap.SimpleEntry::getKey,
              AbstractMap.SimpleEntry::getValue
          ));
    }

    public static LocalDateTime getRecentCallTimesFail(
      AONContext ctx, int domain, Integer rbankId
    ) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusHours(24);

        return ctx.getDslContext()
            .select(NORDIGEN_CALL_LOG.CALL_TIME)
            .from(NORDIGEN_CALL_LOG)
            .where(NORDIGEN_CALL_LOG.DOMAIN.eq(domain))
            .and(NORDIGEN_CALL_LOG.RBANK.eq(rbankId))
            .and(NORDIGEN_CALL_LOG.CALL_TYPE.eq("fail_update"))
            .and(NORDIGEN_CALL_LOG.CALL_TIME.greaterOrEqual(Timestamp.valueOf(from)))
            .orderBy(NORDIGEN_CALL_LOG.CALL_TIME.desc())
            .limit(1)
            .fetchOne(record -> record.get(NORDIGEN_CALL_LOG.CALL_TIME).toLocalDateTime());
    }

}
