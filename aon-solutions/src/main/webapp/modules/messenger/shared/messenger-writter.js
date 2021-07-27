import { AonToolbar } from "../../../components/aon-toolbar.js";
import { API_URL, COLORS, CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG } from "../../../environments/environments.js";
import { ToolbarType } from "../../../models/enums.js";
import { domainName } from "../../../services/request.js";
import { newComponent, setAttributes, setStyles } from "../../../services/utils.js";
import * as ACTIONS from "../../actions.js";
import { createButtonWrapper, createDivEditable, createReceiverDiv, createSendBar, createSendButton } from "../createComponents.js";
import { MESSENGER_COMPONENTS, MESSENGER_IDS, MESSENGER_VIEWS, WORKFLOW_TYPES } from "../MessengerEnums.js";
import { createAonTextArea, createStartJustifiedColumn, createTaskHolder, createWorkgroup, RIGHT } from "./creationUtils.js";
import { appendChatMessage, fillWorkGroup } from "./messenger-chat.js";

/**
 * Build desktop version of the writter 
 * @param {HTMLElement} mainView htmlElement div principal
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 */
export const buildDesktopWritter = (mainView, aonMessengerChat) => {
    const task = aonMessengerChat.task;
    const application = aonMessengerChat.applicationEl;

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
    writter.appendTo(mainView);

    const titleDiv = createStartJustifiedColumn();
    setStyles(titleDiv.element ,{ width : "100%", marginBottom: "5px" });


    //TEST SPAN
    let span = setStyles(document.createElement("span"),{
        fontSize: "0.9375rem",
        width:"100%",
        color:CSS.variable(COLORS.AON_COLOR_INK_MEDIUM_CONTRANST)
    });
    span.textContent = MSG.ISSUE;
    titleDiv.appendChild(span);

    //TITLE
    const title = createDivEditable(task.title, MESSENGER_IDS.TITLE_TASK, MSG.ISSUE);
    titleDiv.appendChild(title);
    titleDiv.appendTo(writter.element);

    const receiverDiv = createReceiverDiv();
    receiverDiv.appendTo(writter.element);

     //-----------------WORKGROUP
    const workgroupSelect = createWorkgroup();
    workgroupSelect.style.width = "100%";
    receiverDiv.appendChild(workgroupSelect);
    // document.getElementById(workgroupSelect.INPUT).style.fontSize = "14px";
    fillWorkGroup(task, application);

    //-----------------TASK HOLDER
    const taskHolderSelect = createTaskHolder();
    taskHolderSelect.style.marginLeft = "5px";
    taskHolderSelect.style.width = "100%";
    receiverDiv.appendChild(taskHolderSelect);
    // document.getElementById(workgroupSelect.INPUT).style.fontSize = "14px";

    const aonTextArea = createAonTextArea(aonMessengerChat.task.id ? `${MSG.WRITE_A_COMMENT}...` : `${MSG.WRITE_A_DESCRIPTION}...`);

    writter.appendChild(aonTextArea);
    buildTextareaToolbar(aonTextArea, task);

    if(aonMessengerChat.task.id){ //UPDATE
        /**
         * Creating send bar
         */
        const sendBar = createSendBar();
        sendBar.appendTo(writter.element);


        const sendButtonWrapper = createButtonWrapper();
        sendButtonWrapper.appendTo(sendBar.element);

        //BUTTON SEND COMMENT
        const sendButton = createSendButton();
        sendButton.addEventListener(EVENT.CLICK,()=> aonMessengerChat.saveTaskWorkflow());
        sendButtonWrapper.appendChild(sendButton);
    }
}

/**
 * Build mobile version of the writter 
 * @param {HTMLElement} wrapper 
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat component 
 */
