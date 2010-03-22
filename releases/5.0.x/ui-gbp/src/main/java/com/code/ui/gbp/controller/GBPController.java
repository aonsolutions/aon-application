package com.code.ui.gbp.controller;

import com.code.aon.bridge.plugin.Utils;

public class GBPController {

    public String getAonUser() {
    	return Utils.getAuthPrincipal().getShortName();
    }

}
