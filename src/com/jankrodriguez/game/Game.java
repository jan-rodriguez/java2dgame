package com.jankrodriguez.game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 160;
	public static final int HEIGHT = WIDTH/12 * 9;
	public static final int SCALE = 3;
	public static final int NUMBUFFERS = 3;
	public static final String NAME = "My Game";
	
	private boolean running = false;
	
	public int tickCount = 0;
	
	private JFrame frame;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
	
	/**
	 * Constructor for the game object
	 */
	public Game(){
		setMinimumSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		setMaximumSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		
		frame = new JFrame();
		
		//Setting up frame properties
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		frame.add(this, BorderLayout.CENTER);
		frame.pack();
		
		frame.setVisible(true);
	}

	private synchronized void start() {
		running = true;
		//Starting the new game thread
		new Thread(this).start();
	}
	
	private synchronized void stop(){
		running = false;
	}
	
	/**
	 * Run the game
	 */
	@Override
	public void run() {
		//Getting last time since update
		long lastUpdate = System.nanoTime();
		//Setting our tick to run 60 times per second
		double nsPerTick = 1000000000D / 60D;
		
		int frames = 0;
		int ticks = 0;
		
		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		
		boolean shouldRender = true;
		
		while(running){
			long now = System.nanoTime();
			//Adding the last time since update
			delta += (now - lastUpdate) / nsPerTick;
			lastUpdate = now;
			
			//If we the delta is greater than our frames per second
			if (delta >= 1){
				//Updating the variables
				ticks++;
				tick();
				delta--;
				shouldRender = true;
			}
			
			//Sleep to throttle the updates
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(shouldRender){
				frames++;
				render();
			}
			
			
			if(System.currentTimeMillis() - lastTimer > 1000){
				lastTimer += 1000;
				System.out.println(frames+","+ticks);
				frames = 0;
				ticks = 0;
			}
		}
	}
	
	private void render() {
		BufferStrategy bs = getBufferStrategy();
		
		if(bs == null){
			createBufferStrategy(NUMBUFFERS);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		//Drawing the image on the screen
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
//		g.setColor(Color.BLACK);
//		g.fillRect(0, 0, getWidth(), getHeight());
	
		//Dispose graphics to free up the memory used by graphics object
		g.dispose();
		bs.show();
		
	}

	private void tick() {
		tickCount++;
		
		for(int i = 0; i < pixels.length; i++){
			pixels[i] = i + tickCount;
		}
		
	}

	public static void main(String... args){
		new Game().start();
	}
}
