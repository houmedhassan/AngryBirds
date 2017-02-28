package AngryBirds;

import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image ;
import java.awt.Label;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;

public class Game extends Panel implements Runnable,  MouseListener, MouseMotionListener, ActionListener{
	/**
	 * les attributs
	 */
	private int score;
	private String [] niveaux={"Facile", "Moyen", "Difficile"};
	private int nbBird;
	
	private double gravity;
	private Pig pig;
	private ArrayList<Bird> bird;
	private BlackHole hole;
	
	private String message;
	private boolean gameOver;
	private boolean selecting;        // vrai lorsque le joueur sélectionne l'angle et la vitesse
	private boolean move;			  //suivi de la souris pour tirer l'oiseau
	private boolean first;
	private boolean absorption;
	
	private Image backgroundImage;    // image en fond
	Image buffer;                     // image pour le rendu hors écran
	
	private ArrayList<Integer> shadowX;
	private ArrayList<Integer> shadowY;
	
	/**
	 * Constructor of Class
	 * @throws IOException 
	 */
	public Game() throws IOException{
		backgroundImage = ImageIO.read(new File("angry-bird-background-icon.png"));	
		pig = new Pig("angry-bird-pig-icon.png");
    	hole = new BlackHole("angry-bird-blackhole-icon.png");
    	
    	bird = new ArrayList<Bird>();
	    bird.add(new Bird("angry-bird-yellow-icon.png"));
	    bird.add(new Bird("angry-bird-yellow-icon.png"));
	    bird.add(new Bird("angry-bird-yellow-icon.png"));
	    
		gravity = 0.1;
        score = 0;
        nbBird=0;
        pan.setPreferredSize(new Dimension(150, 30));
        this.add(pan);
        combox= new JComboBox();
		combox.addItem("Facile");
	    combox.addItem("Moyen");
	    combox.addItem("Difficile");
	    combox.setPreferredSize(new Dimension(150, 30));
	    pan.add(combox);
	    //combox.addItemListener(this);
        //combox.addMouseListener(this);
	    combox.addActionListener(this);
       	addMouseListener(this);
        addMouseMotionListener(this);
        init();
        new Thread(this).start();
	}
	
