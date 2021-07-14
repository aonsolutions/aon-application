import { AonToolbar } from "../../components/aon-toolbar.js";
import { AonElement } from "../../components/AonElement.js";
import { COLORS, CONSTANT, CSS } from "../../environments/environments.js";
import { ToolbarType } from "../../models/enums.js";
import {
  getReader,
  newComponent,
  setAttributes,
  setClasses,
  setStyles,
  setValueName,
} from "../../services/utils.js";
import {
  createMainView,
  createMobileMainView
} from "./createComponents.js";
import {
  buildDesktopChat,
  buildMobileChat,
  fillChat,
} from "./shared/messenger-chat.js";
import {
  buildDesktopWritter,
  sendMessage,
} from "./shared/messenger-writter.js";
import {
  MESSENGER_COMPONENTS,
  MESSENGER_IDS,
  MESSENGER_VIEWS,
  WORKFLOW_TYPES,
} from "./MessengerEnums.js";
import { createOutlinedMaterialIcon } from "./shared/creationUtils.js";
import * as ACTIONS from "../actions.js";
import {
  saveTask,
  getTaskWorkflow,
  saveTaskWorkflow,
  saveTaskAttach,
  getTaskAttach
} from "../../services/taskService.js";
import { openFileBase64 } from "../../services/request.js";

export class AonMessengerChat extends AonElement {
  _data;
  UPDATE;
  static get observedAttributes() {
    return [CONSTANT.DATA];
  }

  get data() {
    return JSON.parse(this.getAttribute(CONSTANT.DATA) || "{}");
  }

  set data(data) {
    this.setAttribute(CONSTANT.DATA, JSON.stringify(data));
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  disconnectedCallback() {
    this.deleteToolbar();
  }

  initialize() {
    this.id = this.id || MESSENGER_VIEWS.AON_MESSENGER_CHAT;
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this.deleteToolbar();
    this.setData({
      id: undefined,
      number: undefined,
      title: undefined,
      for: "",
      workgroup:{
        id:null
      },
      task_holder:{
        id:null
      },
      workflow: [],
    });
  }

  deleteToolbar() {
    try {
      this.getApplication().removeFloatOption();
      this.getApplication().removeToolbarOptions();
    } catch (error) {}
  }

  build() {
    if (this.data && this.data.id) {
      this.setData({...this._data,...this.data});
      this.UPDATE = true;
    }

    this.paintView();
  }

  paintView() {
    console.log(this.data);
    /*Base font-size*/
    this.style.fontSize = "12px";
    /**
     * Switching between mobile and desktop
     */
    if (this.isMobile()) {
      this.paintMobile();
    } else {
      this.paintDesktop();
    }
    if (this.UPDATE) this.setValues();
  }

  paintDesktop() {
    this.buildToolbarDesktop();

    const mainView = createMainView();
    mainView.appendTo(this);

    const writter = newComponent({
      classes: [CSS.FLEX_COLUMN, CSS.FLEX_ALIGN_CENTER],
      styles: {
        width: "50%",
        height: "100%",
        minWidth: "400px",
        maxWidth: "600px",
        paddingTop: "5vh",
        paddingRight: "20px",
        paddingLeft: "60px",
        top: 0,
      },
    });
    writter.appendTo(mainView.element);

    buildDesktopWritter(writter, this);
    this.buildCommentDesktop();

    setTimeout(() => {
      mainView.element.style.opacity = 1;
      mainView.element.style.marginTop = 0;
    }, 100);
  }

  buildCommentDesktop() {
    const mainView = this.getElement(MESSENGER_IDS.MAIN_DIV);
    const chat = newComponent({
      classes: [CSS.FLEX_ROW],
      styles: {
        width: "50%",
        height: "90%",
        minWidth: "400px",
        maxWidth: "600px",
        paddingTop: "5vh",
        paddingRight: "40px",
        paddingLeft: "40px",
      },
    }).element;
    if (!this.UPDATE) chat.style.display = "none";

    mainView.appendChild(chat);
    buildDesktopChat(chat);
    /**
     * Setting the chat line once all is rendered
     * DO NOT change this, is compulsory.
     */
    const lined = this.selector(".continueLined");
    if (lined) lined.style.setProperty("--height", lined.scrollHeight + "px");
  }

  buildToolbarDesktop() {
    const toolbar = setAttributes(new AonToolbar(), {
      id:"id",
      type:ToolbarType.SECONDARY,
      title:"#" + (this.getData().number || "0").toString().padStart(5, 0)
    });

    this.appendChild(toolbar);

    toolbar.addButton2(ACTIONS.SAVE, () => this.save());
    toolbar.addButton2(ACTIONS.BACK, () => {
      const mainView = this.getElement(MESSENGER_IDS.MAIN_DIV);
      mainView.style.opacity = "0";
      mainView.style.transition = ".25s";
      setTimeout(() => {
        this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST);
      }, 250);
    });

    const titleSpan = toolbar.querySelector(
      `.${CSS.AON_SECONDARY_TOOLBAR_TITLE}`
    );

    setClasses(titleSpan, [CSS.FLEX_ROW, CSS.FLEX_ALIGN_CENTER]);

    const status = createOutlinedMaterialIcon({
      color: CSS.variable(COLORS.ONLINE_GREEN),
      name: "info",
      size: "20px",
    });
    status.element.style.marginLeft = "10px";
    status.appendTo(titleSpan);
  }

