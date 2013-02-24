import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

import javax.swing.BorderFactory;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class TrafficPanel extends javax.swing.JPanel implements KeyListener, ActionListener {

	Timer timer;
	YourCar car;
	int roadSpeed;
	int boxY;
	public static TrafficPanel sharedPanel;
	ArrayList<ArrayList<Car>> traffic = new ArrayList<ArrayList<Car>>();
	public TrafficPanel() {
		this.setBackground(Color.gray);
		setBorder(BorderFactory.createLineBorder(Color.gray));
		this.setFocusable(true);
		this.addKeyListener(this);
		this.setDoubleBuffered(true);
		car=new YourCar();
		
		timer = new Timer(30, this);
		timer.start();
		
		roadSpeed=0;
		boxY=0;
		sharedPanel=this;
		
		for(int a=0;a<7;a++){
			traffic.add(new ArrayList<Car>());
		}

		for(int x=0;x<200;x++){
			int lane=(int)(Math.random()*7);
			TrafficCar newCar = new TrafficCar();
			if(traffic.get(lane).size()==0){
				newCar.y=300;
			}
			else{
				TrafficCar lastCar = (TrafficCar)traffic.get(lane).get(traffic.get(lane).size()-1);
				newCar.y=lastCar.y+300;
			}
			newCar.x=lane*80+40;
			traffic.get(lane).add(newCar);

		}
		
		traffic.get(2).add(car);

	}

	public static TrafficPanel getSharedPanel(){
		return sharedPanel;
	}

	public void actionPerformed(ActionEvent e) {
		car.update();

		roadSpeed=-car.speed;
		boxY+=roadSpeed;
		if(boxY<-10){
			boxY=1000;
		}
		else if(boxY>1010){
			boxY=0;
		}
		
		for(int x=0;x<traffic.size();x++){
			for(int y=0;y<traffic.get(x).size();y++){

				if(y==0){
					traffic.get(x).get(y).setSpeed(null);		
					}
				else{
					traffic.get(x).get(y).setSpeed(traffic.get(x).get(y-1));	
				}
				traffic.get(x).get(y).relSpeed=traffic.get(x).get(y).speed-roadSpeed;
				traffic.get(x).get(y).update();
				checkLane(traffic.get(x).get(y));

			}
		}
		
		for(int x=0;x<traffic.size();x++){
			for(int y=0;y<traffic.get(x).size();y++){
				checkLane(traffic.get(x).get(y));
			}
		}
		checkLane(car);

		sortLanes();
		repaint();
		
		
		System.out.print("Lanes: ");
		for(int x=0;x<traffic.size();x++){
			System.out.print(traffic.get(x).size()+" ");
		}
		System.out.println();

	}

	public int timeToCar(int x, int y , int speed, boolean right){
		int lane=(x-35)/80;
		
		if (lane == 0 && !right) return 0;
		if (lane == 6 && right) return 0;
		
		ArrayList<Car> possibleLane;
		
		if (right){
			possibleLane=traffic.get(lane+1);
		}
		else{
			possibleLane=traffic.get(lane-1);
		}
		
		if(possibleLane.size()==0){
			return 10000;
		}
		

		int index;
		for(index=0; index<possibleLane.size()-1 && possibleLane.get(index).y<y; index++){}
		

		if(index-1 >= 0 && (y-possibleLane.get(index-1).y <= 90 || possibleLane.get(index-1).speed <= speed )){
			return 0;
		}
		
		if(possibleLane.get(index).y<y){
			return 10000;
		}
		
		if(possibleLane.get(index).y-y<45){
			return 0;
		}
		
		if(possibleLane.get(index).speed-speed==0){ 
			return 10000;
		}

		return (possibleLane.get(index).y-y)/(possibleLane.get(index).speed-speed);

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(600, 1000);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.white);
		int xs[]={10,17,17,10};
		int ys[]={0,0,1000,1000};
		g.fillPolygon(xs,ys,4);
		int xs2[]={570,577,577,570};
		g.fillPolygon(xs2,ys,4);
		g.setColor(Color.yellow);
		for(int a=-1000; a<1000; a+=200){
			for(int b=80;b<500;b+=80){
				int xp[]={10+b,17+b,17+b,10+b};
				int yp[]={boxY+a,boxY+a,boxY+30+a,boxY+30+a};
				g.fillPolygon(new Polygon(xp,yp,4));
			}
		}



		g.setColor(Color.white);
		for(int x=0;x<traffic.size();x++){
			for(int y=0;y<traffic.get(x).size();y++){

				g.fillPolygon(traffic.get(x).get(y).getCarShape());
			}
		}
		
		g.setColor(Color.blue);
		g.fillPolygon(car.getCarShape());

	}

	public void sortLanes(){
		for(int x=0;x<traffic.size();x++){
			
			Collections.sort(traffic.get(x), new Comparator<Car>(){
				public int compare(Car c1, Car c2) {
					if(c1.y<c2.y){
						return -1;
					}
					else if(c1.y>c2.y){
						return 1;
					}
					return 0;
				}

			});
		}
	}

	public void checkLane(Car c){
		if(c==car&&car.hidden){ return; }
		
		int	left=(c.x-30)/80;	//The lane that the left side of the car is in
		int	right=(c.x)/80;		//The lane that the right side of the car is in
		
		if(right>6){right=6;}
		if(left>6){left=6;}

		if(!traffic.get(left).contains(c)){
			traffic.get(left).add(c);
		}
		if(!traffic.get(right).contains(c)){
			traffic.get(right).add(c);
		}
		for(int x=0;x<traffic.size();x++){
			if(x==left||x==right){ continue; }		//Don't remove the car from the left and right lanes
			if(traffic.get(x).contains(c)){
				traffic.get(x).remove(c);
			}
		}
	}

	public void keyPressed(KeyEvent e) {
		int i = e.getKeyCode();
		if (i == KeyEvent.VK_RIGHT) {
			car.right=true;
		}
		else if (i == KeyEvent.VK_LEFT) {
			car.left=true;
		}
		else if (i == KeyEvent.VK_UP) {
			car.up=true;
		}
		else if (i == KeyEvent.VK_DOWN) {
			car.down=true;
		}
		else if(i == KeyEvent.VK_SPACE){
			car.toggleHidden();
			for(int x=0;x<traffic.size();x++){
				traffic.get(x).remove(car);
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		int i = e.getKeyCode();
		if (i == KeyEvent.VK_RIGHT) {
			car.right=false;
		}
		else if (i == KeyEvent.VK_LEFT) {
			car.left=false;
		}
		else if (i == KeyEvent.VK_UP) {
			car.up=false;
		}
		else if (i == KeyEvent.VK_DOWN) {
			car.down=false;
		}
	}

	
	public void keyTyped(KeyEvent e) {
	}
	
}
