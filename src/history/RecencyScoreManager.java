package history;

import java.io.File;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;


public class RecencyScoreManager {

	String Firefox_History_File;
	String Chrome_History_File;
	String user_home;
	String driverName;
	public static ArrayList<HistoryLink> RecentFiles;
	String Surfclipse_history_file;
	ArrayList<String> blackList;
	
	
	long currentTimeStamp=System.currentTimeMillis()/1000;
	long time_constant=15638400;//6 month time difference
		
	public RecencyScoreManager()
	{
		//default constructor
		this.user_home=System.getProperty("user.home");
		this.Firefox_History_File=new String();
		this.Firefox_History_File=get_Firefox_History_Path();
		this.Chrome_History_File=new String();
		this.Chrome_History_File=get_Chrome_History_Path();
		
		this.blackList=new ArrayList<String>();
		this.create_black_list();
		this.driverName="org.sqlite.JDBC";
		RecentFiles=new ArrayList<HistoryLink>();
		this.Surfclipse_history_file=new String();
		
		//loading SurfClipse history file
		this.load_surfclipse_history();
	}
	
	
	protected String get_surfclipse_history_file()
	{
		//code for getting SurfClipse history file
		try
		{
		File file=new File(this.user_home+"/surfclipse");
		if(!file.exists())
		{
			file.mkdir();	
		}
		File f2=new File(this.user_home+"/surfclipse/history.txt");
		if(!f2.exists())
		{
			f2.createNewFile();
			this.Surfclipse_history_file=f2.getAbsolutePath();
		}
		else this.Surfclipse_history_file=f2.getAbsolutePath();
		}catch(Exception exc){}
		
		System.out.println("History File:"+this.Surfclipse_history_file);
		
		return this.Surfclipse_history_file;
	}
	
	protected void create_black_list() {
		// code for creating a black list
		blackList.add("google.ca");
		blackList.add("yahoo.com");
		blackList.add("bing.com");
		blackList.add("facebook.com");
		blackList.add("linkedin.com");
		blackList.add("twitter.com");
		blackList.add("gmail.com");
		blackList.add("mail.google.com");
		blackList.add("mail.yahoo.com");
		blackList.add("odesk.com");
		blackList.add("elance.com");
		blackList.add("urgrove.com");
		blackList.add("prothom-alo.com");
	}
	
	
	public void load_surfclipse_history()
	{
		//code for loading SurfClipse history data
		try
		{
			//getting SurfClipse history file
			this.Surfclipse_history_file=get_surfclipse_history_file();
			
			//opening SurfClipse history file
			Scanner scanner=new Scanner(new File(this.Surfclipse_history_file));
			while(scanner.hasNext())
			{
				String line=scanner.nextLine();
				String[] parts=line.split("\\s+");
				String url=parts[0];
				if(contains_url_parameter(url))continue;
				long last_visit= Long.parseLong(parts[1]);
				HistoryLink link=new HistoryLink();
				link.linkURL=url;
				link.last_visit_time=last_visit;
				RecentFiles.add(link);
			}
			
			//how ever if the history file is currently empty
			if(RecentFiles.isEmpty())
			{
				//collect data from browsers
				RecentFiles=this.collect_recent_files_from_browsers();
			}
		}catch(Exception exc){
		}
	}
	
	public void calculate_recency_score()
	{
		//code for calculating rececy scores
		double max_recency_score=0;
		for(HistoryLink link:RecentFiles)
		{
			long last_visit=link.last_visit_time;
			long current_time_difference=(this.currentTimeStamp-last_visit)/1000;
			double decay_power=current_time_difference/60;
			double decay_score=1/(Math.exp(decay_power));
			link.recency_score=decay_score;
			if(decay_score>max_recency_score)max_recency_score=decay_score;
		}
		//normalization
		for(HistoryLink hlink:RecentFiles)
		{
			hlink.recency_score=hlink.recency_score/max_recency_score;
		}
		
		System.out.println("Recency score calculated successfully!");
	}
	
	public void save_recent_files()
	{
		//code for saving recent files
		try
		{
		PrintWriter writer=new PrintWriter(new File(this.Surfclipse_history_file));
		for(int i=0;i<20;i++) //always saves top 20
		{
			try
			{
			HistoryLink link=RecentFiles.get(i);
			String line=link.linkURL+"\t"+link.last_visit_time;
			writer.write(line+"\n");
			}catch(Exception exc){}
		}
		writer.close();
		System.out.println("Recent files saved successfully!");
		}catch(Exception exc)
		{
			exc.printStackTrace();
		}
	}
	
	
	protected String get_Firefox_History_Path()
	{
		//code for getting Firefox history path
		String profile_path=this.user_home+"/AppData/Roaming/Mozilla/Firefox/Profiles";
		File file=new File(profile_path);
		if(file.isDirectory()){
			File[] files=file.listFiles();
			for(File f:files)
			{
				if(f.getName().endsWith("default"))
				{
					this.Firefox_History_File=profile_path+"/"+f.getName()+"/places.sqlite";
					break;
				}
			}
		}
		//returning FireFox history
		return this.Firefox_History_File;
	}
	
