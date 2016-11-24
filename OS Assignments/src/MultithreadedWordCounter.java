import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/*
 Filename: MultithreadedWordCounter.java
 Version: 1.0
 Objective: A program to count the frequency of words in a text file. The text file
 is partitioned into N segments. Each segment is processed by a separate thread that outputs
 the intermediate frequency count for its segment. The main process waits until all
 the threads complete; then it computes the consolidated word-frequency data based on the
 individual threads’ output.
 Author: Abhishek Jadav
 created: 1 Oct, 2016
 List of API/Library: -
 */
public class MultithreadedWordCounter {

	// Give the path of the file to open
	private static String fileName = "D:/file.txt";

	public static void main(String[] args) {

		try {
			// Open Buffer to read the contents of the file
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);

			// Next line in the text
			String line = null;

			// To keep the track of line number
			int lineNumber = 0;

			// We'll use list to get the words with their counts in the main
			// process
			HashMap<String, Integer> finalWordList = new HashMap<String, Integer>();

			// We'll use list to keep the track of the threads
			ArrayList<MyThread> threads = new ArrayList<MyThread>();

			// Start all the threads based on segments i.e. number of lines
			while ((line = br.readLine()) != null) {
				MyThread thread = new MyThread(lineNumber++, line);
				thread.start();
				threads.add(thread);
			}

			for (int i = 0; i < threads.size(); i++) {
				try {					
					// Wait for all threads to joining
					threads.get(i).join();

					// Print intermediate results
					threads.get(i).printIntermediateResult();
					
					// As soon as a thread join retrieve the word count list
					// from it
					HashMap<String, Integer> threadWordList = threads.get(i)
							.getWordList();
					Set<String> set = threadWordList.keySet();

					for (Iterator<String> it = set.iterator(); it.hasNext();) {
						String key = it.next();
						if (finalWordList.containsKey(key)) {

							// if a duplicate word occurs just add the count to
							// the existing word count in final word list
							finalWordList.put(key, finalWordList.get(key)
									+ threadWordList.get(key));

							it.remove();
						}
					}

					finalWordList.putAll(threadWordList);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.err.println("Thread Interrupted");
				}
			}

			System.out
					.println("All threads have completed their work. Here's the summary of word list :\n");

			for (Entry<String, Integer> entry : finalWordList.entrySet()) {
				System.out.println("\"" + entry.getKey() + "\" : "
						+ entry.getValue() + " times");
			}

			br.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.err
					.println("I/O exception occured. See log for mor details");
			e1.printStackTrace();
		}
	}

	static class MyThread extends Thread {

		private int lineNumber = 0;
		private String line = null;
		private HashMap<String, Integer> countedWords = new HashMap<String, Integer>();

		private MyThread(int lineNumber, String line) {
			// TODO Auto-generated constructor stub
			this.lineNumber = lineNumber;
			this.line = line;
		}

		public void run() {

			ArrayList<String> duplicateWords = new ArrayList<String>();

			String trimmedLine = line.toLowerCase().trim();

			if (!trimmedLine.isEmpty()) {

				// Replace the common separators to distinguish the words more
				// clearly
				String[] words = trimmedLine.replaceAll("[,\\s?-]+\\\\.", " ")
						.split("[,\\s?-\\\\.]+");

				for (String word : words) {

					if (duplicateWords.contains(word)) {
						// if any duplicate word is there don't add it to the
						// current line's word list, instead just increment
						// counter by 1

						if (countedWords.containsKey(word)) {
							countedWords.put(word, countedWords.get(word) + 1);
						}

					} else {
						countedWords.put(word, 1);

						// if new word is there add it to counted words in the
						// current line's
						// word list
						duplicateWords.add(word);
					}
				}

			}
		}

		HashMap<String, Integer> getWordList() {
			return countedWords;

		}

		void printIntermediateResult() {
			
			System.out.println("Intermediate result of thread "+(lineNumber+1)+" :");

			for (Entry<String, Integer> entry : countedWords.entrySet()) {

				System.out.println("\"" + entry.getKey() + "\" : "
						+ entry.getValue());
				
			}
			
			System.out.println();
		
		}

	}

}
