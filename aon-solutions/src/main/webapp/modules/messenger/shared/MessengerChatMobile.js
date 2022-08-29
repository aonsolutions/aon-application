import { AonToolbar } from "../../../components/aon-toolbar.js";
import { COLORS, CSS, EVENT, MSG, TAG, MATERIAL_ICONS, AON_ICONS } from "../../../environments/environments.js";
import { ToolbarType } from "../../../models/enums.js";
import { newComponent, setAttributes, setStyles} from "../../../services/utilsComponents.js";
import {  MessengerOptions, MESSENGER_COMPONENTS, MESSENGER_IDS, MESSENGER_VIEWS, TASK_EVALUATION, TASK_SOURCE, TASK_STATUS } from "../MessengerEnums.js";
import { TaskCreationUtils} from "./TaskCreationUtils.js";
import { addIconToolbar, buildForm, buildTextareaToolbar, taskNumberParse } from "./utils.js";
import * as ACTIONS from "../../actions.js";
/**
 * 
 * @param {Task} task 
 */
export const buildMobile = (task)=> {
    const mainView = TaskCreationUtils.createMobileMainView();
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    aonMessengerChat.appendChild(mainView);

    const wrapper = createFirstDiv(mainView);

    if(task.getId()){
        buildSectionHistoric(task, wrapper);
    }

    const secondDiv = createSecondDiv(task, mainView);

    buildForm(task, secondDiv);

    if(!task.getId()){
        showForm(true);
    }
}

/**
 VIEW EDIT TASK MOBILE
 * @param {HTMLElement} aonMessengerChat 
 * @param {HTMLElement} wrapper div wrapper
 */
const buildSectionHistoric = (task, wrapper)=>{
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    buildToolbar(task, wrapper, false);
    
    let titleText = task.title;
    if(task.registry && task.registry.name)
        titleText = `<b>[${task.registry.name}]</b> ${task.title}`;
    else if(task.sender && task.sender.name)
        titleText = `<b>[${task.sender.name}]</b> ${task.title}`;

    const title = setStyles(TaskCreationUtils.createTitle(titleText),{
        display : 'block',
        fontSize: '1.3em',
        padding: '10px 18px',
        width : '100%',
        borderBottom : "1px solid " + CSS.variable(COLORS.AON_LIGHT_GRAY)
    });
    wrapper.appendChild(title);

        /**
     * The chat itself
     */
    const chat = TaskCreationUtils.createChat();
    chat.classList.add(CSS.NO_SCROLLBAR);
    wrapper.appendChild(chat);

    const firstDiv = document.getElementById(MESSENGER_IDS.FIRST_DIV);

    if( [TASK_STATUS.IN_PROGRESS, TASK_STATUS.PENDING].includes(task.status) ){
        addTextAreaChat(aonMessengerChat, firstDiv);
    } else if(task.evaluation){        // create section rating section
        const evaluation = task.evaluation;

        let section = TaskCreationUtils.createSectionRating(firstDiv, true);

        Object.values(TASK_EVALUATION)
        .forEach((r)=>{
            const selected = r === evaluation;
            section.appendChild(TaskCreationUtils.createIconEvaluation(`../../../assets/img/evaluation/${r}.png`, selected));
        });
    } else {
        firstDiv.style.height = "100%";
    }

}

const addTextAreaChat = (aonMessengerChat, firstDiv) => {
    const div = newComponent({
        classes: [CSS.FLEX_ROW],
        styles: {
            width: "100%",
            position: "absolute",
            borderTop: "1px solid #ddd",
            bottom: 0
        },
    });
    div.appendTo(firstDiv);

    const divs = TaskCreationUtils.createSectionComment(div);
    divs.iconOpenFull.addEventListener(EVENT.CLICK, ()=>showFullComment(true));
    divs.iconSend.addEventListener(EVENT.CLICK, ()=> aonMessengerChat.saveComment());
    changeStyleSectionComment(divs);

    buildFullComment(aonMessengerChat, divs.aonTextArea);
    
}

/**
 * Build mobile version of the writter 
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat component 
 * @param {HTMLElement} aonTextArea textarea principal
 */
const buildFullComment = (aonMessengerChat, aonTextArea) => { 
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
        aonMessengerChat.saveComment();
        showFullComment(false);
    });
    bar.addButton2(ACTIONS.BACK,() => showFullComment(false));

    const textarea = setStyles(TaskCreationUtils.createAonTextArea(), {
        flexDirection: 'column',
        height: '100%',
        width: '100%',
        boxShadow: "none",
        background: CSS.variable(COLORS.AON_WHITE),
        margin: 0,
    });
    
    writter.appendChild(textarea);
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
    textarea.addEventListener(EVENT.INPUT, ()=>{
      aonTextArea.value = textarea.value || "";
      aonTextArea.FILES = textarea.FILES;
    });
}


const changeStyleSectionComment = (divs) => {
    setStyles(divs.aonTextArea,{
       fontSize: "15px",
       margin:"0",
       minHeight:"55px"
    });

    const size = "1.8em";
    divs.divComment.style.background = "#fff";
    divs.iconOpenFull.querySelector(TAG.I).style.fontSize = size;
    divs.iconSend.querySelector(TAG.I).style.fontSize = size;
}

/**
 * 
 * @param {Task} task 
 * @param {HTMLElement} div 
 * @param {Boolean} create form create true or false
 */
