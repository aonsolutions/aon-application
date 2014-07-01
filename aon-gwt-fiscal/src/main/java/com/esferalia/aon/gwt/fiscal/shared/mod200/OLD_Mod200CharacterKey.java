package com.esferalia.aon.gwt.fiscal.shared.mod200;

import java.io.Serializable;

import com.esferalia.aon.gwt.common.shared.CommonEnum.Administration;
import com.google.gwt.user.client.rpc.IsSerializable;

public enum OLD_Mod200CharacterKey implements Serializable, IsSerializable{
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	 
	 C0001(new String[]{null,null,null,null,"C001"},"Entidad sin \u00E1nimo de lucro acogida r\u00E9gimen fiscal T\u00EDtulo II Ley 49/2002")
	,C0002(new String[]{null,null,null,null,"C002"},"Entidad parcialmente exenta")
	,C0003(new String[]{null,null,null,null,"C003"},"Sociedad de inversi\u00F3n de capital variable o fondo de inversi\u00F3n de car\u00E1cter financiero")
	,C0004(new String[]{null,null,null,null,"C004"},"Sociedad de inversi\u00F3n inmobiliaria o fondo de inversi\u00F3n inmobiliaria")
	,C0005(new String[]{null,null,null,null,"C005"},"Comunidades titulares de montes vecinales en mano com\u00FAn")
	,C0011(new String[]{null,null,null,null,"C011"},"Entidad de tenencia de valores extranjeros")
	,C0013(new String[]{null,null,null,null,"C013"},"Agrupaci\u00F3n de inter\u00E9s econ\u00F3mico espa\u00F1ola o Uni\u00F3n temporal de empresas")
	,C0014(new String[]{null,null,null,null,"C014"},"Agrupaci\u00F3n europea de inter\u00E9s econ\u00F3mico")
	,C0017(new String[]{null,null,null,null,"C017"},"Cooperativa protegida")
	,C0018(new String[]{null,null,null,null,"C018"},"Cooperativa especialmente protegida") 
	,C0019(new String[]{null,null,null,null,"C019"},"Resto cooperativas")
	,C0021(new String[]{null,null,null,null,"C021"},"Establecimiento permanente") 
	,C0023(new String[]{null,null,null,null,"C023"},"Gran empresa")
	,C0024(new String[]{null,null,null,null,"C024"},"Entidad de cr\u00E9dito") 
	,C0025(new String[]{null,null,null,null,"C025"},"Entidad aseguradora")
	,C0031(new String[]{null,null,null,null,"C031"},"Entidades de capital-riesgo")
	,C0032(new String[]{null,null,null,null,"C032"},"Sociedad de desarrollo industrial regional")
	,C0036(new String[]{null,null,null,null,"C036"},"Fondo de pensiones Real Decreto Legislativo 1/2002, de 29 de noviembre")
	,C0048(new String[]{null,null,null,null,"C048"},"Sociedad de garant\u00EDa rec\u00EDproca o de reafianzamiento")
	,C0058(new String[]{null,null,null,null,"C058"},"Mutua de seguros o Mutualidad de previsi\u00F3n social")
	,C0060(new String[]{null,null,null,null,"C060"},"Fondos o activos de titulizaci\u00F3n")

 	,C0006(new String[]{null,null,null,null,"C006"},"Incentivos empresa de reducida dimensi\u00F3n (cap. XII, t\u00EDt. VII LIS)") 
	,C0047(new String[]{null,null,null,null,"C047"},"Entidades sometidas a la normativa foral")
	,C0038(new String[]{null,null,null,null,"C038"},"Entidad dedicada al arrend. de viviendas")
	,C0015(new String[]{null,null,null,null,"C015"},"Entidad ZEC")
	,C0049(new String[]{null,null,null,null,"C049"},"Reg\u00EDmenes especiales de normativa foral") 
	,C0046(new String[]{null,null,null,null,"C046"},"Entidad en r\u00E9g. de atribuci\u00F3n de rentas constitu\u00EDda en el extranjero con presencia en territorio espa\u00F1ol")
	,C0022(new String[]{null,null,null,null,"C022"},"R\u00E9gimen entid. navieras en funci\u00F3n del tonelaje")
	,C0029(new String[]{null,null,null,null,"C029"},"R\u00E9gimen especial Canarias")
	,C0028(new String[]{null,null,null,null,"C028"},"Tribut. conjunta Estado/Diput. Cdad. Forales") 
	,C0033(new String[]{null,null,null,null,"C033"},"R\u00E9gimen especial miner\u00EDa")
	,C0034(new String[]{null,null,null,null,"C034"},"R\u00E9gimen especial hidrocarburos")
	,C0012(new String[]{null,null,null,null,"C012"},"SOCIMI")
	,C0057(new String[]{null,null,null,null,"C057"},"Entidades que aplican el r\u00E9gimen especial Ley 11/2009 (excepto SOCIMI)")
	,C0020(new String[]{null,null,null,null,"C020"},"Otros reg\u00EDmenes especiales")

