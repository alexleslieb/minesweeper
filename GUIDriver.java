import javax.imageio.*;
import java.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class GUIDriver
{
    public static void main(String[] arg)
    {
        JFrame minesweeper = new JFrame ("Minesweeper");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = new Dimension((int)(screenSize.width * 0.5),(int)(screenSize.height * 0.8));

        String icon = "Images/icon.png";
        ImageIcon img = new ImageIcon(icon);
        minesweeper.setIconImage(img.getImage());
        
        minesweeper.setBounds(((int)(screenSize.width * 0.5)) - ((int)(frameSize.width * 0.5)),((int)(screenSize.height * 0.5)) - ((int)(frameSize.height * 0.5)),frameSize.width,frameSize.height);
        minesweeper.setResizable(false);
        
        minesweeper.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);      
        minesweeper.addWindowListener(new java.awt.event.WindowAdapter() 
        {
           @Override
           public void windowClosing(java.awt.event.WindowEvent we)
           { 
            int PromptResult = JOptionPane.showConfirmDialog(null,"Are you sure you want to exit?","Exit?", JOptionPane.YES_NO_OPTION);
             if(PromptResult==JOptionPane.YES_OPTION)
             {
               System.exit(0);
             }
           }
        });

        MainGUI gui = new MainGUI();
        
        minesweeper.getContentPane().add(gui);
        
        minesweeper.setVisible(true);
    }
}