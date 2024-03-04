package com.esferalia.aon.watson.j2html.tags;

public abstract class DomContent implements Renderable {
    @Override
    public String toString() {
        return render();
    }
}
