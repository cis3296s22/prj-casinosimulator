import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

/**
 * BlackjackModel class.
 * Implements the logic of the blackjack game.
 */
public class BlackjackModel {
    /**
     * Current state of the game.
     */
    private currentState currentState;

    private MainMenuModel menuModel;
    private BlackjackView blackjackView;
    /**
     * The deck of cards represented as a linked list.
     */
    private LinkedList<Card> deck;
    /**
     * Player object that represents the dealer.
     */
    private Player dealer;
    /**
     * Player object that represents the player.
     */
    private Player player;

    /**
     * Current balance.
     */
    private int money;
    /**
     * Current bet.
     */
    private int bet;

    private int cards_played; // how many cards were removed from the deck so far
    private int cardsPerRound; //counts number of cards played per round

    private int[] hidden_card; // dealer's hidden card

    public enum currentState{
        pWin, dWin, pStand, dStand, dTurn, pTurn, draw //d = dealer, p = player
    }

    /**
     * gets current state of the game
     * @return current state
     */
    public currentState getCurrentState(){
        return currentState;
    }

    // FOR TESTING
    private boolean doneDeal;
    private boolean doneHit;
    private boolean doneStand;
    private int hitCount = 0;

    /**
     * Constructor.
     * Initializes the necessary variables needed to start the game.
     * @param menu A reference to the MainMenuModel object.
     * @param money An integer representing the amount of money the player currently has.
     */
    public BlackjackModel(MainMenuModel menu, int money) {
        currentState = currentState.pTurn;
        menuModel = menu;
        deck = new LinkedList<>();
        dealer = new Player();
        player = new Player();
        blackjackView = new BlackjackView(this);
        this.money = money;
        bet = 0;
        cards_played = 0;

        blackjackView.UpdateBalance();
        createDeck();
        shuffle();
    }

    /**
     * createDeck method.
     * Create the deck; a linked list of 52 Cards of each suit is initialized.
     */
    public void createDeck() {
        deck.clear();
        int suit; // 0 - clubs; 1 - diamonds; 2 - hearts; 3 - spades
        int points = 0; // point value for each card

        for (int i = 0; i < 4; i++) {
            suit = i;
            for (int j = 0; j < 13; j++) {
                points = j + 1; // 2 to 9 are face value
                if (j >= 10) // 10, jack, queen, king are 10 points
                    points = 10;
                Card c = new Card(j+1, suit, points); // first card is Ace
                deck.add(c);
            }
        }
    }

    /**
     * shuffle method.
     * Shuffle the deck according to the difficulty level.
     */
    public void shuffle() {
        Random randomNumber = new Random();
        Card ace = new Card(1, 0, 1);
        Collections.shuffle(deck);
        if(MainMenuView.difficulty == "Medium") {
            int index = randomNumber.nextInt(8);
            if(index == 0) {//If you pass a 1/8 chance
                for(int i = 0; i < 52; i++) {
                    if(deck.get(i).value == 1) {
                        ace = deck.get(i);
                    }
                }
                int indexAce = deck.indexOf(ace);
                Card firstCard = deck.getFirst();
                deck.set(indexAce, firstCard); //Swap the first card with the ace
                deck.set(0, ace);
            }
        }
        else if (MainMenuView.difficulty == "Easy") { //If the difficulty is easy
            int index = randomNumber.nextInt(6);
            if(index == 0) {//If you pass a 1/6 chance
                for(int i = 0; i < deck.size(); i++) {
                    if(deck.get(i).value == 1) {
                        ace = deck.get(i);
                    }
                }
                int indexAce = deck.indexOf(ace);
                Card firstCard = deck.getFirst();
                deck.set(indexAce, firstCard); //Swap the first card with the ace
                deck.set(0, ace);
            }
        }
    }

