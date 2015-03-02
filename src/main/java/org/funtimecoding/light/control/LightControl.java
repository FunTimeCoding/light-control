package org.funtimecoding.light.control;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

public class LightControl {

    private InputStream in = null;
    private OutputStream out = null;
    private String lineBuffer = "";

    public void connect(String portName) throws Exception {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

        if (portIdentifier.isCurrentlyOwned()) {
            System.err.println("Port " + portName + " is in use.");
        } else {
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                this.in = serialPort.getInputStream();
                this.out = serialPort.getOutputStream();
            }
        }
    }

    public InputStream getIn() {
        return this.in;
    }

    public OutputStream getOut() {
        return this.out;
    }

    public void printCommDevices() {
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {
            CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
            System.out.println(portId.getName());
        }
    }

    void read(LightControlMainFrame lcmf) throws IOException {
        int myByte;
        while ((myByte = this.in.read()) > -1) {
            if (myByte != '\n') {
                lineBuffer += (char) myByte;
            } else {
                System.out.println("Received: " + lineBuffer);
                lineBuffer = "";
                //lcmf.parseMessage(msg);
            }
        }
    }
}
