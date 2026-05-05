package net.aonsolutions.db.up2date.customize;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainApp.DOMAIN_APP;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.AppParamRecord;
import com.esferalia.aon.jooq.tables.records.DomainAppRecord;
import com.esferalia.aon.jooq.tables.records.RattachRecord;

import net.aonsolutions.db.up2date.Update;

public class Customize implements Update {

	private static final long serialVersionUID = 1L;

	public static final Customize CUSTOMIZE_OPENGES = 
	new Customize(DOMAIN.NAME.likeRegex("^openges\\.aonsolutions\\.(org|net)$"))
	.setTheme("/css/theme/future.css")
	.setSupportEmail("info@openges.es")
	.setSupportPhone("(+34) 900 730 037")
	.setFavicon("https://openges.es/images/icons/icon.svg")
	.setLoginLogo("https://www.openges.es/images/logo.svg")
	.setHeaderLogo("https://www.openges.es/images/logo.svg")
	.setTitle("OpenGes  - Asesoría online para pymes y autónomos")
	.setCompanyLogo("https://my.openges.es/v2/LOGOTIPO_VERSION_2_OPENGES-03.png")
	.setVariable("AON_CUSTOMIZE_BUTTON_BACKGROUND", "linear-gradient(to right, #0f172a, #0f172a, #0E1030, #5de49e)")
	;

	public static final Customize CUSTOMIZE_INFOAUTONOMOS = 
	new Customize(DOMAIN.NAME.likeRegex("^infoautonomos\\.aonsolutions\\.(org|net)$"))
	.setTheme("/css/theme/jljimenezNew.css")
	.setSupportPhone("900 525 576")
	.setSupportEmail("asesoria@infoautonomos.com")
	.setTitle("Asesoría Online para Autónomos y Pymes")
	.setCompanyLogo("https://infoautonomos.aonsolutions.net/dist/logos/infoautonomos/logo.svg")
	.setFavicon("https://infoautonomos.aonsolutions.net/dist/favicons/infoautonomos/favicon.svg")
	.setLoginLogo("https://infoautonomos.aonsolutions.net/dist/lCustomize.class.getResource(\"gestoriamunoz-logo.png\"ogos/infoautonomos/logo-horizontal.svg")
	.setHeaderLogo("https://infoautonomos.aonsolutions.net/dist/logos/infoautonomos/logo-horizontal.svg")
	.setVariable("AON_CUSTOMIZE_BUTTON_BACKGROUND", "linear-gradient(to right, #0f172a, #0f172a, #0E1030, #41e0e0)")
	;
	
	public static final Customize CUSTOMIZE_AYUDAT = 
	new Customize(DOMAIN.NAME.likeRegex("^ayudat\\.aonsolutions\\.(org|net)$"))
	.setTheme("/css/theme/jljimenezNew.css")
	.setSupportPhone("900 100 162")
	.setSupportEmail("soporteclientes@ayudatpymes.es")
	.setTitle("Ayuda T Pymes | Asesoría Online para Empresas y Autónomos")
	.setCompanyLogo("https://ayudat.aonsolutions.net/dist/logos/ayudat/logo-gris.svg")
	.setFavicon("https://ayudat.aonsolutions.net/dist/favicons/ayudat/favicon-light.svg")
	.setLoginLogo("https://ayudat.aonsolutions.net/dist/logos/ayudat/logo-horizontal.svg")
	.setHeaderLogo("https://ayudat.aonsolutions.net/dist/logos/ayudat/logo-horizontal.svg")
	;
	
	public static final Customize CUSTOMIZE_GESTORIAMUNOZ = 
	new Customize(DOMAIN.NAME.likeRegex("^gestoriamunoz\\.aonsolutions\\.(org|net)$"))
	.setTheme("/css/theme/future.css")
	.setSupportPhone("963 416 333")
	.setSupportEmail("info@gestoriamunoz.com")
	.setTitle("Gestoria Muñoz - Asesoria Fiscal en Valencia")
	.setFavicon(Customize.class.getResource("gestoriamunoz/favicon.ico"))
	.setLoginLogo(Customize.class.getResource("gestoriamunoz/header-logo.png"))
	.setHeaderLogo(Customize.class.getResource("gestoriamunoz/header-logo.png"))
	.setCompanyLogo(Customize.class.getResource("gestoriamunoz/company-logo.jpeg"))
	.setVariable("AON_CUSTOMIZE_LOGIN_LOGO_WIDTH", "250px")
	.setVariable("AON_CUSTOMIZE_LOGIN_LOGO_HEIGHT", "50px")
	;

	// CUSTOM_VIEW(getEmptyModules(), "Vista Personalizada"),								// 20
	
	private static final byte CUSTOM_VIEW_APP = 20;

