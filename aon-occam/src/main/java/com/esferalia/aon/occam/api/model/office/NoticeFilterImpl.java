package com.esferalia.aon.occam.api.model.office;

import java.util.function.BiPredicate;

import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class NoticeFilterImpl implements BiPredicate<NoticeFilter, Notice> {

	@Override
	public boolean test(NoticeFilter filter, Notice notice) {
		boolean accepted = false;
		
		accepted = !filter.isOpened() ||
				notice.getTags()
				.stream()
				.filter( tag -> (tag.getType() == TagType.OFFICE_STATUS.value()))
				.filter( tag -> NoticeStatus.isOpen(tag.getName()) && tag.getEndDate() == null)
				.findFirst()
				.isPresent();
		
		accepted = !accepted || !filter.isClosed() || 
				notice.getTags()
				.stream()
				.filter( tag -> (tag.getType() == TagType.OFFICE_STATUS.value()))
				.filter( tag -> NoticeStatus.isClosed(tag.getName()) && tag.getEndDate() == null)
				.findFirst()
				.isPresent();
		
		if (AonStringUtils.isNotBlank(filter.getText()))
			accepted = accepted && AonStringUtils.contains(notice.getTitle(), filter.getText());
		
		if (filter.getSince() != null)
			accepted = accepted && !filter.getSince().after(notice.getStartDate());
		
		return accepted;		
	}

}
