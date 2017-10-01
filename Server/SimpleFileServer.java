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

  public final static int SOCKET_PORT = 13267;  // you may change this
  public final static String IMG_FILE = "C:/Users/BJ/Google Drive/Semester 2 2017/Data Communications and Security/Assignment 2/TCP_File_Server/files/leroy.jpg";  //Image file to send
  public final static String MP3_FILE = "C:/Users/BJ/Google Drive/Semester 2 2017/Data Communications and Security/Assignment 2/TCP_File_Server/files/RickRoll.mp3";  //MP3 file to send
  public final static String CSV_FILE = "C:/Users/BJ/Google Drive/Semester 2 2017/Data Communications and Security/Assignment 2/TCP_File_Server/files/SampleCSV.csv";  //CSV file to send
  public final static String PDF_FILE = "C:/Users/BJ/Google Drive/Semester 2 2017/Data Communications and Security/Assignment 2/TCP_File_Server/files/SamplePDF.pdf";  //PDF file to send
  public final static String MP4_FILE = "C:/Users/BJ/Google Drive/Semester 2 2017/Data Communications and Security/Assignment 2/TCP_File_Server/files/SampleVid.mp4";  //MP4 file to send
  

  public static void main (String[] args) throws IOException {
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    OutputStream os = null;
    ServerSocket servsock = null;
    Socket sock = null;
    File fileSending = null;
    int fileRequested = 0;
    
    try {
      servsock = new ServerSocket(SOCKET_PORT);
      while (true) {
        System.out.println("Waiting...");
        try {
          sock = servsock.accept();
          System.out.println("Accepted connection : " + sock);
          while(fileRequested == 0){
        	  //Input stream
        	  InputStream is = sock.getInputStream();
        	  System.out.println("Input stream open");
        	  //Data input stream
        	  DataInputStream dis = new DataInputStream(is);
        	  System.out.println("Data stream open");
        	  System.out.println("Waiting for selection....");
        	  fileRequested = dis.readInt();
        	  System.out.println("File Requested : " + fileRequested);
          }
          // send file
          switch(fileRequested){
          	case 1: fileSending = new File(IMG_FILE);
          	break;
          	case 2: fileSending = new File(MP3_FILE);
          	break;
          	case 3: fileSending = new File(CSV_FILE);
          	break;
          	case 4: fileSending = new File(PDF_FILE);
          	break;
          	case 5: fileSending = new File(MP4_FILE);
          	break;
          }
          byte [] mybytearray  = new byte [(int)fileSending.length()];
          fis = new FileInputStream(fileSending);
          bis = new BufferedInputStream(fis);
          bis.read(mybytearray,0,mybytearray.length);
          os = sock.getOutputStream();
          //Print info on file being sent
          System.out.println("Sending " + fileSending.getName() + "(" + mybytearray.length + " bytes)");
          os.write(mybytearray,0,mybytearray.length);
          os.flush();
          System.out.println("Done.");
          //reset selection
          fileRequested = 0;
        }
        finally {
          if (bis != null) bis.close();
          if (os != null) os.close();
          if (sock!=null) sock.close();
        }
      }
    }
    finally {
      if (servsock != null) servsock.close();
    }
  }
}