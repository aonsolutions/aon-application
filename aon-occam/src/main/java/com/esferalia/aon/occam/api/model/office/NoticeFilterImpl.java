package com.esferalia.aon.occam.api.model.office;

import java.util.function.BiPredicate;

import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class NoticeFilterImpl implements BiPredicate<NoticeFilter, Notice> {

	public boolean evalHeadParams(NoticeFilter filter, Notice notice) {
		boolean accepted = false;

		if (filter.isOpened())
			accepted = evalOpened(filter, notice);

		else if (filter.isClosed())
			accepted = evalClosed(filter, notice);

		else if (filter.isAll())
			accepted = evalAll(filter, notice);

		if (AonStringUtils.isNotBlank(filter.getComany())) {
			accepted = accepted && AonStringUtils.equals(notice.getCompany(),
					filter.getComany());
		}

		if (AonStringUtils.isNotBlank(filter.getText()))
			accepted = accepted && AonStringUtils.contains(notice.getTitle(),
					filter.getText());

		if (filter.getSince() != null)
			accepted = accepted
					&& !filter.getSince().after(notice.getStartDate());

		return accepted;

	}

	@Override
	public boolean test(NoticeFilter filter, Notice notice) {
		boolean accepted = true;

		for (int x = 0; x < filter.getTags().length; x++)
			for (Tag tag : notice.getTags()) {
				accepted = false;
				if (AonStringUtils.equals(filter.getTags()[x], tag.getName())) {
					accepted = true;
					break;
				}
			}

		for (int x = 0; x < filter.getUsers().length; x++) {
			accepted = false;
			if (AonStringUtils.equals(filter.getUsers()[x],
					notice.getSender().getName())) {
				accepted = true;
				break;
			}
		}

		return accepted;
	}

	private boolean evalOpened(NoticeFilter filter, Notice notice) {
		return notice.getTags().stream()
				.filter(tag -> (tag.getType() == TagType.OFFICE_STATUS.value()))
				.filter(tag -> (NoticeStatus.isOpened(tag.getName())
						&& tag.getEndDate() == null))
				.findFirst().isPresent();
	}

	private boolean evalClosed(NoticeFilter filter, Notice notice) {
		return notice.getTags().stream()
				.filter(tag -> (tag.getType() == TagType.OFFICE_STATUS.value()))
				.filter(tag -> (NoticeStatus.isClosed(tag.getName())
						&& tag.getEndDate() == null))
				.findFirst().isPresent();
	}

	private boolean evalAll(NoticeFilter filter, Notice notice) {
		return notice.getTags().stream().findFirst().isPresent();
	}
}
