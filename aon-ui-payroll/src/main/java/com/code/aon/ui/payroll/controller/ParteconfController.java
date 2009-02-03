package com.code.aon.ui.payroll.controller;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.payroll.auxiliares.convenios.calendar.Calendario;
import com.code.aon.payroll.auxiliares.convenios.calendar.Mes;
import com.code.aon.ui.form.LinesController;


public class ParteconfController extends LinesController {
    
	 int num[] = {1,2,3,4,5,6};
	 
	 
	 public  int load() {

	    System.out.println("anyo:");		
		Calendar now = Calendar.getInstance();
		now.set(1950, 1, 1);
		
		if ((now.get(Calendar.YEAR) % 4 == 0) && ((now.get(Calendar.YEAR) % 100 != 0) || (now.get(Calendar.YEAR) % 400 == 0)))
			System.out.println("El año es bisiesto");
		else
			System.out.println("El año no es bisiesto");
     
  
		int dia = now.get(Calendar.DAY_OF_MONTH);
		int mes = now.get(Calendar.MONTH);
		int anio = now.get(Calendar.YEAR);
		
		
		System.out.println(now.get(Calendar.DAY_OF_MONTH));
		System.out.println(now.get(Calendar.MONTH));
		System.out.println(now.get(Calendar.YEAR));
       
	    List<Mes> Listameses = new LinkedList<Mes>();
		Mes enero = new Mes();
		Calendario c = new Calendario();
		c.setCdg(0);
		c.setFeccal(null);
		c.setTipdia(null);
		
		
  while (mes<=12)
   {
 	  while (dia <= numDiasMes(mes,anio))      
 			  {    		      		  
 		        System.out.print(dia);
 		        System.out.print(" ");
 		       // enero.getListadias().add(c);
 		        dia++; 
 		       
 		      }
 	   System.out.println(" ");
 	   dia=1;  
 	   mes++;
 			  }  
   int i=0;
return i;
  
   }

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
	
	public static boolean bisiesto(int año) {
     return ((año%4==0 && año%100!=0) || (año%400==0));
 }
	
	
	
}
