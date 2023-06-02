package Tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import io.restassured.response.Response;

public class Gmail_Tests extends BaseClass {
	public static String encodedbody;
	@Test(priority=1, enabled=true)
	public void getMailsWithDifferentParams() {
		//Get mails by giving max count
		Response res1=obj.getAllMailsViaMaxResults(AccessToken,emailid,200,true); //our required count is 200
		res1.then().log().body();
		Assert.assertEquals(res1.statusCode(),200);
		Assert.assertEquals(res1.contentType(), "application/json; charset=UTF-8");
	    Assert.assertEquals(res1.header("Content-Encoding"), "gzip");
	    Reporter.log("This is a test 1 was completed...", true);
	    //Get mails by giving page number
		Response res2=obj.getAllMailsViaPageToken(AccessToken, emailid,3,false); //page number is 3
		Assert.assertEquals(res1.statusCode(),200);
		Assert.assertEquals(res1.contentType(), "application/json; charset=UTF-8");
		Assert.assertEquals(res1.header("Content-Encoding"), "gzip");
		Reporter.log("This is a test 2 was completed...", true);
		//Get mails by giving search query
		Response res3=obj.getAllMailsViaQuery(AccessToken, emailid,"from:google-noreply@google.com",true);
		Assert.assertEquals(res1.statusCode(),200);
		Assert.assertEquals(res1.contentType(), "application/json; charset=UTF-8");
		Assert.assertEquals(res1.header("Content-Encoding"), "gzip");
		Reporter.log("This is a test 3 was completed...", true);
	}

	@Test(priority=2, enabled=true)
	public void getSpecificMail() {
		Response res1=obj.getAllMailsViaPageToken(AccessToken, emailid,1,false); //page number is 1
		int count=res1.body().jsonPath().getInt("messages.size()");
		for(int i=0; i<count;i++) {
			String id=res1.body().jsonPath().getString("messages["+i+"].id");
			Response res2=obj.getMail(AccessToken, emailid, id);
			System.out.println(res2.body().jsonPath().getString("snippet"));
			
		}
		Assert.assertEquals(res1.statusCode(),200);
		Assert.assertEquals(res1.contentType(), "application/json; charset=UTF-8");
		
	}

	@Test(priority=4, enabled=true)
	public void sendMailTospecificToAddress() {
		//Step-2: get base64 encoded value for mail content manually via navigation(for every new mail)
		 encodedbody="RnJvbTp0ZXN0ZXJiYXRjaDI2MkBnbWFpbC5jb20KVG86IGdvdmluZHRlc3RlcjEwMTFAZ21haWwuY29tCkNjOiBnb"
	    + "3ZpbmR0ZXN0ZXIxMDExQGdtYWlsLmNvbQpCY2M6IHRlc3RlcmJhdGNoMjYyQGdtYWl"
	    + "sLmNvbQpTdWJqZWN0OiBHb29nbGUgQVBJIFRlc3QgRW1haWwKCkhpLAoKVGhpcyBpcyBhIHRlc3"
	    + "QgZW1haWwgc2VudCB2aWEgR29vZ2xlIEFQSSB0aHJvdWdoIFJlc3RBc3N1cmVkLUphdmEgY29kaW5nLgoKUmVnYXJkcy"
	    + "wKR292aW5kCkJhdGNoIDI2Mg==";
		Response res1=obj.sendMail(AccessToken, emailid,encodedbody);
		Assert.assertEquals(res1.statusCode(),200);
		Assert.assertEquals(res1.contentType(), "application/json; charset=UTF-8");
		Assert.assertEquals(res1.header("Content-Encoding"), "gzip");
	}
	
	@Test(priority=3, enabled=true)
	public void deleteSpecificMail() {
		Response res1=obj.getAllMailsViaMaxResults(AccessToken,emailid,200,true);
		String id=res1.body().jsonPath().getString("messages[4].id");
		System.out.println("message id: "+id);
		Assert.assertEquals(res1.statusCode(),200);
		Response res3=obj.deleteMail(AccessToken, emailid, id);
		Assert.assertEquals(res3.statusCode(),204);
		Assert.assertEquals(res3.contentType(), "application/json; charset=UTF-8");
	}

	@Test(priority=5, enabled=true)
	public void sendMailWithAttachment() throws IOException {
		String Subject = "Email with attachment and text through API - Reg";
		String toEmail = "testerbatch262@gmail.com";
		String textBody = "Hi,\n" +
				"\n" +
				"I'm testing message body with attachment through API.\n" +
				"\n" +
				"Regards,\n" +
				"Govind\n" +
				"Batch 262\n";
		String fileName = "1.jpg";
		File file = new File("src\\test\\resources\\DataFiles\\R Govindfresher_Resume.pdf");

		Response res2=obj.uploadAttachment(AccessToken, emailid,toEmail, Subject, textBody, fileName, file);
		Assert.assertEquals(res2.statusCode(),200);
		Assert.assertEquals(res2.contentType(), "application/json; charset=UTF-8");

	}
}
