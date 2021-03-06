package hsma.ss2013.oot.groupproject.ui;

import hsma.ss2013.oot.groupproject.board.Board;
import hsma.ss2013.oot.groupproject.board.Field;
import hsma.ss2013.oot.groupproject.board.House;
import hsma.ss2013.oot.groupproject.game.Move;
import hsma.ss2013.oot.groupproject.player.AIPlayer;
import hsma.ss2013.oot.groupproject.player.HumanPlayer;
import hsma.ss2013.oot.groupproject.player.Player;
import hsma.ss2013.oot.groupproject.rules.RulesPrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import javax.naming.NameAlreadyBoundException;

public class GameIO {

    private static String[][] printField = new String[11][11];

    public static void update(Board board) {
	drawHomes(board);

	for (int i = 0; i < 11; i++) {
	    for (int j = 0; j < 11; j++) {
		printField[i][j] = "  -  ";
	    }
	}

	// Spielbrett printen
	for (int i = 0; i < board.field.length; i++) {
	    // Feld an der aktuellen Position zwischenspeichern
	    Field tempField = board.field[i][0];

	    // Falls auf dem Feld keine Figur steht X ausgeben
	    if (tempField.getToken().size() == 0) {
		 printField[tempField.getXkoord()][tempField.getyKoord()] = "[   ]";
	    } else if(tempField.getToken().size() > 1){
		printField[tempField.getXkoord()][tempField.getyKoord()] = "["
			+ tempField.tokensToString() + "]";
		
	    } else {
		// Sonst Figuren ausgeben
		
		printField[tempField.getXkoord()][tempField.getyKoord()] = "[ "
			+ tempField.tokensToString() + " ]";
	    }

	}

	// Homebretter ausgeben
	for (int i = 9; i < 40; i += 10) {
	    for (int j = 1; j < 5; j++) {
		House tempField = (House) board.field[i][j];
		if (tempField.getToken().size() == 0) {
		     printField[tempField.getXkoord()][tempField.getyKoord()] = "  X  ";
		} else {
		    // Sonst Figuren ausgeben
		    printField[tempField.getXkoord()][tempField.getyKoord()] = "  "
			    + tempField.tokensToString()+"  ";
		}
	    }
	}

	// Spielfeld ausgeben
	for (int i = 0; i < 11; i++) {
	    for (int j = 0; j < 11; j++) {
		System.out.print(printField[j][i] + " ");
	    }
	    System.out.print("\n");
	}
    }

    public static void drawHomes(Board board) {
	Player[] players = board.getPlayers();
	System.out
		.println("***************************************************");
	System.out.println("Spieler Haeuser:");

	for (int i = 0; i < players.length; i++) {
	    System.out.print("Spieler: " + players[i].getName() + "\t[ ");
	    for (int j = 0; j < players[i].getHome().size(); j++) {
		System.out.print(players[i].getHome().get(j).getIcon() + " ");
	    }
	    System.out.print("]");
	    System.out.println();
	}
	System.out
		.println("***************************************************");
	System.out.println();
    }

    @SuppressWarnings("resource")
    public static Move getPlayerMove(ArrayList<Move> moves) {
	Scanner eingabe = new Scanner(System.in);
	for (int i = 0; i < moves.size(); i++) {
	    System.out.printf("%d. Die Figur an der Stelle %d kann ", i + 1,
		    moves.get(i).getToken().getPosition());
	    switch (moves.get(i).getMoveType()) {
	    case SUSPEND:
		System.out.println("nicht bewegt werden");
		break;
	    // TODO
	    case THROW:
		System.out.println("eine Figur schmeissen");
		break;
	    case FINISH:
		System.out.println("ins Haus ruecken");
		break;
	    case BARRIER:
		System.out.println("eine Barriere errichten");
		break;
	    case MOVE:
		System.out.println("gerueckt werden");
		break;
	    case START:
	    case START_THROW:
		System.out.println("aufs Startfeld gesetzt werden");
		break;
	    }
	}
	System.out.print("Welchen Zug moechten Sie durchfuehren?");
	int chosenMove;
	int movesSize = moves.size();
	while (true) {
	    String move = eingabe.nextLine();
	    if (isInt(move)) {
		chosenMove = Integer.parseInt(move);
		if (chosenMove >= 1 && chosenMove <= movesSize) {
		    return moves.get(chosenMove - 1);
		} else {
		    System.out
			    .println("Bitte geben Sie eine Zahl zwischen 1 und " + movesSize + " ein.");
		}
	    } else {
		System.out.println("Bitte geben Sie eine positive Zahl ein.");
	    }
	}
    }

