package solutions.aon.seg.social.toolkit;

import static java.lang.Float.parseFloat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlDefinitionTerm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.UnfilledMandatory;

public class Toolkit {

	// LOGS AN ARRAY OF INFORMATION THROUGH CONSOLE
	public static void log(Object[] things) {
		for (Object th : things)
			if (th != null)
				System.out.println(th.toString());
			else
				System.out.println("[NOT FOUND]");
	}

	// REMOVE EXTRA 0s FROM STRINGS
	public static String removeExtraZeros(String code) {
		StringBuilder str = new StringBuilder();
		boolean extraZeros = true;
		for (int i = 0; i < code.length(); i++)
			if (!extraZeros || code.charAt(i) != '0') {
				extraZeros = false;
				str.append(code.charAt(i));
			}
		return str.toString();
	}

	// PARSE A DATE WITH AN SPECIFIC FORMAT
	public static Date parseDate(String dateStr, String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
		Date formattedDate;

		try {
			formattedDate = dateFormatter.parse(dateStr);
			return formattedDate;
		} catch (ParseException e) {
			return null;
		}
	}

	// FORMAT DATE TO STRING IN A SPECIFIC FORMAT
	public static Optional<String> formatDate(Date date, String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
		Optional<String> formattedDate;
		formattedDate = Optional.of(dateFormatter.format(date));
		return formattedDate;
	}

	public static String[] dateString(Date fecha) {
		String dia = "";
		String mes = "";
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(fecha);
		String anio = "" + (calendar.get(Calendar.YEAR));
		if (calendar.get(Calendar.DATE) < 10)
			dia = "0" + calendar.get(Calendar.DATE);
		else
			dia = "" + calendar.get(Calendar.DATE);
		if ((calendar.get(Calendar.MONTH) + 1) < 10)
			mes = "0" + (calendar.get(Calendar.MONTH) + 1);
		else
			mes = "" + (calendar.get(Calendar.MONTH) + 1);
		return new String[] { dia, mes, anio };
	}

	// GET DATE ARRAY [DD,MM,YYYY]
	public static Integer[] getDateArray(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return new Integer[] { calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1,
				calendar.get(Calendar.YEAR) };
	}

	// REMOVE NBFP CHARACTER FROMA A STRING
	public static String removeNBSP(String cadena) {
		if (cadena != null) {
			String nbe = "" + (char) 160;
			cadena = cadena.replace(nbe, "");
			return cadena;
		} else
			return null;
	}

	// RETURNS BOOLEAN FROM A STRING
	public static Boolean toBoolean(String bool) {
		if (bool != null && ((bool.equalsIgnoreCase("SI")) || (bool.equalsIgnoreCase("SÍ")))) {
			return true;
		} else if (bool != null && bool.equalsIgnoreCase("NO")) {
			return false;
		} else
			return null;
	}

	// SPLITS AN STRING BY 2 AND RETURNS AN ARRAY
	public static String[] SplitString(String str, int i) throws InvalidDataException {
		try {
			return new String[] { str.substring(0, i), str.substring(i) };
		} catch (StringIndexOutOfBoundsException e) {
			throw new InvalidDataException();
		}
	}

	// SPLITS AN ARRAY MULTIPLE TIMES AND RETURNS AN ARRAY
	public static ArrayList<String> splitStringMultiple(String str, int... indexes) throws InvalidDataException {
		try {
			ArrayList<String> result = new ArrayList<>();
			int ant = 0;
			for (int i : indexes) {
				String cut = str.substring(ant, i);
				ant = i;
				result.add(cut);
			}
			result.add(str.substring(ant));

			return result;
		} catch (StringIndexOutOfBoundsException e) {
			throw new InvalidDataException();
		}
	}

	// CONVERTS A BYTE ARRAY INTO A PDF FILE ON THE PROJECT FOLDER PATH
	public static void buildPdf(byte[] bytes, String docName) {
		File f = new File(docName + ".pdf");
		try {
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(bytes);
			fos.close();
		} catch (FileNotFoundException e) {
			System.err.println("Archivo no encontrado");
		} catch (IOException e) {
			System.err.println("Error al escribir");
		}

	}

