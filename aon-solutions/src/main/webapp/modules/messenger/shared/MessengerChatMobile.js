import { AonToolbar } from "../../../components/aon-toolbar.js";
import { COLORS, CSS, EVENT, MSG, TAG } from "../../../environments/environments.js";
import { ToolbarType } from "../../../models/enums.js";
import { newComponent, setAttributes, setStyles} from "../../../services/utils.js";
import * as ACTIONS from "../../actions.js";
import {  MESSENGER_COMPONENTS, MESSENGER_IDS, TASK_SOURCE } from "../MessengerEnums.js";
import {  createDivEditable, createMobileMainView, createTitle, createAonTextArea, createChat, createSectionComment, createTaskHolder, createWorkgroup} from "./creationUtils.js";
import { buildTextareaToolbar, fillWorkGroup } from "./utils.js";

/**
 * 
 * @param {HTMLElement} aonMessengerChat component aon-messenger-chat.js
 */
export const buildMobile = (aonMessengerChat)=> {
    const mainView = createMobileMainView();
    aonMessengerChat.appendChild(mainView);

    if(aonMessengerChat.task.source === TASK_SOURCE.REQUEST) 
        buildRequest(mainView, aonMessengerChat);
    else 
        buildQuery(mainView, aonMessengerChat); // SOURCE QUERY 
}

/**
 * Create desktop chat for messenger (mobile)
 * @param {HTMLElement} mainView htmlElement aon-messenger-chat
 * @param {HTMLElement} aonMessengerChat htmlElement aon-messenger-chat
 */
const buildQuery = (mainView, aonMessengerChat) => {
    const task = aonMessengerChat.task;

    const firstDiv = newComponent({
        classes: [CSS.FLEX_ROW],
        id: MESSENGER_IDS.FIRST_DIV,
        styles: {
          width: "100%",
          height: "90%",
          maxWidth: "600px",
          padding: "0",
        //   paddingBottom: "3px"
        },
    });
    firstDiv.appendTo(mainView);
    
    /**
     * Wrapper 
     * if some new menus / toolbars needed, here.
     */
    const wrapper = newComponent({
        type: MESSENGER_COMPONENTS.WRAPPER,
        id:MESSENGER_IDS.MAIN_WRAPPER,
        classes: [CSS.FLEX_COLUMN],
        styles: {
            width: "100%",
            height: '100%',
            fontSize: '14px'
        }
    });
    wrapper.appendTo(firstDiv.element);

    /**
     * Building toolbars
     */
    buildToolbar(aonMessengerChat, wrapper);
    
    /**
     * The chat itself
     */
    const chat = createChat();
    chat.classList.add(CSS.NO_SCROLLBAR);
    wrapper.element.appendChild(chat);

    const title = setStyles(createTitle(task.title),{
        display : 'block',
        fontSize: '1.3em',
        paddingTop: "10px",
        paddingBottom: "10px",
        width : '100%',
        borderBottom : "1px solid " + CSS.variable(COLORS.AON_LIGHT_GRAY)
    });
    chat.appendChild(title);

    //CREATE COMMENT CHAT
    buildChat(mainView, aonMessengerChat);


    setTimeout(() =>  setStyles(mainView, { opacity: 1, marginTop: 0}), 100);
}

/**
 * @param {HTMLElement} mainView htmlElement aon-messenger-chat
 * @param {HTMLElement} aonMessengerChat htmlElement aon-messenger-chat
 */
const buildRequest =  (mainView, aonMessengerChat) => {
    aonMessengerChat.getApplication().development();
}

const changeStyleSelect = (aonSelect) => {
    const aonSelectInput = aonSelect.querySelector(TAG.INPUT);
    if(aonSelectInput){
        setStyles(aonSelectInput,{
            borderBottom : "1px solid #e0e0e0",
            marginBottom : 0,
            paddingLeft : "1.5em",
            paddingRight : "1.5em",
            transition : "background-color .25s"
        });
    }
    /**
     * Adjust the space issues
     * related to AonInput defaults
     */
    const aonSelectSpan = aonSelect.querySelector(TAG.SPAN);
    if(aonSelectSpan){
        setStyles(aonSelectSpan,{
            paddingLeft : "1.5em",
            paddingRight : "1.5em",
            transition: ".25s",
            top : "5%"
        });
    }
    const aonSelectGroup = aonSelect.querySelector(".aonInputGroup");
    if(aonSelectGroup)
        aonSelectGroup.style.marginBottom = "0px";
}

