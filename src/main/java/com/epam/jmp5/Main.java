package com.epam.jmp5;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Main {

    private final static Logger logger = LogManager.getLogger(Main.class);
    private final static String DEAFAULT_CLASSES_PATH = "d://classes/";


    public static void main(String[] args) throws IOException{
        Path classesPath = Paths.get(DEAFAULT_CLASSES_PATH);
        if(args.length > 0){
            Paths.get(args[0]);
        }
        logger.info("Looking for classes in " + classesPath);

        if(!Files.exists(classesPath)){
            logger.error(classesPath + " path doesn't exist");
            return;
        }

        List<Path> jarPaths = new ArrayList<Path>();

        Files.walk(classesPath).forEach(path -> {
            if(Files.isRegularFile(path) && path.toString().endsWith(".jar")){
                jarPaths.add(path);
            }
        });

        if(jarPaths.isEmpty()){
            logger.error(classesPath + " doesn't contain any jar file");
            return;
        }

        logger.info("Choose jar to load:");

        IntStream.range(0, jarPaths.size()).forEach(indx -> {
            logger.info(indx + " - " + jarPaths.get(indx));
        });

        int userChoice = getIntInput(jarPaths.size() - 1);

        Path chosenPath =jarPaths.get(userChoice);

        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new URL("file:" + chosenPath.toString())});
        boolean end = true;
        do{
            logger.info("Full class name to load:");
            String className = System.console().readLine();
            try {
                Class c = classLoader.loadClass(className);
                logger.info(c + " has been loaded");
            } catch (ClassNotFoundException e) {
                logger.info(className + " not found");
            }
            logger.info("Finish? (y)");
            String finish = System.console().readLine();
            if(finish.equalsIgnoreCase("y")){
                end = false;
            }
        }while(end);
    }

    private static int getIntInput(int maxValue){
        try {
            int input = Integer.parseInt(System.console().readLine());
            if(input < 0 || input > maxValue){
                logger.info("Choose number from 0 to " + maxValue);
                return getIntInput(maxValue);
            }
            return input;
        }catch(NumberFormatException e){
            logger.info("Numbers only.");
            return getIntInput(maxValue);
        }
    }
}
