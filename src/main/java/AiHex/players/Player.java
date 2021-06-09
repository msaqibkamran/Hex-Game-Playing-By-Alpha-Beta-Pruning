package AiHex.players;

import java.util.ArrayList;
import AiHex.gameMechanics.Move;
import AiHex.hexBoards.Board;

public interface Player {

	public static final int CLICK_PLAYER = 3;
	public static final int AI_PLAYER = 4;
	public static final String CLICK_DEFAULT_ARGS = "n/a";
	public static final String CLICK_DEFAULT_ARGS2 = "n/a";

	public static final String[] playerList = {"Human Player", "AI Player"};
	public static final int[] playerIndex = {  CLICK_PLAYER, AI_PLAYER };

  public static final String[] argsList = { CLICK_DEFAULT_ARGS, CLICK_DEFAULT_ARGS2};
	public Move getMove();

	public ArrayList<Board> getAuxBoards();
}
