package AiHex.hexBoards;
import AiHex.gameMechanics.Move;
import AiHex.gameMechanics.InvalidMoveException;
import java.awt.Point;
import java.awt.Toolkit;

public class GameBoard extends AbstractBoard{

	private Point selected;
	protected BoardData data;

	public BoardData getData() {
		return data;
	}

	public GameBoard(int size) {
    this.name = "Game";
		this.data = new BoardData(size);
		this.size = size;
	}

	public GameBoard(int size, BoardData data){
		this.size = size;
		this.data = data;
	}

	public boolean makeMove(Move move) throws InvalidMoveException {

    	boolean moveAccepted = false;
		int x = move.getX();
		int y = move.getY();
		int colour = move.getColour();
		if (x < 0 || x > size-1 || y < 0 ||y > size-1) {
     Toolkit.getDefaultToolkit().beep();
			throw new InvalidMoveException(
					"Coordinates outside the play area!", move,
					InvalidMoveException.OUTSIDE_BOARD);
		} else if (data.get(move.getX(), move.getY()).getValue() == Board.BLANK ) {
			data.set(move.getX(), move.getY(), colour);
      moveAccepted = true;
      changeOccured = true;
		} else {
      Toolkit.getDefaultToolkit().beep();
			throw new InvalidMoveException("That hex is not blank!", move,
					InvalidMoveException.ALREADY_TAKEN);
		}
		return moveAccepted;
	}

  public boolean checkwin(int player){
    return this.data.checkWin(player);
  }

  @Override
	public int get(int x, int y) {
		return data.get(x, y).getValue();
	}

	public Point getSelected() {
		return selected;
	}

	public void setSelected(Point selected) {
		this.selected = selected;
	}

	public void setSelected(int x, int y) {
		this.setSelected(new Point(x,y));
	}


  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    changeOccured = true;
    this.name = name;
  }

}
