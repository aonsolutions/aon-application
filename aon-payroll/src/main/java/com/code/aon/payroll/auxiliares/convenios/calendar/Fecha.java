package com.code.aon.payroll.auxiliares.convenios.calendar;

import java.util.*;

/**
 * Operaciones para manipular fechas.
 * Una fecha como el <b>15-02-2005</b> tambien se considera un 
 * valor numérico como: <b>20050215</b>
 * <br>
 * <br>
 * 
 * Se puede leer, escribir y comprobar la validez de una
 * fecha. Ademas, hay funciones para determinar si un año 
 * dado es bisiesto o para conocer el numero de dias de un
 * mes determinado.
 * 
 * @author (F. Marqués) 
 * @version (15 noviembre 2005)
 */
public class Fecha {

   public static final int ULTAÑO = 2100;
   public static final int PRIMAÑO = 1900;

	/**
	 * Para pedir una fecha al usuario con el formato 
	 * habitual: día, mes y año
	 * 
	 * @return la fecha pedida con el formato inicial
	 */
	public static int leerFecha() throws Exception
	{
	    Scanner teclado = new Scanner(System.in);
        System.out.print("Fecha (dd mm aaaa): ");
        int d = teclado.nextInt();
        int m = teclado.nextInt();
        int a = teclado.nextInt();
        
        if (!fechaVal(d,m,a)) throw new Exception("Fecha incorrecta");
        
        return a*10000+m*100+d;
	}
	
	/**
	 * Escribe una fecha representada mediante un 
	 * entero como: aaaammdd
	 * con un formato simple
	 */
	public static void escribirFecha(int fecha)
	{
	    int a = fecha/10000;
	    int m = fecha%10000/100;
	    int d = fecha%100;
	    
	    System.out.printf(" %1$2d/%2$2d/%3$4d ",d,m,a);
    }

    /**
     * Un año es bisisesto cuando su valor es múltiplo de 4 y 
     * no es múltiplo de 100. Sin embargo, si que son bisiestos
     * los años seculares, esto es, los múltiplos de 400
     * 
     * @return boolean segun el año sea o no bisiesto
     */
    public static boolean bisiesto(int año) {
        return ((año%4==0 && año%100!=0) || (año%400==0));
    }
    
    /**
     * Se consideran años válidos los comprendidos
     * entre Fecha.PRIMAÑO y Fecha.ULTAÑO (ambos incluídos)
     */
    public static boolean añoVal(int año) {
        return (año>=PRIMAÑO && año<=ULTAÑO);
    }
    
    /**
     * Determina si un mes es válido
     */
    public static boolean mesVal(int mes) {
        return (mes >=1 && mes <=12);
    }
    
    /**
     * Determina si un día es válido
     *
     * Un día es válido si está comprendido entre el
     * día 1 y el último día posible del mes.
     */
    public static boolean diaVal(int dia, int mes, int año) {
        return (dia>=1 && dia <=numDiasMes(mes,año));
    }
    
    /**
     * Devuelve el número de días del mes, considerando
     * febrero como de 28 o 29 días según el año sea o no
     * bisiesto
     * 
     * Debe ejecutarse con un mes y un año válido.
     */
    public static int numDiasMes(int mes, int año) {
        int dias = 31;
        switch (mes) {
            case 2: if (bisiesto(año)) dias=29; else dias=28;break;
            case 4: 
            case 6:
            case 9:
            case 11: dias = 30; break;
        }
        return dias;
    }
    
    /**
     * Determina si una fecha en formato:
     * aaaammdd
     * es válida (factible)
     */
    public static boolean fechaVal(int fecha) {
        int a = fecha/10000;
	    int m = fecha%10000/100;
	    int d = fecha%100;
	    
	    return fechaVal(d,m,a);
	}
    
	/**
	 * Determina si una fecha expresada mediante
	 * tres valores enteros para el día el mes y el año
	 * es válida
	 */
    public static boolean fechaVal(int d,int m,int a) {
        return (añoVal(a) && mesVal(m) && diaVal(d,m,a));
    }
    
}
