
/**
 * NetworkDemo is used to send a file over wifi to another computer
 * 
 * @author Logan Haser
 * @version 0.0.1
 */
import java.nio.file.*;
import static java.nio.file.StandardOpenOption.*;
import java.io.*;
import java.net.Socket;

public class NetworkDemo
{
    // instance variables - replace the example below with your own
    public static void main(String[] args) throws Exception {
		Socket server = new Socket(args[0], Integer.parseInt(args[1]));
		
        Path path = Paths.get(args[2]);
        InputStream input = new BufferedInputStream(
			Files.newInputStream(path, READ) 
        );
        
        DataOutputStream output = new DataOutputStream( 
			server.getOutputStream() 
		);
		output.writeUTF(args[2]); //send the file name
		
		byte[] b = new byte[input.available()];
		for(int i = 0; input.available() > 0; i++){
			b[i] = (byte) input.read();
		}
		output.writeInt(b.length); //so they know how many bytes we are transmitting
		
		output.write(b, 0, b.length);
		output.flush();
		input.close();
		output.close();
        
        System.out.println("Program finished");
        
    }

}