	,C0056(new String[]{null,null,null,null,"C056"},"Tipo gravamen reducido mant. o creaci\u00F3n empleo") 
	,C0026(new String[]{null,null,null,null,"C026"},"Entidad inactiva")
	,C0043(new String[]{null,null,null,null,"C043"},"Obligaci\u00F3n informaci\u00F3n art. 15 RIS")
	,C0007(new String[]{null,null,null,null,"C007"},"Inclusi\u00F3n en base imp. rentas positivas art. 107 LIS") 
	,C0027(new String[]{null,null,null,null,"C027"},"Base imponible negativa o cero")
	,C0044(new String[]{null,null,null,null,"C044"},"Obligaci\u00F3n informaci\u00F3n art. 45 RIS") 
	,C0008(new String[]{null,null,null,null,"C008"},"Opci\u00F3n art.107.6 LIS")
	,C0045(new String[]{null,null,null,null,"C045"},"Inversiones anticipadas-reserva inversiones en Canarias (art. 27.11 Ley 19/1994)")
	,C0062(new String[]{null,null,null,null,"C062"},"R\u00E9g. fiscal de operaciones de aportaci\u00F3n de activos a sociedades para la gesti\u00F3n de activos (Ley 8/2012)") 
	,C0009(new String[]{null,null,null,null,"C009"},"Sociedad dominante de grupo fiscal")
	,C0010(new String[]{null,null,null,null,"C010"},"Sociedad dependiente de grupo fiscal") 
	,C0037(new String[]{null,null,null,null,"C037"},"Opci\u00F3n art. 43.3 RIS") 
	,C0063(new String[]{null,null,null,null,"C063"},"Tipo gravamen reducido para entidades de nueva creaci\u00F3n")
	,C0030(new String[]{null,null,null,null,"C030"},"Transmisi\u00F3n elementos patrimoniales arts. 26.2.d) y 84.1 LIS") 
	,C0035(new String[]{null,null,null,null,"C035"},"Opci\u00F3n art. 43.1 RIS")
	,C0016(new String[]{null,null,null,null,"C016"},"Opci\u00F3n art. 51.2.b) LIS Entidad que forma parte de un grupo mercantil") 
	,C0059(new String[]{null,null,null,null,"C059"},"Opci\u00F3n art. 44.2. LIS")
	,C0039(new String[]{null,null,null,null,"C039"},"Entidad que forma parte de un grupo mercantil (art. 42 del C\u00F3d. Comercio)")
	
	,C0050(new String[]{null,null,null,null,"C039"},"Balance y ECPN. Normal")
	,C0051(new String[]{null,null,null,null,"C039"},"Balance y ECPN. Abreviado")
	,C0052(new String[]{null,null,null,null,"C039"},"Balance y ECPN. PYMES")
	,C0053(new String[]{null,null,null,null,"C039"},"Balance y ECPN. Normal")
	,C0054(new String[]{null,null,null,null,"C039"},"Balance y ECPN. Abreviado")
	,C0055(new String[]{null,null,null,null,"C039"},"Balance y ECPN. PYMES")
	;
	 
