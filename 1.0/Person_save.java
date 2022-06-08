import java.awt.*;

public class Person_save {

    //size
    public static final int PERSON_WIDTH = 7;
    public static final int PERSON_HEIGHT = 7;

    //location
    int x, y;

    //movement
    int vx, vy;
    final int MIN_VELOCITY = -5;
    final double DIRECTION_CHANGE_CHANCE = .3; //chance to change direction

    //Disease
    int status = 0;
    final double STARTING_SICKNESS = .01; //% of ppl sick at the start
    public static final double INFECTION_CHANCE = .02; //chance on collision to infect
    final int INFECTION_RADIUS = PERSON_WIDTH*2;
    static int numInfected = 0;
    static int totalNumInfected = 0;
    final double RANDOM_INFECTION = 0;
    //Death
    final double DEATH_RATE = 1;
    double deathChance = 0;
    boolean isDead = false;
    //Resickening
    final double RESICKENING_CHANCE = .0725; //chance a person will be sick despite being recovered?
    static int resickeningCases = 0;
    //recovery time
    int recoveryTime;
    final int MIN_RECOVERY_TIME = (CFrame_save.TIME_PERIOD*7)*10;
    final int MAX_RECOVERY_TIME = (CFrame_save.TIME_PERIOD*14)*10;
    static int numRecovered = 0;

    //social distancing
    final boolean IS_SOCIAL_DISTANCING_RULE = false; //is social distancing a rule?
    boolean isSocialDistancing; //whether a person is distancing or not
    final double PPL_NOT_DISTANCING = 0.5; //% of ppl who ignore the rule

    public Person_save(){
        //random location at the start
        x = (int)(Math.random()*(CFrame_save.WIDTH-10)+0);
        y = (int)(Math.random()*(CFrame_save.HEIGHT-10)+0);

        //percent of ppl being sick at the start
        if(Math.random()<STARTING_SICKNESS){
            status = 1;
            numInfected++;
            totalNumInfected++;
        }

        //people who aren't distancing will still move
        if(Math.random()<PPL_NOT_DISTANCING){
            isSocialDistancing = false;
            //random velocity at the start
            vx = (int)(Math.random()*(10+1)+ MIN_VELOCITY);
            vy = (int)(Math.random()*(10+1)+ MIN_VELOCITY);
        }else{
            isSocialDistancing = IS_SOCIAL_DISTANCING_RULE; //at the start everyone will behave according to the rule
        }

        //random recovery time at the start
        recoveryTime = (int)(Math.random()*(MAX_RECOVERY_TIME) + MIN_RECOVERY_TIME);
        this.deathChance = DEATH_RATE/this.recoveryTime;
    }

    public boolean collision(Person_save p2){
        Rectangle per2 = new Rectangle(p2.x, p2.y, INFECTION_RADIUS,INFECTION_RADIUS);
        Rectangle per1 = new Rectangle(this.x, this.y, INFECTION_RADIUS,INFECTION_RADIUS);

        //two people collide
        if(per1.intersects(per2)){
            //chance of infection
            if(Math.random()<INFECTION_CHANCE){
                if(this.status == 1 && (p2.status == 0 || (p2.status == 2 && Math.random()<RESICKENING_CHANCE))){
                    if(p2.status == 2){
                        resickeningCases++;
                        numRecovered--;
                    }
                    p2.status = 1;
                    numInfected++;
                    totalNumInfected++;
                }else if(p2.status == 1 && (this.status == 0 || (this.status == 2 && Math.random()<RESICKENING_CHANCE))){
                    if(this.status == 2) {
                        numRecovered--;
                        resickeningCases++;
                    }
                    this.status = 1;
                    numInfected++;
                    totalNumInfected++;
                }
            }
            return true;
        }
        return false;
    }

    public void paint(Graphics g){
        //sets colour according to status
        switch(status){
            case 0:
                g.setColor(Color.blue);
                break;
            case 1:
                g.setColor(Color.red);
                break;
            case 2:
                g.setColor(Color.LIGHT_GRAY);
                break;
        }

        if(status == 1){
            //counts-down time being sick according to recovery time
            recoveryTime -= CFrame_save.TIME_PERIOD;
            if(Math.random()<(this.deathChance)){
                this.status = 3;
                isDead = true;
            }
            //once recoveryTime has been reached (=0) the person is recovered
            if(recoveryTime <= 0){
                status = 2;
                numRecovered++;
                numInfected--;
            }
        }

        if(Math.random() < DIRECTION_CHANGE_CHANCE && !this.isSocialDistancing){
            vx = (int)(Math.random()*(5+1)+ -3);
            vy = (int)(Math.random()*(5+1)+ -3);
        }

        if(status != 1 && Math.random()<RANDOM_INFECTION) status = 1;

        //updating position according to velocity
        x += vx;
        y += vy;

        //makes the ppl bounce of the borders fo the frame
        if(x < 0 || x >= CFrame_save.WIDTH){
            vx *= -1;
        }
        //makes the ppl bounce of the borders fo the frame
        if(y < 0 || y >= CFrame_save.HEIGHT){
            vy *= -1;
        }

        //paints the ppl
        g.fillOval(x, y, PERSON_WIDTH, PERSON_HEIGHT);
    }

}
