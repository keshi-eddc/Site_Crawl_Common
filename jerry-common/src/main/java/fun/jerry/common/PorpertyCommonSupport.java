package fun.jerry.common;

import java.util.ResourceBundle;

/**
 * 
 * @author conner
 *
 */
public class PorpertyCommonSupport {
	
	public static String getStringValue(String property, String key) {
		return ResourceBundle.getBundle(property).getString(key);
	}
	
	public static int getIntValue(String property, String key) {
		return Integer.parseInt(ResourceBundle.getBundle(property).getString(key));
	}
}
