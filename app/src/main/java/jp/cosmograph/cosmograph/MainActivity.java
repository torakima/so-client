package jp.cosmograph.cosmograph;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.UnknownHostException;

import jp.cosmograph.cosmograph.databinding.ActivityMainBinding;
import jp.cosmograph.cosmograph.model.Status;

public class MainActivity extends BaseActivity {
    Status status;
    String ipAddressText;
    String PortText;
    private BufferedReader networkReader;
    private BufferedWriter networkWriter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        status = new Status();
        status.setConnect(false);
        binding.setModel(status);
        binding.setMain(this);
        setVideo((VideoView) findViewById(R.id.video));
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

        MyClientTask myClientTask = new MyClientTask(
                "test"
        );
        myClientTask.execute();
        status.setConnect(true);
        status.setStartButton(true);
    }

    public void PickUpStart(View view) {
        sendData("FEED_STA06");
    }

    public void PickUpGood(View view) {
        sendData("PICKUP_GOOD");
        status.setFeedComplete(false);
        status.setStartButton(true);
    }

    public void sendData(final String param) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PrintWriter out = new PrintWriter(networkWriter, true);
                out.println(param);
                out.flush();
            }
        });

    }

    private Handler m_Handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(MainActivity.this, "server socket create : " + msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(MainActivity.this, "server responded : " + msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    public class MyClientTask extends AsyncTask<Void, Void, Void> {
        //
//        String dstAddress;
//        int dstPort;
        String response = "";

        MyClientTask(String status) {
//            dstAddress = addr;
//            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            java.net.Socket socket = null;

            try {
                socket = new java.net.Socket(ipAddressText, Integer.parseInt(PortText));
//                socket = new java.net.Socket("192.168.2.22", 8080);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while (true) {
                    try {
                        line = networkReader.readLine();
                        if (line != null) {
                            Log.d("Chatting is line", "line : " + line);
                            result(line);
                        }
//                        result(line);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                /*
                 * notice:
				 * inputStream.read() will block if no data return
				 */
//                while ((bytesRead = networkReader.read(buffer)) != -1) {
//                    byteArrayOutputStream.write(buffer, 0, bytesRead);
//                    response += byteArrayOutputStream.toString("UTF-8");
//                    result(response);
//                }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }


    private void result(final String msg) {
        switch (msg) {
            case "connected":
                status.setConnect(true);
                status.setStartButton(true);
                break;
            case "FEED_PRO01":
                status.setFeedStatus(1);
                status.setStartButton(false);
                break;
            case "FEED_PRO02":
                status.setFeedStatus(2);
                status.setStartButton(false);
                break;
            case "FEED_PRO03":
                status.setFeedStatus(3);
                status.setStartButton(false);
                break;
            case "FEED_PRO04":
                status.setFeedStatus(4);
                status.setStartButton(false);
                break;
            case "FEED_PRO05":
                status.setFeedStatus(5);
                status.setStartButton(false);
                break;
            case "FEED_PRO06":
                status.setFeedStatus(6);
                status.setStartButton(false);
                break;
            case "PICKUP_END":
                status.setFeedComplete(true);
                status.setStartButton(false);
                break;
            default:
                break;
        }
        Handler h = new Handler(this.getApplication().getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}