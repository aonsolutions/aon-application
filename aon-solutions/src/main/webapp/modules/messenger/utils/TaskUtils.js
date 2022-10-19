import { AON_ICONS, API_URL, COLORS, CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, SIG_URL, TAG } from "../../../environments/environments.js";
import { openFileUrl } from "../../../services/fileService.js";
import { setAttributes, setClasses, setDataset, setStyles } from "../../../services/utilsComponents.js";

import { MessengerOptions, MESSENGER_COMPONENTS, MESSENGER_DIRECTION, MESSENGER_IDS, MESSENGER_VIEWS, TAG_TYPE, TASK_SOURCE, TASK_STATUS, WORKFLOW_TYPE, WORKFLOW_TYPES } from "../MessengerEnums.js";
import { TaskCreationUtils } from "./TaskCreationUtils.js";
import { TaskFill } from "./TaskFill.js";
import { AonCheckbox } from "../../../components/aon-checkbox.js";
import { FormMovSs } from "../forms/FormMvSs.js";
import { FormTimecontrol } from "../forms/FormTimecontrol.js";
import { FormVacation } from "../forms/FormVacation.js";
import { AonDateUtils } from "../../utils/AonDateUtils.js";
import * as ACTIONS from "../../actions.js";
import { MessengerChat } from "./MessengerChat.js";
import { AonIcon } from "../../../components/aon-icon.js";

/**
 * Build standard toolbar options 
 * @param {HTMLElement} aonTextArea aon-text-area
 */
const buildTextareaToolbar =  (aonTextArea) => {
    const textAreaText = aonTextArea.getTextArea();
    if(textAreaText){
        setStyles(textAreaText, {resize: "none"});
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


    //ATTACH
    aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.ATTACH_FILE,
        icon: MATERIAL_ICONS.ATTACH_FILE,
        name:MSG.ADD_FILE
    },() =>{});

    aonTextArea.addColorPicker(MATERIAL_ICONS.FORMAT_LIST_NUMBERED);
}

const addIconToolbar = (toolbar, task) => {
    const titleSpan = setClasses(toolbar.querySelector( `.${CSS.AON_SECONDARY_TOOLBAR_TITLE}` ), [CSS.FLEX_ROW, CSS.FLEX_ALIGN_CENTER]);
    if(titleSpan){
        const iconJson = getIconJson(task);
        let iconEl = null;

        if(task.parent){
            iconEl = new AonIcon();
            iconEl.icon = AON_ICONS.AON_BRANCH;
            iconEl.title = "Branch";
            iconEl.color = iconJson.icon_color;
        } else {
            iconEl = TaskCreationUtils.createOutlinedMaterialIcon({
                name:  iconJson.icon,
                color: iconJson.icon_color,
                size: "20px"
            });
            iconEl.style.marginLeft = "10px";
            iconEl.style.marginTop = "-1px";
        }

        titleSpan.appendChild(iconEl);
    }
}

/**
 * 
 * @param {Object} source,status 
 * @returns icon, icon_color
 */
const getIconJson =({source,status}) => {

    const { AON_MESSENGER_LIST_OPEN, AON_MESSENGER_LIST_IN_PROGRESS, AON_MESSENGER_LIST_CLOSE, AON_MESSENGER_LIST_ARCHIVE } = MessengerOptions;

    let icon = MATERIAL_ICONS.INFO;
    let icon_color = AON_MESSENGER_LIST_OPEN.icon_color;

    if(source === TASK_SOURCE.CAU){
        icon = MATERIAL_ICONS.SUPPORT_AGENT;
    } else if(source===TASK_SOURCE.REQUEST){
        icon = MATERIAL_ICONS.ASSIGNMENT;
    } else if(source===TASK_SOURCE.GROUPED){
        icon = "group_add";
    }  
    
    if(status === TASK_STATUS.IN_PROGRESS) {
        icon_color = AON_MESSENGER_LIST_IN_PROGRESS.icon_color;
    } else if(status === TASK_STATUS.FINISHED) {
        icon_color = AON_MESSENGER_LIST_CLOSE.icon_color;
    } else if(status === TASK_STATUS.DELETED){
        icon_color = AON_MESSENGER_LIST_ARCHIVE.icon_color;
    } 
    
    return {
      icon,
      icon_color
    }
}

/**
 * downChat down chat
 */
const downChat = () => {
    const chat = document.getElementById(MESSENGER_IDS.MESSENGER_CHAT);
    if(chat){
        setTimeout(() =>{
            chat.lastChild.scrollIntoView(); 
            addLine(chat);
        }, 100) 
    }
}
/**
 * upChat up chat
 */
const upChat = () => {
    const chat = document.getElementById(MESSENGER_IDS.MESSENGER_CHAT);
    if(chat){
        setTimeout(() => chat.firstChild.scrollIntoView(), 100)    
    }
}

const dialogTaskTags = (ev, task) => {
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    const rect = ev.target.getBoundingClientRect();
    const x = ev.clientX - rect.left + 180;
    const y = ev.clientY - rect.top;
    const top  = rect.top + y;
    let left = rect.left + x;

    const dialog = aonMessengerChat.getApplication().getOptionDialog();
    dialog.clear();

    const div = setStyles(document.createElement("div"),{ margin:"5px", display:"flex", flexDirection:"column" });

    const tags = aonMessengerChat.getTagsPanel();
    
    const taskTags = task.getTags();

    for (let tag of tags) {
        let aonCheckbox = new AonCheckbox();
        aonCheckbox.description = tag.name;
        aonCheckbox.id = tag.name;
        aonCheckbox.checked = taskTags.find(t=>t.id ===tag.id || tag.name===t.name  ) ? true : false;
        aonCheckbox.addEventListener(EVENT.CHANGE, ({target})=>{
          if(target.checked){
            task.addTag(tag);
          } else {
            task.removeTag(tag.id);
          }
        })
        div.appendChild(aonCheckbox);
    }

    dialog.setContent(div, top, left);
    dialog.open();
}

