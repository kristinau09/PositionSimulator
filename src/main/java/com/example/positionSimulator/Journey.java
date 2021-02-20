package com.example.positionSimulator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.JmsTemplate;

/*
 * A callable that sends messages to a queue periodically - representing the journey f a delivery vehicles
 */

public class Journey implements Callable<Object> {
	
	private List<String> positions;
	private String vehicleName;
	private JmsTemplate jmsTemplate;

	public Journey(String vehicleName, List<String> positions, JmsTemplate jmsTemplate) {
		super();
		this.positions = Collections.unmodifiableList(positions);
		this.vehicleName = vehicleName;
		this.jmsTemplate = jmsTemplate;
	}

	@Override
	public Object call() throws InterruptedException {
		
		for(String nextReport: this.positions) {
			
			String[] data = nextReport.split("\"");
			String latitude = data[1];
			String longitude = data[3];
			
			//Spring will convert a HashMap into a message using the default MessageConverter
			HashMap<String, String> positionMessage = new HashMap<>();
			positionMessage.put("vehicle", vehicleName);
			positionMessage.put("latitude", latitude);
			positionMessage.put("longitude", longitude);
			positionMessage.put("time", new java.util.Date().toString());
			
			sendToQueue(positionMessage);
			
			//randomized the queue nicely
			delay(Math.random() * 200 + 200);
		}
		System.out.println(vehicleName + " has now completed its journey. Having a tea break.");
		return null;
	} 

	/*
	 * Sends a message to the queue called MyQueue
	 * @param positionMessage
	 * @throws InterruptedException
	 */
	private void sendToQueue(HashMap<String, String> positionMessage) throws InterruptedException{
		
		boolean messageNotSent = true;
		while(messageNotSent) {
			
			//broadcast this message
			try {
				jmsTemplate.convertAndSend("VehiclePositionQueue", positionMessage);
				messageNotSent = false;
				
			}catch(UncategorizedJmsException e) {
				//we are going to assume thta this is due to downtime - back off and go again
				System.out.println("\"VehiclePositionQueue\" Queue unavailable - backing off 500ms before retry");
				delay(500);
			}
			
		}
	}	

	private void delay(double d) throws InterruptedException {
		Thread.sleep((long) d);
		
	}

}
