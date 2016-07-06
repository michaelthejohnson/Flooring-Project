
package com.swcguild.flooringmastery.app;

import com.swcguild.flooringmastery.controller.FlooringMasteryController;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FlooringMasteryApp {
    
       public static void main(String[] args) throws FileNotFoundException, IOException {

        ApplicationContext ctxFactory = new ClassPathXmlApplicationContext("applicationContext.xml");
        
        FlooringMasteryController controller = ctxFactory.getBean("controllerBean", FlooringMasteryController.class);

        controller.run();
    } 
       
}