import { AonCard } from "../../components/aon-card.js";
import { AonNumber } from "../../components/aon-number.js";
import { AonSuggestion } from "../../components/aon-suggestion.js";
import { AonToolbar } from "../../components/aon-toolbar.js";
import { TAG } from "../../environments/environments.js";
import { newComponent, setAttributes } from "../../services/utils.js";
import { createDate, createForm, createInput, createSelect } from "../notification/createComponent.js";

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
    let toolbar = setAttributes( new AonToolbar(), attributes);
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

export const createCardEnterprise = (parent) => {
    let divC;
    divC = createDiv({classes:["aonCol-sm-12", "aonCol-md-4"]})
    divC.appendTo(parent);
    createSelect({
        attributes:{
            name:"centro_trabajo",
            id:"centro_trabajo",
            title:"Centro de trabajo"
        }
    }, divC.element);
 

    divC = createDiv({classes:["aonCol-sm-12", "aonCol-md-4"]})
    divC.appendTo(parent);
    createSelect({
        attributes:{
            name:"ctaCti",
            id:"ctaCti",
            title:"Cuenta de cotización"
        }
    }, divC.element);

    divC = createDiv({classes:["aonCol-sm-12", "aonCol-md-4"]})
    divC.appendTo(parent);
    let aonConvenio = new AonSuggestion();
    setAttributes(aonConvenio,{
        id:"convenio",
        title:"Convenio (opcional)",
        name:"convenio"
    });
    divC.appendChild(aonConvenio);

    createInputC({
        attributes:{
            name:"regimen",
            id:"regimen",
            description:"regimen",
            visible:"false",
        }
    }, parent);
}

export const createCardContract = (parent, id, isButton) => {
    let divC;
    divC = createDiv({classes:["aonCol-sm-12", "aonCol-md-6"]})
    divC.appendTo(parent);
    createSelect({
        attributes:{
            name:"type_cto",
            id:"type_cto",
            title:"Tipo de contrato",
            autocomplete: "off",
            readonly: "false"
        }
    }, divC.element);
 
    divC = createDiv({classes:["aonCol-sm-12", "aonCol-md-6"]})
    divC.appendTo(parent);
    createDate({
        attributes:{
            name:"fecha", 
            id:"fecha", 
            title:"Fecha"
        }
    }, divC.element)

    divC = createDiv({classes:["aonCol-sm-12", "aonCol-md-6"]})
    divC.appendTo(parent);
    createSelect({
        attributes:{
            name:"grup_ctz",
            id:"grup_ctz",
            title:"Grupo de cotización"
        }
    }, divC.element);

    divC = createDiv({classes:["aonCol-sm-12", "aonCol-md-6"]})
    divC.appendTo(parent);
    createSelect({
        attributes:{
            name:"ocupacion",
            id:"ocupacion",
            title:"Ocupación"
        }
    }, divC.element);

    let divH = createDiv({
        attributes:{
            id:"div_parcial",
            hidden:true
        }
    })
    divH.appendTo(parent);

    divC = createDiv({classes:["aonCol-xs-6", "aonCol-sm-3"]})
    divC.appendTo(divH.element);
    createSelect({
        attributes:{
            name:"tipo_jornada",
            id:"tipo_jornada",
            title:"Tipo de jornada"
        }
    }, divC.element);

    divC = createDiv({classes:["aonCol-xs-6", "aonCol-sm-3"]})
    divC.appendTo(divH.element);
    let numberC = setAttributes(new AonNumber(),{
        id:"horas_convenio", 
        name:"horas_convenio", 
        description:"Horas convenio",
        format:"true",
        decimals:"2"
    })
    divC.appendChild(numberC);


    divC = createDiv({classes:["aonCol-xs-6", "aonCol-sm-3"]})
    divC.appendTo(divH.element);
    numberC = setAttributes(new AonNumber(),{
        id:"horas", 
        description:"Horas",
        format:"true",
        decimals:"2"
    })
    divC.appendChild(numberC);

    divC = createDiv({classes:["aonCol-xs-6", "aonCol-sm-3"]})
    divC.appendTo(divH.element);
    numberC = setAttributes(new AonNumber(),{
        id:"coefparcial", 
        name:"coefparcial",
        description:"Coef. Parcial"
    })
    divC.appendChild(numberC);

    createInputC({
        attributes:{
            name:"situation",
            id:"situation",
            description:"situation",
            value:"AL",
            visible:"false"
        }
    }, parent);

    if(isButton){
        let divB = createDiv({  styles:{textAlign: 'center'} });
        divB.appendTo(parent);
        let button = document.createElement(TAG.BUTTON);
        button.className="aonButton";
        button.style.marginTop = "10px";
        button.type = "button";
        button.id = `${id}Submit`;
        button.textContent = "Aceptar";
        divB.element.appendChild(button);
    }

    addSpanDecimal();
}

const createDiv = (properties) =>  newComponent({
    type: TAG.DIV,
    ...properties
});

const createCardComunica = (attributes, parent) => {
    const aonCard = setAttributes(new AonCard(), {
        ...attributes,
        flex:"true"
    });
    parent.appendChild(aonCard);
}

export const createInputC = ({attributes, events}, parent) => createInput({attributes, events}, parent);

const addSpanDecimal = () =>  {
    let coefInput = document.getElementById('coefparcialInput')
    if(coefInput){
        let span = document.createElement(TAG.SPAN);
        span.innerHTML = '0,';
        span.style.position = "absolute";
        span.style.top = "50%";
        span.style.zIndex = "9";
        coefInput.parentNode.insertBefore(span, coefInput);
    }
}