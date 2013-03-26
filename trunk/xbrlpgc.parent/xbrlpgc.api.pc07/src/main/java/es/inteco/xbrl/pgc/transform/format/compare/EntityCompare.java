package es.inteco.xbrl.pgc.transform.format.compare;

import es.inteco.xbrl.pgc.transform.format.Entity;


public class EntityCompare implements IFormatIOCompare
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
	Entity entity1 = (Entity) obj1;
	Entity entity2 = (Entity) obj2;
	
	boolean resultComparation = false;
	
	
	resultComparation =  ((entity1 == null ) && (entity2 == null));
	if (!resultComparation)
	{
	    resultComparation = CompareHelper.equalsString(entity1.getId(),entity2.getId()) && 
		 		CompareHelper.equalsString(entity1.getUri(), entity2.getUri()); 	    
	}

	return resultComparation;
    }
    
 
    
    
    
    
    
}
