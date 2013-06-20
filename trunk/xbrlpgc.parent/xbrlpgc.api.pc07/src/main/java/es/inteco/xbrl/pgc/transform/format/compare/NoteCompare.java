package es.inteco.xbrl.pgc.transform.format.compare;

import es.inteco.xbrl.pgc.transform.format.Note;


public class NoteCompare implements IFormatIOCompare
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
	Note note1 = (Note)obj1;
	Note note2 = (Note)obj2;
	
	boolean resultComparation = false;
	
	resultComparation = note1.getId() == note2.getId();
	
	resultComparation = resultComparation && (CompareHelper.equalsString(note1.getText(), note2.getText()));
	
	return resultComparation;
	
    }
    
    
    
    
    
}
