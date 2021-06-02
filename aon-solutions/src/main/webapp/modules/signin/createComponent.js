import { CSS } from "../../environments/environments.js";
import { createDate, createDiv, createForm, createInput, createSelect, createCard } from "../notification/createComponent.js";

export const createFormEvent = (id, parent) => {
    const form = createForm(id+"Form");
    parent.appendChild(form.element);
    const className = parent.isMobile() ? CSS.AON_MOBILE_SUB_CONTENT : CSS.AON_SUB_CONTENT;
    const div = createDiv({id: id+"Div",classes:[className]});
    div.appendTo(form);

    let divC;
    divC = createDiv({classes:[CSS.AON_COL_SM_12]})
    divC.appendTo(div.element);
    createCard({id: id+"CardEvent", title:"Datos del evento"}, divC.element);

    divC = createDiv({classes:[CSS.AON_COL_SM_12]});
    divC.appendTo(div.element);
    createCard({id: id+"CardCoordinate", title:"Coordenadas", visible:"false"}, divC.element);
  
    return form.element;
}

export const createCardEvent = (parent) => {
    
    let divC;
    divC = createDiv({classes:["aonCol-sm-6", "aonCol-md-3"]})
    divC.appendTo(parent);
    createInput({
        attributes:{
            name:"name",
            id:"name",
            description:"Nombre",
            type:"text"
        }
    }, divC.element);

    divC = createDiv({classes:["aonCol-sm-6", "aonCol-md-2"]})
    divC.appendTo(parent);
    createSelect({
        attributes:{
            name:"status",
            id:"status",
            title:"Estado"
        }
    }, divC.element);
 

    divC = createDiv({classes:["aonCol-sm-6", "aonCol-md-3"]})
    divC.appendTo(parent);
    createSelect({
        attributes:{
            name:"location",
            id:"location",
            title:"Ubicación"
        }
    }, divC.element);


    divC = createDiv({classes:["aonCol-sm-6", "aonCol-md-2"]})
    divC.appendTo(parent);
    createDate({
        attributes:{
            name:"date", 
            id:"date", 
            title:"Fecha"
        }
    }, divC.element);


    divC = createDiv({classes:["aonCol-sm-6", "aonCol-md-2"]})
    divC.appendTo(parent);
    createInput({
        attributes:{
            name:"time",
            id:"time",
            description:"Hora",
            type:"time"
        }
    }, divC.element);


    createInput({
        attributes:{
            name:"id",
            id:"id",
            type:"text",
            visible:"false"
        }
    }, parent);

    createInput({
        attributes:{
            name:"coordinates",
            id:"coordinates",
            type:"text",
            visible:"false"
        }
    }, parent);
    
}