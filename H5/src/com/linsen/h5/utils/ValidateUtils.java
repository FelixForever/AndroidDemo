package com.linsen.h5.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtils {
	public static final String REGEX_EMAIL = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
	public static final String REGEX_MOBILE = "(\\d{11})|(\\+\\d{3,})";//"^(13|15|18)\\d{9}$";
	public static final String REGEX_PHONE = "^((\\(\\d{2,3}\\))|(\\d{3}\\-))?(\\(0\\d{2,3}\\)|0\\d{2,3}-)?[1-9]\\d{6,7}(\\-\\d{1,4})?$";
	public static final String REGEX_USERNAME = "^[a-zA-Z][a-zA-Z0-9_]{5,15}$";
	public static final String REGEX_CN_NAME = "^[\\u0391-\\uFFE5]+$";
	public static final String REGEX_EN_NAME = "^\\w+[\\w\\s]+\\w+$";
	public static final String REGEX_PWD = "[A-Za-z0-9]{6,12}$";
	public static final String REGEX_ZIP = "^[1-9]\\d{5}$";
	public static final String REGEX_QQ = "^[1-9]\\d{4,9}$";
	public static final String REGEX_NUMERIC = "\\d*";
	public static final String REGEX_WEIXIN = "^[a-zA-Z][a-zA-Z0-9\\-]{5,19}$";
	public static final String REGEX_ID_CARD = "\\d{15}|\\d{18}";

	public static boolean matchRegex(String regex, String source) {
		if (StringUtil.isNull(source)) {
			return false;
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);
		return matcher.matches();
	}

	public static boolean isCardId(String cardId) {
		return matchRegex(REGEX_ID_CARD, cardId);
	}

	public static boolean isEmail(String email) {
		return matchRegex(REGEX_EMAIL, email);
	}

	public static boolean isMobile(String mobile) {
		return matchRegex(REGEX_MOBILE, mobile);
	}

	public static boolean isPassword(String password) {
		return matchRegex(REGEX_PWD, password);
	}

	public static boolean isUserName(String userName) {
		return matchRegex(REGEX_USERNAME, userName);
	}

	public static boolean isName(String name) {
		return matchRegex(REGEX_CN_NAME, name) || matchRegex(REGEX_EN_NAME, name);
	}

	public static boolean isPhone(String phone) {
		return matchRegex(REGEX_PHONE, phone);
	}

	public static boolean isPhoneOrMobile(String phone) {
		return matchRegex(REGEX_PHONE, phone) || matchRegex(REGEX_MOBILE, phone);
	}

	public static boolean isZip(String zip) {
		return matchRegex(REGEX_ZIP, zip);
	}

	public static boolean isQQ(String qq) {
		return matchRegex(REGEX_QQ, qq);
	}

	public static boolean length(String str, int min, int max) {
		return str.length() >= min && str.length() <= max;
	}

	public static boolean isNumeric(String source) {
		return matchRegex(REGEX_NUMERIC, source);
	}

	public static boolean isWeixin(String weixin) {
		return matchRegex(REGEX_WEIXIN, weixin);
	}

}
