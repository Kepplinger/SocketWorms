package connectionObjects;

import client.ClientModel;
import server.ServerModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Kepplinger on 22.06.2016.
 */
public class Connection implements Runnable {

    volatile Socket socket;
    volatile ServerModel model;

    public Connection(Socket socket, ServerModel model) {
        this.socket = socket;
        this.model = model;
    }

    @Override
    public void run() {

        ObjectInputStream inputStream = null;
        ObjectOutputStream outputStream = null;

        try {

            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }


        while (socket != null) {
            try {
                Request request = (Request) inputStream.readObject();

                if (request.getRequestType().equals(RequestType.RETURN_PACKAGE)) {
                    outputStream.writeObject(model.getPackage());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
