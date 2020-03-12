import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;
// import Timer error: reference to Timer is ambiguous both class javax.swing.Timer 
// in javax.swing and class java.util.Timer in java.util match
import java.util.Timer;
import java.util.TimerTask;


/**
 * This is the Card Layout which will contain Panels to which will be rotated as
 * the user play or interact with the menu.
 */
public class LayoutControl implements Observer {

	
	private JPanel mainPanel;
	private GetConnect guiInfo;
	private ConnectScorePanel score;
	private C4GamePanel gamePanel = null;
   //menu object
   private Menu menu;
	private final static String INPUTPANEL = "Input";
	private final static String GAMEPANEL = "Game";
	private final static String SCOREPANEL = "Score";

	/**
	 * 
	 * Constructor creating the card layout and populating the panels to be used
	 * with in the GUI
	 */
	// public LayoutControl(Menu menu) throws UnknownHostException,
// 			IOException {
   public LayoutControl(Menu menu){
      //try{
   		this.menu = menu;
   		CardLayout card = new CardLayout(25, 25);
   		mainPanel = new JPanel(card);
   		mainPanel.setPreferredSize(new Dimension(500, 500));
   
   		guiInfo = new GetConnect();
   		guiInfo.addObserver(this);
   
   		HomePage inputPanel = new HomePage(this);
   
   		score = new ConnectScorePanel();
   
   		mainPanel.add(inputPanel, INPUTPANEL);
   		mainPanel.add(score, SCOREPANEL);


	}

	/**
	 * This will activate the game play for the client to start playing.
	 */
	public void startGame(String ipNumber) throws UnknownHostException,
			IOException {

		gamePanel = new C4GamePanel(ipNumber, guiInfo);
		mainPanel.add(gamePanel, GAMEPANEL);
		((CardLayout) mainPanel.getLayout()).show(mainPanel, GAMEPANEL);
		menu.nextGame();
		JpanelRequestFocus();

	}

	public void JpanelRequestFocus() {
		gamePanel.JpanelRequestFocus();
	}

	/*
	 */
	public void changePanel(String panel, GameType gameType) throws IOException {
		if (panel.equals(GAMEPANEL)) {
			gamePanel.getBoardPanel().forfeit(gameType);
		} else {
			((CardLayout) mainPanel.getLayout()).show(mainPanel, panel);
		}
	}

	/***/
	public Container getConnectFourContent() {
		return mainPanel;
	}

	private Timer t = null;

	/**
	 * updates the display
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (guiInfo.getGameIsOver()) {
			t = new Timer();
			t.schedule(new TimerTask() {
				public void run() {
					score.setScore();
					try {
						changePanel(SCOREPANEL, null);
						score.JpanelRequestFocus();
					} catch (IOException e) {
						guiInfo.setMessage("An error has occured: "
								+ e.getMessage());
					}
					t.cancel();
				}
			}, 1000);
		} else {
			if (gamePanel != null) {
				((CardLayout) mainPanel.getLayout()).show(mainPanel, GAMEPANEL);
			}
		}
	}

	/**
	 * Inner class to create the panel to be displayed in the card layout during
	 * Game play
	 */
	private class C4GamePanel extends JPanel implements Observer {

      private static final long serialVersionUID =    1789784134692788297L;
		private ConnectAI board;
		private GetConnect guiInfo;
		JLabel message;

		/**
		 * Constructor in creating the C4GamePanel
		 */
		public C4GamePanel(String ipNumber, GetConnect guiInfo)
				throws UnknownHostException, IOException {
			setLayout(new GridBagLayout());
			this.guiInfo = guiInfo;
			guiInfo.addObserver((Observer) this);
			JLabel heading = new JLabel("Connect Four", JLabel.CENTER);
			message = new JLabel("Error message will go here!", JLabel.CENTER);
			board = new ConnectAI(guiInfo, ipNumber);
			board.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			add(heading, makeConstraints(0, 0, 1, 1, .2, .2));
			add(message, makeConstraints(0, 1, 1, 1, .2, .2));
			add(board, makeConstraints(0, 2, 1, 1, 1, 1));
		}

		public void JpanelRequestFocus() {
			board.JpanelRequestFocus();
		}

		/**
		 * Returns the board.
		 * 
		 * @return current board
		 */
		public ConnectAI getBoardPanel() {
			return board;
		}

		/**
		 * Updates the message stating state of play
		 */
		@Override
		public void update(Observable o, Object arg) {
			message.setText(guiInfo.getMessage());
		}
	}

