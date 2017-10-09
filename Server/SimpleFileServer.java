import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleFileServer {

	public final static int SOCKET_PORT = 13267;  //Port that will be used for the socket
	//Locations for files on server. Change based on absolute locations on server
	public final static String IMG_FILE = "C:/Users/brayd/Google Drive/Semester 2 2017/Data Communications and Security/Assignment 2/TCP_File_Server/files/leroy.jpg";  //Image file to send
	public final static String MP3_FILE = "C:/Users/brayd/Google Drive/Semester 2 2017/Data Communications and Security/Assignment 2/TCP_File_Server/files/RickRoll.mp3";  //MP3 file to send
	public final static String CSV_FILE = "C:/Users/brayd/Google Drive/Semester 2 2017/Data Communications and Security/Assignment 2/TCP_File_Server/files/SampleCSV.csv";  //CSV file to send
	public final static String PDF_FILE = "C:/Users/brayd/Google Drive/Semester 2 2017/Data Communications and Security/Assignment 2/TCP_File_Server/files/SamplePDF.pdf";  //PDF file to send
	public final static String MP4_FILE = "C:/Users/brayd/Google Drive/Semester 2 2017/Data Communications and Security/Assignment 2/TCP_File_Server/files/SampleVid.mp4";  //MP4 file to send
  

	public static void main (String[] args) throws IOException {
		//Vars and objects for transfer
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;
		ServerSocket servsock = null;
		Socket sock = null;
		File fileSending = null;
		int fileRequested = 0;
    
		try {
			//Create server socket
			servsock = new ServerSocket(SOCKET_PORT);
			System.out.println("Server Started...");
			while (true) {
				try {
					//Accept connection on socket
					sock = servsock.accept();
					System.out.println("Accepted connection to client : " + sock);
					//While no selection from client
					while(fileRequested == 0){
						//Input stream
						InputStream is = sock.getInputStream();
						//System.out.println("TESTING: Input stream open");
						//Data input stream
						DataInputStream dis = new DataInputStream(is);
						//System.out.println("TESTING: Data stream open");
						System.out.println("Waiting for selection....");
						//Read selection from client
						fileRequested = dis.readInt();
					}
					//send file
					switch(fileRequested){
						case 1: fileSending = new File(IMG_FILE);
						System.out.println("File Requested : leroy.jpg");
						break;
						case 2: fileSending = new File(MP3_FILE);
						System.out.println("File Requested : RickRoll.mp3");
						break;
						case 3: fileSending = new File(CSV_FILE);
						System.out.println("File Requested : SampleCSV.csv");
						break;
						case 4: fileSending = new File(PDF_FILE);
						System.out.println("File Requested : SamplePDF.pdf");
						break;
						case 5: fileSending = new File(MP4_FILE);
						System.out.println("File Requested : SampleVid.mp4");
						break;
					}
					//Create byte arrays with the size of the file selected
					byte [] mybytearray  = new byte [(int)fileSending.length()];
					byte [] encbytearray  = new byte [(int)fileSending.length()];
					//Create streams to read file
					fis = new FileInputStream(fileSending);
					bis = new BufferedInputStream(fis);
					//Read file
					bis.read(mybytearray,0,mybytearray.length);
					//get output stream to send messages to client
					os = sock.getOutputStream();
					//Encrypt bytes to be sent
					encbytearray = encryptByteArray(mybytearray);
					//Print info on file being sent
					System.out.println("Sending " + fileSending.getName() + "(" + encbytearray.length + " bytes)");
					//Send file to client
					os.write(encbytearray,0,encbytearray.length);
					//Clear buffer in output stream
					os.flush();
					System.out.println("File transfer complete");
					//reset selection
					fileRequested = 0;
				}
				finally {
					//close streams and socket
					if (bis != null) bis.close();
					if (os != null) os.close();
					if (sock!=null) sock.close();
				}
			}
		}
		finally {
			//Close server socket
			if (servsock != null) servsock.close();
		}
	}
	
	public static byte[] encryptByteArray(byte[] in){
		//Byte array created to hold file being encrypted
		byte[] encryptedArray = in;
		//Shared key for cypto
		StringBuffer key = new StringBuffer("KazuyaDanielBrayden");
		//Var to hold integer value fo shared key
		int sharedKey = 0;
		//Print status to console
		System.out.println("Encrypting file...");
		//Calculate shared key
		for(int i = 0; i < key.length(); i++){
			sharedKey += (int)key.charAt(i);
			//System.out.println("TESTING: sharedkey = " + sharedKey);
		}
		//Encrypt bits
		for(int j = 0; j < in.length; j++){
			//System.out.println("TESTING: original byte = " + in[j]);
			encryptedArray[j] = (byte) ((int) in[j] + sharedKey);
			//System.out.println("TESTING: new byte = " + encryptedArray[j]);
		}
		//Print status to console
		System.out.println("File Encrypted");
		return encryptedArray;
	}
}