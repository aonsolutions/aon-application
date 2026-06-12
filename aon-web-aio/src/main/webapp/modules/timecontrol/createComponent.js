import { CSS } from "../../environments/environments.js";
import { createDiv } from "../../services/utilsComponents.js";
import { CreateComponent, createDate, createInput, createSelect } from "../../components/CreateComponent.js";

import '../../css/aon-css-utils.css';
import '../../css/aon-grid.css';

export const createFormEvent = (id, parent) => {
    const form = CreateComponent.createForm(id+"Form");
    parent.appendChild(form);
    
    const className = parent.isMobile() ? "" : CSS.AON_SUB_CONTENT;
    const div = createDiv({id: id+"Div",classes:[className]});
    form.appendChild(div.element)

    let divC;
    divC = createDiv({classes:[CSS.AON_COL_SM_12]})
    divC.appendTo(div.element);
    CreateComponent.createAonCard({id: id+"CardEvent", title:"Datos del evento"}, divC.element);

    divC = createDiv({classes:[CSS.AON_COL_SM_12]});
    divC.appendTo(div.element);
    CreateComponent.createAonCard({id: id+"CardCoordinate", title:"Mapa", visible: false}, divC.element);
  
    return form;
}

export const createCardEvent = (parent) => {
    
    let divC;
    divC = createDiv({classes:[CSS.AON_COL_SM_6, CSS.AON_COL_MD_3]})
    divC.appendTo(parent);
    createInput("name", "Nombre", divC.element);
	
    divC = createDiv({classes:[CSS.AON_COL_SM_6, CSS.AON_COL_MD_2]})
    divC.appendTo(parent);
    createSelect("status", "Estado", divC.element);

    divC = createDiv({classes:[CSS.AON_COL_SM_6, CSS.AON_COL_MD_3]})
    divC.appendTo(parent);
    createSelect("location", "Ubicación", divC.element);

    divC = createDiv({classes:[CSS.AON_COL_SM_6, CSS.AON_COL_MD_2]})
    divC.appendTo(parent);
    createDate("date", "Fecha", divC.element);

    divC = createDiv({classes:[CSS.AON_COL_SM_6, CSS.AON_COL_MD_2]})
    divC.appendTo(parent);
    createInput("time", "Hora", divC.element);

	createInput("id", "Id", parent);

	createInput("coordinates", "Coordenadas", parent);
}