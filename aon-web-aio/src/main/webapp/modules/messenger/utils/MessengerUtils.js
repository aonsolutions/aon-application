import { CSS, TAG } from "../../../environments/environments.js";

import { TASK_STATUS } from "../MessengerEnums.js";
import { getTaskHolder } from "../../../services/taskHolderService.js";
import { getDomainUserRoles } from "../../../services/companyService.js";
import { DomainUserRoles } from "../../../models/DomainUserRoles.js";
import { getWorkgroups } from "../../../services/workgroupService.js";
import { getTasks } from "../../../services/taskService.js";
import { sortBy } from "../../../services/utils.js";
import { TaskUtils } from "./TaskUtils.js";

const getMeseggers = async () => {
  let data = [];
  let filter = { page: 1, perPage: 30, status: TASK_STATUS.PENDING };

  const domainUserRoles = await getDomainUserRoles({ reload: true });
  let dur = new DomainUserRoles(domainUserRoles);

  const taskholder = await getTaskHolder({ reload: false }).catch(() => null);
  if (taskholder) {
    filter.task_holder = taskholder.id;
  }

  let filterWorkgroup = { status: "ACTIVE" };
  let isManager = dur.isMessengerManager();
  if (!isManager && taskholder) {
    filterWorkgroup.task_holder = taskholder.id;
  }

  await getWorkgroups(filterWorkgroup).then((workgroup) => {
    filter.workgroups = workgroup.map((t) => t.id);
  });

  try {
    let tasks = await getTasks(filter);

    if (tasks.length !== 0) {
      data = tasks
        .filter((v, idx) => tasks.findIndex((m) => m.id === v.id) === idx)
        .map((task) => ({
          ...task,
          date: task.start_date,
          startDate: new Date(task.start_date),
          newNumber: TaskUtils.taskNumberParse(task.number),
        }));
    }

    data = sortBy(data, "startDate", "desc");
  } catch (error) {
    console.log("error >> ", error);
  }
  return data;
};

const getMessageBadge = (messageLenght) => {
  let badgePanel = document.createElement(TAG.DIV);
  badgePanel.className = CSS.AON_MESSENGER_OPEN_MESSAGES;

  let badgeText = document.createElement(TAG.SPAN);
  badgeText.innerHTML = "Pendientes";
  badgeText.title = "Ver solicitudes pendientes";
  badgePanel.appendChild(badgeText);

  let badge = document.createElement(TAG.SPAN);
  badge.className = CSS.AON_MESSENGER_BADGE;
  badge.innerHTML = messageLenght;
  badge.title = "Ver solicitudes pendientes";
  badgePanel.appendChild(badge);

  return badgePanel;
};

export const MessegerUtils = {
  getMeseggers,
  getMessageBadge,
};
