package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent Contrat@ (S.E.P.E.) TBXCPAIS table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 *  TBXCPAIS	PAÍS								
 *  ------------------------------------------------------------------------
 */ 
public enum TBXCPAIS {

	TBXCPAIS_004( "004", "AFGANISTÁN", null, null ),
	TBXCPAIS_008( "008", "ALBANIA", null, null ),
	TBXCPAIS_010( "010", "ANTÁRTIDA", null, null ),
	TBXCPAIS_012( "012", "ARGELIA", null, null ),
	TBXCPAIS_016( "016", "SAMOA AMERICANA", null, null ),
	TBXCPAIS_020( "020", "ANDORRA", null, null ),
	TBXCPAIS_024( "024", "ANGOLA", null, null ),
	TBXCPAIS_028( "028", "ANTIGUA Y BARBUDA", null, null ),
	TBXCPAIS_031( "031", "AZERBAYÁN", null, null ),
	TBXCPAIS_032( "032", "ARGENTINA", null, null ),
	TBXCPAIS_036( "036", "AUSTRALIA", null, null ),
	TBXCPAIS_040( "040", "AUSTRIA", null, null ),
	TBXCPAIS_044( "044", "BAHAMAS", null, null ),
	TBXCPAIS_048( "048", "BAHREIN", null, null ),
	TBXCPAIS_050( "050", "BANGLADESH", null, null ),
	TBXCPAIS_051( "051", "ARMENIA", null, null ),
	TBXCPAIS_052( "052", "BARBADOS", null, null ),
	TBXCPAIS_056( "056", "BÉLGICA", null, null ),
	TBXCPAIS_060( "060", "BERMUDAS", null, null ),
	TBXCPAIS_064( "064", "BUTÁN", null, null ),
	TBXCPAIS_068( "068", "BOLIVIA", null, null ),
	TBXCPAIS_070( "070", "BOSNIA-HERZEGOVINA", null, null ),
	TBXCPAIS_072( "072", "BOTSWANA", null, null ),
	TBXCPAIS_074( "074", "BOUVET, ISLA", null, null ),
	TBXCPAIS_076( "076", "BRASIL", null, null ),
	TBXCPAIS_084( "084", "BELICE", null, null ),
	TBXCPAIS_086( "086", "OCÉANO INDICO, TERRITORIO BRITÁNICO DEL", null, null ),
	TBXCPAIS_090( "090", "SALOMON, ISLAS", null, null ),
	TBXCPAIS_092( "092", "VÍRGENES, ISLAS (BRITÁNICAS)", null, null ),
	TBXCPAIS_096( "096", "BRUNEI DARUSSALAM", null, null ),
	TBXCPAIS_100( "100", "BULGARIA", null, null ),
	TBXCPAIS_104( "104", "MYANMAR", null, null ),
	TBXCPAIS_108( "108", "BURUNDI", null, null ),
	TBXCPAIS_112( "112", "BIELORUSIA (BELARUS)", null, null ),
	TBXCPAIS_116( "116", "CAMBOYA", null, null ),
	TBXCPAIS_120( "120", "CAMERÚN", null, null ),
	TBXCPAIS_124( "124", "CANADÁ", null, null ),
	TBXCPAIS_132( "132", "CABO VERDE", null, null ),
	TBXCPAIS_136( "136", "CAIMÁN, ISLAS", null, null ),
	TBXCPAIS_140( "140", "CENTROAFRICANA, REPÚBLICA", null, null ),
	TBXCPAIS_144( "144", "SRI LANKA", null, null ),
	TBXCPAIS_148( "148", "CHAD", null, null ),
	TBXCPAIS_152( "152", "CHILE", null, null ),
	TBXCPAIS_156( "156", "CHINA", null, null ),
	TBXCPAIS_158( "158", "TAIWAN, PROVINCIA DE CHINA", null, null ),
	TBXCPAIS_162( "162", "CHRISTMAS, ISLA", null, null ),
	TBXCPAIS_166( "166", "COCOS (KEELING), ISLAS", null, null ),
	TBXCPAIS_170( "170", "COLOMBIA", null, null ),
	TBXCPAIS_174( "174", "COMORES", null, null ),
	TBXCPAIS_175( "175", "MAYOTTE", null, null ),
	TBXCPAIS_178( "178", "CONGO", null, null ),
	TBXCPAIS_180( "180", "CONGO REPUBLICA DEMOCRÁTICA (EX ZAIRE)", null, null ),
	TBXCPAIS_184( "184", "COOK, ISLAS", null, null ),
	TBXCPAIS_188( "188", "COSTA RICA", null, null ),
	TBXCPAIS_191( "191", "CROACIA", null, null ),
	TBXCPAIS_192( "192", "CUBA", null, null ),
	TBXCPAIS_196( "196", "CHIPRE", null, null ),
	TBXCPAIS_203( "203", "CHECA, REPÚBLICA", null, null ),
	TBXCPAIS_204( "204", "BENIN", null, null ),
	TBXCPAIS_208( "208", "DINAMARCA", null, null ),
	TBXCPAIS_212( "212", "DOMINICA", null, null ),
	TBXCPAIS_214( "214", "DOMINICANA, REPÚBLICA", null, null ),
	TBXCPAIS_218( "218", "ECUADOR", null, null ),
	TBXCPAIS_222( "222", "EL SALVADOR", null, null ),
	TBXCPAIS_226( "226", "GUINEA ECUATORIAL", null, null ),
	TBXCPAIS_231( "231", "ETIOPÍA", null, null ),
	TBXCPAIS_232( "232", "ERITREA", null, null ),
	TBXCPAIS_233( "233", "ESTONIA", null, null ),
	TBXCPAIS_234( "234", "FEROE, ISLAS", null, null ),
	TBXCPAIS_238( "238", "MALVINAS, ISLAS (FALKLAND)", null, null ),
	TBXCPAIS_239( "239", "GEORGIA DEL SUR E ISLAS SANDWICH", null, null ),
	TBXCPAIS_242( "242", "FIDJI", null, null ),
	TBXCPAIS_246( "246", "FINLANDIA", null, null ),
	TBXCPAIS_249( "249", "FRANCIA, METROPOLITANA", null, null ),
	TBXCPAIS_250( "250", "FRANCIA", null, null ),
	TBXCPAIS_254( "254", "GUAYANA FRANCESA", null, null ),
	TBXCPAIS_258( "258", "POLINESIA FRANCESA", null, null ),
	TBXCPAIS_260( "260", "TIERRAS AUSTRALES FRANCESAS", null, null ),
	TBXCPAIS_262( "262", "DJIBUTI", null, null ),
	TBXCPAIS_266( "266", "GABÓN", null, null ),
	TBXCPAIS_268( "268", "GEORGIA", null, null ),
	TBXCPAIS_270( "270", "GAMBIA", null, null ),
	TBXCPAIS_276( "276", "ALEMANIA", null, null ),
	TBXCPAIS_288( "288", "GHANA", null, null ),
	TBXCPAIS_292( "292", "GIBRALTAR", null, null ),
	TBXCPAIS_296( "296", "KIRIBATI", null, null ),
	TBXCPAIS_300( "300", "GRECIA", null, null ),
	TBXCPAIS_304( "304", "GROENLANDIA", null, null ),
	TBXCPAIS_308( "308", "GRANADA", null, null ),
	TBXCPAIS_312( "312", "GUADALUPE", null, null ),
	TBXCPAIS_316( "316", "GUAM", null, null ),
	TBXCPAIS_320( "320", "GUATEMALA", null, null ),
	TBXCPAIS_324( "324", "GUINEA", null, null ),
	TBXCPAIS_328( "328", "GUYANA", null, null ),
	TBXCPAIS_332( "332", "HAITÍ", null, null ),
	TBXCPAIS_334( "334", "HEARD Y MC DONALD, ISLAS", null, null ),
	TBXCPAIS_336( "336", "VATICANO, ESTADO DE LA CIUDAD DEL", null, null ),
	TBXCPAIS_340( "340", "HONDURAS", null, null ),
	TBXCPAIS_344( "344", "HONG KONG", null, null ),
	TBXCPAIS_348( "348", "HUNGRÍA", null, null ),
	TBXCPAIS_352( "352", "ISLANDIA", null, null ),
	TBXCPAIS_356( "356", "INDIA", null, null ),
	TBXCPAIS_360( "360", "INDONESIA", null, null ),
	TBXCPAIS_364( "364", "IRÁN, REPÚBLICA ISLÁMICA DE", null, null ),
	TBXCPAIS_368( "368", "IRAQ", null, null ),
	TBXCPAIS_372( "372", "IRLANDA", null, null ),
	TBXCPAIS_376( "376", "ISRAEL", null, null ),
	TBXCPAIS_380( "380", "ITALIA", null, null ),
	TBXCPAIS_384( "384", "COSTA DE MARFIL", null, null ),
	TBXCPAIS_388( "388", "JAMAICA", null, null ),
	TBXCPAIS_392( "392", "JAPÓN", null, null ),
	TBXCPAIS_398( "398", "KAZAKSTAN", null, null ),
	TBXCPAIS_400( "400", "JORDANIA", null, null ),
	TBXCPAIS_404( "404", "KENIA", null, null ),
	TBXCPAIS_408( "408", "COREA, REPÚBLICA POPULAR DEMOCRÁTICA DE", null, null ),
	TBXCPAIS_410( "410", "COREA, REPÚBLICA DE", null, null ),
	TBXCPAIS_414( "414", "KUWAIT", null, null ),
	TBXCPAIS_417( "417", "KIRGHIZISTAN", null, null ),
	TBXCPAIS_418( "418", "LAOS, REPÚBLICA DEMOCRÁTICA POPULAR", null, null ),
	TBXCPAIS_422( "422", "LÍBANO", null, null ),
	TBXCPAIS_426( "426", "LESOTHO", null, null ),
	TBXCPAIS_428( "428", "LETONIA", null, null ),
	TBXCPAIS_430( "430", "LIBERIA", null, null ),
	TBXCPAIS_434( "434", "LIBIA, JAMAHIRIYA ÁRABE", null, null ),
	TBXCPAIS_438( "438", "LIECHTENSTEIN", null, null ),
	TBXCPAIS_440( "440", "LITUANIA", null, null ),
	TBXCPAIS_442( "442", "LUXEMBURGO", null, null ),
	TBXCPAIS_446( "446", "MACAO", null, null ),
	TBXCPAIS_450( "450", "MADAGASCAR", null, null ),
	TBXCPAIS_454( "454", "MALAWI", null, null ),
	TBXCPAIS_458( "458", "MALASIA", null, null ),
	TBXCPAIS_462( "462", "MALDIVAS", null, null ),
	TBXCPAIS_466( "466", "MALÍ", null, null ),
	TBXCPAIS_470( "470", "MALTA", null, null ),
	TBXCPAIS_474( "474", "MARTINICA", null, null ),
	TBXCPAIS_478( "478", "MAURITANIA", null, null ),
	TBXCPAIS_480( "480", "MAURICIO", null, null ),
	TBXCPAIS_484( "484", "MÉJICO", null, null ),
	TBXCPAIS_492( "492", "MÓNACO", null, null ),
	TBXCPAIS_496( "496", "MONGOLIA", null, null ),
	TBXCPAIS_498( "498", "MOLDAVIA, REPÚBLICA DE", null, null ),
	TBXCPAIS_499( "499", "MONTENEGRO", null, null ),
	TBXCPAIS_500( "500", "MONTSERRAT", null, null ),
	TBXCPAIS_504( "504", "MARRUECOS", null, null ),
	TBXCPAIS_508( "508", "MOZAMBIQUE", null, null ),
	TBXCPAIS_512( "512", "OMÁN", null, null ),
	TBXCPAIS_516( "516", "NAMIBIA", null, null ),
	TBXCPAIS_520( "520", "NAURU", null, null ),
	TBXCPAIS_524( "524", "NEPAL", null, null ),
	TBXCPAIS_528( "528", "HOLANDA (PAISES BAJOS)", null, null ),
	TBXCPAIS_530( "530", "ANTILLAS HOLANDESAS", null, null ),
	TBXCPAIS_533( "533", "ARUBA", null, null ),
	TBXCPAIS_540( "540", "NUEVA CALEDONIA", null, null ),
	TBXCPAIS_548( "548", "VANUATU", null, null ),
	TBXCPAIS_554( "554", "NUEVA ZELANDA", null, null ),
	TBXCPAIS_558( "558", "NICARAGUA", null, null ),
	TBXCPAIS_562( "562", "NÍGER", null, null ),
	TBXCPAIS_566( "566", "NIGERIA", null, null ),
	TBXCPAIS_570( "570", "NIUE", null, null ),
	TBXCPAIS_574( "574", "NORFOLK, ISLA", null, null ),
	TBXCPAIS_578( "578", "NORUEGA", null, null ),
	TBXCPAIS_580( "580", "MARIANAS DEL NORTE, ISLAS", null, null ),
	TBXCPAIS_581( "581", "EEUU, ISLAS EXTERIORES MENORES", null, null ),
	TBXCPAIS_583( "583", "MICRONESIA", null, null ),
	TBXCPAIS_584( "584", "MARSHALL, ISLAS", null, null ),
	TBXCPAIS_585( "585", "PALAU", null, null ),
	TBXCPAIS_586( "586", "PAKISTÁN", null, null ),
	TBXCPAIS_591( "591", "PANAMÁ", null, null ),
	TBXCPAIS_598( "598", "PAPUA, NUEVA GUINEA", null, null ),
	TBXCPAIS_600( "600", "PARAGUAY", null, null ),
	TBXCPAIS_604( "604", "PERÚ", null, null ),
	TBXCPAIS_608( "608", "FILIPINAS", null, null ),
	TBXCPAIS_609( "609", "PALESTINA, TERRIORIO OCUPADO DE", null, null ),
	TBXCPAIS_612( "612", "PITCAIRN", null, null ),
	TBXCPAIS_616( "616", "POLONIA", null, null ),
	TBXCPAIS_620( "620", "PORTUGAL", null, null ),
	TBXCPAIS_624( "624", "GUINEA BISSAU", null, null ),
	TBXCPAIS_626( "626", "TIMOR ORIENTAL", null, null ),
	TBXCPAIS_630( "630", "PUERTO RICO", null, null ),
	TBXCPAIS_634( "634", "QATAR", null, null ),
	TBXCPAIS_638( "638", "REUNIÓN", null, null ),
	TBXCPAIS_642( "642", "RUMANIA", null, null ),
	TBXCPAIS_643( "643", "RUSIA, FEDERACION DE", null, null ),
	TBXCPAIS_646( "646", "RUANDA", null, null ),
	TBXCPAIS_654( "654", "SANTA HELENA", null, null ),
	TBXCPAIS_659( "659", "SAN CRISTÓBAL Y NIEVES", null, null ),
	TBXCPAIS_660( "660", "ANGUILA", null, null ),
	TBXCPAIS_662( "662", "SANTA LUCÍA", null, null ),
	TBXCPAIS_666( "666", "SAN PEDRO Y MIQUELON", null, null ),
	TBXCPAIS_670( "670", "SAN VICENTE Y GRANADINAS", null, null ),
	TBXCPAIS_674( "674", "SAN MARINO", null, null ),
	TBXCPAIS_678( "678", "SANTO TOMAS Y PRINCIPE", null, null ),
	TBXCPAIS_682( "682", "ARABIA SAUDÍ", null, null ),
	TBXCPAIS_686( "686", "SENEGAL", null, null ),
	TBXCPAIS_688( "688", "SERBIA", null, null ),
	TBXCPAIS_690( "690", "SEYCHELLES", null, null ),
	TBXCPAIS_694( "694", "SIERRA LEONA", null, null ),
	TBXCPAIS_702( "702", "SINGAPUR", null, null ),
	TBXCPAIS_703( "703", "ESLOVAQUIA", null, null ),
	TBXCPAIS_704( "704", "VIETNAM", null, null ),
	TBXCPAIS_705( "705", "ESLOVENIA", null, null ),
	TBXCPAIS_706( "706", "SOMALIA", null, null ),
	TBXCPAIS_710( "710", "AFRICA DEL SUR", null, null ),
	TBXCPAIS_716( "716", "ZIMBABWE", null, null ),
	TBXCPAIS_724( "724", "ESPAÑA", null, null ),
	TBXCPAIS_732( "732", "SÁHARA OCCIDENTAL", null, null ),
	TBXCPAIS_736( "736", "SUDÁN", null, null ),
	TBXCPAIS_740( "740", "SURINAM", null, null ),
	TBXCPAIS_744( "744", "SVALBARD Y JAN MAYEN, ISLAS", null, null ),
	TBXCPAIS_748( "748", "SWAZILANDIA", null, null ),
	TBXCPAIS_752( "752", "SUECIA", null, null ),
	TBXCPAIS_756( "756", "SUIZA", null, null ),
	TBXCPAIS_760( "760", "SIRIA, REPÚBLICA ÁRABE", null, null ),
	TBXCPAIS_762( "762", "TAYIKISTÁN (TADJIKISTÁN, TAJIKISTÁN)", null, null ),
	TBXCPAIS_764( "764", "TAILANDIA", null, null ),
	TBXCPAIS_768( "768", "TOGO", null, null ),
	TBXCPAIS_772( "772", "TOKELAU", null, null ),
	TBXCPAIS_776( "776", "TONGA", null, null ),
	TBXCPAIS_780( "780", "TRINIDAD Y TOBAGO", null, null ),
	TBXCPAIS_784( "784", "EMIRATOS ÁRABES UNIDOS", null, null ),
	TBXCPAIS_788( "788", "TÚNEZ", null, null ),
	TBXCPAIS_792( "792", "TURQUÍA", null, null ),
	TBXCPAIS_795( "795", "TURKMENISTÁN", null, null ),
	TBXCPAIS_796( "796", "TURKS Y CAICOS, ISLAS", null, null ),
	TBXCPAIS_798( "798", "TUVALU", null, null ),
	TBXCPAIS_800( "800", "UGANDA", null, null ),
	TBXCPAIS_804( "804", "UCRANIA", null, null ),
	TBXCPAIS_807( "807", "MACEDONIA, EX-REPÚBLICA YUGOESLAVA DE", null, null ),
	TBXCPAIS_818( "818", "EGIPTO", null, null ),
	TBXCPAIS_826( "826", "REINO UNIDO", null, null ),
	TBXCPAIS_834( "834", "TANZANIA, REPÚBLICA UNIDA DE", null, null ),
	TBXCPAIS_840( "840", "ESTADOS UNIDOS", null, null ),
	TBXCPAIS_850( "850", "VÍRGENES, ISLAS (EEUU)", null, null ),
	TBXCPAIS_854( "854", "BURKINA FASO", null, null ),
	TBXCPAIS_858( "858", "URUGUAY", null, null ),
	TBXCPAIS_860( "860", "UZBEKISTÁN", null, null ),
	TBXCPAIS_862( "862", "VENEZUELA", null, null ),
	TBXCPAIS_876( "876", "WALLIS Y FUTUNA, ISLAS", null, null ),
	TBXCPAIS_882( "882", "SAMOA", null, null ),
	TBXCPAIS_887( "887", "YEMEN", null, null ),
	TBXCPAIS_891( "891", "YUGOSLAVIA", null, null ),
	TBXCPAIS_894( "894", "ZAMBIA", null, null ),
	TBXCPAIS_999( "999", "APÁTRIDA", null, null ),
	;
	public static final String TABLE_NAME = "TBXCPAIS";
	public static final String TABLE_DESCRIPTION = " TBXCPAIS	PAÍS								";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TBXCPAIS( String code, String description, String startDate, String endDate ) {
		this.code = code;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public Date getStartDate(){
		try {
			if(startDate!=null){
				return DateUtils.ceiling(sdf.parse(startDate), Calendar.DAY_OF_MONTH);
			}
		} catch (ParseException e) {
			// nothing to do
		}
		return null;
	}

	public Date getEndDate(){
		try {
			if(endDate!=null){
				return DateUtils.ceiling(sdf.parse(endDate), Calendar.DAY_OF_MONTH);
			}
		} catch (ParseException e) {
			// nothing to do
		}
		return null;
	}

	public boolean isActive(){
		Date now = new Date();
		now = DateUtils.ceiling(now, Calendar.DAY_OF_MONTH);
		if( (getStartDate()!=null && getStartDate().after(now)) || (getEndDate()!=null && getEndDate().before(now)) ){
			return false;
		}
		return true;
	}

	public static TBXCPAIS getEnumByValue(String expression) {
		for( TBXCPAIS o : TBXCPAIS.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}