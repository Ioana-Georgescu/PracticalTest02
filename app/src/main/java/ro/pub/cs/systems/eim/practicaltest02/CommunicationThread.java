package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }
    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = Utilities.getReader(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintWriter printWriter = null;
            try {
                printWriter = Utilities.getWriter(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");
            try {
                String opStr = bufferedReader.readLine();
                opStr = opStr.trim();
                String[] separated = opStr.split(",");
                String opType = separated[0];
                Integer op1 = Integer.parseInt(separated[1]);
                Integer op2 = Integer.parseInt(separated[2]);
                String finalResult = null;
                Integer result = 0;
                if (Objects.equals(opType, "sum")) {
                    try {
                        result = Math.addExact(op1, op2);
                        finalResult = String.valueOf(result);
                    }
                    catch (ArithmeticException e) {
                        finalResult = "overflow";
                    }
                }
                else {
                    try {
                        result = Math.multiplyExact(op1, op2);
                        finalResult = String.valueOf(result);
                    }
                    catch (ArithmeticException e) {
                        finalResult = "overflow";
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                printWriter.println(finalResult);
                printWriter.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
