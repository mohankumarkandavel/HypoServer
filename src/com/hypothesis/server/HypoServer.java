//package com.hypothesis.server;

import java.io.*;
import java.net.*;

class HypoServer {
	public static void main(String[] args) throws Exception {
		String fromClient;
		String toClient;
		String[] command = { "/bin/sh", "-c","cd /home/HypothesisTest/fejoa/fejoa; gradle test"};
		//String command = "ping -c 3 www.google.com";
		String line = "";

		ServerSocket server =  new ServerSocket(8888);
		System.out.println("wait for connection on port 8888");

		boolean run = true;
		while(run) {
			Socket client = server.accept();
			System.out.println("got connection on port 8888");
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			fromClient = in.readLine();
			System.out.println("received:"+ fromClient);

			switch(fromClient) {
				case "create and commit once" :
					System.out.println("1" + fromClient);
					Process proc = Runtime.getRuntime().exec(command);
					BufferedReader reader =  new BufferedReader(new InputStreamReader(proc.getInputStream()));
					while((line = reader.readLine()) != null) {
						System.out.println(line);
			 			out.println(line);
					}
					proc.waitFor();
					break;
				case "create and commit twice" :
					System.out.println("2" + fromClient);
					break;
				case "create and commit thrice" :
					System.out.println("3" + fromClient);
					break;
				default:
					System.out.println("4");
			}			

			if(fromClient.equals("Hello")){
				toClient = "olleH";
				System.out.println("send olleh");
				out.println(toClient);
				fromClient = in.readLine();
				System.out.println("received:" + fromClient);

				if(fromClient.equals("Bye")) {
					toClient = "eyB";
					System.out.println("send eyB");
					out.println(toClient);
					client.close();
					run = false;
					System.out.println("socket closed");
				}
			}

		}
		System.exit(0);
	}

}
