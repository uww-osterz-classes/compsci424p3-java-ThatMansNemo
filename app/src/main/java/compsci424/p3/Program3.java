/* COMPSCI 424 Program 3
 * Name:
 * 
 * This is a template. Program3.java *must* contain the main class
 * for this program. 
 * 
 * You will need to add other classes to complete the program, but
 * there's more than one way to do this. Create a class structure
 * that works for you. Add any classes, methods, and data structures
 * that you need to solve the problem and display your solution in the
 * correct format.
 */

package compsci424.p3;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Program3 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String command;
        
        String[] argsWithFileLocation = {"auto", "test2.txt"};
        args = argsWithFileLocation;
    
        if (args.length < 2) {
            System.out.println("Not enough command-line arguments provided.");
            System.out.println("Enter the mode (manual/auto): ");
            String mode = scanner.nextLine();
            if (args.length < 1) {
                System.err.println("Please provide the mode (manual or auto) as a command-line argument.");
                return;
            }
        
            if (!mode.equals("manual") && !mode.equals("auto")) {
                System.err.println("Invalid mode. Exiting.");
                return;
            }
            System.out.println("Enter the setup file location: ");
            String setupFileLocation = scanner.nextLine();
            args = new String[]{mode, setupFileLocation};
        }
    
        System.out.println("Selected mode: " + args[0]);
        System.out.println("Setup file location: " + args[1]);
    
    
    
        // 1. Open the setup file using the path in args[1]
        String currentLine;
        BufferedReader setupFileReader;
        try {
            setupFileReader = new BufferedReader(new FileReader(args[1]));
        } catch (FileNotFoundException e) {
            System.err.println("Cannot find setup file at " + args[1] + ", exiting.");
            return;
        }

        // 2. Get the number of resources and processes from the setup file
        int numResources;
        int numProcesses;

        try {
            currentLine = setupFileReader.readLine();
            if (currentLine == null) {
                System.err.println("Cannot find number of resources, exiting.");
                setupFileReader.close();
                return;
            } else {
                numResources = Integer.parseInt(currentLine.split(" ")[0]);
                System.out.println(numResources + " resources");
            }

            currentLine = setupFileReader.readLine();
            if (currentLine == null) {
                System.err.println("Cannot find number of processes, exiting.");
                setupFileReader.close();
                return;
            } else {
                numProcesses = Integer.parseInt(currentLine.split(" ")[0]);
                System.out.println(numProcesses + " processes");
            }

            int[] available = new int[numResources];
            int[][] maximum = new int[numProcesses][numResources];
            int[][] allocation = new int[numProcesses][numResources];
            int[][] need = new int[numProcesses][numResources];

            currentLine = setupFileReader.readLine();
            if (currentLine == null) {
                System.err.println("Cannot find available resources, exiting.");
                setupFileReader.close();
                return;
            } else {
                String[] availableStr = currentLine.split(" ");
                for (int i = 0; i < numResources; i++) {
                    available[i] = Integer.parseInt(availableStr[i]);
                }
            }

            for (int i = 0; i < numProcesses; i++) {
                currentLine = setupFileReader.readLine();
                if (currentLine == null) {
                    System.err.println("Cannot find maximum resource allocation for process " + i + ", exiting.");
                    setupFileReader.close();
                    return;
                } else {
                    String[] maximumStr = currentLine.split("\\s+ ");
                    for (int j = 0; j < numResources; j++) {
                        maximum[i][j] = Integer.parseInt(maximumStr[j]);
                    }
                }
            }

            for (int i = 0; i < numProcesses; i++) {
                currentLine = setupFileReader.readLine();
                if (currentLine == null) {
                    System.err.println("Cannot find resource allocation for process " + i + ", exiting.");
                    setupFileReader.close();
                    return;
                } else {
                    String[] allocationStr = currentLine.split(" ");
                    for (int j = 0; j < numResources; j++) {
                        allocation[i][j] = Integer.parseInt(allocationStr[j]);
                    }
                }
            }

            for (int i = 0; i < numProcesses; i++) {
                for (int j = 0; j < numResources; j++) {
                    need[i][j] = maximum[i][j] - allocation[i][j];
                }
            }

            ResourceManager resourceManager = new ResourceManager(numProcesses, numResources, maximum, allocation,
                    available);

            if (resourceManager.isSafeState()) {
                System.out.println("System is in a safe state.");
            } else {
                System.out.println("System is NOT in a safe state.");
            }

            if (args[0].equals("manual")) {
                manualMode(resourceManager);
            } else if (args[0].equals("auto")) {
                automaticMode(resourceManager);
            } else {
                System.err.println("Invalid mode. Exiting.");
            }

            setupFileReader.close();
        } catch (IOException e) {
            System.err.println("Something went wrong while reading setup file " + args[1] + ". Exiting.");
            e.printStackTrace(System.err);
            System.err.println("Exiting.");
        }

        System.out.println("Enter commands (request, release, end):");
        while (true) {
            command = scanner.nextLine().trim();
            if (command.startsWith("request")) {
                String[] parts = command.split(" ");
                int unitsRequested = Integer.parseInt(parts[1]);
                int resourceType =Integer.parseInt(parts[3]);
                int processID = Integer.parseInt(parts[5]);
                
        }
     scanner.close();   
    }
    }

    private static void manualMode(ResourceManager resourceManager) {
        Scanner scanner = new Scanner(System.in);
        String command;
        do {
            System.out.print("Enter command: ");
            command = scanner.nextLine();
            if (command.startsWith("request")) {
                handleRequestCommand(command, resourceManager);
            } else if (command.startsWith("release")) {
                handleReleaseCommand(command, resourceManager);
            }
        } while (!command.equals("end"));
        scanner.close();
    }

    private static void automaticMode(ResourceManager resourceManager) {
        int numProcesses = 3;
        for (int i = 0; i < numProcesses; i++) {
            Thread[] threads = new Thread[numProcesses];
            for (int j = 0; j < numProcesses; j++) {
                threads[i] = new Thread(new ProcessThread(resourceManager, j));
                threads[j].start();
            }
            try {
                for (Thread t : threads) {
                    t.join(); // Wait for all threads to finish
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void handleRequestCommand(String command, ResourceManager resourceManager) {
        String[] parts = command.split(" ");
        if (parts.length != 6) {
            System.out.println("Invalid request command format.");
            return;
        }
        try {
            int numUnits = Integer.parseInt(parts[1]);
            int resourceType = Integer.parseInt(parts[3]);
            int processId = Integer.parseInt(parts[5]);
            int[] request = new int[resourceManager.getNumResources()];
            request[resourceType] = numUnits;
            if (resourceManager.requestResources(processId, resourceType, numUnits)) {
                System.out.println("Request granted.");
            } else {
                System.out.println("Request denied.");
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid request command format.");
        }
    }

    private static void handleReleaseCommand(String command, ResourceManager resourceManager) {
        String[] parts = command.split(" ");
        if (parts.length != 6) {
            System.out.println("Invalid release command format.");
            return;
        }
        try {
            int numUnits = Integer.parseInt(parts[1]);
            int resourceType = Integer.parseInt(parts[3]);
            int processId = Integer.parseInt(parts[5]);
            int[] release = new int[resourceManager.getNumResources()];
            release[resourceType] = numUnits;
            resourceManager.releaseResources(processId, resourceType, numUnits);
            System.out.println("Resources released.");
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid release command format.");
        }
    }

    private static void executeRequestCommand(String command, ResourceManager resourceManager) {
        // Parse the command string
        String[] tokens = command.split(" ");
        if (tokens.length != 7) {
            System.out.println("Invalid request command. Please follow the format: request I of J for K");
            return;
        }

        int numberOfElements;
        int resourceType;
        int processId;
        try {
            numberOfElements = Integer.parseInt(tokens[1]);
            resourceType = Integer.parseInt(tokens[3]);
            processId = Integer.parseInt(tokens[5]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid request command. Please enter valid numeric values.");
            return;
        }

        // Validate parsed information
        if (numberOfElements < 0 || resourceType < 0 || processId < 0) {
            System.out.println("Invalid request command. Values must be non-negative.");
            return;
        }

        // Check if the requested resources can be allocated
        // Perform necessary checks here, such as deadlock avoidance, safety check, etc.
        // Assuming the resource manager object is available, you can call its methods

        // Assuming resourceManager is the ResourceManager object
        if (resourceManager.requestResources(processId, resourceType, numberOfElements)) {
            System.out.println("Request granted.");
        } else {
            System.out.println("Request denied. Unable to allocate resources.");
        }
    }

    private static void executeReleaseCommand(String command, ResourceManager resourceManager) {
        // Parse the command string
        String[] tokens = command.split(" ");
        if (tokens.length != 7) {
            System.out.println("Invalid release command. Please follow the format: release I of J for K");
            return;
        }

        int numberOfElements;
        int resourceType;
        int processId;
        try {
            numberOfElements = Integer.parseInt(tokens[1]);
            resourceType = Integer.parseInt(tokens[3]);
            processId = Integer.parseInt(tokens[5]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid release command. Please enter valid numeric values.");
            return;
        }

        // Validate parsed information
        if (numberOfElements < 0 || resourceType < 0 || processId < 0) {
            System.out.println("Invalid release command. Values must be non-negative.");
            return;
        }

        // Release the specified resources
        // Perform necessary checks here, such as ensuring the process actually holds
        // the resources
        // Assuming the resource manager object is available, you can call its methods

        // Assuming resourceManager is the ResourceManager object
        resourceManager.releaseResources(processId, resourceType, numberOfElements);
        System.out.println("Resources released successfully.");
    }

}
//package compsci424.p3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class ResourceManager {
    private int numProcesses;
    private int numResources;
    private int[][] allocation;
    private int[][] max;
    private int[] available;
    private int[][] needs;

    public ResourceManager(int numProcesses, int numResources, int[][] max, int[][] allocation, int[] available) {
        this.numProcesses = numProcesses;
        this.numResources = numResources;
        this.max = max;
        this.allocation = allocation;
        this.available = available;
    }

    public boolean isSafeState() {
        int[] work = new int[numResources];
        boolean[] finish = new boolean[numProcesses];
        System.arraycopy(available, 0, work, 0, numResources);
        Arrays.fill(finish, false);
        boolean found;
        do {
            found = false;
            for (int i = 0; i < numProcesses; i++) {
                if (!finish[i]) {
                    boolean canFinish = true;
                    for (int j = 0; j < numResources; j++) {
                        if (max[i][j] - allocation[i][j] > work[j]) {
                            canFinish = false;
                            break;
                        }
                    }
                    if (canFinish) {
                        // Process i can finish
                        finish[i] = true;
                        found = true;
                        for (int k = 0; k < numResources; k++) {
                            work[k] += allocation[i][k];
                        }
                    }
                }
            }
        } while (found);
        for (boolean f : finish) {
            if (!f) {
                return false; // Unsafe state
            }
        }
        return true; // Safe state
    }

    // Method to handle resource requests
    public synchronized boolean requestResources(int processId, int type, int numberOfElements) {
        // Check if the request can be granted
        if (available[type] >= numberOfElements) {
            // Update allocation and available arrays
            allocation[processId][type] += numberOfElements;
            available[type] -= numberOfElements;
            return true; // Request granted
        } else {
            return false; // Request denied
        }
    }

    // Method to handle resource releases
    public synchronized void releaseResources(int processId, int type, int numberOfElements) {
        // Release the specified resources
        allocation[processId][type] -= numberOfElements;
        available[type] += numberOfElements;
    }

    public int getNumProcesses() {
        return numProcesses;
    }

    public int getNumResources() {
        return numResources;
    }

    public int[][] getAllocation() {
        return allocation;
    }

    public int[][] getMax() {
        return max;
    }

    public int[] getAvailable() {
        return available;
    }

    public synchronized int getMaxAllocation(int processId, int resourceId) {
        return max[processId][resourceId];
    }

    public synchronized int getAllocation(int processId, int resourceId) {
        return allocation[processId][resourceId];
    }
}
import java.util.Random;

class ProcessThread implements Runnable {
    private ResourceManager resourceManager;
    private int processId;

    public ProcessThread(ResourceManager resourceManager, int processId) {
        this.resourceManager = resourceManager;
        this.processId = processId;
    }

    @Override
    public void run() {
        Random random = new Random();
        for (int i = 0; i < 3; i++){
            int type = random.nextInt(resourceManager.getNumResources());
            int numberOfElements = random.nextInt(resourceManager.getAvailable()[type] + 1);
            boolean isRequest = random.nextBoolean();

            if (isRequest) {
                if (resourceManager.requestResources(processId, numberOfElements,type)) {
                    System.out.println("Process " + processId + " requested " + numberOfElements + " units of resource type " + type + ". Request granted.");
                } else {
                    System.out.println("Process " + processId + " requested " + numberOfElements + " units of resource type " + type + ". Request denied.");
                }
            } else {
                int[] release = new int[resourceManager.getNumResources()];
                release[type] = numberOfElements;
                resourceManager.releaseResources(processId,type,numberOfElements);
                System.out.println("Process " + processId + " released " + numberOfElements + " units of resource type " + type + ".");
            }

            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private int[] generateRandomRequest() {
        int res = resourceManager.getNumResources();
        int[] request = new int[res];
        for (int i = 0; i < res; i++) {
            request[i] = (int) (Math.random() * (resourceManager.getMaxAllocation(processId, i) + 1));
        }
        return request;
    }

    private int[] generateRandomRelease() {
        int res = resourceManager.getNumResources();
        int[] release = new int[res];
        for (int i = 0; i < res; i++) {
            release[i] = (int) (Math.random() * (resourceManager.getAllocation(processId, i) + 1));
        }
        return release;
    }
}