	// BUILD A FILE FROM ARRAY OF BYTES
	public static void buildFile(byte[] bytes, String docName) {
		File f = new File(docName);
		try {
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(bytes);
			fos.close();
		} catch (FileNotFoundException e) {
			System.err.println("Archivo no encontrado");
		} catch (IOException e) {
			System.err.println("Error al escribir");
		}

	}

	// ADD DAYS TO A DATE
	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	// GET AN UREACHABLE DATE
	public static Date getUnreachableDate() {
		return addDays(new Date(), 10);
	}

	// RETURNS IF A DATE IS FUTURE
	public static boolean isFuture(Date to) {
		int r = to.compareTo(new Date());
		return r > 0;
	}

	// SPLITS A FLOAT/DOUBLE
	public static String[] splitDecimal(double dc, int decimals) {
		String decimalString = dc + "";
		return new String[] { decimalString.substring(0, decimalString.indexOf(".")),
				decimalString.substring(decimalString.indexOf(".") + 1, decimalString.indexOf(".") + decimals) };
	}

	// HANDLES EMPTY DATA
	public static void verifyData(Object[] data) throws InvalidDataException {
		for (Object o : data)
			if (o == null || (o instanceof String && ((String) o).trim().equals("")))
				throw new UnfilledMandatory();
	}

	// REMOVE WEIRD CARACTERS
	public static String removeWeirdCharacters(String str) {
		str = removeNBSP(str.replaceAll("" + ((char) 32), " ").trim());
		return str;
	}

	// REMOVE SPACES, ACTUALLY
	public static String noSpaces(String str) {
		return str.replaceAll("\\s", "");
	}

	// ADD A CHARACTER X TIMES TO A STRING LEFT SIDE
	public static String appendStringLeft(String str, String append, int times) {
		StringBuilder strBuilder = new StringBuilder(str);
		for (int i = 0; i < times; i++)
			strBuilder.insert(0, append);
		return strBuilder.toString();
	}

	// FILL A STRING WITH A CHARACTER (LEFT SIDE)
	public static String fillStringLeft(String str, String append, int max) {
		StringBuilder strBuilder = new StringBuilder(str);
		while (strBuilder.length() < max)
			strBuilder.insert(0, append);
		return strBuilder.toString();
	}

	public static DomNode getNextSibling(DomNode dt) {
		return dt.getNextElementSibling() instanceof HtmlDefinitionTerm
				? dt.getNextElementSibling().getNextElementSibling()
				: dt.getNextElementSibling();
	}

	public static Float parseStringToFloat(String d) {
		String newValue = removeNBSP(d.trim().replace(".", "").replace(',', '.'));
		return parseFloat(removeNBSP(newValue));
	}
	
