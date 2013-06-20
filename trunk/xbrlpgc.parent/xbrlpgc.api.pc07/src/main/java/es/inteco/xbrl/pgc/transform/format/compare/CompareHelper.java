package es.inteco.xbrl.pgc.transform.format.compare;


import java.util.Enumeration;
import java.util.Hashtable;

import org.exolab.castor.types.Date;

import es.inteco.xbrl.pgc.transform.format.Entity;
import es.inteco.xbrl.pgc.transform.format.Item;
import es.inteco.xbrl.pgc.transform.format.Module;
import es.inteco.xbrl.pgc.transform.format.Note;
import es.inteco.xbrl.pgc.transform.format.Record;
import es.inteco.xbrl.pgc.transform.format.Report;
import es.inteco.xbrl.pgc.transform.format.Row;
import es.inteco.xbrl.pgc.transform.format.Table;
import es.inteco.xbrl.pgc.transform.format.types.ItemSignType;
import es.inteco.xbrl.pgc.transform.format.types.Unittype;
import es.inteco.xbrl.pgc.utils.PGCUtils;



/**
 * Clase ayuda para el proceso de comparación.
 *
 */
public class CompareHelper
{
    
    private static final IFormatIOCompare entityCompare = new EntityCompare();
    private static final IFormatIOCompare itemCompare = new ItemCompare();
    private static final IFormatIOCompare moduleCompare = new ModuleCompare();
    private static final IFormatIOCompare noteCompare = new NoteCompare();
    private static final IFormatIOCompare recordCompare = new RecordCompare();
    private static final IFormatIOCompare rowCompare = new RowCompare();
    private static final IFormatIOCompare reportCompare = new ReportCompare();
    private static final IFormatIOCompare tableCompare = new TableCompare();
    

    protected static final IFormatIOCompare getCompare(Object objIO){
	

	if (objIO instanceof Item)
	{
	    return itemCompare;
	}
	else if (objIO instanceof Entity)
	{
	    return entityCompare;
	}
	else if (objIO instanceof Module)
	{
	    return moduleCompare;
	}
	else if (objIO instanceof Note)
	{
	    return noteCompare;
	}
	else if (objIO instanceof Record)
	{
	    return recordCompare;
	}
	else if (objIO instanceof Row)
	{
	    return rowCompare;
	}
	else if (objIO instanceof Report)
	{
	    return reportCompare;
	}
	else if (objIO instanceof Table)
	{
	    return tableCompare;
	}
	else
	{
	    System.out.println("Not exist compare for " + objIO);
	    return null;
	}
	

    }
    
    
    public static final boolean equalsString(String str1, String str2)
    {
	
	boolean result = false;
	
	result = (str1 == null) && (str1 == null);
	
	if (!result)
	{
	    result = (str1 != null) && (str2 != null) && (str1.equals(str2));
	}
	
	return result;
    }
    
    public static final boolean equalsSign(ItemSignType sign1, ItemSignType sign2)
    {
	boolean result = false;
	
	result = ((sign1 == null) && (sign2 == null) ||
		((sign1!=null) && (sign2 != null) && (sign1 == sign2)));
	
	return result;
    }
    
    
    public static final boolean equalsUnit(Unittype unit1, Unittype unit2)
    {
	boolean result = false;
	
	result = ( ((unit1 == null) && (unit2 == null)) ||
		((unit1!=null) && (unit2 != null) && (unit1 == unit2)));
	
	return result;
    }
    
    
    public static final boolean equalsArray(Object[] array1, Object[] array2)
    {

	boolean result = false;

	result = (array1 == null) && (array2 == null);

	if (!result && (array1 != null) && (array2 != null)) {

	    int arrayLen = array1.length;
	    result = true;
	    for (int i=0; i<arrayLen; i++)
	    {
		Object currentItem =  getItem(array2, array1[i]);
		if (currentItem == null){
		    System.out.println("Not found " + PGCUtils.getTraceObject(array1[i]));
		    result = false;
		}
		else
		{
		    IFormatIOCompare compare = getCompare(currentItem);
		    boolean tempResult = compare.equals(currentItem, array1[i]);
		    if (!tempResult)
		    {
			System.out.println(PGCUtils.getTraceObject(currentItem) +  " is different to " + PGCUtils.getTraceObject(array1[i]));
		    }
		    result = result && tempResult;

		}
	    }

	}
	return result;
    }
    
    
    