/**
 * create div tags
 */
const setTaskTags = (task) => {
    const div = document.getElementById(MESSENGER_IDS.DIV_TASK_TAGS);
    div.innerHTML = "";
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);

    task.getTags()
    .filter(t=>t.tag_type && t.tag_type.toUpperCase() == TAG_TYPE.TASK_LABEL)
    .forEach(tag=>{
        if(task.id && task.isExternal() || aonMessengerChat.isCau() || aonMessengerChat.isMobile()){
            TaskCreationUtils.createTagHtml(tag, div);
        } else {
            TaskCreationUtils.appendTaskTag(tag, div, (id)=>  task.removeTag(id));
        }
    });
}

/**
 * Choose icon for the actions
 * @param {Object} message
 * @returns {Object} actionJson message new object
 */
const chooseIconMessage = ({type, date, name, comment, number}) => {
    const dateParse = date ? (AonDateUtils.setFullDate(date) + " " + AonDateUtils.setTime(date)) : null;
    
    let actionJson = {
        icon : MATERIAL_ICONS.INFO,
        type : CONSTANT.MATERIAL_OUTLINED,
        color : CSS.variable(COLORS.MATERIAL_BLUE),
        comment: `${WORKFLOW_TYPE(type)} por <b>${name ? name : null}</b> ${dateParse}`,
    }

    if(WORKFLOW_TYPES.OPEN.includes(type) || WORKFLOW_TYPES.REOPEN.includes(type))
        actionJson.color = CSS.variable(COLORS.ONLINE_GREEN);
    else if(WORKFLOW_TYPES.CLOSE.includes(type)){
        actionJson.icon = MATERIAL_ICONS.CHECK_CIRCLE_OUTLINE;
        actionJson.color = CSS.variable(COLORS.MATERIAL_RED);
    } else if(WORKFLOW_TYPES.DELETE.includes(type)){
        actionJson.icon = MATERIAL_ICONS.ARCHIVE;
        actionJson.color = CSS.variable(COLORS.GRAYSON);
    } else if(WORKFLOW_TYPES.ASSIGN.includes(type)){
        actionJson.comment = `${WORKFLOW_TYPE(type)} por <b>${name ? name : null}</b> a <b>${comment}</b> ${dateParse}`;
    } else if(WORKFLOW_TYPES.CONNECTED.includes(type)){
        const numberStr = taskNumberParse(comment);
        actionJson.comment = `${WORKFLOW_TYPE(type)} con <b>${numberStr}</b> ${dateParse}`;
    } 

    if(number){
        actionJson.icon = MATERIAL_ICONS.FORK_LEFT;
        actionJson.title = number;
        //     const fork = TaskCreationUtils.createAction({
        //         icon:MATERIAL_ICONS.FORK_LEFT,
        //         color: CSS.variable(COLORS.MATERIAL_BLUE),
        //         type: CONSTANT.MATERIAL_OUTLINED,
        //         title: number
        //     }, undefined, undefined, false).element;
        //     actionJson.comment = `${fork.outerHTML} ${actionJson.comment}`;
    }

    return actionJson;
}
/**
 * @param {String} text Optional
 * @param {HTMLElement} aon-messenger-chat 
 * @returns {Obkect} {value, messageEl, workflowId} message element html
 */
const sendMessage = async (text, task) => {
    let workflowId = undefined;
    let taskId     = undefined;
    let messageEl  = undefined;
    let value      = text;

    const isSend = task.gtask_id;

    let textArea = document.getElementById(MESSENGER_IDS.COMMENT_TASK);

    if(!text){
        await checkFilesAndSave(task, textArea); //CHECK FILES COMMENT AND SEND
        value = textArea.value;
        textArea.clear();
    }

    if(textArea.hasAttribute("data-workflow-id")){
        taskId = parseInt(textArea.getAttribute("data-task"));
        textArea.removeAttribute("data-task-id");

        workflowId = parseInt(textArea.getAttribute("data-workflow-id"));
        textArea.removeAttribute("data-workflow-id");
    } 
    
    if(!value || (value && !value.trim().length)) return {};

    if(!workflowId){ //append HTML
        const message = {
            type: WORKFLOW_TYPES.COMMENT,
            sender: "",
            comment: value,
            creation_date: new Date(),
            task:task.id
        }
    
        task.workflow.push(message);

        messageEl = appendChatMessage({
            id: "id",
            name: message.sender,
            comment:message.comment,
            date: message.creation_date,
            task:message.task,
            direction : MESSENGER_DIRECTION.RIGHT,
            me: true,
            isSend
        });
    }

    return{
        comment:value, 
        messageEl,
        workflowId,
        taskId
    } 
}

/**
 * 
 * @param {Number} id 
 * @param {HTMLElement} parent check html and add event 
 */
const checkFilesAddEventClick = ({id}, parent)=>{
    new Promise(r => setTimeout(r, 1)).then(()=>{
        const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
        parent.querySelectorAll(`[${CONSTANT.TYPE}='${CONSTANT.AON_FILE}'], ${TAG.IMG}`)
        .forEach(element=>{
            const tagName = element.tagName;
            if(tagName && tagName.toLowerCase() === TAG.IMG){
                element.classList.add(CSS.AON_IMG_COMMENT);
            }

            let url = element.src || element.href;      
            if(url){
                if(id && aonMessengerChat.isCau()){
                    url = SIG_URL+url.substr(url.indexOf("/ms"));
                    if(element.src){
                        element.src = url;
                    } else if(element.href){
                        element.href = url;
                    }
                }

                element.addEventListener(EVENT.CLICK, (ev)=> {
                    ev.preventDefault();
                    openFileUrl(url);
                })
            }
        });
    });
}