	protected String get_Chrome_History_Path()
	{
		//code for getting Chrome history path
		String user_data=this.user_home+"/AppData/Local/Google/Chrome/User Data/Default/";
		this.Chrome_History_File=user_data+"/History";
		//returning Chrome history
		return this.Chrome_History_File;
	}
	
	protected boolean contains_url_parameter(String url)
	{
		boolean resp=false;
		//code for checking if it contains parameter
		URL u;
		try {
			u = new URL(url);
			if(u.getQuery()!=null)resp=true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resp;
	}
	
	
	protected ArrayList<HistoryLink> get_Firefox_history_files()
	{
		//code for FireFox history files
		ArrayList<HistoryLink> tempFiles=new ArrayList<HistoryLink>();
		try
		{
	    System.out.println("From FF");
		Class.forName(this.driverName);
		Connection connection=null;
		connection=DriverManager.getConnection("jdbc:sqlite:"+this.Firefox_History_File);
		Statement statement=connection.createStatement();
		String select_query="SELECT title, url, visit_date from moz_places, moz_historyvisits where moz_places.id=moz_historyvisits.id order by visit_date DESC LIMIT 50";
		ResultSet results=statement.executeQuery(select_query);
		while(results.next())
		{
			
			HistoryLink hlink=new HistoryLink();
			hlink.title=results.getString("title");
			String tempURL=results.getString("url");
			if(contains_url_parameter(tempURL))continue;
			hlink.linkURL=tempURL;
			hlink.last_visit_time=Long.parseLong(results.getString("visit_date"))/1000000; //base 1970
			//System.out.println(" URL: "+hlink.title+", Last visit:"+hlink.last_visit_time);
			tempFiles.add(hlink);
		}
		}catch(Exception exc){
			exc.printStackTrace();
		}
		return tempFiles;
	}
	
	
	protected ArrayList<HistoryLink> get_Chrome_history_files()
	{
		//code for Chrome history files
		ArrayList<HistoryLink> tempFiles=new ArrayList<HistoryLink>();
		try
		{
		System.out.println("From Chrome");
		Class.forName(this.driverName);
		Connection connection=null;
		connection=DriverManager.getConnection("jdbc:sqlite:"+this.Chrome_History_File);
		Statement statement=connection.createStatement();
		String select_query="SELECT * from urls order by last_visit_time DESC LIMIT 50";
		ResultSet results=statement.executeQuery(select_query);
		while(results.next())
		{
			
			HistoryLink hlink=new HistoryLink();
			hlink.title=results.getString("title");
			String tempURL=results.getString("url");
			if(contains_url_parameter(tempURL))continue;
			hlink.linkURL=tempURL;
			hlink.last_visit_time=Long.parseLong(results.getString("last_visit_time"));
			hlink.last_visit_time=(hlink.last_visit_time/1000000)-11644473600L;  //base 1601/1/1
			//System.out.println(" URL: "+hlink.title+", Last visit:"+hlink.last_visit_time);
			tempFiles.add(hlink);
		}
		}catch(Exception exc){
			exc.printStackTrace();
		}
		return tempFiles;
	}
	
	protected ArrayList<HistoryLink> filter_history_links(ArrayList<HistoryLink> tempFiles)
	{
		//code for filtering history links
		ArrayList<HistoryLink> temp2=new ArrayList<HistoryLink>();
		temp2.addAll(tempFiles);
		for(HistoryLink link:tempFiles)
		{
			for(String domain:blackList)
			{
				if(link.linkURL.contains(domain))
				{
					temp2.remove(link);
					break;
				}
			}
		}
		//returning the elements
		return temp2;
	}
	
	
	
	protected ArrayList<HistoryLink> collect_recent_files_from_browsers()
	{
		//code for collecting recent history links
		try
		{
			ArrayList<HistoryLink> temp1 = new ArrayList<>();
			try {
				temp1 = get_Firefox_history_files();
			} catch (Exception exc) {
			}
			ArrayList<HistoryLink> temp2 = new ArrayList<>();
			try {
				temp2 = get_Chrome_history_files();
			} catch (Exception exc) {

			}
			if (temp1.size() > 0)
				RecentFiles.addAll(temp1);
			else if (temp2.size() > 0)
				RecentFiles.addAll(temp2);
			// filtering elements
			RecentFiles = filter_history_links(RecentFiles);
			// sorting recent files
			Collections.sort(RecentFiles,
					new CustomComparator_last_visit_time());
		}catch(Exception exc){
			exc.printStackTrace();
		}
		return RecentFiles;
	}
	
	
	public class CustomComparator_last_visit_time implements Comparator<HistoryLink> {
	    @Override
	    public int compare(HistoryLink o1, HistoryLink o2) {
	    	if(o1.last_visit_time>o2.last_visit_time)return -1;
	    	else if(o1.last_visit_time<o2.last_visit_time)return 1;
	    	else return 0;
	    }
	}
	
	
	public static void main(String args[])
	{
		//main method
		RecencyScoreManager manager=new RecencyScoreManager();
		manager.calculate_recency_score();
		for(HistoryLink hlink:RecentFiles)
		{
			System.out.println(hlink.linkURL+" "+hlink.recency_score);
		}
	}
}
