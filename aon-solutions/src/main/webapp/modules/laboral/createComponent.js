import { TAG } from "../../environments/environments.js";
import { createDate } from "../notification/createComponent.js";

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
