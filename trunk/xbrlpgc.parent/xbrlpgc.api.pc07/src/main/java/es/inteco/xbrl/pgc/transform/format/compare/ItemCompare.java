package es.inteco.xbrl.pgc.transform.format.compare;


import es.inteco.xbrl.pgc.transform.format.Item;



public class ItemCompare implements IFormatIOCompare
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
	Item item1 = (Item)obj1;
	Item item2 = (Item)obj2;
	boolean resultComparation = false;
	
	resultComparation = (item1 == null ) && (item2 == null);
	
	if (!resultComparation && (item1 != null) && (item2 != null))
	{
	    resultComparation = 
	    	CompareHelper.equalsString(item1.getValue(), item2.getValue()) &&
		CompareHelper.equalsString(item1.getDecimals(),item2.getDecimals()) &&
		CompareHelper.equalsString(item1.getId(),item2.getId()) &&
		CompareHelper.equalsDate(item1.getReportingDate(),item2.getReportingDate()) &&
		CompareHelper.equalsSign(item1.getSign(),item2.getSign()) &&
		CompareHelper.equalsUnit(item1.getUnit(),item2.getUnit()) &&
		CompareHelper.equalsArray(item1.getNote(),item2.getNote()) &&
	    	CompareHelper.equalsArray(item2.getNote(),item1.getNote());
	}
	return resultComparation;
    }
    
    
    
    
    
}
