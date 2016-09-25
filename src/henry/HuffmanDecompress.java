package henry;

import henry.HuffmanCompress.Node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HuffmanDecompress {
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File inputFile = new File(args[0]);
		File outputFile = new File(args[1]);
		
		BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
		OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
			decompress(in,out);
			 
			out.close();
			in.close();

		
	}
    //for decompress
    public static void decompress(BitInputStream in, OutputStream out ) throws IOException {
        // number of bytes to decode
        int length = in.readInt();
        
        // read in Huffman trie from input stream
        //Node root = tree; 
		Node root = readTree(in);



        // decode using the Huffman tree that is used for encode
        for (int i = 0; i < length; i++) {
        	 	        
            Node x = root;
            while (!x.isLeaf()) {
    	        int bit = in.read();
                if (bit==1) x = x.right;
                else if (bit==0)  x = x.left;
                else break;
            }
            out.write(x.ch);
        }

    }

    private static Node readTree(BitInputStream in) throws IOException {
       int isLeaf = in.read();
        if (isLeaf==1) {
            return new Node(in.readChar(), -1, null, null);
        }
        else {
            return new Node('\0', -1, readTree(in), readTree(in));
        }
    }
}
