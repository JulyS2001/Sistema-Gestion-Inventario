package com.inventarioropa;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage; 
import javafx.scene.Scene;
import javafx.scene.Parent;


public class InventarioRopaApp extends Application {
	
	 private ConfigurableApplicationContext springContext;

	    @Override
	    public void init() {

	        springContext = new SpringApplicationBuilder(InventarioRopaApplication.class)
	                .run();
	    }

	@Override
	public void start(Stage stage) throws Exception {
		
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/view/Main.fxml")
				);
		
		loader.setControllerFactory(springContext::getBean);
		
		Parent root = loader.load();
		
		Scene scene = new Scene(root, 900, 600);
		
		String css = getClass()
		        .getResource("/css/main.css")
		        .toExternalForm();

		scene.getStylesheets().add(css);
		
		stage.setTitle("Inventario de Ropa");
		stage.setScene(scene);
		
		stage.show();
	}
	
	@Override
    public void stop() {

        springContext.close();
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
