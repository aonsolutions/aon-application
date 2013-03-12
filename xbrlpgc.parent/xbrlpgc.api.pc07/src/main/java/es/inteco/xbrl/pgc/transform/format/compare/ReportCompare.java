package es.inteco.xbrl.pgc.transform.format.compare;

import java.io.File;
import java.io.FileReader;

import es.inteco.xbrl.pgc.transform.format.Report;

public class ReportCompare implements IFormatIOCompare
{

    /**
     * Punto de entrada de ejecución
     * @param args
     */
    public static void main(String[] args)
    {
	try
	{
	    if ((args == null ) || (args.length != 2))
	    {
		System.out.println("Error en los parámetros de entrada.");
		System.out.println("Use: ReportCompare pathReport1 pathReport2");
	    }
	    else
	    {
		Report report1 = Report.unmarshal(new FileReader(new File(args[0])));
		Report report2 = Report.unmarshal(new FileReader(new File(args[1])));
    
		ReportCompare reportComp = new ReportCompare();
		System.out.println("Comparando reports ...");
		System.out.println("Report1=" + args[0]);
		System.out.println("Report2=" + args[1]);
		System.out.println(" ");
		System.out.println("Resultado:");

		System.out.println("¿Son los reports iguales?  " + reportComp.equals(report1, report2));
	    }
	    
	    
	} catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.transform.format.compare.IFormatIOCompare#equals(java.lang.Object, java.lang.Object)
     */
    public boolean equals(Object obj1, Object obj2)
    {
	
	Report report1 = (Report)obj1;
	Report report2 = (Report)obj2;
	
	boolean resultComparation = false;
	
	resultComparation = (report1 == null) && (report2 == null);
	
	if (!resultComparation && (report1 != null) && (report2 != null))
	{
	    resultComparation = 

		CompareHelper.equalsEntity(report1.getEntity(), report2.getEntity()) &&
		CompareHelper.equalsString(report1.getId(), report2.getId()) &&
		CompareHelper.equalsArray(report1.getModule(),report2.getModule()) &&
		CompareHelper.equalsArray(report2.getModule(),report1.getModule());

	}
	
	return resultComparation;
    }
    
    
    
    
    
    
    
}
