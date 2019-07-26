
import java.awt.*;

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Using a quadtree for collision detection
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for blobs
 * @author CBK, Fall 2016, using generic PointQuadtree
 */
public class CollisionGUI extends DrawingGUI {
	private static final int width=800, height=600;		// size of the universe

	private List<Blob> blobs;						// all the blobs
	private List<Blob> colliders;					// the blobs who collided at this step
	private char blobType = 'b';						// what type of blob to create
	private char collisionHandler = 'c';				// when there's a collision, 'c'olor them, or 'd'estroy them
	private int delay = 100;							// timer control

	public CollisionGUI() {
		super("super-collider", width, height);

		blobs = new ArrayList<Blob>();
		colliders = new ArrayList<Blob>();

		// Timer drives the animation.
		startTimer();
	}

	/**
	 * Adds an blob of the current blobType at the location
	 */
	private void add(int x, int y) {
		if (blobType=='b') {
			blobs.add(new Bouncer(x,y,width,height));
		}
		else if (blobType=='w') {
			blobs.add(new Wanderer(x,y));
		}
		else {
			System.err.println("Unknown blob type "+blobType);
		}
	}

	/**
	 * DrawingGUI method, here creating a new blob
	 */
	public void handleMousePress(int x, int y) {
		add(x,y);
		repaint();
	}

	/**
	 * DrawingGUI method
	 */
	public void handleKeyPress(char k) {
		if (k == 'f') { // faster
			if (delay>1) delay /= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 's') { // slower
			delay *= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 'r') { // add some new blobs at random positions
			for (int i=0; i<10; i++) {
				add((int)(width*Math.random()), (int)(height*Math.random()));
				repaint();
			}			
		}
		else if (k == 'c' || k == 'd') { // control how collisions are handled
			collisionHandler = k;
			System.out.println("collision:"+k);
		}
		else { // set the type for new blobs
			blobType = k;			
		}
	}

	/**
	 * DrawingGUI method, here drawing all the blobs and then re-drawing the colliders in red
	 */
	public void draw(Graphics g) {
		for(Blob eachBlob: blobs) {
			eachBlob.draw(g);
		}
		if (colliders != null) {
		for(Blob eachColliders: colliders) {
			g.setColor(Color.RED);
			eachColliders.draw(g);

		}}
		
		
		// TODO: YOUR CODE HERE
		// Ask all the blobs to draw themselves.
		// Ask the colliders to draw themselves in red.
	}

	/**
	 * Sets colliders to include all blobs in contact with another blob
	 */
	private void findColliders() {

		PointQuadtree<Blob> blobsTree = new PointQuadtree<Blob>(blobs.get(0), 0, 0, width, height);
		for (Blob eachBlob : blobs) {
			blobsTree.insert(eachBlob);
		}	
		for (Blob eachBlob : blobs) {
			List<Blob> tempCollidedList = blobsTree.findInCircle(eachBlob.getX(),eachBlob.getY(), 2*(eachBlob.r));
			//tempCollidedList.add(tempCollidedList.size(), eachBlob);
			if (tempCollidedList.size() != 0) {
			tempCollidedList.remove(0);
			}
			if (tempCollidedList.size() > 0) {
			
				colliders.add(eachBlob);
			
			}
			
			for (Blob collidedBlobs :tempCollidedList){
				colliders.add(collidedBlobs);
			}
			
		}
		// TODO: YOUR CODE HERE
		// Create the tree
		// For each blob, see if anybody else collided with it
	}

	/**
	 * DrawingGUI method, here moving all the blobs and checking for collisions
	 */
	public void handleTimer() {
		// Ask all the blobs to move themselves.
		for (Blob blob : blobs) {
			blob.step();
		}
		// Check for collisions
		if (blobs.size() > 0) {
			findColliders();
			if (collisionHandler=='d') {
				if(colliders != null)
					blobs.removeAll(colliders);
			}
		}
		// Now update the drawing
		repaint();
	}
	private void testColliders() {
		Blob blobTester1  = new Blob(40, 300, 10);
		blobTester1.setVelocity(10,0);
		Blob blobTester2  = new Blob(760, 300, 10);
		blobTester2.setVelocity(-10,0);
		blobs.add(blobTester1);
		blobs.add(blobTester2);
		
		Blob blobTester3  = new Blob(300, 40, 10);
		blobTester3.setVelocity(0,5);
		Blob blobTester4  = new Blob(300, 560, 10);
		blobTester4.setVelocity(0,-5);
		blobs.add(blobTester3);
		blobs.add(blobTester4);

	}
	
	
	
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CollisionGUI();
				new CollisionGUI().testColliders();
			}
		});
	}
}
