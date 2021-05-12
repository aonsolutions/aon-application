import { AonTextArea } from "../../components/aon-textarea.js";
import { COLORS, CSS, MATERIAL_ICONS } from "../../environments/environments.js";
import { newComponent, setEvents, setStyles, waitChildEl } from "../../services/utils.js";
import { createAttachHistory, createAttachTitle, createAttachTitleText, createButtonWrapper, createEditableTitle, createExpandIcon, createReceiverDiv, createReceiverselect, createReceiverTitle, createSendBar, createSendButton, createSendIcon, createUpload, createUploadIcon, createUploadText } from "./createComponents.js";
import { createMaterialIcon, createOutlinedMaterialIcon, createSpaceBetweenRow, createStartJustifiedRow, createText } from "./creationUtils.js";
import { bold, compileHTML, italic, link, list, tab } from "./markup.js";
import { appendChatMessage, LEFT, RIGHT } from "./messenger-chat.js";
import { MESSENGER_COMPONENTS, MESSENGER_VIEWS } from "./MessengerEnums.js";


let me = true;

export const buildDesktopWritter = (parent, data) => {
    const titleDiv = createStartJustifiedRow();
    const title = createEditableTitle(data.title);

    const infobar = createSpaceBetweenRow();
    setStyles(infobar.element, {
        width: "100%",
        borderBottom: "1px solid " + CSS.variable(COLORS.AON_LIGHT_GRAY),
        marginTop: "10px",
        marginBottom: "20px",
        paddingBottom: "2px",
    })


    const left = createStartJustifiedRow();
    const right = createStartJustifiedRow();

    const status = createOutlinedMaterialIcon({
        name: "info",
        color: "#5cb85c",
        size: "25px"
    });
    status.element.style.marginLeft = "10px"

    const statusText = createText({
        text: "Abierta",
        fontSize: "1.1em",
        color: "gray",
    });

    const id = createText({
        text: "<span style='color: var(--aonBlue)'>#" + data.id + "</span>",
        fontSize: "1.8em",
        fontWeight : 400,
        paddingTop: "10px", 
        paddingBottom: "10px", 
        color: "gray",
    });

    statusText.appendTo(right.element);
    status.appendTo(right.element);
    id.appendTo(left.element);

    left.appendTo(infobar.element);
    right.appendTo(infobar.element);

    const receiverDiv = createReceiverDiv();
    const receiverTitle = createReceiverTitle();
    const receiverSelect = createReceiverselect();

    const aonTextArea = new AonTextArea();
    aonTextArea.style.height = "300px";

    buildTextareaToolbar(aonTextArea);

    const sendBar = createSendBar();
    const upload = createUpload();
    const uploadIcon = createUploadIcon();
    const uploadText = createUploadText();

    const sendButtonWrapper = createButtonWrapper();
    const sendButton = createSendButton();

    setEvents(sendButton.element,{
        click : () => {

            const value = aonTextArea.compiledValue;
            const parent = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
            const data = parent.data;
            aonTextArea.clear();
            
            if(!value || value === "") return;
            const message = {
                type: "message",
                sender: "Tú",
                message: value,
                date: new Date()
            }
            data.content.push(message);

            appendChatMessage({
                name: me ? message.sender : "Receptor@email.com",
                message: message.message,
                id: "id",
                date: message.date,
                attach: message.attach,
                direction : me ?  RIGHT : LEFT
            });

            /**
             * Setting the chat line once all is rendered
             * DO NOT change this, is compulsory.
             */
            const lined = document.querySelector(".continueLined");
            if (lined)
                lined.style.setProperty("--height", lined.scrollHeight + "px");
             

            me =! me;
            parent.data = data;
        }
    });

    const sendIcon = createSendIcon();


    title.appendTo(titleDiv.element);

    receiverTitle.appendTo(receiverDiv.element);
    receiverDiv.element.appendChild(receiverSelect);

    uploadIcon.appendTo(upload.element);
    uploadText.appendTo(upload.element);
    upload.appendTo(sendBar.element);

    sendIcon.appendTo(sendButton.element);
    sendButton.appendTo(sendButtonWrapper.element);
    sendButtonWrapper.appendTo(sendBar.element);

    //main elements append
    
    infobar.appendTo(parent.element);
    titleDiv.appendTo(parent.element);

    receiverDiv.appendTo(parent.element);
    parent.appendChild(aonTextArea);

    sendBar.appendTo(parent.element);
}

