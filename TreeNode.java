/*
Name: Gabriel Galipot
Link TreeNode objects to make the Huffman tree in the TextFileHuffmanTree class
TreeNode.java
 */

public class TreeNode implements Comparable<TreeNode> {


    // Field variables with references to two branching nodes
    char character;
    int frequency;
    TreeNode left;
    TreeNode right;


    // Constructor Method for if there is a character to store in an object TreeNode
    public TreeNode(char character, int frequency){

        this.character = character;
        this.frequency = frequency;
        left = null;
        right = null;

    }


    // Constructor Method for a TreeNode object that branches to two other TreeNode objects that have a character frequency, this tree node stores the sum of the two branching tree nodes and does not store a character
    public TreeNode(TreeNode left, TreeNode right){

        // '\u0000' is the default null character for primitive char data types in Java, the character should not be referenced if a node was created with this constructor
        character = '\u0000';
        frequency = left.frequency + right.frequency;
        this.left = left;
        this.right = right;

    }


    // Constructor Method for a TreeNode object that branches to two other TreeNode objects that have a character frequency,
    // this tree node stores the sum of the two branching tree nodes and does not store a character
    public TreeNode(char character, int frequency, TreeNode left, TreeNode right) {
        this.character = character;
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }


    // For custom Comparable in TextFileHuffmanTree class so its priority queue can compare frequency values in tree nodes
    @Override
    public int compareTo(TreeNode otherNode){
        return Integer.compare(this.frequency, otherNode.frequency);
    }


}
