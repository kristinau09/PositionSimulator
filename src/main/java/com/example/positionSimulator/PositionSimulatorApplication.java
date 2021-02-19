package com.example.positionSimulator;

import java.io.IOException;
import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/*
 * This is a dummy application which simulates the progress of vehicles on a delivery route.
 * The progrma reads from one or more text files containing a list of latitude and longitude positions of vehicles.
 * 
 * Messages are sent on to a queue(ActiveMQ)
 * 
 */

@SpringBootApplication
public class PositionSimulatorApplication {

	public static void main(String[] args) throws IOException, InterruptedException {

		try (ConfigurableApplicationContext ctx = SpringApplication.run(PositionSimulatorApplication.class, args)) {

			final JourneySimulator simulator = ctx.getBean(JourneySimulator.class);

			Thread mainThread = new Thread(simulator);
			mainThread.start();

			System.out.println("Press return to terminate the simulate.");
			Scanner input = new Scanner(System.in);
			input.nextLine();
			input.close();

			// try with resoources will automatically close the container
			simulator.finish();
		}

	}

}
