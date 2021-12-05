package org.cis120.Othello;

/**
 * CIS 120 HW09 - TicTacToe Demo
 * (c) University of Pennsylvania
 * Created by Bayley Tuch, Sabrina Green, and Nicolas Corona in Fall 2020.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This class sets up the top-level frame and widgets for the GUI.
 * 
 * This game adheres to a Model-View-Controller design framework. This
 * framework is very effective for turn-based games. We STRONGLY
 * recommend you review these lecture slides, starting at slide 8,
 * for more details on Model-View-Controller:
 * https://www.seas.upenn.edu/~cis120/current/files/slides/lec37.pdf
 * 
 * In a Model-View-Controller framework, Game initializes the view,
 * implements a bit of controller functionality through the reset
 * button, and then instantiates a GameBoard. The GameBoard will
 * handle the rest of the game's view and controller functionality, and
 * it will instantiate a TicTacToe object to serve as the game's model.
 */
public class RunOthello implements Runnable {
    public void run() {
        // NOTE: the 'final' keyword denotes immutability even for local variables.

        // Top-level frame in which game components live
        final JFrame frame = new JFrame("Othello");
        frame.setLocation(330, 15);

        // Status panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);
        final JLabel status = new JLabel("Setting up...");
        status_panel.add(status);

        // Game board
        final GameBoard board = new GameBoard(status);
        //sets background to green like classic othello boards
        Color boardColor = new Color(50, 130, 50);
        board.setBackground(boardColor);
        frame.add(board, BorderLayout.CENTER);

        //a mouselistner to check for events in order to display JOptionPanes in the frame
        board.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                EventStatus event = board.getEvent();
                if (event == EventStatus.ERROR) {
                    JOptionPane.showMessageDialog(frame, "Invalid Move", "Error", JOptionPane.ERROR_MESSAGE);
                    board.setEventNull();
                }
                else if (event == EventStatus.WIN_BLACK)
                {
                    optionPane("BLACK Wins!", frame);
                    board.setEventNull();                }
                else if (event == EventStatus.WIN_WHITE)
                {
                    optionPane("WHITE Wins!", frame);
                    board.setEventNull();                }
                else if (event == EventStatus.TIE)
                {
                    optionPane("It's a Tie!", frame);
                    board.setEventNull();                }
                else if (event == EventStatus.PASS_BLACK)
                {
                    optionPane("BLACK must pass their turn", frame);
                    board.setEventNull();
                    board.passStatus();
                }
                else if (event == EventStatus.PASS_WHITE)
                {
                    optionPane("WHITE must pass their turn", frame);
                    board.setEventNull();
                    board.passStatus();
                }
            }
        });

        // Reset button
        final JPanel control_panel = new JPanel();
        frame.add(control_panel, BorderLayout.NORTH);

        // Note here that when we add an action listener to the reset button, we
        // define it as an anonymous inner class that is an instance of
        // ActionListener with its actionPerformed() method overridden. When the
        // button is pressed, actionPerformed() will be called.
        final JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.reset();
            }
        });

        //save button
        final JButton save = new JButton("Save");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    board.saveGameBoard();
                    JOptionPane.showMessageDialog(frame, "Saved at " + board.getFileString());
                }
                catch (FileNotFoundException exception)
                {
                    JOptionPane.showMessageDialog(frame, "File Note Found", "Error", JOptionPane.ERROR_MESSAGE);
                    //File Not Found exception will alert the user
                }
                catch (IOException exception)
                {
                    JOptionPane.showMessageDialog(frame, "IO Exception", "Error", JOptionPane.ERROR_MESSAGE);
                //IOException will give an error dialogue to the user
                }
            }
        });

        //load button
        //allows user to specify filepath
        final JButton load = new JButton("Load");
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    String filepath = JOptionPane.showInputDialog(frame, "Enter filepath");
                    board.loadGameBoard(filepath);
                }
                catch (FileNotFoundException exception)
                {
                    JOptionPane.showMessageDialog(frame, "File Note Found", "Error", JOptionPane.ERROR_MESSAGE);
                    //file not found exception will alert the user
                }
                catch (IOException exception)
                {
                    //IO exception will alert the user with an error dialogue
                    JOptionPane.showMessageDialog(frame, "IO Exception", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        control_panel.add(reset);
        control_panel.add(save);
        control_panel.add(load);

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start the game
        board.reset();
    }

    //helper method so i don't have to write JOptionPane a lot
    private void optionPane(String msg, JFrame frame)
    {
        JOptionPane.showMessageDialog(frame, msg);
    }
}