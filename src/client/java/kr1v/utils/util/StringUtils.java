package kr1v.utils.util;

import java.util.Locale;

public class StringUtils {
	public static String splitSnakeCase(String name) {
		String[] words = name.split("_");
		StringBuilder friendlyName = new StringBuilder();
		boolean isFirst = true;
		for (String word : words) {
			if (isFirst) {
				friendlyName.append(word.substring(0, 1).toUpperCase(Locale.ROOT));
				friendlyName.append(word.substring(1).toLowerCase(Locale.ROOT));
			} else {
				friendlyName.append(word.toLowerCase(Locale.ROOT));
			}
			isFirst = false;
		}
		return friendlyName.toString();
	}
}
