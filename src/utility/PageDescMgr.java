package utility;

public class PageDescMgr {

	public static String normalizePageDescriptions(String pageDesc)
	{
		//normalizing the page description
		if(pageDesc.trim().isEmpty())return "";
		String normDesc=new String();
		pageDesc=pageDesc.trim();
		String[] lines=pageDesc.split("\\r?\\n");
		if(lines.length>0){
			for(int i=0;i<lines.length;i++)
			normDesc+=lines[i].trim()+" ";
		}
		return normDesc.trim();
	}
}
