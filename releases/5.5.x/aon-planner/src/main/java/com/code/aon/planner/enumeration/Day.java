/*
 * Created on 15-dec-2005
 *
 */
package com.code.aon.planner.enumeration;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Enumeración para identificar los diferentes días de la semana.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 15-dec-2005
 * @version 1.0
 * 
 * @since 1.0
 */
public class Day implements Serializable, Comparable {

    /**
     * Tamaño del array para almacenar todos los tipos.
     */
    private static final int LENGTH = 7;

    /**
     * Array para almacenar todos los tipos.
     */
    private static Day[] ALL = new Day[LENGTH];

    /**
     * Ruta base del fichero de mensajes.
     */
    private static final String BASE_NAME = "com.code.aon.planner.i18n.messages";

    /**
     * Prefijo de la llave de mensajes. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_day_";

    /**
     * Lunes.
     */
    public static final Day MONDAY = new Day(0);

    /**
     * Martes.
     */
    public static final Day TUESDAY = new Day(1);

    /**
     * Miercoles.
     */
    public static final Day WEDNESDAY = new Day(2);

    /**
     * Jueves.
     */
    public static final Day THURSDAY = new Day(3);

    /**
     * Viernes.
     */
    public static final Day FRIDAY = new Day(4);

    /**
     * Sabado.
     */
    public static final Day SATURDAY = new Day(5);

    /**
     * Domingo.
     */
    public static final Day SUNDAY = new Day(6);

    /**
     * Lista de acrónimos correspondientes a cada día de la semana.
     */
	public static final String[] acronyms = {"MO", "TU", "WE", "TH", "FR", "SA", "SU"};

	/**
     * Indice que ocupa este objeto en el array.
     */
    private int index;

    /**
     * Constructor for Day
     * 
     * @param index
     *            int
     */
    private Day(int index) {
        this.index = index;
        ALL[index] = this;
    }

    /**
     * Method readResolve
     * 
     * @return Object
     */
    private Object readResolve() {
        return ALL[index];
    }

    /**
     * Indica cuándo otro objeto "es igual" a éste.
     * 
     * @param obj
     *            La referencia al objeto que se quiere comparar.
     * @return boolean True si el objeto es igual al del parámetro; False de
     *         otra manera.
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object obj) {
        if (obj instanceof Day) {
            Day as = (Day) obj;
            return as.index == index;
        }
        return false;
    }

    /**
     * Devuelve un valor "hash code" para el objeto. Este método se implementa
     * en beneficio de las "hashtables".
     * 
     * @return Devuelve un valor "hash code" para el objeto.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return index;
    }

    /**
     * Devuelve un String que representa a este objeto.
     * 
     * @return String Un String que representa a este objeto.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return Integer.toString(index);
    }

    /**
     * Devuelve un <code>int</code> que representa a este objeto.
     * 
     * @return int un <code>int</code> que representa a este objeto.
     */
    public int getValue() {
        return index;
    }

    /**
     * Devuelve el acrónimo asociado al día en concreto.
     * 
     * @return
     */
    public String getAcronym() {
    	return acronyms[index];
    }

    /**
     * Devuelve un <code>String</code> con la traducción correspondiente al <code>Locale</code>
     * pasado por parámetro.
     * 
     * @return String un <code>String</code>.
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
        return bundle.getString(MSG_KEY_PREFIX + getValue());
    }

    /**
     * Compara este objeto con el especificado. Devuelve un entero negativo,
     * cero o un entero positivo si este objeto es menor, igual o mayor que el
     * indicado.
     * 
     * @param obj
     *            El objeto a ser comparado.
     * @return Un entero negativo, cero o un entero positivo si este objeto es
     *         menor, igual o mayor que el indicado.
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(Object obj) {
        return compareTo((Day) obj);
    }

    /**
     * Compara este objeto con el especificado. Devuelve un entero negativo,
     * cero o un entero positivo si este objeto es menor, igual o mayor que el
     * indicado.
     * 
     * @param ds
     *            El objeto a ser comparado.
     * @return Un entero negativo, cero o un entero positivo si este objeto es
     *         menor, igual o mayor que el indicado.
     * @see java.lang.Comparable#compareTo(Object)
     */
    private int compareTo(Day ds) {
        if (this == ds) {
            return 0;
        }
        return (this.index < ds.index) ? (-1) : (+1);
    }

    /**
     * Devuelve el tipo de estado del evento correspondiente.
     * 
     * @param b
     *            El identificativo de este estado del evento.
     * @return El tipo de estado del evento.
     */
    public static Day get(byte b) {
        if (b < 0 || b >= ALL.length) {
            throw new ArrayIndexOutOfBoundsException(" Looking for item " + b //$NON-NLS-1$
                    + " on a " + ALL.length + "-length array."); //$NON-NLS-2$ //$NON-NLS-1$
        }
        return ALL[b];
    }

    /**
     * Devuelve el tipo de estado del expediente correspondiente.
     * 
     * @param name
     * @param locale
     * @return El tipo de estado del evento.
     */
    public static Day get(String name, Locale locale) {
        Iterator iter = getList().iterator();
        while (iter.hasNext()) {
            Day element = (Day) iter.next();
            if (name.equals(element.getName(locale)))
                return element;
        }
        return null;
    }

    /**
     * Devuelve una lista no modificable los enumerados de <code>Day</code>.
     *  
     * @return
     */
    public static List getList() {
        return Arrays.asList(ALL);
    }

    /**
	 * Devuelve el Día asociado al acrónimo pasado por parámetro.
	 *  
	 * @param acronym
	 * @return
	 */
    public static Day getDay(String acronym) {
    	for (int i = 0; i < acronyms.length; i++) {
    		if (acronyms[i].equals(acronym))
    			return ALL[i]; 
		}
    	return null;
    }

}
