package com.code.aon.ui.calculator.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.calculator.enumeration.ContractType;

public class CalculatorCollectionsController {

    private List<SelectItem> wageNumbers;
    private List<SelectItem> offspringNumbers;
    private List<SelectItem> contractTypes;

    public List<SelectItem> getWageNumbers() {
        if(wageNumbers == null) {
            wageNumbers = new LinkedList<SelectItem>();
            wageNumbers.add(new SelectItem(0, "0"));
            wageNumbers.add(new SelectItem(1, "1"));
            wageNumbers.add(new SelectItem(2, "2"));
            wageNumbers.add(new SelectItem(3, "3"));
            wageNumbers.add(new SelectItem(4, "4"));
        }
        return wageNumbers;
    }

    public List<SelectItem> getOffspringNumbers() {
        if(offspringNumbers == null) {
            offspringNumbers = new LinkedList<SelectItem>();
            offspringNumbers.add(new SelectItem(0, "0"));
            offspringNumbers.add(new SelectItem(1, "1"));
            offspringNumbers.add(new SelectItem(2, "2"));
            offspringNumbers.add(new SelectItem(3, "3"));
            offspringNumbers.add(new SelectItem(4, "4"));
            offspringNumbers.add(new SelectItem(5, "5"));
            offspringNumbers.add(new SelectItem(6, "+"));
        }
        return offspringNumbers;
    }

    public List<SelectItem> getContractTypes() {
        if(contractTypes == null) {
            Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            contractTypes = new LinkedList<SelectItem>();
            for (ContractType contractType : ContractType.values()) {
                String name = contractType.getName(locale);
                SelectItem item = new SelectItem(contractType, name);
                contractTypes.add(item);
            }
        }
        return contractTypes;
    }

}
