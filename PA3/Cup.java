//
// Cup
//
// This class implements a cup of dice from the Zombie Dice game.  This
// object not only tracks the state of the cup and its contents, but also
// provides utilties for calculating the probabilities of draws of fresh
// dice from the cup.
//
// Zombie Dice is a trademark of Steve Jackson Games.  For more information
// about this game, see "zombiedice.sjgames.com".
//
// David Noelle -- Mon Nov  5 20:30:09 PST 2018
//


import java.util.*;


public class Cup {

	// Game parameters ...
	static int total_num_dice = 13;
	static int total_num_green = 6;
	static int total_num_yellow = 4;
	static int total_num_red = 3;

	// Current dice counts ...
	int num_dice = total_num_dice;
	int num_green = total_num_green;
	int num_yellow = total_num_yellow;
	int num_red = total_num_red;

	// Contents of cup as Die objects ...
	List<Die> dice;

	// Default constructor ...
	public Cup() {
		// Initialize cup as full ...
		this.num_dice = total_num_dice;
		this.num_green = total_num_green;
		this.num_yellow = total_num_yellow;
		this.num_red = total_num_red;
		// Allocate list of dice ...
		this.dice = new ArrayList<Die>();
		// Create corresponding Die objects ...
		Die new_die = null;
		for (int i_green = 0; i_green < total_num_green; i_green++) {
			new_die = new Die(DieColor.green);
			this.dice.add(new_die);
		}
		for (int i_yellow = 0; i_yellow < total_num_yellow; i_yellow++) {
			new_die = new Die(DieColor.yellow);
			this.dice.add(new_die);
		}
		for (int i_red = 0; i_red < total_num_red; i_red++) {
			new_die = new Die(DieColor.red);
			this.dice.add(new_die);
		}
	}

	// Copy constructor ...
	public Cup(Cup c) {
		this.num_dice = c.num_dice;
		this.num_green = c.num_green;
		this.num_yellow = c.num_yellow;
		this.num_red = c.num_red;
		// Allocate list of dice ...
		this.dice = new ArrayList<Die>();
		// Copy corresponding Die objects ...
		Die die_copy;
		for (Die this_die : c.dice) {
			die_copy = new Die(this_die);
			this.dice.add(die_copy);
		}
	}

	// isEmpty -- Returns true iff the cup is empty.
	public boolean isEmpty() {
		return (num_dice <= 0);
	}

	// shake -- Randomize the order of dice in the cup.
	public void shake() {
		Collections.shuffle(dice);
	}

	// draw -- Draw a random die from the cup.
	public Die draw() {
		if (dice.isEmpty()) {
			return (null);
		} else {
			Die drawn_die = dice.get(0);
			dice.remove(0);
			// Update counts ...
			num_dice = num_dice - 1;
			switch (drawn_die.getColor()) {
			case green:
				num_green = num_green - 1;
				break;
			case yellow:
				num_yellow = num_yellow - 1;
				break;
			case red:
				num_red = num_red - 1;
				break;
			default:
				// This should never happen ...
				num_dice = num_dice + 1;
				drawn_die = null;
				break;
			}
			// Return the drawn die ...
			return (drawn_die);
		}
	}

	// draw -- Draw a die of the specified color from the cup.
	public Die draw(DieColor col) {
		if (dice.isEmpty()) {
			return (null);
		} else {
			Die drawn_die = null;
			int drawn_die_i = 0;
			while (drawn_die_i < num_dice) {
				drawn_die = dice.get(drawn_die_i);
				if (drawn_die.getColor() == col) {
					// Found a die of the required color ...
					dice.remove(drawn_die_i);
					// Update counts ...
					num_dice = num_dice - 1;
					switch (col) {
					case green:
						num_green = num_green - 1;
						break;
					case yellow:
						num_yellow = num_yellow - 1;
						break;
					case red:
						num_red = num_red - 1;
						break;
					default:
						// This should never happen ...
						num_dice = num_dice + 1;
						drawn_die = null;
						break;
					}
					// Return the drawn die ...
					return (drawn_die);
				}
				drawn_die_i++;
			}
			return (null);
		}
	}

	// replace -- Replace the given die into the cup.
	public void replace(Die d) {
		// Place die in list ...
		dice.add(d);
		// Update counts ...
		num_dice = num_dice + 1;
		switch (d.getColor()) {
		case green:
			num_green = num_green + 1;
			break;
		case yellow:
			num_yellow = num_yellow + 1;
			break;
		case red:
			num_red = num_red + 1;
			break;
		default:
			// This should never happen ...
			break;
		}
	}

	// replace -- Replace the given list of dice into the cup.
	public void replace(List<Die> ds) {
		for (Die d : ds) {
			replace(d);
		}
	}

	// prob -- Return the probability of drawing a die of the given color.
	public double prob(DieColor col) {
		double p = 0.0;

		switch (col) {
		case green:
			p = ((double) num_green) / ((double) num_dice);
			break;
		case yellow:
			p = ((double) num_yellow) / ((double) num_dice);
			break;
		case red:
			p = ((double) num_red) / ((double) num_dice);
			break;
		default:
			p = 0.0;
			break;
		}
		return (p);
	}

}
