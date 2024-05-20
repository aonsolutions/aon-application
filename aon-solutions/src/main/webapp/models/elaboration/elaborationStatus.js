import { CONSTANT, MSG } from "../../environments/environments";

export const ElaborationStatus = {
    PENDING: {
        id: CONSTANT.PENDING,
        name: MSG.PENDING
    },
    CLOSED: {
        id: CONSTANT.CLOSED,
        name: MSG.CLOSED
    },
    IN_PROGRESS: {
        id: CONSTANT.IN_PROGRESS,
        name: MSG.IN_PROGRESS
    },
    FAIL: {
        id: CONSTANT.FAIL,
        name: MSG.FAILED
    },
    REOPEN: {
        id: CONSTANT.REOPEN,
        name: MSG.REOPENED
    }
}

export const elaborationStatuses = [
    {name: MSG.PENDING, value: CONSTANT.PENDING.toUpperCase()},
    {name: MSG.CLOSED, value: CONSTANT.CLOSED.toUpperCase()},
    {name: MSG.IN_PROGRESS, value: CONSTANT.IN_PROGRESS.toUpperCase()},
    {name: MSG.FAILED, value: CONSTANT.FAIL.toUpperCase()},
    {name: MSG.REOPENED, value: CONSTANT.REOPEN.toUpperCase()}

]