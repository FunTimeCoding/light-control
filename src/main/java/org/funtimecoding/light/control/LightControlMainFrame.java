package org.funtimecoding.light.control;

import com.jamierf.rxtx.RXTXLoader;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import javax.swing.JOptionPane;

/**
 * @author shiin
 */
public class LightControlMainFrame extends javax.swing.JFrame {

    public static LightControl control;
    private static Connection connection;
    public static HashMap<String, String> dropdownItems = new HashMap<String, String>();

    /**
     * Creates new form LightControlMainFrame
     */
    public LightControlMainFrame() {
        initComponents();
        jComboBox1.setRenderer(new DropdownRenderer(this));
        
        //ConnectDB
        String url = "jdbc:mysql://kevin-sauter.de:3306/lightControl";
        String user = "lightControl";
        String password = "toor01";

        try {
            LightControlMainFrame.connection = DriverManager.getConnection(url, user, password);
            System.out.println("Opened database successfully");
        } catch (SQLException ex) {
            System.out.println("Opened database FAILED!");
        }

        try {
            RXTXLoader.load();
        } catch (IOException ex) {
            System.err.println("Fatal error: " + ex.getMessage());
        }

        final LightControl control = new LightControl();
        this.control = control;
        //System.out.println("Devices:");
        //control.printCommDevices();
        //System.out.println("Enter device to open (e.g. /dev/tty.mydevice): ");
        //Scanner scan = new Scanner(System.in);
        //String deviceName = scan.nextLine();
        //if (deviceName.equals("")) {
        //    System.err.println("Device name cannot be empty.");
        //    System.exit(1);
        //}
        try {
            //control.connect("/dev/tty.uart-A7FF4A1BBE0D0922");
            System.out.println("connected");
            //control.connect(deviceName);
        } catch (Exception ex) {
            System.err.println("Fatal exception: " + ex.getMessage());
        }

        MouseListener mouse = new MouseListener() {
            @Override
            public void mousePressed(MouseEvent event) {
                //Mouse Pressed Functionality add here
            }

            @Override
            public void mouseClicked(MouseEvent event) {
                // TODO Auto-generated method stub
            }

            @Override
            public void mouseEntered(MouseEvent event) {
                // TODO Auto-generated method stub
            }

            @Override
            public void mouseExited(MouseEvent event) {
                // TODO Auto-generated method stub
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                int red = redSlider.getValue();
                int green = greenSlider.getValue();
                int blue = blueSlider.getValue();
                String msg = "C " + red + " " + green + " " + blue + "\n";
                System.out.print(msg);

                try {
                    OutputStream out = control.getOut();
                    int len = msg.length();

                    for (int i = 0; i < len; i++) {
                        out.write(msg.charAt(i));
                    }
                } catch (Exception e) {
                    System.err.println("Error in stateChanged: " + e.getMessage());
                }
            }
        };

        redSlider.addMouseListener(mouse);
        greenSlider.addMouseListener(mouse);
        blueSlider.addMouseListener(mouse);

        //InputStream in = control.getIn();
        //if (in != null) {
        //    (new Thread(new SerialReader(in, redSlider, greenSlider, blueSlider))).start();
        //} else {
        //    System.err.println("Device not be found.");
        //    System.exit(1);
        //}
        //loadDatabase();
        loadDropdown(false);
        
        Timer timer = new Timer();
        timer.schedule( new CommandPoller(connection, redSlider, greenSlider, blueSlider, jComboBox1), 0, 1000);  
    }

    private void loadDatabase() {
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:lightControl.db");
            System.out.println("Opened database successfully");

            DatabaseMetaData dbm = c.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "rgb_farben", null);
            if (tables.next()) {
                tables.close();
                return;
            }
            tables.close();

            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS rgb_farben "
                    + "(id INTEGER PRIMARY KEY   AUTOINCREMENT,"
                    + " name           TEXT    UNIQUE, "
                    + " red            INT     NOT NULL, "
                    + " green          INT, "
                    + " blue           INT)";
            stmt.executeUpdate(sql);

            sql = "INSERT INTO rgb_farben (name, red, green, blue) "
                    + "VALUES ('Rot', 255, 0, 0);";
            stmt.executeUpdate(sql);

