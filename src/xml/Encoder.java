package xml;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
 This class is a wrapper for the Encode and Decode functions which
 encode and decode a string using huffman's technique.
*/
public class Encoder {
	
	/*
	 This is used to encode a string using huffman's technique
	 @param text the string to decode
	 @return String the encoded string
	*/
	public static String Encode(String text) {
		List<tNode> tree = textToHuffmanTree(text);
		int repetitionCounter = 0;
		for (tNode node: tree) {
			repetitionCounter += node.reps;
		}
		while(tree.get(tree.size()-1).reps < repetitionCounter) {
			// create a list with all nodes without a parent
			List<tNode> parentlessNodes = new ArrayList<tNode>();
			parentlessNodes.addAll(tree);
			parentlessNodes.removeIf(node -> node.parent != null);
			
			Collections.sort(parentlessNodes, Collections.reverseOrder());
			tNode firstNodeToMerge = parentlessNodes.get(0);
			tNode secondNodeToMerge = parentlessNodes.get(1);
			
			// add the new parent node with first and second nodes as children
			int parentID = tree.size();
			int parentLVL = Math.max(firstNodeToMerge.level, secondNodeToMerge.level) + 1;
			String parentSequence = firstNodeToMerge.sequence.concat(secondNodeToMerge.sequence);
			int totalRepetitions = firstNodeToMerge.reps + secondNodeToMerge.reps;
			List<tNode> childrenArray = new ArrayList<tNode>();
			childrenArray.add(firstNodeToMerge);
			childrenArray.add(secondNodeToMerge);
			
			tree.add(new tNode(
				parentID,
				parentLVL,
				null,
				parentSequence,
				totalRepetitions,
				-1,
				childrenArray
			));
			
			// get a reference to the node we just added
			tNode parentNode = tree.get(tree.size()-1);
			firstNodeToMerge.parent = parentNode;
			secondNodeToMerge.parent = parentNode;
		}
		tNode root = tree.get(tree.size()-1);
		markChildrenBinField(root);
		
		Dictionary encodingDictionary = new Dictionary(tree);
		String encodedText = "";
		char[] textArray = text.toCharArray();
		encodedText = String.join("", encodingDictionary.encodeCharArray(textArray));
		
		String[] fileHeaders = createFileHeaders(
			encodingDictionary.binarify(),
			encodedText.length()
		);
		return fileHeaders[0] + convertBinarySequenceToAsciiSequence(encodedText + fileHeaders[1]);
	}
	
	/*
	 This is used to decode a string using huffman's technique.
	 The string must have been encoded using this class's Encode function
	 @param text This is the string to decode
	 @return String This is the decoded string (the original string that was encoded)
	*/
	public static String Decode(String text) {
		
		List<Integer> headers = getFileHeaders(text.substring(0, 6));
		
		String biText = stringToBinary8(text).substring(48);
		
		int dicSize = headers.get(0);
		int dicEntrySize = headers.get(1);
		int additinalDicZeros = headers.get(2);
		int additionalZeroes = headers.get(3);
		
		String dictionaryStringBinary = biText.substring(0, dicSize);
		Dictionary dictionary = new Dictionary(fromBinaryString(dictionaryStringBinary, dicEntrySize));
		
		String encodedText = biText.substring(dicSize + additinalDicZeros, biText.length() - additionalZeroes);
		
		String decodedString = "";
		char[] decodedStringArray = new char[512*1024*1024];
		int index = 0;
		String decodingBuffer = "";
		char decodedCharSoFar;
		for (char c: encodedText.toCharArray()) {
			decodingBuffer += c;
			decodedCharSoFar = dictionary.getReverse(decodingBuffer);
			if (decodedCharSoFar == (char)0) continue;
			decodedStringArray[index++] = decodedCharSoFar;
			decodingBuffer = "";
		}
		decodedString = new String(Arrays.copyOfRange(decodedStringArray, 0, index));
		return decodedString;
	}
	
