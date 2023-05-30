import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
public class PlayerScoreManager {
	String fileName = "data.txt";
	File file;
	int count=1;
	public PlayerScoreManager() throws IOException {
		file = new File(fileName);
	}
	public void writeToFile(String playerName, int playerScore) throws IOException {
		try {
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(count++ +"."+playerName + " " + playerScore + "\n");
            writer.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
	}
	public String readTheFile() {
		String fileOutput=null;
        try {
        	if(file==null)return null;
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String fileData = scanner.nextLine();
                if(fileOutput == null) {
                	fileOutput = fileData + "\n";
                	continue;
                }
                fileOutput += fileData + "\n";
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
        return fileOutput;
	}
	
	public void deleteTheFile() {
		if(file.delete()) {
			System.out.println("file deleted");
		}
	}
}
