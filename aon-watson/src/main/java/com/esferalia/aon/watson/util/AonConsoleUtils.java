package com.esferalia.aon.watson.util;

public class AonConsoleUtils {
	public static final String ANSI_RESET = "\u001B[0m";

	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static final String ANSI_BLACK_BOLD = "\033[1;30m";
	public static final String ANSI_RED_BOLD = "\033[1;31m";
	public static final String ANSI_GREEN_BOLD = "\033[1;32m";
	public static final String ANSI_YELLOW_BOLD = "\033[1;33m";
	public static final String ANSI_BLUE_BOLD = "\033[1;34m";
	public static final String ANSI_PURPLE_BOLD = "\033[1;35m";
	public static final String ANSI_CYAN_BOLD = "\033[1;36m";
	public static final String ANSI_WHITE_BOLD = "\033[1;37m";

	public static final String ANSI_BLACK_BRIGHT = "\u001B[90m";
	public static final String ANSI_RED_BRIGHT = "\u001B[91m";
	public static final String ANSI_GREEN_BRIGHT = "\u001B[92m";
	public static final String ANSI_YELLOW_BRIGHT = "\u001B[93m";
	public static final String ANSI_BLUE_BRIGHT = "\u001B[94m";
	public static final String ANSI_PURPLE_BRIGHT = "\u001B[95m";
	public static final String ANSI_CYAN_BRIGHT = "\u001B[96m";
	public static final String ANSI_WHITE_BRIGHT = "\u001B[97m";

	public static final String ANSI_BLACK_BACKGROUND = "\u001b[40m";
	public static final String ANSI_RED_BACKGROUND = "\u001b[41m";
	public static final String ANSI_GREEN_BACKGROUND = "\u001b[42m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001b[43m";
	public static final String ANSI_BLUE_BACKGROUND = "\u001b[44m";
	public static final String ANSI_MAGENTA_BACKGROUND = "\u001b[45m";
	public static final String ANSI_CYAN_BACKGROUND = "\u001b[46m";
	public static final String ANSI_WHITE_BACKGROUND = "\u001b[47m";
	
	public static String reset() {
		return ANSI_RESET;
	}
	
	public static String color(String color, String text) {
		return color + AonStringUtils.defaultString(text) + ANSI_RESET;
	}

	public static String black(String text) {
		return color(ANSI_BLACK, text);
	}

	public static String red(String text) {
		return color(ANSI_RED, text);
	}

	public static String green(String text) {
		return color(ANSI_GREEN, text);
	}

	public static String yellow(String text) {
		return color(ANSI_YELLOW, text);
	}

	public static String blue(String text) {
		return color(ANSI_BLUE, text);
	}

	public static String purple(String text) {
		return color(ANSI_PURPLE, text);
	}

	public static String cyan(String text) {
		return color(ANSI_CYAN, text);
	}

	public static String white(String text) {
		return color(ANSI_WHITE, text);
	}

	public static String blackBold(String text) {
		return color(ANSI_BLACK_BOLD, text);
	}

	public static String redBold(String text) {
		return color(ANSI_RED_BOLD, text);
	}

	public static String greenBold(String text) {
		return color(ANSI_GREEN_BOLD, text);
	}

	public static String yellowBold(String text) {
		return color(ANSI_YELLOW_BOLD, text);
	}

	public static String blueBold(String text) {
		return color(ANSI_BLUE_BOLD, text);
	}

	public static String purpleBold(String text) {
		return color(ANSI_PURPLE_BOLD, text);
	}

	public static String cyanBold(String text) {
		return color(ANSI_CYAN_BOLD, text);
	}

	public static String whiteBold(String text) {
		return color(ANSI_WHITE_BOLD, text);
	}

	public static String blackBright(String text) {
		return color(ANSI_BLACK_BRIGHT, text);
	}

	public static String redBright(String text) {
		return color(ANSI_RED_BRIGHT, text);
	}

	public static String greenBright(String text) {
		return color(ANSI_GREEN_BRIGHT, text);
	}

	public static String yellowBright(String text) {
		return color(ANSI_YELLOW_BRIGHT, text);
	}

	public static String blueBright(String text) {
		return color(ANSI_BLUE_BRIGHT, text);
	}

	public static String purpleBright(String text) {
		return color(ANSI_PURPLE_BRIGHT, text);
	}

	public static String cyanBright(String text) {
		return color(ANSI_CYAN_BRIGHT, text);
	}

	public static String whiteBright(String text) {
		return color(ANSI_WHITE_BRIGHT, text);
	}
 
	public static String blackBackground() {
		return ANSI_BLACK_BACKGROUND;
	}

	public static String redBackground() {
		return ANSI_RED_BACKGROUND;
	}

	public static String greenBackground() {
		return ANSI_GREEN_BACKGROUND;
	}

	public static String yellowBackground() {
		return ANSI_YELLOW_BACKGROUND;
	}

	public static String blueBackground() {
		return ANSI_BLUE_BACKGROUND;
	}

	public static String magentaBackground() {
		return ANSI_MAGENTA_BACKGROUND;
	}

	public static String cyanBackground() {
		return ANSI_CYAN_BACKGROUND;
	}

	public static String whiteBackground() {
		return ANSI_WHITE_BACKGROUND;
	}

}
