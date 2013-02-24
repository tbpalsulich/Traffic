public class TrafficCar extends Car {
	static double dmin=40;
	static double dmax=600;
	static double decel=3;
	static double accel=1;
	static int mergeTime=80;

	int laneChange;

	public TrafficCar(){
		speed=0;
		maxSpeed=(int)(Math.random()*30+20);
	}

	public void changeLanes(int numLanes){
		if(laneChange!=0){return;}
		
		laneChange=numLanes*80;
	}

	public void reset(){
		maxSpeed=(int)(Math.random()*30+20);
	}

	public void update(){
		if(y<-50000){y=40000; reset();}
		if(y>50000){y=-40000; reset();}		// Keep a steady stream of TrafficCars on the road.
		
		
		if(speed<targetSpeed){		// Accelerate if its below target speed, don't accelerate past its targetSpeed.
			speed+=accel;
			if(speed>targetSpeed){			// If its going above the targetSpeed, decelerate until you get to its targetSpeed.
				speed=targetSpeed;
			}
		}
		
		else if(speed>targetSpeed){
			speed-=decel;
			if(speed<targetSpeed){
				speed=targetSpeed;
			}
		}
		
		
		if(speed<0){			// Can't go in reverse.
			speed=0;
		}
		y-=relSpeed;			// Update y position based on relative speed.

		//Start lane change process.
		

		if(laneChange>0){				// If the car is supposed to go right, add ten to its x position, and subtract ten
			x+=10;										// from the laneChange distance.
			laneChange-=10;
		}
		if(laneChange<0){				// Supposed to go left, subtract ten from its x position, add ten to its laneChange distance.
			x-=10;
			laneChange+=10;
		}
		
		if(speed > maxSpeed){
			speed = maxSpeed;
		}
		
		
		if(targetSpeed<maxSpeed-15){
			int left=TrafficPanel.getSharedPanel().timeToCar(x, y, speed, false);		// Distance to the closest car to the left.
			int right=TrafficPanel.getSharedPanel().timeToCar(x, y, speed, true);		// Distance to the closest car to the right.
			
			if(left>right){
				if(left>mergeTime){
					changeLanes(-1);
				}
			}
			else{
				if(right>mergeTime){
					changeLanes(1);
				}
			}


			if(speed > maxSpeed){							// Make sure the Car isn't going over its maximum speed.
				speed = maxSpeed;
			}
		}
		//End lane change process.

	}
	public void setSpeed(Car next){
		if(next==null){ targetSpeed=maxSpeed; return; }
		double distance = y-next.y-dmin;

		targetSpeed=(int)(maxSpeed*(Math.log10(distance/dmin)/Math.log10(dmax/dmin)));
	}
}