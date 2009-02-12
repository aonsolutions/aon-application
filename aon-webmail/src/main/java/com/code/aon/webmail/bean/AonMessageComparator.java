package com.code.aon.webmail.bean;

import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import com.code.aon.webmail.WebmailException;

public class AonMessageComparator {

	private static final Logger LOGGER = Logger
			.getLogger(AonMessageComparator.class.getName());
	
	public static Comparator<AonMessage> getComparator(String column, boolean ascending) {
		if (AonMessageSortableList.DATE_COLUMN.equals(column) ) {
			return new DateComparator( ascending );
		} else if (AonMessageSortableList.SUBJECT_COLUMN.equals(column) ) {
			return new SubjectComparator( ascending );
		} else if (AonMessageSortableList.FROM_COLUMN.equals(column) ) {
			return new FromComparator( ascending );
		} else if (AonMessageSortableList.TO_COLUMN.equals(column) ) {
			return new ToComparator( ascending );
		}
		return new EmptyComparator();
	}

	private static class EmptyComparator implements Comparator<AonMessage> {

		@Override
		public int compare(AonMessage o1, AonMessage o2) {
			return 0;
		}

	}

	private static class SubjectComparator implements Comparator<AonMessage> {

		private boolean ascending;
		
		public SubjectComparator(boolean ascending) {
			this.ascending = ascending;
		}

		@Override
		public int compare(AonMessage m1, AonMessage m2) {
			try {
				if (ascending) {
					return m1.getSubject().compareToIgnoreCase(m2.getSubject());
				}
				return m2.getSubject().compareToIgnoreCase(m1.getSubject());
			} catch (WebmailException e) {
				LOGGER.log(Level.SEVERE, "Sort error", e);
				return 0;
			}
		}

	}

	private static class FromComparator implements Comparator<AonMessage> {

		private boolean ascending;
		
		public FromComparator(boolean ascending) {
			this.ascending = ascending;
		}

		@Override
		public int compare(AonMessage m1, AonMessage m2) {
			try {
				if (ascending) {
					return m1.getSender().compareToIgnoreCase(m2.getSender());
				}
				return m2.getSender().compareToIgnoreCase(m1.getSender());
			} catch (WebmailException e) {
				LOGGER.log(Level.SEVERE, "Sort error", e);
				return 0;
			}
		}

	}

	private static class ToComparator implements Comparator<AonMessage> {

		private boolean ascending;
		
		public ToComparator(boolean ascending) {
			this.ascending = ascending;
		}

		@Override
		public int compare(AonMessage m1, AonMessage m2) {
			try {
				if (ascending) {
					return m1.getRecipientsTo().compareToIgnoreCase(m2.getRecipientsTo());
				}
				return m2.getRecipientsTo().compareToIgnoreCase(m1.getRecipientsTo());
			} catch (WebmailException e) {
				LOGGER.log(Level.SEVERE, "Sort error", e);
				return 0;
			}
		}

	}

	private static class DateComparator implements Comparator<AonMessage> {

		private boolean ascending;
		
		public DateComparator(boolean ascending) {
			this.ascending = ascending;
		}
		
		private Date getDate( AonMessage m ) throws WebmailException, MessagingException {
			Date date = m.getSentDate();
			if ( date == null ) {
				date = m.getMessage().getReceivedDate();
			}
			return date;
		}

		@Override
		public int compare(AonMessage m1, AonMessage m2) {
			try {
				Date d1 = ascending ? getDate(m1) : getDate(m2);
				Date d2 = ascending ? getDate(m2) : getDate(m1);
				if ( d1 != null ) { 
					if (d2 != null) {
						return d1.compareTo(d2);
					} else {
						return 1;
					}
				} else {
					if ( d2 != null ) {
						return -1;
					} else {
						return 0;
					}
				}
			} catch (WebmailException e) {
				LOGGER.log(Level.SEVERE, "Sort error", e);
			} catch (MessagingException e) {
				LOGGER.log(Level.SEVERE, "Sort error", e);
			}
			return 0;
		}

	}
	
}
