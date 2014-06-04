package de.unipotsdam.ontologypersistence.owl.access;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class URLParamEncoder {

	public static String encode(String input) {
		StringBuilder resultStr = new StringBuilder();
		for (char ch : input.toCharArray()) {
			if (isUnsafe(ch)) {
				resultStr.append('%');
				resultStr.append(toHex(ch / 16));
				resultStr.append(toHex(ch % 16));
			} else {
				resultStr.append(ch);
			}
		}
		return resultStr.toString();
	}

	private static char toHex(int ch) {
		return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
	}

	private static boolean isUnsafe(char ch) {
		if (ch > 128 || ch < 0)
			return true;
		return " %$&+,/:;=?@<>#%".indexOf(ch) >= 0;
	}

	public static String encodeURLComponent(final String s) {
		if (s == null) {
			return "";
		}

		final StringBuilder sb = new StringBuilder();

		try {
			for (int i = 0; i < s.length(); i++) {
				final char c = s.charAt(i);

				if (((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z')) || ((c >= '0') && (c <= '9')) || (c == '-') || (c == '.') || (c == '_') || (c == '~')) {
					sb.append(c);
				} else {
					final byte[] bytes = ("" + c).getBytes("UTF-8");

					for (byte b : bytes) {
						sb.append('%');

						int upper = (((int) b) >> 4) & 0xf;
						sb.append(Integer.toHexString(upper).toUpperCase(Locale.US));

						int lower = ((int) b) & 0xf;
						sb.append(Integer.toHexString(lower).toUpperCase(Locale.US));
					}
				}
			}

			return sb.toString();
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException("UTF-8 unsupported!?", uee);
		}
	}
}
