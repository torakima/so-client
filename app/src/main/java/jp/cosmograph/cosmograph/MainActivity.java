package jp.cosmograph.cosmograph;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import jp.cosmograph.cosmograph.databinding.ActivityMainBinding;
import jp.cosmograph.cosmograph.model.Status;

public class MainActivity extends BaseActivity {
    Status status;
    String ipAddressText;
    String PortText;
    private BufferedReader networkReader;
    private BufferedWriter networkWriter;

    //클라세팅
    private StringBuilder clientMsgBuilder = null;
    private EditText joinIpText;//클라접속아이피
    private java.net.Socket clientSocket;
    private DataInputStream clientIn;
    private DataOutputStream clientOut;
    private String clientMsg;
    private String nickName;
    private static final int CLIENT_TEXT_UPDATE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        status = new Status();
        status.setConnect(false);
        binding.setModel(status);
        binding.setMain(this);
        setVideo((VideoView) findViewById(R.id.video));
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void onIpTextChanged(CharSequence s, int start, int before, int count) {
        Log.w("tag", "onTextChanged " + s);
        ipAddressText = s.toString();
    }

    public void onPortTextChanged(CharSequence s, int start, int before, int count) {
        Log.w("tag", "onTextChanged " + s);
        PortText = s.toString();
    }

    public void Connect(View view) {

        joinServer();

//        MyClientTask myClientTask = new MyClientTask(
//                "test"
//        );
//        myClientTask.execute();
        status.setConnect(true);
//        status.setStartButton(true);
    }

    public void PickUpStart(View view) {
        sendMsg("FEED_START");
    }

    public void PickUpGood(View view) {
        sendMsg("PICKUP_GOOD");
        status.setFeedComplete(false);
        status.setStartButton(true);
    }

    public void sendData(final String param) {

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                PrintWriter out = new PrintWriter(networkWriter, true);
//                out.println(param);
//                out.flush();
//            }
//        });

    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msgg) {
            super.handleMessage(msgg);
            switch (msgg.what) {
                case CLIENT_TEXT_UPDATE: {
                    clientMsgBuilder = new StringBuilder();
                    result(clientMsgBuilder.append(clientMsg).toString());
                }
                break;

            }
        }
    };

    private  void sendMsg(final String msg) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    clientOut.writeUTF(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void joinServer() {
        if (nickName == null) {
            nickName = "connect";
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = new java.net.Socket(ipAddressText, Integer.parseInt(PortText));
//                    clientSocket = new java.net.Socket("192.168.2.22", 7777);

                    clientOut = new DataOutputStream(clientSocket.getOutputStream());
                    clientIn = new DataInputStream(clientSocket.getInputStream());
                    clientOut.writeUTF(nickName);
                    while (clientIn != null) {
                        try {
                            clientMsg = clientIn.readUTF();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(CLIENT_TEXT_UPDATE);
                    }
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();
    }


    private void result(final String msg) {
        switch (msg) {
            case "FEED_SET06":
                status.setConnect(true);
                status.setStartButton(true);
                break;
            case "FEED_PRG01":
                status.setFeedStatus(1);
                status.setStartButton(false);
                break;
            case "FEED_PRG02":
                status.setFeedStatus(2);
                status.setStartButton(false);
                break;
            case "FEED_PRG03":
                status.setFeedStatus(3);
                status.setStartButton(false);
                break;
            case "FEED_PRG04":
                status.setFeedStatus(4);
                status.setStartButton(false);
                break;
            case "FEED_PRG05":
                status.setFeedStatus(5);
                status.setStartButton(false);
                break;
            case "FEED_PRG06":
                status.setFeedStatus(6);
                status.setStartButton(false);
                break;
            case "PICKUP_END":
                status.setFeedStatus(0);
                status.setFeedComplete(true);
                status.setStartButton(false);
                sendMsg("PICKUP_GOOD");
                break;
            default:
                break;
        }
        Handler h = new Handler(this.getApplication().getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}