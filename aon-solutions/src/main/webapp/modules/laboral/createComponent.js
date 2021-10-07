import { AonNumber } from "../../components/aon-number.js";
import { AonSuggestion } from "../../components/aon-suggestion.js";
import { AonSwitch } from "../../components/aon-switch.js";
import { CONSTANT, CSS, EVENT, MSG, TAG } from "../../environments/environments.js";
import { formatDateOrigin } from "../../services/utils.js";
import { setAttributes } from "../../services/utilsComponents.js";
import { createCard, createDate, createDiv, createForm, createIconButton, createInput, createSelect } from "../notification/createComponent.js";

export const createBajaDialogContent = () =>{
    const div = document.createElement(TAG.DIV);
    div.style.margin = "0 9px";
    //---FORM------
    createDate({
        attributes:{
            name:"fechaBaja",
            id:"fechaBaja",
            title:"Fecha de baja"
        }
    }, div);

    const select = createSelect({
        attributes:{
            name:"codBaja",
            id:"codBaja",
            title:"Causa de baja"
        }
    }, div);
    select.style.textAlign = "left";

    const btnSubmit = document.createElement(TAG.BUTTON);
    btnSubmit.id = "btnSubmitBaja";
    btnSubmit.className = "aonButton";
    btnSubmit.textContent = "Aceptar";
    btnSubmit.style.padding ="0.5rem 1rem";
    btnSubmit.style.marginBottom ="5px";
    // btnSubmit.disabled = true;
    div.appendChild(btnSubmit);

    return div;
}

export const createFormComunica = (id, parent) => {
    const form = createForm(id+"Form");
    parent.appendChild(form.element);

    const className = parent.isMobile() ? CSS.AON_MOBILE_SUB_CONTENT : CSS.AON_SUB_CONTENT;
    const div = createDiv({id: id+"Div", classes:[className]});
    div.appendTo(form);

    let divC;
    divC = createDiv({classes:[CSS.AON_COL_SM_12]})
    divC.appendTo(div.element);
    createCard({id: id+"EmpresaCard", title:"Datos de la empresa"}, divC.element);

    divC = createDiv({classes:[CSS.AON_COL_SM_12, CSS.AON_COL_MD_6]});
    divC.appendTo(div.element);
    createCard({id: id+"TrabajadorCard", title:"Datos del trabajador"}, divC.element);

    divC = createDiv({classes:[CSS.AON_COL_SM_12, CSS.AON_COL_MD_6]});
    divC.appendTo(div.element);
    createCard({id: id+"ContratoCard", title:"Datos del contrato"}, divC.element);

    return form.element;
}

export const createEnterpriseData = (parent) => {
    let divC;
    divC = createDiv({classes:[CSS.AON_COL_SM_12, CSS.AON_COL_MD_4]})
    divC.appendTo(parent);
    createSelect({
        attributes:{
            name:"centro_trabajo",
            id:"centro_trabajo",
            title:"Centro de trabajo"
        }
    }, divC.element);
 

    divC = createDiv({classes:[CSS.AON_COL_SM_12, CSS.AON_COL_MD_4]})
    divC.appendTo(parent);
    createSelect({
        attributes:{
            name:"ctaCti",
            id:"ctaCti",
            title:"Cuenta de cotización"
        }
    }, divC.element);

    divC = createDiv({classes:[CSS.AON_COL_SM_12, CSS.AON_COL_MD_4]})
    divC.appendTo(parent);
    let aonConvenio = setAttributes(new AonSuggestion(),{
        id:"convenio",
        title:"Convenio (opcional)",
        name:"convenio"
    });
    aonConvenio.addEventListener(EVENT.KEYUP, ({target}) =>  target.value = target.value.replace(/\D/g,''));
    divC.appendChild(aonConvenio);

    createInput({
        attributes:{
            name:"regimen",
            id:"regimen",
            description:"Regimen",
            visible:CONSTANT.FALSE,
        }
    }, parent);
}

export const createContractData = (parent) => {
    let divC;
    divC = createDiv({classes:[CSS.AON_COL_SM_12, CSS.AON_COL_MD_6]})
    divC.appendTo(parent);
    createSelect({
        attributes:{
            name:"type_cto",
            id:"type_cto",
            title:"Tipo de contrato",
            autocomplete: CONSTANT.OFF
        }
    }, divC.element);
 
    divC = createDiv({classes:[CSS.AON_COL_SM_12, CSS.AON_COL_MD_6]})
    divC.appendTo(parent);
    const dateContract = createDate({
        attributes:{
            name:"fecha", 
            id:"fecha", 
            title:"Fecha"
        }
    }, divC.element)

    divC = createDiv({classes:[CSS.AON_COL_SM_12, CSS.AON_COL_MD_6]})
    divC.appendTo(parent);
    createSelect({
        attributes:{
            name:"grup_ctz",
            id:"grup_ctz",
            title:"Grupo de cotización"
        }
    }, divC.element);

    divC = createDiv({classes:[CSS.AON_COL_SM_12, CSS.AON_COL_MD_6]})
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

    partTime(divH.element)

    createInput({
        attributes:{
            name:"situation",
            id:"situation",
            description:MSG.SITUATION,
            value:"AL",
            visible:CONSTANT.FALSE
        }
    }, parent);

    dateContract.value = formatDateOrigin(new Date());

}