/**
 * Description add event click img or file
 * @param {Task} task class task
 */
const checkFilesAddEventDescription = (task)=>{
    const descriptionEl = document.getElementById(MESSENGER_IDS.DESCRIPTION_TASK);
    if(task && descriptionEl){
        const observation = task.getDescriptionJson().observation;
        if(observation) {
            descriptionEl.setValueHtml(observation);
            checkFilesAddEventClick({id:task.getId()}, descriptionEl.getTextArea());
        }
    }
}

const taskNumberParse = (number) => "#"+(number || "0").toString().padStart(5, 0);

const parseTimeToDouble = (time)=>{
    if(time){
        const timeSplit = time.split(":");
        return parseFloat(timeSplit[0]) + parseFloat(timeSplit[1])/60;
    }
    return 0;
}

const parseDoubleToTime = (value)=>{
    if(value){
        const h = Math.floor(value);
        const m = Math.round((value - h) * 60);
        return `${h.toString().padStart(2, 0)}:${m.toString().padStart(2, 0)}`;
    }
    return null;
}

/**
 * 
 * @param {Task} task
 * @param {HTMLElement} divMain
 */
const buildForm = (task, divMain) => {
    const isAdvisoryCompany = task.isAdvisoryCompany();
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    // const dataDefault = aonMessengerChat.getData();

    //-----------------------APPEND DIV TAGS
    createTagsDiv(task, divMain);
    
    //-----------------------CREATE FIRST CARD
    const aonCard = TaskCreationUtils.createCardMessenger(MSG.DATA, "");
    aonCard.flex = "true";
    divMain.appendChild(aonCard);
    aonCard.getCard().style.margin = 0;

    if( !task.isExternal() && task.registry && task.registry.name && task.getSource() !== TASK_SOURCE.CAU){
        const registryName = `[${task.registry.name}]`;
        aonCard.setTitleSection1(registryName)
    }
    //-----------------------END CREATE FIRST CARD

    const divCard = document.createElement(TAG.DIV);
    aonCard.setContent(divCard);

    //-----------------------CREATE DIV PROCESS
    const processDiv = TaskCreationUtils.createStartJustifiedColumn().element;
    processDiv.id = MESSENGER_IDS.PROCESS_DIV;
    divMain.appendChild(processDiv);
    //---------------------END CREATE DIV PROCESS

    const divStatic = TaskCreationUtils.createDivGrid(divCard, undefined,{ classes:[CSS.AON_COL_XS_12], styles:{ padding:"0" } });

    const dinamicDiv = TaskCreationUtils.createDivGrid(undefined, undefined,{ classes:[CSS.AON_COL_XS_12], styles:{ padding:"0" } });
    dinamicDiv.id = MESSENGER_IDS.DINAMIC_DIV;

    divCard.appendChild(dinamicDiv);

    //-----------------TYPE REQUEST
    const requestTypeSelect = TaskCreationUtils.createRequestType();
  
    if(task.id) {
        requestTypeSelect.disabled = requestTypeSelect.readonly = true; // || dataDefault.source_id
    }
    
    const divRequest = TaskCreationUtils.createDivGrid(divStatic, requestTypeSelect, {classes:[CSS.AON_COL_XS_6]});
  
    //-----------------END TYPE REQUEST
    let btnForExternal = undefined;
    //---------------------IS CAU
    if(aonMessengerChat.isCau() || task.getSource() === TASK_SOURCE.CAU){
        if(aonMessengerChat.isCau()){
            task.setSource(TASK_SOURCE.CAU);
        }
        divRequest.style.display = 'none';
    } else //if(   (!task.id || task.isExternal()) && !( dataDefault.source_id && [1,3].includes(dataDefault.source_id) ))
    if((!task.id || task.isExternal()) && !aonMessengerChat.getDur().isEmployee()){

        let initText = !task.id || task.isExternal() ? 'Para' : 'De';
        let titleBtn = isAdvisoryCompany ?  `${initText} tu ${MSG.CUSTOMER}` : `${initText} tu Gestor`;

        btnForExternal = TaskCreationUtils.createAonSwitch(titleBtn);
        const divBtnForExternal = TaskCreationUtils.createDivGrid(divStatic, btnForExternal, {classes:[CSS.AON_COL_XS_6], styles:{ top:'16px',  marginLeft: "0", display:"none"}});
        btnForExternal.checked = task.isOtherDomain();
        if(task.id || task.isOtherDomain()) btnForExternal.disabled = CONSTANT.TRUE;

        btnForExternal.addEventListener(EVENT.CHANGE, ({target}) => {
            let forExternal = target.checked;
            onChangeTypeSelect(task, requestTypeSelect, forExternal);
        });

        if(!task.isAdvisoryCompany()){
            aonMessengerChat.getOfficeProjects().then(offices => {
                if(offices.length){
                    divBtnForExternal.style.display = "block";
                } else {
                    divRequest.className = CSS.AON_COL_XS_12;
                }
            });
        } else {
            divBtnForExternal.style.display = "block";
        }
    } else {
        divRequest.className = CSS.AON_COL_XS_12;
    }

    requestTypeSelect.addEventListener(EVENT.CHANGE, ()=>{
        let type = requestTypeSelect.getDetail().value;
        if(!task.id){
            if(btnForExternal){
                hideBtnExternal(type, btnForExternal, divRequest);
            }
            aonMessengerChat.setWhAndTh();
        }
        const forExternal = btnForExternal ? btnForExternal.isChecked() : false;
        onChangeTypeSelect(task, requestTypeSelect, forExternal);
    });

    TaskFill.fillRequestType(task);
}

