package com.esferalia.aon.in.payroll.pdf.api.toolkit;

import java.util.Collection;
import java.util.Date;

public class ConsoleToolkit {

	public static int tabs;

	public static void startConsole() {
		tabs = 0;
	}

	public static void startConsole(int t) {
		tabs = t;
	}

	private static String tabulate(String str) {
		for (int i = 0; i < tabs; i++)
			str = "  " + str;
		return str;
	}

	public static String tb(int n) {
		String s = "";
		for (int i = 0; i < n; i++)
			s += "\t";
		return s;
	}

	public static void log(Object name, Object o) {

		String value = "empty";
		if (o != null && !o.toString().trim().equals(""))
			value = o.toString();

		System.out.println(tabulate(name + ": \t " + value));
	}

	public static void slog(Object o) {

		String value = "empty";
		if (o != null && !o.toString().trim().equals(""))
			value = o.toString();

		System.out.println(tabulate(value));
	}

	public static void log(Object name, Object o, Date d1) {
		Date d2 = new Date();
		double seconds = (d2.getTime() - d1.getTime()) / 1000.00;

		String value = "empty";
		if (o != null && !o.toString().trim().equals(""))
			value = o.toString();

		System.out.println(tabulate(seconds + "s " + name + ": " + value));

	}

	public static void logMany(Object name, Collection<Object> o) {
		int i = 0;
		for (Object object : o) {
			log(i, object);
			i++;
		}
	}

	public static void startSection(String title) {
		System.out.println(tabulate(title));
		System.out.println(tabulate(
				"------------------------------------------------------------------------------------------------------------------"));
		tab();
	}

	public static void endSection() {
		untab();
		System.out.println(tabulate(
				"-----------------------------------------------------------------------------------------------------------------"));
	}

	public static void tab() {
		tabs++;
	}

	public static void tab(int n) {
		for (int i = 0; i < n; i++)
			tabs++;
	}

	public static void untab() {
		tabs--;
	}

	public static void jump(int n) {
		for (int i = 0; i < n; i++)
			System.out.println();
	}

	public static void title(String title) {
		System.out.println("\n############################################################################");
		System.out.println("    " + title);
		System.out.println("############################################################################");
	}

	public static void logInfo(String message) {
		System.out.println(tabulate("[Info] " + message));
	}

	public static void logWarning(String message) {
		System.out.println(tabulate("[Warning] " + message));
	}

	public static void logError(String message) {
		System.err.println(tabulate("[Error] " + message));
	}
}
