/*
 * ArrayBuffer.java February 2001
 *
 * Copyright (C) 2001, Niall Gallagher <niallg@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package org.simpleframework.util.buffer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * The <code>ArrayBuffer</code> is intended to be a general purpose byte buffer
 * that stores bytes in an single internal byte array. The intended use of this
 * buffer is to provide a simple buffer object to read and write bytes with. In
 * particular this provides a high performance buffer that can be used to read
 * and write bytes fast.
 * <p>
 * This provides several convenience methods which make the use of the buffer
 * easy and useful. This buffer allows an initial capacity to be specified
 * however if there is a need for extra space to be added to buffer then the
 * <code>append</code> methods will expand the capacity of the buffer as needed.
 * 
 * @author Niall Gallagher
 * 
 * @see org.simpleframework.util.buffer.ArrayAllocator
 */
public class ArrayBuffer implements Buffer {

    /**
     * This is the internal array used to store the buffered bytes.
     */
    private byte[] buffer;

    /**
     * This is used to determine whether this buffer has been closed.
     */
    private boolean closed;

    /**
     * This is the count of the number of bytes buffered.
     */
    private int count;

    /**
     * This is the maximum allowable buffer capacity for this.
     */
    private int limit;

    /**
     * Constructor for the <code>ArrayBuffer</code> object. The initial capacity
     * of the default buffer object is set to 16, the capacity will be expanded
     * when the append methods are used and there is not enough space to
     * accommodate the extra bytes.
     */
    public ArrayBuffer() {
        this(16);
    }

    /**
     * Constructor for the <code>ArrayBuffer</code> object. The initial capacity
     * of the buffer object is set to given size, the capacity will be expanded
     * when the append methods are used and there is not enough space to
     * accommodate the extra bytes.
     * 
     * @param size
     *            the initial capacity of this buffer instance
     */
    public ArrayBuffer(int size) {
        this(size, size);
    }

    /**
     * Constructor for the <code>ArrayBuffer</code> object. The initial capacity
     * of the buffer object is set to given size, the capacity will be expanded
     * when the append methods are used and there is not enough space to
     * accommodate the extra bytes.
     * 
     * @param size
     *            the initial capacity of this buffer instance
     * @param limit
     *            this is the maximum allowable buffer capacity
     */
    public ArrayBuffer(int size, int limit) {
        this.buffer = new byte[size];
        this.limit = limit;
    }