	public enum MimeType {
		JPEG, GIF, ICS, TXT, HTML, XML, PNG, BMP, TIFF, ICO, AVI, MPEG, QUICKTIME, MP3, WAV, MID, RTF, MS_WORD,
		MS_EXCEL, MS_POWER_POINT, STAR_OFFICE_TEXT, STAR_OFFICE_SPREADSHEET, PDF, JAVASCRIPT, ZIP, CSS, MS_WORD_2007,
		MS_EXCEL_2007, MS_POWER_POINT_2007, SIGNED_PDF, CSV, RSS, OCTECT_STREAM, XSIG, SIGNED_FACTURAE, JSON, PKCS12,
		JKS, SVG, WEBM, RAR;
	}

	private static Integer getMimeType(String extension) {
		switch (extension) {
		case "jpg":
		case "jpeg":
		case "pjpeg":
			return MimeType.JPEG.ordinal();
		case "gif":
			return MimeType.GIF.ordinal();
		case "ics":
			return MimeType.ICS.ordinal();
		case "txt":
			return MimeType.TXT.ordinal();
		case "html":
			return MimeType.HTML.ordinal();
		case "xml":
			return MimeType.XML.ordinal();
		case "png":
			return MimeType.PNG.ordinal();
		case "bmp":
			return MimeType.BMP.ordinal();
		case "tif":
		case "tiff":
			return MimeType.TIFF.ordinal();
		case "ico":
			return MimeType.ICO.ordinal();
		case "avi":
			return MimeType.AVI.ordinal();
		case "mpg":
		case "mpeg":
			return MimeType.MPEG.ordinal();
		case "mov":
			return MimeType.QUICKTIME.ordinal();
		case "mp3":
			return MimeType.MP3.ordinal();
		case "wav":
			return MimeType.WAV.ordinal();
		case "mid":
			return MimeType.MID.ordinal();
		case "rtf":
			return MimeType.RTF.ordinal();
		case "doc":
			return MimeType.MS_WORD.ordinal();
		case "xls":
			return MimeType.MS_EXCEL.ordinal();
		case "ppt":
			return MimeType.MS_POWER_POINT.ordinal();
		case "odt":
			return MimeType.STAR_OFFICE_TEXT.ordinal();
		case "ods":
			return MimeType.STAR_OFFICE_SPREADSHEET.ordinal();
		case "pdf":
			return MimeType.PDF.ordinal();
		case "js":
			return MimeType.JAVASCRIPT.ordinal();
		case "zip":
			return MimeType.ZIP.ordinal();
		case "css":
			return MimeType.CSS.ordinal();
		case "docx":
			return MimeType.MS_WORD_2007.ordinal();
		case "xlsx":
			return MimeType.MS_EXCEL_2007.ordinal();
		case "pptx":
			return MimeType.MS_POWER_POINT_2007.ordinal();
		case "svg":
			return MimeType.SVG.ordinal();
		default:
			return null;
		}
	}

	private Condition domainCondition;
	private String companyLogo;
	private Map<String, String> rAttachs = new HashMap<>();
	private Map<String, String> appParams = new HashMap<>();


	private Customize(Condition domainCondition) {
		this.domainCondition = domainCondition;
	}
	
	public Customize setFavicon(String favicon) {
		rAttachs.put("favicon.svg", favicon);
		return this;
	}

	public Customize setFavicon(URL favicon) {
		rAttachs.put("favicon.svg", favicon.toString());
		return this;
	}

	public Customize setLoginLogo(URL loginLogo) {
		rAttachs.put("aon-login-logo", loginLogo.toString());
		return this;
	}

	public Customize setLoginLogo(String loginLogo) {
		rAttachs.put("aon-login-logo", loginLogo);
		return this;
	}
	
	public Customize setHeaderLogo(URL headerLogo) {
		rAttachs.put("aon-header-logo", headerLogo.toString());
		return this;
	}

	public Customize setHeaderLogo(String headerLogo) {
		rAttachs.put("aon-header-logo", headerLogo);
		return this;
	}

	public Customize setTitle(String title) {
		appParams.put("AON_CUSTOMIZE_TITLE", title);
		return this;
	}
	
	public Customize setSupportEmail(String supportEmail) {
		appParams.put("AON_CUSTOMIZE_SUPPORT_EMAIL", supportEmail);
		return this;
	}
	
	public Customize setSupportPhone(String supportPhone) {
		appParams.put("AON_CUSTOMIZE_SUPPORT_PHONE", supportPhone);
		return this;
	}
	
	public Customize setTheme(String theme) {
		appParams.put("AON_CUSTOMIZE_THEME", theme);
		return this;
	}
	
	public Customize setCompanyLogo(URL companyLogo) {
		this.companyLogo = companyLogo.toString();
		return this;
	}

	public Customize setCompanyLogo(String companyLogo) {
		this.companyLogo = companyLogo;
		return this;
	}
	
