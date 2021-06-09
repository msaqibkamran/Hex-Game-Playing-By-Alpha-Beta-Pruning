package AiHex.players;

import AiHex.gameMechanics.Runner;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import AiHex.hexBoards.Board;
import AiHex.gameMechanics.Move;
import AiHex.hexBoards.BoardData;
import AiHex.hexBoards.GameBoard;
import AiHex.hexBoards.HexLocation;


// latest

public class AiPlayer implements Player {  // AI player class
    private Runner game = null;
    private int colour = 0;
    private int size;
    private int abPruning = 1;    // 1 for alphaBeta Pruning , 0 for minimax

    public AiPlayer(Runner game, int colour, int size) {  // contructor of AI Player
        this.game = game;
        this.colour = colour;
        this.size = size;
    }

    int maximum(int a, int b) {   // function to find maximum from two integers
        if(a >= b)
            return a;
        return b;
    }

    int minimum(int a, int b) {   // function to find minimum from two integers
        if(a <= b)
            return a;
        return b;
    }

    int getOpponentColor() {  // function to get color of opponent
        if(this.colour == Board.RED)
            return Board.BLUE;
        return Board.RED;
    }

    int emptyFactor(HexLocation[][] board) {  // function to get emtpy percentage of board
        int count = 0;
        for (int i = 0; i < size ; i++) {    // size == board size
            for (int j = 0; j < size; j++)
                if (board[i][j].getFill() == Board.BLANK) {
                    count = count + 1;
                }
        }
        return (count/(size*size))*100;   // percent of board empty
    }

    int getWinFactor(BoardData board) {    // function to get winning factor from board
        int count = 0;
        ArrayList<Integer> winPath = board.getWinningPath(this.colour, 0);
        for(int i=0; i<this.size; i++) {
            for(int j=0; j<size; j++) {
                if (board.getBoard()[i][j].getFill() == this.colour && winPath.contains(board.getBoard()[i][j].getNodeID()))
                    count =  count + 1;
            }
        }
        return ((count/winPath.size())*100);
    }

    int getAdjFactor(BoardData board) {  // function to get adjacent score from board
        int count = 0;
        for(int i=0; i<this.size; i++) {
            for(int j=0; j<size; j++) {
                if(board.getBoard()[i][j].getFill() != Board.BLANK && board.getBoard()[i][j].getFill() == this.colour) {
                    ArrayList<Point> neighbours = board.getBoardNeighbours(i, j, 0);
                    for(int k=0; k<neighbours.size(); k++) {
                        int x = neighbours.get(k).x;
                        int y = neighbours.get(k).y;
                        //if(x>=0 && y>=0 && x<this.size && y<this.size && board.getBoard()[x][y].getFill() == this.colour)
                        if(x>=0 && y>=0 && x<this.size && y<this.size && board.getBoard()[x][y].getFill() != Board.BLANK)
                            count =  count + 1;
                    }
                }
            }
        }
        return (count/(this.size*this.size))*100;
    }

    int getPathFactor(BoardData board) {  // function to get adjacent path from the board
        ArrayList<Integer> path;
        int score = 0;
        if(this.colour == 1) {
            for(int j=0; j<this.size; j++) {
                for(int i=0; i<this.size; i++) {
                    int start = board.getBoard()[i][0].getValue();
                    int end = board.getBoard()[j][size-1].getValue();
                    path = board.getPath(start, end, this.colour);
                    if(!path.isEmpty())
                        score = score + 1;
                }
            }
        }
        else if(this.colour == 2) {
            for(int j=0; j<this.size; j++) {
                for(int i=0; i<this.size; i++) {
                    int start = board.getBoard()[0][i].getValue();
                    int end = board.getBoard()[size-1][j].getValue();
                    path = board.getPath(start, end, this.colour);
                    if(!path.isEmpty())
                        score = score + 1;
                }
            }
        }
        return (score/(this.size*this.size))*100;
    }

    int evaluate(BoardData board) {    // evaluation function which returns score of a given move

        if(board.checkWin(this.colour))
            return 10000;

        int winFactor = getWinFactor(board);
        int adjFactor = getAdjFactor(board);
        int pathFactor = getPathFactor(board);
//        if(emptyFactor(board.getBoard()) > 30)
//            return adjFactor;
//        else
//            return maximum(pathFactor, adjFactor);

        int tempMax = maximum(winFactor, adjFactor);
        return maximum(tempMax, pathFactor);
    }

    boolean boardFull(HexLocation[][] board) {  // function to check if the board is full or not

        for (int i = 0; i < size ; i++) {    // size == board size
            for (int j = 0; j < size; j++)
                if (board[i][j].getFill() == Board.BLANK) {
                    return false;
                }

        }
        return true;
    }

