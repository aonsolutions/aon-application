import { AonIconButton } from "../../../components/aon-icon-button.js";
import { AonToolbar } from "../../../components/aon-toolbar.js";
import { COLORS, CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from "../../../environments/environments.js";
import { ToolbarType } from "../../../models/enums.js";
import { openFileUrl } from "../../../services/fileService.js";
import { getTastHoldersWorkGroup } from "../../../services/taskHolderService.js";
import { newComponent, setAttributes, setDateTimestampDay, setFullDate, setStyles, setTime, waitEl } from "../../../services/utils.js";
import * as ACTIONS from "../../actions.js";
import { createAction, createDivEditable, createMessageAuthor, createMessageBox, createCommentContent, createTitle } from "../createComponents.js";
import { ICON_TYPES, WORKFLOW_TYPES, WORKFLOW_TYPE, MESSENGER_COMPONENTS, MESSENGER_IDS, MESSENGER_VIEWS } from "../MessengerEnums.js";
import { checkProperties, createAonTextArea, createTaskHolder, createText, createWorkgroup, LEFT, RIGHT } from "./creationUtils.js";
import { buildMobileWritter } from "./messenger-writter.js";

/**
 * Create desktop chat for messenger (mobile)
 * @param {HTMLElement} mainView htmlElement div principal
 * @param {HTMLElement} aonMessengerChat htmlElement aon-messenger-chat
 */
export const buildMobileChat = (mainView, aonMessengerChat) => {
    const data = aonMessengerChat.task;

    const firstView = newComponent({
        classes: [CSS.FLEX_ROW],
        styles: {
          width: "100%",
          height: "100%",
          maxWidth: "600px",
          padding: "0",
        //   paddingRight: "20px",
        //   paddingLeft: "20px",
        },
    });
    firstView.appendTo(mainView);

    /**
     * Wrapper 
     * if some new menus / toolbars needed, here.
     */
    const wrapper = newComponent({
        type: 'wrapper',
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
        title: aonMessengerChat.task.id ? "#"+(data.number  || "0").toString().padStart(5,0) : MSG.NEW_REQUEST
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
        setTimeout(() => {
            aonMessengerChat.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST);
        }, 250);
    });

    
    /**
     * The chat itself
     */
    const chat = newComponent({
        type: 'chat',
        id: MESSENGER_IDS.MESSENGER_CHAT,
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


    const title = createTitle(data.title);
    setStyles(title.element,{
        display : 'block',
        fontSize: '1.3em',
        paddingTop: "10px",
        paddingBottom: "10px",
        width : '100%',
        background : "#fff",
        borderBottom : "1px solid " + CSS.variable(COLORS.AON_LIGHT_GRAY)
    });
    title.appendTo(chat.element);

    buildMobileWritter(wrapper, aonMessengerChat);

    setTimeout(() => {
        setStyles(mainView, {
          opacity: 1,
          marginTop: 0,
        });
      }, 100);
}

/**
 * Create desktop chat for messenger
 * @param {*} parent 
 * @param {*} data 
 */
export const buildDesktopChat = (aonMessengerChat) => {
    const mainView = document.getElementById(MESSENGER_IDS.MAIN_DIV);
    
    const firstDiv = newComponent({
        classes: [CSS.FLEX_ROW],
        styles: {
            width: "50%",
            height: "90%",
            minWidth: "400px",
            maxWidth: "600px",
            paddingTop: "5vh",
            paddingRight: "40px",
            paddingLeft: "40px",
            display: !aonMessengerChat.task.id  ? "none" : null
        },
    }).element;

    mainView.appendChild(firstDiv);

    /**
     * Wrapper 
     * if some new side menus / toolbars needed, here.
     */
    const wrapper = newComponent({
        type: MESSENGER_COMPONENTS.WRAPPER,
        classes: [CSS.FLEX_COLUMN],
        styles: {
            width: "90%",
            height: '100%',
        }
    });
    wrapper.appendTo(firstDiv);

    const title = createTitle(MSG.COMMENTS);
    setStyles(title.element, {
            maxWidth: '550px',
            alignSelf: 'center',
            paddingBottom: '10px',
            borderBottom: '1px solid #f0f0f0',
        }
    );
    title.appendTo(wrapper.element);
    
    /**
     * The chat itself
     */
    const chat = newComponent({
        type: MESSENGER_COMPONENTS.CHAT,
        id: MESSENGER_IDS.MESSENGER_CHAT,
        classes: ["continueLined", CSS.FLEX_COLUMN, CSS.MATERIAL_SCROLL, CSS.FLEX_ALIGN_CENTER],
        styles: {
            position: "relative",
            width: '100%',
            zIndex: "0",
            "scroll-behavior": "smooth",
            height: '100%',
            padding: "20px",
            paddingTop: "20px",
            overflow: 'auto',
            borderBottom: '1px solid #f0f0f0',
        }
    });
    chat.appendTo(wrapper.element);

    /**
     * A side buttonbar 
     */
    const leftButtonBar = newComponent({
        classes: [CSS.FLEX_COLUMN, CSS.FLEX_JUSTIFY_END],
        styles: {
            position: 'relative',
            width: "10%",
            height: "100%"
        }
    });
    leftButtonBar.appendTo(firstDiv);

    const upIcon = setAttributes(new AonIconButton(), {
        icon: MATERIAL_ICONS.EXPAND_LESS,
        id: "upIcon",
        background: "transparent",
    });
    upIcon.onclick = () => chat.element.scrollTo(0,0);
    leftButtonBar.appendChild(upIcon);

    const downIcon = setAttributes(new AonIconButton(), {
        icon: MATERIAL_ICONS.EXPAND_MORE,
        id: "downIcon",
        background: "transparent",
    });
    downIcon.onclick = () =>  chat.element.scrollTo(0, chat.element.scrollHeight);
    leftButtonBar.appendChild(downIcon);

     /**
     * Setting the chat line once all is rendered
     * DO NOT change this, is compulsory.
     */
    const lined = document.querySelector(".continueLined");
    if (lined) lined.style.setProperty("--height", lined.scrollHeight + "px");
    setTimeout(() => {
      mainView.style.opacity = 1;
      mainView.style.marginTop = 0;
    }, 100);
}

/**
 * Append a new message to the chat with a little animation
 * @param {*} properties 
 */
export const appendChatMessage = (properties) => {
    const noMessage = document.getElementById(MESSENGER_IDS.NO_MESSAGES);
    const chat = document.querySelector(MESSENGER_COMPONENTS.CHAT);
    
    if(noMessage)
        chat.removeChild(noMessage);

    const message = createChatMessage(properties, chat).element;
    setStyles(message,{
        opacity : 0,
        marginTop : '20px',
        transition : ".25s"
    });

    /**
     * Appearing animation
     */
    setTimeout(() => {
        setStyles(message, {
            opacity : 1,
            marginTop : '10px'
        });
    }, 100);
} 

/**
 * Create a new message
 * @param {*} properties 
 * @returns 
 */
export const createChatMessage = (properties, chat) => {
    properties = checkProperties(properties);

    const message = createMessageBox(properties);
   
    const name = createMessageAuthor(properties);
    name.appendTo(message.element);

    const description = createCommentContent(properties);
    description.appendTo(message.element);

    const date = createText({
        text: setDateTimestampDay(new Date(properties.date)),
        color: CSS.variable(COLORS.AON_GRAY),
        fontSize : '.7em',
        classes: [CSS.FIRST_LETTER_UPPER]
    });
    date.appendTo(name.element);

    message.appendTo(chat); //ADD MESSAGE IN DIV CHAT

    checkFilesAddEventClick(message.element); //ADD EVENT CLICK

    return message;
}

export const changeStyleSelect = (aonSelect) => {
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
        aonSelectGroup.style.marginBottom = "0px"
}

/**
 VIEW CREATE TASK MOBILE
 * @param {*} wrapper 
 * @param {*} toolbar 
 * @param {*} aonMessengerChat 
 */
const buildMobileChatCreate = (wrapper, toolbar, aonMessengerChat)=>{
    const application = aonMessengerChat.applicationEl;
    const data = aonMessengerChat.task;
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
    const titleIn = setStyles( createDivEditable(data.title, MESSENGER_IDS.TITLE_TASK, MSG.WRITE_YOUR_TITLE), {
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
    fillWorkGroup(data, application);
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

//FILL WORKGROUP
export const fillWorkGroup = async ({workgroup, task_holder}, application) => {
    try {
        const aonSelect = await waitEl(`#${MESSENGER_IDS.WORKGROUP}`);
        aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
            if(detail && detail.value)
                fillTaskHolder(detail.value);
        })
        const workgroups = application.getParent()._workgroups;
        if(workgroups && workgroups.length>0){
            aonSelect.options = JSON.stringify(
                workgroups.map( wg=> ({...wg, id: wg.value}) )
            );
        }
        if(workgroup && workgroup.id){
            aonSelect.value = workgroup.id;

            if(task_holder && task_holder.id) 
                fillTaskHolder(workgroup.id, task_holder.id);
        } 
       
    } catch (error) { console.log(error);}
}

//FILL TASKHOLDERS
const fillTaskHolder = async (workgroupId, taskHolderId=undefined) => {
    try {
        const aonSelect = await waitEl(`#${MESSENGER_IDS.TASKHOLDER}`);
        if(aonSelect){
            aonSelect.clear();
            const taskHolders = await getTastHoldersWorkGroup({workgroupId});
            if(taskHolders){
                aonSelect.options = JSON.stringify(
                    taskHolders.map( th=> ({...th, value: th.id}) )
                );
            }
            if(taskHolderId) aonSelect.value = taskHolderId;
        }
    } catch (error) {console.log(error);}
}

//FILL CHAT
export const fillChat = (workflows=[])=>{
    waitEl(`#${MESSENGER_IDS.MESSENGER_CHAT}`).then(chat=>{
        if(workflows.length == 0){
            let noMessage = newComponent({
                type : MESSENGER_COMPONENTS.ADVICE,
                id : MESSENGER_IDS.NO_MESSAGES,
                text : 'No hay mensajes en esta solicitud',
                styles : {
                    fontSize : '1em',
                    color : CSS.variable(COLORS.GRAYSON),
                }
            });
            noMessage.appendTo(chat);
        } else {
            const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
            const applicationParent = aonMessengerChat.getApplicationParent();
    
            const meId = applicationParent.SENDER.id;
    
            workflows.forEach(workflow => {
                const {id, comment, type, modification_date, task_holder:{name, alias, id:taskHolderId}} = workflow;
                const me = taskHolderId == meId; // if taskHolder id is me
                const message = {
                    id,
                    type,
                    comment,
                    name: me ? "Yo" : alias,
                    direction: me ? RIGHT: LEFT,
                    date: new Date(modification_date)
                }
                if (type == WORKFLOW_TYPES.COMMENT) {
                    createChatMessage(message, chat);
                } else{
                    message.name = name;
                    const actionJson = chooseIconMessage(message);
                    const action = createAction(actionJson, actionJson.comment);
                    action.appendTo(chat);
                }
            });
        }
   })
}

/**
 * Choose icon for the actions
 * @param {Object} message
 * @returns {Object} actionJson message new object
 */
 const chooseIconMessage = ({type, date, name, comment}) => {
    const dateParse = setFullDate(date) + " " + setTime(date);
    
    let actionJson = {
        icon : MATERIAL_ICONS.INFO,
        type : ICON_TYPES.MATERIAL_OUTLINED,
        color : CSS.variable(COLORS.MATERIAL_BLUE),
        comment: `${WORKFLOW_TYPE(type)} por <b>${name ? name : null}</b> ${dateParse}`
    }

    if(WORKFLOW_TYPES.OPEN.indexOf(type)>=0){
        actionJson.color = CSS.variable(COLORS.ONLINE_GREEN);
    }  else if(WORKFLOW_TYPES.CLOSE.indexOf(type)>=0){
        actionJson.icon = MATERIAL_ICONS.CLOSE;
    }  else if(WORKFLOW_TYPES.ASSIGN.indexOf(type)>=0){
        actionJson.comment = `${WORKFLOW_TYPE(type)} por <b>${name ? name : null}</b> a <b>${comment}</b> ${dateParse}`;
    }

    return actionJson;
}

/**
 * 
 * @param {HTMLElement} parent check html and add event 
 * @param {*} json 
 */
const checkFilesAddEventClick = (parent)=>{
    const elements = parent.querySelectorAll(`[${CONSTANT.TYPE}=${CONSTANT.AON_FILE}]`);
    for (const element of elements) {
        const url = element.src || element.href;
        if(url) {
            element.addEventListener(EVENT.CLICK, (ev)=>{
                ev.preventDefault();
                openFileUrl(url);
            });
        } 
    }
}