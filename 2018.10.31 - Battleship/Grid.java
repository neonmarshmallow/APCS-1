import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;

public class Grid extends JFrame implements ActionListener {
    private int rows = 10;
    private int columns = 10;
    private JButton[][] buttons; // Create 2D array of buttons

    public Grid(int inputRows, int inputColumns) {
        rows = inputRows;
        columns = inputColumns;
        buttons = new JButton[rows][columns];
    }
    
    void setFrameTitle(String title) {
        this.setTitle(title);
    }

    void createGrid() {
        Container pane = getContentPane();
        pane.setBackground(Color.lightGray);
        pane.setLayout(new GridLayout(rows, columns));

        //Create 100 buttons, all at a preferred size of 40x40
        int buttonCount = 0;    
        String buttonID;
        for (int y = 0; y < columns; y++) {
            for (int x = 0; x < rows; x++) {
                buttonID = ++buttonCount + "";

                buttons[y][x] = new JButton();
                buttons[y][x].setPreferredSize(new Dimension(40, 40));
                buttons[y][x].setName(buttonID);
                buttons[y][x].addActionListener(this);
                buttons[y][x].setActionCommand(x + ", " + y);
                pane.add(buttons[y][x]);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        //Get button coordinate
        String actionCoordinate = e.getActionCommand();
        String[] xy = actionCoordinate.split(", ");
        int x = Integer.parseInt(xy[0]);
        int y = Integer.parseInt(xy[1]);

        //Actions
        buttons[y][x].setBackground(Color.red);
        PlaySound sound = new PlaySound();
        System.out.println("User called a hit on (" + x + ", " + y + ")");
    }
}