    public static final Object getItem(Object[] array1, Object item)
    {
	Object result = null;
	 Object currentElement = null;
	String itemID = getId(item);
	
	 int arrayLen = array1.length;
	
	for (int i=0; i<arrayLen; i++)
	{
	    currentElement = array1[i];
	    if (getId(currentElement).equals(itemID))
	    {
		if (item instanceof Module)
		{
		    if (PGCUtils.getPropertyValueByName("getReportingDateEnd", item).equals(PGCUtils.getPropertyValueByName("getReportingDateEnd", array1[i])) &&
			PGCUtils.getPropertyValueByName("getReportingDateStart", item).equals(PGCUtils.getPropertyValueByName("getReportingDateStart", array1[i])))
			{
			    result = currentElement;
			    break;			
			}
		}
		else
		{
		    result = currentElement;
		    break;
		}
	    }
	}

	return result;
    }
    
    
    public static final boolean equalsDate(Date date1, Date date2)
    {
	boolean result = false;
	
	result = (date1 == null) && (date2 == null);
	
	if (!result)
	{
	    result = ((date1 != null) && (date2 != null ) && date1.equals(date2));
	}
	
	return result;
    }
    
    
    public static boolean equalsDate(java.util.Date date1, java.util.Date date2)
    {
	boolean result = false;
	
	result = (date1 == null) && (date2 == null);
	
	if (!result)
	{
	    result = ((date1 != null) && (date2 != null ) && date1.equals(date2));
	}
	
	return result;
    }
    
    
    public static boolean equalsEntity(Entity entity1, Entity entity2)
    {
	return entityCompare.equals(entity1, entity2);
    }
    
    
    
    public static String getId(Object objTarget)
    {
	return (String)PGCUtils.getPropertyValueByName("getId", objTarget);
	
    }
    
    private static void getPathTable(Record[] array, Hashtable<String, Item> pathTable, String currentPath)
    {
	int numRecords = array.length;
	for (int i=0; i<numRecords; i++)
	{
	    Record currentRecord = array[i];
	    String newPath = currentPath + currentRecord.getId() + ":";
	    int numItems = currentRecord.getItemCount();
	    for (int j=0; j< numItems; j++)
	    {
		Item currentItem = currentRecord.getItem(j);
		pathTable.put(newPath + currentItem.getId() , currentItem);
	    }
	    if (currentRecord.getRecordCount() > 0)
	    {
		getPathTable(currentRecord.getRecord(), pathTable, newPath);
	    }
	}
    }
    
    public static boolean equalsArrayRecord(Record[] array1, Record[] array2)
    {
	
	boolean result = false;

	result = (array1 == null) && (array2 == null);

	if (!result && (array1 !=null) && (array2 != null))
	{
	    Hashtable<String,Item> tablePath1 = new Hashtable<String,Item>();
	    getPathTable(array1, tablePath1, "");

	    Hashtable<String,Item> tablePath2 = new Hashtable<String,Item>();
	    getPathTable(array2, tablePath2, "");

	    result = true;
	    Enumeration<String> enumPath1 = tablePath1.keys();
	    
	    while (enumPath1.hasMoreElements())
	    {
		String currentPath = enumPath1.nextElement();
		boolean containPath = tablePath2.containsKey(currentPath); 
		if (containPath)
		{
		    Item item1 = tablePath1.get(currentPath);
		    Item item2 = tablePath2.get(currentPath);

		    boolean equals = getCompare(item1).equals(item1, item2);

		    if (!equals)
		    {
			System.out.println("item1=" + PGCUtils.getTraceObject(item1) + " is different to item2=" + PGCUtils.getTraceObject(item2) + " path=" + currentPath);
		    }
		    result = result && equals;
		}
		else{
		    result = false;
		    System.out.println("Module2 not contain path " + currentPath);
		}
	    }
	}
	return result;
    }
    
    
}
