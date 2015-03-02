package org.funtimecoding.light.control;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author shiin
 */
public class SerialWriter implements Runnable {

    OutputStream out;

    public SerialWriter(OutputStream out) {
        this.out = out;
    }

    @Override
    public void run() {
        try {
            int c;

            while ((c = System.in.read()) > -1) {
                this.out.write(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
