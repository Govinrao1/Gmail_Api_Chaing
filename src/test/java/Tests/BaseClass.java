package Tests;

import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import EndPoints.GmailEndPoints;

public class BaseClass {
       
	public static String emailid="testerbatch262@gmail.com";
	public static String password="1234567@#";
	//Step-1: get clientId and client-secret manually via navigation(one time only)
	public static String clientID="909071325588-8j74h9noi8ji3v08p83oehl6vsbbspqf.apps.googleusercontent.com";
	public static String clientsecret="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";
	//Step-2: get desktop code using browser manually via navigation(for every run)
	public String desktopcode="4/0AbUR2VM_3wEZVhuXuiNFhPVgHJEnSogojuCAFX9LjynrBuqWRB_H6zK4Heu8bbl9Zk1bKw";
	public String AccessToken;
	public GmailEndPoints obj;
	

	@BeforeClass
	public void setup() {
		//Create object to utility class
		obj=new GmailEndPoints();

		//Get access token
		AccessToken=obj.getAccessToken(desktopcode, clientID, clientsecret);
		System.out.println(AccessToken);
		Reporter.log("This is a test is starting...", true);
	}
	@AfterClass
	public void afterClassExecuted() {
		Reporter.log("This is a test was completed...", true);
	}
	
}
