package com.code.aon.file.format.model;

import java.util.EventObject;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.file.format.core.Component;
import com.code.aon.file.format.core.Filler;
import com.code.aon.file.format.core.Register;

/**
 * CSB34 generic lines filler
 * 
 * @author Consulting & Development. Iñigo GAyarre - 01/02/2007
 * @since 1.0
 *
 */
public class GenericLinesFiller extends AbstractLinesFiller {

	public void fillLine(Register register, Map<String,Object> properties){
		Iterator<Filler> iter = register.iterator();
		while (iter.hasNext()) {
			Component component = (Component)iter.next();
			String value = component.getValue();
			if (value.equalsIgnoreCase("bean")) {
				try {
					line += (String)component.format(PropertyUtils.getProperty(properties.get(component.getBean()), component.getProperty()));
				} catch (Exception e) {
					Object valueOfProperty = "<ERROR>";
					try {
						valueOfProperty = PropertyUtils.getProperty(properties.get(component.getBean()), component.getProperty());
					} catch (Exception ex) {
					}
					String valueDetail = component.getBean()+" "+component.getProperty()+" "+valueOfProperty;
					String detail = "OBJECT:"+component.getName()+" VALUE:"+valueDetail;
					throw new Fd0Exception(detail,e.toString());
				}
			}else{
				try {
					line += (String)component.format(value);
				} catch (Exception e) {
					String detail = "OBJECT:"+component.getName()+" VALUE:"+value;
					throw new Fd0Exception(detail,e.toString());
				}
			}
		}
		this.fireLineFilled(new EventObject(this));
	}


}
