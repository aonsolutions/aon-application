package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IAutofocus;
import com.esferalia.aon.watson.j2html.tags.attributes.ICols;
import com.esferalia.aon.watson.j2html.tags.attributes.IDirname;
import com.esferalia.aon.watson.j2html.tags.attributes.IDisabled;
import com.esferalia.aon.watson.j2html.tags.attributes.IForm;
import com.esferalia.aon.watson.j2html.tags.attributes.IMaxlength;
import com.esferalia.aon.watson.j2html.tags.attributes.IName;
import com.esferalia.aon.watson.j2html.tags.attributes.IPlaceholder;
import com.esferalia.aon.watson.j2html.tags.attributes.IReadonly;
import com.esferalia.aon.watson.j2html.tags.attributes.IRequired;
import com.esferalia.aon.watson.j2html.tags.attributes.IRows;
import com.esferalia.aon.watson.j2html.tags.attributes.IWrap;

public final class TextareaTag extends ContainerTag<TextareaTag>
    implements IAutofocus<TextareaTag>, ICols<TextareaTag>, IDirname<TextareaTag>, IDisabled<TextareaTag>, IForm<TextareaTag>, IMaxlength<TextareaTag>, IName<TextareaTag>, IPlaceholder<TextareaTag>, IReadonly<TextareaTag>, IRequired<TextareaTag>, IRows<TextareaTag>, IWrap<TextareaTag> {
    public TextareaTag() {
        super("textarea");
    }
}
