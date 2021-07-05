import { AonIconButton } from "../../../components/aon-icon-button.js";
import { AonSelect } from "../../../components/aon-select.js";
import { AonTextArea } from "../../../components/aon-textarea.js";
import { AonToolbar } from "../../../components/aon-toolbar.js";
import { COLORS, CSS, MATERIAL_ICONS, MSG } from "../../../environments/environments.js";
import { ToolbarType } from "../../../models/enums.js";
import { getTastHoldersWorkGroup } from "../../../services/taskHolderService.js";
import { newComponent, setAttributes, setClasses, setEvents, setFullDate, setStyles, setTime, waitChildEl, waitEl } from "../../../services/utils.js";
import * as ACTIONS from "../../actions.js";
import { createAction, createDivEditable, createMessageAuthor, createMessageBox, createMessageContent, createTitle } from "../createComponents.js";
import { ICON_TYPES, TASK_WORKFLOW_TYPE, MESSENGER_COMPONENTS, MESSENGER_IDS, MESSENGER_VIEWS } from "../MessengerEnums.js";
import { createOutlinedMaterialIcon, createSpaceBetweenRow, createText } from "./creationUtils.js";
import { buildMobileWritter } from "./messenger-writter.js";

export const RIGHT = "RIGHT";
export const LEFT = "LEFT";

/**
 * Create desktop chat for messenger (mobile)
 * @param {*} parent 
 * @param {*} data 
 */
