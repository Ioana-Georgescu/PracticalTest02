package ro.pub.cs.systems.eim.practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {
    private EditText op = null;
    private Button doCalc = null;
    private EditText clientAddressEditText = null;
    private EditText clientPortEditText = null;
    private EditText serverPortEditText = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;
    private TextView resultTextView = null;

    private CalcButtonClickListener calcButtonClickListener = new CalcButtonClickListener();
    private class CalcButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String opStr = op.getText().toString();
            if (opStr == null || opStr.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();

            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            resultTextView.setText(Constants.EMPTY_STRING);

            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), resultTextView
            );
            clientThread.start();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);
        doCalc = (Button)findViewById(R.id.calc);
        doCalc.setOnClickListener(calcButtonClickListener);
        op = (EditText)findViewById(R.id.op);

        serverPortEditText = (EditText)findViewById(R.id.server_port_edit_text);
        clientAddressEditText = (EditText)findViewById(R.id.client_address_edit_text);
        clientPortEditText = (EditText)findViewById(R.id.client_port_edit_text);
        resultTextView = (TextView)findViewById(R.id.result);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }

}