package org.stagemonitor.core.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.stagemonitor.core.configuration.converter.SetValueConverter;

public class StringUtils {

	public static final Pattern CAMEL_CASE = Pattern.compile("(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])");

	public static String removeStart(final String str, final String remove) {
		if (remove != null && str.startsWith(remove)) {
			return str.substring(remove.length());
		} else {
			return str;
		}
	}

	public static String capitalize(String self) {
		return Character.toUpperCase(self.charAt(0)) + self.substring(1);
	}

	public static String splitCamelCase(String s) {
		return CAMEL_CASE.matcher(s).replaceAll(" ");
	}

	public static String dateAsIsoString(Date date) {
		TimeZone tz = TimeZone.getDefault();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		df.setTimeZone(tz);
		return df.format(date);
	}

	public static String timestampAsIsoString(long timestamp) {
		return dateAsIsoString(new Date(timestamp));
	}

	public static String slugify(String s) {
		return replaceWhitespacesWithDash(s.toLowerCase().replaceAll("[^\\w ]+", ""));
	}

	public static boolean isNotEmpty(String s) {
		return !isEmpty(s);
	}

	public static boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}

	public static String asCsv(String[] strings) {
		return SetValueConverter.STRINGS_VALUE_CONVERTER.toString(Arrays.asList(strings));
	}

	public static String getLogstashStyleDate() {
		return getLogstashStyleDate(System.currentTimeMillis());
	}

	public static String getLogstashStyleDate(long time) {
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(new Date(time));
	}

	public static String replaceWhitespacesWithDash(String s) {
		if (s == null) {
			return null;
		}
		return s.replaceAll("\\s", "-");
	}

	public static String toCommaSeparatedString(String... strings) {
		StringBuilder sb = new StringBuilder();
		for (String value : strings) {
			sb.append(value).append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
}