/**
 * 
 * @param {Arrays} workflows workflows 
 */
const setStyleMessageHistoric = async (workflows) => {
    if(workflows && workflows.length) {
      for (const workflow of workflows) {
        const message = document.querySelector( `#${MESSENGER_IDS.MESSENGER_CHAT} ${MESSENGER_COMPONENTS.MESSAGE}[data-id='${workflow.id}']`);
        if(message){  //CHANGE STYLE IF SEND MESSAGE
          message.classList.add(CSS.MESSAGE_AFTER, "colorMe");
          const iconSendWorkflow = message.querySelector(`#${MESSENGER_IDS.ICON_SEND_WORKFLOW}`);

          if(iconSendWorkflow){
            iconSendWorkflow.title = "Enviado "+AonDateUtils.setDateTimestampDay(workflow.notification_date)
            iconSendWorkflow.innerText =  MATERIAL_ICONS.MARK_EMAIL_READ;
            iconSendWorkflow.style.color = CSS.variable(COLORS.ONLINE_GREEN);
            
            const iconEdit = message.querySelector(`#${MESSENGER_IDS.ICON_EDIT_WORKFLOW}`);
            if(iconEdit){
                iconEdit.remove();
                iconSendWorkflow.style.right = "17px";
            }
          }
        }
      }
    }
}

const setContentMessageChat = (task, workflowId) => {

    let content = document.querySelector(`${MESSENGER_COMPONENTS.MESSAGE}[data-id="${workflowId}"] > .${CSS.MESSAGE_CONTENT}`);
    let textArea = document.getElementById(MESSENGER_IDS.COMMENT_TASK);

    if(content && textArea){
        // content.scrollIntoView({behavior: "smooth", block: "center", inline: "nearest"});
        setDataset(textArea, { workflowId, task });
        textArea.setValueHtml(content.innerHTML);
    }
}

const checkButtonsToolbar = (task, taskId)=>{

    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);

    const toolbar = document.getElementById(aonMessengerChat.TOOLBAR);

    if(toolbar && task && task.id){
        const show = task.id === taskId;
        toolbar.showButton(MESSENGER_IDS.TOOLBAR_BRANCH, show);
        toolbar.showButton(MESSENGER_IDS.TOOLBAR_LABELS, show);
        toolbar.showButton(ACTIONS.RESTORE.id, show);
        toolbar.showButton(ACTIONS.DELETE.id, show);
        toolbar.showButton(ACTIONS.SAVE.id, show);
        toolbar.showButton(MessengerOptions.AON_MESSENGER_LIST_ARCHIVE.id, show);
        toolbar.showButton(MessengerOptions.AON_MESSENGER_LIST_CLOSE.id, show);

        if(!aonMessengerChat.isCau() && aonMessengerChat.getDur().isEmployee() && show){

            const myTaskHolderId = aonMessengerChat.MY_TASKHOLDER && aonMessengerChat.MY_TASKHOLDER.id ? aonMessengerChat.MY_TASKHOLDER.id : undefined;
        
            const parent         = aonMessengerChat.getApplicationParent();

            parent.getMyWorkgroups()
            .then(myWorkgroups=>{
                const is = isMyTask(task, myTaskHolderId, myWorkgroups);
                toolbar.showButton(MESSENGER_IDS.TOOLBAR_LABELS, is);
                toolbar.showButton(ACTIONS.DELETE.id, is);
                toolbar.showButton(ACTIONS.SAVE.id, is);
                toolbar.showButton(MessengerOptions.AON_MESSENGER_LIST_CLOSE.id, is);
                toolbar.showButton(MessengerOptions.AON_MESSENGER_LIST_ARCHIVE.id, is);
            });
        }   
    }
}

const createLink =() =>{
    const selection = document.getSelection();
    const url = prompt('URL:', 'https://');
    const aEl = setAttributes(document.createElement(TAG.A),{
        target:"_system",
        class:CSS.AON_LINK,
        href:url,
        title:url
    });
    aEl.textContent = selection.toString() ? selection : url;
    document.execCommand('insertHTML', false, aEl.outerHTML);
}

const blockquote = ()=>{
    const selection = document.getSelection();
    const blockquoteEl = setStyles(document.createElement("blockquote"),{
        margin:"0px 0px 0px 0.8ex",
        borderLeft: "1px solid #cccccc",
        paddingLeft: "1ex"
    });
    blockquoteEl.textContent = selection;
    document.execCommand('insertHTML', false, blockquoteEl.outerHTML);
}

/**
 * Append a new message to the chat with a little animation
 * @param {*} properties 
 */
const appendChatMessage = (properties) => {
    const noMessage = document.getElementById(MESSENGER_IDS.NO_MESSAGES);
    const chat = document.querySelector(MESSENGER_COMPONENTS.CHAT);
    
    if(noMessage){
        chat.removeChild(noMessage);
    }

    const message = setStyles( TaskCreationUtils.createChatMessageNew(properties, chat),{
        opacity : 0,
        marginTop : '20px',
        transition : ".25s"
    });
    /**
     * Appearing animation
     */
    setTimeout(() => setStyles(message, { opacity : 1, marginTop : '10px'}), 100);
    return message;
} 

/**
 * 
 * @param {Task} task task
 * @param {HTMLElement} textArea htmlElement textArea
 * check files and send uploadFile(taskAttach) 
 */
