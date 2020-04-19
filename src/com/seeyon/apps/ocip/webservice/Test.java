package com.seeyon.apps.ocip.webservice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.ws.Endpoint;

import org.apache.commons.io.FileUtils;

import com.seeyon.apps.ocip.webservice.impl.OcipWebServiceImpl;

public class Test {

	public static void main(String[] args) {
		// Endpoint.publish("http://localhost:8089/test", new Function());
		// Endpoint.publish("http://127.0.0.1:8089/ocipdemo/services/ocipWebService",
		// new OcipWebServiceImpl());
		/// System.out.println("Publish Success");
		// Long summaryId = Long.valueOf(Long.parseLong("9936600602426562522"));

		// Date date = new Date();
		// String dataForm = new SimpleDateFormat("yyyy/MM/dd/").format(date);
		// System.out.println(dataForm);
		// String path = "F:file" + File.separator + new
		// SimpleDateFormat("yyyy/MM/dd/").format(date);
		// File f = new File(path);
		//
		// if (!f.exists()) {
		//
		// f.mkdirs();
		//
		// }

		// Matcher matcher =
		// Pattern.compile("[\\u005C/:\\u002A\\u003F\"<>\'\\u007C’‘“”:?]").matcher(name);

		String fileName = "四川省生态环境厅办公室\n关于征集2020年度调研课题的通知.Pdf";
		Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
		Matcher matcher = pattern.matcher(fileName);

		String name = matcher.replaceAll(""); // 将匹配到的非法字符以空替换
		System.out.println(name.trim());
		File file = new File("c://" + name.trim());
		BufferedWriter out = null;

		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			out.write("11");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
