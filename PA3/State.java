//
// State
//
// This class implements a state of play for the Zombie Dice game.  This
// includes the state of played dice, as well as the contents of the dice
// cup.  This class is designed to be useful both for tracking the
// actual state of play and for considering hypothetical future states of
// play.  Note that when multiple hypothetical states are being considered,
// state objects will typically need to be copied and modified, rather than
// modified in place.
//
// Zombie Dice is a trademark of Steve Jackson Games.  For more information
// about this game, see "zombiedice.sjgames.com".
//
// David Noelle -- Mon Nov  5 20:30:09 PST 2018
//


import java.io.*;
import java.util.*;


enum Turn { invalid, computer, user }

enum Choice { invalid, undecided, roll, stop }

public class State {

	// Game parameters ...
	static int brains_to_win = 13;
	static int num_terminal_blasts = 3;
	static int hand_size = 3;
	static double win_payoff = 100.0;

	// Current scores ...
	public int comp_brains_eaten = 0;
	public int user_brains_eaten = 0;

	// Turn status ...
	public Turn current_player = Turn.invalid;
	public Choice current_choice = Choice.invalid;

	// Current dice counts ...
	public int brains_collected = 0;
	public int blasts_collected = 0;

	// Dice lists ...
	List<Die> brains; // collected brain dice
	List<Die> blasts; // collected blast dice
	List<Die> hand; // dice rolled (or to be rolled) from hand

	// Dice cup ...
	Cup cup;

	// Default constructor ...
	public State() {
		// Initialize to start of game ...
		this.comp_brains_eaten = 0;
		this.user_brains_eaten = 0;
		this.current_player = Turn.computer;
		this.current_choice = Choice.undecided;
		this.brains_collected = 0;
		this.blasts_collected = 0;
		this.brains = new ArrayList<Die>();
		this.blasts = new ArrayList<Die>();
		this.hand = new ArrayList<Die>();
		this.cup = new Cup();
		// Initialize with the cup already shaken ...
		this.cup.shake();
	}

	// Copy constructor ...
	public State(State s) {
		this.comp_brains_eaten = s.comp_brains_eaten;
		this.user_brains_eaten = s.user_brains_eaten;
		this.current_player = s.current_player;
		this.current_choice = s.current_choice;
		this.brains_collected = s.brains_collected;
		this.blasts_collected = s.blasts_collected;
		// Be sure to copy each individual die ...
		Die this_die;
		this.brains = new ArrayList<Die>();
		for (Die d_brains : s.brains) {
			this_die = new Die(d_brains);
			this.brains.add(this_die);
		}
		this.blasts = new ArrayList<Die>();
		for (Die d_blasts : s.blasts) {
			this_die = new Die(d_blasts);
			this.blasts.add(this_die);
		}
		this.hand = new ArrayList<Die>();
		for (Die d_hand : s.hand) {
			this_die = new Die(d_hand);
			this.hand.add(this_die);
		}
		// The copy constructor for Cup copies the contained dice ...
		this.cup = new Cup(s.cup);
	}

	// numDiceInHand -- Return the number of dice in the current hand.
	public int numDiceInHand() {
		return (hand.size());
	}

	// collectHand -- Remove brains and blasts from the hand, placing them
	// in the brains and blasts lists, respectively. Do
	// this by modifying the State in place. When this
	// function returns, all dice remaining in the hand
	// should have feet up.
	public void collectHand() {
		// Iterate over the Die objects in hand ...
		Iterator<Die> i = hand.iterator();
		// Using an iterator in this way is the only safe way to remove
		// items from a list that is currently being iterated over ...
		while (i.hasNext()) {
			Die d = i.next();
			if (d.getUp() == DieFace.brain) {
				brains_collected = brains_collected + 1;
				brains.add(d);
				i.remove();
				continue;
			}
			if (d.getUp() == DieFace.blast) {
				blasts_collected = blasts_collected + 1;
				blasts.add(d);
				i.remove();
				continue;
			}
		}
	}

	// shotgunned -- Return true iff the current state has collected
	// a number of blasts that meets or exceeds the blast
	// limit for being "shotgunned".
	public boolean shotgunned() {
		return (blasts_collected >= num_terminal_blasts);
	}

