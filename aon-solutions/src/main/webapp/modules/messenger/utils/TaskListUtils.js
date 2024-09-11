import { AonIcon } from "../../../components/aon-icon.js";
import {
  AON_ICONS,
  COLORS,
  CONSTANT,
  CSS,
  EVENT,
  MATERIAL_ICONS,
  TAG,
} from "../../../environments/environments.js";
import { sortBy } from "../../../services/utils.js";
import { setStyles } from "../../../services/utilsComponents.js";
import { firstLetters, StringTwoLetters } from "../../timecontrol/time-control/utils.js";
import { AonDateUtils } from "../../utils/AonDateUtils.js";
import { MESSENGER_VIEWS, TAG_TYPE, TASK_SOURCE, TASK_STATUS } from "../MessengerEnums.js";
import { TaskCreationUtils } from "./TaskCreationUtils.js";
import { TaskUtils } from "./TaskUtils.js";


// ------------DESKTOP
const getTitleHtmlDesktop = (res) => {
  const title = res.title || "Sin asunto";
  let divParent = setStyles(document.createElement(TAG.DIV), {
    position: "relative",
    padding: "5px"
  });

  let div = setStyles(document.createElement(TAG.DIV), {
    top: "-4px",
    position: "absolute",
    left: "0",
    right: "0",
  });
  divParent.appendChild(div);

  let divFlex = setStyles(document.createElement(TAG.DIV), {
    display: "flex",
    whiteSpace: "nowrap",
  });

  div.appendChild(divFlex);

  const divOne = setStyles(document.createElement(TAG.DIV), {
    fontWeight: "550",
    fontSize: "14px",
    overflow: CONSTANT.HIDDEN,
    textOverflow: "ellipsis"
  });
  divOne.innerText = title;
  divOne.title = title;

  divFlex.appendChild(divOne);

  const divTwo = setStyles(document.createElement(TAG.DIV), {
    display: "flex",
    overflow: CONSTANT.HIDDEN,
    marginLeft: "5px",
    marginTop: "-4px",
    gap: "3px",
  });

  divFlex.appendChild(divTwo);

  getTagsLabel(res.tags).forEach((tag) => {
    setStyles(TaskCreationUtils.createTagHtml(tag, divTwo), {
      margin: "0",
      textAlign: "center",
    });
  });

  return divParent;
};

const getSubTitleHtml = (res, doc, documentTh) => {

  let divParent = setStyles(document.createElement(TAG.DIV), {
    position: "relative",
    padding:"11px"
  });


  const type = getTagType(res.tags) || "";
  // sender
  const sender = getSender(res, doc, documentTh);

  //----------- DESCRIPTION
  let description = res.description;
  try {
    description = JSON.parse(res.description).observation;
  } catch (e) {}

  let div = setStyles(document.createElement(TAG.DIV), {
    position: "absolute",
    top: "6px",
    left: "0",
    right: "0",
    whiteSpace: "nowrap",
    textOverflow: "ellipsis",
    overflow: "hidden",
  });
  divParent.appendChild(div);

  let spanOne = document.createElement(TAG.SPAN);
  const subTitle = `${type} ${res.newNumber}`;
  spanOne.title = `${subTitle} ${sender}`;
  spanOne.innerHTML = `${subTitle} <b>${sender}</b>`;
  div.appendChild(spanOne);

  if (description) {
    const dText = description.replace(/<[^>]+>|&nbsp;|\n/g, " ");
    let span = setStyles(document.createElement(TAG.SPAN), {
      color:  CSS.variable(COLORS.AON_GRAY),
      marginLeft: "3px",
    });
    span.textContent = dText;
    span.title = dText;
    div.appendChild(span);
  }
  return divParent;
};
// ------------END DESKTOP

// ------------MOBILE

const getTitleMobile = (res, doc, documentTh) => {
  const div = setStyles(document.createElement(TAG.DIV), { display: "flex" });

  const senderDiv = setStyles(document.createElement(TAG.DIV), {
    overflow: "hidden",
    whiteSpace: "nowrap",
    textOverflow: "ellipsis",
    fontWeight: "500",
  });

  senderDiv.innerText = getSender(res, doc, documentTh);
  div.appendChild(senderDiv);

  const dateDiv = setStyles(document.createElement(TAG.DIV), {
    color: CSS.variable(COLORS.AON_GRAY),
    marginLeft: "auto",
    fontSize: "14px",
    fontWeight: "500",
  });

  div.appendChild(dateDiv);

  dateDiv.innerText = AonDateUtils.getDayMonthOrFull(res.date);

  return div.outerHTML;
};

