import { AonElement } from "../../components/AonElement.js";
import { ToolbarType } from "../../models/enums.js";
import { sendNotification } from "../../services/service.js";
import { serializeForm } from "../../services/utils.js";
import { getTastHolders } from "../../services/taskHolderService.js";
import '../../components/aon-card.js';
import '../../components/aon-input.js';
import '../../components/aon-select.js';
import '../../components/aon-toolbar.js';

export class AonNotificationManual extends AonElement {

    set id(id) {
        this.setAttribute('id', id);
    }

    get id() {
        return this.getAttribute('id');
    }

    constructor() {
        super();
        this.id = this.id || "aonNotificationManual";
        this.TOOLBAR = this.id + "Toolbar";
        this.applicationEl = this.getApplication();
        this.applicationParentEl = this.getApplicationParent();
    }


    connectedCallback() {
        this.build();
    }

    build() {
        this.paintView();
        this.buildToolbar();
        this.listType();
        this.listTaskHolder();
    }


    buildToolbar() {
        const toolbar = this.getElement(this.TOOLBAR);
        toolbar.removeButtons();

        toolbar.addButton2({
            id: 'Send',
            name: 'Enviar',
            icon: 'send'
        }, () => this.sendNotification());
        this.submitEnable();
    }
    
    paintView() {
        const toolbar = /*html*/`<aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}" title="Enviar notificación"> </aon-toolbar>`;
        const form = 
        /*html*/`
        <div>
            <form id="${this.id}Form" action="#" onsubmit="return false;">
                <div id="${this.id}Div">
                    <div class="aonCol-sm-12">
                        <aon-card id="${this.id}NotificationCard" title="Datos de la notificación" flex="true"></aon-card>
                    </div>
                </div>
            </form>
        `;

        this.innerHTML = toolbar + form;

        let aonEmpresaCard = this.getElement(`${this.id}NotificationCard`);

        aonEmpresaCard.setContentHTML(
            /*html*/`
            <div class="aonCol-sm-12 aonCol-md-6">
                <aon-select name="type" id="type" title="Tipo"></aon-select>
            </div>
            <div class="aonCol-sm-12 aonCol-md-6" id="divTaskHolder" >
                <aon-select name="task_holder" id="task_holder" title="Trabajador"></aon-select>
            </div>
            <div class="aonCol-sm-12 aonCol-md-6" id="divEmail" hidden>
                <aon-input name="email" id="email" description="Correo" type="text"></aon-input>
            </div>
            <div class="aonCol-sm-12 aonCol-md-6">
                <aon-input name="title" id="title" description="Título" type="text"></aon-input>
            </div>
            <div class="aonCol-sm-12 aonCol-md-6">
                <aon-input name="body" id="body" description="Mensaje" type="text"></aon-input>
            </div>
            `
        );
        this.eventListener();
    }


    listType(){
        let type = this.getElement('type');
        let options = [
            {
                name:"Empleado",
                value:"employee"
            },
            {
                name:"Personalizado",
                value:"personalized"
            }
        ]
        type.options = JSON.stringify(options);
        type.value = options[0].value;
    }

    async listTaskHolder(){
        const result = await getTastHolders().catch(e=>null);
        if(result){
            let task_holder = this.getElement('task_holder');
            let options = result.map(r=>({name:r.name,value:r.id}));
            task_holder.options = JSON.stringify(options);
        }
    }

    
    eventListener() {
        this.getElement('type').addEventListener('change', ({detail}) => {
            if(detail){
                const {value} = detail;
                if("employee"===value){
                    this.getElement("divTaskHolder").hidden = false;
                    this.getElement("divEmail").hidden = true;
                } else {
                    this.getElement("divTaskHolder").hidden = true;
                    this.getElement("divEmail").hidden = false;
                }
            }
        });

        this.getElement('title').addEventListener('keyup', (ev) => this.submitEnable());
        this.getElement('body').addEventListener('keyup', (ev) => this.submitEnable());
    }

    submitEnable(){
        let color = "grey";
        let toolbarSection = this.getElement(this.TOOLBAR);
        const button = "Send";
        const buttonToolbarEl = this.getElement(toolbarSection.TOOL_SECTION + button + 'Button');
        if(buttonToolbarEl){
            const formData = this.formData();
            if(formData.title && formData.body){
                color = "black";
            } 
            buttonToolbarEl.color = color;
        }
        return color === "black";
    }

    formData(){
        return serializeForm(this.getElement(`${this.id}Form`));
    }

    sendNotification(){
        if(this.submitEnable()){
            const formData = this.formData();
            this.applicationEl.confirmDialog("Enviar notificación", "Desea enviar la notificación?", () => {
                sendNotification(formData).then(result => {
                    this.applicationEl.getToast().start({ message: 'Notificación enviada!', type: 'success', delay: 3000 });
                }).catch(error=>{
                    if(typeof error ==="string") error = JSON.parse(error);
                    const {message, type} = error;
                    this.applicationEl.getToast().start({ message, type});
                });
            });
        }

    }

}
window.customElements.define('aon-notification-manual', AonNotificationManual);
