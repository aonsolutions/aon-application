import { AonToolbar } from "../../../components/aon-toolbar.js";
import { COLORS, CSS, MSG, TAG } from "../../../environments/environments.js";
import { ToolbarType } from "../../../models/enums.js";
import { newComponent, setAttributes, setStyles} from "../../../services/utils.js";
import * as ACTIONS from "../../actions.js";
import {  createDivEditable, createMobileMainView, createTitle } from "../createComponents.js";
import {  MessengerSidenav, MESSENGER_COMPONENTS, MESSENGER_IDS } from "../MessengerEnums.js";
import { createAonTextArea, createTaskHolder, createWorkgroup} from "./creationUtils.js";
import { buildTextareaToolbar, fillWorkGroup } from "./utils.js";

/**
 * 
 * @param {HTMLElement} aonMessengerChat component aon-messenger-chat.js
 */
 export const buildMobile = (aonMessengerChat)=> {
    const task = aonMessengerChat.task;    

    if (task.id) {
        let span = aonMessengerChat.getApplication().addFloatOption(MessengerSidenav.ADD_COMMENT, () => {
            //Hidding float button
            setStyles(span.querySelector("button"), { transition: "0.25s", opacity: 0});
            // show writter
            let componentWrite = setStyles(document.getElementById(MESSENGER_COMPONENTS.WRITTER), { display: "flex" });
            setTimeout(() => setStyles(componentWrite, {zIndex: 9,opacity: 1,left: 0}), 100);
          }
        );
      }
  
    buildMobileChat(aonMessengerChat);
}

/**
 * Create desktop chat for messenger (mobile)
 * @param {HTMLElement} aonMessengerChat htmlElement aon-messenger-chat
 */
const buildMobileChat = (aonMessengerChat) => {
    const task = aonMessengerChat.task;
    const mainView = createMobileMainView();
    aonMessengerChat.appendChild(mainView);

    const firstView = newComponent({
        classes: [CSS.FLEX_ROW],
        styles: {
          width: "100%",
          height: "100%",
          maxWidth: "600px",
          padding: "0"
        },
    });
    firstView.appendTo(mainView);

    /**
     * Wrapper 
     * if some new menus / toolbars needed, here.
     */
    const wrapper = newComponent({
        type: MESSENGER_COMPONENTS.WRAPPER,
        classes: [CSS.FLEX_COLUMN],
        styles: {
            width: "100%",
            height: '100%',
            fontSize: '14px'
        }
    });
    wrapper.appendTo(firstView.element);

    /**
     * Building toolbars
     */
    const toolbar = setAttributes(new AonToolbar(),{
        type: ToolbarType.SECONDARY,
        title: aonMessengerChat.task.id ? "#"+(task.number  || "0").toString().padStart(5,0) : MSG.NEW_REQUEST
    });
    
    if(aonMessengerChat.task.id){
        wrapper.appendChild(toolbar);
    } else {
        buildMobileChatCreate(wrapper, toolbar, aonMessengerChat);
    }

    //TOOLBAR BOTONS  
    toolbar.style.width = "100%"; 
    if(!aonMessengerChat.task.id){
        toolbar.addButton2(ACTIONS.SAVE,() => aonMessengerChat.save())
    }    
    toolbar.addButton2(ACTIONS.BACK,() => {
        wrapper.element.style.transition = ".25s";
        wrapper.element.style.opacity = "0";
        setTimeout(() =>  aonMessengerChat.back(), 250);
    });

    
    /**
     * The chat itself
     */
    const chat = newComponent({
        type: MESSENGER_COMPONENTS.CHAT,
        id: MESSENGER_IDS.MESSENGER_CHAT,
        attributes:{
            title: MSG.COMMENTS
        },
        classes: [
            "continueLined", 
            CSS.FLEX_COLUMN, 
            CSS.NO_SCROLLBAR, 
            CSS.FLEX_ALIGN_CENTER
        ],
        styles: {
            position: "relative",
            width: '100%',
            zIndex: "0",
            "scroll-behavior": "smooth",
            height: '100%',
            padding: "20px",
            paddingTop: "0px",
            overflow: 'auto',
            borderBottom: '1px solid #f0f0f0',
        }
    });

    chat.appendTo(wrapper.element);

    const title = setStyles(createTitle(task.title),{
        display : 'block',
        fontSize: '1.3em',
        paddingTop: "10px",
        paddingBottom: "10px",
        width : '100%',
        background : "#fff",
        borderBottom : "1px solid " + CSS.variable(COLORS.AON_LIGHT_GRAY)
    });
    chat.appendChild(title);

    buildMobileWritter(wrapper, aonMessengerChat);

    setTimeout(() => {
        setStyles(mainView, { opacity: 1, marginTop: 0});
    }, 100);
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
const buildMobileChatCreate = (wrapper, toolbar, aonMessengerChat)=>{
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
    const titleIn = setStyles( createDivEditable(task.title, MESSENGER_IDS.TITLE_TASK, `Escriba su ${MSG.ISSUE} aquí`), {
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
    const aonTextArea = setStyles(createAonTextArea(aonMessengerChat.task.id ? `${MSG.WRITE_A_COMMENT}...` : `${MSG.WRITE_A_DESCRIPTION}...`),{
        height: "100%",
        width: "100%",
        marginTop : 0,
        boxShadow : "none",
    });
    div.appendChild(aonTextArea);
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



/**
 * Build mobile version of the writter 
 * @param {HTMLElement} wrapper 
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat component 
 */
const buildMobileWritter = (wrapper, aonMessengerChat) => { 
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
    });
    writter.appendTo(wrapper.element);

    const bar = setAttributes(new AonToolbar(),{
       type:ToolbarType.SECONDARY,
       title:MSG.COMMENT
    });

    bar.style.background = "#fff";

    writter.element.appendChild(bar);

    bar.addButton2(ACTIONS.SAVE,() => {
        aonMessengerChat.saveTaskWorkflow();
        hideWritter();
    });
    bar.addButton2(ACTIONS.BACK,() => hideWritter());

    const textarea = setStyles(createAonTextArea(), {
        flexDirection: 'column',
        height: '100%',
        width: '100%',
        boxShadow: "none",
        background: CSS.variable(COLORS.AON_WHITE),
        margin: 0,
    });
    writter.element.appendChild(textarea);
    buildTextareaToolbar(textarea, aonMessengerChat.task);

    const textAreaToolbar = textarea.querySelector("toolbar");
    if(textAreaToolbar){
        setStyles(textAreaToolbar, {
            background: CSS.variable(COLORS.AON_LIGHT_GRAY),
            border: "none",
            padding: "10px",
            height: "50px"
        });
    }
}



/**
 * Hide writter with animation
 */
 const hideWritter = () => {
    let button = setStyles(document.getElementById(MESSENGER_IDS.ADD_ICON_BUTTON) , {transition : "0.25s", opacity : "1"});
    setTimeout(() => button.style.display = "block", 100);

    setTimeout(() => {
        setStyles(document.getElementById(MESSENGER_COMPONENTS.WRITTER),{zIndex : -9, opacity : 0});
    }, 100);
}


