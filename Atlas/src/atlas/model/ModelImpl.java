package atlas.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import atlas.model.rules.Algorithm;
import atlas.model.rules.AlgorithmBruteForce;
import atlas.model.rules.CollisionStrategy;
import atlas.model.rules.CollisionStrategyFragments;
import atlas.utils.Pair;

/**
 * Model implementation.
 *
 */
public class ModelImpl implements Model, java.io.Serializable {

	private static final long serialVersionUID = 1670664173059452174L;
	private Algorithm alg;
	private ArrayList<Body> bodies = new ArrayList<>();
	private SimClock clock = new SimClock();

	@Override
	public String toString() {
		return "ModelImpl [ clock=" + clock + "bodies=" + bodies + "]";
	}

	public ModelImpl() {

		this.alg = new AlgorithmBruteForce(new CollisionStrategyFragments(50));

//		 double radius =
//		 EpochJ2000.SUN.getBody().getProperties().getRadius()*2;
//
//		 Body x = EpochJ2000.URANUS.getBody();
//		 Body y = EpochJ2000.VENUS.getBody();
//		
//		 this.bodies.addAll(Arrays.asList(x,y));
//		
//		 x.setPosX(5e3);
//		 x.setPosY(5e3);
//		 x.getProperties().setRadius(radius*3);
//		 x.setVelocity(new Pair<Double,Double>(new Double(0), new Double(0)));
//		 x.getProperties().setRotationPeriod(60000000L);;
//		
//		 y.setPosY(5e3);
//		 y.setPosX(y.getPosX()/3);
//		 y.getProperties().setRadius(radius);
//		 y.setVelocity(new Pair<Double,Double>(new Double(5000), new
//		 Double(0)));
	}

	public ModelImpl(EpochJ2000[] epoch) {
		this();
		this.clock.setEpoch(EpochJ2000.TIME_MILLS);
		for (EpochJ2000 b : epoch) {
			this.bodies.add(b.getBody());
		}
	}

	@Override
	public List<Body> getBodiesToRender() {
		return this.bodies;
	}

	@Override
	public void updateSim(double sec) {
		// update bodies position
		this.bodies = this.alg.exceuteUpdate(bodies, sec);
		// update clock/date
		this.clock.update((long) sec);
	}

	@Override
	public void setAlgorithm(Algorithm algorithm) {
		algorithm.setCollisionStrategy(alg.getCollisionStrategy());
		this.alg = algorithm;
	}

	@Override
	public void setCollsion(CollisionStrategy collision) {
		this.alg.setCollisionStrategy(collision);
	}

	/*
	 * Calculates the circural velocity in a circular orbit, formula: v = sqrt(
	 * (G*M) / R )
	 */
	private double circularVelocity(Body b, double targetMass, double distance) {
		double numerator = BodyType.G * targetMass;
		return Math.sqrt(numerator / distance);
	}

	@Override
	public void addBody(Body b) {

		double angleTheta = Math.atan2(b.getPosY(), b.getPosX());

		Optional<Body> largestBody = this.bodies.stream().max((i, j) -> (int) (i.getMass() - j.getMass()));
		double targetMass = largestBody.isPresent() ? largestBody.get().getMass() : BodyType.SOLAR_MASS;
		double distance = largestBody.isPresent() ? largestBody.get().distanceTo(b)
				: Math.sqrt(b.getPosX() * b.getPosX() + b.getPosY() * b.getPosY());

		double totVel = circularVelocity(b, targetMass, distance);

		/* The angular velocity: omega = v / r */
		double anglev = totVel / distance;

		/*
		 * Once I have the angular velocity I can find the components of the
		 * total velocity vector : vx = -r*Omega*sin(Theta), vx = -r*Omega*sin(Theta)
		 */
		double vx = -distance * anglev * Math.sin(angleTheta);
		double vy = distance * anglev * Math.cos(angleTheta);

		b.setVelocity(new Pair<>(vx, vy));
		this.bodies.add(b);
	}

	@Override
	public String getTime() {
		return this.clock.toString();
	}

	@Override
	public SimClock getClock() {
		return this.clock;
	}

	@Override
	public void setClock(SimClock clock) {
		this.clock = clock;

	}

}
