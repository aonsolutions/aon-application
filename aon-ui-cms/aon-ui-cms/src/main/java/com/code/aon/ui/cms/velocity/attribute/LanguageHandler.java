package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.Language;

public class LanguageHandler {

    private String id = "";

    private String description = "";

    public LanguageHandler(Language language) {
        this.id = language.getLanguage().getLocale().getLanguage();
        this.description = language.getDescription();
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }
}
