/*
 * Created on 23-nov-2005
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
 * Enumeración para identificar los diferentes tipos de estado de la tarea.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 23-nov-2005
 * @version 1.0
 * 
 * @since 1.0
 */
public class TaskStatus implements Serializable, Comparable {

    /**
     * Tamaño del array para almacenar todos los tipos.
     */
    private static final int LENGTH = 6;

    /**
     * Array para almacenar todos los tipos.
     */
    private static TaskStatus[] ALL = new TaskStatus[LENGTH];

    /**
     * Ruta base del fichero de mensajes.
     */
    private static final String BASE_NAME = "com.code.aon.planner.i18n.messages";

    /**
     * Prefijo de la llave de mensajes. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_task_status_";

    /**
     * Desconocida.
     */
    public static final TaskStatus UNKNOWN = new TaskStatus(0);

    /**
     * Activa.
     */
    public static final TaskStatus ACTIVE = new TaskStatus(1);

    /**
     * En ejecución.
     */
    public static final TaskStatus RUNNING = new TaskStatus(2);

    /**
     * En espera.
     */
    public static final TaskStatus HIBERNATE = new TaskStatus(3);

    /**
     * Terminada.
     */
    public static final TaskStatus FINISHED = new TaskStatus(4);

    /**
     * Eliminada.
     */
    public static final TaskStatus REMOVED = new TaskStatus(5);

    /**
     * Indice que ocupa este objeto en el array.
     */
    private int index;

    /**
     * Constructor for TaskStatus
     * 
     * @param index
     *            int
     */
    private TaskStatus(int index) {
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
        if (obj instanceof TaskStatus) {
            TaskStatus as = (TaskStatus) obj;
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
        return compareTo((TaskStatus) obj);
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
    private int compareTo(TaskStatus ds) {
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
    public static TaskStatus get(byte b) {
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
    public static TaskStatus get(String name, Locale locale) {
        Iterator iter = getList().iterator();
        while (iter.hasNext()) {
            TaskStatus element = (TaskStatus) iter.next();
            if (name.equals(element.getName(locale)))
                return element;
        }
        return null;
    }

    /**
     * Devuelve una lista no modificable los enumerados de <code>TaskStatus</code>.
     *  
     * @return
     */
    public static List getList() {
        return Arrays.asList(ALL);
    }

}