            sql = "INSERT INTO rgb_farben (name, red, green, blue) "
                    + "VALUES ('Grün', 0, 255, 0);";
            stmt.executeUpdate(sql);

            sql = "INSERT INTO rgb_farben (name, red, green, blue) "
                    + "VALUES ('Blau', 0, 0, 255);";
            stmt.executeUpdate(sql);

            stmt.close();

            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Table created successfully");

    }

    private void loadDropdown(boolean reload) {

        if (reload) {
            LightControlMainFrame.dropdownItems.clear();
            jComboBox1.removeAllItems();
        }

        Connection c = null;
        Statement stmt = null;
        try {
            c = LightControlMainFrame.connection;
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM rgb_farben;");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String red = rs.getString("red");
                String green = rs.getString("green");
                String blue = rs.getString("blue");
                LightControlMainFrame.dropdownItems.put(name, red + ";" + green + ";" + blue);
                jComboBox1.addItem(name);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Dropdown loaded.");
    }

    public int[] ladeFarbeFürNamen(String name) {
        int[] werte = new int[3];

        Connection c = null;
        Statement stmt = null;
        if (false) {
            try {
                c = LightControlMainFrame.connection;
                System.out.println("Opened database successfully");

                stmt = c.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM rgb_farben WHERE name = '" + name + "';");
                while (rs.next()) {
                    werte[0] = rs.getInt("red");
                    werte[1] = rs.getInt("green");
                    werte[2] = rs.getInt("blue");
                }
                rs.close();
                stmt.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
        }
        
        String daten = LightControlMainFrame.dropdownItems.get(name);
        String[] daten2 = daten.split(";");
        
        werte[0] = Integer.parseInt(daten2[0]);
        werte[1] = Integer.parseInt(daten2[1]);
        werte[2] = Integer.parseInt(daten2[2]);
        
        return werte;
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            System.err.println("Fatal error.");
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LightControlMainFrame().setVisible(true);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        redSlider = new javax.swing.JSlider();
        greenSlider = new javax.swing.JSlider();
        blueSlider = new javax.swing.JSlider();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        redSlider.setMaximum(255);
        redSlider.setValue(0);
        redSlider.setName("r"); // NOI18N

        greenSlider.setMaximum(255);
        greenSlider.setValue(0);
        greenSlider.setName("g"); // NOI18N

        blueSlider.setMaximum(255);
        blueSlider.setValue(0);
        blueSlider.setName("b"); // NOI18N

        jButton1.setText("Speichern");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Anzeigen");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Löschen");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(redSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(greenSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(blueSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(redSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(greenSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(blueSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:

        int[] farbwerte = this.ladeFarbeFürNamen(jComboBox1.getSelectedItem().toString());
        redSlider.setValue(farbwerte[0]);
        greenSlider.setValue(farbwerte[1]);
        blueSlider.setValue(farbwerte[2]);
        String msg = "C " + farbwerte[0] + " " + farbwerte[1] + " " + farbwerte[2] + "\n";
        System.out.print(msg);

        try {
            OutputStream out = LightControlMainFrame.control.getOut();
            int len = msg.length();

            for (int i = 0; i < len; i++) {
                out.write(msg.charAt(i));
            }
        } catch (Exception e) {
            System.err.println("Error in stateChanged: " + e.getMessage());
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:

        Connection c = null;
        Statement stmt = null;
        try {
            c = LightControlMainFrame.connection;

            stmt = c.createStatement();
            String sql = "DELETE FROM rgb_farben WHERE name = '" + jComboBox1.getSelectedItem().toString() + "';";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        this.loadDropdown(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        String name = JOptionPane.showInputDialog("Bitte gib einen Namen für deine Farbe ein.");

        Connection c = null;
        Statement stmt = null;
        try {
            c = LightControlMainFrame.connection;

            stmt = c.createStatement();
            String sql = "INSERT INTO rgb_farben (name, red, green, blue) "
                    + "VALUES ('" + name + "', " + redSlider.getValue() + ", " + greenSlider.getValue() + ", " + blueSlider.getValue() + ");";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Name bereits vergeben!");
        }
        this.loadDropdown(true);

    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider blueSlider;
    private javax.swing.JSlider greenSlider;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSlider redSlider;
    // End of variables declaration//GEN-END:variables
}
