
/**
 * NetworkDemo is used to send a file over wifi to another computer
 * 
 * @author Logan Haser
 * @version 0.0.1
 */
import java.nio.file.*;

import static java.nio.file.StandardOpenOption.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class NetworkDemo extends javafx.application.Application {
	private File currentFile;

	// instance variables - replace the example below with your own
    public static void main(String[] args) throws Exception {
    	launch(args);
    }

	@Override
	public void start(final Stage primaryStage) throws Exception {
		configureScene(primaryStage);
		GridPane pane = new GridPane();
		final FileChooser fileChooser = new FileChooser();
		final TextField fileName = new TextField("The file name");
			fileName.setDisable(true);
		final TextField ipText = new TextField("Set the ip address");
		final TextField portText = new TextField("8000");
		final TextArea feedback = new TextArea();
		
		Button selectFile = new Button("Choose File");
			selectFile.setOnMouseClicked(
					new EventHandler<MouseEvent>(){
						
						public void handle(MouseEvent e){
							currentFile = fileChooser.showOpenDialog(primaryStage);
							fileName.setText(currentFile.getName());
						}
						
					}
			);
		Button receiveFile = new Button("Receive File");
		Button sendFile = new Button("Send File");

		sendFile.setOnMouseClicked(
			new EventHandler<MouseEvent>(){
				public void handle(MouseEvent e){
					if(currentFile != null){
						if(  ipText.getText().matches( "((\\d{1,3}\\.){3}\\d{1,3}||localhost)" ) && 
						   portText.getText().matches( "(\\d{2,}||[1-9]{1})" ) ){
							String ip = ipText.getText();
							int port = Integer.parseInt(
								portText.getText()
							);
							Socket server;
							try {
								server = new Socket( ip, port);
								System.out.println(currentFile.getPath());
						        Path path = Paths.get(currentFile.getPath());
						        InputStream input = new BufferedInputStream(
						        		Files.newInputStream(path, READ) 
						        );
						        DataInputStream dataInput = new DataInputStream(
						        	server.getInputStream()
						        );
						        DataOutputStream output = new DataOutputStream( 
									server.getOutputStream() 
								);
						        
								output.writeUTF(currentFile.getName()); //send the file name
								char goAhead = dataInput.readChar();
								if(goAhead == 'y'){
									long size = currentFile.length();

									output.writeLong(size); //so they know how many bytes we are transmitting
									for(long i = 0; i < size; i++){
										output.writeByte( (byte) input.read());
									}
								}
								output.flush();
								input.close();
								dataInput.close();
								output.close();
						        
								feedback.appendText("File sent\n");
						        server.close();
							} catch (UnknownHostException e1) {
								e1.printStackTrace();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							
							
						} else {
							feedback.appendText("You entered an incorrect IP Address or port!\n");
						}
						
					}else{
						feedback.appendText("Something went wrong with selecting the file\nMake sure to select the file first");						
						
					}
					
				}
			}
		);
		
		receiveFile.setOnMouseClicked(
			new EventHandler<MouseEvent>(){
					public void handle(MouseEvent e){
						if(  portText.getText().matches( "(\\d{2,}||[1-9]{1})" ) ){
	
							int port = Integer.parseInt(
								portText.getText()
							);
	
							ServerSocket server;
							try {
								                 server = new ServerSocket(port);
								Socket           client = server.accept();
								DataInputStream   input = new DataInputStream(
									client.getInputStream()
								);
								DataOutputStream wifiOutput = new DataOutputStream(
									client.getOutputStream()
								);
								
								String fileName = input.readUTF();
								Alert confirm = new Alert(AlertType.CONFIRMATION);
								confirm.setTitle("Incoming file: " + fileName);
								confirm.setHeaderText("File (" + fileName + ") from ip: " + client.getInetAddress()); 
								confirm.setContentText("Do you wish to accept the file " + fileName + " and download a local copy?");
								Optional<ButtonType> result = confirm.showAndWait();
								if(result.get() == ButtonType.OK){
									wifiOutput.writeChar('y');
									Path new_file = Paths.get(fileName);//you won't notice any changes if you run the server and client in the same directory
									
									DataOutputStream  output = new DataOutputStream(
										Files.newOutputStream(new_file, CREATE)
									);
									
									long size = input.readLong();
									for(long i = 0; i < size; i++){
										output.writeByte((byte) input.readByte() );
									}
		
									output.close();
									feedback.appendText("File received\n");
								} else {
									wifiOutput.writeChar('n');
									feedback.appendText("File rejected by user\n");
								}
								input.close();
								server.close();
								client.close();
								wifiOutput.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} else {
							feedback.appendText("You entered an incorrect IP Address or port!\n");
						}
														
					}
				}
		);
		pane.add(ipText, 0, 0);
		pane.add(portText, 1, 0);
		pane.add(fileName, 0, 2);
		pane.add(selectFile, 0, 4);
		pane.add(receiveFile, 1, 4);
		pane.add(sendFile, 2, 4);
		pane.add(feedback, 0, 5);
		Scene defaultScene = new Scene(pane, 790, 500);
		primaryStage.setScene(defaultScene);
		primaryStage.show();
	}  
	
	private void setupServer(){
		/*
		server = new Socket(args[0], Integer.parseInt(args[1]));
		
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
        server.close();
        */
	}
	
	private void configureScene(Stage stage){

	}

}
