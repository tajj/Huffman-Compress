package henry;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.PriorityQueue;
import java.util.Queue;

public class HuffmanCompressAny {


	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File inputFile = new File(args[0]);
		File outputFile = new File(args[1]);
		
		InputStream in;
		BitOutputStream out;
		
		int fileLength=(int)inputFile.length();
			in = new BufferedInputStream(new FileInputStream(inputFile));
			out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
			
			compress(fileLength,in,out);
			
			 
			out.close();
			in.close();

		
	}
	static Node compress(int fileLength, InputStream in, BitOutputStream out) throws IOException {
		//build frequencies table
		Node root;

         int[] freq = new int[256]; //256 is the alphabet size.
         
         BitInputStream bitin = new BitInputStream(in);
         

		    char [] input = new char [fileLength];
	        //  frequencies are stored in an integer array freg and indexed by each characters binary, e.g. a is 97, b is 98
		    System.out.println("input.length:"+input.length);
	        for (int i = 0; i < input.length; i++)
	        {
	        	input[i]=bitin.readChar();
	        	if (input[i]<256)
	            freq[input[i]]++;
	        	else throw new IllegalStateException("Illegal state"+"Please fixed the non-asii in the input file====>"+input[i]);
	        }
	        //test to make sure frequencies are correct
	        for (int i = 0; i < 256; i++){
	        	if (freq[i]>0)
	        	 System.out.println("character:"+Character.toString ((char) i)+" freq:"+freq[i]);
	        }


		
		//build huffman tree nodes
		 root = buildTree(freq);

	     // write number of bytes in original uncompressed message
	     out.writeInt(fileLength);
 	     System.out.println("bytes:"+fileLength);

		 //write the tree to file for decoder
		 writeTree(root,out);
		 
 	     // build code table
	     String[] codeTable = new String[256];
	     buildCode(codeTable, root, "");

	        // use Huffman code to encode input
	     
	        for (int i = 0; i < input.length; i++) {
	            String code = codeTable[input[i]];
	        	 System.out.println("character:"+Character.toString ((char) input[i])+" code:"+code);
	             for (int j = 0; j < code.length(); j++) {
	            	 
	                 if (code.charAt(j) == '0') {
	                     out.write(0);
	                 }
	                 else if (code.charAt(j) == '1') {
	                     out.write(1);
	                 }
	                 else throw new IllegalStateException("Illegal state");
	             }
	              
	        }
			return root;

		 
		
	}
    // Huffman tree node
    public static class Node implements Comparable<Node> {
        public final char ch;
        public final long freq;
        public final Node left, right;

        Node(char ch, long freq, Node left, Node right) {
            this.ch    = ch;
            this.freq  = freq;
            this.left  = left;
            this.right = right;
        }

        // is the node a leaf node?
        public boolean isLeaf() {
            assert ((left == null) && (right == null)) || ((left != null) && (right != null));
            return (left == null) && (right == null);
        }

        // compare, based on frequency
        public int compareTo(Node that) {
           
            if (this.freq>that.freq)
            	return 1;
            if (this.freq==that.freq)
            	return 0;
            return -1;

        }
    }	
	 private static Node buildTree(int[] freq) {
			Queue<Node> pqueue = new PriorityQueue<Node>();
	        for (char i = 0; i < freq.length; i++)
	            if (freq[i] > 0)
	            	pqueue.add(new Node(i, freq[i], null, null));

	        // special case in case there is only one character with a nonzero frequency
	        if (pqueue.size() == 1) {
	            if (freq['\0'] == 0) pqueue.add(new Node('\0', 0, null, null));
	            else                 pqueue.add(new Node('\1', 0, null, null));
	        }

	        // merge two smallest trees
	        while (pqueue.size() > 1) {
	            Node left  = pqueue.remove();
	            Node right = pqueue.remove();
	            Node parent = new Node('\0', left.freq + right.freq, left, right);
	            pqueue.add(parent);
	        }
	        return pqueue.remove();
	 
	 }
	    private static void writeTree(Node x, BitOutputStream out) throws IOException {
	        if (x.isLeaf()) {
	        	out.write(1);
	        	out.write(x.ch);
	       
	   		 	System.out.println("leaf-character:"+Character.toString (x.ch)+" freq:"+x.freq);
	            return;
	        }
	   		System.out.println("node-character:"+Character.toString (x.ch)+" freq:"+x.freq);
		    out.write(0);
	        writeTree(x.left,out);
	        writeTree(x.right,out);
	    }
	    
	    private static void buildCode(String[] st, Node x, String s) {
	        if (!x.isLeaf()) {
	            buildCode(st, x.left,  s + '0');
	            buildCode(st, x.right, s + '1');
	        }
	        else {
	            st[x.ch] = s;
	        }
	    }
	    

}
