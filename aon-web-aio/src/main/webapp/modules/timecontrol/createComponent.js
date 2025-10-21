import { CSS } from "../../environments/environments.js";
import { createDiv } from "../../services/utilsComponents.js";
import { CreateComponent } from "../../components/CreateComponent.js";

//import '../../css/aon-css-utils.css';
//import '../../css/aon-grid.css';

export const createFormEvent = (id, parent) => {
    const form = CreateComponent.createForm(id+"Form");
    parent.appendChild(form);
    
    const className = parent.isMobile() ? CSS.AON_MOBILE_SUB_CONTENT : CSS.AON_SUB_CONTENT;
    const div = createDiv({id: id+"Div",classes:[className]});
    form.appendChild(div.element)

    let divC;
    divC = createDiv();
    divC.appendTo(div.element);
    CreateComponent.createAonCard({id: id+"CardEvent", title:"Datos del evento"}, divC.element);

    divC = createDiv();
    divC.appendTo(div.element);
    CreateComponent.createAonCard({id: id+"CardCoordinate", title:"Mapa", visible: false}, divC.element);
  
    return form;
}

export const createCardEvent = (parent) => {

    CreateComponent.createAonInput({
        attributes:{
            name:"name",
            id:"name",
            title:"Nombre"
        }
    }, parent);

    CreateComponent.createAonSelect({
        attributes:{
            name:"status",
            id:"status",
            title:"Estado"
        }
    }, parent);

    CreateComponent.createAonSelect({
        attributes:{
            name:"location",
            id:"location",
            title:"Ubicación"
        }
    }, parent);

    CreateComponent.createAonDate({
        attributes:{
            name:"date", 
            id:"date", 
            title:"Fecha"
        }
    }, parent);

    CreateComponent.createAonInput({
        attributes:{
            name:"time",
            id:"time",
            title:"Hora",
            type:"time"
        }
    }, parent);

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