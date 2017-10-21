/*
 * Copyright (c) InContent.
 * Author: Val
 * Modified At: 15-8-25 下午1:48
 */

import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhenghuiqiang on 17/10/20.
 */
public class ImageHelper {

	public static final String BLANK_IMG_DECODED = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVQIW2P8////fwAKAAP+j4hsjgAAAABJRU5ErkJggg==";

	//contenttype/image stream
	public static Pair<String, InputStream> decodeBase64Image(String s) {
		Pair<String, InputStream> p = new Pair(null, null);
		String[] arr = s.split(";base64,");
		p.setFirst(arr[0].replaceAll("data:", ""));
		try {
			p.setSecond(new ByteArrayInputStream(new BASE64Decoder().decodeBuffer(arr[1])));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return p;
	}

	public static InputStream produceUnitBlankPng() {
		return decodeBase64Image(BLANK_IMG_DECODED).getSecond();
	}

}
