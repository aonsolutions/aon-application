package com.code.aon.desktop;

import com.code.aon.groupware.enumeration.NoticeType;

public class DesktopNoticeSummary {

    private NoticeType noticeType;
    private Long count;

    public DesktopNoticeSummary(NoticeType noticeType, Long count) {
        this.noticeType = noticeType;
        this.count = count;
    }

    public DesktopNoticeSummary(NoticeType noticeType) {
        this(noticeType, (long)0);
    }

    public NoticeType getNoticeType() {
        return noticeType;
    }
    public void setNoticeType(NoticeType noticeType) {
        this.noticeType = noticeType;
    }

    public Long getCount() {
        return count;
    }
    public void setCount(Long count) {
        this.count = count;
    }

    public boolean isCall() {
        return noticeType.equals(NoticeType.CALL);
    }
    public boolean isVisit() {
        return noticeType.equals(NoticeType.VISIT);
    }
    public boolean isMessage() {
        return noticeType.equals(NoticeType.MESSAGE);
    }
    public boolean isCommunication() {
        return noticeType.equals(NoticeType.COMMUNICATION);
    }
}
