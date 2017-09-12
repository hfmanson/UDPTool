/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mansoft;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

/**
 * FXML Controller class
 *
 * @author hfman
 */
public class UDPToolController implements Initializable {
    DatagramSocket socket;
    UdpClientThread udpClientThead;
    SocketAddress lastSocketAddres;

    @FXML
    ToggleButton buttonBind;

    @FXML
    Button buttonSend;

    @FXML
    TextField localPort;

    @FXML
    TextField message;

    @FXML
    TextField ipAddress;

    @FXML
    TextField remotePort;

    @FXML
    Label state;

    @FXML
    Label response;

    @FXML
    Button copy;

    void closeSocket() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            try {
                System.err.println("Joining thread");
                udpClientThead.join();
                System.err.println("Joined");
            } catch (InterruptedException ex) {
                Logger.getLogger(UDPToolController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        buttonBind.setText("Bind");
        buttonSend.setDisable(true);
    }

    @FXML
    private void handleBind() {
        int port = Integer.parseInt(localPort.getText());
        try {
            if (buttonBind.isSelected()) {
                socket = new DatagramSocket(port);
                udpClientThead = new UdpClientThread(this);
                udpClientThead.start();
                buttonBind.setText("Close");
                buttonSend.setDisable(false);
            } else {
                closeSocket();
            }
        } catch (BindException e) {
            state.setText("port " + port + " already in use");
            buttonBind.setSelected(false);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSend() {
        final byte[] buf = message.getText().getBytes(Charset.forName("UTF-8"));
        new Thread() {
            @Override
            public void run()
            {
                try {
                    DatagramPacket packet =
                            new DatagramPacket(buf, buf.length, InetAddress.getByName(ipAddress.getText()), Integer.parseInt(remotePort.getText()));
                    socket.send(packet);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @FXML
    private void handleCopy() {
        InetSocketAddress lastSocketAddress = (InetSocketAddress) udpClientThead.lastSocketAddress;
        if (lastSocketAddress != null) {
            ipAddress.setText(lastSocketAddress.getHostName());
            remotePort.setText(Integer.toString(lastSocketAddress.getPort()));
        }
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}
