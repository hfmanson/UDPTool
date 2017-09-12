/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mansoft;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javafx.application.Platform;

/**
 *
 * @author hfman
 */
public class UdpClientThread extends Thread {

    String dstAddress;
    int dstPort;
    private boolean running;

    InetAddress address;
    SocketAddress lastSocketAddress;

    UDPToolController udpToolController;

    public UdpClientThread(UDPToolController udpTool) {
        this.udpToolController = udpTool;
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    private void sendState(final String stateText){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                udpToolController.state.setText(stateText);
            }
        });
    }

    @Override
    public void run() {
        running = true;

        try {
            address = InetAddress.getByName(dstAddress);

            // send request
            byte[] buf = new byte[256];
            sendState("socket bound to port " + udpToolController.socket.getLocalPort());
            for (;;) {
                // get response
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                udpToolController.socket.receive(packet);
                lastSocketAddress = packet.getSocketAddress();
                final String line = lastSocketAddress.toString() + ":" + new String(packet.getData(), 0, packet.getLength());

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        udpToolController.response.setText(line);
                    }
                });
//                handler.sendMessage(
//                        Message.obtain(handler, MainActivity.UdpClientHandler.UPDATE_MSG, line));
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sendState("socket unbound");
            if(udpToolController.socket != null && !udpToolController.socket.isClosed()){
                udpToolController.socket.close();
                //handler.sendEmptyMessage(MainActivity.UdpClientHandler.UPDATE_END);
            }
        }

    }

}
