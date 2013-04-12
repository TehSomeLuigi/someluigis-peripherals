/*
 * TransportCursor.java February 2007
 *
 * Copyright (C) 2007, Niall Gallagher <niallg@users.sf.net>
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

package org.simpleframework.transport;

import java.io.IOException;

/**
 * The <code>TransportCursor</code> object represents a cursor that can read and
 * buffer data from an underlying transport. If the number of bytes read from
 * the cursor is more than required for the HTTP request then those bytes can be
 * pushed back in to the cursor using the <code>reset</code> method. This will
 * only allow the last read to be reset within the cursor safely.
 * 
 * @author Niall Gallagher
 * 
 * @see org.simpleframework.transport.Transport
 */
public class TransportCursor implements Cursor {

    /**
     * This is the source for the bytes read by this cursor object.
     */
    private Source source;

    /**
     * This is the buffer used to collect the bytes pushed back.
     */
    private byte[] buffer;

    /**
     * This is the number of bytes that have been pushed back.
     */
    private int count;

    /**
     * This is the mark from the last read from this cursor object.
     */
    private int mark;

    /**
     * This is the position to read data from the internal buffer.
     */
    private int pos;

    /**
     * This is the maximum number of bytes that can be pushed back.
     */
    private int limit;

    /**
     * Constructor for the <code>TransportCursor</code> object. This requires a
     * transport to read the bytes from. By default this will create a buffer of
     * of the specified size to read the input in to which enabled bytes to be
     * buffered internally.
     * 
     * @param transport
     *            this is the underlying transport to use
     */
    public TransportCursor(Transport transport) {
        this(transport, 2048);
    }

    /**
     * Constructor for the <code>TransportCursor</code> object. This requires a
     * transport to read the bytes from. By default this will create a buffer of
     * of the specified size to read the input in to which enabled bytes to be
     * buffered internally.
     * 
     * @param transport
     *            this is the underlying transport to use
     * @param size
     *            this is the size of the internal buffer to use
     */
    public TransportCursor(Transport transport, int size) {
        this.source = new TransportSource(transport, size);
        this.buffer = new byte[0];
        this.limit = size;
    }

    /**
     * Determines whether the cursor is still open. The cursor is considered
     * open if there are still bytes to read. If there is still bytes buffered
     * and the underlying transport is closed then the cursor is still
     * considered open.
     * 
     * @return true if there is nothing more to be read from this
     */
    @Override
    public boolean isOpen() throws IOException {
        return this.source.isOpen();
    }

    /**
     * Determines whether the cursor is ready for reading. When the cursor is
     * ready then it guarantees that some amount of bytes can be read from the
     * underlying stream without blocking.
     * 
     * @return true if some data can be read without blocking
     */
    @Override
    public boolean isReady() throws IOException {
        return this.ready() > 0;
    }

    /**
     * Provides the number of bytes that can be read from the stream without
     * blocking. This is typically the number of buffered or available bytes
     * within the stream. When this reaches zero then the cursor may perform a
     * blocking read.
     * 
     * @return the number of bytes that can be read without blocking
     */
    @Override
    public int ready() throws IOException {
        if (this.count > 0) return this.count;
        return this.source.ready();
    }

    /**
     * Reads a block of bytes from the underlying stream. This will read up to
     * the requested number of bytes from the underlying stream. If there are no
     * ready bytes on the stream this can return zero, representing the fact
     * that nothing was read.
     * 
     * @param data
     *            this is the array to read the bytes in to
     * 
     * @return this returns the number of bytes read from the stream
     */
    @Override
    public int read(byte[] data) throws IOException {
        return this.read(data, 0, data.length);
    }

    /**
     * Reads a block of bytes from the underlying stream. This will read up to
     * the requested number of bytes from the underlying stream. If there are no
     * ready bytes on the stream this can return zero, representing the fact
     * that nothing was read.
     * 
     * @param data
     *            this is the array to read the bytes in to
     * @param off
     *            this is the offset to begin writing the bytes to
     * @param len
     *            this is the number of bytes that are requested
     * 
     * @return this returns the number of bytes read from the stream
     */
    @Override
    public int read(byte[] data, int off, int len) throws IOException {
        if (this.count <= 0) {
            this.mark = this.pos;
            return this.source.read(data, off, len);
        }
        int size = Math.min(this.count, len);

        if (size > 0) {
            System.arraycopy(this.buffer, this.pos, data, off, size);
            this.mark = this.pos;
            this.pos += size;
            this.count -= size;
        }
        return size;
    }

    /**
     * Pushes the provided data on to the cursor. Data pushed on to the cursor
     * will be the next data read from the cursor. This complements the
     * <code>reset</code> method which will reset the cursors position on a
     * stream. Allowing data to be pushed on to the cursor allows more
     * flexibility.
     * 
     * @param data
     *            this is the data to be pushed on to the cursor
     */
    @Override
    public void push(byte[] data) throws IOException {
        this.push(data, 0, data.length);
    }

    /**
     * Pushes the provided data on to the cursor. Data pushed on to the cursor
     * will be the next data read from the cursor. This complements the
     * <code>reset</code> method which will reset the cursors position on a
     * stream. Allowing data to be pushed on to the cursor allows more
     * flexibility.
     * 
     * @param data
     *            this is the data to be pushed on to the cursor
     * @param off
     *            this is the offset to begin reading the bytes
     * @param len
     *            this is the number of bytes that are to be used
     */
    @Override
    public void push(byte[] data, int off, int len) throws IOException {
        int size = this.buffer.length;

        if (size < (len + this.count)) {
            this.expand(len + this.count);
        }
        int start = this.pos - len;

        if (len > 0) {
            System.arraycopy(data, off, this.buffer, start, len);
            this.mark = start;
            this.pos = start;
            this.count += len;
        }
    }

    /**
     * This is used to ensure that there is enough space in the buffer to allow
     * for more bytes to be added. If the buffer is already larger than the
     * required capacity the this will do nothing.
     * 
     * @param capacity
     *            the minimum size needed for the buffer
     */
    private void expand(int capacity) throws IOException {
        if (capacity > this.limit)
            throw new TransportException("Capacity limit exceeded");
        byte[] temp = new byte[capacity];
        int start = capacity - this.count;
        int shift = this.pos - this.mark;
        if (this.count > 0) {
            System.arraycopy(this.buffer, this.pos, temp, start, this.count);
        }
        this.pos = capacity - this.count;
        this.mark = this.pos - shift;
        this.buffer = temp;
    }

    /**
     * Moves the cursor backward within the stream. This ensures that any bytes
     * read from the last read can be pushed back in to the stream so that they
     * can be read again. This will throw an exception if the reset can not be
     * performed.
     * 
     * @param size
     *            this is the number of bytes to reset back
     * 
     * @return this is the number of bytes that have been reset
     */
    @Override
    public int reset(int size) throws IOException {
        if (this.mark == this.pos) return this.source.reset(size);
        if ((this.pos - size) < this.mark) {
            size = this.pos - this.mark;
        }
        if (size > 0) {
            this.count += size;
            this.pos -= size;
        }
        return size;
    }
}
