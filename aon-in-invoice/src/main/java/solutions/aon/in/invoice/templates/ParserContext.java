package solutions.aon.in.invoice.templates;

import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum ParserContext {
	SPANISH {
		private Locale LOCALE = new Locale("es");
		private String[] MONTHS = new String[] { "enero"  , "febrero" , "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre" };
		private Map<String,Integer> MONTHS_MAP = new HashMap<String,Integer>();
		
		@Override
		public String[] getMonths() {
			return MONTHS;
		}
		
		@Override
		public Map<String,Integer> getMonthsMap(){
			if (MONTHS_MAP.size() == 0) fillMap( MONTHS, MONTHS_MAP );
			return MONTHS_MAP;
		}
		
		@Override
		public String getDatePattern() {
			return 
		   		 "(?<day>0?[1-9]|[12][0-9]|3[01])"
		   		+"(?<sep1>[-|/|\\.|\\p{Blank}*|\\h*])"
		   		+"(?:de|\\/)?"
		   		+"(?:\\p{Blank}*)?"
		   		+"(?<month>0?[1-9]|1[012]|[a-z]+\\.?\\'?)"
		   		+"(?<sep2>[-|/|\\.|\\p{Blank}*|\\h*])"
		   		+"(?:de|del|\\/)?"
		   		+"(?:\\p{Blank}*|\\h*)?"
		   		+"(?<year>20\\d{2}|[12][0-9])";
			
		}
		
		@Override
		public boolean datesHasSameSeparator() {
			return true;
		}

		@Override
		protected String[] getIssueDatePatterns() {
			return  new String[]{
					"Fecha emisi.n.*"+ getDatePattern()+"\\b",
					"Fecha de emisi.n.*"+getDatePattern()+"\\b",
					"Fecha de emisi.n de factura.*"+getDatePattern()+"\\b",
					"Fecha factura.*"+getDatePattern()+"\\b",
					"Fecha de factura.*"+getDatePattern()+"\\b",
					"Fecha de la factura.*"+getDatePattern()+"\\b",
					"Fecha operaci.n.*"+getDatePattern()+"\\b",
					"Fecha de env.o.*"+getDatePattern()+"\\b",
					"Fecha.*factura.*"+getDatePattern()+"\\b",
				};
		}

		@Override
		protected String[] getAcceptPatterns() {
			return  new String[]{
					"factura",
			};
		}

		@Override
		public Locale getLocale() {
			return LOCALE;
		}

		@Override
		protected String[] getTotalPatterns() {
			return new String[] {
					"total\\s+pagar.*"+getDecimalPattern()+"\\b",
					"total\\s+a\\s+pagar.*"+getDecimalPattern()+"\\b",
					"total\\s+importe\\s+factura.*"+getDecimalPattern()+"\\b",
					"total\\s+factura.*"+getDecimalPattern()+"\\b",
					"total\\s+euros"+getDecimalPattern()+"\\b",
					"total\\s+"+getDecimalPattern()+CURRENCY+"$"
				};
		}

	},
	ENGLISH_US {
		private Locale LOCALE = Locale.US;
		private String[] MONTHS = new String[] { "january", "february", "march", "april", "may" , "june" , "july" , "august", "september" , "october", "november" , "december"  };
		private Map<String,Integer> MONTHS_MAP = new HashMap<String,Integer>();
		
		@Override
		public String[] getMonths() {
			return MONTHS;
		}
		
		@Override
		public Map<String,Integer> getMonthsMap(){
			if (MONTHS_MAP.size() == 0) fillMap( MONTHS, MONTHS_MAP );
			return MONTHS_MAP;
		}
		
		@Override
		public String getDatePattern() {
			return 
				"(?<month>0?[1-9]|1[012]|[a-z]+\\.?\\'?)"
				+"(?<sep2>[-|/|\\.|\\p{Blank}*|\\h*])"
				+"(?<day>0?[1-9]|[12][0-9]|3[01])"
				+"(?<sep1>[-|/|\\.|\\,|\\p{Blank}*|\\h*])"
				+"(?:\\p{Blank}*|\\h*)?"
				+"(?<year>20\\d{2}|[12][0-9])";
					
		}
		
		@Override
		public boolean datesHasSameSeparator() {
			return false;
		}

		@Override
		protected String[] getIssueDatePatterns() {
			return  new String[]{
				"invoice.*date.*"+getDatePattern()+"\\b",
			};
		}
		@Override
		protected String[] getAcceptPatterns() {
			return  new String[]{
					"invoice",
			};
		}
		
		@Override
		public Locale getLocale() {
			return LOCALE;
		}
		
		@Override
		protected String[] getTotalPatterns() {
			return new String[] {
					"total\\s+amount.*"+CURRENCY+getDecimalPattern()+"\\b",
					"total\\s+"+getDecimalPattern()+CURRENCY+"$"
				};
		}
		
	};
	
	private static void fillMap(String[] months, Map<String,Integer> monthsMap) {
		for (int y = 0; y < months.length ; y++) {
			String m = months[y];
			for (int x = m.length(); x > 1 ; x--) {
				if (x != 2 || (x == 2 && y != 2 && y != 4 && y != 5 && y != 6)) { // ma -> marzo o mayo?; ju -> junio o julio?
					Integer old = monthsMap.put( m.substring(0, x), y+1);
					if (old!= null && old != (y+1)) {
						throw new IllegalStateException( "ERROR!! " + m.substring(0, x) + " " + old + " por " + (y+1) );
					}
				}
			}
		}
	}
	
	public String getDecimalSeparator(){
		return Character.toString(DecimalFormatSymbols.getInstance( getLocale() ).getDecimalSeparator());
	}
	public String getDecimalGroupingSeparator() {
		return Character.toString(DecimalFormatSymbols.getInstance( getLocale() ).getGroupingSeparator());
	}
	public String getDecimalPattern() {
		return "(?<"+INTEGER_KEY+">-?\\+?(\\d+\\"+getDecimalGroupingSeparator()+")*\\d+)"+"\\"+getDecimalSeparator()+"(?<"+FRACTION_KEY+">\\d+)";
	}

	public static final String INTEGER_KEY = "integ";
	public static final String FRACTION_KEY = "fract";
	public static final String CURRENCY = "\\s*[\\p{Sc}|EUR|USD]?\\s*";
	

	public static ParserContext getDefault() {
		return SPANISH;
	}
	
	public abstract String[] getMonths();
	public abstract Map<String,Integer> getMonthsMap();
	public abstract String getDatePattern();
	public abstract boolean datesHasSameSeparator();
	public abstract Locale getLocale();
	protected abstract String[] getIssueDatePatterns();
	protected abstract String[] getAcceptPatterns();
	protected abstract String[] getTotalPatterns();

}