  paintMobile() {
    if (this.UPDATE) {
      let span = this.applicationEl.addFloatOption(
        {
          name: "Addcomment",
          icon: "add_comment",
          id: "Addcomment",
        },
        () => {
          /**
           * Hidding float button
           */
          setStyles(span.querySelector("button"), {
            transition: "0.25s",
            opacity: 0,
          });
          /**
           * show writter
           */
          let componentWrite = setStyles(
            document.getElementById(MESSENGER_COMPONENTS.WRITTER),
            { display: "flex" }
          );
          setTimeout(() => {
            setStyles(componentWrite, {
              zIndex: 9,
              opacity: 1,
              left: 0,
            });
          }, 100);
        }
      );
    }

    const mainView = createMobileMainView();
    mainView.appendTo(this);

    const chat = newComponent({
      classes: [CSS.FLEX_ROW],
      styles: {
        width: "100%",
        height: "100%",
        maxWidth: "600px",
        paddingRight: "20px",
        paddingLeft: "20px",
      },
    });
    chat.appendTo(mainView.element);

    buildMobileChat(chat, this);

    setTimeout(() => {
      setStyles(mainView.element, {
        opacity: 1,
        marginTop: 0,
      });
    }, 100);
  }

  setValues() {
    const { workgroup, task_holder } = this.getData();
    if (workgroup && workgroup.id)
      setValueName(MESSENGER_IDS.WORKGROUP, workgroup.id);
    if (task_holder && task_holder.id)
      setValueName(MESSENGER_IDS.TASKHOLDER, task_holder.id);

    //FILL CHATS WORKFLOW
    this.getTaskWorkflow();

    //TEST 
    this.getTaskAttach();
  }

  formSerialize() {
    let aonTextArea = this.getElement(MESSENGER_IDS.COMMENT_TASK);
    const comment = aonTextArea.value;
    if (comment) sendMessage(aonTextArea); //remove comment and processed
    const sender = this.applicationParentEl.SENDER;
    const domain = sender.domain.id;
    const workgroupEl = this.selector("#" + MESSENGER_IDS.WORKGROUP);
    const task_holderEl = this.selector("#" + MESSENGER_IDS.TASKHOLDER);
    let json = {
      domain,
      sender,
      id: this.getData().id,
      title: this.selector(`#${MESSENGER_IDS.TITLE_TASK}`).innerText,
      workgroup: {
        id:workgroupEl.value
      },
      task_holder: {
        id:task_holderEl.value
      },
      workflow: [],
      workflowTmp:{
        domain,
        comment,
        task_holder: sender,
        type: WORKFLOW_TYPES.COMMENT,
      }
    };
    if (!this.UPDATE) {
      json.workflow.push({
        domain,
        comment,
        task_holder: sender,
        type: WORKFLOW_TYPES.OPEN,
      });
      if (comment) {
        json.workflow.push(json.workflowTmp);
      }
    } else { //UPDATE
      const workflowTmpTwo ={
        domain,
        modification_date:new Date().getTime(),
        task_holder: sender,
        type: WORKFLOW_TYPES.ASSIGN,
      }
      if(this.getData().workgroup && this.getData().workgroup.id!= json.workgroup.id){
        workflowTmpTwo.comment = workgroupEl.getText();
        json.workflow.push(workflowTmpTwo);
      }
      if(this.getData().task_holder && this.getData().task_holder.id!= json.task_holder.id){
        workflowTmpTwo.comment = task_holderEl.getText();
        json.workflow.push(workflowTmpTwo);
      }
    }
    return json;
  }

  async saveTaskWorkflow() {
    try {
      let {workflowTmp, id} = this.formSerialize();
      if(workflowTmp.comment){
        workflowTmp.task = id;
        await saveTaskWorkflow(workflowTmp);
      }
    } catch (error) {
      this.showError(error);
    }
  }

  getTaskWorkflow() {
    getTaskWorkflow({ taskId:this.getData().id }).then((workflow) => fillChat(workflow));
  }


  getTaskAttach() {
    getTaskAttach({ taskId:this.getData().id }).then((taskAttachs) => {
      console.log(taskAttachs);
    });
  }

  async save() {
    this.applicationEl.startLoading();
    try {
      const form = this.formSerialize();
      if (form.title) {
        const data = await saveTask(form);
        if(this.UPDATE){
          this.setData({...form,...data});
          if(form.workflow.length) fillChat(form.workflow);
        }
        else 
          this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, data);
      }
    } catch (error) {
      this.showError(error);
    }
    this.applicationEl.stopLoading();
  }

  async upload(files) {
    const [file] = files; // ONE FILE
    let {workflowTmp, id} = this.formSerialize();
    
    const reader = await getReader(file).catch(e=>null);
    if(reader){
      const {content, content_type} = await saveTaskAttach({file:reader, taskId:id, workflow:workflowTmp});
      await openFileBase64(content, content_type);
    }
  }

  selector(selector) {
    return document.querySelector(selector) || {};
  }

  getData(){
    return this._data;
  }
  setData(data){
    this._data = data;
  }
}

window.customElements.define("aon-messenger-chat", AonMessengerChat);
