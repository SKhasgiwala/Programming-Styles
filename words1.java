package Week7.plugins1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import Week7.TFWords;

public class words1 implements TFWords {
	
	public String[] extract_words(String path_to_file) {
		StringBuilder sb = new StringBuilder();
		String data;
		String[]  wordList;
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(path_to_file));
            String line;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line).append("\n");
            }
            reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        data = sb.toString().replaceAll("[^A-Za-z0-9]+", " ");
        data = data.toLowerCase();
        wordList = data.split(" ");
        
        
        
        File file = new File("stop_words.txt");  
		Scanner sc = null;
		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		String[] stopWords= null;
				
		while (sc.hasNext()) { 
			stopWords=sc.nextLine().split(",");
			}
		
		List<String> stopWordsList = Arrays.asList(stopWords);
		List<String> wordList2 = Arrays.asList(wordList);
		ArrayList<String> arrayList = new ArrayList<>();
		
		
		Iterator itr=wordList2.iterator();
	       while(itr.hasNext()){
	           String word = (String) itr.next();
	           if(word.length() <= 1) continue;
	           if(stopWordsList.contains(word)) continue;

	           arrayList.add(word);
	       }
		
	
		
		String[] stringArray = new String[arrayList.size()];
		stringArray = arrayList.toArray(stringArray);
		
		return stringArray;
		
		
		
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		words1 obj=new words1();
		
		
	}
	
}
