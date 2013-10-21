package mode;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class SurfClipseModeManager {
	
	public static int current_mode=0;
	//0: interactive mode
	//1: proactive mode
	String user_home;
	
	public SurfClipseModeManager()
	{
		//code for managing mode
		this.user_home=System.getProperty("user.home");	
	}
	
	protected boolean create_and_init_mode_settings_file()
	{
		//code for creating setting file
		boolean response=false;
		File file=new File(this.user_home+"/surfclipse");
		if(!file.exists())
		{
			file.mkdir();
		}
		String surfclipse_dir=this.user_home+"/surfclipse";
		File sf=new File(surfclipse_dir+"/scmode.txt");
		if(!sf.exists())
		{
			try
			{
			sf.createNewFile();
			FileWriter fwriter=new FileWriter(sf);
			fwriter.write("mode:0\n");
			fwriter.close();
			response=true;
			}catch(Exception exc){}
		}else response=true;
		
		return response;
	}
	
	public void check_current_mode_settings()
	{
		//code for mode settings
		String setting_file=this.user_home+"/surfclipse/scmode.txt";
		File f=new File(setting_file);
		if(f.exists())
		{
			try
			{
			Scanner scanner=new Scanner(f);
			while(scanner.hasNext())
			{
				String line=scanner.nextLine();
				line.trim();
				if(!line.isEmpty())
				{
					String _current_mode=line.split(":")[1];
					current_mode=Integer.parseInt(_current_mode);
					break;
				}
			}
			}catch(Exception exc){
				
			}
		}else
		{
			create_and_init_mode_settings_file();
			current_mode=0;
		}
	}
	public void save_current_mode_settings()
	{
		//code for saving current mode
		String setting_file=this.user_home+"/surfclipse/scmode.txt";
		try
		{
		File f=new File(setting_file);
		if(!f.exists())f.createNewFile();
		FileWriter writer=new FileWriter(f);
		writer.write("mode:"+current_mode+"\n");
		writer.close();
		}catch(Exception exc){}
		
	}
	
	
	

}
