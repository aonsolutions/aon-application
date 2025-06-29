package net.aonsolutions.aon.bank.nordigen;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletionException; 
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.occam.impl.jooq.dao.NordigenCallLogDAO;
import java.util.Map;

public class NordigenRateLimiter {
    private static final int LIMIT = 3;

    private NordigenRateLimiter() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Registra una llamada exitosa sin rate limit.
     */
    public static void registerCall(AONContext ctx, int domain, Integer rbankId, String callType, Integer reintentos) {
        NordigenCallLogDAO.insertCall(ctx, domain, rbankId, callType, false, null, reintentos);
    }

    /**
     * Registra un evento de rate limit. Se guarda con `retryAfter`
     */
    public static void handleRateLimitException(AONContext ctx, int domain, Integer rbankId, String callType, Exception e) {
        LocalDateTime retryAfter = null;
        String messageToParse = e.getMessage();
        Throwable cause = e.getCause();

        if (e instanceof CompletionException && cause != null) {
            messageToParse = cause.getMessage();
        }

        Integer retrySeconds = extractRetrySeconds(messageToParse);
        if (retrySeconds != null) {
            retryAfter = LocalDateTime.now().plusSeconds(retrySeconds);
        }

        NordigenCallLogDAO.insertCall(ctx, domain, rbankId, callType, true, retryAfter, LIMIT);
    }

    /**
     * Intenta extraer los segundos del mensaje de rate limit.
     */
    public static Integer extractRetrySeconds(String message) {
        if (message == null) return null;

        try {
            Pattern pattern = Pattern.compile("(?:try again in|intente de nuevo en) (\\d+) seconds");
            Matcher matcher = pattern.matcher(message.toLowerCase());
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception ex) {
            System.err.println("No se pudo extraer el tiempo de reintento del mensaje: '" + message + "' - " + ex.getMessage());
        }
        return null;
    }
}
