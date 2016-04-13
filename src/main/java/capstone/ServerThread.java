package capstone;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.io.*;
import java.net.*;
import java.util.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

public class ServerThread implements Runnable   {
	
	String gameSessionID;
	//String testStr = "---------------";
	
	//ArrayList<String> pVal = new ArrayList<>();
	//ArrayList<String> aVal = new ArrayList<>();
	//ArrayList<String> events = new ArrayList<>();
	
	//double nextStop = 0;
	
	public ServerThread (String str)
	{
		gameSessionID = str;
		
		
		
	}
    public void run() {
		AmazonDynamoDBClient client = new AmazonDynamoDBClient();

        DynamoDB dynamoDB = new DynamoDB(client);

		Table gameTable = dynamoDB.getTable("game-test");
		final BufferedWriter out;
		
		//System.out.println(gameSessionID);
		//System.out.println(testStr);
		System.out.println("Made it here?");
        try {
			
			
            ServerSocket serverSocket = new ServerSocket(7777);
            Socket clientSocket = serverSocket.accept();
			System.out.println("Made it here?");
			
			CalculatorThread calculatorThreadInstance = new CalculatorThread(gameSessionID, clientSocket);
			//serverThreadInstance.setString("Hello");
			Thread thr = new Thread(calculatorThreadInstance);
			thr.start();
			
			
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
			
			out = new BufferedWriter (new OutputStreamWriter(clientSocket.getOutputStream()));
			//String outputLine = "PH-\n\r";
			
			/*
			JFrame main = new JFrame("Game Changes");
			main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			main.setResizable(false);
			main.setLayout(null);
			main.setPreferredSize(new Dimension(220, 300));
			main.setLocation(400, 200);

			// Heading: LOGIN
			JLabel heading = new JLabel("Game Changes");
			heading.setBounds(80, 20, 50, 20);
			main.add(heading);

			// Label Username
			JLabel username_label = new JLabel("Change: ");
			username_label.setBounds(5, 70, 80, 20);
			main.add(username_label);
			// Textfield Username
			final JTextField username_field = new JTextField();
			username_field.setBounds(70, 70, 120, 20);
			main.add(username_field);
			//this.name = username_field.getText();

			// Button Login
			JButton loginBtn = new JButton("Send");
			loginBtn.setBounds(40, 150, 120, 25);
			main.add(loginBtn);
			main.pack();
			main.setVisible(true);
			
			
			
				loginBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					//name = username_field.getText();
					//System.out.println(name); //IT WORKS
					try {
						out.flush();
						String outputLine = username_field.getText() + "\n\r";
						out.write(outputLine);
						System.out.println("Sending to the socket");
					}
					catch (IOException c) {
						System.err.println(c.toString());
						System.err.println("cannot get String");
						System.exit(1);
					}	
				}
			});
			
			
			*/
			
			//clientSocket.getOutputStream().write("Change Game".getBytes("UTF-8"));
			
			//nextStop = System.currentTimeMillis() + 5000;
            
            while (true) {
				//out.flush();
				//String outputLine = username_field.getText();
				//out.write(outputLine);
				//System.out.println("Sending to the socket");
				
				inputLine = in.readLine();
				

				String[] gameTemp = inputLine.split(",");
				
				String gameTimeStamp = gameTemp[0].trim();
				long l = Long.parseLong(gameTimeStamp);
				String gameEvent = gameTemp[1].trim();
				System.out.println("PutItem succeeded: " + inputLine);
				
				if (gameEvent.equals("Player Damaged"))
				{
					CalculatorThread.appendPlayerDamaged(gameEvent);
				}
				else if(gameEvent.equals("Player Scored"))
				{
					CalculatorThread.appendPlayerScored(gameEvent);
				}
							

                try {
					gameTable.putItem(new Item()
					.withPrimaryKey("id", gameSessionID, "time", l)
					.withString("event", gameEvent));
					System.out.println("PutItem succeeded: " + inputLine);
                } catch (Exception e) {
					System.err.println("Unable to add: " + inputLine);
					System.err.println(e.getMessage());
                    break;
                }

            }
        } catch (UnknownHostException e) {
            System.exit(1);
        } catch (IOException e) {
			System.err.println(e.toString());
            System.err.println("Couldn't get I/O for the connection");
            System.exit(1);
        }
    }
	
	/*
	public void setString(String str)
	{
		testStr = str;
	}
	
	public void appendP(String str)
	{
		pVal.add(str);
	}
	
	public void appendA(String str)
	{
		aVal.add(str);
	}
	*/
	
}
