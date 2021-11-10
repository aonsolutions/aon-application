import { API_URL, COLORS, CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from "../../../environments/environments.js";
import { openFileUrl } from "../../../services/fileService.js";
import { domainName } from "../../../services/request.js";
import { setAttributes, setClasses, setStyles } from "../../../services/utilsComponents.js";
import { createFormVacation } from "../forms/vacation.js";
import { MessengerOptions, MESSENGER_COMPONENTS, MESSENGER_DIRECTION, MESSENGER_IDS, MESSENGER_VIEWS, TASK_SOURCE, TASK_STATUS, WORKFLOW_TYPE, WORKFLOW_TYPES } from "../MessengerEnums.js";
import { appendTaskTag, createAdvisory, createAonSwitch, createAonTextArea, createCardMessenger, createChatMessage, createCustomer, createDivGrid, createInputContact, createOutlinedMaterialIcon, createProcessType, createProject, createReceiverDiv, createRequestType, createSelectCau, createStartJustifiedColumn, createTaskHolder, createWorkgroup } from "./creationUtils.js";
import { fillAdvisory, fillCustomer, fillProcessType, fillProject, fillRequestType, fillSelectAppCau, fillTaskHolder, fillTypeRequestCau, fillWorkGroup } from "./fill.js";
import { AonCheckbox } from "../../../components/aon-checkbox.js";
import { createFormMov } from "../forms/mov-ss.js";
import { createFormTimeControl } from "../forms/time-control.js";
import { AonDateUtils } from "../../utils/AonDateUtils.js";

/**
 * Build standard toolbar options 
 * @param {HTMLElement} aonTextArea aon-text-area
 */
export const buildTextareaToolbar =  (aonTextArea) => {
    const textAreaText = aonTextArea.querySelector("#" + aonTextArea.TEXTAREA);
    if(textAreaText) setStyles(textAreaText, {resize: "none"});
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
}

const documentExec = (exec) => document.execCommand(exec) ? document.execCommand("normal") : document.execCommand(exec);

export const addIconToolbar = (toolbar, task) => {
    const titleSpan = setClasses(toolbar.querySelector( `.${CSS.AON_SECONDARY_TOOLBAR_TITLE}` ), [CSS.FLEX_ROW, CSS.FLEX_ALIGN_CENTER]);
    if(titleSpan){
        const iconJson = getIconJson(task);
        const status = createOutlinedMaterialIcon({
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
    const aEl = setAttributes(document.createElement("a"),{
        target:"_blank",
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
        borderLeft: "1px solid rgb(204, 204, 204)",
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
    const dateParse = AonDateUtils.setFullDate(date) + " " + AonDateUtils.setTime(date);
    
    let actionJson = {
        icon : MATERIAL_ICONS.INFO,
        type : CONSTANT.MATERIAL_OUTLINED,
        color : CSS.variable(COLORS.MATERIAL_BLUE),
        comment: `${WORKFLOW_TYPE(type)} por <b>${name ? name : null}</b> ${dateParse}`
    }

    if(WORKFLOW_TYPES.OPEN.indexOf(type)>=0 || WORKFLOW_TYPES.REOPEN.indexOf(type)>=0){
        actionJson.color = CSS.variable(COLORS.ONLINE_GREEN);
    }  else if(WORKFLOW_TYPES.CLOSE.indexOf(type)>=0){
        actionJson.icon = MATERIAL_ICONS.CHECK_CIRCLE_OUTLINE;
        actionJson.color = CSS.variable(COLORS.MATERIAL_RED);
    } else if(WORKFLOW_TYPES.DELETE.indexOf(type)>=0){
        actionJson.icon = MATERIAL_ICONS.ARCHIVE;
        actionJson.color = CSS.variable(COLORS.GRAYSON);
    } else if(WORKFLOW_TYPES.ASSIGN.indexOf(type)>=0){
        actionJson.comment = `${WORKFLOW_TYPE(type)} por <b>${name ? name : null}</b> a <b>${comment}</b> ${dateParse}`;
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
    
    if(noMessage)
        chat.removeChild(noMessage);

    const message = setStyles( createChatMessage(properties, chat),{
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
 * @param {String} text optional
 * @param {HTMLElement} aon-messenger-chat 
 * @returns {Array} [value, messageEl] message element html
 */
export const sendMessage = async (text, aonMessengerChat) => {
    let value = text;
    if(!text){
        let aonTextArea = document.getElementById(MESSENGER_IDS.COMMENT_TASK);
        await checkFilesAndSend(aonTextArea); //CHECK FILES COMMENT AND SEND
        value = aonTextArea.value;
        aonTextArea.clear();
    }
    
    if(!value || (value && !value.trim().length)) return ;

    const task = aonMessengerChat.task;
    const message = {
        type: WORKFLOW_TYPES.COMMENT,
        sender: "",
        comment: value,
        creation_date: new Date()
    }

    task.workflow.push(message);

    const messageEl = appendChatMessage({
        id: "id",
        name: message.sender,
        comment:message.comment,
        date: message.creation_date,
        direction : MESSENGER_DIRECTION.RIGHT
    });
    
    aonMessengerChat.data = task;

    return [value, messageEl];
}

/**
 * 
 * @param {HTMLElement} textArea htmlElement textArea
 * check files and send uploadFile(taskAttach) 
 */
const checkFilesAndSend = async (textArea)=>{
    const btnSend = document.getElementById(MESSENGER_IDS.BTN_SEND_MESSAGE);
    if(btnSend)btnSend.style.pointerEvents = "none";
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
                const taskAttach = await aonMessengerChat.uploadFile({ file, task:taskId });
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
    if(btnSend)btnSend.style.pointerEvents = "auto";
}

/**
 * 
 * @param {HTMLElement} parent check html and add event 
 * @param {*} json 
 */
export const checkFilesAddEventClick = (parent)=>{
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

/**
 * Description add event click img or file
 * @param {Task} task class task
 */
export const checkFilesAddEventDescription = (task)=>{
    const descriptionEl = document.getElementById(MESSENGER_IDS.DESCRIPTION_TASK);
    if(task && descriptionEl){
        const observation = task.getDescriptionJson().observation;
        if(observation) {
            descriptionEl.value = observation;
            checkFilesAddEventClick(descriptionEl.getTextAreaDiv());
        }
    }
}

const changeFormProcess = ({value,name}, aonMessengerChat) => {
    const task = aonMessengerChat.task;
    const processDiv = document.getElementById(MESSENGER_IDS.PROCESS_DIV);
    processDiv.innerHTML = "";
    let sender;
    //CREATE CARD
    let aonCard = createCardMessenger("aonCardProcess", name);
    processDiv.appendChild(aonCard);
    setStyles(aonCard.getCard(), { margin:0, marginTop:"10px" });
    aonCard.getCardTitle1().style.whiteSpace = "pre-wrap";

    if(task.id && aonMessengerChat.getDur().isMessengerManager()){ //BUTTON SHOW JSON
        aonCard.addTitleButton(MSG.VIEW, MATERIAL_ICONS.VISIBILITY, false, () => {
            let d = aonMessengerChat.applicationEl.getDialog();
             if(d){
                d.clear();
                if (!aonMessengerChat.isMobile()) d.width = '400px';
                d.setTitle("JSON");
                d.setContent(jsonDiv());
                d.addAcceptAction(() => {});
                d.open();
              }
        });
    }

    if(aonMessengerChat.isMobile() && task.sender && task.sender.name ) 
        sender = `[${task.sender.name}] ${name}`;
     
    if(value===1){ //FORM VACATION
        if(sender) aonCard.setTitleSection1(sender);
        createFormVacation(aonCard, aonMessengerChat);
    } else if(value ===2) {
        createFormMov(aonCard, aonMessengerChat);
    } else if(value ===3) {
        if(sender) aonCard.setTitleSection1(sender);
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
 * @param {HTMLElement} aon-application
 * @param {HTMLElement} aon-messenger-chat columnns div form
 */
export const buildForm = (div, aonMessengerChat) => {
    const task = aonMessengerChat.task;
    const isAdvisoryCompany = task.isAdvisoryCompany();
    // const aonMessenger = aonMessengerChat.applicationParentEl;
    // const myWorkgroups = aonMessenger ? aonMessenger._workgroups: [];
    // const received =  isReceived(task, myWorkgroups);

    const dataDefault = aonMessengerChat.getData();

    //-----------------------APPEND DIV TAGS
    createTagsDiv(div, task);
    //-----------------------CREATE FIRST CARD
    const aonCard = createCardMessenger(MSG.DATA, "");
    aonCard.flex = "true";
    div.appendChild(aonCard);
    aonCard.getCard().style.margin = 0;
    if( !task.isExternal() && task.registry && task.registry.name){
        const registryName = `[${task.registry.name}]`;
        aonCard.setTitleSection1(registryName)
    }
    //-----------------------END CREATE FIRST CARD

    const divCard = document.createElement(TAG.DIV);
    aonCard.setContent(divCard);

    //-----------------------CREATE DIV PROCESS
    const divProcess = createStartJustifiedColumn().element;
    divProcess.id = MESSENGER_IDS.PROCESS_DIV;
    div.appendChild(divProcess);
    //---------------------END CREATE DIV PROCESS

    const divStatic = createDivGrid(divCard, undefined,{ classes:[CSS.AON_COL_XS_12], styles:{ padding:0 } });

    const divDinamic = createDivGrid(undefined, undefined,{ classes:[CSS.AON_COL_XS_12], styles:{ padding:0 } });

    //-----------------TYPE REQUEST
    const requestTypeSelect = createRequestType();
    if(task.id || dataDefault.source_id) requestTypeSelect.disabled = requestTypeSelect.readonly = true;
    const divRequest = createDivGrid(divStatic, requestTypeSelect, {classes:[CSS.AON_COL_XS_6]});
  
    //-----------------END TYPE REQUEST

    //---------------------IS CAU
    if(aonMessengerChat.isCau()){
        showTags(false);
        task.setSource(TASK_SOURCE.CAU);
        divRequest.style.display = "none";

        const selectTypeRequestCau = createSelectCau('typeCau', MESSENGER_IDS.TYPE_REQUEST_CAU, MSG.TYPE_REQUEST);
        createDivGrid(divStatic, selectTypeRequestCau, {classes:[CSS.AON_COL_XS_6]})
        fillTypeRequestCau(aonMessengerChat);

        const selectApp = createSelectCau('selectApp', MESSENGER_IDS.SELECT_APP, 'Aplicación');
        createDivGrid(divStatic, selectApp, {classes:[CSS.AON_COL_XS_6]})
        fillSelectAppCau(aonMessengerChat);
    } else //if(   (!task.id || task.isExternal()) && !( dataDefault.source_id && [1,3].includes(dataDefault.source_id) ))
    if(!task.id || task.isExternal()){
        let initText = !task.id || task.isExternal() ? 'Para' : 'De';
        let titleBtn = isAdvisoryCompany ?  `${initText} tu ${MSG.CUSTOMER}` : `${initText} tu Gestor`;
        const btnExternal = createAonSwitch(titleBtn);
        createDivGrid(divStatic, btnExternal, {classes:[CSS.AON_COL_XS_6], styles:{ top:'16px',  marginLeft: 0} });
        if(task.id || task.isOtherDomain()) btnExternal.disabled = CONSTANT.TRUE;
        btnExternal.checked = task.isOtherDomain();

        btnExternal.addEventListener(EVENT.CHANGE, ({target}) => {
            console.log("---------------------CHANGE BTN INTERNAL--------");
            changeRequestType(aonMessengerChat, requestTypeSelect, divDinamic, divProcess);
            if(!isAdvisoryCompany && target.checked) 
                showTags(false);
            else 
                showTags(true);
        });
    } else {
        divRequest.className = CSS.AON_COL_XS_12;
    }

    divCard.appendChild(divDinamic);

    requestTypeSelect.addEventListener(EVENT.CHANGE, ()=> changeRequestType(aonMessengerChat, requestTypeSelect, divDinamic, divProcess));

    fillRequestType(task, aonMessengerChat);
}

/**
 * 
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 * @param {Select} requestTypeSelect aon select type request
 * @param {HTMLElement} divDinamic appendChild
 * @param {HTMLElement} divProcess div process
 */
 const changeRequestType = (aonMessengerChat, requestTypeSelect, divDinamic, divProcess) =>{
    const task = aonMessengerChat.task;
    const btnExternal = document.getElementById(MESSENGER_IDS.EXTERNAL_TASK);
    
    // if(!task.id && btnExternal && btnExternal.disabled == CONSTANT.TRUE)
    //     task.cleanTask(true);

    const detail = requestTypeSelect.getDetail();
    if(detail){
        //------------------HTML CLEAN UP
        divDinamic.innerHTML = "";
        divProcess.innerHTML = "";
        const aonTextArea = document.getElementById(MESSENGER_IDS.DESCRIPTION_TASK);
        if(aonTextArea) aonTextArea.remove();
        //------------------HTML CLEAN UP
        if(detail.value){
            task.setSource(detail.value);
            
            if(!task.id)
                task.setTitle(detail.name);
               
            const forManager  = btnExternal ? btnExternal.isChecked() : false;
            if(!forManager) task.setProject({});

            if(detail.value === TASK_SOURCE.REQUEST){
                formRequest(divDinamic, aonMessengerChat, forManager);
            } else {
                formQuery(divDinamic, aonMessengerChat, forManager);
            }
        }
    }
} 

/**
 * 
 * @param {Object} source,status 
 * @returns icon, icon_color
 */
export const getIconJson =({source,status}) => {
    const {AON_MESSENGER_LIST_OPEN,AON_MESSENGER_LIST_CLOSE,AON_MESSENGER_LIST_ARCHIVE} = MessengerOptions;
    let icon = MATERIAL_ICONS.INFO;
    let icon_color = AON_MESSENGER_LIST_OPEN.icon_color;

    if(source===TASK_SOURCE.CAU) 
      icon = MATERIAL_ICONS.SUPPORT_AGENT;
    else if(source===TASK_SOURCE.REQUEST) 
      icon = MATERIAL_ICONS.ASSIGNMENT;
    if(status === TASK_STATUS.FINISHED) 
      icon_color = AON_MESSENGER_LIST_CLOSE.icon_color;
    else if(status === TASK_STATUS.DELETED) 
      icon_color = AON_MESSENGER_LIST_ARCHIVE.icon_color;
    
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
        chat.scrollTo(0, chat.scrollHeight); //GO DOWN
        addLine(chat);
    }
}
/**
 * upChat up chat
 */
export const upChat = () => {
    const chat = document.getElementById(MESSENGER_IDS.MESSENGER_CHAT);
    if(chat)
        chat.scrollTo(0,0) //GO UP   
}

/**
 * 
 * @param {HTMLElement} chat add line element html
 */
const addLine = (chat) => chat.style.setProperty("--height", chat.scrollHeight + "px");

/**
 * 
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 */
const addTaskDescription = (aonMessengerChat) => {
    const task = aonMessengerChat.task;
    const aonTextArea = createAonTextArea(`${MSG.WRITE_A_DESCRIPTION}...`);
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
        if(task && task.getDescriptionJson().observation) aonTextArea.value = task.getDescriptionJson().observation;
        checkFilesAddEventDescription(task);//check files description
        
    } else { //-------------------------------------------------MOBILE 
        const div = document.getElementById(MESSENGER_IDS.SECOND_DIV);
        setStyles(aonTextArea,{ height: "100%",  width: "100%", boxShadow : "none", marginTop : 0 });
        div.appendChild(aonTextArea);
        if(task && task.getDescriptionJson().observation) aonTextArea.value = task.getDescriptionJson().observation;

        // /**
        // * CHANGE STYLE AONTEXTAAREA
        // */
        let textAreaDiv = aonTextArea.getTextAreaDiv();
        if(textAreaDiv) textAreaDiv.style.padding = "20px";
        
        const aonTextAreaToolbar = aonTextArea.getToolbar();
        if(aonTextAreaToolbar){
            setStyles(aonTextAreaToolbar,{
                paddingLeft  : "calc(1.5em - 5px)",
                paddingRight : "calc(1.5em - 5px)",
                borderBottom : "1px solid #e0e0e0"
            });
        }
    }

    buildTextareaToolbar(aonTextArea);
}

/**
 * 
 * @param {HTMLElement} divDinamic columns div append html
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 * @param {Boolean} forManager btn by gestor(forManager)
 */
const formQuery = (divDinamic, aonMessengerChat, forManager = false) => {
    const task = aonMessengerChat.task;
    const applicationParent = aonMessengerChat.applicationParentEl;
    const isAdvisoryCompany = task.isAdvisoryCompany();
    //-------------------------CAU--------------------------
    if((task.source === TASK_SOURCE.CAU && !applicationParent.cau) || forManager){
        if(isAdvisoryCompany)
            addCustomerAndContact(task, aonMessengerChat, divDinamic);
        
        if(forManager){
            // ------------------ADVISORY SELECT
            if(!task.isAdvisoryCompany()){
                const advisorySelect = createAdvisory();
                createDivGrid(divDinamic, advisorySelect, {classes:[CSS.AON_COL_XS_12]});
                fillAdvisory(task);
            }

            // ------------------PROJECT
            const projectSelect = createProject();
            projectSelect.default = true;
            if(task.id) setAttributes(projectSelect, {disabled:CONSTANT.TRUE, readonly:CONSTANT.TRUE});
            createDivGrid(divDinamic, projectSelect, {classes:[CSS.AON_COL_XS_12]});
        } 
    } 

    //--------------------------DIV WORKGROUP AND TASKHOLDER
    if((isAdvisoryCompany || !forManager) && !applicationParent.cau)
        addTaskHolderAndWorkgroup(task, aonMessengerChat, divDinamic);

    addTaskDescription(aonMessengerChat);
}

/**
 * 
 * @param {HTMLElement} divDinamic columns div append html
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 * @param {Boolean} external btn by gestor(external)
 */
const formRequest = (divDinamic, aonMessengerChat, forManager = false) => {
    const task = aonMessengerChat.task;
    const dataDefault = aonMessengerChat.getData();

    //-----------------TYPE PROCESS
    const processType = createProcessType();
    processType.default = CONSTANT.TRUE;
    if(dataDefault.source_id) setAttributes(processType, {disabled:CONSTANT.TRUE, readonly:CONSTANT.TRUE});
    createDivGrid(divDinamic, processType, {classes:[CSS.AON_COL_XS_12]});
    processType.addEventListener(EVENT.CHANGE, ({detail})=>{
        if(detail && detail.value)
            changeFormProcess(detail, aonMessengerChat);
    })
    fillProcessType(task, aonMessengerChat);
    if(task.id) processType.setDisabled(CONSTANT.TRUE);
        
    if(forManager){
        // ------------------ADVISORY SELECT
        if(!task.isAdvisoryCompany()){
            const advisorySelect = createAdvisory();
            createDivGrid(divDinamic, advisorySelect, {classes:[CSS.AON_COL_XS_12]});
            fillAdvisory(task);
        }


        // ------------------PROJECT
        const projectSelect = createProject();
        projectSelect.default = true;
        createDivGrid(divDinamic, projectSelect, {classes:[CSS.AON_COL_XS_12]});
        fillProject(task);
    }

    if((task.isAdvisoryCompany() || !forManager) && !aonMessengerChat.getApplicationParent().cau){
        //--------------------------DIV WORKGROUP AND TASKHOLDER
        addTaskHolderAndWorkgroup(task, aonMessengerChat, divDinamic);
    }
}

/**
 * 
 * @param {Task} task class Task
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 * @param {HTMLElement} divDinamic 
 */
const addTaskHolderAndWorkgroup = (task, aonMessengerChat, divDinamic) => {

    //-----------------WORKGROUP
    const workgroupSelect = createWorkgroup();
    workgroupSelect.default = true;
    createDivGrid(divDinamic, workgroupSelect, {classes:[CSS.AON_COL_XS_6]});
    fillWorkGroup(task, aonMessengerChat);

    //-----------------TASK HOLDER
    const taskHolderSelect = createTaskHolder();
    taskHolderSelect.default = true;
    createDivGrid(divDinamic, taskHolderSelect, {classes:[CSS.AON_COL_XS_6]});
    fillTaskHolder(aonMessengerChat);
}

/**
 * 
 * @param {Task} task class Task
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 * @param {HTMLElement} divDinamic 
 */
const addCustomerAndContact = (task, aonMessengerChat, divDinamic) => {
    // CUSTOMER
    const customerSelect = createCustomer();
    customerSelect.default = true;
    createDivGrid(divDinamic, customerSelect, {classes:[CSS.AON_COL_XS_12]});
    fillCustomer(task, aonMessengerChat);
    // DIV CONTACT
    //-----------------CONTACT
    const contact = createInputContact();
    contact.addEventListener(EVENT.INPUT, ({target})=>{
        if(target.value) task.setGTaskId(target.value)
    });
    createDivGrid(divDinamic, contact, {classes:[CSS.AON_COL_XS_12]});
    if(task.gtask_id) contact.value  = task.gtask_id;
}

/**
 * create div tags
 * @param {HTMLElement} parent insertBefore
 */
const createTagsDiv = (parent, task) => {
    const div = createReceiverDiv().element;
    div.style.flexWrap     = "wrap";
    div.id = MESSENGER_IDS.DIV_TASK_TAGS;
    parent.insertBefore(div, parent.firstChild);

    let tags = task.getDescriptionJson().tags || [];
    for (let tag of tags) 
        addTaskTag(tag, div);
}

export const dialogTaskTags = (ev, aonMessengerChat) => {
    const rect = ev.target.getBoundingClientRect();
    const x = ev.clientX - rect.left + 180;
    const y = ev.clientY - rect.top;
    const top  = rect.top + y;
    let left = rect.left + x;
    const dialog = aonMessengerChat.getApplication().getOptionDialog();
    dialog.clear();
    dialog.setContentTitle(MSG.TAGS);

    const div = setStyles(document.createElement("div"),{ margin:"5px", display:"flex", flexDirection:"column" });
  
    const divTaskTags = document.getElementById(MESSENGER_IDS.DIV_TASK_TAGS);

    const tags = aonMessengerChat.getApplicationParent()._tags;
    
    const taskTags = aonMessengerChat.task.getDescriptionJson().tags || [];
    
    for (let tag of tags) {
        let aonCheckbox = new AonCheckbox();
        aonCheckbox.description = tag.name;
        aonCheckbox.checked = taskTags.find(t=>t.id ===tag.id) ? true : false;
        aonCheckbox.addEventListener(EVENT.CHANGE, ({target})=>{
          if(target.checked)
            addTaskTag(tag, divTaskTags);
          else 
            removeTaskTag(tag.id);
        })
        div.appendChild(aonCheckbox);
    }

    dialog.setContent(div);
    dialog.openPosition({top, left});
}
/**
 * 
 * @param {Object} tag 
 * @param {HTMLElement} parent div appendChild
 */
const addTaskTag = ( tag, parent) =>{
    appendTaskTag(tag, parent, (id)=> removeTaskTag(id));
    setTaskTags();
}

/**
 * 
 * @param {Number} id id tag
 */
const removeTaskTag = (id) => {
    let divOne = document.querySelector( `[data-task-tag='${id}']`);
    if(divOne) 
        divOne.remove();

    setTaskTags();
}

const setTaskTags = () => {
    const arrayTags = [];
    [...document.querySelectorAll("[data-task-tag]")].map(el => arrayTags.push(parseInt(el.dataset.taskTag)));

    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    const task = aonMessengerChat.task;
    let tags = task.getDescriptionJson().tags || [];
    tags = aonMessengerChat.getApplicationParent()._tags.filter(tag=> arrayTags.includes(tag.id));
    task.setDescriptionJson({tags});
}

/**
 * 
 * @param {Boolean} b 
 */
const showTags = (b) => {
    
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    if(aonMessengerChat) {
        // if(!aonMessengerChat.task.isExternal()) 
        //     b = false;
        const toolbar = aonMessengerChat.getApplication().getToolbar();
        if(toolbar){
            const iconBtn = toolbar.querySelector(`#${toolbar.TOOL_SECTION}LabelsButton`);
            if(iconBtn){
                const buttonToolbar = iconBtn.parentNode;
                let display = "block";
                if(!b) {
                    display = "none";
                    [...document.querySelectorAll("[data-task-tag]")].map(el => el.remove());
                }
                buttonToolbar.style.display = display;
                setTaskTags();
            }
        }
    }
}

/**
 * 
 * @param {Task} task class task 
 * @param {Array} workgroups my workgrouprs
 * @returns 
 */
// const isReceived = (task, workgroups) => {
//     let condition = false;
//     try {
//         if(task.id){
//             const isWorkgroup = workgroups.some(({id})=> id === task.workgroup.id );
//             condition = task.isAdvisoryCompany()
//             ? 
//                 (task.task_holder.id === task.myTaskHolder.id) || isWorkgroup 
//             : 
//                 task.sender.id && task.gtask_id === task.auth.email;
//         }
//     } catch (error) {console.log(error);}
//     return condition;
// }