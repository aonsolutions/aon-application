package net.aonsolutions.aon.bank.nordigen;

import java.time.LocalDateTime;
import java.util.concurrent.CompletionException; 
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

	public static boolean checkAllowed(AONContext ctx, int domain, Integer rbankId, String callType)
			throws NordigenException {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime retryAfter = NordigenCallLogDAO.getLatestRetryAfter(ctx, domain, rbankId, callType);

		if (retryAfter != null && retryAfter.isAfter(now)) {
			long secondsLeft = java.time.Duration.between(now, retryAfter).getSeconds();
			int madeCalls = NordigenCallLogDAO.getCallsMadeToday(ctx, domain, rbankId);
			if (madeCalls < DAILY_LIMIT) {
				int callsToInsert = DAILY_LIMIT - madeCalls;
				for (int i = 0; i < callsToInsert; i++) {
					NordigenCallLogDAO.insertCall(ctx, domain, rbankId, callType, true, retryAfter, DAILY_LIMIT);
				}
			}
			throw new NordigenException("Límite diario de llamadas alcanzado. Por favor, intente de nuevo en "
					+ secondsLeft + " segundos.");
		}
		return false;
	}

	public static void registerCall(AONContext ctx, int domain, Integer rbankId, String callType) {
		int madeCalls = NordigenCallLogDAO.getCallsMadeToday(ctx, domain, rbankId);
		NordigenCallLogDAO.insertCall(ctx, domain, rbankId, callType, false, null, madeCalls + 1);
	}

	public static void handleRateLimitException(AONContext ctx, int domain, Integer rbankId, String callType,
			Exception e) {

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

		NordigenCallLogDAO.insertCall(ctx, domain, rbankId, callType, true, retryAfter, DAILY_LIMIT);

		if (retryAfter != null) {
			int madeCalls = NordigenCallLogDAO.getCallsMadeToday(ctx, domain, rbankId);
			if (madeCalls < DAILY_LIMIT) {
				int callsToInsert = DAILY_LIMIT - madeCalls;
				for (int i = 0; i < callsToInsert; i++) {
					NordigenCallLogDAO.insertCall(ctx, domain, rbankId, callType, true, retryAfter, DAILY_LIMIT);
				}
			}
		}
	}

	private static Integer extractRetrySeconds(String message) {
		if (message == null)
			return null;
		try {
			Pattern pattern = Pattern.compile("(?:try again in|intente de nuevo en) (\\d+) seconds");
			Matcher matcher = pattern.matcher(message.toLowerCase());
			if (matcher.find()) {
				return Integer.parseInt(matcher.group(1));
			}
		} catch (Exception ex) {
			System.err.println(
					"No se pudo extraer el tiempo de reintento del mensaje: '" + message + "' - " + ex.getMessage());
		}
		return null;
	}
}