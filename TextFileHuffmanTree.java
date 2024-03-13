/*
Name: Gabriel Galipot
Use a Huffman tree to interpret a text file and recreate its content
TextFileHuffmanTree.java
Coding Steps:
1: File reader
2: Character Frequency Table using HashMap implementation
3: Priority Queue for Frequency Table
4: Huffman Tree, assign character frequencies to binary tree nodes
 */

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class TextFileHuffmanTree {


    public static void main(String args[]) throws FileNotFoundException {

        Scanner input = new Scanner(System.in);
        boolean menu = true;

        // User interface loop
        while(menu){

            System.out.println("Enter in the file path of the text file that you want to compress or enter the digit 0 to quit the program:");
            System.out.println("On Windows, the entered file path should be in the format of C:\\Users\\Gabriel\\Desktop\\Project 4\\test.txt");
            System.out.println("You can copy and paste the file path from the file properties window for the text file");


            String filePath = input.nextLine();
            System.out.println();

            // This code will run until it runs into a FileNotFoundException error
            try{

                // Checks if the user wants to end the program
                if(filePath.equals("0")){
                    menu = false;
                }

                if(menu){

                    // File reader
                    char[] contentChars = convertFile(filePath);

                    // Checks if there is no file content, such that the character array is empty
                    if(contentChars.length == 0){

                        System.out.println("The entered file has no content because it is empty");
                        System.out.println();

                    } else{

                        // Prints out the character array's elements from the entered file's content as a string
                        System.out.println("Entered text file's content: ");
                        for(char x: contentChars){
                            System.out.print(x);
                        }

                        System.out.println();

                        // Map frequencies of array's characters
                        Map<Character, Integer> charMap = mapFrequencies(contentChars);

                        // Creates priority queue of binary tree nodes in order to create the Huffman Tree
                        PriorityQueue<TreeNode> encodeQueue = queueNodes(charMap);

                        // Creates priority queue of binary tree nodes in order to decode an already created Huffman code for the text file's content, used in the decode() method
                        PriorityQueue<TreeNode> decodeQueue = queueNodes(charMap);

                        // Adds binary tree nodes filled with the HashMap values to the priority queues
                        for(Map.Entry<Character, Integer> valuePair: charMap.entrySet()){ //Map.Entry<> represents a key-value pair, and Map.entryset() represents a set of those pairs
                            encodeQueue.add(new TreeNode(valuePair.getKey(), valuePair.getValue()));
                        }

                        for(Map.Entry<Character, Integer> valuePair: charMap.entrySet()){
                            decodeQueue.add(new TreeNode(valuePair.getKey(), valuePair.getValue()));
                        }

                        // Map of Huffman tree codes for the file content's characters
                        Map<Character, String> codeMap = generateHuffmanCodes(encode(encodeQueue));

                        // Print Huffman tree code of file's content
                        System.out.println();

                        String encodedFileContent = "";

                        System.out.println("Huffman code of the file's content: ");
                        for(char c: contentChars){
                            encodedFileContent += codeMap.get(c);
                        }
                        System.out.println(encodedFileContent);
                        System.out.println();

                        // Print Huffman tree code of file's content
                        System.out.println("Huffman tree codes for each individual character:");

                        for (Map.Entry<Character, String> entry: codeMap.entrySet()) {
                            System.out.println(entry.getKey() + ": " + entry.getValue());
                        }

                        System.out.println();

                        // Prints the original file's content but decoded from its compressed Huffman binary code
                        System.out.println("The file's content but decoded from its compressed Huffman binary code: ");
                        System.out.println(decode(encode(decodeQueue), encodedFileContent));

                        System.out.println();

                    }

                }

            // Runs this code in case the file was not found with the entered file path
            } catch(FileNotFoundException e){

                System.out.println("No file was found with the entered file path");
                System.out.println();

            }

        }

        // Prints when the user chooses to end the program
        System.out.println();
        System.out.println("Ending program...");

    }


    // Takes text file path and returns its characters in an array
    public static char[] convertFile(String filePath) throws FileNotFoundException, NoSuchElementException {

        String fileContent = "";
        char[] contentChars;

        // This code runs until one of the two exceptions thrown are encountered
        // NoSuchElementException is caught and handled within this method while FileNOtFoundException is caught and handled in the main method when this method gets called in it
        try{

            File file = new File(filePath);
            Scanner reader = new Scanner(file);

            // Transfers the given file's text content to a string
            fileContent = reader.nextLine();

            while (reader.hasNextLine()) {
                fileContent += reader.nextLine();
            }

            // Transfers the string with the file's text content to a char character array
            contentChars = fileContent.toCharArray();

            return contentChars;

        // Runs this code in case there is no file content found such that the file is empty
        } catch(NoSuchElementException e){

            contentChars = new char[0];
            return contentChars;

        }


    }


    // Returns a map in which the given characters of an array are mapped to their frequencies within that array
    public static Map<Character, Integer> mapFrequencies(char[] characters){

        Map<Character, Integer> charToFrequencyMap = new HashMap<>();

        // Calculates the frequencies of each character and assigns their characters as keys to them
        for(char c: characters){
            charToFrequencyMap.put(c, charToFrequencyMap.getOrDefault(c, 0) + 1);
        }

        return charToFrequencyMap;

    }


    // Creates priority queue of Huffman tree nodes given the character HashMap
    public static PriorityQueue<TreeNode> queueNodes(Map<Character, Integer> charMap){

        // Custom Comparator so priority queue can sort through nodes' frequency values
        Comparator<TreeNode> nodeComparison = new Comparator<TreeNode>(){

            @Override
            public int compare(TreeNode first, TreeNode second){
                return Integer.compare(first.frequency,(second.frequency)); // Compares the frequency values of both first and second nodes
            }

        };

        PriorityQueue<TreeNode> queue = new PriorityQueue<>(nodeComparison);
        return queue;

    }


    // Forms Huffman tree with nodes that take two nodes from the priority queue and combine their frequencies
    // The text file's content is returned as a String of binary code from the Huffman tree
    public static TreeNode encode(PriorityQueue<TreeNode> charQueue){ // charQueue is the queue from queueNodes() that has the nodes with characters in them

        // Custom Comparator so priority queue can sort through nodes' frequency values
        Comparator<TreeNode> nodeComparison = new Comparator<TreeNode>(){

            @Override
            public int compare(TreeNode first, TreeNode second){
                return Integer.compare(first.frequency,(second.frequency)); // Compares the frequency values of both first and second nodes
            }

        };

        // New priority queue but the nodes have the left and right branches of nodes with characters and their frequencies are the sum of their two nodes'
        PriorityQueue<TreeNode> sumQueue = new PriorityQueue<>(nodeComparison);

        // Adds nodes with node branches to sumQueue
        while (charQueue.size() > 1) {
            sumQueue.add(new TreeNode(charQueue.poll(), charQueue.poll()));
        }

        // Highest frequency node
        if (charQueue.size() == 1) {
            TreeNode left = charQueue.poll();
            sumQueue.add(new TreeNode(left.character, left.frequency, left, null));
        }

        // The root of the Huffman tree
        TreeNode root = connectNodes(sumQueue);
        return root;

    }


    // Connects nodes to make binary tree for the encode() method
    public static TreeNode connectNodes(PriorityQueue<TreeNode> sumQueue) {

        // Keeps combining the nodes in the sumQueue priority queue into new nodes that branches to them with a frequency that is the sum of its children nodes' frequencies before adding it back into the priority queue
        // This while loop continues to do the above action until there is only one node left in the priority queue, which would be the root node of the Huffman Tree
        while (sumQueue.size() > 1) {
            sumQueue.add(new TreeNode(sumQueue.poll(), sumQueue.poll()));
        }

        // This last node is the root node of the Huffman tree, as it branches off to all other nodes in the tree, which are no longer in the priority queue at this point
        return sumQueue.poll();

    }


    // Returns map of key-value pairs of node characters and their respective Huffman tree Codes
    public static Map<Character, String> generateHuffmanCodes(TreeNode root) {

        Map<Character, String> huffmanCodes = new HashMap<>();

        if (root != null) {

            // Calls binaryCodeGenerator() method to populate the huffmanCodes map with key-value pairs of characters and their Huffman tree Codes
            binaryCodeGenerator(root, huffmanCodes, "");

        }

        return huffmanCodes;

    }


    // Recursive method that generates binary code for each node's character in the Huffman tree for the generateHuffManCodes() method
    public static void binaryCodeGenerator(TreeNode node, Map<Character, String> huffmanCodes, String huffmanCode) {

        if (node != null) {

            // Adds key-value pair of a character and its Huffman tree code to the huffmanCodes map
            if (node.character != '\u0000') {
                huffmanCodes.put(node.character, huffmanCode);
            }

            // Recursion for going through Huffman Tree and adding binary bits to create the code string to find a specific character
            binaryCodeGenerator(node.left, huffmanCodes, huffmanCode + "0");
            binaryCodeGenerator(node.right, huffmanCodes, huffmanCode + "1");

        }

    }


    // Takes the binary code of the file's content and converts its sequences back into its original text format by tracing the nodes in the Huffman tree for each character's binary number sequence
    public static String decode(TreeNode root, String encodedText) {

        String decodedFileContent = "";
        char[] textArray = encodedText.toCharArray();
        TreeNode curr = root; // Is the iteration node to trace through the Huffman tree

        // Huffman tree traversal
        for (char c: textArray) {

            if (c == '0'){
                curr = curr.left;
            }

            if (c == '1'){
                curr = curr.right;
            }

            if (curr.character != '\u0000') {

                // Adds node's character to the decodedFileContent string before repeating the process again for each binary character in the argument's string
                decodedFileContent += curr.character;
                curr = root;

            }

        }

        return decodedFileContent;

    }


}