export const buildMobileChat = (chatEl, data, parent) => {
    const application = parent.getApplication();
    chatEl.element.style.padding = 0;
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
    wrapper.appendTo(chatEl.element);

    if(!data.title){
        const newRequestPanel = newComponent({
            type: 'div',
            id : MESSENGER_IDS.NEW_REQUEST_PANEL,
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
        /**
         * Building toolbars
         */
        const toolbars = setAttributes(new AonToolbar(),{
            id: MESSENGER_IDS.NEW_REQUEST_PANEL_TOOLBAR,
            type: ToolbarType.SECONDARY,
            title: "Nueva solicitud"
        });
        toolbars.style.width = "100%"; 
        newRequestPanel.appendChild(toolbars);
        toolbars.addButton2(ACTIONS.SAVE,() => {
            parent.save();
        })
        
        toolbars.addButton2(ACTIONS.BACK,() => {
            wrapper.element.style.transition = ".25s";
            wrapper.element.style.opacity = "0";
            setTimeout(() => {
                const button = document.getElementById("aonMessengerSidenavAbiertas");
                button.click();
            }, 250);
        });

        waitChildEl(toolbars,"header").then(header => {
            setStyles(header,{
                margin : 0,
                paddingLeft : "1.5em",
                paddingRight : "1.5em",
            });
        });


        const div = document.createElement("div");
        div.className = CSS.AON_MOBILE_SUB_CONTENT;
        div.style.width = "100%";
        newRequestPanel.appendChild(div);



        /**
         * Creating title input
         */
        const titleIn = setStyles( createDivEditable(data.title, MESSENGER_IDS.TITLE_TASK, "Escriba su titulo aquí"), {
            display : "block",
            width: "100%",
        });
        div.appendChild(titleIn); 

         /**
         * Creating description input
         */
        const descriptionIn = setStyles( createDivEditable(data.description, MESSENGER_IDS.DESCRIPTION_TASK, MSG.DESCRIPTION), {
            display : "block",
            width: "100%",
        });
        div.appendChild(descriptionIn); 

        /**
         * smooth border colors
         */
         waitChildEl(titleIn,"input").then(el => {
            setStyles(el,{
                borderBottom : "1px solid #e0e0e0",
                marginBottom : 0,
                paddingLeft : "1.5em",
                paddingRight : "1.5em",
                transition : "background-color .25s"
            });
        });

        /**
         * Adjust the space issues
         * related to AonInput defaults
         */
        waitChildEl(titleIn,"span").then(el => {
            setStyles(el,{
                paddingLeft : "1.5em",
                paddingRight : "1.5em",
                transition: ".25s",
                top : "5%"
            });
        });

        waitChildEl(titleIn,".aonInputGroup").then(el => {
            el.style.marginBottom = "0px"
        });


        //----------------WORKGROUP
        const workgroupSelect = setAttributes(new AonSelect(),{
            id:MESSENGER_IDS.NEW_REQUEST_PANEL_RECEIVER,
            name:"Para",
            title:"Para",
        });
        setStyles(workgroupSelect, {
            display : "block",
            width: "100%",
        });
        div.appendChild(workgroupSelect);
        fillWorkGroup(workgroupSelect,data, application);
        changeStyleSelect(workgroupSelect);

          //-----------------TASK HOLDER
        const taskHolderSelect = setAttributes( new AonSelect(),{
            id: MESSENGER_IDS.TASKHOLDER,
            name: MESSENGER_IDS.TASKHOLDER,
            title: "Asignar a"
        });
        setStyles(taskHolderSelect, {
            display : "block",
            width: "100%",
        });

        div.appendChild(taskHolderSelect);
        changeStyleSelect(taskHolderSelect);


        // /**
        //  * Creating text area
        //  */
        // const aonTextArea = new AonTextArea();
        // aonTextArea.id = MESSENGER_IDS.COMMENT_TASK;
        // aonTextArea.name = MESSENGER_IDS.COMMENT_TASK;
        // setStyles(aonTextArea,{
        //     height: "100%",
        //     width: "100%",
        //     marginTop : 0,
        //     boxShadow : "none",
        // })

        /**
        * smooth border colors
        */
        // waitChildEl(aonTextArea,"toolbar").then(el => {
        //     setStyles(el,{
        //         paddingLeft  : "calc(1.5em - 5px)",
        //         paddingRight : "calc(1.5em - 5px)",
        //         borderBottom : "1px solid #e0e0e0"
        //     });
        // });

        // div.appendChild(aonTextArea);
  
    }

    
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
    setStyles(title.element,
        {
            display : 'block',
            fontSize: '1.3em',
            paddingTop: "10px",
            paddingBottom: "10px",
            width : '100%',
            background : "#fff",
            borderBottom : "1px solid " + CSS.variable(COLORS.AON_LIGHT_GRAY)
        }
    );

    title.appendTo(chat.element);


    const start = newComponent({ id: MESSENGER_IDS.START, 
        styles : {
            padding : '10px' 
        }
    });
    start.appendTo(chat.element);

    /**
     * If no message, put one ;)
     */
    // if(data && data.workflows && data.workflows.length){
    //     data.workflows.forEach(element => {
    //         if (element.type === TASK_WORKFLOW_TYPE.ACTION) {
    //             const action = createAction(chooseActionIcon(element.action), element.message);
    //             action.appendTo(chat.element);
    //         }
    
    //         if (element.type === TASK_WORKFLOW_TYPE.MESSAGE) {
    //             const message = createChatMessage({
    //                 name: element.sender,
    //                 message: element.message,
    //                 id: "noId",
    //                 date: element.date,
    //                 attach: element.attach,
    //                 direction:null
    //             });
    //             message.appendTo(chat.element);
    //         }
    //     });
    // } else {
    //     let noMessage = newComponent({
    //         type : MESSENGER_COMPONENTS.ADVICE,
    //         id   : MESSENGER_IDS.NO_MESSAGES,
    //         text : 'No hay mensajes en esta solicitud',
    //         styles : {
    //             fontSize : '1em',
    //             color : CSS.variable(COLORS.GRAYSON),
    //         }
    //     });
    //     noMessage.appendTo(chat.element);
    // }


    const end = newComponent({ 
        id: MESSENGER_IDS.END, 
        styles : {
            padding : '10px'
        }
    });
    end.appendTo(chat.element);

    buildMobileWritter(wrapper,data)
}

/**
 * Create desktop chat for messenger
 * @param {*} parent 
 * @param {*} data 
 */
export const buildChat = (parent) => {
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
     * Component for easy scroll to the start
     */
    const start = newComponent({ id: MESSENGER_IDS.START, styles : {
        padding : '10px'
    } });
    start.appendTo(chat.element);

    const end = newComponent({ id: MESSENGER_IDS.END, styles : {
        padding : '10px'
    } });
    end.appendTo(chat.element);

    chat.appendTo(wrapper.element);
    wrapper.appendTo(parent.element);

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
    leftButtonBar.appendTo(parent.element);

    const upIcon = setAttributes(new AonIconButton(), {
        icon: MATERIAL_ICONS.EXPAND_LESS,
        id: "upIcon",
        background: "transparent",
    });
    upIcon.onclick = () => start.element.scrollIntoView();
    leftButtonBar.appendChild(upIcon);

    const downIcon = setAttributes(new AonIconButton(), {
        icon: MATERIAL_ICONS.EXPAND_MORE,
        id: "downIcon",
        background: "transparent",
    });
    downIcon.onclick = () =>  end.element.scrollTop();
    leftButtonBar.appendChild(downIcon);
}

/**
 * Append a new message to the chat with a little animation
 * @param {*} properties 
 */
export const appendChatMessage = (properties) => {
    const noMessage = document.getElementById(MESSENGER_IDS.NO_MESSAGES);
    const chat = document.querySelector(MESSENGER_COMPONENTS.CHAT);
    const end = chat.querySelector("#" + MESSENGER_IDS.END);
    
    if(noMessage)
        chat.removeChild(noMessage);

    const message = createChatMessage(properties);
    setStyles(message.element, {
        opacity : 0,
        marginTop : '20px',
        transition : ".25s"
    });

    chat.removeChild(end);
    message.appendTo(chat);
    chat.appendChild(end);

    end.scrollIntoView();

    /**
     * Appearing animation
     */
    setTimeout(() => {
        setStyles(message.element, {
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
export const createChatMessage = (properties) => {
    properties = checkProperties(properties);

    const message = createMessageBox(properties);
    const name = createMessageAuthor(properties);
    const description = createMessageContent(properties);
    const footer = createSpaceBetweenRow({ paddingTop: '5px', height: "20px" });

    const dateObject = properties.date;
    let dateString =  dateObject.getDate() + "/" + dateObject.getMonth() + "/" + dateObject.getFullYear();

    const date = createText({
        text: dateString,
        color: CSS.variable(COLORS.AON_GRAY),
        fontSize : '.7em'
    });

    date.appendTo(name.element);
    name.appendTo(message.element);
    description.appendTo(message.element);
    footer.appendTo(message.element);

    return message;
}

/**
 * Check the properties of the message
 * AVOID showing null or undefined in UI.
 * @param {*} properties 
 * @returns Valid properties object.
 */
const checkProperties = (properties) => {
    if (!properties.name)
        properties.name = ""

    if (!properties.direction || (properties.direction != RIGHT && properties.direction != LEFT))
        properties.direction = LEFT;

    if (!properties.message)
        properties.message = ""

    if (!properties.attach)
        properties.attach = [];

    if (!properties.date)
        properties.date = "";

    return properties;
}

/**
 * Fill aonSelect with possible receivers
 * @param {*} aonSelect 
 */
export const fillWorkGroup = (aonSelect, data, application) => {
    try {
        aonSelect.onchange = ({detail})=>{
            if(detail && detail.value)
                fillTaskHolder(detail.value);
        }
        const workgroups = application.getParent()._workgroups;
        if(workgroups && workgroups.length>0){
            aonSelect.options = JSON.stringify(workgroups);
        }
        if(data &&  data.workgroup) aonSelect.value = data.workgroup;
    } catch (error) { console.log(error);}
}

const fillTaskHolder = async (workgroupId, data=undefined) => {
    try {
        const aonSelect = document.getElementById(MESSENGER_IDS.TASKHOLDER);
        if(aonSelect){
            const taskHolders = await getTastHoldersWorkGroup({workgroupId});
            if(taskHolders && taskHolders.length>0){
                aonSelect.options = JSON.stringify(taskHolders);
            }
            if(data &&  data.taskHolder) aonSelect.value = data.taskHolder;
        }
    } catch (error) {console.log(error);}
}


export const changeStyleSelect = (aonSelect) => {
    waitChildEl(aonSelect,"input").then(el => {
        setStyles(el,{
            borderBottom : "1px solid #e0e0e0",
            marginBottom : 0,
            paddingLeft : "1.5em",
            paddingRight : "1.5em",
            transition : "background-color .25s"
        });
    });

    /**
     * Adjust the space issues
     * related to AonInput defaults
     */
    waitChildEl(aonSelect,"span").then(el => {
        setStyles(el,{
            paddingLeft : "1.5em",
            paddingRight : "1.5em",
            transition: ".25s",
            top : "5%"
        });
    });

    waitChildEl(aonSelect,".aonInputGroup").then(el => {
        el.style.marginBottom = "0px"
    });
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
            const applicationParent = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT).getApplicationParent();
    
            const meId = applicationParent.SENDER.id;
    
            workflows.forEach(workflow => {
                const {comment, type, modification_date, task_holder:{name,alias,id}} = workflow;
                const me = id == meId; // if taskHolder id is me
                const message = {
                    name: me ? "Yo" : alias,
                    direction: me ? RIGHT : LEFT,
                    message: comment,
                    id: "is",
                    date: new Date(modification_date),
                    type,
                    attach: workflow.attach
                }
                if (type == TASK_WORKFLOW_TYPE.COMMENT) {
                    const messageEl = createChatMessage(message);
                    messageEl.appendTo(chat);
                } else{
                    message.name = name;
                    const actionJson = chooseIconMessage(message);
                    const action = createAction(actionJson, actionJson.message);
                    action.appendTo(chat);
                }
            });
        }
   })
}

/**
 * Choose icon for the actions
 * @param {*} actionType 
 * @returns 
 */
 const chooseIconMessage = ({type, date, name}) => {
    const dateParse = setFullDate(date) + " " + setTime(date);
    let actionIcon = {
        icon : MATERIAL_ICONS.INFO,
        type : ICON_TYPES.MATERIAL_OUTLINED,
        color : CSS.variable(COLORS.MATERIAL_BLUE),
        message: `${type} por <b>${name ? name : null}</b> ${dateParse}`
    }
    if(TASK_WORKFLOW_TYPE.OPEN.indexOf(type)>=0){
        actionIcon.color = CSS.variable(COLORS.ONLINE_GREEN);
    }  else if(TASK_WORKFLOW_TYPE.CLOSE.indexOf(type)>=0){
        actionIcon.icon = MATERIAL_ICONS.CLOSE;
    } 
    return actionIcon;
}

