import java.util.Collections;
import java.util.Random;

public class RandnumModel {
    private MainMenuModel menuModel;
    private RandnumView randnumView;
    private int money;
    private int bet;
    private int secretNumValue;
    private int guessCount;
    private currentState gameState;

    public enum currentState {
        WaitingForBet, WaitingForGuess, PlayerWon, PlayerLost, GameOver
    }

    public RandnumModel(MainMenuModel menu, int money) {
        this.menuModel = menu;
        this.money = money;
        this.bet = 0;
        this.gameState = currentState.WaitingForBet;
        this.randnumView = new RandnumView(this); // This line sets up the view, make sure RandnumView is ready for this.
    }

    public int createRandomNumber() {
        return new Random().nextInt(100) + 1;
    }


    public void startGame(int bet) {
        if (money >= bet) {
            this.bet = bet;
            money -= bet;
            secretNumValue = createRandomNumber();
            guessCount = 0;
            gameState = currentState.WaitingForGuess;
            randnumView.updateGameView();
        } else {
            // Handle case where player doesn't have enough money
            randnumView.showInsufficientFundsMessage();
        }
    }

    public int makeGuess(int guess) {

        guessCount++;
        if (guess == secretNumValue) {
            money += bet * 2; // Player wins double their bet
            gameState = currentState.PlayerWon;
            System.out.println("Player won!");
            randnumView.updateGameView();
            endGame();
            return 1;
        } else if (guessCount >= 5) {
            gameState = currentState.PlayerLost; // Player loses after 3 incorrect guesses
            // Game ends, either win or lose
            System.out.println("Player lost!");
            randnumView.updateGameView();
            endGame();
            return 0;
        } else {
            // Keep gameState as WaitingForGuess and inform the player if the guess is too high or too low
            if(guess < secretNumValue) {return 3;}
            return 2;

        }

    }

    private void endGame() {
        bet = 0; // Reset the bet
        // Optionally reset for a new game or update UI to allow for a new game to start
        if (money <= 0) {
            gameState = currentState.GameOver;
            // Handle game over scenario
            randnumView.showGameOverMessage();
        } else {
            gameState = currentState.WaitingForBet;
            // Prepare for new game if needed
        }
        randnumView.updateGameView(); // This should update the game's UI based on the new game state

    }

    // Add getters for view to use for updates
    public int getMoney() {
        return money;
    }


    public currentState getGameState() {
        return gameState;
    }

    public void exit() {
        MainMenuModel.money = money; // Update main menu's money value
        menuModel.setVisible();
    }
}