const buildToolbar = (task, div, create = false) => {
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);

    /**
     * Building toolbars
     */
     const sourceText =  MSG[task.getSource().toString().toUpperCase()] || task.getSource();
     const toolbar = setAttributes(new AonToolbar(),{
        type: ToolbarType.SECONDARY,
        title:sourceText +" " +taskNumberParse(task.number)
    });
    toolbar.style.width = "100%"; 
    
    div.appendChild(toolbar);

    if(create){
        const status = task.getStatus();
        if(task.getId()){

            if([TASK_STATUS.PENDING, TASK_STATUS.IN_PROGRESS].includes(status) ){

                if(!aonMessengerChat.isCau()){
                    toolbar.addButton2({
                        id: MESSENGER_IDS.TOOLBAR_BRANCH,
                        name: "Crear Rama",
                        aonIcon: AON_ICONS.AON_BRANCH,
                    }, () =>TaskCreationUtils.openDialogBranch(task));
                }

                toolbar.addButton2({
                  ...MessengerOptions.AON_MESSENGER_LIST_CLOSE,
                  name: MSG.CLOSE,
                  icon:MATERIAL_ICONS.CHECK_CIRCLE_OUTLINE
                }, () =>{
                  aonMessengerChat.closeTask();
                });
            }

            if([TASK_STATUS.DELETED, TASK_STATUS.FINISHED].includes(status)){
                toolbar.addButton2({...ACTIONS.RESTORE, name:MSG.REOPEN}, () => aonMessengerChat.updateTaskStatus(TASK_STATUS.PENDING));
            }
        
            if(status != TASK_STATUS.DELETED) {
                toolbar.addButton2({...MessengerOptions.AON_MESSENGER_LIST_ARCHIVE, name:MSG.STORE,  icon: MATERIAL_ICONS.ARCHIVE}, () => aonMessengerChat.updateTaskStatus(TASK_STATUS.DELETED));
            }
        }
    
        if([TASK_STATUS.PENDING, TASK_STATUS.IN_PROGRESS].includes(status)){
            toolbar.addButton2(ACTIONS.SAVE,() => {
                aonMessengerChat.save()
                .then((success) =>{
                    if(success) {
                        aonMessengerChat.showMessage();
                    }
                });
            });
        }
    } else {
        if(task.getSource() === TASK_SOURCE.PROCESS){
            toolbar.addButton2({...ACTIONS.SHOW_FILE, name:MSG.TO_SHOW},() => showForm(true, true));
        }

        toolbar.addButton2(ACTIONS.EDIT,() => showForm(true));
    }
    
    toolbar.addButton2(ACTIONS.BACK,() =>{
        if(task.getId() && create) {
            showForm(false);
        } else {
            aonMessengerChat.back();
        }
    });

    addIconToolbar(toolbar, task);
}

/**
 * 
 * @param {Boolean} b true or false 
 */
const showForm = (b, cardDataHidden = false) => {
    const divMainTwo = document.getElementById(MESSENGER_IDS.DIV_MAIN_MOBILE);
    const styles = b ? {zIndex: 9, opacity: 1, left: 0} : {zIndex : -9, opacity : 0};

    setStyles(divMainTwo, styles);

    let aonCardDate = document.getElementById(MSG.DATA);
    if(aonCardDate) {
        aonCardDate.style.display = cardDataHidden ? "none": "block";  
    }
    hiddenBtnToolbar(divMainTwo, cardDataHidden, ACTIONS.BACK.id);
}

/**
 * 
 * @param {Boolean} b true or false 
 */
const showFullComment  = (b) => {
    const writter = document.getElementById(MESSENGER_COMPONENTS.WRITTER);
    const textarea = writter.querySelector(TAG.AON_TEXTAREA);
    const aonTextArea = document.getElementById(MESSENGER_IDS.COMMENT_TASK)
    if(b){
        if(aonTextArea.value) {
            textarea.value = aonTextArea.value;
        }
        setTimeout(() => setStyles(writter, {display: "flex", zIndex: 9,opacity: 1,left: 0}), 100);
    } else {
        setTimeout(() => setStyles(writter, {zIndex : -9, opacity : 0}), 100);
    }
} 


const createFirstDiv = (mainView) => {
    const firstDiv = newComponent({
        classes: [CSS.FLEX_ROW],
        id: MESSENGER_IDS.FIRST_DIV,
        styles: {
          width: "100%",
          height: "90%",
          maxWidth: "600px",
          padding: "0",
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
    }).element;
    firstDiv.element.appendChild(wrapper);
    return wrapper;
}
  

const createSecondDiv = (task, mainView) => {
    let div = newComponent({
        type: TAG.DIV,
        id:MESSENGER_IDS.DIV_MAIN_MOBILE,
        classes : [CSS.FLEX_COLUMN, CSS.FLEX_ALIGN_CENTER],
        styles : {
            height: '100%',
            width: '100%',
            background: CSS.variable(COLORS.AON_WHITE),
            position: 'absolute',
            top: '0%',
            opacity: 0,
            zIndex: -9
        }
    });
    div.appendTo(mainView);

    buildToolbar(task, div, true);

    const secondDiv = document.createElement(TAG.DIV);
    secondDiv.id = MESSENGER_IDS.SECOND_DIV;
    secondDiv.className = CSS.AON_MOBILE_SUB_CONTENT;
    secondDiv.style.width = "100%";
    div.appendChild(secondDiv);
    return secondDiv;
}
  
/**
 * 
 * @param {HTMLElement} parent
 * @param {Boolean} b true or false
 * @param {String} exclude name exclude
 */
const hiddenBtnToolbar =(parent, b, exclude) => {
    const toolbar =  parent.querySelector(TAG.AON_TOOLBAR);
    if(toolbar){
        const section = toolbar.getToolSection();
        if(section){
            [...section.querySelectorAll(TAG.SPAN)].forEach(el=>{
                const child  = el.firstChild;
                if(exclude && child && child.id.toString().includes(exclude)){
                    el.style.display = "block";
                } else {
                    el.style.display = b ? "none" : "block";
                }
            });
        }
    }
}