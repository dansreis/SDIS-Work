package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.plaf.SliderUI;


public class Server {
	static HashMap<String,String> database = new HashMap<String,String>();
	
	public static void main(String[] args) throws IOException{
		if (args.length != 1) {
			System.out.println("Usage: java Server <port_number>");
			return;
		}
		int portNumber = Integer.parseInt(args[0]);
		
		
		
		String[] splitedstring;
		database.put("33-DX-95", "DanielReis");
		database.put("98-RI-11", "ManuelaSilva");
		
		while(true){
			//recebe
			DatagramSocket socket = new DatagramSocket(portNumber);
			byte[] rbuf = new byte[1024];
			DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
			System.out.println("SERVER: Receiving..");
			socket.receive(packet);
			
			String received = new String(packet.getData(), 0, packet.getLength());
			System.out.println("RECEBIDO: "+ received);
			
			//fazer o parse da resposta
			splitedstring = received.split(" ");
			String operation = splitedstring[0];
			String res = null;
			switch(operation){
				case "REGISTER":
					if(checkLicence(splitedstring[1])){
						boolean status = addLicentPlate(splitedstring[2],splitedstring[1]);
						if(status)
							res = ""+database.size();
						else
							res = "Invalid";
					}
					else{
						System.out.println("Invalid licence plate");
						res = "Invalid license plate";
					}
						
				break;
				case "LOOKUP":
					if(checkLicence(splitedstring[1])){
						res = findPlate(splitedstring[1]);
						
					}else{
						System.out.println("Invalid licence plate");
						res = "Invalid license plate";
					}
				break;
				default:
			}
			
			System.out.println("Sending: "+res);
			System.out.println(database);
			
			//envia
			byte[] sbuf = res.getBytes();
			socket.send( new DatagramPacket(sbuf, sbuf.length,packet.getAddress(),packet.getPort()));
			socket.close();
			
		}

	}

	public static String findPlate(String plate_number){
		String res = database.get(plate_number);
		System.out.println("pessoa: "+res);
		if(res == null)
			return  "NOT_FOUND";
		else 
			return res;
	}
	
	public static Boolean addLicentPlate(String ownerName,String plateNumber){
		if(database.get(plateNumber) != null)
			return false;
		database.put(plateNumber, ownerName);
		return true;
	}

	public int getNumPlates() {
		return database.size();
	}
	
	public static boolean checkLicence(String license){
		System.out.println("Licenca: -"+license+"|");
		System.out.println(database);
		Pattern p = Pattern.compile("[0-9]{2}-[A-Z]{2}-[0-9]{2}");
		Matcher m = p.matcher(license);
		boolean b = m.matches();
		System.out.println(p);
		System.out.println(m);
		System.out.println(b);
		return b;
	}


}






















/*
		Set set = database.entrySet();
		Iterator iterator = set.iterator();
		
		while(iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry)iterator.next();
			
			System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
			System.out.println(mentry.getValue());
		}
		return null;
 */
