package EndPoints;
import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ResourceBundle;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.json.JSONTokener;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
public class GmailEndPoints {
	
	static ResourceBundle getUrl()
	{
		ResourceBundle routes =ResourceBundle.getBundle("GmailProperties"); // load properties file
		return routes;
	}
	
	public String getAccessToken(String desktopcode, String clientID, String clientsecret)
	{
		String post_url =getUrl().getString("OAuth2_Url");
		
		Response res=RestAssured.given().urlEncodingEnabled(false)
	            .queryParam("code",desktopcode)
	            .queryParam("client_id",clientID)
	            .queryParam("client_secret",clientsecret)
	            .queryParam("redirect_uri", "http://localhost")
	            .queryParam("grant_type","authorization_code")
	            .when().post(post_url);
		
		System.out.println("The response with Access Token is : " +res.asPrettyString());
		String AccessToken = res.jsonPath().get("access_token");
		return(AccessToken);
	}
	public Response getAllMailsViaMaxResults(String AccessToken, String emailid, int max, boolean spam)
	{
		String getAllmes_url =getUrl().getString("getAllMes_Url");
		
		Response r=given()
		           .header("Authorization","Bearer "+AccessToken)
		          // .pathParam("userId", emailid)
		           .queryParam("maxResults",max)
		           .queryParam("includeSpamTrash",spam) //only to work with INBOX+promotions+social
		           .when()
		           .get(getAllmes_url);
		return(r);
	}
	
	public Response getAllMailsViaPageToken(String AccessToken, String emailid, int pn, boolean spam)
	{
		String getAllmes_url =getUrl().getString("getAllMes_Url");
		
		Response r=given()
		           .header("Authorization","Bearer "+AccessToken)
		          // .pathParam("userId", emailid)
		           .queryParam("pageToken",pn)
		           .queryParam("includeSpamTrash",spam)
		           .when()
		           .get(getAllmes_url);
		 return(r);
	}
	
	public Response getAllMailsViaQuery(String AccessToken, String emailid, String query, boolean spam)
	{
		String getAllmes_url =getUrl().getString("getAllMes_Url");
		Response r=given()
		           .header("Authorization","Bearer "+AccessToken)
		         //  .pathParam("userId", emailid)
		           .queryParam("q",query)
		           .queryParam("includeSpamTrash",spam)
		           .when()
		           .get(getAllmes_url);
		return(r);
	}
	
	public Response getMail(String AccessToken, String emailid, String id)
	{
		String get_url =getUrl().getString("get_Url");
		Response r=given()
		           //.pathParam("userId", emailid)
		           .pathParam("mesId", id)
		           .header("Authorization","Bearer "+AccessToken)
		           .queryParam("format","minimal")
		           .when()
		           .get(get_url);
		return(r);
	}
	
	public Response sendMail(String AccessToken, String emailid, String encodedbody)
	{
		String post_url =getUrl().getString("post_Url");
		
		String temp="{\r\n" + 
				"  \"raw\":\""+encodedbody+"\"\r\n" + 
				"}";
		Response r=given()
		           .header("Authorization","Bearer "+AccessToken)
		          // .pathParam("userId", emailid)
		           .body(temp)
		           .when()
		           .post(post_url);
		return(r);
	}

	public Response uploadAttachment(String AccessToken, String emailid,String toEmail, String Subject, String textBody, String fname, File file) throws IOException {
		
		String attachment_url =getUrl().getString("Attachment_Url");
		
		// Message details
		String sub = Subject;
		String from = emailid;
		String to = toEmail;
		String txtbody = textBody;

		// File details
		String fileName = fname;
		FileInputStream fileInputStreamReader = new FileInputStream(file);
		byte[] bytes = new byte[(int)file.length()];
		fileInputStreamReader.read(bytes);
		String encodedFile = new String(Base64.encodeBase64(bytes), "UTF-8");

		// Build the MIME message
		String boundary = "01";
		String x="Subject: "+sub+" \n" +
				"From: "+from+"\n" +
				"To: "+to+"\n" +
				"Content-Type: multipart/mixed; boundary=\"01\" \n" +
				"\r\n" +
				"--" + boundary + "\r\n" +
				"Content-Type: text/plain; charset=\"UTF-8\"\r\n" +
				"\r\n" +
				txtbody + "\r\n" +
				"\r\n" +
				"--" + boundary + "\r\n" +
				"Content-Type: image/jpeg; name=\"" + fileName + "\"\r\n" +
				"Content-Transfer-Encoding: base64\r\n" +
				"Content-Disposition: attachment; filename=\"" + fileName + "\"\r\n" +
				"\r\n" +
				encodedFile + "\r\n" +
				"--" + boundary + "--";


		Response r=given()
				.header("Authorization", "Bearer " + AccessToken)
				//.pathParam("userId", emailid)
				.header("Content-Type", "message/rfc822")
				.config(RestAssured.config().encoderConfig(encoderConfig().encodeContentTypeAs("message/rfc822", ContentType.TEXT)))
				.body(x)
				.post(attachment_url);

		return (r);
	}

	public Response deleteMail(String AccessToken, String emailid, String id)
	{
		String delete_url =getUrl().getString("delete_Url");
		Response r=given()
		           .header("Authorization","Bearer "+AccessToken)
		          // .pathParam("userId", emailid)
		           .pathParam("mesId", id)
		           .when()
		           .delete(delete_url);
		return(r);
	}  
      
}