const getSubtitleMobileOne = (res) => {
  const div = setStyles(document.createElement(TAG.DIV), { color: CSS.variable(COLORS.AON_BLACK), overflow:"hidden", textOverflow:"ellipsis" });
  div.innerText = res.title || "Sin asunto";
  return div.outerHTML;
};

const getSubtitleMobileTwo = (res) => {
  const type = getTagType(res.tags) || "";
  const div = setStyles(document.createElement(TAG.DIV), { display: "flex" });

  const divOne = document.createElement(TAG.DIV);
  divOne.innerText = type + " " + res.newNumber;
  div.appendChild(divOne);

  const divTwo = setStyles(document.createElement(TAG.DIV), {
    display: "flex",
    gap: "2px",
    marginLeft: "2px",
    overflow: "hidden",
    height: "19px",
    flexWrap: "wrap",
  });

  div.appendChild(divTwo);

  getTagsLabel(res.tags)
  .forEach((tag) => {
    setStyles(TaskCreationUtils.createTagHtml(tag, divTwo), {
      margin: "0",
      textAlign: "center",
      padding: "1px 4px",
      height: "19px",
      fontSize: "12px",
    });
  });

  return div.outerHTML;
};
//----------END MOBILE

const addTaskChilds = (task, row, documents, isCau) => {
  try {
    document.querySelectorAll(`div[data-task-id='${task.id}']`).forEach((l) => l.remove());

    const taskId = task.id;
    const parent = row.parent;
    let childs = task.childs;
    let childAll = [];

    if (childs && childs.length) {
      childAll.push(...childs);  
    } else if (task.parentObj && typeof task.parentObj === "object") {
      childAll.push(task.parentObj);
    }

    if (isCau && childs.length) {
      //distinct task for workgroup
      childAll = sortBy(childAll, "id", "desc").filter((t) => t.workgroup && t.workgroup.id);
      
      childAll.filter((t, idx) => childAll.findIndex(x => x.workgroup.id === t.workgroup.id) === idx);

      childAll = sortBy(childAll, "id", "asc");
    }
    

    childAll.forEach(t => {
      addChild(taskId, t, parent, documents, false, isCau);

      // -------------GRANDCHILD-------------------------
      const grandChild = t.childs && t.childs.length ? t.childs : [];

      grandChild.filter(child=> child.id !== taskId).forEach(child => addChild(taskId, child, parent, documents, true, isCau));
    });
    
    // CHANGE COLORS ALL BRANCH CLOSES

    if ([TASK_STATUS.PENDING, TASK_STATUS.IN_PROGRESS].includes(task.status) && childAll.length) {
      const isChildPending = childAll.some(({ status, parent:p }) => p && [TASK_STATUS.PENDING, TASK_STATUS.IN_PROGRESS].includes(status));
      if (!isChildPending) {
        const iconParent = document.querySelector(`${TAG.SPAN}[data-task-id='${task.id}']`);
        if (iconParent) {
          iconParent.style.color = CSS.variable(COLORS.MATERIAL_RED);
        }
      }
    }
  } catch (err) {
    console.log(err);
  }
};

