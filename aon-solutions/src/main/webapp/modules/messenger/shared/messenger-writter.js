import { AonToolbar } from "../../../components/aon-toolbar.js";
import { API_URL, COLORS, CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from "../../../environments/environments.js";
import { ToolbarType } from "../../../models/enums.js";
import { domainName } from "../../../services/request.js";
import { newComponent, setAttributes, setStyles } from "../../../services/utils.js";
import * as ACTIONS from "../../actions.js";
import {  createButtonWrapper, createDivEditable, createReceiverDiv, createSendBar, createSendButton, createSendIcon, createUpload, createUploadIcon, createUploadText } from "../createComponents.js";
import { MESSENGER_COMPONENTS, MESSENGER_IDS, MESSENGER_VIEWS, WORKFLOW_TYPES } from "../MessengerEnums.js";
import { createAonTextArea, createStartJustifiedColumn, createTaskHolder, createWorkgroup, RIGHT } from "./creationUtils.js";
import { appendChatMessage, fillWorkGroup } from "./messenger-chat.js";
// import { bold, compileHTML, italic, link, list, tab } from "./markup.js";


/**
 * Build desktop version of the writter 
 * @param {HTMLElement} mainView htmlElement div principal
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 */
export const buildDesktopWritter = (mainView, aonMessengerChat) => {
    const data = aonMessengerChat.task;
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

    /**
     * Building title
     */
    const titleDiv = createStartJustifiedColumn();
    setStyles(titleDiv.element ,{ width : "100%" });
    titleDiv.element.style.marginBottom = "5px";
    //TITLE
    const title = createDivEditable(data.title, MESSENGER_IDS.TITLE_TASK, MSG.WRITE_YOUR_TITLE);
    titleDiv.element.appendChild(title);
    titleDiv.appendTo(writter.element);

    const receiverDiv = createReceiverDiv();
    receiverDiv.appendTo(writter.element);
    
    receiverDiv.element.appendChild(createWorkgroup());
    fillWorkGroup(data, application);

    //-----------------TASK HOLDER
    const taskHolderSelect = createTaskHolder();
    taskHolderSelect.style.marginLeft = "5px";
    receiverDiv.element.appendChild(taskHolderSelect);


    const aonTextArea = createAonTextArea(aonMessengerChat.task.id ? `${MSG.WRITE_A_COMMENT}...` : `${MSG.WRITE_A_DESCRIPTION}...`);

    writter.appendChild(aonTextArea);
    buildTextareaToolbar(aonTextArea);

    if(aonMessengerChat.task.id){ //UPDATE
        /**
         * Creating send bar
         */
        const sendBar = createSendBar();
        sendBar.appendTo(writter.element);

        const upload = createUpload();
        upload.appendTo(sendBar.element);
        
        addButtonFileSend(upload.element);
        
        const uploadIcon = createUploadIcon();
        uploadIcon.appendTo(upload.element);

        const sendButtonWrapper = createButtonWrapper();
        sendButtonWrapper.appendTo(sendBar.element);
    
        const uploadText = createUploadText();
        uploadText.appendTo(upload.element);


        //BUTTON SEND COMMENT
        const sendButton = createSendButton();
        sendButton.addEventListener(EVENT.CLICK,()=> aonMessengerChat.saveTaskWorkflow());
        sendButtonWrapper.appendChild(sendButton);
        const sendIcon = createSendIcon();
        sendIcon.appendTo(sendButton);
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
    buildTextareaToolbar(textarea);

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
    const data = aonMessengerChat.task;
    const message = {
        type: WORKFLOW_TYPES.COMMENT,
        sender: "Yo",
        comment: value,
        creation_date: new Date()
    }


    aonTextArea.clear();
    
    data.workflow.push(message);

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

    aonMessengerChat.data = data;

    const chat = document.querySelector(MESSENGER_COMPONENTS.CHAT);
    chat.scrollTo(0, chat.scrollHeight); //GO DOWN

    return value;
}

/**
 * Build standard toolbar options 
 * @param {*} aonTextArea 
 */
 export const buildTextareaToolbar =  (aonTextArea) => {
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
        },
        () => documentExec("bold")
    );

    /**
     * Italic format buttton _italic_
     */
    aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.FORMAT_ITALIC,
        icon: MATERIAL_ICONS.FORMAT_ITALIC,
    },() =>  documentExec("italic"));
    
        /**
     * List bulleted button - listItem
     */
     aonTextArea.addToolbarOptionRight({
        id: MATERIAL_ICONS.FORMAT_LIST_BULLETED,
        icon: MATERIAL_ICONS.FORMAT_LIST_BULLETED,
    },() => documentExec("insertOrderedList") );

    /**
     * Link send button [Name](url)
     */
    aonTextArea.addToolbarOptionRight({
        id: MATERIAL_ICONS.LINK,
        icon: MATERIAL_ICONS.LINK,
    },() => createLink());

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

/**
 * 
 * @param {HTMLElement} upload div button and file send
 */
const addButtonFileSend = (upload)=>{
    const textArea = document.getElementById(MESSENGER_IDS.COMMENT_TASK);
    
    const highlight = ()   => upload.classList.add('highlight');
    const unhighlight = () => upload.classList.remove('highlight');
    [EVENT.DRAGENTER, EVENT.DRAGOVER].forEach(eventName => upload.addEventListener(eventName, highlight, false));
    [EVENT.DRAGLEAVE, EVENT.DROP].forEach(eventName => upload.addEventListener(eventName, unhighlight, false));

    upload.addEventListener(EVENT.DROP, (ev) => {
        if(ev && ev.dataTransfer && ev.dataTransfer.files){
            textArea.addFiles(ev.dataTransfer.files);
        }
    });
    
    //ADD INPUT
    let inputFile = setAttributes(document.createElement(TAG.INPUT),{
        id:MESSENGER_IDS.INPUT_FILES,
        type:'file',
        name:'file',
        multiple:true
    });        
    inputFile.style.display = "none";
    inputFile.addEventListener(EVENT.CHANGE, () => textArea.addFiles(inputFile.files));
    upload.appendChild(inputFile);

    upload.addEventListener(EVENT.CLICK,()=> inputFile.click());
}

/**
 * 
 * @param {HTMLElement} textArea htmlElement textArea
 * check files and send uploadFile(taskAttach) 
 */
const checkFilesAndSend = async (textArea)=>{
    const buttonSend = document.getElementById(MESSENGER_IDS.BUTTON_SEND);
    buttonSend.disabled = true;
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
    buttonSend.disabled = false;
}