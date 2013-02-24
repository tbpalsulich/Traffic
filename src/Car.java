import java.awt.Polygon;

public abstract class Car {
	public int speed;
	int x;
	int y;
	public int relSpeed;
	int maxSpeed;
	int targetSpeed;
	
	public abstract void update();
	public abstract void setSpeed(Car next);

	
	public Polygon getCarShape(){
		int xp[]={x,x+30,x+30,x};
		int yp[]={y,y,y+50,y+50};
		return new Polygon(xp,yp,4);
	}
	
}