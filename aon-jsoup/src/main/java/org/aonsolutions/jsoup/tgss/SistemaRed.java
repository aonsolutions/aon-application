package org.aonsolutions.jsoup.tgss;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;

public class SistemaRed {

	/**
	 * Gets the list of IDC's dates.
	 * 
	 * @param certificateIs
	 * @param password
	 * @param naf
	 * @param regime
	 * @param ccc
	 * @return IDC Dates
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 * @throws ParseException
	 */

	public static Collection<Date> getIDCDates(byte[] certificateData, String password, String naf, String regime,
			String ccc, Date date)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, ParseException {
		try (InputStream is = new ByteArrayInputStream(certificateData)) {
			return IDCSegSocial.getIDCDates(is, password, naf, regime, ccc, date);
		}

	}

	public static Collection<Date> getIDCDates(InputStream certificateData, String password, String naf, String regime,
			String ccc, Date date)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, ParseException {

		return IDCSegSocial.getIDCDates(certificateData, password, naf, regime, ccc, date);

	}

	public static byte[] getIDC(byte[] certificateData, String password, String naf, String regime, String ccc,
			Date date)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, ParseException {
		try (InputStream is = new ByteArrayInputStream(certificateData)) {
			return IDCSegSocial.getIDC(is, password, naf, regime, ccc, date);
		}

	}

	public static byte[] getIDC(InputStream certificateData, String password, String naf, String regime, String ccc,
			Date date)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, ParseException {

		return IDCSegSocial.getIDC(certificateData, password, naf, regime, ccc, date);

	}

}
