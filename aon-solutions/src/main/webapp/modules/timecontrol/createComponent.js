import { CSS } from "../../environments/environments.js";
import { createDiv } from "../../services/utilsComponents.js";
import { CreateComponent } from "../../components/CreateComponent.js";

import '../../css/aon-css-utils.css';
import '../../css/aon-grid.css';

export const createFormEvent = (id, parent) => {
    const form = CreateComponent.createForm(id+"Form");
    parent.appendChild(form.element);
    const className = parent.isMobile() ? CSS.AON_MOBILE_SUB_CONTENT : CSS.AON_SUB_CONTENT;
    const div = createDiv({id: id+"Div",classes:[className]});
    div.appendTo(form);

    let divC;
    divC = createDiv({classes:[CSS.AON_COL_SM_12]})
    divC.appendTo(div.element);
    CreateComponent.createAonCard({id: id+"CardEvent", title:"Datos del evento"}, divC.element);

    divC = createDiv({classes:[CSS.AON_COL_SM_12]});
    divC.appendTo(div.element);
    CreateComponent.createAonCard({id: id+"CardCoordinate", title:"Mapa", visible: false}, divC.element);
  
    return form.element;
}

export const createCardEvent = (parent) => {
    
    let divC;
    divC = createDiv({classes:[CSS.AON_COL_SM_6, CSS.AON_COL_MD_3]})
    divC.appendTo(parent);
    CreateComponent.createAonInput({
        attributes:{
            name:"name",
            id:"name",
            description:"Nombre",
            type:"text"
        }
    }, divC.element);

    divC = createDiv({classes:[CSS.AON_COL_SM_6, CSS.AON_COL_MD_2]})
    divC.appendTo(parent);
    CreateComponent.createAonSelect({
        attributes:{
            name:"status",
            id:"status",
            title:"Estado"
        }
    }, divC.element);
 

    divC = createDiv({classes:[CSS.AON_COL_SM_6, CSS.AON_COL_MD_3]})
    divC.appendTo(parent);
    CreateComponent.createAonSelect({
        attributes:{
            name:"location",
            id:"location",
            title:"Ubicación"
        }
    }, divC.element);


    divC = createDiv({classes:[CSS.AON_COL_SM_6, CSS.AON_COL_MD_2]})
    divC.appendTo(parent);
    CreateComponent.createAonDate({
        attributes:{
            name:"date", 
            id:"date", 
            title:"Fecha"
        }
    }, divC.element);


    divC = createDiv({classes:[CSS.AON_COL_SM_6, CSS.AON_COL_MD_2]})
    divC.appendTo(parent);
    CreateComponent.createAonInput({
        attributes:{
            name:"time",
            id:"time",
            description:"Hora",
            type:"time"
        }
    }, divC.element);


    CreateComponent.createAonInput({
        attributes:{
            name:"id",
            id:"id",
            type:"text",
            visible:"false"
        }
    }, parent);

    CreateComponent.createAonInput({
        attributes:{
            name:"coordinates",
            id:"coordinates",
            type:"text",
            visible:"false"
        }
    }, parent);
}