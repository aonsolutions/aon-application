package com.esferalia.aon.watson.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AonValidationUtil extends com.esferalia.aon.watson.util.AonValidationUtil{
	
    private static final int MAX_USERNAME_LEN = 64;
    private static final String EMAIL_REGEX = "^\\s*?(.+)@(.+?)\\s*$";
	private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final String SPECIAL_CHARS = "\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]";
    private static final String VALID_CHARS = "(\\\\.)|[^\\s" + SPECIAL_CHARS + "]";
    private static final String QUOTED_USER = "(\"(\\\\\"|[^\"])*\")";
    private static final String WORD = "((" + VALID_CHARS + "|')+|" + QUOTED_USER + ")";
    private static final String USER_REGEX = "^\\s*" + WORD + "(\\." + WORD + ")*$";
    private static final Pattern USER_PATTERN = Pattern.compile(USER_REGEX);
    private static final String IP_DOMAIN_REGEX = "^\\[(.*)\\]$";
    private static final Pattern IP_DOMAIN_PATTERN = Pattern.compile(IP_DOMAIN_REGEX);

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        if (email.endsWith(".")) { // check this first - it's cheap!
            return false;
        }
        Matcher emailMatcher = EMAIL_PATTERN.matcher(email);
        if (!emailMatcher.matches()) {
            return false;
        }
        if (!isValidUser(emailMatcher.group(1))) {
            return false;
        }
        if (!isValidDomain(emailMatcher.group(2))) {
            return false;
        }
        return true;
    }

    public static boolean isValidUser(String user) {
        if (user == null || user.length() > MAX_USERNAME_LEN) {
            return false;
        }
        return USER_PATTERN.matcher(user).matches();
    }
    
    public static boolean isValidDomain(String domain) {
        Matcher ipDomainMatcher = IP_DOMAIN_PATTERN.matcher(domain);
        if (ipDomainMatcher.matches()) {
            InetAddressValidator inetAddressValidator =
                    InetAddressValidator.getInstance();
            return inetAddressValidator.isValid(ipDomainMatcher.group(1));
        }
        DomainValidator domainValidator = DomainValidator.getInstance();
        return domainValidator.isValid(domain);
    }
}