    /**
     * Modifizierte {@link #gameStart()} mit Erfassung von Spielern des Typs
     * {@link HumanPlayer} und {@link AIPlayer}. Wird von {@link GameIO}
     * aufgerufen.
     * 
     * @return Ergebnis-Array der
     *         {@link #mergePlayerTypes(HumanPlayer[], AIPlayer[])} - Methode
     */
    public static Player[] gameStart() {
	int iconCounter = 35;
	HumanPlayer[] hp = createHP(iconCounter);
	AIPlayer[] aip;
	int maxAIPlayerpossible = 4 - hp.length;
	if (maxAIPlayerpossible == 0) {
	    aip = new AIPlayer[0];
	    // Wenn keine Computergegner moeglich sind (da schon 4 menschliche
	    // Spieler, wird leeres Array uebergeben
	} else {
	    aip = createAIP(maxAIPlayerpossible, iconCounter + hp.length,
		    10 * hp.length);
	}
	return mergePlayerTypes(hp, aip);
    }

    /**
     * Private Methode fuer die Zusammenfuehrung der Objekte vom Typ HumanPlayer
     * und AIPlayer in ein gemeinsames Array. Wird nur innerhalb dieser Klasse
     * aufgerufen von {@link #gameStart()}.
     * 
     * Gibt das Array {@code hPlayer[]} der menschlichen Spieler zurueck, wenn
     * kein Computergegner angemeldet wurde.
     * 
     * Gibt das Array {@code aiPlayer[]} der computergesteuerten Spieler
     * zurueck, wenn kein menschlicher Spieler angemeldet wurde.
     * 
     * Gibt das Array {@code Player[]}aller angemeldeten Spieler, wenn sowohl
     * menschliche als auch computergesteuerte Spieler angemeldet wurden.
     * 
     * @param player
     *            Liste vom Typ {@link Player} fuer Merge-Operation
     * @param hPlayers
     *            Array mit menschlichen Spielern
     * @param aiPlayers
     *            Array mit computergesteuerten Spielern
     * @return Array aller angemeldeten Spieler
     */
    private static Player[] mergePlayerTypes(HumanPlayer[] hPlayers,
	    AIPlayer[] aiPlayers) {
	final int hPlayerCount = hPlayers.length;
	final int aiPlayerCount = aiPlayers.length;
	if (hPlayerCount == 0) {
	    return aiPlayers;
	}
	if (aiPlayerCount == 0) {
	    return hPlayers;
	}
	List<Player> player = new ArrayList<Player>(hPlayers.length
		+ aiPlayers.length);
	Collections.addAll(player, hPlayers);
	Collections.addAll(player, aiPlayers);
	return player.toArray(new Player[player.size()]);
    }

    /**
     * Private Methode zum Ermitteln der gewuenschten Anzahl computergesteuerter
     * Spieler vom Typ {@link AIPlayer}. Wird nur innerhalb dieser Klasse
     * aufgerufen von {@link #mergePlayerTypes(HumanPlayer[], AIPlayer[])}.
     * ueberprueft auch, ob versucht wird, mehr als 4 Computergegner zu
     * erstellen. Erstellt automatisch Bezeichnungen
     * 
     * @param name
     *            fuer die computergesteuerten Spieler
     * 
     * @return Array aller angemeldeten menschlichen Spieler
     */
    @SuppressWarnings("resource")
    private static AIPlayer[] createAIP(int maxPossibleAIPlayers,
	    int iconCounter, int nextStartPoint) {
	Scanner eingabeString = new Scanner(System.in);
	System.out.print("Wie viele Computergegner sollen erstellt werden? ");
	int aipCount;
	while (true) {
	    String count = eingabeString.nextLine();
	    if (isInt(count)) {
		aipCount = Integer.parseInt(count);
		if (aipCount == 1 && maxPossibleAIPlayers == 4) {
		    System.out
			    .println("Es muessen mindestens 2 Spieler gegeneinander spielen.");
		} else {
		    break;
		}
	    } else {
		System.out.println("Bitte geben Sie eine positive Zahl ein.");
	    }
	}
	if (aipCount <= maxPossibleAIPlayers) {
	    AIPlayer[] aiplayers = new AIPlayer[aipCount];
	    int endpoint = 0;
	    AtomicLong idCounter = new AtomicLong();

	    for (int i = 0; i < aipCount; i++) {
		String name = "CPU_"
			+ String.valueOf(idCounter.getAndIncrement());
		System.out.printf("Name des %d. Computergegners: " + name,
			i + 1);
		System.out.println();
		switch (nextStartPoint) {
		case 0:
		    endpoint = 39;
		    break;
		case 10:
		    endpoint = 9;
		    break;
		case 20:
		    endpoint = 19;
		    break;
		case 30:
		    endpoint = 29;
		    break;
		}
		AIPlayer aiplayer = new AIPlayer(nextStartPoint, endpoint,
			((char) (i + iconCounter)), name);
		nextStartPoint += 10;		

		aiplayers[i] = aiplayer;
	    }
	    System.out.println();
	    return aiplayers;
	} else
	    System.out.println("Es koennen maximal " + maxPossibleAIPlayers
		    + " Computerspieler erstellt werden!");
	return createAIP(maxPossibleAIPlayers, iconCounter, nextStartPoint);
    }

