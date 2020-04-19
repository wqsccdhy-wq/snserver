package com.seeyon.oa.exchange;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.common.base.Strings;

public class SeeyonUtil {

	public static String getTempFilePath() {
		Date date = new Date();
		String dataForm = new SimpleDateFormat("yyyy/MM/dd/").format(date);
		String path = ServletListener.FILE_PATH + File.separator + dataForm;

		return path;

	}

	public static String getCurrenDate(String format) {
		Date date = new Date();
		// "yyyy-MM-dd"
		if (Strings.isNullOrEmpty(format)) {
			format = "yyyy-MM-dd";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
//	public static void main(String[] args) {
//		GovdocInfo info = new GovdocInfo();
//		info.setUnitName("name");
//		info.setTitle("title");
//		info.setCreateTime(SeeyonUtil.getCurrenDate("yyyy-MM-dd"));
//		info.setDocMark("wh");
//		List<String> attsFile = new ArrayList<String>();
//		attsFile.add("e:/d1.txt");
//		attsFile.add("e:/d2.txt");
//		attsFile.add("e:/d3.txt");
//		info.setAttrList(attsFile);
//		String jsonString = JSONObject.toJSONString(info, SerializerFeature.WriteMapNullValue);
//		System.out.println(jsonString);
//	}

}
