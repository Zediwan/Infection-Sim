import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class CFrame_save extends JPanel implements ActionListener {
    //screen
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 800;

    //point
    final int POINT_SIZE = 7;

    //people
    //final int AMOUNT_OF_PEOPLE = (WIDTH+HEIGHT);
    final int AMOUNT_OF_PEOPLE = WIDTH+HEIGHT;
    ArrayList<Person_save> people = new ArrayList<Person_save>();
    ArrayList<Person_save> deadPeople = new ArrayList<Person_save>();
    ArrayList<Point> pointsDead = new ArrayList<Point>();
    ArrayList<Point> pointsInfected = new ArrayList<Point>();
    ArrayList<Point> pointsRecovered = new ArrayList<Point>();
    ArrayList<Point> pointsHealthy = new ArrayList<Point>();

    //time
    int time = 0;
    public static final int TIME_PERIOD = 24;
    final double SCALE_TIME = 1 ;

    public static void main (String[] arg){
        CFrame_save c = new CFrame_save();
    }

    public void paint(Graphics g){
        time += TIME_PERIOD;
        //calculate graph points for Infected ppl (relative)
        double percInfected = (double)Person_save.numInfected/(double)(AMOUNT_OF_PEOPLE);
        pointsInfected.add(new Point((time), percInfected * HEIGHT));
        //calculate graph point for Healthy ppl (relative)
        double percHealthy = (double)(AMOUNT_OF_PEOPLE-Person_save.numInfected-Person_save.numRecovered-deadPeople.size())/(double)(AMOUNT_OF_PEOPLE);
        pointsHealthy.add(new Point((time),percHealthy * HEIGHT));
        //calculate graph point for Recovere ppl (relative)
        double percRecovered = (double)Person_save.numRecovered/(double)(AMOUNT_OF_PEOPLE);
        pointsRecovered.add(new Point((time),percRecovered * HEIGHT));
        //calculate graph point for dead ppl (relative)
        double percDead = (double) deadPeople.size()/(double)AMOUNT_OF_PEOPLE;
        pointsDead.add(new Point((time),percDead * HEIGHT));

        //check if there have been any collisions
        for(int i = 0; i < people.size(); i++){
            for(int j = i+1; j < people.size(); j++){
                if(people.get(i).isDead){               //if the person is dead remove it from the rendering and add it to the deathList
                    noteDeath(people.get(i));
                }else{
                    people.get(i).collision(people.get(j));
                }
            }
        }

        super.paintComponent(g);

        //position and color of the graph
        g.setColor(Color.red);
        for(Point p : pointsInfected){
            int x = (int)(((double)p.time/(double)time)*WIDTH);
            int y =(int)Math.round(HEIGHT - p.value);
            if(y > HEIGHT) y = HEIGHT;
            assert y <= HEIGHT;
            g.fillOval(x, y, POINT_SIZE, POINT_SIZE);
        }

        g.setColor(Color.blue);
        for(Point p : pointsHealthy){
            int x = (int)(((double)p.time/(double)time)*WIDTH);
            int y = (int)Math.round(HEIGHT - p.value);
            if(y > HEIGHT) y = HEIGHT;
            assert y <= HEIGHT;
            g.fillOval(x, y, POINT_SIZE, POINT_SIZE);
        }

        g.setColor(Color.LIGHT_GRAY);
        for(Point p : pointsRecovered){
            int x = (int)(((double)p.time/(double)time)*WIDTH);
            int y = (int)Math.round(HEIGHT - p.value);
            if(y > HEIGHT) y = HEIGHT;
            assert y <= HEIGHT;
            g.fillOval(x, y, POINT_SIZE, POINT_SIZE);
        }

        g.setColor(Color.black);
        for(Point p : pointsDead){
            int x = (int)(((double)p.time/(double)time)*WIDTH);
            int y = (int)Math.round(HEIGHT - p.value);
            if(y > HEIGHT) y = HEIGHT;
            assert y <= HEIGHT;
            g.fillOval(x, y, POINT_SIZE, POINT_SIZE);
        }
        //paint all the people who are alive
        for(Person_save p : people){
            p.paint(g);
        }

        System.out.println( "People alive: " + (AMOUNT_OF_PEOPLE- deadPeople.size()) + " (" + Math.round((1-percDead)*100) + "%)\n" +
                            "Fatal Cases: " + deadPeople.size() + " (" + Math.round(percDead*100) + "% / " +Math.round(((double) deadPeople.size()/(double)Person_save.totalNumInfected)*100) +"%)\n" +
                            "People never infected: " + (AMOUNT_OF_PEOPLE-Person_save.numInfected-Person_save.numRecovered- deadPeople.size()) + " (" + Math.round(percHealthy*100) + "%)\n" +
                            "Cases: " + (Person_save.totalNumInfected) + " (" + Math.round(((double)Person_save.totalNumInfected/(double)AMOUNT_OF_PEOPLE)*100) + "%)\n" +
                            "Resickening Cases:" + (Person_save.resickeningCases) + " (" + Math.round(((double) Person_save.resickeningCases/(double)Person_save.totalNumInfected)*100) + "%)\n" +
                            "People recovered: " + Person_save.numRecovered + " (" + Math.round(percRecovered*100) + "%)\n\n");

    }

    public CFrame_save(){
        JFrame frame = new JFrame("Simulation");        //title of the frame
        frame.setSize(WIDTH, HEIGHT);                        //frame size
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        for(int i = 0; i < AMOUNT_OF_PEOPLE; i++){
            people.add(new Person_save());
        }

        //Timer for animation
        Timer t = new Timer(TIME_PERIOD,this);
        t.start();

        frame.add(this);
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e){
        repaint();
    }

    public void noteDeath(Person_save p){
        people.remove(p);       //remove from the ppl List
        deadPeople.add(p);     //add to the death List
    }
}


