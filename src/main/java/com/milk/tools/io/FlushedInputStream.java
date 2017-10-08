package com.milk.tools.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wiki on 15/11/21.
 */
public class FlushedInputStream extends FilterInputStream {
    /**
     * Constructs a new {@code FilterInputStream} with the specified input
     * stream as source.
     * <p>
     * <p><strong>Warning:</strong> passing a null source creates an invalid
     * {@code FilterInputStream}, that fails on every method that is not
     * overridden. Subclasses should check for null in their constructors.
     *
     * @param in the input stream to filter reads on.
     */
    protected FlushedInputStream(InputStream in) {
        super(in);
    }

    @Override
    public long skip(long byteCount) throws IOException {
        long totalBytesSkipped = 0L;
        while (totalBytesSkipped < byteCount) {
            long bytesSkipped = in.skip(byteCount - totalBytesSkipped);
            if (bytesSkipped == 0L) {
                int count = read();
                if (count < 0) {
                    break;  // we reached EOF
                } else {
                    bytesSkipped = 1; // we read one byte
                }
            }
            totalBytesSkipped += bytesSkipped;
        }
        return totalBytesSkipped;
    }
}
