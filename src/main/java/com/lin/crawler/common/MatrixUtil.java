package com.lin.crawler.common;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import javax.imageio.ImageIO;

import com.lin.crawler.common.httpclient.RequestUtils;
import com.lin.crawler.common.httpclient.HttpRequestData;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 使用ZXing2.3，生成条码的辅助类。可以编码、解码。编码使用code包，解码需要javase包。
 * 
 */
public final class MatrixUtil {
	private static final String CHARSET = "utf-8";
	/**
	 * 禁止生成实例，生成实例也没有意义。
	 */
	private MatrixUtil() {
	}


	public static void main(String[] args) throws Exception {

		HttpRequestData httpRequestData = new HttpRequestData();

		String html = IOUtils.toString(new FileReader(new File("C:\\Users\\admin\\Desktop\\test.html")));
		Document document = Jsoup.parse(html);
		Elements elements = document.getElementsByTag("img");
		for(int i = 0; i < elements.size(); i++) {
			Element element = elements.get(i);
			String src = element.attr("data-src");

			if(StringUtils.isBlank(src)) {
				src = element.attr("src");
			}

			if(StringUtils.isNotBlank(src)) {
				byte[] image = RequestUtils.getBytes(httpRequestData, src, null, new HashMap<>(0));
				InputStream inputStream = new ByteArrayInputStream(image);
				String qrcode = decode(inputStream);
				if(StringUtils.isNotBlank(qrcode)) {
					element.remove();
				}
			}
		}

		IOUtils.write(document.html(), new FileOutputStream(new File("C:\\Users\\admin\\Desktop\\test.html")));
	}


	/**
	 * 解码，需要javase包。
	 * 文件方式解码
	 * @param file
	 * @return
	 */
	public static String decode(File file) {
		BufferedImage image;
		try {
			if (file == null || file.exists() == false) {
				throw new Exception(" File not found:" + file.getPath());
			}
			image = ImageIO.read(file);
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			Result result;
			// 解码设置编码方式为：utf-8，
			Hashtable<DecodeHintType,String> hints = new Hashtable<DecodeHintType,String>();
			hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
			result = new MultiFormatReader().decode(bitmap, hints);
			return result.getText();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public static String decode(String link) {
		try {
			HttpRequestData httpRequestData = new HttpRequestData();
			byte[] image = RequestUtils.getBytes(httpRequestData, link, null, new HashMap<>(0));
			InputStream inputStream = new ByteArrayInputStream(image);
			return decode(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}


	/**
	 * 流方式解码
	 * @param input
	 * @return
	 */
	public static String decode(InputStream input) {
		
		BufferedImage image;
		try {
			if (input == null ) {
				throw new Exception(" input is null");
			}
			
			image = ImageIO.read(input);
			
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			
			Result result;
			
			// 解码设置编码方式为：utf-8，
			Hashtable<DecodeHintType,String> hints = new Hashtable<DecodeHintType,String>();
			hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
			
			result = new MultiFormatReader().decode(bitmap, hints);
			
			return result.getText();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}