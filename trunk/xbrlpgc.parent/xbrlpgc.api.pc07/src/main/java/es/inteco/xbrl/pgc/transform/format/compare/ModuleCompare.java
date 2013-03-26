package es.inteco.xbrl.pgc.transform.format.compare;

import es.inteco.xbrl.pgc.transform.format.Module;


public class ModuleCompare implements IFormatIOCompare
{

    /**
     * Punto de entrada de ejecución
     * @param args
     */
    public static void main(String[] args)
    {
	// TODO Auto-generated method stub

    }

    public boolean equals(Object obj1, Object obj2)
    {
	Module module1 = (Module)obj1;
	Module module2 = (Module)obj2;
	
	boolean resultComparation = false;
	
	resultComparation = ((module1 == null) && (module2 == null));
	if (!resultComparation && (module1 != null) && (module2 != null))
	{
	    resultComparation = 
		CompareHelper.equalsString(module1.getId(), module2.getId()) &&
		CompareHelper.equalsUnit(module1.getBaseUnit(),module2.getBaseUnit()) &&
		CompareHelper.equalsArray(module1.getItem(), module2.getItem()) &&
		CompareHelper.equalsArray(module2.getItem(), module1.getItem()) &&
		CompareHelper.equalsArrayRecord(module1.getRecord(), module2.getRecord()) &&
		CompareHelper.equalsArrayRecord(module2.getRecord(), module1.getRecord()) &&
		CompareHelper.equalsArray(module1.getTable(),module2.getTable()) &&
		CompareHelper.equalsArray(module2.getTable(),module1.getTable()) &&
		CompareHelper.equalsDate(module1.getReportingDateStart(), module2.getReportingDateStart()) &&
		CompareHelper.equalsDate(module1.getReportingDateEnd(), module2.getReportingDateEnd());
	    
	}
	
	
	return resultComparation;
    }
    
    
    
    
    
}
