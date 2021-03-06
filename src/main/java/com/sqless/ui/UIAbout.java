package com.sqless.ui;

import com.sqless.utils.SQLUtils;
import java.io.IOException;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import com.sqless.utils.MiscUtils;
import com.sqless.utils.UIUtils;
import java.util.Map;
import java.util.Properties;

public class UIAbout extends javax.swing.JDialog {

    public UIAbout() {
        super(UIClient.getInstance(), true);
        initComponents();
        loadInfo();
        setLocationRelativeTo(getParent());
        getRootPane().setDefaultButton(btnClose);
        btnClose.requestFocus();
    }

    public void appendCategory(String cat) {
        HTMLDocument doc = (HTMLDocument) txtInfo.getDocument();
        HTMLEditorKit kit = (HTMLEditorKit) txtInfo.getEditorKit();
        try {
            kit.insertHTML(doc, doc.getLength(), "<div style=\"font-family: Segoe UI; font-size: 15px;\">" + cat
                    + "</div>", 0, 0, null);
        } catch (BadLocationException | IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void appendItem(String title, String text) {
        HTMLDocument doc = (HTMLDocument) txtInfo.getDocument();
        HTMLEditorKit kit = (HTMLEditorKit) txtInfo.getEditorKit();
        try {
            kit.insertHTML(doc, doc.getLength(), "<div style=\"font-family: Segoe UI\">"
                    + "<span style=\"font-weight: bold\">" + title + ":</span>"
                    + "<span style=\"font-size: 10px\"> " + text + "</span>"
                    + "</div>", 0, 0, null);
        } catch (BadLocationException | IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void appendItem(String text) {
        HTMLDocument doc = (HTMLDocument) txtInfo.getDocument();
        HTMLEditorKit kit = (HTMLEditorKit) txtInfo.getEditorKit();
        try {
            kit.insertHTML(doc, doc.getLength(), "<div style=\"font-family: Segoe UI\">"
                    + "<span style=\"font-size: 10px\"> " + text + "</span>"
                    + "</div>", 0, 0, null);
        } catch (BadLocationException | IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void appendSeparator() {
        HTMLDocument doc = (HTMLDocument) txtInfo.getDocument();
        HTMLEditorKit kit = (HTMLEditorKit) txtInfo.getEditorKit();
        try {
            kit.insertHTML(doc, doc.getLength(), "<hr style=\"padding: 5px\">", 0, 0, null);
        } catch (BadLocationException | IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void loadInfo() {
        MiscUtils.getSystemInfo(() -> {
            appendItem("Cargando información de sistema...");
        }, info -> {
            txtInfo.setText("");
            appendCategory("SQLess");
            appendItem("Diseñado por", "David Orquin, Tomás Casir, Valeria Fornieles");
            appendSeparator();
            appendCategory("Sistema");
            appendItem("Sistema operativo", info.get("OS Name") + " (" + info.get("OS Version") + ")");
            appendItem("Arquitectura", info.get("System Type"));
            appendItem("Usuario", info.get("user"));
            appendSeparator();
            appendCategory("MySQL");
            appendItem("Version MySQL", info.get("sql-version"));
            appendItem("Directorio MySQL", info.get("sql-basedir"));
            appendSeparator();
            appendCategory("Java™");
            appendItem("Home", info.get("Java-Home"));
            appendItem("Version", info.get("Java-Version"));
            appendItem("Vendor", info.get("Java-Vendor"));
            UIUtils.scrollToTop(scrMain);
        }, () -> {
            UIUtils.showErrorMessage("Error", "Hubo un error al traer la información del sistema. Intenta de nuevo más adelante.", UIClient.getInstance());
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlContainer = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        scrMain = new javax.swing.JScrollPane();
        txtInfo = new javax.swing.JTextPane();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Acerca de SQLess");

        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ui_splash/SQLess_SPLASH.png"))); // NOI18N

        txtInfo.setEditable(false);
        txtInfo.setContentType("text/html"); // NOI18N
        scrMain.setViewportView(txtInfo);

        btnClose.setText("OK");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlContainerLayout = new javax.swing.GroupLayout(pnlContainer);
        pnlContainer.setLayout(pnlContainerLayout);
        pnlContainerLayout.setHorizontalGroup(
            pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrMain)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlContainerLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlContainerLayout.setVerticalGroup(
            pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblLogo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrMain, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JPanel pnlContainer;
    private javax.swing.JScrollPane scrMain;
    private javax.swing.JTextPane txtInfo;
    // End of variables declaration//GEN-END:variables

}