/**
 * Set markup to selection
 * @param {*} element 
 * @param {*} funct 
 */
const setSelectionMarkup = (element, funct, conditions) => {
    
    const text = element.value;
    let start = element.selectionStart;
    if (start == -1)
        return;
    
    let indexOfNextSpace = text.indexOf(" ",start);
    
    if(indexOfNextSpace == -1)
        indexOfNextSpace = text.length

    let selection = text.substr(start, indexOfNextSpace);

    if (conditions(text, selection , start, end)){

        let pre = "";
        let compiled = funct(selection);
        let post = "";

        if(start !== 0)
            pre = text.substr(0, start);
        
        if(indexOfNextSpace !== text.length)
            post = text.substr(end, text.length);


        element.value = pre + compiled + post;
    }
}

/**
 * Build mobile version of the writter 
 * @param {*} parent 
 * @param {*} data 
 */
export const buildMobileWritter = (parent) => {
       
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

    const bar = newComponent({
        classes: [
            CSS.FLEX_ROW,
            CSS.FLEX_ALIGN_CENTER,
            CSS.FLEX_JUSTIFY_BETWEEN
        ],
        styles: {
            padding: "10px",
            paddingTop: "5px",
            paddingBottom: "5px",
            color: "gray",
            background : CSS.variable(COLORS.AON_WHITE),
            borderBottom: "1px solid " + CSS.variable(COLORS.AON_LIGHT_GRAY)
        }
    });

    const back = createOutlinedMaterialIcon({
        name: MATERIAL_ICONS.ARROW_BACK,
        color: CSS.variable(COLORS.AON_DARK_GRAY),
        size: "20px"
    });
   back.element.style.marginLeft = "16px"

    const icons = newComponent({
        classes : [CSS.FLEX_ROW,CSS.FLEX_ALIGN_CENTER]
    });

    const save = createMaterialIcon({
        name: MATERIAL_ICONS.SAVE,
        color: CSS.variable(COLORS.AON_DARK_GRAY),
        size: "20px"
    });

    save.element.style.marginLeft = "10px"
   
    const comment = createText({
        text :  "Comentario",
        color : "gray",
        size : "1.2em",
        fontWeight : 600
    });
    comment.element.style.paddingLeft = "5px";

    back.appendTo(icons.element);
    save.appendTo(icons.element);
    
    icons.appendTo(bar.element);
    comment.appendTo(bar.element);
    bar.appendTo(writter.element);

    const textarea = new AonTextArea();
    setStyles(textarea, {
        flexDirection: 'column',
        height: '100%',
        width: '100%',
        boxShadow: "none",
        margin: 0,
    });

    buildTextareaToolbar(textarea);

    waitChildEl(textarea, "#" + textarea.TEXTAREA).then(writtable => {
        setStyles(writtable, {
            resize: "none",
            height: "100%",
        });
    });

    waitChildEl(textarea, "#" + textarea.TOOLBAR).then(toolbar => {
        setStyles(toolbar, {
            background: CSS.variable(COLORS.AON_LIGHT_GRAY),
            border: "none",
            padding: "10px",
            height: "40px",
            justifyContent: "flex-start"
        });
    });

    setEvents(back.element,{
       click : () => {
          /**
          * Showing float button
          */
          let button = document.querySelector("#aonMessengeraddButtonIconButton")
          setStyles(button , {
              transition : "0.25s",
              opacity : "1"
          });
          setTimeout(() => button.style.display = "block", 100);

          hideWritter();
       } 
    });


    setEvents(save.element,{
        click : () => {

            const value = textarea.compiledValue;
            const parent = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
            const data = parent.data;
            textarea.clear();
            
            if(!value || value === "") return;
            const message = {
                type: "message",
                sender: "Tú",
                message: value,
                date: new Date()
            }
            data.content.push(message);

            appendChatMessage({
                name: me ? message.sender : "Receptor@email.com",
                message: message.message,
                id: "id",
                date: message.date,
                attach: message.attach,
                direction : me ?  RIGHT : LEFT
            });

            /**
             * Setting the chat line once all is rendered
             * DO NOT change this, is compulsory.
             */
            const lined = document.querySelector(".continueLined");
            if (lined)
            lined.style.setProperty("--height", lined.scrollHeight + "px");
            me =! me;
            parent.data = data;

            hideWritter();
            
            /**
             * Showing float button
             */
            let button = document.querySelector("#aonMessengeraddButtonIconButton")
            setStyles(button , {
                transition : "0.25s",
                opacity : "1"
            });
            setTimeout(() => button.style.display = "block", 100);

            document.querySelector("#end").scrollIntoView();
        }
    });


    writter.element.appendChild(textarea);
    writter.appendTo(parent.element);
}