    int miniMax(BoardData board, int depth, int isMaxTurn) {  // miniMAx pruning on game board with given depth

        if (depth == 0 || board.checkWin(Board.RED)|| board.checkWin(Board.BLUE))
            return evaluate(board);  // return score

        if(boardFull(board.getBoard()))
            return 0;

        if (isMaxTurn == 1) {
            int maxVal = Integer.MIN_VALUE;   // -infinity
            for (int i = 0; i < size ; i++) {    // size == board size
                for (int j = 0; j < size; j++)
                    if (board.getBoard()[i][j].getFill() == Board.BLANK) {
                        board.getBoard()[i][j].setFill(this.colour);
                        maxVal  = miniMax(board, depth-1,0);  // 0 => opposite (Min)
                        board.getBoard()[i][j].setFill(Board.BLANK);
                    }
            }
            return maxVal;
        }
        else {/* minimizing player */
            int minVal = Integer.MAX_VALUE;      // infinity
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++)
                    if (board.getBoard()[i][j].getFill() == Board.BLANK) {
                        board.getBoard()[i][j].setFill(getOpponentColor());
                        minVal = miniMax(board, depth-1, 1);  // 1 => oppsite(Max)
                        board.getBoard()[i][j].setFill(Board.BLANK);
                    }
            }
            return minVal;
        }

    }

    int alphaBetaPruning(BoardData board, int depth, int alpha, int beta, int isMaxTurn) {  // alpha beta pruning on game board

        if (depth == 0 || board.checkWin(Board.RED)|| board.checkWin(Board.BLUE) || boardFull(board.getBoard()))  // if depth is 0 or depth is not zero and someone win
            return evaluate(board);  // return score

        if (isMaxTurn == 1) {
            int maxVal = Integer.MIN_VALUE;   // -infinity
            for (int i = 0; i < size ; i++) {    // size == board size
                for (int j = 0; j < size; j++) {
                    if (board.getBoard()[i][j].getFill() == Board.BLANK) {
                        board.getBoard()[i][j].setFill(this.colour);
                        int val = alphaBetaPruning(board, depth - 1, alpha, beta, 0);  // 0 => opposite (Min)
                        maxVal = maximum(maxVal, val);
                        alpha = maximum(alpha, val);
                        board.getBoard()[i][j].setFill(Board.BLANK);
                        if (alpha >= beta)   // for inner loop
                            break;
                    }
                }
                if (alpha >= beta)   // for outer loop
                    break;
            }
            return maxVal;
        }
        else {/* minimizing player */
            int minVal = Integer.MAX_VALUE;      // infinity
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (board.getBoard()[i][j].getFill() == Board.BLANK) {
                        board.getBoard()[i][j].setFill(getOpponentColor());
                        int val = alphaBetaPruning(board, depth - 1, alpha, beta, 1);  // 1 => oppsite(Max)
                        minVal = minimum(minVal, val);
                        beta = minimum(beta, val);
                        board.getBoard()[i][j].setFill(Board.BLANK);
                        if (alpha >= beta)  // for inner loop
                            break;
                    }
                }
                if (alpha >= beta)  // for outer loop
                    break;
            }
            return minVal;
        }

    }

    Point getFirstPoint(HexLocation[][] board) { // function to get first empty place
        Point x = new Point();
        x.x = -1;
        x.y = -1;
        for (int i = 0; i < size ; i++) {    // size == board size
            for (int j = 0; j < size; j++)
                if (board[i][j].getValue() == Board.BLANK) {
                    x.x = i;
                    x.y = j;
                    return x;
                }
        }
        return x;
    }

    Point goodMove(BoardData board, int depth, int isMaxPlayer ) {   // function to check good move for the given player
        Point p = getFirstPoint(board.getBoard());
        int alpha = Integer.MIN_VALUE, max = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE, min = Integer.MAX_VALUE;
        int val = 0;

        int i = 0;
        int j = 0;
      //  if(emptyFactor(board.getBoard()) > 30)
        {
            Random rand = new Random();
            i = rand.nextInt(size-1);
            j = rand.nextInt(size-1);
        }
        if (isMaxPlayer == 1) {
            for (; i < size ; i++) {    // size == board size
                for (; j < size; j++)
                    if (board.getBoard()[i][j].getFill() == Board.BLANK) {
                        board.getBoard()[i][j].setFill(this.colour);
                        if(abPruning == 1) {
                            val  = alphaBetaPruning(board, depth, alpha, beta,0);
                        }
                        else {
                            val  = miniMax(board, depth,0);
                        }
                        board.getBoard()[i][j].setFill(Board.BLANK);
                        if(val > max){
                            max = val;
                            p.x = i;
                            p.y = j;
                        }
                    }
            }
        }
//        else {/* minimizing player */
//            for (; i < size ; i++) {    // size == board size
//                for (; j < size; j++)
//                    if (board.getBoard()[i][j].getFill() == Board.BLANK) {
//                        board.getBoard()[i][j].setFill(this.colour);
//                        if(abPruning == 1) {
//                            val  = alphaBetaPruning(board, depth, alpha, beta,1);
//                        }
//                        else {
//                            val  = miniMax(board, depth,1);
//                        }
//                        board.getBoard()[i][j].setFill(Board.BLANK);
//                        if(val < min){
//                            min = val;
//                            p.x = i;
//                            p.y = j;
//                        }
//
//                    }
//
//            }
//        }
        return p;
    }

    public Move getMove() {    // function to get move of given player

        switch (colour) {
            case Board.RED:
                System.out.print("Red move: ");
                break;
            case Board.BLUE:
                System.out.print("Blue move: ");
                break;
        }

        BoardData currentGameBoard = game.getBoard().getData();
        Point p = goodMove(currentGameBoard, 5, 1);
//        try {
//            Thread.sleep(50);                         // for both AI players
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        Move move = new Move(colour, p.x, p.y);

        return move;
    }

    public ArrayList<Board> getAuxBoards() {
        return new ArrayList<Board>();
    }
}