export const createEmployeeData = (parent, id) => {
    let divT;
    divT = createDiv({
        classes:[CSS.AON_COL_SM_12, CSS.AON_COL_MD_4],
        styles:{
            paddingTop: "18px",
            paddingBottom: "10px"
        }
    })
    divT.appendTo(parent);
    let aonSwitch = setAttributes(new AonSwitch(),{
        id:"switchDni", 
        title:"Por DNI"
    })
    divT.appendChild(aonSwitch);

    let divReiniciar = createDiv({
        attributes:{
            id:id+"Reiniciar",
            hidden: CONSTANT.TRUE,
        },
        styles:{
            marginTop:"-15px" 
        }
    });
    divReiniciar.appendTo(divT);
    let span = document.createElement(TAG.SPAN);
    span.textContent =  MSG.RESTORE;
    divReiniciar.appendChild(span);
    createIconButton({attributes:{ id: id+"IconReset", icon:"cached"}}, divReiniciar);

    divT = createDiv({ classes:[CSS.AON_COL_SM_12, CSS.AON_COL_MD_4] })
    divT.appendTo(parent);
    let divNss = createDiv({ attributes:{id: id+"NssDiv"} });
    divNss.appendTo(divT.element);
    createInput({
        attributes:{
            name:"nss",
            id: id+"Nss", 
            description:"NSS/NAF", 
            autocomplete:"on"
        }
    }, divNss.element);


    divT = createDiv({ classes:[CSS.AON_COL_SM_12, CSS.AON_COL_MD_4] })
    divT.appendTo(parent);
    let divDni = createDiv({ attributes:{id: id+"DniDiv"} });
    divDni.appendTo(divT.element);
    createInput({
        attributes:{
            name:"ipf",
            id: id+"Dni", 
            description:"DNI/NIE", 
            autocomplete:"on",
            disabled: true
        }
    }, divDni.element);

    let divSurnames = createDiv({ attributes:{id:"div_apellidos", hidden:true} });
    divSurnames.appendTo(parent);
    divT = createDiv({ classes:[CSS.AON_COL_SM_12, CSS.AON_COL_MD_6] })
    divT.appendTo(divSurnames.element);
    createInput({
        attributes:{
            name:"apellido1",
            id:"apellido1",
            description:"1er Apellido",
            type:"text",
        }
    }, divT.element);
    
    divT = createDiv({ classes:[CSS.AON_COL_SM_12, CSS.AON_COL_MD_6] })
    divT.appendTo(divSurnames.element);
    createInput({
        attributes:{
            name:"apellido2",
            id:"apellido2",
            description:"2do Apellido",
            type:"text",
        }
    }, divT.element);

    divT = createDiv({ classes:[CSS.AON_COL_SM_12, CSS.AON_COL_MD_12, CSS.AON_COL_XS_12] })
    divT.appendTo(parent);
    createInput({
        attributes:{
            name:"name",
            id:"name",
            description:MSG.NAME,
            type:"text",
            disabled: CONSTANT.TRUE
        }
    }, divT.element);

    addIconSurname();
}

const addIconSurname = () => {
    const surnameTwo = document.getElementById("apellido2");
    if(surnameTwo){
        surnameTwo.addAonIcon("aon_seg_social");
        if(surnameTwo.getIcon()){
            let iass = surnameTwo.getIcon().querySelector("aon-icon");
            if(iass)
                iass.size = "18px";
        }
    }
}


/**
 * 
 * @param {HTMLElement} divH parent 
 */
const partTime = (divH) => {
    let divC = createDiv({classes:[CSS.AON_COL_XS_6, CSS.AON_COL_SM_3]})
    divC.appendTo(divH);
    createSelect({
        attributes:{
            name:"tipo_jornada",
            id:"tipo_jornada",
            title:"Jornada"
        }
    }, divC.element);

    divC = createDiv({classes:[CSS.AON_COL_XS_6, CSS.AON_COL_SM_3]})
    divC.appendTo(divH);
    let numberC = setAttributes(new AonNumber(),{
        id:"horas_convenio", 
        name:"horas_convenio", 
        description:"Horas convenio",
        format:CONSTANT.TRUE,
        decimals:"2"
    })
    divC.appendChild(numberC);


    divC = createDiv({classes:[CSS.AON_COL_XS_6, CSS.AON_COL_SM_3]})
    divC.appendTo(divH);
    numberC = setAttributes(new AonNumber(),{
        id:"horas", 
        description:MSG.HOURS,
        format:CONSTANT.TRUE,
        decimals:"2"
    })
    divC.appendChild(numberC);

    divC = createDiv({classes:[CSS.AON_COL_XS_6, CSS.AON_COL_SM_3]})
    divC.appendTo(divH);
    numberC = setAttributes(new AonNumber(),{
        id:"coefparcial", 
        name:"coefparcial",
        description:"Coef. Parcial"
    })
    divC.appendChild(numberC);
    addSpanDecimal();
    return divC;
}

const addSpanDecimal = () =>  {
    let coefInput = document.getElementById('coefparcialInput');
    if(coefInput){
        let span = document.createElement(TAG.SPAN);
        span.innerHTML = '0,';
        span.style.position = "absolute";
        span.style.top = "50%";
        coefInput.parentNode.insertBefore(span, coefInput);
    }
}