	/**
	 * This is the panel to display player's score. This may be displayed in
	 * several locations.
	 * 
	 * @author Natacha Gabbamonte
	 * @author Gabriel Gheorghian
	 * @author Mark Scerbo
	 * 
	 */
	private class ConnectScorePanel extends JPanel {

      private static final long serialVersionUID = -1697273123456780008L;
		JLabel winL;
		JLabel lossL;
		JLabel drawL;
		JLabel winner;
		JButton yesButton;
		JButton noButton;
		JLabel question;
		String[] winners = new String[] { "IT'S A DRAW", "PLAYER ONE WON!",
				"PLAYER TWO WON!" };
		String[] questions = new String[] { "Would you like to play again?"};

		/**
		 * Constructor to the Score panel populates variables and proccess them.
		 */
		public ConnectScorePanel() {
			setLayout(new GridBagLayout());

			winL = new JLabel("", JLabel.CENTER);
			lossL = new JLabel("", JLabel.CENTER);
			drawL = new JLabel("", JLabel.CENTER);

			winner = new JLabel("", JLabel.CENTER);

			question = new JLabel(" ", JLabel.CENTER);
			yesButton = new JButton("Yes");
			yesButton.addActionListener(new ActionListener() {

				/**
				 * updates the mainPanel
				 */
				@Override
				public void actionPerformed(ActionEvent e) {
					menu.nextGame();
					try {
						gamePanel.getBoardPanel().playerWantsToPlayAgain();
					} catch (IOException e1) {
						guiInfo.setMessage("An error has occured: "
								+ e1.getMessage());
						((CardLayout) mainPanel.getLayout())
								.show(mainPanel, "GAME");
					}
				}

			});

			noButton = new JButton("No");
			noButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						gamePanel.getBoardPanel().playerDoesntWantToPlayAgain();
					} catch (IOException e1) {
						guiInfo.setMessage("An error has occured: "
								+ e1.getMessage());
						((CardLayout) mainPanel.getLayout())
								.show(mainPanel, "GAME");
					}
					System.exit(0);
				}

			});

			// Adds a keyboard listener to listen for Y or N.
			this.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					char key = e.getKeyChar();
					switch (key) {
					case 'y':
					case 'Y':
						yesButton.doClick();
						break;
					case 'n':
					case 'N':
						noButton.doClick();
						break;
					}
				}
			});

			JLabel gameOverLabel = new JLabel("Game Over", JLabel.CENTER);
			gameOverLabel.setFont(new Font("Times New Roman", Font.BOLD, 25));

			add(gameOverLabel, makeConstraints(0, 0, 2, 1, 0.2, 0.2));
			add(winner, makeConstraints(0, 1, 2, 1, 0.2, 0.2));
			add(winL, makeConstraints(0, 2, 2, 1, 0.2, 0.2));
			add(lossL, makeConstraints(0, 3, 2, 1, 0.2, 0.2));
			add(drawL, makeConstraints(0, 4, 2, 1, 0.2, 0.2));

			add(question, makeConstraints(0, 5, 2, 1, 0.2, 0.2));
			add(yesButton, makeConstraints(0, 6, 1, 1, 0.2, 0.2));
			add(noButton, makeConstraints(1, 6, 1, 1, 0.2, 0.2));
		}

		/* This arranges the text to be displayed on the score panel
		 */
		public void setScore() {
			menu.deactivate();
			winner.setText(winners[guiInfo.getWinner()]);
			question.setText(questions[guiInfo.getGameOverMessage()]);
			winL.setText("Player one's Wins:     " + guiInfo.getWins());
			lossL.setText("Player two's Wins:     " + guiInfo.getLostCount());
			drawL.setText("Draws:                          " + guiInfo.getDraws());
			if (guiInfo.isNextIsNewGame()) {
				guiInfo.setNextIsNewGame(false);
				guiInfo.setGameOverMessage(0);
				guiInfo.resetStats();
			}
		}

		public void JpanelRequestFocus() {
			this.requestFocusInWindow();
		}
	}

	/* sets constraints based on the values sent in */
	public static GridBagConstraints makeConstraints(int gridx, int gridy,

	int gridwidth, int gridheight, double weightx, double weighty) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridheight = gridheight;
		constraints.gridwidth = gridwidth;
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.weightx = weightx;
		constraints.weighty = weighty;

		// Default for all the components.
		constraints.insets = new Insets(1, 1, 1, 1);
		constraints.fill = GridBagConstraints.BOTH;
		return constraints;
	}
}
