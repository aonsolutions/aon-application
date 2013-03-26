package es.inteco.xbrl.pgc.transform.format.compare;


import es.inteco.xbrl.pgc.transform.format.Table;

public class TableCompare implements IFormatIOCompare
{

    /**
     * Punto de entrada de ejecución
     * @param args
     */
    public static void main(String[] args)
    {
	// TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.transform.format.compare.IFormatIOCompare#equals(java.lang.Object, java.lang.Object)
     */
    public boolean equals(Object obj1, Object obj2)
    {
	
	Table table1 = (Table)obj1;
	Table table2 = (Table)obj2;
	
	boolean resultComparation = false;
	
	resultComparation = (table1 == null) && (table2 == null);
	
	if (!resultComparation && (table1 != null ) && (table2 != null))
	{
	    resultComparation = CompareHelper.equalsString(table1.getId(), table2.getId()) &&
	    			CompareHelper.equalsArray(table1.getRow(),table2.getRow()) &&
	    			CompareHelper.equalsArray(table2.getRow(),table1.getRow());
    
	}
	
	
	return resultComparation;
    }
    
    
    
    
    
}