const addChild = (taskId, child, parent, documents, grandChild, isCau)=> {
  const { person, workgroupDescription } = getAssined(child, documents.domainId);

  const aonMessengerList = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_LIST);

  let assigned = person || workgroupDescription;

  const div = setStyles(document.createElement(TAG.DIV), {
    color: CSS.variable(COLORS.AON_GRAY),
    display: "flex",
    whiteSpace: "nowrap",
    textOverflow: "ellipsis",
    overflow: "hidden",
    gap: "4px",
    transition: "opacity .5s linear",
    opacity: "1",
  });

  if(grandChild){
    div.style.marginLeft = "17px";
  }

  div.dataset.taskId = taskId;
  div.dataset.taskChild = child.id;

  parent.appendChild(div);

  let icon = getIcon(child, "16px", true);
  icon.style.display = "inline-block";
  div.appendChild(icon);

  const sender = getSender(child, documents.document, documents.documentTh);
  let span = document.createElement(TAG.SPAN);
  span.className = CSS.AON_LINK;
  span.innerText = TaskUtils.taskNumberParse(child.number);
  span.title = "Creador por " + sender;

  if (!isCau && aonMessengerList.getIsMyTask(child) ) {
    span.addEventListener(EVENT.CLICK, (ev) => {
      ev.preventDefault();
      ev.stopPropagation();
      aonMessengerList.goMessengerChat(child, 0);
    });
  } else {
    assigned = workgroupDescription || null;
    span.classList.add(CSS.TEXT_DECORATION_NONE);
  }
  div.appendChild(span);

  if (assigned) {
    const spanTwo = document.createElement(TAG.SPAN);
    const wg = workgroupDescription && assigned != workgroupDescription  ? `<b>${workgroupDescription}</b>`  : "";
    spanTwo.innerHTML = `${wg} Asignada a <b>${assigned}</b>`;
    div.appendChild(spanTwo);
  }

  const spanThree = document.createElement(TAG.SPAN);
  spanThree.innerText = firstLetters(AonDateUtils.setFullDate(child.creation_date)) + " " +AonDateUtils.setTime(child.creation_date);
  div.appendChild(spanThree);
}

const getIcon = (task, size = undefined, isChild = undefined) => {
  const { source, status, parent, id } = task;

  let div = document.createElement(TAG.DIV);

  let iconJson = getIconList({ source, status, parent });

  let span = setStyles(document.createElement(TAG.SPAN), {
    color: iconJson.icon_color,
    position: "relative",
  });
  span.title = source;
  div.appendChild(span);

  span.dataset.taskId = id;
  if (isChild) {
    span.dataset.isChild = isChild;
  } else {
    try {
      let arr = [];
      if (task.parent) {
        arr.push(task.parent);
      }
      if (task.childs && task.childs.length) {
        task.childs.forEach((k) => arr.push(k.id));
      }
      if (arr.length) {
        span.dataset.taskChild = arr.join(",");
      }
    } catch (e) {}
  }

  let icon = document.createElement(TAG.I);

  if (iconJson.aonIcon) {
    icon = new AonIcon();
    icon.icon = iconJson.aonIcon;
    icon.color = iconJson.icon_color;
    if (size) {
      icon.size = size;
    }
  } else {
    icon.className = CONSTANT.MATERIAL_ICONS_OUTLINED;
    icon.textContent = iconJson.icon;
    if (size) {
      icon.style.fontSize = size;
    }
  }

  span.appendChild(icon);

  return div;
};

const getAssignedHtml = (res, domainId) => {
  let { person, workgroupDescription } = getAssined(res, domainId);

  let div = setStyles(document.createElement(TAG.DIV), {
    display: "flex",
    gap: "4px",
  });

  if (workgroupDescription) {
    const color = person ? "949393" : "FF6F1D";
    const divOne = setStyles(document.createElement(TAG.DIV), {
      border: `2px solid var(--aonMessenger)`,
      color: `var(--aonMessenger)`,
      borderRadius: "29px",
      height: "23px",
      lineHeight: "21px",
      width: "23px",
      display: "block",
      fontSize: "15px",
      textAlign: "center",
    });
    divOne.title = workgroupDescription;
    div.appendChild(divOne);

    const icon = setStyles(document.createElement(TAG.I), {
      fontSize: "17px",
      lineHeight: "19px",
    });

    icon.className = CSS.MATERIAL_ICONS;
    icon.innerText = MATERIAL_ICONS.PEOPLE_ALT;
    divOne.appendChild(icon);
  }

  if (person) {
    const divTwo = setStyles(document.createElement(TAG.DIV), {
      border: `2px solid ${CSS.variable(COLORS.AON_BLACK)}`,
      color: CSS.variable(COLORS.AON_BLACK),
      borderRadius: "29px",
      height: "23px",
      lineHeight: "21px",
      width: "23px",
      display: "block",
      fontSize: "12px",
      textAlign: "center",
    });

    divTwo.innerText = StringTwoLetters(person.toUpperCase());
    divTwo.title = person;
    div.appendChild(divTwo);
  }

  if (!workgroupDescription && !person) {
    const divOne = setStyles(document.createElement(TAG.DIV), {
      border: `2px solid ${CSS.variable(COLORS.MATERIAL_RED)}`,
      color: CSS.variable(COLORS.MATERIAL_RED),
      borderRadius: "29px",
      height: "23px",
      lineHeight: "21px",
      width: "23px",
      display: "block",
      fontSize: "15px",
      textAlign: "center",
    });

    divOne.title = "Sin asignar";
    div.appendChild(divOne);

    const icon = setStyles(document.createElement(TAG.I), {
      fontSize: "17px",
      lineHeight: "19px",
    });

    icon.className = CSS.MATERIAL_ICONS;
    icon.innerText = MATERIAL_ICONS.GROUP_OFF;
    divOne.appendChild(icon);
  }

  return div;
};

