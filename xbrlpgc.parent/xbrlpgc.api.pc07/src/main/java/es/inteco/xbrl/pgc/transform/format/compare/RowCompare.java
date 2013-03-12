package es.inteco.xbrl.pgc.transform.format.compare;


import es.inteco.xbrl.pgc.transform.format.Row;

public class RowCompare implements IFormatIOCompare
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
	Row row1 = (Row)obj1;
	Row row2 = (Row)obj2;
	
	boolean resultComparation = false;
	
	resultComparation = (row1 == null) && (row2 == null);
	if (!resultComparation && (row1 != null) && (row2 != null))
	{
	    resultComparation = (row1.getIndex() == row2.getIndex()) &&
	    			(CompareHelper.equalsString(row1.getId(),row2.getId())) &&
	    			(CompareHelper.equalsArray(row1.getItem(),row2.getItem()))&&
	    			(CompareHelper.equalsArray(row2.getItem(),row1.getItem()));
	}
	
	
	return resultComparation;
    }
    
    
    
    
    
}
