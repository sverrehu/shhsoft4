package no.shhsoft.net;

import no.shhsoft.utils.StringUtils;
import no.shhsoft.utils.UncheckedIoException;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Maintains a byte buffer that may be sent across the network as packets. Contains methods for
 * reading and writing various data types from/to the buffer. The buffer will automatically be
 * expanded when necessary.
 * </p>
 * <p>
 * Please note that an instance of this class should be used <i>either</i> for reading a message,
 * <i>or</i> for writing a message. If you mix the use of put- and get-methods, the result is
 * undefined (unless you use the <code>clear</code> method).
 *
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Message
implements Cloneable {

    /** Number of reserved bytes at the start of the buffer. */
    private static final int NUM_RESERVED_BYTES = 3;

    private static long numBytesWrittenToStreams = 0L;
    private static long numBytesReadFromStreams = 0L;
    private static long numBytesWrittenToDatagrams = 0L;
    private static long numBytesReadFromDatagrams = 0L;

    /**
     * An address, typically set to the source or destination of the message, depending on the usage
     * of this Message.
     */
    private InetAddress addr;
    /**
     * A port, typically set to the source or destination of the message.
     */
    private int port;

    /** Number of bytes to add for each resizing of the buffer. */
    private static final int BUFFER_CHUNK_SIZE = 64;

    /** The message contents buffer. */
    private byte[] buff;

    /** Number of used bytes in the buffer. */
    private int buffLen;

    /** Index of next byte when getting values from the buffer. */
    private int idx;

    /**
     * <p>
     * Makes room for at least the given number of additional bytes. Note that <code>buffLen</code>
     * is not updated.
     * </p>
     * <p>
     * Bytes are allocated in chunks to avoid having to resize the array every time. This method
     * checks if there are enough preallocated entries to match the number of new entries. If there
     * is, this method does nothing. If there is not, this method reallocates the buffer.
     * </p>
     *
     * @param numNew
     *            the number of new bytes we want to make room for.
     */
    private void ensureAvailable(final int numNew) {
        if (buffLen + numNew <= buff.length) {
            return;
        }
        final int newLen = ((buffLen + numNew - 1) / BUFFER_CHUNK_SIZE + 1) * BUFFER_CHUNK_SIZE;
        final byte[] tmp = new byte[newLen];
        if (buff.length > 0) {
            System.arraycopy(buff, 0, tmp, 0, buff.length);
        }
        buff = tmp;
    }

    /**
     * Constructs a new message.
     */
    public Message(final byte type, final short id) {
        buff = new byte[BUFFER_CHUNK_SIZE];
        clear();
        setType(type);
        setId(id);
    }

    /**
     * Constructs a new message.
     */
    public Message(final byte type) {
        this(type, (short) -1);
    }

    /**
     * Constructs a new message.
     */
    public Message() {
        this((byte) -1, (short) -1);
    }

    public static long getNumBytesWrittenToStreams() {
        return numBytesWrittenToStreams;
    }

    public static long getNumBytesReadFromStreams() {
        return numBytesReadFromStreams;
    }

    public static long getNumBytesWrittenToDatagrams() {
        return numBytesWrittenToDatagrams;
    }

    public static long getNumBytesReadFromDatagrams() {
        return numBytesReadFromDatagrams;
    }

    @Override
    public Object clone() {
        final Message m = new Message();
        m.addr = addr;
        m.port = port;
        m.buff = getBuffer();
        m.buffLen = buffLen;
        m.idx = idx;
        return m;
    }

    /**
     * <p>
     * Fetches a copy of the current buffer. Please note that the returned array is a <i>copy</i>.
     * You may change it without affecting the state of the <code>Message</code>.
     * </p>
     * <p>
     * You will want to use this method when sending the message.
     * </p>
     *
     * @return a copy of the byte buffer in this <code>Message</code>.
     */
    public byte[] getBuffer() {
        final byte[] ret = new byte[buffLen];
        System.arraycopy(buff, 0, ret, 0, buffLen);
        return ret;
    }

    /**
     * <p>
     * Sets the contents of the message byte buffer. Discards whatever was in the buffer. The
     * provided byte array is copied into the <code>Message</code>, so you're free to change the
     * array after calling this method, without disturbing the state of the message.
     * </p>
     * <p>
     * You will want to use this method when you have received a message from the network.
     * </p>
     */
    public void setBuffer(final byte[] b, final int n) {
        buff = new byte[BUFFER_CHUNK_SIZE];
        buffLen = 0;
        ensureAvailable(n);
        System.arraycopy(b, 0, buff, 0, n);
        buffLen = n;
        idx = NUM_RESERVED_BYTES;
    }

    /**
     * <p>
     * Sets the contents of the message byte buffer. Discards whatever was in the buffer. The
     * provided byte array is copied into the <code>Message</code>, so you're free to change the
     * array after calling this method, without disturbing the state of the message.
     * </p>
     * <p>
     * You will want to use this method when you have received a message from the network.
     * </p>
     */
    public void setBuffer(final byte[] b) {
        setBuffer(b, b.length);
    }

    /**
     * Resets this <code>Message</code>. In effect this is equivalent to making a new
     * <code>Message</code> using the null constructor, but saves the object creation overhead.
     */
    public void clear() {
        addr = null;
        port = -1;
        setType((byte) -1);
        setId((short) -1);
        buffLen = NUM_RESERVED_BYTES;
        idx = NUM_RESERVED_BYTES;
    }

    public int getSize() {
        return buffLen;
    }

    public int getNumAddedBytes() {
        return buffLen - NUM_RESERVED_BYTES;
    }

    public int getBytesLeftToRead() {
        return buffLen - idx;
    }

    /**
     * Resets the "read head" to the start of the <code>Message</code>.
     */
    public void rewind() {
        idx = NUM_RESERVED_BYTES;
    }

    /**
     * Sets the address associated with this <code>Message</code>. You will probably want to call
     * this method after reading the message from the network. Note that setting the address is not
     * required, unless you want to call the <code>getAddress</code> method later. (The address
     * has no meaning to the <code>Message</code> object. Wheteher you store the source address,
     * the destination address or something else, us up to you.)
     */
    public void setAddress(final InetAddress addr) {
        this.addr = addr;
    }

    /**
     * Fetches the address associated with this <code>Message</code>. Calling this method is only
     * meaningful if <code>setAddress</code> was called earlier.
     *
     * @return the address, or <code>null</code> if no address was set.
     */
    public InetAddress getAddress() {
        return addr;
    }

    /**
     * Sets the port associated with this <code>Message</code>. You will probably want to call
     * this method after reading the message from the network. Note that setting the port is not
     * required, unless you want to call the <code>getPort</code> method later. (The port has no
     * meaning to the <code>Message</code> object. Wheteher you store the source port, the
     * destination port or something else, us up to you.)
     */
    public void setPort(final int port) {
        this.port = port;
    }

    /**
     * Fetches the port associated with this <code>Message</code>. Calling this method is only
     * meaningful if <code>setPort</code> was called earlier, probably when the message was
     * received from the network.
     *
     * @return the port, or <code>-1</code> if no port was set.
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the type of this <code>Message</code>. The type is a user defined <code>byte</code>,
     * that need not be set.
     */
    public void setType(final byte type) {
        buff[0] = type;
    }

    /**
     * Fetches the type of this <code>Message</code>.
     */
    public byte getType() {
        return buff[0];
    }

    /**
     * Sets the identification of this <code>Message</code>. The identification is a user defined
     * <code>short</code>, that need not be set.
     */
    public void setId(final short id) {
        buff[1] = (byte) ((id >> 8) & 0xff);
        buff[2] = (byte) (id & 0xff);
    }

    /**
     * Fetches the identification of this <code>Message</code>.
     */
    public short getId() {
        return (short) ((buff[1] << 8) | (buff[2] & 0xff));
    }

    /**
     * Adds a sequence of bytes to the byte buffer.
     */
    public void putBytes(final byte[] b, final int n) {
        ensureAvailable(n);
        System.arraycopy(b, 0, buff, buffLen, n);
        buffLen += n;
    }

    /**
     * Adds a sequence of bytes to the byte buffer.
     */
    public void putBytes(final byte[] b) {
        putBytes(b, b.length);
    }

    /**
     * Fetches a sequence of bytes from the byte buffer.
     */
    public byte[] getBytes(final int n) {
        final byte[] ret = new byte[n];
        System.arraycopy(buff, idx, ret, 0, n);
        idx += n;
        return ret;
    }

    /**
     * Adds a byte to the byte buffer.
     */
    public void putByte(final byte b) {
        ensureAvailable(1);
        buff[buffLen++] = b;
    }

    public void putByte(final short b) {
        putByte((byte) b);
    }

    public void putByte(final int b) {
        putByte((byte) b);
    }

    /**
     * Fetches a byte from the byte buffer.
     */
    public byte getByte() {
        return buff[idx++];
    }

    /**
     * Adds a boolean to the byte buffer.
     */
    public void putBoolean(final boolean b) {
        ensureAvailable(1);
        buff[buffLen++] = (b ? (byte) 1 : (byte) 0);
    }

    /**
     * Fetches a boolean from the byte buffer.
     */
    public boolean getBoolean() {
        return (buff[idx++] != 0);
    }

    /**
     * Adds a short (16 bit) integer to the byte buffer.
     */
    public void putShort(final short s) {
        ensureAvailable(2);
        buff[buffLen++] = (byte) ((s >> 8) & 0xff);
        buff[buffLen++] = (byte) (s & 0xff);
    }

    public void putShort(final int s) {
        putShort((short) s);
    }

    /**
     * Fetches a short (16 bit) integer from the byte buffer.
     */
    public short getShort() {
        short ret = (short) (buff[idx++] << 8);
        ret |= buff[idx++] & 0xff;
        return ret;
    }

    /**
     * Adds a 32 bit integer to the byte buffer.
     */
    public void putInt(final int i) {
        ensureAvailable(4);
        buff[buffLen++] = (byte) ((i >> 24) & 0xff);
        buff[buffLen++] = (byte) ((i >> 16) & 0xff);
        buff[buffLen++] = (byte) ((i >> 8) & 0xff);
        buff[buffLen++] = (byte) (i & 0xff);
    }

    /**
     * Fetches a 32 bit integer from the byte buffer.
     */
    public int getInt() {
        int ret = (buff[idx++] << 24);
        ret |= ((buff[idx++] & 0xff) << 16);
        ret |= ((buff[idx++] & 0xff) << 8);
        ret |= buff[idx++] & 0xff;
        return ret;
    }

    /**
     * Adds a 64 bit integer to the byte buffer.
     */
    public void putLong(final long l) {
        ensureAvailable(8);
        buff[buffLen++] = (byte) ((l >> 56) & 0xff);
        buff[buffLen++] = (byte) ((l >> 48) & 0xff);
        buff[buffLen++] = (byte) ((l >> 40) & 0xff);
        buff[buffLen++] = (byte) ((l >> 32) & 0xff);
        buff[buffLen++] = (byte) ((l >> 24) & 0xff);
        buff[buffLen++] = (byte) ((l >> 16) & 0xff);
        buff[buffLen++] = (byte) ((l >> 8) & 0xff);
        buff[buffLen++] = (byte) (l & 0xff);
    }

    /**
     * Fetches a 64 bit integer from the byte buffer.
     */
    public long getLong() {
        long ret = ((long) (buff[idx++] & 0xff) << 56);
        ret |= ((long) (buff[idx++] & 0xff) << 48);
        ret |= ((long) (buff[idx++] & 0xff) << 40);
        ret |= ((long) (buff[idx++] & 0xff) << 32);
        ret |= ((long) (buff[idx++] & 0xff) << 24);
        ret |= ((long) (buff[idx++] & 0xff) << 16);
        ret |= ((long) (buff[idx++] & 0xff) << 8);
        ret |= buff[idx++] & 0xff;
        return ret;
    }

    /**
     * Adds a <code>Date</code> to the byte buffer.
     */
    public void putDate(final Date d) {
        ensureAvailable(9);
        if (d == null) {
            putBoolean(false);
            return;
        }
        putBoolean(true);
        putLong(d.getTime());
    }

    /**
     * Fetches a <code>Date</code> from the byte buffer.
     */
    public Date getDate() {
        if (!getBoolean()) {
            return null;
        }
        return new Date(getLong());
    }

    /**
     * Adds a string to the byte buffer. The string is coded using UTF.
     */
    public void putString(final String s) {
        if (s == null) {
            putShort((short) -1);
        } else {
            final byte[] b = StringUtils.getBytesUtf8(s);
            putShort((short) b.length);
            putBytes(b);
        }
    }

    /**
     * Fetches an UTF encoded string from the byte buffer.
     */
    public String getString() {
        final int n = getShort();
        if (n < 0) {
            return null;
        }
        final String ret = StringUtils.newStringUtf8(buff, idx, n);
        idx += n;
        return ret;
    }

    public void putStringArray(final String[] array) {
        if (array == null) {
            putInt(-1);
        } else {
            putInt(array.length);
            for (final String anArray : array) {
                putString(anArray);
            }
        }
    }

    public String[] getStringArray() {
        final int len = getInt();
        if (len < 0) {
            return null;
        }
        final String[] array = new String[len];
        for (int q = 0; q < array.length; q++) {
            array[q] = getString();
        }
        return array;
    }

    public void putStringList(final List<String> strings) {
        if (strings == null) {
            putInt(-1);
        } else {
            putStringArray(strings.toArray(new String[0]));
        }
    }

    public List<String> getStringList() {
        final String[] array = getStringArray();
        if (array == null) {
            return null;
        }
        return Arrays.asList(array);
    }

    public void putObject(final Object object) {
        try {
            if (object == null) {
                putInt(-1);
            } else {
                final ByteArrayOutputStream baOut = new ByteArrayOutputStream();
                final ObjectOutputStream out = new ObjectOutputStream(baOut);
                out.writeUnshared(object);
                out.close();
                final byte[] bytes = baOut.toByteArray();
                putInt(bytes.length);
                putBytes(bytes);
            }
        } catch (final IOException e) {
            throw new RuntimeException("Error writing object to byte array", e);
        }
    }

    public Object getObject() {
        final int len = getInt();
        if (len < 0) {
            return null;
        }
        final byte[] bytes = getBytes(len);
        try {
            final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
            final Object object = in.readUnshared();
            in.close();
            return object;
        } catch (final IOException e) {
            throw new RuntimeException("Error reading object from byte array", e);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException("Class of object not found", e);
        }
    }

    public void readFromStream(final DataInputStream in) {
        readFromStream(in, 1024 * 1024 * 1024);
    }

    public void readFromStream(final DataInputStream in, final int maxBytes) {
        try {
            buffLen = in.readInt();
            if (buffLen < 0 || buffLen > maxBytes) {
                throw new UncheckedIoException("Cannot read " + buffLen + " bytes.  It is too much (or too little).  Max is " + maxBytes);
            }
            buff = new byte[buffLen];
            in.readFully(buff);
            idx = NUM_RESERVED_BYTES;
            numBytesReadFromStreams += 2 + buffLen;
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        }
    }

    public void writeToStream(final DataOutputStream out) {
        try {
            out.writeInt(buffLen);
            out.write(buff, 0, buffLen);
            numBytesWrittenToStreams += buffLen;
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        }
    }

    public void readFromDatagram(final DatagramSocket sock) {
        final byte[] b = new byte[65507];
        final DatagramPacket dp = new DatagramPacket(b, b.length);

        try {
            sock.receive(dp);
            setBuffer(dp.getData(), dp.getLength());
            setAddress(dp.getAddress());
            setPort(dp.getPort());
            numBytesReadFromDatagrams += 2 + buffLen;
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        }
    }

    public void writeToDatagram(final DatagramSocket sock, final InetAddress theAddress, final int thePort) {
        try {
            final DatagramPacket dp = new DatagramPacket(buff, buffLen, theAddress, thePort);
            sock.send(dp);
            numBytesWrittenToDatagrams += buffLen;
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        }
    }

}