	  /**
	   *  calcule la distance entre deux points
	   * @param x1
	   * @param y1
	   * @param x2
	   * @param y2
	   * @return
	   */
    static double distance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }
	
	/**
	 * initialisation of the game 
	 * @throws IOException 
	 */
	void init() throws IOException {
		gameOver = false;
	    selecting = true;
	    absorption = false;
	    first=false;
	    double alea;
	  
	    
	    bird.get(nbBird).setImage("angry-bird-yellow-icon.png");

	    //position initiale de l'oiseau
	    for(int i=0; i<bird.size(); i++){
		    bird.get(i).setXs(50);
		    bird.get(i).setYs(50);
		    bird.get(i).setInitX(153);
		    bird.get(i).setInitY(431);
		    bird.get(i).setPositionX(bird.get(nbBird).getInitX());
		    bird.get(i).setPositionY(bird.get(nbBird).getInitY());
		    bird.get(i).setVelocityX(0);
		    bird.get(i).setVelocityY(0);
	    }
	    
	    alea =Math.random()*485+265;
	    pig.setXs(50);
	    pig.setYs(50);
	    pig.setPositionX(alea); // position aléatoire pour le cochon
	    pig.setPositionY(505);
	    
	    if(combox.getSelectedIndex()==2){
		    do{
		    	alea =Math.random()*485+265;
		    }while (alea==pig.getPositionX());
		    hole.setXs(200);
		    hole.setYs(100);
		    hole.setPositionX(alea); // position aléatoire pour le trous
		    hole.setPositionY(505);
	    }
	    
	    shadowX =new ArrayList<Integer>();
		shadowY =new ArrayList<Integer>();
	    
	    message = "Choisissez l'angle et la vitesse.";    
	}
	
	// fin de partie
    void stop() {
       bird.get(nbBird).setVelocityX(0);
       bird.get(nbBird).setVelocityY(0);
       gameOver = true;
       if(nbBird<(bird.size()-1)){
    	   nbBird++;
       } else nbBird=0;
    }
	
	// boucle qui calcule la position de l'oiseau en vol, effectue l'affichage et teste les conditions de victoire
    public void run() {
        while(true) {
            // un pas de simulation toutes les 10ms
            try { Thread.currentThread().sleep(10); } catch(InterruptedException e) { }

            if(!gameOver && !selecting) {

                // moteur physique
            	bird.get(nbBird).setPositionX((bird.get(nbBird).getPositionX()+bird.get(nbBird).getVelocityX()));
            	bird.get(nbBird).setPositionY((bird.get(nbBird).getPositionY()+bird.get(nbBird).getVelocityY()));
            	bird.get(nbBird).setVelocityY(bird.get(nbBird).getVelocityY()+gravity);
                
                System.out.println("bird="+nbBird+" velocityX="+bird.get(nbBird).getVelocityX()+" velocityY="+bird.get(nbBird).getVelocityY());
                
                // conditions d'absorption par le trous noir
                if(distance(bird.get(nbBird).getPositionX(), bird.get(nbBird).getPositionY(), hole.getPositionX(), hole.getPositionY())
                		< 65){
                	absorption = true;
                	stop();
	                if(nbBird == bird.size()){
	                	message =" Game Over vous avez fini la partie  avec un score de "+score;
	                	score=0;	
	                }
                }
               
                // conditions de victoire
	            if(distance(bird.get(nbBird).getPositionX(), bird.get(nbBird).getPositionY(), pig.getPositionX(), pig.getPositionY()) 
	            		< 45) {
	                stop();
	                message = "Gagn� : cliquez pour recommencer.";
	                score++;
	                //run();
	            } else if(bird.get(nbBird).getPositionX() < 20 || bird.get(nbBird).getPositionX() > 780 
	            				|| bird.get(nbBird).getPositionY() < 0 || bird.get(nbBird).getPositionY() > 480) {
	                stop();
	                if(nbBird == bird.size()){
	                	message =" Game Over vous avez fini la partie  avec un score de "+score;
	                	score=0;
	                	
	                }else {
	                	message = "Perdu : cliquez pour recommencer. il vous reste "+(bird.size()-nbBird) +"oiseau � jouer";
	                
	                }
	            }

                // redessine
                repaint();
            }
        }
    }
	
	
    private JComboBox combox;
    Panel pan =new Panel();
    
	/**
	 *  this method is used to add the images in the interface
	 * @param g
	 */
	public void paint(Graphics g2){
		
		if(buffer == null) buffer = createImage(800, 600);
        Graphics2D g = (Graphics2D) buffer.getGraphics();
		
		g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		
		// fond
		g.drawImage(backgroundImage, 0, 0, null);
		
		
	    
		
		//premiere corde 
		g.setColor(Color.black);
		if(bird.get(nbBird).getPositionX()<=bird.get(nbBird).getInitX()){
			g.drawLine(202, 416, (int)(bird.get(nbBird).getPositionX()-15),(int)(bird.get(nbBird).getPositionY()+5));
			//(int)(bird.get(nbBird).getInitX()+11),(int)(bird.get(nbBird).getInitY()+16)
		}
		// create a bird image		
		if(!absorption){
			if(bird.get(nbBird).getVelocityX()>6.5)
				try {
					bird.get(nbBird).setImage("angry-bird-red-icon.png");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else if(6.5>bird.get(nbBird).getVelocityX() && bird.get(nbBird).getVelocityX()>3.5){
				try {
					bird.get(nbBird).setImage("angry-bird-green-icon.png");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			g.drawImage(bird.get(nbBird).getImage(),(int)bird.get(nbBird).getPositionX()-20,(int)bird.get(nbBird).getPositionY()-20,
					(int)bird.get(nbBird).getXs(),(int)bird.get(nbBird).getYs(),this);
		}
		//System.out.println("nbBird="+nbBird);
		
		//nuage de point derri�re l'oiseau
		if(bird.get(nbBird).getPositionX()>bird.get(nbBird).getInitX()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			shadowX.add((int)bird.get(nbBird).getPositionX()-20);
			shadowY.add((int)bird.get(nbBird).getPositionY()-10);
			if(gameOver)
			{
				shadowX.clear();
				shadowY.clear();
			}
		}
		
		
		for(int i=0; i<shadowX.size(); i++){
			g.setColor(Color.yellow);
			g.fillOval(shadowX.get(i),shadowY.get(i), 10, 10);
		}
		//deuxieme corde
		g.setColor(Color.black);
		if(bird.get(nbBird).getPositionX()<=bird.get(nbBird).getInitX()){
			g.drawLine(180, 414, (int)(bird.get(nbBird).getPositionX()-15),(int)(bird.get(nbBird).getPositionY()+5));
			//(int)(bird.get(nbBird).getInitX()-12),(int)(bird.get(nbBird).getInitY()-10)
		}
		
		// creation a Pig image		
		g.drawImage(pig.getImage(),(int)pig.getPositionX()-20,(int)pig.getPositionY()-28,
					(int)pig.getXs(),(int)pig.getYs(),this);
		
		// creation a BlackHole image 
		if(combox.getSelectedIndex()==2)
		g.drawImage(hole.getImage(),(int)hole.getPositionX()-20,(int)hole.getPositionY()-100,
				(int)hole.getXs(),(int)hole.getYs(),this);
		
		// messages and score
        g.setColor(Color.BLACK);
        g.drawString(message, 300, 100);
        g.drawString("score: " + score, 20, 20);
        
        // affichage à l'écran sans scintillement
        g2.drawImage(buffer, 0, 0, null);
	}
	
	/**
	 * 
	 */
	public void update(Graphics g) {
		
        paint(g);
    }
	
	 /**
	  * size of interface
	  */
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }
    /**
     *  principal method
     * @param args
     * @throws IOException 
     */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		  Frame frame = new Frame("Oiseau pas content");
		  Game game = new Game();
		  frame.add(game);
	      frame.pack();
	      
	      frame.setLocationRelativeTo(null);
	      frame.setVisible(true);
	      frame.setResizable(false);
	      frame.addWindowListener(new WindowAdapter() {
	            public void windowClosing(WindowEvent event) {
	                System.exit(0);
	            }
	        });
	}
	
	
	  // gestion des événements souris
    public void mouseClicked(MouseEvent e) { 
    	
   
    }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) {
    }
    public void mousePressed(MouseEvent e) {
    	if(((bird.get(nbBird).getPositionX()>=e.getX()-20 && bird.get(nbBird).getPositionX()<=e.getX())||
    	   (bird.get(nbBird).getPositionX()>=e.getX() && bird.get(nbBird).getPositionX()<=e.getX()+20)) &&
    			((bird.get(nbBird).getPositionY()>=e.getY()-20 && bird.get(nbBird).getPositionY()<=e.getY())||
    			 (bird.get(nbBird).getPositionY()>=e.getY() && bird.get(nbBird).getPositionY()<=e.getY()+20))){
    		move=true;
    	}
    	setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }
    public void mouseReleased(MouseEvent e) {
	    	if(gameOver) {
	            try {
					init();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        } else if(selecting && move && 
	        		bird.get(nbBird).getInitX()!=bird.get(nbBird).getPositionX()) {
	            bird.get(nbBird).setVelocityX((bird.get(nbBird).getInitX()-bird.get(nbBird).getPositionX()) / 10.0);
	            bird.get(nbBird).setVelocityY((bird.get(nbBird).getInitY()-bird.get(nbBird).getPositionY()) / 7.0);
	            System.out.println("VelocityX="+bird.get(nbBird).getVelocityX()+" VelocityY="+bird.get(nbBird).getVelocityY());
	            message = "L'oiseau prend sont envol";
	            selecting = false;
	            move=false;
	        }
	    //	combox.getSelectedIndex();
	    	System.out.println(combox.getSelectedItem());
    	setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        repaint();
    }
    public void mouseDragged(MouseEvent e) {
    	if(move){
    		bird.get(nbBird).setPositionX(e.getX());
        	bird.get(nbBird).setPositionY(e.getY());
        	repaint();
    	}
    }
    public void mouseMoved(MouseEvent e) { 
		//System.out.println("X="+e.getX()+" Y="+e.getY() + " BirdX="+bird.get(nbBird).getPositionX()+" BirdY="+bird.get(nbBird).getPositionY());
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==combox){
		combox.getSelectedIndex();
		try {
			init();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	System.out.println(combox.getSelectedIndex());
		}
		
	}


}
