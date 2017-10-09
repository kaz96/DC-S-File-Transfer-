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

	public final static int SOCKET_PORT = 13267;      //Port that will be used for the socket
	public final static String SERVER = "192.168.0.33";  //Local IP address of server. Change based on network being used.
 
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));	//Static buffered reader for reading user input from terminal

	public final static int FILE_SIZE = 5500000; // file size hard coded 5.5MB.

	public static void main (String [] args ) throws IOException {
		//Local vars and objects for transfer
		File FILE_TO_RECEIVED = null; 
		int bytesRead;
		int current = 0;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		Socket sock = null;
		//Try to connect to server
		try {
			while(true){
				System.out.println("Connecting to server...");
				//Create socket to connect to
				sock = new Socket(SERVER, SOCKET_PORT);
				System.out.println("Established connection successfully");
				//var to tell server which file to download
				int selectFile = 0;
				//Object holding output stream to send message to server
				OutputStream outStream = sock.getOutputStream();
				DataOutputStream dos = new DataOutputStream(outStream);
				//Print menu to console
				printMenu();
				//Read choice
				try{
					//read input as integer
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
			    //array of bytes created at specified size (above)
			    byte [] mybytearray  = new byte [FILE_SIZE];
			    byte [] decryptedArray  = new byte [FILE_SIZE];
			    //Object holding stream of data from server
			    InputStream is = sock.getInputStream();
			    fos = new FileOutputStream(FILE_TO_RECEIVED);
			    bos = new BufferedOutputStream(fos);
			    //Read bytes from server into variable
			    bytesRead = is.read(mybytearray,0,mybytearray.length);
			    //System.out.println("TESTING: Bytes Read: " + bytesRead);
			    //get first position in file
			    current = bytesRead;
				
				//read file until EOF
			    do {
			    	bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
			    	if(bytesRead >= 0) current += bytesRead;
			    } while(bytesRead > -1);
			    
			    //Decrypt file
			    decryptedArray = decryptByteArray(mybytearray);
	
				//Write data to file
			    bos.write(decryptedArray, 0 , current);
			    //Clear buffer
			    bos.flush();
			    System.out.println("File: " + FILE_TO_RECEIVED + " downloaded sucessfully. (" + current + " bytes read)");
			}
		}
		finally {
			//Close Streams and Socket
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
	
	//Decrypt message from server
	public static byte[] decryptByteArray(byte[] in){
		//Store arg containing byte array
		byte[] decryptedArray = in;
		//Shared key for crypto
		StringBuffer key = new StringBuffer("KazuyaDanielBrayden");
		//Int value of shared key
		int sharedKey = 0;
		//Print status to console
		System.out.println("Decrypting file...");
		//Calculate shared key
		for(int i = 0; i < key.length(); i++){
			sharedKey += (int)key.charAt(i);
			//System.out.println("TESTING: sharedkey = " + sharedKey);
		}
		//Decrypt bits
		for(int j = 0; j < in.length; j++){
			//System.out.println("TESTING: original byte = " + in[j]);
			decryptedArray[j] = (byte) ((int) in[j] - sharedKey);
			//System.out.println("TESTING: new byte = " + encryptedArray[j]);
		}
		//Print status to console
		System.out.println("File Decrypted");
		return decryptedArray;
	}

}