export const buildMobileWritter = (wrapper, aonMessengerChat) => { 
    const writter = newComponent({
        id : MESSENGER_COMPONENTS.WRITTER,
        classes: [CSS.FLEX_COLUMN],
        styles: {
            height: '100%',
            width: '100%',
            background: COLORS.AON_WHITE,
            position: 'absolute',
            display: 'none',
            top: '0%',
            opacity: 0,
            transition: '.5s',
            zIndex : -9
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
    bar.addButton2(ACTIONS.BACK,() => {
        hideWritter();
    });

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
    let button = setStyles(document.getElementById(MESSENGER_IDS.ADD_ICON_BUTTON) , {
        transition : "0.25s",
        opacity : "1"
    });
    setTimeout(() => button.style.display = "block", 100);

    setTimeout(() => {
        setStyles(document.getElementById(MESSENGER_COMPONENTS.WRITTER),{
          zIndex : -9,
          opacity : 0,
        });
    }, 100);
}

/**
 * Send message to chat
 * @param {*} aonTextArea - The mensaje source
 * @returns void.
 */
export const sendMessage = async (aonTextArea) => {
    await checkFilesAndSend(aonTextArea); //CHECK FILES COMMENT AND SEND

    const value =  aonTextArea.value;
    if(!value || (value && !value.trim().length)) return ;

    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    const task = aonMessengerChat.task;
    const message = {
        type: WORKFLOW_TYPES.COMMENT,
        sender: "Yo",
        comment: value,
        creation_date: new Date()
    }

    aonTextArea.clear();
    
    task.workflow.push(message);

    appendChatMessage({
        id: "id",
        name: message.sender,
        comment:message.comment,
        date: message.creation_date,
        direction : RIGHT
    });

    /**
     * Setting the chat line once all is rendered
     * DO NOT change this, is compulsory.
     */
    const lined = document.querySelector(".continueLined");
    if (lined)
        lined.style.setProperty("--height", lined.scrollHeight + "px");

    aonMessengerChat.data = task;

    const chat = document.querySelector(MESSENGER_COMPONENTS.CHAT);
    chat.scrollTo(0, chat.scrollHeight); //GO DOWN

    return value;
}

/**
 * Build standard toolbar options 
 * @param {*} aonTextArea 
 */
 export const buildTextareaToolbar =  (aonTextArea, task) => {
    const textAreaText = aonTextArea.querySelector("#" + aonTextArea.TEXTAREA);
    if(textAreaText){
        setStyles(textAreaText, {
            resize: "none",
            height: "100%",
            minHeight: "300px",
            maxHeight: "300px"
        });
    }
    /**
     * Bold format button **bold**
     */
    aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.FORMAT_BOLD,
        icon: MATERIAL_ICONS.FORMAT_BOLD,
        name:MSG.BOLD
    },() => documentExec("bold"));

    /**
     * Italic format buttton _italic_
     */
    aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.FORMAT_ITALIC,
        icon: MATERIAL_ICONS.FORMAT_ITALIC,
        name:"Cursiva"
    },() =>  documentExec("italic"));
    
        /**
     * List bulleted button - listItem
     */
     aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.FORMAT_LIST_NUMBERED,
        icon: MATERIAL_ICONS.FORMAT_LIST_NUMBERED,
        name:"Lista numerada"
    },() => documentExec("insertOrderedList") );

    /**
     * Link send button [Name](url)
     */
    aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.LINK,
        icon: MATERIAL_ICONS.LINK,
        name:"Insertar enlace"
    },() => createLink());

    //UNDERLINED
    aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.FORMAT_UNDERLINED,
        icon: MATERIAL_ICONS.FORMAT_UNDERLINED,
        name:"Subrayado"
    },() =>documentExec('underline'));

    //FORMAT_QUOTE
    aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.FORMAT_QUOTE,
        icon: MATERIAL_ICONS.FORMAT_QUOTE,
        name:"Cita"
    },() =>blockquote());


    if(task && task.id){
        //ATTACH
        aonTextArea.addToolbarOptionLeft({
            id: MATERIAL_ICONS.ATTACH_FILE,
            icon: MATERIAL_ICONS.ATTACH_FILE,
            name:MSG.ADD_FILE
        },() =>{});


        if(!aonTextArea.isMobile()){
            const iconSend = aonTextArea.addToolbarOptionRight({
                id: MATERIAL_ICONS.SEND,
                icon: MATERIAL_ICONS.SEND,
                name:MSG.SEND,
            },() =>aonMessengerChat.saveTaskWorkflow());
            if(iconSend) iconSend.style.color = CSS.variable(COLORS.MATERIAL_BLACK);
        }
    }
}

const documentExec = (exec) => document.execCommand(exec) ? document.execCommand("normal") : document.execCommand(exec);

const createLink =() =>{
    const selection = document.getSelection();
    if(selection && selection.toString().trim()){
        const url = prompt('URL:', 'https://');
        const aEl = setAttributes(document.createElement("a"),{
            target:"_blank",
            class:CSS.AON_LINK,
            href:url
        });
        aEl.textContent = selection;
        document.execCommand('insertHTML', false, aEl.outerHTML);
    }
}

const blockquote = ()=>{
    const selection = document.getSelection();
    const blockquoteEl = setStyles(document.createElement("blockquote"),{
        margin:"0px 0px 0px 0.8ex",
        borderLeft: "1px solid rgb(204, 204, 204)",
        paddingLeft: "1ex"
    });
    blockquoteEl.textContent = selection;
    document.execCommand('insertHTML', false, blockquoteEl.outerHTML);
}

/**
 * 
 * @param {HTMLElement} textArea htmlElement textArea
 * check files and send uploadFile(taskAttach) 
 */
const checkFilesAndSend = async (textArea)=>{
    const btnSend = document.getElementById(MESSENGER_IDS.BUTTON_SEND);
    if(btnSend)btnSend.disabled = true;
    try {
        const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
        const textAreaDiv = textArea.getTextAreaDiv();
        const elements = textAreaDiv.querySelectorAll(`[${CONSTANT.TYPE}=${WORKFLOW_TYPES.AON_FILE}]`);
        const {id:taskId} = aonMessengerChat.task;
        const files = textArea.FILES;
        for await (const el of elements) {
            const fileId = el.dataset.id;
            const file = files.find(({id})=> id == fileId);
            if(file){
                const taskAttach = await aonMessengerChat.uploadFile({ file, taskId });
                if(taskAttach){
                    const json = {
                        domain_name: domainName(),
                        attach_type:"task",
                        domain_id:taskAttach.domain,
                        id:taskAttach.id
                    };
                    const jsonBase64 = btoa( JSON.stringify(json) );
                    
                    const linkTmp = `/${API_URL}/file/${jsonBase64}`;
    
                    if(file.contentType.indexOf("image")>=0)
                        el.src = linkTmp;
                    else 
                        el.href = linkTmp;
                }
            }
        }
    } catch (error) { console.log(error); }
    if(btnSend)btnSend.disabled = false;
}