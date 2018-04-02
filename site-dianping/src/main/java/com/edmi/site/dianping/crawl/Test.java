package com.edmi.site.dianping.crawl;

import org.apache.log4j.Logger;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import fun.jerry.browser.WebDriverSupport;
import fun.jerry.common.LogSupport;
import fun.jerry.common.enumeration.Project;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.common.enumeration.Site;
import fun.jerry.httpclient.bean.HttpRequestHeader;
import fun.jerry.httpclient.core.HttpClientSupport;

@Component
public class Test {
	
	private static Logger log = LogSupport.getJdlog();
	
	public static void main(String[] args) {
//		WebDriver driver = WebDriverSupport.getChromeDriverInstance(null);
//		driver.manage().deleteAllCookies();
//		driver.manage().addCookie(new Cookie("bcookie", "\"v=2&346cbeef-10f4-454d-82cc-068fdabd931c\""));
//		driver.manage().addCookie(new Cookie("bscookie", "\"v=1&201803200400446db4d492-707f-43d6-8f36-6d3e4d244058AQGcrMcMXiEZKddlhBMZzxwxaNHPgOdJ\""));
//		driver.manage().addCookie(new Cookie("visit", "\"v=1&G\""));
//		driver.manage().addCookie(new Cookie("lang", "v=2&lang=zh-cn"));
//		driver.manage().addCookie(new Cookie("liap", "true"));
//		driver.manage().addCookie(new Cookie("JSESSIONID", "ajax:1875872609262722676"));
//		driver.manage().addCookie(new Cookie("li_at", "AQEDASaA3poEmsYxAAABYlGfB5UAAAFidauLlVEAyr2D4aKgkZcSX-5ltamxnL3sXOK5WJ66f2e_kZKdtDH1Nosfi6EQk34KjkyflFHyPeX5P8tJriddrGJCK8Nyg3KOjEJuEYIQRjS4IfwEy-cXvnXY;"));
//		driver.manage().addCookie(new Cookie("_lipt", "CwEAAAFiUaVcyna1_BtjoSQjGDNQ40ENpBz_J9zVc2vyiW4rMgIZko3O1czuAB0VbbdspC0KRR82SyI5hL_yVyQfiTM5e4qrUi_7IlDsjWsfSi6QCB49vfKnCdY"));
//		driver.manage().addCookie(new Cookie("lidc", "b=SGST01:g=3:u=1:i=1521790348:t=1521874073:s=AQEiH40P7myxAdVxPMNRJ1T2P7oa6XLn"));
//		driver.get("https://www.linkedin.com/in/%E4%BA%AC%E6%B6%9B-%E5%BC%A0-886639b8/?trk=public-profile-join-page");
//		log.info(driver.getPageSource());
//		driver.close();
//		driver.quit();
		
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("https://sycm.taobao.com/adm/execute/preview.json?date=2018-02-21,2018-04-02&itemId=41847571860&dateId=1007376&dateType=static&desc=&filter=%5b13,7%5d&id=null&name=&owner=user&show=%5b%7b%22id%22:1007434%7d,%7b%22id%22:1007433%7d,%7b%22id%22:1007435%7d,%7b%22id%22:1016033%7d,%7b%22id%22:1015331%7d,%7b%22id%22:1015332%7d,%7b%22id%22:1007440%7d,%7b%22id%22:1007439%7d,%7b%22id%22:1007441%7d,%7b%22id%22:1016054%7d,%7b%22id%22:1007425%7d,%7b%22id%22:1007424%7d,%7b%22id%22:1016009%7d,%7b%22id%22:1007426%7d,%7b%22id%22:1007428%7d,%7b%22id%22:1007427%7d,%7b%22id%22:1007429%7d,%7b%22id%22:1016021%7d,%7b%22id%22:1015330%7d%5d&sycmToken=d5dd54af1&_=1437469440528&sessionid=&token=880801064");
//		header.setUrl("https%3a%2f%2fsycm.taobao.com%2fadm%2fexecute%2fpreview.json%3fdate%3d2018-02-21%2c2018-04-02%26itemId%3d41847571860%26dateId%3d1007376%26dateType%3dstatic%26desc%3d%26filter%3d%5b13%2c7%5d%26id%3dnull%26name%3d%26owner%3duser%26show%3d%5b%7b%2522id%2522%3a1007434%7d%2c%7b%2522id%2522%3a1007433%7d%2c%7b%2522id%2522%3a1007435%7d%2c%7b%2522id%2522%3a1016033%7d%2c%7b%2522id%2522%3a1015331%7d%2c%7b%2522id%2522%3a1015332%7d%2c%7b%2522id%2522%3a1007440%7d%2c%7b%2522id%2522%3a1007439%7d%2c%7b%2522id%2522%3a1007441%7d%2c%7b%2522id%2522%3a1016054%7d%2c%7b%2522id%2522%3a1007425%7d%2c%7b%2522id%2522%3a1007424%7d%2c%7b%2522id%2522%3a1016009%7d%2c%7b%2522id%2522%3a1007426%7d%2c%7b%2522id%2522%3a1007428%7d%2c%7b%2522id%2522%3a1007427%7d%2c%7b%2522id%2522%3a1007429%7d%2c%7b%2522id%2522%3a1016021%7d%2c%7b%2522id%2522%3a1015330%7d%5d%26sycmToken%3dd5dd54af1%26_%3d1437469440528%26sessionid%3d%26token%3d880801064");
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate, br");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setCookie("t=3cbf0810ae24d23b67d9ba4eccf5720c; cna=77gvE/IgakgCAcuc19Ne64x2; JSESSIONID=870D19DB423C287B3F156B548DE628C3; cookie2=1f1af9959e5b7d2cdcfcbe1b6ae33cf0; _tb_token_=efa774f537635; x=2255775604; mt=ci=0_0; uc1=cookie14=UoTePMFnlekhyA%3D%3D&lng=zh_CN; uc3=nk2=&id2=&lg2=; tracknick=; sn=%E8%81%94%E5%90%88%E5%88%A9%E5%8D%8E%E6%B5%B7%E5%A4%96%E6%97%97%E8%88%B0%E5%BA%97%3A%E8%81%94%E5%90%88%E5%88%A9%E5%8D%8E; csg=d91ba689; unb=3441319194; skt=0b806f808aac9502; _euacm_ac_l_uid_=3441319194; 3441319194_euacm_ac_c_uid_=2255775604; 3441319194_euacm_ac_rs_uid_=2255775604; _euacm_ac_rs_sid_=113762738; v=0; _portal_version_=new; adm_version=new; isg=BDEx5bOYzwElrWNoGdAlz66VQL0LtqZ8c73omRNHv_gFOlaMW2wTYKAYWM5c8j3I; apushfa8a005f5c4454addbfb2a0acef2604a=%7B%22ts%22%3A1522638133362%2C%22parentId%22%3A1522637469726%7D");
		header.setPragma("no-cache");
		header.setUpgradeInsecureRequests("1");
		header.setAutoUa(false);
		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
		header.setHost("sycm.taobao.com");
		
		header.setProject(Project.OTHER);
		header.setSite(Site.TAOBAO);
		header.setProxyType(ProxyType.NONE);
		System.out.println(HttpClientSupport.get(header).getContent());
	}
}
