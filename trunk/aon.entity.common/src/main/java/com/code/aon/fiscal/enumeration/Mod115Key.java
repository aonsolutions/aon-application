package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;
import com.code.aon.config.enumeration.Administration;

public enum Mod115Key implements IFiscalModelKey, IResourceable, IStringEnum  {

	// Rendimientos Dinerarios.
	CT1("115-CT1",true ,0,false,false,null),
	// Rendimientos Dinerarios. Número de perceptores
	C01("115-01" ,false,1,false,false,null),
	// Rendimientos Dinearios. Base de las retencioines e ingresos a cuenta.
	C02("115-02" ,false,1,true ,false,null),
	// Rendimientos Dinerarios. Retenciones e ingresos a cuenta.
	C03("115-03" ,false,1,true ,false,null),
	// Rendimientos Especie.
	CT2("115-CT2",true ,0,false,false,new Administration[]{Administration.ALAVA,Administration.BIZKAIA,Administration.GIPUZKOA}),
	// Rendimientos Especie. Número de perceptores
	C04("115-04" ,false,1,false,false,new Administration[]{Administration.ALAVA,Administration.BIZKAIA,Administration.GIPUZKOA}),
	// Rendimientos Especie. Base de las retencioines e ingresos a cuenta.
	C05("115-05" ,false,1,true ,false,new Administration[]{Administration.ALAVA,Administration.BIZKAIA,Administration.GIPUZKOA}),
	// Rendimientos Especie. Retenciones e ingresos a cuenta.
	C06("115-06" ,false,1,true ,false,new Administration[]{Administration.ALAVA,Administration.BIZKAIA,Administration.GIPUZKOA}),
	// Sep.
	SEP("115-SEP",true ,0,false,false,null),
	// A Deducir. Declaraciones Complementarias.
	C07("115-07" ,false,1,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	// A Ingresar.
	C08("115-08" ,false,0,false,true ,null),
	// Recargo de prorroga
	C09("115-09" ,false,0,false,false,new Administration[]{Administration.ALAVA}),
	// Intereses de demora
	C10("115-10" ,false,0,false,false,new Administration[]{Administration.ALAVA}),
	// Total Deuda Tributaria.
	C11("115-11" ,false,0,false,true ,new Administration[]{Administration.ALAVA});

    private static final String BASE_NAME = "com.code.aon.fiscal.i18n.keys";
    private static final String MSG_KEY_PREFIX = "aon_enum_mod";

    public static Mod115Key getKeyWithValue( String value ) {
    	for (Mod115Key key : Mod115Key.values() ) {
    		if (StringUtils.equals(value, key.getValue())) {
    			return key;
    		}
    	}
    	throw new IllegalArgumentException("No enum constant " + Mod115Key.class.getName() + " for value " + value);
    }
    
    private String value;
    private boolean title;
    private int level;
    private boolean difEnabled;
    private boolean total;
    private Administration[] administrations;
    
    private Mod115Key(String value,boolean title,int level,boolean difEnabled,boolean total,Administration[] administrations) {
    	this.value = value;
    	this.difEnabled = difEnabled;
    	this.level = level;
    	this.title = title;
    	this.total = total;
    	this.administrations = administrations;
    }
    
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + value);
    }

	public boolean isDifEnabled() {
		return !isTitle() && difEnabled;
	}
	
	public boolean isTitle() {
		return title;
	}
	
	public boolean isTotal() {
		return total;
	}

	@Override
	public boolean isDescriptionEnabled() {
		return false;
	}

	public int getLevel() {
		return level;
	}

	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public boolean accept(Administration administration) {
		if (administrations == null) {
			return true;
		}
		for (Administration admin : administrations) {
			if (admin == administration) {
				return true;
			}
		}
		return false;
	}
}