	public Customize setVariable(String name, String value) {
		appParams.put(name, value);
		return this;
	}
	
	

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		dslContext.transaction(config -> {
			config.dsl().select()
			.from(DOMAIN)
			.where(domainCondition)
			.fetchInto(DOMAIN).forEach(domain -> {
				
				// Add CUSTOM_VIEW_APP to domains if not exists and activate it
				DomainAppRecord customViewApp = 
				config.dsl()
				.select()
				.from(DOMAIN_APP)
				.where(DOMAIN_APP.DOMAIN.eq(domain.getId()))
				.and(DOMAIN_APP.APP.eq(CUSTOM_VIEW_APP))
				.fetchOptionalInto(DOMAIN_APP)
				.orElseGet(() -> {
					DomainAppRecord newCustomViewApp = config.dsl().newRecord(DOMAIN_APP);
					newCustomViewApp.setDomain(domain.getId());
					newCustomViewApp.setApp(CUSTOM_VIEW_APP);
					return newCustomViewApp;
				});
				customViewApp.setActive((byte) 1);
				customViewApp.store();
				
				Optional<Integer> domainRegistry = 
				config.dsl()
				.select()
				.from(COMPANY)
				.where(COMPANY.DOMAIN.eq(domain.getId()))
				.fetchOptional(COMPANY.REGISTRY);
				
				domainRegistry.ifPresent( customizeId -> appParams.putIfAbsent("AON_CUSTOMIZE_ID", customizeId.toString()) );
				
				// Add app parameters for CUSTOM_VIEW_APP 
				appParams.forEach((paramName, paramValue) -> {
					AppParamRecord supportPhoneParam =
					config.dsl()
					.select()
					.from(APP_PARAM)
					.where(APP_PARAM.DOMAIN.eq(domain.getId()))
					.and(APP_PARAM.NAME.eq(paramName))
					.fetchOptionalInto(APP_PARAM)
					.orElseGet(() -> {
						AppParamRecord newSupportPhoneParam = config.dsl().newRecord(APP_PARAM);
						newSupportPhoneParam.setDomain(domain.getId());
						newSupportPhoneParam.setName(paramName);
						return newSupportPhoneParam;
					});
					supportPhoneParam.setValue(paramValue);
					supportPhoneParam.store();
					System.out.println("Customized " + paramName + " for domain " + domain.getName() + " with value " + paramValue);
				});
				
				domainRegistry.ifPresent(registry -> {
					rAttachs.forEach((description, str) -> {
						
						RattachRecord rattach =
						config.dsl()
						.select()
						.from(RATTACH)
						.where(RATTACH.TYPE.eq((byte) 2))
						.and(RATTACH.REGISTRY.eq(registry))
						.and(RATTACH.DOMAIN.eq(domain.getId()))
						.and(RATTACH.DESCRIPTION.eq(description))
						.fetchOptionalInto(RATTACH)
						.orElseGet(() -> {
							RattachRecord newRattach = 
							config.dsl().newRecord(RATTACH);
							newRattach.setType((byte) 2);
							newRattach.setRegistry(registry);
							newRattach.setDomain(domain.getId());
							newRattach.setDescription(description);
							return newRattach;
						});
						try {
							URL url = URI.create(str).toURL();
							byte [] data = url.openStream().readAllBytes();
							rattach.setData(data);
							Integer mimeType = getMimeType(str.substring(str.lastIndexOf(".") + 1));
							rattach.setMimetype(mimeType != null ? mimeType.byteValue() : null);
							rattach.store();
							System.out.println("Customized " + description + " for domain " + domain.getName() + " with url " + url + " (size: " + data.length + " bytes)" + (mimeType != null ? " and mime type " + MimeType.values()[mimeType] : ""));
						} catch (IOException e) {
							rattach.delete();
							System.out.println("Failed to customize " + description + " for domain " + domain.getName() + " with url " + str + ": " + e.getMessage());
						}
						if ( companyLogo != null  ) {
							RattachRecord companyLogoRattach =
							config.dsl()
							.select()
							.from(RATTACH)
							.where(RATTACH.TYPE.eq((byte) 0)) // LOGO
							.and(RATTACH.REGISTRY.eq(registry))
							.and(RATTACH.DOMAIN.eq(domain.getId()))
							.fetchOptionalInto(RATTACH)
							.orElseGet(() -> {
								RattachRecord newCompanyLogoRattach = 
								config.dsl().newRecord(RATTACH);
								newCompanyLogoRattach.setType((byte) 0); // LOGO
								newCompanyLogoRattach.setRegistry(registry);
								newCompanyLogoRattach.setDomain(domain.getId());
								return newCompanyLogoRattach;
							});
							try {
								URL url = URI.create(companyLogo).toURL();
								byte [] data = url.openStream().readAllBytes();
								companyLogoRattach.setData(data);
								Integer mimeType = getMimeType(companyLogo.substring(companyLogo.lastIndexOf(".") + 1));
								companyLogoRattach.setMimetype(mimeType != null ? mimeType.byteValue() : null);
								companyLogoRattach.store();
								System.out.println("Customized company logo for domain " + domain.getName() + " with url " + url + " (size: " + data.length + " bytes)" + (mimeType != null ? " and mime type " + MimeType.values()[mimeType] : ""));
							} catch (IOException e) {
								companyLogoRattach.delete();
								 System.out.println("Failed to customize company logo for domain " + domain.getName() + " with url " + companyLogo + ": " + e.getMessage());
							}
						}
					});
					
					
				});
				
				
			});

		});
	}
	
	
	
	
}
