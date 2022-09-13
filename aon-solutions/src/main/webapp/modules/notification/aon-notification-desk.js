import { AonElement } from "../../components/AonElement.js";
import { serializeForm } from "../../services/utils.js";
import { getDomainUserRoles, getNotification, getTastHolders, markReadNotification, sendNotification } from "../../services/service.js";
import { AonDocumental } from "../documental/aon-documental.js";
import { CONSTANT, MSG } from "../../environments/environments.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { AonMessenger } from "../messenger/aon-messenger.js";
import { App } from "../../models/enums.js";
import * as LS from "../../services/localStorageService.js";
import { AonApplication } from "../../components/aon-application.js";
import { SigninSidenav } from "../timecontrol/signinEnums.js";
import { NotificationUtils } from "./NotificationUtils.js";
import { NotificationEnums } from "./NotificationEnums.js";

export class AonNotificationDesk extends AonElement {
  AON_NOTIFICATION_DESK;
  MORE;
  TASK_HOLDERS;
  dur;
  DIV_PARENT;
  static get observedAttributes() {
    return [CONSTANT.DATA];
  }

  get data() {
    return this.getAttribute(CONSTANT.DATA) ? JSON.parse(this.getAttribute(CONSTANT.DATA)) : null;
  }

  set data(data) {
    this.setAttribute(CONSTANT.DATA, data);
  }

  getFilter() {
		return this.hasAttribute(CONSTANT.FILTER)? JSON.parse(this.getAttribute(CONSTANT.FILTER))	: {page:0, perPage:10};
	}

	setFilter(filter) {
		return this.setAttribute(CONSTANT.FILTER, JSON.stringify(filter));
	}

  attributeChangedCallback(name, oldValue, newValue) {}

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    getDomainUserRoles({reload:true}).then(r=>{
      this.dur = new DomainUserRoles(r);
      this.build();
    });
  }

  initialize() {
    this.id = NotificationEnums.NOTIFICATION_IDS.AON_NOTIFICATION_DESK;
    this.AON_NOTIFICATION_DESK =this.id+"Application";
    this.aonNotifyIconEl = document.querySelector("aon-notification-icon");
    this.TASK_HOLDERS = [];
    this.MORE = true;
  }

  getDur() {
    return this.dur;  
  }

  build() {
		this.createApplication(this.AON_NOTIFICATION_DESK, MSG.NOTIFICATIONS, new AonApplication());
    this.buildToobar();

    this.paintView();

    this.loadMore(true);
  }

  buildToobar(){
    const application = this.getApplication();

    application.addToolbarOption2(SigninSidenav.ADD, () => this.openDialog());

    this.navBar();
  }

  navBar(){
    const notificationOptions = NotificationEnums.NotificationOptions;
    let notificationOpts = [
			{
				...notificationOptions.NOTIFICATION_ALL,
				fn: () =>{
          this.setFilter({page:0, perPage:10, status: undefined});
          this.loadMore(true);
				}
			},
			{
				...notificationOptions.NOTIFICATION_NOT_READ,
				fn: () =>{
          this.setFilter({page:0, perPage:10, status: undefined});
          this.loadMore(true);
				}
			}
		];
		
		this.getApplication().addSidenavOptions(MSG.STATUS, notificationOpts);
  }

  paintView() {
    this.DIV_PARENT = NotificationUtils.buildDesk(this.id + "DivParent");
 
    this.getApplication().setContent(this.DIV_PARENT);

    let timeOut = undefined;
    this.DIV_PARENT.addEventListener("scroll", ({target}) => {

      clearTimeout(timeOut);
      if(this.MORE){
        const scrollTop = target.scrollTop;
        const offsetHeight = target.offsetHeight;
        const physicalSize = target.scrollHeight;
        const maxScrollPosition = physicalSize - offsetHeight;

        if (scrollTop >= maxScrollPosition) {
          timeOut = setTimeout(async () => {
            await this.loadMore(false);
          }, 300);
        }
      }
    });
  }

  async loadMore(reload) {
    
    const datos = await this.getData();

    if (reload){
      this.DIV_PARENT.innerHTML ="";
    } 

    datos.forEach((res) => {
      this.DIV_PARENT.appendChild(
        NotificationUtils.buildRow(res)
      );
    });
  }

  async getData() {
    let data = [];
    try {
      let filter = this.getFilter();
        filter.page = filter.page + 1;
        this.setFilter(filter);
        const datos = await getNotification(filter);
      
        if(datos.length == 0 ){
          this.MORE = false;
        } else {
          data = datos;
        }
    } catch (error) {
      console.log("error>>", error);
      this.showError(error);
    }
    return data;
  }
  
  openDialog() {
    const dialog = this.getApplication().getDialog();
    dialog.width = "500px";
    dialog.clear();
    
    dialog.setContent(NotificationUtils.buildDialogAdd(`${this.id}Form`));

    this.listTaskHolder().then(taskHolders=>{
      document.getElementById("task_holder").setOptions(taskHolders);
    });

    dialog.setTitle(MSG.NOTIFICATION);
    dialog.open();
    dialog.addSendAction(() =>  this.sendNotification(), MSG.SEND);
  }

  async sendNotification(){
    const formData = serializeForm(this.getElement(`${this.id}Form`));
    if(formData.title && formData.body) {
      const dialog = this.getApplication().getDialog();
      let buttonAccept = dialog.getButtonAccept();
      buttonAccept.disabled = true;
      try {
          await sendNotification(formData);
          this.showToast({ message: MSG.MSG_SENT, type: CONSTANT.SUCCESS, delay: 3000 });
      } catch (error) {
        this.showToast(error);
      }
      buttonAccept.disabled = false;
      dialog.close();
    }
  }

  async listTaskHolder(){
    if(this.TASK_HOLDERS.length <= 0){
      let result = await getTastHolders().catch(e=>null);
      if(result) this.TASK_HOLDERS = result.map(r=>({name:r.name,value:r.id}));
    }
    return this.TASK_HOLDERS;
  }
  
  markReadNotification(res){
    if(res.status ===0){
      markReadNotification(res);
      res.status=1;
    }
  }

  async goNotification(data) {
    const {source, source_id, domain} = data;
    if(source && source_id){
      let aonComponent = null;
      switch(source){
        case App.DOCUMENTAL:
          aonComponent =  new AonDocumental();
          break;
        case App.MESSENGER:
          aonComponent =  new AonMessenger();
          break;
      }

      aonComponent.value = source_id;

      if(aonComponent){
        this.setDomainStorage(domain);
        this.rootPanel(aonComponent);
      }
    }
  }

  setDomainStorage(domain){
    if(domain && domain.id){
      LS.setDomainId(domain.id);
      LS.setDomainName(domain.name);
    }
  }
}
window.customElements.define("aon-notification-desk", AonNotificationDesk);
