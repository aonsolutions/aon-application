package net.aonsolutions.aon.bank.nordigen;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.occam.impl.jooq.dao.NordigenCallLogDAO;

public class NordigenRateLimiter {
    private static final int DAILY_LIMIT = 4;

    private NordigenRateLimiter() {
        throw new IllegalStateException("Utility class");
    }

    public static void checkAllowed(AONContext ctx, int domain, Integer rbankId, String callType) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime retryAfter = NordigenCallLogDAO.getLatestRetryAfter(ctx, domain, rbankId, "update");

        if (retryAfter != null && retryAfter.isAfter(now)) {
            throw new RuntimeException("Límite diario de llamadas alcanzado. Intente de nuevo después de: " + retryAfter);
        }

        int remaining = NordigenCallLogDAO.getRemainingRetriesToday(ctx, domain, rbankId);

        if (remaining <= 0) {
            throw new RuntimeException("Límite diario de llamadas alcanzado: " + DAILY_LIMIT + " intentos permitidos por día.");
        }

        System.out.println("Intentos restantes: " + remaining + " de " + DAILY_LIMIT);
    }

    public static void registerCall(AONContext ctx, int domain, Integer rbankId, String callType) {
        int remaining = NordigenCallLogDAO.getRemainingRetriesToday(ctx, domain, rbankId);

        // Reducimos el contador en 1 (si hay intentos restantes)
        int newRemaining = Math.max(remaining - 1, 0);

        NordigenCallLogDAO.insertCall(ctx, domain, rbankId, callType, false, null, newRemaining);
    }

    public static void handleRateLimitException(AONContext ctx, int domain, Integer rbankId, String callType, NordigenException e) {
        System.out.println("mensaje excepcion : " + e.getMessage());
        System.out.println(e.getResponse());
        e.printStackTrace();

        Integer retrySeconds = extractRetrySeconds(e.getMessage());
        LocalDateTime retryAfter = retrySeconds != null ? LocalDateTime.now().plusSeconds(retrySeconds) : null;

        // Cuando hay rate limit, fijamos los reintentos a 0 directamente
        NordigenCallLogDAO.insertCall(ctx, domain, rbankId, callType, true, retryAfter, 0);
    }

    public static int getRemainingCallsToday(AONContext ctx, int domain, Integer rbankId) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime retryAfter = NordigenCallLogDAO.getLatestRetryAfter(ctx, domain, rbankId, "update");
        if (retryAfter != null && retryAfter.isAfter(now)) {
            return 0;
        }

        return NordigenCallLogDAO.getRemainingRetriesToday(ctx, domain, rbankId);
    }

    private static Integer extractRetrySeconds(String message) {
        if (message == null) return null;
        try {
            Pattern pattern = Pattern.compile("try again in (\\d+) seconds");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception ex) {
            System.err.println("No se pudo extraer el tiempo de reintento: " + ex.getMessage());
        }
        return null;
    }
}
