/*
 * Created on 15-dic-2005
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
 * Enumeración para identificar los diferentes meses del año.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 15-dic-2005
 * @version 1.0
 * 
 * @since 1.0
 */
public class Month implements Serializable, Comparable {

    /**
     * Tamaño del array para almacenar todos los tipos.
     */
    private static final int LENGTH = 12;

    /**
     * Array para almacenar todos los tipos.
     */
    private static Month[] ALL = new Month[LENGTH];

    /**
     * Ruta base del fichero de mensajes.
     */
    private static final String BASE_NAME = "com.code.aon.planner.i18n.messages";

    /**
     * Prefijo de la llave de mensajes. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_month_";

    /**
     * Enero.
     */
    public static final Month JANUARY = new Month(0);

    /**
     * Febrero.
     */
    public static final Month FEBRUARY = new Month(1);

    /**
     * Marzo.
     */
    public static final Month MARCH = new Month(2);

    /**
     * Abril.
     */
    public static final Month APRIL = new Month(3);

    /**
     * Mayo.
     */
    public static final Month MAY = new Month(4);

    /**
     * Junio.
     */
    public static final Month JUNE = new Month(5);

    /**
     * Julio.
     */
    public static final Month JULY = new Month(6);

    /**
     * Agosto.
     */
    public static final Month AUGUST = new Month(7);

    /**
     * Septiembre.
     */
    public static final Month SEPTEMBER = new Month(8);

    /**
     * Octubre.
     */
    public static final Month OCTOBER = new Month(9);

    /**
     * Noviembre.
     */
    public static final Month NOVEMBER = new Month(10);

    /**
     * Diciembre.
     */
    public static final Month DECEMBER = new Month(11);

    /**
     * Indice que ocupa este objeto en el array.
     */
    private int index;

    /**
     * Constructor for Month
     * 
     * @param index
     *            int
     */
    private Month(int index) {
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
        if (obj instanceof Month) {
            Month as = (Month) obj;
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
        return compareTo((Month) obj);
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
    private int compareTo(Month ds) {
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
    public static Month get(byte b) {
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
    public static Month get(String name, Locale locale) {
        Iterator iter = getList().iterator();
        while (iter.hasNext()) {
            Month element = (Month) iter.next();
            if (name.equals(element.getName(locale)))
                return element;
        }
        return null;
    }

    /**
     * Devuelve una lista no modificable los enumerados de <code>Month</code>.
     *  
     * @return
     */
    public static List getList() {
        return Arrays.asList(ALL);
    }

}
