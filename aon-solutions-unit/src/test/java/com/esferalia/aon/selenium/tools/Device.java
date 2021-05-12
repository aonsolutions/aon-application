package com.esferalia.aon.selenium.tools;

public enum Device {

	//----PHONES-----------------------------------
	IPHONE_5(
		"Apple iPhone 5", 
		320, 
		568,
		2,
		"Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1",
		true
	),
	IPHONE_6(
		"Apple iPhone 6",
		375,
		667,
		2,
		"Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1",
		true   
	),
	IPHONE_X(
		"iPhone X/XS",
		375,
		812,
		3,
		"Mozilla/5.0 (iPhone; CPU iPhone OS 12_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/12.0 Mobile/15A372 Safari/604.1",
		true
	),
	NEXUS_5(
		"Google Nexus 5",
		360,
		640,
		3,
		"Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 5 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19",
		true
	),
	PIXEL_2(
		"Pixel 2",
		411,
		731,
		2.625,
		"Mozilla/5.0 (Linux; Android 8.0; Pixel 2 Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Mobile Safari/537.36",
		true
	),
	PIXEL_2XL(
		"Pixel 2 XL",
		411,
		823,
		3.5,
		"Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL Build/OPD1.170816.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Mobile Safari/537.36",
		true
			
	),

	
	//----TABLETS----------------------------------
	IPAD(
		"iPad",
		768,
		1024,
		2,
		"Mozilla/5.0 (iPad; CPU OS 11_0 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 Mobile/15A5341f Safari/604.1",
		true      
	),
	IPAD_PRO(
		"iPad Pro (12.9-inch)",
		1024,
		1366,
		2,
		"Mozilla/5.0 (iPad; CPU OS 11_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1",
		true
	),
	
	//----DESKTOP/LAPTOP---------------------------
	HD_LAPTOP(
		"Notebook with HD screen",
		1280,
		720,
		2,
		"",
		false	
	),
	HD_LAPTOP_TOUCH(
		"Notebook with HD screen",
		1280,
		720,
		2,
		"",
		true	
	),
	FULL_HD_LAPTOP(
		"Notebook FullHD",
		1920,
		1080,
		2,
		"",
		false	
	),
	FULL_HD_LAPTOP_TOUCH(
		"Notebook FullHD",
		1920,
		1080,
		2,
		"",
		true
	),
	QUAD_HD_LAPTOP(
		"Notebook 4k",
		3840,
		2160,
		2,
		"",
		false	
	),
	;

	private String name;
	private int width;
	private int height;
	private double pixelRatio;
	private String userAgent;
	private boolean touch;

	private Device(String name, int width, int height, double pixelRatio, String userAgent, boolean touch) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.pixelRatio = pixelRatio;
		this.userAgent = userAgent;
		this.touch = touch;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public double getPixelRatio() {
		return this.pixelRatio;
	}
	
	public String getUserAgent() {
		return this.userAgent;
	}
	
	public boolean isTouchable() {
		return this.touch;
	}	
	
}