	// endTurn -- Record brains eaten during this turn, modifying
	// the State object in place. This function assumes that
	// the latest hand has been collected.
	public void endTurn() {
		// Only eat brains if the player has not been shotgunned ...
		if (shotgunned() == false) {
			if (current_player == Turn.computer) {
				comp_brains_eaten = comp_brains_eaten + brains_collected;
			} else {
				user_brains_eaten = user_brains_eaten + brains_collected;
			}
		}
		// Clear out collection counts ...
		brains_collected = 0;
		blasts_collected = 0;
		// Return dice to the cup ...
		cup.replace(brains);
		brains.clear();
		cup.replace(blasts);
		blasts.clear();
		cup.replace(hand);
		hand.clear();
		// Shake the cup ...
		cup.shake();
		// Force choice to stop rolling ...
		current_choice = Choice.stop;
	}

	// nextPlayer -- Modifies the State in place to prepare for the next
	// player's turn. This function assumes that the
	// previous player's turn has ended, and all dice have
	// been returned to the cup, as done in the "endTurn"
	// function. The State object is modified in place.
	public void nextPlayer() {
		// Update turn status
		if (current_player == Turn.computer) {
			current_player = Turn.user;
		} else {
			current_player = Turn.computer;
		}
		current_choice = Choice.undecided;
	}

	// cupIsEmpty -- Returns true iff the cup is empty.
	public boolean cupIsEmpty() {
		return (cup.isEmpty());
	}

	// reuseBrains -- When the cup is empty, collected brain dice may be
	// replaced in the cup to provide dice to roll. This
	// function replaces collected brain dice into the cup.
	public void reuseBrains() {
		cup.replace(brains);
		brains.clear();
	}

	// draw -- Draw a random die from the cup into the hand. Return null
	// if the cup is empty or the hand is full. Return the drawn
	// die, otherwise. The State object is modified in place.
	public Die draw() {
		if (hand.size() >= hand_size) {
			// Hand is already full ...
			return (null);
		}
		// Draw a die randomly from the cup ...
		Die drawn_die = cup.draw();
		// Check for an empty cup ...
		if (drawn_die == null) {
			// No dice left ...
			return (null);
		}
		// To be safe, initialize die to feet up ...
		drawn_die.setUp(DieFace.feet);
		// Add die to the hand ...
		hand.add(drawn_die);
		// Success ...
		return (drawn_die);
	}

	// draw -- Draw a die of the specified color from the cup into the
	// hand. Return null if the cup is empty or the hand is full.
	// Return the drawn die, otherwise. The State object is
	// modified in place.
	public Die draw(DieColor col) {
		if (hand.size() >= hand_size) {
			// Hand is already full ...
			return (null);
		}
		// Draw a die of the specified color from the cup ...
		Die drawn_die = cup.draw(col);
		// Check for a failure to find a die of this color ...
		if (drawn_die == null) {
			// No dice of this color left ...
			return (null);
		}
		// To be safe, initialize die to feet up ...
		drawn_die.setUp(DieFace.feet);
		// Add die to the hand ...
		hand.add(drawn_die);
		// Success ...
		return (drawn_die);
	}

	// drawHand -- Randomly draw dice from the cup until the hand is full.
	// The State object is modified in place. Return false
	// if there are insufficient dice in the cup.
	public boolean drawHand() {
		if (hand.size() >= hand_size) {
			// The hand is full ...
			return (true);
		} else {
			// Attempt to draw one random die ...
			if (draw() == null) {
				// The cup is empty. Following the policy in the game
				// rules, the collected brains may be reused at this point,
				// adding them to the cup ...
				reuseBrains();
				// Note that the collected brain count is not reset,
				// at this point. Only the brain dice are reused.
				return (true);
			} else {
				// Recursively fill the cup ...
				return (drawHand());
			}
		}
	}

	// replace -- Replace the specified die from the hand into the cup.
	// Return false if the specified die is not found in the
	// hand. The State object is modified in place.
	public boolean replace(Die d) {
		if (hand.remove(d)) {
			// The hand did contain the specified die ...
			cup.replace(d);
			return (true);
		} else {
			// The die was not found in the hand ...
			return (false);
		}
	}

	// drawProb -- Return the probability of drawing a die of the given
	// color from the current cup.
	public double drawProb(DieColor col) {
		return (cup.prob(col));
	}

	// rollProb -- Return the probability of rolling the specified face
	// values on the dice in the current hand. Note that this
	// function assumes that there are three dice in a hand
	// (i.e., that the value of "have_size" is three).
	public double rollProb(DieFace up1, DieFace up2, DieFace up3) {
		if (hand.size() >= 3) {
			return ((hand.get(0)).prob(up1) * (hand.get(1)).prob(up2) * (hand.get(2)).prob(up3));
		} else {
			return (0.0);
		}
	}

