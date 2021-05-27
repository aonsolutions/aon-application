package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum FiscalActivityInfoKey implements IResourceable, IStringEnum {
	
	// Trimestre de inicio de la actividad.
	A01 ("A01",FiscalActivityInfoType.INFO,2012,9999,false,Integer.class,false,false,false,"0",null
		,new FiscalActivityInfoKeyEntry[] {
			new FiscalActivityInfoKeyEntry(0,"-"),
			new FiscalActivityInfoKeyEntry(1,"Segundo Trimestre"),
			new FiscalActivityInfoKeyEntry(2,"Tercer Trimestre"),
			new FiscalActivityInfoKeyEntry(3,"Cuarto Trimestre")
		}),
	
	// Comunidad, Sociedad Civil o Similar. Porcentaje de participación.
	A02 ("A02",FiscalActivityInfoType.INFO,2012,9999,false,Double.class,false,false,false,null,null,null),
	
	// Actividad de Temporada. nº de dias de ejercicio en el año anterior.	
	A03 ("A03",FiscalActivityInfoType.INFO,2012,9999,false,Integer.class,false,false,false,null,null,null),
	
	// Nuevas actividades iniciadas a partir del 1 de enero del año anterior. Año de inicio.
	A04 ("A04",FiscalActivityInfoType.INFO,2012,9999,false,Integer.class,false,false,false,null,null,null),
	
	// Deducción por rentas obtenidas en Ceuta y Melilla.
	A05 ("A05",FiscalActivityInfoType.INFO,2012,9999,false,Integer.class,false,false,false,"0",null
		,new FiscalActivityInfoKeyEntry[] {
			new FiscalActivityInfoKeyEntry(0,"NO"),
			new FiscalActivityInfoKeyEntry(1,"SI")
		}),
	
	// Número de bateas y de barcos auxiliares de la empresa.
	B06 ("B06",FiscalActivityInfoType.INFO,2012,9999,false,Integer.class,true,false,false,"1",null
		,new FiscalActivityInfoKeyEntry[] {
			new FiscalActivityInfoKeyEntry(1,"Una batea y ningún barco."),
			new FiscalActivityInfoKeyEntry(2,"Una batea y un barco de menos de 15 TRB."),
			new FiscalActivityInfoKeyEntry(3,"Una batea y un barco de 15 a 30 TRB"),
			new FiscalActivityInfoKeyEntry(4,"Una batea y un barco de más de 30 TRB"),
			new FiscalActivityInfoKeyEntry(5,"Dos bateas y ningún barco"),
			new FiscalActivityInfoKeyEntry(6,"Dos bateas y un barco de menos de 15 TRB."),
			new FiscalActivityInfoKeyEntry(7,"Otros: numero de bateas, barcos o TRB distintos de los anteriores.")
		}),
		
	// Ejerce la actividad en un sólo local o sin él.
	A06 ("A06",FiscalActivityInfoType.INFO,2012,9999,false,Integer.class,true,false,false,"0",null
		,new FiscalActivityInfoKeyEntry[] {
			new FiscalActivityInfoKeyEntry(0,"NO"),
			new FiscalActivityInfoKeyEntry(1,"SI")
		}),
		
	// Número de vehículos afectos de la actividad.
	A07 ("A07",FiscalActivityInfoType.INFO,2012,9999,false,Integer.class,true,false,false,"0",null,null),
	
	// Capacidad de carga del vehículo superior a 1000 Kg.
	A08 ("A08",FiscalActivityInfoType.INFO,2012,9999,false,Integer.class,true,false,false,"0",null
		,new FiscalActivityInfoKeyEntry[] {
			new FiscalActivityInfoKeyEntry(0,"NO"),
			new FiscalActivityInfoKeyEntry(1,"SI")
		}),
	
	// Municipio donde se ejerce la actividad.
	A09 ("A09",FiscalActivityInfoType.INFO,2012,9999,false,Integer.class,true,false,false,"6",null
		,new FiscalActivityInfoKeyEntry[] {
			new FiscalActivityInfoKeyEntry(1,"Hasta 2.000 habitantes."),
			new FiscalActivityInfoKeyEntry(2,"Desde 2.001 hasta 5.000 habitantes."),
			new FiscalActivityInfoKeyEntry(3,"Desde 5.001 hasta 10.000 habitantes."),
			new FiscalActivityInfoKeyEntry(4,"Desde 10.001 hasta 50.000 habitantes."),
			new FiscalActivityInfoKeyEntry(5,"Desde 50.001 hasta 100.000 habitantes."),
			new FiscalActivityInfoKeyEntry(6,"Más de 100.000 habitantes."),
			new FiscalActivityInfoKeyEntry(7,"Madrid o Barcelona."),
		}),
		
	// Número de empleados al inicio de ejercicio (o al inicio de la actividad).
	A10 ("A10",FiscalActivityInfoType.INFO,2014,9999,false,Integer.class,true,false,false,"0",null,null),
	
	// Indique si la actividad se realiza con tractocamiones y el titular carece de semirremolques.
	C10 ("C10",FiscalActivityInfoType.INFO,2012,9999,false,Integer.class,false,false,false,"0",null
		,new FiscalActivityInfoKeyEntry[] {
			new FiscalActivityInfoKeyEntry(0,"NO"),
			new FiscalActivityInfoKeyEntry(1,"SI")
		}),
	// Indique si la actividad se realiza con un único tractocamión y sin semirremolques.
	C11 ("C11",FiscalActivityInfoType.INFO,2012,9999,false,Integer.class,false,false,false,"0",null
		,new FiscalActivityInfoKeyEntry[] {
			new FiscalActivityInfoKeyEntry(0,"NO"),
			new FiscalActivityInfoKeyEntry(1,"SI")
		}),
	// Si en 2013 realiza la actividad en LORCA, seleccione:
	A11 ("A11",FiscalActivityInfoType.INFO,2012,9999,false,Integer.class,false,false,false,"0",null
		,new FiscalActivityInfoKeyEntry[] {
			new FiscalActivityInfoKeyEntry(0,"-"),
			new FiscalActivityInfoKeyEntry(1,"Actividad relizada exclusivamente en Lorca."),
			new FiscalActivityInfoKeyEntry(2,"Actividad realizada en Lorca y otros municipios.")
		}),		 
																 
	// Si en 2012 realió la actividad en LORCA, estando en el régimen simplificado de IVA, seleccione:
//	A12 ("A12",FiscalActivityInfoType.INFO,2012,9999,false,Integer.class,false,false,false,"0"
//		,new FiscalActivityInfoKeyEntry[] {
//			new FiscalActivityInfoKeyEntry(0,"-"),
//			new FiscalActivityInfoKeyEntry(1,"Actividad relizada exclusivamente en Lorca."),
//			new FiscalActivityInfoKeyEntry(2,"Actividad realizada en Lorca y otros municipios."),
//			new FiscalActivityInfoKeyEntry(3,"Actividad en Lorca con autorización en 2012 de reducción de los módulos de IVA aplicables.")
//		}),		 			 
	// Indique si el titular es discapacitado en grado igual o superior al 33%
	A13 ("A13",FiscalActivityInfoType.INFO,2012,9999,false,Integer.class,false,false,false,"0",null
		,new FiscalActivityInfoKeyEntry[] {
			new FiscalActivityInfoKeyEntry(0,"NO"),
			new FiscalActivityInfoKeyEntry(1,"SI")
		}),

	// MODULES
	M01 ("M01",FiscalActivityInfoType.MODULE,2012,9999,true,Double.class,false,false,false,"0",null,null),
	M011 ("M011",FiscalActivityInfoType.MODULE,2012,9999,false,Integer.class,false,false,false,"0",FiscalActivityInfoKey.M01,null),
	M012 ("M012",FiscalActivityInfoType.MODULE,2012,9999,false,Integer.class,false,false,false,"0",FiscalActivityInfoKey.M01,null),
	M013 ("M013",FiscalActivityInfoType.MODULE,2012,9999,false,Integer.class,false,false,false,"0",FiscalActivityInfoKey.M01,null),
	M014 ("M014",FiscalActivityInfoType.MODULE,2012,9999,false,Integer.class,false,false,false,"1800",FiscalActivityInfoKey.M01,null),
	M02 ("M02",FiscalActivityInfoType.MODULE,2012,9999,true,Double.class,false,false,false,"0",null,null),
	M021 ("M021",FiscalActivityInfoType.MODULE,2012,9999,false,Integer.class,false,false,false,"0",FiscalActivityInfoKey.M02,null),
	M022 ("M022",FiscalActivityInfoType.MODULE,2012,9999,false,Integer.class,false,false,false,"0",FiscalActivityInfoKey.M02,null),
	M023 ("M023",FiscalActivityInfoType.MODULE,2012,9999,false,Integer.class,false,false,false,"0",FiscalActivityInfoKey.M02
		,new FiscalActivityInfoKeyEntry[] {
			new FiscalActivityInfoKeyEntry(0,"NO"),
			new FiscalActivityInfoKeyEntry(1,"SI")
		}),
	M024 ("M024",FiscalActivityInfoType.MODULE,2012,9999,false,Integer.class,false,false,false,"0",FiscalActivityInfoKey.M02,null),
	M025 ("M025",FiscalActivityInfoType.MODULE,2012,9999,false,Integer.class,false,false,false,"0",FiscalActivityInfoKey.M02,null),
	M03 ("M03",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M04 ("M04",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M05 ("M05",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M06 ("M06",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M07 ("M07",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M08 ("M08",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M09 ("M09",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M10 ("M10",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M11 ("M11",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M12 ("M12",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M13 ("M13",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M14 ("M14",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M15 ("M15",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M16 ("M16",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M17 ("M17",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M18 ("M18",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M19 ("M19",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M20 ("M20",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M21 ("M21",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M22 ("M22",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M23 ("M23",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M24 ("M24",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M25 ("M25",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M26 ("M26",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M27 ("M27",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M28 ("M28",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M29 ("M29",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M30 ("M30",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M31 ("M31",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M32 ("M32",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M33 ("M33",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M34 ("M34",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M35 ("M35",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M36 ("M36",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M37 ("M37",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M38 ("M38",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M39 ("M39",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M40 ("M40",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M41 ("M41",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M42 ("M42",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M43 ("M43",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M44 ("M44",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M45 ("M45",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M46 ("M46",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M47 ("M47",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M48 ("M48",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M49 ("M49",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M50 ("M50",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M51 ("M51",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M52 ("M52",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M53 ("M53",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M54 ("M54",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M55 ("M55",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M56 ("M56",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M57 ("M57",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M58 ("M58",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M59 ("M59",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M60 ("M60",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),
	M61 ("M61",FiscalActivityInfoType.MODULE,2012,9999,false,Double.class,false,false,false,"0",null,null),

	I01 ("I01",FiscalActivityInfoType.IRPF_INFO,2012,9999,false,Double.class,false,true ,false,"0",null,null),
	I02 ("I02",FiscalActivityInfoType.IRPF_INFO,2012,9999,false,Double.class,false,true ,false,"0",null,null),
	I03 ("I03",FiscalActivityInfoType.IRPF_INFO,2012,9999,false,Double.class,false,false,false,"0",null,null),
	I04 ("I04",FiscalActivityInfoType.IRPF_INFO,2012,9999,false,Double.class,false,true ,false,"0",null,null),
	I05 ("I05",FiscalActivityInfoType.IRPF_INFO,2012,9999,false,Double.class,false,false,true ,"0",null,null),
	I06 ("I06",FiscalActivityInfoType.IRPF_INFO,2012,9999,false,Double.class,false,true ,false,"0",null,null),
	I07 ("I07",FiscalActivityInfoType.IRPF_INFO,2012,9999,false,Double.class,false,true ,false,"0",null,null),
	I08 ("I08",FiscalActivityInfoType.IRPF_INFO,2012,9999,false,Double.class,false,true ,false,"0",null,null),
	I09 ("I09",FiscalActivityInfoType.IRPF_INFO,2012,9999,false,Double.class,false,true ,false,"0",null,null),
	I10 ("I10",FiscalActivityInfoType.IRPF_INFO,2012,9999,false,Double.class,false,true ,false,"0",null,null),
	I11 ("I11",FiscalActivityInfoType.IRPF_INFO,2012,9999,false,Double.class,false,true ,false,"0",null,null),
	I12 ("I12",FiscalActivityInfoType.IRPF_INFO,2012,9999,false,Double.class,false,true ,false,"0",null,null),
	I13 ("I13",FiscalActivityInfoType.IRPF_INFO,2012,9999,false,Double.class,false,true ,false,"0",null,null),
	I14 ("I14",FiscalActivityInfoType.IRPF_INFO,2012,9999,false,Double.class,false,true ,false,"0",null,null),
	I15 ("I15",FiscalActivityInfoType.IRPF_INFO,2012,9999,false,Double.class,false,true ,false,"0",null,null),
	
	V01 ("V01",FiscalActivityInfoType.VAT_INFO,2012,9999,false,Double.class,false,true ,false,"0",null,null),
	V02 ("V02",FiscalActivityInfoType.VAT_INFO,2012,9999,false,Double.class,false,true ,false,"0",null,null),
	V03 ("V03",FiscalActivityInfoType.VAT_INFO,2012,9999,false,Double.class,false,true ,false,"0",null,null),
	V04 ("V04",FiscalActivityInfoType.VAT_INFO,2012,9999,false,Double.class,false,true ,false,"0",null,null),
	V05 ("V05",FiscalActivityInfoType.VAT_INFO,2012,9999,false,Double.class,false,true ,false,"0",null,null),
	V06 ("V06",FiscalActivityInfoType.VAT_INFO,2012,9999,false,Double.class,false,true ,false,"0",null,null),
	;
	
	public static final String MODULE_PREFIX = "M";
	
	private String key;
	private FiscalActivityInfoType type;
	private int fromYear;
	private int toYear;
	private Class<?> clazz;
	private boolean detailed;
	private boolean required;
	private boolean calculated;
	private boolean title;
	private String defaultValue;
	private FiscalActivityInfoKey parentKey;
	private FiscalActivityInfoKeyEntry[] values;
	
	
	
	private FiscalActivityInfoKey(String key
								,FiscalActivityInfoType type
								,int fromYear
								,int toYear
								,boolean detailed
								,Class<?> clazz
								,boolean required
								,boolean calculated
								,boolean title
								,String defaultValue
								,FiscalActivityInfoKey parentKey
								,FiscalActivityInfoKeyEntry[] values) {
		this.key = key;
		this.type = type;
		this.fromYear = fromYear;
		this.toYear = toYear;
		this.required = required;
		this.calculated = calculated;
		this.title = title;
		this.detailed = detailed;
		this.clazz = clazz;
		this.defaultValue = defaultValue;
		this.parentKey = parentKey; 
		this.values = values;
	}

    private static final String MSG_KEY_PREFIX = "aon_enum_activityAddInfo_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
	@Override
	public String getValue() {
		return getKey();
	}

	public FiscalActivityInfoType getType() {
		return type;
	}

	public String getKey() {
		return key;
	}
	
	public int getFromYear() {
		return fromYear;
	}
	
	public int getToYear() {
		return toYear;
	}
	
	public boolean isDetailed() {
		return detailed;
	}

	public boolean isRequired() {
		return required;
	}
	
	public boolean isCalculated() {
		return calculated;
	}

	public boolean isTitle() {
		return title;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public FiscalActivityInfoKey getParentKey() {
		return parentKey;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public FiscalActivityInfoKeyEntry[] getValues() {
		return values;
	}
	public boolean isRounded() {
		return true;
	}
	public boolean isChoice() {
		boolean ret = (getValues() != null && getValues().length > 0); 
		return ret;
	}
}