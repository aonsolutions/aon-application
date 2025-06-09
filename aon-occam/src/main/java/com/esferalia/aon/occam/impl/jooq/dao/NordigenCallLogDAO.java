package com.esferalia.aon.occam.impl.jooq.dao;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.jooq.Record1;

import static com.esferalia.aon.jooq.tables.NordigenCallLog.NORDIGEN_CALL_LOG;

import com.esferalia.aon.occam.api.AONContext;

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
                reintentos != null ? reintentos : 4
            ).execute();
    }

    public static void insertCall(AONContext ctx, int domain, Integer rbankId, String callType, boolean wasRateLimited, LocalDateTime retryAfter) {
        insertCall(ctx, domain, rbankId, callType, wasRateLimited, retryAfter, null);
    }

    public static int getCallsMadeToday(AONContext ctx, int domain, Integer rbankId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        return ctx.getDslContext()
            .selectCount()
            .from(NORDIGEN_CALL_LOG)
            .where(NORDIGEN_CALL_LOG.DOMAIN.eq(domain))
            .and(NORDIGEN_CALL_LOG.RBANK.eq(rbankId))
            .and(NORDIGEN_CALL_LOG.CALL_TIME.ge(Timestamp.valueOf(startOfDay)))
            .and(NORDIGEN_CALL_LOG.CALL_TIME.lt(Timestamp.valueOf(endOfDay)))
            .fetchOne(0, int.class);
    }

    public static int getRemainingRetriesToday(AONContext ctx, int domain, Integer rbankId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        Record1<Integer> record = ctx.getDslContext()
            .select(NORDIGEN_CALL_LOG.RETRY_COUNT)
            .from(NORDIGEN_CALL_LOG)
            .where(NORDIGEN_CALL_LOG.DOMAIN.eq(domain))
            .and(NORDIGEN_CALL_LOG.RBANK.eq(rbankId))
            .and(NORDIGEN_CALL_LOG.CALL_TIME.ge(Timestamp.valueOf(startOfDay)))
            .and(NORDIGEN_CALL_LOG.CALL_TIME.lt(Timestamp.valueOf(endOfDay)))
            .orderBy(NORDIGEN_CALL_LOG.CALL_TIME.desc())
            .limit(1)
            .fetchOne();

        return record != null ? record.value1() : 4;
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
}