/**
 VIEW CREATE TASK MOBILE
 * @param {*} wrapper 
 * @param {*} toolbar 
 * @param {*} aonMessengerChat 
 */
const buildCreate = (wrapper, toolbar, aonMessengerChat)=>{
    const application = aonMessengerChat.applicationEl;
    const task = aonMessengerChat.task;
    const newRequestPanel = newComponent({
        type: TAG.DIV,
        classes : [CSS.FLEX_COLUMN, CSS.FLEX_ALIGN_CENTER],
        styles : {
            height: '100%',
            width: '100%',
            background: CSS.variable(COLORS.AON_WHITE),
            position: 'absolute',
            top: '0%',
            opacity: 1,
            transition: '.5s',
            zIndex : 10
        }
    });
    newRequestPanel.appendTo(wrapper.element);

    newRequestPanel.appendChild(toolbar);
 
    const toolbarsHeader = toolbar.querySelector("header");
    if(toolbarsHeader){
        setStyles(toolbarsHeader,{
            margin : 0,
            paddingLeft : "1.5em",
            paddingRight : "1.5em",
        });
    }

    const div = document.createElement(TAG.DIV);
    div.className = CSS.AON_MOBILE_SUB_CONTENT;
    div.style.width = "100%";
    newRequestPanel.appendChild(div);

    /**
     * Creating title input
     */
    const titleIn = setStyles( createDivEditable(task.title, MESSENGER_IDS.TITLE_TASK, MSG.TYPE_HERE), {
        padding: "10px 20px",
        display : "block",
        width: "100%",
    });
    div.appendChild(titleIn); 

    /**
     * smooth border colors
     */
    const titleInInput = document.querySelector(TAG.INPUT);
    if(titleInInput){
        setStyles(titleInInput,{
            borderBottom : "1px solid #e0e0e0",
            marginBottom : 0,
            paddingLeft : "1.5em",
            paddingRight : "1.5em",
            transition : "background-color .25s"
        });
    }
    /**
     * Adjust the space issues
     * related to AonInput defaults
     */
     const titleInSpan = document.querySelector(TAG.SPAN);
     if(titleInSpan){
        setStyles(titleInSpan,{
            paddingLeft : "1.5em",
            paddingRight : "1.5em",
            transition: ".25s",
            top : "5%"
        });
     }

     const titleInGroup = document.querySelector(".aonInputGroup");
     if(titleInGroup)
        titleInGroup.style.marginBottom = "0px"

    //----------------WORKGROUP
    const workgroupSelect = setStyles(createWorkgroup(), {
        display : "block",
        width: "100%",
    });
    div.appendChild(workgroupSelect);
    fillWorkGroup(task, application);
    changeStyleSelect(workgroupSelect);

      //-----------------TASK HOLDER
    const taskHolderSelect = setStyles(createTaskHolder(), {
        display : "block",
        width: "100%",
    });
    div.appendChild(taskHolderSelect);
    changeStyleSelect(taskHolderSelect);

    // /**
    //  * Creating text area
    //  */
    const aonTextArea = setStyles(createAonTextArea(`${MSG.WRITE_A_DESCRIPTION}...`),{
        height: "100%",
        width: "100%",
        marginTop : 0,
        boxShadow : "none",
    });
    aonTextArea.id = MESSENGER_IDS.DESCRIPTION_TASK;
    div.appendChild(aonTextArea);
    buildTextareaToolbar(aonTextArea, aonMessengerChat.task, false);
    let textAreaDiv = document.getElementById(aonTextArea.TEXTAREA);
    if(textAreaDiv) textAreaDiv.style.padding = "20px";

   /**
    * smooth border colors
    */
    const aonTextAreaToolbar = document.querySelector("toolbar");
    if(aonTextAreaToolbar){
        setStyles(aonTextAreaToolbar,{
            paddingLeft  : "calc(1.5em - 5px)",
            paddingRight : "calc(1.5em - 5px)",
            borderBottom : "1px solid #e0e0e0"
        });
    }
}

