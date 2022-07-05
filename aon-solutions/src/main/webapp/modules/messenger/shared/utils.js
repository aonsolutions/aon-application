import { API_URL, COLORS, CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, SIG_URL, TAG } from "../../../environments/environments.js";
import { openFileUrl } from "../../../services/fileService.js";
import { setAttributes, setClasses, setDataset, setStyles } from "../../../services/utilsComponents.js";
import { createFormVacation } from "../forms/vacation.js";
import { MessengerOptions, MESSENGER_COMPONENTS, MESSENGER_DIRECTION, MESSENGER_IDS, MESSENGER_VIEWS, TAG_TYPE, TASK_SOURCE, TASK_STATUS, WORKFLOW_TYPE, WORKFLOW_TYPES } from "../MessengerEnums.js";
import { TaskCreationUtils } from "./TaskCreationUtils.js";
import { TaskFill } from "./TaskFill.js";
import { AonCheckbox } from "../../../components/aon-checkbox.js";
import { createFormMov } from "../forms/mov-ss.js";
import { createFormTimeControl } from "../forms/time-control.js";
import { AonDateUtils } from "../../utils/AonDateUtils.js";
import * as ACTIONS from "../../actions.js";

/**
 * Build standard toolbar options 
 * @param {HTMLElement} aonTextArea aon-text-area
 */
