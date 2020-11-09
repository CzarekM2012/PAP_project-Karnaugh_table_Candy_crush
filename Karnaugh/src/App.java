import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Container;
import java.awt.Component;
import java.awt.Font;
import java.awt.Dimension;

class ButtonListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent event) {
        ((JButton) event.getSource()).setText("imPressed!");
    }
}

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("First Button");
            Container pane = frame.getContentPane();
            frame.setBounds(0, 0, 500, 500); // window size setting

            // Finish after the window has been closed
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            BoxLayout mainLayout = new BoxLayout(pane, BoxLayout.Y_AXIS);
            pane.setLayout(mainLayout);

            // Hello text field
            JTextField helloTextField = new JTextField("Hello World!");
            Font font = new Font("SansSerif", Font.BOLD, 70);
            helloTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
            helloTextField.setEditable(false);
            helloTextField.setFont(font);
            helloTextField.setHorizontalAlignment(JTextField.CENTER);
            pane.add(helloTextField);

            // Button
            JButton closeButton = new JButton("Not yet pressed");
            closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            closeButton.addActionListener(new ButtonListener());
            closeButton.setPreferredSize(new Dimension(250, 100));
            pane.add(closeButton);

            // frame.pack();
            frame.setVisible(true); // pokaż onkno
        });
    }
}