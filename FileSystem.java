import java.io.*; 
import java.util.*;
import java.lang.*;

public class FileSystem{
	private static File dataFile = new File ("data.dat");

	public FileSystem(){

	}

	public static boolean checkFilePresence(){
		try{	

			if (!dataFile.exists()){
				dataFile.createNewFile();

				FileWriter fw = new FileWriter (dataFile.getName());
	            BufferedWriter bw = new BufferedWriter (fw);
	            PrintWriter out = new PrintWriter (bw);

				out.println("1-0");
				out.println("2-0");
				out.println("3-0");

				out.close();
			}

			return true;
		}
		catch(Exception e){
			System.out.println(e);
		}
		return false;
	}

	public static boolean storeData(ArrayList<Pair> scores){
		try{
            if(!dataFile.exists())
            {
            	dataFile.createNewFile();
            }

            FileWriter fw = new FileWriter (dataFile.getName());
            BufferedWriter bw = new BufferedWriter (fw);
            PrintWriter out = new PrintWriter (bw);

            for(Pair score:scores){
            	out.println(score.getX() + "-" + score.getY());
            }
            out.close();
            return true;
		}
		catch(Exception e){
			System.out.println(e);
		}
		return false;
	}

	public static ArrayList<Pair> getData(){
		ArrayList<Pair> scores = new ArrayList<Pair>();

		try{
			Scanner fileScan = new Scanner(dataFile);

			while(fileScan.hasNext()){
				String[] readData = fileScan.nextLine().split("-");
				scores.add(new Pair(Integer.valueOf(readData[0]),Integer.valueOf(readData[1])));
			}
		}
		catch(Exception e){
			System.out.println(e);
		}

		return scores;
	}
}