const checkFilesAndSave = async (task, textArea)=>{
    const btnSend = document.getElementById(MESSENGER_IDS.BTN_SEND_MESSAGE);

    if(btnSend){
        btnSend.style.pointerEvents = "none";
    }

    try {
        await textArea.checkFileBase64();
        await saveFiles(task, textArea);
    } catch (error) { console.log(error); }

    if(btnSend) {
        btnSend.style.pointerEvents = "auto";
    }
}

const saveFiles = async(task, textArea)=> {

    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);

    const files = textArea.getFiles();
    for await (const file of files) {
        const attach = await aonMessengerChat.uploadFile({ file, task });
        if(attach){
            const json = {
                domain_name: attach.domain_name,
                attach_type: attach.attach_type,
                domain_id:attach.domain,
                id:attach.id
            };

            const jsonBase64 = btoa( JSON.stringify(json) );
            
            let linkTmp = `/${API_URL}/file/${jsonBase64}`;

            if(aonMessengerChat.isCau()){
                linkTmp = SIG_URL+linkTmp;
            }

            const element = document.querySelector(`[${CONSTANT.TYPE}=${WORKFLOW_TYPES.AON_FILE}][data-id='${file.id}']`);
            if(element){
                if(file.contentType.includes("image")){
                    element.src = linkTmp;
                } else {
                    element.href = linkTmp;
                } 
            }
        }
    }
}

const changeFormProcess = (task, {value,name}) => {
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    const processDiv = document.getElementById(MESSENGER_IDS.PROCESS_DIV);
    processDiv.innerHTML = "";
    let sender;
    //CREATE CARD
    let aonCard = TaskCreationUtils.createCardMessenger("aonCardProcess", name);
    processDiv.appendChild(aonCard);
    setStyles(aonCard.getCard(), { margin:0, marginTop:"10px" });
    aonCard.getCardTitle1().style.whiteSpace = "pre-wrap";

    if(task.id && aonMessengerChat.getDur().isDev()){ //BUTTON SHOW JSON
        aonCard.addTitleButton(MSG.VIEW, MATERIAL_ICONS.VISIBILITY, false, () => {
            let d = aonMessengerChat.applicationEl.getDialog();
            if(d){
                d.clear();
                if (!aonMessengerChat.isMobile()) {
                    d.width = '400px';
                }
                d.setTitle("JSON");
                d.setContent(jsonDiv(task));
                d.addAcceptAction(() => {});
                d.open();
            }
        });
    }

    if(aonMessengerChat.isMobile() && task.sender && task.sender.name ) {
        sender = `[${task.sender.name}] ${name}`;
    }
     
    if(value===1){ //FORM VACATION
        if(sender) {
            aonCard.setTitleSection1(sender);
        }
        FormVacation.createForm(task, aonCard);
    } else if(value ===2) {
        FormMovSs.createForm(task, aonCard);
    } else if(value ===3) {
        if(sender) {
            aonCard.setTitleSection1(sender);
        }
        FormTimecontrol.createForm(task, aonCard);
    }
}

const jsonDiv = (task)=> {
    const pre  = setStyles(document.createElement("pre"),{
        backgroundColor: "ghostwhite",
        border: "1px solid silver",
        padding: "10px 20px",
        margin: "20px",
        whiteSpace: "pre-wrap"
    });
    const code = document.createElement("code");
    code.style.color = "brown";
    pre.appendChild(code); 
    code.textContent = JSON.stringify(task.getDescriptionJson(), undefined, 2);
    return pre;
}

/**
 * 
 * @param {Task} task
 * @param {Select} requestTypeSelect aon select type request
 */
const onChangeTypeSelect = (task, requestTypeSelect, forExternal = false) =>{
    const detail = requestTypeSelect.getDetail() || {};
    if(detail){
        const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
        const dinamicDiv = document.getElementById(MESSENGER_IDS.DINAMIC_DIV);
        const processDiv = document.getElementById(MESSENGER_IDS.PROCESS_DIV);

        //------------------HTML CLEAN UP
        dinamicDiv.innerHTML = "";
        processDiv.innerHTML = "";

        if(!aonMessengerChat.isMobile()){
            const secondDiv  = document.getElementById(MESSENGER_IDS.SECOND_DIV);
            secondDiv.innerHTML = "";
        }
     
        const aonTextArea = document.getElementById(MESSENGER_IDS.DESCRIPTION_TASK);
        if(aonTextArea) {
            aonTextArea.remove();
        }

        const type = detail.value;
        if(type){

            if(!task.id){
                task.setSource(type);
                
                if(type == TASK_SOURCE.PROCESS) {
                    task.setTitle(detail.name);
                } 
                else if(type === TASK_SOURCE.GROUPED && !aonMessengerChat.isCau() && !aonMessengerChat.isMobile()){
                    MessengerChat.loadTaskGrouped(task);
                }
        
                if(!forExternal) {
                    task.setDomain(task.getDomainTmp());
                    task.setProject({});
                    task.setRegistry({});
                    task.setGTaskId(undefined);
                    if(aonMessengerChat.isBeta() && !aonMessengerChat.isCau()){
                        const myTaskHolder = task.myTaskHolder;
                        if(myTaskHolder && myTaskHolder.id && task.getTaskHolder() && !task.getTaskHolder().id){
                            task.setTaskHolder(myTaskHolder);
                        }
                    }
                } 
            }

            if(type === TASK_SOURCE.REQUEST){
                formRequest(task, dinamicDiv, forExternal);
            } else if(type === TASK_SOURCE.CAU){
                formCau(task, dinamicDiv, forExternal);
            } else {
                formQuery(task, dinamicDiv, forExternal);
            }
        }
    }
} 

