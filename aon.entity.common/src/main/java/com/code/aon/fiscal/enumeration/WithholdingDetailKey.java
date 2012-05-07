package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum WithholdingDetailKey  implements IResourceable, IStringEnum {

	//A. Rendimientos de trabajo. Personas empleadas por cuenta ajena en general.
	A("A",false),
	//B. Rendimientos de trabajo. Pensionistas y perceptores de haberes pasivos y demás prestaciones previstas en el Reglamento del Impuesto.
	B("B",true),
	//C. Rendimientos de trabajo. Prestaciones o subsidios de desempleo.
	C("C",false),
	//D. Rendimientos de trabajo. Prestaciones o subsidios de desempleo abonadas en la modalidad de pago único.
	D("D",false),
	//E. Rendimientos de trabajo. Consejeros/as y administradores/as.
	E("E",false),
	//F. Rendimientos de trabajo. Cursos, conferencias, seminarios y similares y elaboración de obras literarias, artísticas o científicas.
	F("F",true),
	//G. Rendimientos de actividades económicas. Actividades profesionales.
	G("G",true),
	//H. Rendimientos de actividades económicas. Actividades agrícolas, ganaderas y forestales y actividades empresariales en estimación objetiva a las que se refiere el Reglamento del Impuesto.
	H("H",true),
	//I. Rendimientos de actividades económicas. Rendimientos a que se refiere el Reglamento del Impuesto.
	I("I",true),
	//J. Imputación de rentas por la cesión de derechos de imagen.
	J("J",false),
	//K. Premios y ganancias patrimoniales de los vecinos derivadas de los aprovechamientos forestales en montes públicos.
	K("K",true),
	//L. Rentas exentas y dietas exceptuadas de gravamen.
	L("L",true);

	private String key;
	private boolean subkeyEnabled;
	
	private WithholdingDetailKey( String key, boolean subkeyEnabled) {
		this.key = key;
		this.subkeyEnabled = subkeyEnabled;
	}
	
	public String getKey() {
		return key;
	}
	public boolean isSubkeyEnabled() {
		return subkeyEnabled;
	}
	
	@Override
	public String getValue() {
		return getKey();
	}
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.fiscal.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_withholding_detail_key_";

    /**
     * Returns a <code>String</code> with the transalation <code>Locale</code>
     * for the locale.
     * 
     * @param locale Required Locale.
     * 
     * @return String a <code>String</code>.
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}