    /**
     * This method is used so that the buffer can be represented as a stream of
     * bytes. This provides a quick means to access the data that has been
     * written to the buffer. It wraps the buffer within an input stream so that
     * it can be read directly.
     * 
     * @return a stream that can be used to read the buffered bytes
     */
    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.buffer, 0, this.count);
    }

    /**
     * This method is used to allocate a segment of this buffer as a separate
     * buffer object. This allows the buffer to be sliced in to several smaller
     * independent buffers, while still allowing the parent buffer to manage a
     * single buffer. This is useful if the parent is split in to logically
     * smaller segments.
     * 
     * @return this returns a buffer which is a segment of this buffer
     */
    @Override
    public Buffer allocate() throws IOException {
        return new Segment(this, this.count);
    }

    /**
     * This method is used to acquire the buffered bytes as a string. This is
     * useful if the contents need to be manipulated as a string or transferred
     * into another encoding. If the UTF-8 content encoding is not supported the
     * platform default is used, however this is unlikely as UTF-8 should be
     * supported.
     * 
     * @return this returns a UTF-8 encoding of the buffer contents
     */
    @Override
    public String encode() throws IOException {
        return this.encode("UTF-8");
    }

    /**
     * This method is used to acquire the buffered bytes as a string. This is
     * useful if the contents need to be manipulated as a string or transferred
     * into another encoding. This will convert the bytes using the specified
     * character encoding format.
     * 
     * @return this returns the encoding of the buffer contents
     */
    @Override
    public String encode(String charset) throws IOException {
        return new String(this.buffer, 0, this.count, charset);
    }

    /**
     * This method is used to append bytes to the end of the buffer. This will
     * expand the capacity of the buffer if there is not enough space to
     * accommodate the extra bytes.
     * 
     * @param array
     *            this is the byte array to append to this buffer
     * 
     * @return this returns this buffer for another operation
     */
    @Override
    public Buffer append(byte[] array) throws IOException {
        return this.append(array, 0, array.length);
    }

    /**
     * This method is used to append bytes to the end of the buffer. This will
     * expand the capacity of the buffer if there is not enough space to
     * accommodate the extra bytes.
     * 
     * @param array
     *            this is the byte array to append to this buffer
     * @param off
     *            this is the offset to begin reading the bytes from
     * @param size
     *            the number of bytes to be read from the array
     * 
     * @return this returns this buffer for another operation
     */
    @Override
    public Buffer append(byte[] array, int off, int size) throws IOException {
        if (this.closed) throw new BufferException("Buffer is closed");
        if ((size + this.count) > this.buffer.length) {
            this.expand(this.count + size);
        }
        if (size > 0) {
            System.arraycopy(array, off, this.buffer, this.count, size);
            this.count += size;
        }
        return this;
    }

    /**
     * This is used to ensure that there is enough space in the buffer to allow
     * for more bytes to be added. If the buffer is already larger than the
     * required capacity the this will do nothing.
     * 
     * @param capacity
     *            the minimum size needed for this buffer object
     */
    private void expand(int capacity) throws IOException {
        if (capacity > this.limit)
            throw new BufferException("Capacity limit %s exceeded", this.limit);
        int resize = this.buffer.length * 2;
        int size = Math.max(capacity, resize);
        byte[] temp = new byte[size];

        System.arraycopy(this.buffer, 0, temp, 0, this.count);
        this.buffer = temp;
    }

    /**
     * This will clear all data from the buffer. This simply sets the count to
     * be zero, it will not clear the memory occupied by the instance as the
     * internal buffer will remain. This allows the memory occupied to be reused
     * as many times as is required.
     */
    @Override
    public void clear() throws IOException {
        if (this.closed) throw new BufferException("Buffer is closed");
        this.count = 0;
    }

    /**
     * This method is used to ensure the buffer can be closed. Once the buffer
     * is closed it is an immutable collection of bytes and can not longer be
     * modified. This ensures that it can be passed by value without the risk of
     * modification of the bytes.
     */
    @Override
    public void close() throws IOException {
        this.closed = true;
    }

    /**
     * A <code>Segment</code> represents a segment within a buffer. It is used
     * to allow a buffer to be split in to several logical parts without the
     * need to create several separate buffers. This means that the buffer can
     * be represented in a single memory space, as both a single large buffer
     * and as several individual buffers.
     */
    private class Segment implements Buffer {

        /**
         * This is the parent buffer which is used for collecting data.
         */
        private Buffer parent;

        /**
         * This is used to determine if the buffer has closed or not.
         */
        private boolean closed;

        /**
         * This represents the start of the segment within the buffer.
         */
        private int start;

        /**
         * This represents the number of bytes this segment contains.
         */
        private int length;

        /**
         * Constructor for the <code>Segment</code> object. This is used to
         * create a buffer within a buffer. A segment is a region of bytes
         * within the original buffer. It allows the buffer to be split in to
         * several logical parts of a single buffer.
         * 
         * @param parent
         *            this is the parent buffer used to append to
         * @param start
         *            this is the start within the buffer to read
         */
        public Segment(Buffer parent, int start) {
            this.parent = parent;
            this.start = start;
        }

        /**
         * This method is used so that the buffer can be represented as a stream
         * of bytes. This provides a quick means to access the data that has
         * been written to the buffer. It wraps the buffer within an input
         * stream so that it can be read directly.
         * 
         * @return a stream that can be used to read the buffered bytes
         */
        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(ArrayBuffer.this.buffer,
                    this.start, this.length);
        }

        /**
         * This method is used to allocate a segment of this buffer as a
         * separate buffer object. This allows the buffer to be sliced in to
         * several smaller independent buffers, while still allowing the parent
         * buffer to manage a single buffer. This is useful if the parent is
         * split in to logically smaller segments.
         * 
         * @return this returns a buffer which is a segment of this buffer
         */
        @Override
        public Buffer allocate() throws IOException {
            return new Segment(this, ArrayBuffer.this.count);
        }

        /**
         * This method is used to acquire the buffered bytes as a string. This
         * is useful if the contents need to be manipulated as a string or
         * transferred into another encoding. If the UTF-8 content encoding is
         * not supported the platform default is used, however this is unlikely
         * as UTF-8 should be supported.
         * 
         * @return this returns a UTF-8 encoding of the buffer contents
         */
        @Override
        public String encode() throws IOException {
            return this.encode("UTF-8");
        }

        /**
         * This method is used to acquire the buffered bytes as a string. This
         * is useful if the contents need to be manipulated as a string or
         * transferred into another encoding. This will convert the bytes using
         * the specified character encoding format.
         * 
         * @return this returns the encoding of the buffer contents
         */
        @Override
        public String encode(String charset) throws IOException {
            return new String(ArrayBuffer.this.buffer, this.start, this.length,
                    charset);
        }

        /**
         * This method is used to append bytes to the end of the buffer. This
         * will expand the capacity of the buffer if there is not enough space
         * to accommodate the extra bytes.
         * 
         * @param array
         *            this is the byte array to append to this buffer
         */
        @Override
        public Buffer append(byte[] array) throws IOException {
            return this.append(array, 0, array.length);
        }

        /**
         * This method is used to append bytes to the end of the buffer. This
         * will expand the capacity of the buffer if there is not enough space
         * to accommodate the extra bytes.
         * 
         * @param array
         *            this is the byte array to append to this buffer
         * @param off
         *            this is the offset to begin reading the bytes from
         * @param size
         *            the number of bytes to be read from the array
         */
        @Override
        public Buffer append(byte[] array, int off, int size)
                throws IOException {
            if (this.closed) throw new BufferException("Buffer is closed");
            if (size > 0) {
                this.parent.append(array, off, size);
                this.length += size;
            }
            return this;
        }

        /**
         * This will clear all data from the buffer. This simply sets the count
         * to be zero, it will not clear the memory occupied by the instance as
         * the internal buffer will remain. This allows the memory occupied to
         * be reused as many times as is required.
         */
        @Override
        public void clear() throws IOException {
            this.length = 0;
        }

        /**
         * This method is used to ensure the buffer can be closed. Once the
         * buffer is closed it is an immutable collection of bytes and can not
         * longer be modified. This ensures that it can be passed by value
         * without the risk of modification of the bytes.
         */
        @Override
        public void close() throws IOException {
            this.closed = true;
        }
    }
}
