package es.inteco.xbrl.pgc.transform.format.compare;

import es.inteco.xbrl.pgc.transform.format.Record;

public class RecordCompare implements IFormatIOCompare
{



    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.transform.format.compare.IFormatIOCompare#equals(java.lang.Object, java.lang.Object)
     */
    public boolean equals(Object obj1, Object obj2)
    {
	
	Record record1 = (Record)obj1;
	Record record2 = (Record)obj2;
	
	boolean compareResult = false;
	
	compareResult = (record1 == null)  && (record2 == null);
	
	if (!compareResult && (record1 != null) && (record2 != null))
	{
	    compareResult = 
		  	CompareHelper.equalsString(record1.getId(),record2.getId()) &&
	    	CompareHelper.equalsArray(record1.getItem(),record2.getItem()) &&
	    	CompareHelper.equalsArray(record1.getRecord(),record2.getRecord());
	}
	
	return compareResult;
    }
    
    
    
    /**
     * Punto de entrada de ejecución
     * @param args
     */
    public static void main(String[] args)
    {
	// TODO Auto-generated method stub

    }
    
    
    
    
}
