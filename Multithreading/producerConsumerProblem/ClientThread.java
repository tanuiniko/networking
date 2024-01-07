package Multithreading.producerConsumerProblem;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

class ClientThread extends Thread{
    private Socket client;
    private Resource item;
    private Scanner input;
    private PrintWriter output;

    public ClientThread(Socket socket, Resource resource){
        client=socket;
        item=resource;
        try {
            input=new Scanner(client.getInputStream());
            output=new PrintWriter(client.getOutputStream(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        String request="";
        do{
            request=input.nextLine();
            if(request.equals("1")){
                item.takeOne();
                output.println("Request granted.");
            }
        }while(!request.equals("0"));

        try {
            System.out.println("Closing down connection..");
            client.close();
        } catch (IOException e) {
            System.out.println("Unable to close connection to client!");
        }
    }
}

class Producer extends Thread {
    private Resource item;

    public Producer(Resource resource){
        item=resource;
    }

    public void run(){
        int pause;
        int newLevel;
        do{
            try {
                newLevel=item.addOne();
                System.out.println("<Producer> New Level : "+newLevel);
                pause=(int)(Math.random()*5000);
                sleep(pause);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }while(true);
    }
}

class Resource{
    private int numResources;
    private final int MAX=5;

    public Resource(int startLevel){
        numResources=startLevel;
    }

    public int getLevel(){
        return numResources;
    }

    public synchronized int addOne(){
        try {
            while(numResources>=MAX) wait();
            numResources++;
            notifyAll();
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        return numResources;
    }

    public synchronized int takeOne(){
        try {
            while(numResources==0) wait();
            numResources--;
            notify();
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        return numResources;
    }
}