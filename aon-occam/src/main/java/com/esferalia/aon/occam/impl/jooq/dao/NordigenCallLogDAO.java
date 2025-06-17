package com.esferalia.aon.occam.impl.jooq.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

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
                reintentos != null ? reintentos : 3
            ).execute();
    }
    
    public static int getCallsMadeToday(AONContext ctx, int domain, Integer rbankId, String callType) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        return ctx.getDslContext()
            .selectCount()
            .from(NORDIGEN_CALL_LOG)
            .where(NORDIGEN_CALL_LOG.DOMAIN.eq(domain))
            .and(NORDIGEN_CALL_LOG.RBANK.eq(rbankId))
            .and(NORDIGEN_CALL_LOG.CALL_TYPE.eq(callType))
            .and(NORDIGEN_CALL_LOG.CALL_TIME.greaterOrEqual(Timestamp.valueOf(startOfDay)))
            .fetchOne(0, int.class);
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
    
    public static List<LocalDateTime> getRecentCallTimes(
            AONContext ctx, int domain, Integer rbankId, String callType) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusHours(24);

        return ctx.getDslContext()
            .select(NORDIGEN_CALL_LOG.CALL_TIME)
            .from(NORDIGEN_CALL_LOG)
            .where(NORDIGEN_CALL_LOG.DOMAIN.eq(domain))
            .and(NORDIGEN_CALL_LOG.RBANK.eq(rbankId))
            .and(NORDIGEN_CALL_LOG.CALL_TYPE.eq(callType))
            .and(NORDIGEN_CALL_LOG.CALL_TIME.greaterOrEqual(Timestamp.valueOf(from)))
            .orderBy(NORDIGEN_CALL_LOG.CALL_TIME.asc())
            .fetch(record -> record.get(NORDIGEN_CALL_LOG.CALL_TIME).toLocalDateTime());
    }
}
