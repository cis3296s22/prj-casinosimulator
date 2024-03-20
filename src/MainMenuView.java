import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * MainMenuView class.
 * Implements the UI of the main menu.
 * The GUI is used to launch the casino games.
 */
public class MainMenuView extends JFrame {
    private MainMenuModel menuModel;
    /**
     * stores the current gamemode
     */
    public static String gamemode;

    private JComboBox<String> gamemodeSelect;

    /**
     * stores the current difficulty
     */
    public static String difficulty;
    private JLabel background;
    private JLabel blackjackMenu;
    private JLabel randnumMenu;
    private JLabel slotMenu;
    private JLabel randnumText;
    private JLabel blackjackText;
    private JLabel slotText;
    private JLabel saveButton;
    private JLabel saveText;
    private JLabel helpButton;
    private JLabel helpText;
    private JLabel difficultyText;
    private JLabel moneyTotal;
    private JLabel gameRules;
    private JLabel gamemodeHelp;
    private JLabel difficultyHelp;

    /**
     * Constructor.
     * Initializes all the elements of the UI; assigns code for button interactions.
     * @param menu A reference to the MainMenuModel object.
     */
    public MainMenuView(MainMenuModel menu) {
        super("CasinoSimulator");
        menuModel = menu;
        setLayout(null);

        setSize(650, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        //gamemode dropdown code
        JLabel gamemodeTxt = new JLabel();
        gamemodeTxt.setBounds(20, 0, 130, 60);
        gamemodeTxt.setText("Gamemode: ");
        gamemodeTxt.setForeground(Color.WHITE);
        gamemodeTxt.setFont(new Font("Dialog", Font.BOLD, 16));
        String[] playOptions = {"Simulated Casino", "Freeplay"};
        gamemodeSelect = new JComboBox<>(playOptions);
        gamemodeSelect.setBounds(120, 0, 160, 60);

        //difficulty dropdown code
        String[] difficultyOptions = {"Hard", "Medium", "Easy"};
        JComboBox<String> difficultySelect = new JComboBox<>(difficultyOptions);
        difficultySelect.setBounds(120, 60, 140, 60);
        difficultySelect.setVisible(true);

        LoadAssets();

        //blackjack label
        blackjackText = new JLabel("Blackjack");
        blackjackText.setForeground(Color.WHITE);
        blackjackText.setBounds(400, 215, 80, 50);
        blackjackText.setFont(new Font("Dialog", Font.BOLD, 16));
        blackjackText.setVisible(false);

        //slot machine text
        slotText = new JLabel("Slot Machine");
        slotText.setForeground(Color.WHITE);
        slotText.setBounds(146, 215, 110, 50);
        slotText.setFont(new Font("Dialog", Font.BOLD, 16));
        slotText.setVisible(false);

        //randnum label
        randnumText = new JLabel("Random Number");
        randnumText.setForeground(Color.WHITE);
        randnumText.setBounds(270, 560, 130, 50);
        randnumText.setFont(new Font("Dialog", Font.BOLD, 16));
        randnumText.setVisible(false);

        //save text
        saveText = new JLabel("Save");
        saveText.setForeground(Color.WHITE);
        saveText.setBounds(560, 65, 80, 20);
        saveText.setFont(new Font("Dialog", Font.BOLD, 16));
        saveText.setVisible(false);

        //rules text
        helpText = new JLabel("Rules");
        helpText.setForeground(Color.WHITE);
        helpText.setBounds(556, 548, 80, 20);
        helpText.setFont(new Font("Dialog", Font.BOLD, 16));
        helpText.setVisible(false);

        //difficulty text
        difficultyText = new JLabel("Difficulty:");
        difficultyText.setForeground(Color.WHITE);
        difficultyText.setBounds(20, 60, 100, 60);
        difficultyText.setFont(new Font("Dialog", Font.BOLD, 16));

        //total balance text
        //MainMenuModel.getMoney();
        moneyTotal = new JLabel("Total balance: $" + MainMenuModel.getMoney());
        moneyTotal.setForeground(Color.WHITE);
        moneyTotal.setBounds(10, 570, 300, 50);
        moneyTotal.setFont(new Font("Dialog", Font.BOLD, 20));

        //updates money value when player mouses over background
        background.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                moneyTotal.setText("Total balance: $" + MainMenuModel.getMoney());
            }
        });

        //save button code
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                menuModel.save();
            }
        });

        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                saveText.setVisible(true);
            }
            public void mouseExited(MouseEvent e){
                super.mouseExited(e);
                saveText.setVisible(false);
            }
        });

        //Help Button Code
        helpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                helpText.setVisible(true);

            }
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                helpText.setVisible(false);
            }
        });

        helpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                rulesBox();
            }
        });

        //slot machine code
        //code to click slot image
        slotMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                gamemode = gamemodeSelect.getItemAt(gamemodeSelect.getSelectedIndex());
                difficulty = difficultySelect.getItemAt(difficultySelect.getSelectedIndex());
                if(MainMenuModel.getMoney() <= 5 && gamemode == "Simulated Casino"){   //if player has no money and wants to play simulated casino, add 100 and notify
                    MainMenuModel.addMoney();
                    infoBox("$100 has been added", "You Ran Out of Money!");
                    setVisible(false);
                    menuModel.startSlot();
                }
                else{
                    gamemode = gamemodeSelect.getItemAt(gamemodeSelect.getSelectedIndex());
                    difficulty = difficultySelect.getItemAt(difficultySelect.getSelectedIndex());
                    setVisible(false);
                    menuModel.startSlot();
                }
            }
        });

        //code for rollover effect on slot image
        slotMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                slotText.setVisible(true);
                try {
                    playSlotClip();
                } catch (LineUnavailableException ex) {
                    ex.printStackTrace();
                } catch (UnsupportedAudioFileException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                slotText.setVisible(false);
            }
        });

        //randnum code
        //Cards when clicked now start randnum game
        randnumMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                gamemode = gamemodeSelect.getItemAt(gamemodeSelect.getSelectedIndex());
                difficulty = difficultySelect.getItemAt(difficultySelect.getSelectedIndex());
                if(MainMenuModel.getMoney() < 5 && gamemode == "Simulated Casino") { //if player has no money and wants to play simulated casino, add 100 and notify
                    MainMenuModel.addMoney();
                    infoBox("$100 has been added", "You Ran Out of Money!");
                    setVisible(false);
                    menuModel.startRandnum();
                }
                else {
                    gamemode = gamemodeSelect.getItemAt(gamemodeSelect.getSelectedIndex());
                    difficulty = difficultySelect.getItemAt(difficultySelect.getSelectedIndex());
                    setVisible(false);
                    menuModel.startRandnum();
                }
            }
        });

        //code for rollover effect on randnum card image
        randnumMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                randnumText.setVisible(true);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                randnumText.setVisible(false);
            }
        });
        //blackjack code
        //Cards when clicked now start blackjack game
        blackjackMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                gamemode = gamemodeSelect.getItemAt(gamemodeSelect.getSelectedIndex());
                difficulty = difficultySelect.getItemAt(difficultySelect.getSelectedIndex());
                if(MainMenuModel.getMoney() < 5 && gamemode == "Simulated Casino") { //if player has no money and wants to play simulated casino, add 100 and notify
                    MainMenuModel.addMoney();
                    infoBox("$100 has been added", "You Ran Out of Money!");
                    setVisible(false);
                    menuModel.startBlackjack();
                }
                else {
                    gamemode = gamemodeSelect.getItemAt(gamemodeSelect.getSelectedIndex());
                    difficulty = difficultySelect.getItemAt(difficultySelect.getSelectedIndex());
                    setVisible(false);
                    menuModel.startBlackjack();
                }
            }
        });

        //code for rollover effect on blackjack card image
        blackjackMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                blackjackText.setVisible(true);
                try {
                    playCardClip();
                } catch (LineUnavailableException ex) {
                    ex.printStackTrace();
                } catch (UnsupportedAudioFileException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                blackjackText.setVisible(false);
            }
        });

        //code for gamemode help button
        gamemodeHelp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                gamemodeBox();
            }
        });

        //code for difficulty help button
        difficultyHelp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                difficultyBox();
            }
        });

        // intercept closing the window
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                menuModel.exit();
            }
        });

        //exit button
        /*
        JButton exitBtn = new JButton("Exit");
        exitBtn.setBounds(435, 450, 100, 100);
        add(exitBtn);

        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuModel.exit();
            }
        });
        */

        add(background);
        background.add(gamemodeSelect);
        background.add(gamemodeTxt);
        background.add(blackjackText);
        background.add(slotText);
        background.add(randnumText);
        background.add(saveText);
        background.add(helpText);
        background.add(difficultyText);
        background.add(difficultySelect);
        background.add(moneyTotal);
       // background.add(gamemodeHelp);
        //background.add(difficultyHelp);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * creates pop-up window for getting below $5
     * @param infoMessage (what the message will say)
     * @param titleBar    (text in the title)
     */
    public static void infoBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "Attention! " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * creates pop-up window with rules for blackjack and slot machine
     */
    public void rulesBox(){
        BufferedImage img = null;
        String path = System.getProperty("user.dir"); // get main folder path

        //create pop-up frame
        JFrame ruleFrame = new JFrame();
        JDialog jd = new JDialog(ruleFrame);
        jd.setLayout(new FlowLayout());
        jd.setBounds(300, 100, 550, 565);

        //load picture of rules
        try {
            img = ImageIO.read(new File(path + "/Assets/MainMenu/GameRules.png"));
        } catch (Exception e) {
            System.out.println("Cannot load BlackjackMenu image!");
        }
        gameRules = new JLabel(new ImageIcon(img));
        gameRules.setBounds(5, 5, 200, 200);

        //close button
        JButton close = new JButton("Close");
        close.setBounds(400, 400, 100, 100);
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jd.setVisible(false);
            }
        });
        jd.add(gameRules);
        jd.add(close);
        jd.setVisible(true);
    }

    /**
     * Creates pop-up with information on different game modes
     */
    public void gamemodeBox(){
        BufferedImage img = null;
        String path = System.getProperty("user.dir"); // get main folder path

        //create pop-up frame
        JFrame ruleFrame = new JFrame();
        JDialog jd = new JDialog(ruleFrame);
        jd.setLayout(new FlowLayout());
        jd.setBounds(400, 300, 570, 210);

        //load picture of gamemode rules
        try {
            img = ImageIO.read(new File(path + "/Assets/MainMenu/gamemodeRules.png"));
        } catch (Exception e) {
            System.out.println("Cannot load gamemode rules image!");
        }
        gameRules = new JLabel(new ImageIcon(img));
        gameRules.setBounds(5, 5, 200, 200);

        //close button
        JButton close = new JButton("Close");
        close.setBounds(400, 400, 100, 100);
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jd.setVisible(false);
            }
        });
        jd.add(gameRules);
        jd.add(close);
        jd.setVisible(true);
    }

    /**
     * Creates pop-up with information on different difficulties
     */
    public void difficultyBox(){
        BufferedImage img = null;
        String path = System.getProperty("user.dir"); // get main folder path

        //create pop-up frame
        JFrame ruleFrame = new JFrame();
        JDialog jd = new JDialog(ruleFrame);
        jd.setLayout(new FlowLayout());
        jd.setBounds(400, 300, 360, 385);

        //load picture of rules
        try {
            img = ImageIO.read(new File(path + "/Assets/MainMenu/DifficultyRules.png"));
        } catch (Exception e) {
            System.out.println("Cannot load difficulty rules image!");
        }
        gameRules = new JLabel(new ImageIcon(img));
        gameRules.setBounds(5, 5, 200, 200);

        //close button
        JButton close = new JButton("Close");
        close.setBounds(400, 400, 100, 100);
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jd.setVisible(false);
            }
        });
        jd.add(gameRules);
        jd.add(close);
        jd.setVisible(true);
    }

    /**
     * Loads all pictures from the assets folder
     */
    private void LoadAssets() {

        BufferedImage img = null;
        String path = System.getProperty("user.dir"); // get main folder path

        // Load background
        try {
            img = ImageIO.read(new File(path + "/Assets/MainMenu/MenuBackground1.jpg"));
        }
        catch (Exception e) { System.out.println("Cannot load background image!"); }
        background = new JLabel(new ImageIcon(img));
        background.setBounds(0, 0, 750, 700);

        //load Random Number Icon
        try {
            img = ImageIO.read(new File(path + "/Assets/MainMenu/RandnumMenu.png"));
        } catch (Exception e) {
            System.out.println("Cannot load RandnumMenu image!");
        }
        randnumMenu = new JLabel(new ImageIcon(img));
        randnumMenu.setBounds(250, 420, 150, 150);
        background.add(randnumMenu);


        //load Blackjack Icon
        try {
            img = ImageIO.read(new File(path + "/Assets/MainMenu/BlackjackMenu.png"));
        } catch (Exception e) {
            System.out.println("Cannot load BlackjackMenu image!");
        }
        blackjackMenu = new JLabel(new ImageIcon(img));
        blackjackMenu.setBounds(350, 260, 200, 200);
        background.add(blackjackMenu);

        //load Slotmachine Icon
        try {
            img = ImageIO.read(new File(path + "/Assets/MainMenu/SlotMachineMenu.png"));
        } catch (Exception e) {
            System.out.println("Cannot load SlotMachineMenu image!");
        }
        slotMenu = new JLabel(new ImageIcon(img));
        slotMenu.setBounds(75, 215, 250, 200);
        background.add(slotMenu);

        //load save button
        try {
            img = ImageIO.read(new File(path + "/Assets/MainMenu/SaveButtonResized2.png"));
        } catch (Exception e) {
            System.out.println("Cannot load SaveButton image!");
        }
        saveButton = new JLabel(new ImageIcon(img));
        saveButton.setBounds(560, 20, 40, 40);
        background.add(saveButton);

        //load help button
        try {
            img = ImageIO.read(new File(path + "/Assets/MainMenu/HelpButton2.png"));
        } catch (Exception e) {
            System.out.println("Cannot load HelpButton image!");
        }
        helpButton = new JLabel(new ImageIcon(img));
        helpButton.setBounds(550, 575, 60, 60);
        background.add(helpButton);

        //load gamemode Help Button
        try {
            img = ImageIO.read(new File(path + "/Assets/MainMenu/HelpButtonSmaller.png"));
        } catch (Exception e) {
            System.out.println("Cannot load HelpButton image!");
        }
        gamemodeHelp = new JLabel(new ImageIcon(img));
        gamemodeHelp.setBounds(285, 10, 30, 30);
        background.add(gamemodeHelp);

        //load difficulty help button
        try {
            img = ImageIO.read(new File(path + "/Assets/MainMenu/HelpButtonSmaller.png"));
        } catch (Exception e) {
            System.out.println("Cannot load HelpButton image!");
        }
        difficultyHelp = new JLabel(new ImageIcon(img));
        difficultyHelp.setBounds(285, 75, 30, 30);
        background.add(difficultyHelp);
    }

    /**
     * plays audio for card shuffling
     * @throws LineUnavailableException
     * @throws UnsupportedAudioFileException
     * @throws IOException
     */
    public void playCardClip() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        String path = System.getProperty("user.dir");
        File audioFile = new File(path + "/Sounds/CardShuffle.wav").getAbsoluteFile();
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        //Plays audio once
        clip.start();
    }
    /**
     * plays audio for slot game
     * @throws LineUnavailableException
     * @throws UnsupportedAudioFileException
     * @throws IOException
     */
    public void playSlotClip() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        String path = System.getProperty("user.dir");
        File audioFile = new File(path + "/Sounds/SlotStart.wav").getAbsoluteFile();
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        //Plays audio once
        clip.start();
    }

    /*
     * For testing
     */
    public JLabel getBlackLabel() { return blackjackMenu; }
    public JComboBox<String> getGameOptions() { return gamemodeSelect; }
}
