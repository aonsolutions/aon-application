package com.esferalia.aon.watson.j2html.utils;

@FunctionalInterface
public interface TextEscaper {
    String escape(String text);
}
