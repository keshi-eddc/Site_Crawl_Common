package fun.jerry.common.enumeration;

public enum RequestType {
	
	DRIVER_CHROME,
	
	HTTP_GET,
	
	HTTP_POST;
	
	public static void main(String[] args) {
		System.out.println(DRIVER_CHROME.equals(RequestType.DRIVER_CHROME));
		System.out.println(DRIVER_CHROME.equals(RequestType.HTTP_GET));
	}
}
