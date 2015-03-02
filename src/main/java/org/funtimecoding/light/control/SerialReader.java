package org.funtimecoding.light.control;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import javax.swing.JSlider;

/**
 * @author shiin
 */
public class SerialReader implements Runnable {

    private InputStream in;
    private final JSlider redSlider;
    private final JSlider greenSlider;
    private final JSlider blueSlider;

    public SerialReader(InputStream in, JSlider redSlider, JSlider greenSlider, JSlider blueSlider) {
        this.in = in;
        this.redSlider = redSlider;
        this.greenSlider = greenSlider;
        this.blueSlider = blueSlider;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.in));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                System.out.println("Received: " + Arrays.toString(parts));

                if (parts[0].equals("C")) {
                    try {
                        redSlider.setValue(Integer.parseInt(parts[1]));
                        greenSlider.setValue(Integer.parseInt(parts[2]));
                        blueSlider.setValue(Integer.parseInt(parts[3]));
                    } catch (Exception e) {
                        System.err.println("Error during int parsing: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error in SerialReader: " + e.getMessage());
        }
    }
}
