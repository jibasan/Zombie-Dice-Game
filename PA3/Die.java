//
// Die
//
// This class implements a single die from the Zombie Dice game.  This
// object not only tracks the state of this single die, but also caches
// probabilities concerning possible outcomes of a roll.
//
// Zombie Dice is a trademark of Steve Jackson Games.  For more information
// about this game, see "zombiedice.sjgames.com".
//
// David Noelle -- Mon Nov  5 20:30:09 PST 2018
//


import java.io.*;
import java.util.*;


enum DieColor { invalid, green, yellow, red }

enum DieFace { invalid, brain, feet, blast }


public class Die {

	// Dice parameters ...
	static double die_sides = 6.0;
	static double green_brains = 3.0;
	static double green_feets = 2.0;
	static double green_blasts = 1.0;
	static double yellow_brains = 2.0;
	static double yellow_feets = 2.0;
	static double yellow_blasts = 2.0;
	static double red_brains = 1.0;
	static double red_feets = 2.0;
	static double red_blasts = 3.0;

	// Color of this die ...
	DieColor color = DieColor.invalid;
	// Symbol on the top face ...
	DieFace up = DieFace.invalid;

	// Probabilities ...
	double Pbrain = 0.0;
	double Pfeet = 0.0;
	double Pblast = 0.0;

	// Default constructor ...
	public Die() {
		this.color = DieColor.yellow;
		this.up = DieFace.feet;
		this.Pbrain = yellow_brains / die_sides;
		this.Pfeet = yellow_feets / die_sides;
		this.Pblast = yellow_blasts / die_sides;
	}

	// Constructor with color specified ...
	public Die(DieColor col) {
		this.color = col;
		this.up = DieFace.feet;
		switch (col) {
		case green:
			this.Pbrain = green_brains / die_sides;
			this.Pfeet = green_feets / die_sides;
			this.Pblast = green_blasts / die_sides;
			break;
		case yellow:
			this.Pbrain = yellow_brains / die_sides;
			this.Pfeet = yellow_feets / die_sides;
			this.Pblast = yellow_blasts / die_sides;
			break;
		case red:
			this.Pbrain = red_brains / die_sides;
			this.Pfeet = red_feets / die_sides;
			this.Pblast = red_blasts / die_sides;
			break;
		default:
			this.Pbrain = 0.0;
			this.Pfeet = 0.0;
			this.Pblast = 0.0;
			break;
		}
	}

	// Copy constructor ...
	public Die(Die d) {
		this.color = d.color;
		this.up = d.up;
		this.Pbrain = d.Pbrain;
		this.Pfeet = d.Pfeet;
		this.Pblast = d.Pblast;
	}

	// getColor -- Return the color of the die.
	public DieColor getColor() {
		return (color);
	}

	// getUp -- Return the symbol on the top face of the die.
	public DieFace getUp() {
		return (up);
	}

	// setUp -- Manually set the top face of the die to the given symbol.
	public DieFace setUp(DieFace sym) {
		if (sym != DieFace.invalid) {
			up = sym;
		}
		return (up);
	}

	// prob -- Return the probability of rolling the given symbol.
	public double prob(DieFace sym) {
		switch (sym) {
		case brain:
			return (Pbrain);
		case feet:
			return (Pfeet);
		case blast:
			return (Pblast);
		default:
			return (0.0);
		}
	}

	// roll -- Randomly select a top face symbol for the die.
	public DieFace roll() {
		// Pick a random number between zero and one ...
		Random generator = new Random();
		double num = generator.nextDouble();
		// Translate random number into a face symbol ...
		if (num <= Pbrain) {
			up = DieFace.brain;
		} else {
			if (num <= Pbrain + Pfeet) {
				up = DieFace.feet;
			} else {
				if (num <= 1.0) {
					up = DieFace.blast;
				} else {
					up = DieFace.invalid;
				}
			}
		}
		// Clean up ...
		generator = null;
		// Return the roll ...
		return (up);
	}

	// write -- Write the state of this die to the given stream.
	public void write(OutputStream str) {
		String colorName = "INVALID";
		String upName = "BLANK";

		switch (color) {
		case green:
			colorName = "GREEN";
			break;
		case yellow:
			colorName = "YELLOW";
			break;
		case red:
			colorName = "RED";
			break;
		default:
			colorName = "INVALID";
			break;
		}
		switch (up) {
		case brain:
			upName = "BRAIN";
			break;
		case feet:
			upName = "FEET";
			break;
		case blast:
			upName = "BLAST";
			break;
		default:
			upName = "BLANK";
			break;
		}

		PrintWriter out = new PrintWriter(str, true);
		out.printf("[%s %s]", colorName, upName);
	}

}