export const buildTextareaToolbar =  (aonTextArea) => {
    const textAreaText = aonTextArea.querySelector("#" + aonTextArea.TEXTAREA);
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

const documentExec = (exec) => document.execCommand(exec) ? document.execCommand("normal") : document.execCommand(exec);

export const addIconToolbar = (toolbar, task) => {
    const titleSpan = setClasses(toolbar.querySelector( `.${CSS.AON_SECONDARY_TOOLBAR_TITLE}` ), [CSS.FLEX_ROW, CSS.FLEX_ALIGN_CENTER]);
    if(titleSpan){
        const iconJson = getIconJson(task);
        const status = TaskCreationUtils.createOutlinedMaterialIcon({
          name:  iconJson.icon,
          color: iconJson.icon_color,
          size: "20px"
        });
        status.element.style.marginLeft = "10px";
        status.element.style.marginTop = "-1px";
        status.appendTo(titleSpan);
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
 * Choose icon for the actions
 * @param {Object} message
 * @returns {Object} actionJson message new object
 */
export const chooseIconMessage = ({type, date, name, comment}) => {
    const dateParse = date ? (AonDateUtils.setFullDate(date) + " " + AonDateUtils.setTime(date)) : null;
    
    let actionJson = {
        icon : MATERIAL_ICONS.INFO,
        type : CONSTANT.MATERIAL_OUTLINED,
        color : CSS.variable(COLORS.MATERIAL_BLUE),
        comment: `${WORKFLOW_TYPE(type)} por <b>${name ? name : null}</b> ${dateParse}`
    }

    if(WORKFLOW_TYPES.OPEN.indexOf(type)>=0 || WORKFLOW_TYPES.REOPEN.indexOf(type)>=0)
        actionJson.color = CSS.variable(COLORS.ONLINE_GREEN);
    else if(WORKFLOW_TYPES.CLOSE.indexOf(type)>=0){
        actionJson.icon = MATERIAL_ICONS.CHECK_CIRCLE_OUTLINE;
        actionJson.color = CSS.variable(COLORS.MATERIAL_RED);
    } else if(WORKFLOW_TYPES.DELETE.indexOf(type)>=0){
        actionJson.icon = MATERIAL_ICONS.ARCHIVE;
        actionJson.color = CSS.variable(COLORS.GRAYSON);
    } else if(WORKFLOW_TYPES.ASSIGN.indexOf(type)>=0){
        actionJson.comment = `${WORKFLOW_TYPE(type)} por <b>${name ? name : null}</b> a <b>${comment}</b> ${dateParse}`;
    } else if(WORKFLOW_TYPES.CONNECTED.indexOf(type)>=0){
        let b = taskNumberParse(comment);
        actionJson.comment = `${WORKFLOW_TYPE(type)} con <b>${b}</b> ${dateParse}`;
    }

    return actionJson;
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
 * @param {String} text Optional
 * @param {HTMLElement} aon-messenger-chat 
 * @returns {Obkect} {value, messageEl, workflowId} message element html
 */
export const sendMessage = async (text, task) => {
    let workflowId = undefined;
    let taskId     = undefined;
    let messageEl  = undefined;
    let value = text;

    let textArea = document.getElementById(MESSENGER_IDS.COMMENT_TASK);

    if(!text){
        await checkFilesAndSend(task, textArea); //CHECK FILES COMMENT AND SEND
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
            me: true
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
 * @param {Task} task task
 * @param {HTMLElement} textArea htmlElement textArea
 * check files and send uploadFile(taskAttach) 
 */
const checkFilesAndSend = async (task, textArea)=>{
    const btnSend = document.getElementById(MESSENGER_IDS.BTN_SEND_MESSAGE);

    if(btnSend){
        btnSend.style.pointerEvents = "none";
    }

    try {
        await checkFileBase64(textArea);
        await checkFileAonFile(task, textArea);
    } catch (error) { console.log(error); }

    if(btnSend) {
        btnSend.style.pointerEvents = "auto";
    }
}

const checkFileAonFile = async(task, textArea)=> {

    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);

    const textAreaDiv = textArea.getTextArea();

    const elements = textAreaDiv.querySelectorAll(`[${CONSTANT.TYPE}=${WORKFLOW_TYPES.AON_FILE}]`);
    const files = textArea.FILES;
    for await (const el of elements) {
        const fileId = el.dataset.id;
        const file = files.find(({id})=> id == fileId);
        if(file){
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

                if(file.contentType.indexOf("image")>=0){
                    el.src = linkTmp;
                } else {
                    el.href = linkTmp;
                } 
            }
        }
    }
}

const checkFileBase64 = async(textArea)=> {
    const elements = textArea.getTextArea().querySelectorAll(`img[src*=";base64"]`);

    for await (const el of elements) {
        let parent = document.createElement(TAG.DIV);
        let blob = getBlobBySrc(el.src);
        if(blob){
            await textArea.addFile(blob, parent);
        }
        el.parentNode.replaceChild(parent, el);
    }
}

const getBlobBySrc = (src)=>{
    try {
        // base64 encoded data doesn't contain commas    
        const base64ContentArray = src.split(",")     
        
        // base64 content cannot contain whitespaces but nevertheless skip if there are!
        const contentType = base64ContentArray[0].match(/[^:\s*]\w+\/[\w-+\d.]+(?=[;| ])/)[0]
        
        // base64 encoded data - pure
        const base64Str = base64ContentArray[1]

        let byteCharacters = atob(base64Str);
        let byteNumbers = new Array(byteCharacters.length);

        for (let i = 0; i < byteCharacters.length; i++) byteNumbers[i] = byteCharacters.charCodeAt(i);

        return new Blob([new Uint8Array(byteNumbers)], { type: `${contentType};base64` });
    } catch (error) {
        console.log("error getContentFileBase64", error);
    }
    return null;
}




/**
 * 
 * @param {HTMLElement} parent check html and add event 
 * @param {Task} tsks tasks
 * @param {*} json 
 */
export const checkFilesAddEventClick = (parent)=>{
    new Promise(r => setTimeout(r, 1)).then(()=>{
        const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
        const task = aonMessengerChat.task;
        parent.querySelectorAll(`[${CONSTANT.TYPE}=${CONSTANT.AON_FILE}], ${TAG.IMG}`)
        .forEach(element=>{
            const tagName = element.tagName;
            if(tagName && tagName.toLowerCase() === TAG.IMG){
                // parent.style.position = "relative";
                // magnify(element, 3);
            }

            let url = element.src || element.href;      
            if(url){
                if(task.id && aonMessengerChat.isCau()){
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
export const checkFilesAddEventDescription = (task)=>{
    const descriptionEl = document.getElementById(MESSENGER_IDS.DESCRIPTION_TASK);
    if(task && descriptionEl){
        const observation = task.getDescriptionJson().observation;
        if(observation) {
            const element = document.getElementById(descriptionEl.TEXTAREA);
            descriptionEl.setValueHtml(observation);
            checkFilesAddEventClick(element);
        }
    }
}

const changeFormProcess = ({value,name}, aonMessengerChat) => {
    const task = aonMessengerChat.task;
    const processDiv = document.getElementById(MESSENGER_IDS.PROCESS_DIV);
    processDiv.innerHTML = "";
    let sender;
    //CREATE CARD
    let aonCard = TaskCreationUtils.createCardMessenger("aonCardProcess", name);
    processDiv.appendChild(aonCard);
    setStyles(aonCard.getCard(), { margin:0, marginTop:"10px" });
    aonCard.getCardTitle1().style.whiteSpace = "pre-wrap";

    if(task.id && aonMessengerChat.getDur().isMessengerManager()){ //BUTTON SHOW JSON
        aonCard.addTitleButton(MSG.VIEW, MATERIAL_ICONS.VISIBILITY, false, () => {
            let d = aonMessengerChat.applicationEl.getDialog();
            if(d){
                d.clear();
                if (!aonMessengerChat.isMobile()) {
                    d.width = '400px';
                }
                d.setTitle("JSON");
                d.setContent(jsonDiv());
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
        createFormVacation(aonCard, aonMessengerChat);
    } else if(value ===2) {
        createFormMov(aonCard, aonMessengerChat);
    } else if(value ===3) {
        if(sender) {
            aonCard.setTitleSection1(sender);
        }
        createFormTimeControl(aonCard, aonMessengerChat);
    }
}

const jsonDiv = ()=> {
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    const task = aonMessengerChat.task;
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
 * @param {HTMLElement} firstDiv
 * @param {HTMLElement} aonMessengerChatcolumnns div form
 */
export const buildForm = (firstDiv, aonMessengerChat) => {
    const task = aonMessengerChat.task;
    const isAdvisoryCompany = task.isAdvisoryCompany();
    // const dataDefault = aonMessengerChat.getData();

    //-----------------------APPEND DIV TAGS
    createTagsDiv(firstDiv);
    
    //-----------------------CREATE FIRST CARD
    const aonCard = TaskCreationUtils.createCardMessenger(MSG.DATA, "");
    aonCard.flex = "true";
    firstDiv.appendChild(aonCard);
    aonCard.getCard().style.margin = 0;

    if( !task.isExternal() && task.registry && task.registry.name && task.getSource() !== TASK_SOURCE.CAU){
        const registryName = `[${task.registry.name}]`;
        aonCard.setTitleSection1(registryName)
    }
    //-----------------------END CREATE FIRST CARD

    const divCard = document.createElement(TAG.DIV);
    aonCard.setContent(divCard);

    //-----------------------CREATE DIV PROCESS
    const divProcess = TaskCreationUtils.createStartJustifiedColumn().element;
    divProcess.id = MESSENGER_IDS.PROCESS_DIV;
    firstDiv.appendChild(divProcess);
    //---------------------END CREATE DIV PROCESS

    const divStatic = TaskCreationUtils.createDivGrid(divCard, undefined,{ classes:[CSS.AON_COL_XS_12], styles:{ padding:"0" } });

    const divDinamic = TaskCreationUtils.createDivGrid(undefined, undefined,{ classes:[CSS.AON_COL_XS_12], styles:{ padding:"0" } });

    divCard.appendChild(divDinamic);
    //-----------------TYPE REQUEST
    const requestTypeSelect = TaskCreationUtils.createRequestType();
  
    if(task.id) requestTypeSelect.disabled = requestTypeSelect.readonly = true; // || dataDefault.source_id
    
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
            onChangeTypeSelect(aonMessengerChat, requestTypeSelect, divDinamic, divProcess, forExternal);
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
        onChangeTypeSelect(aonMessengerChat, requestTypeSelect, divDinamic, divProcess, btnForExternal ? btnForExternal.isChecked() : false)
    });

    TaskFill.fillRequestType(task, aonMessengerChat);
}

/**
 * 
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 * @param {Select} requestTypeSelect aon select type request
 * @param {HTMLElement} divDinamic appendChild
 * @param {HTMLElement} divProcess div process
 */
 const onChangeTypeSelect = (aonMessengerChat, requestTypeSelect, divDinamic, divProcess, forExternal = false) =>{
    const task = aonMessengerChat.task;
    const detail = requestTypeSelect.getDetail() || {};

    if(detail){
        const type = detail.value;
        //------------------HTML CLEAN UP
        divDinamic.innerHTML = "";
        divProcess.innerHTML = "";
        const aonTextArea = document.getElementById(MESSENGER_IDS.DESCRIPTION_TASK);
        if(aonTextArea) {
            aonTextArea.remove();
        }
        //------------------HTML CLEAN UP
        if(type){
            task.setSource(type);
            
            if(!task.id){
                
                if(type == TASK_SOURCE.PROCESS) {
                    task.setTitle(detail.name);
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
                formRequest(divDinamic, aonMessengerChat, forExternal);
            } else if(type === TASK_SOURCE.CAU){
                formCau(divDinamic, aonMessengerChat, forExternal);
            } else {
                formQuery(divDinamic, aonMessengerChat, forExternal);
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
 * @param {HTMLElement} divDinamic columns div append html
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 * @param {Boolean} forExternal btn by gestor(forExternal)
 */
 const formQuery = (divDinamic, aonMessengerChat, forExternal = false) => {
    const task = aonMessengerChat.task;
    const applicationParent = aonMessengerChat.applicationParentEl;
    const isAdvisoryCompany = task.isAdvisoryCompany();
    const hideData = task.getId() && task.source === TASK_SOURCE.CAU && task.isOtherDomain();

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

    TaskCreationUtils.createDivGrid(divDinamic, title, {classes:[CSS.AON_COL_XS_12]});

    //-------------------------CAU--------------------------
    if( ( (task.source === TASK_SOURCE.CAU && !applicationParent.cau) || forExternal) && !hideData){

        if(isAdvisoryCompany || task.source === TASK_SOURCE.CAU){
            addCustomerAndContact(task, aonMessengerChat, divDinamic);
        }
        
        if(forExternal){
            // ------------------ADVISORY SELECT
            if(!task.isAdvisoryCompany()){
                const advisorySelect = TaskCreationUtils.createAdvisory();
                TaskCreationUtils.createDivGrid(divDinamic, advisorySelect, {classes:[CSS.AON_COL_XS_12]});
                TaskFill.fillAdvisory(task, aonMessengerChat);
            }

            // ------------------PROJECT
            const projectSelect = TaskCreationUtils.createProject();
            projectSelect.default = true;
            TaskCreationUtils.createDivGrid(divDinamic, projectSelect, {classes:[CSS.AON_COL_XS_12], styles: {display: 'none'}});
        } 
    } 

    //--------------------------DIV WORKGROUP AND TASKHOLDER
    if((!forExternal) && !applicationParent.cau && !hideData){
        addTaskHolderAndWorkgroup(aonMessengerChat, divDinamic);
    }

    addTaskDescription(aonMessengerChat);
}

/**
 * 
 * @param {HTMLElement} divDinamic columns div append html
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 * @param {Boolean} external btn by gestor(external)
 */
const formRequest = (divDinamic, aonMessengerChat, forExternal = false) => {
    const task = aonMessengerChat.task;
    const dataDefault = aonMessengerChat.getData();

    //-----------------TYPE PROCESS
    const processType = TaskCreationUtils.createProcessType();
    processType.default = CONSTANT.TRUE;
    if(dataDefault.source_id) {
        setAttributes(processType, {disabled:CONSTANT.TRUE, readonly:CONSTANT.TRUE});
    }

    TaskCreationUtils.createDivGrid(divDinamic, processType, {classes:[CSS.AON_COL_XS_12]});

    processType.addEventListener(EVENT.CHANGE, ({detail})=>{
        if(detail && detail.value){
            changeFormProcess(detail, aonMessengerChat);
        }
    })

    TaskFill.fillProcessType(task, aonMessengerChat);

    if(task.id) {
        processType.setDisabled(CONSTANT.TRUE);
    }

    if(forExternal){
        // ------------------ADVISORY SELECT
        if(!task.isAdvisoryCompany()){
            const advisorySelect = TaskCreationUtils.createAdvisory();
            TaskCreationUtils.createDivGrid(divDinamic, advisorySelect, {classes:[CSS.AON_COL_XS_12]});
            TaskFill.fillAdvisory(task, aonMessengerChat);
        }

        // ------------------PROJECT
        const projectSelect = TaskCreationUtils.createProject();
        projectSelect.default = true;
        TaskCreationUtils.createDivGrid(divDinamic, projectSelect, {classes:[CSS.AON_COL_XS_12], styles: {display: 'none'}});
        TaskFill.fillProject(task);
    }

     //--------------------------DIV WORKGROUP AND TASKHOLDER
    if((!forExternal) && !aonMessengerChat.getApplicationParent().cau){
        addTaskHolderAndWorkgroup(aonMessengerChat, divDinamic);
    }
}

/**
 * 
 * @param {HTMLElement} divDinamic columns div append html
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 * @param {Boolean} forExternal btn by gestor(forExternal)
 */
const formCau = (divDinamic, aonMessengerChat, forExternal = false) => {
    addCauForm(aonMessengerChat, divDinamic);
    
    formQuery(divDinamic, aonMessengerChat, forExternal);
}

/**
 * 
 * @param {Object} source,status 
 * @returns icon, icon_color
 */
export const getIconJson =({source,status}) => {
    const { AON_MESSENGER_LIST_OPEN, AON_MESSENGER_LIST_IN_PROGRESS, AON_MESSENGER_LIST_CLOSE, AON_MESSENGER_LIST_ARCHIVE } = MessengerOptions;
    let icon = MATERIAL_ICONS.INFO;
    let icon_color = AON_MESSENGER_LIST_OPEN.icon_color;

    if(source===TASK_SOURCE.CAU){
        icon = MATERIAL_ICONS.SUPPORT_AGENT;
    } else if(source===TASK_SOURCE.REQUEST){
        icon = MATERIAL_ICONS.ASSIGNMENT;
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
export const downChat = () => {
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
export const upChat = () => {
    const chat = document.getElementById(MESSENGER_IDS.MESSENGER_CHAT);
    if(chat){
        setTimeout(() => chat.firstChild.scrollIntoView(), 100)    
    }
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
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 */
const addTaskDescription = (aonMessengerChat) => {
    const task = aonMessengerChat.task;
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
          task.setFiles(target.FILES);
          if(target.value) task.setDescriptionJson({observation:target.value})
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
 * @param {HTMLElement} divDinamic 
 */
const addTaskHolderAndWorkgroup = (aonMessengerChat, divDinamic) => {
    //-----------------WORKGROUP
    const workgroupSelect = TaskCreationUtils.createWorkgroup();
    workgroupSelect.default = true;
    TaskCreationUtils.createDivGrid(divDinamic, workgroupSelect, {classes:[CSS.AON_COL_XS_6]});
    TaskFill.fillWorkGroup(aonMessengerChat);

    //-----------------TASK HOLDER
    const taskHolderSelect = TaskCreationUtils.createTaskHolder();
    taskHolderSelect.default = true;
    TaskCreationUtils.createDivGrid(divDinamic, taskHolderSelect, {classes:[CSS.AON_COL_XS_6]});
    TaskFill.fillTaskHolder(aonMessengerChat);
}

/**
 * 
 * @param {Task} task class Task
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 * @param {HTMLElement} divDinamic 
 */
const addCustomerAndContact = (task, aonMessengerChat, divDinamic) => {
    // CUSTOMER
    const customerSelect = TaskCreationUtils.createCustomer();
    customerSelect.default = true;
    TaskCreationUtils.createDivGrid(divDinamic, customerSelect, {classes:[CSS.AON_COL_XS_12]});
    TaskFill.fillCustomer(task, aonMessengerChat);
    // DIV CONTACT
    //-----------------CONTACT
    const contact = TaskCreationUtils.createInputContact();
    contact.addEventListener(EVENT.INPUT, ({target})=>{
        task.setGTaskId(target.value)
    });
    TaskCreationUtils.createDivGrid(divDinamic, contact, {classes:[CSS.AON_COL_XS_12]});
    if(task.gtask_id) contact.value  = task.gtask_id;
}

/**
 * create div tags
 * @param {HTMLElement} parent insertBefore
 */
const createTagsDiv = (parent) => {
    const div = TaskCreationUtils.createReceiverDiv().element;
    div.style.flexWrap = "wrap";
    div.id = MESSENGER_IDS.DIV_TASK_TAGS;
    parent.insertBefore(div, parent.firstChild);
    setTaskTags();
}


export const dialogTaskTags = (ev, aonMessengerChat) => {
    const task = aonMessengerChat.task;
    const rect = ev.target.getBoundingClientRect();
    const x = ev.clientX - rect.left + 180;
    const y = ev.clientY - rect.top;
    const top  = rect.top + y;
    let left = rect.left + x;
    const dialog = aonMessengerChat.getApplication().getOptionDialog();
    dialog.clear();
    dialog.setContentTitle(MSG.TAGS);

    const div = setStyles(document.createElement("div"),{ margin:"5px", display:"flex", flexDirection:"column" });

    const tags = aonMessengerChat.getTagsPanel();
    
    const taskTags = task.getTags();
    
    for (let tag of tags) {
        let aonCheckbox = new AonCheckbox();
        aonCheckbox.description = tag.name;
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

    dialog.setContent(div);
    dialog.openPosition({top, left});
}


/**
 * create div tags
 */
export const setTaskTags = () => {
    const div = document.getElementById(MESSENGER_IDS.DIV_TASK_TAGS);
    div.innerHTML = "";
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    const task = aonMessengerChat.task;

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

const addCauForm = (aonMessengerChat, divStatic)=> {
    const task = aonMessengerChat.task;
    const isCau = aonMessengerChat.isCau();
    const isBeta = aonMessengerChat.isBeta();
    try {
        if( task.source === TASK_SOURCE.CAU ){
            const json = task.getDescriptionJson();
            const isEditable = isBeta && !isCau;
            let cauInfo = task.id && json.cauInfo ? json.cauInfo : aonMessengerChat.getCauInfo();
            
            // if(task.id && !isCau && task.getDescriptionJson().cauInfo) cauInfo = task.getDescriptionJson().cauInfo;
        
            const company = cauInfo.company;
            const auth    = cauInfo.auth;
            const email   = auth && auth.email ? auth.email : undefined;
            const login   = cauInfo.login;

            const fn = (task.id || isCau) ? TaskCreationUtils.createDivGrid : TaskCreationUtils.createDivGridBefore;

            const companyName = company && company.name ? company.name : undefined;

            let element = createLabelAnchor(MSG.ENTERPRISE, companyName, false, isEditable);
            fn(divStatic, element.label, {classes:[CSS.AON_COL_XS_12], styles:{paddingBottom:"5px"}});
            if(!companyName || isEditable){
                element.anchor.addEventListener(EVENT.INPUT, ({target})=>{
                    const value = target.innerText;
                    cauInfo.company.name = value;
                    task.setDescriptionJson({cauInfo});
                });
            }
    
            if(!isCau){
                const domainName = task.id && company && company.domain && company.domain.name ? company.domain.name : undefined;
                const domainIsEditable = !domainName && isEditable;
                let {label, anchor} = createLabelAnchor(MSG.DOMAIN, domainName, true, domainIsEditable);
                fn(divStatic, label, {classes:[CSS.AON_COL_XS_12], styles:{paddingBottom:"5px"}});
                if(domainIsEditable){
                    anchor.addEventListener(EVENT.INPUT, ({target})=>{
                        const value = target.innerText;
                        cauInfo.company.domain.name = value;
                        task.setDescriptionJson({cauInfo});
                    });
                }
            }

            let elementTwo = createLabelAnchor(MSG.EMAIL, email, false, isEditable);
            fn(divStatic, elementTwo.label, {classes:[CSS.AON_COL_XS_12], styles:{paddingBottom:"5px"}});
            if(!email || isEditable){
                elementTwo.anchor.addEventListener(EVENT.INPUT, ({target})=>{
                    const value = target.innerText;
                    cauInfo.auth.email = value;
                    task.setDescriptionJson({cauInfo});
                });
            }

            if(!isCau){
                let {label, anchor} = createLabelAnchor(MSG.USER, login, false, isEditable);
                fn(divStatic, label, {classes:[CSS.AON_COL_XS_12], styles:{paddingBottom:"10px"}});
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

    task.setSource(TASK_SOURCE.CAU);

    const selectTypeIncident = TaskCreationUtils.createSelectCau('typeCau', MESSENGER_IDS.TYPE_REQUEST_CAU, MSG.TYPE_INCIDENT);
    const divTypeIncident = TaskCreationUtils.createDivGrid(divStatic, selectTypeIncident, {classes:[CSS.AON_COL_XS_6]})
    TaskFill.fillTypeRequestCau(aonMessengerChat);
    
    if(!task.id && aonMessengerChat.isCau()){
        const selectApp = TaskCreationUtils.createSelectCau('selectApp', MESSENGER_IDS.SELECT_APP,  MSG.APPLICATION);
        TaskCreationUtils.createDivGrid(divStatic, selectApp, {classes:[CSS.AON_COL_XS_6]})
        TaskFill.fillSelectAppCau(aonMessengerChat);
    } else {
        divTypeIncident.classList.remove(CSS.AON_COL_XS_6);
        divTypeIncident.classList.add(CSS.AON_COL_XS_12);
    }
}

const createLabelAnchor = (text, domainNam, clickable = true, editable = false) => {
    const label = setStyles(document.createElement(TAG.LABEL),{
        color:CSS.variable(COLORS.GRAYSON),
        paddingLeft:"4px",
        display:"flex"
    });
    label.textContent =  `${text}: `;

    const anchor = setStyles(document.createElement("a"),{
        color:CSS.variable(COLORS.AON_BLUE),
        cursor: "text"
    });
    
    if(editable){
        anchor.style.minWidth = "150px";
        anchor.setAttribute("placeholder", "Introducir texto aquí");
        anchor.setAttribute("contenteditable", true);
    
        anchor.addEventListener(EVENT.PASTE, (ev) => {
            ev.preventDefault();
            const t = ev.clipboardData.getData('text/plain');
            document.execCommand("insertHTML", false, t);
        });
    } else if(clickable){
        anchor.href = "https://"+domainNam;
        anchor.target = "_system";
        anchor.style.cursor = "pointer";
    }
 
    anchor.textContent = domainNam;
    label.appendChild(anchor);

    return {
        label,
        anchor
    };
}



/**
 * 
 * @param {Arrays} workflows workflows 
 */
export const setStyleMessageHistoric = async (workflows) => {
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

export const setContentMessageChat = (task, workflowId) => {

    let content = document.querySelector(`${MESSENGER_COMPONENTS.MESSAGE}[data-id="${workflowId}"] > .${CSS.MESSAGE_CONTENT}`);
    let textArea = document.getElementById(MESSENGER_IDS.COMMENT_TASK);

    if(content && textArea){
        // content.scrollIntoView({behavior: "smooth", block: "center", inline: "nearest"});
        setDataset(textArea, { workflowId, task });
        textArea.setValueHtml(content.innerHTML);
    }
}

export const checkButtonsToolbar = (aonMessengerChat, taskId)=>{
    const task = aonMessengerChat.task;

    let toolbar = document.getElementById(aonMessengerChat.TOOLBAR);

    if(toolbar && task && task.id){
        const show = task.id === taskId;
        toolbar.showButton(MESSENGER_IDS.TOOLBAR_BRANCH, show);
        toolbar.showButton(MESSENGER_IDS.TOOLBAR_LABELS, show);
        toolbar.showButton(ACTIONS.RESTORE.id, show);
        toolbar.showButton(ACTIONS.DELETE.id, show);
        toolbar.showButton(ACTIONS.SAVE.id, show);
        toolbar.showButton(MessengerOptions.AON_MESSENGER_LIST_CLOSE.id, show);
        toolbar.showButton(MessengerOptions.AON_MESSENGER_LIST_ARCHIVE.id, show);
    }
}

export const taskNumberParse = (number) => "#"+(number || "0").toString().padStart(5, 0);

export const parseTimeToDouble = (time)=>{
    if(time){
        const timeSplit = time.split(":");
        return parseFloat(timeSplit[0]) + parseFloat(timeSplit[1])/60;
    }
    return 0;
}

export const parseDoubleToTime = (value)=>{
    if(value){
        const hours = Math.floor(value);
        const minutes = Math.round((value - hours) * 60);
        return `${hours}:${minutes}`;
    }
    return null;
}