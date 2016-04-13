package com.esferalia.aon.occam.api.model.office;

import java.util.function.BiPredicate;

import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.esferalia.aon.watson.util.AonStringUtils;

public class NoticeFilterImpl implements BiPredicate<NoticeFilter, Notice> {

	@Override
	public boolean test(NoticeFilter filter, Notice notice) {

		boolean accepted = false;
		
		if (filter.isOpened())
			accepted = evalOpened(filter, notice);

		else if (filter.isClosed())
			accepted = evalClosed(filter, notice);

		else if (filter.isAll())
			accepted = evalAll(filter, notice);
		
		else if (filter.isFaq())
			accepted = evalFaq(filter, notice);
		
		if (filter.isDuplicated())
			accepted = accepted && notice.getNotice() != null;

		if (AonStringUtils.isNotBlank(filter.getComany())) {
			accepted = accepted && AonStringUtils.equals(notice.getCompany().toUpperCase(),
					filter.getComany().toUpperCase());
		}

		if (AonStringUtils.isNotBlank(filter.getText()))
			accepted = accepted && AonStringUtils.contains(notice.getTitle().toUpperCase(),
					filter.getText().toUpperCase());

		if (filter.getSince() != null)
			accepted = accepted
					&& !filter.getSince().after(notice.getStartDate());
		
		
		//accepted tiene que llegar true con la lista de tags filtrada
		
		if (filter.getTags().length > 0) {
			
			for (int x = 0; x < filter.getTags().length ; x++) {
				final String name = filter.getTags()[x];
				accepted = accepted && notice.getTags().stream()
						.filter(tag -> AonStringUtils.equals(tag.getName(), name)
								&& tag.getEndDate() == null)
						.findFirst()
						.isPresent();
			}
		}
		
		if (AonStringUtils.isNotBlank(filter.getUser()))
			accepted = accepted && AonStringUtils.equals(notice.getSender().getName(), filter.getUser());

		return accepted;
	}

	private boolean evalOpened(NoticeFilter filter, Notice notice) {
		return notice.getTags().stream()
				.filter(tag -> (tag.isTagStatus()
						&& NoticeStatus.isOpened(tag.getName())
						&& tag.getEndDate() == null))
				.findFirst()
				.isPresent();
	}

	private boolean evalClosed(NoticeFilter filter, Notice notice) {
		return notice.getTags()
				.stream()				
				.filter(tag -> (tag.isTagStatus()
						&& NoticeStatus.isClosed(tag.getName())
						&& tag.getEndDate() == null))
				.findFirst()
				.isPresent();
	}

	private boolean evalAll(NoticeFilter filter, Notice notice) {
		return notice.getTags().stream()
				.findFirst()
				.isPresent();
	}
	
	private boolean evalFaq(NoticeFilter filter, Notice notice) {
		return notice.getTags()
				.stream()
				.filter(tag -> (tag.isTagStatus() 
						&& NoticeStatus.isFAQ(tag.getName()) 
						&& tag.getEndDate() == null))
				.findFirst()
				.isPresent();
	}
}
