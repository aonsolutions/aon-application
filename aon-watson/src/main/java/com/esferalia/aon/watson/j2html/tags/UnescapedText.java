package com.esferalia.aon.watson.j2html.tags;

import java.io.IOException;

import com.esferalia.aon.watson.j2html.Config;
import com.esferalia.aon.watson.j2html.rendering.FlatHtml;
import com.esferalia.aon.watson.j2html.rendering.HtmlBuilder;

public class UnescapedText extends DomContent {

    private final String text;

    public UnescapedText(String text) {
        this.text = text;
    }

    @Override
    public <T extends Appendable> T render(HtmlBuilder<T> builder, Object model) throws IOException {
        builder.appendUnescapedText(String.valueOf(text));
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof UnescapedText)) {
            return false;
        }
        return ((UnescapedText) obj).render().equals(this.render());
    }
}
