package AiHex.hexBoards;

public class HexLocation {

  private int nodeId;
  private int season;
  private int value;
  private int fill = 0;

  public HexLocation(int nodeId) {
    this.nodeId = nodeId;
    this.value = Board.BLANK;
    this.fill = 0;
  }

  public HexLocation(int nodeId, int season, int value) {
    this.nodeId = nodeId;
    this.season = season;
    this.value = value;
    this.fill = 0;
  }

  public int getNodeID() {
    return nodeId;
  }

  public void setNodeID(int nodeID) {
    this.nodeId = nodeID;
  }

  public int getSeason() {
    return season;
  }

  public void setSeason(int season) {
    this.season = season;
  }

  public int getFill() {
    return fill;
  }

  public void setFill(int fill) {
    this.fill = fill;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
    this.fill = 1;
  }

  @Override
  public HexLocation clone() {
    return new HexLocation(nodeId, season, value);
  }
}