/**
 * Build standard toolbar options 
 * @param {*} aonTextArea 
 */
export const buildTextareaToolbar = (aonTextArea) => {

    aonTextArea.compile = () => compileHTML(aonTextArea);

    aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.FORMAT_BOLD,
        icon: MATERIAL_ICONS.FORMAT_BOLD,
    },
        (e) => {
            waitChildEl(aonTextArea, "#" + aonTextArea.TEXTAREA)
            .then(el => setSelectionMarkup(el,
                (selection) => {
                    el.dataset.start = -1;
                    el.dataset.end = -1;
                    return bold(selection);
                },() => true)
            );
        }
    );

    aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.FORMAT_ITALIC,
        icon: MATERIAL_ICONS.FORMAT_ITALIC,
    },(e) => {
        waitChildEl(aonTextArea, "#" + aonTextArea.TEXTAREA)
        .then(el => setSelectionMarkup(el, (selection) => italic(selection), () => true));
    });

    aonTextArea.addToolbarOptionRight({
        id: MATERIAL_ICONS.FORMAT_INDENT_INCREASE,
        icon: MATERIAL_ICONS.FORMAT_INDENT_INCREASE,
    },(e) => {
        waitChildEl(aonTextArea, "#" + aonTextArea.TEXTAREA)
        .then(el => setSelectionMarkup(el, (selection) => tab(selection), () => true));
    });

    aonTextArea.addToolbarOptionRight({
        id: MATERIAL_ICONS.FORMAT_LIST_BULLETED,
        icon: MATERIAL_ICONS.FORMAT_LIST_BULLETED,
    },(e) => {
        waitChildEl(aonTextArea, "#" + aonTextArea.TEXTAREA)
        .then(el => setSelectionMarkup(el, (selection) => list(selection), () => true));
    });

    aonTextArea.addToolbarOptionRight({
        id: MATERIAL_ICONS.LINK,
        icon: MATERIAL_ICONS.LINK,
    },(e) => {
            waitChildEl(aonTextArea, "#" + aonTextArea.TEXTAREA)
                .then(el => setSelectionMarkup(el, (selection) => link(selection), () => true));
    });

    aonTextArea.addToolbarOptionRight({
        id: MATERIAL_ICONS.ATTACH_FILE,
        icon: MATERIAL_ICONS.ATTACH_FILE,
    },(e) => { alert("Not supported yet") });
}


/**
 * Show the writter with animation
 */
export const showWritter = () => {
    setTimeout(() => {
        setStyles(document.getElementById(MESSENGER_COMPONENTS.WRITTER),{
          zIndex : 9,
          opacity : 1,
          left : 0,
        });
  
      }, 100);
}

/**
 * Hide writter with animation
 */
export const hideWritter = () => {
    setTimeout(() => {
        setStyles(document.getElementById(MESSENGER_COMPONENTS.WRITTER),{
          zIndex : -9,
          opacity : 0,
        });
  
    }, 100);
}

