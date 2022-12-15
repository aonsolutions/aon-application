package solutions.aon.seg.social.toolkit;

import static java.lang.Float.parseFloat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlDefinitionTerm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import solutions.aon.seg.social.IServicioRedConstants;
import solutions.aon.seg.social.ServicioREDRegeXML;
import solutions.aon.seg.social.exception.ReportTooLongException;
import solutions.aon.seg.social.exception.RevokedCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.DataDoesNotExist;
import solutions.aon.seg.social.exception.invalid.InvalidCccException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.LiquidationDoesNotExist;
import solutions.aon.seg.social.exception.invalid.UnfilledMandatory;
import solutions.aon.seg.social.exception.invalid.WrongRegimeException;
import solutions.aon.seg.social.object.Idc;
import solutions.aon.seg.social.object.WorkerLiquidation;
import solutions.aon.seg.social.object.WorkerLiquidation.WorkerLiquidationBuilder;

public class Toolkit {
	
	private static final String PROSA_ERR = "\\<div[^<>]*?id=(\"|')ARQContenMensaje(\"|')[^<>]*\\>.*?\\<li[^<>]*?class=(\"|')mensajeError(\"|')[^<>]*\\>(?<error>[^<>]*?)\\<\\/li\\>.*?\\<\\/div\\>";
	private static final Pattern FORM_PATTERN = Pattern.compile("\\<form.*action=\"(?<link>.+?SessionId=(?<session>[^\\&]+?)\\&.*?)\"");
	private static final Pattern FORM_PATTERN_PROSA = Pattern.compile("\\<form.*action=\"(?<link>[^\"']+?ARQ\\.SPM\\.TICKET=(?<ticket>[^\\&]+?)\\&.*?)\"");

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
	
