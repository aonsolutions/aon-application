package com.esferalia.aon.watson.j2html.tags;

import java.io.IOException;

import com.esferalia.aon.watson.j2html.Config;
import com.esferalia.aon.watson.j2html.attributes.Attribute;
import com.esferalia.aon.watson.j2html.rendering.FlatHtml;
import com.esferalia.aon.watson.j2html.rendering.HtmlBuilder;
import com.esferalia.aon.watson.j2html.rendering.TagBuilder;

public class EmptyTag<T extends EmptyTag<T>> extends Tag<T> {

    public EmptyTag(String tagName) {
        super(tagName);
        if (tagName == null) {
            throw new IllegalArgumentException("Illegal tag name: null");
        }
        if ("".equals(tagName)) {
            throw new IllegalArgumentException("Illegal tag name: \"\"");
        }
    }

    @Override
    public <A extends Appendable> A render(HtmlBuilder<A> builder, Object model) throws IOException {
        TagBuilder attrs = builder.appendEmptyTag(getTagName());
        for (Attribute attr : getAttributes()) {
            attr.render(attrs, model);
        }
        attrs.completeTag();
        return builder.output();
    }

    @Override
    @Deprecated
    public void renderModel(Appendable writer, Object model) throws IOException {
        HtmlBuilder<?> builder = (writer instanceof HtmlBuilder)
            ? (HtmlBuilder<?>) writer
            : FlatHtml.into(writer, Config.global());

        render(builder, model);
    }
}