const getAssined = (res, domainId) => {
  const workgroup = res.workgroup;

  let person = undefined;

  let workgroupDescription = undefined;

  if (res.domain && res.domain.id && domainId !== parseInt(res.domain.id)) {
    person = res.domain.description;
  } else if (res.task_holder && res.task_holder.id) {
    person = res.task_holder.alias || res.task_holder.name;
  }

  if (workgroup && workgroup.description) {
    workgroupDescription = workgroup.description;
  }

  return {
    person,
    workgroupDescription,
  };
};

const getSender = (res, document, documentTh) => {
  let sender = "";
  if (res.registry && res.registry.name && document !== res.registry.document) {
    sender = `${res.registry.name} ${sender}`;
  } else if (res.sender && res.sender.name && documentTh !== res.sender.document) {
    sender = `${res.sender.name} ${sender}`;
  } else if (res.workgroup && res.workgroup.description) {
    // GRUPO ASIGNADO
    sender = res.workgroup.description;
  } else {
    sender = "SIN GRUPO ASIGNADO";
  }

  return sender;
};

const getTagsLabel = (tags) => tags && tags.length ? tags.filter(({ tag_type }) => tag_type === TAG_TYPE.TASK_LABEL) : [];

const getTagType = (tags) => {
  if (tags && tags.length) {
    let type = tags.find((tag) => tag.tag_type === TAG_TYPE.TASK_TYPE);
    return type ? type.name : null;
  }
  return null;
};

const getIconList = ({ source, status, parent }) => {
  let json = {
    ...TaskUtils.getIconJson({ source, status }),
    icon_class: CONSTANT.MATERIAL_ICONS_OUTLINED,
    icon_title: source,
  };

  if (source === TASK_SOURCE.TASK && parent) {
    json.aonIcon = AON_ICONS.AON_BRANCH;
  }

  return json;
};

const getDateParseNew = ({ gtask_id, date }) => {
  const email = gtask_id;

  const style = {
    left: 0,
    right: 0,
    position: "absolute",
    whiteSpace: "nowrap",
    textOverflow: "ellipsis",
    overflow: "hidden",
  };

  const div = setStyles(document.createElement(TAG.DIV), { position: "relative" });

  const dateText = firstLetters(AonDateUtils.setFullDate(date)) + " " + AonDateUtils.setTime(date);

  const divTwo = setStyles(document.createElement(TAG.DIV), {
    bottom: email ? "-1px" : "-9px",
    ...style
  });

  divTwo.innerText = dateText;
  divTwo.title = dateText;
  div.appendChild(divTwo);

  if (email) {
    const divThree = setStyles(document.createElement(TAG.DIV), {
      color: CSS.variable(COLORS.AON_GRAY),
      position: "absolute",
      top: "4px",
      ...style
    });
    divThree.textContent = email;
    divThree.title = email;

    div.appendChild(divThree);
  }

  return div;
};

export const TaskListUtils = {
  getTitleHtmlDesktop,
  getSubTitleHtml,
  getTitleMobile,
  getSubtitleMobileOne,
  getSubtitleMobileTwo,
  addTaskChilds,
  getIcon,
  getIconList,
  getAssignedHtml,
  getDateParseNew
};
