import java.awt.Polygon;

public class YourCar extends Car {
	boolean up;
	boolean down;
	boolean left;
	boolean right;
	
	boolean hidden;

	
	public YourCar(){
		x=280;
		y=500;
		maxSpeed=80;
		hidden=false;
	}
	
	public void update(){
		if(right){
			x+=10;
		}
		if(left){
			x-=10;
		}
		if(up){
			speed-=2;
		}
		if(down){
			speed+=2;
		}
		if(speed>0){
			speed=0;
		}
		if(speed<-maxSpeed){
			speed=-maxSpeed;
		
		}
		if(x<40){
			x=40;
		}
		if(x>520){
			x=520;
		}
	}
	public void setSpeed(Car next){
		
	}
	
	public void toggleHidden(){
		hidden=!hidden;
	}
	public Polygon getCarShape(){
		if(hidden){ int xp[]={0,0,0,0};
			int yp[]={0,0,0,0};
			return new Polygon(xp,yp,4);

		}
		return super.getCarShape();
	}
}
