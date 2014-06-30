package guang.crawler.extension.urlExtractor;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONParser {
	private static void formatJSON(final File file)
			throws FileNotFoundException, IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		
		try {
			int ch = -1;
			int depth = 0;
			while ((ch = reader.read()) != -1) {
				if (('{' == ch) || ('[' == ch)) {
					sb.append("\n");
					for (int i = 0; i < depth; i++) {
						sb.append(" ");
					}
					sb.append((char) ch);
					depth++;
					sb.append("\n");
					for (int i = 0; i < depth; i++) {
						sb.append(" ");
					}
				} else if (('}' == ch) || (']' == ch)) {
					depth--;
					sb.append("\n");
					for (int i = 0; i < depth; i++) {
						sb.append(" ");
					}
					sb.append((char) ch);
				} else if (',' == ch) {
					sb.append((char) ch);
					sb.append("\n");
					for (int i = 0; i < depth; i++) {
						sb.append(" ");
					}
				} else if (('\n' == ch) || (' ' == ch)) {
					// 吃掉换行
				} else {
					sb.append((char) ch);
				}
			}

		} finally {
			reader.close();
		}
		BufferedOutputStream fileout = new BufferedOutputStream(
				new FileOutputStream(file));
		try {
			fileout.write(sb.toString().getBytes());
		} finally {
			fileout.close();
		}
	}

	public static void main(final String[] args) throws IOException {
		File file = new File("/home/sun/desktop/QQComment");
		JSONParser.formatJSON(file);
		JSONParser.transferUnicode(file);
	}

	private static void transferUnicode(final File file)
			throws FileNotFoundException, IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
		        new FileInputStream(file)));
		try {
			String line;
			Pattern pattern = Pattern.compile("\\\\u([0-9a-f]{4})");
			while ((line = reader.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				while (matcher.find()) {
					String unicode = matcher.group(1);
					char data = (char) Integer.parseInt(unicode, 16);
					matcher.appendReplacement(sb, String.valueOf(data));
				}
				matcher.appendTail(sb);
				sb.append("\n");
			}
		} finally {
			reader.close();
		}
		BufferedOutputStream fileout = new BufferedOutputStream(
				new FileOutputStream(file));
		try {
			fileout.write(sb.toString().getBytes());
		} finally {
			fileout.close();
		}

	}
}
