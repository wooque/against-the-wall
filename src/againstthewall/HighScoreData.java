package againstthewall;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

public class HighScoreData {
	
	public static final String filename = "high_score";

	public static ArrayList<Integer> loadHighScoreData() throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		
		final ArrayList<Integer> scores = new ArrayList<>();
		
		String line = reader.readLine();
		while (line != null) {
			scores.add(Integer.parseInt(line));
			line = reader.readLine();
		}
		
		reader.close();
		return scores;
	}
	
	
	public static void saveHighScore(ArrayList<Integer> scores) throws IOException {
		
		PrintWriter printer = new PrintWriter(new FileWriter(filename), true);
		
		Collections.sort(scores, Collections.reverseOrder());
		for(Integer score: scores) {
			printer.println(score);
		}
		
		printer.close();
	}
}
