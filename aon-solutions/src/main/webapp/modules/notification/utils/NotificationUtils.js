import { CreateComponent } from "../../../components/CreateComponent.js";
import { CONSTANT, MSG } from "../../../environments/environments.js";
import { serializeForm } from "../../../services/utils.js";
import { NotificationEnums } from "../NotificationEnums.js";
import * as LS from "../../../services/localStorageService.js";

import { sendNotification } from "../../../services/service.js";

const buildDialogAdd = (idForm) =>{
    const form = CreateComponent.createForm(idForm);
    CreateComponent.createAonSelect({
      attributes:{
        name:"type",
        id:"type",
        title:MSG.TYPE,
        options: NotificationEnums.TYPE_USER,
        value: NotificationEnums.TYPE_USER[0].value
      },   
      events: {
        change: ({detail})=>{
          if(detail && detail.value) {
            hiddenElement(detail.value);
          }
        }
      },
    }, form);

    CreateComponent.createAonSelect({
      attributes:{
        name:"task_holder",
        id:"task_holder",
        title: MSG.EMPLOYEE
      }
    }, form);
    
    CreateComponent.createAonInput({
      attributes:{
        name:"email",
        id:"email",
        description:MSG.EMAIL,
        type:"text",
        hidden:true
      }
    }, form);

    CreateComponent.createAonInput({
      attributes:{
        name:"title", 
        id:"title", 
        description:MSG.TITLE,
        type:"text"
      }
    }, form);

    CreateComponent.createAonInput({
      attributes:{
        name:"body",
        id:"body",
        description:"Mensaje",
        type:"text"
      }
    }, form);

    return form;
}

const hiddenElement = (value)=> {
  let taskHolderEl = document.getElementById("task_holder");
  let emailEl = document.getElementById("email");
  
  if("employee"===value){
    taskHolderEl.hidden = false;
  
    taskHolderEl.hidden = true;
    emailEl.removeAttribute(CONSTANT.HIDDEN);
  }
}

  
const openDialog = (parent, dialog) =>{
  if (!parent.isMobile()) {
    dialog.width = "500px";
  }

  dialog.clear();
  
  dialog.setContent(buildDialogAdd(`${parent.id}Form`));

  parent.listTaskHolder()
  .then(taskHolders=>{
    document.getElementById("task_holder").setOptions(taskHolders);
  });

  dialog.setTitle(MSG.NOTIFICATION);
  dialog.open();
  dialog.addSendAction(() =>  saveNotification(parent, dialog), MSG.SEND);
}

const saveNotification = async(parent, dialog)=> {
  const formData = serializeForm(document.getElementById(`${parent.id}Form`));
  if(formData.title && formData.body) {
    let buttonAccept = dialog.getButtonAccept();
    buttonAccept.disabled = true;
    try {
        await sendNotification(formData);
        parent.showToast({ message: MSG.MSG_SENT, type: CONSTANT.SUCCESS, delay: 3000 });
    } catch (error) {
      parent.showToast(error);
    }
    buttonAccept.disabled = false;
    dialog.close();
  }
}

const setDomainStorage = (domain)=>{
  if(domain && domain.id){
    LS.setDomainId(domain.id);
    LS.setDomainName(domain.name);
  }
}


export const NotificationUtils = {
  buildDialogAdd,
  openDialog,
  setDomainStorage
}