import { AonCard } from "../../components/aon-card.js";
import { AonToolbar } from "../../components/aon-toolbar.js";
import { TAG } from "../../environments/environments.js";
import { newComponent, setAttributes, setEvents } from "../../services/utils.js";
import { createDate, createForm } from "../notification/createComponent.js";

export const createBajaDialogContent = () =>{
    const div = document.createElement(TAG.DIV);
    //---FORM------
    createDate({
        attributes:{
            name:"fechaBaja",
            id:"fechaBaja",
            title:"Fecha de baja",
            value: new Date()
        },
    }, div);
    return div;
}

export const createToolbarComunica = (attributes, parent) => {
    let toolbar = new AonToolbar();
    setAttributes(toolbar, attributes);
    parent.appendChild(toolbar);
}


export const createFormComunica = (id, parent) => {
    const form = createForm(id+"Form");
    parent.appendChild(form.element);

    const div = createDiv({id: id+"Div"});
    div.appendTo(form);

    let divC;
    divC = createDiv({classes:["aonCol-sm-12"]})
    divC.appendTo(div.element);
    createCardComunica({id: id+"EmpresaCard", title:"Datos de la empresa"}, divC.element);

    divC = createDiv({classes:["aonCol-sm-12", "aonCol-md-6"]});
    divC.appendTo(div.element);
    createCardComunica({id: id+"TrabajadorCard", title:"Datos del trabajador"}, divC.element);

    divC = createDiv({classes:["aonCol-sm-12", "aonCol-md-6"]});
    divC.appendTo(div.element);
    createCardComunica({id: id+"ContratoCard", title:"Datos del contrato"}, divC.element);

    return form.element;
}

const createDiv = (properties) =>  newComponent({
    type: TAG.DIV,
    ...properties
});

const createCardComunica = (attributes, parent) => {
    const aonCard = new AonCard();
    setAttributes(aonCard, {
        ...attributes,
        flex:"true"
    });
    parent.appendChild(aonCard);
}