	/*
	 Encodes the provided text and creates a file to which the compressed
	 text is written
	 @param originalFileLocation the text to compress/encode
	 @param compressedFileLocation the location of the file to create
	 and place the encoded/compressed text
	*/
	public static void Encode(String originalFileLocation, String compressedFileLocation) {
		
		File inputFile = new File(originalFileLocation);
		File outputFile = new File(compressedFileLocation);
		
		FileInputStream fis;
		DataInputStream dis;
		FileOutputStream fos;
		DataOutputStream dos;
		
		try {			
			fis = new FileInputStream(inputFile);
			dis = new DataInputStream(fis);
			String readFileString = "";
			
			char[] chars = new char[dis.available()];
			int index = 0;
			while (dis.available() > 0) {
				chars[index++] = (char)(0xff&dis.readByte());
			}
			readFileString = new String(chars);
			dis.close();
			
			fos = new FileOutputStream(outputFile);
			dos = new DataOutputStream(fos);
			
			String encodedText = Encode(readFileString);
			dos.writeBytes(encodedText);
			
			dos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeBytes(File file, String text) {
		FileOutputStream fos;
		DataOutputStream dos;
		
		try {
			fos = new FileOutputStream(file);
			dos = new DataOutputStream(fos);
			dos.writeBytes(text);
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String readAndDecodeFile(File file) {
		String readFileString;
		String decodedText = "";
		FileInputStream fis;
		DataInputStream dis;
		
		try {
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);
			
			char[] chars = new char[dis.available()];
			int index = 0;
			while (dis.available() > 0) {
				chars[index++] = (char)(0xff&dis.readByte());
			}
			readFileString = new String(chars);
			dis.close();
			decodedText = Decode(readFileString);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return decodedText;
	}
	
	/*
	 Decodes a file which is decoded/compressed using the encoding
	 function in this class
	 @param encodedFileLocation the location of the file to decode
	 @param decodedFileLocation the location where the decompressed file will be placed
	*/
	public static void Decode(String encodedFileLocation, String decodedFileLocation) {
		
		File inputFile = new File(encodedFileLocation);
		File outputFile = new File(decodedFileLocation);
		
		FileInputStream fis;
		DataInputStream dis;
		FileOutputStream fos;
		DataOutputStream dos;
		
		try {
			fis = new FileInputStream(inputFile);
			dis = new DataInputStream(fis);
			
			char[] chars = new char[dis.available()];
			int index = 0;
			while (dis.available() > 0) {
				chars[index++] = (char)(0xff&dis.readByte());
			}
			String readFileString = new String(chars);
			dis.close();
			
			fos = new FileOutputStream(outputFile);
			dos = new DataOutputStream(fos);
			
			String decodedText = Decode(readFileString);
			
			dos.writeBytes(decodedText);
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static String fromBinaryString(String binaryString, int dicEntrySize) {

		int arraySize = binaryString.length() / dicEntrySize;
		String[] dictionaryConstructorArray2 = new String[arraySize];
		int dictionaryStringIndex = 0;
		int dictionaryArrayIndex = 0;
		String entry2;
		while(dictionaryArrayIndex < arraySize) {
			entry2 = binaryString.substring(dictionaryStringIndex, dictionaryStringIndex + dicEntrySize);
			dictionaryStringIndex += dicEntrySize;
			char key = (char)Integer.parseInt(entry2.substring(0, 8), 2);
			int value = Integer.parseInt(entry2.substring(8), 2);
			dictionaryConstructorArray2[dictionaryArrayIndex++] =
				String.format("%c", key).concat(String.format("%s", value));
		}
		String dictionaryConstructorString = String.join(String.format("%c", 184), dictionaryConstructorArray2);
		return dictionaryConstructorString;
	}
	
	static List<Integer> getFileHeaders(String headersAscii) {
		List<Integer> headers = new ArrayList<Integer>();
		String headersBinary = stringToBinary8(headersAscii);
		int dictionarySizeHeaderInt = Integer.parseInt(headersBinary.substring(0, 16), 2);
		int dicEntrySize = Integer.parseInt(headersBinary.substring(16, 21), 2);
		int additionalDictionaryZeroesInt = Integer.parseInt(headersBinary.substring(42, 45), 2);
		int additionalZeroesInt = Integer.parseInt(headersBinary.substring(45, 48), 2);
		headers.add(dictionarySizeHeaderInt);
		headers.add(dicEntrySize);
		headers.add(additionalDictionaryZeroesInt);
		headers.add(additionalZeroesInt);
		return headers;
	}
	
	static String stringToBinary8(String s) {
		String[] outputArray = new String[s.length()];
		int index = 0;
		for (char c: s.toCharArray()) {
			outputArray[index++] = charToBinary8(c);
		}
		String output = String.join("", outputArray);
		return output;
	}
	
	static String charToBinary8(char c) {
		String binary8 = Integer.toBinaryString((int)c);
		int l = binary8.length();
		char[] additionalZeroesArray = new char[8];
		int counter = 0;
		while(l + counter < 8) {
			additionalZeroesArray[counter++] = '0';
		}
		String additionalZeroes = new String(Arrays.copyOfRange(additionalZeroesArray, 0, counter));
		String output = String.join("", new String[] {additionalZeroes, binary8});
		return output;
	}
	
	static String[] createFileHeaders(
		List<String> encodingDictionaryArray,
		int encodedTextLength
	) {
		String encodingDictionaryEnrySize = Integer.toBinaryString(encodingDictionaryArray.get(1).length());
		while(encodingDictionaryEnrySize.length() < 5) {
			encodingDictionaryEnrySize = "0" + encodingDictionaryEnrySize;
		}
		String encodingDictionary = String.join("", encodingDictionaryArray);
		
		String encodingDictionarySize = Integer.toBinaryString(encodingDictionary.length());
		while(encodingDictionarySize.length() < 16) {
			encodingDictionarySize = "0" + encodingDictionarySize;
		}
		String encodedTextSize = Integer.toBinaryString(encodedTextLength);
		while(encodedTextSize.length() < 29) {
			encodedTextSize = "0" + encodedTextSize;
		}
		
		String filler = "";
		while(filler.length() < 21) {
			filler += "1";
		}
		
		String addedBitsAfterEncodingDictionary = "";
		while(((encodingDictionary.length() + addedBitsAfterEncodingDictionary.length()) % 8) != 0) {
			addedBitsAfterEncodingDictionary += "0";
		}
		String numberOfAddedBitsAfterEncodingDictionary = Integer.toBinaryString(addedBitsAfterEncodingDictionary.length());
		while(numberOfAddedBitsAfterEncodingDictionary.length() < 3) {
			numberOfAddedBitsAfterEncodingDictionary = "0" + numberOfAddedBitsAfterEncodingDictionary;
		}
		
		String addedBitsAfterEncodedText = "";
		while(((encodingDictionary.length() + addedBitsAfterEncodingDictionary.length() + encodedTextLength + addedBitsAfterEncodedText.length()) % 8) != 0) {
			addedBitsAfterEncodedText += "0";
		}
		String numberOfAddedBitsAfterEncodedText = Integer.toBinaryString(addedBitsAfterEncodedText.length());
		while(numberOfAddedBitsAfterEncodedText.length() < 3) {
			numberOfAddedBitsAfterEncodedText = "0" + numberOfAddedBitsAfterEncodedText;
		}
		
		String headers =
			encodingDictionarySize +
			encodingDictionaryEnrySize +
			filler +
			numberOfAddedBitsAfterEncodingDictionary +
			numberOfAddedBitsAfterEncodedText +
			encodingDictionary +
			addedBitsAfterEncodingDictionary;
		
		String[] returnArray = new String[] {
			convertBinarySequenceToAsciiSequence(headers),
			addedBitsAfterEncodedText
		};
		
		return returnArray;
	}
	
	static String convertBinarySequenceToAsciiSequence(String binarySequence) {
		char[] asciiSequenceArray = new char[binarySequence.length() / 8];
		char nextChar;
		for (int i=0; i<binarySequence.length(); i+=8) {
			nextChar = (char)Integer.parseInt(binarySequence.substring(i, i+8), 2);
			asciiSequenceArray[i/8] = nextChar;
		}
		String asciiSequence = new String(asciiSequenceArray);
		return asciiSequence;
	}
	
	static void markChildrenBinField(tNode node) {
		tNode child;
		int childrenArraySize = node.children.size();
		for (int i=0; i<childrenArraySize; i++) {
			child = node.children.get(i);
			child.bin = 1 - i;
			markChildrenBinField(child);
		}
	}
	
	static List<tNode> textToHuffmanTree(String text) {
		// Split the string into a character array
		List<Character> charList = new ArrayList<Character>();
		for (char ch: text.toCharArray()) {
			charList.add(ch);
		}
		// create a list of unique characters
		List<Character> uniqueChars = new ArrayList<Character>();
		for (char c: charList) {
			if (uniqueChars.contains(c)) continue;
			uniqueChars.add(c);
		}
		
		// create the immediate Huffman tree
		List<tNode> tree = new ArrayList<tNode>();
		int tId = 0;
		for (char character: uniqueChars) {
			int characterFrequency = Collections.frequency(charList, character);
			tree.add(new tNode(
				tId++,
				0,
				null,
				String.valueOf(character),
				characterFrequency,
				-1,
				new ArrayList<tNode>()
			));
		}
		Collections.sort(tree);
		return tree;
	}
}


class tNode implements Comparable<tNode> {
	public int id;
	public int level;
	public tNode parent;
	public String sequence;
	public int reps;
	public int bin;
	public List<tNode> children;

	public tNode(int id, int level, tNode parent, String sequence, int reps, int bin, List<tNode> children) {
		this.id = id;
		this.level = level;
		this.parent = parent;
		this.sequence = sequence;
		this.reps = reps;
		this.bin = bin;
		this.children = children;
	}

	@Override
	public int compareTo(tNode other) {
		return other.reps - this.reps;
	}
	
	@Override
	public String toString() {
		return String.format("id: %d; level:%d child of %s \"%s\" (%d) bin:%d c:[%s]", id, level, parent == null ? "null" : Integer.toString(parent.id), sequence, reps, bin, children);
	}
}

class Dictionary {
	private String items[] = new String[256];
	private List<Character> reverseItemsValues = new ArrayList<Character>();
	private List<String> reverseItemsKeys = new ArrayList<String>();
	
	public Dictionary(List<tNode> tree) {
		List<tNode> immediateDictionary = new ArrayList<tNode>();
		immediateDictionary.addAll(tree);
		immediateDictionary.removeIf(node -> node.level > 0);
		for (tNode node: immediateDictionary) {
			tNode tempNode = node;
			String charBinaryCode = "";
			while(tempNode.parent != null) {
				charBinaryCode = String.format("%s%s", Integer.toString(tempNode.bin), charBinaryCode);
				tempNode = tempNode.parent;
			}
			this.set(node.sequence.charAt(0), charBinaryCode);
		}
	}

	public Dictionary(String text) {
		List<String> tokenList = Arrays.asList(text.split(String.format("%c", (char)184))); //comma
		for (String token: tokenList) {
			if (token.length() < 1) continue;
			char key = token.charAt(0);
			String value = token.substring(1, token.length());
			value = Integer.toBinaryString(Integer.parseInt(value));
			value = value.substring(1, value.length());
			this.set(key, value);
		}
	}
	
	public void set(char key, String value) {
		if ((int)key < 256) {
			this.items[key] = value;
			// we need to append 1 to the start of the string in order to avoid
			// duplicated reverseKeys i.e. 101 and 0101
			this.reverseItemsKeys.add(value);
			this.reverseItemsValues.add(key);
		}
	}
	
	public String get(char key) {
		if (key >= 0 && key < 256) {
			return this.items[key];
		}
		return null;
	}
	
	public String[] encodeCharArray(char[] input) {
		int l = input.length;
		String[] output = new String[l];
		for (int i=0; i<l; i++) {
			output[i] = items[input[i]];
		}
		return output;
	}
	
	public char getReverse(String value) {
		if (this.reverseItemsKeys.contains(value)) {
			return this.reverseItemsValues.get(
				reverseItemsKeys.indexOf(value)
			);
		}
		return (char)0;
	}
	
	public List<String> binarify() {
		List<String[]> temporaryElementsArray = new ArrayList<String[]>();
		int maxValueLength = 0;
		for (int i=0; i<this.reverseItemsValues.size(); i++) {
			String currentKeyBinary = Encoder.charToBinary8(reverseItemsValues.get(i));
			String currentValueBinary = intStringToBinaryX("1"+this.reverseItemsKeys.get(i));
			int currentValueLength = currentValueBinary.length();
			maxValueLength = Math.max(maxValueLength, currentValueLength);
			temporaryElementsArray.add(
				new String[] {currentKeyBinary, currentValueBinary}
			);
		}
		List<String> outputElementsArray = new ArrayList<String>();
		for (String[] element: temporaryElementsArray) {
			String valueBinary = element[1];
			while(valueBinary.length() < maxValueLength) {
				valueBinary = "0" + valueBinary;
			}
			outputElementsArray.add(element[0] + valueBinary);
		}
		return outputElementsArray;
	}
	
	static String intStringToBinaryX(String s) {
		String binary = Integer.toBinaryString(Integer.parseInt(s, 2));
		return binary;
	}
	
	public String stringify() {
		List<String> outputElementsArray = new ArrayList<String>();
		for (int i=0; i<this.reverseItemsValues.size(); i++) {
			outputElementsArray.add(
				reverseItemsValues.get(i) +
				""+Integer.parseInt("1"+this.reverseItemsKeys.get(i), 2)
			);
		}
		return String.join(String.format("%c", (char)184), outputElementsArray); //comma
	}
	
	@Override
	public String toString() {
		return this.stringify();
	}
	
	public void print() {
		for (String i: this.items) {
			if (i == null) continue;
		}
	}
}