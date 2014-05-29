package guang.crawler.util;

import java.io.File;

public class PathHelper {

	public static String getName(String path) {
		while (path.endsWith("/")) {
			if ("/".equals(path)) {
				return path;
			}
			path = path.substring(0, path.length() - 1);
		}
		int pos = path.lastIndexOf('/');
		if (pos == -1) {
			return path;
		}
		String result = path.substring(pos + 1);
		return result;

	}

	public static String getParent(String path) {
		while (path.endsWith("/")) {
			if ("/".equals(path)) {
				return path;
			}
			path = path.substring(0, path.length() - 1);
		}
		int pos = path.lastIndexOf('/');
		if (pos == 0) {
			return "/";
		} else if (pos == -1) {
			return "";
		}
		String result = path.substring(0, pos);
		return result;
	}

	public static void main(String[] args) {
		System.out.println(PathHelper.getParent("/test/../t////"));
		File file = new File("/test/../t/");
		System.out.println(file.getParent());
		System.out.println(PathHelper.getName("/test/../t////"));
		System.out.println(file.getName());
	}
}