    /**
     * Private Methode zum Ermitteln der gewuenschten Anzahl menschlicher
     * Spieler vom Typ {@link #HumanPlayer}. Wird nur innerhalb dieser Klasse
     * aufgerufen von {@link #mergePlayerTypes(HumanPlayer[], AIPlayer[])}.
     * ueberprueft auch, ob versucht wird, mehr als 4 Spieler zu erstellen.
     * 
     * @return Array aller angemeldeten menschlichen Spieler
     */
    @SuppressWarnings("resource")
    private static HumanPlayer[] createHP(int iconCounter) {
	Scanner eingabeString = new Scanner(System.in);
	System.out.println();
	System.out.print("Wie viele menschliche Spieler spielen mit? ");
	int hpCount;
	while (true) {
	    String count = eingabeString.nextLine();
	    if (isInt(count)) {
		hpCount = Integer.parseInt(count);
		break;
	    } else {
		System.out.println("Bitte geben Sie eine positive Zahl ein.");
	    }
	}
	if (hpCount <= 4) {
	    HumanPlayer[] hPlayers = new HumanPlayer[hpCount];
	    int startpoint = 0;
	    int endpoint = 39;
	    for (int i = 0; i < hpCount; i++) {
		System.out.printf("Name des %d. Spielers: ", i + 1);
		HumanPlayer hPlayer;
		// richtigen Namen eingeben
		while (true) {
		    String name = eingabeString.nextLine();
		    if (nameAvailable(hPlayers, name)) {
			hPlayer = new HumanPlayer(startpoint, endpoint,
				((char) (i + iconCounter)), name);
			break;
		    } else {
			System.out
				.println("Bitte geben Sie einen gueltigen Namen ein.");
		    }
		}
		startpoint += 10;
		/*
		 * if (i == 0) { endpoint = 40; } else { endpoint += 4; }
		 */
		switch (startpoint) {
		case 10:
		    endpoint = 9;
		    break;
		case 20:
		    endpoint = 19;
		    break;
		case 30:
		    endpoint = 29;
		    break;
		}
		hPlayers[i] = hPlayer;
	    }
	    System.out.println();
	    return hPlayers;
	} else
	    System.out
		    .println("Es koennen maximal 4 menschliche Spieler erstellt werden!");
	return createHP(iconCounter);
    }

    private static boolean nameAvailable(HumanPlayer[] player, String name) {
	if (name.length() != 0) {
	    for (int i = 0; i < player.length; i++) {
		if (player[i] != null) {
		    if (player[i].getName().equals(name)) {
			return false;
		    }
		}
	    }
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Methode zur Benutzerinformation, wenn keine Zuege moeglich sind
     */
    @SuppressWarnings("resource")
    public static void noMoves() {
	Scanner eingabe = new Scanner(System.in);
	System.out.println();
	System.out.println("Keine Zuege moeglich! (Mit Enter bestaetigen)");
	eingabe.nextLine();
    }

    /**
     * Private Methode zur Ueberpuefung der Eingaben auf Integer-Werte.
     * 
     * @param text
     *            der uebergebene Text {@literal String}
     * @return true, wenn uebergebener String eine Zahl ist; ansonsten false
     */
    private static boolean isInt(String text) {
	if (text != null) {
	    if (text.length() > 0) {
		boolean correct = true;
		for (int i = 0; i < text.length() && correct; i++) {
		    if (text.charAt(i) < '0' || text.charAt(i) > '9') {
			correct = false;
		    }
		}
		return correct;
	    }
	}
	return false;
    }

    /**
     * Gibt das Hauptmenue aus und leitet die Benutzereingabe weiter
     * 
     * @return int 1, 2 oder 3
     */
    @SuppressWarnings("resource")
    public static int printOptions() {
	Scanner eingabe = new Scanner(System.in);
	System.out.println("Welche Aktion moechten Sie durchfuehren?");
	System.out.println("1. Spielen");
	System.out.println("2. Regeln anzeigen");
	System.out.println("3. Spiel beenden");

	while (true) {
	    String text = eingabe.nextLine();
	    if (isInt(text)) {
		int number = Integer.parseInt(text);
		if (number < 1 || number > 3) {
		    System.out
			    .println("Bitte geben Sie eine Zahl zwischen 1 und 3 ein.");
		} else {
		    System.out.println("");
		    return number;
		}
	    } else {
		System.out
			.println("Bitte geben Sie eine Zahl zwischen 1 und 3 ein.");
	    }
	}
    }

    /**
     * Methode zur Konsolenausgabe der Spielregeln. Verweist auf
     * {@link RulesPrinter #printOutRules()}. Alle Spielregeln sind in
     * {@link RulesPrinter} ausgelagert, um einen moeglichen Austausch der
     * Regeln zu vereinfachen.
     */
    public static void printRules() {
	RulesPrinter.printOutRules();
    }
}
