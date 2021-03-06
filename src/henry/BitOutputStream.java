package henry;

import java.io.IOException;
import java.io.OutputStream;



public final class BitOutputStream {
	
	// Underlying byte stream to write to.
	private OutputStream output;
	
	// The accumulated bits for the current byte. Always in the range 0x00 to 0xFF.
	private int currentByte;
	
	// The number of accumulated bits in the current byte. Always between 0 and 7 (inclusive).
	private int numBitsInCurrentByte;
	
	
	
	// Creates a bit output stream based on the given byte output stream.
	public BitOutputStream(OutputStream out) {
		if (out == null)
			throw new NullPointerException("Argument is null");
		output = out;
		currentByte = 0;
		numBitsInCurrentByte = 0;
	}
	
    public void write(char x) throws IOException {
        if (x < 0 || x >= 256) throw new IllegalArgumentException("Illegal 8-bit char = " + x);
        // write one bit at a time
        for (int i = 0; i < 8; i++) {
            int bit = (x >>> (8 - i - 1)) & 1;
            write(bit);
        }
    }

    public void writeInt(int x) throws IOException {
        for (int i = 0; i < 32; i++) {
            int bit = ((x >>> (32 - i - 1)) & 1) ;
            write(bit);
        }

    }
	// Writes a bit to the stream. The specified bit must be 0 or 1.
	public void write(int b) throws IOException {
		if (!(b == 0 || b == 1))
			throw new IllegalArgumentException("Argument must be 0 or 1");
		currentByte = currentByte << 1 | b;
		numBitsInCurrentByte++;
		if (numBitsInCurrentByte == 8) {
			output.write(currentByte);
			numBitsInCurrentByte = 0;
		}
	}
	
	
	// Closes this stream and the underlying OutputStream. If called when this bit stream is not at a byte boundary,
	// then the minimum number of "0" bits (between 0 and 7 of them) are written as padding to reach the next byte boundary.
	public void close() throws IOException {
		while (numBitsInCurrentByte != 0)
			write(0);
		output.close();
	}
	
}
