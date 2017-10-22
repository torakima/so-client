package jp.cosmograph.cosmograph.manager;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;

/**
 * Created by ingyu on 2017-10-22.
 */

public class SocketManager {
    private String IP;
    private int Port;
    private SocketChannel m_hSocketChannel;
    private Selector m_hSelector;
    private readDataThread m_readData;
    private sendDataThread m_sendData;
    private Handler m_handler;

    public SocketManager(String ip, int port, Handler h) {
        this.IP = ip;
        this.Port = port;
        this.m_handler = h;
        m_readData = new readDataThread();
        m_readData.start();
    }

    private void setSocket(String ip, int port) throws IOException {
        m_hSelector = Selector.open();
        m_hSocketChannel = SocketChannel.open(new InetSocketAddress(ip, port));
        m_hSocketChannel.configureBlocking(false);
        m_hSocketChannel.register(m_hSelector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }

    public void sendData(String data) {
        m_sendData = new sendDataThread(m_hSocketChannel, data);
        m_sendData.start();
    }

    private void read(SelectionKey key) throws Exception {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int read = 0;
        read = sc.read(buffer);
        buffer.flip();
        String data = new String();
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        data = decoder.decode(buffer).toString();
        Message msg = m_handler.obtainMessage();
        msg.what = 1;
        msg.obj = data;
        m_handler.sendMessage(msg);
        clearBuffer(buffer);
    }

    private void clearBuffer(ByteBuffer buffer) {
        if (buffer != null) {
            buffer.clear();
            buffer = null;
        }
    }

    /*********** inner thread classes **************/
    public class sendDataThread extends Thread {
        private SocketChannel sdt_hSocketChannel;
        private String data;

        public sendDataThread(SocketChannel sc, String d) {
            sdt_hSocketChannel = sc;
            data = d;
        }

        public void run() {
            try {
                sdt_hSocketChannel.write(ByteBuffer.wrap(data.getBytes()));
            } catch (Exception e1) {
            }
        }
    }

    public class readDataThread extends Thread {
        public readDataThread() {
        }

        public void run() {
            try {
                setSocket(IP, Port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            m_handler.obtainMessage();
            m_handler.sendEmptyMessage(0);
            try {
                while (true) {
                    m_hSelector.select();
                    Iterator it = m_hSelector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey key = (SelectionKey) it.next();
                        if (key.isReadable()) {
                            try {
                                read(key);
                            } catch (Exception e) {
                            }
                        }
                        it.remove();
                    }
                }
            } catch (Exception e) {
            }
        }
    }
}