const hideBtnExternal = (type, btnForExternal, divRequest) => {
    const none = "none";
    const display = type === TASK_SOURCE.CAU ? none : "block";
    
    const x6 = CSS.AON_COL_XS_6;
    const x12 = CSS.AON_COL_XS_12;

    if(display===none){
        btnForExternal.checked = CONSTANT.FALSE;
        divRequest.classList.remove(x6);
        divRequest.classList.add(x12);
    } else {
        divRequest.classList.remove(x12);
        divRequest.classList.add(x6);
    }
    btnForExternal.style.display = display;
}

/**
 * 
 * @param {Task} task 
 * @param {HTMLElement} dinamicDiv aon-messenger-chat
 * @param {Boolean} forExternal btn by gestor(forExternal)
 */
const formQuery = (task, dinamicDiv, forExternal = false) => {
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    const applicationParent = aonMessengerChat.applicationParentEl;
    const isAdvisoryCompany = task.isAdvisoryCompany();
    const hideData = task.getId() && task.getSource() === TASK_SOURCE.CAU && task.isOtherDomain();

    //-----------------TITLE
    const title = TaskCreationUtils.createInputTitle();
    if(task.getTitle()){
        title.value = task.getTitle();
    }

    title.addEventListener(EVENT.KEYUP, ({target})=>{
        if(target.value) {
            task.setTitle(target.value)
        }
    });

    TaskCreationUtils.createDivGrid(dinamicDiv, title, {classes:[CSS.AON_COL_XS_12]});

    //-------------------------CAU--------------------------
    if( ( (task.getSource() === TASK_SOURCE.CAU && !applicationParent.cau) || forExternal) && !hideData){

        if(isAdvisoryCompany || task.getSource() === TASK_SOURCE.CAU){
            addCustomerAndContact(task, dinamicDiv);
        }
        
        if(forExternal){
            // ------------------ADVISORY SELECT
            if(!task.isAdvisoryCompany()){
                const advisorySelect = TaskCreationUtils.createAdvisory();
                TaskCreationUtils.createDivGrid(dinamicDiv, advisorySelect, {classes:[CSS.AON_COL_XS_12]});
                TaskFill.fillAdvisory(task);
            }

            // ------------------PROJECT
            const projectSelect = TaskCreationUtils.createProject();
            projectSelect.default = true;
            TaskCreationUtils.createDivGrid(dinamicDiv, projectSelect, {classes:[CSS.AON_COL_XS_12], styles: {display: 'none'}});
        } 
    } 

    //--------------------------DIV WORKGROUP AND TASKHOLDER
    if((!forExternal) && !applicationParent.cau && !hideData){
        addTaskHolderAndWorkgroup(task, dinamicDiv);
    }

    addTaskDescription(task);
}

/**
 * 
 * @param {Task} task
 * @param {HTMLElement} dinamicDiv columns div append html
 * @param {Boolean} external btn by gestor(external)
 */
const formRequest = (task, dinamicDiv, forExternal = false) => {
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    const dataDefault = aonMessengerChat.getData();

    //-----------------TYPE PROCESS
    const processType = TaskCreationUtils.createProcessType();
    processType.default = CONSTANT.TRUE;
    if(dataDefault.source_id) {
        setAttributes(processType, {disabled:CONSTANT.TRUE, readonly:CONSTANT.TRUE});
    }

    TaskCreationUtils.createDivGrid(dinamicDiv, processType, {classes:[CSS.AON_COL_XS_12]});

    processType.addEventListener(EVENT.CHANGE, ({detail})=>{
        if(detail && detail.value){
            changeFormProcess(task, detail);
        }
    })

    TaskFill.fillProcessType(task);

    if(task.id) {
        processType.setDisabled(CONSTANT.TRUE);
    }

    if(forExternal){
        // ------------------ADVISORY SELECT
        if(!task.isAdvisoryCompany()){
            const advisorySelect = TaskCreationUtils.createAdvisory();
            TaskCreationUtils.createDivGrid(dinamicDiv, advisorySelect, {classes:[CSS.AON_COL_XS_12]});
            TaskFill.fillAdvisory(task);
        }

        // ------------------PROJECT
        const projectSelect = TaskCreationUtils.createProject();
        projectSelect.default = true;
        TaskCreationUtils.createDivGrid(dinamicDiv, projectSelect, {classes:[CSS.AON_COL_XS_12], styles: {display: 'none'}});
        TaskFill.fillProject(task);
    }

     //--------------------------DIV WORKGROUP AND TASKHOLDER
    if((!forExternal) && !aonMessengerChat.getApplicationParent().cau){
        addTaskHolderAndWorkgroup(task, dinamicDiv);
    }
}

/**
 * 
 * @param {Task} task
 * @param {HTMLElement} dinamicDiv columns div append html
 * @param {Boolean} forExternal btn by gestor(forExternal)
 */
const formCau = (task, dinamicDiv, forExternal = false) => {
    addCauForm(task, dinamicDiv);
    formQuery(task, dinamicDiv, forExternal);
}

/**
 * 
 * @param {HTMLElement} chat add line element html
 */
const addLine = (chat) =>{
    setTimeout(() =>{
        chat.style.setProperty("--height", chat.scrollHeight + "px");
    }, 250);
}

/**
 * 
 * @param {Task} task
 */
