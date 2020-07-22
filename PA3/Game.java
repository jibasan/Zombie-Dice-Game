//
// Game
//
// This class implements a game session for a Zombie Dice game.
//
// Zombie Dice is a trademark of Steve Jackson Games.  For more information
// about this game, see "zombiedice.sjgames.com".
//
// David Noelle -- Mon Nov  5 20:30:09 PST 2018
//


import java.io.*;


public class Game {

	State status;

	// Default constructor ...
	public Game() {
		// Initialize the game state ...
		this.status = new State();
	}

	// chooseMove -- Return a move selected by the computer.
	Choice chooseMove() {
		double eu_roll; // expected utility value of rolling
		double eu_stop; // expected utility value of stoping

		if (status.current_player == Turn.computer) {
			// Always roll if no brains have been collected ...
			if (status.brains_collected == 0) {
				return (Choice.roll);
			}
			// Make a copy of the current state ...
			State new_status = new State(status);
			// Calculate the expected utility value for rolling ...
			new_status.current_choice = Choice.roll;
			eu_roll = Eval.value(new_status);
			// Calculate the expected utility value for stopping ...
			new_status.current_choice = Choice.stop;
			eu_stop = Eval.value(new_status);
			// Make a choice ...
			if (eu_roll >= eu_stop) {
				return (Choice.roll);
			} else {
				return (Choice.stop);
			}
		} else {
			return (Choice.invalid);
		}
	}

	// requestMove -- Return a move selected by the user.
	Choice requestMove() {
		Choice action = Choice.invalid;
		try {
			InputStreamReader converter = new InputStreamReader(System.in);
			BufferedReader in = new BufferedReader(converter);
			String buffer;

			while (action == Choice.invalid) {
				System.out.println("");
				System.out.println("Roll or Stop?  ");
				System.out.flush();
				buffer = in.readLine();
				if (((buffer.substring(0, 1)).equals("R")) || ((buffer.substring(0, 1)).equals("r"))) {
					action = Choice.roll;
				} else {
					if (((buffer.substring(0, 1)).equals("S")) || ((buffer.substring(0, 1)).equals("s"))) {
						action = Choice.stop;
					} else {
						action = Choice.invalid;
					}
				}
			}
		} catch (IOException e) {
			// Something went wrong ...
			return (Choice.invalid);
		}
		return (action);
	}

	// takeAction -- Update the current state of play by having the
	// current player take the specified action.
	void takeAction(Choice act) {
		status.current_choice = act;
		switch (act) {
		case roll:
			// Draw dice from cup ...
			if (status.drawHand()) {
				// Successfully drew dice from the cup, so roll the dice ...
				status.rollInPlace();
				// Collect brains and blasts ...
				status.collectHand();
				// Check for being shotgunned ...
				if (status.shotgunned()) {
					// Forced end of turn, so display result ...
					System.out.println("");
					status.write(System.out);
					System.out.println("");
					System.out.println("SHOTGUNNED!");
					System.out.flush();
					takeAction(Choice.stop);
				} else {
					// Next choice for the current player ...
					status.current_choice = Choice.undecided;
				}
			} else {
				// There was a failure to draw a complete hand. Given
				// the policy of reusing collected brain dice when the
				// cup empties, this should never happen ...
				System.out.println("");
				System.out.println("CANNOT DRAW COMPLETE HAND FROM CUP.");
				takeAction(Choice.stop);
			}
			break;
		case stop:
			// Record any brains eaten ...
			status.endTurn();
			// Check for end of game ...
			if (!(status.terminal())) {
				// Game is not over, so hand over the turn ...
				status.nextPlayer();
			}
			break;
		default:
			System.out.println("");
			System.out.println("ERROR:  ATTEMPTING INVALID ACTION.");
			status.current_choice = Choice.invalid;
			break;
		}
	}

	// play -- Play a game of Zombie Dice.
	public void play() {
		Choice action = Choice.invalid;

		System.out.println("");
		System.out.println("ZOMBIE DICE!");
		// Show initial game state ...
		System.out.println("");
		status.write(System.out);
		System.out.flush();
		// Loop until the state is terminal ...
		while (!(status.terminal())) {
			// Check whose turn it is ...
			switch (status.current_player) {
			case computer:
				// Computer player's turn ...
				action = chooseMove();
				if (action == Choice.invalid) {
					System.out.println("ERROR:  BAD CHOICE BY COMPUTER.");
					return;
				}
				break;
			case user:
				// User player's turn ...
				action = requestMove();
				if (action == Choice.invalid) {
					System.out.println("ERROR:  BAD CHOICE BY USER.");
					return;
				}
				break;
			default:
				// We should never get here ...
				System.out.println("ERROR:  INVALID PLAYER.");
				return;
			}
			// Display this action ...
			System.out.println("");
			switch (action) {
			case roll:
				System.out.println("PLAYER ROLLS!");
				break;
			case stop:
				System.out.println("PLAYER STOPS!");
				break;
			default:
				System.out.println("ERROR:  BAD PLAYER ACTION!");
				break;
			}
			// Perform the action, updating the game state ...
			takeAction(action);
			// Show the resulting game state ...
			System.out.println("");
			status.write(System.out);
			System.out.flush();
		}
		// Report winner ...
		System.out.println("");
		if (status.comp_brains_eaten > status.user_brains_eaten) {
			System.out.println("COMPUTER WINS!");
		} else {
			System.out.println("USER WINS!");
		}
		System.out.println("");
	}

}