const buildChat = (mainView, aonMessengerChat) => {
    const secondDiv = newComponent({
        classes: [CSS.FLEX_ROW],
        id: MESSENGER_IDS.SECOND_DIV,
        styles: {
          width: "100%",
          position: "absolute",
          bottom: 0
        },
    });
    secondDiv.appendTo(mainView);

    const divs = createSectionComment(secondDiv);
    divs.iconOpenFull.addEventListener(EVENT.CLICK, ()=>openFullComment(divs.aonTextArea));
    divs.iconSend.addEventListener(EVENT.CLICK, ()=> aonMessengerChat.saveTaskWorkflow());
    changeStyleSectionComment(divs);

    createFullComment(aonMessengerChat, divs.aonTextArea);
}


/**
 * Build mobile version of the writter 
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat component 
 * @param {HTMLElement} aonTextArea textarea principal
 */
 const createFullComment = (aonMessengerChat, aonTextArea) => { 
    const wrapper = document.getElementById(MESSENGER_IDS.MAIN_WRAPPER);
    const writter = newComponent({
        id : MESSENGER_COMPONENTS.WRITTER,
        classes: [CSS.FLEX_COLUMN],
        styles: {
            background: COLORS.AON_WHITE,
            height: '100%',
            width: '100%',
            position: 'absolute',
            display: 'none',
            transition: '.5s',
            top: 0,
            opacity: 0,
            zIndex: -9
        }
    }).element;
    wrapper.appendChild(writter);

    const bar = setAttributes(new AonToolbar(),{
       type:ToolbarType.SECONDARY,
       title:MSG.COMMENT
    });
    bar.style.background = "#fff";
    writter.appendChild(bar);

    bar.addButton2(ACTIONS.SEND,() => {
        aonMessengerChat.saveTaskWorkflow();
        closeFullComment();
    });
    bar.addButton2(ACTIONS.BACK,() => closeFullComment());

    const textarea = setStyles(createAonTextArea(), {
        flexDirection: 'column',
        height: '100%',
        width: '100%',
        boxShadow: "none",
        background: CSS.variable(COLORS.AON_WHITE),
        margin: 0,
    });
    writter.appendChild(textarea);
    buildTextareaToolbar(textarea, aonMessengerChat.task, true);

    const textAreaToolbar = textarea.querySelector("toolbar");
    if(textAreaToolbar){
        setStyles(textAreaToolbar, {
            background: CSS.variable(COLORS.AON_LIGHT_GRAY),
            border: "none",
            padding: "10px",
            height: "50px"
        });
    }
    textarea.addEventListener(EVENT.INPUT, ()=>{
      aonTextArea.value = textarea.value || "";
      aonTextArea.FILES = textarea.FILES;
    });
}
/**
 * Hide writter with animation
 */
const closeFullComment = () => setTimeout(() => setStyles(document.getElementById(MESSENGER_COMPONENTS.WRITTER),{zIndex : -9, opacity : 0}), 100);

const openFullComment  = (aonTextArea) => {
    const writter = document.getElementById(MESSENGER_COMPONENTS.WRITTER);
    const textarea = writter.querySelector("aon-textarea");
    if(aonTextArea.value) textarea.value = aonTextArea.value;
    // show writter
    let componentWrite = setStyles(writter, { display: "flex" });
    setTimeout(() => setStyles(componentWrite, {zIndex: 9,opacity: 1,left: 0}), 100);
} 

const changeStyleSectionComment = (divs) => {
    divs.aonTextArea.style.fontSize = "15px";
    divs.aonTextArea.style.margin = "0";
    divs.iconOpenFull.querySelector("i").style.fontSize = "2.5em";
    divs.iconSend.querySelector("i").style.fontSize = "2.5em";
}

const buildToolbar = (aonMessengerChat, wrapper) => {
    const task = aonMessengerChat.task;
    /**
     * Building toolbars
     */
     const toolbar = setAttributes(new AonToolbar(),{
        type: ToolbarType.SECONDARY,
        title:"#" + (task.number || "0").toString().padStart(5, 0)
    });
    toolbar.style.width = "100%"; 
    
    if(task.id){
        wrapper.appendChild(toolbar);
    } else {
        buildCreate(wrapper, toolbar, aonMessengerChat);

        toolbar.addButton2(ACTIONS.SAVE,() => aonMessengerChat.save())
    }
    
    toolbar.addButton2(ACTIONS.BACK,() => aonMessengerChat.back());
}

