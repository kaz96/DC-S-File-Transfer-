import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class SimpleFileClient {

	public final static int SOCKET_PORT = 13267;      // you may change this
	public final static String SERVER = "192.168.0.33";  // bryaden's local IP
 
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));	//For reading user input from terminal

	public final static int FILE_SIZE = 5500000; // file size hard coded 5.5MB

	public static void main (String [] args ) throws IOException {
		File FILE_TO_RECEIVED = null;  // set name when file is selected
		int bytesRead;
		int current = 0;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		Socket sock = null;
		try {
			while(true){
				//Connect to server
				System.out.println("Connecting to server...");
				sock = new Socket(SERVER, SOCKET_PORT);
				System.out.println("Established connection successfully");
				//Send message to server choosing which file to send
				int selectFile = 0;
				OutputStream outStream = sock.getOutputStream();
				DataOutputStream dos = new DataOutputStream(outStream);
				//Print menu to console
				printMenu();
				//Read choice
				try{
					selectFile = Integer.parseInt(br.readLine());
					//Validate input
					while(selectFile < 1 || selectFile > 5){
						System.out.println("Please enter a value (1-5)");
						selectFile = Integer.parseInt(br.readLine());  
					}
					//Set output file based on selection
					switch(selectFile){
					    case 1: FILE_TO_RECEIVED = new File("Server Downloaded Files/download.jpg");
					    break;
					    case 2: FILE_TO_RECEIVED = new File("Server Downloaded Files/download.mp3");
					    break;
					    case 3: FILE_TO_RECEIVED = new File("Server Downloaded Files/download.csv");
					    break;
					    case 4: FILE_TO_RECEIVED = new File("Server Downloaded Files/download.pdf");
					    break;
					    case 5: FILE_TO_RECEIVED = new File("Server Downloaded Files/download.mp4");
					    break;
					}
					//Send choice to server
					dos.writeInt(selectFile);
				}catch (Exception e){
					System.out.println("ERROR: Incorrect input");
				}
				System.out.println("Requesting file...");
			    //Create directory for files
			    boolean successful =  (new File("Server Downloaded Files")).mkdir();
			    if (successful){	System.out.println("Directory was created successfully");	}	//Directory created successfully
			    else{	System.out.println("Failed trying to create the directory");	}	//Directory failed to create
			     
			    //Receive file from server
			    byte [] mybytearray  = new byte [FILE_SIZE];
			    InputStream is = sock.getInputStream();
			    fos = new FileOutputStream(FILE_TO_RECEIVED);
			    bos = new BufferedOutputStream(fos);
			    bytesRead = is.read(mybytearray,0,mybytearray.length);
			    //System.out.println("Bytes Read: " + bytesRead);
			    current = bytesRead;
	
			    do {
			    	bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
			    	if(bytesRead >= 0) current += bytesRead;
			    } while(bytesRead > -1);
	
			    bos.write(mybytearray, 0 , current);
			    bos.flush();
			    System.out.println("File: " + FILE_TO_RECEIVED + " downloaded sucessfully. (" + current + " bytes read)");
			}
		}
		finally {
			if (fos != null) fos.close();
			if (bos != null) bos.close();
			if (sock != null) sock.close();
		}
	}
  
	//Print menu to console
	public static void printMenu(){
		System.out.println("Select a file to download from server: ");
		System.out.println("1) Image File");
		System.out.println("2) Music File");
		System.out.println("3) CSV File");
		System.out.println("4) PDF File");
		System.out.println("5) Video File");
	}

}