package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ContentLoader {
	
	public static String loadFileContent(String fileName) {
		// code for loading the file name
		String fileContent = new String();
		try {
			File f = new File(fileName);
			BufferedReader bufferedReader = new BufferedReader(
					new FileReader(f));
			while (bufferedReader.ready()) {
				String line = bufferedReader.readLine();
				fileContent += line + "\n";
			}
		} catch (Exception ex) {
			// handle the exception
		}
		return fileContent;
	}
}
