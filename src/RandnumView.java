import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.util.Objects.isNull;

public class RandnumView extends JFrame {
    private RandnumModel randnumModel;

    private JLabel balanceLabel;
    private JLabel betLabel;
    private JTextField betField;
    private JTextField guessField;
    private JButton jButton;
    private JButton jButtonExit;
    private JLabel feedbackLabel;

    public RandnumView(RandnumModel model) {
        super("Guess the Number Game");
        this.randnumModel = model;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));


        balanceLabel = new JLabel("Balance: $" + randnumModel.getMoney());
        balanceLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align to the left
        add(balanceLabel);

        betLabel = new JLabel("Current Bet:");
        balanceLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align to the left
        add(betLabel);

        betField = new JTextField(10);
        add(betField);

        feedbackLabel = new JLabel("");
        balanceLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align to the left
        add(feedbackLabel);

        jButton = new JButton("Bet");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (randnumModel.getGameState() != RandnumModel.currentState.WaitingForBet)
                    try {
                        int guess = Integer.parseInt(betField.getText());
                        betField.setText("");
                        int result = randnumModel.makeGuess(guess);
                        if(result == 1) {
                            updateGameView();
                        }
                        else {
                            if (result == 3)
                            {
                                feedbackLabel.setText("The number is too Low.");
                            }
                            if(result == 2)
                            {
                                feedbackLabel.setText("The number is too High.");
                            }

                        }
                    } catch (NumberFormatException ex) {
                        feedbackLabel.setText("Please enter a valid number.");
                    }
                else {
                    try {
                        int bet = Integer.parseInt(betField.getText());
                        randnumModel.startGame(bet);
                        updateGameView();
                        jButton.setText("Guess");
                        betLabel.setText("Take a guess(1-100):");
                        } catch (NumberFormatException ex) {
                        feedbackLabel.setText("Please enter a valid number.");
                    }
                    betField.setText("");
                }

            }
        });
        add(jButton);
        jButtonExit = new JButton("Exit");
        jButtonExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                randnumModel.exit();
            }
        });
        add(jButtonExit);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void updateGameView() {
        balanceLabel.setText("Balance: $" + randnumModel.getMoney());

        RandnumModel.currentState gameState = randnumModel.getGameState();
        switch (gameState) {
            case WaitingForBet:
                jButton.setText("Bet");
                betLabel.setText("Current Bet:");
                break;
            case WaitingForGuess:

                feedbackLabel.setText("Make your guess!");
                break;
            case PlayerWon:
                feedbackLabel.setText("You won! Start a new game?");
                break;
            case PlayerLost:

                feedbackLabel.setText("You lost! Try again?");
                break;
            case GameOver:

                feedbackLabel.setText("Game over! Reload to play again.");
                break;
        }
    }

    public void showInsufficientFundsMessage() {
        JOptionPane.showMessageDialog(this, "Insufficient funds to place bet.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showGameOverMessage() {
        JOptionPane.showMessageDialog(this, "Game over! You've run out of money.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

}
