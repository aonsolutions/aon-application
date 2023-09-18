package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.JavaScriptObject;



public class EmployeeDataResult extends JavaScriptObject {


	
    protected EmployeeDataResult() {
    }

    public final native String getDni() /*-{
        return this.dni;
    }-*/;
    
    public final native String getNationality() /*-{
        return this.nacionalidad;
    }-*/;

    public final native String getName() /*-{
        return this.nombre;
    }-*/;

    public final native String getFirstSurname() /*-{
        return this.apellido1;
    }-*/;

    public final native String getSecondSurname() /*-{
        return this.apellido2;
    }-*/;
    
    
    

}