const addTaskDescription = (task) => {
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    if(task.id) return;
    
    const aonTextArea = TaskCreationUtils.createAonTextArea(`${MSG.WRITE_A_DESCRIPTION}...`);
    aonTextArea.id = MESSENGER_IDS.DESCRIPTION_TASK;

    if(!aonMessengerChat.isMobile()){ //-------------------------------------------------------DESKTOP
        const processDiv = document.getElementById(MESSENGER_IDS.PROCESS_DIV);
  
        setStyles(aonTextArea, { minHeight: '150px', maxHeight: '300px', position: 'relative', borderRadius:"5px"});
        processDiv.appendChild(aonTextArea);
        
        const label = aonTextArea.addLabelTextEnd();
        label.addEventListener(EVENT.CLICK, ()=> aonTextArea.clickFile());
    
        aonTextArea.addEventListener(EVENT.INPUT, ({target})=>{
          if(target.value) {
            task.setDescriptionJson({observation:target.value});
          }
          
          aonTextArea.checkFileBase64()
          .then(()=> {
            task.setFiles(aonTextArea.getFiles());
          });
        });
        checkFilesAddEventDescription(task);//check files description
        
    } else { //-------------------------------------------------MOBILE 
        const div = document.getElementById(MESSENGER_IDS.SECOND_DIV);
        div.appendChild(aonTextArea);
        aonTextArea.addEventListener(EVENT.INPUT, ({target})=>{
            if(target.value) {
                task.setDescriptionJson({observation:target.value});
            }
        });
        
        // /**
        // * CHANGE STYLE AONTEXTAAREA
        // */
        setStyles(aonTextArea,{ height: "100%",  width: "100%", boxShadow : "none", marginTop : 0 });
        let textAreaDiv = aonTextArea.getTextArea();
        if(textAreaDiv) {
            textAreaDiv.style.padding = "20px";
        }
        
        const aonTextAreaToolbar = aonTextArea.getToolbar();
        if(aonTextAreaToolbar){
            setStyles(aonTextAreaToolbar,{
                paddingLeft  : "calc(1.5em - 5px)",
                paddingRight : "calc(1.5em - 5px)",
                borderBottom : "1px solid #e0e0e0"
            });
        }
    }

    if(task && task.getDescriptionJson().observation) {
        aonTextArea.value = task.getDescriptionJson().observation;
    }

    buildTextareaToolbar(aonTextArea);
}

/**
 * 
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 * @param {HTMLElement} dinamicDiv 
 */
const addTaskHolderAndWorkgroup = (task, dinamicDiv) => {
    //-----------------WORKGROUP
    const workgroupSelect = TaskCreationUtils.createWorkgroup();
    workgroupSelect.default = true;
    TaskCreationUtils.createDivGrid(dinamicDiv, workgroupSelect, {classes:[CSS.AON_COL_XS_6]});
    TaskFill.fillWorkGroup(task);

    //-----------------TASK HOLDER
    const taskHolderSelect = TaskCreationUtils.createTaskHolder();
    taskHolderSelect.default = true;
    TaskCreationUtils.createDivGrid(dinamicDiv, taskHolderSelect, {classes:[CSS.AON_COL_XS_6]});
    TaskFill.fillTaskHolder(task);
}

/**
 * 
 * @param {Task} task class Task
 * @param {HTMLElement} dinamicDiv 
 */
const addCustomerAndContact = (task, dinamicDiv) => {
    // CUSTOMER
    const customerSelect = TaskCreationUtils.createCustomer();
    customerSelect.default = true;
    TaskCreationUtils.createDivGrid(dinamicDiv, customerSelect, {classes:[CSS.AON_COL_XS_12]});
    TaskFill.fillCustomer(task);
    // DIV CONTACT
    //-----------------CONTACT
    const contact = TaskCreationUtils.createInputContact();
    contact.addEventListener(EVENT.INPUT, ({target})=>{
        task.setGTaskId(target.value)
    });
    TaskCreationUtils.createDivGrid(dinamicDiv, contact, {classes:[CSS.AON_COL_XS_12]});
    if(task.gtask_id) contact.value  = task.gtask_id;
}

/**
 * create div tags
 * @param {Task} task
 * @param {HTMLElement} parent insertBefore
 */
const createTagsDiv = (task, parent) => {
    const div = TaskCreationUtils.createReceiverDiv();
    div.style.flexWrap = "wrap";
    div.id = MESSENGER_IDS.DIV_TASK_TAGS;
    parent.insertBefore(div, parent.firstChild);
    setTaskTags(task);
}

const addCauForm = (task, dinamicDiv)=> {
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    
    if(task.getSource() === TASK_SOURCE.CAU ){
        addInfoDomain(task, dinamicDiv);
    }

    const selectTypeIncident = TaskCreationUtils.createSelectCau('typeCau', MESSENGER_IDS.TYPE_REQUEST_CAU, MSG.TYPE_INCIDENT);
    const divTypeIncident = TaskCreationUtils.createDivGrid(dinamicDiv, selectTypeIncident, {classes:[CSS.AON_COL_XS_6]})
    TaskFill.fillTypeRequestCau(task);
    
    if(!task.getId() && aonMessengerChat.isCau()){
        const selectApp = TaskCreationUtils.createSelectCau('selectApp', MESSENGER_IDS.SELECT_APP,  MSG.APPLICATION);
        TaskCreationUtils.createDivGrid(dinamicDiv, selectApp, {classes:[CSS.AON_COL_XS_6]})
        TaskFill.fillSelectAppCau(task);
    } else {
        divTypeIncident.classList.remove(CSS.AON_COL_XS_6);
        divTypeIncident.classList.add(CSS.AON_COL_XS_12);
    }
}

