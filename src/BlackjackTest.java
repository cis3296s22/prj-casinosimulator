import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.event.InputEvent;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BlackjackTest {
    public MainMenuModel mainMenuModel;
    public MainMenuView mainMenuView;
    public BlackjackModel blackjackModel;
    public BlackjackView blackjackView;

    public Robot bot;

    @Before
    public void setup() throws AWTException {
        mainMenuModel = new MainMenuModel();
        mainMenuView = mainMenuModel.getView();
        mainMenuView.getGameOptions().setSelectedIndex(0);
        bot = new Robot();
        bot.mouseMove(844, 439);
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        blackjackModel = mainMenuModel.getModel();
        blackjackView = blackjackModel.getView();
    }

    @Test
    public void deckHas52Cards() {
        assertEquals("Should have 52 cards in the deck.", 52, blackjackModel.getDeckCount());
    }

    @Test
    public void afterDealPlayerHas2Cards() {
        blackjackView.getDealBtn().doClick();
        while (!blackjackModel.isDoneDeal()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }
        assertEquals("Player should have 2 cards in hand.", 2, blackjackModel.getPlayerCardCount());
    }

    @Test
    public void afterDealDealerHas2Cards() {
        blackjackView.getDealBtn().doClick();
        while (!blackjackModel.isDoneDeal()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }
        assertEquals("Dealer should have 2 cards in hand.", 2, blackjackModel.getDealerCardCount());
    }

    @Test
    public void bet5Dollars() throws AWTException {
        Point p = blackjackView.getLocationOnScreen();

        try { Thread.sleep(1000); }
        catch (InterruptedException e) {}
        bot.mouseMove(p.x + 59, p.y + 559);
        //bot.mouseMove(479, 592);
        // click twice to make sure it registers
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        assertEquals("Amount bet should be $5.", 5, blackjackModel.getBet());
    }

    @Test
    public void playerHitShouldIncreaseTotal () {
        int prev_points, curr_points ;

        blackjackView.getDealBtn().doClick();
        while (!blackjackModel.isDoneDeal()) {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
        prev_points = blackjackModel.getPlayerTotal();

        blackjackView.getHitBtn().doClick();
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        curr_points = blackjackModel.getPlayerTotal();

        assertEquals("Total points should be greater than before hitting.", true, curr_points > prev_points);
    }

    @Test
    public void cardAddedAfterHit() {
        blackjackView.getDealBtn().doClick();
        while (!blackjackModel.isDoneDeal()) {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
        blackjackView.getHitBtn().doClick();
        try { Thread.sleep(100); } catch (InterruptedException e) {}

        assertEquals("After hit player should have 3 cards.", 3, blackjackModel.getPlayerCardCount());
    }

    @Test
    public void afterLossLoseMoney(){
        int temp = blackjackModel.getBalance();

        Point p = blackjackView.getLocationOnScreen();

        try { Thread.sleep(1000); }
        catch (InterruptedException e) {}
        bot.mouseMove(p.x + 59, p.y + 559);
        //bot.mouseMove(479, 592);
        // click twice to make sure it registers
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

        blackjackView.getDealBtn().doClick();
        while (!blackjackModel.isDoneDeal()) {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }

        while(blackjackModel.getPlayerTotal() <= 21) {
            blackjackView.getHitBtn().doClick();
            try {Thread.sleep(100);} catch (InterruptedException e) {}
        }
        assertTrue("player should have less money after losing",blackjackModel.getBalance() < temp);
    }

    @Test
    public void playerStandMeansDealerTurn(){
        blackjackView.getDealBtn().doClick();
        while (!blackjackModel.isDoneDeal()) {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }

        blackjackView.getStandBtn().doClick();
        try { Thread.sleep(100); } catch (InterruptedException e) {}

        assertEquals("should be dTurn", "dTurn", blackjackModel.getCurrentState().toString());
        try { Thread.sleep(100); } catch (InterruptedException e) {}
    }

    @Test
    public void dealerTurnEndsGame(){
        blackjackView.getDealBtn().doClick();
        while (!blackjackModel.isDoneDeal()) {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
        blackjackView.getStandBtn().doClick();
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        assertNotEquals("Someone should win game", "dTurn", blackjackModel.getCurrentState().toString());
    }

    @Test
    public void dealerStandsAt17() {
        blackjackModel.dealCardTest(true, false, 4, 0, 4);
        blackjackModel.dealCardTest(true, false, 5, 0, 5);
        blackjackModel.dealCardTest(false, false, 9, 0, 9);
        blackjackModel.dealCardTest(false, true, 8, 0, 8);
        blackjackView.getStandBtn().setEnabled(true);
        blackjackView.ShowBetLabels(false);
        blackjackView.hideChipValues();
        blackjackView.getStandBtn().doClick();
        assertEquals("Dealer's points should remain at 17.", 17, blackjackModel.getDealerTotal());
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
    }

    @Test
    public void dealerStandsIfTotalOver17() {
        blackjackModel.dealCardTest(true, false, 4, 0, 4);
        blackjackModel.dealCardTest(true, false, 5, 0, 5);
        blackjackModel.dealCardTest(false, false, 9, 0, 9);
        blackjackModel.dealCardTest(false, true, 9, 1, 9);
        blackjackView.getStandBtn().setEnabled(true);
        blackjackView.ShowBetLabels(false);
        blackjackView.hideChipValues();
        blackjackView.getStandBtn().doClick();
        assertEquals("Dealer's points should remain at 18.", 18, blackjackModel.getDealerTotal());
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
    }

    @Test
    public void dealerHitsIfTotalLessThan17() {
        blackjackModel.dealCardTest(true, false, 4, 0, 4);
        blackjackModel.dealCardTest(true, false, 5, 0, 5);
        blackjackModel.dealCardTest(false, false, 9, 0, 9);
        blackjackModel.dealCardTest(false, true, 2, 1, 2);
        blackjackView.getStandBtn().setEnabled(true);
        blackjackView.ShowBetLabels(false);
        blackjackView.hideChipValues();
        blackjackView.getStandBtn().doClick();
        while (!blackjackModel.isDoneStand())
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        // if standing is false it means that the dealer hit, stood and won and the reset function was called
        assertEquals("Dealer should hit then stand.", false, blackjackModel.dealerIsStanding());
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
    }

    @Test
    public void dealerHitsBlackjack() {
        blackjackModel.dealCardTest(true, false, 4, 0, 4);
        blackjackModel.dealCardTest(true, false, 5, 0, 5);
        blackjackModel.dealCardTest(false, false, 1, 0, 11);
        blackjackModel.dealCardTest(false, true, 10, 1, 10);
        blackjackView.getStandBtn().setEnabled(true);
        blackjackView.ShowBetLabels(false);
        blackjackView.hideChipValues();
        blackjackView.getStandBtn().doClick();
        while (!blackjackModel.isDoneStand())
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        assertEquals("Dealer hits blackjack.", "dWin", blackjackModel.getCurrentState().toString());
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
    }

    @Test
    public void playerHitsBlackjack() {
        blackjackModel.dealCardTest(true, false, 1, 0, 11);
        blackjackModel.dealCardTest(true, false, 10, 0, 10);
        blackjackModel.dealCardTest(false, false, 2, 0, 2);
        blackjackModel.dealCardTest(false, true, 10, 1, 10);
        blackjackView.getStandBtn().setEnabled(true);
        blackjackView.ShowBetLabels(false);
        blackjackView.hideChipValues();
        blackjackView.getStandBtn().doClick();
        while (!blackjackModel.isDoneStand())
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        assertEquals("Player hits blackjack.", "pWin", blackjackModel.getCurrentState().toString());
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
    }

    @Test
    public void nobodyWins() {
        blackjackModel.dealCardTest(true, false, 1, 0, 11);
        blackjackModel.dealCardTest(true, false, 10, 0, 10);
        blackjackModel.dealCardTest(false, false, 1, 1, 11);
        blackjackModel.dealCardTest(false, true, 10, 1, 10);
        blackjackView.getStandBtn().setEnabled(true);
        blackjackView.ShowBetLabels(false);
        blackjackView.hideChipValues();
        blackjackView.getStandBtn().doClick();
        while (!blackjackModel.isDoneStand())
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        assertEquals("It's a draw.", "draw", blackjackModel.getCurrentState().toString());
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
    }
}
