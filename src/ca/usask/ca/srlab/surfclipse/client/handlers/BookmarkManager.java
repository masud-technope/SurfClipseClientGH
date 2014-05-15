package ca.usask.ca.srlab.surfclipse.client.handlers;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
public class BookmarkManager {
   
	//book mark file URL
	static String bookmarkFileName=System.getProperty("user.home")+"/surfclipse/bookmark.json";
	public static void createBookMarkFile()
	{
		//code for creating book mark file
		try{
			File f=new File(bookmarkFileName);
			if(!f.exists()){
				f.createNewFile();
				//add a favorite link
				try{
					JSONArray array=new JSONArray();
					JSONObject jobject=new JSONObject();
					jobject.put("title", "Google");
					jobject.put("url", "http://www.google.ca");
					jobject.put("date", new Date().toString());
					array.add(jobject);
					FileWriter writer=new FileWriter(f);
					array.writeJSONString(writer);
					writer.close();
					System.out.println("Bookmark file created successfully.");
				}catch(Exception exc){
					exc.printStackTrace();
				}
			}
		}catch(Exception exc){
		}
	}
	
	static String getFileContent()
	{
		//code for getting file content
		String content=new String();
		try{
			File f=new File(bookmarkFileName);
			Scanner scanner=new Scanner(f);
			while(scanner.hasNext())
			{
				String line=scanner.nextLine();
				content+=line;
			}
		}catch(Exception exc){
		}
		return content;
	}
	
	
	public static void addBookMark(String title, String resultURL)
	{
		//code for adding book mark
		try{
			String jsonStr=getFileContent();
			JSONParser parser=new JSONParser();
			JSONArray array=(JSONArray)parser.parse(jsonStr);
			JSONObject bookmakrObj=new JSONObject();
			bookmakrObj.put("title", title);
			bookmakrObj.put("url", resultURL);
			bookmakrObj.put("date", new Date().toString());
			array.add(bookmakrObj);
			FileWriter writer=new FileWriter(bookmarkFileName);
			array.writeJSONString(writer);
			writer.close();
		}catch(Exception exc){
			exc.printStackTrace();
		}
	}
	
	
	public static HashMap<String, String> loadBookMarks(){
		//code for loading current book marks
		HashMap<String, String> bookmarks=new HashMap<>();
		try{
			String filecontent=getFileContent();
			JSONParser parser=new JSONParser();
			JSONArray array=(JSONArray) parser.parse(filecontent);
			for(int i=0;i<array.size();i++){
				JSONObject jobj=(JSONObject)array.get(i);
				String title=jobj.get("title").toString();
				String url=jobj.get("url").toString();
				if(!bookmarks.containsKey(title))
				bookmarks.put(title, url);
			}
		}catch(Exception exc){
		}
		return bookmarks;
	}
	
	
}