	// roll -- Roll the dice that are in the hand, selecting random
	// values for the face up symbols, returning a newly
	// allocated State.
	public State roll() {
		State newstate = new State(this);
		for (Die d : newstate.hand) {
			d.roll();
		}
		// Force choice to undecided ...
		newstate.current_choice = Choice.undecided;
		return (newstate);
	}

	// roll -- Manually set the outcome of the roll to the specified face
	// values. Return a newly allocated State object. Note
	// that this function assumes that there are three dice in
	// a hand (i.e., that the value of "hand_size" is three).
	public State roll(DieFace up1, DieFace up2, DieFace up3) {
		State newstate = new State(this);
		if (newstate.hand.size() >= 3) {
			(newstate.hand.get(0)).setUp(up1);
			(newstate.hand.get(1)).setUp(up2);
			(newstate.hand.get(2)).setUp(up3);
		}
		return (newstate);
	}

	// rollInPlace -- Roll the dice that are in the hand, selecting
	// random values for the face up symbols, modifying
	// the State in place.
	public State rollInPlace() {
		for (Die d : hand) {
			d.roll();
		}
		// Force choice to undecided ...
		current_choice = Choice.undecided;
		return (this);
	}

	// rollInPlace -- Manually set the outcome of the roll to the
	// specified face values. Modify the State object
	// in place. Note that this function assumes that
	// there are three dice in a hand (i.e., that the
	// value of "hand_size" is three).
	public State rollInPlace(DieFace up1, DieFace up2, DieFace up3) {
		if (hand.size() >= 3) {
			(hand.get(0)).setUp(up1);
			(hand.get(1)).setUp(up2);
			(hand.get(2)).setUp(up3);
		}
		return (this);
	}

	// terminal -- Return true iff this is a terminal state (i.e., the
	// game is over). This function assumes that the current
	// turn has been ended, as done in the "endTurn" function.
	public boolean terminal() {
		return ((current_player == Turn.user) && (current_choice == Choice.stop)
				&& (comp_brains_eaten != user_brains_eaten)
				&& ((comp_brains_eaten >= brains_to_win) || (user_brains_eaten >= brains_to_win)));
	}

	// payoff -- Return the utility of the current state, if it is a
	// terminal state, or the value of a heuristic evaluation
	// function if it is not a terminal state.
	public double payoff() {
		if (terminal()) {
			if (comp_brains_eaten > user_brains_eaten) {
				return (win_payoff);
			} else {
				return (-win_payoff);
			}
		} else {
			return (Eval.heuristic(this));
		}
	}

	// write -- Write the game state to the given stream.
	public void write(OutputStream str) {
		PrintWriter out = new PrintWriter(str, true);
		String playerName = "BLANK";

		out.printf("GAME STATE:\n");
		out.printf("  COMP BRAINS EATEN = %2d\n", comp_brains_eaten);
		out.printf("  USER BRAINS EATEN = %2d\n", user_brains_eaten);
		out.printf("\n");
		if (current_player == Turn.computer) {
			playerName = "comp";
		} else {
			playerName = "user";
		}
		out.printf("  CURRENT PLAYER = %s\n", playerName);
		out.printf("\n");
		out.printf("  BLASTS COLLECTED =");
		if (blasts.isEmpty()) {
			out.printf(" NONE.");
		} else {
			// List collected blast dice ...
			for (Die blast_d : blasts) {
				out.printf("\n    ");
				blast_d.write(str);
			}
		}
		out.printf("\n\n");
		out.printf("  BRAINS COLLECTED =");
		if (brains_collected == 0) {
			out.printf(" NONE.");
		} else {
			// Check if brain dice have been reused, replaced in
			// the cup because the cup was emptied ...
			if (brains_collected > brains.size()) {
				out.printf("\n    %d reused brains", (brains_collected - brains.size()));
			}
			// List collected brain dice ...
			for (Die brain_d : brains) {
				out.printf("\n    ");
				brain_d.write(str);
			}
		}
		out.printf("\n\n");
		out.printf("  DICE IN HAND =");
		if (hand.isEmpty()) {
			out.printf(" NONE.");
		} else {
			// List dice in hand ...
			for (Die hand_d : hand) {
				out.printf("\n    ");
				hand_d.write(str);
			}
		}
		out.printf("\n");
	}

}
