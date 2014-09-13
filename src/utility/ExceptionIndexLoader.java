package utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
//import core.StaticData;

public class ExceptionIndexLoader {
	public static ArrayList<Integer> loadExceptionIndices() {
		// loading the exceptions
		String indexFile ="";// StaticData.QCDataset + "/eIndex.txt";
		ArrayList<Integer> indices = new ArrayList<>();
		try {
			Scanner scanner = new Scanner(new File(indexFile));
			while (scanner.hasNext()) {
				String line = scanner.nextLine().trim();
				indices.add(Integer.parseInt(line));
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return indices;
	}
}