	static {
		C0001.setIncompatibility(new OLD_Mod200CharacterKey[] {C0002,C0005,C0013,C0017,C0031,C0034,C0060,
					C0056,C0003,C0011,C0014,C0018,C0032,C0048,C0059,C0004,C0012,C0015,C0019,C0033,
					C0036,C0063});
		C0002.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0011,C0015,C0031,C0038
					,C0059,C0003,C0012,C0017,C0032,C0048,C0063,C0004,C0013,C0018,C0033
					,C0036,C0056,C0005,C0014,C0019,C0034,C0060});
		C0003.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0011,C0015,C0021,C0033
					,C0036,C0063,C0002,C0012,C0017,C0022,C0034,C0058,C0056,C0004,C0013
					,C0018,C0031,C0038,C0060,C0005,C0014,C0019,C0032,C0048,C0059});
		C0004.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0011,C0015,C0021,C0033 
					,C0036,C0063,C0056,C0002,C0012,C0017,C0022,C0034,C0058,C0003,C0013
					,C0018,C0031,C0038,C0060,C0005,C0014,C0019,C0032,C0048,C0059});
		C0005.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0002,C0003,C0004,C0007
					,C0008,C0009,C0010,C0011,C0012,C0013,C0014,C0015,C0017,C0018,C0019
					,C0021,C0022,C0024,C0025,C0031,C0032,C0033,C0034,C0038,C0047,C0048
					,C0049,C0058,C0036,C0060,C0062,C0059,C0063,C0056});
		C0006.setIncompatibility(new OLD_Mod200CharacterKey[] {C0012,C0038,C0048});
		C0007.setIncompatibility(new OLD_Mod200CharacterKey[] {C0005});
		
		C0008.setIncompatibility(new OLD_Mod200CharacterKey[] {C0005});
		C0008.setAlsoCheck(new OLD_Mod200CharacterKey[] {C0007});
		
		C0009.setIncompatibility(new OLD_Mod200CharacterKey[] {C0005,C0010,C0012,C0013,C0014
					,C0048});
		C0010.setIncompatibility(new OLD_Mod200CharacterKey[] {C0005,C0009,C0012,C0013,C0014
					,C0048});
		C0011.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0005,C0015,C0038,C0060
					,C0002,C0012,C0017,C0048,C0003,C0013,C0018,C0034,C0004,C0014,C0019
					,C0036});
		C0012.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0006,C0014,C0056,C0002
					,C0009,C0015,C0022,C0003,C0010,C0017,C0033,C0004,C0011,C0018,C0034
					,C0005,C0013,C0019,C0038,C0031,C0032,C0057,C0036,C0048,C0058,C0060
					,C0059,C0063});
		C0013.setIncompatibility(new OLD_Mod200CharacterKey[] {C0005,C0009,C0010,C0011,C0001
					,C0002,C0003,C0004,C0012,C0014,C0015,C0016,C0017,C0018,C0019,C0021
					,C0025,C0048,C0031,C0058,C0032,C0060,C0038,C0022});
		C0014.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0005,C0012,C0017,C0025
					,C0002,C0009,C0013,C0018,C0031,C0003,C0010,C0015,C0019,C0032,C0004
					,C0011,C0016,C0022,C0038,C0048,C0058,C0060,C0059,C0063});
		C0015.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0005,C0017,C0021,C0033
					,C0060,C0002,C0011,C0018,C0022,C0034,C0059,C0003,C0013,C0019,C0031
					,C0048,C0063,C0004,C0014,C0020,C0032,C0012,C0056});
		C0016.setIncompatibility(new OLD_Mod200CharacterKey[] {C0013,C0014,C0038,C0056});
		C0017.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0002,C0003,C0004,C0005
					,C0011,C0013,C0014,C0015,C0018,C0019,C0021,C0022,C0031,C0032,C0048
					,C0012,C0060,C0059,C0063,C0056});
		C0018.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0002,C0003,C0004,C0005
					,C0011,C0013,C0014,C0015,C0018,C0019,C0021,C0022,C0031,C0032,C0048
					,C0012,C0060,C0059,C0063,C0056});
		C0019.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0002,C0003,C0004,C0005
					,C0011,C0013,C0014,C0015,C0018,C0019,C0021,C0022,C0031,C0032,C0048
					,C0012,C0060});
		C0020.setIncompatibility(new OLD_Mod200CharacterKey[] {C0015});		
		C0021.setIncompatibility(new OLD_Mod200CharacterKey[] {C0003,C0004,C0005,C0018,C0013
					,C0015,C0017,C0019,C0031,C0032,C0035,C0037,C0043,C0044,C0046,C0048
					,C0060});
		C0022.setIncompatibility(new OLD_Mod200CharacterKey[] {C0003,C0004,C0005,C0012,C0014
					,C0015,C0017,C0018,C0019,C0024,C0025,C0031,C0032,C0033,C0034,C0038		
					,C0048,C0036,C0058,C0060,C0062,C0013});
		C0024.setIncompatibility(new OLD_Mod200CharacterKey[] {C0005,C0025,C0032,C0034,C0036
					,C0022,C0031,C0033,C0048,C0058});
		C0025.setIncompatibility(new OLD_Mod200CharacterKey[] {C0005,C0014,C0014,C0024,C0032
					,C0034,C0036,C0022,C0031,C0033,C0048,C0062});
		C0025.setAlsoCheck(new OLD_Mod200CharacterKey[] {C0058});
		C0030.setIncompatibility(new OLD_Mod200CharacterKey[] {C0048});
		C0031.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0002,C0003,C0004,C0005
					,C0013,C0014,C0015,C0017,C0018,C0019,C0021,C0022,C0024,C0025,C0033
					,C0034,C0038,C0048,C0036,C0012,C0058,C0060,C0062});
		C0032.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0002,C0003,C0004,C0005
					,C0013,C0014,C0015,C0017,C0018,C0019,C0021,C0022,C0024,C0025,C0033
					,C0034,C0038,C0048,C0036,C0012,C0058,C0060,C0062});
		C0033.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0002,C0003,C0004,C0005
					,C0024,C0038,C0060,C0012,C0025,C0048,C0062,C0015,C0031,C0036,C0022
					,C0032,C0058}); 
		C0034.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0002,C0003,C0004,C0005
					,C0012,C0015,C0022,C0024,C0025,C0031,C0032,C0038,C0018,C0036,C0011
					,C0056,C0058,C0060,C0062,C0063});
		C0035.setIncompatibility(new OLD_Mod200CharacterKey[] {C0005,C0025,C0032,C0034,C0003
					,C0024,C0001,C0058,C0060,C0059,C0022,C0031,C0033,C0048,C0004,C0002
					,C0011,C0012,C0062,C0063,C0056});
		C0037.setIncompatibility(new OLD_Mod200CharacterKey[] {C0021});
		C0038.setIncompatibility(new OLD_Mod200CharacterKey[] {C0002,C0003,C0004,C0005,C0006
					,C0011,C0012,C0013,C0014,C0016,C0022,C0031,C0032,C0033,C0034,C0048
					,C0056,C0060,C0059,C0063});
		C0043.setIncompatibility(new OLD_Mod200CharacterKey[] {C0021});
		C0044.setIncompatibility(new OLD_Mod200CharacterKey[] {C0021});
		C0046.setIncompatibility(new OLD_Mod200CharacterKey[] {C0021,C0059,C0063});
		C0047.setIncompatibility(new OLD_Mod200CharacterKey[] {C0005});
		C0047.setAlsoCheck(new OLD_Mod200CharacterKey[] {C0028});
		C0048.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0002,C0003,C0004,C0005
					,C0006,C0009,C0010,C0011,C0013,C0014,C0015,C0017,C0018,C0019,C0021
					,C0022,C0024,C0025,C0030,C0031,C0032,C0033,C0034,C0038,C0036,C0056
					,C0058,C0012,C0060,C0062,C0059,C0063});
		C0049.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0002,C0003,C0004,C0005
					,C0012,C0015,C0016,C0017,C0018,C0034,C0038,C0036,C0038,C0048,C0058
					,C0059,C0063});
		C0049.setAlsoCheck(new OLD_Mod200CharacterKey[] {C0047,C0028});
		C0037.setIncompatibility(new OLD_Mod200CharacterKey[] {C0012,C0059,C0063});
		C0058.setIncompatibility(new OLD_Mod200CharacterKey[] {C0005,C0014,C0024,C0032,C0034
					,C0013,C0022,C0031,C0033,C0048,C0036,C0003,C0060,C0059,C0004,C0012
					,C0062,C0063,C0056});
		C0058.setAlsoCheck(new OLD_Mod200CharacterKey[] {C0025});
		C0059.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0002,C0003,C0004,C0005
					,C0012,C0014,C0015,C0017,C0018,C0038,C0046,C0048,C0056,C0036,C0057
					,C0058,C0063});
		C0060.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0002,C0003,C0004,C0005
					,C0011,C0012,C0013,C0014,C0015,C0017,C0018,C0019,C0021,C0022,C0031
					,C0032,C0033,C0034,C0038,C0036,C0048,C0058});
		C0062.setIncompatibility(new OLD_Mod200CharacterKey[] {C0005,C0025,C0032,C0034,C0036
					,C0022,C0031,C0033,C0048,C0058});
		C0063.setIncompatibility(new OLD_Mod200CharacterKey[] {C0001,C0002,C0003,C0004,C0005
					,C0012,C0014,C0015,C0017,C0018,C0034,C0036,C0038,C0046,C0048,C0056
					,C0057,C0058,C0059});
		
		
	}
	 
	 
	private String[] codes;
    private String description;
    private OLD_Mod200CharacterKey[] incompatibility;
    private OLD_Mod200CharacterKey[] alsoCheck;

	private OLD_Mod200CharacterKey(String[] codes, String description) {
		this.codes = codes;
		this.description = description;
	}

	private void setIncompatibility(OLD_Mod200CharacterKey[] incompatibility) {
		this.incompatibility = incompatibility;
	}
	public OLD_Mod200CharacterKey[] getIncompatibility() {
		return incompatibility;
	}
	
	private void setAlsoCheck(OLD_Mod200CharacterKey[] alsoCheck) {
		this.alsoCheck = alsoCheck;
	}
	public OLD_Mod200CharacterKey[] getAlsoCheck() {
		return alsoCheck;
	}
	public String[] getCodes() {
		return codes;
	}
	public String getDescription() {
		return description;
	}
	public boolean isPresent(Administration administration) {
		boolean retValue = (getCodes() == null)?false:(getCodes()[administration.ordinal()]!=null);
		return retValue;
	}
	public String getCode(Administration administration) {
		return (getCodes() == null)?null:getCodes()[administration.ordinal()];
	}
}
