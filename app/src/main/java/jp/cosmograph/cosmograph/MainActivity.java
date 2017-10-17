package jp.cosmograph.cosmograph;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

import jp.cosmograph.cosmograph.databinding.ActivityMainBinding;
import jp.cosmograph.cosmograph.model.Status;

public class MainActivity extends AppCompatActivity {
    Status status;
    String ipAddressText;
    String PortText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        status = new Status();
        status.setConnect(false);
        binding.setModel(status);
        binding.setMain(this);

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
    }

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

                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();

				/*
                 * notice:
				 * inputStream.read() will block if no data return
				 */
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                    result(response);
                }

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
//            textResponse.setText(response);
            super.onPostExecute(result);
        }

    }

    private void result(final String msg) {
        switch (msg) {
            case "connected":
                status.setConnect(true);
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