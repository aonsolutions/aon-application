package com.code.aon.aio.filter;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;

import org.apache.catalina.filters.RequestFilter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ThemeFilter extends RequestFilter {

	private static final String PATH_PARAM = "path";
	private static final String THEME_COOKIE_NAME = "aonTheme";
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			setThemeCookieIfNeeded(request, response);
		} catch (Exception e) {
			getLogger().error("Error setting theme cookie", e);
		}

		String path = getForwardPath(request);
		request.getRequestDispatcher(path).forward(request, response);
	}

	protected String getForwardPath( ServletRequest request) {
		if ( request instanceof HttpServletRequest httpRequest) {
			return AonStringUtils.defaultIfBlank(httpRequest.getParameter(PATH_PARAM), "/");
		}
		return "/";
	}
	
	@Override
	protected Log getLogger() {
		return LogFactory.getLog(ThemeFilter.class);
	}
	
	private static void setThemeCookieIfNeeded(ServletRequest request, ServletResponse response) {
		if (request instanceof HttpServletRequest httpRequest 
				&& notHasCookie(httpRequest, THEME_COOKIE_NAME)
				&& response instanceof HttpServletResponse httpResponse) {
			Locale locale = request.getLocale();
			getDefaultTheme(request).ifPresent(defaultTheme -> {
				Cookie cookie = new Cookie(THEME_COOKIE_NAME, defaultTheme);
				cookie.setPath("/");
				cookie.setMaxAge(getSecondsUntilMidnight(locale));
				httpResponse.addCookie(cookie);
			});
		}
	}
	
	private static Optional<String> getDefaultTheme(ServletRequest request) {
		String domainName = request.getServerName();
		
		Domain domain = AON.getDomain(domainName, 1, "", f -> f.getNameProperty().eq(domainName));

		return 
		AON.getApplicationParameterStream(
				domain.getName(), 
				domain.getId(), 
				"",  
				f -> f.getNameProperty().eq(AppParam.AON_DEFAULT_THEME.name())
				.and(f.getDomainProperty().in( new Integer[] {domain.getId(), domain.getParentId(),0 /*console*/ } ))
				)
		.filter(param -> AonStringUtils.isNotBlank(param.getValue()))
		.sorted((p1, p2) -> p2.getDomain().compareTo(p1.getDomain())) // sorted by domain descending, so the most specific one is first 
		.map(param -> param.getValue())
		.findFirst();
	}
	
	
	private static int getSecondsUntilMidnight(Locale locale) {
		Calendar calendar = Calendar.getInstance(locale);
		long nowMillis = calendar.getTimeInMillis();
		
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		long midnightMillis = calendar.getTimeInMillis();
		
		return (int) ((midnightMillis - nowMillis) / 1000);
		
	}
	
	private static boolean notHasCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if ( cookies == null || cookies.length == 0) {
			return true;
		}
		for (Cookie cookie : cookies) {
			if (cookie.getName().equalsIgnoreCase(name)) {
				return AonStringUtils.isBlank(cookie.getValue());
			}
		}
		return true;
	}

}