	// CHECK DISPONIBILITY BEFORE TEST
	public static boolean checkSiteDisponibility(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String url) {
		
		final String[] possibleFailKeyWords = 	{"FUERA DE SERVICIO"
												, "SERVICIO APAGADO"
												, "TEMPORALMENTE"
												, "NO DISPONIBLE"
												, "EN MANTENIMIENTO"
												, "NO SE PUEDE ATENDER"};
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			HtmlPage htmlPage = webClient.getPage(url);
			if (htmlPage.getWebResponse().getStatusCode() >= 300)
				return false;
			String pageText = htmlPage.asXml().toUpperCase();
			
			for (String keyWord : possibleFailKeyWords) {
				if (pageText.contains(keyWord))
					return false;
			}
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
    public static KeyStore readStore(InputStream certificateInputStream, final String certificatePassword, final String certificateType) throws Exception {
        try (InputStream keyStoreStream = certificateInputStream) {
            KeyStore keyStore = KeyStore.getInstance(certificateType);
            keyStore.load(keyStoreStream, certificatePassword.toCharArray());
            return keyStore;
        }
    }
    
    public static String getValue(String body, String id) {
    	Pattern pattern = Pattern.compile("\\<\\w+?\\s*.*?id=('|\")" + id + "('|\").*?\\>", Pattern.CASE_INSENSITIVE);
    	Matcher matcher = pattern.matcher(body);
    	if (matcher.find()) {
    		String element = matcher.group();
    		pattern = Pattern.compile(".*?value=('|\")(?<value>.*?)('|\").*");
    		matcher = pattern.matcher(element);
    		if (matcher.find()) {
    			return matcher.group("value");
    		}
    	}
    	return null;
    }
    
    public static String getDIL(String body) {
    	Pattern pattern = Pattern.compile("\\<\\w+\\s*.*?id=('|\")DIL('|\").*?\\>(?<innertext>.*?)\\<\\/\\w+\\>", Pattern.CASE_INSENSITIVE);
		
		Matcher m = pattern.matcher(body);
		
		if (m.find()) {
			String match = m.group("innertext");
			return match != null ? match.trim() : null;
		} else
			return null;
    	
    }
    
    public static Integer getErrCode (final String DIL) {
    	if (DIL != null) {    		
    		Pattern pattern = Pattern.compile("\\**\\s*(?<code>\\d+)\\s*?\\**\\s*.*", Pattern.CASE_INSENSITIVE);
    		Matcher matcher = pattern.matcher(DIL);
    		if (matcher.matches()) {
    			String errStr = matcher.group("code");
    			try {
    				return Integer.parseInt(errStr);
    			} catch (NumberFormatException e) {
    				return null;
    			}
    		} else
    			return null;
    	} else
    		return null;
    }
    
    public static String getErrMsg (final String DIL) {
    	if (DIL != null) {    		
    		Pattern pattern = Pattern.compile("\\**\\s*(\\d+)\\s*?\\**\\s*(?<msg>.*)", Pattern.CASE_INSENSITIVE);
    		Matcher matcher = pattern.matcher(DIL);
    		if (matcher.matches()) {
    			return matcher.group("msg");
    		} else
    			return null;
    	} else
    		return null;
    }
    
    public static HttpPost reportGenerationForm (String body) throws UnsupportedEncodingException {
		String link = "https://w2.seg-social.es/ImprPDF/InSeNaCoder";
		
		HttpPost httpPost = new HttpPost(link);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		params.add(new BasicNameValuePair("param", Toolkit.getValue(body, "SDFFICHERO")));
		params.add(new BasicNameValuePair("trans",
				Toolkit.getValue(body, "SDFINFORMEA601")
			+ Toolkit.getValue(body, "SDFINFORMEA602")
			+ Toolkit.getValue(body, "SDFINFORMEA603")
			+ Toolkit.getValue(body, "SDFINFORMEA604")
		));
//		System.out.println(Toolkit.getValue(body, "SDFINFORMEA601"));
		params.add(new BasicNameValuePair("aplicacion", Toolkit.getValue(body, "SDFAPLICACION")));
		params.add(new BasicNameValuePair("usuario", Toolkit.getValue(body, "SDFUSUARIO")));
		params.add(new BasicNameValuePair("idioma", Toolkit.getValue(body, "SDFIDIOMA")));
		params.add(new BasicNameValuePair("fecha", Toolkit.getValue(body, "SDFFECHA") + " " + Toolkit.getValue(body, "SDFHORA")));
		params.add(new BasicNameValuePair("tipo", Toolkit.getValue(body, "SDFTIPO")));
		
		httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		return httpPost;
    }
	
	public static void checkResponseStatus (CloseableHttpResponse resp) throws SegSocialException {
		if (resp.getStatusLine().getStatusCode() != 200)
			throw new StatusCodeException(resp.getStatusLine().getStatusCode());
	}
	
	public static String getButtonNameByValue (String body, String value) {
		Pattern pattern = Pattern.compile("\\<input\\s*[^>]*?value=(\"|')" + value + "(\"|')[^>]*?\\/\\>", Pattern.CASE_INSENSITIVE);
		
		Matcher matcher = pattern.matcher(body);
		if (matcher.find()) {
			String element = matcher.group();
			Pattern namePattern = Pattern.compile("\\<input\\s*.*?name=(\"|')(?<name>.+?)(\"|').*?\\/\\>", Pattern.CASE_INSENSITIVE);
			matcher = namePattern.matcher(element);
			if (matcher.matches()) {
				return matcher.group("name");
			}
		}
		return null;
	}

}
