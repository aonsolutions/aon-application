package com.esferalia.aon.gwt.template.server.projectCommercial;

import java.util.HashMap;
import java.util.Map;

public class BankBic11 {

	private static final Map<String,BankBic11> VALUES;
	
	private String bankCode;
	
	private String description;
	
	private String bic;

	static {
		VALUES = new HashMap<String,BankBic11>();
		// PARTICIPANTES DIRECTOS EN TARGET2 - BANCO DE ESPANA (a 31/03/2015)        
		// (Relacion publicada en el BOE de fecha 25/05/2015)
		addValue( new BankBic11("0019","DEUTESBBXXX","DEUTSCHE BANK, S.A.E."));
		addValue( new BankBic11("0058","BNPAESMMXXX","BNP PARIBAS ESPAÑA, S.A."));
		addValue( new BankBic11("0065","BARCESMMXXX","BARCLAYS BANK, S.A."));
		addValue( new BankBic11("0108","SOGEESMMXXX","SOCIETE GENERALE, SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("0144","PARBESMXXXX","BNP PARIBAS SECURITIES SERVICES, SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("0149","BNPAESMSXXX","BNP PARIBAS, SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("0152","BPLCESMMXXX","BARCLAYS BANK PLC, SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("0159","COBAESMXXXX","COMMERZBANK AKTIENGESELLSCHAFT, SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("0162","MIDLESMMXXX","HSBC BANK PLC, SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("0167","GEBAESMMXXX","BNP PARIBAS FORTIS, S.A., N.V., SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("0196","WELAESMMXXX","PORTIGON AG, SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("0226","UBSWESMMXXX","UBS BANK, S.A."));
		addValue( new BankBic11("1467","EHYPESMXXXX","HYPOTHEKENBANK FRANKFURT AG., SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("1474","CITIESMXXXX","CITIBANK INTERNATIONAL LTD, SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("3656","CSSOES2SFIN","FINANDUERO, SOCIEDAD DE VALORES, S.A."));
		addValue( new BankBic11("0156","ABNAESMMXXX","THE ROYAL BANK OF SCOTLAND PLC, SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("3524","AHCFESMMXXX","AHORRO CORPORACION FINANCIERA, S.A., SOCIEDAD DE VALORES"));
		addValue( new BankBic11("0188","ALCLESMMXXX","BANCO ALCALA, S.A."));
		addValue( new BankBic11("0136","AREBESMMXXX","ARESBANK, S.A."));
		addValue( new BankBic11("0078","BAPUES22XXX","BANCA PUEYO, S.A."));
		addValue( new BankBic11("2095","BASKES2BXXX","KUTXABANK, S.A."));
		addValue( new BankBic11("0190","BBPIESMMXXX","BANCO BPI, S.A., SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("0168","BBRUESMXXXX","ING BELGIUM, S.A., SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("0182","BBVAESMMXXX","BANCO BILBAO VIZCAYA ARGENTARIA, S.A."));
		addValue( new BankBic11("0240","BCCAESMMXXX","BANCO DE CRÉDITO SOCIAL COOPERATIVO S.A."));
		addValue( new BankBic11("3081","BCOEESMM081","CAJA RURAL DE CASTILLA-LA MANCHA, S.C.C."));
		addValue( new BankBic11("0198","BCOEESMMXXX","BANCO COOPERATIVO ESPAÑOL, S.A."));                
		addValue( new BankBic11("0131","BESMESMMXXX","NOVO BANCO, S.A., SUCURSAL EN ESPAÑA"));                     
		addValue( new BankBic11("0186","BFIVESBBXXX","BANCO MEDIOLANUM, S.A."));
		addValue( new BankBic11("0128","BKBKESMMXXX","BANKINTER, S.A."));
		addValue( new BankBic11("0138","BKOAES22XXX","BANKOA, S.A."));
		addValue( new BankBic11("0061","BMARES2MXXX","BANCA MARCH, S.A."));
		addValue( new BankBic11("0219","BMCEESMMXXX","BANQUE MAROCAINE COMMERCE EXTERIEUR INTERNATIONAL, S.A."));
		addValue( new BankBic11("0160","BOTKESMXXXX","THE BANK OF TOKYO-MITSUBISHI UFJ, LTD, SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("0155","BRASESMMXXX","BANCO DO BRASIL AG, SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("0081","BSABESBBXXX","BANCO DE SABADELL, S.A."));
		addValue( new BankBic11("0049","BSCHESMMXXX","BANCO SANTANDER, S.A."));
		addValue( new BankBic11("0154","BSUIESMMXXX","CREDIT AGRICOLE CORPORATE AND INVESTMENT BANK, SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("0094","BVALESMMXXX","RBC INVESTOR SERVICES ESPAÑA, S.A."));
		addValue( new BankBic11("2080","CAGLESMMVIG","ABANCA CORPORACIÓN BANCARIA, S.A."));                     
		addValue( new BankBic11("2038","CAHMESMMXXX","BANKIA, S.A."));
		addValue( new BankBic11("2100","CAIXESBBXXX","CAIXABANK, S.A."));
		addValue( new BankBic11("3604","CAPIESMMXXX","CM CAPITAL MARKETS BOLSA, SOCIEDAD DE VALORES, S.A."));
		addValue( new BankBic11("3183","CASDESBBXXX","CAJA DE ARQUITECTOS, S.C.C."));
		addValue( new BankBic11("2085","CAZRES2ZXXX","IBERCAJA BANCO, S.A."));
		addValue( new BankBic11("0234","CCOCESMMXXX","BANCO CAMINOS, S.A."));
		addValue( new BankBic11("3058","CCRIES2AXXX","CAJAS RURALES UNIDAS, S.C.C."));
		addValue( new BankBic11("3025","CDENESBBXXX","CAIXA DE CREDIT DELS ENGINYERS - CAJA DE CREDITO DE LOS INGENIEROS, S.C.C."));
		addValue( new BankBic11("2045","CECAESMM045","CAJA DE AHORROS Y M.P. DE ONTINYENT"));
		addValue( new BankBic11("2048","CECAESMM048","LIBERBANK, S.A."));
		addValue( new BankBic11("2056","CECAESMM056","COLONYA - CAIXA D'ESTALVIS DE POLLENSA"));
		addValue( new BankBic11("2105","CECAESMM105","BANCO DE CASTILLA-LA MANCHA, S.A. /CAJA DE AHORROS DE CASTILLA-LA MANCHA"));
		addValue( new BankBic11("2000","CECAESMMXXX","CECABANK, S.A."));
		addValue( new BankBic11("2013","CESCESBBXXX","CATALUNYA BANC, S.A."));
		addValue( new BankBic11("0130","CGDIESMMXXX","BANCO CAIXA GERAL, S.A."));
		addValue( new BankBic11("0122","CITIES2XXXX","CITIBANK ESPAÑA, S.A."));
		addValue( new BankBic11("3035","CLPEES2MXXX","CAJA LABORAL POPULAR, C.C."));
		addValue( new BankBic11("1460","CRESESMMXXX","CREDIT SUISSE AG, SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("2108","CSPAES2L108","BANCO DE CAJA ESPAÑA DE INVERSIONES, SALAMANCA Y SORIA, S.A."));
		addValue( new BankBic11("0237","CSURES2CXXX","CAJASUR BANCO, S.A."));
		addValue( new BankBic11("0231","DSBLESMMXXX","DEXIA SABADELL, S.A."));
		addValue( new BankBic11("9000","ESPBESMMXXX","BANCO DE ESPAÑA"));
		addValue( new BankBic11("1497","ESSIESMMXXX","BANCO ESPIRITO SANTO DE INVESTIMENTO, S.A., SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("0239","EVOBESMMXXX","EVO BANCO S.A.U."));
		addValue( new BankBic11("0220","FIOFESM1XXX","BANCO FINANTIA SOFINLOC, S.A."));
		addValue( new BankBic11("0487","GBMNESMMXXX","BANCO MARE NOSTRUM, S.A."));
		addValue( new BankBic11("3682","GVCBESBBETB","GVC GAESCO VALORES, SOCIEDAD DE VALORES, S.A."));
		addValue( new BankBic11("9096","IBRCESMMXXX","SOCIEDAD DE GESTION DE LOS SISTEMAS DE REGISTRO, COMPENSACION Y LIQUIDACION DE VALORES, S.A.U."));		
		addValue( new BankBic11("1000","ICROESMMXXX","INSTITUTO DE CREDITO OFICIAL"));
		addValue( new BankBic11("1465","INGDESMMXXX","ING BANK, N.V., SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("3575","INSGESMMXXX","INVERSEGUROS, SOCIEDAD DE VALORES, S.A."));
		addValue( new BankBic11("0232","INVLESMMXXX","BANCO INVERSIS, S.A."));
		addValue( new BankBic11("9020","IPAYESMMXXX","SOCIEDAD ESPAÑOLA DE SISTEMAS DE PAGO, S.A."));
		addValue( new BankBic11("3669","IVALESMMXXX","INTERMONEY VALORES, SOCIEDAD DE VALORES, S.A."));
		addValue( new BankBic11("3641","LISEESMMXXX","LINK SECURITIES, SOCIEDAD DE VALORES, S.A."));
		addValue( new BankBic11("0059","MADRESMMXXX","BANCO DE MADRID, S.A."));
		addValue( new BankBic11("9094","MEFFESBBXXX","BME CLEARING, S.A."));
		addValue( new BankBic11("3563","MISVESMMXXX","MAPFRE INVERSION, SOCIEDAD DE VALORES, S.A."));
		addValue( new BankBic11("3661","MLCEESMMXXX","MERRILL LYNCH CAPITAL MARKETS ESPAÑA, S.A., SOCIEDAD DE VALORES"));
		addValue( new BankBic11("0169","NACNESMMXXX","BANCO DE LA NACION ARGENTINA, SUCURSAL EN ESPAÑA "));
		addValue( new BankBic11("1479","NATXESMMXXX","NATIXIS, S.A., SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("0216","POHIESMMXXX","TARGOBANK, S.A."));
		addValue( new BankBic11("0233","POPIESMMXXX","POPULAR BANCA PRIVADA, S.A."));
		addValue( new BankBic11("0229","POPLESMMXXX","BANCOPOPULAR-E, S.A."));
		addValue( new BankBic11("0075","POPUESMMXXX","BANCO POPULAR ESPAÑOL, S.A."));
		addValue( new BankBic11("1459","PRABESMMXXX","COOPERATIEVE CENTRALE RAIFFEISEN-BOERENLEENBANK B.A. (RABOBANK NEDERLAND), SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("0241","PRDVESM1XXX","AyG BANCA PRIVADA S.A.U."));
		addValue( new BankBic11("0211","PROAESMMXXX","EBN BANCO DE NEGOCIOS, S.A."));
		addValue( new BankBic11("0238","PSTRESMMXXX","BANCO PASTOR, S.A."));
		addValue( new BankBic11("0083","RENBESMMXXX","RENTA 4 BANCO, S.A."));
		addValue( new BankBic11("3501","RENTESMMXXX","RENTA 4 SOCIEDAD DE VALORES, S.A."));
		addValue( new BankBic11("1524","UBIBESMMXXX","UBI BANCA INTERNATIONAL, S.A., SUCURSAL EN ESPAÑA"));
		addValue( new BankBic11("2103","UCJAES2MXXX","UNICAJA BANCO, S.A."));
		addValue( new BankBic11("9091","XBCNESBBXXX","SOCIEDAD RECTORA BOLSA VALORES DE BARCELONA, S.A., S.R.B.V."));
		addValue( new BankBic11("9092","XRBVES2BXXX","SOCIEDAD RECTORA BOLSA DE VALORES DE BILBAO, S.A., S.R.B.V."));
		addValue( new BankBic11("9093","XRVVESVVXXX","SOCIEDAD RECTORA BOLSA VALORES DE VALENCIA, S.A., S.R.B.V."));
		
		addValue( new BankBic11("0133","",""));

		addValue( new BankBic11("1491","TRIOESMMXXX","TRIODOS BANK"));

		addValue( new BankBic11("0073","OPENESMM","OPENBANK"));
		

		addValue( new BankBic11("0030","BAEMESM1XXX","BANESTO"));
		addValue( new BankBic11("0004","POPUESMMXXX", "BANCO DE ANDALUCIA, S.A."));
		addValue( new BankBic11("2024","CSURES2CXXX","BBK BANK CAJASUR S.A.U."));
		addValue( new BankBic11("2106","CECAESMM106","CAJASOL"));

		addValue( new BankBic11("3005","BCOEESMM005","CAJA RURAL CENTRAL"));
		addValue( new BankBic11("3016","BCOEESMM016","CAJA RURAL DE SALAMANCA"));
		addValue( new BankBic11("3020","BCOEESMM020","CAJA RURAL DE UTRERA"));
		addValue( new BankBic11("3029","BCOEESMM029","CAJA DE CREDITO DE PETREL, CAIXAPETRER"));
		addValue( new BankBic11("3059","BCOEESMM059","CAJA RURAL DE ASTURIAS"));
		addValue( new BankBic11("3060","BCOEESMM060","CAJA RURAL DE BURGOS"));
		addValue( new BankBic11("3063","BCOEESMM063","CAJA RURAL DE CORDOBA"));
		addValue( new BankBic11("3067","BCOEESMM067","CAJA RURAL DE JAEN"));
		addValue( new BankBic11("3070","BCOEESMM070","CAIXA RURAL GALEGA"));
		addValue( new BankBic11("3076","BCOEESMM076","CAJA RURAL DE TENERIFE"));
		addValue( new BankBic11("3080","BCOEESMM080","CAJA RURAL DE TERUEL"));
		addValue( new BankBic11("3085","BCOEESMM085","CAJA RURAL DE ZAMORA"));
		addValue( new BankBic11("3159","BCOEESMM159","CAIXA POPULAR, CAIXA RURAL"));
		addValue( new BankBic11("3187","BCOEESMM187","CAJA RURAL DEL SUR"));
		addValue( new BankBic11("3190","BCOEESMM190","CAJA RURAL ALBACETE C. REAL CUENCA"));
		addValue( new BankBic11("3191","BCOEESMM191","CAJA RURAL DE ARAGON"));
		addValue( new BankBic11("3023","BCOEESMM023","CAJA RURAL DE GRANADA"));
	}
	
	private static void addValue( BankBic11 value ) {
		VALUES.put(value.getBankCode(), value);
	}
	
	public BankBic11(String bankCode, String bic, String description) {
		this.bankCode = bankCode;
		this.description = description;
		this.bic = bic;
	}

	public String getBankCode() {
		return bankCode;
	}

	public String getDescription() {
		return description;
	}

	public String getBic() {
		return bic;
	}
	
	public static BankBic11 getBankBic11( String bankCode ) {
		return VALUES.get(bankCode);
	}
	
}
