package org.cis120.Othello;

/**
 * CIS 120 HW09 - TicTacToe Demo
 * (c) University of Pennsylvania
 * Created by Bayley Tuch, Sabrina Green, and Nicolas Corona in Fall 2020.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * This class instantiates a TicTacToe object, which is the model for the game.
 * As the user clicks the game board, the model is updated. Whenever the model
 * is updated, the game board repaints itself and updates its status JLabel to
 * reflect the current state of the model.
 * 
 * This game adheres to a Model-View-Controller design framework. This
 * framework is very effective for turn-based games. We STRONGLY
 * recommend you review these lecture slides, starting at slide 8,
 * for more details on Model-View-Controller:
 * https://www.seas.upenn.edu/~cis120/current/files/slides/lec37.pdf
 * 
 * In a Model-View-Controller framework, GameBoard stores the model as a field
 * and acts as both the controller (with a MouseListener) and the view (with
 * its paintComponent method and the status JLabel).
 */

@SuppressWarnings("serial")
public class GameBoard extends JPanel {

    private final Othello o; // model for the game
    private final JLabel status; // current status text
    EventStatus eventStatus; // arg for keeping track of any events such as errors, passing, wins,
                             // etc.
    EventStatusEnum tempEventStatus; // temp holder for event status

    // Game constants
    public static final int BOARD_WIDTH = 600;
    public static final int BOARD_HEIGHT = 600;

    /**
     * Initializes the game board.
     */
    public GameBoard(JLabel statusInit, EventStatus eventStatusParam) {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        o = new Othello(); // initializes model for the game
        status = statusInit; // initializes the status JLabel
        eventStatus = eventStatusParam; // initializes event status object

        /*
         * Listens for mouseclicks. Updates the model, then updates the game
         * board based off of the updated model.
         */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = e.getPoint();

                try { // if play turn fails an invalid move was played
                      // updates the model given the coordinates of the mouseclick
                    o.playTurn(p.x / 75, p.y / 75, o.getCurrentTurn());
                    boardUpdate(); // repaints the game board and updates status
                } catch (IllegalArgumentException exception) {
                    tempEventStatus = EventStatusEnum.ERROR;// sets temp event status as error
                }
                eventStatus.setEvent(tempEventStatus); // sends temp event status to the actual
                                                       // event status object so the listener can
                                                       // recieve the change
                tempEventStatus = null; // resets the temp event status
            }
        });
    }

    /**
     * (Re-)sets the game to its initial state.
     */
    public void reset() {
        o.reset();
        status.setText("BLACK's Turn | B: " + o.getBlackPoints() + ", W: " + o.getWhitePoints());
        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    // calls save gameboard method in Othello.java
    public void saveGameBoard() throws IOException {
        o.saveGameBoard();
    }

    // gets output file name to display to the user
    public String getFileString() {
        return o.getFileString();
    }

    // loads a game board at a file location based on user input
    // can be relative or absolute file path
    public void loadGameBoard(String filepath) throws IOException {
        o.loadFile(filepath);
        boardUpdate();
        eventStatus.setEvent(tempEventStatus); // incase events happen, sets the event status
        tempEventStatus = null; // resets the temp event holder
    }

    // shortcut to update status and repaint board
    public void boardUpdate() {
        updateStatus();
        repaint();
    }

    /**
     * Updates the JLabel to reflect the current state of the game.
     */
    private void updateStatus() {
        if (o.getCurrentTurn() == PlayerColor.BLACK) {
            // if next turn is pass then upon the previous move the pass event is declared
            if (o.getPass()) {
                status.setText(
                        "WHITE must pass | B: " + o.getBlackPoints() + ", W: " + o.getWhitePoints()
                );
                tempEventStatus = EventStatusEnum.PASS_WHITE;
            } else {
                status.setText(
                        "BLACK's Turn | B: " + o.getBlackPoints() + ", W: " + o.getWhitePoints()
                );
            }
        } else {
            if (o.getPass()) {
                status.setText(
                        "BLACK must pass | B: " + o.getBlackPoints() + ", W: " + o.getWhitePoints()
                );
                tempEventStatus = EventStatusEnum.PASS_BLACK;
            } else {
                status.setText(
                        "WHITE's Turn | B: " + o.getBlackPoints() + ", W: " + o.getWhitePoints()
                );
            }
        }

        PlayerColor winner = o.checkWinner();
        if (winner == PlayerColor.BLACK) {
            status.setText(
                    "BLACK wins!!! | B: " + o.getBlackPoints() + ", W: " + o.getWhitePoints()
            );
            tempEventStatus = EventStatusEnum.WIN_BLACK; // win event declared
        } else if (winner == PlayerColor.WHITE) {
            status.setText(
                    "WHITE wins!!! | B: " + o.getBlackPoints() + ", W: " + o.getWhitePoints()
            );
            tempEventStatus = EventStatusEnum.WIN_WHITE; // win event declared
        } else if (winner == PlayerColor.EMPTY) {
            status.setText("It's a tie | B: " + o.getBlackPoints() + ", W: " + o.getWhitePoints());
            tempEventStatus = EventStatusEnum.TIE; // tie event declared
        }
    }

    // called after a pass event to reset the status bar
    public void passStatus() {
        if (o.getCurrentTurn() == PlayerColor.BLACK) {
            status.setText(
                    "BLACK's Turn | B: " + o.getBlackPoints() + ", W: " + o.getWhitePoints()
            );
        } else {
            status.setText(
                    "WHITE's Turn | B: " + o.getBlackPoints() + ", W: " + o.getWhitePoints()
            );
        }
    }

    /**
     * Draws the game board.
     * 
     * There are many ways to draw a game board. This approach
     * will not be sufficient for most games, because it is not
     * modular. All of the logic for drawing the game board is
     * in this method, and it does not take advantage of helper
     * methods. Consider breaking up your paintComponent logic
     * into multiple methods or classes, like Mushroom of Doom.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);

        // Draws board grid
        for (int i = 75; i < 600; i += 75) {
            g.drawLine(i, 0, i, 600);
        }

        for (int i = 75; i < 600; i += 75) {
            g.drawLine(0, i, 600, i);
        }

        // dots that are sometimes on online boards
        // used simply as a reference point and serve no gameplay purpose
        for (int i = 145; i < 600; i += 300) {
            for (int j = 145; j < 600; j += 300) {
                g.fillOval(i, j, 10, 10);
            }
        }

        // Draws disks and potential moves
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                PlayerColor disk = o.getBoardSpace(j, i);
                if (disk == null) {
                    continue;
                }
                g.setColor(Color.black);
                if (disk == PlayerColor.BLACK) {
                    g.setColor(Color.BLACK);
                } else if (disk == PlayerColor.WHITE) {
                    g.setColor(Color.WHITE);

                }
                if (disk != PlayerColor.EMPTY) {
                    g.fillOval(75 * j + 5, 75 * i + 5, 65, 65);
                } else {
                    // potential moves are highlighted in player color
                    if (o.getCurrentTurn() == PlayerColor.BLACK) {
                        g.setColor(Color.BLACK);
                    } else {
                        g.setColor(Color.WHITE);
                    }
                    g.drawOval(75 * j + 5, 75 * i + 5, 65, 65);
                }
            }
        }
    }

    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }
}