	// ADD DAYS TO A DATE
	public static Date addMonth(Date date, int month) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, month);
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
	
	public static String safeRemoveWeirdCharacters(String str) {
		try {
			str = removeNBSP(str.replaceAll("" + ((char) 32), " ").trim());
			return str;			
		} catch (NullPointerException e) {
			return null;
		}
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
		if(d!=null) {
			String newValue = removeNBSP(d.trim().replace(".", "").replace(',', '.'));
			return parseFloat(removeNBSP(newValue));
		}
		return null;
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
    
    public static SSLContext getSSLContext (InputStream certificateInputStream, String certificatePassword, String certificateType) throws KeyStoreException, Exception {
		KeyStore keyStore = Toolkit.readStore(certificateInputStream, certificatePassword, certificateType);
//    	TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//		tmf.init((KeyStore)null);
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		char[] pass = certificatePassword.toCharArray();
		kmf.init(keyStore, pass);
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(kmf.getKeyManagers(), null, null);
		return sslContext;
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
    
    public static String getInnerText(String body, String id) {
    	Pattern pattern = Pattern.compile("\\<\\w+?\\s*.*?id=('|\")" + id + "('|\").*?\\>(?<innertext>[^<>]*)\\<\\/\\w*?\\>", Pattern.CASE_INSENSITIVE);
    	Matcher matcher = pattern.matcher(body);
    	if (matcher.find()) {
    		return matcher.group("innertext") != null ? matcher.group("innertext").trim() : null;
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
		params.add(new BasicNameValuePair("aplicacion", getValue(body, "SDFAPLICACION")));
		params.add(new BasicNameValuePair("usuario", getValue(body, "SDFUSUARIO")));
		params.add(new BasicNameValuePair("idioma", getValue(body, "SDFIDIOMA")));
		params.add(new BasicNameValuePair("fecha", getValue(body, "SDFFECHA") + " " + getValue(body, "SDFHORA")));
		params.add(new BasicNameValuePair("tipo", getValue(body, "SDFTIPO")));
		
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
	
	public static Map<String, String> getEverythingWithId(String body) {
		BufferedReader buffer = new BufferedReader(new StringReader(body));
		String line = null;
		Map<String, String> map = new HashMap<String, String>();
		Pattern pattern = Pattern.compile("\\<[^<>]*?id=(\"|')(?<id>[^\"<>']+?)(\"|')[^<>]*\\>(?<innertext>[^<>]*)\\<[^<>]+\\>", Pattern.CASE_INSENSITIVE);
		
		try {
			while((line = buffer.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.matches()) {
					String id = matcher.group("id") != null ? Toolkit.removeNBSP(matcher.group("id")).trim() : null;
					String value = matcher.group("innertext") != null ? Toolkit.removeNBSP(matcher.group("innertext")).trim() : null;
					map.put(id, value);
				}
			}
			return map;
		} catch (IOException e) {
			return null;
		}
	}
	
	public static Collection<Idc> getIDCDatesByRegex(String body) {
		DateFormat df = new SimpleDateFormat("dd MM yyyy", new Locale("es", "ES"));
		BufferedReader buffer = new BufferedReader(new StringReader(body));
		String line = null;
		Collection<Idc> idcs = new ArrayList<Idc>();
		Pattern pattern = Pattern.compile("\\<label\\s*.*?id=(\"|')(?<id>Sub0900112078_(?<col>\\d+)_\\d+)(\"|')[^<>]*\\>(?<date>\\s*\\d+\\s*\\d+\\s*\\d+\\s*)\\<\\/label\\>", Pattern.CASE_INSENSITIVE);
		
		try {
			while((line = buffer.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.matches()) {
					String col = matcher.group("col") != null ? Toolkit.removeNBSP(matcher.group("col")).trim() : null;
					String dateStr = matcher.group("date") != null ? Toolkit.removeNBSP(matcher.group("date")).trim() : null;
					String description = null;
					if (col.equals("1")) {
						description = "ALTA";
					} else if (col.equals("2")) {
						description = "BAJA";
					}
					
					try {
						Date date = df.parse(dateStr);
						idcs.add(new Idc(description, date));
					} catch (ParseException e) {
					}
					
				}
			}
			return idcs;
		} catch (IOException e) {
			return null;
		}
	}
	
	public static int getNumberOfLiquidations(String body) {
		Pattern pattern = Pattern.compile("\\<input.*?name=(\"|')LIQUIDACION(\"|')", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(body);
		int counter = 0;
		while (matcher.find()) {
			counter++;
		}
		return counter;
		
	}
	
	public static String getLiqType(String body) {
		Pattern pattern = Pattern.compile("T\\.\\s*LIQ:\\s*\\<\\/strong\\>[^<>]*\\<\\/abbr\\>(?<liqtype>[^<>]*)\\<\\/p\\>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(body);
		if (matcher.find()) {
			String ret = matcher.group("liqtype");
			return safeRemoveWeirdCharacters(ret);
		}
		return null;
	}
	
	public static String getCalculationTable (String body) {
		Pattern pattern = Pattern.compile("\\<table\\>.+?(\\<th.*?\\>Base\\<\\/th\\>).+?\\<\\/table>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(body);
		try {			
			return matcher.find() ? matcher.group().replaceAll("\\&nbsp;", "").replaceAll("\\&euro;", "") : null;
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	public static String getTable (String body) {
		Pattern pattern = Pattern.compile("\\<table[^<>]*\\>.+?\\<\\/table>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(body);
		try {			
			return matcher.find() ? matcher.group().replaceAll("\\&nbsp;", "").replaceAll("\\&euro;", "") : null;
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	public static Collection<String> getTrs (String html) {
		if (html == null)
			return new ArrayList<>();
		Pattern pattern = Pattern.compile("\\<tr\\>.*?\\<\\/tr\\>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(html);
		
		Collection<String> collection = new ArrayList<>();
		
		while (matcher.find()) {
			collection.add(matcher.group());
		}
		return collection;
	}

	public static Collection<String> getTds (String html) {
		Pattern pattern = Pattern.compile("\\<td\\>.*?\\<\\/td\\>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(html);
		
		Collection<String> collection = new ArrayList<String>();
		
		while (matcher.find()) {
			collection.add(matcher.group());
		}
		return collection;
	}
	
	public static List<String> getTdsTexts (String html) {
		Pattern pattern = Pattern.compile("\\<td[^<>]*\\>(?<inner>.*?)\\<\\/td\\>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(html);
		
		LinkedList<String> collection = new LinkedList<>();
		
		while (matcher.find()) {
			
			String inner = matcher.group("inner");
			inner = Toolkit.removeTags(inner);
			inner = inner != null ? Toolkit.removeNBSP(inner).trim() : inner;
			
			collection.add(inner);
		}
		return collection;
	}
	
	public static String removeTags (String text) {
		if (text != null) {
			return text.replaceAll("\\<[^<>]*?\\>", "");
		}
		return null;
	}
	
	public static Integer strToInteger(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NullPointerException | NumberFormatException e) {
			return null;
		}
	}
	
	public static Float strToFloat(String str) {
		try {
			return Float.parseFloat(str.replaceAll("\\.", "").replaceAll(",", "."));
		} catch (NullPointerException | NumberFormatException e) {
			return null;
		}
	}
	
	public static Double strToDouble(String str) {
		try {
			return Double.parseDouble(str.replaceAll("\\.", "").replaceAll(",", "."));
		} catch (NullPointerException | NumberFormatException e) {
			return null;
		}
	}
	
	public static <E> E safeGet(List<E> list, int index) {
		try {
			return list.get(index);			
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	static void checkLiquidationExceptions(String errText) throws LiquidationDoesNotExist, DataDoesNotExist,
	WrongRegimeException, InvalidCccException, UnfilledMandatory, NullPointerException, ElementNotFoundException {
		if (errText != null) {
			if(errText.toUpperCase().contains("NO EXISTE LIQUIDACI"))
				throw new LiquidationDoesNotExist();
			else if(errText.toUpperCase().contains("NO EXISTEN DATOS"))
				throw new DataDoesNotExist();
			else if(errText.toUpperCase().contains("CUENTA DE COTIZACI") && errText.toUpperCase().contains("N NO EXISTE"))
				throw new WrongRegimeException();
			else if(errText.toUpperCase().contains("C.C.C. ERR"))
				throw new InvalidCccException();
			else if(errText.toUpperCase().contains("DEBE TENER CONTENIDO"))
				throw new UnfilledMandatory();
			else if(errText.toUpperCase().contains("EL CCC NO PERTENECE AL COLECTIVO DE CLEGIOS CONCERTADOS"))
				throw new UnfilledMandatory();
		}
	}
	
	public static void checkProsaError(String body) throws SegSocialException{
		Pattern pattern = Pattern.compile(PROSA_ERR, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(body);
		String errText = null;
		if (matcher.find()) {
			errText = matcher.group("error");
			checkLiquidationExceptions(errText);
		}
	}

	public static void checkTooLong(String body) throws SegSocialException {
		Pattern pattern = Pattern.compile("informe\\s*requerido\\s*excede\\s*el\\s*l.mite", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(body);
		if (matcher.find()) {
			throw new ReportTooLongException("El informe requerido excede el límite de información de transmisión permitido. Solicítelo en diferido o en su Administración habitual.");
		}
	}
	
	public static int howManyNafs (String body) {
		if (body == null)
			return 0;
		Pattern pattern = Pattern.compile("\\<input[^<>]*?name=(\"|')NAF(\"|')[^<>]*\\>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(body);
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		return count;
	}
	
	public static String goBack(CloseableHttpClient httpClient, String link, String ticket) throws ClientProtocolException, IOException, SegSocialException {
		HttpPost httpPost = new HttpPost(link);
		
		ArrayList<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("ARQ.SPM.TICKET", ticket));
		params.add(new BasicNameValuePair("SPM.CONTEXT", "internet"));
		params.add(new BasicNameValuePair("SPM.PORTALTYPE", "HTML"));
		params.add(new BasicNameValuePair("SPM.ACC.ATRAS", "Atr%E1s"));
		
		httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		
		try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
			Toolkit.checkResponseStatus(resp);
			HttpEntity entity = resp.getEntity();
		
			if (entity != null) {
				String body = EntityUtils.toString(entity, "UTF-8");
				
				String error = Toolkit.getDIL(body);
				if (Toolkit.getErrCode(error) != null)
					InvalidDataException.checkCode(Toolkit.getErrCode(error), Toolkit.getErrMsg(error));
				return body;
			}
		}
		return null;
	}
	
	public static String getNaf (String body) {
		Pattern pattern = Pattern.compile("NAF:\\s*[^\\d]*(?<naf>\\d*)\\<", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(body);
		if(matcher.find()) {
			return matcher.group("naf");
		}
		return null;
	}
	
	public static Matcher find(BufferedReader reader, Pattern pattern) throws IOException, SegSocialException {

		String line;
		while ((line = reader.readLine()) != null) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.matches()) {
				// System.out.println(line);
				continue;
			}

			return matcher;
		}

		throw new SegSocialException("Error parsing page");

	}
	
	public static WorkerLiquidation getWorkerLiquidation (String body) throws IOException, SegSocialException {
		WorkerLiquidationBuilder wlb = new WorkerLiquidationBuilder();
		if (body != null) {			
			//<abbr title="N?mero de afiliaci?n a la Seguridad Social">NAF: </abbr>010019805355</div>
			String nafcaf = "\\s*\\<abbr[^<>]*\\>\\s*(?<type>\\wAF):\\s*\\<[^<>]*\\>(?<nafcaf>[^<>]*)\\<[^<>]*\\>\\s*";
			
			Pattern pattern = Pattern.compile(nafcaf, Pattern.CASE_INSENSITIVE);
			
			Matcher matcher = pattern.matcher(body);
			
			while (matcher.find()) {
				if (matcher.group("type") != null) {
					if (matcher.group("type").equalsIgnoreCase("naf")) {
						wlb.setNss(Toolkit.removeExtraZeros(Toolkit.safeRemoveWeirdCharacters(matcher.group("nafcaf"))));
					} else if (matcher.group("type").equalsIgnoreCase("caf")) {
						wlb.setCaf(Toolkit.safeRemoveWeirdCharacters(matcher.group("nafcaf")));
					}
				}
			}
			String table = Toolkit.getCalculationTable(body);
			
			Collection<String> trs = Toolkit.getTrs(table);
			ServicioREDRegeXML.workerLiquidationDataType(wlb, trs);
		}
		return wlb.build();
	}
	
	public static String getBodyGET(CloseableHttpClient httpClient, String link) throws SegSocialException, IOException {
		String html = null;
		try (CloseableHttpResponse resp = httpClient.execute(new HttpGet(link))) {
			Toolkit.checkResponseStatus(resp);
			HttpEntity entity = resp.getEntity();
			if (entity != null) {
				html = EntityUtils.toString(resp.getEntity(), IServicioRedConstants.ISO_8859_1);
				Toolkit.checkCertificateRevoked(html);
			}
		}
		return html;
	}

	public static String getBodyPOST(CloseableHttpClient httpClient, HttpPost httpPost) throws SegSocialException, IOException {
		try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
			Toolkit.checkResponseStatus(resp);
			HttpEntity entity = resp.getEntity();
			if (entity != null) {
				return EntityUtils.toString(entity, IServicioRedConstants.ISO_8859_1);
			}
			return null;
		}
	}
	
	public static String getLinkPROSA(String body) {
		Matcher matcher = FORM_PATTERN_PROSA.matcher(body);
		if (matcher.find()) {
			String params = matcher.group("link").replace("&amp;", "&");
			return "https://w2.seg-social.es/" + params;
		}
		return null;
	}
	
	public static void checkCertificateRevoked(String body) throws SegSocialException {
		if(body!=null) {
			String element = getElementByAttribute(body, "src", "revokedError.jpg");
			if(element!=null) {
				throw new RevokedCertificateException("Certificado revocado");
			}
		}
	}
	
	public static String getLink(String body) {
		Matcher matcher = FORM_PATTERN.matcher(body);
		if (matcher.find()) {
			String params = matcher.group("link").replace("&amp;", "&");
			return "https://w2.seg-social.es/" + params;
		}
		return null;
	}
	
	public static String getSessionId(String body) {
		Matcher matcher = FORM_PATTERN.matcher(body);
		if (matcher.find()) {
			return matcher.group(IServicioRedConstants.SESSION);
		}
		return null;
	}
	
	public static String getTicket(String body) {
		Matcher matcher = FORM_PATTERN_PROSA.matcher(body);
		if (matcher.find()) {
			return matcher.group("ticket");
		}
		return null;
	}
	
	public static List<String> getRelations(String body) {
		String regex = "\\<tr\\>.*?\\<input\\s*[^<>]*?name=\"TRAMO\"[^<>]*\\>.*?\\<\\/tr\\>";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(body);
		LinkedList<String> list = new LinkedList<>();
		while (matcher.find()) {
			String content = safeRemoveWeirdCharacters(matcher.group());
			content = content != null ? content.replace("&nbsp;", "") : content;
			list.add(content);
		}
		return list;
	}
	
	public static boolean isThereProsaError(String body) {
		Pattern pattern = Pattern.compile(PROSA_ERR, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(body);
		return matcher.find();
	}
	
	public static String changeAccent(String description) {
		return description.replaceAll("FORMACI.?N", "FORMACION");
	}
	
	public static boolean idExists(String id, String body) {
		Pattern pattern = Pattern.compile("id=(\"|')" + id + "(\"|')", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(body);
		return matcher.find();
	}
	
	public static Map<String, String> getNafValues (String body) {
		Map<String, String> nafValues = new LinkedHashMap<>();
		String regex = "\\<input\\s*[^>]*?name=\"NAF\"[^>]*\\>";
		String valueRegex = "value=[\"'](?<value>[^\"'])[\"']";
		String nafRegex = "title=\"Seleccionar\\s*el\\s*NAF\\s*(?<naf>\\d+)\"";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Pattern valuePattern = Pattern.compile(valueRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Pattern nafPattern = Pattern.compile(nafRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher= pattern.matcher(body);
		while (matcher.find()) {		
			String match = matcher.group();
			Matcher valueMatcher = valuePattern.matcher(match);
			if (valueMatcher.find()) {
				String value = valueMatcher.group("value");
				Matcher nafMatcher = nafPattern.matcher(match);
				if (nafMatcher.find()) {
					String naf = nafMatcher.group("naf");
					nafValues.put(naf, value);
				}
			}
		}
		return nafValues;
	}
	
	public static String getElementByAttributeFirstTag(String body, String attributeName, String attributeValue) {
		if (attributeName == null || attributeName.isEmpty() || attributeValue == null) {
			return null;
		}
		String regex = "\\<[^<>/]+" + attributeName + "=[\"']" + attributeValue + "[\"'][^<>]*\\>";
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(body);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}
	
	public static List<String> getElementsByAttributeFirstTag(String body, String attributeName, String attributeValue) {
		if (attributeName == null || attributeName.isEmpty() || attributeValue == null) {
			return Collections.emptyList();
		}
		LinkedList<String> elementList = new LinkedList<>();
		String regex = "\\<[^<>/]+" + attributeName + "=[\"']" + attributeValue + "[\"'][^<>]*\\>";
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(body);
		while (matcher.find()) {
			elementList.add(matcher.group());
		}
		return elementList;
	}
	
	public static List<String> getElementsContainingAttributeFirstTag(String body, String attributeName, String stringContained) {
		if (attributeName == null || attributeName.isEmpty() || stringContained == null) {
			return Collections.emptyList();
		}
		LinkedList<String> elementList = new LinkedList<>();
		String regex = "\\<[^<>/]+" + attributeName + "=[\"'][^\"'<>]" + stringContained + "[^\"'<>][\"'][^<>]*\\>";
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(body);
		while (matcher.find()) {
			elementList.add(matcher.group());
		}
		return elementList;
	}
	
	public static String getElementByAttribute(String body, String attributeName, String attributeValue) {
		if (attributeName == null || attributeName.isEmpty() || attributeValue == null) {
			return null;
		}
		String regex = "\\<[^<>]+" + attributeName + "=[\"']" + attributeValue + "[\"'][^<>]*(\\/\\>|\\>[^<>]*\\<[^<>]*\\>)";
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(body);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}
	
	public static List<String> getElementsByAttribute(String body, String attributeName, String attributeValue) {
		if (attributeName == null || attributeName.isEmpty() || attributeValue == null) {
			return Collections.emptyList();
		}
		LinkedList<String> elementList = new LinkedList<>();
		String regex = "\\<[^<>]+" + attributeName + "=[\"']" + attributeValue + "[\"'][^<>]*(\\/\\>|\\>[^<>]*\\<[^<>]*\\>)";
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(body);
		while (matcher.find()) {
			elementList.add(matcher.group());
		}
		return elementList;
	}
	//TODO
	public static List<String> getElementsContainingAttribute(String body, String attributeName, String stringContained) {
		if (attributeName == null || attributeName.isEmpty() || stringContained == null || stringContained.isEmpty()) {
			return Collections.emptyList();
		}
		LinkedList<String> elementList = new LinkedList<>();
		String regex = "\\<[^<>]+?" + attributeName + "=[\"'][^\"'<>]*" + stringContained + "[^\"'<>]*[\"'][^<>]*(\\/\\>|\\>[^<>]*\\<[^<>]*\\>)";
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(body);
		while (matcher.find()) {
			elementList.add(matcher.group());
		}
		return elementList;
	}
	
	
	public static String getAttribute(String element, String attributeName) {
		StringBuilder strBuilder = new StringBuilder("");
		if (element == null || element.isEmpty() || attributeName == null || attributeName.isEmpty())
			return null;
		else if (attributeName.equals("innerText")) {
			strBuilder.append("\\>(?<value>[^\"'<>]*)\\<");
		} else {
			strBuilder.append(attributeName + "=[\"'](?<value>[^'\"<>]*)[\"']");
		}
		String regex = strBuilder.toString();
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(element);
		if (matcher.find()) {
			return matcher.group("value");
		}
		return null;
	}
	
	public static String getTagXmlFirst(String body, String tag) {
		
		String regex = "\\<("+tag+")[^<>]*\\>[^<>\\s]*\\<[^<>]*\\>";

		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(body);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}
	
	public static String getCleanAttribute(String element, String attributeName) {
		String raw = getAttribute(element, attributeName);
		if (raw == null)
			return null;
		else
			return Toolkit.removeWeirdCharacters(raw);
	}
	
	public static String goBackPdf(CloseableHttpClient httpClient, String link, String sessionId)
			throws UnsupportedEncodingException, SegSocialException, IOException {
		HttpPost httpPost;
		List<NameValuePair> params;
		httpPost = new HttpPost(link);
		params = new ArrayList<>();
		params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, "SGIRED"));
		params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "EIOMINTE"));
		params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
		params.add(new BasicNameValuePair(IServicioRedConstants.DEFAULT_NULL, "1"));
		params.add(new BasicNameValuePair(IServicioRedConstants.TXT_ENTORNO_PR, "0"));
		params.add(new BasicNameValuePair(IServicioRedConstants.TXT_PRACTICE_MENU, "I"));
		params.add(new BasicNameValuePair(IServicioRedConstants.TXT_COMMAND_EDIT, "EN"));
		params.add(new BasicNameValuePair("btn_FkeyButton", "+"));
		httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
		return getBodyPOST(httpClient, httpPost);
	}
	
	/**
	 * getIdentityType DEFAULT 2 PASSPORT
	 * @param ipf
	 * @return 1 (nif, dni), 6 nie, 2 pasaporte
	 */
	public static String getIdentityType(String ipf) {
		Pattern nif  = Pattern.compile(
				//  -------- LEGAL_PERSON_NIF PATTERN  
				// -------- (1) --> X00000000
					"0?"
					+"^[A-JUV]"
					+"[\\s-_/]?"
					+"[0-9]{2}"
					+"[-_/\\.]?"
					+"[0-9]{3}"
					+"[-_/\\.]?"
					+"[0-9]{3}$"
					, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		Pattern dni  = Pattern.compile(
					"0?"
					+"[0-9]?"
					+"[0-9]"
					+"[\\s-_/\\.]?"
					+"[0-9]{3}"
					+"[\\s-_/\\.]?"
					+"[0-9]{3}"
					+"[\\s-_/]?"
					+"[A-Z]"
					, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
				//  -------- NIE PATTERN 
				// -------- (1) --> X0000000X
		Pattern nie  = Pattern.compile(
					"0?"
					+"[XYZ]"
					+"[\\s-_/]?"
					+"[0-9]{7}"
					+"[\\s-_/]?"
					+"[A-HJ-NP-TV-Z]"
				, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		
		Map<Pattern, Integer> patterns = new HashMap<>();
		patterns.put(nif, 1);
		patterns.put(dni, 1);
		patterns.put(nie, 6);
		
		String identity = "2"; // passport
		for (Entry<Pattern, Integer> entry : patterns.entrySet()) {
			if ( entry.getKey().matcher(ipf).matches()) { identity = entry.getValue().toString(); break; }
		}
		return identity;
	}
	
}
