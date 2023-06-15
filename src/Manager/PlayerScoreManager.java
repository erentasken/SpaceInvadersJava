package Manager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
public class PlayerScoreManager {
	private String fileName = "data.txt";
	private File file;
	private int count=1;
	
	public PlayerScoreManager() throws IOException {
		file = new File(fileName);
		if (!file.exists()) {
            file.createNewFile();
        }
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
	
	public boolean checkUser(String username, String password) { // it is not in usage!!!.
	    try {
	        Scanner scanner = new Scanner(file);
	        while (scanner.hasNextLine()) {
	            String line = scanner.nextLine();
	            String[] parts = line.split(" ");
	            if (parts.length >= 2) {
	                String storedUsername = parts[0];
	                String storedPassword = parts[1];
	                if (storedUsername.equals(username) && storedPassword.equals(password)) {
	                    scanner.close();
	                    return true; // User found in the file
	                }
	            }
	        }
	        scanner.close();
	    } catch (FileNotFoundException e) {
	        System.out.println("File not found.");
	    }
	    return false; // User not found in the file
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
