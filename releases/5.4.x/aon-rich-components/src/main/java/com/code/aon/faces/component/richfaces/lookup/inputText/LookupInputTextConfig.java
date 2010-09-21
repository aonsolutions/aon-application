package com.code.aon.faces.component.richfaces.lookup.inputText;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.faces.component.AttributeInfo;
import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.faces.component.richfaces.lookup.ILookupTags;
import com.code.aon.faces.component.util.AonComponentConfig;
import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class LookupInputTextConfig extends AonComponentConfig {

    private static final String VALUE_CHANGE_LISTENER = "lookupChanged";
		
	public LookupInputTextConfig(ComponentConfig config) {
		super(config);
    	TagAttribute lookupTag = config.getTag().getAttributes().get(ILookupTags.LOOKUP);
    	if ( lookupTag != null ) {
        	String value = FaceletUtil.appendExpression( lookupTag.getValue(), VALUE_CHANGE_LISTENER );
        	List<AttributeInfo> extraAttributes = new LinkedList<AttributeInfo>();
        	extraAttributes.add( new AttributeInfo(IRichFacesTags.ACTION_LISTENER, value) );
        	setTag( duplicate(config.getTag(), extraAttributes) );    		
    	}
	}

}
