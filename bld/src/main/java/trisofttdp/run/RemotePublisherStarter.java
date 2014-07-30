package trisofttdp.run;

import org.springframework.context.support.ClassPathXmlApplicationContext;


public class RemotePublisherStarter {

	public static void main(String[] args) {
		//RMI Server Application Context is started... 
		new ClassPathXmlApplicationContext("rmiServerAppContext.xml");
	}
}