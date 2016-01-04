package com.esferalia.aon.gwt.template.shared.marketplace;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;

public class DisclosureImages {

	
	private ImageResource open= new ImageResource() {
		
		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return "open";
		}
		
		@Override
		public boolean isAnimated() {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public int getWidth() {
			// TODO Auto-generated method stub
			return 16;
		}
		
		@Override
		public String getURL() {
			// TODO Auto-generated method stub
			return "data:image/gif;base64,R0lGODlhEAAQAIQaAFhorldnrquz1mFxsvz9/vr6/M3Q2ZGbw5mixvb3+Gp5t2Nys77F4GRzs9ze4mt6uGV1s8/R2VZnrl5usFdortPV2/P09+3u8eXm6lZnrf///wAAzP///////////////yH5BAEAAB8ALAAAAAAQABAAAAVE4CeOZGmeaKquo5K974MuTKHdhDCcgOVvvoTkRLkYN8bL0ETBbJ5PTIaIqW6q0lPAYcVOTRNEpEI2HCYoCOzVYLnf7hAAOw==";
		}
		
		@Override
		public int getTop() {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public SafeUri getSafeUri() {
			// TODO Auto-generated method stub
			SafeUri safeUri = new SafeUri() {
				
				@Override
				public String asString() {
					// TODO Auto-generated method stub
					return "data:image/gif;base64,R0lGODlhEAAQAIQaAFhorldnrquz1mFxsvz9/vr6/M3Q2ZGbw5mixvb3+Gp5t2Nys77F4GRzs9ze4mt6uGV1s8/R2VZnrl5usFdortPV2/P09+3u8eXm6lZnrf///wAAzP///////////////yH5BAEAAB8ALAAAAAAQABAAAAVE4CeOZGmeaKquo5K974MuTKHdhDCcgOVvvoTkRLkYN8bL0ETBbJ5PTIaIqW6q0lPAYcVOTRNEpEI2HCYoCOzVYLnf7hAAOw==";
				}
			};
			return safeUri;
		}
		
		@Override
		public int getLeft() {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public int getHeight() {
			// TODO Auto-generated method stub
			return 16;
		}
	};
	
	private ImageResource closed = new ImageResource() {
		
		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public boolean isAnimated() {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public int getWidth() {
			// TODO Auto-generated method stub
			return 16;
		}
		
		@Override
		public String getURL() {
			// TODO Auto-generated method stub
			return "data:image/gif;base64,R0lGODlhEAAQAIQaAFhorldnrquz1mFxsvz9/vr6/M3Q2ZGbw5mixvb3+Gp5t2Nys77F4GRzs9ze4mt6uGV1s8/R2VZnrl5usFdortPV2/P09+3u8eXm6lZnrf///wAAzP///////////////yH5BAEAAB8ALAAAAAAQABAAAAVD4CeOZGmeaKquo5K974MuTKHdhDCcgOVfvoTkRLkYj5ehiYLZOJ2YDBFDvVCjp4CjepWaJohIZWw4TFAQ2KvBarvbIQA7";
		}
		
		@Override
		public int getTop() {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public SafeUri getSafeUri() {
			SafeUri safeUri= new SafeUri() {
				
				@Override
				public String asString() {
					// TODO Auto-generated method stub
					return "data:image/gif;base64,R0lGODlhEAAQAIQaAFhorldnrquz1mFxsvz9/vr6/M3Q2ZGbw5mixvb3+Gp5t2Nys77F4GRzs9ze4mt6uGV1s8/R2VZnrl5usFdortPV2/P09+3u8eXm6lZnrf///wAAzP///////////////yH5BAEAAB8ALAAAAAAQABAAAAVD4CeOZGmeaKquo5K974MuTKHdhDCcgOVfvoTkRLkYj5ehiYLZOJ2YDBFDvVCjp4CjepWaJohIZWw4TFAQ2KvBarvbIQA7";
				}
			};
			return safeUri;
		}
		
		@Override
		public int getLeft() {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public int getHeight() {
			// TODO Auto-generated method stub
			return 16;
		}
	};

	public ImageResource getOpen() {
		return open;
	}

	public void setOpen(ImageResource open) {
		this.open = open;
	}

	public ImageResource getClosed() {
		return closed;
	}

	public void setClosed(ImageResource closed) {
		this.closed = closed;
	}
	
	
}
