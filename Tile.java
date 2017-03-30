public class Tile{
	int number;
	boolean isMine;
	boolean isHidden;
	boolean isFlagged;

	public Tile(){
		isHidden = true;
		isMine = false;
		isFlagged = false;
	}

	public void setNumber(int num){
		number = num;
	}

	public void setIsMine(boolean mine){
		isMine = mine;
	}

	public void setIsHidden(boolean hidden){
		isHidden = hidden;
	}

	public void setIsFlagged(boolean flagged){
		isFlagged = flagged;
	}


	public int getNumber(){
		return number;
	}

	public boolean getIsMine(){
		return isMine;
	}

	public boolean getIsHidden(){
		return isHidden;
	}

	public boolean getIsFlagged(){
		return isFlagged;
	}
}