    /**
     * playerHit method.Mark
     * The player is dealt another card from the deck; checks if the player won.
     */
    public void playerHit() {
        doneHit = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                player.addCard();
                int[] c = popPlayerCard();
                blackjackView.ShowPlayerCard(c);

                if (player.isBlackjack()) {
                    currentState = currentState.pWin; //player wins
                    blackjackView.ShowDealerCard(hidden_card);
                    blackjackView.ShowBackCard(false);
                    playerWins();
                    win();
                }
                else if (player.hasBusted()) {
                    currentState = currentState.dWin; //dealer wins
                    blackjackView.ShowDealerCard(hidden_card);
                    blackjackView.ShowBackCard(false);
                    playerLoses();
                    win();
                }
                //else {
                //currentState = currentState.dTurn;
                //}
                hitCount++;
                doneHit = true;
                System.out.println(hitCount);
            }
        }).start();
    }

    /**
     * playerStand method.
     * The playre gives up his turn and the dealer takes over.
     */
    public void playerStand() {
        //player stands which makes it the dealer's turn
        doneStand = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                currentState = currentState.dTurn;
                player.setToStanding(true);
                blackjackView.ShowDealerCard(hidden_card);
                blackjackView.ShowBackCard(false);
                dealerTurn();
                doneStand = true;
            }
        }).start();
    }

    /**
     * doubleDown method.
     * The player is dealt one last card and the current bet is doubled.
     * Checks if the player won; if not the player stands.
     */
    public void doubleDown() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //money = money - bet;
                //bet = bet + bet;
                playerBet(bet);

                player.addCard();
                int[] c = popPlayerCard();
                blackjackView.ShowPlayerCard(c);

                if (player.isBlackjack()) {
                    currentState = currentState.pWin; //player wins
                    blackjackView.ShowDealerCard(hidden_card);
                    blackjackView.ShowBackCard(false);
                    playerWins();
                    win();
                }
                else if (player.hasBusted()) {
                    currentState = currentState.dWin; //dealer wins
                    blackjackView.ShowDealerCard(hidden_card);
                    blackjackView.ShowBackCard(false);
                    playerLoses();
                    win();
                }
                else playerStand();
            }
        }).start();
    }

    /**
     * dealerHit method.
     * The dealer is dealt another card from the deck; checks if the dealer won.
     */
    public void dealerHit() {
        dealer.addCard();
        int[] c = popDealerCard();
        blackjackView.ShowDealerCard(c);

        if(dealer.isBlackjack()) {
            currentState = currentState.dWin; //dealer has blackjack and wins
            playerLoses();
            win();
        }
        else if(dealer.hasBusted()) {
            currentState = currentState.pWin; //dealer busted and player wins
            playerWins();
            win();
        }
        else if (player.isStanding()) { //dealer neither has blackjack nor busted and loops back to check if at 17
            dealerTurn();
        }
//        else {
//            currentState = currentState.pTurn;
//        }
    }

    /**
     * dealerStand method.
     * Dealer gives up his turn; checks who won.
     */
    public void dealerStand() {
        currentState = currentState.pTurn;
        dealer.setToStanding(true);                         // if both player and dealer are standing then do this
        if (player.isStanding()) {
            if (player.getTotal() == dealer.getTotal()) {     //player and dealer have same total, draw
                currentState = currentState.draw;
                playerDraws();
            }
            else if (player.getTotal() < dealer.getTotal()) {     //dealer has higher total than player, dealer wins
                currentState = currentState.dWin;
                playerLoses();
            }
            else {                                               //otherwise player has higher total and wins
                currentState = currentState.pWin;
                playerWins();
            }
            win();
        }
    }

    /**
     * dealerTurn method.
     * Calls dealerHit if dealer's total points are less than 17; otherwise
     * calls dealerStand.
     */
    public void dealerTurn() {               //dealer's turn
        if (dealer.getTotal() < 17) {        //if dealer has less than 17 they hit
            dealerHit();
        }
        else                                //otherwise they stand
            dealerStand();
    }

    /**
     * win method.
     * Update BlackjackView, reset players and deck.
     */
    public void win() {
        switch (currentState) {
            case pWin -> blackjackView.ShowPlayerWin();
            case dWin -> blackjackView.ShowDealerWin();
            case draw -> { currentState = currentState.draw; blackjackView.ShowDraw(); }
        }

        dealer.reset();
        player.reset();
        createDeck();
        shuffle(); //Shuffle the deck after every turn
    }

    /**
     * playerWins method.
     * Update bet and balance in BlackjackView, reset bet.
     */
    public void playerWins(){
        money = money + (bet * 2);
        bet = 0;
        blackjackView.UpdateBalance();
        blackjackView.UpdateBet();
        cardsPerRound = 0;
    }

    /**
     * playerLoses method.
     * Update bet and balance in BlackjackView.
     */
    public void playerLoses(){
        bet = 0;
        blackjackView.UpdateBalance();
        blackjackView.UpdateBet();
        cardsPerRound = 0;
    }

    /**
     * playerDraws method.
     * Update bet and balance in BlackjackView, reset bet.
     */
    public void playerDraws(){
        money = money + bet;
        bet = 0;
        blackjackView.UpdateBalance();
        blackjackView.UpdateBet();
        cardsPerRound = 0;
    }

    /**
     * playerBet method.
     * Player places a bet.
     */
    public void playerBet(int bet) {
        if (money >= bet) {
            this.bet += bet;
            money -= bet;
            blackjackView.UpdateBalance();
            blackjackView.UpdateBet();
        }
    }

    /**
     * deal method.
     * Initial deal of cards. The order is player, dealer, player, dealer.
     * The dealer's last card is face down.
     */
    public void deal() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                doneDeal = false; // for testing
                int[] c;
                for (int i = 0; i < 4; i++) {
                    if (i % 2 == 0) {
                        player.addCard();
                        c = popPlayerCard();
                        blackjackView.ShowPlayerCard(c);
                    }
                    else {
                        dealer.addCard();
                        c = popDealerCard();
                        if (i < 3)
                            blackjackView.ShowDealerCard(c);
                        else { // dealer's last card is face down
                            hidden_card = c;
                            blackjackView.ShowBackCard(true);
                        }
                    }
                }
                doneDeal = true; // for testing
                if (player.isBlackjack()) {
                    currentState = currentState.pWin; //player wins
                    blackjackView.ShowDealerCard(hidden_card);
                    blackjackView.ShowBackCard(false);
                    playerWins();
                    win();
                }
            }
        }).start();
    }

    /**
     * popDealerCard method.
     * Get dealer's top card in hand.
     * @return An integer array containing the card's suit, value, and points.
     */
    int[] popDealerCard() {
        int[] ret = new int[3];
        Card c = dealer.popCard();

        ret[0] = c.suit;
        ret[1] = c.value-1;
        ret[2] = c.points;

        return ret;
    }

    /**
     * popPlayerCard method.
     * Get player's top card in hand.
     * @return An integer array containing the card's suit, value, and points.
     */
    int[] popPlayerCard() {
        int[] ret = new int[3];
        Card c = player.popCard();

        ret[0] = c.suit;
        ret[1] = c.value-1; // subtract 1 because index of array starts at 0
        ret[2] = c.points;

        return ret;
    }

    /**
     * getDealerTotal method.
     * @return The dealer's total points as an integer.
     */
    public int getDealerTotal() { return dealer.getTotal(); }
    /**
     * getPlayerTotal method.
     * @return The player's total points as an integer.
     */
    public int getPlayerTotal() { return player.getTotal(); }
    /**
     * getBalance method.
     * @return The current balance as an integer.
     */
    public int getBalance() { return money; }
    /**
     * getBet method.
     * @return The current bet as an integer.
     */
    public int getBet() { return bet; }

    /**
     * exit method.
     * Update balance and quit game.
     */
    public void exit() {
        MainMenuModel.money = money;
        menuModel.setVisible();
    }

    // DEBUG
    public void printDeck() {
        deck.forEach(element -> System.out.println(element.value + " " + element.suit + "\n"));
    }

    /**
     * Card class.
     * Used to define a card in the deck.
     */
    private class Card {
        int value;
        int suit;
        int points;

        /**
         * Constructor.
         * Initialize a card object.
         * @param v The face value of the card.
         * @param s The suit of the card.
         * @param p The number of points for the card.
         */
        Card(int v, int s, int p) {
            value = v;
            suit = s;
            points = p;
        }

        /**
         * Constructor.
         * Initialize a card object.
         * @param c A card object.
         */
        Card(Card c) {
            value = c.value;
            suit = c.suit;
            points = c.points;
        }
    }

    /**
     * Player class.
     * Used to define a player entity.
     */
    private class Player {
        Card[] hand;
        int i; // hand index
        int j; // index for popping
        int total;
        private boolean stand;

        /**
         * Constructor.
         * Initialize a player object; creates a hand which is
         * an array of Cards.
         */
        Player() {
            stand = false;
            hand = new Card[10]; // max of 10 possible cards should be enough
            i = j = total = 0;
        }

        /**
         * addCard method.
         * Add a card to the hand; if all cards are dealt reset the deck.
         */
        void addCard() {
            // if all cards are dealt create a new deck and shuffle before continuing
            //int cardCount = 0;
            if (cards_played == 52) {
                createDeck();
                shuffle();
                cards_played = 0;
            }
            Card c = new Card(deck.pollFirst()); // get top card from deck
            cards_played++;
            hand[i++] = c;
            if (c.points == 1 && total < 11) { //if card is ace add 10 points as long as it won't cause player to bust
                total += c.points + 10;
            } else {                           // otherwise all cards are normal values
                total += c.points;
            }
            try {
                playDealSound();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

         /* For testing!!!
         * Add a specific card to the hand.
         */
        void addCard(int value, int suit, int points) {
            Card c = new Card(value, suit, points);
            cards_played++;
            hand[i++] = c;
            total += points;
        }

        /**
         * getHandCount method.
         * @return The size of the hand as an integer.
         */
        public int getHandCount() { return i; }

        /**
         * hasBusted method.
         * @return True if the total points are over 21.
         */
        public boolean hasBusted() { return total > 21; }

        /**
         * isBlackjack method.
         * @return True if the total points add up to 21.
         */
        public boolean isBlackjack() { return total == 21; }

        /**
         * isStanding method.
         * @return True is player is standing.
         */
        public boolean isStanding() { return stand; }

        /**
         * setToStanding method.
         * @param stand Boolean variable; set to true if the player stands.
         */
        public void setToStanding(boolean stand) { this.stand = stand; }

        /**
         * popCard method.
         * @return The top Card object in the hand.
         */
        Card popCard() { return hand[j++]; }

        /**
         * getTotal method.
         * @return Total points as an integer.
         */
        int getTotal() { return total; }

        /**
         * reset method.
         * Resets the player's points and hand.
         */
        void reset() {
            i = j = total = 0;
            stand = false;
        }
    }
  
    /**
     * playDealSound method.
     * Plays a wav file when a card is dealt.
     * @throws LineUnavailableException
     * @throws UnsupportedAudioFileException
     * @throws IOException
     */
    public void playDealSound() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        String path = System.getProperty("user.dir");
        File audioFile = new File(path + "/Sounds/CardSound.wav").getAbsoluteFile();
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        //Plays audio once
        clip.start();
    }

    /*
     * For testing
     */
    public int getDeckCount() { return deck.size(); }
    public int getPlayerCardCount() { return player.getHandCount(); }
    public int getDealerCardCount() { return dealer.getHandCount(); }
    public BlackjackView getView() { return blackjackView; }
    public boolean isDoneDeal() { return doneDeal; }
    public boolean isDoneHit() { return doneHit; }
    public boolean isDoneStand() { return doneStand; }
    public int getHitCount() { return hitCount; }
    public boolean dealerIsStanding() { return dealer.isStanding(); }

    /*
     * Deal a chosen card for testing purposes.
     * If p is set to true it will deal to the player; otherwise to the dealer.
     * If hidden is true it will show the hidden card; used to follow the normal course of the game.
     */
    public void dealCardTest(boolean p, boolean hidden, int value, int suit, int points) {
        doneDeal = false;
        if (p) { // if p is true deal to player; otherwise deal to dealer
            player.addCard(value, suit, points);
            int[] c = popPlayerCard();
            blackjackView.ShowPlayerCard(c);
        }
        else {
            dealer.addCard(value, suit, points);
            int[] c = popDealerCard();
            if (hidden) {
                hidden_card = c;
                blackjackView.ShowBackCard(true);
            }
            else blackjackView.ShowDealerCard(c);
        }
    }
}
