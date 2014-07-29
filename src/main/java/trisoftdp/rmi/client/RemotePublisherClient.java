package trisoftdp.rmi.client;

import java.io.IOException;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import trisoftdp.core.DynException;
import trisoftdp.core.DynamicPublishingPackage;
import trisoftdp.rmi.server.RemotePublisher;

public class RemotePublisherClient {
	

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("rmiClientAppContext.xml");		
		//Remote User Service is called via RMI Client Application Context...
		RemotePublisher rmiClient = (RemotePublisher) context.getBean("RemotePublisher");
		
		try {
			rmiClient.process(0, "id", null, null, null, null, "English");
		} catch (DynException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
