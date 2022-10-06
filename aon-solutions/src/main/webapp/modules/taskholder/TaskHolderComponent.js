import { CreateComponent } from "../../components/CreateComponent.js";
import { CSS, MSG, TAG } from "../../environments/environments.js";

const createFormAdd = (aonTaskHolder, parent, taskholder)=> {

    const form = CreateComponent.createForm(aonTaskHolder.id+"Form");
    form.style.width = "100%"
    parent.appendChild(form);

    const aonCard = CreateComponent.createAonCard({id: aonTaskHolder.id+"Card", flex:"true"}, form);
 
    let div = document.createElement(TAG.DIV);
    aonCard.addContent(div);

    let divG = document.createElement(TAG.DIV);
    divG.classList.add(CSS.AON_COL_XS_12);
    div.appendChild(divG);
    CreateComponent.createAonInput({
      attributes:{
        name:"document",
        id:"document" ,
        type:"text",
        required: true,
        description:"DNI/NIE",
        value: taskholder.getDocument() || "",
      },
      events:{
        input: ({target}) => {
          taskholder.setDocument(target.value);
        }
      }
    }, divG);

    divG = document.createElement(TAG.DIV);
    divG.classList.add(CSS.AON_COL_XS_12);
    div.appendChild(divG);
    CreateComponent.createAonInput({
      attributes:{
        name:"name",
        id:"name" ,
        type:"text",
        required: true,
        description:MSG.NAME,
        value: taskholder.getName() || ""
      },
      events:{
        input: ({target}) => {
          taskholder.setName(target.value);
        }
      }
    }, divG);


    divG = document.createElement(TAG.DIV);
    divG.classList.add(CSS.AON_COL_XS_12);
    div.appendChild(divG);
    CreateComponent.createAonInput({
      attributes:{
        name:"alias",
        id:"alias" ,
        type:"text",
        description:"Alias",
        value: taskholder.getAlias() || ""
      },
      events:{
        input: ({target}) => {
          taskholder.setAlias(target.value);
        }
      }
    }, divG);

    const inputId = CreateComponent.createAonInput({
      attributes:{
        name:"taskHolderid",
        id:"taskHolderid" ,
        type:"text",
        visible:"false",
        value: taskholder.getId() || ""
      }
    });
    aonCard.addContent(inputId);
}


export const TaskHolderComponent = {
    createFormAdd
}