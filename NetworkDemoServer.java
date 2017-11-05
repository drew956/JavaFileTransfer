import java.nio.file.*;
import static java.nio.file.StandardOpenOption.*;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;


public class NetworkDemoServer {
	
	public static void main(String[] args) throws Exception {
		
		ServerSocket     server = new ServerSocket(Integer.parseInt(args[0]));
		Socket           client = server.accept();
		DataInputStream   input = new DataInputStream(
			client.getInputStream()
		);
		
		
		String fileName = input.readUTF();
		Path new_file = Paths.get(fileName);//you won't notice any changes if you run the server and client in the same directory
		
		DataOutputStream  output = new DataOutputStream(
			Files.newOutputStream(new_file, CREATE)
		);
		
		int size = input.readInt();
		byte[] b = new byte[size];

		input.read(b);
		output.write(b, 0, b.length);
		output.close();
		System.out.println("Program ended");
		System.out.println(b);
		
	}
	
	
	
}
