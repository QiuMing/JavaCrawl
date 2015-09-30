package JavaCrawl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description A demo to get HTML 
 * @author Ming
 * @date: 2015年9月30日--下午12:31:54
 */
public class GetHtml {
	public static void main(String[] args) throws Exception {
		URL url = new URL("http://www.hiphop8.com/city/guangdong/guangzhou.php");
		String urlsource = getURLSource(url);

		//正则匹配获取电话号码
		Pattern p = Pattern.compile(">(13\\d{5}|15\\d{5}|18\\d{5}|147\\d{4})<");
		Matcher m = p.matcher(urlsource);

		int num = 0;
		System.out.println(num);
		while (m.find()) {
			System.out.println(m.group(1) + "  " + (num++));
		}
		System.out.println(num);
		System.out.println(m.groupCount());
	}
	/*
	 * 根据url获取html
	 */
	 public static String getURLSource(URL url) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000); 
		InputStream inStream = conn.getInputStream(); 

		byte[] data = readInputStream(inStream); 
		String htmlSource = new String(data);
		return htmlSource;
	}

	public static byte[] readInputStream(InputStream inStream)
			throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1204];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}
}