const addInfoDomain = (task, dinamicDiv)=> {
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    const isCau = aonMessengerChat.isCau();
    const isBeta = aonMessengerChat.isBeta();

    try {
        if( task.getSource() === TASK_SOURCE.CAU ){

            const json = task.getDescriptionJson();
            const isEditable = isBeta && !isCau;
            let cauInfo = task.id && json.cauInfo ? json.cauInfo : aonMessengerChat.getCauInfo();
            
            // if(task.id && !isCau && task.getDescriptionJson().cauInfo) cauInfo = task.getDescriptionJson().cauInfo;
        
            const company = cauInfo.company;
            const auth    = cauInfo.auth;
            const email   = auth && auth.email ? auth.email : undefined;
            const login   = cauInfo.login;

            // const fn = (task.id || isCau) ? TaskCreationUtils.createDivGrid : TaskCreationUtils.createDivGridBefore;
            const fn = TaskCreationUtils.createDivGrid;

            if(task.id){
                const companyName = company && company.name ? company.name : undefined;

                let element = createLabelAnchor(MSG.ENTERPRISE, companyName, false, isEditable);
                fn(dinamicDiv, element.label, {classes:[CSS.AON_COL_XS_12], styles:{paddingBottom:"5px"}});
                if( !companyName || isEditable ){
                    element.anchor.addEventListener(EVENT.INPUT, ({target})=>{
                        const value = target.innerText;
                        cauInfo.company.name = value;
                        task.setDescriptionJson({cauInfo});
                    });
                }
            }

            if(!isCau){
                const domainName = task.id && company && company.domain && company.domain.name ? company.domain.name : undefined;
                const domainIsEditable = !domainName && isEditable;
                let {label, anchor} = createLabelAnchor(MSG.DOMAIN, domainName, true, domainIsEditable);
                fn(dinamicDiv, label, {classes:[CSS.AON_COL_XS_12], styles:{paddingBottom:"5px"}});
                if(domainIsEditable){
                    anchor.addEventListener(EVENT.INPUT, ({target})=>{
                        const value = target.innerText;
                        cauInfo.company.domain.name = value;
                        task.setDescriptionJson({cauInfo});
                    });
                }
            }

            let elementTwo = createLabelAnchor(MSG.EMAIL, email, false, isEditable);
            fn(dinamicDiv, elementTwo.label, {classes:[CSS.AON_COL_XS_12], styles:{paddingBottom:"5px"}});
            if(!email || isEditable){
                elementTwo.anchor.addEventListener(EVENT.INPUT, ({target})=>{
                    const value = target.innerText;
                    cauInfo.auth.email = value;
                    task.setDescriptionJson({cauInfo});
                });
            }

            if(!isCau){
                let {label, anchor} = createLabelAnchor(MSG.USER, login, false, isEditable);
                fn(dinamicDiv, label, {classes:[CSS.AON_COL_XS_12], styles:{paddingBottom:"10px"}});
                if(isEditable){
                    anchor.addEventListener(EVENT.INPUT, ({target})=>{
                        const value = target.innerText;
                        cauInfo.login = value;
                        task.setDescriptionJson({cauInfo});
                    });
                }
            }
        } 
    } catch (error) {
        console.log(error);
    }
}

const createLabelAnchor = (text, domainName, clickable = true, editable = false) => {
    const label = setStyles(document.createElement(TAG.LABEL),{
        color:CSS.variable(COLORS.GRAYSON),
        paddingLeft:"4px",
        display:"flex"
    });
    label.textContent = `${text}: `;

    const anchor = setStyles(document.createElement(TAG.A),{
        color:CSS.variable(COLORS.AON_BLUE),
        cursor: "text"
    });
    
    if(editable){
        anchor.style.minWidth = "150px";
        anchor.setAttribute("placeholder", "Introducir texto aquí");
        anchor.setAttribute("contenteditable", true);
    
        anchor.addEventListener(EVENT.PASTE, (ev) => {
            ev.preventDefault();
            const clipboardData = ev.clipboardData.getData('text/plain');
            document.execCommand("insertHTML", false, clipboardData);
        });
    } else if(clickable){
        anchor.href = "https://"+domainName;
        anchor.target = "_system";
        anchor.style.cursor = "pointer";
    }
 
    anchor.textContent = domainName;
    label.appendChild(anchor);

    return {
        label,
        anchor
    };
}

/**
 * 
 * @param {Task} task 
 * @param {Integer} myTaskHolderId 
 * @param {Array} myWorkgroups 
 * @returns 
 */
const isMyTask = (task, myTaskHolderId, myWorkgroups) => {

    const taskTaskholderId = task.task_holder && task.task_holder.id ? task.task_holder.id : undefined;

    const taskSenderId     = task.sender && task.sender.id ? task.sender.id : undefined;

    const taskWorkgroup    = task.workgroup && task.workgroup.id ? task.workgroup.id : undefined;
   
    const isMyTaskHolder   = (taskTaskholderId == myTaskHolderId) || (taskSenderId == myTaskHolderId);

    const isMyWorkgroup   = myWorkgroups && myWorkgroups.length ? myWorkgroups.some(({id})=> id == taskWorkgroup) : false;

    return isMyTaskHolder || isMyWorkgroup;
}

const documentExec = (exec) => document.execCommand(exec) ? document.execCommand("normal") : document.execCommand(exec);

export const TaskUtils = {
    buildTextareaToolbar,
    addIconToolbar,
    getIconJson,
    downChat,
    upChat,
    dialogTaskTags,
    setTaskTags,
    chooseIconMessage,
    sendMessage,
    checkFilesAddEventClick,
    checkFilesAddEventDescription,
    taskNumberParse,
    parseTimeToDouble,
    parseDoubleToTime,
    buildForm,
    setStyleMessageHistoric,
    setContentMessageChat,
    checkButtonsToolbar,
    isMyTask
}