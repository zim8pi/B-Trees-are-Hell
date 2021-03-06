import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;

/**
 * Parses through a given file
 * Converts all ATCGs between 'ORIGIN' and '//' to binary
 * @author Karan Davis, Ally Oliphant, Cybil Lesbyn
 */
public class ParsingFile {
	
	//stuff to read the file
	private BufferedReader buffer;
	private String line;  //current line in the file being used
	
	//stuff for the sequences
	private String[] character;
	private StringBuilder subSequence;
	private Stack<Long> allSequences = new Stack<Long>();
	private int totalSequences = 0;  //will be the max size of the array DNA
	private NodeObject[] DNA;  //all unique sequences from file with corresponding frequencies
	
	public ParsingFile()
	{
		//default constructor
	}
	
	
	/**
	 * Read through file and create an array of NodeObjects with the unique DNA sequences
	 * @param fileName that contains the DNA sequences
	 * @param sequenceLength
	 * @return an array of NodeObjects in no particular order
	 */
	public NodeObject[] parseDNAFile(String fileName, int sequenceLength) {
		try {
			subSequence = new StringBuilder(2*sequenceLength);
			buffer = new BufferedReader(new FileReader(fileName));
				
			//read the file until the start of the DNA sequences
			while ((line = buffer.readLine()) != null)
			{
				if (line.contains("ORIGIN"))
				{
					break;
				}
			}
			
			//read the file for the DNA sequences until '//' is reached
			while ((line = buffer.readLine()) != null)
			{
				//end of DNA sequences has been reached
				if (line.contains("//"))
				{
					break;
				}
				
				//break the line up into individual characters
				character = line.split("");
				
				int i = 0;
				while (i < character.length && character[i] != null) 
				{
					//if the character is relevant to what we are looking for 
					if (character[i].equals("A") || character[i].equals("a") || 
					    character[i].equals("T") || character[i].equals("t") || 
					    character[i].equals("C") || character[i].equals("c") || 
					    character[i].equals("G") || character[i].equals("g") || 
					    character[i].equals("N") || character[i].equals("n"))
					{
						//remove first base if subSequence is at max length
						if (subSequence.length() == 2*sequenceLength)
						{
							subSequence.delete(0, 2);
						}
						
						//add base to the end of subSequence
						switch (character[i])
						{
							case "A":
							case "a":
								subSequence.append("00");
								break;
							case "T":
							case "t":
								subSequence.append("11");
								break;
							case "C":
							case "c":
								subSequence.append("01");
								break;
							case "G":
							case "g":
								subSequence.append("10");
								break;
							case "N":
							case "n":
								//reset the subSequence
								subSequence = new StringBuilder(2*sequenceLength);
								break;
						}
						
						//add sequence to queue if it has hit max length
						if (subSequence.length() == sequenceLength)
						{
							allSequences.push(Long.parseLong(subSequence.toString()));
							totalSequences++;
						}
					}
					i++;
				}						
			}			
			buffer.close();
		} 
		catch (IOException e) 
		{
			System.out.println(e);
		}	
		

		//add unique sequences to DNA and increment their frequencies as needed
		DNA = new NodeObject[totalSequences];
		Long currentSequence;
		int DNASize = 0;  //so I only have to search through indexes with information in them
		boolean needsToBeAdded;
		
		//while there are still sequences to be added
		while (!allSequences.empty())
		{
			currentSequence = allSequences.pop();
			needsToBeAdded = true;
			
			//check if currentSequence is already in DNA
			int i = 0;
			while (needsToBeAdded && i < DNASize)
			{
				//currentSequence already in DNA
				if (currentSequence.compareTo(DNA[i].getKey()) == 0)
				{
					//increment frequency
					DNA[i].setFrequency(DNA[i].getFrequency() + 1);
					DNASize++;
					needsToBeAdded = false;
					break;
				}
			}
			
			//currentSequence is not yet in DNA
			if (needsToBeAdded)
			{
				DNA[DNASize] = new NodeObject(currentSequence, 1);
				DNASize++;
			}
		}
		
		//remove the unused indexes from DNA
		DNA = Arrays.copyOf(DNA, DNASize);
				
		return DNA;
	}
}