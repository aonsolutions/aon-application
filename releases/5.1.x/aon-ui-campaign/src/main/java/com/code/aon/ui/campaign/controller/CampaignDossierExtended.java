/**
 * 
 */
package com.code.aon.ui.campaign.controller;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;

import com.code.aon.campaign.CampaignDossier;
import com.code.aon.campaign.ProcessDetail;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.project.Task;

public class CampaignDossierExtended implements ITransferObject {

	private static final long serialVersionUID = 1L;
	private static final Color color2 = new Color( 221,139,69);
	private static final Color color3 = new Color( 216,182,69);
	private static final Color color4 = new Color( 214,216,69);
	private static final Color color5 = new Color( 172,216,69);
	private static final Color color6 = new Color( 112,216,69);

	private CampaignDossier campaignDossier;
	private ProcessDetail processDetail;
	private Double processDetailPercent;
	private Task task;
	private byte[] percentImage;
	private Color color;

	
	public CampaignDossier getCampaignDossier() {
		return campaignDossier;
	}

	public void setCampaignDossier(CampaignDossier campaignDossier) {
		this.campaignDossier = campaignDossier;
	}

	public ProcessDetail getProcessDetail() {
		return processDetail;
	}

	public void setProcessDetail(ProcessDetail processDetail) {
		this.processDetail = processDetail;
	}

	public Double getProcessDetailPercent() {
		return processDetailPercent;
	}

	public void setProcessDetailPercent(Double processDetailPercent) {
		this.processDetailPercent = processDetailPercent;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public byte[] getPercentImage() {
		if (percentImage == null) {
			try {
				int w = 100;
				int h = 10;
				BufferedImage buf = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
				Graphics g = buf .getGraphics();  
				g.setColor(Color.WHITE); //Color de fondo  
				g.fillRect(1, 1, 98, 8);
				g.setColor( getColor() );
				int p = (int) CommonUtil.round(getProcessDetailPercent(),0);
				if (p > 98) {
					p = 98;
				}
				g.fillRect(1, 1, p, 8);
				g.setColor(Color.BLACK); 
				g.dispose();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ImageIO.write(buf, "jpg", out);
				out.flush();
				out.close();
				percentImage = out.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return percentImage;
	}
	public void setPercentImage(byte[] percentImage) {
		this.percentImage = percentImage;
	}
	
	public InputStream getPercentImageInputStream() {
		if (getPercentImage() != null) {
			return new ByteArrayInputStream( getPercentImage() );
		}
		return null;
	}

	public Color getColor() {
		if (color == null) {
			int p = (int) CommonUtil.round(getProcessDetailPercent(),0);
			if (p >=0 && p < 20) {
				color = Color.RED;
			} else if (p >=20 && p < 40) {
				color = color2;
			} else if (p >=40 && p < 60) {
				color = color3;
			} else if (p >=60 && p < 80) {
				color = color4;
			} else if (p >=80 && p < 100) {
				color = color5;
			} else if (p == 100) {
				color = color6;
			}
		}
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public String getAbbreviatedProcessDetailDescription() {
		String s = null;
		if (getProcessDetail() != null) {
			s = StringUtils.abbreviate(getProcessDetail().getDescription(), 35);
		}
		return s;
	}
	
	public String getAssigned() {
		String s = null;
		if (getTask() != null) {
			if (getTask().getUser() != null) {
				s = getTask().getUser().getName();
			} else if (getTask().getWorkGroup() != null) {
				s = getTask().getWorkGroup().getDescription();	
			} else {
				s = "¿?";
			}
		}
		return s;
	}
	public String getCustomerDossier() {
		String fn = getCampaignDossier().getDossier().getCustomer().getRegistry().getFullName();
		fn = StringUtils.abbreviate(fn, 25);
		fn = fn + " [" + getCampaignDossier().getDossier().getNumber() + "]";
		return fn;
	}
}