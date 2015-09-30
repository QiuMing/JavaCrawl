package JavaCrawl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawl {
	private static Connection conn = Database.connectToDataBase();
	static String home_url = "http://www.hiphop8.com";   			//爬取网站的首页

	static String pattern_province_city = "<DIV class=title><SPAN>(.*?) - (.*?)<\\/SPAN><\\/DIV>";		//正则匹配获取省、市名称 
	static String pattern_number = ">(13\\d{5}|15\\d{5}|18\\d{5}|147\\d{4})<"; 		//正则匹配号码段
	static String pattern_province = "\\w{3}\\.\\w{7}\\.\\w{3}\\/\\w{4}\\/\\w+";    //正则匹配获取省份ULR
	static String pattern_city_hz = "<LI><A href=\"(.*?)\" target=_blank>";     	//正则匹配获取ULR中城市的后缀

	static String insertSQL = "insert ignore into number_segment(segment,provincevince,city) values(?, ?, ?)";
	static PreparedStatement pst = null;

	static int num_province = 0;
	static int num_city = 0;
	static int all_num_tele = 0;

	public static void main(String[] args) throws Exception {
		String PreStat = "insert ignore into number_segment(segment,provincevince,city) values (?,?,?) ";
		pst = conn.prepareStatement(PreStat.toString());

		Matcher mat_home = getMatcherResult(home_url, pattern_province);

		long start = System.currentTimeMillis();
		while (mat_home.find()) {
			num_province++;
			System.out.println("------第"+num_province+"个省-----");
			String city_url_qz = "http://" + mat_home.group() + "/";
			int len = city_url_qz.length();

			StringBuffer city_ur = new StringBuffer();
			city_ur.append(city_url_qz);
			Matcher mat_city_hz = getMatcherResult(city_url_qz, pattern_city_hz);
			while (mat_city_hz.find()) 
			{
				num_city++;
				System.out.println("第"+num_city+"个市");
				String last_city_url = city_ur.append(mat_city_hz.group(1))
						.toString();
				// String last_city_url = city_url_qz + mat_city_hz.group(1);
				int len2 = last_city_url.length();

				One_City_Tele_to_DB(last_city_url);
				city_ur.delete(len, len2);
			}
		}
		long end = System.currentTimeMillis();
		long time = (end - start) / (1000 * 60);
		conn.close();
		System.out.println("查询到的电话号码段总数量："+all_num_tele);
	    System.out.println("花费的时间是:"+time);
	}

	public static void One_City_Tele_to_DB(String url) throws Exception {
		int this_city_num = 0;
		String province = null;
		String city = null;

		Matcher mat_province_city = getMatcherResult(url, pattern_province_city); 
		while (mat_province_city.find()) {
			String long_province = mat_province_city.group(1);
			province = long_province.substring(0, long_province.length() - 1);
			String long_city = mat_province_city.group(2);
			city = long_city.substring(0, long_city.length() - 10);
			System.out.println(province + "  city" + city);
		}

		Matcher mat_number = getMatcherResult(url, pattern_number); 
		while (mat_number.find()) {
			pst.setString(1, mat_number.group(1));
			pst.setString(2, province);
			pst.setString(3, city);
			pst.addBatch();
			this_city_num++;
			all_num_tele++;
		}
		pst.executeBatch(); 
		pst.clearBatch();
		System.out.println("该市插入的号码段的数量是："+ this_city_num); 
	}

	public static Matcher getMatcherResult(String str_url, String pattern) throws Exception {
		String urlsource = getHtml(str_url);
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(urlsource);
		return m;
	}

	public static String getHtml(String str_url) throws IOException {
		URL url = new URL(str_url);
		String content = "";
		StringBuffer page = new StringBuffer();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream()));
			while ((content = in.readLine()) != null) {
				page.append(content);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return page.toString();
	}
}
