import java.text.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class MainGUI extends JPanel{
    private Tile[][] tiles;
    private JButton[][] tileButtons;
    private JButton resetButton = new JButton("Reset"), setDifficultyButton = new JButton("Set Difficulty"), highScoresButton = new JButton("High Scores");
    private JPanel grid, difficultyPanel, mainButtonsPanel, mainLabelsPanel;
    private Dimension gridSize, screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private int numBombs, x_axis, y_axis,  clickCount, flagCount, win_lose = 0, timePassed, hiddenCount, level;
    private ButtonListener buttonListener = new ButtonListener();
    private ClickListener clickListener = new ClickListener();
    private Random random = new Random();
    private JLabel bombCountLabel = new JLabel(), timerLabel = new JLabel();
    private javax.swing.Timer timer;
    private String bombImageName = "Images/bomb_small.png", redBombImageName = "Images/red_bomb_small.png", flagImageName = "Images/flag.png", 
    flagErrorImageName = "Images/flag_error.png", easyText = "Easy (6 X 6)", mediumText = "Medium (9 X 9)", hardText = "Hard (12 X 12)";
    private ImageIcon bombIcon, redBombIcon, flagIcon, flagErrorIcon;
    private JRadioButton easy, medium, hard;
    private ButtonGroup difficulty;
    private ArrayList<Pair> highScores = new ArrayList<Pair>();
    private JTextArea bestTimes = new JTextArea();
    private boolean gameStart = false, gameOpen = false;

    public MainGUI(){
        if(FileSystem.checkFilePresence()){
            bestTimes.setEditable(false);
            bestTimes.setOpaque(false);
            
            gridSize = new Dimension((int)(screenSize.width * 0.49),(int)(screenSize.height * 0.68));
            
            highScores = FileSystem.getData();

            setDifficulty();

            mainLabelsPanel = new JPanel(new GridLayout(1,0));
            mainLabelsPanel.setPreferredSize(new Dimension((int)(screenSize.width * 0.49),(int)(screenSize.height * 0.03)));

            mainButtonsPanel = new JPanel(new GridLayout(1,0));
            mainButtonsPanel.setPreferredSize(new Dimension((int)(screenSize.width * 0.49),(int)(screenSize.height * 0.03)));
            
            timer = new javax.swing.Timer(1000, new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent event){
                    timePassed ++;
                    timerLabel.setText("Time: " + timePassed + "s");
                }
            });

            setDifficultyButton.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent event){
                    timer.stop();
                    difficultyBox();
                }
            });
            
            resetButton.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent event){
                    timer.stop();
                    reset();
                }
            });

            highScoresButton.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent event){
                    timer.stop();
                    showHighScores();
                }
            });

            timerLabel.setBackground(Color.WHITE);
            timerLabel.setHorizontalAlignment(JLabel.CENTER);

            bombCountLabel.setBackground(Color.WHITE);
            bombCountLabel.setHorizontalAlignment(JLabel.CENTER);

            difficultyBox();
        }
        
    }

    public void difficultyBox(){
        if(inputBox("Set Difficulty", difficultyPanel) == JOptionPane.OK_OPTION){
            if(!easy.isSelected() && !medium.isSelected() && !hard.isSelected()){
                JOptionPane.showMessageDialog(null, "Select a difficulty first.", null, 0);
                difficultyBox();
            }
            else{
                if(easy.isSelected()){
                    x_axis = 6;
                    y_axis = 6;
                    level = 1;
                }
                else{
                    if(medium.isSelected()){
                        x_axis = 9;
                        y_axis = 9;
                        level = 2;
                    }
                    else{
                        x_axis = 12;
                        y_axis = 12;
                        level = 3;
                    }
                }
                gameOpen = true;
                startGame();
            }   
        }   
        else{
            if(!gameOpen){
                System.exit(0);
            }
            timer.start();
       }
    }

    public void setDifficulty(){
        difficulty = new ButtonGroup();
        difficultyPanel = new JPanel(new GridLayout(0,1));

        easy = new JRadioButton(easyText);
        medium = new JRadioButton(mediumText);
        hard = new JRadioButton(hardText);

        difficulty.add(easy);
        difficulty.add(medium);
        difficulty.add(hard);

        difficultyPanel.add(easy);
        difficultyPanel.add(medium);
        difficultyPanel.add(hard);
    }

    public void reset(){
        if(JOptionPane.showConfirmDialog(null,"Reset Grid?","Reset?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
            gameStart = false;
            startGame();
            repaint();
            revalidate();
        }
        else{
            timer.start();
        }
    }

    public int inputBox(String message, JPanel panel){
        return JOptionPane.showConfirmDialog(null, panel, message, JOptionPane.OK_CANCEL_OPTION);  
    }

    public void startGame(){
        timer.stop();
        removeAll();

        timePassed = 0;
        timerLabel.setText("Time: 0s");

        clickCount = 0;

        flagCount = 0;

        makeTiles();

        numBombs = genNumberOfBombs();

        bombCountLabel.setText("#Bombs: " + String.valueOf(numBombs));

        mainButtonsPanel.add(setDifficultyButton);
        mainButtonsPanel.add(highScoresButton);
        mainButtonsPanel.add(resetButton);

        mainLabelsPanel.add(timerLabel);
        mainLabelsPanel.add(bombCountLabel);

        add(mainLabelsPanel);
        add(mainButtonsPanel);
        add(grid);

        repaint();
        revalidate();
    }

    public void gameOver(){
        gameStart = false;
        timer.stop();

        if(hiddenCount == 0){
            revealAllTiles();
            if(isHighScore()){
                JOptionPane.showMessageDialog(null,"YOU WIN!\n\nNew Best Time: " + timePassed + " seconds");
            }
            else{
                JOptionPane.showMessageDialog(null,"YOU WIN!\n\nYour Time: " + timePassed + " seconds\n\nBest Time: " + highScores.get(level-1).getY() + "seconds");    
            }
        }
        else{
            revealAllTiles();
            if(highScores.size() > 0){
                if(highScores.get(level-1).getY() > 0){
                    JOptionPane.showMessageDialog(null,"YOU LOSE!\n\nBest Time: " + highScores.get(level-1).getY() + "seconds");    
                }
                else{
                    JOptionPane.showMessageDialog(null,"YOU LOSE!");
                }
            }
            else{
                JOptionPane.showMessageDialog(null,"YOU LOSE!");    
            }
        }

        reset();
    }

    public void makeTiles(){
        tiles = new Tile [x_axis][y_axis];
        tileButtons = new JButton [x_axis][y_axis];
        grid = new JPanel(new GridLayout(x_axis,y_axis));

        for(int j = 0; j < y_axis; j++){
            for(int i = 0; i < x_axis; i++){
                tiles[i][j] = new Tile();
                tileButtons[i][j] = new JButton();
                tileButtons[i][j].addActionListener(buttonListener);
                tileButtons[i][j].addMouseListener(clickListener);
                
                grid.add(tileButtons[i][j]);
                grid.setPreferredSize(gridSize);
            }
        }
    }

    public int genNumberOfBombs(){
        return (int)((x_axis * y_axis)* 0.2);
    }

    public void makeBombs(int pos_x, int pos_y){
        int bombCount = 0;

        while(bombCount < numBombs){
            for(int j = 0; j < y_axis; j++){
                for(int i = 0; i < x_axis; i++){
                    if(tileButtons[i][j].isEnabled() && !tiles[i][j].getIsMine() && !(i == pos_x && j == pos_y)){
                        if(((int)random.nextInt(6)) == 2){
                            tiles[i][j].setIsMine(true);
                            tiles[i][j].setNumber(-1);
                            bombCount++;
                            if(bombCount == numBombs){
                                hiddenCount = (x_axis * y_axis) - numBombs;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    public void setNumbers(){
        for(int j = 0; j < y_axis; j++){
            for(int i = 0; i < x_axis; i++){
                if(!tiles[i][j].getIsMine()){
                    tiles[i][j].setNumber(countSurroundingBombs(i,j));
                }
            }
        }
    }

    public boolean isSurroundingTile(int x, int y, int pos_x, int pos_y){
        if((x >= 0 && x < x_axis) && (y >= 0 && y < y_axis) && !(x == pos_x && y == pos_y)){
            return true;
        }
        else{
            return false;
        }
    }

    public int countSurroundingBombs(int pos_x, int pos_y){
        int bombCount = 0;

        for (int y = pos_y - 1; y < pos_y + 2; y++){
            for (int x = pos_x - 1; x < pos_x + 2; x++){
                if(isSurroundingTile(x,y,pos_x,pos_y)){
                    if(tiles[x][y].getIsMine()){
                        bombCount ++;
                    }
                }
            }
        }
        return bombCount;
    }

    public int countSurroundingBlanks(int pos_x, int pos_y){
        int blankCount = 0;

        for (int y = pos_y - 1; y < pos_y + 2; y++){
            for (int x = pos_x - 1; x < pos_x + 2; x++){
                if(isSurroundingTile(x,y,pos_x,pos_y)){
                    if(tiles[x][y].getNumber() == 0){
                        blankCount ++;
                    }
                }
            }
        }
        return blankCount;
    }

    public int countSurroundingFlags(int pos_x, int pos_y){
        int flags = 0;

        for (int y = pos_y - 1; y < pos_y + 2; y++){
            for (int x = pos_x - 1; x < pos_x + 2; x++){
                if(isSurroundingTile(x,y,pos_x,pos_y)){
                    if(tiles[x][y].getIsFlagged()){
                        flags ++;
                    }
                }
            }
        }
        return flags;
    }

    public void revealTile(int pos_x, int pos_y){
        if(tiles[pos_x][pos_y].getIsHidden()){
            if(!tiles[pos_x][pos_y].getIsFlagged()){
                tiles[pos_x][pos_y].setIsHidden(false);
                if(tiles[pos_x][pos_y].getIsMine() && !tiles[pos_x][pos_y].getIsFlagged()){
                    tileButtons[pos_x][pos_y].setIcon(bombIcon);
                }
                else{
                    int mines = tiles[pos_x][pos_y].getNumber();

                    if(tiles[pos_x][pos_y].getNumber() > 0){
                        tileButtons[pos_x][pos_y].setText(String.valueOf(mines));
                    }
                    else{
                        if(mines == 0){
                            tileButtons[pos_x][pos_y].setEnabled(false);
                            clearBlankTiles(pos_x,pos_y);
                        }
                    }

                    tileButtons[pos_x][pos_y].setBackground(Color.WHITE);

                    hiddenCount--;
                }
            }
            else{
                if(!tiles[pos_x][pos_y].getIsMine()){
                    tileButtons[pos_x][pos_y].setIcon(flagErrorIcon);
                }
            }
        }
        tiles[pos_x][pos_y].setIsHidden(false);
    }

    public void revealAllTiles(){
        for(int j = 0; j < y_axis; j++){
            for(int i = 0; i < x_axis; i++){
                revealTile(i,j);
            }
        }
        bombCountLabel.setText("#Bombs: 0");
    }

    public void clearBlankTiles(int pos_x, int pos_y){  

        ArrayList<Pair> blankSpots = clearSurrounding(pos_x,pos_y);
        
        for(int i = 0; i < blankSpots.size(); i++){
            blankSpots.addAll(clearSurrounding(blankSpots.get(i).getX(),blankSpots.get(i).getY()));
        }
    }

    public void setImages(){
        int height, width; 

        height = (int)(tileButtons[0][0].getHeight() * 0.7);
        width = (int)(tileButtons[0][0].getWidth() * 0.7);

        bombIcon = new ImageIcon(getClass().getResource(bombImageName));
        Image bombImg = bombIcon.getImage().getScaledInstance(width,height,java.awt.Image.SCALE_SMOOTH);
        bombIcon.setImage(bombImg);

        redBombIcon = new ImageIcon(getClass().getResource(redBombImageName));
        Image redBombImg = redBombIcon.getImage().getScaledInstance(width,height,java.awt.Image.SCALE_SMOOTH);
        redBombIcon.setImage(redBombImg);

        flagIcon = new ImageIcon(getClass().getResource(flagImageName));
        Image flagImg = flagIcon.getImage().getScaledInstance(width,height,java.awt.Image.SCALE_SMOOTH);
        flagIcon.setImage(flagImg);
        
        flagErrorIcon = new ImageIcon(getClass().getResource(flagErrorImageName));
        Image flagErrorImg = flagErrorIcon.getImage().getScaledInstance(width,height,java.awt.Image.SCALE_SMOOTH);
        flagErrorIcon.setImage(flagErrorImg);
    }

    public ArrayList<Pair> clearSurrounding(int pos_x, int pos_y){
        ArrayList<Pair> blanks = new ArrayList<Pair>();

        revealTile(pos_x,pos_y);

        if(tiles[pos_x][pos_y].getNumber() == 0){
            for (int y = pos_y - 1; y < pos_y + 2; y++){
                for (int x = pos_x - 1; x < pos_x + 2; x++){
                    if(isSurroundingTile(x,y,pos_x,pos_y) && !tiles[x][y].getIsMine() && tiles[x][y].getIsHidden()){
                        revealTile(x,y);

                        if(tiles[x][y].getNumber() == 0){
                            blanks.add(new Pair(x,y));
                        }
                    }
                }
            }
        }
        return blanks;
    }

    public void clearFreeTiles(int pos_x, int pos_y){
        if(verifyFlags(pos_x,pos_y)){
            for (int y = pos_y - 1; y < pos_y + 2; y++){
                for (int x = pos_x - 1; x < pos_x + 2; x++){
                    if(isSurroundingTile(x,y,pos_x,pos_y) && !tiles[x][y].getIsFlagged()){
                        revealTile(x,y);
                    }
                }
            }
            if(hiddenCount == 0){
                gameOver();
            }
        }
        else{
            gameOver();
        }
    }

    public boolean verifyFlags(int pos_x, int pos_y){
        for (int y = pos_y - 1; y < pos_y + 2; y++){
            for (int x = pos_x - 1; x < pos_x + 2; x++){
                if(isSurroundingTile(x,y,pos_x,pos_y) && !tiles[x][y].getIsMine() && tiles[x][y].getIsFlagged()){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isHighScore(){
        if ((highScores.size() > 0)){
            if((highScores.get(level-1).getY() == 0) || timePassed < highScores.get(level-1).getY()){
                highScores.get(level-1).setY(timePassed);

                FileSystem.storeData(highScores);

                return true;
            }
        }
        return false;
    }

    public void showHighScores(){
        bestTimes.setText(easyText + ": " + highScores.get(0).getY() + "\n" + mediumText + ": " + highScores.get(1).getY() + "\n" + hardText + ": " + highScores.get(2).getY());

        JOptionPane.showMessageDialog(null,bestTimes, "Best Times", 1);
    }

    private class ButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent event){
            for(int j = 0; j < y_axis; j++){
                for(int i = 0; i < x_axis; i++){
                    if(event.getSource() == tileButtons[i][j]){
                        clickCount++;
                        gameStart = true;

                        if(clickCount == 1){
                            makeBombs(i,j);
                            setImages();
                            setNumbers();
                            timer.start();
                        }   

                        if(!tiles[i][j].getIsFlagged()){
                            if(!tiles[i][j].getIsMine()){
                                if(tiles[i][j].getIsHidden()){
                                    revealTile(i,j);
                                    
                                    if(hiddenCount == 0)
                                    {
                                        gameOver();
                                    }
                                }
                                else{
                                    if(countSurroundingFlags(i,j) == tiles[i][j].getNumber() && hiddenCount > 0){
                                        clearFreeTiles(i,j);
                                    }
                                }
                            }
                            else{
                                if(hiddenCount > 0){
                                    tiles[i][j].setIsHidden(false);
                                    tileButtons[i][j].setIcon(redBombIcon);
                                    gameOver();     
                                }               
                            }       
                        }               
                    }
                }
            }
        }
    }

    private class ClickListener extends MouseAdapter{
        private int clickCount = 0;

        public void mousePressed(MouseEvent e){
            for(int j = 0; j < y_axis; j++){
                for(int i = 0; i < x_axis; i++){
                    if(e.getSource() == tileButtons[i][j] && e.getButton() == MouseEvent.BUTTON3 && (gameStart)){
                        if(!tiles[i][j].getIsFlagged() && tiles[i][j].getIsHidden() && (flagCount < numBombs)){
                            flagCount ++;
                            tiles[i][j].setIsFlagged(true);
                            tileButtons[i][j].setIcon(flagIcon);
                            bombCountLabel.setText("#Bombs: " + String.valueOf(numBombs - flagCount));
                        }
                        else{
                            if(tiles[i][j].getIsFlagged() && tiles[i][j].getIsHidden()){
                                flagCount --;
                                tiles[i][j].setIsFlagged(false);
                                tileButtons[i][j].setIcon(null);
							}
						}
					}
				}
			}
		}
	}
}