import javax.swing.*;
import java.awt.event.*;

public class GuiDemo implements ActionListener {
    JButton button;
    private int count;

    public void createAndRenderGui() {
        JFrame frame = new JFrame();
        button = new JButton("submit");

        button.addActionListener(this);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(button);
        frame.setSize(400, 400);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        count++;
        String text = count == 1 ? "Clicked once" : "Clicked " + count + " time(s)";
        button.setText(text);
    }

    public static void main(String[] args) {
        GuiDemo gui = new GuiDemo();
        gui.createAndRenderGui();
    }
}

